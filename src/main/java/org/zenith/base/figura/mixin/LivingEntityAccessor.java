package org.zenith.base.figura.mixin;

import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LivingEntity.class)
public interface LivingEntityAccessor {
   @Accessor("jumping")
   boolean figura$isJumping();

   @Invoker("updateWalkAnimation")
   void figura$updateWalkAnimation(float distance);
}
