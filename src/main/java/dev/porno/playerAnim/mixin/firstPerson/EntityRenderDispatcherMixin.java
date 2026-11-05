package dev.porno.playerAnim.mixin.firstPerson;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.porno.playerAnim.impl.IPlayerAnimationState;
import java.util.List;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {
   @WrapWithCondition(
      method = "submit(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;Lnet/minecraft/client/renderer/state/level/CameraRenderState;DDDLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;)V",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/SubmitNodeCollector;submitShadow(Lcom/mojang/blaze3d/vertex/PoseStack;FLjava/util/List;)V"
      )
   )
   private boolean skipFirstPersonShadow(
      SubmitNodeCollector collector, PoseStack poseStack, float radius, List<?> pieces, @Local(argsOnly = true) EntityRenderState entityRenderState
   ) {
      return !(entityRenderState instanceof IPlayerAnimationState state && state.playerAnimator$isFirstPersonPass());
   }
}
