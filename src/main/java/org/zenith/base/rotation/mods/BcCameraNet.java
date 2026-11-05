package org.zenith.base.rotation.mods;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Random;

public final class BcCameraNet {
   public static final String text = "neurotest-bc-camera-v1";
   public static final double CONST_doubleValue = 1.0E-4;
   public static final double CONST_doubleValue2 = 0.002;
   public int intValue;
   public int intValue2;
   public int layers;
   public int intValue3;
   public int intValue4;
   public int intValue5;
   public int clip;
   public int intValue6;
   public boolean flag;
   public boolean flag2;
   public int[] intValues = new int[0];
   public double doubleValue;
   public volatile double doubleValue2 = -1.0;
   public double[] doubleValues;
   public double[] doubleValues2;
   public double doubleValue3;
   public double doubleValue4 = 45.0;
   public boolean flag3;
   public double[][] doubleValues3;
   public BcCameraNet.Entry3 entry3;
   public double doubleValue5 = 1.0;
   public double[] doubleValues4;
   public static final double CONST_doubleValue3 = 1.62;
   public static final double CONST_doubleValue4 = 1.27;
   public static final double CONST_doubleValue5 = 0.6;
   public static final double CONST_doubleValue6 = 1.8;
   public static final double CONST_doubleValue7 = 1.5;
   public double[][] doubleValues5;
   public double[] doubleValues6;
   public double[] doubleValues7;
   public double[][][] doubleValues8;
   public double[][][] doubleValues9;
   public double[][] doubleValues10;
   public double[][] doubleValues11;
   public double[][] doubleValues12;
   public double[][] doubleValues13;
   public double[][] doubleValues14;
   public double[][] doubleValues15;
   public double[][] doubleValues16;
   public double[] doubleValues17;
   public double[] doubleValues18;
   public double[] doubleValues19;
   public double[] doubleValues20;
   public String text2 = "";
   public String source = "?";
   public BcCameraNet.Entry[] entrys = new BcCameraNet.Entry[0];
   public int intValue7;
   public double doubleValue6;

   public double[] method3344(double[] doubleValues) {
      double d = 1.0E9;
      if (this.doubleValue3 > 0.0 && !(doubleValues[21] <= 0.5) && Math.abs(method3351(doubleValues[23])) < this.doubleValue4) {
         double d2 = method3351(doubleValues[23]);
         double d3 = method3351(doubleValues[26]);
         double d4 = method3351(doubleValues[27]);
         double d5 = Math.max(Math.abs(d2), Math.exp(doubleValues[28] * 3.0) + this.doubleValue3);
         return new double[]{d2 - d5, d2 + d5, Math.min(d3 - this.doubleValue3, 0.0), Math.max(d4 + this.doubleValue3, 0.0)};
      } else {
         return new double[]{-d, d, -d, d};
      }
   }

   public double method3345(int n) {
      return n == 0 ? 0.0 : (n <= this.intValue4 ? this.doubleValues4[n - 1] : -this.doubleValues4[n - this.intValue4]);
   }

   public double method3346(int n) {
      return n == 0 ? 0.0 : (n <= this.intValue4 ? this.doubleValues4[n] : -this.doubleValues4[n - 1 - this.intValue4]);
   }

   public double[] method3347(double[] doubles, double d, double d2) {
      double[] doubles2 = (double[])doubles.clone();

      for (int i = 0; i < doubles2.length; i++) {
         if (this.method3346(i) < d || this.method3345(i) > d2) {
            doubles2[i] = -1.0E9;
         }
      }

      return doubles2;
   }

   public double[] method3348(BcCameraNet.Entry2 entry2, double[] doubleValues, double[] doubles, double d, Random random) {
      double[] doubles2 = this.method3344(doubleValues);
      double[] doubleValue = this.method3373(entry2, doubles);
      int n = this.method3377(this.method3347(this.method3375(doubleValue), doubles2[0], doubles2[1]), random.nextDouble());
      int n2 = this.method3377(this.method3347(this.method3376(doubleValue, n), doubles2[2], doubles2[3]), random.nextDouble());
      double d2 = Math.max(doubles2[0], Math.min(doubles2[1], this.method3378(n, d, random)));
      double d3 = Math.max(doubles2[2], Math.min(doubles2[3], this.method3378(n2, d, random)));
      return new double[]{d2, d3};
   }

   public double[] method3349(double[] doubles, double d, double d2) {
      double[] doubles2 = this.method3347(doubles, d, d2);

      for (double d3 : doubles2) {
         if (d3 > -1.0E8) {
            return doubles2;
         }
      }

      return doubles;
   }

   public double[] method3350(BcCameraNet.Entry2 entry2, double[] doubleValues, double[] doubles, double d, Random random) {
      BcCameraNet.Entry3 entry3 = this.entry3;
      if (entry2.doubleValues3 == null) {
         entry2.doubleValues3 = entry3.doubleValues[random.nextInt(entry3.doubleValues.length)];
         entry2.doubleValues4 = new double[entry3.intValue];
         entry2.doubleValues5 = new double[entry3.intValue];
         entry2.flags = new boolean[entry3.intValue];
      }

      if (entry2.intValue == entry3.intValue) {
         System.arraycopy(entry2.doubleValues4, 1, entry2.doubleValues4, 0, entry3.intValue - 1);
         System.arraycopy(entry2.doubleValues5, 1, entry2.doubleValues5, 0, entry3.intValue - 1);
         System.arraycopy(entry2.flags, 1, entry2.flags, 0, entry3.intValue - 1);
         entry2.intValue--;
      }

      entry2.doubleValues4[entry2.intValue] = Math.sinh(doubleValues[1] * 4.0) * 0.5;
      entry2.doubleValues5[entry2.intValue] = Math.sinh(doubleValues[3] * 4.0) * 0.5;
      entry2.flags[entry2.intValue] = doubleValues[4] > 0.5;
      entry2.intValue++;
      double[] doubles2 = entry3.method3397(
         entry2.doubleValues4, entry2.doubleValues5, entry2.flags, entry2.intValue, entry2.doubleValues3, random.nextDouble(), random.nextDouble()
      );
      double[] doubleValue = this.method3373(entry2, doubles);
      int n = this.method3377(this.method3349(this.method3375(doubleValue), doubles2[0], doubles2[1]), random.nextDouble());
      int n2 = this.method3377(this.method3349(this.method3376(doubleValue, n), doubles2[2], doubles2[3]), random.nextDouble());
      double d2 = this.method3378(n, d, random);
      double d3 = this.method3378(n2, d, random);
      double d4 = Math.max(doubles2[0], Math.min(doubles2[1], d2));
      double d5 = Math.max(doubles2[2], Math.min(doubles2[3], d3));
      entry3.longValue++;
      if (Math.abs(d4 - d2) > 1.0E-9 || Math.abs(d5 - d3) > 1.0E-9) {
         entry3.longValue2++;
      }

      return new double[]{d4, d5};
   }

   public static double method3351(double d) {
      return Math.sinh(d * 3.0) * 2.0;
   }

   public double[] method3352(double[] doubleValues, double d, double d2) {
      if (this.doubleValue3 > 0.0 && !(doubleValues[21] <= 0.5)) {
         double d3 = method3351(doubleValues[23]);
         double d4 = method3351(doubleValues[26]);
         double d5 = method3351(doubleValues[27]);
         double exp = Math.exp(doubleValues[28] * 3.0);
         if (!(Math.abs(d3) < this.doubleValue4)) {
            return new double[]{d, d2};
         } else {
            double d6 = Math.max(Math.abs(d3), exp + this.doubleValue3);
            double d7 = d3 - d;
            if (Math.abs(d7) > d6) {
               d = d3 - Math.signum(d7) * d6;
            }

            double d8 = Math.min(d4 - this.doubleValue3, 0.0);
            double d9 = Math.max(d5 + this.doubleValue3, 0.0);
            d2 = Math.max(d8, Math.min(d9, d2));
            return new double[]{d, d2};
         }
      } else {
         return new double[]{d, d2};
      }
   }

