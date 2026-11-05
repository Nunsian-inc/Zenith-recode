package org.zenith.utility.misc;

import net.minecraft.client.Minecraft;

public final class Util15 {
   private static float floatValue;

   private Util15() {
   }

   public static void method2703(float var0) {
      floatValue = var0;
   }

   public static float method2704() {
      float var0 = floatValue;
      if (var0 <= 1.0F || var0 >= 179.0F) {
         var0 = Minecraft.getInstance().options.fov().get().floatValue();
      }

      return (float)Math.tan(Math.toRadians(var0) * 0.5);
   }
}
