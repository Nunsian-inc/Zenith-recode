package org.zenith.utility.misc;

import net.minecraft.client.Minecraft;

public final class Util15 {
   private static float floatValue;

   private Util15() {
   }

   public static void method2703(float returnValueF) {
      floatValue = returnValueF;
   }

   public static float method2704() {
      float angdeg = floatValue;
      if (angdeg <= 1.0F || angdeg >= 179.0F) {
         angdeg = Minecraft.getInstance().options.fov().get().floatValue();
      }

      return (float)Math.tan(Math.toRadians(angdeg) * 0.5);
   }
}