   public double method3353(double[] doubleValues) {
      if (this.doubleValue2 >= 0.0) {
         return doubleValues[21] > 0.5 ? this.doubleValue2 : 0.0;
      } else if (this.doubleValues2 == null) {
         return 1.0;
      } else if (doubleValues[21] <= 0.5) {
         return 0.0;
      } else {
         double d = Math.abs(Math.sinh(doubleValues[23] * 3.0) * 2.0);
         int n = 0;

         while (n + 1 < this.doubleValues.length && this.doubleValues[n + 1] <= d) {
            n++;
         }

         n = Math.min(n, this.doubleValues2.length - 1);
         return Math.min(1.0, this.doubleValue * this.doubleValues2[n]);
      }
   }

   public int method3354() {
      return (this.flag ? 7 : 0) + (this.flag2 ? 1 : 0);
   }

   public BcCameraNet.Entry2 method3355() {
      return new BcCameraNet.Entry2();
   }

   public static BcCameraNet method3356(int intValue, int clip, int n) {
      BcCameraNet bcCameraNet = new BcCameraNet();
      bcCameraNet.intValue = intValue;
      bcCameraNet.clip = clip;
      bcCameraNet.intValue6 = n;
      return bcCameraNet;
   }

   public static double method3357(double d) {
      double d2 = (d + 180.0) % 360.0;
      if (d2 < 0.0) {
         d2 += 360.0;
      }

      return d2 - 180.0;
   }

   public static double asinh(double sign) {
      double d = Math.abs(sign);
      return Math.copySign(Math.log(d + Math.sqrt(d * d + 1.0)), sign);
   }

   public static double method3358(double d, double d2, double d3) {
      return Math.max(d2, Math.min(d3, d));
   }

   public double method3359(double x) {
      return Math.log1p(x) / Math.log1p(this.clip);
   }

   public double method3360(boolean[] flags) {
      int n = flags.length - 1;

      for (int i = n; i >= 0; i--) {
         if (flags[i]) {
            return Math.min(n - i, this.clip);
         }
      }

      return this.clip;
   }

   public static boolean method3361(double doubleValue, double d, double d2) {
      return !Double.isNaN(doubleValue) && !Double.isNaN(d) && !Double.isNaN(d2);
   }

   public static double[] method3362(double x, double y, double d) {
      return new double[]{Math.toDegrees(Math.atan2(-x, d)), -Math.toDegrees(Math.atan2(y, Math.hypot(x, d)))};
   }

   public static double[] method3363(double angdeg, double d) {
      double d2 = Math.toRadians(angdeg);
      double d3 = Math.toRadians(d);
      return new double[]{-Math.sin(d2) * Math.cos(d3), -Math.sin(d3), Math.cos(d2) * Math.cos(d3)};
   }

   public static boolean method3364(double[] doubles, double[] doubles2, double[] doubles3, double d) {
      double[] doubles4 = new double[]{doubles3[0] - 0.3, doubles3[1], doubles3[2] - 0.3};
      double[] doubles5 = new double[]{doubles3[0] + 0.3, doubles3[1] + d, doubles3[2] + 0.3};
      double d2 = Double.NEGATIVE_INFINITY;
      double d3 = Double.POSITIVE_INFINITY;
      boolean flag = false;
      boolean flag2 = false;

      for (int i = 0; i < 3; i++) {
         double d4 = 1.0 / doubles2[i];
         double d5 = (doubles4[i] - doubles[i]) * d4;
         double d6 = (doubles5[i] - doubles[i]) * d4;
         double d7 = !Double.isNaN(d5) && !Double.isNaN(d6) ? Math.min(d5, d6) : Double.NaN;
         double d8 = !Double.isNaN(d5) && !Double.isNaN(d6) ? Math.max(d5, d6) : Double.NaN;
         if (!Double.isNaN(d7)) {
            d2 = flag ? Math.max(d2, d7) : d7;
            flag = true;
         }

         if (!Double.isNaN(d8)) {
            d3 = flag2 ? Math.min(d3, d8) : d8;
            flag2 = true;
         }
      }

      return flag && flag2 ? d3 >= Math.max(d2, 0.0) && d2 < 6.0 : false;
   }

