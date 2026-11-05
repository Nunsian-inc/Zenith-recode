package dev.porno.playerAnim.impl.animation;

import dev.porno.playerAnim.api.PartKey;
import dev.porno.playerAnim.api.TransformType;
import dev.porno.playerAnim.api.layered.AnimationStack;
import dev.porno.playerAnim.api.layered.IAnimation;
import dev.porno.playerAnim.core.impl.AnimationProcessor;
import dev.porno.playerAnim.core.util.MathHelper;
import dev.porno.playerAnim.core.util.Vec3f;
import net.minecraft.client.model.geom.ModelPart;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

public class AnimationApplier extends AnimationProcessor {
   @NotNull
   public static final AnimationApplier EMPTY = new AnimationApplier(null);

   public AnimationApplier(@Nullable IAnimation animation) {
      super(animation);
   }

   @Internal
   @NotNull
   public static AnimationApplier snapshot(@NotNull AnimationStack stack, float tickDelta) {
      AnimationStack frozen = stack.snapshot();
      if (!frozen.isActive()) {
         return EMPTY;
      }

      AnimationApplier applier = new AnimationApplier(frozen);
      applier.setTickDelta(tickDelta);
      return applier;
   }

   public void updatePart(@NotNull PartKey partKey, ModelPart part) {
      Vec3f pos = this.get3DTransform(partKey, TransformType.POSITION, new Vec3f(part.x, part.y, part.z));
      part.x = pos.getX();
      part.y = pos.getY();
      part.z = pos.getZ();
      Vec3f rot = this.get3DTransform(
         partKey,
         TransformType.ROTATION,
         new Vec3f(MathHelper.clampToRadian(part.xRot), MathHelper.clampToRadian(part.yRot), MathHelper.clampToRadian(part.zRot))
      );
      part.setRotation(rot.getX(), rot.getY(), rot.getZ());
      Vec3f scale = this.get3DTransform(partKey, TransformType.SCALE, new Vec3f(part.xScale, part.yScale, part.zScale));
      part.xScale = scale.getX();
      part.yScale = scale.getY();
      part.zScale = scale.getZ();
   }
}
