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
   public void onPacket(PacketEvent var1) {
      if (var1.method7100() && var1.getPacket3() instanceof ClientboundResourcePackPushPacket var2) {
         net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl var6 = var1.getPacketListener() instanceof ClientCommonPacketListenerImpl var4 ? var4 : mc.getConnection();
         if (var6 != null) {
            var1.setCancelled(true);
            var6.send(new ServerboundResourcePackPacket(var2.id(), Action.ACCEPTED));
            var6.send(new ServerboundResourcePackPacket(var2.id(), Action.SUCCESSFULLY_LOADED));
         }
      }
   }
}
