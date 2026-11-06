package org.zenith.utility.game;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import lombok.Generated;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;
import org.zenith.MinecraftAccess;
import org.zenith.Rotation;
import org.zenith.Zenith;
import org.zenith.base.rotation.RotationUtils;
import org.zenith.client.modules.impl.combat.AuraModule;
import org.zenith.utility.misc.Vec2f;

public class AuraRandomizer implements MinecraftAccess {
   private static final double SAMPLE_SPACING = 0.15;
   private static final int MAX_HORIZONTAL_SAMPLES = 14;
   private static final int VERTICAL_SAMPLES = 14;
   private final Random random = new SecureRandom();
   private Vec3 jitterOffset = Vec3.ZERO;

   public Vec3 method4673(Vec3 eyePosition, AABB boundingBox, float range, Vec3 jitterScale, boolean ignoreWalls) {
      var samples = this.method4675(eyePosition, boundingBox, range, ignoreWalls);
      var innerSamples = this.filterInnerSamples(samples, range);
      Vec3 aimPoint = this.selectNearCenter(eyePosition, innerSamples, range, ignoreWalls);
      if (aimPoint == null) {
         aimPoint = this.selectNearCenter(eyePosition, samples, range, ignoreWalls);
      }

      if (aimPoint == null) {
         aimPoint = this.closestToEyes(eyePosition, samples);
      }

      this.updateJitter(jitterScale);
      return (aimPoint == null ? boundingBox.getCenter() : aimPoint).add(this.jitterOffset);
   }

   public Vec3 method4674(Vec3 eyePosition, AABB boundingBox, float range, Vec3 jitterScale, boolean ignoreWalls) {
      var samples = this.method4675(eyePosition, boundingBox, range, ignoreWalls);
      var innerSamples = this.filterInnerSamples(samples, range);
      Vec3 aimPoint = this.selectByRotation(eyePosition, innerSamples, range, ignoreWalls);
      if (aimPoint == null) {
         aimPoint = this.selectByRotation(eyePosition, samples, range, ignoreWalls);
      }

      if (aimPoint == null) {
         aimPoint = this.closestToRotation(eyePosition, samples);
      }

      this.updateJitter(jitterScale);
      return (aimPoint == null ? boundingBox.getCenter() : aimPoint).add(this.jitterOffset);
   }

   public List<Vec3> method4675(Vec3 eyePosition, AABB boundingBox, float range, boolean ignoreWalls) {
      if (isOutsideRange(eyePosition, boundingBox, range)) {
         return new ArrayList<>();
      } else {
         double width = boundingBox.getXsize();
         double height = boundingBox.getYsize();
         double depth = boundingBox.getZsize();
         int samplesX = this.sampleCountForExtent(width);
         int samplesZ = this.sampleCountForExtent(depth);
         int samplesY = Math.max(2, VERTICAL_SAMPLES);
         double stepY = height / (samplesY - 1);
         double minX = boundingBox.minX;
         double minY = boundingBox.minY;
         double minZ = boundingBox.minZ;
         double stepX = samplesX <= 1 ? 0.0 : width / (samplesX - 1);
         double stepZ = samplesZ <= 1 ? 0.0 : depth / (samplesZ - 1);
         List<Vec3> samples = new ArrayList<>(samplesY * samplesX * samplesZ);
         double rangeSquared = (double)range * range;

         for (int i = 0; i < samplesY; i++) {
            double y = minY + i * stepY;

            for (int j = 0; j < samplesX; j++) {
               double x = minX + j * stepX;

               for (int k = 0; k < samplesZ; k++) {
                  double z = minZ + k * stepZ;
                  if (!(distanceSquared(eyePosition, x, y, z) > rangeSquared)) {
                     Vec3 sample = new Vec3(x, y, z);
                     if (this.isSampleReachable(eyePosition, sample, range, ignoreWalls)) {
                        samples.add(sample);
                     }
                  }
               }
            }
         }

         return samples;
      }
   }

