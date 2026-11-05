package org.zenith.client.events;

import lombok.Generated;
import net.minecraft.world.phys.Vec3;
import org.zenith.CancellableEvent;

public class PlayerMoveEvent extends CancellableEvent {
   private Vec3 vec3d;

   @Generated
   public PlayerMoveEvent(Vec3 vec3d) {
      this.vec3d = vec3d;
   }

   @Generated
   public Vec3 getVec3d() {
      return this.vec3d;
   }

   @Generated
   public void setVec3d(Vec3 vec3d) {
      this.vec3d = vec3d;
   }
}
