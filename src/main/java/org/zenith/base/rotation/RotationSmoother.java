package org.zenith.base.rotation;

import java.util.concurrent.ThreadLocalRandom;
import lombok.Generated;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.zenith.MinecraftAccess;
import org.zenith.Rotation;

public final class RotationSmoother implements MinecraftAccess {
   private static final RotationSmoother.Entry defaultState = new RotationSmoother.Entry();

   public static Rotation method7954(Vec3 location) {
      return method7955(defaultState, location);
   }

   public static Rotation method7955(RotationSmoother.Entry state, Vec3 location) {
      return method7957(state, RotationUtils.method6913(location));
   }

   public static Rotation method7956(Rotation targetRotation) {
      return method7957(defaultState, targetRotation);
   }

   public static Rotation method7957(RotationSmoother.Entry state, Rotation targetRotation) {
      return method7959(state, targetRotation, 34.0F, 18.0F, 0.55F);
   }

   public static Rotation method7958(Rotation targetRotation, float yawSpeed, float pitchSpeed, float smoothing) {
      return method7959(defaultState, targetRotation, yawSpeed, pitchSpeed, smoothing);
   }

   public static Rotation method7959(RotationSmoother.Entry requestedState, Rotation targetRotation, float yawSpeed, float pitchSpeed, float smoothing) {
      RotationSmoother.Entry state = requestedState == null ? defaultState : requestedState;
      Rotation currentRotation = state.rotation == null ? RotationUtils.method6911() : state.rotation;
      Rotation smoothedRotation = method7961(state, currentRotation, targetRotation, yawSpeed, pitchSpeed, smoothing);
      return targetRotation;
   }

   public static Rotation method7960(Rotation currentRotation, Rotation targetRotation, float yawSpeed, float pitchSpeed, float smoothing) {
      return method7961(defaultState, currentRotation, targetRotation, yawSpeed, pitchSpeed, smoothing);
   }

   public static Rotation method7961(RotationSmoother.Entry requestedState, Rotation currentRotation, Rotation targetRotation, float yawSpeed, float pitchSpeed, float smoothing) {
      RotationSmoother.Entry state = requestedState == null ? defaultState : requestedState;
      refreshMotionProfile(state);
      float yawDelta = Mth.wrapDegrees(targetRotation.getFloatValue() - currentRotation.getFloatValue());
      float pitchDelta = targetRotation.getFloatValue2() - currentRotation.getFloatValue2();
      float angularDistance = (float)Math.hypot(yawDelta, pitchDelta);
      float noiseScale = Mth.clamp(angularDistance / 12.0F, 0.0F, 1.0F);
      float distanceScale = Mth.clamp(angularDistance / 45.0F, 0.18F, 1.0F);
      updateJitter(state, noiseScale);
      float smoothingAlpha = Mth.clamp(smoothing * state.smoothScale, 0.08F, 0.95F);
      float maxYawStep = calculateStepLimit(yawDelta, yawSpeed * state.speedScale, smoothingAlpha, distanceScale);
      float maxPitchStep = calculateStepLimit(pitchDelta, pitchSpeed * state.speedScale, smoothingAlpha, distanceScale);
      float yawNoise = calculateNoise(yawDelta, state.yawBias + state.yawJitter, noiseScale);
      float pitchNoise = calculateNoise(pitchDelta, state.pitchBias + state.pitchJitter, noiseScale);
      float desiredYawStep = Mth.clamp(yawDelta + yawNoise, -maxYawStep, maxYawStep);
      float desiredPitchStep = Mth.clamp(pitchDelta + pitchNoise, -maxPitchStep, maxPitchStep);
      state.yawVelocity = Mth.lerp(smoothingAlpha, state.yawVelocity, desiredYawStep);
      state.pitchVelocity = Mth.lerp(smoothingAlpha, state.pitchVelocity, desiredPitchStep);
      state.yawVelocity = dampOpposingVelocity(state.yawVelocity, yawDelta, angularDistance);
      state.pitchVelocity = dampOpposingVelocity(state.pitchVelocity, pitchDelta, angularDistance);
      Rotation smoothedRotation = currentRotation.add(state.yawVelocity, state.pitchVelocity);
      return smoothedRotation.quantizeRelativeTo(currentRotation);
   }

