package org.zenith.client.events;

import com.darkmagician6.eventapi.events.Event;
import lombok.Generated;
import net.minecraft.world.entity.LivingEntity;

public class EntityDeathEvent implements Event {
   private final LivingEntity livingEntity;

   @Generated
   public EntityDeathEvent(LivingEntity livingEntity) {
      this.livingEntity = livingEntity;
   }

   @Generated
   public LivingEntity getLivingEntity() {
      return this.livingEntity;
   }
}
