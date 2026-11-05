package org.zenith.utility.misc;

import net.minecraft.util.Mth;
import org.zenith.utility.game.Class28;
import org.zenith.utility.render.Glowboost;

public final class Util24 {
   public static final int CONST_intValue = 0;
   public static final int CONST_intValue2 = 1;
   public static final int CONST_intValue3 = 2;
   private static final float CONST_floatValue = (float) (Math.PI * 2);
   private static final int CONST_intValue4 = 3;

   private Util24() {
   }

   public static void method6028(Glowboost glowboost, Class28 class2, float f, int color, float f2, float f3, float f4) {
      int n = Math.max(8, (int)(f3 * 0.6F));
      float f5 = f * class2.intValue2 / 1000.0F;
      float f6 = Glowboost.method3266(f);
      if (!(f6 < 0.01F)) {
         double d = class2.doubleValue15 + class2.doubleValue14 * 0.5;

         for (int i = 0; i < n; i++) {
            float f7 = Glowboost.method3268(class2.longValue2, i, 0) * (float) (Math.PI * 2);
            float f8 = -0.25F + Glowboost.method3268(class2.longValue2, i, 1) * 1.45F;
            float f9 = Mth.cos(f8);
            double d2 = Mth.cos(f7) * f9;
            double d3 = Mth.sin(f8);
            double d4 = Mth.sin(f7) * f9;
            float f10 = (1.6F + Glowboost.method3268(class2.longValue2, i, 2) * 3.2F) * f4;
            float f11 = f10 * (1.0F - (float)Math.exp(-2.8F * f5)) / 2.8F;
            double doubleValue = class2.doubleValue + d2 * f11;
            double d5 = d + d3 * f11 - 1.4 * f5 * f5;
            double d6 = class2.doubleValue3 + d4 * f11;
            float f12 = (0.14F + Glowboost.method3268(class2.longValue2, i, 3) * 0.2F) * f2;
            float f13 = f12 * 0.3F;
            double d7 = doubleValue - glowboost.getDoubleValue();
            double d8 = d5 - glowboost.getDoubleValue2();
            double d9 = d6 - glowboost.getDoubleValue3();
            double d10 = d3 * d9 - d4 * d8;
            double d11 = d4 * d7 - d2 * d9;
            double d12 = d2 * d8 - d3 * d7;
            double d13 = Math.sqrt(d10 * d10 + d11 * d11 + d12 * d12);
            if (d13 < 1.0E-6) {
               d10 = glowboost.getDoubleValue4();
               d11 = glowboost.getDoubleValue5();
               d12 = glowboost.getDoubleValue6();
               d13 = 1.0;
            }

            d10 = d10 / d13 * f13;
            d11 = d11 / d13 * f13;
            d12 = d12 / d13 * f13;
            float alpha = f6 * (0.55F + 0.45F * Glowboost.method3268(class2.longValue2, i, 4));
            int color2 = ColorUtils.method6439(color, alpha);
            double doubleValue2 = doubleValue + d2 * f12 * 0.6;
            double d14 = d5 + d3 * f12 * 0.6;
            double d15 = d6 + d4 * f12 * 0.6;
            double doubleValue3 = doubleValue - d2 * f12 * 0.4;
            double d16 = d5 - d3 * f12 * 0.4;
            double d17 = d6 - d4 * f12 * 0.4;
            glowboost.method3236(doubleValue2, d14, d15, doubleValue + d10, d5 + d11, d6 + d12, color2);
            glowboost.method3236(doubleValue + d10, d5 + d11, d6 + d12, doubleValue3, d16, d17, color2);
            glowboost.method3236(doubleValue3, d16, d17, doubleValue - d10, d5 - d11, d6 - d12, color2);
            glowboost.method3236(doubleValue - d10, d5 - d11, d6 - d12, doubleValue2, d14, d15, color2);
            glowboost.method3250(doubleValue, d5, d6, f12 * 2.0F, color, alpha * 0.5F);
         }

         float f7 = 1.0F - Math.min(1.0F, f * 2.6F);
         if (f7 > 0.01F) {
            glowboost.method3250(class2.doubleValue, d, class2.doubleValue3, 0.6F * f2 * (0.4F + f7), color, f7 * 0.8F);
            glowboost.method3250(class2.doubleValue, d, class2.doubleValue3, 1.5F * f2 * (0.5F + f7), color, f7 * 0.28F);
         }
      }
   }

