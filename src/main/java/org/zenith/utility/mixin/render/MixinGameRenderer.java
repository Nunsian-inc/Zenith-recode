package org.zenith.utility.mixin.render;

import com.darkmagician6.eventapi.EventManager;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.state.level.PlayerRenderState;
import com.mojang.renderpearl.api.textures.GpuTextureView;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.zenith.MinecraftAccess;
import org.zenith.RenderState;
import org.zenith.client.events.CrosshairTargetEvent;
import org.zenith.client.events.FovEvent;
import org.zenith.client.events.ProjectionMatrixEvent;
import org.zenith.client.events.ScreenRenderEvent;
import org.zenith.client.events.WorldRenderEvent;
import org.zenith.client.modules.impl.render.ShaderHandModule;
import org.zenith.client.screens.media.NowPlayingScreen;
import org.zenith.utility.misc.FrameTimer;
import org.zenith.utility.misc.Util15;
import org.zenith.utility.render.GlowUtil;
import org.zenith.utility.render.HandShaders;
import org.zenith.utility.render.ProjectionUtils;
import org.zenith.utility.render.ScissorFlusher;
import org.zenith.utility.render.display.base.MenuDrawContext;

@Mixin({GameRenderer.class})
public abstract class MixinGameRenderer {
   @Shadow
   @Final
   private Camera mainCamera;

   @Shadow
   private void renderItemInHand(CameraRenderState var1, PlayerRenderState var2, GpuTextureView var3) {
   }

   // fov / projection / crosshair hooks moved to MixinCamera and MixinMinecraftClient (26.3 moved that code there)

   @Inject(
      method = {"renderLevel()V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void zenith$skipCoveredWorld(CallbackInfo var2) {
      if (NowPlayingScreen.coversWorld()) {
         var2.cancel();
      }
   }

   @Inject(
      method = {"renderLevel()V"},
      at = {@At("HEAD")}
   )
   private void beginWorldGrade(CallbackInfo var2) {
      FrameTimer.beginFrame();
   }

   @Inject(
      method = {"renderLevel()V"},
      at = {@At("RETURN")}
   )
   private void endWorldGrade(CallbackInfo var2) {
      FrameTimer.endFrame();
   }

   @Redirect(
      method = {"render3dHud"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/GameRenderer;renderItemInHand(Lnet/minecraft/client/renderer/state/level/CameraRenderState;Lnet/minecraft/client/renderer/state/level/PlayerRenderState;Lcom/mojang/renderpearl/api/textures/GpuTextureView;)V"
      )
   )
   public void afterRenderHand(GameRenderer var1, CameraRenderState var6, PlayerRenderState var3, GpuTextureView var4) {
      float var2 = var6.cameraEntityPartialTicks;
      ShaderHandModule var5 = ShaderHandModule.INSTANCE;
      if (var5 != null && var5.isEnabled()) {
         if (!HandShaders.isInitialized()) {
            HandShaders.method6278();
         }

         HandShaders.method6281();
         // the module swaps mainRenderTarget to its own framebuffer; draw the hand against that depth too
         var5.method1239(() -> this.renderItemInHand(var6, var3, MinecraftAccess.mc.gameRenderer.mainRenderTarget.getDepthTextureView()), var2);
      } else {
         this.renderItemInHand(var6, var3, var4);
      }
   }

   @Inject(
      method = {"renderLevel()V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/LevelRenderer;render(Lcom/mojang/blaze3d/resource/GraphicsResourceAllocator;ZLnet/minecraft/client/renderer/state/level/CameraRenderState;Lcom/mojang/renderpearl/api/buffers/GpuBufferSlice;Lorg/joml/Vector4f;ZZ)V",
         shift = Shift.AFTER
      )}
   )
   public void hookWorldRender(CallbackInfo var2, @Local(ordinal = 0) Matrix4f var3) {
      // 26.3 keeps the view rotation in the camera render state instead of a renderLevel local
      net.minecraft.client.renderer.state.level.LevelRenderState var7 = MinecraftAccess.mc.gameRenderer.gameRenderState().levelRenderState;
      Matrix4f var4 = new Matrix4f(var7.cameraRenderState.viewRotationMatrix);
      PoseStack var5 = new PoseStack();
      var5.mulPose(var4);
      GlowUtil.method2250(new Matrix4f(var3));
      GlowUtil.method2251(RenderSystem.getModelViewMatrixCopy());
      GlowUtil.method2252(var4);
      ProjectionUtils.method299();
      WorldRenderEvent var6 = new WorldRenderEvent(var5, var7.worldPartialTicks);
      EventManager.call(var6);
      GlowUtil.method2226(var6.getMatrixStack());
   }

   // ScreenRenderEvent moved to MixinGuiExtraction (26.3 extracts the GUI in Gui.extractRenderState)
}
