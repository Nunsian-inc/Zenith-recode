package org.zenith.utility.mixin.render;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.renderpearl.api.commands.RenderPass;
import com.mojang.blaze3d.vertex.MeshData;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import org.joml.Vector4fc;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.utility.render.GuiVertexBatch;
import org.zenith.utility.render.UniformBinder;

// 26.3: RenderType only writes the dynamic transforms (2-arg, white colour); drawing moved to PreparedRenderType
// (see MixinPreparedRenderTypeUniforms for the Zenith uniform binding).
@Mixin({RenderType.class})
public abstract class MixinRenderLayerUniforms {
   @org.spongepowered.asm.mixin.injection.Redirect(
      method = {"writeDynamicTransforms(Lorg/joml/Matrix4f;)Lcom/mojang/renderpearl/api/buffers/GpuBufferSlice;"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/DynamicGpuData;writeTransform(Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;)Lcom/mojang/renderpearl/api/buffers/GpuBufferSlice;"
      )
   )
   private com.mojang.renderpearl.api.buffers.GpuBufferSlice zenith$shaderColor(net.minecraft.client.renderer.DynamicGpuData dynamicGpuData, org.joml.Matrix4f modelView, org.joml.Matrix4f textureMatrix) {
      Vector4fc vector4fc = GuiVertexBatch.method105(new org.joml.Vector4f(1.0F, 1.0F, 1.0F, 1.0F));
      return dynamicGpuData.writeTransform(modelView, new org.joml.Vector4f(vector4fc), new org.joml.Vector3f(), textureMatrix);
   }
}
