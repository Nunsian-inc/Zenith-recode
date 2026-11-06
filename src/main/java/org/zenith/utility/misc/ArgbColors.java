package org.zenith.utility.misc;

import java.awt.Color;

public final class ArgbColors {
   public static final int CONST_intValue = -1;
   public static final int CONST_intValue2 = -16777216;
   public static final int CONST_intValue3 = -16087809;
   public static final int CONST_intValue4 = -266593250;
   public static final int CONST_intValue5 = -1;
   public static final int CONST_intValue6 = -1712591883;

   private ArgbColors() {
   }

   public static int packArgb(int red, int green, int blue, int alpha) {
      return (alpha & 0xFF) << 24 | (red & 0xFF) << 16 | (green & 0xFF) << 8 | blue & 0xFF;
   }

   public static int getAlpha(int color) {
      return color >> 24 & 0xFF;
   }

   public static int withAlpha(int color, int alpha) {
      return Math.clamp((long)alpha, 0, 255) << 24 | color & 16777215;
   }

   public static int multiplyAlpha(int color, float opacity) {
      return withAlpha(color, Math.round(getAlpha(color) * Math.clamp(opacity, 0.0F, 1.0F)));
   }

   public static int lerpArgb(int startColor, int endColor, float progress) {
      float clampedProgress = Math.clamp(progress, 0.0F, 1.0F);
      int alpha = Math.round(getAlpha(startColor) + (getAlpha(endColor) - getAlpha(startColor)) * clampedProgress);
      int red = Math.round((startColor >> 16 & 0xFF) + ((endColor >> 16 & 0xFF) - (startColor >> 16 & 0xFF)) * clampedProgress);
      int green = Math.round((startColor >> 8 & 0xFF) + ((endColor >> 8 & 0xFF) - (startColor >> 8 & 0xFF)) * clampedProgress);
      int blue = Math.round((startColor & 0xFF) + ((endColor & 0xFF) - (startColor & 0xFF)) * clampedProgress);
      return packArgb(red, green, blue, alpha);
   }

   public static int rainbow(int offsetMillis, float saturation, float brightness) {
      return rainbowAt(System.currentTimeMillis(), offsetMillis, saturation, brightness);
   }

   public static int rainbowAt(long now, int offsetMillis, float saturation, float brightness) {
      float hue = (float)((now + offsetMillis) % 4000L) / 4000.0F;
      return 0xFF000000 | Color.HSBtoRGB(hue, saturation, brightness) & 16777215;
   }
}
