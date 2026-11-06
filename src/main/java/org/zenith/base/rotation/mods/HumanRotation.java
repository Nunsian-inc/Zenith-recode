package org.zenith.base.rotation.mods;

import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.util.Mth;
import org.zenith.Rotation;
import org.zenith.utility.misc.RotationContext;

public final class HumanRotation extends RotationContext {
   private static final float CONST_floatValue = 0.05F;
   private static final float CONST_floatValue2 = 0.045F;
   private static final float CONST_floatValue3 = 0.06F;
   private static final float CONST_floatValue4 = 3.5F;
   private static final float CONST_floatValue5 = 1.5F;
   private static final float CONST_floatValue6 = 2.5F;
   private static final long CONST_longValue = 250000000L;
   private static final long CONST_longValue2 = 400000000L;
   private static final float CONST_floatValue7 = 8.0F;
   private boolean active;
   private float floatValue;
   private float floatValue2;
   private float floatValue3;
   private float floatValue4;
   private float floatValue5;
   private float floatValue6;
   private float floatValue7;
   private float floatValue8;
   private float floatValue9;
   private float floatValue10;
   private float floatValue11;
   private int intValue;
   private float floatValue12 = 1.0F;
   private final float floatValue13 = ThreadLocalRandom.current().nextBoolean() ? 1.0F : -1.0F;
   private float floatValue14;
   private float floatValue15;
   private long longValue;
   private long longValue2;

   public Rotation method3653(Rotation rotation) {
      Rotation rotation2 = rotationManager.getEffectiveRotation();
      float floatValue = rotation2.getFloatValue();
      float f = rotation2.getFloatValue2();
      float f2 = floatValue + Mth.wrapDegrees(rotation.getFloatValue() - floatValue);
      float f3 = Mth.clamp(rotation.getFloatValue2(), -90.0F, 90.0F);
      long nanoTime = System.nanoTime();
      if (nanoTime - this.longValue > 250000000L) {
         this.active = false;
         this.intValue = 0;
      }

      this.longValue = nanoTime;
      if (this.active) {
         this.method3654(floatValue, f2, f3);
      }

      if (!this.active) {
         float hypot = (float)Math.hypot(f2 - floatValue, f3 - f);
         if (hypot <= this.floatValue12) {
            return rotation2;
         }

         this.method3655(floatValue, f, f2, f3, hypot, nanoTime);
      }

      if (this.intValue > 0) {
         this.intValue--;
         return rotation2;
      } else {
         float f4 = this.method3656(this.floatValue10 / this.floatValue9);
         this.floatValue10 = Math.min(this.floatValue10 + 1.0F, this.floatValue9);
         float f5 = this.floatValue10 / this.floatValue9;
         float f6 = this.method3656(f5);
         float yRot = method3657(this.floatValue, this.floatValue3, this.floatValue5, f6);
         float f7 = method3657(this.floatValue2, this.floatValue4, this.floatValue6, f6);
         float hypot = (float)Math.hypot(this.floatValue5 - this.floatValue, this.floatValue6 - this.floatValue2);
         float f8 = (f6 - f4) * hypot;
         ThreadLocalRandom current = ThreadLocalRandom.current();
         float f9 = 0.035F * f8 + 0.015F;
         this.floatValue14 = this.floatValue14 * 0.55F + (float)current.nextGaussian() * f9;
         this.floatValue15 = this.floatValue15 * 0.55F + (float)current.nextGaussian() * f9 * 0.8F;
         float f10 = 1.0F - f5;
         yRot += this.floatValue14 * f10;
         f7 += this.floatValue15 * f10;
         if (f5 >= 1.0F) {
            this.active = false;
            this.longValue2 = nanoTime;
         }

         yRot = floatValue + Mth.wrapDegrees(yRot - floatValue);
         return new Rotation(yRot, Mth.clamp(f7, -90.0F, 90.0F));
      }
   }

