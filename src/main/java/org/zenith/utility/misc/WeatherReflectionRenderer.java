package org.zenith.utility.misc;

import org.zenith.utility.game.FlakeState;
import org.zenith.utility.render.Glowboost;

public final class WeatherReflectionRenderer {
   private static final int MAX_RADIUS = 14;
   private static final double SURFACE_OFFSET = 0.008;
   private static final float SPECULAR_EXPONENT = 14.0F;
   private static final float FRESNEL_STRENGTH = 0.2F;
   private static final float SPECULAR_STRENGTH = 0.28F;
   private static final float MAX_REFLECTION_ALPHA = 0.34F;

   private WeatherReflectionRenderer() {
   }

   public static void drawReflections(Glowboost glowboost, FlakeState flakeState, WeatherFlakeChannel weatherFlakeChannel, float f, float f2, double d, double d2, double d3) {
      if (!weatherFlakeChannel.flag) {
         float f3 = weatherFlakeChannel.floatValue23 * f;
         if (!(f3 <= 0.02F)) {
            glowboost.method3235(0.0F);
            int n = (int)Math.floor(d);
            int n2 = (int)Math.floor(d3);
            int n3 = Math.min(14, flakeState.getIntValue3());
            float f4 = vectorLength(weatherFlakeChannel.floatValues4);
            float f5 = weatherFlakeChannel.floatValues4[0] / f4;
            float f6 = weatherFlakeChannel.floatValues4[1] / f4;
            float f7 = weatherFlakeChannel.floatValues4[2] / f4;

            for (int i = -n3; i <= n3; i++) {
               for (int j = -n3; j <= n3; j++) {
                  int n4 = n + i;
                  int n5 = n2 + j;
                  int n6 = n4 - flakeState.getIntValue();
                  int n7 = n5 - flakeState.getIntValue2();
                  double d4 = flakeState.method4544(n6, n7);
                  if (d4 != Double.NEGATIVE_INFINITY && flakeState.method4546(n6, n7) && !(Math.abs(d4 - d2) > 16.0)) {
                     double d5 = Math.sqrt(i * i + j * j);
                     if (!(d5 > n3)) {
                        float f8 = 1.0F - smoothstep(n3 - 3.5F, n3, (float)d5);
                        if (!(f8 <= 0.02F)) {
                           double d6 = d4 + 0.008;
                           drawReflectionCell(glowboost, weatherFlakeChannel, n4, n5, d6, f3, f8, n4 + 0.5 - d, d6 - d2, n5 + 0.5 - d3, f5, f6, f7);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private static void drawReflectionCell(
      Glowboost glowboost,
      WeatherFlakeChannel weatherFlakeChannel,
      int n,
      int n2,
      double d,
      float f,
      float f2,
      double d2,
      double d3,
      double d4,
      float f3,
      float f4,
      float f5
   ) {
      drawGroundQuad(glowboost, n, d, n2, scaleAlpha(weatherFlakeChannel.intValue4, f * f2), false);
      float f6 = reflectionIntensity(weatherFlakeChannel, d2, d3, d4, f3, f4, f5);
      int n3 = withAlpha(weatherFlakeChannel.intValue5, (int)(255.0F * Math.min(0.34F, f6 * f * f2)));
      drawGroundQuad(glowboost, n, d + 0.008, n2, n3, true);
   }

   private static float reflectionIntensity(WeatherFlakeChannel weatherFlakeChannel, double d, double d2, double d3, float f, float f2, float f3) {
      double d4 = Math.sqrt(d * d + d2 * d2 + d3 * d3);
      if (d4 < 1.0E-4) {
         return 0.0F;
      } else {
         double d5 = d / d4;
         double d6 = d2 / d4;
         double d7 = d3 / d4;
         float f4 = fresnelFactor((float)Math.abs(d6));
         float f5 = (float)Math.max(0.0, d5 * f - d6 * f2 + d7 * f3);
         f5 = (float)Math.pow(f5, 14.0);
         float f6 = weatherFlakeChannel.floatValue24 * f4 * 0.2F + f5 * weatherFlakeChannel.floatValue25 * 0.28F;
         float f7 = weatherFlakeChannel.floatValue24 * 0.34F * 0.2F + 0.06F;
         return f7 + (f6 - f7) * Math.clamp(weatherFlakeChannel.floatValue26, 0.0F, 1.0F);
      }
   }

   private static void drawGroundQuad(Glowboost glowboost, double d, double d2, double d3, int n, boolean flag) {
      if (n >>> 24 != 0) {
         if (flag) {
            glowboost.method3238(d, d2, d3, d + 1.0, d2, d3, d + 1.0, d2, d3 + 1.0, d, d2, d3 + 1.0, n);
         } else {
            glowboost.method3237(d, d2, d3, d + 1.0, d2, d3, d + 1.0, d2, d3 + 1.0, d, d2, d3 + 1.0, n);
         }
      }
   }

   private static float fresnelFactor(float f) {
      float f2 = 1.0F - Math.clamp(f, 0.0F, 1.0F);
      float f3 = f2 * f2;
      return 0.02F + 0.98F * f3 * f3 * f2;
   }

   private static int withAlpha(int intValue, int n) {
      return Math.clamp((long)n, 0, 255) << 24 | intValue & 16777215;
   }

   private static int scaleAlpha(int intValue, float f) {
      return withAlpha(intValue, (int)(ColorUtils.method6423(intValue) * Math.clamp(f, 0.0F, 1.0F)));
   }

   private static float vectorLength(float[] floatValues4) {
      float f = (float)Math.sqrt(floatValues4[0] * floatValues4[0] + floatValues4[1] * floatValues4[1] + floatValues4[2] * floatValues4[2]);
      return f < 1.0E-5F ? 1.0F : f;
   }

   private static float smoothstep(float f, float f2, float f3) {
      float f4 = Math.clamp((f3 - f) / Math.max(f2 - f, 1.0E-4F), 0.0F, 1.0F);
      return f4 * f4 * (3.0F - 2.0F * f4);
   }
}
