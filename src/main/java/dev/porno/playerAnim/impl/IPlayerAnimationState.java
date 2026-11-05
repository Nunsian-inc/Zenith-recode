package dev.porno.playerAnim.impl;

import dev.porno.playerAnim.impl.animation.AnimationApplier;
import org.jetbrains.annotations.NotNull;

public interface IPlayerAnimationState {
   boolean playerAnimator$isLocalPlayer();

   void playerAnimator$setLocalPlayer(boolean flag);

   boolean playerAnimator$isCameraEntity();

   void playerAnimator$setCameraEntity(boolean camera);

   boolean playerAnimator$isFirstPersonPass();

   void playerAnimator$setFirstPersonPass(boolean flag);

   void playerAnimator$setAnimationApplier(AnimationApplier snapshot);

   @NotNull
   AnimationApplier playerAnimator$getAnimationApplier();
}
