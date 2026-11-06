package org.zenith;

import lombok.Generated;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;

public class PacketEvent extends CancellableEvent {
   private final PacketEvent.Type type;
   private Packet<?> packet;
   private PacketListener packetListener;

   public PacketEvent(PacketEvent.Type type, Packet<?> packet) {
      this(type, packet, null);
   }

   public boolean isOutgoing() {
      return this.getType() == PacketEvent.Type.SENT;
   }

   public boolean isIncoming() {
      return this.getType() == PacketEvent.Type.RECEIVE;
   }

   @Generated
   public PacketEvent.Type getType() {
      return this.type;
   }

   @Generated
   public Packet<?> getPacket() {
      return this.packet;
   }

   @Generated
   public PacketListener getPacketListener() {
      return this.packetListener;
   }

   @Generated
   public void setPacket(Packet<?> packet) {
      this.packet = packet;
   }

   @Generated
   public void setPacketListener(PacketListener packetListener) {
      this.packetListener = packetListener;
   }

   @Generated
   public PacketEvent(PacketEvent.Type type, Packet<?> packet, PacketListener packetListener) {
      this.type = type;
      this.packet = packet;
      this.packetListener = packetListener;
   }

   public static enum Type {
      SENT,
      RECEIVE;
   }
}
