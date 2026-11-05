package org.zenith.utility.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffects;
import org.joml.Vector4f;
import org.zenith.Zenith;
import org.zenith.utility.game.FlakeState;
import org.zenith.utility.misc.ColorUtils;
import org.zenith.utility.misc.FrustumHolder;
import org.zenith.utility.misc.Util15;
import org.zenith.utility.misc.WeatherFlakeChannel;

public final class RippleUtil {
   private static final Identifier identifier = Zenith.method1501("visuals/weather/drop.png");
   private static final Identifier identifier2 = Zenith.method1501("visuals/weather/flake.png");
   private static final long CONST_longValue = 374227287185L;
   private static final int CONST_intValue = 22;
   private static final float CONST_floatValue = 7.0F;
   private static final float CONST_floatValue2 = 11.0F;
   private static final float CONST_floatValue3 = 5.0F;
   private static final float CONST_floatValue4 = 6.0F;
   private static final Identifier identifier3 = Zenith.method1501("visuals/weather/ripple.png");
   private static final float CONST_floatValue5 = 0.075F;
   private static final float CONST_floatValue6 = 1.4F;
   private static final float CONST_floatValue7 = 0.42F;
   private static final float CONST_floatValue8 = 9.5F;
   private static final float CONST_floatValue9 = 1.5F;
   private static final float CONST_floatValue10 = 3.2F;
   private static final float CONST_floatValue11 = 3.2F;
   private static final float CONST_floatValue12 = 1.6F;
   private static final float CONST_floatValue13 = 1.1F;
   private static final float CONST_floatValue14 = 0.5F;
   private static final int CONST_intValue2 = 3;
   private static final int CONST_intValue3 = 3;
   private static final float CONST_floatValue15 = 0.085F;
   private static final float CONST_floatValue16 = 0.17F;
   private static final float CONST_floatValue17 = 0.032F;
   private static final float CONST_floatValue18 = 0.046F;
   private static final float CONST_floatValue19 = 2.5F;
   private static final double CONST_doubleValue = Math.toRadians(18.0);
   private static final double CONST_doubleValue2 = 0.25;
   private static final double CONST_doubleValue3 = Math.cos(Math.toRadians(1.0));
   private static boolean flag;
   private static double doubleValue;
   private static double doubleValue2;
   private static double doubleValue3;
   private static double doubleValue4;
   private static double doubleValue5;
   private static double doubleValue6;
   private static double doubleValue7;

   private RippleUtil() {
   }

   public static void method7826(Glowboost glowboost, FlakeState flakeState2, WeatherFlakeChannel weatherFlakeChannel, float f, float f2, double x, double y, double z) {
      method7828(glowboost);
      method7830(glowboost, flakeState2, weatherFlakeChannel, f, f2, x, y, z, 11.0F, 5.0F, 6.0F, 22, 7.0F);
   }

   public static void method7827(Glowboost glowboost, FlakeState flakeState2, WeatherFlakeChannel weatherFlakeChannel, float f, float f2, double x, double y, double z) {
      flag = false;
      method7830(glowboost, flakeState2, weatherFlakeChannel, f, f2, x, y, z, 5.5F, 3.4F, 2.0F, 5, 1.0E9F);
   }

   private static void method7828(Glowboost glowboost) {
      flag = false;
      Minecraft minecraft = Minecraft.getInstance();
      Frustum frustum = FrustumHolder.getFrustum();
      LocalPlayer player = minecraft.player;
      if (frustum != null && player != null) {
         if (!(player.portalEffectIntensity > 0.0F) && !(player.oPortalEffectIntensity > 0.0F) && !player.hasEffect(MobEffects.NAUSEA)) {
            double d = glowboost.getDoubleValue10();
            double d2 = glowboost.getDoubleValue11();
            double d3 = glowboost.getDoubleValue12();
            double d4 = 1.0;
            double d5 = 0.0;
            double d6 = 0.0;
            double d7 = 0.0;

            for (Vector4f frustumPoint : frustum.getFrustumPoints()) {
               double d8 = Math.sqrt(frustumPoint.x * frustumPoint.x + frustumPoint.y * frustumPoint.y + frustumPoint.z * frustumPoint.z);
               if (!(d8 > 1.0E-6) || !Double.isFinite(d8)) {
                  return;
               }

               double d9 = frustumPoint.x / d8;
               double d10 = frustumPoint.y / d8;
               double d11 = frustumPoint.z / d8;
               d4 = Math.min(d4, d9 * d + d10 * d2 + d11 * d3);
               d5 += d9;
               d6 += d10;
               d7 += d11;
            }

            double d8 = Math.sqrt(d5 * d5 + d6 * d6 + d7 * d7);
            if (!(d8 < 1.0E-6) && !((d5 * d + d6 * d2 + d7 * d3) / d8 < CONST_doubleValue3)) {
               double d9 = Math.acos(Math.clamp(d4, -1.0, 1.0));
               double d10 = Util15.method2704();
               double d11 = (double)minecraft.getWindow().getWidth() / Math.max(1, minecraft.getWindow().getHeight());
               d9 = Math.max(d9, Math.atan(d10 * Math.sqrt(1.0 + d11 * d11)));
               d9 += CONST_doubleValue;
               if (d9 < 1.5607963267948965) {
                  doubleValue = Math.cos(d9);
                  doubleValue2 = Math.sin(d9);
                  flag = true;
               }
            }
         }
      }
   }

