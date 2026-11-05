package org.zenith.client.screens.scripts;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.base.font.MsdfRenderer;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public final class CodeArea {
   private static final Set<String> KEYWORDS = Set.of(
      "var",
      "let",
      "const",
      "function",
      "return",
      "if",
      "else",
      "for",
      "while",
      "do",
      "break",
      "continue",
      "new",
      "typeof",
      "instanceof",
      "true",
      "false",
      "null",
      "undefined",
      "this",
      "try",
      "catch",
      "finally",
      "throw",
      "switch",
      "case",
      "default",
      "in",
      "of",
      "delete",
      "void",
      "yield"
   );
   private static final ZenithColor KEYWORD = new ZenithColor(199, 146, 234);
   private static final ZenithColor STRING = new ZenithColor(195, 232, 141);
   private static final ZenithColor NUMBER = new ZenithColor(247, 140, 108);
   private static final ZenithColor COMMENT = new ZenithColor(103, 110, 149);
   private static final ZenithColor API = new ZenithColor(130, 170, 255);
   private static final ZenithColor PLAIN = new ZenithColor(220, 224, 235);
   private static final ZenithColor GUTTER = new ZenithColor(96, 102, 124);
   private static final ZenithColor ERROR = new ZenithColor(242, 79, 68);
   private static final Pattern WORD_BEFORE = Pattern.compile("(?:([A-Za-z_$][\\w$]*(?:\\.[A-Za-z_$][\\w$]*)*)\\.)?([A-Za-z_$][\\w$]*)?$");
   private static final Pattern IDENTIFIER = Pattern.compile("[A-Za-z_$][\\w$]{2,}");
   private static final int MAX_UNDO = 200;
   private static final int MAX_CHARS = 1048576;
   private final List<StringBuilder> lines = new ArrayList<>();
   private final Deque<CodeArea.Snapshot> undo = new ArrayDeque<>();
   private final Deque<CodeArea.Snapshot> redo = new ArrayDeque<>();
   private final Map<Character, Float> widths = new HashMap<>();
   private final Supplier<Map<String, List<String>>> api;
   private Font font;
   private float fontSize = 6.0F;
   private int line;
   private int col;
   private int anchorLine = -1;
   private int anchorCol;
   private final Smooth scroller = new Smooth(18.0F);
   private float scroll;
   private float scrollX;
   private boolean dragging;
   private boolean focused = true;
   private boolean dirty;
   private long lastSnapshot;
   private String lastKind = "";
   private int errorLine = -1;
   private long blinkStart = System.currentTimeMillis();
   private final List<String> completions = new ArrayList<>();
   private int completionIndex;
   private String completionPrefix = "";
   private float x;
   private float y;
   private float width;
   private float height;
   private float gutter;
   private int editVersion;
   private int commentVersion = -1;
   private boolean[] commentAtStart = new boolean[0];

   public CodeArea(Supplier<Map<String, List<String>>> api) {
      this.api = api;
      this.setText("");
   }

   public void setText(String text) {
      this.lines.clear();

      for (String str : text.replace("\r\n", "\n").replace("\t", "    ").split("\n", -1)) {
         this.lines.add(new StringBuilder(str));
      }

      if (this.lines.isEmpty()) {
         this.lines.add(new StringBuilder());
      }

      this.line = 0;
      this.col = 0;
      this.anchorLine = -1;
      this.undo.clear();
      this.redo.clear();
      this.editVersion++;
      this.dirty = false;
      this.scroller.reset(0.0F);
      this.scroll = 0.0F;
      this.scrollX = 0.0F;
      this.completions.clear();
   }

   public String getText() {
      StringBuilder builder = new StringBuilder();

      for (int i = 0; i < this.lines.size(); i++) {
         if (i > 0) {
            builder.append('\n');
         }

         builder.append(this.lines.get(i));
      }

      return builder.toString();
   }

   public boolean isDirty() {
      return this.dirty;
   }

   public void markSaved() {
      this.dirty = false;
   }

   public void setErrorLine(int errorLine) {
      this.errorLine = errorLine;
   }

   public void setFocused(boolean focused) {
      this.focused = focused;
   }

   public boolean isFocused() {
      return this.focused;
   }

   public int caretLine() {
      return this.line + 1;
   }

   public int caretColumn() {
      return this.col + 1;
   }

   public int lineCount() {
      return this.lines.size();
   }

   public void goToLine(int n) {
      this.line = Mth.clamp(n - 1, 0, this.lines.size() - 1);
      this.col = 0;
      this.anchorLine = -1;
      this.ensureCaretVisible();
   }

   public void zoom(float f) {
      this.fontSize = Mth.clamp(this.fontSize + f, 4.5F, 10.0F);
      this.widths.clear();
   }

   private Font font() {
      if (this.font == null || this.font.getSize() != this.fontSize) {
         this.font = Fonts.NEW_MEDIUM.getFont(this.fontSize);
         this.widths.clear();
      }

      return this.font;
   }

   private float lineHeight() {
      return this.fontSize * 1.55F;
   }

   private float charWidth(char charAt) {
      Float width = this.widths.get(charAt);
      if (width == null) {
         width = this.font().width(String.valueOf(charAt));
         this.widths.put(charAt, width);
      }

      return width;
   }

   private float textWidth(CharSequence line, int n, int col) {
      float f = 0.0F;

      for (int i = n; i < col && i < line.length(); i++) {
         f += this.charWidth(line.charAt(i));
      }

      return f;
   }

   private int columnAt(StringBuilder line, float textWidth) {
      float f = 0.0F;

      for (int i = 0; i < line.length(); i++) {
         float charWidth = this.charWidth(line.charAt(i));
         if (f + charWidth / 2.0F >= textWidth) {
            return i;
         }

         f += charWidth;
      }

      return line.length();
   }

   public void render(MenuDrawContext menuDrawContext, float x, float y, float width, float height, int lastClickX, int lastClickY, float previewAlpha, ZenithColor zenithColor) {
      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;
      Font font = this.font();
      float lineHeight = this.lineHeight();
      this.gutter = font.width(String.valueOf(this.lines.size())) + 12.0F;
      float f = this.lines.size() * lineHeight + height * 0.5F;
      this.scroller.clamp(Math.min(0.0F, height - f), 0.0F);
      this.scroll = this.scroller.update();
      menuDrawContext.drawRoundedRect(x, y, width, height, ProxyDotRadiusData.method8069(4.0F), zenithColor.method7294(previewAlpha));
      menuDrawContext.drawRect(x + this.gutter - 3.0F, y + 3.0F, 1.0F, height - 6.0F, GUTTER.method7294(previewAlpha * 0.35F));
      if (this.dragging) {
         this.placeCaret(lastClickX, lastClickY, true);
      }

      MsdfRenderer.flushBatch();
      menuDrawContext.enableScissor(x, y, x + width, y + height);
      int n = Math.max(0, (int)(-this.scroll / lineHeight) - 1);
      int n2 = Math.min(this.lines.size() - 1, n + (int)(height / lineHeight) + 2);
      boolean blockCommentBefore = this.blockCommentBefore(n);
      float f2 = x + this.gutter + 2.0F - this.scrollX;

      for (int i = n; i <= n2; i++) {
         float y2 = y + 4.0F + this.scroll + i * lineHeight;
         float centeredTextY = y2 + (lineHeight - font.height()) / 2.0F;
         if (i == this.line && this.focused) {
            menuDrawContext.drawRect(x + this.gutter - 2.0F, y2, width - this.gutter + 2.0F, lineHeight, PLAIN.method7294(previewAlpha * 0.045F));
         }

         if (i == this.errorLine - 1) {
            menuDrawContext.drawRect(x + 1.0F, y2, width - 2.0F, lineHeight, ERROR.method7294(previewAlpha * 0.14F));
            menuDrawContext.drawRect(x + 2.0F, y2 + 1.0F, 2.0F, lineHeight - 2.0F, ERROR.method7294(previewAlpha));
         }

         this.renderSelection(menuDrawContext, i, f2, y2, lineHeight, previewAlpha);
         String text = String.valueOf(i + 1);
         menuDrawContext.drawText(
            font,
            text,
            x + this.gutter - 6.0F - font.width(text),
            centeredTextY,
            (i == this.line ? PLAIN : GUTTER).method7294(previewAlpha * (i == this.line ? 0.8F : 1.0F))
         );
         blockCommentBefore = this.renderLine(menuDrawContext, this.lines.get(i), f2, centeredTextY, previewAlpha, blockCommentBefore);
      }

      if (this.focused && (System.currentTimeMillis() - this.blinkStart) / 530L % 2L == 0L) {
         float floatValue = f2 + this.textWidth(this.lines.get(this.line), 0, this.col);
         float f3 = y + 4.0F + this.scroll + this.line * lineHeight;
         menuDrawContext.drawRect(floatValue, f3 + 1.0F, 0.8F, lineHeight - 2.0F, PLAIN.method7294(previewAlpha));
      }

      MsdfRenderer.flushBatch();
      menuDrawContext.disableScissor();
      this.renderCompletions(menuDrawContext, f2, previewAlpha);
   }

   private void renderSelection(MenuDrawContext menuDrawContext, int n, float f, float y, float lineHeight, float previewAlpha) {
      if (this.hasSelection()) {
         int[] selection = this.selection();
         if (n >= selection[0] && n <= selection[2]) {
            StringBuilder line = this.lines.get(n);
            int col = n == selection[0] ? selection[1] : 0;
            int length = n == selection[2] ? selection[3] : line.length();
            float floatValue = f + this.textWidth(line, 0, col);
            float f2 = f + this.textWidth(line, 0, length) + (n != selection[2] ? this.charWidth(' ') : 0.0F);
            menuDrawContext.drawRect(floatValue, y, Math.max(1.0F, f2 - floatValue), lineHeight, API.method7294(previewAlpha * 0.28F));
         }
      }
   }

   private boolean blockCommentBefore(int n) {
      if (this.commentVersion != this.editVersion || this.commentAtStart.length != this.lines.size() + 1) {
         boolean[] commentAtStart = new boolean[this.lines.size() + 1];
         boolean blockCommentBefore = false;

         for (int i = 0; i < this.lines.size(); i++) {
            commentAtStart[i] = blockCommentBefore;
            blockCommentBefore = scanComment(this.lines.get(i), blockCommentBefore);
         }

         commentAtStart[this.lines.size()] = blockCommentBefore;
         this.commentAtStart = commentAtStart;
         this.commentVersion = this.editVersion;
      }

      return this.commentAtStart[Math.max(0, Math.min(n, this.lines.size()))];
   }

   private static boolean scanComment(CharSequence line, boolean blockCommentBefore) {
      char charAt = 0;

      for (int i = 0; i < line.length(); i++) {
         char c = line.charAt(i);
         char charAt2 = i + 1 < line.length() ? line.charAt(i + 1) : 0;
         if (blockCommentBefore) {
            if (c == '*' && charAt2 == '/') {
               blockCommentBefore = false;
               i++;
            }
         } else if (charAt != 0) {
            if (c == '\\') {
               i++;
            } else if (c == charAt) {
               charAt = 0;
            }
         } else if (c != '"' && c != '\'' && c != '`') {
            if (c == '/' && charAt2 == '/') {
               return false;
            }

            if (c == '/' && charAt2 == '*') {
               blockCommentBefore = true;
               i++;
            }
         } else {
            charAt = c;
         }
      }

      return blockCommentBefore;
   }

   private boolean renderLine(MenuDrawContext menuDrawContext, StringBuilder line, float f, float centeredTextY, float previewAlpha, boolean blockCommentBefore) {
      Font font = this.font();
      float floatValue = f;
      int length = 0;
      int end = line.length();
      float f2 = this.x + this.width;

      while (length < end) {
         int start = length;
         ZenithColor zenithColor = PLAIN;
         char charAt = line.charAt(length);
         if (blockCommentBefore) {
            int indexOf = line.indexOf("*/", length);
            length = indexOf < 0 ? end : indexOf + 2;
            blockCommentBefore = indexOf < 0;
            zenithColor = COMMENT;
         } else if (charAt == '/' && length + 1 < end && line.charAt(length + 1) == '/') {
            length = end;
            zenithColor = COMMENT;
         } else if (charAt == '/' && length + 1 < end && line.charAt(length + 1) == '*') {
            int indexOf = line.indexOf("*/", length + 2);
            length = indexOf < 0 ? end : indexOf + 2;
            blockCommentBefore = indexOf < 0;
            zenithColor = COMMENT;
         } else if (charAt == '"' || charAt == '\'' || charAt == '`') {
            length++;

            for (; length < end && line.charAt(length) != charAt; length++) {
               if (line.charAt(length) == '\\') {
                  length++;
               }
            }

            length = Math.min(end, length + 1);
            zenithColor = STRING;
         } else if (!Character.isDigit(charAt)) {
            if (!Character.isJavaIdentifierStart(charAt)) {
               length++;
            } else {
               while (length < end && Character.isJavaIdentifierPart(line.charAt(length))) {
                  length++;
               }

               String str = line.substring(start, length);
               if (KEYWORDS.contains(str)) {
                  zenithColor = KEYWORD;
               } else if (this.api.get().containsKey(str)) {
                  zenithColor = API;
               }
            }
         } else {
            while (length < end && (Character.isLetterOrDigit(line.charAt(length)) || line.charAt(length) == '.')) {
               length++;
            }

            zenithColor = NUMBER;
         }

         String str = line.substring(start, length);
         float textWidth = this.textWidth(str, 0, str.length());
         if (floatValue + textWidth >= this.x && floatValue <= f2) {
            menuDrawContext.drawText(font, str, floatValue, centeredTextY, zenithColor.method7294(previewAlpha));
         }

         floatValue += textWidth;
         if (floatValue > f2) {
            break;
         }
      }

      if (length < end) {
         blockCommentBefore = scanComment(line.subSequence(length, end), blockCommentBefore);
      }

      return blockCommentBefore;
   }

   private void renderCompletions(MenuDrawContext menuDrawContext, float f, float previewAlpha) {
      if (!this.completions.isEmpty() && this.focused) {
         Font font = this.font();
         float lineHeight = this.lineHeight();
         int n = Math.min(8, this.completions.size());
         int n2 = Mth.clamp(this.completionIndex - n + 1, 0, Math.max(0, this.completions.size() - n));
         float width = 0.0F;

         for (int i = n2; i < n2 + n; i++) {
            width = Math.max(width, font.width(this.completions.get(i)));
         }

         width += 14.0F;
         float floatValue = Math.min(
            f + this.textWidth(this.lines.get(this.line), 0, this.col - this.completionPrefix.length()), this.x + this.width - width - 4.0F
         );
         float y = this.y + 4.0F + this.scroll + (this.line + 1) * lineHeight + 2.0F;
         if (y + n * lineHeight > this.y + this.height) {
            y = this.y + 4.0F + this.scroll + this.line * lineHeight - n * lineHeight - 2.0F;
         }

         menuDrawContext.drawRoundedRect(floatValue, y, width, n * lineHeight + 4.0F, ProxyDotRadiusData.method8069(3.0F), new ZenithColor(24, 26, 34).method7294(previewAlpha * 0.97F));

         for (int i = 0; i < n; i++) {
            int n3 = n2 + i;
            float y2 = y + 2.0F + i * lineHeight;
            if (n3 == this.completionIndex) {
               menuDrawContext.drawRoundedRect(floatValue + 2.0F, y2, width - 4.0F, lineHeight, ProxyDotRadiusData.method8069(2.0F), API.method7294(previewAlpha * 0.25F));
            }

            menuDrawContext.drawText(
               font,
               this.completions.get(n3),
               floatValue + 7.0F,
               y2 + (lineHeight - font.height()) / 2.0F,
               (n3 == this.completionIndex ? PLAIN : GUTTER.method7303(0.4F)).method7294(previewAlpha)
            );
         }
      }
   }

   public boolean contains(double lastClickX, double lastClickY) {
      return lastClickX >= this.x && lastClickX <= this.x + this.width && lastClickY >= this.y && lastClickY <= this.y + this.height;
   }

   public boolean mouseClicked(double lastClickX, double lastClickY, int n) {
      if (!this.contains(lastClickX, lastClickY)) {
         this.focused = false;
         this.completions.clear();
         return false;
      } else {
         this.focused = true;
         this.completions.clear();
         if (n == 0) {
            boolean flag = shiftDown();
            if (!flag) {
               this.anchorLine = -1;
            } else if (this.anchorLine < 0) {
               this.anchorLine = this.line;
               this.anchorCol = this.col;
            }

            this.placeCaret(lastClickX, lastClickY, false);
            if (!flag) {
               this.anchorLine = this.line;
               this.anchorCol = this.col;
            }

            this.dragging = true;
         }

         return true;
      }
   }

   public void mouseReleased() {
      this.dragging = false;
      if (this.anchorLine == this.line && this.anchorCol == this.col) {
         this.anchorLine = -1;
      }
   }

   private void placeCaret(double lastClickX, double lastClickY, boolean flag) {
      float lineHeight = this.lineHeight();
      int n = (int)Math.floor((lastClickY - this.y - 4.0 - this.scroll) / lineHeight);
      this.line = Mth.clamp(n, 0, this.lines.size() - 1);
      this.col = this.columnAt(this.lines.get(this.line), (float)(lastClickX - (this.x + this.gutter + 2.0F - this.scrollX)));
      this.blinkStart = System.currentTimeMillis();
      if (flag && lastClickY < this.y) {
         this.scroller.add(lineHeight);
      }

      if (flag && lastClickY > this.y + this.height) {
         this.scroller.add(-lineHeight);
      }
   }

   public boolean mouseScrolled(double lastClickX, double lastClickY, double verticalAmount) {
      if (!this.contains(lastClickX, lastClickY)) {
         return false;
      } else if (ctrlDown()) {
         this.zoom((float)Math.signum(verticalAmount) * 0.5F);
         return true;
      } else if (shiftDown()) {
         this.scrollX = Math.max(0.0F, this.scrollX - (float)verticalAmount * 30.0F);
         return true;
      } else {
         this.scroller.add((float)verticalAmount * this.lineHeight() * 3.0F);
         return true;
      }
   }

   private static boolean ctrlDown() {
      long handle = Minecraft.getInstance().getWindow().handle();
      return org.zenith.utility.misc.KeyCompat.isKeyDown(341) || org.zenith.utility.misc.KeyCompat.isKeyDown(345);
   }

   private static boolean shiftDown() {
      long handle = Minecraft.getInstance().getWindow().handle();
      return org.zenith.utility.misc.KeyCompat.isKeyDown(340) || org.zenith.utility.misc.KeyCompat.isKeyDown(344);
   }

   public boolean hasCompletions() {
      return !this.completions.isEmpty();
   }

   public void closeCompletions() {
      this.completions.clear();
   }

   public boolean keyPressed(int keyCode, int modifiers) {
      if (!this.focused) {
         return false;
      } else {
         boolean flag = (modifiers & 2) != 0;
         boolean flag2 = (modifiers & 1) != 0;
         this.blinkStart = System.currentTimeMillis();
         if (!this.completions.isEmpty()) {
            switch (keyCode) {
               case 256:
                  this.completions.clear();
                  return true;
               case 257:
               case 258:
               case 335:
                  this.acceptCompletion();
                  return true;
               case 264:
                  this.completionIndex = (this.completionIndex + 1) % this.completions.size();
                  return true;
               case 265:
                  this.completionIndex = (this.completionIndex - 1 + this.completions.size()) % this.completions.size();
                  return true;
            }
         }

         if (flag) {
            switch (keyCode) {
               case 32:
                  this.updateCompletions(true);
                  return true;
               case 45:
               case 333:
                  this.zoom(-0.5F);
                  return true;
               case 47:
                  this.toggleComment();
                  return true;
               case 61:
               case 334:
                  this.zoom(0.5F);
                  return true;
               case 65:
                  this.anchorLine = 0;
                  this.anchorCol = 0;
                  this.line = this.lines.size() - 1;
                  this.col = this.lines.get(this.line).length();
                  return true;
               case 67:
                  if (this.hasSelection()) {
                     Minecraft.getInstance().keyboardHandler.setClipboard(this.selectedText());
                  } else {
                     Minecraft.getInstance().keyboardHandler.setClipboard(this.lines.get(this.line) + "\n");
                  }

                  return true;
               case 68:
                  this.snapshot("dup", true);
                  this.lines.add(this.line + 1, new StringBuilder(this.lines.get(this.line)));
                  this.line++;
                  this.changed();
                  return true;
               case 86:
                  String clipboard = Minecraft.getInstance().keyboardHandler.getClipboard();
                  if (clipboard != null && !clipboard.isEmpty()) {
                     this.snapshot("paste", true);
                     this.insert(clipboard.replace("\r\n", "\n").replace("\t", "    "));
                  }

                  return true;
               case 88:
                  this.snapshot("cut", true);
                  if (this.hasSelection()) {
                     Minecraft.getInstance().keyboardHandler.setClipboard(this.selectedText());
                     this.deleteSelection();
                  } else {
                     Minecraft.getInstance().keyboardHandler.setClipboard(this.lines.get(this.line) + "\n");
                     this.removeLine(this.line);
                  }

                  this.changed();
                  return true;
               case 89:
                  this.redo();
                  return true;
               case 90:
                  if (flag2) {
                     this.redo();
                  } else {
                     this.undo();
                  }

                  return true;
               case 262:
                  this.moveTo(this.line, this.wordBoundary(1), flag2);
                  return true;
               case 263:
                  this.moveTo(this.line, this.wordBoundary(-1), flag2);
                  return true;
               case 268:
                  this.moveTo(0, 0, flag2);
                  return true;
               case 269:
                  this.moveTo(this.lines.size() - 1, this.lines.get(this.lines.size() - 1).length(), flag2);
                  return true;
               default:
                  return false;
            }
         } else {
            switch (keyCode) {
               case 257:
               case 335:
                  this.snapshot("enter", true);
                  if (this.hasSelection()) {
                     this.deleteSelection();
                  }

                  StringBuilder line = this.lines.get(this.line);
                  String repeat = " ".repeat(indentOf(line));
                  boolean flag3 = this.col > 0 && "{[(".indexOf(line.charAt(this.col - 1)) >= 0;
                  String str = line.substring(this.col);
                  line.setLength(this.col);
                  if (!flag3) {
                     this.lines.add(this.line + 1, new StringBuilder(repeat + str));
                     this.line++;
                     this.col = repeat.length();
                  } else {
                     boolean flag4 = !str.isEmpty() && "}])".indexOf(str.charAt(0)) >= 0;
                     this.lines.add(this.line + 1, new StringBuilder(repeat + "    " + (flag4 ? "" : str)));
                     if (flag4) {
                        this.lines.add(this.line + 2, new StringBuilder(repeat + str));
                     }

                     this.line++;
                     this.col = repeat.length() + 4;
                  }

                  this.changed();
                  break;
               case 258:
                  this.snapshot("indent", true);
                  if (this.hasSelection()) {
                     int[] selection = this.selection();

                     for (int i = selection[0]; i <= selection[2]; i++) {
                        StringBuilder builder = this.lines.get(i);
                        if (flag2) {
                           builder.delete(0, Math.min(4, indentOf(builder)));
                        } else {
                           builder.insert(0, "    ");
                        }
                     }
                  } else if (flag2) {
                     int end = Math.min(4, indentOf(this.lines.get(this.line)));
                     this.lines.get(this.line).delete(0, end);
                     this.col = Math.max(0, this.col - end);
                  } else {
                     this.insert("    ");
                  }

                  this.changed();
                  break;
               case 259:
                  this.snapshot("delete", false);
                  if (this.hasSelection()) {
                     this.deleteSelection();
                  } else if (this.col > 0) {
                     StringBuilder builder = this.lines.get(this.line);
                     byte b = 1;
                     if (this.col >= 4 && builder.substring(this.col - 4, this.col).equals("    ") && this.col <= indentOf(builder)) {
                        b = 4;
                     }

                     builder.delete(this.col - b, this.col);
                     this.col -= b;
                  } else if (this.line > 0) {
                     int length = this.lines.get(this.line - 1).length();
                     this.lines.get(this.line - 1).append(this.lines.get(this.line));
                     this.lines.remove(this.line);
                     this.line--;
                     this.col = length;
                  }

                  this.changed();
                  this.updateCompletions(false);
                  break;
               case 261:
                  this.snapshot("delete", false);
                  if (this.hasSelection()) {
                     this.deleteSelection();
                  } else if (this.col < this.lines.get(this.line).length()) {
                     this.lines.get(this.line).deleteCharAt(this.col);
                  } else if (this.line < this.lines.size() - 1) {
                     this.lines.get(this.line).append(this.lines.get(this.line + 1));
                     this.lines.remove(this.line + 1);
                  }

                  this.changed();
                  break;
               case 262:
                  if (this.hasSelection() && !flag2) {
                     int[] selection = this.selection();
                     this.moveTo(selection[2], selection[3], false);
                  } else if (this.col < this.lines.get(this.line).length()) {
                     this.moveTo(this.line, this.col + 1, flag2);
                  } else if (this.line < this.lines.size() - 1) {
                     this.moveTo(this.line + 1, 0, flag2);
                  }
                  break;
               case 263:
                  if (this.hasSelection() && !flag2) {
                     int[] selection = this.selection();
                     this.moveTo(selection[0], selection[1], false);
                  } else if (this.col > 0) {
                     this.moveTo(this.line, this.col - 1, flag2);
                  } else if (this.line > 0) {
                     this.moveTo(this.line - 1, this.lines.get(this.line - 1).length(), flag2);
                  }
                  break;
               case 264:
                  this.moveVertical(1, flag2);
                  break;
               case 265:
                  this.moveVertical(-1, flag2);
                  break;
               case 266:
                  this.moveVertical(-((int)(this.height / this.lineHeight())), flag2);
                  break;
               case 267:
                  this.moveVertical((int)(this.height / this.lineHeight()), flag2);
                  break;
               case 268:
                  int n = indentOf(this.lines.get(this.line));
                  this.moveTo(this.line, this.col == n ? 0 : n, flag2);
                  break;
               case 269:
                  this.moveTo(this.line, this.lines.get(this.line).length(), flag2);
                  break;
               default:
                  return keyCode != 256 && keyCode < 290;
            }

            this.ensureCaretVisible();
            return true;
         }
      }
   }

   public boolean charTyped(char codepoint) {
      if (this.focused && codepoint >= ' ' && codepoint != 127) {
         this.snapshot("type", false);
         String str = "()[]{}\"\"''``";
         int indexOf = str.indexOf(codepoint);
         StringBuilder line = this.lines.get(this.line);
         if ((codepoint == ')' || codepoint == ']' || codepoint == '}' || codepoint == '"' || codepoint == '\'' || codepoint == '`')
            && !this.hasSelection()
            && this.col < line.length()
            && line.charAt(this.col) == codepoint) {
            this.col++;
         } else if (indexOf < 0 || indexOf % 2 != 0 || this.hasSelection() || this.col < line.length() && " )]};,".indexOf(line.charAt(this.col)) < 0) {
            this.insert(String.valueOf(codepoint));
         } else {
            this.insert("" + codepoint + str.charAt(indexOf + 1));
            this.col--;
         }

         if (codepoint == '}' && this.col > 0) {
            StringBuilder builder = this.lines.get(this.line);
            if (builder.toString().trim().equals("}") && indentOf(builder) >= 4) {
               builder.delete(0, 4);
               this.col = Math.max(0, this.col - 4);
            }
         }

         this.changed();
         this.updateCompletions(codepoint == '.');
         this.ensureCaretVisible();
         return true;
      } else {
         return false;
      }
   }

   private void insert(String str) {
      if (this.hasSelection()) {
         this.deleteSelection();
      }

      if (this.charCount() + str.length() <= 1048576) {
         String[] strings = str.split("\n", -1);
         StringBuilder line = this.lines.get(this.line);
         String str2 = line.substring(this.col);
         line.setLength(this.col);
         line.append(strings[0]);
         int n = this.line;

         for (int i = 1; i < strings.length; i++) {
            this.lines.add(++n, new StringBuilder(strings[i]));
         }

         StringBuilder builder = this.lines.get(n);
         this.col = builder.length();
         builder.append(str2);
         this.line = n;
         this.changed();
      }
   }

   private int charCount() {
      int n = 0;

      for (StringBuilder line : this.lines) {
         n += line.length() + 1;
      }

      return n;
   }

   private void removeLine(int line) {
      this.editVersion++;
      if (this.lines.size() == 1) {
         this.lines.get(0).setLength(0);
         this.col = 0;
      } else {
         this.lines.remove(line);
         this.line = Math.min(line, this.lines.size() - 1);
         this.col = Math.min(this.col, this.lines.get(this.line).length());
      }
   }

   private void toggleComment() {
      this.snapshot("comment", true);
      int line = this.line;
      int n = this.line;
      if (this.hasSelection()) {
         int[] selection = this.selection();
         line = selection[0];
         n = selection[2];
      }

      boolean flag = true;

      for (int i = line; i <= n; i++) {
         if (!this.lines.get(i).toString().trim().startsWith("//") && !this.lines.get(i).toString().isBlank()) {
            flag = false;
         }
      }

      for (int i = line; i <= n; i++) {
         StringBuilder builder = this.lines.get(i);
         int offset = indentOf(builder);
         if (flag) {
            int indexOf = builder.indexOf("//");
            if (indexOf >= 0) {
               builder.delete(indexOf, Math.min(builder.length(), indexOf + (builder.length() > indexOf + 2 && builder.charAt(indexOf + 2) == ' ' ? 3 : 2)));
            }
         } else if (!builder.toString().isBlank()) {
            builder.insert(offset, "// ");
         }
      }

      this.col = Math.min(this.col, this.lines.get(this.line).length());
      this.changed();
   }

   private int wordBoundary(int n) {
      StringBuilder line = this.lines.get(this.line);
      int col = this.col;
      if (n < 0) {
         while (col > 0 && !Character.isJavaIdentifierPart(line.charAt(col - 1))) {
            col--;
         }

         while (col > 0 && Character.isJavaIdentifierPart(line.charAt(col - 1))) {
            col--;
         }

         return col;
      } else {
         while (col < line.length() && !Character.isJavaIdentifierPart(line.charAt(col))) {
            col++;
         }

         while (col < line.length() && Character.isJavaIdentifierPart(line.charAt(col))) {
            col++;
         }

         return col;
      }
   }

   private static int indentOf(CharSequence line) {
      int n = 0;

      while (n < line.length() && line.charAt(n) == ' ') {
         n++;
      }

      return n;
   }

   private void moveVertical(int n, boolean flag) {
      float textWidth = this.textWidth(this.lines.get(this.line), 0, this.col);
      int line = Mth.clamp(this.line + n, 0, this.lines.size() - 1);
      this.moveTo(line, this.columnAt(this.lines.get(line), textWidth), flag);
   }

   private void moveTo(int line, int length, boolean flag) {
      if (flag) {
         if (this.anchorLine < 0) {
            this.anchorLine = this.line;
            this.anchorCol = this.col;
         }
      } else {
         this.anchorLine = -1;
      }

      this.line = Mth.clamp(line, 0, this.lines.size() - 1);
      this.col = Mth.clamp(length, 0, this.lines.get(this.line).length());
      this.completions.clear();
   }

   private boolean hasSelection() {
      return this.anchorLine >= 0 && (this.anchorLine != this.line || this.anchorCol != this.col);
   }

   private int[] selection() {
      boolean flag = this.anchorLine < this.line || this.anchorLine == this.line && this.anchorCol <= this.col;
      return flag ? new int[]{this.anchorLine, this.anchorCol, this.line, this.col} : new int[]{this.line, this.col, this.anchorLine, this.anchorCol};
   }

   private String selectedText() {
      int[] selection = this.selection();
      if (selection[0] == selection[2]) {
         return this.lines.get(selection[0]).substring(selection[1], selection[3]);
      } else {
         StringBuilder builder = new StringBuilder(this.lines.get(selection[0]).substring(selection[1]));

         for (int i = selection[0] + 1; i < selection[2]; i++) {
            builder.append('\n').append(this.lines.get(i));
         }

         builder.append('\n').append(this.lines.get(selection[2]), 0, selection[3]);
         return builder.toString();
      }
   }

   private void deleteSelection() {
      int[] selection = this.selection();
      StringBuilder line = this.lines.get(selection[0]);
      String str = this.lines.get(selection[2]).substring(selection[3]);
      line.setLength(selection[1]);
      line.append(str);

      for (int i = selection[2]; i > selection[0]; i--) {
         this.lines.remove(i);
      }

      this.line = selection[0];
      this.col = selection[1];
      this.anchorLine = -1;
      this.editVersion++;
   }

   private void snapshot(String lastKind, boolean flag) {
      long now = System.currentTimeMillis();
      if (flag || !lastKind.equals(this.lastKind) || now - this.lastSnapshot >= 800L) {
         this.lastKind = lastKind;
         this.lastSnapshot = now;
         this.undo.push(new CodeArea.Snapshot(this.getText(), this.line, this.col));

         while (this.undo.size() > 200) {
            this.undo.removeLast();
         }

         this.redo.clear();
      }
   }

   private void undo() {
      if (!this.undo.isEmpty()) {
         this.redo.push(new CodeArea.Snapshot(this.getText(), this.line, this.col));
         this.restore(this.undo.pop());
      }
   }

   private void redo() {
      if (!this.redo.isEmpty()) {
         this.undo.push(new CodeArea.Snapshot(this.getText(), this.line, this.col));
         this.restore(this.redo.pop());
      }
   }

   private void restore(CodeArea.Snapshot snapshot) {
      this.lines.clear();

      for (String str : snapshot.text().split("\n", -1)) {
         this.lines.add(new StringBuilder(str));
      }

      this.line = Mth.clamp(snapshot.line(), 0, this.lines.size() - 1);
      this.col = Mth.clamp(snapshot.col(), 0, this.lines.get(this.line).length());
      this.anchorLine = -1;
      this.lastKind = "";
      this.editVersion++;
      this.dirty = true;
      this.ensureCaretVisible();
   }

   private void changed() {
      this.editVersion++;
      this.dirty = true;
      this.errorLine = -1;
   }

   private void ensureCaretVisible() {
      float lineHeight = this.lineHeight();
      float f = this.line * lineHeight + this.scroller.target();
      if (f < 0.0F) {
         this.scroller.target(-this.line * lineHeight);
      } else if (f + lineHeight * 2.0F > this.height) {
         this.scroller.target(this.height - (this.line + 2) * lineHeight);
      }

      float textWidth = this.textWidth(this.lines.get(this.line), 0, this.col);
      float f2 = this.width - this.gutter - 12.0F;
      if (textWidth - this.scrollX > f2) {
         this.scrollX = textWidth - f2;
      }

      if (textWidth < this.scrollX) {
         this.scrollX = Math.max(0.0F, textWidth - 20.0F);
      }
   }

   private void updateCompletions(boolean flag) {
      this.completions.clear();
      this.completionIndex = 0;
      String input = this.lines.get(this.line).substring(0, this.col);
      Matcher matcher = WORD_BEFORE.matcher(input);
      if (matcher.find()) {
         String group = matcher.group(1);
         String completionPrefix = matcher.group(2) == null ? "" : matcher.group(2);
         if (flag || !completionPrefix.isEmpty() || group != null) {
            if (flag || group != null || completionPrefix.length() >= 2) {
               this.completionPrefix = completionPrefix;
               var map = this.api.get();
               Object obj;
               if (group != null) {
                  List list = (List)map.get(group);
                  if (list == null) {
                     return;
                  }

                  obj = list;
               } else {
                  LinkedHashSet linkedHashSet = new LinkedHashSet();

                  for (String str : map.keySet()) {
                     if (!str.contains(".")) {
                        linkedHashSet.add(str);
                     }
                  }

                  linkedHashSet.addAll(KEYWORDS);
                  Matcher matcher2 = IDENTIFIER.matcher(this.getText());
                  int n = 0;

                  while (matcher2.find() && n++ < 5000) {
                     linkedHashSet.add(matcher2.group());
                  }

                  obj = linkedHashSet;
               }

               String lowerCase = completionPrefix.toLowerCase(Locale.ROOT);

               for (Object obj2 : (Iterable<?>)(Object)(obj)) {
                  String str = (String)obj2;
                  if (!str.equals(completionPrefix)) {
                     if (str.toLowerCase(Locale.ROOT).startsWith(lowerCase)) {
                        this.completions.add(str);
                     }

                     if (this.completions.size() >= 60) {
                        break;
                     }
                  }
               }

               this.completions.sort(String.CASE_INSENSITIVE_ORDER);
            }
         }
      }
   }

   private void acceptCompletion() {
      if (!this.completions.isEmpty()) {
         String completion = this.completions.get(this.completionIndex);
         this.snapshot("complete", true);
         StringBuilder line = this.lines.get(this.line);
         line.replace(this.col - this.completionPrefix.length(), this.col, completion);
         this.col = this.col - this.completionPrefix.length() + completion.length();
         this.completions.clear();
         this.changed();
      }
   }

   private static final class Snapshot {
      private final String text;
      private final int line;
      private final int col;

      public String text() {
         return this.text;
      }

      public int line() {
         return this.line;
      }

      public int col() {
         return this.col;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Snapshot other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.line, other.line) && java.util.Objects.equals(this.col, other.col);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, line, col);
      }

      @Override
      public String toString() {
         return "Snapshot[" + "text=" + this.text + ", " + "line=" + this.line + ", " + "col=" + this.col + "]";
      }

      private Snapshot(String text, int line, int col) {
         this.text = text;
         this.line = line;
         this.col = col;
      }
   }
}