   public double[] method3365(BcCameraNet.Entry2 entry2, double d) {
      int size = entry2.size();
      BcCameraNet.Entry4[] entry4s = new BcCameraNet.Entry4[size];

      for (int i = 0; i < size; i++) {
         entry4s[i] = entry2.method3395(size - 1 - i);
      }

      double[] doubles = new double[this.intValue];
      BcCameraNet.Entry4 entry4 = entry4s[size - 1];
      double d2 = entry4.flag5 ? entry4.doubleValue9 : 0.0;
      double d3 = entry4.flag5 ? entry4.doubleValue10 : 0.0;
      doubles[0] = method3358(d2 / 10.0, -6.0, 6.0);
      doubles[1] = asinh(d2 / 0.5) / 4.0;
      doubles[2] = method3358(d3 / 10.0, -6.0, 6.0);
      doubles[3] = asinh(d3 / 0.5) / 4.0;
      doubles[4] = entry4.flag5 ? 1.0 : 0.0;
      doubles[5] = entry4.doubleValue8 / 90.0;
      doubles[6] = Math.log(d / 0.15);
      boolean[] flags = new boolean[size];
      boolean[] flags2 = new boolean[size];

      for (int i = 0; i < size; i++) {
         double d4 = entry4s[i].flag5 ? entry4s[i].doubleValue9 : 0.0;
         double d5 = entry4s[i].flag5 ? entry4s[i].doubleValue10 : 0.0;
         flags2[i] = d4 != 0.0 || d5 != 0.0;
         flags[i] = !flags2[i];
      }

      doubles[7] = this.method3359(this.method3360(flags));
      doubles[8] = this.method3359(this.method3360(flags2));
      doubles[9] = entry4.flag ? 1.0 : 0.0;
      doubles[10] = entry4.flag2 ? 1.0 : 0.0;

      for (int i = 0; i < 7; i++) {
         doubles[11 + i] = entry4.intValue >> i & 1;
      }

      double d4 = Math.toRadians(entry4.doubleValue7);
      double[] doubles2 = new double[]{-Math.sin(d4), 0.0, Math.cos(d4)};
      double[] doubles3 = new double[]{-Math.cos(d4), 0.0, -Math.sin(d4)};
      BcCameraNet.Entry4 entry42 = size > 1 ? entry4s[size - 2] : null;
      double d5 = entry42 == null ? 0.0 : method3366(entry4.doubleValue - entry42.doubleValue);
      double d6 = entry42 == null ? 0.0 : method3366(entry4.doubleValue2 - entry42.doubleValue2);
      double d7 = entry42 == null ? 0.0 : method3366(entry4.doubleValue3 - entry42.doubleValue3);
      doubles[18] = method3358((d5 * doubles2[0] + d7 * doubles2[2]) / 0.3, -3.0, 3.0);
      doubles[19] = method3358((d5 * doubles3[0] + d7 * doubles3[2]) / 0.3, -3.0, 3.0);
      doubles[20] = method3358(d6 / 0.3, -3.0, 3.0);
      boolean[] flags3 = new boolean[size];
      double[] doubles4 = new double[size];
      double[] doubles5 = new double[size];

      for (int i = 0; i < size; i++) {
         BcCameraNet.Entry4 entry43 = entry4s[i];
         flags3[i] = entry43.intValue2 >= 0
            && method3361(entry43.doubleValue4, entry43.doubleValue5, entry43.doubleValue6)
            && method3361(entry43.doubleValue, entry43.doubleValue2, entry43.doubleValue3);
         double[] doubles6 = method3368(entry43, flags3[i]);
         double[] doubles7 = method3362(doubles6[0], doubles6[1], doubles6[2]);
         doubles4[i] = doubles7[0];
         doubles5[i] = doubles7[1];
      }

      boolean flag = flags3[size - 1];
      double[] doubles6 = method3368(entry4, flag);
      double d8 = entry4.flag4 ? 1.5 : 1.8;
      double d9 = flag ? method3357(doubles4[size - 1] - entry4.doubleValue7) : 0.0;
      double d10 = flag ? doubles5[size - 1] - entry4.doubleValue8 : 0.0;
      double d11 = method3362(doubles6[0], doubles6[1] + d8 / 2.0, doubles6[2])[1];
      double d12 = method3362(doubles6[0], doubles6[1] - d8 / 2.0, doubles6[2])[1];
      double d13 = Math.max(Math.hypot(doubles6[0], doubles6[2]), 0.05);
      double d14 = Math.max(Math.sqrt(doubles6[0] * doubles6[0] + doubles6[1] * doubles6[1] + doubles6[2] * doubles6[2]), 0.05);
      doubles[21] = flag ? 1.0 : 0.0;
      doubles[22] = method3358(d9 / 30.0, -6.0, 6.0);
      doubles[23] = asinh(d9 / 2.0) / 3.0;
      doubles[24] = method3358(d10 / 30.0, -6.0, 6.0);
      doubles[25] = asinh(d10 / 2.0) / 3.0;
      doubles[26] = flag ? asinh((d11 - entry4.doubleValue8) / 2.0) / 3.0 : 0.0;
      doubles[27] = flag ? asinh((d12 - entry4.doubleValue8) / 2.0) / 3.0 : 0.0;
      doubles[28] = flag ? Math.log(Math.toDegrees(Math.atan(0.3 / d13))) / 3.0 : 0.0;
      doubles[29] = flag ? Math.log(d14) / 2.0 : 0.0;
      if (flag) {
         double[] doubles7 = method3367(entry4);
         doubles[30] = method3364(
               doubles7, method3363(entry4.doubleValue7, entry4.doubleValue8), new double[]{entry4.doubleValue4, entry4.doubleValue5, entry4.doubleValue6}, d8
            )
            ? 1.0
            : 0.0;
      }

      boolean[] flags4 = new boolean[size];
      boolean[] flags5 = new boolean[size];

      for (int i = 0; i < size; i++) {
         flags4[i] = flags3[i] && i > 0 && flags3[i - 1] && entry4s[i].intValue2 == entry4s[i - 1].intValue2;
         flags5[i] = flags3[i] && !flags4[i] || !flags3[i];
      }

      boolean flag2 = flags4[size - 1];
      if (flag2) {
         doubles[31] = asinh(method3357(doubles4[size - 1] - doubles4[size - 2]) / 0.5) / 4.0;
         doubles[32] = asinh((doubles5[size - 1] - doubles5[size - 2]) / 0.5) / 4.0;
         double d15 = method3366(entry4.doubleValue4 - entry42.doubleValue4);
         double d16 = method3366(entry4.doubleValue5 - entry42.doubleValue5);
         double d17 = method3366(entry4.doubleValue6 - entry42.doubleValue6);
         doubles[33] = method3358((d15 * doubles2[0] + d17 * doubles2[2]) / 0.3, -3.0, 3.0);
         doubles[34] = method3358((d15 * doubles3[0] + d17 * doubles3[2]) / 0.3, -3.0, 3.0);
         doubles[35] = method3358(d16 / 0.3, -3.0, 3.0);
      }

      doubles[36] = flag && entry4.flag4 ? 1.0 : 0.0;
      doubles[37] = flag ? Math.min(entry4.intValue3, 3) / 3.0 : 0.0;
      doubles[38] = flag && !flag2 ? 1.0 : 0.0;
      doubles[39] = flag ? this.method3359(this.method3360(flags5)) : 0.0;
      boolean[] flags6 = new boolean[size];

      for (int i = 0; i < size; i++) {
         flags6[i] = entry4s[i].flag3;
      }

      doubles[40] = entry4.flag3 ? 1.0 : 0.0;
      doubles[41] = this.method3359(this.method3360(flags6));
      return doubles;
   }

   public static double method3366(double d) {
      return Double.isNaN(d) ? 0.0 : d;
   }

   public static double[] method3367(BcCameraNet.Entry4 entry4) {
      boolean flag = (entry4.intValue >> 5 & 1) != 0;
      return new double[]{entry4.doubleValue, entry4.doubleValue2 + (flag ? 1.27 : 1.62), entry4.doubleValue3};
   }

   public static double[] method3368(BcCameraNet.Entry4 entry4, boolean flag) {
      if (!flag) {
         return new double[]{0.0, 0.0, 1.0};
      } else {
         double d = entry4.flag4 ? 1.5 : 1.8;
         double[] doubles = method3367(entry4);
         return new double[]{entry4.doubleValue4 - doubles[0], entry4.doubleValue5 + d / 2.0 - doubles[1], entry4.doubleValue6 - doubles[2]};
      }
   }

   public static double method3369(double d) {
      return 0.5 * d * (1.0 + method3370(d / Math.sqrt(2.0)));
   }

   public static double method3370(double d) {
      double d2 = Math.abs(d);
      double d3 = 1.0 / (1.0 + 0.5 * d2);
      double d4 = d3
         * Math.exp(
            -d2 * d2
               - 1.26551223
               + d3
                  * (
                     1.00002368
                        + d3
                           * (
                              0.37409196
                                 + d3
                                    * (
                                       0.09678418
                                          + d3
                                             * (
                                                -0.18628806
                                                   + d3
                                                      * (0.27886807 + d3 * (-1.13520398 + d3 * (1.48851587 + d3 * (-0.82215223 + d3 * 0.17087277))))
                                             )
                                    )
                           )
                  )
         );
      return d >= 0.0 ? 1.0 - d4 : d4 - 1.0;
   }

   public static double method3371(double d) {
      return 1.0 / (1.0 + Math.exp(-d));
   }

   public static double[] method3372(double[][] doubleValues2, double[] doubleValues6, double[] doubleValue) {
      double[] doubles = new double[doubleValues2.length];

      for (int i = 0; i < doubleValues2.length; i++) {
         double d = doubleValues6[i];
         double[] doubles2 = doubleValues2[i];

         for (int j = 0; j < doubleValue.length; j++) {
            d += doubles2[j] * doubleValue[j];
         }

         doubles[i] = d;
      }

      return doubles;
   }

   public double[] method3373(BcCameraNet.Entry2 entry2, double[] doubles) {
      return this.method3374(entry2, doubles, 0);
   }

   public double[] method3374(BcCameraNet.Entry2 entry2, double[] doubles, int n) {
      int n2 = this.intValue + this.method3354();
      double[] doubleValue = new double[n2 + this.intValue3];
      System.arraycopy(doubles, 0, doubleValue, 0, n2);
      System.arraycopy(this.doubleValues7, 0, doubleValue, n2, this.intValue3);
      double[] doubleValue2 = method3372(this.doubleValues5, this.doubleValues6, doubleValue);

      for (int i = 0; i < doubleValue2.length; i++) {
         doubleValue2[i] = method3369(doubleValue2[i]);
      }

      for (int i = 0; i < this.layers; i++) {
         double[] doubleValue3 = entry2.doubleValues[i];
         double[] doubles2 = method3372(this.doubleValues8[i], this.doubleValues10[i], doubleValue2);
         double[] doubles3 = method3372(this.doubleValues9[i], this.doubleValues11[i], doubleValue3);
         double[] doubles4 = new double[this.intValue2];

         for (int j = 0; j < this.intValue2; j++) {
            double d = method3371(doubles2[j] + doubles3[j]);
            double d2 = method3371(doubles2[this.intValue2 + j] + doubles3[this.intValue2 + j]);
            double tanh = Math.tanh(doubles2[2 * this.intValue2 + j] + d * doubles3[2 * this.intValue2 + j]);
            doubles4[j] = (1.0 - d2) * tanh + d2 * doubleValue3[j];
         }

         entry2.doubleValues[i] = doubles4;
         doubleValue2 = doubles4;
      }

      return doubleValue2;
   }

