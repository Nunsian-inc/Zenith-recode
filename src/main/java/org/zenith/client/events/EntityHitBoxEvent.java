package org.zenith.client.events;

import com.darkmagician6.eventapi.events.Event;
import lombok.Generated;
import net.minecraft.world.entity.Entity;

public class EntityHitBoxEvent implements Event {
   private Entity entity;
   private float size;

   @Generated
   public Entity getEntity() {
      return this.entity;
   }

   @Generated
   public float getSize() {
      return this.size;
   }

   @Generated
   public void setEntity(Entity entity) {
      this.entity = entity;
   }

   @Generated
   public void setSize(float size) {
      this.size = size;
   }

   @Generated
   @Override
   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof EntityHitBoxEvent event)) {
         return false;
      } else if (!event.method265(this)) {
         return false;
      } else if (Float.compare(this.getSize(), event.getSize()) != 0) {
         return false;
      } else {
         Entity entity = this.getEntity();
         Entity entity2 = event.getEntity();
         return entity == null ? entity2 == null : entity.equals(entity2);
      }
   }

   @Generated
   public boolean method265(Object obj) {
      return obj instanceof EntityHitBoxEvent;
   }

   @Generated
   @Override
   public int hashCode() {
      byte b = 59;
      int n = 1;
      n = n * 59 + Float.floatToIntBits(this.getSize());
      Entity entity = this.getEntity();
      return n * 59 + (entity == null ? 43 : entity.hashCode());
   }

   @Generated
   @Override
   public String toString() {
      return "EventEntityHitBox(entity=" + this.getEntity() + ", size=" + this.getSize() + ")";
   }

   @Generated
   public EntityHitBoxEvent(Entity entity, float size) {
      this.entity = entity;
      this.size = size;
   }
}
