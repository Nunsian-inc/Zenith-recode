package org.zenith.utility.game;

import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public final class ElytraMath {
   private ElytraMath() {
   }

   public static Vec3 method5972(LivingEntity var0, Vec3 var1, Vec3 var2) {
      return method5973(var0, var1, var2, 3.2);
   }

   public static Vec3 method5973(LivingEntity var0, Vec3 var1, Vec3 var2, double var3) {
      return method5974(var0, var1, var2, var3).multiply(0.99F, 0.98F, 0.99F);
   }

   public static Vec3 method5974(LivingEntity var0, Vec3 var1, Vec3 var2, double var3) {
      float var5 = (float)(-Math.asin(Mth.clamp(var2.y, -1.0, 1.0)));
      double var6 = Math.sqrt(var2.x * var2.x + var2.z * var2.z);
      double var8 = var1.horizontalDistance();
      double var10 = method5975(var0);
      double var12 = Mth.square(Math.cos(var5));
      var1 = var1.add(0.0, var10 * (-1.0 + var12 * 0.75), 0.0);
      if (var1.y < 0.0 && var6 > 0.0) {
         double var14 = var1.y * -0.1 * var12;
         var1 = var1.add(var2.x * var14 / var6, var14, var2.z * var14 / var6);
      }

      if (var5 < 0.0F && var6 > 0.0) {
         double var17 = var8 * -Mth.sin(var5) * 0.04;
         var1 = var1.add(-var2.x * var17 / var6, var17 * var3, -var2.z * var17 / var6);
      }

      if (var6 > 0.0) {
         var1 = var1.add((var2.x / var6 * var8 - var1.x) * 0.1, 0.0, (var2.z / var6 * var8 - var1.z) * 0.1);
      }

      return var1;
   }

   public static double method5975(LivingEntity var0) {
      double var1 = var0.getGravity();
      return var0.getDeltaMovement().y <= 0.0 && var0.hasEffect(MobEffects.SLOW_FALLING) ? Math.min(var1, 0.01) : var1;
   }
}
