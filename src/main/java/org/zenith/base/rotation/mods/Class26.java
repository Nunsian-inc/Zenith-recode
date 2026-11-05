package org.zenith.base.rotation.mods;

import java.security.SecureRandom;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.zenith.Rotation;
import org.zenith.client.modules.impl.combat.AuraModule;
import org.zenith.utility.game.TargetUtils;
import org.zenith.utility.misc.Util22;
import org.zenith.utility.misc.Vec2f;

public class Class26 extends Util22 {
   private Random random = new Random();
   private boolean flag;
   private boolean flag2;
   private float floatValue = 1.0F;
   private long longValue;
   private int intValue;
   private Class26.Entry2 entry2 = new Class26.Entry2();
   private float floatValue2 = 1.0F;
   private long longValue2 = System.currentTimeMillis();
   private long longValue3 = System.currentTimeMillis();
   private int intValue2 = Integer.MIN_VALUE;
   private boolean flag3;
   private boolean flag4;
   private int intValue3 = Integer.MIN_VALUE;
   private float floatValue3;
   private float floatValue4;
   private float floatValue5;
   private float floatValue6;
   private float floatValue7;
   private float floatValue8;
   private float floatValue9;
   private float floatValue10;
   private double doubleValue;
   private double doubleValue2;
   private double doubleValue3;
   private float floatValue11;
   private float floatValue12;
   private long longValue4;
   private int intValue4;
   private boolean flag5;
   private int intValue5;
   private int intValue6;
   private int intValue7;
   private float floatValue13;
   private float floatValue14;
   private float floatValue15;
   private SecureRandom secureRandom = new SecureRandom();
   private boolean flag6;
   private float floatValue16;
   private float floatValue17;
   private float floatValue18;
   private int intValue8 = Integer.MIN_VALUE;
   private int intValue9 = Integer.MIN_VALUE;
   private long longValue5;
   private long longValue6;
   private long longValue7;
   private float floatValue19;
   private float floatValue20;
   private float floatValue21 = 1.0F;
   private float floatValue22 = 1.0F;
   private float floatValue23;
   private float floatValue24;
   private int intValue10 = Integer.MIN_VALUE;
   private Vec3 vec3d = Vec3.ZERO;
   private long longValue8;
   private float floatValue25;
   private float floatValue26;
   private int intValue11 = Integer.MIN_VALUE;
   private Vec3 vec3d2 = Vec3.ZERO;
   private long longValue9;
   private float floatValue27;
   private float floatValue28;
   private int intValue12 = 1;
   private int intValue13 = Integer.MIN_VALUE;
   private long longValue10;
   private long longValue11;
   private long longValue12;
   private Rotation rotation = Rotation.INSTANCE;
   private Rotation rotation2 = Rotation.INSTANCE;
   private float floatValue29;
   private int intValue14 = Integer.MIN_VALUE;
   private Rotation rotation3 = Rotation.INSTANCE;
   private boolean flag7;
   private float floatValue30;
   private int intValue15 = Integer.MIN_VALUE;
   private float floatValue31;
   private float floatValue32;
   private float floatValue33;
   private float floatValue34;
   private int intValue16 = Integer.MIN_VALUE;
   private int intValue17;
   private int intValue18;
   private float floatValue35;
   private float floatValue36;
   private int intValue19 = Integer.MIN_VALUE;
   private long longValue13;
   private float floatValue37;
   private float floatValue38;
   private int intValue20 = Integer.MIN_VALUE;
   private float floatValue39;
   private float floatValue40;
   private float floatValue41;
   private float floatValue42;
   private float floatValue43;
   private float floatValue44;
   private float floatValue45;
   private float floatValue46;
   private double doubleValue4;
   private double doubleValue5;
   private double doubleValue6;
   private float floatValue47;
   private float floatValue48;
   private long longValue14;
   private int intValue21;
   private boolean flag8;
   private int intValue22;
   private int intValue23;
   private int intValue24;
   private float floatValue49;
   private Class26.Entry3[] entry3s = new Class26.Entry3[12];
   private Class26.Entry[] entrys = new Class26.Entry[10];
   public int intValue25 = 0;

   public Rotation method3747(Rotation rotation) {
      if (this.secureRandom == null) {
         this.secureRandom = new SecureRandom();
         this.random = new Random();
         this.entry2 = new Class26.Entry2();
      }

      Rotation rotation2 = rotationManager.method7844();
      LivingEntity livingEntity = AuraModule.INSTANCE.method6358();
      if (mc.player != null && livingEntity != null && livingEntity.isAlive()) {
         if (livingEntity.getId() != this.intValue2) {
            this.method3834();
            this.intValue2 = livingEntity.getId();
         }

         if (AuraModule.INSTANCE.isFlag26() || AuraModule.INSTANCE.isFlag9()) {
            this.longValue3 = System.currentTimeMillis();
         }

         if (mc.player.tickCount % 100 == 0) {
            this.intValue25++;
            if (this.intValue25 > 50) {
               this.intValue25 = 41;
            }
         }

         int n = this.intValue25;

         return switch (n) {
            case 0 -> this.method3748(livingEntity, rotation2);
            case 1 -> this.method3749(livingEntity);
            case 2 -> this.method3750(livingEntity);
            case 3 -> this.method3751(livingEntity, rotation2);
            case 4 -> this.method3752(livingEntity, rotation2);
            case 5 -> this.method3753(livingEntity);
            case 6 -> this.method3754(livingEntity);
            case 7 -> this.method3755(livingEntity);
            case 8 -> this.method3756(livingEntity, rotation2);
            case 9 -> this.method3757(livingEntity, rotation2);
            case 10 -> this.method3758(livingEntity, rotation2);
            case 11 -> this.method3759(livingEntity, rotation2);
            case 12 -> this.method3760(livingEntity, rotation2);
            case 13 -> this.method3761(livingEntity, rotation2);
            case 14 -> this.method3762(livingEntity, rotation2);
            case 15 -> this.method3763(livingEntity, rotation2);
            case 16 -> this.method3764(livingEntity);
            case 17 -> this.method3765(livingEntity, rotation2);
            case 18 -> this.method3766(livingEntity);
            case 19 -> this.method3767(livingEntity, rotation2);
            case 20 -> this.method3768(livingEntity, rotation2);
            case 21 -> this.method3769(livingEntity, rotation2);
            case 22 -> this.method3770(livingEntity, rotation2);
            case 23 -> this.method3771(livingEntity, rotation2);
            case 24 -> this.method3772(livingEntity, rotation2);
            case 25 -> this.method3773(livingEntity, rotation2);
            case 26 -> this.method3774(livingEntity, rotation2);
            case 27 -> this.method3775(livingEntity, rotation2);
            case 28 -> this.method3781(livingEntity, rotation2);
            case 29 -> this.method3788(0, livingEntity, rotation2);
            case 30 -> this.method3788(1, livingEntity, rotation2);
            case 31 -> this.method3788(2, livingEntity, rotation2);
            case 32 -> this.method3788(3, livingEntity, rotation2);
            case 33 -> this.method3788(4, livingEntity, rotation2);
            case 34 -> this.method3788(5, livingEntity, rotation2);
            case 35 -> this.method3788(6, livingEntity, rotation2);
            case 36 -> this.method3788(7, livingEntity, rotation2);
            case 37 -> this.method3788(8, livingEntity, rotation2);
            case 38 -> this.method3788(9, livingEntity, rotation2);
            case 39 -> this.method3788(10, livingEntity, rotation2);
            case 40 -> this.method3788(11, livingEntity, rotation2);
            case 41 -> this.method3799(0, livingEntity, rotation2);
            case 42 -> this.method3799(1, livingEntity, rotation2);
            case 43 -> this.method3799(2, livingEntity, rotation2);
            case 44 -> this.method3799(3, livingEntity, rotation2);
            case 45 -> this.method3799(4, livingEntity, rotation2);
            case 46 -> this.method3799(5, livingEntity, rotation2);
            case 47 -> this.method3799(6, livingEntity, rotation2);
            case 48 -> this.method3799(7, livingEntity, rotation2);
            case 49 -> this.method3799(8, livingEntity, rotation2);
            case 50 -> this.method3799(9, livingEntity, rotation2);
            default -> rotation != null && !rotation.method3137() ? rotation : rotation2;
         };
      } else {
         this.method3834();
         return rotation2;
      }
   }

   private Rotation method3748(LivingEntity livingEntity, Rotation rotation) {
      Vec3 vec = livingEntity.position().add(0.0, livingEntity.getBbHeight() * 0.9, 0.0).subtract(mc.player.getEyePosition());
      float yRot = (float)Mth.wrapDegrees(Math.toDegrees(Math.atan2(vec.z, vec.x)) - 90.0);
      float xRot = (float)(-Math.toDegrees(Math.atan2(vec.y, Math.sqrt(vec.x * vec.x + vec.z * vec.z))));
      boolean flag = this.flag;
      this.flag = TargetUtils.method3209(16.0, new Rotation(yRot, xRot), livingEntity);
      if (this.flag != flag) {
         if (this.flag) {
            this.floatValue = 0.2F;
            this.flag2 = false;
         } else {
            this.flag2 = true;
            this.longValue = System.currentTimeMillis();
            this.intValue = 100 + this.random.nextInt(21);
         }
      }

      if (this.flag2 && System.currentTimeMillis() - this.longValue >= this.intValue) {
         float f = Math.min((float)(System.currentTimeMillis() - this.longValue - this.intValue) / 200.0F, 1.0F);
         this.floatValue = 0.2F + f;
         if (f >= 1.0F) {
            this.flag2 = false;
            this.floatValue = 1.0F;
         }
      }

      float f = Mth.wrapDegrees(yRot - rotation.getFloatValue());
      float f2 = Mth.wrapDegrees(xRot - rotation.getFloatValue2());
      float f3 = Math.min(Math.max(Math.abs(f), 1.0F), this.method3831(Math.abs(f), 120.0F * this.floatValue));
      float f4 = Math.min(Math.max(Math.abs(f2), 1.0F), this.method3831(Math.abs(f2), 52.5F * this.floatValue));
      float f5 = this.method3832(Math.abs(f));
      f3 *= f5;
      f4 *= f5;
      float yRot2 = rotation.getFloatValue() + (f > 0.0F ? f3 : -f3);
      float xRot2 = Mth.clamp(rotation.getFloatValue2() + (f2 > 0.0F ? f4 : -f4), -89.0F, 89.0F);
      float f6 = mc.player.tickCount * 0.8F;
      float f7 = 1.8F + (float)Math.sin(f6 * 0.4F) * 2.2F;
      yRot2 += (float)Math.sin(f6 * 1.3F) * f7 * 0.9F;
      xRot2 += (float)Math.cos(f6 * 1.1F) * f7 * 0.7F;
      float f8 = this.flag ? 0.1F : 0.3F;
      if (mc.player.tickCount % 2 == 0) {
         yRot2 += (this.random.nextFloat() - 0.5F) * f8;
         xRot2 += (this.random.nextFloat() - 0.5F) * f8 * 0.6F;
      }

      yRot2 += (float)Math.sin(f6 * 2.1F) * 0.008F;
      xRot2 += (float)Math.cos(f6 * 1.8F) * 0.015F;
      float f9 = Rotation.method3138();
      float f10 = 1.85F + this.random.nextFloat() * 0.45F;
      yRot2 -= (yRot2 - rotation.getFloatValue()) % (f9 * f10);
      xRot2 -= (xRot2 - rotation.getFloatValue2()) % (f9 * f10);
      float max = 32.0F + (float)Math.sin(f6 * 0.7F) * 6.0F;
      float max2 = 25.0F + (float)Math.cos(f6 * 0.8F) * 5.0F;
      yRot2 = rotation.getFloatValue() + Mth.clamp(yRot2 - rotation.getFloatValue(), -max, max);
      xRot2 = Mth.clamp(rotation.getFloatValue2() + Mth.clamp(xRot2 - rotation.getFloatValue2(), -max2, max2), -89.0F, 89.0F);
      return new Rotation(yRot2, xRot2);
   }

   private Rotation method3749(LivingEntity livingEntity) {
      return this.method3826(this.method3824(livingEntity));
   }

   private Rotation method3750(LivingEntity livingEntity) {
      return this.method3826(livingEntity.position().add(0.0, livingEntity.getBbHeight() / 2.0F, 0.0));
   }

   private Rotation method3751(LivingEntity livingEntity, Rotation rotation) {
      this.method3828();
      Rotation rotation2 = this.method3826(this.method3825(livingEntity));
      boolean flag = System.currentTimeMillis() - this.longValue3 >= 300L;
      float floatValue = rotation2.getFloatValue();
      float xRot = rotation2.getFloatValue2();
      if (flag) {
         if (mc.player.isSprinting()) {
            floatValue += 5.0F;
            xRot -= 10.0F;
         } else {
            floatValue -= 5.0F;
         }
      }

      long l = System.currentTimeMillis() - this.longValue2;
      floatValue += (float)this.entry2.method3835(l * 5.0E-4) * 25.0F * this.floatValue2;
      xRot += (float)this.entry2.method3836(l * 5.0E-4, 10.0) * 25.0F * this.floatValue2;
      this.method3829(rotation, rotation2);
      return new Rotation(floatValue, xRot);
   }

   private Rotation method3752(LivingEntity livingEntity, Rotation rotation) {
      AABB boundingBox = livingEntity.getBoundingBox();
      double d = this.method3830((float)Math.cos(System.currentTimeMillis() / 1000.0)) * 0.15;
      double d2 = this.method3830((float)Math.cos(System.currentTimeMillis() / 10000.0)) * 0.15;
      double d3 = this.method3830((float)Math.cos(System.currentTimeMillis() / 1000.0)) * 0.15;
      Vec3 center = this.method3825(livingEntity);
      Rotation rotation2 = this.method3826(
         new Vec3(
            Mth.clamp(center.x + d, boundingBox.minX, boundingBox.maxX),
            center.y + livingEntity.getBbHeight() / 2.0F + d2,
            Mth.clamp(center.z + d3, boundingBox.minZ, boundingBox.maxZ)
         )
      );
      float xRot = rotation.getFloatValue2();
      if (!TargetUtils.method3209(16.0, new Rotation(rotation2.getFloatValue(), xRot), livingEntity)) {
         xRot = this.method3826(center).getFloatValue2();
      }

      return new Rotation(rotation2.getFloatValue(), xRot);
   }

   private Rotation method3753(LivingEntity livingEntity) {
      return this.method3826(livingEntity.getBoundingBox().getCenter());
   }

   private Rotation method3754(LivingEntity livingEntity) {
      AABB boundingBox = livingEntity.getBoundingBox();
      Vec3 vec = new Vec3(
         Mth.lerp(this.random.nextDouble(), boundingBox.minX, boundingBox.maxX),
         Mth.lerp(this.random.nextDouble(), boundingBox.minY, boundingBox.maxY),
         Mth.lerp(this.random.nextDouble(), boundingBox.minZ, boundingBox.maxZ)
      );
      double d = System.currentTimeMillis() / 500.0;
      return this.method3826(vec.add(Math.sin(d) * 0.15, Math.cos(d * 1.2) * 0.2, Math.cos(d * 0.8) * 0.15));
   }