   public static void method6029(Glowboost glowboost, Class28 class2, float f, int color, float f2, float f3) {
      float f4 = Glowboost.method3267(f, 0.08F, 0.5F);
      if (!(f4 < 0.01F)) {
         int n = 28 + (int)(f3 * 0.3F);
         double d = (class2.doubleValue13 * 0.9 + 0.25) * f2;
         double d2 = class2.doubleValue14 + 0.6;

         for (int i = 0; i < 3; i++) {
            float f5 = i * 0.14F;
            if (!(f <= f5)) {
               float f6 = (f - f5) / (1.0F - f5);
               if (!(f6 >= 1.0F)) {
                  float alpha = f4 * (1.0F - f6) * (i == 0 ? 1.0F : 0.7F);
                  if (!(alpha < 0.02F)) {
                     int color2 = ColorUtils.method6439(color, alpha);
                     double d3 = class2.doubleValue15 + Glowboost.easeOut(f6) * d2;
                     double d4 = d * (0.4 + 0.9 * Glowboost.easeOut(f6));
                     float f7 = f * 2.2F + i * 1.3F + class2.floatValue;
                     double doubleValue = 0.0;
                     double d5 = 0.0;

                     for (int j = 0; j <= n; j++) {
                        float f8 = (float)j / n * (float) (Math.PI * 2) + f7;
                        double d6 = d4 * (1.0F + 0.06F * Mth.sin(f8 * 4.0F + i + class2.floatValue));
                        double doubleValue2 = class2.doubleValue + Mth.cos(f8) * d6;
                        double d7 = class2.doubleValue3 + Mth.sin(f8) * d6;
                        if (j > 0) {
                           glowboost.method3236(doubleValue, d3, d5, doubleValue2, d3, d7, color2);
                        }

                        if (j % 6 == 0) {
                           glowboost.method3250(doubleValue2, d3, d7, 0.1F * f2, color, alpha * 0.5F);
                        }

                        doubleValue = doubleValue2;
                        d5 = d7;
                     }
                  }
               }
            }
         }

         int n2 = Math.max(6, (int)(f3 * 0.3F));

         for (int i = 0; i < n2; i++) {
            float f5 = Glowboost.method3268(class2.longValue2, i, 0);
            float f6 = Mth.clamp(f * 1.5F + f5 * 0.4F, 0.0F, 1.0F);
            float f7 = f5 * (float) (Math.PI * 2) + f * 5.5F * (Glowboost.method3268(class2.longValue2, i, 1) > 0.5F ? 1.0F : -1.0F);
            double d3 = d * (0.55 + 0.5 * Glowboost.method3268(class2.longValue2, i, 2)) * (0.5 + 0.5 * f6);
            double doubleValue = class2.doubleValue + Mth.cos(f7) * d3;
            double d4 = class2.doubleValue3 + Mth.sin(f7) * d3;
            double d5 = class2.doubleValue15 + f6 * d2 * (0.6 + 0.5 * Glowboost.method3268(class2.longValue2, i, 3));
            glowboost.method3250(doubleValue, d5, d4, 0.09F * f2, color, f4 * 0.7F);
         }
      }
   }

