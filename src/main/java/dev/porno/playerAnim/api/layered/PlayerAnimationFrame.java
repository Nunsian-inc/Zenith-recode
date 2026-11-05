package dev.porno.playerAnim.api.layered;

import dev.porno.playerAnim.api.PartKey;
import dev.porno.playerAnim.api.TransformType;
import dev.porno.playerAnim.core.util.Pair;
import dev.porno.playerAnim.core.util.Vec3f;
import java.util.HashMap;
import java.util.Map.Entry;
import org.jetbrains.annotations.NotNull;

public abstract class PlayerAnimationFrame implements IAnimation {
   protected PlayerAnimationFrame.PlayerPart head = new PlayerAnimationFrame.PlayerPart();
   protected PlayerAnimationFrame.PlayerPart body = new PlayerAnimationFrame.PlayerPart();
   protected PlayerAnimationFrame.PlayerPart rightArm = new PlayerAnimationFrame.PlayerPart();
   protected PlayerAnimationFrame.PlayerPart leftArm = new PlayerAnimationFrame.PlayerPart();
   protected PlayerAnimationFrame.PlayerPart rightLeg = new PlayerAnimationFrame.PlayerPart();
   protected PlayerAnimationFrame.PlayerPart leftLeg = new PlayerAnimationFrame.PlayerPart();
   protected PlayerAnimationFrame.PlayerPart rightItem = new PlayerAnimationFrame.PlayerPart();
   protected PlayerAnimationFrame.PlayerPart leftItem = new PlayerAnimationFrame.PlayerPart();
   HashMap<PartKey, PlayerAnimationFrame.PlayerPart> parts = new HashMap<>();

   public PlayerAnimationFrame() {
      this.parts.put(PartKey.HEAD, this.head);
      this.parts.put(PartKey.BODY, this.body);
      this.parts.put(PartKey.RIGHT_ARM, this.rightArm);
      this.parts.put(PartKey.LEFT_ARM, this.leftArm);
      this.parts.put(PartKey.RIGHT_LEG, this.rightLeg);
      this.parts.put(PartKey.LEFT_LEG, this.leftLeg);
      this.parts.put(PartKey.RIGHT_ITEM, this.rightItem);
      this.parts.put(PartKey.LEFT_ITEM, this.leftItem);
   }

   @Override
   public void tick() {
      IAnimation.super.tick();
   }

   @Override
   public boolean isActive() {
      for (Entry<PartKey, PlayerAnimationFrame.PlayerPart> entry : this.parts.entrySet()) {
         PlayerAnimationFrame.PlayerPart part = entry.getValue();
         if (part.bend != null || part.pos != null || part.rot != null || part.scale != null) {
            return true;
         }
      }

      return false;
   }

   public void resetPose() {
      for (Entry<PartKey, PlayerAnimationFrame.PlayerPart> entry : this.parts.entrySet()) {
         entry.getValue().setNull();
      }
   }

   @NotNull
   @Override
   public Vec3f get3DTransform(@NotNull PartKey modelKey, @NotNull TransformType type, float tickDelta, @NotNull Vec3f value0) {
      PlayerAnimationFrame.PlayerPart part = this.parts.get(modelKey);
      if (part == null) {
         return value0;
      }

      return switch (type) {
         case POSITION -> part.pos == null ? value0 : part.pos;
         case ROTATION -> part.rot == null ? value0 : part.rot;
         case SCALE -> part.scale == null ? value0 : part.scale;
         case BEND -> part.bend == null ? value0 : new Vec3f(part.bend.getLeft(), part.bend.getRight(), 0.0F);
         default -> value0;
      };
   }

   public static class PlayerPart {
      public Vec3f pos;
      public Vec3f scale;
      public Vec3f rot;
      public Pair<Float, Float> bend;

      protected void setNull() {
         this.pos = this.scale = this.rot = null;
         this.bend = null;
      }
   }
}
