package org.zenith.client.events;

import com.darkmagician6.eventapi.events.Event;
import lombok.Generated;
import net.minecraft.world.phys.Vec3;

public class CameraPosEvent implements Event {
   private Vec3 vec3d;

   @Generated
   public Vec3 getVec3d() {
      return this.vec3d;
   }

   @Generated
   public void setVec3d(Vec3 var1) {
      this.vec3d = var1;
   }

   @Generated
   public CameraPosEvent(Vec3 var1) {
      this.vec3d = var1;
   }
}
