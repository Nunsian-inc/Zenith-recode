package org.zenith.utility.misc;

import java.util.Arrays;
import java.util.Random;
import org.zenith.utility.api.AimPolicy;

public final class AimPolicyRunner {
   private static final float CONST_floatValue = -8.0F;
   private static final float CONST_floatValue2 = 4.0F;
   private final AimPolicy aimPolicy;
   private final Random random = new Random();
   private final float[] floatValues;
   private final float[] floatValues2;
   private final float[] floatValues3;
   private final float[] floatValues4;
   private final float[] floatValues5;
   private final float[] floatValues6;
   private final float[] floatValues7;
   private final float[] floatValues8;
   private final float[] floatValues9;
   private final float[] floatValues10;
   private final float[] floatValues11;
   private final float[] floatValues12;
   private final float[] floatValues13;
   private final double[] doubleValues;
   private final int[] intValues;
   private final float[] floatValues14;
   private final float[] floatValues15;
   private final float[] floatValues16;
   private final float[] floatValues17;
   private final float[] floatValues18;
   private boolean flag;
   private int intValue;
   private int intValue2;

   public AimPolicyRunner(AimPolicy aimPolicy) {
      this.aimPolicy = aimPolicy;
      int n = Math.max(this.aimPolicy.intValue5, this.aimPolicy.intValue6);
      this.floatValues = new float[this.aimPolicy.intValue3];
      this.floatValues2 = new float[this.aimPolicy.intValue3];
      this.floatValues3 = new float[this.aimPolicy.intValue4];
      this.floatValues4 = new float[this.aimPolicy.intValue2];
      this.floatValues5 = new float[this.aimPolicy.intValue4];
      this.floatValues6 = new float[4 * this.aimPolicy.intValue3];
      this.floatValues7 = new float[4 * this.aimPolicy.intValue3];
      this.floatValues8 = new float[this.aimPolicy.intValue4 + 2 * this.aimPolicy.intValue9 + this.aimPolicy.intValue10];
      this.floatValues9 = new float[3 * this.aimPolicy.intValue4];
      this.floatValues10 = new float[3 * this.aimPolicy.intValue4];
      this.floatValues11 = new float[this.aimPolicy.intValue4 + this.aimPolicy.intValue9];
      this.floatValues12 = new float[this.aimPolicy.intValue4];
      this.floatValues13 = new float[n];
      this.doubleValues = new double[n];
      this.intValues = new int[2 * this.aimPolicy.intValue];
      this.floatValues14 = new float[this.intValues.length];
      this.floatValues15 = new float[this.aimPolicy.intValue10];
      this.floatValues16 = new float[this.aimPolicy.intValue4 + 2 * this.aimPolicy.intValue9];
      this.floatValues17 = new float[this.aimPolicy.intValue11];
      this.floatValues18 = new float[2 * this.aimPolicy.intValue10];
      this.reset();
   }

   public void reset() {
      Arrays.fill(this.floatValues, 0.0F);
      Arrays.fill(this.floatValues2, 0.0F);
      Arrays.fill(this.floatValues3, 0.0F);
      Arrays.fill(this.floatValues15, 0.0F);
      this.flag = false;
      this.intValue = this.aimPolicy.intValue7;
      this.intValue2 = this.aimPolicy.intValue8;
   }

   public int[][] method6749(float[] floats) {
      return this.method6751(floats, false);
   }

   public int[][] method6750(float[] floats) {
      return this.method6751(floats, true);
   }

