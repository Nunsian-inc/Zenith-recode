package org.zenith.client.screens.media;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.utility.misc.HudListLayout;
import org.zenith.utility.render.display.base.CustomDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public final class MediaGlyphs {
   private static volatile MediaGlyphs.Stamp stampA;
   private static volatile MediaGlyphs.Stamp stampB;
   private static boolean stampTurn;
   private static final Map<MediaGlyphs.EllipsisKey, String> ELLIPSIZED = new LinkedHashMap<MediaGlyphs.EllipsisKey, String>(64, 0.75F, true) {
      @Override
      public boolean removeEldestEntry(Entry<MediaGlyphs.EllipsisKey, String> eldest) {
         return this.size() > 256;
      }
   };

   private MediaGlyphs() {
   }

   public static void playPause(CustomDrawContext customDrawContext, float x, float y, float f, boolean flag, ZenithColor zenithColor) {
      if (flag) {
         float width = f * 0.24F;
         float height = f * 0.78F;
         float f2 = f * 0.2F;
         ProxyDotRadiusData headerRadius = ProxyDotRadiusData.method8069(Math.min(width / 2.0F, f * 0.07F));
         customDrawContext.drawRoundedRect(x - f2 / 2.0F - width, y - height / 2.0F, width, height, headerRadius, zenithColor);
         customDrawContext.drawRoundedRect(x + f2 / 2.0F, y - height / 2.0F, width, height, headerRadius, zenithColor);
      } else {
         glyph(customDrawContext, "s", x + f * 0.06F, y, f * 0.95F, zenithColor);
      }
   }

   public static void skip(CustomDrawContext customDrawContext, float f, float y, float f2, boolean flag, ZenithColor zenithColor) {
      String text = flag ? "z" : "y";
      float px = f2 * 0.62F;
      float f3 = f2 * 0.23F;
      glyph(customDrawContext, text, f - f3, y, px, zenithColor);
      glyph(customDrawContext, text, f + f3, y, px, zenithColor);
   }

   public static void shuffle(CustomDrawContext customDrawContext, float f, float f2, float f3, ZenithColor zenithColor) {
      float x = f - f3 * 0.5F;
      float f4 = f + f3 * 0.28F;
      float y = f2 - f3 * 0.3F;
      float y2 = f2 + f3 * 0.3F;
      float x2 = x + f3 * 0.22F;
      float height = Math.max(0.8F, f3 * 0.11F);
      line(customDrawContext, x, y, x2, y, height, zenithColor);
      line(customDrawContext, x2, y, f4, y2, height, zenithColor);
      line(customDrawContext, x, y2, x2, y2, height, zenithColor);
      line(customDrawContext, x2, y2, f4, y, height, zenithColor);
      glyph(customDrawContext, "z", f4 + f3 * 0.1F, y, f3 * 0.42F, zenithColor);
      glyph(customDrawContext, "z", f4 + f3 * 0.1F, y2, f3 * 0.42F, zenithColor);
   }

   public static void repeat(CustomDrawContext customDrawContext, float f, float y, float f2, boolean flag, ZenithColor zenithColor) {
      float f3 = f2 * 0.86F;
      float f4 = f2 * 0.56F;
      float height = Math.max(0.8F, f2 * 0.11F);
      float f5 = f - f3 / 2.0F;
      float f6 = f + f3 / 2.0F;
      float y2 = y - f4 / 2.0F;
      float y3 = y + f4 / 2.0F;
      float f7 = f4 * 0.5F;
      line(customDrawContext, f5 + f7, y2, f6 - f7, y2, height, zenithColor);
      line(customDrawContext, f5 + f7, y3, f6 - f7, y3, height, zenithColor);
      arc(customDrawContext, f5 + f7, y, f4 / 2.0F, height, 180.0F, 180.0F, zenithColor);
      arc(customDrawContext, f6 - f7, y, f4 / 2.0F, height, 0.0F, 180.0F, zenithColor);
      glyph(customDrawContext, "z", f + f3 * 0.14F, y2, f2 * 0.36F, zenithColor);
      glyph(customDrawContext, "y", f - f3 * 0.14F, y3, f2 * 0.36F, zenithColor);
      if (flag) {
         Font font = Fonts.NEW_SEMIBOLD.getFont(f2 * 0.42F);
         HudListLayout.method7637(customDrawContext, font, "1", f - f2 * 0.2F, y - f2 * 0.2F, f2 * 0.4F, zenithColor);
      }
   }

   public static void speaker(CustomDrawContext customDrawContext, float f, float y, float f2, int n, ZenithColor zenithColor) {
      float width = f2 * 0.2F;
      float height = f2 * 0.34F;
      float floatValue = f - f2 * 0.42F;
      customDrawContext.drawRoundedRect(floatValue, y - height / 2.0F, width, height, ProxyDotRadiusData.method8069(width * 0.3F), zenithColor);
      glyph(customDrawContext, "y", floatValue + width + f2 * 0.08F, y, f2 * 0.66F, zenithColor);
      float height2 = Math.max(0.7F, f2 * 0.09F);
      float x = floatValue + width + f2 * 0.05F;

      for (int i = 0; i < n; i++) {
         float f3 = f2 * (0.34F + i * 0.2F);
         arc(customDrawContext, x, y, f3, height2, 50.0F, 80.0F, zenithColor);
      }
   }

   public static void arc(CustomDrawContext customDrawContext, float x, float y, float f, float height, float angdeg, float f2, ZenithColor zenithColor) {
      float width = (f + height / 2.0F + 1.6F) * 2.0F;
      customDrawContext.pushMatrix();
      customDrawContext.pose().translate(x, y);
      customDrawContext.pose().rotate((float)Math.toRadians(angdeg));
      customDrawContext.drawArcBorder(-width / 2.0F, -width / 2.0F, width, width, height, f2, 1.0F, zenithColor);
      customDrawContext.popMatrix();
   }

   public static void close(CustomDrawContext customDrawContext, float f, float f2, float f3, ZenithColor zenithColor) {
      float height = Math.max(0.8F, f3 * 0.13F);
      float f4 = f3 * 0.36F;
      line(customDrawContext, f - f4, f2 - f4, f + f4, f2 + f4, height, zenithColor);
      line(customDrawContext, f - f4, f2 + f4, f + f4, f2 - f4, height, zenithColor);
   }

   public static void line(CustomDrawContext customDrawContext, float x, float y, float f, float y2, float height, ZenithColor zenithColor) {
      float x2 = f - x;
      float y3 = y2 - y;
      float f2 = (float)Math.sqrt(x2 * x2 + y3 * y3);
      if (!(f2 < 0.01F)) {
         customDrawContext.pushMatrix();
         customDrawContext.pose().translate(x, y);
         customDrawContext.pose().rotate((float)Math.atan2(y3, x2));
         customDrawContext.drawRoundedRect(-height / 2.0F, -height / 2.0F, f2 + height, height, ProxyDotRadiusData.method8069(height / 2.0F), zenithColor);
         customDrawContext.popMatrix();
      }
   }

   private static void arrow(CustomDrawContext customDrawContext, float f, float f2, float f3, float height, ZenithColor zenithColor) {
      float f4 = f3 * 0.7071F;
      line(customDrawContext, f - f4, f2 - f4, f, f2, height, zenithColor);
      line(customDrawContext, f - f4, f2 + f4, f, f2, height, zenithColor);
   }

   private static void glyph(CustomDrawContext customDrawContext, String text, float f, float y, float px, ZenithColor zenithColor) {
      Font font = Fonts.NEW_ICONS.getFont(px);
      HudListLayout.method7637(customDrawContext, font, text, f - px / 2.0F, y - px / 2.0F, px, zenithColor);
   }

   public static String time(double d) {
      long total = Math.max(0L, Math.round(Math.floor(d)));
      MediaGlyphs.Stamp stamp = stampA;
      if (stamp != null && stamp.total() == total) {
         return stamp.text();
      } else {
         MediaGlyphs.Stamp stampB2 = stampB;
         if (stampB2 != null && stampB2.total() == total) {
            return stampB2.text();
         } else {
            MediaGlyphs.Stamp stamp2 = new MediaGlyphs.Stamp(total, String.format("%d:%02d", total / 60L, total % 60L));
            if (stampTurn) {
               stampB = stamp2;
            } else {
               stampA = stamp2;
            }

            stampTurn = !stampTurn;
            return stamp2.text();
         }
      }
   }

   public static String ellipsize(Font font, String text, float maxWidth) {
      MediaGlyphs.EllipsisKey ellipsisKey = new MediaGlyphs.EllipsisKey(font.getFont(), font.getSize(), maxWidth, text);
      String str = ELLIPSIZED.get(ellipsisKey);
      if (str != null) {
         return str;
      } else {
         String ellipsis = computeEllipsis(font, text, maxWidth);
         ELLIPSIZED.put(ellipsisKey, ellipsis);
         return ellipsis;
      }
   }

   private static String computeEllipsis(Font font, String text, float maxWidth) {
      if (font.width(text) <= maxWidth) {
         return text;
      } else {
         String str = "...";
         float width = font.width(str);
         int length = text.length();

         while (length > 0 && font.width(text.substring(0, length)) + width > maxWidth) {
            length--;
         }

         return text.substring(0, length).stripTrailing() + str;
      }
   }

   private static final class EllipsisKey {
      private final Object font;
      private final float size;
      private final float maxWidth;
      private final String text;

      public Object font() {
         return this.font;
      }

      public float size() {
         return this.size;
      }

      public float maxWidth() {
         return this.maxWidth;
      }

      public String text() {
         return this.text;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof EllipsisKey other)) return false;
         return java.util.Objects.equals(this.font, other.font) && java.util.Objects.equals(this.size, other.size) && java.util.Objects.equals(this.maxWidth, other.maxWidth) && java.util.Objects.equals(this.text, other.text);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(font, size, maxWidth, text);
      }

      @Override
      public String toString() {
         return "EllipsisKey[" + "font=" + this.font + ", " + "size=" + this.size + ", " + "maxWidth=" + this.maxWidth + ", " + "text=" + this.text + "]";
      }

      private EllipsisKey(Object font, float size, float maxWidth, String text) {
         this.font = font;
         this.size = size;
         this.maxWidth = maxWidth;
         this.text = text;
      }
   }

   private static final class Stamp {
      private final long total;
      private final String text;

      public long total() {
         return this.total;
      }

      public String text() {
         return this.text;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Stamp other)) return false;
         return java.util.Objects.equals(this.total, other.total) && java.util.Objects.equals(this.text, other.text);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(total, text);
      }

      @Override
      public String toString() {
         return "Stamp[" + "total=" + this.total + ", " + "text=" + this.text + "]";
      }

      private Stamp(long total, String text) {
         this.total = total;
         this.text = text;
      }
   }
}
