package org.zenith.client.events;

import lombok.Generated;
import net.minecraft.network.protocol.Packet;
import org.zenith.CancellableEvent;

public final class PacketReceiveEvent extends CancellableEvent {
   private final Packet<?> packet;

   public PacketReceiveEvent(Packet<?> packet) {
      this.packet = packet;
   }

   @Generated
   public Packet<?> getPacket() {
      return this.packet;
   }
}
