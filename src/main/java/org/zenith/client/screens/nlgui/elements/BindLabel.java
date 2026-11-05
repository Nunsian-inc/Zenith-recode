package org.zenith.client.screens.nlgui.elements;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.util.Mth;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.utility.misc.KeyName;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public final class BindLabel {
   public static final String HINT = "+";
   private static final long HINT_DELAY_MS = 350L;
   private static final long HINT_FADE_MS = 220L;
   private static final float HIT_PAD = 3.0F;
   private static final long ANIM_MS = 800L;
   private static final long ENTER_DELAY_MS = 90L;
   private static final String[] ASCII = new String[128];
   private static final Map<String, String> SHORT_NAMES = new ConcurrentHashMap<>();
   private final Animation hintFade = new Animation(220L, 0.0F, Easing.INSTANCE12);
   private BindLabel.Kind kind = BindLabel.Kind.NONE;
   private long startedAt;
   private String from;
   private String shown;
   private long hoverStartAt = -1L;
   private boolean binding;

   private static String glyph(char charAt) {
      return charAt < ASCII.length ? ASCII[charAt] : String.valueOf(charAt);
   }

   public void waiting() {
      this.start(BindLabel.Kind.WAIT);
   }

   public void set() {
      this.start(BindLabel.Kind.SET);
   }

   public void cleared() {
      this.start(BindLabel.Kind.CLEAR);
   }

   private void start(BindLabel.Kind kind) {
      this.from = this.shown;
      this.kind = kind;
      this.startedAt = System.currentTimeMillis();
   }

   public static String waitText() {
      return Zenith.getInstance().getLocalization().translate("gui.modules.bind.wait");
   }

   public static String text(boolean binding, int keyCode, int modifiers) {
      if (binding) {
         return waitText();
      } else if (keyCode != -1 && keyCode != 0) {
         try {
            String str = KeyName.method6702(keyCode, modifiers);
            if (str != null && !str.isBlank()) {
               String shortKeyName2 = SHORT_NAMES.get(str);
               if (shortKeyName2 == null) {
                  shortKeyName2 = shortKeyName(str.toUpperCase());
                  if (SHORT_NAMES.size() < 512) {
                     SHORT_NAMES.put(str, shortKeyName2);
                  }
               }

               return shortKeyName2;
            }
         } catch (Exception e) {
         }

         return "?";
      } else {
         return null;
      }
   }

   public static String shortKeyName(String upperCase) {
      String str = upperCase.replace(" + ", "+")
         .replace("SHIFT", "SHFT")
         .replace("SUPER", "WIN")
         .replace("RIGHT_", "R")
         .replace("LEFT_", "L")
         .replace("CONTROL", "CTRL")
         .replace("KEY_PAD_", "NUM")
         .replace("PAGE_", "PG");
      int endIndex = str.indexOf(43) >= 0 ? 14 : 8;
      return str.length() <= endIndex ? str : str.substring(0, endIndex);
   }

   public BoundsData render(
      MenuDrawContext menuDrawContext,
      float mouseX,
      float mouseY,
      float y,
      float height,
      float f,
      String text,
      boolean binding,
      Font font,
      float f2,
      boolean flag,
      float previewAlpha,
      BoundsData boundsData,
      ZenithStyle currentStyle
   ) {
      this.binding = binding;
      long now = System.currentTimeMillis();
      if (flag) {
         if (this.hoverStartAt < 0L) {
            this.hoverStartAt = now;
         }
      } else {
         this.hoverStartAt = -1L;
      }

      ZenithColor zenithColor = currentStyle.getPrimaryColor().getZenithColor();
      ZenithColor zenithColor2 = currentStyle.getTextTertiary().getZenithColor();
      ZenithColor zenithColor3 = currentStyle.getTextEnable().getZenithColor();
      float f3 = y + (height - font.height()) / 2.0F;
      float y2 = f3 + font.height() + 0.6F;
      long l = this.kind == BindLabel.Kind.NONE ? Long.MAX_VALUE : now - this.startedAt;
      if (l >= 800L) {
         this.kind = BindLabel.Kind.NONE;
         this.from = null;
      }

      boolean flag2 = this.kind != BindLabel.Kind.NONE;
      boolean flag3 = text == null && !flag2 && this.hoverStartAt >= 0L && now - this.hoverStartAt >= 350L && f2 <= f - font.width("+");
      float f4 = this.hintFade.method2808(flag3 ? 1.0F : 0.0F);
      if (flag2 || text != null) {
         this.hintFade.method2809(0.0F);
         f4 = 0.0F;
      }

      String str = text != null ? text : (flag3 && f4 > 0.3F ? "+" : null);
      BoundsData boundsData2 = null;
      if (str != null) {
         float width = font.width(str);
         boundsData2 = new BoundsData(f - width - 3.0F, y, width + 6.0F, height);
      }

      boolean flag4 = boundsData2 != null && boundsData2.contains(mouseX, mouseY) && (boundsData == null || boundsData.contains(mouseX, mouseY));
      if (flag2 && this.from != null) {
         boolean flag5 = this.from.equals(waitText());
         ZenithColor zenithColor4 = flag5 ? zenithColor.method7294(0.8F) : zenithColor2.method7295(zenithColor3, 0.1F);
         this.drawExit(menuDrawContext, font, this.from, f, f3, l, zenithColor4, previewAlpha);
         if (this.kind == BindLabel.Kind.CLEAR) {
            float f5 = Mth.clamp((float)l / 260.0F, 0.0F, 1.0F);
            float width = font.width(this.from) * (1.0F - easeOut(f5));
            float f6 = f - font.width(this.from) / 2.0F;
            if (width > 0.3F) {
               menuDrawContext.drawRect(f6 - width / 2.0F, y2, width, 0.5F, (flag5 ? zenithColor : zenithColor2).method7294((1.0F - f5) * 0.8F * previewAlpha));
            }
         }
      }

      if (text != null) {
         float width = font.width(text);
         float floatValue = f - width;
         ZenithColor zenithColor4 = binding ? zenithColor : zenithColor2.method7295(zenithColor3, flag4 ? 0.55F : 0.1F);
         long l2 = flag2 ? l - 90L : Long.MAX_VALUE;
         float hintY2 = "+".equals(text) ? hintY(font, y, height, f3) : f3;
         this.drawEnter(menuDrawContext, font, text, floatValue, hintY2, l2, zenithColor4, zenithColor, now, previewAlpha);
         if (binding) {
            float f5 = flag2 ? Mth.clamp((float)l2 / 220.0F, 0.0F, 1.0F) : 1.0F;
            menuDrawContext.drawRect(floatValue, y2, width * f5, 0.5F, zenithColor.method7294(0.22F * previewAlpha));
            float f6 = width * 0.32F;
            float f7 = (0.5F + 0.5F * Mth.sin((float)now / 260.0F)) * (width - f6);
            menuDrawContext.drawRect(floatValue + f7, y2, f6 * f5, 0.5F, zenithColor.method7294(0.9F * f5 * previewAlpha));
         } else if (flag2 && this.kind == BindLabel.Kind.SET) {
            float easeOut2 = easeOut(Mth.clamp((float)l2 / 260.0F, 0.0F, 1.0F));
            float f5 = 1.0F - Mth.clamp(((float)l2 - 300.0F) / 320.0F, 0.0F, 1.0F);
            if (l2 > 0L && f5 > 0.0F) {
               float width2 = width * easeOut2;
               menuDrawContext.drawRect(floatValue + (width - width2) / 2.0F, y2, width2, 0.5F, zenithColor.method7295(ZenithColor.INSTANCE, 0.3F).method7294(f5 * previewAlpha));
            }

            drawSparks(menuDrawContext, floatValue + width / 2.0F, f3 + font.height() / 2.0F, l2, zenithColor, previewAlpha);
         } else if (flag4 && !flag2) {
            menuDrawContext.drawRect(floatValue, y2, width, 0.5F, zenithColor2.method7294(0.5F * previewAlpha));
         }
      } else if (f4 > 0.0F) {
         float width = font.width("+");
         ZenithColor zenithColor4 = zenithColor2.method7295(zenithColor3, flag4 ? 0.6F : 0.0F).method7294((flag4 ? 0.9F : 0.45F) * f4 * previewAlpha);
         menuDrawContext.drawText(font, "+", f - width, hintY(font, y, height, f3) + (1.0F - easeOut(f4)) * 1.5F, zenithColor4);
      }

      this.shown = text != null ? text : (f4 > 0.5F ? "+" : null);
      return boundsData2;
   }

   private static float hintY(Font font, float y, float height, float f) {
      float[] inkBounds = font.inkBounds("+");
      return inkBounds == null ? f : y + (height - inkBounds[1]) / 2.0F - font.baseline() + inkBounds[2];
   }

   private void drawExit(MenuDrawContext menuDrawContext, Font font, String from, float f, float f2, long l, ZenithColor zenithColor, float previewAlpha) {
      boolean flag = this.kind == BindLabel.Kind.CLEAR;
      long l2 = flag ? 28L : 16L;
      float f3 = flag ? 360.0F : 190.0F;
      int length = from.length();
      float f4 = f - font.width(from);

      for (int i = 0; i < length; i++) {
         String text = glyph(from.charAt(i));
         float width = font.width(text);
         long l3 = (flag ? length - 1 - i : i) * l2;
         float f5 = Mth.clamp((float)(l - l3) / f3, 0.0F, 1.0F);
         if (f5 >= 1.0F) {
            f4 += width;
         } else {
            float f6 = 0.0F;
            float f7;
            if (flag) {
               f7 = 5.0F * f5 * f5;
               f6 = (hash01(i * 31 + length) - 0.5F) * 3.5F * f5;
            } else {
               f7 = -3.0F * f5 * f5;
            }

            menuDrawContext.drawText(font, text, f4 + f6, f2 + f7, zenithColor.method7294((1.0F - f5) * previewAlpha));
            f4 += width;
         }
      }
   }

   private void drawEnter(
      MenuDrawContext menuDrawContext, Font font, String text, float floatValue, float f, long l, ZenithColor zenithColor, ZenithColor zenithColor2, long now, float previewAlpha
   ) {
      int length = text.length();
      boolean flag = l == Long.MAX_VALUE;

      for (int i = 0; i < length; i++) {
         String str = glyph(text.charAt(i));
         float width = font.width(str);
         float f2 = 0.0F;
         float f3 = 1.0F;
         ZenithColor zenithColor3 = zenithColor;
         if (!flag) {
            if (this.kind == BindLabel.Kind.SET) {
               float f4 = Mth.clamp((float)(l - i * 35L) / 420.0F, 0.0F, 1.0F);
               f2 = -4.0F * (float)(Math.exp(-7.0F * f4) * Math.cos(11.0F * f4));
               f3 = Math.min(1.0F, f4 * 4.0F);
               zenithColor3 = zenithColor.method7295(zenithColor2.method7295(ZenithColor.INSTANCE, 0.5F), (1.0F - f4) * (1.0F - f4));
            } else {
               float f4 = Mth.clamp((float)(l - i * 18L) / 200.0F, 0.0F, 1.0F);
               f2 = (1.0F - easeOut(f4)) * 3.0F;
               f3 = f4;
            }
         }

         if (this.binding) {
            float f4 = (float)(now % 900L) / 900.0F * (length + 4.0F) - 2.0F;
            float f5 = Math.max(0.0F, 1.0F - Math.abs(i - f4) / 1.6F);
            zenithColor3 = zenithColor3.method7295(ZenithColor.INSTANCE, 0.55F * f5);
         }

         if (f3 > 0.01F) {
            menuDrawContext.drawText(font, str, floatValue, f + f2, zenithColor3.method7294(f3 * previewAlpha));
         }

         floatValue += width;
      }
   }

   private static void drawSparks(MenuDrawContext menuDrawContext, float f, float f2, long l, ZenithColor zenithColor, float previewAlpha) {
      float f3 = Mth.clamp((float)l / 450.0F, 0.0F, 1.0F);
      if (l >= 0L && !(f3 >= 1.0F)) {
         float easeOut2 = easeOut(f3);

         for (int i = 0; i < 6; i++) {
            float f4 = (i * 60.0F + 30.0F) * (float) (Math.PI / 180.0);
            float f5 = easeOut2 * (6.0F + i % 2 * 3.0F);
            float width = 1.2F * (1.0F - f3);
            if (!(width < 0.05F)) {
               float f6 = f + Mth.cos(f4) * f5 * 1.4F;
               float f7 = f2 + Mth.sin(f4) * f5 * 0.8F;
               menuDrawContext.drawRoundedRect(
                  f6 - width / 2.0F,
                  f7 - width / 2.0F,
                  width,
                  width,
                  ProxyDotRadiusData.method8069(width / 2.0F),
                  (i % 2 == 0 ? zenithColor : zenithColor.method7295(ZenithColor.INSTANCE, 0.5F)).method7294((1.0F - f3) * previewAlpha)
               );
            }
         }
      }
   }

   private static float easeOut(float f) {
      float f2 = 1.0F - f;
      return 1.0F - f2 * f2 * f2;
   }

   private static float hash01(int n) {
      int n2 = n * 668265261;
      n2 ^= n2 >>> 15;
      n2 *= -2048144789;
      n2 ^= n2 >>> 13;
      return (n2 >>> 8) / 1.6777216E7F;
   }

   static {
      for (int i = 0; i < ASCII.length; i++) {
         ASCII[i] = String.valueOf((char)i);
      }
   }

   private static enum Kind {
      NONE,
      WAIT,
      SET,
      CLEAR;
   }
}
