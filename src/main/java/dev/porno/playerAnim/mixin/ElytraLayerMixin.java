package dev.porno.playerAnim.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.porno.playerAnim.api.PartKey;
import dev.porno.playerAnim.api.TransformType;
import dev.porno.playerAnim.core.util.Vec3f;
import dev.porno.playerAnim.impl.IPlayerAnimationState;
import dev.porno.playerAnim.impl.animation.AnimationApplier;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.layers.WingsLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WingsLayer.class)
public abstract class ElytraLayerMixin<S extends HumanoidRenderState, M extends EntityModel<S>> extends RenderLayer<S, M> {
   private ElytraLayerMixin(RenderLayerParent<S, M> renderLayerParent, Void v) {
      super(renderLayerParent);
   }

   @Inject(
      method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/HumanoidRenderState;FF)V",
      at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V")
   )
   private void inject(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int i, S humanoidRenderState, float f, float g, CallbackInfo ci) {
      if (humanoidRenderState instanceof IPlayerAnimationState animationState) {
         AnimationApplier emote = animationState.playerAnimator$getAnimationApplier();
         if (emote.isActive()) {
            Vec3f translation = emote.get3DTransform(PartKey.TORSO, TransformType.POSITION, Vec3f.ZERO);
            Vec3f rotation = emote.get3DTransform(PartKey.TORSO, TransformType.ROTATION, Vec3f.ZERO);
            translation = emote.get3DTransform(PartKey.CAPE, TransformType.POSITION, translation);
            rotation = emote.get3DTransform(PartKey.CAPE, TransformType.ROTATION, rotation);
            translation = emote.get3DTransform(PartKey.ELYTRA, TransformType.POSITION, translation);
            rotation = emote.get3DTransform(PartKey.ELYTRA, TransformType.ROTATION, rotation);
            poseStack.translate(translation.getX() / 16.0F, translation.getY() / 16.0F, translation.getZ() / 16.0F);
            poseStack.rotate(new Quaternionf().rotateXYZ(rotation.getX(), rotation.getY(), rotation.getZ()));
            Vec3f scale = emote.get3DTransform(PartKey.ELYTRA, TransformType.SCALE, Vec3f.ONE);
            poseStack.scale(scale.getX(), scale.getY(), scale.getZ());
         }
      }
   }
}
