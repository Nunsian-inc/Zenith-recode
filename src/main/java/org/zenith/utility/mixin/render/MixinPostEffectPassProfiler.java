package org.zenith.utility.mixin.render;

import net.minecraft.client.renderer.PostPass;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.zenith.utility.render.WorldProfiler;

@Mixin({PostPass.class})
public abstract class MixinPostEffectPassProfiler {
   @ModifyArg(
      method = {"addToFrame(Lcom/mojang/blaze3d/framegraph/FrameGraphBuilder;Ljava/util/Map;Lcom/mojang/renderpearl/api/buffers/GpuBufferSlice;)V"},
      at = @At(
         value = "INVOKE",
         target = "Lcom/mojang/blaze3d/framegraph/FramePass;executes(Ljava/lang/Runnable;)V"
      )
   )
   private Runnable zenith$profileWeatherPass(Runnable runnable) {
      return WorldProfiler.method789(runnable);
   }
}
