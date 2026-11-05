package org.zenith.utility.mixin.network;

import com.darkmagician6.eventapi.EventManager;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.MinecraftAccess;
import org.zenith.PacketEvent;
import org.zenith.client.events.OutgoingPacketEvent;
import org.zenith.client.events.SendPacketEvent;

@Mixin({Connection.class})
public abstract class MixinClientConnection implements MinecraftAccess {
   @Unique
   private boolean stackOverflowFix;
   @Shadow
   private PacketListener packetListener;

   @Shadow
   public abstract void send(Packet<?> packet);

   @Inject(
      method = {"genericsFtw(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static <T extends PacketListener> void triggerReceivePacketEvent(Packet<T> packet, PacketListener packetListener, CallbackInfo callbackInfo) {
      if (EventManager.hasListeners(PacketEvent.class)) {
         PacketEvent event = new PacketEvent(PacketEvent.Type.RECEIVE, packet, packetListener);
         EventManager.call(event);
         if (event.isCancelled()) {
            callbackInfo.cancel();
         }
      }
   }

   @Inject(
      method = {"send(Lnet/minecraft/network/protocol/Packet;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void triggerSendPacketEvent(Packet<?> packet, CallbackInfo callbackInfo) {
      if (!this.stackOverflowFix) {
         if (EventManager.hasListeners(OutgoingPacketEvent.class)) {
            OutgoingPacketEvent event = new OutgoingPacketEvent(packet);
            EventManager.call(event);
            if (event.isCancelled()) {
               callbackInfo.cancel();
               return;
            }
         }

         Packet packet2 = packet;
         if (EventManager.hasListeners(PacketEvent.class)) {
            PacketEvent event = new PacketEvent(PacketEvent.Type.SENT, packet);
            EventManager.call(event);
            if (event.isCancelled()) {
               callbackInfo.cancel();
               return;
            }

            packet2 = event.getPacket3();
            if (packet2 != packet) {
               callbackInfo.cancel();

               try {
                  this.stackOverflowFix = true;
                  this.send(packet2);
               } finally {
                  this.stackOverflowFix = false;
               }
            }
         }

         if (EventManager.hasListeners(SendPacketEvent.class)) {
            EventManager.call(new SendPacketEvent(packet2));
         }
      }
   }
}
