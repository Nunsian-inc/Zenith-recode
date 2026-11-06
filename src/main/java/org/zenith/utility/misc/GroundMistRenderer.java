package org.zenith.utility.misc;

import org.zenith.utility.game.FlakeState;
import org.zenith.utility.render.Glowboost;

public final class GroundMistRenderer {
   private static final long MIST_SEED = 501470595897L;
   private static final int CELL_SIZE = 2;
   private static final int RENDER_DISTANCE = 15;

   private GroundMistRenderer() {
   }

   public static void renderGroundMist(Glowboost glowboost, FlakeState flakeState, WeatherFlakeChannel weatherFlakeChannel, float intensity, float time, double x, double y, double z) {
      float mistStrength = weatherFlakeChannel.floatValue27 * intensity;
      if (!(mistStrength <= 0.02F)) {
         glowboost.method3235(1.0F);
         byte cellRadius = 7;
         int centerCellX = (int)Math.floor(x / 2.0);
         int centerCellZ = (int)Math.floor(z / 2.0);
         int particlesPerCell = mistStrength > 0.6F ? 4 : 3;

         for (int cellOffsetX = -cellRadius; cellOffsetX <= cellRadius; cellOffsetX++) {
            for (int cellOffsetZ = -cellRadius; cellOffsetZ <= cellRadius; cellOffsetZ++) {
               int cellX = centerCellX + cellOffsetX;
               int cellZ = centerCellZ + cellOffsetZ;
               long seed = cellSeed(cellX, cellZ);

               for (int particleIndex = 0; particleIndex < particlesPerCell; particleIndex++) {
                  double localX = Glowboost.method3268(seed, particleIndex, 0) * 2.0F;
                  double localZ = Glowboost.method3268(seed, particleIndex, 1) * 2.0F;
                  double driftTime = time * (0.16 + 0.22 * Glowboost.method3268(seed, particleIndex, 2));
                  double particleX = cellX * 2.0 + mod(localX + weatherFlakeChannel.floatValue20 * driftTime, 2.0);
                  double particleZ = cellZ * 2.0 + mod(localZ + weatherFlakeChannel.floatValue21 * driftTime, 2.0);
                  double deltaX = particleX - x;
                  double deltaZ = particleZ - z;
                  double distance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
                  if (!(distance > 15.0) && (!(distance > 3.0) || !(deltaX * glowboost.getDoubleValue10() + deltaZ * glowboost.getDoubleValue12() < -0.25 * distance))) {
                     double surfaceY = flakeState.method4547(particleX, particleZ);
                     if (surfaceY != Double.NEGATIVE_INFINITY) {
                        double particleY = surfaceY + 0.25 + 1.15 * Glowboost.method3268(seed, particleIndex, 3);
                        double verticalDistance = Math.abs(particleY - y);
                        if (!(verticalDistance > 11.0)) {
                           float verticalFade = (float)Math.min(1.0, (11.0 - verticalDistance) / 3.0);
                           float nearFade = (float)Math.min(1.0, distance / 2.6);
                           float farFade = 1.0F - (float)Math.min(1.0, Math.max(0.0, distance - 15.0 + 4.0) / 4.0);
                           float pulse = 0.55F
                              + 0.45F
                                 * (float)Math.sin(
                                    time * (0.25 + 0.35 * Glowboost.method3268(seed, particleIndex, 4)) + Glowboost.method3268(seed, particleIndex, 5) * 6.283F
                                 );
                           float density = 0.35F + 0.65F * sampleMistDensity(particleX, particleZ, time, weatherFlakeChannel.floatValue20, weatherFlakeChannel.floatValue21);
                           float opacity = mistStrength * nearFade * farFade * verticalFade * pulse * density * 0.13F;
                           if (!(opacity <= 0.002F)) {
                              float sizeRandom = Glowboost.method3268(seed, particleIndex, 6);
                              float size = 1.2F + 4.6F * sizeRandom * sizeRandom;
                              double offsetX = (Glowboost.method3268(seed, particleIndex, 7) - 0.5F) * size * 0.7;
                              double offsetZ = (Glowboost.method3268(seed, particleIndex, 8) - 0.5F) * size * 0.7;
                              double offsetY = (Glowboost.method3268(seed, particleIndex, 9) - 0.5F) * size * 0.35;
                              glowboost.method3251(particleX - offsetX, particleY - offsetY, particleZ - offsetZ, size * 0.78F, weatherFlakeChannel.intValue8, opacity);
                              glowboost.method3251(particleX + offsetX, particleY + offsetY, particleZ + offsetZ, size * 1.25F, weatherFlakeChannel.intValue8, opacity * 0.72F);
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private static float sampleMistDensity(double x, double z, float time, float windX, float windZ) {
      double driftedX = x - windX * time * 0.35;
      double driftedZ = z - windZ * time * 0.35;
      double noise = Math.sin(driftedX * 0.21 + driftedZ * 0.13) + Math.sin(driftedX * 0.07 - driftedZ * 0.31 + 2.1) + Math.sin((driftedX + driftedZ) * 0.045 + time * 0.12);
      return (float)Math.clamp((noise + 3.0) / 6.0, 0.0, 1.0);
   }

   private static double mod(double value, double modulus) {
      double remainder = value % modulus;
      return remainder < 0.0 ? remainder + modulus : remainder;
   }

   private static long cellSeed(int cellX, int cellZ) {
      return 501470595897L ^ cellX * -6752110988234923001L ^ cellZ * -6939452855193903323L;
   }
}
