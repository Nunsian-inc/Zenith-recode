package org.zenith.utility.game;

import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class PetFollowerPhysics {
   private static final Minecraft mc = Minecraft.getInstance();
   private static final double CONST_doubleValue = 0.05;
   private static final int CONST_intValue = 5;
   private static final double CONST_doubleValue2 = 0.12;
   private static final double CONST_doubleValue3 = 0.72;
   private static final double CONST_doubleValue4 = 0.08;
   private static final double CONST_doubleValue5 = 0.98;
   private static final double CONST_doubleValue6 = -0.5;
   private static final float CONST_floatValue = 0.6F;
   private static final float CONST_floatValue2 = 0.91F;
   private static final double CONST_doubleValue7 = 0.52;
   private static final double CONST_doubleValue8 = 0.45;
   private static final double CONST_doubleValue9 = 2.0;
   private static final double CONST_doubleValue10 = 10.0;
   private static final double CONST_doubleValue11 = 1.1;
   private double doubleValue;
   private double doubleValue2;
   private double doubleValue3;
   private double doubleValue4;
   private double doubleValue5;
   private double doubleValue6;
   private double doubleValue7;
   private double doubleValue8;
   private double doubleValue9;
   private float floatValue;
   private float floatValue2;
   private boolean flag;
   private boolean flag2;
   private double doubleValue10;
   private long longValue;
   private int ticks;
   private int intValue = ThreadLocalRandom.current().nextBoolean() ? 1 : -1;
   private int intValue2;
   private boolean flag3;
   private double doubleValue11;

   public long method455() {
      return this.longValue;
   }

   public void method456(long longValue) {
      this.longValue = longValue;
   }

   public boolean method457() {
      return this.flag3;
   }

   public void method458(Vec3 vec, float f, boolean flag, boolean flag2, LivingEntity livingEntity, double d) {
      if (!this.flag2) {
         this.method461(vec);
         this.flag2 = true;
      }

      this.doubleValue10 = Math.min(this.doubleValue10 + Math.max(0.0, d), 0.25);
      int n = 0;

      while (this.doubleValue10 >= 0.05 && n < 5) {
         this.doubleValue10 -= 0.05;
         n++;
         this.method462(vec, f, flag, flag2, livingEntity);
      }
   }

   public Vec3 method459() {
      float alpha1 = (float)Mth.clamp(this.doubleValue10 / 0.05, 0.0, 1.0);
      return new Vec3(
         Mth.lerp((double)alpha1, this.doubleValue4, this.doubleValue),
         Mth.lerp((double)alpha1, this.doubleValue5, this.doubleValue2),
         Mth.lerp((double)alpha1, this.doubleValue6, this.doubleValue3)
      );
   }

   public float method460() {
      float f = (float)Mth.clamp(this.doubleValue10 / 0.05, 0.0, 1.0);
      return this.floatValue2 + Mth.wrapDegrees(this.floatValue - this.floatValue2) * f;
   }

   private void method461(Vec3 vec) {
      this.doubleValue = this.doubleValue4 = vec.x;
      this.doubleValue2 = this.doubleValue5 = vec.y;
      this.doubleValue3 = this.doubleValue6 = vec.z;
      this.floatValue = this.floatValue2 = 0.0F;
      this.doubleValue7 = this.doubleValue8 = this.doubleValue9 = 0.0;
      this.doubleValue10 = 0.0;
   }

   private void method462(Vec3 vec, float f, boolean flag, boolean flag2, LivingEntity livingEntity) {
      this.doubleValue4 = this.doubleValue;
      this.doubleValue5 = this.doubleValue2;
      this.doubleValue6 = this.doubleValue3;
      this.floatValue2 = this.floatValue;
      this.ticks++;
      if (!(method470(this.doubleValue, this.doubleValue3, vec.x, vec.z) > 10.0) && !(Math.abs(this.doubleValue2 - vec.y) > 10.0)) {
         if (!flag2 && !flag) {
            this.method463();
            this.method464();
            this.method465(vec, livingEntity, true);
         } else {
            double d = flag2 ? Math.sin(this.ticks * 0.08) * 0.08 : 0.0;
            double d2 = vec.y + (flag2 ? 1.1 : 0.0) + d;
            this.doubleValue2 = this.doubleValue2 + (d2 - this.doubleValue2) * 0.2;
            this.doubleValue8 = 0.0;
            this.flag = false;
            this.method465(vec, livingEntity, false);
         }

         this.method469(vec, livingEntity);
         double d = this.doubleValue - this.doubleValue4;
         double d2 = this.doubleValue3 - this.doubleValue6;
         this.doubleValue11 = Math.sqrt(d * d + d2 * d2) / 0.05;
         this.flag3 = (this.doubleValue11 > (this.flag3 ? 0.5 : 0.9));
      } else {
         this.method461(vec);
      }
   }

   private void method463() {
      this.doubleValue8 -= 0.08;
      this.doubleValue8 *= 0.98;
      if (this.doubleValue8 < -0.5) {
         this.doubleValue8 = -0.5;
      }
   }

   private void method464() {
      this.flag = false;
      if (!method472(this.doubleValue, this.doubleValue2 + this.doubleValue8, this.doubleValue3)) {
         this.doubleValue2 = this.doubleValue2 + this.doubleValue8;
      } else {
         if (this.doubleValue8 < 0.0) {
            double d = this.doubleValue2 + this.doubleValue8;
            double d2 = this.doubleValue2;

            for (int i = 0; i < 8; i++) {
               double minY = (d + d2) * 0.5;
               if (method472(this.doubleValue, minY, this.doubleValue3)) {
                  d = minY;
               } else {
                  d2 = minY;
               }
            }

            this.doubleValue2 = d2;
            this.flag = true;
         }

         this.doubleValue8 = 0.0;
      }

      if (!this.flag && method472(this.doubleValue, this.doubleValue2 - 0.02, this.doubleValue3)) {
         this.flag = true;
      }
   }

   private void method465(Vec3 vec, LivingEntity livingEntity, boolean flag) {
      double d = !this.flag && flag ? 0.2 : 1.0;
      if (livingEntity != null && livingEntity.isAlive()) {
         this.method468(vec, livingEntity);
      } else {
         this.intValue2 = 0;
         double d2;
         if ((d2 = method470(this.doubleValue, this.doubleValue3, vec.x, vec.z)) > 2.0) {
            double d3 = Math.min(1.0, (d2 - 2.0) / 3.0) * d;
            double d4 = (vec.x - this.doubleValue) / d2;
            double d5 = (vec.z - this.doubleValue3) / d2;
            this.doubleValue7 += d4 * d3;
            this.doubleValue9 += d5 * d3;
         } else if (d2 < 0.1) {
            double d3 = ThreadLocalRandom.current().nextDouble() * Math.PI * 2.0;
            this.doubleValue7 = this.doubleValue7 + -Math.sin(d3) * 0.1;
            this.doubleValue9 = this.doubleValue9 + Math.cos(d3) * 0.1;
         }
      }

      if (flag) {
         this.method466();
         this.method467();
      } else {
         this.doubleValue = this.doubleValue + this.doubleValue7;
         this.doubleValue3 = this.doubleValue3 + this.doubleValue9;
      }

      float f = !this.flag && flag ? 0.91F : 0.6F;
      this.doubleValue7 *= f;
      this.doubleValue9 *= f;
   }

   private void method466() {
      if (!(Math.abs(this.doubleValue7) <= 1.0E-5)) {
         if (!method472(this.doubleValue + this.doubleValue7, this.doubleValue2, this.doubleValue3)) {
            this.doubleValue = this.doubleValue + this.doubleValue7;
         } else {
            for (double d = 0.1; d <= 0.6; d += 0.1) {
               if (!method472(this.doubleValue + this.doubleValue7, this.doubleValue2 + d, this.doubleValue3)
                  && !method472(this.doubleValue, this.doubleValue2 + d, this.doubleValue3)) {
                  this.doubleValue2 += d;
                  this.doubleValue = this.doubleValue + this.doubleValue7;
                  return;
               }
            }

            if (this.flag
               && this.doubleValue8 <= 0.0
               && !method472(this.doubleValue + Math.signum(this.doubleValue7) * 0.35, this.doubleValue2 + 1.0, this.doubleValue3)) {
               this.doubleValue8 = 0.52;
            }

            this.doubleValue7 = 0.0;
         }
      }
   }

   private void method467() {
      if (!(Math.abs(this.doubleValue9) <= 1.0E-5)) {
         if (!method472(this.doubleValue, this.doubleValue2, this.doubleValue3 + this.doubleValue9)) {
            this.doubleValue3 = this.doubleValue3 + this.doubleValue9;
         } else {
            for (double d = 0.1; d <= 0.6; d += 0.1) {
               if (!method472(this.doubleValue, this.doubleValue2 + d, this.doubleValue3 + this.doubleValue9)
                  && !method472(this.doubleValue, this.doubleValue2 + d, this.doubleValue3)) {
                  this.doubleValue2 += d;
                  this.doubleValue3 = this.doubleValue3 + this.doubleValue9;
                  return;
               }
            }

            if (this.flag
               && this.doubleValue8 <= 0.0
               && !method472(this.doubleValue, this.doubleValue2 + 1.0, this.doubleValue3 + Math.signum(this.doubleValue9) * 0.35)) {
               this.doubleValue8 = 0.52;
            }

            this.doubleValue9 = 0.0;
         }
      }
   }

   private void method468(Vec3 vec, LivingEntity livingEntity) {
      AABB inflate = livingEntity.getBoundingBox().inflate(0.1);
      double x = (inflate.minX + inflate.maxX) * 0.5;
      double z = (inflate.minZ + inflate.maxZ) * 0.5;
      double d = Math.max((inflate.maxX - inflate.minX) * 0.5, (inflate.maxZ - inflate.minZ) * 0.5) + 0.6;
      double y = livingEntity.getY();
      if ((!(Math.abs(this.doubleValue2 - y) > 1.0) || !this.flag) && !(method470(this.doubleValue, this.doubleValue3, x, z) > 4.0)) {
         double d2 = Math.atan2(this.doubleValue3 - z, this.doubleValue - x) + 0.3 * this.intValue;
         double d3 = Math.atan2(vec.z - z, vec.x - x);
         double d4;
         if ((d4 = Mth.wrapDegrees(Math.toDegrees(d2 - d3)) / 180.0 * Math.PI) > Math.PI / 2) {
            d4 = Math.PI / 2;
            this.intValue = -this.intValue;
         } else if (d4 < -Math.PI / 2) {
            d4 = -Math.PI / 2;
            this.intValue = -this.intValue;
         }

         d2 = d3 + d4;
         double d5 = x + Math.cos(d2) * d;
         double d6 = z + Math.sin(d2) * d;
         double doubleValue = Mth.lerp(0.35, this.doubleValue, d5);
         double d7 = Mth.lerp(0.35, this.doubleValue3, d6);
         if (!method472(doubleValue, this.doubleValue2, this.doubleValue3) && method473(doubleValue, this.doubleValue2, this.doubleValue3)) {
            this.doubleValue = doubleValue;
         }

         if (!method472(this.doubleValue, this.doubleValue2, d7) && method473(this.doubleValue, this.doubleValue2, d7)) {
            this.doubleValue3 = d7;
         }

         this.doubleValue7 = 0.0;
         this.doubleValue9 = 0.0;
         if (this.ticks % 40 == 0) {
            this.intValue = -this.intValue;
         }

         if (this.flag) {
            if (++this.intValue2 >= 3) {
               this.doubleValue8 = 0.45;
               this.intValue2 = 0;
            }
         } else {
            this.intValue2 = 0;
         }
      } else {
         double d2 = Math.atan2(vec.z - z, vec.x - x);
         this.doubleValue = x + Math.cos(d2) * d;
         this.doubleValue3 = z + Math.sin(d2) * d;
         this.doubleValue2 = y;
         this.doubleValue4 = this.doubleValue;
         this.doubleValue5 = this.doubleValue2;
         this.doubleValue6 = this.doubleValue3;
         this.doubleValue7 = this.doubleValue8 = this.doubleValue9 = 0.0;
         this.intValue2 = 0;
      }
   }

   private void method469(Vec3 vec, LivingEntity livingEntity) {
      Vec3 position = livingEntity != null && livingEntity.isAlive() ? livingEntity.position() : vec;
      this.floatValue = (float)Math.toDegrees(Math.atan2(position.z - this.doubleValue3, position.x - this.doubleValue)) - 90.0F;
   }

   private static double method470(double doubleValue, double d, double x, double z) {
      double d2 = doubleValue - x;
      double d3 = d - z;
      return Math.sqrt(d2 * d2 + d3 * d3);
   }

   private static AABB method471(double doubleValue, double minY, double d) {
      return new AABB(doubleValue - 0.12, minY, d - 0.12, doubleValue + 0.12, minY + 0.72, d + 0.12);
   }

   private static boolean method472(double doubleValue, double minY, double d) {
      return (mc.level != null && !mc.level.noCollision(method471(doubleValue, minY, d)));
   }

   private static boolean method473(double doubleValue, double d, double d2) {
      if (mc.level == null) {
         return true;
      } else {
         for (double d3 = 0.01; d3 <= 1.5; d3 += 0.25) {
            if (!mc.level.noCollision(method471(doubleValue, d - d3, d2))) {
               return true;
            }
         }

         return false;
      }
   }
}
