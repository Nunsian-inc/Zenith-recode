package org.zenith.utility.game;

import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public final class ElytraMath {
   private ElytraMath() {
   }

   public static Vec3 method5972(LivingEntity player, Vec3 velocity, Vec3 lookAngle) {
      return method5973(player, velocity, lookAngle, 3.2);
   }

   public static Vec3 method5973(LivingEntity player, Vec3 velocity, Vec3 lookAngle, double liftMultiplier) {
      return method5974(player, velocity, lookAngle, liftMultiplier).multiply(0.99F, 0.98F, 0.99F);
   }

   public static Vec3 method5974(LivingEntity player, Vec3 velocity, Vec3 lookAngle, double liftMultiplier) {
      float pitch = (float)(-Math.asin(Mth.clamp(lookAngle.y, -1.0, 1.0)));
      double horizontalLookLength = Math.sqrt(lookAngle.x * lookAngle.x + lookAngle.z * lookAngle.z);
      double horizontalSpeed = velocity.horizontalDistance();
      double gravity = method5975(player);
      double cosPitchSquared = Mth.square(Math.cos(pitch));
      velocity = velocity.add(0.0, gravity * (-1.0 + cosPitchSquared * 0.75), 0.0);
      if (velocity.y < 0.0 && horizontalLookLength > 0.0) {
         double downwardLift = velocity.y * -0.1 * cosPitchSquared;
         velocity = velocity.add(lookAngle.x * downwardLift / horizontalLookLength, downwardLift, lookAngle.z * downwardLift / horizontalLookLength);
      }

      if (pitch < 0.0F && horizontalLookLength > 0.0) {
         double upwardLift = horizontalSpeed * -Mth.sin(pitch) * 0.04;
         velocity = velocity.add(-lookAngle.x * upwardLift / horizontalLookLength, upwardLift * liftMultiplier, -lookAngle.z * upwardLift / horizontalLookLength);
      }

      if (horizontalLookLength > 0.0) {
         velocity = velocity.add((lookAngle.x / horizontalLookLength * horizontalSpeed - velocity.x) * 0.1, 0.0, (lookAngle.z / horizontalLookLength * horizontalSpeed - velocity.z) * 0.1);
      }

      return velocity;
   }

   public static double method5975(LivingEntity player) {
      double gravity = player.getGravity();
      return player.getDeltaMovement().y <= 0.0 && player.hasEffect(MobEffects.SLOW_FALLING) ? Math.min(gravity, 0.01) : gravity;
   }
}