   private Rotation method3755(LivingEntity livingEntity) {
      Vec3 center = this.method3824(livingEntity);
      if (mc.player.getEyePosition().distanceTo(livingEntity.getEyePosition()) > 3.0) {
         center = livingEntity.position().add(0.0, livingEntity.getEyeHeight(livingEntity.getPose()), 0.0);
      }

      return this.method3826(center);
   }

   private Rotation method3756(LivingEntity livingEntity, Rotation rotation) {
      float f = Rotation.method3138();
      double x = Math.sin(System.currentTimeMillis() / 150.0) * 0.15;
      double y = Math.cos(System.currentTimeMillis() / 150.0) * 0.15;
      Vec3 center = livingEntity.position().add(0.0, Mth.clamp(mc.player.getEyeY() - livingEntity.getY(), 0.0, livingEntity.getBbHeight() * 0.8), 0.0).add(x, y, x);
      Vec2f vec2f = rotation.method3131(this.method3826(center));
      float hypot = (float)Math.hypot(vec2f.getFloatValue(), vec2f.getFloatValue2());
      float f2 = Mth.clamp(hypot / 18.0F, 0.2F, 0.95F);
      float yRot = rotation.getFloatValue() + Math.round(vec2f.getFloatValue() * f2 / f) * f;
      float xRot = rotation.getFloatValue2() + Math.round(vec2f.getFloatValue2() * f2 / f) * f;
      return new Rotation(yRot, xRot);
   }

   private Rotation method3757(LivingEntity livingEntity, Rotation rotation) {
      this.method3828();
      Rotation rotation2 = this.method3826(this.method3825(livingEntity));
      boolean flag = System.currentTimeMillis() - this.longValue3 >= 300L;
      float floatValue = rotation2.getFloatValue();
      float xRot = rotation2.getFloatValue2();
      Vec2f vec2f = rotation.method3131(rotation2);
      if (flag) {
         if (mc.player.isSprinting()) {
            floatValue += 5.0F;
            xRot -= 10.0F;
         } else {
            floatValue -= 5.0F;
         }
      }

      float f = Math.max(
            (90.0F - Math.abs(vec2f.getFloatValue())) / (flag ? (mc.player.fallDistance > 0.0 ? 20.0F : 60.0F) : 40.0F), this.method3833(1.0F, 5.0F)
         )
         * this.method3833(0.9F, 1.1F);
      float f2 = Math.abs(vec2f.getFloatValue2()) / (flag ? (mc.player.fallDistance > 0.0 ? 60.0F : 100.0F) : 30.0F) * this.method3833(0.9F, 1.1F);
      return this.method3827(rotation, new Rotation(floatValue, xRot), f * 25.0F, f2 * 25.0F);
   }

   private Rotation method3758(LivingEntity livingEntity, Rotation rotation) {
      this.method3828();
      boolean intersects = livingEntity.getBoundingBox().inflate(1.0).intersects(mc.player.getBoundingBox());
      Vec3 center = this.method3824(livingEntity);
      Rotation rotation2 = this.method3826(this.method3825(livingEntity));
      Rotation rotation3 = this.method3826(center);
      if (intersects) {
         rotation2 = this.method3826(livingEntity.position().add(0.0, 0.5, 0.0));
      }

      if (!TargetUtils.method3209(16.0, rotation2, livingEntity) && mc.player.getEyePosition().distanceTo(livingEntity.getEyePosition()) > 3.0) {
         rotation2 = new Rotation(rotation2.getFloatValue(), rotation3.getFloatValue2() + 10.0F);
      }

      boolean flag = System.currentTimeMillis() - this.longValue3 >= (intersects ? 500L : 200L);
      float floatValue = rotation2.getFloatValue();
      float xRot = rotation2.getFloatValue2();
      Vec2f vec2f = rotation.method3131(rotation2);
      boolean air = mc.level.getBlockState(mc.player.blockPosition().above(2)).isAir();
      if (!mc.player.isSprinting() && flag && air) {
         floatValue += 10.0F;
         xRot -= 20.0F;
      }

      float f = Math.max(
            (90.0F - Math.abs(vec2f.getFloatValue()))
               / (!mc.player.isSprinting() && flag ? 5.0F : (flag ? (mc.player.fallDistance > 0.0 ? 25.0F : 10.0F) : 60.0F)),
            this.method3833(1.0F, 5.0F)
         )
         * this.method3833(0.9F, 1.1F);
      float f2 = Math.abs(vec2f.getFloatValue2())
         / (!mc.player.isSprinting() && flag ? 5.0F : (flag ? (mc.player.fallDistance > 0.0 ? 25.0F : 10.0F) : 40.0F))
         * this.method3833(0.9F, 1.1F);
      long l = System.currentTimeMillis() - this.longValue2;
      floatValue += (float)this.entry2.method3835(l * 5.0E-4) * 25.0F * this.floatValue2;
      xRot += (float)this.entry2.method3836(l * 5.0E-4, 10.0) * 25.0F * this.floatValue2;
      this.method3829(rotation, rotation2);
      return this.method3827(rotation, new Rotation(floatValue, xRot), f * 25.0F, f2 * 25.0F);
   }

   private Rotation method3759(LivingEntity livingEntity, Rotation rotation) {
      Rotation rotation2 = this.method3813(livingEntity.position().add(0.0, livingEntity.getBbHeight() - 0.2, 0.0));
      Vec2f vec2f = rotation.method3131(rotation2);
      float f = Math.min(Math.max(Math.abs(vec2f.getFloatValue()), 0.0F), 150.0F);
      float f2 = Math.max(Math.abs(vec2f.getFloatValue2()), 0.0F);
      float yRot = rotation.getFloatValue() + (vec2f.getFloatValue() > 0.0F ? f : -f);
      float xRot = Mth.clamp(rotation.getFloatValue2() + (vec2f.getFloatValue2() > 0.0F ? f2 : -f2), -89.0F, 89.0F);
      float f3 = Rotation.method3138();
      yRot -= (yRot - rotation.getFloatValue()) % f3;
      xRot -= (xRot - rotation.getFloatValue2()) % f3;
      return new Rotation(yRot, xRot);
   }

   private Rotation method3760(LivingEntity livingEntity, Rotation rotation) {
      Rotation rotation2 = this.method3813(livingEntity.position().add(0.0, livingEntity.getBbHeight() - 0.2, 0.0));
      Vec2f vec2f = rotation.method3131(rotation2);
      float attackStrengthTicker = mc.player.attackStrengthTicker;
      boolean flag = attackStrengthTicker > 0.0F && attackStrengthTicker < 0.7F;
      boolean flag2 = attackStrengthTicker >= 0.7F && attackStrengthTicker < 1.0F;
      if ((flag || flag2) && !this.flag3) {
         this.flag3 = true;
      } else if (!flag && !flag2 && this.flag3) {
         this.flag4 = true;
         this.flag3 = false;
      }

      if (this.flag4) {
         this.flag4 = false;
      }

      float f;
      float max;
      float max2;
      float f2;
      if (flag2) {
         f = 0.4F;
         max = 175.0F;
         max2 = 8.0F;
         f2 = 0.01F;
      } else if (flag) {
         f = 0.5F;
         max = 70.0F;
         max2 = 65.0F;
         f2 = 0.09F;
      } else {
         f = 0.8F;
         max = 45.0F;
         max2 = 40.0F;
         f2 = 0.15F;
      }

      float f3 = Mth.clamp(Math.abs(vec2f.getFloatValue()), 0.3F, max);
      float f4 = Mth.clamp(Math.abs(vec2f.getFloatValue2()), 0.25F, max2);
      if (Math.abs(vec2f.getFloatValue()) < 5.0F && !flag2) {
         f3 *= 0.8F + 0.05F * (float)Math.sin(0.0);
      }

      if (flag2 || flag) {
         float f5 = 2.0F - attackStrengthTicker * attackStrengthTicker;
         f3 *= f5;
         f4 *= f5;
      }

      float f5;
      float floatValue;
      if (flag2) {
         floatValue = rotation.getFloatValue();
         f5 = Mth.clamp(rotation.getFloatValue2() + (int)vec2f.getFloatValue(), -60.0F, 60.0F);
      } else {
         floatValue = rotation.getFloatValue() + (vec2f.getFloatValue() > 0.0F ? f3 : -f3) * (1.0F - f2 * (float)Math.abs(Math.sin(0.0)));
         f5 = Mth.clamp(rotation.getFloatValue2() + (vec2f.getFloatValue2() > 0.0F ? f4 : -f4) - 12.0F, -85.0F, 85.0F + f4);
      }

      if (!flag2 && !flag && !this.flag4) {
         floatValue = (float)(floatValue + (Math.random() - 0.5) * 0.2);
         f5 = (float)(f5 + (Math.random() - 0.5) * 0.1);
      }

      float f6 = Rotation.method3138() * (!flag2 && !flag ? 1.0F : 0.7F);
      floatValue -= (floatValue - rotation.getFloatValue()) % f6;
      f5 -= (f5 - rotation.getFloatValue2()) % f6;
      return new Rotation(floatValue * f + rotation.getFloatValue() * (1.0F - f), f5 * f + rotation.getFloatValue2() * (1.0F - f));
   }

   private Rotation method3761(LivingEntity livingEntity, Rotation rotation) {
      if (this.intValue3 != livingEntity.getId()) {
         this.intValue3 = livingEntity.getId();
         this.floatValue3 = rotation.getFloatValue();
         this.floatValue4 = rotation.getFloatValue2();
         this.floatValue9 = this.floatValue7 = this.floatValue3;
         this.floatValue10 = this.floatValue8 = this.floatValue4;
         this.floatValue5 = this.floatValue6 = 0.0F;
         this.method3814(livingEntity);
         this.floatValue11 = (float)(Math.random() * Math.PI * 2.0);
         this.floatValue12 = (float)(Math.random() * Math.PI * 2.0);
         float f = rotation.method3130(this.method3826(livingEntity.getBoundingBox().getCenter()));
         this.intValue4 = f > 130.0F
            ? 160 + this.random.nextInt(70)
            : (f > 70.0F ? 100 + this.random.nextInt(55) : (f > 30.0F ? 55 + this.random.nextInt(30) : 18 + this.random.nextInt(16)));
         this.longValue4 = System.currentTimeMillis();
         this.flag5 = false;
         this.intValue5 = this.intValue6 = 0;
         this.intValue7 = livingEntity.hurtTime;
      }

      float f = this.method3817();
      if (!this.flag5) {
         long l = System.currentTimeMillis() - this.longValue4;
         if (l < this.intValue4) {
            float f2 = (float)l / Math.max(this.intValue4, 1);
            float f3 = 1.0F - this.method3821(f2) * 0.4F;
            float yRot = this.floatValue9 + (this.random.nextFloat() - 0.5F) * 0.3F * f3;
            float xRot = Mth.clamp(this.floatValue10 + (this.random.nextFloat() - 0.5F) * 0.19F * f3, -89.0F, 89.0F);
            yRot -= (yRot - this.floatValue9) % f;
            xRot -= (xRot - this.floatValue10) % f;
            this.floatValue9 = yRot;
            this.floatValue10 = xRot;
            return new Rotation(yRot, xRot);
         }

         this.flag5 = true;
      }

      Vec3 eyePosition = mc.player.getEyePosition();
      Vec3 center = livingEntity.getBoundingBox().getCenter();
      float f2 = (float)eyePosition.distanceTo(center);
      float[] floats = this.method3815(f2);
      if (livingEntity.hurtTime > this.intValue7 && livingEntity.hurtTime > 0) {
         this.intValue5 = 1;
         this.intValue6 = 0;
         this.floatValue13 = this.floatValue4;
      }

      this.intValue7 = livingEntity.hurtTime;
      if (this.intValue5 > 0) {
         this.intValue6++;
         if (this.intValue5 == 1) {
            float alpha1 = this.method3820(Mth.clamp(this.intValue6 / 18.0F, 0.0F, 1.0F));
            this.floatValue4 = Mth.lerp(alpha1, this.floatValue13, -88.0F);
            if (this.intValue6 >= 18) {
               this.intValue5 = 2;
               this.intValue6 = 0;
            }
         } else {
            float alpha1 = this.method3821(Mth.clamp(this.intValue6 / 26.0F, 0.0F, 1.0F));
            this.floatValue4 = Mth.lerp(alpha1, -88.0F, this.floatValue13);
            if (this.intValue6 >= 26) {
               this.intValue5 = this.intValue6 = 0;
            }
         }

         return this.method3816(this.floatValue3 + floats[0], this.floatValue4 + floats[1], f);
      } else {
         if (Math.random() < 0.018) {
            this.method3814(livingEntity);
         }

         Vec3 vec = new Vec3(livingEntity.getX() - livingEntity.xo, livingEntity.getY() - livingEntity.yo, livingEntity.getZ() - livingEntity.zo);
         int scale = !mc.player.isFallFlying() && !livingEntity.isFallFlying() ? 2 : 0;
         Vec3 center2 = center.add(vec.scale(scale)).add(this.doubleValue, this.doubleValue2, this.doubleValue3);
         Rotation rotation2 = this.method3826(center2);
         float f3 = Mth.wrapDegrees(rotation2.getFloatValue() - this.floatValue3);
         float f4 = rotation2.getFloatValue2() - this.floatValue4;
         float f5 = 1.0F;
         if (mc.player.isFallFlying()) {
            float f6 = Math.abs(f3) + Math.abs(f4);
            if (f6 > 120.0F) {
               f5 = 0.22F;
            } else if (f6 > 80.0F) {
               f5 = Mth.lerp(this.method3821((f6 - 80.0F) / 40.0F), 0.4F, 0.22F);
            } else if (f6 > 25.0F) {
               f5 = Mth.lerp(this.method3821((f6 - 25.0F) / 55.0F), 0.7F, 0.4F);
            } else {
               f5 = 0.7F + 0.3F * (1.0F - f6 / 25.0F);
            }
         } else if (mc.options.keyUp.isDown() || this.method3818(livingEntity)) {
            f5 = 0.55F;
         }

         float f6 = (0.045F + this.random.nextFloat() * 0.011F) * f5;
         float f7 = 0.72F + 0.1F * (1.0F - f5);
         float f8 = (float)Math.sqrt(f3 * f3 + f4 * f4);
         if (f8 > 34.0F) {
            f6 += 0.021F * f5;
         } else if (f8 < 3.8F) {
            f6 *= 0.42F;
         }

         f6 += Mth.clamp((f2 - 1.5F) / 8.0F, 0.0F, 0.05F) * f5;
         this.floatValue5 = this.floatValue5 + (f3 * f6 - this.floatValue5 * f7);
         this.floatValue6 = this.floatValue6 + (f4 * f6 * 0.92F - this.floatValue6 * f7);
         this.floatValue5 = Mth.clamp(this.floatValue5, -8.2F * f5, 8.2F * f5);
         this.floatValue6 = Mth.clamp(this.floatValue6, -6.3F * f5, 6.3F * f5);
         this.floatValue3 = this.floatValue3 + this.floatValue5;
         this.floatValue4 = Mth.clamp(this.floatValue4 + this.floatValue6, -89.0F, 89.0F);
         float f9 = mc.player.isFallFlying() ? 0.25F + f5 * 0.45F : 0.8F;
         this.floatValue7 = this.method3822(this.floatValue7, this.floatValue3, f9);
         this.floatValue8 = this.method3822(this.floatValue8, this.floatValue4, f9 * 0.9F);
         return this.method3816(this.floatValue7 + floats[0], this.floatValue8 + floats[1], f);
      }
   }

