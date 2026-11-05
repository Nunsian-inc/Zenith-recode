package org.zenith.client.events;

import com.darkmagician6.eventapi.events.Event;
import lombok.Generated;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;

public class EntityStatusEvent implements Event {
   private final ClientboundEntityEventPacket entityStatusS2CPacket;

   @Generated
   public EntityStatusEvent(ClientboundEntityEventPacket var1) {
      this.entityStatusS2CPacket = var1;
   }

   @Generated
   public ClientboundEntityEventPacket getEntityStatusS2CPacket() {
      return this.entityStatusS2CPacket;
   }
}
