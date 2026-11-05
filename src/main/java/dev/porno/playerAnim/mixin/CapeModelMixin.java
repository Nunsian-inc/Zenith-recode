package dev.porno.playerAnim.mixin;

import dev.porno.playerAnim.api.PartKey;
import dev.porno.playerAnim.api.TransformType;
import dev.porno.playerAnim.core.util.Vec3f;
import dev.porno.playerAnim.impl.IPlayerAnimationState;
import dev.porno.playerAnim.impl.animation.AnimationApplier;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.player.PlayerCapeModel;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerCapeModel.class)
public class CapeModelMixin {
   @Shadow
   @Final
   private ModelPart cape;

   @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;)V", at = @At("RETURN"))
   private void playerAnimator$animateCape(AvatarRenderState avatarRenderState, CallbackInfo ci) {
      AnimationApplier emote = ((IPlayerAnimationState)avatarRenderState).playerAnimator$getAnimationApplier();
      if (emote.isActive()) {
         Vec3f position = emote.get3DTransform(PartKey.CAPE, TransformType.POSITION, Vec3f.ZERO);
         Vec3f rotation = emote.get3DTransform(PartKey.CAPE, TransformType.ROTATION, Vec3f.ZERO);
         Vec3f scale = emote.get3DTransform(PartKey.CAPE, TransformType.SCALE, Vec3f.ONE);
         this.cape.x = -position.getX();
         this.cape.y = position.getY();
         this.cape.z = 2.0F - position.getZ();
         this.cape.setRotation(0.0F, (float) Math.PI, 0.0F);
         this.cape.rotateBy(new Quaternionf().rotationZYX(rotation.getZ(), rotation.getY(), rotation.getX()));
         this.cape.xScale = scale.getX();
         this.cape.yScale = scale.getY();
         this.cape.zScale = scale.getZ();
      }
   }
}