   public double[] method3375(double[] doubleValue) {
      double[] doubles = method3372(this.doubleValues12, this.doubleValues17, doubleValue);

      for (int i = 0; i < doubles.length; i++) {
         doubles[i] = method3369(doubles[i]);
      }

      return method3372(this.doubleValues13, this.doubleValues18, doubles);
   }

   public double[] method3376(double[] doubleValue, int n) {
      double[] doubles = new double[doubleValue.length + this.doubleValues16[n].length];
      System.arraycopy(doubleValue, 0, doubles, 0, doubleValue.length);
      System.arraycopy(this.doubleValues16[n], 0, doubles, doubleValue.length, this.doubleValues16[n].length);
      double[] doubleValue2 = method3372(this.doubleValues14, this.doubleValues19, doubles);

      for (int i = 0; i < doubleValue2.length; i++) {
         doubleValue2[i] = method3369(doubleValue2[i]);
      }

      return method3372(this.doubleValues15, this.doubleValues20, doubleValue2);
   }

   public int method3377(double[] doubles, double nextDouble) {
      double d = Double.NEGATIVE_INFINITY;

      for (double d2 : doubles) {
         d = Math.max(d, d2);
      }

      double d2 = 0.0;
      double[] doubles2 = new double[doubles.length];

      for (int i = 0; i < doubles2.length; i++) {
         doubles2[i] = Math.exp(doubles[i] - d);
         d2 += doubles2[i];
      }

      if (this.doubleValue5 != 1.0) {
         double d3 = doubles2[0] / d2;
         double d4 = Double.NEGATIVE_INFINITY;

         for (int i = 1; i < doubles2.length; i++) {
            d4 = Math.max(d4, doubles[i] / this.doubleValue5);
         }

         double d5 = 0.0;

         for (int i = 1; i < doubles2.length; i++) {
            doubles2[i] = Math.exp(doubles[i] / this.doubleValue5 - d4);
            d5 += doubles2[i];
         }

         for (int i = 1; i < doubles2.length; i++) {
            doubles2[i] = doubles2[i] / d5 * (1.0 - d3);
         }

         doubles2[0] = d3;
         d2 = 1.0;
      }

      double d3 = 0.0;
      double d4 = nextDouble * d2;

      for (int i = 0; i < doubles2.length; i++) {
         d3 += doubles2[i];
         if (d4 < d3) {
            return i;
         }
      }

      return doubles2.length - 1;
   }

   public double method3378(int n, double d, Random random) {
      if (n == 0) {
         return 0.0;
      } else {
         boolean flag = n <= this.intValue4;
         int n2 = Math.max(0, Math.min(this.intValue4 - 1, flag ? n - 1 : n - 1 - this.intValue4));
         double log = Math.log(this.doubleValues4[n2]);
         double log2 = Math.log(this.doubleValues4[n2 + 1]);
         if (this.doubleValues3 != null) {
            double[] doubles = this.doubleValues3[n];
            double nextDouble = random.nextDouble();
            double d2 = 0.0;
            int n3 = doubles.length - 1;

            for (int i = 0; i < doubles.length; i++) {
               d2 += doubles[i];
               if (nextDouble < d2) {
                  n3 = i;
                  break;
               }
            }

            double d3 = (log2 - log) / doubles.length;
            log += d3 * n3;
            log2 = log + d3;
         }

         double exp = Math.exp(log + (log2 - log) * random.nextDouble());
         double d2 = Math.ceil(Math.exp(log) / d);
         double d3 = Math.ceil(Math.exp(log2) / d) - 1.0;
         double nextDouble = random.nextDouble();
         double d4 = d3 >= d2 ? (d2 + Math.floor(nextDouble * (d3 - d2 + 1.0))) * d : exp;
         return flag ? d4 : -d4;
      }
   }

   public double[] method3379(double[] doubleValues, int n) {
      double[] doubles = new double[this.intValue + this.method3354()];
      System.arraycopy(doubleValues, 0, doubles, 0, this.intValue);

      for (int intValue : this.intValues) {
         doubles[intValue] = 0.0;
      }

      int intValue = this.intValue;
      if (this.flag) {
         for (int i = 0; i < 7; i++) {
            doubles[intValue++] = n >> i & 1;
         }
      }

      if (this.flag2) {
         doubles[intValue] = this.method3353(doubleValues);
      }

      return doubles;
   }

   public double[] method3380(BcCameraNet.Entry2 entry2, double[] doubles, double d, Random random) {
      double[] doubleValue = this.method3373(entry2, doubles);
      int n = this.method3377(this.method3375(doubleValue), random.nextDouble());
      int n2 = this.method3377(this.method3376(doubleValue, n), random.nextDouble());
      return new double[]{this.method3378(n, d, random), this.method3378(n2, d, random)};
   }

   public boolean method3381() {
      return this.entrys.length > 0 && this.intValue7 > 1;
   }

   public static double[] method3382(double[] doubleValues, double d, double d2) {
      double[] doubles = (double[])doubleValues.clone();
      doubles[0] = method3358(d / 10.0, -6.0, 6.0);
      doubles[1] = asinh(d / 0.5) / 4.0;
      doubles[2] = method3358(d2 / 10.0, -6.0, 6.0);
      doubles[3] = asinh(d2 / 0.5) / 4.0;
      doubles[4] = 1.0;
      double d3 = method3358(doubleValues[5] * 90.0 + d2, -90.0, 90.0);
      doubles[5] = d3 / 90.0;
      boolean flag = doubleValues[21] > 0.5;
      double d4 = Math.sinh(doubleValues[23] * 3.0) * 2.0 - d;
      double d5 = Math.sinh(doubleValues[25] * 3.0) * 2.0 - d2;
      doubles[22] = flag ? method3358(d4 / 30.0, -6.0, 6.0) : 0.0;
      doubles[23] = flag ? asinh(d4 / 2.0) / 3.0 : 0.0;
      doubles[24] = flag ? method3358(d5 / 30.0, -6.0, 6.0) : 0.0;
      doubles[25] = flag ? asinh(d5 / 2.0) / 3.0 : 0.0;

      for (int i = 26; i <= 27; i++) {
         double d6 = Math.sinh(doubleValues[i] * 3.0) * 2.0 - d2;
         doubles[i] = flag ? asinh(d6 / 2.0) / 3.0 : 0.0;
      }

      double exp = Math.exp(doubleValues[28] * 3.0);
      double d6 = Math.sinh(doubles[26] * 3.0) * 2.0;
      double d7 = Math.sinh(doubles[27] * 3.0) * 2.0;
      doubles[30] = flag && Math.abs(d4) < exp && d6 <= 0.0 && d7 >= 0.0 ? 1.0 : 0.0;
      return doubles;
   }

