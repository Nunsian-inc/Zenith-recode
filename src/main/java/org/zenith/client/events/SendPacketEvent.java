package org.zenith.client.events;

import lombok.Generated;
import net.minecraft.network.protocol.Packet;
import org.zenith.CancellableEvent;

public class SendPacketEvent extends CancellableEvent {
   private Packet<?> packet;

   @Generated
   public Packet<?> getPacket2() {
      return this.packet;
   }

   @Generated
   public void setPacket(Packet<?> packet) {
      this.packet = packet;
   }

   @Generated
   public SendPacketEvent(Packet<?> packet) {
      this.packet = packet;
   }
}
