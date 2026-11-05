package org.zenith.utility.misc;

import net.minecraft.util.Mth;
import org.zenith.utility.render.Glowboost;

public final class Util12 {
   private static final float CONST_floatValue = (float) (Math.PI * 2);
   private static final long CONST_longValue = 25214903917L;
   private static final double CONST_doubleValue = 4096.0;
   private static final int CONST_intValue = 14;
   private static final float CONST_floatValue2 = 0.42F;
   private static final double CONST_doubleValue2 = 3.4;
   private static final double CONST_doubleValue3 = 1.9;

   private Util12() {
   }

   public static void method2340(
      Glowboost glowboost,
      double x,
      double d,
      double z,
      double doubleValue,
      double d2,
      double d3,
      float f,
      int n,
      float f2,
      float f3,
      ColorProvider colorProvider
   ) {
      for (int i = 0; i < n; i++) {
         method2341(glowboost, i, x, d, z, doubleValue, d2, d3, f, f2, f3, colorProvider.color(i * 29));
      }
   }

   private static void method2341(
      Glowboost glowboost,
      int n,
      double x,
      double d,
      double z,
      double doubleValue,
      double d2,
      double d3,
      float f,
      float f2,
      float f3,
      int color
   ) {
      double d4 = method2342(n, 0);
      double d5 = method2342(n, 1);
      double d6 = method2342(n, 2);
      double d7 = method2343(d4, x, f2);
      double d8 = method2343(d5, d, f2);
      double d9 = method2343(d6, z, f2);
      double d10 = d7 - d4;
      double d11 = d8 - d5;
      double d12 = d9 - d6;
      float f4 = method2344(d7 - x, d8 - d, d9 - z, f2);
      if (!(f4 < 0.01F)) {
         float f5 = Glowboost.method3268(25214903917L, n, 11) * 0.4F;
         float f6 = Mth.clamp((f3 - f5) / (1.0F - f5), 0.0F, 1.0F);
         if (!(f6 <= 0.0F)) {
            float f7 = f4 * Glowboost.easeOut(f6);
            float f8 = 1.5F + Glowboost.method3268(25214903917L, n, 3) * 3.4F;
            float f9 = 0.16F + Glowboost.method3268(25214903917L, n, 4) * 0.3F;
            float f10 = 0.14F + Glowboost.method3268(25214903917L, n, 5) * 0.26F;
            float f11 = 0.17F + Glowboost.method3268(25214903917L, n, 6) * 0.32F;
            float f12 = Glowboost.method3268(25214903917L, n, 7) * (float) (Math.PI * 2);
            float f13 = Glowboost.method3268(25214903917L, n, 8) * (float) (Math.PI * 2);
            float f14 = Glowboost.method3268(25214903917L, n, 9) * (float) (Math.PI * 2);
            float f15 = 0.42F * (0.6F + Glowboost.method3268(25214903917L, n, 10) * 0.9F);
            float f16 = 0.72F + 0.28F * Mth.sin(f * 0.7F + n * 1.7F);
            double doubleValue2 = 0.0;
            double d13 = 0.0;
            double d14 = 0.0;
            float f17 = 0.0F;
            boolean flag = false;

            for (int i = 0; i < 14; i++) {
               float f18 = f - i * f15;
               double doubleValue3 = d4 + d10 + f8 * Mth.sin(f9 * f18 + f12);
               double d15 = d5 + d11 + f8 * 0.5 * Mth.sin(f10 * f18 + f13);
               double d16 = d6 + d12 + f8 * Mth.cos(f11 * f18 + f14);
               double d17 = doubleValue3 - doubleValue;
               double d18 = d15 - d2;
               double d19 = d16 - d3;
               double d20 = Math.sqrt(d17 * d17 + d18 * d18 + d19 * d19);
               if (d20 < 3.4 && d20 > 0.001) {
                  double d21 = (3.4 - d20) / 3.4;
                  double d22 = d21 * d21 * 1.9 / d20;
                  doubleValue3 += d17 * d22;
                  d15 += d18 * d22;
                  d16 += d19 * d22;
               }

               float f19 = 1.0F - i / 14.0F;
               float f20 = f7 * f16 * f19 * f19;
               if (flag && f20 > 0.01F && f17 > 0.01F) {
                  glowboost.method3236(doubleValue2, d13, d14, doubleValue3, d15, d16, ColorUtils.method6439(color, Math.min(f20, f17)));
               }

               if (i % 2 == 0 && f20 > 0.02F) {
                  glowboost.method3250(doubleValue3, d15, d16, 0.055F + 0.075F * f19, color, f20 * 0.8F);
               }

               doubleValue2 = doubleValue3;
               d13 = d15;
               d14 = d16;
               f17 = f20;
               flag = true;
            }
         }
      }
   }

   private static double method2342(int n, int n2) {
      return Glowboost.method3268(25214903917L, n, n2) * 4096.0;
   }

   private static double method2343(double d, double x, double d2) {
      double d3 = d2 * 2.0;
      double d4 = d - x;
      d4 -= Math.floor((d4 + d2) / d3) * d3;
      return x + d4;
   }

   private static float method2344(double d, double d2, double d3, double d4) {
      return Math.min(method2345(d, d4), Math.min(method2345(d2, d4), method2345(d3, d4)));
   }

   private static float method2345(double d, double d2) {
      double d3 = Math.abs(d);
      double d4 = d2 * 0.72;
      return d3 <= d4 ? 1.0F : (float)Math.max(0.0, 1.0 - (d3 - d4) / (d2 - d4));
   }
}
