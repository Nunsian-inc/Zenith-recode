package org.zenith.utility.game;

import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public final class ElytraMath {
   private ElytraMath() {
   }

   public static Vec3 method5972(LivingEntity player, Vec3 vec, Vec3 lookAngle) {
      return method5973(player, vec, lookAngle, 3.2);
   }

   public static Vec3 method5973(LivingEntity player, Vec3 vec, Vec3 lookAngle, double d) {
      return method5974(player, vec, lookAngle, d).multiply(0.99F, 0.98F, 0.99F);
   }

   public static Vec3 method5974(LivingEntity player, Vec3 vec, Vec3 lookAngle, double d) {
      float f = (float)(-Math.asin(Mth.clamp(lookAngle.y, -1.0, 1.0)));
      double d2 = Math.sqrt(lookAngle.x * lookAngle.x + lookAngle.z * lookAngle.z);
      double horizontalDistance = vec.horizontalDistance();
      double d3 = method5975(player);
      double square = Mth.square(Math.cos(f));
      vec = vec.add(0.0, d3 * (-1.0 + square * 0.75), 0.0);
      if (vec.y < 0.0 && d2 > 0.0) {
         double y = vec.y * -0.1 * square;
         vec = vec.add(lookAngle.x * y / d2, y, lookAngle.z * y / d2);
      }

      if (f < 0.0F && d2 > 0.0) {
         double d4 = horizontalDistance * -Mth.sin(f) * 0.04;
         vec = vec.add(-lookAngle.x * d4 / d2, d4 * d, -lookAngle.z * d4 / d2);
      }

      if (d2 > 0.0) {
         vec = vec.add((lookAngle.x / d2 * horizontalDistance - vec.x) * 0.1, 0.0, (lookAngle.z / d2 * horizontalDistance - vec.z) * 0.1);
      }

      return vec;
   }

   public static double method5975(LivingEntity player) {
      double gravity = player.getGravity();
      return player.getDeltaMovement().y <= 0.0 && player.hasEffect(MobEffects.SLOW_FALLING) ? Math.min(gravity, 0.01) : gravity;
   }
}