   private Rotation method3762(LivingEntity livingEntity, Rotation rotation) {
      AABB aabb = this.method3819(livingEntity);
      Vec3 eyePosition = mc.player.getEyePosition();
      Vec3 to = eyePosition.add(rotation.method3135().scale(999.0));
      var clip = aabb.clip(eyePosition, to);
      boolean flag = clip.isPresent() || aabb.contains(eyePosition);
      ThreadLocalRandom current = ThreadLocalRandom.current();
      if (flag) {
         this.floatValue14 = Mth.clamp(this.floatValue14 - current.nextFloat(0.005F, 0.02F), 0.0F, 1.0F);
         this.floatValue15 = Mth.clamp(this.floatValue15 - current.nextFloat(0.005F, 0.02F), 0.0F, 1.0F);
      } else if (mc.player.isFallFlying()) {
         this.floatValue14 = Mth.clamp(this.floatValue14 + current.nextFloat(9.0E-5F, 0.009F), 0.0F, 1.0F);
         this.floatValue15 = Mth.clamp(this.floatValue15 + current.nextFloat(9.0E-4F, 0.009F), 0.0F, 1.0F);
      } else if (livingEntity.isVisuallySwimming()) {
         this.floatValue14 = Mth.clamp(this.floatValue14 + current.nextFloat(9.0E-5F, 0.009F), 0.0F, 1.0F);
         this.floatValue15 = Mth.clamp(this.floatValue15 + current.nextFloat(9.0E-5F, 9.0E-4F), 0.0F, 1.0F);
      } else {
         this.floatValue14 = Mth.clamp(this.floatValue14 + current.nextFloat(9.0E-5F, 0.009F), 0.0F, 1.0F);
         this.floatValue15 = Mth.clamp(this.floatValue15 + current.nextFloat(9.0E-4F, 0.009F), 0.0F, 1.0F);
      }

      Rotation rotation2 = this.method3826(livingEntity.getBoundingBox().getCenter());
      Vec2f vec2f = rotation.method3131(rotation2);
      return new Rotation(rotation.getFloatValue() + vec2f.getFloatValue() * this.floatValue14, rotation.getFloatValue2() + vec2f.getFloatValue2() * this.floatValue15);
   }

   private Rotation method3763(LivingEntity livingEntity, Rotation rotation) {
      Rotation rotation2 = this.method3826(this.method3824(livingEntity));
      Vec2f vec2f = rotation.method3131(rotation2);
      float hypot = (float)Math.hypot(Math.abs(vec2f.getFloatValue()), Math.abs(vec2f.getFloatValue2()));
      if (hypot <= 1.0E-4F) {
         return rotation;
      } else {
         double d = System.currentTimeMillis() - this.longValue3;
         float f = AuraModule.INSTANCE.isFlag26() ? 0.3F : 1.0F;
         float f2 = (float)(this.method3823(3.0F, 6.0F) * Math.cos(d / 60.0)) * f;
         float f3 = (float)(this.method3823(2.0F, 4.0F) * Math.sin(d / 40.0)) * f;
         boolean flag = TargetUtils.method3209(16.0, rotation, livingEntity);
         float max = Math.abs(vec2f.getFloatValue() / hypot) * 180.0F;
         float max2 = Math.abs(vec2f.getFloatValue2() / hypot) * 180.0F;
         float f4 = Mth.clamp(vec2f.getFloatValue(), -max, max);
         float f5 = Mth.clamp(vec2f.getFloatValue2(), -max2, max2);
         boolean flag2 = d < 250.0;
         float alpha1 = flag ? this.method3823(0.25F, 0.35F) : this.method3823(0.5F, 0.7F);
         if (flag2) {
            alpha1 = this.secureRandom.nextFloat() < 0.1F ? this.method3823(0.5F, 0.8F) : this.method3823(0.3F, 0.45F);
         }

         return new Rotation(
            Mth.lerp(alpha1, rotation.getFloatValue(), rotation.getFloatValue() + f4) + f2,
            Mth.lerp(alpha1, rotation.getFloatValue2(), rotation.getFloatValue2() + f5) + f3
         );
      }
   }

   private Rotation method3764(LivingEntity livingEntity) {
      return this.method3826(livingEntity.getBoundingBox().getCenter());
   }

   private Rotation method3765(LivingEntity livingEntity, Rotation rotation) {
      if (this.intValue8 != livingEntity.getId()) {
         this.intValue8 = livingEntity.getId();
         this.flag6 = false;
         this.floatValue16 = 0.0F;
         this.floatValue17 = rotation.getFloatValue();
         this.floatValue18 = Mth.clamp(rotation.getFloatValue2(), -40.0F, 40.0F);
      }

      Rotation rotation2 = this.method3826(livingEntity.getBoundingBox().getCenter());
      Vec3 eyePosition = mc.player.getEyePosition();
      boolean present = livingEntity.getBoundingBox().inflate(-0.5).clip(eyePosition, eyePosition.add(rotation.method3135().scale(999.0))).isPresent();
      if (this.flag6) {
         if (this.floatValue16 >= -0.01F) {
            this.floatValue16 = this.floatValue16 - (Math.abs(Mth.wrapDegrees(rotation2.getFloatValue() - this.floatValue17)) > 80.0F ? 0.055F : 0.0055F);
         }

         if (this.floatValue16 <= -0.01F) {
            this.flag6 = false;
         }
      } else {
         this.floatValue16 += 0.0034F;
         if (this.floatValue16 >= 0.36F || present) {
            this.flag6 = true;
         }
      }

      float f = Mth.wrapDegrees(rotation2.getFloatValue() - this.floatValue17);
      float f2 = Mth.clamp(rotation2.getFloatValue2(), -40.0F, 40.0F) - this.floatValue18;
      float f3 = Math.max(this.floatValue16, 0.0F);
      float f4 = this.floatValue17 + f * Mth.clamp(f3 * 1.3F, 0.0F, 1.0F);
      float f5 = this.floatValue18 + f2 * Mth.clamp(f3 / 1.7F, 0.0F, 1.0F);
      float f6 = Rotation.method3138();
      f4 -= (f4 - this.floatValue17) % f6;
      f5 -= (f5 - this.floatValue18) % f6;
      this.floatValue17 = f4;
      this.floatValue18 = Mth.clamp(f5, -40.0F, 40.0F);
      return new Rotation(this.floatValue17, this.floatValue18);
   }

   private Rotation method3766(LivingEntity livingEntity) {
      Vec3 vec = this.method3824(livingEntity).subtract(mc.player.getEyePosition());
      float f = (float)Mth.wrapDegrees(Math.toDegrees(Math.atan2(vec.z, vec.x)) - 90.0);
      float f2 = (float)(-Math.toDegrees(Math.atan2(vec.y, Math.hypot(vec.x, vec.z))));
      float f3 = this.method3833(0.0F, 3.0F);
      return new Rotation(f + this.method3833(-f3, f3), f2 + 0.2958859F);
   }

   private Rotation method3767(LivingEntity livingEntity, Rotation rotation) {
      if (this.intValue9 != livingEntity.getId()) {
         this.intValue9 = livingEntity.getId();
         this.longValue5 = 0L;
         this.floatValue19 = this.floatValue20 = 0.0F;
         this.floatValue21 = this.floatValue22 = Mth.lerp(0.80275816F, 0.75F, 1.2F);
         this.floatValue23 = this.method3833(0.0F, (float) (Math.PI * 2));
         this.floatValue24 = this.method3833(0.0F, (float) (Math.PI * 2));
         this.longValue7 = System.currentTimeMillis() + 120L + this.random.nextInt(160);
      }

      long nanoTime = System.nanoTime();
      float f = this.longValue5 == 0L ? 0.016666668F : (float)(nanoTime - this.longValue5) / 1.0E9F;
      this.longValue5 = nanoTime;
      f = Mth.clamp(f, 0.001F, 0.1F);
      long now = System.currentTimeMillis();
      if (now >= this.longValue6) {
         this.floatValue22 = Math.random() < 0.12 ? Mth.lerp(0.80275816F, 0.1F, 0.3F) : Mth.lerp(0.80275816F, 0.75F, 1.2F);
         this.longValue6 = now + 250L + this.random.nextInt(550);
      }

      this.floatValue21 = this.floatValue21 + (this.floatValue22 - this.floatValue21) * Mth.clamp(4.0F * f, 0.0F, 1.0F);
      if (now < this.longValue7) {
         this.floatValue19 = this.floatValue19 * Math.max(0.0F, 1.0F - 6.0F * f);
         this.floatValue20 = this.floatValue20 * Math.max(0.0F, 1.0F - 6.0F * f);
         return this.method3811(rotation, rotation.getFloatValue() + this.floatValue19 * f, rotation.getFloatValue2() + this.floatValue20 * f);
      } else {
         Vec3 vec = this.method3810(livingEntity, rotation.getFloatValue());
         this.floatValue23 += f * 1.5F;
         this.floatValue24 += f * 1.1F;
         float f2 = (float)Mth.wrapDegrees(Math.toDegrees(Math.atan2(vec.z, vec.x)) - 90.0) + (float)Math.sin(this.floatValue23) * 0.18F;
         float f3 = Mth.clamp(
            (float)(-Math.toDegrees(Math.atan2(vec.y, Math.hypot(vec.x, vec.z)))) + (float)Math.sin(this.floatValue24) * 0.09F, -89.0F, 89.0F
         );
         float f4 = Mth.wrapDegrees(f2 - rotation.getFloatValue());
         float f5 = f3 - rotation.getFloatValue2();
         float f6 = (float)Math.sqrt(f4 * f4 + f5 * f5);
         if (f6 > 1.0E-4F) {
            float f7 = Math.min(140.0F * this.floatValue21, f6 * 5.5F);
            float f8 = f4 * f6 * f7;
            float f9 = f5 * f6 * f7 * 0.78F;
            float f10 = Mth.clamp(10.0F * f, 0.0F, 1.0F);
            this.floatValue19 = this.floatValue19 + (f8 - this.floatValue19) * f10;
            this.floatValue20 = this.floatValue20 + (f9 - this.floatValue20) * f10 * 0.85F;
         } else {
            this.floatValue19 = this.floatValue19 * Math.max(0.0F, 1.0F - 8.0F * f);
            this.floatValue20 = this.floatValue20 * Math.max(0.0F, 1.0F - 8.0F * f);
         }

         return this.method3811(rotation, rotation.getFloatValue() + this.floatValue19 * f, rotation.getFloatValue2() + this.floatValue20 * f);
      }
   }

   private Rotation method3768(LivingEntity livingEntity, Rotation rotation) {
      if (this.intValue10 != livingEntity.getId()) {
         this.intValue10 = livingEntity.getId();
         this.vec3d = Vec3.ZERO;
         this.longValue8 = 0L;
         this.floatValue25 = this.floatValue26 = 0.0F;
      }

      AABB boundingBox = livingEntity.getBoundingBox();
      long now = System.currentTimeMillis();
      if (now >= this.longValue8 || this.vec3d.equals(Vec3.ZERO)) {
         Vec3 vec = new Vec3(
            ThreadLocalRandom.current().nextDouble(-boundingBox.getXsize() * 0.035, boundingBox.getXsize() * 0.035),
            boundingBox.getYsize() * ThreadLocalRandom.current().nextDouble(0.7, 0.79),
            ThreadLocalRandom.current().nextDouble(-boundingBox.getZsize() * 0.045, boundingBox.getZsize() * 0.045)
         );
         this.vec3d = this.vec3d.equals(Vec3.ZERO)
            ? vec
            : new Vec3(
               Mth.lerp((double)this.method3833(0.16F, 0.3F), this.vec3d.x, vec.x),
               Mth.lerp((double)this.method3833(0.08F, 0.18F), this.vec3d.y, vec.y),
               Mth.lerp((double)this.method3833(0.16F, 0.3F), this.vec3d.z, vec.z)
            );
         this.longValue8 = now + ThreadLocalRandom.current().nextLong(170L, 420L);
      }

      Vec3 vec = mc.player.getEyePosition().subtract(livingEntity.position()).multiply(1.0, 0.0, 1.0);
      if (vec.lengthSqr() < 1.0E-6) {
         vec = new Vec3(0.0, 0.0, 1.0);
      }

      Vec3 vec2 = vec.normalize();
      Vec3 vec3 = new Vec3(vec2.z, 0.0, -vec2.x);
      Vec3 center = livingEntity.position()
         .add(vec3.scale(Math.max(0.1, boundingBox.getXsize() * 0.3)))
         .add(vec3.scale(this.vec3d.x))
         .add(vec2.scale(this.vec3d.z))
         .add(0.0, this.vec3d.y, 0.0);
      Rotation rotation2 = this.method3826(center);
      Vec2f vec2f = rotation.method3131(rotation2);
      boolean flag = TargetUtils.method3209(16.0, rotation, livingEntity);
      float f = Mth.clamp((Math.abs(vec2f.getFloatValue2()) - this.method3833(flag ? 0.55F : 0.45F, flag ? 1.1F : 0.95F)) / 5.5F, 0.0F, 1.0F);
      float f2 = this.method3833(flag ? 0.24F : 0.37F, flag ? 0.43F : 0.68F);
      float f3 = this.method3833(flag ? 0.16F : 0.24F, flag ? 0.3F : 0.44F) * f;
      float f4 = (float)mc.player.getEyePosition().distanceTo(center);
      float max = f4 < 2.4F ? this.method3833(1.9F, 3.9F) : (f4 < 4.6F ? this.method3833(2.5F, 5.2F) : this.method3833(3.4F, 6.9F));
      float max2 = f4 < 2.4F ? this.method3833(0.55F, 1.8F) : (f4 < 4.6F ? this.method3833(0.8F, 2.7F) : this.method3833(1.1F, 3.6F));
      float f5 = Mth.clamp(vec2f.getFloatValue() * f2, -max, max);
      float f6 = f < 0.08F ? 0.0F : Mth.clamp(vec2f.getFloatValue2() * f3, -max2, max2);
      this.floatValue25 = this.floatValue25 + Mth.clamp(f5 - this.floatValue25, -this.method3833(0.1F, 0.29F), this.method3833(0.1F, 0.29F));
      this.floatValue26 = this.floatValue26 + Mth.clamp(f6 - this.floatValue26, -this.method3833(0.05F, 0.37F), this.method3833(0.05F, 0.37F));
      float magnitude = Rotation.method3138();
      float f7 = this.method3812(this.floatValue25, vec2f.getFloatValue(), magnitude);
      float f8 = this.method3812(this.floatValue26, vec2f.getFloatValue2(), magnitude);
      return new Rotation(rotation.getFloatValue() + f7, rotation.getFloatValue2() + f8);
   }

