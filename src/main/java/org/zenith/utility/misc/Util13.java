package org.zenith.utility.misc;

public final class Util13 {
   private static volatile float floatValue = 1.0F;

   private Util13() {
   }

   public static float getFloatValue() {
      return floatValue;
   }

   public static void method2404(float f) {
      if (Float.isFinite(f) && f > 0.0F) {
         floatValue = f;
      }
   }
}