   private int[][] method6751(float[] floats, boolean flag) {
      this.method6753(floats);
      this.method6754();
      method6762(this.aimPolicy.floatValues5, this.floatValues, this.aimPolicy.floatValues6, this.floatValues5);
      method6763(this.floatValues5);
      int[][] intses = new int[this.aimPolicy.intValue][2];

      for (int i = 0; i < this.aimPolicy.intValue; i++) {
         int n = 0;
         System.arraycopy(this.floatValues5, 0, this.floatValues8, n, this.floatValues5.length);
         n += this.floatValues5.length;
         System.arraycopy(this.aimPolicy.floatValues7[this.intValue], 0, this.floatValues8, n, this.aimPolicy.intValue9);
         n += this.aimPolicy.intValue9;
         System.arraycopy(this.aimPolicy.floatValues8[this.intValue2], 0, this.floatValues8, n, this.aimPolicy.intValue9);
         n += this.aimPolicy.intValue9;
         System.arraycopy(this.floatValues15, 0, this.floatValues8, n, this.aimPolicy.intValue10);
         this.method6755();
         method6762(this.aimPolicy.floatValues13, this.floatValues3, this.aimPolicy.floatValues14, this.floatValues13);
         int intValue = flag ? method6765(this.floatValues13, this.aimPolicy.intValue5) : this.method6766(this.floatValues13, this.aimPolicy.intValue5);
         int n2 = 2 * i;
         this.intValues[n2] = intValue;
         this.floatValues14[n2] = this.floatValues13[intValue];
         System.arraycopy(this.floatValues3, 0, this.floatValues11, 0, this.aimPolicy.intValue4);
         System.arraycopy(this.aimPolicy.floatValues7[intValue], 0, this.floatValues11, this.aimPolicy.intValue4, this.aimPolicy.intValue9);
         method6762(this.aimPolicy.floatValues15, this.floatValues11, this.aimPolicy.floatValues16, this.floatValues12);
         method6763(this.floatValues12);
         method6762(this.aimPolicy.floatValues17, this.floatValues12, this.aimPolicy.floatValues18, this.floatValues13);
         int intValue2 = flag ? method6765(this.floatValues13, this.aimPolicy.intValue6) : this.method6766(this.floatValues13, this.aimPolicy.intValue6);
         this.intValues[n2 + 1] = intValue2;
         this.floatValues14[n2 + 1] = this.floatValues13[intValue2];
         intses[i][0] = flag ? this.method6769(0, intValue) : this.method6768(0, intValue);
         intses[i][1] = flag ? this.method6769(1, intValue2) : this.method6768(1, intValue2);
         this.intValue = intValue;
         this.intValue2 = intValue2;
         boolean flag2 = intValue == this.aimPolicy.intValue7 && intValue2 == this.aimPolicy.intValue8;
         if (this.flag && flag2) {
            this.flag = false;
            Arrays.fill(this.floatValues15, 0.0F);
         } else if (!this.flag && !flag2) {
            this.flag = true;
            this.method6752(intValue, intValue2, flag);
         }
      }

      return intses;
   }

   private void method6752(int intValue, int n, boolean flag) {
      int n2 = 0;
      System.arraycopy(this.floatValues5, 0, this.floatValues16, n2, this.floatValues5.length);
      n2 += this.floatValues5.length;
      System.arraycopy(this.aimPolicy.floatValues7[intValue], 0, this.floatValues16, n2, this.aimPolicy.intValue9);
      n2 += this.aimPolicy.intValue9;
      System.arraycopy(this.aimPolicy.floatValues8[n], 0, this.floatValues16, n2, this.aimPolicy.intValue9);
      method6762(this.aimPolicy.floatValues19, this.floatValues16, this.aimPolicy.floatValues20, this.floatValues17);
      method6763(this.floatValues17);
      method6762(this.aimPolicy.floatValues21, this.floatValues17, this.aimPolicy.floatValues22, this.floatValues18);

      for (int i = 0; i < this.aimPolicy.intValue10; i++) {
         if (flag) {
            this.floatValues15[i] = this.floatValues18[i];
         } else {
            float f = Math.max(-8.0F, Math.min(4.0F, this.floatValues18[this.aimPolicy.intValue10 + i]));
            this.floatValues15[i] = this.floatValues18[i] + (float)(Math.exp(0.5 * f) * this.random.nextGaussian());
         }
      }
   }

   private void method6753(float[] floats) {
      for (int i = 0; i < this.aimPolicy.entrys.length; i++) {
         AimPolicy.Entry entry = this.aimPolicy.entrys[i];
         double d = floats[entry.intValue];
         if (entry.flag) {
            d = Math.signum(d) * Math.log1p(Math.abs(d) / entry.floatValue);
         }

         this.floatValues4[i] = (float)((d - entry.floatValue2) / entry.floatValue3);
      }
   }

   private void method6754() {
      method6762(this.aimPolicy.floatValues, this.floatValues4, this.aimPolicy.floatValues3, this.floatValues6);
      method6762(this.aimPolicy.floatValues2, this.floatValues, this.aimPolicy.floatValues4, this.floatValues7);
      int n = this.aimPolicy.intValue3;

      for (int i = 0; i < n; i++) {
         double d = method6764(this.floatValues6[i] + this.floatValues7[i]);
         double d2 = method6764(this.floatValues6[n + i] + this.floatValues7[n + i]);
         double tanh = Math.tanh(this.floatValues6[2 * n + i] + this.floatValues7[2 * n + i]);
         double d3 = method6764(this.floatValues6[3 * n + i] + this.floatValues7[3 * n + i]);
         double x = d2 * this.floatValues2[i] + d * tanh;
         this.floatValues2[i] = (float)x;
         this.floatValues[i] = (float)(d3 * Math.tanh(x));
      }
   }

   private void method6755() {
      method6762(this.aimPolicy.floatValues9, this.floatValues8, this.aimPolicy.floatValues11, this.floatValues9);
      method6762(this.aimPolicy.floatValues10, this.floatValues3, this.aimPolicy.floatValues12, this.floatValues10);
      int n = this.aimPolicy.intValue4;

      for (int i = 0; i < n; i++) {
         double d = method6764(this.floatValues9[i] + this.floatValues10[i]);
         double d2 = method6764(this.floatValues9[n + i] + this.floatValues10[n + i]);
         double tanh = Math.tanh(this.floatValues9[2 * n + i] + d * this.floatValues10[2 * n + i]);
         this.floatValues3[i] = (float)((1.0 - d2) * tanh + d2 * this.floatValues3[i]);
      }
   }