   private Rotation method3769(LivingEntity livingEntity, Rotation rotation) {
      if (this.intValue11 != livingEntity.getId()) {
         this.intValue11 = livingEntity.getId();
         this.vec3d2 = Vec3.ZERO;
         this.longValue9 = 0L;
         this.floatValue27 = this.floatValue28 = 0.0F;
         this.intValue12 = 1;
      }

      AABB boundingBox = livingEntity.getBoundingBox();
      long now = System.currentTimeMillis();
      if (now >= this.longValue9 || this.vec3d2.equals(Vec3.ZERO)) {
         Vec3 vec = new Vec3(
            ThreadLocalRandom.current().nextDouble(-boundingBox.getXsize() * 0.19, boundingBox.getXsize() * 0.19),
            boundingBox.getYsize() * 0.72,
            ThreadLocalRandom.current().nextDouble(-boundingBox.getZsize() * 0.21, boundingBox.getZsize() * 0.21)
         );
         this.vec3d2 = this.vec3d2.equals(Vec3.ZERO)
            ? vec
            : new Vec3(
               Mth.lerp(0.22, this.vec3d2.x, vec.x), Mth.lerp(0.16, this.vec3d2.y, vec.y), Mth.lerp(0.22, this.vec3d2.z, vec.z)
            );
         this.longValue9 = now + ThreadLocalRandom.current().nextLong(140L, 380L);
         if (this.random.nextBoolean()) {
            this.intValue12 = -this.intValue12;
         }
      }

      Vec3 vec = mc.player.position().subtract(livingEntity.position()).multiply(1.0, 0.0, 1.0);
      if (vec.lengthSqr() < 1.0E-6) {
         vec = new Vec3(0.0, 0.0, 1.0);
      }

      vec = vec.normalize();
      Vec3 vec2 = new Vec3(vec.z, 0.0, -vec.x);
      Vec3 center = livingEntity.position()
         .add(vec2.scale(boundingBox.getXsize() * 0.15 * this.intValue12))
         .add(vec2.scale(this.vec3d2.x))
         .add(vec.scale(this.vec3d2.z))
         .add(0.0, this.vec3d2.y, 0.0);
      Vec2f vec2f = rotation.method3131(this.method3826(center));
      float f = Mth.clamp(vec2f.getFloatValue() * 0.38F, -6.0F, 6.0F);
      float f2 = Math.abs(vec2f.getFloatValue2()) < 0.65F ? 0.0F : Mth.clamp(vec2f.getFloatValue2() * 0.3F, -2.9F, 2.9F);
      this.floatValue27 = this.floatValue27 + Mth.clamp(f - this.floatValue27, -0.42F, 0.42F);
      this.floatValue28 = this.floatValue28 + Mth.clamp(f2 - this.floatValue28, -0.28F, 0.28F);
      this.floatValue27 = Mth.lerp(0.22F, this.floatValue27, this.floatValue27 + this.method3833(-0.14F, 0.14F));
      this.floatValue28 = Mth.lerp(0.14F, this.floatValue28, this.floatValue28 + this.method3833(-0.11F, 0.11F));
      float magnitude = Rotation.method3138();
      return new Rotation(
         rotation.getFloatValue() + this.method3812(this.floatValue27, vec2f.getFloatValue(), magnitude),
         rotation.getFloatValue2() + this.method3812(this.floatValue28, vec2f.getFloatValue2(), magnitude)
      );
   }

   private Rotation method3770(LivingEntity livingEntity, Rotation rotation) {
      Rotation rotation2 = this.method3826(this.method3824(livingEntity));
      Vec2f vec2f = rotation.method3131(rotation2);
      float floatValue = vec2f.getFloatValue();
      float y = vec2f.getFloatValue2();
      float hypot = (float)Math.hypot(floatValue, y);
      boolean flag = this.method3778();
      float f = flag ? 0.4F : 1.0F;
      float f2 = Math.min(Math.abs(floatValue), (45.0F + this.method3823(0.0F, 0.5F)) * f);
      float f3 = Math.min(Math.abs(y), 20.0F * f);
      float floatValue2 = rotation.getFloatValue();
      float f4 = rotation.getFloatValue2();
      if (hypot > 0.5F) {
         boolean flag2 = Math.abs(y) >= f3;
         float f5 = flag ? this.method3823(3.0F, 5.0F) : this.method3823(6.0F, 9.0F);
         float f6 = flag ? this.method3823(12.0F, 18.0F) : this.method3823(25.0F, 35.0F);
         float f7 = Math.min(hypot, flag2 ? f6 : f5) / hypot;
         if (!flag2) {
            f7 = this.method3779(f7);
         }

         float max = flag ? 3.0F : 15.0F;
         f4 = Mth.clamp(rotation.getFloatValue2() + Mth.clamp(y * f7, -max, max), -89.0F, 90.0F);
         boolean flag3 = Math.abs(floatValue) >= f2;
         f5 = flag ? this.method3823(3.0F, 5.0F) : this.method3823(6.0F, 9.0F);
         f6 = flag ? this.method3823(12.0F, 18.0F) : this.method3823(25.0F, 35.0F);
         float f8 = Math.min(hypot, flag3 ? f6 : f5) / hypot;
         if (!flag3) {
            f8 = this.method3779(f8);
         }

         floatValue2 = rotation.getFloatValue() + floatValue * f8;
      }

      if (hypot < 5.0F) {
         float f5 = (float)(System.currentTimeMillis() % 12000L) / 1200.0F;
         float f6 = flag ? 0.3F : 1.0F;
         floatValue2 += Mth.sin(f5 * 2.0F * (float) (Math.PI * 2)) * 0.5F * f6 * (0.5F + this.random.nextFloat() * 0.5F);
      }

      return this.method3780(rotation, floatValue2, f4);
   }

   private Rotation method3771(LivingEntity livingEntity, Rotation rotation) {
      Rotation rotation2 = this.method3826(this.method3777(livingEntity, 0.5, 0.05));
      long now = System.currentTimeMillis();
      if (this.intValue13 != livingEntity.getId()) {
         this.intValue13 = livingEntity.getId();
         this.longValue10 = now + 55L + this.random.nextInt(115);
         this.longValue12 = 0L;
         this.floatValue29 = 0.0F;
      }

      if (now < this.longValue10) {
         return this.method3780(rotation, rotation.getFloatValue() + this.method3833(-0.08F, 0.08F), rotation.getFloatValue2() + this.method3833(-0.05F, 0.05F));
      } else {
         if (this.longValue12 <= 0L || now >= this.longValue11 + this.longValue12 || this.rotation2.method3130(rotation2) > 2.5F) {
            float f = Math.max(rotation.method3130(rotation2), 0.01F);
            this.rotation = rotation;
            this.rotation2 = rotation2;
            this.longValue11 = now;
            this.longValue12 = (long)Mth.clamp(75.0F + f * 4.2F + this.method3833(-20.0F, 35.0F), 90.0F, 520.0F);
         }

         float f = Mth.clamp((float)(now - this.longValue11) / (float)Math.max(this.longValue12, 1L), 0.0F, 1.0F);
         float f2 = f * f * f * (10.0F + f * (-15.0F + 6.0F * f));
         Vec2f vec2f = this.rotation.method3131(this.rotation2);
         this.floatValue29 = this.floatValue29 * 0.72F + this.method3833(-0.12F, 0.12F);
         float f3 = (float)Math.sin(Math.PI * f);
         float f4 = this.rotation.getFloatValue() + vec2f.getFloatValue() * f2 + this.floatValue29 * f3;
         float f5 = this.rotation.getFloatValue2() + vec2f.getFloatValue2() * f2 + this.floatValue29 * 0.45F * f3;
         return this.method3780(rotation, f4, f5);
      }
   }

   private Rotation method3772(LivingEntity livingEntity, Rotation rotation) {
      Rotation rotation2 = this.method3826(this.method3777(livingEntity, 0.56, 0.04));
      if (this.intValue14 != livingEntity.getId()) {
         this.intValue14 = livingEntity.getId();
         this.method3776(rotation, rotation2);
      }

      Rotation rotation3 = this.flag7 ? rotation2 : this.rotation3;
      Vec2f vec2f = rotation.method3131(rotation3);
      float f = vec2f.method5327();
      if (!this.flag7 && f < 1.2F) {
         this.flag7 = true;
         vec2f = rotation.method3131(rotation2);
         this.floatValue30 *= 0.48F;
      } else if (this.flag7 && rotation.method3130(rotation2) > 12.0F) {
         this.method3776(rotation, rotation2);
         rotation3 = this.rotation3;
         vec2f = rotation.method3131(rotation3);
      }

      float f2 = Mth.clamp(vec2f.method5327() * (this.flag7 ? 0.18F : 0.32F), 0.18F, this.flag7 ? 2.8F : 8.5F);
      this.floatValue30 = this.floatValue30 + Mth.clamp(f2 - this.floatValue30, -0.65F, 0.85F);
      float f3 = Math.max(vec2f.method5327(), 1.0E-4F);
      float f4 = vec2f.getFloatValue() / f3 * Math.min(f3, this.floatValue30);
      float f5 = vec2f.getFloatValue2() / f3 * Math.min(f3, this.floatValue30 * 0.72F);
      return this.method3780(rotation, rotation.getFloatValue() + f4, rotation.getFloatValue2() + f5);
   }

   private Rotation method3773(LivingEntity livingEntity, Rotation rotation) {
      if (this.intValue15 != livingEntity.getId()) {
         this.intValue15 = livingEntity.getId();
         this.floatValue31 = this.floatValue32 = 0.0F;
         this.floatValue33 = this.floatValue34 = 0.0F;
      }

      Rotation rotation2 = this.method3826(this.method3777(livingEntity, 0.48, 0.025));
      Vec2f vec2f = rotation.method3131(rotation2);
      float f = vec2f.method5327();
      float f2 = Mth.lerp(Mth.clamp(f / 35.0F, 0.0F, 1.0F), 0.1F, 0.24F);
      float f3 = f < 2.0F ? 0.82F : 0.68F;
      this.floatValue31 = this.floatValue31 * f3 + vec2f.getFloatValue() * f2;
      this.floatValue32 = this.floatValue32 * (f3 + 0.04F) + vec2f.getFloatValue2() * f2 * 0.78F;
      this.floatValue31 = Mth.clamp(this.floatValue31, -7.2F, 7.2F);
      this.floatValue32 = Mth.clamp(this.floatValue32, -4.6F, 4.6F);
      this.floatValue33 = this.floatValue33 * 0.86F + this.method3833(-0.075F, 0.075F);
      this.floatValue34 = this.floatValue34 * 0.81F + this.method3833(-0.045F, 0.045F);
      float f4 = Mth.clamp(1.0F - f / 18.0F, 0.12F, 1.0F);
      return this.method3780(
         rotation, rotation.getFloatValue() + this.floatValue31 + this.floatValue33 * f4, rotation.getFloatValue2() + this.floatValue32 + this.floatValue34 * f4
      );
   }

   private Rotation method3774(LivingEntity livingEntity, Rotation rotation) {
      if (this.intValue16 != livingEntity.getId()) {
         this.intValue16 = livingEntity.getId();
         this.intValue17 = 0;
         this.intValue18 = 1 + this.random.nextInt(3);
      }

      Rotation rotation2 = this.method3826(this.method3777(livingEntity, 0.52, 0.065));
      Vec2f vec2f = rotation.method3131(rotation2);
      if (this.intValue18 > 0) {
         this.intValue18--;
         return rotation;
      } else {
         if (this.intValue17 <= 0) {
            float f = Math.max(vec2f.method5327(), 0.01F);
            this.intValue17 = Mth.clamp(2 + Math.round(f / this.method3833(3.5F, 7.5F)), 2, 9);
            float f2 = f < 4.0F ? this.method3833(0.35F, 0.7F) : this.method3833(0.68F, 1.05F);
            this.floatValue35 = vec2f.getFloatValue() * f2 / this.intValue17;
            this.floatValue36 = vec2f.getFloatValue2() * f2 / this.intValue17;
         }

         float f = 1.0F - (float)(this.intValue17 - 1) / Math.max(this.intValue17 + 1, 2);
         float f2 = 0.65F + 0.55F * (float)Math.sin(Math.PI * f);
         this.intValue17--;
         if (this.intValue17 <= 0) {
            this.intValue18 = this.random.nextFloat() < 0.22F ? 1 + this.random.nextInt(3) : 0;
         }

         return this.method3780(rotation, rotation.getFloatValue() + this.floatValue35 * f2, rotation.getFloatValue2() + this.floatValue36 * f2);
      }
   }

   private Rotation method3775(LivingEntity livingEntity, Rotation rotation) {
      Rotation rotation2 = this.method3826(livingEntity.getBoundingBox().getCenter());
      long now = System.currentTimeMillis();
      if (this.intValue19 != livingEntity.getId()) {
         this.intValue19 = livingEntity.getId();
         this.longValue13 = now + 70L + this.random.nextInt(140);
         this.floatValue37 = this.floatValue38 = 0.0F;
      }

      if (now < this.longValue13) {
         return rotation;
      } else {
         Vec2f vec2f = rotation.method3131(rotation2);
         float f = Math.max(vec2f.method5327(), 0.001F);
         double d = Math.max(mc.player.getEyePosition().distanceTo(livingEntity.getBoundingBox().getCenter()), 0.1);
         double d2 = Math.max((double)livingEntity.getBbWidth(), 0.1);
         float f2 = (float)Math.toDegrees(2.0 * Math.atan(d2 / (2.0 * d)));
         float f3 = (float)(Math.log(f / Math.max(f2, 0.05F) + 1.0) / Math.log(2.0));
         float f4 = 85.0F + 92.0F * f3;
         float f5 = Math.max(f4 / 50.0F, 1.0F);
         float f6 = vec2f.getFloatValue() / f5;
         float f7 = vec2f.getFloatValue2() / f5;
         float f8 = f > f2 ? 0.42F : 0.2F;
         this.floatValue37 = this.floatValue37 + Mth.clamp(f6 - this.floatValue37, -f8 * 1.25F, f8 * 1.25F);
         this.floatValue38 = this.floatValue38 + Mth.clamp(f7 - this.floatValue38, -f8 * 0.8F, f8 * 0.8F);
         if (f < f2 * 0.45F) {
            this.floatValue37 *= 0.55F;
            this.floatValue38 *= 0.5F;
         }

         return this.method3780(
            rotation,
            rotation.getFloatValue() + this.floatValue37 + this.method3833(-0.025F, 0.025F),
            rotation.getFloatValue2() + this.floatValue38 + this.method3833(-0.018F, 0.018F)
         );
      }
   }

