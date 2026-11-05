package org.zenith.utility.misc;

import java.time.Instant;
import java.time.ZoneId;

public final class Class16 {
   private static final float CONST_floatValue = (float) (Math.PI / 180.0);
   private static final long CONST_longValue = System.nanoTime();
   private static int intValue;
   private static long longValue;
   private static boolean flag;
   private final float[] floatValues = new float[9];
   private float[] floatValues2 = new float[0];
   private float[] floatValues3 = new float[0];
   public float[] values = new float[0];

   public float[] method2139(Class15 key, AnimationState animationState, float floatValue, Class2 class2) {
      int n = key.method2030();
      if (this.values.length != n * 9) {
         this.values = new float[n * 9];
         this.floatValues2 = new float[n * 2];
         this.floatValues3 = new float[n * 2];
      }

      if (animationState != null) {
         animationState.method6542(key, floatValue);
      }

      for (int i = 0; i < n; i++) {
         if (animationState != null) {
            animationState.method6546(key, i, floatValue, this.floatValues);
         } else {
            method2146(this.floatValues);
         }

         int n2 = i * 9;
         this.values[n2] = key.floatValues2[i * 3] + this.floatValues[0] * (float) (Math.PI / 180.0);
         this.values[n2 + 1] = key.floatValues2[i * 3 + 1] + this.floatValues[1] * (float) (Math.PI / 180.0);
         this.values[n2 + 2] = key.floatValues2[i * 3 + 2] + this.floatValues[2] * (float) (Math.PI / 180.0);
         this.values[n2 + 3] = this.floatValues[3];
         this.values[n2 + 4] = this.floatValues[4];
         this.values[n2 + 5] = this.floatValues[5];
         this.values[n2 + 6] = this.floatValues[6];
         this.values[n2 + 7] = this.floatValues[7];
         this.values[n2 + 8] = this.floatValues[8];
         if (key.type2s[i] == Class15.Type2.CLOCK) {
            this.method2143(key, i);
         } else if (class2 != null) {
            this.method2142(key, i, class2, floatValue);
         }
      }

      return this.values;
   }

   public void method2140(Class15 key) {
      int n = Math.min(key.method2030(), this.values.length / 9);

      for (int i = 0; i < n; i++) {
         if (key.intValues[i] < 0 && key.intValues2[i] >= 0) {
            int n2 = i * 9;
            this.values[n2] = key.floatValues2[i * 3];
            this.values[n2 + 1] = key.floatValues2[i * 3 + 1];
            this.values[n2 + 2] = key.floatValues2[i * 3 + 2];
            this.values[n2 + 3] = 0.0F;
            this.values[n2 + 4] = 0.0F;
            this.values[n2 + 5] = 0.0F;
            this.values[n2 + 6] = 1.0F;
            this.values[n2 + 7] = 1.0F;
            this.values[n2 + 8] = 1.0F;
         }
      }
   }

   public void method2141(Class15 key, AnimationState animationState, String text, float powerForTime, float f) {
      if (key.method2032(text) != null && !(f <= 0.0F)) {
         for (int i = 0; i < key.method2030(); i++) {
            if (key.method2032(text).method6241(i)) {
               animationState.method6548(key, text, i, powerForTime, this.floatValues);
               int n = i * 9;
               this.values[n] = this.values[n] + this.floatValues[0] * (float) (Math.PI / 180.0) * f;
               this.values[n + 1] = this.values[n + 1] + this.floatValues[1] * (float) (Math.PI / 180.0) * f;
               this.values[n + 2] = this.values[n + 2] + this.floatValues[2] * (float) (Math.PI / 180.0) * f;
               this.values[n + 3] = this.values[n + 3] + this.floatValues[3] * f;
               this.values[n + 4] = this.values[n + 4] + this.floatValues[4] * f;
               this.values[n + 5] = this.values[n + 5] + this.floatValues[5] * f;
               this.values[n + 6] = this.values[n + 6] * (1.0F + (this.floatValues[6] - 1.0F) * f);
               this.values[n + 7] = this.values[n + 7] * (1.0F + (this.floatValues[7] - 1.0F) * f);
               this.values[n + 8] = this.values[n + 8] * (1.0F + (this.floatValues[8] - 1.0F) * f);
            }
         }
      }
   }

