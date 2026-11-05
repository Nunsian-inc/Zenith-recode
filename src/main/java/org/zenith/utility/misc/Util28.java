package org.zenith.utility.misc;

import net.minecraft.util.Mth;
import org.zenith.utility.game.Class28;
import org.zenith.utility.render.Glowboost;

public final class Util28 {
   public static final int CONST_intValue = 0;
   public static final int CONST_intValue2 = 1;
   public static final int CONST_intValue3 = 2;
   private static final float CONST_floatValue = (float) (Math.PI * 2);

   private Util28() {
   }

   public static void method7552(Glowboost glowboost, Class28 class2, float f, int color, float f2, float f3) {
      float f4 = (float)(class2.doubleValue13 * 0.8 + 0.45) * f2;
      int n = 26 + (int)(f3 * 0.42F);
      double d = glowboost.getDoubleValue4();
      double d2 = glowboost.getDoubleValue5();
      double d3 = glowboost.getDoubleValue6();
      double d4 = glowboost.getDoubleValue7();
      double d5 = glowboost.getDoubleValue8();
      double d6 = glowboost.getDoubleValue9();

      for (int i = 0; i < 3; i++) {
         float f5 = i * 0.12F;
         if (!(f <= f5)) {
            float f6 = (f - f5) / (1.0F - f5);
            if (!(f6 >= 1.0F)) {
               float alpha = (1.0F - f6) * (1.0F - f6) * (f6 < 0.1F ? f6 / 0.1F : 1.0F) * (i == 0 ? 1.0F : 0.6F);
               if (!(alpha < 0.02F)) {
                  int color2 = ColorUtils.method6439(color, alpha);
                  float f7 = f4 * (0.12F + Glowboost.easeOut(f6) * (1.0F + i * 0.42F));
                  double doubleValue = 0.0;
                  double d7 = 0.0;
                  double d8 = 0.0;

                  for (int j = 0; j <= n; j++) {
                     float f8 = (float)j / n * (float) (Math.PI * 2) + i * 0.4F;
                     double d9 = f7 * (1.0F + 0.05F * Mth.sin(f8 * 5.0F + class2.floatValue + i));
                     float f9 = Mth.cos(f8);
                     float f10 = Mth.sin(f8);
                     double doubleValue2 = class2.doubleValue + (d * f9 + d4 * f10) * d9;
                     double d10 = class2.doubleValue2 + (d2 * f9 + d5 * f10) * d9;
                     double d11 = class2.doubleValue3 + (d3 * f9 + d6 * f10) * d9;
                     if (j > 0) {
                        glowboost.method3236(doubleValue, d7, d8, doubleValue2, d10, d11, color2);
                     }

                     if (i == 0 && j % 5 == 0) {
                        glowboost.method3250(doubleValue2, d10, d11, 0.1F * f2, color, alpha * 0.5F);
                     }

                     doubleValue = doubleValue2;
                     d7 = d10;
                     d8 = d11;
                  }
               }
            }
         }
      }

      float f5 = 1.0F - Math.min(1.0F, f * 3.2F);
      if (f5 > 0.01F) {
         glowboost.method3250(class2.doubleValue, class2.doubleValue2, class2.doubleValue3, 0.5F * f2 * (0.4F + f5), color, f5 * 0.85F);
         glowboost.method3250(class2.doubleValue, class2.doubleValue2, class2.doubleValue3, 1.2F * f2 * (0.5F + f5), color, f5 * 0.3F);
      }

      float f6 = Math.min(1.0F, f * 1.5F);
      float alpha = (1.0F - f6) * (1.0F - f6);
      if (alpha > 0.02F) {
         int color2 = ColorUtils.method6439(color, alpha);
         float f7 = f4 * (0.5F + Glowboost.easeOut(f6) * 1.6F);

         for (int i = 0; i < 4; i++) {
            float f8 = i / 4.0F * (float) (Math.PI * 2) + 0.39F + class2.floatValue;
            float f9 = Mth.cos(f8);
            float f10 = Mth.sin(f8);
            double d7 = d * f9 + d4 * f10;
            double d8 = d2 * f9 + d5 * f10;
            double d9 = d3 * f9 + d6 * f10;
            glowboost.method3236(
               class2.doubleValue + d7 * f7 * 0.3,
               class2.doubleValue2 + d8 * f7 * 0.3,
               class2.doubleValue3 + d9 * f7 * 0.3,
               class2.doubleValue + d7 * f7,
               class2.doubleValue2 + d8 * f7,
               class2.doubleValue3 + d9 * f7,
               color2
            );
         }
      }
   }

