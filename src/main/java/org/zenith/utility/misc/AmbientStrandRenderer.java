package org.zenith.utility.misc;

import net.minecraft.util.Mth;
import org.zenith.utility.render.Glowboost;

public final class AmbientStrandRenderer {
   private static final float TWO_PI = (float) (Math.PI * 2);
   private static final long STRAND_SEED = 25214903917L;
   private static final double SEED_COORDINATE_RANGE = 4096.0;
   private static final int TRAIL_SEGMENTS = 14;
   private static final float BASE_TRAIL_TIME_STEP = 0.42F;
   private static final double REPULSION_RADIUS = 3.4;
   private static final double REPULSION_STRENGTH = 1.9;

   private AmbientStrandRenderer() {
   }

   public static void renderStrands(
      Glowboost glowboost,
      double x,
      double y,
      double z,
      double repulsionX,
      double repulsionY,
      double repulsionZ,
      float time,
      int strandCount,
      float radius,
      float revealProgress,
      ColorProvider colorProvider
   ) {
      for (int strandIndex = 0; strandIndex < strandCount; strandIndex++) {
         renderStrand(glowboost, strandIndex, x, y, z, repulsionX, repulsionY, repulsionZ, time, radius, revealProgress, colorProvider.color(strandIndex * 29));
      }
   }

   private static void renderStrand(
      Glowboost glowboost,
      int strandIndex,
      double x,
      double y,
      double z,
      double repulsionX,
      double repulsionY,
      double repulsionZ,
      float time,
      float radius,
      float revealProgress,
      int color
   ) {
      double seedX = seedCoordinate(strandIndex, 0);
      double seedY = seedCoordinate(strandIndex, 1);
      double seedZ = seedCoordinate(strandIndex, 2);
      double wrappedX = wrapCoordinate(seedX, x, radius);
      double wrappedY = wrapCoordinate(seedY, y, radius);
      double wrappedZ = wrapCoordinate(seedZ, z, radius);
      double wrapOffsetX = wrappedX - seedX;
      double wrapOffsetY = wrappedY - seedY;
      double wrapOffsetZ = wrappedZ - seedZ;
      float edgeFade = volumeFade(wrappedX - x, wrappedY - y, wrappedZ - z, radius);
      if (!(edgeFade < 0.01F)) {
         float revealDelay = Glowboost.method3268(25214903917L, strandIndex, 11) * 0.4F;
         float localRevealProgress = Mth.clamp((revealProgress - revealDelay) / (1.0F - revealDelay), 0.0F, 1.0F);
         if (!(localRevealProgress <= 0.0F)) {
            float strandOpacity = edgeFade * Glowboost.easeOut(localRevealProgress);
            float amplitude = 1.5F + Glowboost.method3268(25214903917L, strandIndex, 3) * 3.4F;
            float frequencyX = 0.16F + Glowboost.method3268(25214903917L, strandIndex, 4) * 0.3F;
            float frequencyY = 0.14F + Glowboost.method3268(25214903917L, strandIndex, 5) * 0.26F;
            float frequencyZ = 0.17F + Glowboost.method3268(25214903917L, strandIndex, 6) * 0.32F;
            float phaseX = Glowboost.method3268(25214903917L, strandIndex, 7) * (float) (Math.PI * 2);
            float phaseY = Glowboost.method3268(25214903917L, strandIndex, 8) * (float) (Math.PI * 2);
            float phaseZ = Glowboost.method3268(25214903917L, strandIndex, 9) * (float) (Math.PI * 2);
            float trailTimeStep = 0.42F * (0.6F + Glowboost.method3268(25214903917L, strandIndex, 10) * 0.9F);
            float pulse = 0.72F + 0.28F * Mth.sin(time * 0.7F + strandIndex * 1.7F);
            double previousX = 0.0;
            double previousY = 0.0;
            double previousZ = 0.0;
            float previousOpacity = 0.0F;
            boolean hasPreviousPoint = false;

            for (int segmentIndex = 0; segmentIndex < 14; segmentIndex++) {
               float segmentTime = time - segmentIndex * trailTimeStep;
               double pointX = seedX + wrapOffsetX + amplitude * Mth.sin(frequencyX * segmentTime + phaseX);
               double pointY = seedY + wrapOffsetY + amplitude * 0.5 * Mth.sin(frequencyY * segmentTime + phaseY);
               double pointZ = seedZ + wrapOffsetZ + amplitude * Mth.cos(frequencyZ * segmentTime + phaseZ);
               double repulsionDeltaX = pointX - repulsionX;
               double repulsionDeltaY = pointY - repulsionY;
               double repulsionDeltaZ = pointZ - repulsionZ;
               double repulsionDistance = Math.sqrt(repulsionDeltaX * repulsionDeltaX + repulsionDeltaY * repulsionDeltaY + repulsionDeltaZ * repulsionDeltaZ);
               if (repulsionDistance < 3.4 && repulsionDistance > 0.001) {
                  double repulsionFalloff = (3.4 - repulsionDistance) / 3.4;
                  double repulsionScale = repulsionFalloff * repulsionFalloff * 1.9 / repulsionDistance;
                  pointX += repulsionDeltaX * repulsionScale;
                  pointY += repulsionDeltaY * repulsionScale;
                  pointZ += repulsionDeltaZ * repulsionScale;
               }

               float trailFraction = 1.0F - segmentIndex / 14.0F;
               float pointOpacity = strandOpacity * pulse * trailFraction * trailFraction;
               if (hasPreviousPoint && pointOpacity > 0.01F && previousOpacity > 0.01F) {
                  glowboost.method3236(previousX, previousY, previousZ, pointX, pointY, pointZ, ColorUtils.method6439(color, Math.min(pointOpacity, previousOpacity)));
               }

               if (segmentIndex % 2 == 0 && pointOpacity > 0.02F) {
                  glowboost.method3250(pointX, pointY, pointZ, 0.055F + 0.075F * trailFraction, color, pointOpacity * 0.8F);
               }

               previousX = pointX;
               previousY = pointY;
               previousZ = pointZ;
               previousOpacity = pointOpacity;
               hasPreviousPoint = true;
            }
         }
      }
   }

   private static double seedCoordinate(int strandIndex, int axis) {
      return Glowboost.method3268(25214903917L, strandIndex, axis) * 4096.0;
   }

   private static double wrapCoordinate(double coordinate, double center, double radius) {
      double diameter = radius * 2.0;
      double offset = coordinate - center;
      offset -= Math.floor((offset + radius) / diameter) * diameter;
      return center + offset;
   }

   private static float volumeFade(double offsetX, double offsetY, double offsetZ, double radius) {
      return Math.min(axisFade(offsetX, radius), Math.min(axisFade(offsetY, radius), axisFade(offsetZ, radius)));
   }

   private static float axisFade(double offset, double radius) {
      double distance = Math.abs(offset);
      double innerRadius = radius * 0.72;
      return distance <= innerRadius ? 1.0F : (float)Math.max(0.0, 1.0 - (distance - innerRadius) / (radius - innerRadius));
   }
}
