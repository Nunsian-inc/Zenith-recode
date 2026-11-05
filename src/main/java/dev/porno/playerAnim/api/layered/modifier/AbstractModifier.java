package dev.porno.playerAnim.api.layered.modifier;

import dev.porno.playerAnim.api.layered.AnimationContainer;
import dev.porno.playerAnim.api.layered.IAnimation;
import dev.porno.playerAnim.api.layered.ModifierLayer;
import lombok.Generated;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractModifier extends AnimationContainer<IAnimation> {
   @Nullable
   protected ModifierLayer host;

   public AbstractModifier() {
      super(null);
   }

   public boolean canRemove() {
      return false;
   }

   @Generated
   public void setHost(@Nullable ModifierLayer host) {
      this.host = host;
   }
}
