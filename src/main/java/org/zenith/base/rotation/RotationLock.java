package org.zenith.base.rotation;

import org.zenith.MinecraftAccess;
import org.zenith.Rotation;
import org.zenith.client.modules.impl.player.AutoWardenModule;

public final class RotationLock implements MinecraftAccess {
   private static final int MAX_LOCK_AGE_TICKS = 2;
   private static Rotation lockedRotation;
   private static int lockTick = Integer.MIN_VALUE;

   private RotationLock() {
   }

   public static boolean method2706() {
      return AutoWardenModule.INSTANCE.isEnabled() && AutoWardenModule.INSTANCE.method3949();
   }

   public static void method2707(float yaw, float pitch) {
      if (mc.player != null) {
         lockedRotation = new Rotation(yaw, pitch);
         lockTick = mc.player.tickCount;
      }
   }

   public static Rotation method2708() {
      if (mc.player != null && lockedRotation != null) {
         int elapsedTicks = mc.player.tickCount - lockTick;
         return elapsedTicks >= 0 && elapsedTicks <= 2 ? lockedRotation : null;
      } else {
         return null;
      }
   }

   public static void reset() {
      lockedRotation = null;
      lockTick = Integer.MIN_VALUE;
   }
}
