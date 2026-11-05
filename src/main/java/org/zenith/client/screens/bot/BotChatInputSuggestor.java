package org.zenith.client.screens.bot;

import com.google.common.base.Strings;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.context.SuggestionContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.function.IntSupplier;
import java.util.function.IntUnaryOperator;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import org.zenith.MinecraftAccess;
import org.zenith.base.bot.net.BotCommandSource;
import org.zenith.base.bot.net.BotPlayHandler;
import org.zenith.utility.misc.SearchBox;
import org.zenith.utility.misc.WindowUtils;

public final class BotChatInputSuggestor implements MinecraftAccess {
   private static final Pattern WHITESPACE_PATTERN = Pattern.compile("(\\s+)");
   private static final Style ERROR_STYLE = Style.EMPTY.withColor(ChatFormatting.RED);
   private static final Style INFO_STYLE = Style.EMPTY.withColor(ChatFormatting.GRAY);
   private static final List<Style> HIGHLIGHT_STYLES = Stream.of(ChatFormatting.AQUA, ChatFormatting.YELLOW, ChatFormatting.GREEN, ChatFormatting.LIGHT_PURPLE, ChatFormatting.GOLD)
      .map(Style.EMPTY::withColor)
      .toList();
   private static final int IN_WINDOW_INDEX_OFFSET = 1;
   private static final int MAX_SUGGESTION_SIZE = 10;
   private static final int COLOR = -805306368;
   private final Screen owner;
   private final SearchBox textField;
   private final Font textRenderer;
   private final Supplier<BotPlayHandler> handlerSupplier;
   private final IntUnaryOperator characterX;
   private final IntSupplier fieldTop;
   private final List<FormattedCharSequence> messages = new ArrayList<>();
   private int x;
   private int width;
   @Nullable
   private ParseResults<SharedSuggestionProvider> parse;
   @Nullable
   private CompletableFuture<Suggestions> pendingSuggestions;
   @Nullable
   private BotChatInputSuggestor.SuggestionWindow window;
   private boolean windowActive;
   private boolean completingSuggestions;
   @Nullable
   private String suggestion;
   private String lastText = "";

   public BotChatInputSuggestor(Screen owner, SearchBox textField, Font textRenderer, Supplier<BotPlayHandler> handlerSupplier, IntUnaryOperator characterX, IntSupplier fieldTop) {
      this.owner = owner;
      this.textField = textField;
      this.textRenderer = textRenderer;
      this.handlerSupplier = handlerSupplier;
      this.characterX = characterX;
      this.fieldTop = fieldTop;
   }

   public void setWindowActive(boolean windowActive) {
      this.windowActive = windowActive;
      if (!windowActive) {
         this.window = null;
      }
   }

   public void update(String originalChatText) {
      String text = this.textField.getText();
      if (!text.equals(this.lastText)) {
         this.lastText = text;
         this.setWindowActive(!text.equals(originalChatText));
         this.refresh();
      }
   }

   public void reset(String lastText) {
      this.lastText = lastText;
      this.parse = null;
      this.pendingSuggestions = null;
      this.suggestion = null;
      this.messages.clear();
      this.setWindowActive(false);
      this.refresh();
   }

   @Nullable
   public String getSuggestion() {
      return this.suggestion;
   }

   public boolean isOpen() {
      return this.window != null;
   }

   public boolean keyPressed(KeyEvent event) {
      return this.keyPressed(org.zenith.utility.misc.KeyCompat.toGlfw(event.key()), event.keycode(), org.zenith.utility.misc.KeyCompat.modsToGlfw(event.modifiers()));
   }

   public boolean keyPressed(int keyCode, int keycode, int modifiers) {
      boolean flag = this.window != null;
      if (flag && this.window.keyPressed(keyCode)) {
         return true;
      } else if (keyCode == 258) {
         this.show();
         return this.window != null;
      } else {
         return false;
      }
   }

   public boolean mouseScrolled(double verticalAmount, double mouseX, double mouseY) {
      return this.window != null && this.window.mouseScrolled(Mth.clamp(verticalAmount, -1.0, 1.0), mouseX, mouseY);
   }

