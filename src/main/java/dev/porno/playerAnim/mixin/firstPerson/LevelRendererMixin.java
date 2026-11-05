package dev.porno.playerAnim.mixin.firstPerson;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.porno.playerAnim.api.firstPerson.FirstPersonMode;
import dev.porno.playerAnim.impl.IAnimatedPlayer;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// 26.3 moved entity extraction to LevelExtractor
@Mixin(net.minecraft.client.renderer.extract.LevelExtractor.class)
public class LevelRendererMixin {
   @ModifyExpressionValue(
      method = "extractVisibleEntities(Lnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/culling/Frustum;Lnet/minecraft/client/DeltaTracker;Lnet/minecraft/client/renderer/state/level/LevelRenderState;)V",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;isDetached()Z")
   )
   private boolean fakeThirdPersonMode(boolean original, Camera camera) {
      if (camera.entity() instanceof IAnimatedPlayer player && player.playerAnimator_getAnimation().getFirstPersonMode() == FirstPersonMode.THIRD_PERSON_MODEL) {
         FirstPersonMode.setFirstPersonPass(!original && !(camera.entity() instanceof LivingEntity living && living.isSleeping()));
         return true;
      } else {
         return original;
      }
   }

   @Inject(
      method = "extractVisibleEntities(Lnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/culling/Frustum;Lnet/minecraft/client/DeltaTracker;Lnet/minecraft/client/renderer/state/level/LevelRenderState;)V",
      at = @At("RETURN")
   )
   private void endFirstPersonExtract(Camera camera, Frustum frustum, DeltaTracker deltaTracker, LevelRenderState levelRenderState, CallbackInfo ci) {
      FirstPersonMode.setFirstPersonPass(false);
   }
}
