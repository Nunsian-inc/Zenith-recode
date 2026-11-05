package dev.porno.playerAnim.core.impl;

import dev.porno.playerAnim.api.PartKey;
import dev.porno.playerAnim.api.TransformType;
import dev.porno.playerAnim.api.firstPerson.FirstPersonConfiguration;
import dev.porno.playerAnim.api.firstPerson.FirstPersonMode;
import dev.porno.playerAnim.api.layered.AnimationStack;
import dev.porno.playerAnim.api.layered.IAnimation;
import dev.porno.playerAnim.core.util.Pair;
import dev.porno.playerAnim.core.util.Vec3f;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.ApiStatus.Internal;

public class AnimationProcessor {
   @Nullable
   private final IAnimation animation;
   private float tickDelta = 0.0F;

   public AnimationProcessor(@Nullable IAnimation animation) {
      this.animation = animation;
   }

   @Internal
   public void tick() {
      if (this.animation != null) {
         this.animation.tick();
      }
   }

   public boolean isActive() {
      return this.animation != null && this.animation.isActive();
   }

   @Deprecated(forRemoval = true)
   public Vec3f get3DTransform(String modelKey, TransformType type, Vec3f value0) {
      return this.get3DTransform(PartKey.keyForId(modelKey), type, value0);
   }

   public Vec3f get3DTransform(PartKey partKey, TransformType type, Vec3f value0) {
      return this.animation == null ? value0 : this.animation.get3DTransform(partKey, type, this.tickDelta, value0);
   }

   @Internal
   public void setTickDelta(float tickDelta) {
      this.tickDelta = tickDelta;
      if (this.animation != null) {
         this.animation.setupAnim(tickDelta);
      }
   }

   @Experimental
   @Nullable
   public IAnimation getAnimation() {
      return this.animation;
   }

   public boolean isFirstPersonAnimationDisabled() {
      return this.animation == null || !this.animation.getFirstPersonMode(this.tickDelta).isEnabled();
   }

   @NotNull
   public FirstPersonMode getFirstPersonMode() {
      return this.animation == null ? FirstPersonMode.NONE : this.animation.getFirstPersonMode(this.tickDelta);
   }

   @NotNull
   public FirstPersonConfiguration getFirstPersonConfiguration() {
      return this.animation == null ? new FirstPersonConfiguration() : this.animation.getFirstPersonConfiguration(this.tickDelta);
   }

   @NotNull
   public Pair<Float, Float> getBend(PartKey partKey) {
      if (this.animation == null) {
         return new Pair<>(0.0F, 0.0F);
      }

      Vec3f bendVec = this.get3DTransform(partKey, TransformType.BEND, Vec3f.ZERO);
      return new Pair<>(bendVec.getX(), bendVec.getY());
   }

   public int getPriority() {
      return this.animation instanceof AnimationStack animationStack ? animationStack.getPriority() : 0;
   }
}
