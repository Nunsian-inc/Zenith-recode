package org.zenith.utility.mixin.render;

import com.mojang.renderpearl.api.commands.RenderPass;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import net.minecraft.client.renderer.rendertype.PreparedRenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.utility.render.UniformBinder;

// old RenderType.draw hook: bind Zenith's per-pipeline uniforms right after the vanilla defaults.
// OIT stages use other pipelines without Zenith's bind group, so only the regular pipeline gets them.
@Mixin({PreparedRenderType.class})
public abstract class MixinPreparedRenderTypeUniforms {
   @Inject(
      method = {"draw"},
      at = {@At(
         value = "INVOKE",
         target = "Lcom/mojang/blaze3d/systems/RenderSystem;bindDefaultUniforms(Lcom/mojang/renderpearl/api/commands/RenderPass;)V",
         shift = Shift.AFTER
      )}
   )
   private void zenith$bindLayerUniforms(
      net.minecraft.client.renderer.StagedVertexBuffer.ExecuteInfo var1, RenderPass var2, RenderPipeline var3, CallbackInfo var4
   ) {
      if (var3 == ((PreparedRenderType)(Object)this).pipeline()) {
         UniformBinder.method5482(var2, var3);
      }
   }
}