   public static void method6030(Glowboost glowboost, Class28 class2, float f, int color, float f2, float f3, float f4) {
      int n = Math.max(8, (int)(f3 * 0.6F));
      float f5 = f * class2.intValue2 / 1000.0F;
      float f6 = Glowboost.method3267(f, 0.08F, 0.5F);
      if (!(f6 < 0.01F)) {
         float f7 = Glowboost.method3268(class2.longValue2, 0, 77) > 0.5F ? 1.0F : -1.0F;
         double d = class2.doubleValue14 + 0.9;
         double d2 = (class2.doubleValue13 * 0.55 + 0.18) * f2;

         for (int i = 0; i < n; i++) {
            float f8 = Glowboost.method3268(class2.longValue2, i, 0) * (float) (Math.PI * 2);
            float f9 = Glowboost.method3268(class2.longValue2, i, 1);
            float f10 = Glowboost.method3268(class2.longValue2, i, 2) * 0.35F;
            float f11 = Mth.clamp((f - f10) / Math.max(0.15F, 0.75F - f10 * 0.5F), 0.0F, 1.0F);
            if (!(f11 <= 0.0F)) {
               double d3 = d2 * (0.35 + Glowboost.easeOut(f11) * (0.9 + f9 * 2.4));
               double d4 = f7 * f4 * 5.4 / (0.6 + d3);
               double d5 = f8 + d4 * f5;
               double d6 = class2.doubleValue15 + d * (0.08 + Glowboost.easeOut(f11) * (0.35 + f9 * 0.85));
               double d7 = Math.cos(d5);
               double d8 = Math.sin(d5);
               double doubleValue = class2.doubleValue + d7 * d3;
               double d9 = class2.doubleValue3 + d8 * d3;
               float alpha = f6 * f11 * (0.55F + 0.45F * Glowboost.method3268(class2.longValue2, i, 3));
               if (!(alpha < 0.02F)) {
                  int color2 = ColorUtils.method6439(color, alpha);
                  double d10 = -d8 * f7 + d7 * 0.45;
                  double d11 = d7 * f7 + d8 * 0.45;
                  double d12 = 0.5 + f9;
                  double d13 = Math.sqrt(d10 * d10 + d12 * d12 + d11 * d11);
                  d10 /= d13;
                  d12 /= d13;
                  d11 /= d13;
                  float f12 = (0.14F + Glowboost.method3268(class2.longValue2, i, 4) * 0.2F) * f2;
                  float f13 = f12 * 0.3F;
                  double d14 = doubleValue - glowboost.getDoubleValue();
                  double d15 = d6 - glowboost.getDoubleValue2();
                  double d16 = d9 - glowboost.getDoubleValue3();
                  double d17 = d12 * d16 - d11 * d15;
                  double d18 = d11 * d14 - d10 * d16;
                  double d19 = d10 * d15 - d12 * d14;
                  double d20 = Math.sqrt(d17 * d17 + d18 * d18 + d19 * d19);
                  if (d20 < 1.0E-6) {
                     d17 = glowboost.getDoubleValue4();
                     d18 = glowboost.getDoubleValue5();
                     d19 = glowboost.getDoubleValue6();
                     d20 = 1.0;
                  }

                  d17 = d17 / d20 * f13;
                  d18 = d18 / d20 * f13;
                  d19 = d19 / d20 * f13;
                  double doubleValue2 = doubleValue + d10 * f12 * 0.6;
                  double d21 = d6 + d12 * f12 * 0.6;
                  double d22 = d9 + d11 * f12 * 0.6;
                  double doubleValue3 = doubleValue - d10 * f12 * 0.4;
                  double d23 = d6 - d12 * f12 * 0.4;
                  double d24 = d9 - d11 * f12 * 0.4;
                  glowboost.method3236(doubleValue2, d21, d22, doubleValue + d17, d6 + d18, d9 + d19, color2);
                  glowboost.method3236(doubleValue + d17, d6 + d18, d9 + d19, doubleValue3, d23, d24, color2);
                  glowboost.method3236(doubleValue3, d23, d24, doubleValue - d17, d6 - d18, d9 - d19, color2);
                  glowboost.method3236(doubleValue - d17, d6 - d18, d9 - d19, doubleValue2, d21, d22, color2);
                  glowboost.method3250(doubleValue, d6, d9, f12 * 2.0F, color, alpha * 0.5F);
                  double doubleValue4 = doubleValue;
                  double d25 = d6;
                  double d26 = d9;

                  for (int j = 1; j <= 3; j++) {
                     double d27 = d5 - d4 * 0.055 * j;
                     double d28 = class2.doubleValue + Math.cos(d27) * d3;
                     double d29 = class2.doubleValue3 + Math.sin(d27) * d3;
                     double d30 = d6 - d * 0.012 * j;
                     float alpha2 = alpha * (1.0F - j / 4.0F) * 0.6F;
                     glowboost.method3236(doubleValue4, d25, d26, d28, d30, d29, ColorUtils.method6439(color, alpha2));
                     doubleValue4 = d28;
                     d25 = d30;
                     d26 = d29;
                  }
               }
            }
         }
      }
   }
}
