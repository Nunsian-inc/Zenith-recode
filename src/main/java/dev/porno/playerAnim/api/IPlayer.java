package dev.porno.playerAnim.api;

import dev.porno.playerAnim.api.layered.AnimationStack;
import org.jetbrains.annotations.NotNull;

public interface IPlayer {
   @Deprecated(forRemoval = true)
   default AnimationStack getAnimationStack() {
      return this.playerAnimator$getAnimationStack();
   }

   @NotNull
   AnimationStack playerAnimator$getAnimationStack();
}
