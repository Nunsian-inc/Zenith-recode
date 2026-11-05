package org.zenith.client.events;

import com.darkmagician6.eventapi.events.Event;
import lombok.Generated;
import net.minecraft.world.entity.Entity;

public class EntityAddEvent implements Event {
   private Entity entity;

   @Generated
   public Entity getEntity() {
      return this.entity;
   }

   @Generated
   public EntityAddEvent(Entity entity) {
      this.entity = entity;
   }
}
