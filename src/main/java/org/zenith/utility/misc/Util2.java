package org.zenith.utility.misc;

import java.awt.Color;

public final class Util2 {
   public static final int CONST_intValue = -1;
   public static final int CONST_intValue2 = -16777216;
   public static final int CONST_intValue3 = -16087809;
   public static final int CONST_intValue4 = -266593250;
   public static final int CONST_intValue5 = -1;
   public static final int CONST_intValue6 = -1712591883;

   private Util2() {
   }

   public static int method809(int n, int n2, int n3, int n4) {
      return (n4 & 0xFF) << 24 | (n & 0xFF) << 16 | (n2 & 0xFF) << 8 | n3 & 0xFF;
   }

   public static int method810(int n) {
      return n >> 24 & 0xFF;
   }

   public static int method811(int n, int n2) {
      return Math.clamp((long)n2, 0, 255) << 24 | n & 16777215;
   }

   public static int method812(int n, float f) {
      return method811(n, Math.round(method810(n) * Math.clamp(f, 0.0F, 1.0F)));
   }

   public static int method813(int n, int n2, float f) {
      float f2 = Math.clamp(f, 0.0F, 1.0F);
      int n3 = Math.round(method810(n) + (method810(n2) - method810(n)) * f2);
      int n4 = Math.round((n >> 16 & 0xFF) + ((n2 >> 16 & 0xFF) - (n >> 16 & 0xFF)) * f2);
      int n5 = Math.round((n >> 8 & 0xFF) + ((n2 >> 8 & 0xFF) - (n >> 8 & 0xFF)) * f2);
      int n6 = Math.round((n & 0xFF) + ((n2 & 0xFF) - (n & 0xFF)) * f2);
      return method809(n4, n5, n6, n3);
   }

   public static int method814(int n, float saturation, float brightness) {
      return method815(System.currentTimeMillis(), n, saturation, brightness);
   }

   public static int method815(long now, int n, float saturation, float brightness) {
      float hue = (float)((now + n) % 4000L) / 4000.0F;
      return 0xFF000000 | Color.HSBtoRGB(hue, saturation, brightness) & 16777215;
   }
}
