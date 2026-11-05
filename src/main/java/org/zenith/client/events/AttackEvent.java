package org.zenith.client.events;

import lombok.Generated;
import net.minecraft.world.entity.Entity;
import org.zenith.CancellableEvent;

public final class AttackEvent extends CancellableEvent {
   private final Entity entity;
   private final AttackEvent.Type type;

   @Generated
   public Entity getEntity() {
      return this.entity;
   }

   @Generated
   public AttackEvent.Type getType4() {
      return this.type;
   }

   @Generated
   public AttackEvent(Entity entity, AttackEvent.Type type) {
      this.entity = entity;
      this.type = type;
   }

   public static enum Type {
      POST,
      PRE;
   }
}
