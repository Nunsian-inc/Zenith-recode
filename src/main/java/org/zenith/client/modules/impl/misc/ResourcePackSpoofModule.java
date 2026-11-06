package org.zenith.client.modules.impl.misc;

import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.network.protocol.common.ClientboundResourcePackPushPacket;
import net.minecraft.network.protocol.common.ServerboundResourcePackPacket;
import net.minecraft.network.protocol.common.ServerboundResourcePackPacket.Action;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.base.Module;

@ModuleInfo(
   name = "ResourcePackSpoof",
   category = ModuleCategory.MISC,
   description = "module.srpspoof.description"
)
public final class ResourcePackSpoofModule extends Module {
   public static final ResourcePackSpoofModule INSTANCE = new ResourcePackSpoofModule();

   private ResourcePackSpoofModule() {
   }

   @EventTarget
   public void onPacket(PacketEvent event) {
      if (event.isIncoming() && event.getPacket() instanceof ClientboundResourcePackPushPacket resourcePackPushPacket) {
         net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl connection = event.getPacketListener() instanceof ClientCommonPacketListenerImpl packetListenerImpl ? packetListenerImpl : mc.getConnection();
         if (connection != null) {
            event.setCancelled(true);
            connection.send(new ServerboundResourcePackPacket(resourcePackPushPacket.id(), Action.ACCEPTED));
            connection.send(new ServerboundResourcePackPacket(resourcePackPushPacket.id(), Action.SUCCESSFULLY_LOADED));
         }
      }
   }
}
