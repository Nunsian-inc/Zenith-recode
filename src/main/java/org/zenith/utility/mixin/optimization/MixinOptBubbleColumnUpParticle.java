package org.zenith.utility.mixin.optimization;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.BubbleColumnUpParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.zenith.utility.misc.OptimizationFlags;
import org.zenith.utility.render.FluidColorCache;

@Mixin({BubbleColumnUpParticle.class})
public abstract class MixinOptBubbleColumnUpParticle {
   @WrapOperation(
      method = {"tick()V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/multiplayer/ClientLevel;getFluidState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/material/FluidState;"
      )}
   )
   private FluidState zenith$cachedFluid(ClientLevel referent, BlockPos pos, Operation<FluidState> operation) {
      if (!OptimizationFlags.flag4) {
         return (FluidState)operation.call(new Object[]{referent, pos});
      } else {
         FluidState fluidState = FluidColorCache.method7103(referent, pos);
         if (fluidState == null) {
            fluidState = (FluidState)operation.call(new Object[]{referent, pos});
            FluidColorCache.method7104(pos, fluidState);
         }

         return fluidState;
      }
   }
}