   public boolean mouseClicked(double xo, double yo) {
      return this.window != null && this.window.mouseClicked((int)xo, (int)yo);
   }

   private void show() {
      if (this.pendingSuggestions != null && this.pendingSuggestions.isDone()) {
         Suggestions suggestions = this.pendingSuggestions.join();
         if (!suggestions.isEmpty()) {
            int n = 0;

            for (Suggestion suggestion : suggestions.getList()) {
               n = Math.max(n, this.textRenderer.width(suggestion.getText()));
            }

            int n2 = Mth.clamp(this.characterX.applyAsInt(suggestions.getRange().getStart()), 0, Math.max(0, this.owner.width - n));
            this.window = new BotChatInputSuggestor.SuggestionWindow(n2, n, this.sortSuggestions(suggestions));
         }
      }
   }

   public void clearWindow() {
      this.window = null;
   }

   private List<Suggestion> sortSuggestions(Suggestions suggestions) {
      String input = this.textField.getText().substring(0, this.textField.getCursor());
      int startOfCurrentWord = getStartOfCurrentWord(input);
      String lowerCase = input.substring(startOfCurrentWord).toLowerCase(Locale.ROOT);
      ArrayList<Suggestion> suggestions2 = new ArrayList<>();
      ArrayList arrayList = new ArrayList();

      for (Suggestion suggestion : suggestions.getList()) {
         if (!suggestion.getText().startsWith(lowerCase) && !suggestion.getText().startsWith("minecraft:" + lowerCase)) {
            arrayList.add(suggestion);
         } else {
            suggestions2.add(suggestion);
         }
      }

      suggestions2.addAll(arrayList);
      return suggestions2;
   }

   public void refresh() {
      BotPlayHandler botPlayHandler = this.handlerSupplier.get();
      String text = this.textField.getText();
      if (this.parse != null && !this.parse.getReader().getString().equals(text)) {
         this.parse = null;
      }

      if (!this.completingSuggestions) {
         this.suggestion = null;
         this.window = null;
      }

      this.messages.clear();
      StringReader stringReader = new StringReader(text);
      boolean flag = stringReader.canRead() && stringReader.peek() == '/';
      if (flag) {
         stringReader.skip();
      }

      int cursor = this.textField.getCursor();
      if (flag && botPlayHandler != null) {
         CommandDispatcher commandDispatcher = botPlayHandler.getCommandDispatcher();
         BotCommandSource commandSource = botPlayHandler.getCommandSource();
         if (this.parse == null) {
            this.parse = commandDispatcher.parse(stringReader, commandSource);
         }

         if (cursor >= 1 && (this.window == null || !this.completingSuggestions)) {
            this.pendingSuggestions = commandDispatcher.getCompletionSuggestions(this.parse, cursor);
            this.pendingSuggestions.thenRun(() -> {
               var var1x = this.pendingSuggestions;
               if (var1x != null && var1x.isDone()) {
                  mc.execute(() -> {
                     if (this.pendingSuggestions == var1x) {
                        this.showCommandSuggestions();
                     }
                  });
               }
            });
         }
      } else {
         String input = text.substring(0, cursor);
         int startOfCurrentWord = getStartOfCurrentWord(input);
         Object customTabSuggestions = botPlayHandler != null ? botPlayHandler.getCommandSource().getCustomTabSuggestions() : List.of();
         this.pendingSuggestions = SharedSuggestionProvider.suggest((Iterable<String>)customTabSuggestions, new SuggestionsBuilder(input, startOfCurrentWord));
      }
   }

   private static int getStartOfCurrentWord(String input) {
      if (Strings.isNullOrEmpty(input)) {
         return 0;
      } else {
         int end = 0;
         Matcher matcher = WHITESPACE_PATTERN.matcher(input);

         while (matcher.find()) {
            end = matcher.end();
         }

         return end;
      }
   }

   private static FormattedCharSequence formatException(CommandSyntaxException parseException) {
      Component component = ComponentUtils.fromMessage(parseException.getRawMessage());
      String context = parseException.getContext();
      return context == null ? component.getVisualOrderText() : Component.translatable("command.context.parse_error", component, parseException.getCursor(), context).getVisualOrderText();
   }

