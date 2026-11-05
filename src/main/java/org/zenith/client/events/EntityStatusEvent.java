package org.zenith.client.events;

import com.darkmagician6.eventapi.events.Event;
import lombok.Generated;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;

public class EntityStatusEvent implements Event {
   private final ClientboundEntityEventPacket entityStatusS2CPacket;

   @Generated
   public EntityStatusEvent(ClientboundEntityEventPacket entityStatusS2CPacket) {
      this.entityStatusS2CPacket = entityStatusS2CPacket;
   }

   @Generated
   public ClientboundEntityEventPacket getEntityStatusS2CPacket() {
      return this.entityStatusS2CPacket;
   }
}