   public static void method7553(Glowboost glowboost, Class28 class2, float f, int color, float floatValue, float f2, float f3) {
      int n = Math.max(5, (int)(f3 * 0.55F));
      float f4 = Glowboost.method3266(f);
      if (!(f4 < 0.01F)) {
         float time = f * class2.intValue2 / 1000.0F;
         Class42 class3 = class2.method4654(1, n, floatValue);
         class3.method7382(time);

         for (int i = 0; i < n; i++) {
            double d = class3.method7392(i);
            double d2 = class3.method7393(i);
            double d3 = class3.method7394(i);
            double doubleValue = class3.method7385(i);
            double d4 = class3.method7386(i);
            double d5 = class3.method7387(i);
            float f5 = (0.16F + Glowboost.method3268(class2.longValue2, i, 3) * 0.24F) * f2;
            float f6 = f5 * 0.3F;
            double d6 = -d3;
            double d7 = Math.sqrt(d6 * d6 + d * d);
            double d8;
            if (d7 < 1.0E-6) {
               d6 = 1.0;
               d8 = 0.0;
            } else {
               d6 /= d7;
               d8 = d / d7;
            }

            double d9 = d2 * d8;
            double d10 = d3 * d6 - d * d8;
            double d11 = -d2 * d6;
            float f7 = Glowboost.method3268(class2.longValue2, i, 5) * (float) (Math.PI * 2)
               + time * (3.0F + Glowboost.method3268(class2.longValue2, i, 8) * 7.0F);
            f7 = method7554(glowboost, doubleValue, d4, d5, d, d2, d3, d6, d9, d10, d11, d8, f7);
            float f8 = Mth.cos(f7) * f6;
            float f9 = Mth.sin(f7) * f6;
            double d12 = d6 * f8 + d9 * f9;
            double d13 = d10 * f9;
            double d14 = d8 * f8 + d11 * f9;
            double d15 = d9 * f8 - d6 * f9;
            double d16 = d10 * f8;
            double d17 = d11 * f8 - d8 * f9;
            float alpha = f4 * (0.55F + 0.45F * Glowboost.method3268(class2.longValue2, i, 6));
            int color2 = ColorUtils.method6439(color, alpha);
            int color3 = ColorUtils.method6439(color, alpha * 0.7F);
            double doubleValue2 = doubleValue + d * f5 * 0.62;
            double d18 = d4 + d2 * f5 * 0.62;
            double d19 = d5 + d3 * f5 * 0.62;
            double d20 = doubleValue - d * f5 * 0.38;
            double d21 = d4 - d2 * f5 * 0.38;
            double d22 = d5 - d3 * f5 * 0.38;
            method7555(glowboost, doubleValue2, d18, d19, d20, d21, d22, doubleValue, d4, d5, d12, d13, d14, color2);
            method7555(glowboost, doubleValue2, d18, d19, d20, d21, d22, doubleValue, d4, d5, d15, d16, d17, color3);
            glowboost.method3250(doubleValue, d4, d5, f5 * 2.1F, color, alpha * (0.5F + 0.35F * class3.method7398(i)));
         }
      }
   }

