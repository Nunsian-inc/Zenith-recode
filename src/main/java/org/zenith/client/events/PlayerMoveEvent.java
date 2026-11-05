package org.zenith.client.events;

import lombok.Generated;
import net.minecraft.world.phys.Vec3;
import org.zenith.CancellableEvent;

public class PlayerMoveEvent extends CancellableEvent {
   private Vec3 vec3d;

   @Generated
   public PlayerMoveEvent(Vec3 var1) {
      this.vec3d = var1;
   }

   @Generated
   public Vec3 getVec3d() {
      return this.vec3d;
   }

   @Generated
   public void setVec3d(Vec3 var1) {
      this.vec3d = var1;
   }
}
