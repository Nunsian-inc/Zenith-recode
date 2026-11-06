package org.zenith.utility.mixin.entity;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.zenith.Rotation;
import org.zenith.Zenith;
import org.zenith.utility.misc.ModuleHookDefinitions;

@Mixin({FireworkRocketEntity.class})
public abstract class MixinFireworkRocketEntity {
   @Shadow
   public LivingEntity attachedToEntity;
   @Shadow
   private int life;

   @ModifyExpressionValue(
      method = {"tick()V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/phys/Vec3;add(DDD)Lnet/minecraft/world/phys/Vec3;",
         ordinal = 0
      )}
   )
   private Vec3 hookExtendedFirework(Vec3 vec3d) {
      Minecraft minecraft = Minecraft.getInstance();
      if (this.attachedToEntity != minecraft.player || minecraft.player == null) {
         return vec3d;
      } else if (ModuleHookDefinitions.class123.active()) {
         Fireworks fireworks = ((FireworkRocketEntity)(Object)this).getItem().get(DataComponents.FIREWORKS);
         int flightDuration = fireworks == null ? 0 : fireworks.flightDuration();
         Rotation currentRotation = Zenith.getInstance().getRotationManager().getCurrentRotation();
         Vec3 lookAngle = currentRotation == null ? minecraft.player.getLookAngle() : currentRotation.toDirection();
         return ModuleHookDefinitions.class123
               .dispatch(new ModuleHookDefinitions.Entry4(vec3d, this.attachedToEntity.getDeltaMovement(), lookAngle, this.life, 10 * (1 + flightDuration)))
            .vec3d;
      } else {
         return vec3d;
      }
   }

   @ModifyExpressionValue(
      method = {"tick()V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/LivingEntity;getLookAngle()Lnet/minecraft/world/phys/Vec3;"
      )}
   )
   private Vec3 getRotationVector(Vec3 vec) {
      if (this.attachedToEntity != Minecraft.getInstance().player) {
         return vec;
      } else {
         Rotation currentRotation = Zenith.getInstance().getRotationManager().getCurrentRotation();
         return currentRotation == null ? vec : currentRotation.toDirection();
      }
   }
}
