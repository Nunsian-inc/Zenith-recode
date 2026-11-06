package org.zenith;

import lombok.Generated;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.zenith.utility.misc.Vec2f;

public class Rotation {
   private final float yaw;
   private final float pitch;
   private boolean sensitivityQuantized;
   public static final Rotation INSTANCE = new Rotation(0.0F, 0.0F);

   public Rotation(float yaw, float pitch) {
      this(yaw, pitch, false);
   }

   public Rotation(float yaw, float pitch, boolean sensitivityQuantized) {
      this.yaw = yaw;
      this.pitch = Mth.clamp(pitch, -90.0F, 90.0F);
      this.sensitivityQuantized = sensitivityQuantized;
   }

   public static Rotation lookingAt(Vec3 targetPosition, Vec3 eyePosition) {
      return fromDirection(targetPosition.subtract(eyePosition));
   }

   public static Rotation fromDirection(Vec3 direction) {
      double x = direction.x;
      double y = direction.y;
      double z = direction.z;
      return new Rotation(
         (float)Mth.wrapDegrees(Math.toDegrees(Math.atan2(z, x)) - 90.0),
         (float)Mth.wrapDegrees(-Math.toDegrees(Math.atan2(y, Math.sqrt(x * x + z * z))))
      );
   }

   public float angularDistanceTo(Rotation rotation) {
      return Math.min(this.wrappedDeltaTo(rotation).length(), 180.0F);
   }

   public Vec2f wrappedDeltaTo(Rotation rotation) {
      return new Vec2f(this.wrappedAngleDifference(rotation.yaw, this.yaw), this.wrappedAngleDifference(rotation.pitch, this.pitch));
   }

   public Vec2f deltaTo(Rotation rotation) {
      return new Vec2f(rotation.yaw - this.yaw, rotation.pitch - this.pitch);
   }

   private float wrappedAngleDifference(float targetAngle, float currentAngle) {
      return Mth.wrapDegrees(targetAngle - currentAngle);
   }

   public boolean isWithinAngle(Rotation rotation, float maximumAngle) {
      return this.angularDistanceTo(rotation) <= maximumAngle;
   }

   public boolean isFlag() {
      return this.sensitivityQuantized;
   }

   public Vec3 toDirection() {
      return Vec3.directionFromRotation(this.pitch, this.yaw);
   }

   public Rotation stepTowards(Rotation targetRotation, float yawSpeed, float pitchSpeed) {
      Vec2f angleDelta = this.wrappedDeltaTo(targetRotation);
      float angularDistance = angleDelta.length();
      if (!(angularDistance <= 0.0F) && !Float.isNaN(angularDistance)) {
         float maxYawStep = Math.abs(angleDelta.getFloatValue() / angularDistance) * yawSpeed;
         float maxPitchStep = Math.abs(angleDelta.getFloatValue2() / angularDistance) * pitchSpeed;
         float yawStep = Mth.clamp(angleDelta.getFloatValue(), -maxYawStep, maxYawStep);
         float pitchStep = Mth.clamp(angleDelta.getFloatValue2(), -maxPitchStep, maxPitchStep);
         return new Rotation(this.yaw + yawStep, this.pitch + pitchStep);
      } else {
         return this;
      }
   }

   public boolean isInvalid() {
      return Float.isInfinite(this.yaw) || Float.isNaN(this.yaw) || Float.isInfinite(this.pitch) || Float.isNaN(this.pitch);
   }

   public static float sensitivityStep() {
      double sensitivityScale = Minecraft.getInstance().options.sensitivity().get() * 0.6F + 0.2F;
      return (float)(sensitivityScale * sensitivityScale * sensitivityScale * 8.0 * 0.15F);
   }

   public Rotation quantizeRelativeTo(Rotation referenceRotation) {
      if (!this.sensitivityQuantized && !this.equals(referenceRotation)) {
         Vec2f angleDelta = referenceRotation.wrappedDeltaTo(this);
         float sensitivityStep = sensitivityStep();
         if (!Float.isNaN(sensitivityStep) && !Float.isInfinite(sensitivityStep) && !(sensitivityStep <= 0.0F)) {
            float quantizedYawDelta = Math.round(angleDelta.getFloatValue() / sensitivityStep) * sensitivityStep;
            float quantizedPitchDelta = Math.round(angleDelta.getFloatValue2() / sensitivityStep) * sensitivityStep;
            return new Rotation(referenceRotation.getFloatValue() + quantizedYawDelta, referenceRotation.getFloatValue2() + quantizedPitchDelta, true);
         } else {
            return this;
         }
      } else {
         return this;
      }
   }

   public Rotation add(float yawDelta, float pitchDelta) {
      return new Rotation(this.yaw + yawDelta, Mth.clamp(this.pitch + pitchDelta, -90.0F, 90.0F));
   }

   public Rotation add(Vec2f angleDelta) {
      return new Rotation(this.yaw + angleDelta.getFloatValue(), Mth.clamp(this.pitch + angleDelta.getFloatValue2(), -90.0F, 90.0F));
   }

   @Override
   public boolean equals(Object obj) {
      return !(obj instanceof Rotation rotation) ? false : rotation.yaw == this.yaw && rotation.pitch == this.pitch;
   }

   @Generated
   public float getFloatValue() {
      return this.yaw;
   }

   @Generated
   public float getFloatValue2() {
      return this.pitch;
   }
}
