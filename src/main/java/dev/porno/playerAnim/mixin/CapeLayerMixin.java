package dev.porno.playerAnim.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.porno.playerAnim.impl.IPlayerAnimationState;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CapeLayer.class)
public abstract class CapeLayerMixin extends RenderLayer<AvatarRenderState, PlayerModel> {
   private CapeLayerMixin(RenderLayerParent<AvatarRenderState, PlayerModel> renderLayerParent, Void v) {
      super(renderLayerParent);
   }

   @WrapWithCondition(
      method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/AvatarRenderState;FF)V",
      at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V")
   )
   private boolean translate(PoseStack instance, float f, float g, float h, @Local(argsOnly = true) AvatarRenderState avatarRenderState) {
      return !((IPlayerAnimationState)avatarRenderState).playerAnimator$getAnimationApplier().isActive();
   }
}
