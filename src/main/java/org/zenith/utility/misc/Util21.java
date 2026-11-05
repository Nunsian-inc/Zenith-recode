package org.zenith.utility.misc;

import org.zenith.utility.game.FlakeState;
import org.zenith.utility.render.Glowboost;

public final class Util21 {
   private static final long CONST_longValue = 501470595897L;
   private static final int CONST_intValue = 2;
   private static final int CONST_intValue2 = 15;

   private Util21() {
   }

   public static void method5356(Glowboost glowboost, FlakeState flakeState2, WeatherFlakeChannel weatherFlakeChannel, float f, float f2, double x, double y, double z) {
      float f3 = weatherFlakeChannel.floatValue27 * f;
      if (!(f3 <= 0.02F)) {
         glowboost.method3235(1.0F);
         byte b = 7;
         int n = (int)Math.floor(x / 2.0);
         int n2 = (int)Math.floor(z / 2.0);
         int n3 = f3 > 0.6F ? 4 : 3;

         for (int i = -b; i <= b; i++) {
            for (int j = -b; j <= b; j++) {
               int n4 = n + i;
               int n5 = n2 + j;
               long longValue = method5358(n4, n5);

               for (int k = 0; k < n3; k++) {
                  double d = Glowboost.method3268(longValue, k, 0) * 2.0F;
                  double d2 = Glowboost.method3268(longValue, k, 1) * 2.0F;
                  double d3 = f2 * (0.16 + 0.22 * Glowboost.method3268(longValue, k, 2));
                  double d4 = n4 * 2.0 + mod(d + weatherFlakeChannel.floatValue20 * d3, 2.0);
                  double d5 = n5 * 2.0 + mod(d2 + weatherFlakeChannel.floatValue21 * d3, 2.0);
                  double d6 = d4 - x;
                  double d7 = d5 - z;
                  double d8 = Math.sqrt(d6 * d6 + d7 * d7);
                  if (!(d8 > 15.0) && (!(d8 > 3.0) || !(d6 * glowboost.getDoubleValue10() + d7 * glowboost.getDoubleValue12() < -0.25 * d8))) {
                     double d9 = flakeState2.method4547(d4, d5);
                     if (d9 != Double.NEGATIVE_INFINITY) {
                        double d10 = d9 + 0.25 + 1.15 * Glowboost.method3268(longValue, k, 3);
                        double d11 = Math.abs(d10 - y);
                        if (!(d11 > 11.0)) {
                           float f4 = (float)Math.min(1.0, (11.0 - d11) / 3.0);
                           float f5 = (float)Math.min(1.0, d8 / 2.6);
                           float f6 = 1.0F - (float)Math.min(1.0, Math.max(0.0, d8 - 15.0 + 4.0) / 4.0);
                           float f7 = 0.55F
                              + 0.45F
                                 * (float)Math.sin(
                                    f2 * (0.25 + 0.35 * Glowboost.method3268(longValue, k, 4)) + Glowboost.method3268(longValue, k, 5) * 6.283F
                                 );
                           float f8 = 0.35F + 0.65F * method5357(d4, d5, f2, weatherFlakeChannel.floatValue20, weatherFlakeChannel.floatValue21);
                           float f9 = f3 * f5 * f6 * f4 * f7 * f8 * 0.13F;
                           if (!(f9 <= 0.002F)) {
                              float f10 = Glowboost.method3268(longValue, k, 6);
                              float f11 = 1.2F + 4.6F * f10 * f10;
                              double d12 = (Glowboost.method3268(longValue, k, 7) - 0.5F) * f11 * 0.7;
                              double d13 = (Glowboost.method3268(longValue, k, 8) - 0.5F) * f11 * 0.7;
                              double d14 = (Glowboost.method3268(longValue, k, 9) - 0.5F) * f11 * 0.35;
                              glowboost.method3251(d4 - d12, d10 - d14, d5 - d13, f11 * 0.78F, weatherFlakeChannel.intValue8, f9);
                              glowboost.method3251(d4 + d12, d10 + d14, d5 + d13, f11 * 1.25F, weatherFlakeChannel.intValue8, f9 * 0.72F);
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private static float method5357(double d, double d2, float f, float f2, float f3) {
      double d3 = d - f2 * f * 0.35;
      double d4 = d2 - f3 * f * 0.35;
      double d5 = Math.sin(d3 * 0.21 + d4 * 0.13) + Math.sin(d3 * 0.07 - d4 * 0.31 + 2.1) + Math.sin((d3 + d4) * 0.045 + f * 0.12);
      return (float)Math.clamp((d5 + 3.0) / 6.0, 0.0, 1.0);
   }

   private static double mod(double d, double d2) {
      double d3 = d % d2;
      return d3 < 0.0 ? d3 + d2 : d3;
   }

   private static long method5358(int n, int n2) {
      return 501470595897L ^ n * -6752110988234923001L ^ n2 * -6939452855193903323L;
   }
}