   public boolean method4676(LivingEntity living, float range, boolean ignoreWalls) {
      AABB boundingBox = living.getBoundingBox();
      Vec3 eyePosition = mc.player.getEyePosition();
      if (isOutsideRange(eyePosition, boundingBox, range)) {
         return false;
      } else {
         double width = boundingBox.getXsize();
         double height = boundingBox.getYsize();
         double depth = boundingBox.getZsize();
         int samplesX = this.sampleCountForExtent(width);
         int samplesZ = this.sampleCountForExtent(depth);
         int samplesY = Math.max(2, VERTICAL_SAMPLES);
         double stepY = height / (samplesY - 1);
         double minX = boundingBox.minX;
         double minY = boundingBox.minY;
         double minZ = boundingBox.minZ;
         double stepX = samplesX <= 1 ? 0.0 : width / (samplesX - 1);
         double stepZ = samplesZ <= 1 ? 0.0 : depth / (samplesZ - 1);
         double rangeSquared = (double)range * range;

         for (int i = 0; i < samplesY; i++) {
            double y = minY + i * stepY;

            for (int j = 0; j < samplesX; j++) {
               double x = minX + j * stepX;

               for (int k = 0; k < samplesZ; k++) {
                  double z = minZ + k * stepZ;
                  if (!(distanceSquared(eyePosition, x, y, z) > rangeSquared)) {
                     Vec3 sample = new Vec3(x, y, z);
                     if (this.isSampleReachable(eyePosition, sample, range, ignoreWalls)) {
                        return true;
                     }
                  }
               }
            }
         }

         return false;
      }
   }

   private static boolean isOutsideRange(Vec3 eyePosition, AABB boundingBox, float range) {
      double distanceX = Math.max(0.0, Math.max(boundingBox.minX - eyePosition.x, eyePosition.x - boundingBox.maxX));
      double distanceY = Math.max(0.0, Math.max(boundingBox.minY - eyePosition.y, eyePosition.y - boundingBox.maxY));
      double distanceZ = Math.max(0.0, Math.max(boundingBox.minZ - eyePosition.z, eyePosition.z - boundingBox.maxZ));
      double paddedRange = Math.abs((double)range) + 1.0E-4;
      return distanceX * distanceX + distanceY * distanceY + distanceZ * distanceZ > paddedRange * paddedRange;
   }

   private static double distanceSquared(Vec3 eyePosition, double x, double y, double z) {
      double deltaX = x - eyePosition.x;
      double deltaY = y - eyePosition.y;
      double deltaZ = z - eyePosition.z;
      return deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;
   }

   private boolean isSampleReachable(Vec3 eyePosition, Vec3 sample, float range, boolean ignoreWalls) {
      if (eyePosition.distanceToSqr(sample) > (double)range * range) {
         return false;
      } else if (ignoreWalls) {
         return true;
      } else {
         ClipContext clipContext = new ClipContext(eyePosition, sample, Block.COLLIDER, Fluid.NONE, mc.player);
         BlockHitResult blockHitResult = TargetUtils.method3217(clipContext, AuraModule.INSTANCE::method6362);
         return blockHitResult.getType() != Type.BLOCK;
      }
   }

   private Vec3 selectByRotation(Vec3 eyePosition, List<Vec3> samples, float range, boolean ignoreWalls) {
      if (samples != null && !samples.isEmpty()) {
         Vec3 center = this.averageSamples(samples);
         return this.isSampleReachable(eyePosition, center, range, ignoreWalls)
            ? center
            : samples.stream().filter(sample -> this.isSampleReachable(eyePosition, sample, range, ignoreWalls)).min(Comparator.comparingDouble(sample -> {
               Vec2f rotationDelta = Zenith.getInstance().getRotationManager().getEffectiveRotation().wrappedDeltaTo(Rotation.lookingAt(sample, eyePosition));
               return Math.abs(rotationDelta.getFloatValue()) + Math.abs(rotationDelta.getFloatValue2());
            })).orElse(null);
      } else {
         return null;
      }
   }

