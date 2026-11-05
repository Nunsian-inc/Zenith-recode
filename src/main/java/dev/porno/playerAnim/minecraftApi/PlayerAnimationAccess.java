package dev.porno.playerAnim.minecraftApi;

import dev.porno.playerAnim.api.IPlayer;
import dev.porno.playerAnim.api.layered.AnimationStack;
import dev.porno.playerAnim.api.layered.IAnimation;
import dev.porno.playerAnim.core.impl.event.Event;
import dev.porno.playerAnim.impl.IAnimatedPlayer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public final class PlayerAnimationAccess {
   public static final Event<PlayerAnimationAccess.AnimationRegister> REGISTER_ANIMATION_EVENT = new Event<>(
      PlayerAnimationAccess.AnimationRegister.class, listeners -> (player, animationStack) -> {
         for (PlayerAnimationAccess.AnimationRegister listener : listeners) {
            listener.registerAnimation(player, animationStack);
         }
      }
   );

   public static AnimationStack getPlayerAnimLayer(AbstractClientPlayer player) throws IllegalArgumentException {
      if (player instanceof IPlayer) {
         return ((IPlayer)player).playerAnimator$getAnimationStack();
      } else {
         throw new IllegalArgumentException(player + " is not a player or library mixins failed");
      }
   }

   public static PlayerAnimationAccess.PlayerAssociatedAnimationData getPlayerAssociatedData(@NotNull AbstractClientPlayer player) {
      if (player instanceof IAnimatedPlayer animatedPlayer) {
         return new PlayerAnimationAccess.PlayerAssociatedAnimationData(animatedPlayer);
      } else {
         throw new IllegalArgumentException(player + " is not a player or library mixins failed");
      }
   }

   @FunctionalInterface
   public interface AnimationRegister {
      void registerAnimation(@NotNull AbstractClientPlayer var1, @NotNull AnimationStack var2);
   }

   public static class PlayerAssociatedAnimationData {
      @NotNull
      private final IAnimatedPlayer player;

      public PlayerAssociatedAnimationData(@NotNull IAnimatedPlayer player) {
         this.player = player;
      }

      @Nullable
      public IAnimation get(@NotNull Identifier id) {
         return this.player.playerAnimator_getAnimation(id);
      }

      @Nullable
      public IAnimation set(@NotNull Identifier id, @Nullable IAnimation animation) {
         return this.player.playerAnimator_setAnimation(id, animation);
      }
   }
}
