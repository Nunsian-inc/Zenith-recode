package dev.porno.playerAnim.api;

import dev.porno.playerAnim.api.layered.AnimationStack;

public abstract class AnimUtils {
   public static AnimationStack getPlayerAnimLayer(Object player) throws IllegalArgumentException {
      if (player instanceof IPlayer) {
         return ((IPlayer)player).playerAnimator$getAnimationStack();
      } else {
         throw new IllegalArgumentException(player + " is not a player or library mixins failed");
      }
   }
}
