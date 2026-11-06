package org.zenith.client.screens.scripts;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public final class FormField {
   private final StringBuilder text = new StringBuilder();
   private final int maxLength;
   private final boolean multiline;
   private int caret;
   private boolean focused;
   private BoundsData bounds;
   private String original = "";
   private final Animation focus = new Animation(200L, 0.0F, Easing.INSTANCE15);
   private long typedAt;
   private boolean masked;
   private int maxLines = Integer.MAX_VALUE;
   private int minLines = 3;
   private int version;
   private int wrapVersion = -1;
   private Font wrapFont;
   private float wrapWidth = Float.NaN;
   private List<String> wrapLines;
   private int shownVersion = -1;
   private boolean shownMasked;
   private Font shownFont;
   private float shownWidth = Float.NaN;
   private String shownText;

   public FormField(int maxLength, boolean multiline) {
      this.maxLength = maxLength;
      this.multiline = multiline;
   }

   public void set(String description) {
      this.text.setLength(0);
      this.text.append(description == null ? "" : description);
      this.version++;
      this.caret = this.text.length();
      this.original = this.text.toString();
   }

   public String get() {
      return this.text.toString().trim();
   }

   public boolean changed() {
      return !this.text.toString().equals(this.original);
   }

   public void commit() {
      this.original = this.text.toString();
   }

   public boolean isFocused() {
      return this.focused;
   }

   public void setFocused(boolean focused) {
      this.focused = focused;
      if (focused) {
         this.caret = this.text.length();
      }
   }

   public FormField masked(boolean masked) {
      this.masked = masked;
      return this;
   }

   public FormField lines(int minLines, int maxLines) {
      this.minLines = minLines;
      this.maxLines = maxLines;
      return this;
   }

   public void clear() {
      this.text.setLength(0);
      this.version++;
      this.caret = 0;
   }

   public float height(Font font, float f) {
      if (!this.multiline) {
         return 16.0F;
      } else {
         int n = Math.min(this.maxLines, Math.max(this.minLines, this.wrap(font, f - 12.0F).size()));
         return n * (font.height() + 3.0F) + 8.0F;
      }
   }

   public BoundsData bounds() {
      return this.bounds;
   }

   public float render(
      MenuDrawContext menuDrawContext,
      Font shownFont,
      float floatValue,
      float y,
      float shownWidth,
      float previewAlpha,
      String name,
      ZenithColor zenithColor,
      ZenithColor zenithColor2,
      ZenithColor zenithColor3,
      ZenithColor zenithColor4,
      ZenithColor zenithColor5
   ) {
      var strings = this.multiline ? this.wrap(shownFont, shownWidth - 12.0F) : List.of(this.text.toString());
      int n = Math.max(0, strings.size() - this.maxLines);
      List<String> subList = n > 0 ? strings.subList(n, strings.size()) : strings;
      float f = shownFont.height() + 3.0F;
      float height = this.multiline ? Math.min(this.maxLines, Math.max(this.minLines, subList.size())) * f + 8.0F : 16.0F;
      this.bounds = new BoundsData(floatValue, y, shownWidth, height);
      ProxyDotRadiusData headerRadius = ProxyDotRadiusData.method8069(4.0F);
      float hover = this.focus.animateTo(this.focused ? 1.0F : 0.0F);
      menuDrawContext.drawRoundedRect(floatValue, y, shownWidth, height, headerRadius, zenithColor.method7295(zenithColor3.method7292(0.08F), hover * 0.6F).method7294(previewAlpha));
      menuDrawContext.drawRoundedBorder(floatValue, y, shownWidth, height, 0.15F, headerRadius, zenithColor2.method7295(zenithColor3, hover).method7294(previewAlpha));
      if (hover > 0.01F) {
         menuDrawContext.drawRoundedBorder(
            floatValue - hover,
            y - hover,
            shownWidth + hover * 2.0F,
            height + hover * 2.0F,
            0.12F,
            ProxyDotRadiusData.method8069(4.0F + hover),
            zenithColor3.method7292(0.18F * hover).method7294(previewAlpha)
         );
      }

      long l = System.currentTimeMillis() - this.typedAt;
      float f2 = l < 500L ? 1.0F : 0.5F + 0.5F * (float)Math.cos((l - 500L) / 530.0 * Math.PI);
      boolean flag = this.focused && f2 > 0.02F;
      if (this.text.isEmpty() && !this.focused) {
         menuDrawContext.drawText(
            shownFont, shownFont.clip(name, shownWidth - 12.0F), floatValue + 6.0F, y + (this.multiline ? 5.0F : (height - shownFont.height()) / 2.0F), zenithColor5.method7294(previewAlpha)
         );
         return height;
      } else if (!this.multiline) {
         String repeat = this.masked ? "*".repeat(this.text.length()) : this.text.toString();
         String shownText;
         if (this.shownVersion == this.version && this.shownMasked == this.masked && sameFont(this.shownFont, shownFont) && this.shownWidth == shownWidth) {
            shownText = this.shownText;
         } else {
            shownText = repeat;

            while (shownFont.width(shownText) > shownWidth - 14.0F && shownText.length() > 1) {
               shownText = shownText.substring(1);
            }

            this.shownVersion = this.version;
            this.shownMasked = this.masked;
            this.shownFont = shownFont;
            this.shownWidth = shownWidth;
            this.shownText = shownText;
         }

         float centeredTextY = y + (height - shownFont.height()) / 2.0F;
         menuDrawContext.drawText(shownFont, shownText, floatValue + 6.0F, centeredTextY, zenithColor4.method7294(previewAlpha));
         if (flag) {
            int n2 = repeat.length() - shownText.length();
            float floatValue2 = floatValue + 6.0F + shownFont.width(shownText.substring(0, Math.max(0, Math.min(shownText.length(), this.caret - n2))));
            menuDrawContext.drawRect(floatValue2, centeredTextY - 0.5F, 0.7F, shownFont.height() + 1.0F, zenithColor4.method7294(previewAlpha * f2 * hover));
         }

         return height;
      } else {
         float centeredTextY = y + 5.0F;
         int n2 = 0;

         for (int i = 0; i < n; i++) {
            n2 += ((String)strings.get(i)).length();
         }

         for (String sub : subList) {
            menuDrawContext.drawText(shownFont, sub, floatValue + 6.0F, centeredTextY, zenithColor4.method7294(previewAlpha));
            if (flag && this.caret >= n2 && this.caret <= n2 + sub.length()) {
               float floatValue2 = floatValue + 6.0F + shownFont.width(sub.substring(0, this.caret - n2));
               menuDrawContext.drawRect(floatValue2, centeredTextY - 0.5F, 0.7F, shownFont.height() + 1.0F, zenithColor4.method7294(previewAlpha * f2 * hover));
               flag = false;
            }

            n2 += sub.length();
            centeredTextY += f;
         }

         return height;
      }
   }

   private List<String> wrap(Font wrapFont, float wrapWidth) {
      if (this.wrapVersion == this.version && sameFont(this.wrapFont, wrapFont) && this.wrapWidth == wrapWidth) {
         return this.wrapLines;
      } else {
         var wrapUncached = this.wrapUncached(wrapFont, wrapWidth);
         this.wrapVersion = this.version;
         this.wrapFont = wrapFont;
         this.wrapWidth = wrapWidth;
         this.wrapLines = wrapUncached;
         return wrapUncached;
      }
   }

   private static boolean sameFont(Font shownFont, Font font) {
      return shownFont != null && shownFont.getFont() == font.getFont() && shownFont.getSize() == font.getSize();
   }

   private List<String> wrapUncached(Font wrapFont, float wrapWidth) {
      ArrayList<String> strings = new ArrayList<>();
      String string = this.text.toString();
      int index = 0;

      while (index < string.length()) {
         int n = index;

         int n2;
         for (n2 = -1; n < string.length() && wrapFont.width(string.substring(index, n + 1)) <= wrapWidth; n++) {
            if (string.charAt(n) == ' ') {
               n2 = n;
            }
         }

         if (n < string.length() && n2 > index) {
            n = n2 + 1;
         }

         if (n == index) {
            n = index + 1;
         }

         strings.add(string.substring(index, n));
         index = n;
      }

      if (strings.isEmpty()) {
         strings.add("");
      }

      return strings;
   }

   public boolean keyPressed(int keyCode, int modifiers) {
      if (!this.focused) {
         return false;
      } else {
         this.typedAt = System.currentTimeMillis();
         boolean flag = (modifiers & 2) != 0;
         switch (keyCode) {
            case 65:
               if (flag) {
                  this.caret = this.text.length();
               }
               break;
            case 67:
               if (flag && !this.masked) {
                  Minecraft.getInstance().keyboardHandler.setClipboard(this.text.toString());
               }
               break;
            case 86:
               if (flag) {
                  String clipboard = Minecraft.getInstance().keyboardHandler.getClipboard();
                  if (clipboard != null) {
                     this.insert(clipboard.replace("\r", " ").replace("\n", " "));
                  }
               }
               break;
            case 256:
            case 257:
            case 258:
            case 335:
               this.focused = false;
               break;
            case 259:
               if (flag) {
                  int caret = this.caret;

                  while (caret > 0 && this.text.charAt(caret - 1) == ' ') {
                     caret--;
                  }

                  while (caret > 0 && this.text.charAt(caret - 1) != ' ') {
                     caret--;
                  }

                  this.text.delete(caret, this.caret);
                  this.version++;
                  this.caret = caret;
               } else if (this.caret > 0) {
                  this.text.deleteCharAt(--this.caret);
                  this.version++;
               }
               break;
            case 261:
               if (this.caret < this.text.length()) {
                  this.text.deleteCharAt(this.caret);
                  this.version++;
               }
               break;
            case 262:
               this.caret = Math.min(this.text.length(), this.caret + 1);
               break;
            case 263:
               this.caret = Math.max(0, this.caret - 1);
               break;
            case 268:
               this.caret = 0;
               break;
            case 269:
               this.caret = this.text.length();
         }

         return true;
      }
   }

   public boolean charTyped(char codepoint) {
      if (this.focused && codepoint >= ' ') {
         this.insert(String.valueOf(codepoint));
         return true;
      } else {
         return false;
      }
   }

   private void insert(String str) {
      this.typedAt = System.currentTimeMillis();
      int endIndex = this.maxLength - this.text.length();
      if (endIndex > 0) {
         String str2 = str.length() > endIndex ? str.substring(0, endIndex) : str;
         this.text.insert(this.caret, str2);
         this.version++;
         this.caret = this.caret + str2.length();
      }
   }
}
