package dev.porno.playerAnim.api.layered;

import dev.porno.playerAnim.api.firstPerson.FirstPersonConfiguration;
import dev.porno.playerAnim.api.firstPerson.FirstPersonMode;
import org.jetbrains.annotations.NotNull;

public interface IActualAnimation<T extends IActualAnimation<T>> extends IAnimation {
   @NotNull
   T setFirstPersonMode(@NotNull FirstPersonMode var1);

   @NotNull
   T setFirstPersonConfiguration(@NotNull FirstPersonConfiguration var1);
}
