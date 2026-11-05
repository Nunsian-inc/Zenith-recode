package dev.porno.playerAnim.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.porno.playerAnim.api.PartKey;
import dev.porno.playerAnim.api.firstPerson.FirstPersonConfiguration;
import dev.porno.playerAnim.api.firstPerson.FirstPersonMode;
import dev.porno.playerAnim.impl.IMutableModel;
import dev.porno.playerAnim.impl.IPlayerAnimationState;
import dev.porno.playerAnim.impl.IPlayerModel;
import dev.porno.playerAnim.impl.animation.AnimationApplier;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PlayerModel.class, priority = 2000)
public class PlayerModelMixin extends HumanoidModel<AvatarRenderState> implements IPlayerModel {
   @Shadow
   @Final
   public ModelPart rightSleeve;
   @Shadow
   @Final
   public ModelPart leftSleeve;
   @Unique
   private boolean firstPersonNext = false;
   @Unique
   private boolean playerAnimator$partsHidden = false;
   @Unique
   private boolean playerAnimator$outerFirstPersonPass = false;

   public PlayerModelMixin(ModelPart modelPart) {
      super(modelPart);
   }

   @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;)V", at = @At("HEAD"))
   private void enterFirstPersonPass(AvatarRenderState avatarRenderState, CallbackInfo ci) {
      this.playerAnimator$outerFirstPersonPass = FirstPersonMode.isFirstPersonPass();
      FirstPersonMode.setFirstPersonPass(((IPlayerAnimationState)avatarRenderState).playerAnimator$isFirstPersonPass());
   }

   @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;)V", at = @At("RETURN"))
   private void setupPlayerAnimation(AvatarRenderState avatarRenderState, CallbackInfo ci) {
      IPlayerAnimationState state = (IPlayerAnimationState)avatarRenderState;
      if (!this.firstPersonNext && state.playerAnimator$getAnimationApplier().isActive()) {
         AnimationApplier emote = state.playerAnimator$getAnimationApplier();
         ((IMutableModel)this).playerAnimator$setAnimation(emote);
         emote.updatePart(PartKey.HEAD, this.head);
         emote.updatePart(PartKey.RIGHT_ARM, this.rightArm);
         emote.updatePart(PartKey.LEFT_ARM, this.leftArm);
         emote.updatePart(PartKey.RIGHT_LEG, this.rightLeg);
         emote.updatePart(PartKey.LEFT_LEG, this.leftLeg);
         emote.updatePart(PartKey.TORSO, this.body);
      } else {
         this.firstPersonNext = false;
         ((IMutableModel)this).playerAnimator$setAnimation(AnimationApplier.EMPTY);
      }

      if (state.playerAnimator$isFirstPersonPass()) {
         FirstPersonConfiguration config = state.playerAnimator$getAnimationApplier().getFirstPersonConfiguration();
         this.playerAnimator$setAllPartsVisible(false);
         boolean skipRightArm = !config.isShowRightArm();
         boolean skipLeftArm = !config.isShowLeftArm();
         this.rightArm.skipDraw = skipRightArm;
         this.rightSleeve.skipDraw = skipRightArm;
         this.leftArm.skipDraw = skipLeftArm;
         this.leftSleeve.skipDraw = skipLeftArm;
         this.playerAnimator$partsHidden = true;
      } else if (this.playerAnimator$partsHidden) {
         this.playerAnimator$setAllPartsVisible(true);
         this.playerAnimator$partsHidden = false;
      }

      FirstPersonMode.setFirstPersonPass(this.playerAnimator$outerFirstPersonPass);
   }

   @WrapWithCondition(
      method = "translateToHand(Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;Lnet/minecraft/world/entity/HumanoidArm;Lcom/mojang/blaze3d/vertex/PoseStack;)V",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/geom/ModelPart;translateAndRotate(Lcom/mojang/blaze3d/vertex/PoseStack;)V")
   )
   private boolean translateToHand(ModelPart modelPart, PoseStack poseStack) {
      if (!((IMutableModel)this).playerAnimator$getAnimation().isActive()) {
         return true;
      }

      poseStack.translate(modelPart.x / 16.0F, modelPart.y / 16.0F, modelPart.z / 16.0F);
      if (modelPart.xRot != 0.0F || modelPart.yRot != 0.0F || modelPart.zRot != 0.0F) {
         poseStack.rotate(new Quaternionf().rotationZYX(modelPart.zRot, modelPart.yRot, modelPart.xRot));
      }

      poseStack.translate(0.0, (modelPart.yScale - 1.0F) * 0.609375, (modelPart.zScale - 1.0F) * 0.0625);
      return false;
   }

   @Unique
   private void playerAnimator$setAllPartsVisible(boolean visible) {
      boolean skip = !visible;
      this.head.getAllParts().forEach(p -> p.skipDraw = skip);
      this.body.getAllParts().forEach(p -> p.skipDraw = skip);
      this.leftLeg.getAllParts().forEach(p -> p.skipDraw = skip);
      this.rightLeg.getAllParts().forEach(p -> p.skipDraw = skip);
      this.rightArm.getAllParts().forEach(p -> p.skipDraw = skip);
      this.leftArm.getAllParts().forEach(p -> p.skipDraw = skip);
   }

   @Override
   public void playerAnimator_prepForFirstPersonRender() {
      this.firstPersonNext = true;
   }
}