   private void showCommandSuggestions() {
      if (this.pendingSuggestions != null && this.parse != null) {
         boolean flag = false;
         if (this.textField.getCursor() == this.textField.getText().length()) {
            if (this.pendingSuggestions.join().isEmpty() && !this.parse.getExceptions().isEmpty()) {
               int n = 0;

               for (Entry entry : this.parse.getExceptions().entrySet()) {
                  CommandSyntaxException parseException = (CommandSyntaxException)entry.getValue();
                  if (parseException.getType() == CommandSyntaxException.BUILT_IN_EXCEPTIONS.literalIncorrect()) {
                     n++;
                  } else {
                     this.messages.add(formatException(parseException));
                  }
               }

               if (n > 0) {
                  this.messages.add(formatException(CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().create()));
               }
            } else if (this.parse.getReader().canRead()) {
               flag = true;
            }
         }

         this.x = 0;
         this.width = this.owner.width;
         if (this.messages.isEmpty() && !this.showUsages() && flag) {
            this.messages.add(formatException(Commands.getParseException(this.parse)));
         }

         this.window = null;
         if (this.windowActive) {
            this.show();
         }
      }
   }

   private boolean showUsages() {
      BotPlayHandler botPlayHandler = this.handlerSupplier.get();
      if (botPlayHandler != null && this.parse != null) {
         CommandContextBuilder context = this.parse.getContext();
         SuggestionContext suggestionContext = context.findSuggestionContext(this.textField.getCursor());
         var smartUsage = botPlayHandler.getCommandDispatcher().getSmartUsage(suggestionContext.parent, botPlayHandler.getCommandSource());
         ArrayList arrayList = new ArrayList();
         int width = 0;

         for (Object obj : (Iterable<?>)(Object)(smartUsage.entrySet())) {
            Entry entry = (Entry)obj;
            if (!(entry.getKey() instanceof LiteralCommandNode)) {
               arrayList.add(FormattedCharSequence.forward((String)entry.getValue(), INFO_STYLE));
               width = Math.max(width, this.textRenderer.width((String)entry.getValue()));
            }
         }

         if (!arrayList.isEmpty()) {
            this.messages.addAll(arrayList);
            this.x = Mth.clamp(this.characterX.applyAsInt(suggestionContext.startPos), 0, Math.max(0, this.owner.width - width));
            this.width = width;
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public FormattedCharSequence provideRenderText(String plainSubstrByWidth, int chatFirstChar) {
      return this.parse != null ? highlight(this.parse, plainSubstrByWidth, chatFirstChar) : FormattedCharSequence.forward(plainSubstrByWidth, Style.EMPTY);
   }

   @Nullable
   private static String getSuggestionSuffix(String text, String str) {
      return str.startsWith(text) ? str.substring(text.length()) : null;
   }

   private static FormattedCharSequence highlight(ParseResults<SharedSuggestionProvider> parse, String plainSubstrByWidth, int chatFirstChar) {
      ArrayList parts = new ArrayList();
      int beginIndex = 0;
      int n = -1;
      CommandContextBuilder lastChild = parse.getContext().getLastChild();

      for (Object obj : (Iterable<?>)(Object)(lastChild.getArguments().values())) {
         ParsedArgument parsedArgument = (ParsedArgument)obj;
         if (++n >= HIGHLIGHT_STYLES.size()) {
            n = 0;
         }

         int endIndex = Math.max(parsedArgument.getRange().getStart() - chatFirstChar, 0);
         if (endIndex >= plainSubstrByWidth.length()) {
            break;
         }

         int endIndex2 = Math.min(parsedArgument.getRange().getEnd() - chatFirstChar, plainSubstrByWidth.length());
         if (endIndex2 > 0) {
            parts.add(FormattedCharSequence.forward(plainSubstrByWidth.substring(beginIndex, endIndex), INFO_STYLE));
            parts.add(FormattedCharSequence.forward(plainSubstrByWidth.substring(endIndex, endIndex2), HIGHLIGHT_STYLES.get(n)));
            beginIndex = endIndex2;
         }
      }

      if (parse.getReader().canRead()) {
         int endIndex = Math.max(parse.getReader().getCursor() - chatFirstChar, 0);
         if (endIndex < plainSubstrByWidth.length()) {
            int endIndex2 = Math.min(endIndex + parse.getReader().getRemainingLength(), plainSubstrByWidth.length());
            parts.add(FormattedCharSequence.forward(plainSubstrByWidth.substring(beginIndex, endIndex), INFO_STYLE));
            parts.add(FormattedCharSequence.forward(plainSubstrByWidth.substring(endIndex, endIndex2), ERROR_STYLE));
            beginIndex = endIndex2;
         }
      }

      parts.add(FormattedCharSequence.forward(plainSubstrByWidth.substring(beginIndex), INFO_STYLE));
      return FormattedCharSequence.composite(parts);
   }

   public void render(GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY) {
      if (this.window != null) {
         this.window.render(guiGraphicsExtractor, mouseX, mouseY);
      } else {
         this.renderMessages(guiGraphicsExtractor);
      }
   }

   private void renderMessages(GuiGraphicsExtractor guiGraphicsExtractor) {
      int n = 0;

      for (FormattedCharSequence message : this.messages) {
         int y0 = this.fieldTop.getAsInt() - 13 - 12 * n;
         guiGraphicsExtractor.fill(this.x - 1, y0, this.x + this.width + 1, y0 + 12, -805306368);
         guiGraphicsExtractor.text(this.textRenderer, message, this.x, y0 + 2, -1);
         n++;
      }
   }

   private final class SuggestionWindow {
      private final Rect2i area;
      private final String typedText;
      private final List<Suggestion> suggestions;
      private int inWindowIndex;
      private int selection;
      private double lastMouseX;
      private double lastMouseY;
      private boolean completed;

      public SuggestionWindow(int n, int n2, List<Suggestion> suggestions) {
         int y = BotChatInputSuggestor.this.fieldTop.getAsInt() - 3 - Math.min(suggestions.size(), 10) * 12;
         this.area = new Rect2i(n - 1, y, n2 + 1, Math.min(suggestions.size(), 10) * 12);
         this.typedText = BotChatInputSuggestor.this.textField.getText();
         this.suggestions = suggestions;
         this.select(0);
      }

      public void render(GuiGraphicsExtractor guiGraphicsExtractor, int lastMouseX, int lastMouseY) {
         int n = Math.min(this.suggestions.size(), 10);
         boolean flag = this.inWindowIndex > 0;
         boolean flag2 = this.suggestions.size() > this.inWindowIndex + n;
         boolean flag3 = flag || flag2;
         boolean flag4 = this.lastMouseX != lastMouseX || this.lastMouseY != lastMouseY;
         if (flag4) {
            this.lastMouseX = lastMouseX;
            this.lastMouseY = lastMouseY;
         }

         if (flag3) {
            guiGraphicsExtractor.fill(this.area.getX(), this.area.getY() - 1, this.area.getX() + this.area.getWidth(), this.area.getY(), -805306368);
            guiGraphicsExtractor.fill(
               this.area.getX(),
               this.area.getY() + this.area.getHeight(),
               this.area.getX() + this.area.getWidth(),
               this.area.getY() + this.area.getHeight() + 1,
               -805306368
            );
            if (flag) {
               for (int i = 0; i < this.area.getWidth(); i++) {
                  if (i % 2 == 0) {
                     guiGraphicsExtractor.fill(this.area.getX() + i, this.area.getY() - 1, this.area.getX() + i + 1, this.area.getY(), -1);
                  }
               }
            }

            if (flag2) {
               for (int i = 0; i < this.area.getWidth(); i++) {
                  if (i % 2 == 0) {
                     guiGraphicsExtractor.fill(
                        this.area.getX() + i,
                        this.area.getY() + this.area.getHeight(),
                        this.area.getX() + i + 1,
                        this.area.getY() + this.area.getHeight() + 1,
                        -1
                     );
                  }
               }
            }
         }

         boolean flag5 = false;

         for (int i = 0; i < n; i++) {
            Suggestion suggestion = this.suggestions.get(i + this.inWindowIndex);
            guiGraphicsExtractor.fill(this.area.getX(), this.area.getY() + 12 * i, this.area.getX() + this.area.getWidth(), this.area.getY() + 12 * i + 12, -805306368);
            if (lastMouseX > this.area.getX()
               && lastMouseX < this.area.getX() + this.area.getWidth()
               && lastMouseY > this.area.getY() + 12 * i
               && lastMouseY < this.area.getY() + 12 * i + 12) {
               if (flag4) {
                  this.select(i + this.inWindowIndex);
               }

               flag5 = true;
            }

            guiGraphicsExtractor.text(
               BotChatInputSuggestor.this.textRenderer,
               suggestion.getText(),
               this.area.getX() + 1,
               this.area.getY() + 2 + 12 * i,
               i + this.inWindowIndex == this.selection ? '\uff00' : -5592406
            );
         }

         if (flag5) {
            Message tooltip = this.suggestions.get(this.selection).getTooltip();
            if (tooltip != null) {
               guiGraphicsExtractor.setTooltipForNextFrame(BotChatInputSuggestor.this.textRenderer, ComponentUtils.fromMessage(tooltip), lastMouseX, lastMouseY);
            }
         }
      }

      public boolean mouseClicked(int xo, int yo) {
         if (!this.area.contains(xo, yo)) {
            return false;
         } else {
            int selection = (yo - this.area.getY()) / 12 + this.inWindowIndex;
            if (selection >= 0 && selection < this.suggestions.size()) {
               this.select(selection);
               this.complete();
            }

            return true;
         }
      }

      public boolean mouseScrolled(double d, double mouseX, double mouseY) {
         if (this.area.contains((int)mouseX, (int)mouseY)) {
            this.inWindowIndex = Mth.clamp((int)(this.inWindowIndex - d), 0, Math.max(this.suggestions.size() - 10, 0));
            return true;
         } else {
            return false;
         }
      }

      public boolean keyPressed(int keyCode) {
         if (keyCode == 265) {
            this.scroll(-1);
            this.completed = false;
            return true;
         } else if (keyCode == 264) {
            this.scroll(1);
            this.completed = false;
            return true;
         } else if (keyCode == 258) {
            if (this.completed) {
               this.scroll(WindowUtils.method5476() ? -1 : 1);
            }

            this.complete();
            return true;
         } else if (keyCode == 256) {
            BotChatInputSuggestor.this.clearWindow();
            BotChatInputSuggestor.this.suggestion = null;
            return true;
         } else {
            return false;
         }
      }

      public void scroll(int n) {
         this.select(this.selection + n);
         int inWindowIndex = this.inWindowIndex;
         int n2 = this.inWindowIndex + 10 - 1;
         if (this.selection < inWindowIndex) {
            this.inWindowIndex = Mth.clamp(this.selection, 0, Math.max(this.suggestions.size() - 10, 0));
         } else if (this.selection > n2) {
            this.inWindowIndex = Mth.clamp(this.selection + 1 - 10, 0, Math.max(this.suggestions.size() - 10, 0));
         }
      }

      public void select(int selection) {
         this.selection = selection;
         if (this.selection < 0) {
            this.selection = this.selection + this.suggestions.size();
         }

         if (this.selection >= this.suggestions.size()) {
            this.selection = this.selection - this.suggestions.size();
         }

         Suggestion suggestion = this.suggestions.get(this.selection);
         BotChatInputSuggestor.this.suggestion = BotChatInputSuggestor.getSuggestionSuffix(
            BotChatInputSuggestor.this.textField.getText(), suggestion.apply(this.typedText)
         );
      }

      public void complete() {
         Suggestion suggestion = this.suggestions.get(this.selection);
         BotChatInputSuggestor.this.completingSuggestions = true;
         String lastText = suggestion.apply(this.typedText);
         BotChatInputSuggestor.this.textField.setText(lastText);
         BotChatInputSuggestor.this.lastText = lastText;
         int cursor = suggestion.getRange().getStart() + suggestion.getText().length();
         BotChatInputSuggestor.this.textField.setCursor(cursor);
         BotChatInputSuggestor.this.windowActive = true;
         BotChatInputSuggestor.this.refresh();
         this.select(this.selection);
         BotChatInputSuggestor.this.completingSuggestions = false;
         this.completed = true;
      }
   }
}
