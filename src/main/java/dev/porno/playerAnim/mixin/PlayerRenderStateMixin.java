package dev.porno.playerAnim.mixin;

import dev.porno.playerAnim.impl.IPlayerAnimationState;
import dev.porno.playerAnim.impl.animation.AnimationApplier;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(AvatarRenderState.class)
public class PlayerRenderStateMixin implements IPlayerAnimationState {
   @Unique
   boolean playerAnimator$isLocalPlayer = false;
   @Unique
   boolean playerAnimator$isCameraEntity = false;
   @Unique
   boolean playerAnimator$isFirstPersonPass = false;
   @Unique
   AnimationApplier playerAnimator$animationApplier = AnimationApplier.EMPTY;

   @Override
   public boolean playerAnimator$isLocalPlayer() {
      return this.playerAnimator$isLocalPlayer;
   }

   @Override
   public void playerAnimator$setLocalPlayer(boolean value) {
      this.playerAnimator$isLocalPlayer = value;
   }

   @Override
   public boolean playerAnimator$isCameraEntity() {
      return this.playerAnimator$isCameraEntity;
   }

   @Override
   public void playerAnimator$setCameraEntity(boolean value) {
      this.playerAnimator$isCameraEntity = value;
   }

   @Override
   public boolean playerAnimator$isFirstPersonPass() {
      return this.playerAnimator$isFirstPersonPass;
   }

   @Override
   public void playerAnimator$setFirstPersonPass(boolean value) {
      this.playerAnimator$isFirstPersonPass = value;
   }

   @Override
   public void playerAnimator$setAnimationApplier(AnimationApplier value) {
      this.playerAnimator$animationApplier = value;
   }

   @NotNull
   @Override
   public AnimationApplier playerAnimator$getAnimationApplier() {
      return this.playerAnimator$animationApplier;
   }
}
