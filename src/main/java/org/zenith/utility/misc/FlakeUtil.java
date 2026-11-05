package org.zenith.utility.misc;

import java.util.Arrays;
import java.util.List;
import net.minecraft.resources.Identifier;
import org.zenith.Zenith;
import org.zenith.utility.game.FlakeState;
import org.zenith.utility.game.Print;
import org.zenith.utility.game.Util11;
import org.zenith.utility.render.Glowboost;
import org.zenith.utility.render.SnowShader;
import org.zenith.utility.render.WorldProfiler;

public final class FlakeUtil {
   private static final long CONST_longValue = 488613995701L;
   private static final int CONST_intValue = 48;
   private static final float CONST_floatValue = 0.55F;
   private static final double CONST_doubleValue = 0.035;
   private static final double CONST_doubleValue2 = 0.004;
   private static final double CONST_doubleValue3 = 0.006;
   private static final float CONST_floatValue2 = 0.167F;
   private static final float CONST_floatValue3 = 0.147F;
   private static final float CONST_floatValue4 = 0.054F;
   private static final float CONST_floatValue5 = 0.585F;
   private static final float CONST_floatValue6 = 5.5F;
   private static final float CONST_floatValue7 = 0.24F;
   private static final float CONST_floatValue8 = 0.58F;
   private static final float CONST_floatValue9 = 0.38F;
   private static final float CONST_floatValue10 = 2.0F;
   private static final float CONST_floatValue11 = 0.42F;
   private static final float CONST_floatValue12 = 14.0F;
   private static final float CONST_floatValue13 = 0.16F;
   private static final float[] CONST_floatValues = new float[]{0.78F, 0.84F, 0.96F};
   private static final float CONST_floatValue14 = 1.6F;
   private static final float CONST_floatValue15 = 0.1F;
   private static final float CONST_floatValue16 = 90.0F;
   private static final float CONST_floatValue17 = 0.95F;
   private static final float CONST_floatValue18 = 0.35F;
   private static final long CONST_longValue2 = 400L;
   private static final float[] CONST_floatValues2 = new float[]{0.95F, 0.97F, 1.0F};
   private static final float[] CONST_floatValues3 = new float[]{0.74F, 0.81F, 0.96F};
   private static final FlakeUtil.Entry entry = new FlakeUtil.Entry();
   private static final FlakeUtil.Entry entry2 = new FlakeUtil.Entry();
   private static final Identifier identifier = Zenith.method1501("visuals/weather/flake.png");
   private static final float CONST_floatValue19 = 15.0F;
   private static final int CONST_intValue2 = 18;

   private FlakeUtil() {
   }

   public static float method7506() {
      return 48.0F;
   }

   public static void method7507(
      Glowboost glowboost, FlakeState flakeState2, WeatherFlakeChannel weatherFlakeChannel, float f, Print print, long now, double x, double y, double z, float f2
   ) {
      method7510(entry, glowboost, flakeState2, weatherFlakeChannel, f, print, now, x, y, z, 48, f2);
   }

   public static void method7508(Glowboost glowboost, FlakeState flakeState2, WeatherFlakeChannel weatherFlakeChannel, float f, double x, double y, double z) {
      method7510(entry2, glowboost, flakeState2, weatherFlakeChannel, f, null, 0L, x, y, z, 6, 1.0F);
   }

   public static float method7509(double doubleValue, double d) {
      FlakeUtil.Entry entry2 = entry;
      float[] floatValues2 = entry2.floatValues2;
      if (entry2.intValue != 0 && floatValues2.length != 0) {
         int n = (int)Math.floor(doubleValue);
         int n2 = (int)Math.floor(d);
         int n3 = entry2.method7540(n, n2);
         int n4 = entry2.method7540(n + 1, n2);
         int n5 = entry2.method7540(n, n2 + 1);
         int n6 = entry2.method7540(n + 1, n2 + 1);
         if (n3 >= 0 && n4 >= 0 && n5 >= 0 && n6 >= 0) {
            float f = (float)(doubleValue - n);
            float f2 = (float)(d - n2);
            float f3 = floatValues2[n3] + (floatValues2[n4] - floatValues2[n3]) * f;
            float f4 = floatValues2[n5] + (floatValues2[n6] - floatValues2[n5]) * f;
            return 0.006F + f3 + (f4 - f3) * f2;
         } else {
            return 0.0F;
         }
      } else {
         return 0.0F;
      }
   }

