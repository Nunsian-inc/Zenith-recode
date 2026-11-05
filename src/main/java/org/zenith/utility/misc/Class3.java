package org.zenith.utility.misc;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class Class3 {
   private static final Minecraft mc = Minecraft.getInstance();
   public static final double CONST_doubleValue = 0.05;
   private static final int CONST_intValue = 5;
   private static final double CONST_doubleValue2 = 0.08;
   private static final double CONST_doubleValue3 = -0.6;
   public final Class15.Type4 type4;
   public final double doubleValue;
   public final double doubleValue2;
   public double doubleValue3;
   public double doubleValue4;
   public double doubleValue5;
   public double doubleValue6;
   public double doubleValue7;
   public double doubleValue8;
   public double doubleValue9;
   public double doubleValue10;
   public double doubleValue11;
   public float floatValue;
   public float floatValue2;
   public float floatValue3;
   public float floatValue4;
   public boolean flag;
   public boolean flag2;
   public boolean flag3;
   public double doubleValue12;
   public double doubleValue13;
   public double doubleValue14;

   public Class3(Class15.Type4 type4, double doubleValue, double d) {
      this.type4 = type4;
      this.doubleValue = doubleValue;
      this.doubleValue2 = d;
   }

   public boolean method423() {
      return this.type4 == Class15.Type4.FLYER || this.type4 == Class15.Type4.FLOATER;
   }

   public int method424(double d) {
      this.doubleValue12 = Math.min(this.doubleValue12 + Math.max(0.0, d), 0.25);

      int n;
      for (n = 0; this.doubleValue12 >= 0.05 && n < 5; n++) {
         this.doubleValue12 -= 0.05;
      }

      return n;
   }

   public void begin() {
      this.doubleValue6 = this.doubleValue3;
      this.doubleValue7 = this.doubleValue4;
      this.doubleValue8 = this.doubleValue5;
      this.floatValue2 = this.floatValue;
      this.floatValue4 = this.floatValue3;
      this.flag3 = false;
   }

   public void method425(Vec3 vec) {
      this.doubleValue3 = this.doubleValue6 = vec.x;
      this.doubleValue4 = this.doubleValue7 = vec.y;
      this.doubleValue5 = this.doubleValue8 = vec.z;
      this.doubleValue9 = this.doubleValue10 = this.doubleValue11 = 0.0;
      this.doubleValue12 = 0.0;
   }

   public void method426(double x, double y, double z, double d, double hover) {
      double d2 = x - this.doubleValue3;
      double d3 = z - this.doubleValue5;
      double d4 = Math.sqrt(d2 * d2 + d3 * d3);
      if (d4 > 0.05) {
         double d5 = Math.min(1.0, d4 / 2.0) * d;
         double d6 = !this.method423() && !this.flag ? 0.25 : 1.0;
         double d7 = this.method423() ? 0.062 : 0.06;
         this.doubleValue9 += d2 / d4 * d5 * d7 * d6;
         this.doubleValue11 += d3 / d4 * d5 * d7 * d6;
      }

      if (this.method423()) {
         this.doubleValue14 += 0.08;
         double d5 = y + hover + Math.sin(this.doubleValue14) * (this.type4 == Class15.Type4.FLOATER ? 0.12 : 0.06);
         double d6 = Mth.clamp(d5 - this.doubleValue4, -0.25, 0.25);

         while (this.method434(this.doubleValue3, this.doubleValue4 + d6, this.doubleValue5) && d6 < 1.0) {
            d6 += 0.1;
         }

         this.doubleValue4 += d6 * 0.25;
         this.doubleValue10 = 0.0;
         this.flag = false;
         this.method432(false);
         this.method433(false);
         this.doubleValue9 *= 0.85;
         this.doubleValue11 *= 0.85;
      } else {
         this.method430();
         this.method431();
         this.method432(true);
         this.method433(true);
         float f = this.flag ? 0.78F : 0.91F;
         this.doubleValue9 *= f;
         this.doubleValue11 *= f;
      }

      double d5 = this.doubleValue3 - this.doubleValue6;
      double d6 = this.doubleValue5 - this.doubleValue8;
      this.doubleValue13 = Math.sqrt(d5 * d5 + d6 * d6) / 0.05;
   }

   public void method427(double d) {
      if (this.flag && this.doubleValue10 <= 0.0) {
         this.doubleValue10 = d;
         this.flag = false;
      }
   }

   public void method428(double x, double z, float max) {
      float f = (float)Math.toDegrees(Math.atan2(z - this.doubleValue5, x - this.doubleValue3)) - 90.0F;
      float f2 = Mth.wrapDegrees(f - this.floatValue);
      this.floatValue = this.floatValue + Mth.clamp(f2 * 0.28F, -max, max);
   }

   public void method429(double x, double z) {
      float f = (float)Math.toDegrees(Math.atan2(z - this.doubleValue5, x - this.doubleValue3)) - 90.0F;
      float f2 = Mth.clamp(Mth.wrapDegrees(f - this.floatValue), -55.0F, 55.0F);
      this.floatValue3 = this.floatValue3 + (f2 - this.floatValue3) * 0.2F;
   }

   private void method430() {
      this.doubleValue10 -= 0.08;
      this.doubleValue10 *= 0.98;
      if (this.doubleValue10 < -0.6) {
         this.doubleValue10 = -0.6;
      }
   }

   private void method431() {
      boolean flag = this.flag;
      this.flag = false;
      if (!this.method434(this.doubleValue3, this.doubleValue4 + this.doubleValue10, this.doubleValue5)) {
         this.doubleValue4 = this.doubleValue4 + this.doubleValue10;
      } else {
         if (this.doubleValue10 < 0.0) {
            double d = this.doubleValue4 + this.doubleValue10;
            double d2 = this.doubleValue4;

            for (int i = 0; i < 8; i++) {
               double minY = (d + d2) * 0.5;
               if (this.method434(this.doubleValue3, minY, this.doubleValue5)) {
                  d = minY;
               } else {
                  d2 = minY;
               }
            }

            this.doubleValue4 = d2;
            this.flag = true;
         }

         this.doubleValue10 = 0.0;
      }

      if (!this.flag && this.method434(this.doubleValue3, this.doubleValue4 - 0.02, this.doubleValue5)) {
         this.flag = true;
      }

      this.flag3 = this.flag && !flag;
   }

   private void method432(boolean flag) {
      if (!(Math.abs(this.doubleValue9) <= 1.0E-5)) {
         if (!this.method434(this.doubleValue3 + this.doubleValue9, this.doubleValue4, this.doubleValue5)) {
            this.doubleValue3 = this.doubleValue3 + this.doubleValue9;
         } else {
            for (double d = 0.1; d <= (flag ? 0.6 : 1.2); d += 0.1) {
               if (!this.method434(this.doubleValue3 + this.doubleValue9, this.doubleValue4 + d, this.doubleValue5)
                  && !this.method434(this.doubleValue3, this.doubleValue4 + d, this.doubleValue5)) {
                  this.doubleValue4 += d;
                  this.doubleValue3 = this.doubleValue3 + this.doubleValue9;
                  return;
               }
            }

            if (flag && this.flag && !this.method434(this.doubleValue3 + Math.signum(this.doubleValue9) * 0.35, this.doubleValue4 + 1.1, this.doubleValue5)) {
               this.doubleValue10 = 0.52;
            }

            this.doubleValue9 = 0.0;
         }
      }
   }

   private void method433(boolean flag) {
      if (!(Math.abs(this.doubleValue11) <= 1.0E-5)) {
         if (!this.method434(this.doubleValue3, this.doubleValue4, this.doubleValue5 + this.doubleValue11)) {
            this.doubleValue5 = this.doubleValue5 + this.doubleValue11;
         } else {
            for (double d = 0.1; d <= (flag ? 0.6 : 1.2); d += 0.1) {
               if (!this.method434(this.doubleValue3, this.doubleValue4 + d, this.doubleValue5 + this.doubleValue11)
                  && !this.method434(this.doubleValue3, this.doubleValue4 + d, this.doubleValue5)) {
                  this.doubleValue4 += d;
                  this.doubleValue5 = this.doubleValue5 + this.doubleValue11;
                  return;
               }
            }

            if (flag && this.flag && !this.method434(this.doubleValue3, this.doubleValue4 + 1.1, this.doubleValue5 + Math.signum(this.doubleValue11) * 0.35)) {
               this.doubleValue10 = 0.52;
            }

            this.doubleValue11 = 0.0;
         }
      }
   }

   public boolean method434(double d, double minY, double d2) {
      return mc.level != null
         && !mc.level
            .noCollision(
               new AABB(d - this.doubleValue, minY, d2 - this.doubleValue, d + this.doubleValue, minY + this.doubleValue2, d2 + this.doubleValue)
            );
   }

   public Vec3 method435(float alpha1) {
      return new Vec3(
         Mth.lerp((double)alpha1, this.doubleValue6, this.doubleValue3),
         Mth.lerp((double)alpha1, this.doubleValue7, this.doubleValue4),
         Mth.lerp((double)alpha1, this.doubleValue8, this.doubleValue5)
      );
   }

   public float method436(float alpha1) {
      return this.floatValue2 + Mth.wrapDegrees(this.floatValue - this.floatValue2) * alpha1;
   }

   public float method437(float alpha1) {
      return this.floatValue4 + (this.floatValue3 - this.floatValue4) * alpha1;
   }

   public float method438() {
      return (float)Mth.clamp(this.doubleValue12 / 0.05, 0.0, 1.0);
   }
}