   private static boolean method7829(Glowboost glowboost, double d, double d2, double d3, double d4) {
      if (!flag) {
         return false;
      } else {
         double d5 = d - glowboost.getDoubleValue();
         double d6 = d2 - glowboost.getDoubleValue2();
         double d7 = d3 - glowboost.getDoubleValue3();
         double d8 = d5 * glowboost.getDoubleValue10() + d6 * glowboost.getDoubleValue11() + d7 * glowboost.getDoubleValue12();
         double d9 = d4 + 0.25 + d8 * doubleValue2;
         if (d9 < 0.0) {
            return true;
         } else {
            double d10 = d5 * d5 + d6 * d6 + d7 * d7 - d8 * d8;
            return d10 * doubleValue * doubleValue > d9 * d9;
         }
      }
   }

   private static void method7830(
      Glowboost glowboost,
      FlakeState flakeState2,
      WeatherFlakeChannel weatherFlakeChannel,
      float f,
      float f2,
      double x,
      double y,
      double z,
      float f3,
      float f4,
      float f5,
      int n,
      float f6
   ) {
      if (weatherFlakeChannel.intValue2 != 0) {
         float f7 = weatherFlakeChannel.floatValue18 * f;
         if (!(f7 <= 0.01F)) {
            boolean flag = weatherFlakeChannel.intValue2 == 2;
            glowboost.method3235(0.0F);
            float f8 = 2.0F * Util15.method2704() / Math.max(1, Minecraft.getInstance().getWindow().getHeight());
            int n2 = (int)Math.floor(x);
            int n3 = (int)Math.floor(z);
            float f9 = (flag ? 3.2F : 3.2F) * f7;
            float f10 = (flag ? 1.5F : 9.5F) * Math.max(0.05F, weatherFlakeChannel.floatValue19);
            float f11 = 0.72F + 0.28F * (float)Math.sin(f2 * 0.53) + 0.18F * (float)Math.sin(f2 * 1.31 + 2.1);
            float f12 = weatherFlakeChannel.floatValue20 * f11;
            float f13 = weatherFlakeChannel.floatValue21 * f11;
            float f14 = Math.abs(f12) + Math.abs(f13);
            glowboost.method3244(flag ? identifier2 : identifier);
            if (flag) {
               method7834(glowboost);
            }

            float f15 = flag ? f4 * 1.6F : f4 * 1.8F;
            float f16 = flag ? f5 : f5 * 1.35F;
            float f17 = f15 + f16;
            double d = y - f16;
            double d2 = y + f15;
            float f18 = flag ? 1.1F : 1.6F;
            float f19 = f17 / f3;
            int n4 = Math.min(n, flakeState2.getIntValue3());
            int n5 = n4 * n4;

            for (int i = -n4; i <= n4; i++) {
               for (int j = -n4; j <= n4; j++) {
                  int n6 = i * i + j * j;
                  if (n6 <= n5) {
                     int n7 = n2 + i;
                     int n8 = n3 + j;
                     double d3 = n7 + 0.5 - x;
                     double d4 = n8 + 0.5 - z;
                     float f20 = (float)Math.sqrt(d3 * d3 + d4 * d4);
                     if (!method7837(glowboost, d3, d4, f20)) {
                        double d5 = flakeState2.method4549(n7, n8);
                        if (d5 != Double.NEGATIVE_INFINITY && !(d5 > d2)) {
                           float f21 = Math.min(1.0F, f20 / f18);
                           float f22 = 1.0F - method7836(n4 - 4.0F, n4, f20);
                           float f23 = f21 * f22;
                           if (!(f23 <= 0.02F)) {
                              long longValue = method7838(n7, n8);
                              float f24 = f9 * f19 / (1.0F + f20 / f6);
                              int n9 = (int)f24;
                              if (Glowboost.method3268(longValue, 0, 7) < f24 - n9) {
                                 n9++;
                              }

                              for (int k = 0; k < n9; k++) {
                                 if (flag) {
                                    method7833(
                                       glowboost, weatherFlakeChannel, longValue, k, d5, f2, f10, f * f23, n7, n8, f12, f13, f14, f17, d, d2
                                    );
                                 } else {
                                    method7831(
                                       glowboost,
                                       weatherFlakeChannel,
                                       longValue,
                                       k,
                                       d5,
                                       f2,
                                       f10,
                                       f * f23,
                                       n7,
                                       n8,
                                       f12,
                                       f13,
                                       f17,
                                       d,
                                       d2,
                                       f8,
                                       f20
                                    );
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }

            if (!flag) {
               method7832(glowboost, flakeState2, weatherFlakeChannel, f, f2, x, y, z, n);
            }
         }
      }
   }

   private static void method7831(
      Glowboost glowboost,
      WeatherFlakeChannel weatherFlakeChannel,
      long longValue,
      int n,
      double d,
      float f,
      float f2,
      float f3,
      int n2,
      int n3,
      float f4,
      float f5,
      float f6,
      double d2,
      double d3,
      float f7,
      float f8
   ) {
      float f9 = Glowboost.method3268(longValue, n, 0);
      float f10 = Glowboost.method3268(longValue, n, 1);
      float f11 = Glowboost.method3268(longValue, n, 2);
      float f12 = Glowboost.method3268(longValue, n, 4);
      float f13 = f2 * (0.8F + 0.45F * f12);
      double d4 = mod(f * f13 + f11 * f6, f6);
      double d5 = method7835(-d4, f6, d2);
      if (!(d5 > d3)) {
         double d6 = d5 - d;
         if (!(d6 < 0.0)) {
            float f14 = f13 * 0.075F * (0.62F + 0.76F * Glowboost.method3268(longValue, n, 11));
            float f15 = f14 * 0.5F;
            float f16 = Math.max(0.014F + 0.016F * f12, f7 * f8 * 1.4F);
            float f17 = 0.05F + 0.04F * Glowboost.method3268(longValue, n, 6);
            double d7 = n2 + f9 - f4 * 0.55 * d4;
            double d8 = n3 + f10 - f5 * 0.55 * d4;
            if (!method7829(glowboost, d7, d5, d8, f17 * 1.17F + f14 + f16)) {
               double d9 = Math.sin(d5 * 1.7 + f11 * 6.283) * f17;
               double d10 = d7 + d9;
               double d11 = d8 + d9 * 0.6;
               float f18 = Math.min(1.0F, (float)d6 / 0.45F);
               float f19 = Math.min(1.0F, (float)(d3 - d5) / 2.5F);
               float f20 = Math.min(1.0F, f8 / 3.2F);
               float alpha = f3 * f18 * f19 * f20 * (0.36F + 0.48F * f12 * f12);
               int n4 = ColorUtils.method6439(weatherFlakeChannel.intValue3, alpha);
               float f21 = (Glowboost.method3268(longValue, n, 12) - 0.5F) * 0.11F;
               double d12 = f4 * 0.55 + f21;
               double d13 = f5 * 0.55 + f21 * 0.6;
               double d14 = Math.sqrt(d12 * d12 + 1.0 + d13 * d13);
               glowboost.method3249(
                  d10 + d12 / d14 * f15,
                  d5 + f15 / d14,
                  d11 + d13 / d14 * f15,
                  d12,
                  1.0,
                  d13,
                  f15,
                  f16,
                  0.0F,
                  0.0F,
                  1.0F,
                  1.0F,
                  n4
               );
            }
         }
      }
   }

   private static void method7832(
      Glowboost glowboost, FlakeState flakeState2, WeatherFlakeChannel weatherFlakeChannel, float f, float f2, double x, double y, double z, int n
   ) {
      float f3 = weatherFlakeChannel.floatValue22 * weatherFlakeChannel.floatValue18 * f;
      if (!(f3 <= 0.02F)) {
         glowboost.method3244(identifier3);
         glowboost.method3235(0.0F);
         int n2 = (int)Math.floor(x);
         int n3 = (int)Math.floor(z);
         int n4 = Math.min(9, Math.min(n, flakeState2.getIntValue3()));

         for (int i = -n4; i <= n4; i++) {
            for (int j = -n4; j <= n4; j++) {
               int n5 = n2 + i;
               int n6 = n3 + j;
               double d = flakeState2.method4549(n5, n6);
               if (d != Double.NEGATIVE_INFINITY && !(Math.abs(d - y) > 8.0)) {
                  float f4 = (float)Math.sqrt(i * i + j * j);
                  if (!(f4 > n4) && !method7837(glowboost, i, j, f4)) {
                     float f5 = (1.0F - method7836(n4 - 3.0F, n4, f4)) * Math.min(1.0F, f4 / 1.5F);
                     if (!(f5 <= 0.02F)) {
                        long longValue = method7838(n5, n6) ^ 93L;
                        int n7 = f3 > 0.6F ? 2 : 1;

                        for (int k = 0; k < n7; k++) {
                           float f6 = 0.9F + 0.7F * Glowboost.method3268(longValue, k, 1);
                           float f7 = (float)mod(f2 + Glowboost.method3268(longValue, k, 2) * f6, f6);
                           if (!(f7 > 0.42F)) {
                              float f8 = f7 / 0.42F;
                              float f9 = 0.06F + 0.2F * (float)Math.sqrt(f8);
                              int n8 = ColorUtils.method6439(weatherFlakeChannel.intValue3, f3 * f5 * (1.0F - f8) * 0.55F);
                              if (n8 >>> 24 != 0) {
                                 double doubleValue = n5 + Glowboost.method3268(longValue, k, 3);
                                 double d2 = n6 + Glowboost.method3268(longValue, k, 4);
                                 if (!method7829(glowboost, doubleValue, d + 0.02, d2, f9 * 1.42F)) {
                                    glowboost.method3246(doubleValue, d + 0.02, d2, 1.0, 0.0, f9, f9, 0.0F, 0.0F, 1.0F, 1.0F, n8);
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }

         glowboost.method3244(identifier);
      }
   }

   private static void method7833(
      Glowboost glowboost,
      WeatherFlakeChannel weatherFlakeChannel,
      long longValue,
      int n,
      double d,
      float f,
      float f2,
      float f3,
      int n2,
      int n3,
      float f4,
      float f5,
      float f6,
      float f7,
      double d2,
      double d3
   ) {
      float f8 = Glowboost.method3268(longValue, n, 0);
      float f9 = Glowboost.method3268(longValue, n, 1);
      float f10 = Glowboost.method3268(longValue, n, 2);
      boolean flag = Glowboost.method3268(longValue, n, 9) < 0.34F;
      float f11 = Glowboost.method3268(longValue, n, 5);
      float f12 = f11 * f11;
      float f13 = flag ? f2 * (2.1F + 0.7F * f12) : f2 * (1.25F - 0.5F * f12);
      double d4 = mod(f * f13 + f10 * f7, f7);
      double d5 = method7835(-d4, f7, d2);
      if (!(d5 > d3)) {
         float f14 = flag ? 0.0F : (0.55F + 0.45F * f12) * 0.22F;
         d5 += Math.sin(f * 0.83 + f10 * 6.283F) * f14;
         double d6 = d5 - d;
         if (!(d6 < 0.0)) {
            float f15 = flag ? 0.032F + 0.046F * f12 : 0.085F + 0.17F * f12;
            float f16 = (0.2F + 0.5F * f6) * (0.55F + 0.75F * f12) * (flag ? 0.35F : 1.0F);
            double d7 = n2 + f8 - f4 * 0.55 * d4;
            double d8 = n3 + f9 - f5 * 0.55 * d4;
            float f17 = f16 * 1.3F + f15 * 1.42F;
            if (f6 >= 0.7F) {
               f17 += 0.75F * (Math.abs(f4) + Math.abs(f5) + 0.2F);
            }

            if (!method7829(glowboost, d7, d5, d8, f17)) {
               float f18 = f10 * 6.283F + f * (0.55F + 0.45F * Glowboost.method3268(longValue, n, 4));
               double doubleValue = d7 + Math.sin(f18) * f16;
               double d9 = d8 + Math.cos(f18 * 0.73F) * f16 * 0.8;
               float f19 = f10 * 6.283F + f * (1.1F + 2.3F * Glowboost.method3268(longValue, n, 10));
               float f20 = flag ? 1.0F : (float)Math.abs(Math.sin(f19));
               float f21 = flag ? 1.0F : 0.58F + 0.42F * f20;
               float f22 = Math.min(1.0F, (float)d6 / 0.35F);
               float f23 = Math.min(1.0F, (float)(d3 - d5) / 2.5F);
               float alpha = f3 * f22 * f23 * f21 * (flag ? 0.62F + 0.28F * f12 : 0.74F + 0.26F * Glowboost.method3268(longValue, n, 6));
               int n4 = ColorUtils.method6439(weatherFlakeChannel.intValue3, alpha);
               int n5 = flag ? 3 : (int)(Glowboost.method3268(longValue, n, 13) * 3.0F) % 3;
               float f24 = n5 % 2 * 0.5F;
               float f25 = n5 / 2 * 0.5F;
               double d10;
               double d11;
               double d12;
               if (flag) {
                  double d13 = Math.sin(f18 * 0.6) * 0.45;
                  d10 = f4 * 0.3 + d13;
                  d11 = 1.0;
                  d12 = f5 * 0.3 + d13 * 0.6;
               } else {
                  double d13 = f19 * 0.53;
                  double d14 = Math.cos(d13);
                  double d15 = Math.sin(d13);
                  d10 = doubleValue5 * d14 + doubleValue3 * d15;
                  d11 = doubleValue6 * d14;
                  d12 = doubleValue7 * d14 + doubleValue4 * d15;
               }

               glowboost.method3249(doubleValue, d5, d9, d10, d11, d12, f15, f15 * f21, f24, f25, f24 + 0.5F, f25 + 0.5F, n4);
               if (!(f6 < 0.7F) && !(Glowboost.method3268(longValue, n, 8) > 0.3F)) {
                  float f26 = 0.3F + 0.45F * Glowboost.method3268(longValue, n, 7);
                  glowboost.method3236(
                     doubleValue,
                     d5,
                     d9,
                     doubleValue + f4 * f26,
                     d5 + f26 * 0.2,
                     d9 + f5 * f26,
                     ColorUtils.method6439(weatherFlakeChannel.intValue3, alpha * 0.35F)
                  );
               }
            }
         }
      }
   }

   private static void method7834(Glowboost glowboost) {
      double d = glowboost.getDoubleValue10();
      double d2 = glowboost.getDoubleValue11();
      double d3 = glowboost.getDoubleValue12();
      double d4 = Math.sqrt(d * d + d3 * d3);
      double d5 = d4 < 1.0E-4 ? 1.0 : d3 / d4;
      double d6 = d4 < 1.0E-4 ? 0.0 : -d / d4;
      doubleValue3 = d5;
      doubleValue4 = d6;
      doubleValue5 = -d6 * d2;
      doubleValue6 = d6 * d - d5 * d3;
      doubleValue7 = d5 * d2;
   }

   private static double method7835(double d, float f, double d2) {
      return d2 + mod(d - d2, f);
   }

   private static float method7836(float f, float f2, float f3) {
      float f4 = Math.clamp((f3 - f) / Math.max(f2 - f, 1.0E-4F), 0.0F, 1.0F);
      return f4 * f4 * (3.0F - 2.0F * f4);
   }

   private static double mod(double d, double d2) {
      double d3 = d - Math.floor(d / d2) * d2;
      if (d3 >= d2) {
         d3 -= d2;
      } else if (d3 < 0.0) {
         d3 += d2;
      }

      return d3;
   }

   private static boolean method7837(Glowboost glowboost, double d, double d2, float f) {
      return f < 3.0F ? false : d * glowboost.getDoubleValue10() + d2 * glowboost.getDoubleValue12() < -0.25 * f;
   }

   private static long method7838(int n, int n2) {
      return 374227287185L ^ n * -7046029254386353131L ^ n2 * -4417276706812531889L;
   }
}