   public double[] method3383(BcCameraNet.Entry2 entry2, double[] doubleValues, double[] doubles, double d, Random random) {
      for (int i = 0; i < this.entrys.length; i++) {
         entry2.doubleValues2[i] = this.entrys[i].method3392(entry2.doubleValues2[i], this.entrys[i].method3391(doubleValues));
      }

      double[] doubleValue = this.method3373(entry2, doubles);
      double[] doubles2 = this.method3375(doubleValue);
      int n = this.intValue7;
      double[][] doubleses = new double[n][];
      double[] doubles3 = new double[n];
      double d2 = Double.NEGATIVE_INFINITY;

      for (int i = 0; i < n; i++) {
         int n2 = this.method3377(doubles2, random.nextDouble());
         int n3 = this.method3377(this.method3376(doubleValue, n2), random.nextDouble());
         double d3 = this.method3378(n2, d, random);
         double d4 = this.method3378(n3, d, random);
         doubleses[i] = new double[]{d3, d4};
         double[] doubleValues2 = method3382(doubleValues, d3, d4);
         double d5 = 0.0;

         for (int j = 0; j < this.entrys.length; j++) {
            d5 += this.entrys[j].method3393(this.entrys[j].method3392(entry2.doubleValues2[j], this.entrys[j].method3391(doubleValues2)));
         }

         doubles3[i] = this.doubleValue6 * d5;
         d2 = Math.max(d2, doubles3[i]);
      }

      double d3 = 0.0;

      for (int i = 0; i < n; i++) {
         doubles3[i] = Math.exp(doubles3[i] - d2);
         d3 += doubles3[i];
      }

      double d4 = random.nextDouble() * d3;
      double d5 = 0.0;

      for (int i = 0; i < n; i++) {
         d5 += doubles3[i];
         if (d4 < d5) {
            return doubleses[i];
         }
      }

      return doubleses[n - 1];
   }

   public static double[] method3384(BcCameraNet.Entry2 entry2, double d, double d2, double d3, double d4) {
      entry2.doubleValue += d;
      entry2.doubleValue2 += d2;
      double rint = Math.rint(entry2.doubleValue / d3);
      double rint2 = Math.rint(entry2.doubleValue2 / d3);
      double d5 = rint * d3;
      double d6 = rint2 * d3;
      entry2.doubleValue -= d5;
      entry2.doubleValue2 -= d6;
      double d7 = Math.max(-90.0, Math.min(90.0, d4 + d6));
      return new double[]{d5, d7 - d4};
   }

   public static double[] method3385(JsonObject json, String memberName) {
      JsonObject jsonObject = json.getAsJsonObject(memberName);
      byte[] array = Base64.getDecoder().decode(jsonObject.get("b64").getAsString());
      ByteBuffer order = ByteBuffer.wrap(array).order(ByteOrder.LITTLE_ENDIAN);
      double[] doubles = new double[array.length / 4];

      for (int i = 0; i < doubles.length; i++) {
         doubles[i] = order.getFloat();
      }

      return doubles;
   }

   public static double[][] method3386(JsonObject json, String memberName) {
      JsonArray jsonArray = json.getAsJsonObject(memberName).getAsJsonArray("shape");
      int n = jsonArray.get(0).getAsInt();
      int n2 = jsonArray.get(1).getAsInt();
      double[] doubles = method3385(json, memberName);
      double[][] doubleses = new double[n][n2];

      for (int i = 0; i < n; i++) {
         System.arraycopy(doubles, i * n2, doubleses[i], 0, n2);
      }

      return doubleses;
   }

   public static BcCameraNet method3387(InputStream inputStream) throws IOException {
      InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);

      JsonObject jsonObject;
      try {
         jsonObject = JsonParser.parseReader(inputStreamReader).getAsJsonObject();
      } catch (Throwable e) {
         try {
            inputStreamReader.close();
         } catch (Throwable ex) {
            e.addSuppressed(ex);
         }

         throw e;
      }

