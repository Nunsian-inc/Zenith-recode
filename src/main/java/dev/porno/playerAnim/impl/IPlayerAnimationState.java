package dev.porno.playerAnim.impl;

import dev.porno.playerAnim.impl.animation.AnimationApplier;
import org.jetbrains.annotations.NotNull;

public interface IPlayerAnimationState {
   boolean playerAnimator$isLocalPlayer();

   void playerAnimator$setLocalPlayer(boolean var1);

   boolean playerAnimator$isCameraEntity();

   void playerAnimator$setCameraEntity(boolean var1);

   boolean playerAnimator$isFirstPersonPass();

   void playerAnimator$setFirstPersonPass(boolean var1);

   void playerAnimator$setAnimationApplier(AnimationApplier var1);

   @NotNull
   AnimationApplier playerAnimator$getAnimationApplier();
}
