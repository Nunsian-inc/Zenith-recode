package org.zenith.utility.misc;

public final class TimerSpeed {
   private static volatile float speedMultiplier = 1.0F;

   private TimerSpeed() {
   }

   public static float getSpeedMultiplier() {
      return speedMultiplier;
   }

   public static void setSpeedMultiplier(float multiplier) {
      if (Float.isFinite(multiplier) && multiplier > 0.0F) {
         speedMultiplier = multiplier;
      }
   }
}