   private static void method7510(
      FlakeUtil.Entry entry2,
      Glowboost glowboost,
      FlakeState flakeState2,
      WeatherFlakeChannel weatherFlakeChannel,
      float f,
      Print print,
      long now,
      double x,
      double y,
      double z,
      int n,
      float f2
   ) {
      boolean flag = entry2 == entry;
      if (!weatherFlakeChannel.flag) {
         entry2.flag = false;
         if (flag) {
            Util11.method2336();
            method7516();
         }
      } else {
         float f3 = weatherFlakeChannel.floatValue23 * Math.clamp(f, 0.0F, 1.0F);
         if (f3 <= 0.02F) {
            entry2.flag = false;
            if (flag) {
               method7516();
            }
         } else {
            glowboost.method3235(0.0F);
            int n2 = (int)Math.floor(x);
            int n3 = (int)Math.floor(z);
            int n4 = Math.min(n, flakeState2.getIntValue3());
            if (flag) {
               Util11.method2331(now);
            }

            method7512(glowboost, weatherFlakeChannel, f, now, x, y, z);
            boolean flag2 = flag && SnowShader.method390();
            method7517(entry2, flakeState2, weatherFlakeChannel, n2, n3, n4, f3 * f2, x, y, z, now, flag2);
            float[] floatValues2 = entry2.floatValues2;
            int[] intValues = entry2.intValues;
            if (flag2) {
               long longValue = method7514(entry2, n2, n3, n4, flakeState2) * 31L + Util11.method2333(now);
               if (longValue == entry2.longValue && SnowShader.isFlag3()) {
                  return;
               }

               entry2.longValue = longValue;
               SnowShader.method393();
               WorldProfiler.method784(1);
            }

            long l = flag ? WorldProfiler.method782() : 0L;
            int n5 = n4 * n4;

            for (int i = -n4; i <= n4; i++) {
               for (int j = -n4; j <= n4; j++) {
                  if (i * i + j * j <= n5) {
                     int x2 = n2 + i;
                     int z2 = n3 + j;
                     int n6 = x2 - flakeState2.getIntValue();
                     int n7 = z2 - flakeState2.getIntValue2();
                     double d = flakeState2.method4544(n6, n7);
                     if (d != Double.NEGATIVE_INFINITY && flakeState2.method4546(n6, n7) && !(Math.abs(d - y) > 64.0)) {
                        int n8 = entry2.method7540(x2, z2);
                        int n9 = entry2.method7540(x2 + 1, z2);
                        int n10 = entry2.method7540(x2 + 1, z2 + 1);
                        int n11 = entry2.method7540(x2, z2 + 1);
                        if (n8 >= 0 && n9 >= 0 && n10 >= 0 && n11 >= 0) {
                           float f4 = Util11.method2334(x2, z2, d, now);
                           if (!(f4 <= 0.001F)) {
                              float f5 = flakeState2.method4545(n6, n7) ? 0.55F : 1.0F;
                              float f6 = floatValues2[n8] * method7511(f4, x2, z2) * f5;
                              float f7 = floatValues2[n9] * method7511(f4, x2 + 1, z2) * f5;
                              float f8 = floatValues2[n10] * method7511(f4, x2 + 1, z2 + 1) * f5;
                              float f9 = floatValues2[n11] * method7511(f4, x2, z2 + 1) * f5;
                              if (!(f6 + f7 + f8 + f9 <= 0.008F)) {
                                 double d2 = d + 0.006;
                                 int n12 = intValues[n8];
                                 int n13 = intValues[n9];
                                 int n14 = intValues[n10];
                                 int n15 = intValues[n11];
                                 method7533(
                                    glowboost,
                                    flag2,
                                    x2,
                                    d2 + f6,
                                    z2,
                                    x2,
                                    d2 + f9,
                                    z2 + 1,
                                    x2 + 1,
                                    d2 + f8,
                                    z2 + 1,
                                    x2 + 1,
                                    d2 + f7,
                                    z2,
                                    n12,
                                    n15,
                                    n14,
                                    n13
                                 );
                                 float f10 = f4 * f5;
                                 method7530(glowboost, flag2, flakeState2, x2, z2, d, d2, 0, -1, n12, n13, f6, f7, f10, now);
                                 method7530(glowboost, flag2, flakeState2, x2, z2, d, d2, 1, 0, n13, n14, f7, f8, f10, now);
                                 method7530(glowboost, flag2, flakeState2, x2, z2, d, d2, 0, 1, n14, n15, f8, f9, f10, now);
                                 method7530(glowboost, flag2, flakeState2, x2, z2, d, d2, -1, 0, n15, n12, f9, f6, f10, now);
                              }
                           }
                        }
                     }
                  }
               }
            }

            WorldProfiler.method783(22, l);
         }
      }
   }

   private static float method7511(float f, int n, int n2) {
      if (f >= 1.0F) {
         return 1.0F;
      } else {
         float f2 = method7537(n + 811, n2 - 277) * 0.18F;
         float f3 = Math.clamp((f - f2) / (1.0F - f2), 0.0F, 1.0F);
         return Math.max(f3, f * 0.55F);
      }
   }

