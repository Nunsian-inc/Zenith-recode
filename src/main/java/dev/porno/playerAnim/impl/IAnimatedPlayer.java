package dev.porno.playerAnim.impl;

import dev.porno.playerAnim.api.IPlayer;
import dev.porno.playerAnim.api.layered.IAnimation;
import dev.porno.playerAnim.impl.animation.AnimationApplier;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public interface IAnimatedPlayer extends IPlayer {
   AnimationApplier playerAnimator_getAnimation();

   @Nullable
   IAnimation playerAnimator_getAnimation(@NotNull Identifier var1);

   @Nullable
   IAnimation playerAnimator_setAnimation(@NotNull Identifier var1, @Nullable IAnimation var2);
}