   private Vec3 selectNearCenter(Vec3 eyePosition, List<Vec3> samples, float range, boolean ignoreWalls) {
      if (samples != null && !samples.isEmpty()) {
         Vec3 center = this.averageSamples(samples);
         return this.isSampleReachable(eyePosition, center, range, ignoreWalls)
            ? center
            : samples.stream()
               .filter(sample -> this.isSampleReachable(eyePosition, sample, range, ignoreWalls))
               .min(Comparator.comparingDouble(sample -> sample.distanceToSqr(center)))
               .orElse(null);
      } else {
         return null;
      }
   }

   private List<Vec3> filterInnerSamples(List<Vec3> samples, float range) {
      if (samples != null && !samples.isEmpty()) {
         Vec3 eyePosition = mc.player.getEyePosition();
         double innerRange = Math.max(0.0, range - 0.3);
         double innerRangeSquared = innerRange * innerRange;
         ArrayList<Vec3> innerSamples = new ArrayList<>();

         for (Vec3 sample : samples) {
            if (eyePosition.distanceToSqr(sample) < innerRangeSquared) {
               innerSamples.add(sample);
            }
         }

         return innerSamples;
      } else {
         return List.of();
      }
   }

   private Vec3 averageSamples(List<Vec3> samples) {
      double sumX = 0.0;
      double sumY = 0.0;
      double sumZ = 0.0;
      int size = samples.size();

      for (Vec3 sample : samples) {
         sumX += sample.x;
         sumY += sample.y;
         sumZ += sample.z;
      }

      return new Vec3(sumX / size, sumY / size, sumZ / size);
   }

   private Vec3 closestToRotation(Vec3 eyePosition, List<Vec3> samples) {
      return samples != null && !samples.isEmpty() ? samples.stream().min(Comparator.comparingDouble(sample -> {
         Vec2f rotationDelta = Zenith.getInstance().getRotationManager().getEffectiveRotation().wrappedDeltaTo(Rotation.lookingAt(sample, eyePosition));
         return Math.abs(rotationDelta.getFloatValue()) + Math.abs(rotationDelta.getFloatValue2());
      })).orElse(null) : null;
   }

   private Vec3 closestToEyes(Vec3 eyePosition, List<Vec3> samples) {
      return samples != null && !samples.isEmpty() ? samples.stream().min(Comparator.comparingDouble(sample -> sample.distanceToSqr(eyePosition))).orElse(null) : null;
   }

   private void updateJitter(Vec3 jitterScale) {
      this.jitterOffset = this.jitterOffset.add(this.random.nextGaussian(), this.random.nextGaussian(), this.random.nextGaussian()).multiply(jitterScale);
   }

   private double rotationDistance(Vec3 origin, Vec3 target, Rotation rotation) {
      if (rotation == null) {
         return Double.POSITIVE_INFINITY;
      } else {
         Rotation targetRotation = RotationUtils.method6912(target.subtract(origin));
         Vec2f rotationDelta = rotation.wrappedDeltaTo(targetRotation);
         return Math.hypot(rotationDelta.getFloatValue(), rotationDelta.getFloatValue2());
      }
   }

   private int sampleCountForExtent(double extent) {
      if (extent <= 0.0) {
         return 1;
      } else {
         int sampleCount = (int)Math.ceil(extent / SAMPLE_SPACING) + 1;
         int cappedSampleCount = Math.min(sampleCount, MAX_HORIZONTAL_SAMPLES);
         return Math.max(2, cappedSampleCount);
      }
   }

   @Generated
   public Random getRandom() {
      return this.random;
   }

   @Generated
   public Vec3 getVec3d() {
      return this.jitterOffset;
   }
}
