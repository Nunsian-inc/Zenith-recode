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
   private void zenith$guardRenderingReadyWithoutChunks(BlockPos pos, long l, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
      if (Minecraft.getInstance().level == null || this.viewArea == null) {
         callbackInfoReturnable.setReturnValue(false);
      }
   }

   @Inject(method = {RENDER}, at = {@At("HEAD")}, cancellable = true)
   private void zenith$skipMainRenderWithoutWorld(
      GraphicsResourceAllocator resourceAllocator, boolean flag, CameraRenderState cameraRenderState, GpuBufferSlice gpuBufferSlice, Vector4f vec, boolean flag2, boolean flag3, CallbackInfo callbackInfo
   ) {
      if (Minecraft.getInstance().level == null || this.viewArea == null) {
         callbackInfo.cancel();
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
   private void zenith$renderShaderEspOutline(PostChain postChain, FrameGraphBuilder frame, int screenWidth, int screenHeight, TargetBundle providedTargets) {
      boolean flag = EspGlowPass.method2636(frame, screenWidth, screenHeight, providedTargets);
      boolean flag2 = flag | FirePostPass.method5079(frame, screenWidth, screenHeight, providedTargets, flag);
      if (!flag2) {
         postChain.addToFrame(frame, screenWidth, screenHeight, providedTargets);
      }
   }

   @Inject(
      method = {"submitEntities(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/state/level/LevelRenderState;Lnet/minecraft/client/renderer/SubmitNodeCollector;)V"},
      at = {@At("HEAD")}
   )
   private void zenith$captureTerrainDepth(PoseStack poseStack, LevelRenderState levelRenderState, SubmitNodeCollector submitNodeCollector, CallbackInfo callbackInfo) {
      if (WetnessPass.method3122(Minecraft.getInstance().gameRenderer.mainCamera())) {
         int n = WorldProfiler.method785(0);
         WeatherDepthBuffer.method5130();
         WorldProfiler.method786(n);
      }
   }

   @Inject(
      method = {"addSkyPass(Lcom/mojang/blaze3d/framegraph/FrameGraphBuilder;Lnet/minecraft/client/renderer/state/level/CameraRenderState;Lcom/mojang/renderpearl/api/buffers/GpuBufferSlice;)V"},
      at = {@At(value = "INVOKE", target = "Lcom/mojang/blaze3d/framegraph/FramePass;executes(Ljava/lang/Runnable;)V", shift = Shift.AFTER)}
   )
   private void zenith$renderSkyEffects(FrameGraphBuilder frame, CameraRenderState cameraRenderState, GpuBufferSlice gpuBufferSlice, CallbackInfo callbackInfo) {
      Minecraft minecraft = Minecraft.getInstance();
      int width = minecraft.gameRenderer.mainRenderTarget().width;
      int height = minecraft.gameRenderer.mainRenderTarget().height;
      long l = WorldProfiler.method782();
      WorldProfiler.method787(0);
      SkyPass.method7267(frame, width, height, this.targets, minecraft.gameRenderer.mainCamera());
      WorldProfiler.method788();
      WorldProfiler.method783(25, l);
   }

   @Inject(
      method = {RENDER},
      at = {@At(
         value = "INVOKE",
         target = "Lcom/mojang/blaze3d/framegraph/FrameGraphBuilder;execute(Lcom/mojang/blaze3d/resource/GraphicsResourceAllocator;Lcom/mojang/blaze3d/framegraph/FrameGraphBuilder$Inspector;)V"
      )}
   )
   private void zenith$appendWeatherGloss(
      GraphicsResourceAllocator resourceAllocator,
      boolean flag,
      CameraRenderState cameraRenderState,
      GpuBufferSlice gpuBufferSlice,
      Vector4f vec,
      boolean flag2,
      boolean flag3,
      CallbackInfo callbackInfo,
      @Local FrameGraphBuilder frame
   ) {
      Minecraft minecraft = Minecraft.getInstance();
      Camera mainCamera = minecraft.gameRenderer.mainCamera();
      int width = minecraft.gameRenderer.mainRenderTarget().width;
      int height = minecraft.gameRenderer.mainRenderTarget().height;
      WeatherDepthBuffer.method5131();
      long l = WorldProfiler.method782();
      WorldProfiler.method787(2);
      boolean flag4 = WetnessPass.method3123(frame, width, height, this.targets, mainCamera);
      WorldProfiler.method788();
      WorldProfiler.method783(27, l);
      if (!flag4) {
         l = WorldProfiler.method782();
         WorldProfiler.method787(1);
         WorldGradePass.method6105(frame, width, height, this.targets, mainCamera);
         WorldProfiler.method788();
         WorldProfiler.method783(26, l);
      }

      if (WeatherModule.INSTANCE.method3116()) {
         FramePass addPass = frame.addPass("zenith_weather");
         this.targets.main = addPass.readsAndWrites(this.targets.main);
         addPass.executes(() -> {
            int n = WorldProfiler.method785(13);
            WeatherModule.INSTANCE.method3117(zenith$worldMatrix);
            WorldProfiler.method786(n);
         });
      }
   }

   @Inject(method = {RENDER}, at = {@At("HEAD")})
   private void zenith$captureWorldMatrix(
      GraphicsResourceAllocator resourceAllocator, boolean flag, CameraRenderState cameraRenderState, GpuBufferSlice gpuBufferSlice, Vector4f vec, boolean flag2, boolean flag3, CallbackInfo callbackInfo
   ) {
      if (WorldProfiler.enabled) {
         WorldProfiler.method790();
      }

      // 1.21.11 passed the view-rotation matrix as the first renderLevel matrix
      zenith$worldMatrix.set(cameraRenderState.viewRotationMatrix);
      UniformBinder.method5481();
   }
}
