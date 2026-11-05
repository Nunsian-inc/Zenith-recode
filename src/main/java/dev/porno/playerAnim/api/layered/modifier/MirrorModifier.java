package dev.porno.playerAnim.api.layered.modifier;

import dev.porno.playerAnim.api.PartKey;
import dev.porno.playerAnim.api.TransformType;
import dev.porno.playerAnim.api.firstPerson.FirstPersonConfiguration;
import dev.porno.playerAnim.core.util.Vec3f;
import java.util.Map;
import lombok.Generated;
import org.jetbrains.annotations.NotNull;

public class MirrorModifier extends AbstractModifier {
   public static final Map<PartKey, PartKey> mirrorMap = Map.of(
      PartKey.LEFT_ARM,
      PartKey.RIGHT_ARM,
      PartKey.LEFT_LEG,
      PartKey.RIGHT_LEG,
      PartKey.LEFT_ITEM,
      PartKey.RIGHT_ITEM,
      PartKey.RIGHT_ARM,
      PartKey.LEFT_ARM,
      PartKey.RIGHT_LEG,
      PartKey.LEFT_LEG,
      PartKey.RIGHT_ITEM,
      PartKey.LEFT_ITEM
   );
   private boolean enabled = true;

   @NotNull
   @Override
   public Vec3f get3DTransform(@NotNull String modelName, @NotNull TransformType type, float tickDelta, @NotNull Vec3f value0) {
      return this.get3DTransform(PartKey.keyForId(modelName), type, tickDelta, value0);
   }

   @NotNull
   @Override
   public Vec3f get3DTransform(@NotNull PartKey partKey, @NotNull TransformType type, float tickDelta, @NotNull Vec3f value0) {
      if (!this.isEnabled()) {
         return super.get3DTransform(partKey, type, tickDelta, value0);
      }

      if (mirrorMap.containsKey(partKey)) {
         partKey = mirrorMap.get(partKey);
      }

      value0 = this.transformVector(value0, type);
      Vec3f vec3f = super.get3DTransform(partKey, type, tickDelta, value0);
      return this.transformVector(vec3f, type);
   }

   @NotNull
   @Override
   public FirstPersonConfiguration getFirstPersonConfiguration(float tickDelta) {
      FirstPersonConfiguration configuration = super.getFirstPersonConfiguration(tickDelta);
      return this.isEnabled()
         ? new FirstPersonConfiguration()
            .setShowLeftArm(configuration.isShowRightArm())
            .setShowRightArm(configuration.isShowLeftArm())
            .setShowLeftItem(configuration.isShowRightItem())
            .setShowRightItem(configuration.isShowLeftItem())
         : configuration;
   }

   protected Vec3f transformVector(Vec3f value0, TransformType type) {
      switch (type) {
         case POSITION:
            return new Vec3f(-value0.getX(), value0.getY(), value0.getZ());
         case ROTATION:
            return new Vec3f(value0.getX(), -value0.getY(), -value0.getZ());
         case BEND:
            return new Vec3f(value0.getX(), -value0.getY(), value0.getZ());
         default:
            return value0;
      }
   }

   @Generated
   public MirrorModifier() {
   }

   @Generated
   public MirrorModifier(boolean enabled) {
      this.enabled = enabled;
   }

   @Generated
   public boolean isEnabled() {
      return this.enabled;
   }

   @Generated
   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }
}