   private void method3776(Rotation rotation, Rotation rotation2) {
      Vec2f vec2f = rotation.method3131(rotation2);
      float f = vec2f.getFloatValue() == 0.0F ? (this.random.nextBoolean() ? 1.0F : -1.0F) : Math.signum(vec2f.getFloatValue());
      float f2 = vec2f.getFloatValue2() == 0.0F ? (this.random.nextBoolean() ? 1.0F : -1.0F) : Math.signum(vec2f.getFloatValue2());
      float f3 = Mth.clamp(vec2f.method5327() / 70.0F, 0.25F, 1.0F);
      this.rotation3 = new Rotation(
         rotation2.getFloatValue() + f * this.method3833(0.8F, 3.8F) * f3, rotation2.getFloatValue2() + f2 * this.method3833(0.25F, 1.35F) * f3
      );
      this.flag7 = false;
      this.floatValue30 = this.method3833(0.25F, 0.65F);
   }

   private Vec3 method3777(LivingEntity livingEntity, double d, double d2) {
      AABB boundingBox = livingEntity.getBoundingBox();
      double x = Mth.clamp(livingEntity.getX() + this.method3833((float)(-d2), (float)d2) * boundingBox.getXsize(), boundingBox.minX, boundingBox.maxX);
      double y = Mth.clamp(
         boundingBox.minY + boundingBox.getYsize() * d + this.method3833((float)(-d2), (float)d2) * boundingBox.getYsize(), boundingBox.minY, boundingBox.maxY
      );
      double z = Mth.clamp(livingEntity.getZ() + this.method3833((float)(-d2), (float)d2) * boundingBox.getZsize(), boundingBox.minZ, boundingBox.maxZ);
      return new Vec3(x, y, z);
   }

   private boolean method3778() {
      return !mc.level.isEmptyBlock(mc.player.blockPosition().above())
         || !mc.level.isEmptyBlock(mc.player.blockPosition().west())
         || !mc.level.isEmptyBlock(mc.player.blockPosition().east())
         || !mc.level.isEmptyBlock(mc.player.blockPosition().north())
         || !mc.level.isEmptyBlock(mc.player.blockPosition().south());
   }

   private float method3779(float f) {
      return f * (0.5F + 0.5F * f);
   }

   private Rotation method3780(Rotation rotation, float f, float f2) {
      float f3 = Rotation.method3138();
      float yRot = rotation.getFloatValue() + Math.round(Mth.wrapDegrees(f - rotation.getFloatValue()) / f3) * f3;
      float f4 = rotation.getFloatValue2() + Math.round((f2 - rotation.getFloatValue2()) / f3) * f3;
      return new Rotation(yRot, Mth.clamp(f4, -89.0F, 89.0F));
   }

   private Rotation method3781(LivingEntity livingEntity, Rotation rotation) {
      if (this.intValue20 != livingEntity.getId()) {
         this.intValue20 = livingEntity.getId();
         this.floatValue39 = rotation.getFloatValue();
         this.floatValue40 = rotation.getFloatValue2();
         this.floatValue45 = this.floatValue43 = this.floatValue39;
         this.floatValue46 = this.floatValue44 = this.floatValue40;
         this.floatValue41 = this.floatValue42 = 0.0F;
         this.floatValue47 = this.floatValue48 = 0.0F;
         this.method3782(livingEntity);
         float f = rotation.method3130(this.method3826(livingEntity.getBoundingBox().getCenter()));
         int n = f > 130.0F ? 140 : (f > 70.0F ? 90 : (f > 30.0F ? 45 : 12));
         int n2 = f > 130.0F ? 30 : (f > 70.0F ? 20 : (f > 30.0F ? 15 : 5));
         this.intValue21 = Math.max(5, Math.round((n + (float)this.random.nextGaussian() * n2) * 0.275F));
         this.longValue14 = System.currentTimeMillis();
         this.flag8 = false;
         this.intValue22 = this.intValue23 = 0;
         this.intValue24 = livingEntity.hurtTime;
      }

      float f = this.method3817();
      if (!this.flag8) {
         if (System.currentTimeMillis() - this.longValue14 < this.intValue21) {
            float yRot = this.floatValue45 + (float)this.random.nextGaussian() * 0.15F;
            float xRot = Mth.clamp(this.floatValue46 + (float)this.random.nextGaussian() * 0.1F, -89.0F, 89.0F);
            return this.method3783(yRot, xRot, f);
         }

         this.flag8 = true;
      }

      Vec3 eyePosition = mc.player.getEyePosition();
      Vec3 center = livingEntity.getBoundingBox().getCenter();
      float f2 = (float)eyePosition.distanceTo(center);
      float f3 = Mth.clamp(f2 / 4.5F, 0.25F, 1.0F);
      this.floatValue47 = (this.floatValue47 + (float)this.random.nextGaussian() * 0.4F * f3) * 0.85F;
      this.floatValue48 = (this.floatValue48 + (float)this.random.nextGaussian() * 0.3F * f3) * 0.85F;
      if (livingEntity.hurtTime > this.intValue24 && livingEntity.hurtTime > 0) {
         this.intValue22 = 1;
         this.intValue23 = 0;
         this.floatValue49 = this.floatValue40;
      }

      this.intValue24 = livingEntity.hurtTime;
      if (this.intValue22 > 0) {
         this.intValue23++;
         if (this.intValue22 == 1) {
            float alpha1 = this.method3787(Mth.clamp(this.intValue23 / 25.0F, 0.0F, 1.0F));
            this.floatValue40 = Mth.lerp(alpha1, this.floatValue49, -89.0F);
            if (this.intValue23 >= 25) {
               this.intValue22 = 2;
               this.intValue23 = 0;
            }
         } else {
            float alpha1 = this.method3786(Mth.clamp(this.intValue23 / 20.0F, 0.0F, 1.0F));
            this.floatValue40 = Mth.lerp(alpha1, -89.0F, this.floatValue49);
            if (this.intValue23 >= 20) {
               this.intValue22 = this.intValue23 = 0;
            }
         }

         return this.method3783(this.floatValue39 + this.floatValue47, this.floatValue40 + this.floatValue48, f);
      } else {
         if (this.random.nextDouble() < 0.015) {
            this.method3782(livingEntity);
         }

         Vec3 vec = new Vec3(livingEntity.getX() - livingEntity.xo, livingEntity.getY() - livingEntity.yo, livingEntity.getZ() - livingEntity.zo);
         int scale = !mc.player.isFallFlying() && !livingEntity.isFallFlying() ? 2 : 0;
         Vec3 center2 = center.add(vec.scale(scale)).add(this.doubleValue4, this.doubleValue5, this.doubleValue6);
         Rotation rotation2 = this.method3826(center2);
         float f4 = Mth.wrapDegrees(rotation2.getFloatValue() - this.floatValue39);
         float f5 = rotation2.getFloatValue2() - this.floatValue40;
         float f6 = 1.0F;
         if (mc.player.isFallFlying()) {
            float f7 = Math.abs(f4) + Math.abs(f5);
            if (f7 > 120.0F) {
               f6 = 0.18F;
            } else if (f7 > 80.0F) {
               f6 = Mth.lerp(this.method3786((f7 - 80.0F) / 40.0F), 0.35F, 0.18F);
            } else if (f7 > 25.0F) {
               f6 = Mth.lerp(this.method3786((f7 - 25.0F) / 55.0F), 0.65F, 0.35F);
            } else {
               f6 = 0.65F + 0.35F * (1.0F - f7 / 25.0F);
            }
         } else if (mc.options.keyUp.isDown() || this.method3818(livingEntity)) {
            f6 = 0.78F;
         }

         float f7 = (0.13F + (float)Math.abs(this.random.nextGaussian()) * 0.024F) * f6;
         float f8 = 0.62F + 0.1F * (1.0F - f6);
         float f9 = (float)Math.sqrt(f4 * f4 + f5 * f5);
         if (f9 > 32.0F) {
            f7 += 0.064F * f6;
         } else if (f9 < 4.2F) {
            f7 *= 0.48F;
         }

         f7 += Mth.clamp((f2 - 1.6F) / 7.5F, 0.0F, 0.045F) * f6;
         this.floatValue41 = this.method3784(f4, this.floatValue41, f7, f8, true);
         this.floatValue42 = this.method3784(f5, this.floatValue42, f7 * 0.87F, f8, false);
         this.floatValue41 = this.method3785(this.floatValue41, 55.0F);
         this.floatValue42 = this.method3785(this.floatValue42, 40.0F);
         this.floatValue39 = this.floatValue39 + this.floatValue41;
         this.floatValue40 = Mth.clamp(this.floatValue40 + this.floatValue42, -89.0F, 89.0F);
         float f10 = mc.player.isFallFlying() ? 0.6F + f6 * 0.38F : 0.98F;
         this.floatValue43 = this.method3822(this.floatValue43, this.floatValue39, f10);
         this.floatValue44 = this.method3822(this.floatValue44, this.floatValue40, f10 * 0.95F);
         return this.method3783(this.floatValue43 + this.floatValue47, this.floatValue44 + this.floatValue48, f);
      }
   }

   private void method3782(LivingEntity livingEntity) {
      AABB boundingBox = livingEntity.getBoundingBox();
      this.doubleValue4 = Mth.clamp(this.random.nextGaussian() * 0.15, -0.5, 0.5) * boundingBox.getXsize() * 0.4;
      this.doubleValue5 = Mth.clamp(this.random.nextGaussian() * 0.15, -0.5, 0.5) * boundingBox.getYsize() * 0.4;
      this.doubleValue6 = Mth.clamp(this.random.nextGaussian() * 0.15, -0.5, 0.5) * boundingBox.getZsize() * 0.4;
   }

   private Rotation method3783(float yRot, float xRot, float f) {
      xRot = Mth.clamp(xRot, -89.0F, 89.0F);
      yRot -= (yRot - this.floatValue45) % f;
      xRot -= (xRot - this.floatValue46) % f;
      this.floatValue45 = yRot;
      this.floatValue46 = xRot;
      return new Rotation(yRot, xRot);
   }

   private float method3784(float f, float f2, float f3, float f4, boolean flag) {
      if (flag && Math.abs(f) > 45.0F) {
         f3 *= 1.2F;
         f4 *= 0.8F;
      }

      return f2 + f * f3 - f2 * f4;
   }

   private float method3785(float f, float f2) {
      return (float)(f2 * Math.tanh(f / f2));
   }

   private float method3786(float f) {
      f = Mth.clamp(f, 0.0F, 1.0F);
      return f * f * (3.0F - 2.0F * f);
   }

   private float method3787(float f) {
      f = Mth.clamp(f, 0.0F, 1.0F);
      return 1.0F - (1.0F - f) * (1.0F - f);
   }

   private Rotation method3788(int n, LivingEntity livingEntity, Rotation rotation) {
      Class26.Entry3 entry3 = this.method3790(n);
      long now = System.currentTimeMillis();
      if (entry3.intValue != livingEntity.getId()) {
         this.method3791(entry3, n, livingEntity, rotation, now);
      }

      if (now < entry3.longValue) {
         return n % 3 == 0
            ? this.method3780(rotation, rotation.getFloatValue() + this.method3833(-0.035F, 0.035F), rotation.getFloatValue2() + this.method3833(-0.025F, 0.025F))
            : rotation;
      } else if (now < entry3.longValue3) {
         entry3.floatValue *= 0.35F;
         entry3.floatValue2 *= 0.35F;
         return rotation;
      } else if (now >= entry3.longValue2) {
         entry3.longValue3 = now + 65L + this.random.nextInt(240);
         entry3.longValue2 = entry3.longValue3 + 550L + this.random.nextInt(1900);
         entry3.intValue2 = 0;
         return rotation;
      } else {
         if (now >= entry3.longValue5 || entry3.vec3d.equals(Vec3.ZERO)) {
            entry3.vec3d = this.method3792(n, livingEntity, entry3, now);
            entry3.longValue5 = now + this.method3797(n);
         }

         Rotation rotation2 = this.method3826(entry3.vec3d);
         boolean flag = TargetUtils.method3209(16.0, rotation, livingEntity);
         if (flag && entry3.intValue2 <= 0) {
            if (entry3.longValue4 == 0L) {
               entry3.longValue4 = now + 130L + this.random.nextInt(520);
            }

            if (now < entry3.longValue4) {
               entry3.floatValue *= 0.45F;
               entry3.floatValue2 *= 0.45F;
               if (n != 4 && n != 10) {
                  return rotation;
               }

               return this.method3780(rotation, rotation.getFloatValue() + entry3.floatValue3 * 0.08F, rotation.getFloatValue2() + entry3.floatValue4 * 0.06F);
            }

            entry3.intValue2 = 1 + this.random.nextInt(n % 4 == 0 ? 3 : 2);
            entry3.longValue4 = now + 180L + this.random.nextInt(650);
         } else if (!flag) {
            entry3.longValue4 = 0L;
            entry3.intValue2 = Math.max(entry3.intValue2, 1);
         }

         Rotation rotation3 = this.method3789(n, livingEntity, rotation, rotation2, entry3, now);
         if (entry3.intValue2 > 0) {
            entry3.intValue2--;
         }

         return rotation3;
      }
   }

