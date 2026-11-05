package org.zenith.client.events;

import com.darkmagician6.eventapi.events.Event;
import lombok.Generated;
import net.minecraft.world.phys.Vec3;

public class MovementCorrectionEvent implements Event {
   private final float speed;
   private final Vec3 vec3d;

   @Generated
   public float getSpeed() {
      return this.speed;
   }

   @Generated
   public Vec3 getVec3d() {
      return this.vec3d;
   }

   @Generated
   public MovementCorrectionEvent(float speed, Vec3 vec3d) {
      this.speed = speed;
      this.vec3d = vec3d;
   }
}
