package org.zenith.utility.misc;

import net.minecraft.util.Mth;
import org.zenith.utility.game.Class28;
import org.zenith.utility.render.Glowboost;

public final class Class42 {
   public static final Class42.Handler handler = (d, d2, d3) -> false;
   private static Class42.Handler handler2 = handler;
   private static final float CONST_floatValue = 0.008333334F;
   private static final int CONST_intValue = 900;
   private static final float CONST_floatValue2 = (float) (Math.PI * 2);
   public final int intValue;
   public final int intValue2;
   public final float floatValue;
   private final double doubleValue;
   private final double doubleValue2;
   private final double doubleValue3;
   private final float floatValue2;
   private final float floatValue3;
   private final float floatValue4;
   private final float floatValue5;
   private final float floatValue6;
   private final float floatValue7;
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
   private final float[] floatValues14;
   private final boolean[] flags;
   private float time;

   public static void method7379(Class42.Handler handler22) {
      handler2 = handler22 == null ? handler : handler22;
   }

   public static Class42 method7380(int intValue, Class28 class2, int n, float floatValue) {
      return new Class42(intValue, class2, n, floatValue);
   }

   public boolean method7381(int intValue, int n, float floatValue) {
      return this.intValue == intValue && this.intValue2 == n && Math.abs(this.floatValue - floatValue) < 1.0E-4F;
   }

   private Class42(int intValue, Class28 class2, int n, float floatValue) {
      this.intValue = intValue;
      this.intValue2 = Math.max(1, n);
      this.floatValue = floatValue;
      this.doubleValue = class2.doubleValue;
      this.doubleValue2 = class2.doubleValue2;
      this.doubleValue3 = class2.doubleValue3;
      boolean flag = intValue == 2;
      this.floatValue2 = flag ? 4.2F : 3.4F;
      this.floatValue3 = flag ? 4.4F : 3.2F;
      this.floatValue4 = flag ? 0.42F : 0.28F;
      this.floatValue5 = flag ? 0.74F : 0.55F;
      this.floatValue6 = flag ? 0.35F : 0.45F;
      this.floatValue7 = flag ? 14.0F : 10.0F;
      int n2 = this.intValue2;
      this.floatValues = new float[n2];
      this.floatValues2 = new float[n2];
      this.floatValues3 = new float[n2];
      this.floatValues4 = new float[n2];
      this.floatValues5 = new float[n2];
      this.floatValues6 = new float[n2];
      this.floatValues8 = new float[n2];
      this.floatValues9 = new float[n2];
      this.floatValues10 = new float[n2];
      this.floatValues11 = new float[n2];
      this.floatValues12 = new float[n2];
      this.floatValues13 = new float[n2];
      this.floatValues7 = new float[n2];
      this.floatValues14 = new float[n2];
      this.flags = new boolean[n2];
      long longValue = class2.longValue2;

      for (int i = 0; i < n2; i++) {
         float f = (i + Glowboost.method3268(longValue, i, 3) * 0.8F) / n2 * (float) (Math.PI * 2);
         double d = Mth.cos(f);
         double d2 = Mth.sin(f);
         double d3 = -d2;
         float f2 = Glowboost.method3268(longValue, i, 0) * (float) (Math.PI * 2);
         float f3 = flag ? 0.15F + Glowboost.method3268(longValue, i, 1) * 1.55F : 0.22F + Glowboost.method3268(longValue, i, 1) * 1.15F;
         float f4 = Mth.sin(f3);
         float f5 = Mth.cos(f3);
         float f6 = Mth.cos(f2);
         float f7 = Mth.sin(f2);
         double d4 = d * f5 + d3 * f6 * f4;
         double d5 = f7 * f4 + (flag ? 0.35 : 0.0);
         double d6 = d2 * f5 + d * f6 * f4;
         float f8 = flag ? (2.0F + Glowboost.method3268(longValue, i, 2) * 4.5F) * floatValue : (1.4F + Glowboost.method3268(longValue, i, 2) * 2.6F) * floatValue;
         this.floatValues4[i] = (float)(d4 * f8);
         this.floatValues5[i] = (float)(d5 * f8);
         this.floatValues6[i] = (float)(d6 * f8);
         double d7 = Math.sqrt(d4 * d4 + d5 * d5 + d6 * d6);
         if (d7 < 1.0E-6) {
            d4 = 0.0;
            d5 = 1.0;
            d6 = 0.0;
            d7 = 1.0;
         }

         this.floatValues8[i] = (float)(d4 / d7);
         this.floatValues9[i] = (float)(d5 / d7);
         this.floatValues10[i] = (float)(d6 / d7);
         this.floatValues11[i] = this.floatValues8[i];
         this.floatValues12[i] = this.floatValues9[i];
         this.floatValues13[i] = this.floatValues10[i];
      }
   }