   public static void method7962(Rotation rotation) {
      if (mc.player != null && rotation != null) {
         mc.player.setYRot(rotation.getFloatValue());
         mc.player.setXRot(rotation.getFloatValue2());
      }
   }

   public static void reset() {
      method7963(defaultState);
   }

   public static void method7963(RotationSmoother.Entry requestedState) {
      RotationSmoother.Entry state = requestedState == null ? defaultState : requestedState;
      state.reset();
   }

   private static void refreshMotionProfile(RotationSmoother.Entry state) {
      if (state.profileTicksRemaining-- <= 0) {
         state.profileTicksRemaining = randomIntInclusive(3, 9);
         state.speedScale = randomFloat(0.86F, 1.18F);
         state.smoothScale = randomFloat(0.82F, 1.08F);
         state.yawBias = randomSignedFloat(0.18F);
         state.pitchBias = randomSignedFloat(0.11F);
      }
   }

   private static void updateJitter(RotationSmoother.Entry state, float noiseScale) {
      float bound = 0.025F + noiseScale * 0.08F;
      state.yawJitter = Mth.lerp(0.22F, state.yawJitter, randomSignedFloat(bound));
      state.pitchJitter = Mth.lerp(0.22F, state.pitchJitter, randomSignedFloat(bound * 0.65F));
   }

   private static float calculateNoise(float angleDelta, float bias, float noiseScale) {
      float direction = Math.signum(angleDelta);
      float proportionalNoise = 0.0F;
      if (Math.abs(angleDelta) > 8.0F) {
         proportionalNoise = Mth.clamp(angleDelta * randomFloat(0.01F, 0.035F), -1.15F, 1.15F);
      }

      return proportionalNoise + bias * noiseScale + direction * randomFloat(0.0F, 0.018F) * noiseScale;
   }

   private static float dampOpposingVelocity(float velocity, float angleDelta, float angularDistance) {
      return !(angularDistance > 2.0F) && Math.signum(velocity) != Math.signum(angleDelta) ? velocity * randomFloat(0.35F, 0.7F) : velocity;
   }

   private static float calculateStepLimit(float angleDelta, float speed, float smoothingAlpha, float distanceScale) {
      float absoluteDelta = Math.abs(angleDelta);
      float minimumSpeed = Math.max(0.1F, speed);
      float angleScale = Mth.clamp(absoluteDelta / 35.0F, 0.22F, 1.0F);
      float randomScale = 0.72F + distanceScale * randomFloat(0.22F, 0.38F);
      return Math.max(Rotation.sensitivityStep(), minimumSpeed * angleScale * smoothingAlpha * randomScale);
   }

   private static int randomIntInclusive(int origin, int maximum) {
      return ThreadLocalRandom.current().nextInt(origin, maximum + 1);
   }

   private static float randomFloat(float origin, float bound) {
      return (float)ThreadLocalRandom.current().nextDouble(origin, bound);
   }

   private static float randomSignedFloat(float bound) {
      return randomFloat(-bound, bound);
   }

   @Generated
   private RotationSmoother() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }

   public static final class Entry {
      private Rotation rotation;
      private float yawVelocity;
      private float pitchVelocity;
      private float speedScale = 1.0F;
      private float smoothScale = 1.0F;
      private float yawBias;
      private float pitchBias;
      private float yawJitter;
      private float pitchJitter;
      private int profileTicksRemaining;

      private void reset() {
         this.rotation = null;
         this.yawVelocity = 0.0F;
         this.pitchVelocity = 0.0F;
         this.speedScale = 1.0F;
         this.smoothScale = 1.0F;
         this.yawBias = 0.0F;
         this.pitchBias = 0.0F;
         this.yawJitter = 0.0F;
         this.pitchJitter = 0.0F;
         this.profileTicksRemaining = 0;
      }
   }
}
