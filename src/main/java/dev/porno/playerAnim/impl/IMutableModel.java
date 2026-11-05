package dev.porno.playerAnim.impl;

import dev.porno.playerAnim.core.impl.AnimationProcessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public interface IMutableModel {
   void playerAnimator$setAnimation(@NotNull AnimationProcessor emote);

   @NotNull
   AnimationProcessor playerAnimator$getAnimation();
}