   private void method2142(Class15 key, int n, Class2 class2, float floatValue) {
      Class15.Type2 type2 = key.type2s[n];
      if (type2 != Class15.Type2.NONE) {
         float f = key.floatValues3[n * 4];
         float f2 = key.floatValues3[n * 4 + 1];
         float f3 = key.floatValues3[n * 4 + 2];
         float f4 = key.floatValues3[n * 4 + 3];
         int n2 = n * 9;
         switch (type2) {
            case CAPE:
               float f5 = f == 0.0F ? 1.0F : f;
               this.values[n2] = this.values[n2] + (6.0F + class2.floatValue2 / 2.0F + class2.floatValue) * (float) (Math.PI / 180.0) * f5;
               this.values[n2 + 2] = this.values[n2 + 2] + class2.floatValue3 / 2.0F * (float) (Math.PI / 180.0) * f5;
               this.values[n2 + 1] = this.values[n2 + 1] - class2.floatValue3 / 2.0F * (float) (Math.PI / 180.0) * f5 * 0.5F;
               break;
            case LEGS_FRONT:
               this.values[n2] = this.values[n2] + Math.min(0.0F, Math.min(class2.floatValue4, class2.floatValue5)) * (f == 0.0F ? 1.0F : f);
               break;
            case LEGS_BACK:
               this.values[n2] = this.values[n2] + Math.max(0.0F, Math.max(class2.floatValue4, class2.floatValue5)) * (f == 0.0F ? 1.0F : f);
               break;
            case SPRING:
               this.method2144(n, class2, f == 0.0F ? 70.0F : f, f2 == 0.0F ? 9.0F : f2, f3 == 0.0F ? 1.0F : f3, f4 == 0.0F ? 35.0F : f4);
               break;
            case SWAY:
               float f6 = f == 0.0F ? 6.0F : f;
               float f7 = f2 == 0.0F ? 0.5F : f2;
               int n3 = Math.max(0, Math.min(2, (int)f3));
               this.values[n2 + n3] = this.values[n2 + n3]
                  + (float)Math.sin((floatValue * f7 + f4) * Math.PI * 2.0) * f6 * (float) (Math.PI / 180.0);
               break;
            case CLOTH:
               float f8 = f == 0.0F ? 0.35F : f;
               float f9 = f2 == 0.0F ? 5.0F : f2;
               float f10 = f3 == 0.0F ? 0.9F : f3;
               float f11 = Math.min(1.5F, class2.floatValue6 + (class2.flag2 ? 0.6F : 0.0F));
               float f12 = (float)Math.sin((floatValue * f10 * (1.0F + f11 * 1.6F) - f4) * Math.PI * 2.0);
               float f13 = (float)Math.sin((floatValue * f10 * 2.3F * (1.0F + f11) - f4 * 1.7F) * Math.PI * 2.0);
               this.values[n2] = this.values[n2]
                  + ((6.0F + class2.floatValue2 / 2.0F + class2.floatValue) * f8 + f12 * f9 * (0.35F + f11)) * (float) (Math.PI / 180.0);
               this.values[n2 + 2] = this.values[n2 + 2]
                  + (class2.floatValue3 / 2.0F * f8 + f13 * f9 * 0.25F * (0.2F + f11)) * (float) (Math.PI / 180.0);
         }
      }
   }

   private void method2143(Class15 key, int n) {
      int n2 = (int)key.floatValues3[n * 4];
      if (n2 >= 3) {
         double d = (System.nanoTime() - CONST_longValue) / 1.0E9;
         float f = key.floatValues3[n * 4 + 1];
         float f2 = key.floatValues3[n * 4 + 2];
         double d2 = n2 == 3 ? d * f + f2 : Math.sin((d * key.floatValues3[n * 4 + 3] + f2) * Math.PI * 2.0) * f;
         this.values[n * 9] = (float)Math.toRadians(d2 % 360.0);
      } else {
         Instant now = Instant.now();
         long nanoTime = System.nanoTime();
         if (!flag || nanoTime - longValue > 1000000000L) {
            intValue = ZoneId.systemDefault().getRules().getOffset(now).getTotalSeconds();
            longValue = nanoTime;
            flag = true;
         }

         long floorMod = Math.floorMod(now.getEpochSecond() + intValue, 86400L);
         int n3 = (int)(floorMod / 3600L);
         int n4 = (int)(floorMod / 60L % 60L);
         double d = floorMod % 60L + now.getNano() / 1.0E9;

         double angdeg = switch (n2) {
            case 0 -> d * 6.0;
            case 1 -> (n4 + d / 60.0) * 6.0;
            default -> (n3 % 12 + n4 / 60.0 + d / 3600.0) * 30.0;
         };
         this.values[n * 9] = (float)Math.toRadians(angdeg);
      }
   }

   private void method2144(int n, Class2 class2, float f, float f2, float f3, float f4) {
      float f5 = Math.min(Math.max(class2.floatValue8, 0.0F), 0.05F);
      float f6 = method2145(class2.floatValue2 * 0.35F * f3 + (class2.flag2 ? -8.0F * f3 : 0.0F), f4);
      float f7 = method2145(-class2.floatValue7 * 0.04F * f3, f4);
      int n2 = n * 2;
      if (f5 > 0.0F) {
         for (int i = 0; i < 2; i++) {
            float f8 = i == 0 ? f6 : f7;
            float f9 = f * (f8 - this.floatValues2[n2 + i]) - f2 * this.floatValues3[n2 + i];
            this.floatValues3[n2 + i] = this.floatValues3[n2 + i] + f9 * f5;
            this.floatValues2[n2 + i] = this.floatValues2[n2 + i] + this.floatValues3[n2 + i] * f5;
            this.floatValues2[n2 + i] = method2145(this.floatValues2[n2 + i], f4 * 1.4F);
         }
      }

      int n3 = n * 9;
      this.values[n3] = this.values[n3] + this.floatValues2[n2] * (float) (Math.PI / 180.0);
      this.values[n3 + 2] = this.values[n3 + 2] + this.floatValues2[n2 + 1] * (float) (Math.PI / 180.0);
   }

   private static float method2145(float f, float f2) {
      return Math.max(-f2, Math.min(f2, f));
   }

   private static void method2146(float[] floatValues) {
      floatValues[0] = floatValues[1] = floatValues[2] = 0.0F;
      floatValues[3] = floatValues[4] = floatValues[5] = 0.0F;
      floatValues[6] = floatValues[7] = floatValues[8] = 1.0F;
   }
}
