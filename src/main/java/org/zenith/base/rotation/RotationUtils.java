package org.zenith.base.rotation;

import lombok.Generated;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.zenith.MinecraftAccess;
import org.zenith.Rotation;

public final class RotationUtils implements MinecraftAccess {
   public static Rotation method6911() {
      return new Rotation(mc.player.getYRot(), mc.player.getXRot());
   }

   public static Rotation method6912(Vec3 vec) {
      return new Rotation(
         (float)Mth.wrapDegrees(Math.toDegrees(Math.atan2(vec.z, vec.x)) - 90.0),
         (float)Mth.wrapDegrees(Math.toDegrees(-Math.atan2(vec.y, Math.hypot(vec.x, vec.z))))
      );
   }

   public static Rotation method6913(Vec3 location) {
      return method6912(location.subtract(mc.player.getEyePosition()));
   }

   @Generated
   private RotationUtils() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
}
