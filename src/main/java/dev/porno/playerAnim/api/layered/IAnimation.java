package dev.porno.playerAnim.api.layered;

import dev.porno.playerAnim.api.PartKey;
import dev.porno.playerAnim.api.TransformType;
import dev.porno.playerAnim.api.firstPerson.FirstPersonConfiguration;
import dev.porno.playerAnim.api.firstPerson.FirstPersonMode;
import dev.porno.playerAnim.core.util.Vec3f;
import org.jetbrains.annotations.NotNull;

public interface IAnimation {
   default void tick() {
   }

   boolean isActive();

   @Deprecated(forRemoval = true)
   @NotNull
   default Vec3f get3DTransform(@NotNull String modelName, @NotNull TransformType type, float tickDelta, @NotNull Vec3f value0) {
      throw new AssertionError("this, of get3DTransform must be implemented.");
   }

   @NotNull
   default Vec3f get3DTransform(@NotNull PartKey modelKey, @NotNull TransformType type, float tickDelta, @NotNull Vec3f value0) {
      return this.get3DTransform(modelKey.getKey(), type, tickDelta, value0);
   }

   void setupAnim(float var1);

   @NotNull
   default FirstPersonMode getFirstPersonMode(float tickDelta) {
      return FirstPersonMode.NONE;
   }

   @NotNull
   default FirstPersonConfiguration getFirstPersonConfiguration(float tickDelta) {
      return new FirstPersonConfiguration();
   }
}