   public void method7382(float time) {
      if (!(time <= this.time)) {
         int n;
         for (n = 0; this.time < time && n < 900; n++) {
            this.method7383();
            this.time += 0.008333334F;
         }

         if (n >= 900) {
            this.time = time;
         }
      }
   }

   private void method7383() {
      float f = Math.max(0.0F, 1.0F - this.floatValue2 * 0.008333334F);

      for (int i = 0; i < this.intValue2; i++) {
         if (this.floatValues14[i] > 0.0F) {
            this.floatValues14[i] = Math.max(0.0F, this.floatValues14[i] - 0.045833334F);
         }

         if (!this.flags[i]) {
            this.floatValues4[i] = this.floatValues4[i] * f;
            this.floatValues5[i] = this.floatValues5[i] * f;
            this.floatValues6[i] = this.floatValues6[i] * f;
            this.floatValues7[i] = Math.max(-this.floatValue7, this.floatValues7[i] - this.floatValue3 * 0.008333334F);
            float f2 = this.floatValues4[i];
            float f3 = this.floatValues5[i] + this.floatValues7[i];
            float f4 = this.floatValues6[i];
            float floatValue = this.floatValues[i];
            float f5 = this.floatValues2[i];
            float f6 = this.floatValues3[i];
            float floatValue2 = floatValue + f2 * 0.008333334F;
            float f7 = f5 + f3 * 0.008333334F;
            float f8 = f6 + f4 * 0.008333334F;
            boolean flag = false;
            boolean flag2 = false;
            if (this.method7384(floatValue2, f5, f6)) {
               f2 = -f2 * this.floatValue4;
               floatValue2 = floatValue;
               flag = true;
            }

            if (this.method7384(floatValue2, f7, f6)) {
               if (f3 < 0.0F) {
                  flag2 = true;
               }

               f3 = -f3 * this.floatValue4;
               f7 = f5;
               f2 *= this.floatValue5;
               f4 *= this.floatValue5;
               flag = true;
            }

            if (this.method7384(floatValue2, f7, f8)) {
               f4 = -f4 * this.floatValue4;
               f8 = f6;
               flag = true;
            }

            this.floatValues[i] = floatValue2;
            this.floatValues2[i] = f7;
            this.floatValues3[i] = f8;
            float x = f2 * f2 + f3 * f3 + f4 * f4;
            if (x > 1.0E-8F) {
               float f9 = 1.0F / Mth.sqrt(x);
               this.floatValues8[i] = f2 * f9;
               this.floatValues9[i] = f3 * f9;
               this.floatValues10[i] = f4 * f9;
            }

            if (flag) {
               this.floatValues4[i] = f2;
               this.floatValues5[i] = f3;
               this.floatValues6[i] = f4;
               this.floatValues7[i] = 0.0F;
               this.floatValues14[i] = 1.0F;
               if (flag2 && x < this.floatValue6 * this.floatValue6) {
                  this.flags[i] = true;
                  this.floatValues4[i] = 0.0F;
                  this.floatValues5[i] = 0.0F;
                  this.floatValues6[i] = 0.0F;
                  this.floatValues7[i] = 0.0F;
               }
            }
         }
      }
   }

   private boolean method7384(float f, float f2, float f3) {
      return handler2.solid(this.doubleValue + f, this.doubleValue2 + f2, this.doubleValue3 + f3);
   }

   public double method7385(int n) {
      return this.doubleValue + this.floatValues[n];
   }

   public double method7386(int n) {
      return this.doubleValue2 + this.floatValues2[n];
   }

   public double method7387(int n) {
      return this.doubleValue3 + this.floatValues3[n];
   }

   public float method7388(int n) {
      return this.flags[n] ? 0.0F : this.floatValues4[n];
   }

   public float method7389(int n) {
      return this.flags[n] ? 0.0F : this.floatValues5[n] + this.floatValues7[n];
   }

   public float method7390(int n) {
      return this.flags[n] ? 0.0F : this.floatValues6[n];
   }

   public float method7391(int n) {
      float f = this.method7388(n);
      float f2 = this.method7389(n);
      float f3 = this.method7390(n);
      return Mth.sqrt(f * f + f2 * f2 + f3 * f3);
   }

   public float method7392(int n) {
      return this.floatValues11[n];
   }

   public float method7393(int n) {
      return this.floatValues12[n];
   }

   public float method7394(int n) {
      return this.floatValues13[n];
   }

   public float method7395(int n) {
      return this.floatValues8[n];
   }

   public float method7396(int n) {
      return this.floatValues9[n];
   }

   public float method7397(int n) {
      return this.floatValues10[n];
   }

   public float method7398(int n) {
      return this.floatValues14[n];
   }

   public boolean method7399(int n) {
      return this.flags[n];
   }

   public interface Handler {
      boolean solid(double d, double d2, double d3);
   }
}