   private Rotation method3789(int n, LivingEntity livingEntity, Rotation rotation, Rotation rotation2, Class26.Entry3 entry3, long now) {
      Vec2f vec2f = rotation.method3131(rotation2);
      float floatValue = vec2f.getFloatValue();
      float sign = vec2f.getFloatValue2();
      float f = Math.max(vec2f.method5327(), 1.0E-4F);
      entry3.floatValue3 = entry3.floatValue3 * (0.72F + n % 3 * 0.06F) + this.method3833(-0.055F, 0.055F);
      entry3.floatValue4 = entry3.floatValue4 * (0.68F + n % 4 * 0.05F) + this.method3833(-0.035F, 0.035F);
      float copySign;
      float copySign2;
      switch (n) {
         case 0:
            float alpha1 = (float)Math.sin(Mth.clamp(f / 75.0F, 0.0F, 1.0F) * Math.PI * 0.5);
            float f2 = Mth.lerp(alpha1, 0.45F, 60.0F);
            copySign = floatValue / f * Math.min(f, f2);
            copySign2 = sign / f * Math.min(f, f2 * 0.67F);
            break;
         case 1:
            float f3 = 1.0F - (float)Math.exp(-0.085F * f);
            float f4 = 0.35F + f3 * 59.65F;
            copySign = Math.copySign(Math.min(Math.abs(floatValue), f4), floatValue);
            copySign2 = Math.copySign(Math.min(Math.abs(sign), f4 * 0.67F), sign);
            break;
         case 2:
            float f5 = 1.0F / (1.0F + (float)Math.exp(-0.13F * (f - 17.0F)));
            float f6 = 0.5F + f5 * 59.5F;
            copySign = floatValue / f * Math.min(f, f6);
            copySign2 = sign / f * Math.min(f, f6 * 0.67F);
            break;
         case 3:
            float f7 = 0.17F + Mth.clamp(f / 100.0F, 0.0F, 0.11F);
            entry3.floatValue = entry3.floatValue * 0.31F + floatValue * f7;
            entry3.floatValue2 = entry3.floatValue2 * 0.36F + sign * f7 * 0.76F;
            copySign = Mth.clamp(entry3.floatValue, -60.0F, 60.0F);
            copySign2 = Mth.clamp(entry3.floatValue2, -40.0F, 40.0F);
            break;
         case 4:
            float f8 = f > 35.0F ? 0.24F : 0.12F;
            entry3.floatValue = entry3.floatValue * 0.58F + floatValue * f8;
            entry3.floatValue2 = entry3.floatValue2 * 0.62F + sign * f8 * 0.68F;
            copySign = Mth.clamp(entry3.floatValue, -60.0F, 60.0F);
            copySign2 = Mth.clamp(entry3.floatValue2, -40.0F, 40.0F);
            break;
         case 5:
            if (entry3.longValue8 <= 0L || now >= entry3.longValue7 + entry3.longValue8 || entry3.rotation2.method3130(rotation2) > 3.5F) {
               entry3.rotation = rotation;
               entry3.rotation2 = rotation2;
               entry3.longValue7 = now;
               entry3.longValue8 = (long)Mth.clamp(95.0F + f * 5.0F + this.method3833(-25.0F, 55.0F), 110.0F, 650.0F);
            }

            float f9 = Mth.clamp((float)(now - entry3.longValue7) / (float)entry3.longValue8, 0.0F, 1.0F);
            float f10 = f9 * f9 * (3.0F - 2.0F * f9);
            Vec2f vec2f2 = entry3.rotation.method3131(entry3.rotation2);
            float f11 = entry3.rotation.getFloatValue() + vec2f2.getFloatValue() * f10;
            float f12 = entry3.rotation.getFloatValue2() + vec2f2.getFloatValue2() * f10;
            copySign = Mth.wrapDegrees(f11 - rotation.getFloatValue());
            copySign2 = f12 - rotation.getFloatValue2();
            break;
         case 6:
            entry3.floatValue5 = entry3.floatValue5 + (0.28F + Mth.clamp(f / 180.0F, 0.0F, 0.22F));
            float f13 = 0.35F + (0.5F - 0.5F * (float)Math.cos(entry3.floatValue5)) * 0.95F;
            float f14 = Math.min(f, 46.0F * f13);
            copySign = floatValue / f * f14;
            copySign2 = sign / f * f14 * 0.67F;
            break;
         case 7:
            float f15 = Mth.clamp((float)Math.pow(f, 0.67) * 3.9F, 0.35F, 60.0F);
            copySign = floatValue / f * Math.min(f, f15);
            copySign2 = sign / f * Math.min(f, f15 * 0.67F);
            break;
         case 8:
            float f16 = this.method3798(livingEntity) * 0.38F;
            if (f <= Math.max(f16, 0.35F)) {
               entry3.floatValue *= 0.25F;
               entry3.floatValue2 *= 0.25F;
               return rotation;
            }

            float f17 = Mth.clamp((f - f16) * 1.8F, 0.35F, 60.0F);
            copySign = floatValue / f * Math.min(f, f17);
            copySign2 = sign / f * Math.min(f, f17 * 0.67F);
            break;
         case 9:
            float f18 = 1.0F - (float)Math.exp(-0.11F * Math.abs(floatValue));
            float f19 = 1.0F - (float)Math.exp(-0.065F * Math.abs(sign));
            copySign = Math.copySign(Math.min(Math.abs(floatValue), 0.4F + f18 * 59.6F), floatValue);
            copySign2 = Math.copySign(Math.min(Math.abs(sign), 0.3F + f19 * 39.7F), sign);
            break;
         case 10:
            float f20 = (float)(now - entry3.longValue6) / 1000.0F;
            float f21 = Mth.clamp(1.0F - f20 / 14.0F, 0.58F, 1.0F);
            float f22 = Mth.clamp(f * 1.45F, 0.4F, 60.0F) * f21;
            entry3.floatValue6 = entry3.floatValue6 + this.method3833(-0.012F, 0.012F);
            entry3.floatValue6 *= 0.985F;
            copySign = floatValue / f * Math.min(f, f22) + entry3.floatValue6;
            copySign2 = sign / f * Math.min(f, f22 * 0.68F) + entry3.floatValue6 * 0.32F;
            break;
         default:
            if (entry3.intValue4 <= 0) {
               entry3.intValue4 = 2 + this.random.nextInt(7);
               float f23 = f < 5.0F ? this.method3833(0.35F, 0.75F) : this.method3833(0.72F, 1.08F);
               entry3.floatValue7 = floatValue * f23 / entry3.intValue4;
               entry3.floatValue8 = sign * f23 / entry3.intValue4;
            }

            float f23 = 0.72F + 0.38F * (float)Math.sin(Math.PI / Math.max(entry3.intValue4, 2));
            copySign = entry3.floatValue7 * f23;
            copySign2 = entry3.floatValue8 * f23;
            entry3.intValue4--;
      }

      float f2 = Mth.clamp(1.0F - f / 35.0F, 0.18F, 1.0F);
      copySign = Mth.clamp(copySign + entry3.floatValue3 * f2, -60.0F, 60.0F);
      copySign2 = Mth.clamp(copySign2 + entry3.floatValue4 * f2, -40.0F, 40.0F);
      return this.method3780(rotation, rotation.getFloatValue() + copySign, rotation.getFloatValue2() + copySign2);
   }

   private Class26.Entry3 method3790(int n) {
      if (this.entry3s == null || this.entry3s.length != 12) {
         this.entry3s = new Class26.Entry3[12];
      }

      if (this.entry3s[n] == null) {
         this.entry3s[n] = new Class26.Entry3();
      }

      return this.entry3s[n];
   }

   private void method3791(Class26.Entry3 entry3, int n, LivingEntity livingEntity, Rotation rotation, long now) {
      entry3.intValue = livingEntity.getId();
      entry3.longValue = now + 25L + this.random.nextInt(65 + n * 7);
      entry3.longValue2 = now + 650L + this.random.nextInt(1150 + n * 65);
      entry3.longValue3 = 0L;
      entry3.longValue4 = 0L;
      entry3.longValue5 = 0L;
      entry3.intValue2 = 0;
      entry3.floatValue = entry3.floatValue2 = 0.0F;
      entry3.floatValue3 = entry3.floatValue4 = 0.0F;
      entry3.floatValue5 = this.method3833(0.0F, (float) (Math.PI * 2));
      entry3.floatValue6 = 0.0F;
      entry3.longValue8 = 0L;
      entry3.intValue4 = 0;
      entry3.longValue6 = now;
      entry3.vec3d = Vec3.ZERO;
   }

   private Vec3 method3792(int n, LivingEntity livingEntity, Class26.Entry3 entry3, long now) {
      AABB boundingBox = livingEntity.getBoundingBox();
      Vec3 vec = new Vec3(livingEntity.getX() - livingEntity.xo, livingEntity.getY() - livingEntity.yo, livingEntity.getZ() - livingEntity.zo);

      return switch (n) {
         case 0 -> boundingBox.getCenter();
         case 1 -> this.method3824(livingEntity);
         case 2 -> this.method3793(livingEntity, entry3.intValue3, 0.7);
         case 3 -> this.method3794(boundingBox, 0.52, 0.16);
         case 4 -> this.method3796(boundingBox, boundingBox.getCenter().add(vec.scale(2.0)));
         case 5 -> this.method3795(boundingBox);
         case 6 -> {
            double d = now / 700.0;
            yield this.method3796(
               boundingBox,
               boundingBox.getCenter()
                  .add(
                     Math.sin(d) * boundingBox.getXsize() * 0.16,
                     Math.cos(d * 0.73) * boundingBox.getYsize() * 0.08,
                     Math.cos(d * 0.87) * boundingBox.getZsize() * 0.16
                  )
            );
         }
         case 7 -> new Vec3(livingEntity.getX(), boundingBox.minY + boundingBox.getYsize() * 0.38, livingEntity.getZ());
         case 8 -> new Vec3(livingEntity.getX(), boundingBox.minY + boundingBox.getYsize() * 0.78, livingEntity.getZ());
         case 9 -> {
            entry3.intValue3 = -entry3.intValue3;
            yield this.method3793(livingEntity, entry3.intValue3, 0.58);
         }
         case 10 -> this.method3796(boundingBox, this.method3824(livingEntity).add(vec.scale(1.4)));
         default -> this.method3794(boundingBox, this.method3833(0.42F, 0.72F), 0.26);
      };
   }

   private Vec3 method3793(LivingEntity livingEntity, int n, double d) {
      AABB boundingBox = livingEntity.getBoundingBox();
      Vec3 vec = mc.player.position().subtract(livingEntity.position()).multiply(1.0, 0.0, 1.0);
      if (vec.lengthSqr() < 1.0E-6) {
         vec = new Vec3(0.0, 0.0, 1.0);
      }

      vec = vec.normalize();
      Vec3 vec2 = new Vec3(vec.z, 0.0, -vec.x);
      return this.method3796(
         boundingBox,
         new Vec3(
            livingEntity.getX() + vec2.x * boundingBox.getXsize() * 0.28 * n,
            boundingBox.minY + boundingBox.getYsize() * d,
            livingEntity.getZ() + vec2.z * boundingBox.getZsize() * 0.28 * n
         )
      );
   }

   private Vec3 method3794(AABB boundingBox, double d, double d2) {
      return this.method3796(
         boundingBox,
         new Vec3(
            boundingBox.getCenter().x + this.random.nextGaussian() * boundingBox.getXsize() * d2,
            boundingBox.minY + boundingBox.getYsize() * d + this.random.nextGaussian() * boundingBox.getYsize() * d2 * 0.45,
            boundingBox.getCenter().z + this.random.nextGaussian() * boundingBox.getZsize() * d2
         )
      );
   }

   private Vec3 method3795(AABB boundingBox) {
      Vec3 eyePosition = mc.player.getEyePosition();
      double minX = Math.abs(eyePosition.x - boundingBox.minX) < Math.abs(eyePosition.x - boundingBox.maxX) ? boundingBox.minX : boundingBox.maxX;
      double minZ = Math.abs(eyePosition.z - boundingBox.minZ) < Math.abs(eyePosition.z - boundingBox.maxZ) ? boundingBox.minZ : boundingBox.maxZ;
      return new Vec3(
         Mth.lerp(0.72, boundingBox.getCenter().x, minX),
         boundingBox.minY + boundingBox.getYsize() * this.method3833(0.44F, 0.68F),
         Mth.lerp(0.72, boundingBox.getCenter().z, minZ)
      );
   }

   private Vec3 method3796(AABB boundingBox, Vec3 vec) {
      return new Vec3(
         Mth.clamp(vec.x, boundingBox.minX, boundingBox.maxX), Mth.clamp(vec.y, boundingBox.minY, boundingBox.maxY), Mth.clamp(vec.z, boundingBox.minZ, boundingBox.maxZ)
      );
   }

   private long method3797(int n) {
      short s = switch (n) {
         case 1, 8 -> 90;
         default -> 150;
         case 3, 5, 11 -> 240;
         case 4, 10 -> 70;
         case 6 -> 45;
      };
      return s + this.random.nextInt(s + 90);
   }

   private float method3798(LivingEntity livingEntity) {
      double x = Math.max(mc.player.getEyePosition().distanceTo(livingEntity.getBoundingBox().getCenter()), 0.1);
      return (float)Math.toDegrees(Math.atan2(livingEntity.getBbWidth() * 0.5, x));
   }

   private Rotation method3799(int n, LivingEntity livingEntity, Rotation rotation) {
      Class26.Entry entry = this.method3801(n);
      if (entry.intValue != livingEntity.getId()) {
         this.method3802(entry, n, livingEntity, rotation);
      }

      entry.intValue9++;
      boolean flag = TargetUtils.method3209(16.0, rotation, livingEntity);
      if (entry.intValue2 > 0) {
         entry.intValue2--;
         return rotation;
      } else if (entry.intValue3 > 0) {
         entry.intValue3--;
         entry.floatValue *= 0.35F;
         entry.floatValue2 *= 0.35F;
         entry.floatValue3 *= 0.25F;
         entry.floatValue4 *= 0.25F;
         return rotation;
      } else {
         if (entry.intValue4 <= 0 || entry.vec3d.equals(Vec3.ZERO)) {
            entry.vec3d = this.method3807(n, livingEntity, entry);
            entry.intValue4 = this.method3806(n);
            entry.intValue5 = 0;
         }

         entry.intValue4--;
         Rotation rotation2 = this.method3826(entry.vec3d);
         Vec2f vec2f = rotation.method3131(rotation2);
         if (entry.intValue5 <= 0 || entry.intValue6 >= entry.intValue5 || entry.rotation2.method3130(rotation2) > (flag ? 4.0F : 7.0F)) {
            if (entry.intValue5 > 0 && entry.intValue6 >= entry.intValue5) {
               entry.intValue3 = this.method3805(flag);
               if (entry.intValue3 > 0) {
                  entry.intValue5 = 0;
                  return rotation;
               }
            }

            this.method3803(n, entry, rotation, rotation2, vec2f.method5327());
         }

         Rotation rotation3 = this.method3800(n, livingEntity, rotation, rotation2, flag, entry);
         entry.intValue6++;
         entry.flag = flag;
         return rotation3;
      }
   }

