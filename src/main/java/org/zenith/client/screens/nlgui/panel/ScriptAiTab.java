package org.zenith.client.screens.nlgui.panel;

import com.darkmagician6.eventapi.EventManager;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.zenith.utility.misc.FileDialogs;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.events.TickEvent;
import org.zenith.client.screens.media.MediaGlyphs;
import org.zenith.client.screens.nlgui.HoverInfo;
import org.zenith.client.screens.nlgui.MenuGlyph;
import org.zenith.client.screens.nlgui.elements.ChipStrip;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.screens.scripts.FormField;
import org.zenith.client.screens.scripts.ScriptInfoPage;
import org.zenith.client.screens.scripts.Smooth;
import org.zenith.client.scripting.Script;
import org.zenith.client.scripting.Storage;
import org.zenith.utility.api.AiChatStorage;
import org.zenith.utility.api.AiSettingsStore;
import org.zenith.utility.config.AiImageStore;
import org.zenith.utility.misc.HudHolder;
import org.zenith.utility.misc.ShownProvider;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public final class ScriptAiTab {
   private static final float INSET = 8.0F;
   private static final ZenithColor OK = new ZenithColor(96, 214, 128);
   private static final ZenithColor ERROR = new ZenithColor(242, 79, 68);
   private static final ZenithColor WARN = new ZenithColor(247, 206, 59);
   private static final Pattern BLOCK = Pattern.compile("```([^\\n`]*)\\n(.*?)(```|$)", 32);
   private static final Pattern FILE = Pattern.compile("(?:file|path|title)\\s*[:=]\\s*\"?([^\"]+)\"?");
   private static final String[] IDEAS = new String[]{"gui.scripts.ai.idea.1", "gui.scripts.ai.idea.2", "gui.scripts.ai.idea.3", "gui.scripts.ai.idea.4"};
   private final ScriptsSectionPanel panel;
   public final FormField input = new FormField(6000, true).lines(2, 5);
   public final FormField keyField = new FormField(300, false).masked(true);
   public final FormField modelField = new FormField(120, false);
   public final FormField endpointField = new FormField(300, false).masked(true);
   public final FormField addName = new FormField(24, false);
   public final FormField addUrl = new FormField(300, false).masked(true);
   public final FormField addKey = new FormField(300, false).masked(true);
   public final FormField addModel = new FormField(120, false);
   private boolean adding;
   private String addError = "";
   private String armedDelete = "";
   private long armedDeleteAt;
   private final Map<String, BoundsData> hits = new HashMap<>();
   private final Map<String, Long> pressedAt = new HashMap<>();
   private BoundsData sendRect;
   public static final int VIEW_CHATS = 0;
   public static final int VIEW_SETTINGS = 1;
   private int asideView = -1;
   private final ChipStrip asideTabs = new ChipStrip("gui.scripts.ai.view.chats", "gui.scripts.ai.view.settings");
   private String armedChat = "";
   private long armedChatAt;
   private static final SimpleDateFormat CLOCK = new SimpleDateFormat("HH:mm");
   private ShownProvider shownProvider;
   private int seenRevision = -1;
   private int seenMessages;
   private static final float LINE = 7.4F;
   private static final float THUMB = 44.0F;
   private static final float CHIP = 13.0F;
   private static final float CODE_LINE = 6.6F;
   private static final int CODE_PREVIEW = 6;
   private static final float CARD_PAD = 7.0F;
   private static final float BUBBLE_PAD = 5.0F;
   private static final float HEAD_GAP = 4.0F;
   private static final float BUTTON = 12.0F;
   private static final float BUTTON_GAP = 3.0F;
   private final Map<AiChatStorage.Entry2, ScriptAiTab.CachedLayout> layouts = new WeakHashMap<>();
   private static final Pattern HEADING = Pattern.compile("(?m)^#{1,6}\\s*");
   private double lastMouseX;
   private double lastMouseY;
   private static final float CARD = 22.0F;
   private static final float CHAT_CARD = 28.0F;
   private static final float AFTER_TABS = 2.0F;
   private static final ZenithColor LOCAL = new ZenithColor(110, 168, 255);
   private static final float STATUS_DOT = 4.0F;
   private final List<String> pending = new CopyOnWriteArrayList<>();
   private volatile int importing;
   private static final int MAX_ATTACH = 6;
   private static final float STRIP = 30.0F;
   private static final float ATTACH_RADIUS = 4.0F;
   private static final float REMOVE = 8.0F;
   private static final String[][] COMMANDS = new String[][]{
      {"/new", "gui.scripts.ai.cmd.new"},
      {"/fix", "gui.scripts.ai.cmd.fix"},
      {"/explain", "gui.scripts.ai.cmd.explain"},
      {"/shot", "gui.scripts.ai.cmd.shot"},
      {"/model", "gui.scripts.ai.cmd.model"},
      {"/export", "gui.scripts.ai.cmd.export"},
      {"/clear", "gui.scripts.ai.cmd.clear"},
      {"/help", "gui.scripts.ai.cmd.help"}
   };
   private static int shotCountdown = -1;
   private static Screen shotReturn;
   private static Consumer<String> shotDone;
   private static boolean shotHooked;

   private List<FormField> fields() {
      return List.of(this.input, this.keyField, this.modelField, this.endpointField, this.addName, this.addUrl, this.addKey, this.addModel);
   }

   public ScriptAiTab(ScriptsSectionPanel panel) {
      this.panel = panel;
   }

   private static String t(String description) {
      return SectionPanel.t(description);
   }

   public int revision() {
      return AiChatStorage.method2741().revision();
   }

   public boolean pollChanged() {
      int revision = AiChatStorage.method2741().revision();
      if (revision == this.seenRevision) {
         return false;
      } else {
         this.seenRevision = revision;
         return true;
      }
   }

   private void syncFields() {
      AiSettingsStore aiSettingsStore = AiSettingsStore.method4467();
      ShownProvider shownProvider = aiSettingsStore.getShownProvider();
      if (!shownProvider.equals(this.shownProvider)) {
         this.commitFields();
         this.shownProvider = shownProvider;
         this.keyField.set(aiSettingsStore.method4469(shownProvider));
         this.modelField.set(aiSettingsStore.method4472(shownProvider));
         this.endpointField.set(aiSettingsStore.method4474(shownProvider));
      }
   }

   public void commitFields() {
      if (this.shownProvider != null) {
         AiSettingsStore aiSettingsStore = AiSettingsStore.method4467();
         if (this.keyField.changed()) {
            aiSettingsStore.method4470(this.shownProvider, this.keyField.get());
            this.keyField.commit();
         }

         if (this.modelField.changed()) {
            aiSettingsStore.method4473(this.shownProvider, this.modelField.get());
            this.modelField.commit();
         }

         if (this.endpointField.changed()) {
            aiSettingsStore.method4475(this.shownProvider, this.endpointField.get());
            this.endpointField.commit();
         }
      }
   }

   public void build(Storage hostOrNull) {
      this.syncFields();
      AiChatStorage aiChatStorage = AiChatStorage.method2741();
      var entry2s = new ArrayList<>(aiChatStorage.method2745());
      if (entry2s.isEmpty()) {
         this.welcome(hostOrNull);
      } else {
         float width = 236.0F;

         for (int i = 0; i < entry2s.size(); i++) {
            AiChatStorage.Entry2 entry2 = (AiChatStorage.Entry2)entry2s.get(i);
            boolean lastOne = i == entry2s.size() - 1;
            ScriptAiTab.Layout layout = this.layout(entry2, width, lastOne);
            int n = i;
            this.panel
               .custom(
                  layout.height + 8.0F,
                  (var6x, var7x, var8x, var9x, f, f2, previewAlpha, currentStyle) -> this.renderMessage(
                     var6x, var7x, var8x, var9x, f, f2, previewAlpha, currentStyle, hostOrNull, entry2, layout, n, lastOne
                  ),
                  () -> this.clickMessage(hostOrNull, entry2, n),
                  null
               );
         }

         this.quickReplies(hostOrNull, (AiChatStorage.Entry2)entry2s.get(entry2s.size() - 1));
         this.panel.gap(4.0F);
      }

      this.aside(hostOrNull);
      if (entry2s.size() != this.seenMessages || aiChatStorage.isBusy()) {
         this.seenMessages = entry2s.size();
         this.panel.scrollToEnd();
      }
   }

   private List<String> suggestions(AiChatStorage.Entry2 entry2) {
      if (entry2.isFlag() || entry2.streaming() || AiChatStorage.method2741().isBusy()) {
         return List.of();
      } else if (!entry2.error().isEmpty()) {
         return List.of();
      } else if (entry2.isFlag3()) {
         return List.of("gui.scripts.ai.quick.fix", "gui.scripts.ai.quick.simpler");
      } else {
         return !entry2.method2775().isEmpty()
            ? List.of("gui.scripts.ai.quick.settings", "gui.scripts.ai.quick.prettier", "gui.scripts.ai.quick.explain", "gui.scripts.ai.quick.publish")
            : List.of("gui.scripts.ai.quick.doit", "gui.scripts.ai.quick.example");
      }
   }

   private void quickReplies(Storage hostOrNull, AiChatStorage.Entry2 entry2) {
      List<String> suggestions = this.suggestions(entry2);
      if (!suggestions.isEmpty()) {
         Font font = Fonts.NEW_MEDIUM.getFont(4.5F);
         float f = 220.0F;
         ArrayList arrayList = new ArrayList();
         float f2 = 0.0F;
         float f3 = 0.0F;

         for (String suggestion : suggestions) {
            float f4 = font.width(t(suggestion)) + 14.0F;
            if (f2 > 0.0F && f2 + f4 > f) {
               f2 = 0.0F;
               f3 += 15.0F;
            }

            arrayList.add(new float[]{f2, f3, f4});
            f2 += f4 + 4.0F;
         }

         long now = System.currentTimeMillis();
         this.panel
            .custom(
               f3 + 16.0F,
               (var6x, var7x, var8x, f4, var10x, var11x, var12x, zenithStyle) -> {
                  ZenithColor zenithColor = zenithStyle.getPrimaryColor().getZenithColor();

                  for (int i = 0; i < suggestions.size(); i++) {
                     float[] floats = (float[])arrayList.get(i);
                     float f5 = Math.min(1.0F, Math.max(0.0F, (float)(System.currentTimeMillis() - now - i * 60L) / 260.0F));
                     float f6 = 1.0F - (1.0F - f5) * (1.0F - f5) * (1.0F - f5);
                     BoundsData boundsData = new BoundsData(f4 + 8.0F + floats[0], var10x + 1.0F + floats[1] + (1.0F - f6) * 5.0F, floats[2], 12.0F);
                     this.hits.put("quick:" + i, boundsData);
                     boolean contains = boundsData.contains(var7x, var8x);
                     float hot = this.panel.hot("quick:" + (String)suggestions.get(i), contains);
                     var6x.drawRoundedRect(
                        boundsData.getFloatValue(),
                        boundsData.getFloatValue2(),
                        boundsData.width(),
                        12.0F,
                        ProxyDotRadiusData.method8069(6.0F),
                        zenithColor.method7292(0.08F + 0.14F * hot).method7294(var12x * f6)
                     );
                     var6x.drawRoundedBorder(
                        boundsData.getFloatValue(),
                        boundsData.getFloatValue2(),
                        boundsData.width(),
                        12.0F,
                        0.2F,
                        ProxyDotRadiusData.method8069(6.0F),
                        zenithColor.method7292(0.3F + 0.4F * hot).method7294(var12x * f6)
                     );
                     var6x.drawText(
                        font,
                        t((String)suggestions.get(i)),
                        boundsData.getFloatValue() + 7.0F,
                        boundsData.getFloatValue2() + (12.0F - font.height()) / 2.0F,
                        zenithColor.method7295(ZenithColor.INSTANCE, 0.35F + 0.4F * hot).method7294(var12x * f6)
                     );
                  }
               },
               () -> {
                  for (int var3x = 0; var3x < suggestions.size(); var3x++) {
                     if (this.hovered(this.hits.get("quick:" + var3x))) {
                        AiChatStorage.method2741().method2757(hostOrNull, t((String)suggestions.get(var3x) + ".prompt"));
                        this.panel.invalidate();
                        return;
                     }
                  }
               },
               null
            );
      }
   }

   private void welcome(Storage hostOrNull) {
      this.panel
         .custom(
            84.0F,
            this.panel
               .entering(
                  0,
                  (var1x, var2x, var3x, var4x, f, f2, var7x, zenithStyle) -> {
                     Font font = Fonts.NEW_SEMIBOLD.getFont(7.0F);
                     Font font2 = Fonts.NEW_REGULAR.getFont(4.5F);
                     ZenithColor zenithColor = zenithStyle.getPrimaryColor().getZenithColor();
                     float f3 = 0.5F + 0.5F * (float)Math.sin(System.currentTimeMillis() / 700.0);
                     float f4 = var4x + f2 / 2.0F;
                     var1x.drawRoundedRect(
                        f4 - 14.0F,
                        f + 10.0F,
                        28.0F,
                        28.0F,
                        ProxyDotRadiusData.method8069(9.0F),
                        zenithColor.method7292(0.12F + 0.08F * f3).method7294(var7x)
                     );
                     Font font3 = Fonts.NEW_SEMIBOLD.getFont(10.0F);
                     var1x.drawText(font3, "AI", f4 - font3.width("AI") / 2.0F, f + 24.0F - font3.height() / 2.0F, zenithColor.method7294(var7x));
                     String text = t("gui.scripts.ai.welcome");
                     var1x.drawText(font, text, f4 - font.width(text) / 2.0F, f + 44.0F, zenithStyle.getTextEnable().getZenithColor().method7294(var7x));
                     List<String> wrapText = SectionPanel.wrapText(this.readyHint(), font2, f2 - 48.0F, 3);
                     float centeredTextY = f + 47.0F + font.height();

                     for (String str : wrapText) {
                        var1x.drawText(font2, str, f4 - font2.width(str) / 2.0F, centeredTextY, zenithStyle.getTextTertiary().getZenithColor().method7294(var7x));
                        centeredTextY += font2.height() + 2.0F;
                     }
                  }
               )
         );
      int n = 1;
      var entries = AiChatStorage.method2741().method2744();
      if (!entries.isEmpty() && entries.get(0) != AiChatStorage.method2741().getEntry()) {
         AiChatStorage.Entry entry = (AiChatStorage.Entry)entries.get(0);
         this.panel
            .custom(
               26.0F,
               this.panel
                  .entering(
                     n++,
                     (var2x, var3x, var4x, f, f2, var7x, previewAlpha, zenithStyle) -> {
                        BoundsData boundsData = new BoundsData(f + 24.0F, f2 + 1.0F, var7x - 48.0F, 22.0F);
                        boolean contains = boundsData.contains(var3x, var4x);
                        float hot = this.panel.hot("continue", contains);
                        ZenithColor zenithColor = zenithStyle.getPrimaryColor().getZenithColor();
                        var2x.drawRoundedRect(
                           boundsData.getFloatValue(),
                           boundsData.getFloatValue2(),
                           boundsData.width(),
                           boundsData.height(),
                           ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() * 0.7F),
                           zenithColor.method7292(0.1F + 0.1F * hot).method7294(previewAlpha)
                        );
                        var2x.drawRoundedBorder(
                           boundsData.getFloatValue(),
                           boundsData.getFloatValue2(),
                           boundsData.width(),
                           boundsData.height(),
                           0.2F,
                           ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() * 0.7F),
                           zenithColor.method7292(0.35F + 0.3F * hot).method7294(previewAlpha)
                        );
                        Font font = Fonts.NEW_MEDIUM.getFont(4.3F);
                        Font font2 = Fonts.NEW_MEDIUM.getFont(4.6F);
                        var2x.drawText(
                           font,
                           t("gui.scripts.ai.continue"),
                           boundsData.getFloatValue() + 8.0F + hot * 2.0F,
                           boundsData.getFloatValue2() + 4.0F,
                           zenithColor.method7294(previewAlpha)
                        );
                        var2x.drawText(
                           font2,
                           font2.clip(entry.title(), boundsData.width() - 50.0F),
                           boundsData.getFloatValue() + 8.0F + hot * 2.0F,
                           boundsData.getFloatValue2() + 6.0F + font.height(),
                           zenithStyle.getTextEnable().getZenithColor().method7294(previewAlpha)
                        );
                        String ago = ScriptsSectionPanel.ago(entry.getLongValue2());
                        var2x.drawText(
                           font,
                           ago,
                           boundsData.getFloatValue() + boundsData.width() - 8.0F - font.width(ago),
                           boundsData.getFloatValue2() + (boundsData.height() - font.height()) / 2.0F,
                           zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha)
                        );
                     }
                  ),
               () -> {
                  AiChatStorage.method2741().method2749(entry);
                  this.panel.invalidate();
               },
               null
            );
      }

      for (String description : IDEAS) {
         this.panel
            .custom(
               20.0F,
               this.panel
                  .entering(
                     n++,
                     (var2x, var3x, var4x, f, f2, var7x, previewAlpha, currentStyle) -> {
                        BoundsData boundsData = new BoundsData(f + 24.0F, f2 + 1.0F, var7x - 48.0F, 17.0F);
                        boolean contains = boundsData.contains(var3x, var4x);
                        float hot = this.panel.hot("idea:" + description, contains);
                        ScriptsSectionPanel.card(var2x, currentStyle, boundsData.getFloatValue(), boundsData.getFloatValue2(), boundsData.width(), boundsData.height(), hot, previewAlpha);
                        Font font = Fonts.NEW_REGULAR.getFont(4.5F);
                        ZenithColor zenithColor = currentStyle.getPrimaryColor().getZenithColor();
                        var2x.drawText(
                           font,
                           "›",
                           boundsData.getFloatValue() + 7.0F + hot * 2.0F,
                           boundsData.getFloatValue2() + (boundsData.height() - font.height()) / 2.0F,
                           zenithColor.method7294(previewAlpha)
                        );
                        var2x.drawText(
                           font,
                           font.clip(t(description), boundsData.width() - 24.0F),
                           boundsData.getFloatValue() + 15.0F + hot * 2.0F,
                           boundsData.getFloatValue2() + (boundsData.height() - font.height()) / 2.0F,
                           currentStyle.getTextSecondary().getZenithColor().method7295(currentStyle.getTextEnable().getZenithColor(), hot).method7294(previewAlpha)
                        );
                     }
                  ),
               () -> {
                  this.input.set(t(description));
                  this.input.setFocused(true);
               },
               null
            );
      }
   }

   private String readyHint() {
      AiSettingsStore aiSettingsStore = AiSettingsStore.method4467();
      ShownProvider shownProvider = aiSettingsStore.getShownProvider();
      return !aiSettingsStore.method4471(shownProvider) ? t("gui.scripts.ai.welcome.nokey").replace("{p}", shownProvider.title()) : t("gui.scripts.ai.welcome.desc");
   }

   private static Font headFont() {
      return Fonts.NEW_MEDIUM.getFont(4.3F);
   }

   private ScriptAiTab.Layout layout(AiChatStorage.Entry2 entry2, float width, boolean lastOne) {
      String text = entry2.text();
      String errorText = !entry2.isFlag() && !entry2.error().isEmpty() ? this.errorText(entry2) : "";
      ScriptAiTab.CachedLayout layout = this.layouts.get(entry2);
      if (layout != null && layout.matches(entry2, text, errorText, width, lastOne)) {
         return layout.layout();
      } else {
         ScriptAiTab.Layout layout2 = this.computeLayout(entry2, width, lastOne);
         this.layouts
            .put(entry2, new ScriptAiTab.CachedLayout(text, entry2.streaming(), entry2.note(), errorText, entry2.result(), entry2.getItems().size(), width, lastOne, layout2));
         return layout2;
      }
   }

   private ScriptAiTab.Layout computeLayout(AiChatStorage.Entry2 entry2, float width, boolean lastOne) {
      Font font = Fonts.NEW_REGULAR.getFont(4.6F);
      boolean flag = !entry2.isFlag() && !entry2.note();
      float f = flag ? 7.0F : 5.0F;
      float bubble = entry2.isFlag() ? Math.min(width * 0.78F, width - 16.0F) : width - 16.0F;
      ArrayList<ScriptAiTab.Piece> pieces = new ArrayList();
      String text = entry2.text();
      Matcher matcher = BLOCK.matcher(text);

      int end;
      for (end = 0; matcher.find(); end = matcher.end()) {
         if (matcher.start() > end) {
            pieces.add(new ScriptAiTab.Piece(0, text.substring(end, matcher.start()), "", 0));
         }

         String input = matcher.group(1).trim();
         String group = matcher.group(2);
         String stripTrailing = group.stripTrailing();
         int length = stripTrailing.isEmpty() ? 0 : stripTrailing.split("\n", -1).length;
         Matcher matcher2 = FILE.matcher(input);
         if (matcher2.find()) {
            pieces.add(new ScriptAiTab.Piece(1, matcher2.group(1).trim(), matcher.group(3).isEmpty() ? "…" : "", length));
         } else {
            pieces.add(new ScriptAiTab.Piece(2, group, input, length));
         }
      }

      if (end < text.length()) {
         pieces.add(new ScriptAiTab.Piece(0, text.substring(end), "", 0));
      }

      ArrayList wrapped = new ArrayList();
      float f2 = Math.max(0.0F, 7.4F - font.height());
      float f3 = f;
      float f4 = 0.0F;
      if (!entry2.getItems().isEmpty()) {
         f3 = f + 48.0F;
         f4 = 4.0F;
      }

      if (flag) {
         f3 += headFont().height() + 4.0F;
         f4 = 4.0F;
      }

      for (ScriptAiTab.Piece piece : pieces) {
         if (piece.kind == 0) {
            ArrayList arrayList = new ArrayList();

            for (String description : clean(piece.text).split("\n")) {
               if (description.isBlank()) {
                  if (!arrayList.isEmpty() && !((String)arrayList.get(arrayList.size() - 1)).isEmpty()) {
                     arrayList.add("");
                  }
               } else {
                  arrayList.addAll(SectionPanel.wrapText(description, font, bubble - 14.0F, 400));
               }
            }

            while (!arrayList.isEmpty() && ((String)arrayList.get(arrayList.size() - 1)).isEmpty()) {
               arrayList.remove(arrayList.size() - 1);
            }

            wrapped.add(arrayList);
            if (!arrayList.isEmpty()) {
               f3 += arrayList.size() * 7.4F;
               f4 = f2;
            }
         } else if (piece.kind == 1) {
            wrapped.add(List.of());
            f3 += 16.0F;
            f4 = 3.0F;
         } else {
            ArrayList arrayList = new ArrayList();
            String[] strings = piece.text.split("\n");

            for (int i = 0; i < Math.min(6, strings.length); i++) {
               arrayList.add(strings[i].replace("\t", "  "));
            }

            wrapped.add(arrayList);
            f3 += codeHeight(arrayList.size(), piece.lines) + 2.0F;
            f4 = 2.0F;
         }
      }

      if (text.isEmpty() && entry2.streaming()) {
         f3 += 7.4F;
         f4 = f2;
      }

      if (flag) {
         if (!entry2.error().isEmpty()) {
            f3 += 2.0F + SectionPanel.wrapText(this.errorText(entry2), font, bubble - 14.0F, 6).size() * 7.4F;
            f4 = f2;
         }

         if (!entry2.result().isEmpty()) {
            f3 += 2.0F + SectionPanel.wrapText(entry2.result(), font, bubble - 14.0F, 6).size() * 7.4F;
            f4 = f2;
         }

         if (!entry2.streaming() && hasButtons(entry2, lastOne)) {
            f3 += 15.0F;
            f4 = 0.0F;
         }
      }

      return new ScriptAiTab.Layout(pieces, wrapped, f3 - f4 + f, bubble);
   }

   private static float codeHeight(int size, int lines) {
      return size * 6.6F + 6.0F + (lines > 6 ? 6.6F : 0.0F);
   }

   private String extractCode(AiChatStorage.Entry2 entry2, int n) {
      Matcher matcher = BLOCK.matcher(entry2.text());
      int n2 = 0;
      int end = 0;

      for (int i = 0; matcher.find(); n2++) {
         if (matcher.start() > end) {
            i++;
         }

         if (i == n) {
            return matcher.group(2);
         }

         i++;
         end = matcher.end();
      }

      return n2 == 0 ? entry2.text() : "";
   }

   private static String clean(String text) {
      return HEADING.matcher(text.replace("**", "").replace("__", "")).replaceAll("").replace("`", "");
   }

   private static boolean hasButtons(AiChatStorage.Entry2 entry2, boolean lastOne) {
      return !entry2.method2775().isEmpty() || !entry2.error().isEmpty() || entry2.isFlag3() || !entry2.text().isBlank() || lastOne;
   }

   private String errorText(AiChatStorage.Entry2 entry2) {
      return entry2.error().equals("nokey")
         ? t("gui.scripts.ai.nokey.msg").replace("{p}", AiSettingsStore.method4467().getShownProvider().title())
         : entry2.error();
   }

   private void renderMessage(
      MenuDrawContext menuDrawContext,
      int lastClickX,
      int lastClickY,
      float f,
      float f2,
      float f3,
      float previewAlpha,
      ZenithStyle currentStyle,
      Storage hostOrNull,
      AiChatStorage.Entry2 entry2,
      ScriptAiTab.Layout layout,
      int n,
      boolean lastOne
   ) {
      Font font = Fonts.NEW_REGULAR.getFont(4.6F);
      Font headFont2 = headFont();
      Font font2 = Fonts.NEW_REGULAR.getFont(4.3F);
      ZenithColor zenithColor = currentStyle.getPrimaryColor().getZenithColor();
      float hot = this.panel.hot("msg:" + n + ":" + entry2.getLongValue(), true);
      float f4 = (1.0F - hot) * 6.0F;
      previewAlpha *= hot;
      boolean flag = !entry2.isFlag() && !entry2.note();
      float floatValue = entry2.isFlag() ? f + f3 - 8.0F - layout.bubble : f + 8.0F;
      float y = f2 + 3.0F + f4;
      float height = layout.height;
      ProxyDotRadiusData headerRadius = ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() * 0.8F);
      if (entry2.note()) {
         menuDrawContext.drawRoundedRect(floatValue, y, layout.bubble, height, headerRadius, currentStyle.getDisableActiveBg().getZenithColor().method7294(previewAlpha * 0.35F));
         menuDrawContext.drawRoundedRect(floatValue, y + 4.0F, 2.0F, height - 8.0F, ProxyDotRadiusData.method8069(1.0F), zenithColor.method7294(previewAlpha * 0.6F));
      } else if (entry2.isFlag()) {
         menuDrawContext.drawRoundedRect(floatValue, y, layout.bubble, height, headerRadius, zenithColor.method7292(entry2.isFlag5() ? 0.08F : 0.16F).method7294(previewAlpha));
         Font font3 = Fonts.NEW_REGULAR.getFont(4.2F);
         String text = CLOCK.format(new Date(entry2.getLongValue()));
         boolean contains = new BoundsData(floatValue - 30.0F, y, layout.bubble + 30.0F, height).contains(lastClickX, lastClickY);
         float hot2 = this.panel.hot("time:" + entry2.getLongValue(), contains);
         menuDrawContext.drawText(
            font3,
            text,
            floatValue - 5.0F - font3.width(text),
            y + (height - font3.height()) / 2.0F,
            currentStyle.getTextTertiary().getZenithColor().method7294(previewAlpha * (0.35F + 0.65F * hot2))
         );
      } else {
         ScriptsSectionPanel.card(menuDrawContext, currentStyle, floatValue, y, layout.bubble, height, 0.0F, previewAlpha);
      }

      float centeredTextY = y + (flag ? 7.0F : 5.0F);
      if (!entry2.getItems().isEmpty()) {
         float floatValue2 = floatValue + 7.0F;

         for (int i = 0; i < entry2.getItems().size(); i++) {
            String key = entry2.getItems().get(i);
            Identifier texture = AiImageStore.method4639(key);
            int[] ints = AiImageStore.method4638(key);
            float width = Math.max(26.400002F, Math.min(96.8F, 44.0F * ints[0] / Math.max(1, ints[1])));
            if (floatValue2 + width > floatValue + layout.bubble - 7.0F) {
               break;
            }

            BoundsData boundsData = new BoundsData(floatValue2, centeredTextY, width, 44.0F);
            this.hits.put("img:" + i + ":" + n, boundsData);
            float hot2 = this.panel.hot("img:" + key, boundsData.contains(lastClickX, lastClickY));
            thumb(menuDrawContext, texture, boundsData, 5.0F, ZenithColor.INSTANCE.method7294(previewAlpha * (0.15F + 0.5F * hot2)), previewAlpha, 1.0F);
            if (boundsData.contains(lastClickX, lastClickY)) {
               HoverInfo.request(t("gui.scripts.ai.image"), t("gui.scripts.ai.image.desc"));
            }

            floatValue2 += width + 4.0F;
         }

         centeredTextY += 48.0F;
      }

      if (flag) {
         String name = entry2.getText2().isEmpty() ? "AI" : entry2.getText2();
         menuDrawContext.drawText(headFont2, headFont2.clip(name, layout.bubble - 60.0F), floatValue + 7.0F, centeredTextY, zenithColor.method7294(previewAlpha * 0.9F));
         String text = CLOCK.format(new Date(entry2.getLongValue()));
         menuDrawContext.drawText(headFont2, text, floatValue + layout.bubble - 7.0F - headFont2.width(text), centeredTextY, currentStyle.getTextTertiary().getZenithColor().method7294(previewAlpha * 0.8F));
         if (entry2.streaming()) {
            long now = System.currentTimeMillis();

            for (int i = 0; i < 3; i++) {
               float f5 = (float)Math.sin(now / 180.0 - i * 0.9);
               menuDrawContext.drawRoundedRect(
                  floatValue + 10.0F + headFont2.width(headFont2.clip(name, layout.bubble - 60.0F)) + i * 4.0F,
                  centeredTextY + (headFont2.height() - 2.0F) / 2.0F - Math.max(0.0F, f5) * 1.2F,
                  2.0F,
                  2.0F,
                  ProxyDotRadiusData.method8069(1.0F),
                  zenithColor.method7294(previewAlpha * (0.4F + 0.6F * Math.max(0.0F, f5)))
               );
            }
         }

         centeredTextY += headFont2.height() + 4.0F;
      }

      ZenithColor zenithColor2 = entry2.isFlag()
         ? currentStyle.getTextEnable().getZenithColor()
         : currentStyle.getTextSecondary().getZenithColor().method7295(currentStyle.getTextEnable().getZenithColor(), 0.5F);

      for (int i = 0; i < layout.pieces.size(); i++) {
         ScriptAiTab.Piece piece = layout.pieces.get(i);
         List<String> strings = layout.wrapped.get(i);
         if (piece.kind == 0) {
            for (String text : strings) {
               menuDrawContext.drawText(font, text, floatValue + 7.0F, centeredTextY, zenithColor2.method7294(previewAlpha));
               centeredTextY += 7.4F;
            }
         } else if (piece.kind == 1) {
            String text = piece.text + "  ·  " + piece.lines + " " + t("gui.scripts.ai.lines") + piece.extra;
            float width = Math.min(layout.bubble - 14.0F, headFont2.width(text) + 22.0F);
            BoundsData boundsData = new BoundsData(floatValue + 7.0F, centeredTextY, width, 13.0F);
            this.hits.put("file:" + i + ":" + n, boundsData);
            float hot2 = this.panel.hot("chip:" + n + ":" + i, boundsData.contains(lastClickX, lastClickY));
            menuDrawContext.drawRoundedRect(
               floatValue + 7.0F, centeredTextY, width, 13.0F, ProxyDotRadiusData.method8069(4.0F), zenithColor.method7292(0.12F + 0.12F * hot2).method7294(previewAlpha)
            );
            if (boundsData.contains(lastClickX, lastClickY)) {
               HoverInfo.request(piece.text, t("gui.scripts.ai.chip.desc"));
            }

            menuDrawContext.drawRoundedRect(floatValue + 11.0F, centeredTextY + 3.5F, 5.0F, 6.0F, ProxyDotRadiusData.method8069(1.0F), zenithColor.method7292(0.6F).method7294(previewAlpha));
            menuDrawContext.drawText(
               headFont2,
               headFont2.clip(text, width - 22.0F),
               floatValue + 20.0F,
               centeredTextY + (13.0F - headFont2.height()) / 2.0F,
               zenithColor.method7295(ZenithColor.INSTANCE, 0.3F).method7294(previewAlpha)
            );
            centeredTextY += 16.0F;
         } else {
            float codeHeight2 = codeHeight(strings.size(), piece.lines);
            menuDrawContext.drawRoundedRect(
               floatValue + 7.0F, centeredTextY, layout.bubble - 14.0F, codeHeight2, ProxyDotRadiusData.method8069(3.0F), new ZenithColor(10, 11, 16, 140).method7294(previewAlpha)
            );
            float centeredTextY2 = centeredTextY + 3.0F;

            for (String name : strings) {
               menuDrawContext.drawText(
                  font2, font2.clip(name, layout.bubble - 24.0F), floatValue + 11.0F, centeredTextY2, currentStyle.getTextSecondary().getZenithColor().method7294(previewAlpha * 0.9F)
               );
               centeredTextY2 += 6.6F;
            }

            if (piece.lines > 6) {
               menuDrawContext.drawText(font2, "… +" + (piece.lines - 6), floatValue + 11.0F, centeredTextY2, currentStyle.getTextTertiary().getZenithColor().method7294(previewAlpha));
            }

            centeredTextY += codeHeight2 + 2.0F;
         }
      }

      if (entry2.text().isEmpty() && entry2.streaming()) {
         menuDrawContext.drawText(
            font,
            t("gui.scripts.ai.thinking"),
            floatValue + 7.0F,
            centeredTextY,
            currentStyle.getTextTertiary().getZenithColor().method7294(previewAlpha * (0.5F + 0.5F * (float)Math.abs(Math.sin(System.currentTimeMillis() / 400.0))))
         );
         centeredTextY += 7.4F;
      }

      if (flag) {
         if (!entry2.error().isEmpty()) {
            centeredTextY += 2.0F;
            boolean flag2 = entry2.error().equals("nokey");

            for (String text : SectionPanel.wrapText(this.errorText(entry2), font, layout.bubble - 14.0F, 6)) {
               menuDrawContext.drawText(font, text, floatValue + 7.0F, centeredTextY, (flag2 ? WARN : ERROR).method7294(previewAlpha));
               centeredTextY += 7.4F;
            }
         }

         if (!entry2.result().isEmpty()) {
            centeredTextY += 2.0F;

            for (String text : SectionPanel.wrapText(entry2.result(), font, layout.bubble - 14.0F, 6)) {
               menuDrawContext.drawText(font, text, floatValue + 7.0F, centeredTextY, (entry2.isFlag3() ? ERROR : OK).method7294(previewAlpha));
               centeredTextY += 7.4F;
            }
         }

         if (!entry2.streaming() && hasButtons(entry2, lastOne)) {
            float button = floatValue + 7.0F;
            float y2 = centeredTextY + 3.0F;
            if (!entry2.method2775().isEmpty()) {
               button = this.button(
                  menuDrawContext,
                  lastClickX,
                  lastClickY,
                  currentStyle,
                  "apply:" + n,
                  entry2.isFlag4() ? t("gui.scripts.ai.reapply") : t("gui.scripts.ai.apply").replace("{n}", String.valueOf(entry2.method2775().size())),
                  button,
                  y2,
                  true,
                  previewAlpha
               );
               if (entry2.isFlag4()) {
                  button = this.button(menuDrawContext, lastClickX, lastClickY, currentStyle, "ide:" + n, t("gui.scripts.ai.openIde"), button, y2, false, previewAlpha);
               }
            }

            if (entry2.error().equals("nokey")) {
               button = this.button(menuDrawContext, lastClickX, lastClickY, currentStyle, "key:" + n, t("gui.scripts.ai.nokey.button"), button, y2, true, previewAlpha);
            } else if (!entry2.error().isEmpty()) {
               button = this.button(menuDrawContext, lastClickX, lastClickY, currentStyle, "retry:" + n, t("gui.scripts.ai.retry"), button, y2, true, previewAlpha);
            }

            if (entry2.isFlag3()) {
               button = this.button(menuDrawContext, lastClickX, lastClickY, currentStyle, "fix:" + n, t("gui.scripts.ai.fix"), button, y2, false, previewAlpha);
            }

            if (!entry2.text().isBlank()) {
               long l = this.pressedAt.getOrDefault("copy:" + n, 0L);
               boolean flag2 = System.currentTimeMillis() - l < 1500L;
               button = this.button(
                  menuDrawContext, lastClickX, lastClickY, currentStyle, "copy:" + n, t(flag2 ? "gui.scripts.ai.copied" : "gui.scripts.ai.copy"), button, y2, false, previewAlpha
               );
            }

            if (lastOne && !AiChatStorage.method2741().isBusy()) {
               this.button(menuDrawContext, lastClickX, lastClickY, currentStyle, "again:" + n, t("gui.scripts.ai.again"), button, y2, false, previewAlpha);
            }
         }
      }
   }

   private float button(MenuDrawContext menuDrawContext, int lastClickX, int lastClickY, ZenithStyle currentStyle, String key, String text, float button, float y, boolean flag, float previewAlpha) {
      Font font = Fonts.NEW_MEDIUM.getFont(4.5F);
      float width = font.width(text) + 12.0F;
      BoundsData boundsData = new BoundsData(button, y, width, 12.0F);
      this.hits.put(key, boundsData);
      boolean contains = boundsData.contains(lastClickX, lastClickY);
      float hot = this.panel.hot("aib:" + key, contains);
      float press = Smooth.press(this.pressedAt.getOrDefault(key, 0L), 320L);
      ZenithColor zenithColor = currentStyle.getPrimaryColor().getZenithColor();
      ZenithColor zenithColor2 = flag
         ? zenithColor.method7292(0.2F + 0.18F * hot)
         : currentStyle.getFieldSurfaceBackground().getZenithColor().method7295(currentStyle.getDisableActiveBg().getZenithColor(), 0.4F + hot * 0.5F);
      menuDrawContext.drawRoundedRect(button, y, width, 12.0F, ProxyDotRadiusData.method8069(6.0F), zenithColor2.method7294(previewAlpha));
      if (press > 0.01F) {
         float f = (1.0F - press) * 3.0F;
         menuDrawContext.drawRoundedRect(
            button - f / 2.0F,
            y - f / 2.0F,
            width + f,
            12.0F + f,
            ProxyDotRadiusData.method8069(6.0F + f / 2.0F),
            zenithColor.method7292(0.3F * press).method7294(previewAlpha)
         );
      }

      menuDrawContext.drawText(
         font,
         text,
         button + 6.0F,
         y + (12.0F - font.height()) / 2.0F,
         (flag
               ? zenithColor.method7295(ZenithColor.INSTANCE, 0.3F + hot * 0.4F)
               : currentStyle.getTextSecondary().getZenithColor().method7295(currentStyle.getTextEnable().getZenithColor(), hot))
            .method7294(previewAlpha)
      );
      return button + width + 4.0F;
   }

   private void clickMessage(Storage hostOrNull, AiChatStorage.Entry2 entry2, int n) {
      AiChatStorage aiChatStorage = AiChatStorage.method2741();

      for (Entry entry : this.hits.entrySet()) {
         if (((String)entry.getKey()).endsWith(":" + n) && this.hovered((BoundsData)entry.getValue())) {
            this.pressedAt.put((String)entry.getKey(), System.currentTimeMillis());
            String str = ((String)entry.getKey()).substring(0, ((String)entry.getKey()).indexOf(58));
            label96:
            switch (str) {
               case "apply":
                  aiChatStorage.method2767(hostOrNull, entry2);
                  break;
               case "ide":
                  Script workspace = aiChatStorage.method2747() == null ? null : hostOrNull.method1294(aiChatStorage.method2747());
                  if (workspace != null) {
                     this.panel.openWorkspace(workspace);
                  }
                  break;
               case "retry":
                  var entry2s = aiChatStorage.method2745();

                  for (int i = entry2s.indexOf(entry2) - 1; i >= 0; i--) {
                     if (((AiChatStorage.Entry2)entry2s.get(i)).isFlag()) {
                        aiChatStorage.method2757(hostOrNull, ((AiChatStorage.Entry2)entry2s.get(i)).text());
                        break label96;
                     }
                  }
                  break;
               case "fix":
                  aiChatStorage.method2769(hostOrNull);
                  break;
               case "copy":
                  Minecraft.getInstance().keyboardHandler.setClipboard(entry2.text());
                  break;
               case "img":
                  int n2 = Integer.parseInt(((String)entry.getKey()).split(":")[1]);
                  if (n2 < entry2.getItems().size()) {
                     com.mojang.blaze3d.Blaze3D.openPath((AiImageStore.method4637(entry2.getItems().get(n2)).toFile()).toPath());
                  }
                  break;
               case "file":
                  int n3 = Integer.parseInt(((String)entry.getKey()).split(":")[1]);
                  ScriptAiTab.CachedLayout layout = this.layouts.get(entry2);
                  ScriptAiTab.Layout layout2 = layout != null ? layout.layout() : this.layout(entry2, 236.0F, false);
                  Script script = aiChatStorage.method2747() == null ? null : hostOrNull.method1294(aiChatStorage.method2747());
                  if (n3 < layout2.pieces.size() && script != null && entry2.isFlag4()) {
                     this.panel.openWorkspaceFile(script, layout2.pieces.get(n3).text);
                  } else {
                     Minecraft.getInstance().keyboardHandler.setClipboard(this.extractCode(entry2, n3));
                     HudHolder.method6969().method6973("i", Component.literal(t("gui.scripts.ai.chip.copied")), 2000L);
                  }
                  break;
               case "again":
                  aiChatStorage.method2761(hostOrNull);
                  break;
               case "key":
                  this.asideView = 1;
                  this.focus(this.keyField);
            }

            this.panel.invalidate();
            return;
         }
      }
   }

   public void mouse(double lastMouseX, double lastMouseY) {
      this.lastMouseX = lastMouseX;
      this.lastMouseY = lastMouseY;
   }

   private boolean hovered(BoundsData hit) {
      return hit != null && hit.contains(this.lastMouseX, this.lastMouseY);
   }

   private int view() {
      return this.asideView >= 0 ? this.asideView : (AiSettingsStore.method4467().method4471(AiSettingsStore.method4467().getShownProvider()) ? 0 : 1);
   }

   private void aside(Storage hostOrNull) {
      AiSettingsStore aiSettingsStore = AiSettingsStore.method4467();
      ShownProvider shownProvider = aiSettingsStore.getShownProvider();
      int view = this.view();
      this.panel
         .asideCustom(
            11.0F + GuiStyle.PADDING.intValue() * 2.0F,
            (var1x, var2x, var3x, var4x, var5x, var6x, var7x, zenithStyle) -> this.asideTabs
               .render(var1x, var2x, var3x, var4x + 8.0F, var5x + GuiStyle.PADDING.intValue(), var6x - 16.0F, this.view(), var7x, zenithStyle),
            () -> {
               int var1x = this.asideTabs.hovered();
               if (var1x >= 0 && var1x != this.view()) {
                  this.commitFields();
                  this.asideView = var1x;
                  this.panel.resetAsideScroll();
                  this.panel.replayAside();
                  this.panel.invalidate();
               }
            },
            null
         );
      if (view == 0) {
         this.chatsAside(hostOrNull);
      } else {
         this.leadCaption("gui.scripts.ai.caption.provider");
         this.providerGrid();
         if (this.adding) {
            this.addForm();
         } else {
            this.panel.asideCaption("gui.scripts.ai.caption.key");
            if (!shownProvider.isFlag() && !shownProvider.isFlag2()) {
               this.panel.paragraph(t("gui.scripts.ai.key.local"), true);
            } else {
               this.field(this.keyField, t("gui.scripts.ai.key.hint").replace("{p}", shownProvider.title()), "paste", t("gui.scripts.ai.key.paste"));
               Font font = Fonts.NEW_REGULAR.getFont(4.5F);
               float shownWidth = 122.0F;
               int n = Math.max(
                  SectionPanel.wrapText(t("gui.scripts.ai.key.none"), font, shownWidth, 2).size(),
                  SectionPanel.wrapText(t("gui.scripts.ai.key.saved").replace("{k}", AiSettingsStore.method4480("sk-ant-0000000000")), font, shownWidth, 2).size()
               );
               this.panel
                  .asideCustom(
                     n * (font.height() + 2.0F) + 4.0F,
                     (var3x, var4x, var5x, var6x, var7x, f, f2, zenithStyle) -> {
                        String str = AiSettingsStore.method4467().method4469(AiSettingsStore.method4467().getShownProvider());
                        boolean flag = !str.isEmpty();
                        float hot = flag ? this.panel.hot("keyok:" + AiSettingsStore.method4467().getShownProvider().getText(), true) : 0.0F;
                        String description = flag ? t("gui.scripts.ai.key.saved").replace("{k}", AiSettingsStore.method4480(str)) : t("gui.scripts.ai.key.none");
                        float centeredTextY = var7x + 2.0F;
                        if (flag) {
                           var3x.drawRoundedRect(
                              var6x + 8.0F + 1.0F,
                              centeredTextY + (font.height() - 3.0F) / 2.0F,
                              3.0F,
                              3.0F,
                              ProxyDotRadiusData.method8069(1.5F),
                              OK.method7294(f2 * hot)
                           );
                        }

                        ZenithColor zenithColor = (flag ? OK.method7295(zenithStyle.getTextTertiary().getZenithColor(), 0.35F) : zenithStyle.getTextTertiary().getZenithColor())
                           .method7294(f2 * (flag ? hot : 1.0F));

                        for (String text : SectionPanel.wrapText(description, font, f - 16.0F - 6.0F, n)) {
                           var3x.drawText(font, text, var6x + 8.0F + (flag ? 6.0F : 0.0F), centeredTextY, zenithColor);
                           centeredTextY += font.height() + 2.0F;
                        }
                     }
                  );
               if (!shownProvider.isFlag2()) {
                  this.panel
                     .asideAction(
                        "gui.scripts.ai.key.where",
                        MenuGlyph.NETWORK,
                        () -> "",
                        () -> org.zenith.utility.misc.OsOpen.uri(shownProvider.getText5()),
                        "gui.scripts.ai.key.where.desc"
                     );
               }
            }

            this.panel.asideCaption("gui.scripts.ai.caption.model");
            this.field(this.modelField, t("gui.scripts.ai.model.hint"), null, null);
            this.modelChips(shownProvider);
            if (shownProvider.equals(ShownProvider.INSTANCE9) || shownProvider.isFlag2()) {
               this.field(this.endpointField, t("gui.scripts.ai.endpoint.hint"), null, null);
            }

            if (shownProvider.isFlag2()) {
               this.panel
                  .asideAction(
                     "gui.scripts.ai.custom.remove",
                     MenuGlyph.CLOSE,
                     () -> this.armedDelete.equals(shownProvider.getText()) && System.currentTimeMillis() - this.armedDeleteAt < 3000L
                        ? t("gui.scripts.market.again")
                        : "",
                     () -> {
                        if (this.armedDelete.equals(shownProvider.getText()) && System.currentTimeMillis() - this.armedDeleteAt < 3000L) {
                           this.armedDelete = "";
                           AiSettingsStore.method4467().method4477(shownProvider);
                           this.syncFields();
                        } else {
                           this.armedDelete = shownProvider.getText();
                           this.armedDeleteAt = System.currentTimeMillis();
                        }

                        this.panel.invalidate();
                     },
                     "gui.scripts.ai.custom.remove.desc"
                  );
            }

            this.panel.asideCaption("gui.scripts.ai.caption.behaviour");
            this.toggle("autoApply", "gui.scripts.ai.autoApply", "gui.scripts.ai.autoApply.desc", aiSettingsStore::isFlag2, aiSettingsStore::method4478);
            this.toggle("autoFix", "gui.scripts.ai.autoFix", "gui.scripts.ai.autoFix.desc", aiSettingsStore::isFlag3, aiSettingsStore::method4479);
            this.panel.paragraph(t("gui.scripts.ai.privacy"), true);
         }
      }
   }

   private void leadCaption(String description) {
      Font font = Fonts.NEW_MEDIUM.getFont(4.0F);
      float f = (16.0F - font.height()) / 2.0F - 2.0F;
      this.panel
         .asideCustom(
            2.0F + font.height() + f,
            (var2x, var3x, n, f2, f3, f4, previewAlpha, zenithStyle) -> var2x.drawText(
               font, HoverInfo.spaced(t(description).toUpperCase(Locale.ROOT)), f2 + 8.0F, f3 + 2.0F, zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha)
            )
         );
   }

   private void cardFrame(MenuDrawContext menuDrawContext, ZenithStyle currentStyle, BoundsData boundsData, float hot, float f, float press, float previewAlpha) {
      ScriptsSectionPanel.card(menuDrawContext, currentStyle, boundsData.getFloatValue(), boundsData.getFloatValue2(), boundsData.width(), boundsData.height(), Math.max(hot, f * 0.6F), previewAlpha);
      ZenithColor zenithColor = currentStyle.getPrimaryColor().getZenithColor();
      if (f > 0.01F) {
         menuDrawContext.drawRoundedBorder(
            boundsData.getFloatValue(),
            boundsData.getFloatValue2(),
            boundsData.width(),
            boundsData.height(),
            0.25F,
            ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() * 0.7F),
            zenithColor.method7294(previewAlpha * 0.7F * f)
         );
      }

      if (press > 0.01F) {
         menuDrawContext.drawRoundedRect(
            boundsData.getFloatValue(),
            boundsData.getFloatValue2(),
            boundsData.width(),
            boundsData.height(),
            ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() * 0.7F),
            zenithColor.method7292(0.16F * press).method7294(previewAlpha)
         );
      }
   }

   private void iconTile(MenuDrawContext menuDrawContext, ZenithStyle currentStyle, MenuGlyph menuGlyph, float floatValue, float y, float width, boolean flag, float previewAlpha) {
      ZenithColor zenithColor = currentStyle.getPrimaryColor().getZenithColor();
      menuDrawContext.drawRoundedRect(
         floatValue,
         y,
         width,
         width,
         ProxyDotRadiusData.method8069(width * 0.28F),
         (flag ? zenithColor.method7292(0.18F) : currentStyle.getDisableActiveBg().getZenithColor().method7294(0.8F)).method7294(previewAlpha)
      );
      menuGlyph.draw(
         menuDrawContext,
         floatValue + (width - 6.0F) / 2.0F,
         y + (width - 6.0F) / 2.0F,
         (flag ? zenithColor.method7295(ZenithColor.INSTANCE, 0.2F) : currentStyle.getTextSecondary().getZenithColor()).method7294(previewAlpha)
      );
   }

   private void chatsAside(Storage hostOrNull) {
      AiChatStorage aiChatStorage = AiChatStorage.method2741();
      this.panel
         .asideCustom(
            26.0F,
            this.panel
               .asideEntering(
                  0,
                  (var1x, var2x, var3x, f, var5x, var6x, previewAlpha, var8x) -> {
                     BoundsData boundsData = new BoundsData(f + 8.0F, var5x + 2.0F, var6x - 16.0F, 22.0F);
                     float hot = this.panel.hot("newchat", boundsData.contains(var2x, var3x));
                     float press = Smooth.press(this.pressedAt.getOrDefault("newchat", 0L), 320L);
                     this.cardFrame(var1x, var8x, boundsData, hot, 0.0F, press, previewAlpha);
                     float width = 14.0F;
                     this.iconTile(var1x, var8x, MenuGlyph.CHAT, boundsData.getFloatValue() + 4.0F, boundsData.getFloatValue2() + 4.0F, width, true, previewAlpha);
                     Font font = Fonts.NEW_MEDIUM.getFont(4.8F);
                     var1x.drawText(
                        font,
                        t("gui.scripts.ai.newChat"),
                        boundsData.getFloatValue() + width + 9.0F,
                        boundsData.getFloatValue2() + (22.0F - font.height()) / 2.0F,
                        var8x.getTextEnable().getZenithColor().method7294(previewAlpha)
                     );
                     String text = "+";
                     Font font2 = Fonts.NEW_MEDIUM.getFont(6.0F);
                     var1x.drawText(
                        font2,
                        text,
                        boundsData.getFloatValue() + boundsData.width() - 8.0F - font2.width(text),
                        boundsData.getFloatValue2() + (22.0F - font2.height()) / 2.0F,
                        var8x.getPrimaryColor().getZenithColor().method7295(ZenithColor.INSTANCE, hot * 0.4F).method7294(previewAlpha)
                     );
                     if (boundsData.contains(var2x, var3x)) {
                        HoverInfo.request(t("gui.scripts.ai.newChat"), t("gui.scripts.ai.newChat.desc"));
                     }
                  }
               ),
            () -> {
               this.pressedAt.put("newchat", System.currentTimeMillis());
               AiChatStorage.method2741().method2750(null);
               this.input.clear();
               this.focus(this.input);
               this.panel.replayEntrance();
               this.panel.invalidate();
            },
            null
         );
      if (aiChatStorage.isBusy()) {
         this.panel.asideAction("gui.scripts.ai.stop", MenuGlyph.CLOSE, () -> "", () -> AiChatStorage.method2741().stop(), "gui.scripts.ai.stop.desc");
      }

      this.panel.asideCaption("gui.scripts.ai.caption.target");
      int n = 1;
      this.target(hostOrNull, null, n++);

      for (Script script : hostOrNull.method1293()) {
         this.target(hostOrNull, script, n++);
      }

      List<AiChatStorage.Entry> entries = aiChatStorage.method2744();
      this.panel.asideCaption("gui.scripts.ai.caption.history");
      if (entries.isEmpty()) {
         this.panel.paragraph(t("gui.scripts.ai.history.empty"), true);
      } else {
         for (AiChatStorage.Entry sub : entries.subList(0, Math.min(40, entries.size()))) {
            this.panel
               .asideCustom(
                  31.0F,
                  this.panel
                     .asideEntering(
                        n++,
                        (var3x, lastClickX, var5x, var6x, f, var8x, previewAlpha, currentStyle) -> this.renderChatRow(
                           var3x, lastClickX, var5x, var6x, f, var8x, previewAlpha, currentStyle, hostOrNull, sub
                        )
                     ),
                  () -> {
                     AiChatStorage.method2741().method2749(sub);
                     this.panel.invalidate();
                  },
                  null,
                  () -> {
                     if (this.armedChat.equals(sub.getText()) && System.currentTimeMillis() - this.armedChatAt < 3000L) {
                        this.armedChat = "";
                        AiChatStorage.method2741().method2752(sub);
                     } else {
                        this.armedChat = sub.getText();
                        this.armedChatAt = System.currentTimeMillis();
                     }

                     this.panel.invalidate();
                  }
               );
         }

         this.panel.gap(2.0F);
         if (!aiChatStorage.getEntry().empty()) {
            this.panel.asideAction("gui.scripts.ai.export", MenuGlyph.STORAGE, () -> "", () -> {
               try {
                  Path path = AiChatStorage.method2741().method2760();
                  com.mojang.blaze3d.Blaze3D.openPath((path.getParent().toFile()).toPath());
               } catch (Exception var1x) {
                  AiChatStorage.method2741().method2759(String.valueOf(var1x.getMessage()));
               }
            }, "gui.scripts.ai.export.desc");
         }

         this.panel
            .asideAction(
               "gui.scripts.ai.history.clear",
               MenuGlyph.CLOSE,
               () -> this.armedChat.equals("*") && System.currentTimeMillis() - this.armedChatAt < 3000L ? t("gui.scripts.market.again") : "",
               () -> {
                  if (this.armedChat.equals("*") && System.currentTimeMillis() - this.armedChatAt < 3000L) {
                     this.armedChat = "";
                     AiChatStorage.method2741().method2753();
                  } else {
                     this.armedChat = "*";
                     this.armedChatAt = System.currentTimeMillis();
                  }

                  this.panel.invalidate();
               },
               "gui.scripts.ai.history.clear.desc"
            );
      }
   }

   private void renderChatRow(
      MenuDrawContext menuDrawContext, int lastClickX, int lastClickY, float f, float f2, float f3, float previewAlpha, ZenithStyle currentStyle, Storage hostOrNull, AiChatStorage.Entry sub
   ) {
      BoundsData boundsData = new BoundsData(f + 8.0F, f2 + 1.0F, f3 - 16.0F, 28.0F);
      boolean contains = boundsData.contains(lastClickX, lastClickY);
      boolean flag = sub == AiChatStorage.method2741().getEntry();
      boolean flag2 = this.armedChat.equals(sub.getText()) && System.currentTimeMillis() - this.armedChatAt < 3000L;
      float hot = this.panel.hot("chat:" + sub.getText(), contains);
      float hot2 = this.panel.hot("chatsel:" + sub.getText(), flag);
      this.cardFrame(menuDrawContext, currentStyle, boundsData, hot, hot2, 0.0F, previewAlpha);
      if (flag2) {
         menuDrawContext.drawRoundedRect(
            boundsData.getFloatValue(),
            boundsData.getFloatValue2(),
            boundsData.width(),
            boundsData.height(),
            ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() * 0.7F),
            ERROR.method7292(0.12F).method7294(previewAlpha)
         );
      }

      float width = 20.0F;
      float floatValue = boundsData.getFloatValue() + 4.0F;
      Script script = sub.getText2() == null ? null : hostOrNull.method1294(sub.getText2());
      if (script != null) {
         ScriptInfoPage.drawAvatar(menuDrawContext, currentStyle, script, floatValue, boundsData.getFloatValue2() + 4.0F, width, previewAlpha, -2);
      } else {
         this.iconTile(menuDrawContext, currentStyle, MenuGlyph.CHAT, floatValue, boundsData.getFloatValue2() + 4.0F, width, false, previewAlpha);
      }

      Font font = Fonts.NEW_MEDIUM.getFont(4.6F);
      Font font2 = Fonts.NEW_REGULAR.getFont(4.3F);
      float floatValue2 = floatValue + width + 5.0F;
      String ago = ScriptsSectionPanel.ago(sub.getLongValue2());
      float f4 = boundsData.getFloatValue() + boundsData.width() - 5.0F;
      float centeredTextY = boundsData.getFloatValue2() + (28.0F - font.height() - font2.height() - 2.5F) / 2.0F;
      menuDrawContext.drawText(
         font,
         font.clip(sub.title().isEmpty() ? t("gui.scripts.ai.newChat") : sub.title(), f4 - font2.width(ago) - 4.0F - floatValue2),
         floatValue2,
         centeredTextY,
         (flag ? currentStyle.getTextEnable().getZenithColor() : currentStyle.getTextSecondary().getZenithColor().method7295(currentStyle.getTextEnable().getZenithColor(), hot))
            .method7294(previewAlpha)
      );
      menuDrawContext.drawText(
         font2, ago, f4 - font2.width(ago), centeredTextY + (font.height() - font2.height()), currentStyle.getTextTertiary().getZenithColor().method7294(previewAlpha)
      );
      String name = flag2 ? t("gui.scripts.ai.history.confirm") : sub.method2772();
      menuDrawContext.drawText(
         font2,
         font2.clip(name, f4 - floatValue2),
         floatValue2,
         centeredTextY + font.height() + 2.5F,
         (flag2 ? ERROR : currentStyle.getTextTertiary().getZenithColor()).method7294(previewAlpha)
      );
      if (AiChatStorage.method2741().isBusy() && !flag && sub.getItems().stream().anyMatch(AiChatStorage.Entry2::streaming)) {
         float f5 = 0.5F + 0.5F * (float)Math.sin(System.currentTimeMillis() / 200.0);
         menuDrawContext.drawRoundedRect(
            floatValue + width - 3.0F,
            boundsData.getFloatValue2() + 3.0F,
            4.0F,
            4.0F,
            ProxyDotRadiusData.method8069(2.0F),
            currentStyle.getPrimaryColor().getZenithColor().method7294(previewAlpha * f5)
         );
      }

      if (contains) {
         HoverInfo.request(sub.title(), t("gui.scripts.ai.history.hint"));
      }
   }

   public float headerHeight() {
      return 18.0F;
   }

   public void renderHeader(MenuDrawContext menuDrawContext, int lastClickX, int lastClickY, float f, float f2, float f3, float previewAlpha, ZenithStyle zenithStyle) {
      AiChatStorage aiChatStorage = AiChatStorage.method2741();
      Storage hostOrNull = this.panel.hostOrNull();
      Font font = Fonts.NEW_SEMIBOLD.getFont(5.2F);
      Font font2 = Fonts.NEW_MEDIUM.getFont(4.3F);
      ZenithColor zenithColor = zenithStyle.getPrimaryColor().getZenithColor();
      String name = aiChatStorage.getEntry().title().isEmpty() ? t("gui.scripts.ai.newChat") : aiChatStorage.getEntry().title();
      AiSettingsStore aiSettingsStore = AiSettingsStore.method4467();
      String text = aiSettingsStore.getShownProvider().title() + " · " + aiSettingsStore.method4472(aiSettingsStore.getShownProvider());
      Script script = aiChatStorage.method2747() != null && hostOrNull != null ? hostOrNull.method1294(aiChatStorage.method2747()) : null;
      String str = script == null ? t("gui.scripts.ai.target.new") : script.method1403();
      float f4 = f + f3 - 8.0F;
      float width = Math.min(font2.width(text) + 12.0F, f3 * 0.34F);
      BoundsData boundsData = new BoundsData(f4 - width, f2 + 3.0F, width, 12.0F);
      float width2 = Math.min(font2.width(str) + 12.0F, f3 * 0.28F);
      BoundsData boundsData2 = new BoundsData(boundsData.getFloatValue() - 4.0F - width2, f2 + 3.0F, width2, 12.0F);
      this.hits.put("hdr:model", boundsData);
      this.hits.put("hdr:script", boundsData2);

      for (Object[] objects : new Object[][]{{boundsData2, str, "hdr:script"}, {boundsData, text, "hdr:model"}}) {
         BoundsData boundsData3 = (BoundsData)objects[0];
         float hot = this.panel.hot((String)objects[2], boundsData3.contains(lastClickX, lastClickY));
         menuDrawContext.drawRoundedRect(
            boundsData3.getFloatValue(),
            boundsData3.getFloatValue2(),
            boundsData3.width(),
            12.0F,
            ProxyDotRadiusData.method8069(6.0F),
            zenithStyle.getDisableActiveBg().getZenithColor().method7294((0.35F + 0.5F * hot) * previewAlpha)
         );
         menuDrawContext.drawText(
            font2,
            font2.clip((String)objects[1], boundsData3.width() - 10.0F),
            boundsData3.getFloatValue() + 6.0F,
            boundsData3.getFloatValue2() + (12.0F - font2.height()) / 2.0F,
            zenithStyle.getTextSecondary().getZenithColor().method7295(zenithColor, hot * 0.6F).method7294(previewAlpha)
         );
      }

      menuDrawContext.drawText(
         font,
         font.clip(name, boundsData2.getFloatValue() - f - 8.0F - 6.0F),
         f + 8.0F,
         f2 + (18.0F - font.height()) / 2.0F,
         zenithStyle.getTextEnable().getZenithColor().method7294(previewAlpha)
      );
      menuDrawContext.drawRect(f + 8.0F, f2 + 17.5F, f3 - 16.0F, 0.5F, zenithStyle.getFieldBorder().getZenithColor().method7294(previewAlpha * 2.0F));
      if (boundsData2.contains(lastClickX, lastClickY)) {
         HoverInfo.request(t("gui.scripts.ai.caption.target"), t("gui.scripts.ai.hdr.script"));
      }

      if (boundsData.contains(lastClickX, lastClickY)) {
         HoverInfo.request(t("gui.scripts.ai.caption.model"), t("gui.scripts.ai.hdr.model"));
      }
   }

   public boolean clickHeader(double lastClickX, double lastClickY) {
      this.mouse(lastClickX, lastClickY);
      if (this.hovered(this.hits.get("hdr:script"))) {
         this.asideView = 0;
         this.panel.resetAsideScroll();
         this.panel.invalidate();
         return true;
      } else if (this.hovered(this.hits.get("hdr:model"))) {
         this.asideView = 1;
         this.panel.resetAsideScroll();
         this.panel.invalidate();
         return true;
      } else {
         return false;
      }
   }

   private void addForm() {
      this.panel.asideCaption("gui.scripts.ai.custom.caption");
      this.panel.paragraph(t("gui.scripts.ai.custom.desc"), true);
      this.field(this.addName, t("gui.scripts.ai.custom.name"), null, null);
      this.field(this.addUrl, t("gui.scripts.ai.custom.url"), null, null);
      this.field(this.addKey, t("gui.scripts.ai.custom.key"), "addpaste", t("gui.scripts.ai.key.paste"));
      this.field(this.addModel, t("gui.scripts.ai.custom.model"), null, null);
      this.panel
         .asideCustom(
            this.addError.isEmpty() ? 22.0F : 34.0F,
            (menuDrawContext, lastClickX, lastClickY, f, f2, f3, previewAlpha, zenithStyle) -> {
               Font font = Fonts.NEW_MEDIUM.getFont(4.6F);
               ZenithColor zenithColor = zenithStyle.getPrimaryColor().getZenithColor();
               float y = f2 + 3.0F;
               if (!this.addError.isEmpty()) {
                  Font font2 = Fonts.NEW_REGULAR.getFont(4.5F);
                  menuDrawContext.drawText(font2, font2.clip(this.addError, f3 - 16.0F), f + 8.0F, f2 + 2.0F, ERROR.method7294(previewAlpha));
                  y += 12.0F;
               }

               float width = (f3 - 16.0F - 4.0F) / 2.0F;
               BoundsData boundsData = new BoundsData(f + 8.0F, y, width, 16.0F);
               BoundsData boundsData2 = new BoundsData(f + 8.0F + width + 4.0F, y, width, 16.0F);
               this.hits.put("add:ok", boundsData);
               this.hits.put("add:cancel", boundsData2);
               float hot = this.panel.hot("add:ok", boundsData.contains(lastClickX, lastClickY));
               float hover = this.panel.hot("add:cancel", boundsData2.contains(lastClickX, lastClickY));
               float press = Smooth.press(this.pressedAt.getOrDefault("add:ok", 0L), 320L);
               menuDrawContext.drawRoundedRect(
                  boundsData.getFloatValue(),
                  boundsData.getFloatValue2(),
                  boundsData.width(),
                  16.0F,
                  ProxyDotRadiusData.method8069(4.0F),
                  zenithColor.method7292(0.22F + 0.2F * hot + 0.3F * press).method7294(previewAlpha)
               );
               menuDrawContext.drawRoundedRect(
                  boundsData2.getFloatValue(),
                  boundsData2.getFloatValue2(),
                  boundsData2.width(),
                  16.0F,
                  ProxyDotRadiusData.method8069(4.0F),
                  zenithStyle.getDisableActiveBg().getZenithColor().method7294(0.4F + 0.5F * hover).method7294(previewAlpha)
               );
               String text = t("gui.scripts.ai.custom.add");
               String str = t("gui.scripts.ai.custom.cancel");
               menuDrawContext.drawText(
                  font,
                  text,
                  boundsData.getFloatValue() + (width - font.width(text)) / 2.0F,
                  boundsData.getFloatValue2() + (16.0F - font.height()) / 2.0F,
                  zenithColor.method7295(ZenithColor.INSTANCE, 0.3F + hot * 0.4F).method7294(previewAlpha)
               );
               menuDrawContext.drawText(
                  font,
                  str,
                  boundsData2.getFloatValue() + (width - font.width(str)) / 2.0F,
                  boundsData2.getFloatValue2() + (16.0F - font.height()) / 2.0F,
                  zenithStyle.getTextSecondary().getZenithColor().method7295(zenithStyle.getTextEnable().getZenithColor(), hover).method7294(previewAlpha)
               );
            },
            () -> {
               if (this.hovered(this.hits.get("add:ok"))) {
                  this.pressedAt.put("add:ok", System.currentTimeMillis());
                  this.submitCustom();
               } else if (this.hovered(this.hits.get("add:cancel"))) {
                  this.adding = false;
                  this.addError = "";
                  this.panel.invalidate();
               }
            },
            null
         );
   }

   private void submitCustom() {
      String str = this.addName.get();
      String string = this.addUrl.get();
      if (str.isEmpty()) {
         this.addError = t("gui.scripts.ai.custom.err.name");
      } else if (!string.startsWith("http://") && !string.startsWith("https://")) {
         this.addError = t("gui.scripts.ai.custom.err.url");
      } else if (this.addModel.get().isEmpty()) {
         this.addError = t("gui.scripts.ai.custom.err.model");
      } else {
         this.commitFields();
         AiSettingsStore.method4467().method4476(str, string, this.addKey.get(), this.addModel.get());

         for (FormField formField : List.of(this.addName, this.addUrl, this.addKey, this.addModel)) {
            formField.clear();
            formField.setFocused(false);
         }

         this.adding = false;
         this.addError = "";
         this.syncFields();
         this.panel.replayAside();
      }

      this.panel.invalidate();
   }

   private static ScriptAiTab.KeyState keyState(ShownProvider shownProvider) {
      if (!AiSettingsStore.method4467().method4469(shownProvider).isEmpty()) {
         return ScriptAiTab.KeyState.SAVED;
      } else {
         return shownProvider.isFlag() ? ScriptAiTab.KeyState.MISSING : ScriptAiTab.KeyState.NOT_NEEDED;
      }
   }

   private static void statusDot(MenuDrawContext menuDrawContext, ZenithStyle zenithStyle, ScriptAiTab.KeyState keyState, float floatValue, float y, float previewAlpha) {
      ProxyDotRadiusData headerRadius = ProxyDotRadiusData.method8069(2.0F);
      switch (keyState) {
         case MISSING:
            menuDrawContext.drawRoundedBorder(floatValue, y, 4.0F, 4.0F, 0.6F, headerRadius, zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha));
            break;
         case SAVED:
            menuDrawContext.drawRoundedRect(floatValue, y, 4.0F, 4.0F, headerRadius, OK.method7294(previewAlpha));
            break;
         case NOT_NEEDED:
            menuDrawContext.drawRoundedRect(floatValue, y, 4.0F, 4.0F, headerRadius, LOCAL.method7294(previewAlpha));
      }
   }

   private void providerGrid() {
      ShownProvider[] shownProviders = ShownProvider.method7336();
      byte b = 3;
      int n = (shownProviders.length + 1 + b - 1) / b;
      float height = 20.0F;
      this.panel
         .asideCustom(
            n * (height + 3.0F) + 2.0F,
            (var4x, lastClickX, lastClickY, f, f2, f3, f4, currentStyle) -> {
               float f5 = f + 8.0F;
               float f6 = f3 - 16.0F;
               float width = (f6 - (b - 1) * 3.0F) / b;
               ZenithColor zenithColor = currentStyle.getPrimaryColor().getZenithColor();
               Font font = Fonts.NEW_MEDIUM.getFont(4.7F);

               for (int i = 0; i < shownProviders.length; i++) {
                  ShownProvider shownProvider = shownProviders[i];
                  float floatValue = f5 + i % b * (width + 3.0F);
                  float y = f2 + 1.0F + i / b * (height + 3.0F);
                  BoundsData boundsData = new BoundsData(floatValue, y, width, height);
                  this.hits.put("prov:" + shownProvider.getText(), boundsData);
                  boolean contains = boundsData.contains(lastClickX, lastClickY);
                  boolean flag = AiSettingsStore.method4467().getShownProvider().equals(shownProvider);
                  ScriptAiTab.KeyState keyState2 = keyState(shownProvider);
                  float hot = this.panel.hot("prov:" + shownProvider.getText(), contains);
                  float hover = this.panel.hot("provsel:" + shownProvider.getText(), flag);
                  float asideEnter = this.panel.asideEnter(i);
                  float f7 = (1.0F - asideEnter) * 5.0F;
                  ProxyDotRadiusData headerRadius = ProxyDotRadiusData.method8069(5.0F);
                  var4x.drawRoundedRect(
                     floatValue,
                     y + f7,
                     width,
                     height,
                     headerRadius,
                     ScriptsSectionPanel.cardFill(currentStyle, hot * 0.8F).method7295(zenithColor.method7292(0.18F), hover * 0.7F).method7294(f4 * asideEnter)
                  );
                  var4x.drawRoundedBorder(
                     floatValue,
                     y + f7,
                     width,
                     height,
                     0.2F,
                     headerRadius,
                     currentStyle.getFieldBorder()
                        .getZenithColor()
                        .method7294(2.0F)
                        .method7295(zenithColor, hover)
                        .method7294(f4 * asideEnter * (0.6F + 0.4F * Math.max(hover, hot)))
                  );
                  statusDot(var4x, currentStyle, keyState2, floatValue + width - 3.5F - 4.0F, y + f7 + 3.5F, f4 * asideEnter);
                  var4x.drawText(
                     font,
                     font.clip(shownProvider.title(), width - 10.0F),
                     floatValue + 5.0F,
                     y + f7 + (height - font.height()) / 2.0F,
                     currentStyle.getTextSecondary()
                        .getZenithColor()
                        .method7295(currentStyle.getTextEnable().getZenithColor(), Math.max(hover, hot))
                        .method7294(f4 * asideEnter)
                  );
                  if (contains) {
                     HoverInfo.request(shownProvider.title() + " · " + shownProvider.getText3(), t(keyState2.description));
                  }
               }

               int length = shownProviders.length;
               float floatValue = f5 + length % b * (width + 3.0F);
               float y = f2 + 1.0F + length / b * (height + 3.0F);
               BoundsData boundsData = new BoundsData(floatValue, y, width, height);
               this.hits.put("prov:+", boundsData);
               boolean contains = boundsData.contains(lastClickX, lastClickY);
               float hot = this.panel.hot("prov:+", contains || this.adding);
               float asideEnter = this.panel.asideEnter(length);
               float f7 = (1.0F - asideEnter) * 5.0F;
               ProxyDotRadiusData headerRadius = ProxyDotRadiusData.method8069(5.0F);
               var4x.drawRoundedRect(floatValue, y + f7, width, height, headerRadius, zenithColor.method7292(0.06F + 0.12F * hot).method7294(f4 * asideEnter));
               var4x.drawRoundedBorder(floatValue, y + f7, width, height, 0.2F, headerRadius, zenithColor.method7292(0.35F + 0.4F * hot).method7294(f4 * asideEnter));
               String name = "+ " + t("gui.scripts.ai.custom.tile");
               var4x.drawText(
                  font,
                  font.clip(name, width - 8.0F),
                  floatValue + (width - font.width(font.clip(name, width - 8.0F))) / 2.0F,
                  y + f7 + (height - font.height()) / 2.0F,
                  zenithColor.method7295(ZenithColor.INSTANCE, 0.25F + hot * 0.4F).method7294(f4 * asideEnter)
               );
               if (contains) {
                  HoverInfo.request(t("gui.scripts.ai.custom.caption"), t("gui.scripts.ai.custom.desc"));
               }
            },
            () -> {
               if (this.hovered(this.hits.get("prov:+"))) {
                  this.commitFields();
                  this.adding = !this.adding;
                  this.addError = "";
                  if (this.adding) {
                     this.focus(this.addName);
                  }

                  this.panel.invalidate();
               } else {
                  for (ShownProvider shownProvider : shownProviders) {
                     if (this.hovered(this.hits.get("prov:" + shownProvider.getText()))) {
                        this.commitFields();
                        this.adding = false;
                        AiSettingsStore.method4467().method4468(shownProvider);
                        this.syncFields();
                        this.panel.invalidate();
                        return;
                     }
                  }
               }
            },
            null
         );
   }

   private void field(FormField keyField, String name, String str, String text) {
      this.panel
         .asideCustom(
            21.0F,
            (menuDrawContext, lastClickX, lastClickY, f, f2, f3, previewAlpha, zenithStyle) -> {
               Font font = Fonts.NEW_MEDIUM.getFont(4.5F);
               float width = text == null ? 0.0F : font.width(text) + 12.0F;
               float shownWidth = f3 - 16.0F - (width > 0.0F ? width + 4.0F : 0.0F);
               keyField.render(
                  menuDrawContext,
                  font,
                  f + 8.0F,
                  f2 + 2.5F,
                  shownWidth,
                  previewAlpha,
                  name,
                  zenithStyle.getFieldSurfaceBackground().getZenithColor(),
                  zenithStyle.getFieldBorder().getZenithColor().method7294(2.0F),
                  zenithStyle.getPrimaryColor().getZenithColor(),
                  zenithStyle.getTextEnable().getZenithColor(),
                  zenithStyle.getTextTertiary().getZenithColor()
               );
               if (text != null) {
                  BoundsData boundsData = new BoundsData(f + 8.0F + shownWidth + 4.0F, f2 + 2.5F, width, 16.0F);
                  this.hits.put("aside:" + str, boundsData);
                  boolean contains = boundsData.contains(lastClickX, lastClickY);
                  float hot = this.panel.hot("aside:" + str, contains);
                  ZenithColor zenithColor = zenithStyle.getPrimaryColor().getZenithColor();
                  menuDrawContext.drawRoundedRect(
                     boundsData.getFloatValue(),
                     boundsData.getFloatValue2(),
                     width,
                     16.0F,
                     ProxyDotRadiusData.method8069(4.0F),
                     zenithColor.method7292(0.16F + 0.2F * hot).method7294(previewAlpha)
                  );
                  menuDrawContext.drawText(
                     font,
                     text,
                     boundsData.getFloatValue() + 6.0F,
                     boundsData.getFloatValue2() + (16.0F - font.height()) / 2.0F,
                     zenithColor.method7295(ZenithColor.INSTANCE, hot * 0.5F).method7294(previewAlpha)
                  );
               }
            },
            () -> {
               if (str != null && this.hovered(this.hits.get("aside:" + str))) {
                  String var3x = Minecraft.getInstance().keyboardHandler.getClipboard();
                  if (str.equals("paste") && var3x != null && !var3x.isBlank()) {
                     this.keyField.set(var3x.strip());
                     AiSettingsStore.method4467().method4470(AiSettingsStore.method4467().getShownProvider(), var3x.strip());
                     this.panel.invalidate();
                  } else if (str.equals("addpaste") && var3x != null && !var3x.isBlank()) {
                     this.addKey.set(var3x.strip());
                  }
               } else {
                  this.focus(keyField);
               }
            },
            null
         );
   }

   private void modelChips(ShownProvider shownProvider) {
      Font font = Fonts.NEW_MEDIUM.getFont(4.4F);
      float f = 128.0F;
      ArrayList arrayList = new ArrayList();
      float f2 = 0.0F;
      float f3 = 0.0F;

      for (String text : shownProvider.getItems3()) {
         float f4 = font.width(text) + 10.0F;
         if (f2 > 0.0F && f2 + f4 > f) {
            f2 = 0.0F;
            f3 += 13.0F;
         }

         arrayList.add(new float[]{f2, f3, f4});
         f2 += f4 + 3.0F;
      }

      float customHeight = f3 + 16.0F;
      this.panel
         .asideCustom(
            customHeight,
            (var4x, var5x, var6x, f4, var8x, var9x, var10x, zenithStyle) -> {
               for (int i = 0; i < shownProvider.getItems3().size(); i++) {
                  String text = shownProvider.getItems3().get(i);
                  float[] floats = (float[])arrayList.get(i);
                  BoundsData boundsData = new BoundsData(f4 + 8.0F + floats[0], var8x + 2.0F + floats[1], floats[2], 11.0F);
                  this.hits.put("model:" + text, boundsData);
                  boolean contains = boundsData.contains(var5x, var6x);
                  boolean flag = text.equals(AiSettingsStore.method4467().method4472(shownProvider));
                  float hot = this.panel.hot("model:" + text, contains || flag);
                  var4x.drawRoundedRect(
                     boundsData.getFloatValue(),
                     boundsData.getFloatValue2(),
                     boundsData.width(),
                     11.0F,
                     ProxyDotRadiusData.method8069(5.5F),
                     (flag
                           ? zenithStyle.getPrimaryColor().getZenithColor().method7292(0.22F)
                           : zenithStyle.getDisableActiveBg().getZenithColor().method7294(0.3F + hot * 0.5F))
                        .method7294(var10x)
                  );
                  var4x.drawText(
                     font,
                     text,
                     boundsData.getFloatValue() + 5.0F,
                     boundsData.getFloatValue2() + (11.0F - font.height()) / 2.0F,
                     (flag
                           ? zenithStyle.getPrimaryColor().getZenithColor().method7295(ZenithColor.INSTANCE, 0.3F)
                           : zenithStyle.getTextTertiary().getZenithColor().method7295(zenithStyle.getTextEnable().getZenithColor(), hot))
                        .method7294(var10x)
                  );
               }
            },
            () -> {
               for (String var3x : shownProvider.getItems3()) {
                  if (this.hovered(this.hits.get("model:" + var3x))) {
                     AiSettingsStore.method4467().method4473(shownProvider, var3x);
                     this.modelField.set(var3x);
                     this.panel.invalidate();
                     return;
                  }
               }
            },
            null
         );
   }

   private void target(Storage hostOrNull, Script script, int length) {
      String id = script == null ? null : script.getId();
      this.panel
         .asideCustom(
            25.0F,
            this.panel
               .asideEntering(
                  length,
                  (var4x, lastClickX, lastClickY, f, f2, f3, previewAlpha, currentStyle) -> {
                     BoundsData boundsData = new BoundsData(f + 8.0F, f2 + 1.0F, f3 - 16.0F, 22.0F);
                     String argument = AiChatStorage.method2741().method2747();
                     boolean contains = id == null ? argument == null || hostOrNull.method1294(argument) == null : id.equals(argument);
                     float hot = this.panel.hot("tgt:" + id, boundsData.contains(lastClickX, lastClickY));
                     float hot2 = this.panel.hot("tgtsel:" + id, contains);
                     this.cardFrame(var4x, currentStyle, boundsData, hot, hot2, 0.0F, previewAlpha);
                     float width = 14.0F;
                     float floatValue = boundsData.getFloatValue() + 4.0F;
                     Font font = Fonts.NEW_MEDIUM.getFont(4.6F);
                     Font font2 = Fonts.NEW_REGULAR.getFont(4.3F);
                     ZenithColor zenithColor = currentStyle.getTextTertiary().getZenithColor();
                     String name;
                     String str;
                     if (script == null) {
                        this.iconTile(var4x, currentStyle, MenuGlyph.ACTION, floatValue, boundsData.getFloatValue2() + 4.0F, width, true, previewAlpha);
                        name = t("gui.scripts.ai.target.new");
                        str = t("gui.scripts.ai.target.new.sub");
                     } else {
                        ScriptInfoPage.drawAvatar(var4x, currentStyle, script, floatValue, boundsData.getFloatValue2() + 4.0F, width, previewAlpha, -2);
                        name = script.method1403();
                        if (!script.getError().isBlank()) {
                           str = t("gui.scripts.state.failed");
                           zenithColor = ERROR;
                        } else if (script.isRunning()) {
                           str = t("gui.scripts.state.running");
                           zenithColor = OK.method7295(currentStyle.getTextTertiary().getZenithColor(), 0.3F);
                        } else {
                           str = t("gui.scripts.state.off");
                        }
                     }

                     float floatValue2 = floatValue + width + 5.0F;
                     float width2 = boundsData.getFloatValue() + boundsData.width() - 5.0F - floatValue2;
                     float centeredTextY = boundsData.getFloatValue2() + (22.0F - font.height() - font2.height() - 2.0F) / 2.0F;
                     var4x.drawText(
                        font,
                        font.clip(name, width2),
                        floatValue2,
                        centeredTextY,
                        (contains
                              ? currentStyle.getTextEnable().getZenithColor()
                              : currentStyle.getTextSecondary().getZenithColor().method7295(currentStyle.getTextEnable().getZenithColor(), hot))
                           .method7294(previewAlpha)
                     );
                     var4x.drawText(font2, font2.clip(str, width2), floatValue2, centeredTextY + font.height() + 2.0F, zenithColor.method7294(previewAlpha));
                  }
               ),
            () -> {
               AiChatStorage.method2741().method2748(id);
               this.panel.invalidate();
            },
            null
         );
   }

   private void toggle(String str, String description, String description2, BooleanSupplier booleanSupplier, Consumer<Boolean> consumer) {
      this.panel
         .asideCustom(
            16.0F,
            (var5x, lastClickX, lastClickY, floatValue, y, width, previewAlpha, currentStyle) -> {
               Font font = Fonts.NEW_MEDIUM.getFont(4.6F);
               boolean contains = new BoundsData(floatValue, y, width, 16.0F).contains(lastClickX, lastClickY);
               var5x.drawText(
                  font,
                  t(description),
                  floatValue + 8.0F,
                  y + (16.0F - font.height()) / 2.0F,
                  (contains ? currentStyle.getTextEnable().getZenithColor() : currentStyle.getTextSecondary().getZenithColor()).method7294(previewAlpha)
               );
               this.panel.drawSwitch(var5x, "ai:" + str, floatValue + width - 8.0F - 14.0F, y + 4.0F, 14.0F, 8.0F, booleanSupplier.getAsBoolean(), previewAlpha, currentStyle);
               if (contains) {
                  HoverInfo.request(t(description), t(description2));
               }
            },
            () -> {
               consumer.accept(!booleanSupplier.getAsBoolean());
               this.panel.invalidate();
            },
            null
         );
   }

   private void focus(FormField input) {
      for (FormField field : this.fields()) {
         if (field != input && field.isFocused()) {
            field.setFocused(false);
         }
      }

      this.commitFields();
      if (input != null) {
         input.setFocused(true);
      }
   }

   public FormField focused() {
      for (FormField field : this.fields()) {
         if (field.isFocused()) {
            return field;
         }
      }

      return null;
   }

   private static void thumb(MenuDrawContext menuDrawContext, Identifier texture, BoundsData boundsData, float intValue, ZenithColor zenithColor, float previewAlpha, float f) {
      ProxyDotRadiusData headerRadius = ProxyDotRadiusData.method8069(intValue);
      if (texture != null) {
         float f2 = (1.0F - Math.min(1.0F, f)) / 2.0F;
         menuDrawContext.drawRoundedTexture(
            texture,
            boundsData.getFloatValue(),
            boundsData.getFloatValue2(),
            boundsData.width(),
            boundsData.height(),
            headerRadius,
            ZenithColor.INSTANCE.method7294(previewAlpha),
            f2,
            0.0F,
            1.0F - f2,
            1.0F
         );
      } else {
         menuDrawContext.drawRoundedRect(
            boundsData.getFloatValue(), boundsData.getFloatValue2(), boundsData.width(), boundsData.height(), headerRadius, ZenithColor.INSTANCE.method7292(0.05F).method7294(previewAlpha)
         );
      }

      menuDrawContext.drawRoundedBorder(boundsData.getFloatValue(), boundsData.getFloatValue2(), boundsData.width(), boundsData.height(), 0.3F, headerRadius, zenithColor);
   }

   private static void removeBadge(MenuDrawContext menuDrawContext, BoundsData boundsData, float hot, float previewAlpha) {
      float width = boundsData.width();
      ProxyDotRadiusData headerRadius = ProxyDotRadiusData.method8069(width / 2.0F);
      menuDrawContext.drawRoundedRect(
         boundsData.getFloatValue(), boundsData.getFloatValue2(), width, width, headerRadius, new ZenithColor(12, 12, 14, 190).method7295(ERROR, 0.85F * hot).method7294(previewAlpha)
      );
      menuDrawContext.drawRoundedBorder(
         boundsData.getFloatValue(), boundsData.getFloatValue2(), width, width, 0.2F, headerRadius, ZenithColor.INSTANCE.method7292(0.16F + 0.24F * hot).method7294(previewAlpha)
      );
      MediaGlyphs.close(
         menuDrawContext,
         boundsData.getFloatValue() + width / 2.0F,
         boundsData.getFloatValue2() + width / 2.0F,
         width * 0.5F,
         ZenithColor.INSTANCE.method7292(0.8F + 0.2F * hot).method7294(previewAlpha)
      );
   }

   private boolean slashMode() {
      String str = this.input.get();
      return str.startsWith("/") && !str.contains(" ") && this.input.isFocused();
   }

   public float footerHeight() {
      float height = this.input.height(Fonts.NEW_REGULAR.getFont(4.7F), 158.0F);
      return height + 12.0F + (this.pending.isEmpty() && this.importing == 0 ? 0.0F : 30.0F) + (this.slashMode() ? 13.0F : 0.0F);
   }

   public void renderFooter(MenuDrawContext menuDrawContext, int lastClickX, int lastClickY, float f, float y, float f2, float previewAlpha, ZenithStyle zenithStyle) {
      menuDrawContext.drawRect(f + 8.0F, y, f2 - 16.0F, 1.0F, zenithStyle.getFieldBorder().getZenithColor().method7294(previewAlpha * 2.0F));
      Font font = Fonts.NEW_REGULAR.getFont(4.7F);
      Font font2 = Fonts.NEW_MEDIUM.getFont(4.3F);
      AiChatStorage aiChatStorage = AiChatStorage.method2741();
      ZenithColor zenithColor = zenithStyle.getPrimaryColor().getZenithColor();
      float y2 = y + 6.0F;
      if (this.slashMode()) {
         String lowerCase = this.input.get().toLowerCase(Locale.ROOT);
         float floatValue = f + 8.0F;

         for (String[] strings : COMMANDS) {
            if (strings[0].startsWith(lowerCase)) {
               String text = strings[0];
               float width = font2.width(text) + 8.0F;
               if (floatValue + width > f + f2 - 8.0F) {
                  break;
               }

               BoundsData boundsData = new BoundsData(floatValue, y2, width, 10.0F);
               this.hits.put("cmd:" + strings[0], boundsData);
               boolean contains = boundsData.contains(lastClickX, lastClickY);
               float hot = this.panel.hot("cmd:" + strings[0], contains);
               menuDrawContext.drawRoundedRect(
                  boundsData.getFloatValue(),
                  boundsData.getFloatValue2(),
                  width,
                  10.0F,
                  ProxyDotRadiusData.method8069(5.0F),
                  zenithColor.method7292(0.14F + 0.2F * hot).method7294(previewAlpha)
               );
               menuDrawContext.drawText(
                  font2,
                  text,
                  floatValue + 4.0F,
                  y2 + (10.0F - font2.height()) / 2.0F,
                  zenithColor.method7295(ZenithColor.INSTANCE, 0.3F + hot * 0.4F).method7294(previewAlpha)
               );
               if (contains) {
                  HoverInfo.request(text, t(strings[1]));
               }

               floatValue += width + 3.0F;
            }
         }

         y2 += 13.0F;
      }

      if (!this.pending.isEmpty() || this.importing > 0) {
         float floatValue = f + 8.0F;
         float height = 24.0F;
         String text = this.pending.size() + "/6";
         float[] floats = new float[this.pending.size()];
         float f3 = 0.0F;

         for (int i = 0; i < floats.length; i++) {
            int[] ints = AiImageStore.method4638(this.pending.get(i));
            floats[i] = Math.max(height * 0.6F, Math.min(height * 2.0F, height * ints[0] / Math.max(1, ints[1])));
            f3 += floats[i];
         }

         float f4 = f2 - 16.0F - font2.width(text) - 6.0F - (this.importing > 0 ? height + 4.0F : 0.0F) - Math.max(0, floats.length - 1) * 4.0F;
         float f5 = f3 > f4 && f3 > 0.0F ? Math.max(0.1F, f4 / f3) : 1.0F;

         for (int i = 0; i < floats.length; i++) {
            String key = this.pending.get(i);
            Identifier texture = AiImageStore.method4639(key);
            float width = floats[i] * f5;
            BoundsData boundsData = new BoundsData(floatValue, y2, width, height);
            boolean contains = boundsData.contains(lastClickX, lastClickY);
            float hot = this.panel.hot("att:" + key, contains);
            thumb(menuDrawContext, texture, boundsData, 4.0F, zenithColor.method7292(0.3F + 0.4F * hot).method7294(previewAlpha), previewAlpha, f5);
            BoundsData boundsData2 = new BoundsData(floatValue + width - 8.0F - 2.0F, y2 + 2.0F, 8.0F, 8.0F);
            this.hits.put("attx:" + i, boundsData2);
            if (hot > 0.01F) {
               removeBadge(menuDrawContext, boundsData2, this.panel.hot("attx:" + key, boundsData2.contains(lastClickX, lastClickY)), previewAlpha * hot);
            }

            floatValue += width + 4.0F;
         }

         if (this.importing > 0) {
            float f6 = 0.5F + 0.5F * (float)Math.sin(System.currentTimeMillis() / 160.0);
            menuDrawContext.drawRoundedRect(floatValue, y2, 24.0F, 24.0F, ProxyDotRadiusData.method8069(4.0F), zenithColor.method7292(0.1F + 0.15F * f6).method7294(previewAlpha));
         }

         menuDrawContext.drawText(
            font2,
            text,
            f + f2 - 8.0F - font2.width(text),
            y2 + (24.0F - font2.height()) / 2.0F,
            zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha)
         );
         y2 += 30.0F;
      }

      String replacement = aiChatStorage.method2747() != null && this.panel.hostOrNull() != null && this.panel.hostOrNull().method1294(aiChatStorage.method2747()) != null
         ? this.panel.hostOrNull().method1294(aiChatStorage.method2747()).method1403()
         : t("gui.scripts.ai.target.new");
      String name = t("gui.scripts.ai.input.hint").replace("{s}", replacement);
      float width = 18.0F;
      float floatValue = f + 8.0F + width + 4.0F;
      float shownWidth = f2 - 16.0F - 40.0F - width - 4.0F;
      float render = this.input
         .render(
            menuDrawContext,
            font,
            floatValue,
            y2,
            shownWidth,
            previewAlpha,
            name,
            zenithStyle.getFieldSurfaceBackground().getZenithColor(),
            zenithStyle.getFieldBorder().getZenithColor().method7294(2.0F),
            zenithStyle.getPrimaryColor().getZenithColor(),
            zenithStyle.getTextEnable().getZenithColor(),
            zenithStyle.getTextTertiary().getZenithColor()
         );
      float height = (render - 2.0F) / 2.0F;
      BoundsData boundsData = new BoundsData(f + 8.0F, y2, width, height);
      BoundsData boundsData2 = new BoundsData(f + 8.0F, y2 + height + 2.0F, width, height);
      this.hits.put("tool:attach", boundsData);
      this.hits.put("tool:shot", boundsData2);

      for (Object[] objects : new Object[][]{{boundsData, "attach"}, {boundsData2, "shot"}}) {
         BoundsData boundsData3 = (BoundsData)objects[0];
         String str = (String)objects[1];
         boolean contains = boundsData3.contains(lastClickX, lastClickY);
         float hot = this.panel.hot("tool:" + str, contains);
         float press = Smooth.press(this.pressedAt.getOrDefault("tool:" + str, 0L), 320L);
         menuDrawContext.drawRoundedRect(
            boundsData3.getFloatValue(),
            boundsData3.getFloatValue2(),
            boundsData3.width(),
            boundsData3.height(),
            ProxyDotRadiusData.method8069(4.0F),
            zenithStyle.getFieldSurfaceBackground().getZenithColor().method7295(zenithColor.method7292(0.3F), 0.2F + hot * 0.5F + press * 0.3F).method7294(previewAlpha)
         );
         ZenithColor zenithColor2 = zenithStyle.getTextSecondary().getZenithColor().method7295(zenithColor, hot).method7294(previewAlpha);
         float f3 = boundsData3.getFloatValue() + boundsData3.width() / 2.0F;
         float f4 = boundsData3.getFloatValue2() + boundsData3.height() / 2.0F;
         if (str.equals("attach")) {
            menuDrawContext.drawRect(f3 - 3.0F, f4 - 0.4F, 6.0F, 0.8F, zenithColor2);
            menuDrawContext.drawRect(f3 - 0.4F, f4 - 3.0F, 0.8F, 6.0F, zenithColor2);
         } else {
            menuDrawContext.drawRoundedBorder(f3 - 4.0F, f4 - 2.6F, 8.0F, 5.6F, 0.4F, ProxyDotRadiusData.method8069(1.2F), zenithColor2);
            menuDrawContext.drawRoundedRect(f3 - 1.3F, f4 - 1.3F, 2.6F, 2.6F, ProxyDotRadiusData.method8069(1.3F), zenithColor2);
         }

         if (contains) {
            HoverInfo.request(t("gui.scripts.ai.tool." + str), t("gui.scripts.ai.tool." + str + ".desc"));
         }
      }

      this.sendRect = new BoundsData(floatValue + shownWidth + 4.0F, y2, 36.0F, render);
      boolean contains = this.sendRect.contains(lastClickX, lastClickY);
      float hot = this.panel.hot("ai:send", contains);
      boolean busy = aiChatStorage.isBusy();
      float press = Smooth.press(this.pressedAt.getOrDefault("send", 0L), 320L);
      menuDrawContext.drawRoundedRect(
         this.sendRect.getFloatValue(),
         this.sendRect.getFloatValue2(),
         this.sendRect.width(),
         this.sendRect.height(),
         ProxyDotRadiusData.method8069(5.0F),
         (busy ? ERROR.method7292(0.2F + hot * 0.15F) : zenithColor.method7292(0.22F + hot * 0.2F + press * 0.3F)).method7294(previewAlpha)
      );
      Font font3 = Fonts.NEW_MEDIUM.getFont(4.6F);
      String text = t(busy ? "gui.scripts.ai.stop" : "gui.scripts.ai.send");
      menuDrawContext.drawText(
         font3,
         text,
         this.sendRect.getFloatValue() + (this.sendRect.width() - font3.width(text)) / 2.0F,
         this.sendRect.getFloatValue2() + (this.sendRect.height() - font3.height()) / 2.0F,
         (busy ? ERROR : zenithColor.method7295(ZenithColor.INSTANCE, 0.3F + hot * 0.4F)).method7294(previewAlpha)
      );
      if (contains) {
         HoverInfo.request(text, t("gui.scripts.ai.send.desc"));
      }

      int length = this.input.get().length();
      if (length > 300) {
         String str = length + "/6000";
         menuDrawContext.drawText(
            font2,
            str,
            floatValue + shownWidth - 4.0F - font2.width(str),
            y2 + render - font2.height() - 2.0F,
            zenithStyle.getTextTertiary().getZenithColor().method7294(previewAlpha * 0.8F)
         );
      }
   }

   public boolean clickFooter(double lastMouseX, double lastMouseY) {
      this.mouse(lastMouseX, lastMouseY);
      if (this.sendRect != null && this.sendRect.contains(lastMouseX, lastMouseY)) {
         this.pressedAt.put("send", System.currentTimeMillis());
         if (AiChatStorage.method2741().isBusy()) {
            AiChatStorage.method2741().stop();
         } else {
            this.send();
         }

         return true;
      } else {
         for (String[] strings : COMMANDS) {
            if (this.hovered(this.hits.get("cmd:" + strings[0]))) {
               this.input.set(strings[0] + (strings[0].equals("/model") ? " " : ""));
               this.focus(this.input);
               if (!strings[0].equals("/model")) {
                  this.send();
               }

               return true;
            }
         }

         if (this.hovered(this.hits.get("tool:attach"))) {
            this.pressedAt.put("tool:attach", System.currentTimeMillis());
            this.pickImages();
            return true;
         } else if (this.hovered(this.hits.get("tool:shot"))) {
            this.pressedAt.put("tool:shot", System.currentTimeMillis());
            this.screenshot();
            return true;
         } else {
            for (int i = this.pending.size() - 1; i >= 0; i--) {
               if (this.hovered(this.hits.get("attx:" + i))) {
                  this.pending.remove(i);
                  this.panel.invalidate();
                  return true;
               }
            }

            this.focus(this.input);
            return true;
         }
      }
   }

   private void notify(String message) {
      HudHolder.method6969().method6973("i", Component.literal(message), 3000L);
   }

   public void attachFiles(List<Path> paths) {
      for (Path path : paths) {
         if (this.pending.size() + this.importing >= 6) {
            this.notify(t("gui.scripts.ai.attach.limit"));
            break;
         }

         if (!AiImageStore.method4631(path)) {
            this.notify(t("gui.scripts.ai.attach.type") + " " + path.getFileName());
         } else {
            this.importing++;
            Thread.ofPlatform().daemon(true).name("zenith-ai-attach").start(() -> {
               try {
                  String str = AiImageStore.method4632(path);
                  Minecraft.getInstance().execute(() -> {
                     this.pending.add(str);
                     this.importing--;
                     this.panel.invalidate();
                  });
               } catch (Exception var3x) {
                  Minecraft.getInstance().execute(() -> {
                     this.importing--;
                     this.notify(var3x.getMessage());
                  });
               }
            });
         }
      }

      this.focus(this.input);
      this.panel.invalidate();
   }

   private void pickImages() {
      Thread.ofPlatform().daemon(true).name("zenith-ai-pick").start(() -> {
         MemoryStack stackPush = MemoryStack.stackPush();

         String tinyfd_openFileDialog;
         try {
            PointerBuffer mallocPointer = stackPush.mallocPointer(5);

            for (String text : new String[]{"*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp"}) {
               mallocPointer.put(stackPush.UTF8(text));
            }

            mallocPointer.flip();
            tinyfd_openFileDialog = FileDialogs.tinyfd_openFileDialog(t("gui.scripts.ai.tool.attach"), "", mallocPointer, "Картинки", true);
         } catch (Throwable e) {
            if (stackPush != null) {
               try {
                  stackPush.close();
               } catch (Throwable ex) {
                  e.addSuppressed(ex);
               }
            }

            throw e;
         }

         if (stackPush != null) {
            stackPush.close();
         }

         if (tinyfd_openFileDialog != null && !tinyfd_openFileDialog.isBlank()) {
            ArrayList paths = new ArrayList();

            for (String first : tinyfd_openFileDialog.split("\\|")) {
               if (!first.isBlank()) {
                  paths.add(Path.of(first));
               }
            }

            Minecraft.getInstance().execute(() -> this.attachFiles(paths));
         }
      });
   }

   private void screenshot() {
      if (this.pending.size() + this.importing >= 6) {
         this.notify(t("gui.scripts.ai.attach.limit"));
      } else {
         Minecraft minecraft = Minecraft.getInstance();
         if (minecraft.level == null) {
            this.notify(t("gui.scripts.ai.attach.noworld"));
         } else {
            if (!shotHooked) {
               shotHooked = true;
               EventManager.main().subscribe(TickEvent.class, (byte)4, (obj, var1x) -> tickShot());
            }

            this.importing++;
            shotReturn = minecraft.gui.screen();
            shotDone = var1x -> {
               this.importing--;
               if (var1x != null) {
                  this.pending.add(var1x);
               }

               this.panel.invalidate();
            };
            shotCountdown = 3;
            minecraft.gui.setScreen(null);
         }
      }
   }

   private static void tickShot() {
      if (shotCountdown >= 0) {
         if (--shotCountdown <= 0) {
            shotCountdown = -1;
            Minecraft minecraft = Minecraft.getInstance();
            Screen screen = shotReturn;
            var consumer = shotDone;
            Screenshot.takeScreenshot(minecraft.gameRenderer.mainRenderTarget(), 1, nativeImage -> Thread.ofPlatform().daemon(true).start(() -> {
               String str = null;

               try {
                  str = AiImageStore.method4633(nativeImage);
               } catch (Exception e) {
               } finally {
                  nativeImage.close();
               }

               String str2 = str;
               minecraft.execute(() -> {
                  if (screen != null) {
                     minecraft.gui.setScreen(screen);
                  }

                  if (consumer != null) {
                     consumer.accept(str2);
                  }
               });
            }));
         }
      }
   }

   public void send() {
      Storage hostOrNull = this.panel.hostOrNull();
      String text = this.input.get();
      if (hostOrNull != null && (!text.isEmpty() || !this.pending.isEmpty()) && !AiChatStorage.method2741().isBusy() && this.importing <= 0) {
         if (text.startsWith("/") && this.command(hostOrNull, text)) {
            this.input.clear();
            this.panel.invalidate();
         } else {
            this.input.clear();
            var strings = new ArrayList<>(this.pending);
            this.pending.clear();
            AiChatStorage.method2741().method2758(hostOrNull, text, strings);
            this.panel.invalidate();
         }
      }
   }

   private boolean command(Storage hostOrNull, String text) {
      String[] strings = text.strip().split("\\s+", 2);
      String string = strings.length > 1 ? strings[1] : "";
      AiChatStorage aiChatStorage = AiChatStorage.method2741();
      String lowerCase = strings[0].toLowerCase(Locale.ROOT);
      switch (lowerCase) {
         case "/new":
            aiChatStorage.method2750(null);
            break;
         case "/clear":
            aiChatStorage.clear();
            break;
         case "/fix":
            if (aiChatStorage.method2747() == null) {
               aiChatStorage.method2759(t("gui.scripts.ai.cmd.noscript"));
            } else {
               aiChatStorage.method2769(hostOrNull);
            }
            break;
         case "/explain":
            aiChatStorage.method2757(hostOrNull, t("gui.scripts.ai.quick.explain.prompt"));
            break;
         case "/shot":
            this.screenshot();
            break;
         case "/export":
            try {
               Path path = aiChatStorage.method2760();
               com.mojang.blaze3d.Blaze3D.openPath((path.getParent().toFile()).toPath());
               aiChatStorage.method2759(t("gui.scripts.ai.cmd.exported").replace("{f}", path.getFileName().toString()));
            } catch (Exception e) {
               aiChatStorage.method2759(e.getMessage());
            }
            break;
         case "/model":
            if (string.isBlank()) {
               aiChatStorage.method2759(
                  t("gui.scripts.ai.cmd.model.now").replace("{m}", AiSettingsStore.method4467().method4472(AiSettingsStore.method4467().getShownProvider()))
               );
            } else {
               AiSettingsStore.method4467().method4473(AiSettingsStore.method4467().getShownProvider(), string);
               this.modelField.set(AiSettingsStore.method4467().method4472(AiSettingsStore.method4467().getShownProvider()));
               aiChatStorage.method2759(
                  t("gui.scripts.ai.cmd.model.set").replace("{m}", AiSettingsStore.method4467().method4472(AiSettingsStore.method4467().getShownProvider()))
               );
            }
            break;
         case "/help":
            StringBuilder builder = new StringBuilder(t("gui.scripts.ai.cmd.helpTitle")).append('\n');

            for (String[] strings2 : COMMANDS) {
               builder.append(strings2[0]).append(" — ").append(t(strings2[1])).append('\n');
            }

            builder.append(t("gui.scripts.ai.cmd.helpTail"));
            aiChatStorage.method2759(builder.toString());
            break;
         default:
            return false;
      }

      return true;
   }

   public boolean keyPressed(int keyCode, int modifiers) {
      FormField focused = this.focused();
      if (focused == null) {
         return false;
      } else if (keyCode == 257 || keyCode == 335) {
         var formFields = List.of(this.addName, this.addUrl, this.addKey, this.addModel);
         int indexOf = formFields.indexOf(focused);
         if (indexOf >= 0) {
            if (indexOf == formFields.size() - 1) {
               this.submitCustom();
            } else {
               this.focus((FormField)formFields.get(indexOf + 1));
            }

            return true;
         } else {
            if (focused == this.input) {
               this.send();
            } else {
               this.commitFields();
               focused.setFocused(false);
               this.panel.invalidate();
            }

            return true;
         }
      } else if (focused == this.input && keyCode == 265 && this.input.get().isEmpty()) {
         String description = AiChatStorage.method2741().method2762();
         if (!description.isEmpty()) {
            this.input.set(description);
         }

         return true;
      } else if (keyCode == 256 && focused == this.input && AiChatStorage.method2741().isBusy()) {
         AiChatStorage.method2741().stop();
         return true;
      } else if (keyCode == 256) {
         focused.setFocused(false);
         this.commitFields();
         return true;
      } else if (focused == this.input && keyCode == 86 && (modifiers & 2) != 0) {
         String clipboard = Minecraft.getInstance().keyboardHandler.getClipboard();
         if (clipboard != null) {
            for (char codepoint : clipboard.replace("\r", "").replace('\n', ' ').replace('\t', ' ').toCharArray()) {
               focused.charTyped(codepoint);
            }
         }

         return true;
      } else {
         focused.keyPressed(keyCode, modifiers);
         return true;
      }
   }

   public boolean charTyped(char codepoint) {
      FormField focused = this.focused();
      return focused != null && focused.charTyped(codepoint);
   }

   public void blurAll(double lastClickX, double lastClickY) {
      for (FormField field : this.fields()) {
         BoundsData bounds = field.bounds();
         if (field.isFocused() && (bounds == null || !bounds.contains(lastClickX, lastClickY))) {
            field.setFocused(false);
         }
      }

      this.commitFields();
   }

   private static final class CachedLayout {
      private final String text;
      private final boolean streaming;
      private final boolean note;
      private final String error;
      private final String result;
      private final int images;
      private final float width;
      private final boolean lastOne;
      private final ScriptAiTab.Layout layout;

      public String text() {
         return this.text;
      }

      public boolean streaming() {
         return this.streaming;
      }

      public boolean note() {
         return this.note;
      }

      public String error() {
         return this.error;
      }

      public String result() {
         return this.result;
      }

      public int images() {
         return this.images;
      }

      public float width() {
         return this.width;
      }

      public boolean lastOne() {
         return this.lastOne;
      }

      public ScriptAiTab.Layout layout() {
         return this.layout;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof CachedLayout other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.streaming, other.streaming) && java.util.Objects.equals(this.note, other.note) && java.util.Objects.equals(this.error, other.error) && java.util.Objects.equals(this.result, other.result) && java.util.Objects.equals(this.images, other.images) && java.util.Objects.equals(this.width, other.width) && java.util.Objects.equals(this.lastOne, other.lastOne) && java.util.Objects.equals(this.layout, other.layout);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, streaming, note, error, result, images, width, lastOne, layout);
      }

      @Override
      public String toString() {
         return "CachedLayout[" + "text=" + this.text + ", " + "streaming=" + this.streaming + ", " + "note=" + this.note + ", " + "error=" + this.error + ", " + "result=" + this.result + ", " + "images=" + this.images + ", " + "width=" + this.width + ", " + "lastOne=" + this.lastOne + ", " + "layout=" + this.layout + "]";
      }

      private CachedLayout(String text, boolean streaming, boolean note, String error, String result, int images, float width, boolean lastOne, ScriptAiTab.Layout layout) {
         this.text = text;
         this.streaming = streaming;
         this.note = note;
         this.error = error;
         this.result = result;
         this.images = images;
         this.width = width;
         this.lastOne = lastOne;
         this.layout = layout;
      }

      public boolean matches(AiChatStorage.Entry2 entry2, String text, String errorText, float width, boolean lastOne) {
         return (this.text == text || this.text.equals(text))
            && this.streaming == entry2.streaming()
            && this.note == entry2.note()
            && this.error.equals(errorText)
            && this.result.equals(entry2.result())
            && this.images == entry2.getItems().size()
            && this.width == width
            && this.lastOne == lastOne;
      }
   }

   private static enum KeyState {
      MISSING("gui.scripts.ai.provider.nokey"),
      SAVED("gui.scripts.ai.provider.ready"),
      NOT_NEEDED("gui.scripts.ai.provider.local");

      private final String description;

      private KeyState(String description) {
         this.description = description;
      }
   }

   private static final class Layout {
      private final List<ScriptAiTab.Piece> pieces;
      private final List<List<String>> wrapped;
      private final float height;
      private final float bubble;

      public List<ScriptAiTab.Piece> pieces() {
         return this.pieces;
      }

      public List<List<String>> wrapped() {
         return this.wrapped;
      }

      public float height() {
         return this.height;
      }

      public float bubble() {
         return this.bubble;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Layout other)) return false;
         return java.util.Objects.equals(this.pieces, other.pieces) && java.util.Objects.equals(this.wrapped, other.wrapped) && java.util.Objects.equals(this.height, other.height) && java.util.Objects.equals(this.bubble, other.bubble);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(pieces, wrapped, height, bubble);
      }

      @Override
      public String toString() {
         return "Layout[" + "pieces=" + this.pieces + ", " + "wrapped=" + this.wrapped + ", " + "height=" + this.height + ", " + "bubble=" + this.bubble + "]";
      }

      private Layout(List<ScriptAiTab.Piece> pieces, List<List<String>> wrapped, float height, float bubble) {
         this.pieces = pieces;
         this.wrapped = wrapped;
         this.height = height;
         this.bubble = bubble;
      }
   }

   private static final class Piece {
      private final int kind;
      private final String text;
      private final String extra;
      private final int lines;

      public int kind() {
         return this.kind;
      }

      public String text() {
         return this.text;
      }

      public String extra() {
         return this.extra;
      }

      public int lines() {
         return this.lines;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Piece other)) return false;
         return java.util.Objects.equals(this.kind, other.kind) && java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.extra, other.extra) && java.util.Objects.equals(this.lines, other.lines);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(kind, text, extra, lines);
      }

      @Override
      public String toString() {
         return "Piece[" + "kind=" + this.kind + ", " + "text=" + this.text + ", " + "extra=" + this.extra + ", " + "lines=" + this.lines + "]";
      }

      private Piece(int kind, String text, String extra, int lines) {
         this.kind = kind;
         this.text = text;
         this.extra = extra;
         this.lines = lines;
      }
   }
}