   private static float method7554(
      Glowboost glowboost,
      double doubleValue,
      double d,
      double d2,
      double d3,
      double d4,
      double d5,
      double d6,
      double d7,
      double d8,
      double d9,
      double d10,
      float f
   ) {
      double d11 = glowboost.getDoubleValue() - doubleValue;
      double d12 = glowboost.getDoubleValue2() - d;
      double d13 = glowboost.getDoubleValue3() - d2;
      double d14 = d4 * d13 - d5 * d12;
      double d15 = d5 * d11 - d3 * d13;
      double d16 = d3 * d12 - d4 * d11;
      double d17 = Math.sqrt(d14 * d14 + d15 * d15 + d16 * d16);
      if (d17 < 1.0E-6) {
         return f;
      } else {
         d14 /= d17;
         d15 /= d17;
         d16 /= d17;
         float f2 = (float)Math.atan2(d14 * d7 + d15 * d8 + d16 * d9, d14 * d6 + d16 * d10);
         float f3 = f - f2;
         f3 -= (float)(Math.PI * Math.floor(f3 / Math.PI + 0.5));
         return f2 + f3 * 0.45F;
      }
   }

   private static void method7555(
      Glowboost glowboost,
      double doubleValue,
      double d,
      double d2,
      double doubleValue2,
      double d3,
      double d4,
      double doubleValue3,
      double d5,
      double d6,
      double d7,
      double d8,
      double d9,
      int color
   ) {
      glowboost.method3236(doubleValue, d, d2, doubleValue3 + d7, d5 + d8, d6 + d9, color);
      glowboost.method3236(doubleValue3 + d7, d5 + d8, d6 + d9, doubleValue2, d3, d4, color);
      glowboost.method3236(doubleValue2, d3, d4, doubleValue3 - d7, d5 - d8, d6 - d9, color);
      glowboost.method3236(doubleValue3 - d7, d5 - d8, d6 - d9, doubleValue, d, d2, color);
   }

   public static void method7556(Glowboost glowboost, Class28 class2, float f, int color, float floatValue, float f2, float f3) {
      int n = Math.max(6, (int)f3);
      float time = f * class2.intValue2 / 1000.0F;
      float f4 = Glowboost.method3266(f);
      if (!(f4 < 0.01F)) {
         Class42 class3 = class2.method4654(2, n, floatValue);
         class3.method7382(time);

         for (int i = 0; i < n; i++) {
            double doubleValue = class3.method7385(i);
            double d = class3.method7386(i);
            double d2 = class3.method7387(i);
            double d3 = class3.method7391(i);
            double d4 = class3.method7395(i);
            double d5 = class3.method7396(i);
            double d6 = class3.method7397(i);
            float f5 = 0.65F + 0.35F * Mth.sin(i * 12.9F + time * 55.0F);
            float alpha = f4 * f5 * (0.6F + 0.4F * Glowboost.method3268(class2.longValue2, i, 3));
            alpha = Math.min(1.0F, alpha * (1.0F + class3.method7398(i) * 0.8F));
            if (!(alpha < 0.02F)) {
               double d7 = Math.min(0.55, d3 * 0.05) * f2 * 1.6;
               double d8 = Math.abs(d4 * glowboost.getDoubleValue10() + d5 * glowboost.getDoubleValue11() + d6 * glowboost.getDoubleValue12());
               double d9 = Math.sqrt(Math.max(0.0, 1.0 - d8 * d8));
               double d10 = d7 * (0.35 + 0.65 * d9);
               int color2 = ColorUtils.method6439(color, alpha);
               if (d10 > 1.0E-4) {
                  glowboost.method3236(doubleValue, d, d2, doubleValue - d4 * d10, d - d5 * d10, d2 - d6 * d10, color2);
               }

               glowboost.method3250(doubleValue, d, d2, 0.09F * f2 * (0.7F + 0.5F * Glowboost.method3268(class2.longValue2, i, 4)), color, alpha * 0.65F);
            }
         }
      }
   }
}