   private Rotation method3800(int n, LivingEntity livingEntity, Rotation rotation, Rotation rotation2, boolean flag, Class26.Entry entry) {
      Vec2f vec2f = rotation.method3131(rotation2);
      float floatValue = vec2f.getFloatValue();
      float f = vec2f.getFloatValue2();
      float f2 = Math.max(Rotation.method3138(), 1.0E-4F);
      float f3 = Mth.clamp((entry.intValue6 + 1.0F) / Math.max(entry.intValue5, 1), 0.0F, 1.0F);
      float floatValue2;
      float f4;
      switch (n) {
         case 0:
            float f5 = this.method3809(Mth.clamp((float)entry.intValue6 / Math.max(entry.intValue5, 1), 0.0F, 1.0F));
            float f6 = this.method3809(f3);
            Vec2f vec2f2 = entry.rotation.method3131(entry.rotation2);
            floatValue2 = vec2f2.getFloatValue() * (f6 - f5);
            f4 = vec2f2.getFloatValue2() * (f6 - f5);
            floatValue2 = Mth.clamp(floatValue2, -19.65F, 19.65F);
            f4 = Mth.clamp(f4, -10.5F, 10.5F);
            break;
         case 1:
            float f7 = Mth.clamp(entry.floatValue7, 0.18F, 0.84F);
            float f8 = f3 < f7 ? (float)Math.pow(f3 / f7, 1.35) : (float)Math.pow((1.0F - f3) / (1.0F - f7), 0.72);
            f8 = Mth.clamp(f8, 0.12F, 1.0F);
            float f9 = Math.max(entry.intValue5 - entry.intValue6, 1);
            floatValue2 = floatValue / f9 * (0.65F + f8 * 0.72F);
            f4 = f / f9 * (0.58F + f8 * 0.68F);
            floatValue2 = Mth.clamp(floatValue2, -12.6F, 12.6F);
            f4 = Mth.clamp(f4, -7.5F, 7.5F);
            break;
         case 2:
            Vec2f vec2f3 = rotation.method3131(entry.rotation2);
            float f10 = Math.max(vec2f3.method5327(), 1.0E-4F);
            float f11 = Mth.clamp(f10 * 0.74F, 1.35F, 41.7F);
            floatValue2 = vec2f3.getFloatValue() / f10 * Math.min(f10, f11);
            f4 = vec2f3.getFloatValue2() / f10 * Math.min(f10, Math.min(f11 * 0.55F, 17.8F));
            if (entry.intValue6 > entry.intValue5 * 0.58F) {
               floatValue2 = Mth.clamp(floatValue * 0.42F, -19.65F, 19.65F);
               f4 = Mth.clamp(f * 0.36F, -10.5F, 10.5F);
            }
            break;
         case 3:
            float f12 = Math.max(this.method3798(livingEntity), 0.25F);
            if (flag && vec2f.method5327() < f12 * 0.65F) {
               entry.floatValue5 = entry.floatValue5 * 0.72F + this.method3833(-0.16F, 0.16F);
               entry.floatValue6 = entry.floatValue6 * 0.68F + this.method3833(-0.11F, 0.11F);
               floatValue2 = floatValue * 0.14F + entry.floatValue5;
               f4 = f * 0.11F + entry.floatValue6;
            } else {
               floatValue2 = floatValue * 0.38F;
               f4 = f * 0.31F;
            }

            floatValue2 = Mth.clamp(floatValue2, -15.9F, 15.9F);
            f4 = Mth.clamp(f4, -9.9F, 9.9F);
            break;
         case 4:
            if (entry.intValue7 > 0) {
               entry.intValue7--;
            }

            if (!entry.flag3 && this.random.nextFloat() < 0.48F) {
               entry.flag3 = true;
            }

            if (!entry.flag4 && entry.intValue7 <= 0 && this.random.nextFloat() < 0.42F) {
               entry.flag4 = true;
            }

            floatValue2 = entry.flag3 ? floatValue * this.method3833(0.28F, 0.62F) : 0.0F;
            f4 = entry.flag4 ? f * this.method3833(0.22F, 0.51F) : 0.0F;
            if (this.random.nextFloat() < 0.067F) {
               f4 = 0.0F;
            }

            floatValue2 = Mth.clamp(floatValue2, -30.0F, 30.0F);
            f4 = Mth.clamp(f4, -14.0F, 14.0F);
            break;
         case 5:
            float f13 = (float)Math.sin(f3 * Math.PI);
            float f14 = Math.max(entry.intValue5 - entry.intValue6, 1);
            floatValue2 = floatValue / f14 * (0.72F + f13 * 0.7F);
            f4 = f / f14 * (0.66F + f13 * 0.62F);
            entry.floatValue5 = entry.floatValue5 * 0.8F + this.method3833(-0.18F, 0.18F);
            entry.floatValue6 = entry.floatValue6 * 0.76F + this.method3833(-0.12F, 0.12F);
            floatValue2 = Mth.clamp(floatValue2 + entry.floatValue5, -19.65F, 19.65F);
            f4 = Mth.clamp(f4 + entry.floatValue6, -10.5F, 10.5F);
            break;
         case 6:
            float f15 = entry.flag2 ? 0.24F : 0.42F;
            float f16 = entry.flag2 ? 0.43F : 0.62F;
            entry.floatValue = entry.floatValue * f16 + floatValue * f15;
            entry.floatValue2 = entry.floatValue2 * (f16 + 0.05F) + f * f15 * 0.72F;
            if (!entry.flag2 && f3 > entry.floatValue7) {
               entry.flag2 = true;
               entry.floatValue = entry.floatValue * this.method3833(0.28F, 0.52F);
               entry.floatValue2 = entry.floatValue2 * this.method3833(0.3F, 0.58F);
            }

            floatValue2 = Mth.clamp(entry.floatValue, -24.0F, 24.0F);
            f4 = Mth.clamp(entry.floatValue2, -12.0F, 12.0F);
            break;
         case 7:
            float f17 = Mth.clamp(floatValue * 0.46F, -41.7F, 41.7F);
            float f18 = Mth.clamp(f * 0.39F, -17.8F, 17.8F);
            float f19 = Mth.clamp(f17 - entry.floatValue, -11.0F * f2, 11.0F * f2);
            float f20 = Mth.clamp(f18 - entry.floatValue2, -8.0F * f2, 8.0F * f2);
            entry.floatValue3 = entry.floatValue3 + Mth.clamp(f19 - entry.floatValue3, -17.0F * f2, 17.0F * f2);
            entry.floatValue4 = entry.floatValue4 + Mth.clamp(f20 - entry.floatValue4, -11.0F * f2, 11.0F * f2);
            entry.floatValue = entry.floatValue + entry.floatValue3;
            entry.floatValue2 = entry.floatValue2 + entry.floatValue4;
            floatValue2 = Mth.clamp(entry.floatValue, -41.7F, 41.7F);
            f4 = Mth.clamp(entry.floatValue2, -17.8F, 17.8F);
            break;
         case 8:
            float max = flag ? 15.9F : 24.9F;
            float max2 = flag ? 9.9F : 11.45F;
            float f21 = flag ? 0.26F : 0.47F;
            if (flag != entry.flag) {
               entry.floatValue *= flag ? 0.56F : 0.82F;
               entry.floatValue2 *= flag ? 0.52F : 0.8F;
            }

            entry.floatValue = entry.floatValue * (flag ? 0.38F : 0.56F) + floatValue * f21;
            entry.floatValue2 = entry.floatValue2 * (flag ? 0.42F : 0.6F) + f * f21 * 0.72F;
            floatValue2 = Mth.clamp(entry.floatValue, -max, max);
            f4 = Mth.clamp(entry.floatValue2, -max2, max2);
            break;
         default:
            float[] floats = new float[]{5.4F, 12.6F, 19.65F};
            float[] floats2 = new float[]{3.6F, 7.5F, 10.5F};
            float[] floats3 = new float[]{0.23F, 0.38F, 0.55F};
            int n2 = Mth.clamp(entry.intValue8, 0, 2);
            float f22 = Mth.clamp(1.0F - entry.intValue9 / 900.0F, 0.72F, 1.0F);
            entry.floatValue = entry.floatValue * (0.46F + n2 * 0.07F) + floatValue * floats3[n2];
            entry.floatValue2 = entry.floatValue2 * (0.5F + n2 * 0.06F) + f * floats3[n2] * 0.7F;
            floatValue2 = Mth.clamp(entry.floatValue * f22, -floats[n2], floats[n2]);
            f4 = Mth.clamp(entry.floatValue2 * f22, -floats2[n2], floats2[n2]);
      }

      floatValue2 = Mth.clamp(floatValue2, -60.0F, 60.0F);
      f4 = Mth.clamp(f4, -40.0F, 40.0F);
      boolean flag2 = n == 2 && entry.intValue6 <= entry.intValue5 * 0.58F || n == 6 && !entry.flag2;
      if (!flag2) {
         if (Math.signum(floatValue2) == Math.signum(floatValue) && Math.abs(floatValue2) > Math.abs(floatValue)) {
            floatValue2 = floatValue;
         }

         if (Math.signum(f4) == Math.signum(f) && Math.abs(f4) > Math.abs(f)) {
            f4 = f;
         }
      }

      return this.method3780(rotation, rotation.getFloatValue() + floatValue2, rotation.getFloatValue2() + f4);
   }

   private Class26.Entry method3801(int n) {
      if (this.entrys == null || this.entrys.length != 10) {
         this.entrys = new Class26.Entry[10];
      }

      if (this.entrys[n] == null) {
         this.entrys[n] = new Class26.Entry();
      }

      return this.entrys[n];
   }

   private void method3802(Class26.Entry entry, int n, LivingEntity livingEntity, Rotation rotation) {
      entry.intValue = livingEntity.getId();
      entry.intValue2 = this.random.nextFloat() < 0.7F ? this.random.nextInt(3) : 3 + this.random.nextInt(4);
      entry.intValue3 = 0;
      entry.intValue4 = 0;
      entry.intValue5 = 0;
      entry.intValue6 = 0;
      entry.intValue9 = 0;
      entry.intValue8 = this.random.nextFloat() < 0.28F ? 0 : (this.random.nextFloat() < 0.78F ? 1 : 2);
      entry.floatValue = entry.floatValue2 = 0.0F;
      entry.floatValue3 = entry.floatValue4 = 0.0F;
      entry.floatValue5 = entry.floatValue6 = 0.0F;
      entry.floatValue8 = 0.52F;
      entry.floatValue9 = 0.48F;
      entry.rotation = rotation;
      entry.rotation2 = rotation;
      entry.vec3d = Vec3.ZERO;
      entry.flag = false;
      entry.flag2 = false;
      entry.flag3 = n != 4;
      entry.flag4 = n != 4;
      entry.intValue7 = 0;
   }

   private void method3803(int n, Class26.Entry entry, Rotation rotation, Rotation rotation2, float f) {
      entry.intValue5 = this.method3804(n, f);
      entry.intValue6 = 0;
      entry.rotation = rotation;
      entry.rotation2 = rotation2;
      entry.floatValue7 = this.method3833(0.18F, 0.84F);
      entry.flag2 = false;
      entry.flag3 = n != 4 || this.random.nextFloat() < 0.72F;
      entry.flag4 = n != 4 || this.random.nextFloat() < 0.61F;
      entry.intValue7 = n == 4 ? this.random.nextInt(4) : 0;
      if (n == 2 || n == 6) {
         Vec2f vec2f = rotation.method3131(rotation2);
         float f2 = Mth.clamp(f / 70.0F, 0.2F, 1.0F);
         float copySign = Math.copySign(
            this.method3833(0.45F, 3.2F) * f2, vec2f.getFloatValue() == 0.0F ? (this.random.nextBoolean() ? 1.0F : -1.0F) : vec2f.getFloatValue()
         );
         float copySign2 = Math.copySign(
            this.method3833(0.2F, 1.35F) * f2, vec2f.getFloatValue2() == 0.0F ? (this.random.nextBoolean() ? 1.0F : -1.0F) : vec2f.getFloatValue2()
         );
         entry.rotation2 = new Rotation(rotation2.getFloatValue() + copySign, rotation2.getFloatValue2() + copySign2);
      }
   }

   private int method3804(int n, float f) {
      if (n == 2) {
         return 2 + this.random.nextInt(5);
      } else {
         float nextFloat = this.random.nextFloat();
         int n2;
         if (nextFloat < 0.5F) {
            n2 = 3 + this.random.nextInt(7);
         } else if (nextFloat < 0.75F) {
            n2 = 10 + this.random.nextInt(8);
         } else if (nextFloat < 0.9F) {
            n2 = 18 + this.random.nextInt(13);
         } else if (nextFloat < 0.95F) {
            n2 = 31 + this.random.nextInt(11);
         } else {
            n2 = 42 + this.random.nextInt(30);
         }

         if (n == 1) {
            n2 = Math.max(n2, 10 + this.random.nextInt(12));
         }

         if (f > 80.0F) {
            n2 = Math.max(3, Math.round(n2 * 0.72F));
         }

         return n2;
      }
   }

   private int method3805(boolean flag) {
      float f = flag ? 0.34F : 0.28F;
      if (this.random.nextFloat() < f) {
         return 0;
      } else {
         float nextFloat = this.random.nextFloat();
         if (nextFloat < 0.7F) {
            return 1;
         } else if (nextFloat < 0.9F) {
            return 2 + this.random.nextInt(3);
         } else {
            return nextFloat < 0.98F ? 5 + this.random.nextInt(3) : 8 + this.random.nextInt(10);
         }
      }
   }

   private int method3806(int n) {
      if (n == 3) {
         return 4 + this.random.nextInt(9);
      } else {
         return n == 5 ? 6 + this.random.nextInt(24) : 8 + this.random.nextInt(22);
      }
   }

   private Vec3 method3807(int n, LivingEntity livingEntity, Class26.Entry entry) {
      float f;
      float f2;
      switch (n) {
         case 0:
            f = this.method3833(0.28F, 0.76F);
            f2 = this.method3833(0.25F, 0.7F);
            break;
         case 1:
            f = Mth.clamp(0.52F + (float)this.random.nextGaussian() * 0.17F, 0.05F, 0.98F);
            f2 = Mth.clamp(0.48F + (float)this.random.nextGaussian() * 0.15F, 0.05F, 0.95F);
            break;
         case 2:
            f = this.method3833(0.12F, 0.92F);
            f2 = this.method3833(0.18F, 0.82F);
            break;
         case 3:
            f = Mth.clamp(entry.floatValue8 + this.method3833(-0.1F, 0.1F), 0.12F, 0.9F);
            f2 = Mth.clamp(entry.floatValue9 + this.method3833(-0.08F, 0.08F), 0.12F, 0.86F);
            break;
         case 4:
            f = this.method3833(0.18F, 0.86F);
            f2 = this.method3833(0.16F, 0.8F);
            break;
         case 5:
            f = this.method3833(0.03F, 0.99F);
            f2 = this.method3833(0.05F, 0.95F);
            break;
         case 6:
            f = entry.floatValue8 < 0.5F ? this.method3833(0.5F, 0.88F) : this.method3833(0.15F, 0.5F);
            f2 = this.method3833(0.22F, 0.78F);
            break;
         case 7:
            f = Mth.clamp(0.52F + (float)this.random.nextGaussian() * 0.22F, 0.04F, 0.98F);
            f2 = Mth.clamp(0.48F + (float)this.random.nextGaussian() * 0.19F, 0.04F, 0.95F);
            break;
         case 8:
            Vec3 vec = this.method3824(livingEntity);
            AABB boundingBox = livingEntity.getBoundingBox();
            f = (float)Mth.clamp((vec.x - boundingBox.minX) / Math.max(boundingBox.getXsize(), 1.0E-5), 0.08, 0.94);
            f2 = (float)Mth.clamp((vec.y - boundingBox.minY) / Math.max(boundingBox.getYsize(), 1.0E-5), 0.08, 0.92);
            break;
         default:
            float f3 = entry.intValue8 == 0 ? 0.22F : (entry.intValue8 == 1 ? 0.34F : 0.46F);
            f = Mth.clamp(0.52F + this.method3833(-f3, f3), 0.03F, 0.99F);
            f2 = Mth.clamp(0.48F + this.method3833(-f3 * 0.86F, f3 * 0.86F), 0.04F, 0.95F);
      }

      entry.floatValue8 = f;
      entry.floatValue9 = f2;
      return this.method3808(livingEntity, f, f2, this.method3833(0.22F, 0.78F));
   }