      inputStreamReader.close();
      if (!"neurotest-bc-camera-v1".equals(jsonObject.get("format").getAsString())) {
         throw new IOException("формат " + jsonObject.get("format").getAsString() + ", ожидался neurotest-bc-camera-v1");
      } else {
         BcCameraNet bcCameraNet = new BcCameraNet();
         bcCameraNet.intValue = jsonObject.get("nf").getAsInt();
         bcCameraNet.intValue2 = jsonObject.get("h").getAsInt();
         bcCameraNet.layers = jsonObject.get("layers").getAsInt();
         bcCameraNet.intValue3 = jsonObject.get("emb").getAsInt();
         bcCameraNet.intValue4 = jsonObject.get("nb").getAsInt();
         bcCameraNet.intValue5 = jsonObject.get("ncls").getAsInt();
         bcCameraNet.clip = jsonObject.get("clip").getAsInt();
         bcCameraNet.intValue6 = jsonObject.get("window").getAsInt();
         bcCameraNet.flag = jsonObject.has("keys_now") && jsonObject.get("keys_now").getAsBoolean();
         bcCameraNet.flag2 = jsonObject.has("intent") && jsonObject.get("intent").getAsBoolean();
         if (jsonObject.has("intent_hold") && jsonObject.get("intent_hold").isJsonObject()) {
            bcCameraNet.doubleValue2 = jsonObject.getAsJsonObject("intent_hold").get("ref").getAsDouble();
         }

         if (jsonObject.has("intent_err") && jsonObject.get("intent_err").isJsonObject()) {
            JsonObject json = jsonObject.getAsJsonObject("intent_err");
            bcCameraNet.doubleValue = json.get("k").getAsDouble();
            bcCameraNet.doubleValues = method3388(json.getAsJsonArray("edges"));
            bcCameraNet.doubleValues2 = method3388(json.getAsJsonArray("p"));
         }

         String str = "";
         if (jsonObject.has("guard") && jsonObject.get("guard").isJsonObject()) {
            JsonObject json = jsonObject.getAsJsonObject("guard");
            bcCameraNet.doubleValue3 = json.get("m").getAsDouble();
            bcCameraNet.doubleValue4 = json.get("front").getAsDouble();
            bcCameraNet.flag3 = json.has("mode") && "mask".equals(json.get("mode").getAsString());
            JsonObject jsonObject2 = json.getAsJsonObject("ref");
            JsonArray jsonArray = jsonObject2.getAsJsonArray("x");
            JsonArray jsonArray2 = jsonObject2.getAsJsonArray("d");
            JsonArray jsonArray3 = jsonObject2.getAsJsonArray("out");
            double d = 0.0;

            for (int i = 0; i < jsonArray.size(); i++) {
               double[] doubles = method3388(jsonArray2.get(i).getAsJsonArray());
               double[] doubles2 = method3388(jsonArray3.get(i).getAsJsonArray());
               double[] doubles3 = bcCameraNet.method3352(method3388(jsonArray.get(i).getAsJsonArray()), doubles[0], doubles[1]);
               d = Math.max(d, Math.max(Math.abs(doubles3[0] - doubles2[0]), Math.abs(doubles3[1] - doubles2[1])));
            }

            if (!(d < 1.0E-6)) {
               throw new IOException("эталон предохранителя не сошёлся: max |Δ| " + d);
            }

            str = String.format(
               "; предохранитель M %.1f° %s (эталон %d, max |Δ| %.1e)", bcCameraNet.doubleValue3, bcCameraNet.flag3 ? "выбором" : "обрезкой", jsonArray.size(), d
            );
         }

         if (jsonObject.has("sub_table") && jsonObject.get("sub_table").isJsonObject()) {
            JsonArray jsonArray = jsonObject.getAsJsonObject("sub_table").getAsJsonArray("p");
            bcCameraNet.doubleValues3 = new double[jsonArray.size()][];

            for (int i = 0; i < jsonArray.size(); i++) {
               bcCameraNet.doubleValues3[i] = method3388(jsonArray.get(i).getAsJsonArray());
            }

            str = str + String.format("; подбин по людям (%d×%d)", bcCameraNet.doubleValues3.length, bcCameraNet.doubleValues3[0].length);
         }

         if (jsonObject.has("drop")) {
            JsonArray jsonArray = jsonObject.getAsJsonArray("drop");
            bcCameraNet.intValues = new int[jsonArray.size()];

            for (int i = 0; i < jsonArray.size(); i++) {
               bcCameraNet.intValues[i] = jsonArray.get(i).getAsInt();
            }
         }

         JsonArray jsonArray = jsonObject.getAsJsonArray("edges");
         bcCameraNet.doubleValues4 = new double[jsonArray.size()];

         for (int i = 0; i < jsonArray.size(); i++) {
            bcCameraNet.doubleValues4[i] = jsonArray.get(i).getAsDouble();
         }

         if (jsonObject.has("limits") && jsonObject.get("limits").isJsonObject()) {
            JsonObject json = jsonObject.getAsJsonObject("limits");
            BcCameraNet.Entry3 entry3 = new BcCameraNet.Entry3();
            JsonArray jsonArray2 = json.getAsJsonArray("players");
            entry3.doubleValues = new double[jsonArray2.size()][];

            for (int i = 0; i < jsonArray2.size(); i++) {
               entry3.doubleValues[i] = method3388(jsonArray2.get(i).getAsJsonArray());
            }

            JsonObject jsonObject2 = json.getAsJsonObject("window");
            double[] doubles = method3388(jsonObject2.getAsJsonArray("huge"));
            double[] doubles2 = method3388(jsonObject2.getAsJsonArray("flip"));
            entry3.doubleValue = doubles[0];
            entry3.doubleValue2 = doubles[1];
            entry3.doubleValue3 = doubles2[0];
            entry3.doubleValue4 = doubles2[1];
            entry3.doubleValue5 = json.get("big").getAsDouble();
            entry3.doubleValue6 = json.get("flip").getAsDouble();
            entry3.intValue = json.get("hist").getAsInt();
            JsonArray jsonArray3 = json.getAsJsonArray("ref");
            double d = 0.0;
            int n = 0;

            for (int i = 0; i < jsonArray3.size(); i++) {
               JsonObject jsonObject3 = jsonArray3.get(i).getAsJsonObject();
               JsonArray jsonArray4 = jsonObject3.getAsJsonArray("h");
               int size = jsonArray4.size();
               double[] doubleValues4 = new double[size];
               double[] doubleValues5 = new double[size];
               boolean[] flags = new boolean[size];

               for (int j = 0; j < size; j++) {
                  double[] doubles3 = method3388(jsonArray4.get(j).getAsJsonArray());
                  doubleValues4[j] = doubles3[0];
                  doubleValues5[j] = doubles3[1];
                  flags[j] = doubles3[2] > 0.5;
               }

               double[] doubles3 = entry3.method3397(
                  doubleValues4,
                  doubleValues5,
                  flags,
                  size,
                  entry3.doubleValues[jsonObject3.get("player").getAsInt()],
                  jsonObject3.get("u_big").getAsDouble(),
                  jsonObject3.get("u_flip").getAsDouble()
               );
               double[] doubles4 = method3388(jsonObject3.getAsJsonArray("out"));

               for (int j = 0; j < 4; j++) {
                  d = Math.max(d, Math.abs(doubles3[j] - doubles4[j]));
               }

               double[] doubles5 = new double[bcCameraNet.intValue5];
               double[] doubles6 = bcCameraNet.method3349(doubles5, doubles3[0], doubles3[1]);
               double[] doubles7 = bcCameraNet.method3349(doubles5, doubles3[2], doubles3[3]);
               double[] doubles8 = method3388(jsonObject3.getAsJsonArray("allow_yaw"));
               double[] doubles9 = method3388(jsonObject3.getAsJsonArray("allow_pitch"));

               for (int j = 0; j < bcCameraNet.intValue5; j++) {
                  if (doubles6[j] > -1.0E8 != doubles8[j] > 0.5 || doubles7[j] > -1.0E8 != doubles9[j] > 0.5) {
                     n++;
                  }
               }
            }

            if (!(d < 1.0E-6) || n > 0) {
               throw new IOException("эталон пределов не сошёлся: max |Δ| " + d + ", классов не так " + n);
            }

            bcCameraNet.entry3 = entry3;
            str = str + String.format("; пределы людей: %d игроков (эталон %d, max |Δ| %.1e)", entry3.doubleValues.length, jsonArray3.size(), d);
         }

         JsonObject json = jsonObject.getAsJsonObject("weights");
         bcCameraNet.doubleValues5 = method3386(json, "inp.0.weight");
         bcCameraNet.doubleValues6 = method3385(json, "inp.0.bias");
         bcCameraNet.doubleValues7 = method3385(json, "pemb0");
         bcCameraNet.doubleValues8 = new double[bcCameraNet.layers][][];
         bcCameraNet.doubleValues9 = new double[bcCameraNet.layers][][];
         bcCameraNet.doubleValues10 = new double[bcCameraNet.layers][];
         bcCameraNet.doubleValues11 = new double[bcCameraNet.layers][];

         for (int i = 0; i < bcCameraNet.layers; i++) {
            bcCameraNet.doubleValues8[i] = method3386(json, "rnn.weight_ih_l" + i);
            bcCameraNet.doubleValues9[i] = method3386(json, "rnn.weight_hh_l" + i);
            bcCameraNet.doubleValues10[i] = method3385(json, "rnn.bias_ih_l" + i);
            bcCameraNet.doubleValues11[i] = method3385(json, "rnn.bias_hh_l" + i);
         }

         bcCameraNet.doubleValues12 = method3386(json, "yaw.0.weight");
         bcCameraNet.doubleValues17 = method3385(json, "yaw.0.bias");
         bcCameraNet.doubleValues13 = method3386(json, "yaw.2.weight");
         bcCameraNet.doubleValues18 = method3385(json, "yaw.2.bias");
         bcCameraNet.doubleValues14 = method3386(json, "pitch.0.weight");
         bcCameraNet.doubleValues19 = method3385(json, "pitch.0.bias");
         bcCameraNet.doubleValues15 = method3386(json, "pitch.2.weight");
         bcCameraNet.doubleValues20 = method3385(json, "pitch.2.bias");
         bcCameraNet.doubleValues16 = method3386(json, "yemb.weight");
         String str2 = "";
         if (jsonObject.has("guide") && !jsonObject.get("guide").isJsonNull()) {
            JsonObject jsonObject2 = jsonObject.getAsJsonObject("guide");
            bcCameraNet.intValue7 = jsonObject2.get("n").getAsInt();
            bcCameraNet.doubleValue6 = jsonObject2.get("lam").getAsDouble();
            JsonArray jsonArray2 = jsonObject2.getAsJsonArray("discs");
            bcCameraNet.entrys = new BcCameraNet.Entry[jsonArray2.size()];
            JsonArray jsonArray3 = jsonObject2.getAsJsonArray("ref_rows");
            double d = 0.0;

            for (int i = 0; i < jsonArray2.size(); i++) {
               JsonObject jsonObject3 = jsonArray2.get(i).getAsJsonObject();
               JsonObject jsonObject4 = jsonObject3.getAsJsonObject("weights");
               BcCameraNet.Entry entry = new BcCameraNet.Entry();
               double[] doubles = method3388(jsonObject3.getAsJsonArray("cols"));
               entry.intValues = new int[doubles.length];

               for (int j = 0; j < doubles.length; j++) {
                  entry.intValues[j] = (int)doubles[j];
               }

               entry.intValue = jsonObject3.get("h").getAsInt();
               entry.doubleValues = method3386(jsonObject4, "inp.0.weight");
               entry.doubleValues4 = method3385(jsonObject4, "inp.0.bias");
               entry.doubleValues2 = method3386(jsonObject4, "rnn.weight_ih_l0");
               entry.doubleValues3 = method3386(jsonObject4, "rnn.weight_hh_l0");
               entry.doubleValues5 = method3385(jsonObject4, "rnn.bias_ih_l0");
               entry.doubleValues6 = method3385(jsonObject4, "rnn.bias_hh_l0");
               entry.doubleValues7 = method3386(jsonObject4, "out.weight")[0];
               entry.doubleValue = method3385(jsonObject4, "out.bias")[0];
               bcCameraNet.entrys[i] = entry;
               double[] doubles2 = method3388(jsonObject3.getAsJsonArray("ref_scores"));
               double[] doubleValue = new double[entry.intValue];

               for (int j = 0; j < jsonArray3.size(); j++) {
                  doubleValue = entry.method3392(doubleValue, entry.method3391(method3388(jsonArray3.get(j).getAsJsonArray())));
                  d = Math.max(d, Math.abs(entry.method3393(doubleValue) - doubles2[j]));
               }
            }

            str2 = String.format(
               "; выбор хода: %d дискр., N %d, lam %.1f, эталон оценок %.2e", bcCameraNet.entrys.length, bcCameraNet.intValue7, bcCameraNet.doubleValue6, d
            );
            if (!(d <= 0.002)) {
               throw new IOException("ЭТАЛОН ДИСКРИМИНАТОРА НЕ СОШЁЛСЯ" + str2);
            }
         }

         bcCameraNet.source = jsonObject.get("source").getAsString().replace(',', ';') + (bcCameraNet.method3381() ? "+guide" + bcCameraNet.entrys.length : "");
         bcCameraNet.text2 = bcCameraNet.method3390(jsonObject.getAsJsonArray("ref")) + str2 + str + "; модель " + jsonObject.get("source").getAsString();
         return bcCameraNet;
      }
   }

   public static double[] method3388(JsonArray jsonArray) {
      double[] doubles = new double[jsonArray.size()];

      for (int i = 0; i < doubles.length; i++) {
         doubles[i] = jsonArray.get(i).getAsDouble();
      }

      return doubles;
   }

   public static double method3389(double d) {
      return d > 1.0E29 ? Double.NaN : d;
   }

   public String method3390(JsonArray jsonArray) throws IOException {
      double d = 0.0;
      double d2 = 0.0;
      int n = 0;

      for (JsonElement json : jsonArray) {
         JsonObject jsonObject = json.getAsJsonObject();
         double d3 = jsonObject.get("grid").getAsDouble();
         double[] doubles = method3388(jsonObject.getAsJsonArray("yaw"));
         double[] doubles2 = method3388(jsonObject.getAsJsonArray("pitch"));
         double[] doubles3 = method3388(jsonObject.getAsJsonArray("my"));
         double[] doubles4 = method3388(jsonObject.getAsJsonArray("mp"));
         double[] doubles5 = method3388(jsonObject.getAsJsonArray("ok"));
         JsonObject jsonObject2 = jsonObject.getAsJsonObject("rec");
         JsonArray jsonArray2 = jsonObject2.getAsJsonArray("pos");
         JsonArray jsonArray3 = jsonObject2.getAsJsonArray("tgt");
         double[] doubles6 = method3388(jsonObject2.getAsJsonArray("keys"));
         double[] doubles7 = method3388(jsonObject2.getAsJsonArray("attack"));
         double[] doubles8 = method3388(jsonObject2.getAsJsonArray("tgt_id"));
         double[] doubles9 = method3388(jsonObject2.getAsJsonArray("n_cand"));
         double[] doubles10 = method3388(jsonObject2.getAsJsonArray("tgt_sneak"));
         double[] doubles11 = method3388(jsonObject2.getAsJsonArray("on_ground"));
         double[] doubles12 = method3388(jsonObject2.getAsJsonArray("hcoll"));
         JsonArray jsonArray4 = jsonObject.getAsJsonArray("G");
         int n2 = jsonObject.get("start").getAsInt();
         JsonArray jsonArray5 = jsonObject.getAsJsonArray("ly");
         JsonArray jsonArray6 = jsonObject.getAsJsonArray("lp");
         double[] doubles13 = method3388(jsonObject.getAsJsonArray("yc"));
         BcCameraNet.Entry2 entry2 = this.method3355();

         for (int i = 0; i < doubles.length; i++) {
            BcCameraNet.Entry4 entry4 = new BcCameraNet.Entry4();
            JsonArray jsonArray7 = jsonArray2.get(i).getAsJsonArray();
            JsonArray jsonArray8 = jsonArray3.get(i).getAsJsonArray();
            entry4.doubleValue = method3389(jsonArray7.get(0).getAsDouble());
            entry4.doubleValue2 = method3389(jsonArray7.get(1).getAsDouble());
            entry4.doubleValue3 = method3389(jsonArray7.get(2).getAsDouble());
            entry4.doubleValue4 = method3389(jsonArray8.get(0).getAsDouble());
            entry4.doubleValue5 = method3389(jsonArray8.get(1).getAsDouble());
            entry4.doubleValue6 = method3389(jsonArray8.get(2).getAsDouble());
            entry4.intValue = (int)doubles6[i];
            entry4.flag3 = doubles7[i] > 0.0;
            entry4.intValue2 = (int)doubles8[i];
            entry4.intValue3 = (int)doubles9[i];
            entry4.flag4 = doubles10[i] > 0.0;
            entry4.flag = doubles11[i] > 0.0;
            entry4.flag2 = doubles12[i] > 0.0;
            entry4.doubleValue7 = doubles[i];
            entry4.doubleValue8 = doubles2[i];
            entry4.doubleValue9 = doubles3[i];
            entry4.doubleValue10 = doubles4[i];
            entry4.flag5 = doubles5[i] > 0.0;
            entry2.method3394(entry4);
            double[] doubles14 = this.method3365(entry2, d3);
            double[] doubles15 = method3388(jsonArray4.get(i).getAsJsonArray());

            for (int j = 0; j < this.intValue; j++) {
               d = Math.max(d, Math.abs(doubles14[j] - doubles15[j]));
            }

            if (i >= n2) {
               double[] doubles16 = this.method3379(i == 0 ? new double[this.intValue] : method3388(jsonArray4.get(i - 1).getAsJsonArray()), (int)doubles6[i]);
               double[] doubleValue = this.method3373(entry2, doubles16);
               double[] doubles17 = this.method3375(doubleValue);
               double[] doubles18 = this.method3376(doubleValue, (int)doubles13[i - n2]);
               double[] doubles19 = method3388(jsonArray5.get(i - n2).getAsJsonArray());
               double[] doubles20 = method3388(jsonArray6.get(i - n2).getAsJsonArray());

               for (int j = 0; j < this.intValue5; j++) {
                  d2 = Math.max(d2, Math.max(Math.abs(doubles17[j] - doubles19[j]), Math.abs(doubles18[j] - doubles20[j])));
               }
            }

            n++;
         }
      }

      String str = String.format("эталон: %d тиков, признаки макс. расхождение %.2e, логиты %.2e", n, d, d2);
      if (d <= 1.0E-4 && d2 <= 0.002) {
         return str;
      } else {
         throw new IOException("ЭТАЛОН НЕ СОШЁЛСЯ — " + str);
      }
   }

   public static void main(String[] strings) throws Exception {
      try (InputStream inputStream = Files.newInputStream(Path.of(strings[0]))) {
         BcCameraNet bcCameraNet = method3387(inputStream);
         System.out.println("OK: " + bcCameraNet.text2);
         BcCameraNet.Entry2 entry2 = bcCameraNet.method3355();
         Random random = new Random(0L);
         double[] doubles = new double[bcCameraNet.intValue + bcCameraNet.method3354()];
         long nanoTime = System.nanoTime();
         double[] doubleValues = new double[bcCameraNet.intValue];

         for (int i = 0; i < 2000; i++) {
            if (bcCameraNet.method3381()) {
               bcCameraNet.method3383(entry2, doubleValues, doubles, 0.15, random);
            } else if (bcCameraNet.entry3 != null) {
               bcCameraNet.method3350(entry2, doubleValues, doubles, 0.15, random);
            } else {
               bcCameraNet.method3380(entry2, doubles, 0.15, random);
            }
         }

         System.out.printf("шаг сети: %.3f мс%n", (System.nanoTime() - nanoTime) / 2000L / 1000000.0);
      }
   }

   public static final class Entry {
      public int[] intValues;
      public int intValue;
      public double[][] doubleValues;
      public double[][] doubleValues2;
      public double[][] doubleValues3;
      public double[] doubleValues4;
      public double[] doubleValues5;
      public double[] doubleValues6;
      public double[] doubleValues7;
      public double doubleValue;

      public double[] method3391(double[] doubleValues) {
         double[] doubleValue = new double[this.intValues.length];

         for (int i = 0; i < this.intValues.length; i++) {
            doubleValue[i] = doubleValues[this.intValues[i]];
         }

         double[] doubles = BcCameraNet.method3372(this.doubleValues, this.doubleValues4, doubleValue);

         for (int i = 0; i < doubles.length; i++) {
            doubles[i] = BcCameraNet.method3369(doubles[i]);
         }

         return doubles;
      }

      public double[] method3392(double[] doubleValue, double[] doubles) {
         double[] doubles2 = BcCameraNet.method3372(this.doubleValues2, this.doubleValues5, doubles);
         double[] doubles3 = BcCameraNet.method3372(this.doubleValues3, this.doubleValues6, doubleValue);
         double[] doubles4 = new double[this.intValue];

         for (int i = 0; i < this.intValue; i++) {
            double d = BcCameraNet.method3371(doubles2[i] + doubles3[i]);
            double d2 = BcCameraNet.method3371(doubles2[this.intValue + i] + doubles3[this.intValue + i]);
            double tanh = Math.tanh(doubles2[2 * this.intValue + i] + d * doubles3[2 * this.intValue + i]);
            doubles4[i] = (1.0 - d2) * tanh + d2 * doubleValue[i];
         }

         return doubles4;
      }

      public double method3393(double[] doubleValue) {
         double d = this.doubleValue;

         for (int i = 0; i < this.intValue; i++) {
            d += this.doubleValues7[i] * doubleValue[i];
         }

         return d;
      }
   }

   public final class Entry2 {
      public final BcCameraNet.Entry4[] entry4s = new BcCameraNet.Entry4[BcCameraNet.this.intValue6];
      public int n;
      public double[][] doubleValues = new double[BcCameraNet.this.layers][BcCameraNet.this.intValue2];
      public double doubleValue;
      public double doubleValue2;
      public final double[][] doubleValues2 = new double[BcCameraNet.this.entrys.length][];
      public double[] doubleValues3;
      public double[] doubleValues4;
      public double[] doubleValues5;
      public boolean[] flags;
      public int intValue;

      public Entry2() {
         for (int i = 0; i < BcCameraNet.this.entrys.length; i++) {
            this.doubleValues2[i] = new double[BcCameraNet.this.entrys[i].intValue];
         }
      }

      public void method3394(BcCameraNet.Entry4 entry4) {
         this.entry4s[this.n % BcCameraNet.this.intValue6] = entry4;
         this.n++;
      }

      public BcCameraNet.Entry4 method3395(int n) {
         return this.entry4s[(this.n - 1 - n) % BcCameraNet.this.intValue6];
      }

      public int size() {
         return Math.min(this.n, BcCameraNet.this.intValue6);
      }

      public void method3396() {
         this.doubleValue = 0.0;
         this.doubleValue2 = 0.0;
      }
   }

   public static final class Entry3 {
      public double[][] doubleValues;
      public double doubleValue;
      public double doubleValue2;
      public double doubleValue3;
      public double doubleValue4;
      public double doubleValue5 = 40.0;
      public double doubleValue6 = 15.0;
      public int intValue = 200;
      public long longValue;
      public long longValue2;

      public double[] method3397(double[] doubleValues4, double[] doubleValues5, boolean[] flags, int intValue, double[] doubleValues3, double nextDouble, double d) {
         double[] doubles = new double[intValue];
         double[] doubles2 = new double[intValue];

         for (int i = 0; i < intValue; i++) {
            doubles[i] = flags[i] ? doubleValues4[i] : 0.0;
            doubles2[i] = flags[i] ? doubleValues5[i] : 0.0;
         }

         double d2 = intValue > 0 ? doubles[intValue - 1] : 0.0;
         double d3 = intValue > 0 ? doubles2[intValue - 1] : 0.0;
         double d4 = Math.max(-doubleValues3[0], d2 - doubleValues3[1]);
         double d5 = Math.min(doubleValues3[0], d2 + doubleValues3[1]);
         double d6 = 0.0;
         double d7 = 0.0;

         for (int i = Math.max(0, intValue - 3); i < intValue; i++) {
            d6 += doubles[i];
         }

         for (int i = Math.max(0, intValue - 19); i < intValue; i++) {
            d7 += Math.abs(doubles[i]);
         }

         d4 = Math.max(d4, -doubleValues3[2] - d6);
         d5 = Math.min(d5, doubleValues3[2] - d6);
         double d8 = Math.max(20.0 * doubleValues3[3] - d7, 0.0);
         d4 = Math.max(d4, -d8);
         d5 = Math.min(d5, d8);
         int n = Math.max(0, intValue - 199);
         int n2 = 0;
         int n3 = 0;

         for (int i = n; i < intValue; i++) {
            if (flags[i] && Math.abs(doubles[i]) > this.doubleValue5) {
               n2++;
            }

            boolean flag = false;

            for (int j = 1; j <= 3 && i - j >= n; j++) {
               int n4 = i - j;
               if (Math.abs(doubles[i]) > this.doubleValue6
                  && Math.abs(doubles[n4]) > this.doubleValue6
                  && Math.signum(doubles[i]) != Math.signum(doubles[n4])
                  && flags[i]
                  && flags[n4]) {
                  flag = true;
               }
            }

            if (flag) {
               n3++;
            }
         }

         double d9 = Math.max(0.0, Math.min(1.0, (this.doubleValue2 - n2) / Math.max(this.doubleValue2 - this.doubleValue, 1.0E-9)));
         boolean flag = nextDouble > (n2 >= this.doubleValue ? d9 : 1.0);
         if (flag) {
            d4 = Math.max(d4, -this.doubleValue5);
            d5 = Math.min(d5, this.doubleValue5);
         }

         double d10 = Math.max(0.0, Math.min(1.0, (this.doubleValue4 - n3) / Math.max(this.doubleValue4 - this.doubleValue3, 1.0E-9)));
         boolean flag2 = d > (n3 >= this.doubleValue3 ? d10 : 1.0);
         double d11 = 0.0;

         for (int i = 0; i < Math.min(3, intValue); i++) {
            double d12 = doubles[intValue - 1 - i];
            if (d11 == 0.0 && Math.abs(d12) > this.doubleValue6) {
               d11 = Math.signum(d12);
            }
         }

         if (flag2 && d11 > 0.0) {
            d4 = Math.max(d4, -this.doubleValue6);
         }

         if (flag2 && d11 < 0.0) {
            d5 = Math.min(d5, this.doubleValue6);
         }

         double d12 = Math.max(-doubleValues3[4], d3 - doubleValues3[5]);
         double d13 = Math.min(doubleValues3[4], d3 + doubleValues3[5]);
         if (d4 > d5) {
            d4 = d5 = (d4 + d5) / 2.0;
         }

         if (d12 > d13) {
            d12 = d13 = (d12 + d13) / 2.0;
         }

         return new double[]{d4, d5, d12, d13};
      }
   }

   public static final class Entry4 {
      public double doubleValue = Double.NaN;
      public double doubleValue2 = Double.NaN;
      public double doubleValue3 = Double.NaN;
      public int intValue;
      public boolean flag;
      public boolean flag2;
      public boolean flag3;
      public double doubleValue4 = Double.NaN;
      public double doubleValue5 = Double.NaN;
      public double doubleValue6 = Double.NaN;
      public int intValue2 = -1;
      public int intValue3;
      public boolean flag4;
      public double doubleValue7;
      public double doubleValue8;
      public double doubleValue9;
      public double doubleValue10;
      public boolean flag5 = true;
   }
}