   private void method3654(float floatValue, float f, float f2) {
      float f3 = floatValue + Mth.wrapDegrees(this.floatValue7 - floatValue);
      float x = f - f3;
      float y = f2 - this.floatValue8;
      float hypot = (float)Math.hypot(x, y);
      if (!(hypot <= 1.0E-4F)) {
         float hypot2 = (float)Math.hypot(this.floatValue5 - this.floatValue, this.floatValue6 - this.floatValue2);
         if (hypot > Math.max(2.5F, hypot2 * 0.35F)) {
            this.active = false;
         } else {
            this.floatValue5 += x;
            this.floatValue6 = Mth.clamp(this.floatValue6 + y, -90.0F, 90.0F);
            this.floatValue3 += x * 0.5F;
            this.floatValue4 += y * 0.5F;
            this.floatValue7 = f;
            this.floatValue8 = f2;
         }
      }
   }

   private void method3655(float floatValue, float f, float f2, float f3, float hypot, long nanoTime) {
      ThreadLocalRandom current = ThreadLocalRandom.current();
      boolean flag = hypot < 8.0F && nanoTime - this.longValue2 < 400000000L;
      float f4 = flag ? 1.5F : 3.5F;
      float f5 = 0.045F + 0.06F * (float)(Math.log(hypot / f4 + 1.0F) / Math.log(2.0));
      f5 *= (float)Math.exp(current.nextGaussian() * 0.12);
      this.floatValue9 = Math.max(1.6F, f5 / 0.05F);
      this.floatValue10 = 0.0F;
      float f6 = (f2 - floatValue) / hypot;
      float f7 = (f3 - f) / hypot;
      float f8 = -f7;
      float f9;
      float f10;
      if (!flag && !(hypot < 3.0F)) {
         f9 = method3658(current, 0.95F, 0.035F, 0.85F, 1.04F);
         f10 = (float)current.nextGaussian() * 0.03F * hypot;
      } else {
         f9 = method3658(current, 0.99F, 0.03F, 0.9F, 1.04F);
         f10 = (float)current.nextGaussian() * 0.015F * hypot;
      }

      this.floatValue = floatValue;
      this.floatValue2 = f;
      this.floatValue5 = floatValue + (f2 - floatValue) * f9 + f8 * f10;
      this.floatValue6 = Mth.clamp(f + (f3 - f) * f9 + f6 * f10, -90.0F, 90.0F);
      float f11 = current.nextFloat() < 0.7F ? this.floatValue13 : -this.floatValue13;
      float f12 = f11 * Math.abs((float)current.nextGaussian()) * 0.07F * hypot;
      this.floatValue3 = (this.floatValue + this.floatValue5) * 0.5F + f8 * f12;
      this.floatValue4 = Mth.clamp((this.floatValue2 + this.floatValue6) * 0.5F + f6 * f12, -90.0F, 90.0F);
      this.floatValue11 = 0.8F + current.nextFloat() * 0.15F;
      this.intValue = !flag && hypot > 25.0F && current.nextFloat() < 0.5F ? 1 : 0;
      this.floatValue12 = 0.6F + current.nextFloat() * 0.7F;
      this.floatValue7 = f2;
      this.floatValue8 = f3;
      this.active = true;
   }

   private float method3656(float f) {
      float f2 = (float)Math.pow(Mth.clamp(f, 0.0F, 1.0F), this.floatValue11);
      return f2 * f2 * f2 * (10.0F - 15.0F * f2 + 6.0F * f2 * f2);
   }

   private static float method3657(float floatValue, float f, float f2, float f3) {
      float f4 = 1.0F - f3;
      return f4 * f4 * floatValue + 2.0F * f4 * f3 * f + f3 * f3 * f2;
   }

   private static float method3658(ThreadLocalRandom current, float f, float f2, float min, float max) {
      return Mth.clamp(f + (float)current.nextGaussian() * f2, min, max);
   }
}
