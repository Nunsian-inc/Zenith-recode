package dev.porno.playerAnim.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.porno.playerAnim.api.PartKey;
import dev.porno.playerAnim.api.TransformType;
import dev.porno.playerAnim.api.firstPerson.FirstPersonMode;
import dev.porno.playerAnim.core.util.Vec3f;
import dev.porno.playerAnim.impl.IAnimatedPlayer;
import dev.porno.playerAnim.impl.IPlayerAnimationState;
import dev.porno.playerAnim.impl.animation.AnimationApplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.Avatar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AvatarRenderer.class)
public abstract class PlayerRendererMixin extends LivingEntityRenderer<Avatar, AvatarRenderState, PlayerModel> {
   public PlayerRendererMixin(Context context, PlayerModel entityModel, float f, Void v) {
      super(context, entityModel, f);
   }

   @Inject(
      method = "extractRenderState(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;F)V",
      at = @At("HEAD")
   )
   private void modifyRenderState(Avatar avatar, AvatarRenderState avatarRenderState, float f, CallbackInfo ci) {
      IPlayerAnimationState state = (IPlayerAnimationState)avatarRenderState;
      if (avatar instanceof IAnimatedPlayer player) {
         AnimationApplier animation = player.playerAnimator_getAnimation();
         animation.setTickDelta(f);
         state.playerAnimator$setAnimationApplier(AnimationApplier.snapshot(player.playerAnimator$getAnimationStack(), f));
      } else {
         state.playerAnimator$setAnimationApplier(AnimationApplier.EMPTY);
      }

      state.playerAnimator$setLocalPlayer(avatar == Minecraft.getInstance().player);
   }

   @Inject(
      method = "setupRotations(Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;FF)V",
      at = @At("RETURN")
   )
   private void applyBodyTransforms(AvatarRenderState avatarRenderState, PoseStack matrixStack, float f, float g, CallbackInfo ci) {
      AnimationApplier animationPlayer = ((IPlayerAnimationState)avatarRenderState).playerAnimator$getAnimationApplier();
      if (animationPlayer.isActive()) {
         Vec3f vec3e = animationPlayer.get3DTransform(PartKey.BODY, TransformType.SCALE, new Vec3f(1.0F, 1.0F, 1.0F));
         matrixStack.scale(vec3e.getX(), vec3e.getY(), vec3e.getZ());
         Vec3f vec3d = animationPlayer.get3DTransform(PartKey.BODY, TransformType.POSITION, Vec3f.ZERO);
         matrixStack.translate(vec3d.getX().floatValue(), vec3d.getY().floatValue() + 0.7, vec3d.getZ().floatValue());
         Vec3f vec3f = animationPlayer.get3DTransform(PartKey.BODY, TransformType.ROTATION, Vec3f.ZERO);
         matrixStack.rotate(Axis.ZP.rotation(vec3f.getZ()));
         matrixStack.rotate(Axis.YP.rotation(vec3f.getY()));
         matrixStack.rotate(Axis.XP.rotation(vec3f.getX()));
         matrixStack.translate(0.0, -0.7, 0.0);
      }
   }

   @Inject(
      method = "extractRenderState(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;F)V",
      at = @At("TAIL")
   )
   private void extractRenderState(Avatar avatar, AvatarRenderState avatarRenderState, float f, CallbackInfo ci) {
      IPlayerAnimationState state = (IPlayerAnimationState)avatarRenderState;
      boolean camera = avatar == Minecraft.getInstance().getCameraEntity();
      state.playerAnimator$setCameraEntity(camera);
      state.playerAnimator$setFirstPersonPass(camera && FirstPersonMode.isFirstPersonPass());
   }
}
