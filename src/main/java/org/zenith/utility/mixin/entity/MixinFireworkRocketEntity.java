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
   private Vec3 hookExtendedFirework(Vec3 var1) {
      Minecraft var2 = Minecraft.getInstance();
      if (this.attachedToEntity != var2.player || var2.player == null) {
         return var1;
      } else if (ModuleHookDefinitions.class123.active()) {
         Fireworks var3 = ((FireworkRocketEntity)(Object)this).getItem().get(DataComponents.FIREWORKS);
         int var4 = var3 == null ? 0 : var3.flightDuration();
         Rotation var5 = Zenith.getInstance().getRotationManager().getCurrentRotation();
         Vec3 var6 = var5 == null ? var2.player.getLookAngle() : var5.method3135();
         return ModuleHookDefinitions.class123
               .method1645(new ModuleHookDefinitions.Entry4(var1, this.attachedToEntity.getDeltaMovement(), var6, this.life, 10 * (1 + var4)))
            .vec3d;
      } else {
         return var1;
      }
   }

   @ModifyExpressionValue(
      method = {"tick()V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/LivingEntity;getLookAngle()Lnet/minecraft/world/phys/Vec3;"
      )}
   )
   private Vec3 getRotationVector(Vec3 var1) {
      if (this.attachedToEntity != Minecraft.getInstance().player) {
         return var1;
      } else {
         Rotation var2 = Zenith.getInstance().getRotationManager().getCurrentRotation();
         return var2 == null ? var1 : var2.method3135();
      }
   }
}
