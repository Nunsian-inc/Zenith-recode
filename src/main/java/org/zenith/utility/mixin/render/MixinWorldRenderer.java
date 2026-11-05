package org.zenith.utility.mixin.render;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import com.mojang.blaze3d.framegraph.FramePass;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LevelTargetBundle;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.PostChain.TargetBundle;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.ViewArea;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.core.BlockPos;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.zenith.client.modules.impl.render.WeatherModule;
import org.zenith.utility.render.EspGlowPass;
import org.zenith.utility.render.FirePostPass;
import org.zenith.utility.render.SkyPass;
import org.zenith.utility.render.UniformBinder;
import org.zenith.utility.render.WeatherDepthBuffer;
import org.zenith.utility.render.WetnessPass;
import org.zenith.utility.render.WorldGradePass;
import org.zenith.utility.render.WorldProfiler;

// 26.3: LevelRenderer.renderLevel(...) -> render(allocator, outline, CameraRenderState, fog, fogColor, sky, consistentDepth).
// Block-dirty tracking and the cull frustum moved to LevelExtractor / Camera (see MixinCamera for the frustum),
// vanilla weather is drawn inside the main pass (skipped in MixinWeatherRendering).
@Mixin({LevelRenderer.class})
public abstract class MixinWorldRenderer {
   @Unique
   private static final String RENDER = "render(Lcom/mojang/blaze3d/resource/GraphicsResourceAllocator;ZLnet/minecraft/client/renderer/state/level/CameraRenderState;Lcom/mojang/renderpearl/api/buffers/GpuBufferSlice;Lorg/joml/Vector4f;ZZ)V";
   @Shadow
   @Final
   private LevelTargetBundle targets;
   @Shadow
   private ViewArea viewArea;
   @Unique
   private static final Matrix4f zenith$worldMatrix = new Matrix4f();

   @Inject(
      method = {"isSectionCompiledAndVisible(Lnet/minecraft/core/BlockPos;J)Z"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void zenith$guardRenderingReadyWithoutChunks(BlockPos var1, long var2, CallbackInfoReturnable<Boolean> var3) {
      if (Minecraft.getInstance().level == null || this.viewArea == null) {
         var3.setReturnValue(false);
      }
   }

   @Inject(method = {RENDER}, at = {@At("HEAD")}, cancellable = true)
   private void zenith$skipMainRenderWithoutWorld(
      GraphicsResourceAllocator var1, boolean var2, CameraRenderState var3, GpuBufferSlice var4, Vector4f var5, boolean var6, boolean var7, CallbackInfo var8
   ) {
      if (Minecraft.getInstance().level == null || this.viewArea == null) {
         var8.cancel();
      }
   }

   @Redirect(
      method = {RENDER},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/PostChain;addToFrame(Lcom/mojang/blaze3d/framegraph/FrameGraphBuilder;IILnet/minecraft/client/renderer/PostChain$TargetBundle;)V",
         ordinal = 0
      )
   )
   private void zenith$renderShaderEspOutline(PostChain var1, FrameGraphBuilder var2, int var3, int var4, TargetBundle var5) {
      boolean var6 = EspGlowPass.method2636(var2, var3, var4, var5);
      boolean var7 = var6 | FirePostPass.method5079(var2, var3, var4, var5, var6);
      if (!var7) {
         var1.addToFrame(var2, var3, var4, var5);
      }
   }

   @Inject(
      method = {"submitEntities(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/state/level/LevelRenderState;Lnet/minecraft/client/renderer/SubmitNodeCollector;)V"},
      at = {@At("HEAD")}
   )
   private void zenith$captureTerrainDepth(PoseStack var1, LevelRenderState var2, SubmitNodeCollector var3, CallbackInfo var4) {
      if (WetnessPass.method3122(Minecraft.getInstance().gameRenderer.mainCamera())) {
         int var5 = WorldProfiler.method785(0);
         WeatherDepthBuffer.method5130();
         WorldProfiler.method786(var5);
      }
   }

   @Inject(
      method = {"addSkyPass(Lcom/mojang/blaze3d/framegraph/FrameGraphBuilder;Lnet/minecraft/client/renderer/state/level/CameraRenderState;Lcom/mojang/renderpearl/api/buffers/GpuBufferSlice;)V"},
      at = {@At(value = "INVOKE", target = "Lcom/mojang/blaze3d/framegraph/FramePass;executes(Ljava/lang/Runnable;)V", shift = Shift.AFTER)}
   )
   private void zenith$renderSkyEffects(FrameGraphBuilder var1, CameraRenderState var2, GpuBufferSlice var3, CallbackInfo var4) {
      Minecraft var9 = Minecraft.getInstance();
      int var5 = var9.gameRenderer.mainRenderTarget().width;
      int var6 = var9.gameRenderer.mainRenderTarget().height;
      long var7 = WorldProfiler.method782();
      WorldProfiler.method787(0);
      SkyPass.method7267(var1, var5, var6, this.targets, var9.gameRenderer.mainCamera());
      WorldProfiler.method788();
      WorldProfiler.method783(25, var7);
   }

   @Inject(
      method = {RENDER},
      at = {@At(
         value = "INVOKE",
         target = "Lcom/mojang/blaze3d/framegraph/FrameGraphBuilder;execute(Lcom/mojang/blaze3d/resource/GraphicsResourceAllocator;Lcom/mojang/blaze3d/framegraph/FrameGraphBuilder$Inspector;)V"
      )}
   )
   private void zenith$appendWeatherGloss(
      GraphicsResourceAllocator var1,
      boolean var2,
      CameraRenderState var3,
      GpuBufferSlice var8,
      Vector4f var9,
      boolean var10,
      boolean var11,
      CallbackInfo var19,
      @Local FrameGraphBuilder var12
   ) {
      Minecraft var20 = Minecraft.getInstance();
      Camera var4 = var20.gameRenderer.mainCamera();
      int var13 = var20.gameRenderer.mainRenderTarget().width;
      int var14 = var20.gameRenderer.mainRenderTarget().height;
      WeatherDepthBuffer.method5131();
      long var15 = WorldProfiler.method782();
      WorldProfiler.method787(2);
      boolean var17 = WetnessPass.method3123(var12, var13, var14, this.targets, var4);
      WorldProfiler.method788();
      WorldProfiler.method783(27, var15);
      if (!var17) {
         var15 = WorldProfiler.method782();
         WorldProfiler.method787(1);
         WorldGradePass.method6105(var12, var13, var14, this.targets, var4);
         WorldProfiler.method788();
         WorldProfiler.method783(26, var15);
      }

      if (WeatherModule.INSTANCE.method3116()) {
         FramePass var18 = var12.addPass("zenith_weather");
         this.targets.main = var18.readsAndWrites(this.targets.main);
         var18.executes(() -> {
            int var0 = WorldProfiler.method785(13);
            WeatherModule.INSTANCE.method3117(zenith$worldMatrix);
            WorldProfiler.method786(var0);
         });
      }
   }

   @Inject(method = {RENDER}, at = {@At("HEAD")})
   private void zenith$captureWorldMatrix(
      GraphicsResourceAllocator var1, boolean var2, CameraRenderState var3, GpuBufferSlice var4, Vector4f var5, boolean var6, boolean var7, CallbackInfo var8
   ) {
      if (WorldProfiler.enabled) {
         WorldProfiler.method790();
      }

      // 1.21.11 passed the view-rotation matrix as the first renderLevel matrix
      zenith$worldMatrix.set(var3.viewRotationMatrix);
      UniformBinder.method5481();
   }
}