   private Vec3 method3808(LivingEntity livingEntity, float f, float f2, float f3) {
      AABB boundingBox = livingEntity.getBoundingBox();
      Vec3 vec = mc.player.getEyePosition().subtract(boundingBox.getCenter()).multiply(1.0, 0.0, 1.0);
      if (vec.lengthSqr() < 1.0E-6) {
         vec = new Vec3(0.0, 0.0, 1.0);
      }

      vec = vec.normalize();
      Vec3 vec2 = new Vec3(vec.z, 0.0, -vec.x);
      double d = Math.max(boundingBox.getXsize(), boundingBox.getZsize()) * 0.5;
      Vec3 vec3 = boundingBox.getCenter().add(vec2.scale((f - 0.5F) * d * 1.86)).add(vec.scale((f3 - 0.5F) * d * 1.2));
      return this.method3796(boundingBox, new Vec3(vec3.x, boundingBox.minY + boundingBox.getYsize() * f2, vec3.z));
   }

   private float method3809(float f) {
      f = Mth.clamp(f, 0.0F, 1.0F);
      return f * f * f * (10.0F + f * (-15.0F + 6.0F * f));
   }

   private Vec3 method3810(LivingEntity livingEntity, float floatValue) {
      Vec3 eyePosition = mc.player.getEyePosition();
      Vec3 position = livingEntity.position();
      Vec3 vec = position.add(0.0, livingEntity.getBbHeight() * 0.52, 0.0);
      Vec3 vec2 = vec.subtract(eyePosition);
      float f = (float)Mth.wrapDegrees(Math.toDegrees(Math.atan2(vec2.z, vec2.x)) - 90.0);
      float f2 = Mth.wrapDegrees(f - floatValue);
      float alpha1 = Mth.clamp(Math.abs(f2) / 40.0F, 0.0F, 1.0F);
      if (alpha1 <= 0.01F) {
         return vec2;
      } else {
         Vec3 vec3 = new Vec3(vec2.x, 0.0, vec2.z);
         if (vec3.lengthSqr() > 1.0E-6) {
            vec3 = vec3.normalize();
         }

         Vec3 vec4 = new Vec3(-vec3.z, 0.0, vec3.x);
         double d = f2 > 0.0F ? -1.0 : 1.0;
         Vec3 vec5 = new Vec3(
               vec.x + vec4.x * livingEntity.getBbWidth() * 0.32 * d, position.y + livingEntity.getBbHeight() * 0.78, vec.z + vec4.z * livingEntity.getBbWidth() * 0.32 * d
            )
            .subtract(eyePosition);
         return new Vec3(
            Mth.lerp((double)alpha1, vec2.x, vec5.x), Mth.lerp((double)alpha1, vec2.y, vec5.y), Mth.lerp((double)alpha1, vec2.z, vec5.z)
         );
      }
   }

   private Rotation method3811(Rotation rotation, float yRot, float f) {
      float f2 = Rotation.method3138();
      yRot = rotation.getFloatValue() + Math.round((yRot - rotation.getFloatValue()) / f2) * f2;
      f = rotation.getFloatValue2() + Math.round((f - rotation.getFloatValue2()) / f2) * f2;
      return new Rotation(yRot, Mth.clamp(f, -89.0F, 89.0F));
   }

   private float method3812(float f, float floatValue, float magnitude) {
      float copySign = Math.round(f / magnitude) * magnitude;
      if (copySign == 0.0F && Math.abs(floatValue) > magnitude * 0.6F) {
         copySign = Math.copySign(magnitude, floatValue);
      }

      if (Math.signum(copySign) == Math.signum(floatValue) && Math.abs(copySign) > Math.abs(floatValue)) {
         copySign = floatValue;
      }

      return copySign;
   }

   private Rotation method3813(Vec3 vec) {
      return Rotation.method3128(vec, mc.player.position());
   }

   private void method3814(LivingEntity livingEntity) {
      AABB boundingBox = livingEntity.getBoundingBox();
      this.doubleValue = (Math.random() - 0.5) * boundingBox.getXsize() * 0.15;
      this.doubleValue2 = (Math.random() - 0.5) * boundingBox.getYsize() * 0.09;
      this.doubleValue3 = (Math.random() - 0.5) * boundingBox.getZsize() * 0.15;
   }

   private float[] method3815(float f) {
      this.floatValue11 = this.floatValue11 + (0.031F + this.random.nextFloat() * 0.021F);
      this.floatValue12 = this.floatValue12 + (0.057F + this.random.nextFloat() * 0.014F);
      float f2 = 1.35F * Mth.clamp(f / 5.0F, 0.2F, 1.0F);
      float f3 = ((float)Math.sin(this.floatValue11 * 1.05) * 0.33F + (float)Math.sin(this.floatValue12 * 1.31 + 0.48) * 0.29F) * f2;
      float f4 = ((float)Math.cos(this.floatValue11 * 0.62 + 1.1) * 0.24F + (float)Math.cos(this.floatValue12 * 1.87 + 2.05) * 0.2F) * f2 * 0.46F;
      f3 += (this.random.nextFloat() - 0.5F) * f2 * 0.1F;
      f4 += (this.random.nextFloat() - 0.5F) * f2 * 0.07F;
      return new float[]{f3, f4};
   }

   private Rotation method3816(float yRot, float xRot, float f) {
      xRot = Mth.clamp(xRot, -89.0F, 89.0F);
      yRot -= (yRot - this.floatValue9) % f;
      xRot -= (xRot - this.floatValue10) % f;
      this.floatValue9 = yRot;
      this.floatValue10 = xRot;
      return new Rotation(yRot, xRot);
   }

   private float method3817() {
      double d = mc.options.sensitivity().get() * 0.6 + 0.2;
      return (float)(d * d * d * 1.2);
   }

   private boolean method3818(LivingEntity livingEntity) {
      Vec3 vec = new Vec3(mc.player.getX() - mc.player.xo, mc.player.getY() - mc.player.yo, mc.player.getZ() - mc.player.zo);
      Vec3 vec2 = new Vec3(livingEntity.getX() - livingEntity.xo, livingEntity.getY() - livingEntity.yo, livingEntity.getZ() - livingEntity.zo);
      Vec3 vec3 = livingEntity.position().subtract(mc.player.position()).normalize();
      double d = vec.dot(vec3) + vec2.dot(vec3.scale(-1.0));
      double hypot = Math.hypot(mc.player.getX() - livingEntity.getX(), mc.player.getZ() - livingEntity.getZ());
      return d > 0.05 && hypot < 4.0;
   }

   private AABB method3819(LivingEntity livingEntity) {
      AABB boundingBox = livingEntity.getBoundingBox();
      double d = livingEntity.isFallFlying() ? -0.5 : 0.1;
      double d2 = livingEntity.isFallFlying() ? -0.5 : 0.1;
      return new AABB(
         boundingBox.minX + boundingBox.getXsize() * d / 2.0,
         boundingBox.minY,
         boundingBox.minZ + boundingBox.getZsize() * d / 2.0,
         boundingBox.maxX - boundingBox.getXsize() * d / 2.0,
         boundingBox.maxY - boundingBox.getYsize() * d2,
         boundingBox.maxZ - boundingBox.getZsize() * d / 2.0
      );
   }

   private float method3820(float f) {
      f = Mth.clamp(f, 0.0F, 1.0F);
      float f2 = 1.0F - f;
      return 1.0F - f2 * f2 * f2;
   }

   private float method3821(float f) {
      f = Mth.clamp(f, 0.0F, 1.0F);
      return f < 0.5F ? 2.0F * f * f : 1.0F - (float)Math.pow(-2.0F * f + 2.0F, 2.0) / 2.0F;
   }

   private float method3822(float f, float f2, float f3) {
      return f + Mth.wrapDegrees(f2 - f) * Mth.clamp(f3, 0.0F, 1.0F);
   }

   private float method3823(float f, float f2) {
      return Mth.lerp(this.secureRandom.nextFloat(), f, f2);
   }

   private Vec3 method3824(LivingEntity livingEntity) {
      AABB boundingBox = livingEntity.getBoundingBox();
      Vec3 eyePosition = mc.player.getEyePosition();
      return new Vec3(
         Mth.clamp(eyePosition.x, boundingBox.minX, boundingBox.maxX), Mth.clamp(eyePosition.y, boundingBox.minY, boundingBox.maxY), Mth.clamp(eyePosition.z, boundingBox.minZ, boundingBox.maxZ)
      );
   }

   private Vec3 method3825(LivingEntity livingEntity) {
      Vec3 vec = this.method3824(livingEntity);
      AABB boundingBox = livingEntity.getBoundingBox();
      return new Vec3(vec.x, Mth.clamp(Mth.lerp(0.5, mc.player.getY(), livingEntity.getEyeY()), boundingBox.minY, boundingBox.maxY), vec.z);
   }

   private Rotation method3826(Vec3 center) {
      return Rotation.method3128(center, mc.player.getEyePosition());
   }

   private Rotation method3827(Rotation rotation, Rotation rotation2, float max, float f) {
      Vec2f vec2f = rotation.method3131(rotation2);
      return new Rotation(
         rotation.getFloatValue() + Mth.clamp(vec2f.getFloatValue(), -max, max),
         rotation.getFloatValue2() + Mth.clamp(vec2f.getFloatValue2(), -f, f)
      );
   }

   private void method3828() {
      if (mc.player.tickCount % 500 == 0) {
         this.entry2 = new Class26.Entry2();
         this.floatValue2 = 1.0F;
         this.longValue2 = System.currentTimeMillis();
      }
   }

   private void method3829(Rotation rotation, Rotation rotation2) {
      Vec2f vec2f = rotation.method3131(rotation2);
      if (Math.abs(vec2f.getFloatValue()) + Math.abs(vec2f.getFloatValue2()) < 10.0F) {
         this.floatValue2 = Math.max(0.0F, this.floatValue2 - 0.05F);
      }
   }

   private float method3830(float f) {
      float f2 = Rotation.method3138();
      return Math.round(f / f2) * f2;
   }

   private float method3831(float f, float f2) {
      return Mth.clamp(f / 90.0F, 0.2F, 1.0F) * f2;
   }

   private float method3832(float f) {
      return Mth.clamp(f / 45.0F, 0.2F, 1.0F);
   }

   private float method3833(float f, float f2) {
      return f + this.random.nextFloat() * (f2 - f);
   }

   private void method3834() {
      this.flag = false;
      this.flag2 = false;
      this.floatValue = 1.0F;
      this.entry2 = new Class26.Entry2();
      this.floatValue2 = 1.0F;
      this.longValue2 = System.currentTimeMillis();
      this.longValue3 = this.longValue2;
      this.intValue2 = Integer.MIN_VALUE;
      this.flag3 = false;
      this.flag4 = false;
      this.intValue3 = Integer.MIN_VALUE;
      this.floatValue14 = 0.0F;
      this.floatValue15 = 0.0F;
      this.intValue8 = Integer.MIN_VALUE;
      this.intValue9 = Integer.MIN_VALUE;
      this.intValue10 = Integer.MIN_VALUE;
      this.intValue11 = Integer.MIN_VALUE;
      this.intValue13 = Integer.MIN_VALUE;
      this.intValue14 = Integer.MIN_VALUE;
      this.intValue15 = Integer.MIN_VALUE;
      this.intValue16 = Integer.MIN_VALUE;
      this.intValue19 = Integer.MIN_VALUE;
      this.intValue20 = Integer.MIN_VALUE;
      this.entry3s = new Class26.Entry3[12];
      this.entrys = new Class26.Entry[10];
   }

   private static final class Entry {
      private int intValue = Integer.MIN_VALUE;
      private int intValue2;
      private int intValue3;
      private int intValue4;
      private int intValue5;
      private int intValue6;
      private int intValue7;
      private int intValue8;
      private int intValue9;
      private float floatValue;
      private float floatValue2;
      private float floatValue3;
      private float floatValue4;
      private float floatValue5;
      private float floatValue6;
      private float floatValue7;
      private float floatValue8 = 0.52F;
      private float floatValue9 = 0.48F;
      private boolean flag;
      private boolean flag2;
      private boolean flag3;
      private boolean flag4;
      private Vec3 vec3d = Vec3.ZERO;
      private Rotation rotation = Rotation.INSTANCE;
      private Rotation rotation2 = Rotation.INSTANCE;
   }

   private static final class Entry2 {
      private final int intValue = new Random().nextInt();

      private double method3835(double d) {
         return this.method3836(d, 0.0);
      }

      private double method3836(double d, double d2) {
         int n = (int)Math.floor(d);
         int n2 = (int)Math.floor(d2);
         double d3 = method3838(d - n);
         double d4 = method3838(d2 - n2);
         double d5 = method3839(this.method3837(n, n2), this.method3837(n + 1, n2), d3);
         double d6 = method3839(this.method3837(n, n2 + 1), this.method3837(n + 1, n2 + 1), d3);
         return method3839(d5, d6, d4);
      }

      private double method3837(int n, int n2) {
         int n3 = n * 374761393 + n2 * 668265263 + this.intValue * 1442695041;
         n3 = (n3 ^ n3 >>> 13) * 1274126177;
         return ((n3 ^ n3 >>> 16) & 2147483647) / 1.073741823E9 - 1.0;
      }

      private static double method3838(double d) {
         return d * d * d * (d * (d * 6.0 - 15.0) + 10.0);
      }

      private static double method3839(double d, double d2, double d3) {
         return d + (d2 - d) * d3;
      }
   }

   private static final class Entry3 {
      private int intValue = Integer.MIN_VALUE;
      private long longValue;
      private long longValue2;
      private long longValue3;
      private long longValue4;
      private long longValue5;
      private long longValue6;
      private int intValue2;
      private int intValue3 = 1;
      private int intValue4;
      private float floatValue;
      private float floatValue2;
      private float floatValue3;
      private float floatValue4;
      private float floatValue5;
      private float floatValue6;
      private float floatValue7;
      private float floatValue8;
      private Vec3 vec3d = Vec3.ZERO;
      private Rotation rotation = Rotation.INSTANCE;
      private Rotation rotation2 = Rotation.INSTANCE;
      private long longValue7;
      private long longValue8;
   }
}
