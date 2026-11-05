package org.zenith.utility.mixin.optimization;

import com.mojang.renderpearl.api.commands.GpuFence;
import com.mojang.renderpearl.backend.opengl.GlCommandEncoder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.zenith.utility.misc.OptimizationFlags;
import org.zenith.utility.render.CompletionFence;

@Mixin({GlCommandEncoder.class})
public class MixinOptGlCommandEncoder {
   @Inject(
      method = {"createFence()Lcom/mojang/renderpearl/api/commands/GpuFence;"},
      at = {@At(
         value = "NEW",
         target = "Lcom/mojang/renderpearl/backend/opengl/GlFence;"
      )},
      cancellable = true
   )
   private void zenith$sharedFrameFence(CallbackInfoReturnable<GpuFence> callbackInfoReturnable) {
      if (OptimizationFlags.flag20) {
         callbackInfoReturnable.setReturnValue(CompletionFence.method5163());
      }
   }
}
