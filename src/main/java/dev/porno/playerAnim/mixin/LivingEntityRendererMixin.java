package dev.porno.playerAnim.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.porno.playerAnim.api.firstPerson.FirstPersonMode;
import dev.porno.playerAnim.impl.IPlayerAnimationState;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin {
   @Unique
   private boolean playerAnimator$outerFirstPersonPass;

   @Inject(
      method = "submit(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
      at = @At("HEAD")
   )
   private void enterFirstPersonPass(
      LivingEntityRenderState renderState, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera, CallbackInfo ci
   ) {
      this.playerAnimator$outerFirstPersonPass = FirstPersonMode.isFirstPersonPass();
      FirstPersonMode.setFirstPersonPass(renderState instanceof IPlayerAnimationState state && state.playerAnimator$isFirstPersonPass());
   }

   @Inject(
      method = "submit(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
      at = @At("RETURN")
   )
   private void leaveFirstPersonPass(
      LivingEntityRenderState renderState, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera, CallbackInfo ci
   ) {
      FirstPersonMode.setFirstPersonPass(this.playerAnimator$outerFirstPersonPass);
   }
}
