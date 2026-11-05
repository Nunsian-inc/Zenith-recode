package org.zenith.base.rotation;

import org.zenith.MinecraftAccess;
import org.zenith.Rotation;
import org.zenith.client.modules.impl.player.AutoWardenModule;

public final class RotationLock implements MinecraftAccess {
   private static final int CONST_intValue = 2;
   private static Rotation rotation;
   private static int intValue = Integer.MIN_VALUE;

   private RotationLock() {
   }

   public static boolean method2706() {
      return AutoWardenModule.INSTANCE.isEnabled() && AutoWardenModule.INSTANCE.method3949();
   }

   public static void method2707(float yaw, float pitch) {
      if (mc.player != null) {
         rotation = new Rotation(yaw, pitch);
         intValue = mc.player.tickCount;
      }
   }

   public static Rotation method2708() {
      if (mc.player != null && rotation != null) {
         int n = mc.player.tickCount - intValue;
         return n >= 0 && n <= 2 ? rotation : null;
      } else {
         return null;
      }
   }

   public static void reset() {
      rotation = null;
      intValue = Integer.MIN_VALUE;
   }
}