   public int[] method6756() {
      return (int[])this.intValues.clone();
   }

   public float method6757() {
      return this.floatValues14[0];
   }

   public double method6758() {
      return method6761(this.floatValues);
   }

   public double method6759() {
      return method6761(this.floatValues3);
   }

   public double method6760() {
      return method6761(this.floatValues15);
   }

   public boolean isFlag() {
      return this.flag;
   }

   private static double method6761(float[] floatValues) {
      double d = 0.0;

      for (float floatValue : floatValues) {
         d += (double)floatValue * floatValue;
      }

      return Math.sqrt(d);
   }

   private static void method6762(float[][] floatValues5, float[] floatValues, float[] floatValues6, float[] floatValues13) {
      for (int i = 0; i < floatValues5.length; i++) {
         double d = floatValues6[i];

         for (int j = 0; j < floatValues5[i].length; j++) {
            d += floatValues5[i][j] * floatValues[j];
         }

         floatValues13[i] = (float)d;
      }
   }

   private static void method6763(float[] floatValues5) {
      for (int i = 0; i < floatValues5.length; i++) {
         floatValues5[i] = (float)Math.tanh(floatValues5[i]);
      }
   }

   private static double method6764(double d) {
      return 1.0 / (1.0 + Math.exp(-d));
   }

   private static int method6765(float[] floatValues13, int n) {
      int n2 = 0;

      for (int i = 1; i < n; i++) {
         if (floatValues13[i] > floatValues13[n2]) {
            n2 = i;
         }
      }

      return n2;
   }

   private int method6766(float[] floatValues13, int n) {
      double d = Math.max((double)this.aimPolicy.floatValue, 1.0E-6);
      double d2 = Double.NEGATIVE_INFINITY;

      for (int i = 0; i < n; i++) {
         d2 = Math.max(d2, floatValues13[i] / d);
      }

      double d3 = 0.0;

      for (int i = 0; i < n; i++) {
         this.doubleValues[i] = Math.exp(floatValues13[i] / d - d2);
         d3 += this.doubleValues[i];
      }

      for (int i = 0; i < n; i++) {
         this.doubleValues[i] = this.doubleValues[i] / d3;
      }

      if (this.aimPolicy.floatValue2 < 1.0F) {
         this.method6767(n);
      }

      double nextDouble = this.random.nextDouble();
      double d4 = 0.0;

      for (int i = 0; i < n; i++) {
         d4 += this.doubleValues[i];
         if (nextDouble <= d4) {
            return i;
         }
      }

      return n - 1;
   }

   private void method6767(int n) {
      Integer[] integers = new Integer[n];

      for (int i = 0; i < n; i++) {
         integers[i] = i;
      }

      Arrays.sort(integers, (var1x, var2x) -> Double.compare(this.doubleValues[var2x], this.doubleValues[var1x]));
      double d = 0.0;
      boolean flag = false;
      Integer[] integers2 = integers;
      int length = integers.length;

      for (int i = 0; i < length; i++) {
         int n2 = integers2[i];
         if (flag) {
            this.doubleValues[n2] = 0.0;
         } else {
            d += this.doubleValues[n2];
            if (d >= this.aimPolicy.floatValue2) {
               flag = true;
            }
         }
      }

      for (int i = 0; i < n; i++) {
         this.doubleValues[i] = this.doubleValues[i] / d;
      }
   }

   private int method6768(int n, int intValue) {
      int n2 = this.aimPolicy.intValues[n][intValue];
      int n3 = this.aimPolicy.intValues2[n][intValue];
      if (n2 == n3) {
         return n2;
      } else {
         int n4 = Math.min(Math.abs(n2), Math.abs(n3));
         int n5 = Math.max(Math.abs(n2), Math.abs(n3));
         double d = Math.log(n4) + this.random.nextDouble() * (Math.log(n5 + 1.0) - Math.log(n4));
         int n6 = Math.max(n4, Math.min(n5, (int)Math.exp(d)));
         return n2 > 0 ? n6 : -n6;
      }
   }

   private int method6769(int n, int intValue) {
      int n2 = this.aimPolicy.intValues[n][intValue];
      int n3 = this.aimPolicy.intValues2[n][intValue];
      if (n2 == n3) {
         return n2;
      } else {
         int n4 = Math.min(Math.abs(n2), Math.abs(n3));
         int n5 = Math.max(Math.abs(n2), Math.abs(n3));
         int n6 = Math.max(n4, Math.min(n5, (int)Math.round(Math.sqrt((double)n4 * n5))));
         return n2 > 0 ? n6 : -n6;
      }
   }
}