   private static void method7512(Glowboost glowboost, WeatherFlakeChannel weatherFlakeChannel, float f, long now, double x, double y, double z) {
      List<Util11.Data> items = Util11.getItems();
      if (!items.isEmpty()) {
         glowboost.method3244(identifier);
         glowboost.method3235(0.0F);

         for (Util11.Data data : items) {
            float f2 = (float)(now - data.getLongValue()) / 900.0F;
            if (!(f2 < 0.0F) && !(f2 >= 1.0F)) {
               double d = data.getDoubleValue() + 0.5;
               double d2 = data.getDoubleValue2() + 0.5;
               double d3 = d - x;
               double d4 = d2 - z;
               if (!(d3 * d3 + d4 * d4 > 4096.0)) {
                  float f3 = f2 * 0.9F;
                  double d5 = Math.max(0.02, data.getDoubleValue3() - data.getDoubleValue4());
                  float f4 = (float)Math.sqrt(2.0 * d5 / 15.0);
                  long longValue = 488613995701L ^ Double.doubleToLongBits(data.getDoubleValue()) ^ Long.rotateLeft(data.getLongValue(), 19);

                  for (int i = 0; i < 18; i++) {
                     float f5 = f3 - Glowboost.method3268(longValue, i, 0) * 0.14F;
                     if (!(f5 <= 0.0F)) {
                        double d6 = 7.5 * f5 * f5;
                        double d7 = data.getDoubleValue3() - d6;
                        float f6;
                        if (d7 <= data.getDoubleValue4()) {
                           d7 = data.getDoubleValue4() + 0.015;
                           f6 = Math.max(0.0F, 1.0F - (f5 - f4) / 0.3F);
                        } else {
                           f6 = 1.0F;
                        }

                        if (!(f6 <= 0.02F)) {
                           double d8 = Glowboost.method3268(longValue, i, 1) * Math.PI * 2.0;
                           float f7 = 0.12F + 0.26F * Glowboost.method3268(longValue, i, 2);
                           double d9 = 0.35 + f5 * 1.5;
                           double doubleValue = d + Math.cos(d8) * f7 * d9;
                           double d10 = d2 + Math.sin(d8) * f7 * d9;
                           float f8 = 0.028F + 0.04F * Glowboost.method3268(longValue, i, 3);
                           int n = ColorUtils.method6439(
                              weatherFlakeChannel.intValue3, f6 * (0.55F + 0.35F * Glowboost.method3268(longValue, i, 4)) * Math.clamp(f, 0.0F, 1.0F)
                           );
                           glowboost.method3249(doubleValue, d7, d10, 0.0, 1.0, 0.0, f8, f8, 0.5F, 0.5F, 1.0F, 1.0F, n);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private static boolean method7513(FlakeState flakeState2, int x, int z, double d, float f, long now) {
      int n = x - flakeState2.getIntValue();
      int n2 = z - flakeState2.getIntValue2();
      double d2 = flakeState2.method4544(n, n2);
      if (d2 != Double.NEGATIVE_INFINITY && !(Math.abs(d2 - d) > 0.5)) {
         float f2 = Util11.method2334(x, z, d2, now);
         if (flakeState2.method4545(n, n2)) {
            f2 *= 0.55F;
         }

         if (!flakeState2.method4546(n, n2)) {
            f2 = 0.0F;
         }

         return f2 < f - 0.12F;
      } else {
         return false;
      }
   }

   private static long method7514(FlakeUtil.Entry entry, int n, int n2, int n3, FlakeState flakeState2) {
      long l = 1125899906842597L;
      l = l * 31L + n;
      l = l * 31L + n2;
      l = l * 31L + n3;
      l = l * 31L + flakeState2.getIntValue();
      l = l * 31L + flakeState2.getIntValue2();
      return l * 31L + entry.longValue2;
   }

   private static long method7515(FlakeUtil.Entry entry, int n) {
      long l = 1L;

      for (int i = 0; i < n; i++) {
         l = l * 31L + Float.floatToRawIntBits(entry.floatValues2[i]);
         l = l * 31L + entry.intValues[i];
      }

      return l;
   }

   private static void method7516() {
      entry.longValue = 0L;
      SnowShader.method401();
   }

   private static void method7517(
      FlakeUtil.Entry entry2,
      FlakeState flakeState,
      WeatherFlakeChannel weatherFlakeChannel,
      int n,
      int n2,
      int n3,
      float f,
      double x,
      double y,
      double z,
      long now,
      boolean flag
   ) {
      long l = method7518(weatherFlakeChannel, f) * 31L + (flag ? 1 : 0);
      boolean flag2 = entry2.flag
         && entry2.intValue5 == n3
         && entry2.intValue3 == n
         && entry2.intValue4 == n2
         && entry2.intValue6 == flakeState.getIntValue()
         && entry2.intValue7 == flakeState.getIntValue2()
         && entry2.longValue3 == l
         && entry2.flakeState == flakeState
         && entry2.longValue4 == flakeState.getLongValue2()
         && (flag || now - entry2.longValue5 < 400L);
      if (!flag2) {
         long l2 = 0L;
         if (entry2 == entry) {
            WorldProfiler.method784(0);
            l2 = WorldProfiler.method782();
         }

         method7519(entry2, flakeState, weatherFlakeChannel, n, n2, n3, f);
         method7524(entry2, flakeState, weatherFlakeChannel, n3, f, x, y, z, flag);
         int n4 = entry2.intValue * entry2.intValue;
         System.arraycopy(entry2.floatValues, 0, entry2.floatValues2, 0, n4);
         entry2.longValue2 = method7515(entry2, n4);
         entry2.flag = true;
         entry2.intValue5 = n3;
         entry2.intValue6 = flakeState.getIntValue();
         entry2.intValue7 = flakeState.getIntValue2();
         entry2.longValue3 = l;
         entry2.flakeState = flakeState;
         entry2.longValue4 = flakeState.getLongValue2();
         entry2.longValue5 = now;
         WorldProfiler.method783(21, l2);
      }
   }

   private static long method7518(WeatherFlakeChannel weatherFlakeChannel, float f) {
      long floatToIntBits = Float.floatToIntBits(f);
      floatToIntBits = floatToIntBits * 31L + Float.floatToIntBits(weatherFlakeChannel.floatValue20);
      floatToIntBits = floatToIntBits * 31L + Float.floatToIntBits(weatherFlakeChannel.floatValue21);

      for (int i = 0; i < 3; i++) {
         floatToIntBits = floatToIntBits * 31L + Float.floatToIntBits(weatherFlakeChannel.floatValues4[i]);
         floatToIntBits = floatToIntBits * 31L + Float.floatToIntBits(weatherFlakeChannel.floatValues5[i]);
         floatToIntBits = floatToIntBits * 31L + Float.floatToIntBits(weatherFlakeChannel.floatValues[i]);
         floatToIntBits = floatToIntBits * 31L + Float.floatToIntBits(weatherFlakeChannel.floatValues2[i]);
      }

      return floatToIntBits;
   }

   private static void method7519(FlakeUtil.Entry entry, FlakeState flakeState, WeatherFlakeChannel weatherFlakeChannel, int n, int n2, int n3, float f) {
      entry.intValue2 = n3 + 2;
      entry.intValue3 = n;
      entry.intValue4 = n2;
      entry.intValue = entry.intValue2 * 2 + 1;
      int n4 = entry.intValue * entry.intValue;
      if (entry.floatValues.length < n4) {
         entry.floatValues = new float[n4];
         entry.floatValues2 = new float[n4];
         entry.intValues = new int[n4];
         entry.intValue11 = -1;
      }

      int n5 = entry.intValue2;
      float[] floatValues = entry.floatValues;
      method7522(entry, n3);
      float[] floatValues9 = entry.floatValues9;
      float f2 = weatherFlakeChannel.floatValue20;
      float f3 = weatherFlakeChannel.floatValue21;
      float f4 = (float)Math.sqrt(f2 * f2 + f3 * f3);
      if (f4 < 1.0E-4F) {
         f2 = 0.7071F;
         f3 = 0.7071F;
      } else {
         f2 /= f4;
         f3 /= f4;
      }

      entry.method7542(entry.intValue, f2, f3);
      method7520(entry, flakeState, n4, f2, f3);
      float[] floatValues3 = entry.floatValues3;
      float[] floatValues4 = entry.floatValues4;

      for (int i = -n5; i <= n5; i++) {
         for (int j = -n5; j <= n5; j++) {
            int n6 = entry.method7541(i, j);
            floatValues[n6] = floatValues3[n6] * f * floatValues9[n6] * floatValues4[n6];
         }
      }

      method7523(entry);
   }

   private static void method7520(FlakeUtil.Entry entry, FlakeState flakeState2, int n, float f, float f2) {
      int n2 = entry.intValue2;
      int intValue = entry.intValue;
      long l = (long)Float.floatToIntBits(f) << 32 ^ Float.floatToIntBits(f2) & 4294967295L;
      int n3 = entry.intValue3 - entry.intValue9;
      int n4 = entry.intValue4 - entry.intValue10;
      boolean flag = entry.flakeState2 == flakeState2
         && entry.longValue6 == flakeState2.getLongValue2()
         && entry.longValue7 == l
         && entry.intValue8 == n2
         && entry.floatValues3.length >= n
         && Math.abs(n3) < intValue
         && Math.abs(n4) < intValue;
      if (!flag || n3 != 0 || n4 != 0) {
         if (entry.floatValues6.length < n) {
            entry.floatValues6 = new float[n];
            entry.floatValues7 = new float[n];
            entry.floatValues8 = new float[n];
         }

         int n5 = Math.round(f * 1.0F);
         int n6 = Math.round(f2 * 1.0F);
         int n7 = Math.round(f * 2.0F);
         int n8 = Math.round(f2 * 2.0F);

         for (int i = -n2; i <= n2; i++) {
            for (int j = -n2; j <= n2; j++) {
               int n9 = entry.method7541(i, j);
               int n10 = i + n3;
               int n11 = j + n4;
               if (flag && n10 >= -n2 && n10 <= n2 && n11 >= -n2 && n11 <= n2) {
                  int n12 = entry.method7541(n10, n11);
                  entry.floatValues6[n9] = entry.floatValues3[n12];
                  entry.floatValues7[n9] = entry.floatValues4[n12];
                  entry.floatValues8[n9] = entry.floatValues5[n12];
               } else {
                  method7521(entry, flakeState2, entry.intValue3 + i, entry.intValue4 + j, f, f2, n5, n6, n7, n8, n9);
               }
            }
         }

         float[] floatValues3 = entry.floatValues3;
         entry.floatValues3 = entry.floatValues6;
         entry.floatValues6 = floatValues3;
         floatValues3 = entry.floatValues4;
         entry.floatValues4 = entry.floatValues7;
         entry.floatValues7 = floatValues3;
         floatValues3 = entry.floatValues5;
         entry.floatValues5 = entry.floatValues8;
         entry.floatValues8 = floatValues3;
         entry.flakeState2 = flakeState2;
         entry.longValue6 = flakeState2.getLongValue2();
         entry.longValue7 = l;
         entry.intValue8 = n2;
         entry.intValue9 = entry.intValue3;
         entry.intValue10 = entry.intValue4;
      }
   }

   private static void method7521(
      FlakeUtil.Entry entry, FlakeState flakeState2, int n, int n2, float f, float f2, int n3, int n4, int n5, int n6, int n7
   ) {
      int n8 = entry.method7543(n, n2);
      float f3;
      if (entry.intValues2[n8] == n && entry.intValues3[n8] == n2) {
         f3 = entry.floatValues11[n8];
      } else {
         float f4 = n * f + n2 * f2;
         float f5 = -n * f2 + n2 * f;
         float f6 = method7536(f4 * 0.24F, f5 * 0.58F) * 0.68F + method7536(f4 * 0.24F * 3.1F + 11.3F, f5 * 0.58F * 2.4F + 4.1F) * 0.32F;
         float f7 = method7536(f4 * 0.9F + 31.1F, f5 * 2.2F + 7.7F);
         f3 = 0.147F * f6 + 0.054F * f7;
         entry.intValues2[n8] = n;
         entry.intValues3[n8] = n2;
         entry.floatValues11[n8] = f3;
      }

      float f4 = method7529(flakeState2, n, n2);
      float f5 = 0.35F + 0.65F * f4;
      entry.floatValues6[n7] = 0.167F + f3 * f5 + 0.585F * method7527(flakeState2, n, n2, n3, n4, n5, n6);
      entry.floatValues7[n7] = method7526(flakeState2, n, n2);
      entry.floatValues8[n7] = f4;
   }

   private static void method7522(FlakeUtil.Entry entry, int n) {
      int n2 = entry.intValue2;
      int n3 = entry.intValue * entry.intValue;
      if (entry.intValue11 != n || entry.floatValues9.length < n3) {
         if (entry.floatValues9.length < n3) {
            entry.floatValues9 = new float[n3];
            entry.floatValues10 = new float[n3];
         }

         for (int i = -n2; i <= n2; i++) {
            for (int j = -n2; j <= n2; j++) {
               float f = (float)Math.sqrt(i * i + j * j);
               int n4 = entry.method7541(i, j);
               entry.floatValues9[n4] = 1.0F - method7538(n2 - 9.0F, n2 - 2.0F, f);
               entry.floatValues10[n4] = 1.0F - method7538(n - 8.0F, n, f);
            }
         }

         entry.intValue11 = n;
      }
   }

   private static void method7523(FlakeUtil.Entry entry) {
      int n = entry.intValue2 - 1;
      float[] floatValues = entry.floatValues;
      float[] floatValues2 = entry.floatValues2;

      for (int i = 0; i < 2.0F; i++) {
         System.arraycopy(floatValues, 0, floatValues2, 0, entry.intValue * entry.intValue);

         for (int j = -n; j <= n; j++) {
            for (int k = -n; k <= n; k++) {
               float f = floatValues2[entry.method7541(j - 1, k)]
                  + floatValues2[entry.method7541(j + 1, k)]
                  + floatValues2[entry.method7541(j, k - 1)]
                  + floatValues2[entry.method7541(j, k + 1)];
               floatValues[entry.method7541(j, k)] = floatValues2[entry.method7541(j, k)] * 0.42F + f * 0.25F * 0.58000004F;
            }
         }
      }
   }

   private static void method7524(
      FlakeUtil.Entry entry, FlakeState flakeState, WeatherFlakeChannel weatherFlakeChannel, int n, float f, double x, double y, double z, boolean flag
   ) {
      float[] floatValues = entry.floatValues;
      int n2 = entry.intValue3;
      int n3 = entry.intValue4;
      float f2 = method7539(weatherFlakeChannel.floatValues4);
      float f3 = weatherFlakeChannel.floatValues4[0] / f2;
      float f4 = weatherFlakeChannel.floatValues4[1] / f2;
      float f5 = weatherFlakeChannel.floatValues4[2] / f2;
      float f6 = (weatherFlakeChannel.floatValues[0] + weatherFlakeChannel.floatValues2[0]) * 0.5F + 0.16F;
      float f7 = (weatherFlakeChannel.floatValues[1] + weatherFlakeChannel.floatValues2[1]) * 0.5F + 0.16F;
      float f8 = (weatherFlakeChannel.floatValues[2] + weatherFlakeChannel.floatValues2[2]) * 0.5F + 0.17F;
      float[] floatValues5 = entry.floatValues5;
      float[] floatValues10 = entry.floatValues10;
      int n4 = entry.intValue2 - 1;

      for (int i = -n4; i <= n4; i++) {
         for (int j = -n4; j <= n4; j++) {
            int n5 = entry.method7541(i, j);
            float f9 = floatValues[entry.method7541(i - 1, j)];
            float f10 = floatValues[entry.method7541(i + 1, j)];
            float f11 = floatValues[entry.method7541(i, j - 1)];
            float f12 = floatValues[entry.method7541(i, j + 1)];
            float f13 = floatValues[n5];
            float f14 = floatValues5[n5];
            float f15 = (f9 + f10 + f12 + f11) * 0.25F - f13;
            f14 *= Math.clamp(1.0F - f15 * 1.6F, 0.8F, 1.12F);
            float f16 = method7537(n2 + i - 401, n3 + j + 257);
            if (flag) {
               entry.intValues[n5] = method7532(f13, f14, f16);
            } else {
               float f17 = floatValues10[n5];
               float f18 = (f9 - f10) * 0.5F * 5.5F;
               float f19 = (f11 - f12) * 0.5F * 5.5F;
               float f20 = (float)Math.sqrt(f18 * f18 + 1.0F + f19 * f19);
               f18 /= f20;
               float f21 = 1.0F / f20;
               f19 /= f20;
               float f22 = method7525(flakeState, n2 + i, n3 + j, f18, f21, f19, f3, f4, f5, x, y, z);
               entry.intValues[n5] = method7531(
                  f18, f21, f19, f13, f14, f * f17, f22, f16, f6, f7, f8, weatherFlakeChannel, f3, f4, f5, false
               );
            }
         }
      }
   }

   private static float method7525(
      FlakeState flakeState, int n, int n2, float f, float f2, float f3, float f4, float f5, float f6, double x, double y, double z
   ) {
      float f7 = method7537(n + 977, n2 - 613);
      if (f7 > 0.1F) {
         return 0.0F;
      } else {
         double d = method7528(flakeState, n, n2);
         if (d == Double.NEGATIVE_INFINITY) {
            return 0.0F;
         } else {
            float f8 = (float)(x - n);
            float f9 = (float)(y - d);
            float f10 = (float)(z - n2);
            float f11 = (float)Math.sqrt(f8 * f8 + f9 * f9 + f10 * f10);
            if (f11 < 0.001F) {
               return 0.0F;
            } else {
               f8 /= f11;
               f9 /= f11;
               f10 /= f11;
               float f12 = f8 + f4;
               float f13 = f9 + f5;
               float f14 = f10 + f6;
               float f15 = (float)Math.sqrt(f12 * f12 + f13 * f13 + f14 * f14);
               if (f15 < 0.001F) {
                  return 0.0F;
               } else {
                  float f16 = (f * f12 + f2 * f13 + f3 * f14) / f15;
                  if (f16 <= 0.0F) {
                     return 0.0F;
                  } else {
                     float f17 = Math.clamp(f5 * 3.0F, 0.0F, 1.0F);
                     return (float)Math.pow(f16, 90.0) * f17 * 0.95F;
                  }
               }
            }
         }
      }
   }

   private static float method7526(FlakeState flakeState2, int n, int n2) {
      int n3 = 0;
      double d = Double.NEGATIVE_INFINITY;
      int n4 = 0;
      int n5 = 0;

      for (int i = -1; i <= 0; i++) {
         for (int j = -1; j <= 0; j++) {
            int n6 = n + i - flakeState2.getIntValue();
            int n7 = n2 + j - flakeState2.getIntValue2();
            double d2 = flakeState2.method4544(n6, n7);
            if (d2 != Double.NEGATIVE_INFINITY && flakeState2.method4546(n6, n7)) {
               n3++;
               if (d == Double.NEGATIVE_INFINITY) {
                  d = d2;
                  n4 = 1;
               } else if (Math.abs(d2 - d) < 0.5) {
                  n4++;
               }

               n5 = Math.max(n5, n4);
            }
         }
      }

      return n3 == 0 ? 0.0F : 0.8F + 0.2F * (n5 / 4.0F);
   }

   private static float method7527(FlakeState flakeState2, int n, int n2, int n3, int n4, int n5, int n6) {
      double d = method7528(flakeState2, n, n2);
      if (d == Double.NEGATIVE_INFINITY) {
         return 0.0F;
      } else {
         float f = 0.0F;

         for (int i = 1; i <= 2; i++) {
            double d2 = i == 1 ? method7528(flakeState2, n - n3, n2 - n4) : method7528(flakeState2, n - n5, n2 - n6);
            if (d2 != Double.NEGATIVE_INFINITY) {
               float f2 = (float)(d2 - d);
               if (!(f2 < 0.5F)) {
                  f = Math.max(f, Math.min(1.0F, f2 / 2.0F) / i);
               }
            }
         }

         return f;
      }
   }

   private static double method7528(FlakeState flakeState2, int n, int n2) {
      return flakeState2.method4544(n - flakeState2.getIntValue(), n2 - flakeState2.getIntValue2());
   }

   private static float method7529(FlakeState flakeState2, int n, int n2) {
      double d = method7528(flakeState2, n, n2);
      if (d == Double.NEGATIVE_INFINITY) {
         return 1.0F;
      } else {
         float f = 0.0F;

         for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
               if (i != 0 || j != 0) {
                  double d2 = method7528(flakeState2, n + i, n2 + j);
                  if (d2 != Double.NEGATIVE_INFINITY) {
                     float f2 = (float)(d2 - d);
                     if (f2 >= 0.5F) {
                        f += Math.min(1.0F, f2 / 3.0F);
                     }
                  }
               }
            }
         }

         return 1.0F - Math.min(0.42F, f * 0.14F);
      }
   }

   private static void method7530(
      Glowboost glowboost,
      boolean flag,
      FlakeState flakeState2,
      int n,
      int n2,
      double d,
      double d2,
      int n3,
      int n4,
      int n5,
      int n6,
      float f,
      float f2,
      float f3,
      long now
   ) {
      if (!(f + f2 <= 0.008F)) {
         int n7 = n + n3 - flakeState2.getIntValue();
         int n8 = n2 + n4 - flakeState2.getIntValue2();
         double d3 = flakeState2.method4544(n7, n8);
         boolean flag2 = d3 == Double.NEGATIVE_INFINITY
            || !flakeState2.method4546(n7, n8)
            || d3 < d - 0.5
            || method7513(flakeState2, n + n3, n2 + n4, d, f3, now);
         if (flag2) {
            double d4;
            double d5;
            double d6;
            double d7;
            if (n4 == -1) {
               d4 = n;
               d5 = n2;
               d6 = n + 1;
               d7 = n2;
            } else if (n3 == 1) {
               d4 = n + 1;
               d5 = n2;
               d6 = n + 1;
               d7 = n2 + 1;
            } else if (n4 == 1) {
               d4 = n + 1;
               d5 = n2 + 1;
               d6 = n;
               d7 = n2 + 1;
            } else {
               d4 = n;
               d5 = n2 + 1;
               d6 = n;
               d7 = n2;
            }

            float f4 = flag ? 1.0F : 0.88F;
            int n9 = method7534(method7535(n5, f4));
            int n10 = method7534(method7535(n6, f4));
            double d8 = d2 - 0.035;
            double d9 = n3 * 0.004;
            double d10 = n4 * 0.004;
            method7533(
               glowboost,
               flag,
               d4 + d9,
               d2 + f,
               d5 + d10,
               d6 + d9,
               d2 + f2,
               d7 + d10,
               d6 + d9,
               d8,
               d7 + d10,
               d4 + d9,
               d8,
               d5 + d10,
               n9,
               n10,
               n10,
               n9
            );
         }
      }
   }

   private static int method7531(
      float f,
      float f2,
      float f3,
      float f4,
      float f5,
      float f6,
      float f7,
      float f8,
      float f9,
      float f10,
      float f11,
      WeatherFlakeChannel weatherFlakeChannel,
      float f12,
      float f13,
      float f14,
      boolean flag
   ) {
      float f15 = (0.66F + 0.34F * Math.max(0.0F, f2)) * f5;
      float f16 = Math.max(0.0F, (f * f12 + f2 * f13 + f3 * f14 + 0.38F) / 1.38F);
      float f17 = 0.965F + 0.07F * f8;
      float f18 = Math.clamp(f16 * f5, 0.0F, 1.0F);
      float f19 = CONST_floatValues[0] + (1.0F - CONST_floatValues[0]) * f18;
      float f20 = CONST_floatValues[1] + (1.0F - CONST_floatValues[1]) * f18;
      float f21 = CONST_floatValues[2] + (1.0F - CONST_floatValues[2]) * f18;
      float f22;
      float f23;
      float f24;
      if (flag) {
         f22 = CONST_floatValues2[0] * f17 * f5;
         f23 = CONST_floatValues2[1] * f17 * f5;
         f24 = CONST_floatValues2[2] * f17 * f5;
      } else {
         f22 = CONST_floatValues2[0] * f17 * f19 * (f9 * f15 + weatherFlakeChannel.floatValues5[0] * f16 * 0.46F);
         f23 = CONST_floatValues2[1] * f17 * f20 * (f10 * f15 + weatherFlakeChannel.floatValues5[1] * f16 * 0.46F);
         f24 = CONST_floatValues2[2] * f17 * f21 * (f11 * f15 + weatherFlakeChannel.floatValues5[2] * f16 * 0.46F);
      }

      if (!flag) {
         float f25 = (float)Math.pow(Math.max(0.0F, f2 * 0.5F + f16 * 0.5F), 14.0) * 0.16F;
         f22 += weatherFlakeChannel.floatValues5[0] * f25;
         f23 += weatherFlakeChannel.floatValues5[1] * f25;
         f24 += weatherFlakeChannel.floatValues5[2] * f25;
      }

      float f25 = Math.clamp(f4 / 0.16F, 0.0F, 1.0F);
      f22 *= CONST_floatValues3[0] + (1.0F - CONST_floatValues3[0]) * f25;
      f23 *= CONST_floatValues3[1] + (1.0F - CONST_floatValues3[1]) * f25;
      f24 *= CONST_floatValues3[2] + (1.0F - CONST_floatValues3[2]) * f25;
      if (f7 > 0.0F && !flag) {
         f22 += weatherFlakeChannel.floatValues5[0] * f7;
         f23 += weatherFlakeChannel.floatValues5[1] * f7;
         f24 += weatherFlakeChannel.floatValues5[2] * f7;
      }

      return 0xFF000000 | channel(f22 * 255.0F) << 16 | channel(f23 * 255.0F) << 8 | channel(f24 * 255.0F);
   }

   private static int method7532(float f, float f2, float f3) {
      float f4 = 0.965F + 0.07F * f3;
      float f5 = CONST_floatValues2[0] * f4 * f2;
      float f6 = CONST_floatValues2[1] * f4 * f2;
      float f7 = CONST_floatValues2[2] * f4 * f2;
      float f8 = Math.clamp(f / 0.16F, 0.0F, 1.0F);
      f5 *= CONST_floatValues3[0] + (1.0F - CONST_floatValues3[0]) * f8;
      f6 *= CONST_floatValues3[1] + (1.0F - CONST_floatValues3[1]) * f8;
      f7 *= CONST_floatValues3[2] + (1.0F - CONST_floatValues3[2]) * f8;
      return 0xFF000000 | channel(f5 * 255.0F) << 16 | channel(f6 * 255.0F) << 8 | channel(f7 * 255.0F);
   }

   private static void method7533(
      Glowboost glowboost,
      boolean flag,
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
      double d11,
      double d12,
      int n,
      int n2,
      int n3,
      int n4
   ) {
      if (flag) {
         SnowShader.method395(d, d2, d3, d4, d5, d6, d7, d8, d9, d10, d11, d12, n, n2, n3, n4);
      } else {
         glowboost.method3239(d, d2, d3, d4, d5, d6, d7, d8, d9, d10, d11, d12, n, n2, n3, n4);
      }
   }

   private static int method7534(int n) {
      return n & 16777215 | -33554432;
   }

   private static int method7535(int n, float f) {
      int n2 = n >>> 24 & 0xFF;
      int n3 = (int)((n >> 16 & 0xFF) * f);
      int n4 = (int)((n >> 8 & 0xFF) * f);
      int n5 = (int)((n & 0xFF) * f);
      return n2 << 24 | n3 << 16 | n4 << 8 | n5;
   }

   private static float method7536(float f, float f2) {
      int n = (int)Math.floor(f);
      int n2 = (int)Math.floor(f2);
      float f3 = f - n;
      float f4 = f2 - n2;
      f3 = f3 * f3 * (3.0F - 2.0F * f3);
      f4 = f4 * f4 * (3.0F - 2.0F * f4);
      float f5 = method7537(n, n2);
      float f6 = method7537(n + 1, n2);
      float f7 = method7537(n, n2 + 1);
      float f8 = method7537(n + 1, n2 + 1);
      return (f5 * (1.0F - f3) + f6 * f3) * (1.0F - f4) + (f7 * (1.0F - f3) + f8 * f3) * f4;
   }

   private static float method7537(int n, int n2) {
      return Glowboost.method3268(488613995701L ^ n * -7046029254386353131L, n2, 3);
   }

   private static int channel(float f) {
      return Math.clamp((long)((int)(f + 0.5F)), 0, 255);
   }

   private static float method7538(float f, float f2, float f3) {
      float f4 = Math.clamp((f3 - f) / Math.max(f2 - f, 1.0E-4F), 0.0F, 1.0F);
      return f4 * f4 * (3.0F - 2.0F * f4);
   }

   private static float method7539(float[] floatValues4) {
      float f = (float)Math.sqrt(floatValues4[0] * floatValues4[0] + floatValues4[1] * floatValues4[1] + floatValues4[2] * floatValues4[2]);
      return f < 1.0E-5F ? 1.0F : f;
   }

   private static final class Entry {
      private long longValue;
      private float[] floatValues = new float[0];
      private float[] floatValues2 = new float[0];
      private int[] intValues = new int[0];
      private int intValue;
      private int intValue2;
      private int intValue3;
      private int intValue4;
      private long longValue2;
      private boolean flag;
      private int intValue5 = -1;
      private int intValue6 = Integer.MIN_VALUE;
      private int intValue7 = Integer.MIN_VALUE;
      private long longValue3;
      private FlakeState flakeState;
      private long longValue4 = Long.MIN_VALUE;
      private long longValue5;
      private float[] floatValues3 = new float[0];
      private float[] floatValues4 = new float[0];
      private float[] floatValues5 = new float[0];
      private float[] floatValues6 = new float[0];
      private float[] floatValues7 = new float[0];
      private float[] floatValues8 = new float[0];
      private FlakeState flakeState2;
      private long longValue6 = Long.MIN_VALUE;
      private long longValue7;
      private int intValue8 = -1;
      private int intValue9;
      private int intValue10;
      private float[] floatValues9 = new float[0];
      private float[] floatValues10 = new float[0];
      private int intValue11 = -1;
      private int intValue12;
      private int intValue13 = -1;
      private int[] intValues2 = new int[0];
      private int[] intValues3 = new int[0];
      private float[] floatValues11 = new float[0];
      private int intValue14;
      private int intValue15;

      private int method7540(int n, int n2) {
         int n3 = n - this.intValue3;
         int n4 = n2 - this.intValue4;
         return n3 >= -this.intValue2 && n3 <= this.intValue2 && n4 >= -this.intValue2 && n4 <= this.intValue2 ? this.method7541(n3, n4) : -1;
      }

      private int method7541(int n, int n2) {
         return (n + this.intValue2) * this.intValue + n2 + this.intValue2;
      }

      private void method7542(int intValue, float f, float f2) {
         int floatToRawIntBits = Float.floatToRawIntBits(f);
         int n = Float.floatToRawIntBits(f2);
         if (this.intValue13 + 1 < intValue) {
            int n2 = 1;

            while (1 << n2 < intValue) {
               n2++;
            }

            this.intValue12 = n2;
            this.intValue13 = (1 << n2) - 1;
            int n3 = 1 << n2 * 2;
            this.intValues2 = new int[n3];
            this.intValues3 = new int[n3];
            this.floatValues11 = new float[n3];
            Arrays.fill(this.intValues2, Integer.MIN_VALUE);
         } else if (floatToRawIntBits != this.intValue14 || n != this.intValue15) {
            Arrays.fill(this.intValues2, Integer.MIN_VALUE);
         }

         this.intValue14 = floatToRawIntBits;
         this.intValue15 = n;
      }

      private int method7543(int n, int n2) {
         return (n & this.intValue13) << this.intValue12 | n2 & this.intValue13;
      }
   }
}
