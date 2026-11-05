package dev.porno.playerAnim.api.layered;

import dev.porno.playerAnim.api.PartKey;
import dev.porno.playerAnim.api.TransformType;
import dev.porno.playerAnim.api.firstPerson.FirstPersonConfiguration;
import dev.porno.playerAnim.api.firstPerson.FirstPersonMode;
import dev.porno.playerAnim.core.util.Pair;
import dev.porno.playerAnim.core.util.Vec3f;
import java.util.ArrayList;
import org.jetbrains.annotations.NotNull;

public class AnimationStack implements IAnimation {
   private final ArrayList<Pair<Integer, IAnimation>> layers = new ArrayList<>();

   @Override
   public boolean isActive() {
      for (Pair<Integer, IAnimation> layer : this.layers) {
         if (layer.getRight().isActive()) {
            return true;
         }
      }

      return false;
   }

   @Override
   public void tick() {
      for (Pair<Integer, IAnimation> layer : this.layers) {
         if (layer.getRight().isActive()) {
            layer.getRight().tick();
         }
      }
   }

   @NotNull
   @Override
   public Vec3f get3DTransform(@NotNull String modelName, @NotNull TransformType type, float tickDelta, @NotNull Vec3f value0) {
      for (Pair<Integer, IAnimation> layer : this.layers) {
         if (layer.getRight().isActive() && (!FirstPersonMode.isFirstPersonPass() || layer.getRight().getFirstPersonMode(tickDelta).isEnabled())) {
            value0 = layer.getRight().get3DTransform(modelName, type, tickDelta, value0);
         }
      }

      return value0;
   }

   @NotNull
   @Override
   public Vec3f get3DTransform(@NotNull PartKey partKey, @NotNull TransformType type, float tickDelta, @NotNull Vec3f value0) {
      for (Pair<Integer, IAnimation> layer : this.layers) {
         if (layer.getRight().isActive() && (!FirstPersonMode.isFirstPersonPass() || layer.getRight().getFirstPersonMode(tickDelta).isEnabled())) {
            value0 = layer.getRight().get3DTransform(partKey, type, tickDelta, value0);
         }
      }

      return value0;
   }

   @Override
   public void setupAnim(float tickDelta) {
      for (Pair<Integer, IAnimation> layer : this.layers) {
         layer.getRight().setupAnim(tickDelta);
      }
   }

   public AnimationStack snapshot() {
      AnimationStack copy = new AnimationStack();

      for (Pair<Integer, IAnimation> layer : this.layers) {
         IAnimation animation = layer.getRight();
         if (animation.getClass() == AnimationContainer.class) {
            animation = ((AnimationContainer)animation).getAnim();
         }

         if (animation != null && animation.isActive()) {
            copy.layers.add(new Pair<>(layer.getLeft(), animation));
         }
      }

      return copy;
   }

   public void addAnimLayer(int priority, IAnimation layer) {
      int search = 0;

      while (this.layers.size() > search && this.layers.get(search).getLeft() < priority) {
         search++;
      }

      this.layers.add(search, new Pair<>(priority, layer));
   }

   public boolean removeLayer(IAnimation layer) {
      return this.layers.removeIf(integerIAnimationPair -> integerIAnimationPair.getRight() == layer);
   }

   public boolean removeLayer(int layerLevel) {
      return this.layers.removeIf(integerIAnimationPair -> integerIAnimationPair.getLeft() == layerLevel);
   }

   @NotNull
   @Override
   public FirstPersonMode getFirstPersonMode(float tickDelta) {
      int i = this.layers.size();

      while (i > 0) {
         Pair<Integer, IAnimation> layer = this.layers.get(--i);
         if (layer.getRight().isActive()) {
            FirstPersonMode mode = layer.getRight().getFirstPersonMode(tickDelta);
            if (mode != FirstPersonMode.NONE) {
               return mode;
            }
         }
      }

      return FirstPersonMode.NONE;
   }

   @NotNull
   @Override
   public FirstPersonConfiguration getFirstPersonConfiguration(float tickDelta) {
      int i = this.layers.size();

      while (i > 0) {
         Pair<Integer, IAnimation> layer = this.layers.get(--i);
         if (layer.getRight().isActive()) {
            FirstPersonMode mode = layer.getRight().getFirstPersonMode(tickDelta);
            if (mode != FirstPersonMode.NONE) {
               return layer.getRight().getFirstPersonConfiguration(tickDelta);
            }
         }
      }

      return IAnimation.super.getFirstPersonConfiguration(tickDelta);
   }

   public int getPriority() {
      int priority = 0;

      for (int i = this.layers.size() - 1; i >= 0; i--) {
         Pair<Integer, IAnimation> layer = this.layers.get(i);
         if (layer.getRight().isActive()) {
            priority = layer.getLeft();
            break;
         }
      }

      return priority;
   }
}
