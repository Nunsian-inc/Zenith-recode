package org.zenith.utility.misc;

import net.minecraft.client.Minecraft;

public final class CameraFov {
   private static float capturedFov;

   private CameraFov() {
   }

   public static void setCapturedFov(float fovDegrees) {
      capturedFov = fovDegrees;
   }

   public static float getTanHalfFov() {
      float fovDegrees = capturedFov;
      if (fovDegrees <= 1.0F || fovDegrees >= 179.0F) {
         fovDegrees = Minecraft.getInstance().options.fov().get().floatValue();
      }

      return (float)Math.tan(Math.toRadians(fovDegrees) * 0.5);
   }
}
