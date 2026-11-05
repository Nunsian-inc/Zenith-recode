package org.zenith.utility.misc;

import org.zenith.utility.render.Glowboost;

public final class SnowVisibility {
   private static final float[] CONST_floatValues = new float[]{0.42F, 0.84F, 0.34F};
   private static boolean visible;
   private static final FloatArrayBuffer floatArrayBuffer = new FloatArrayBuffer();
   private static int intValue;
   private static int intValue2;
   private static int intValue3;

   private SnowVisibility() {
   }

   public static boolean isVisible() {
      return visible && !floatArrayBuffer.isEmpty();
   }

   public static void setVisible(boolean flag) {
      visible = flag;
   }

   public static FloatArrayBuffer getFloatArrayBuffer() {
      return floatArrayBuffer;
   }

   public static void method2401(int n, int n2, int n3) {
      intValue = n;
      intValue2 = n2;
      intValue3 = n3;
   }

   public static void method2402(Glowboost glowboost) {
      if (isVisible()) {
         glowboost.method3235(0.0F);
         float f = 0.25F;

         for (int i = 0; i < floatArrayBuffer.getIntValue(); i++) {
            int n = method2403(floatArrayBuffer.method383(i, 0), floatArrayBuffer.method384(i, 0), floatArrayBuffer.method385(i, 0));
            int n2 = method2403(floatArrayBuffer.method383(i, 1), floatArrayBuffer.method384(i, 1), floatArrayBuffer.method385(i, 1));
            int n3 = method2403(floatArrayBuffer.method383(i, 2), floatArrayBuffer.method384(i, 2), floatArrayBuffer.method385(i, 2));
            int n4 = method2403(floatArrayBuffer.method383(i, 3), floatArrayBuffer.method384(i, 3), floatArrayBuffer.method385(i, 3));
            glowboost.method3239(
               floatArrayBuffer.method380(i, 0) * f,
               floatArrayBuffer.method381(i, 0) * f,
               floatArrayBuffer.method382(i, 0) * f,
               floatArrayBuffer.method380(i, 1) * f,
               floatArrayBuffer.method381(i, 1) * f,
               floatArrayBuffer.method382(i, 1) * f,
               floatArrayBuffer.method380(i, 2) * f,
               floatArrayBuffer.method381(i, 2) * f,
               floatArrayBuffer.method382(i, 2) * f,
               floatArrayBuffer.method380(i, 3) * f,
               floatArrayBuffer.method381(i, 3) * f,
               floatArrayBuffer.method382(i, 3) * f,
               n,
               n2,
               n3,
               n4
            );
         }
      }
   }

   private static int method2403(float f, float f2, float f3) {
      float f4 = Math.max(0.0F, f * CONST_floatValues[0] + f2 * CONST_floatValues[1] + f3 * CONST_floatValues[2]);
      float f5 = 0.42F + 0.58F * f4;
      int n = Math.clamp((long)((int)(f5 * 255.0F + 0.5F)), 0, 255);
      return 0xFF000000 | n << 16 | n << 8 | Math.min(255, n + 8);
   }
}
