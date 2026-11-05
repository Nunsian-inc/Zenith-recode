package org.zenith.utility.mixin.network;

import com.darkmagician6.eventapi.EventManager;
import net.minecraft.network.protocol.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.client.events.PacketReceiveEvent;

@Mixin(
   targets = {"net/minecraft/network/PacketProcessor$ListenerAndPacket"}
)
public abstract class MixinPacketApplyEntry {
   @Shadow
   public abstract Packet<?> packet();

   @Inject(
      method = {"handle()V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void zenith$beforeApply(CallbackInfo callbackInfo) {
      if (EventManager.hasListeners(PacketReceiveEvent.class)) {
         if (((PacketReceiveEvent)EventManager.call(new PacketReceiveEvent(this.packet()))).isCancelled()) {
            callbackInfo.cancel();
         }
      }
   }
}
