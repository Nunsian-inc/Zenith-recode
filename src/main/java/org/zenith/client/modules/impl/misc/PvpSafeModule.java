package org.zenith.client.modules.impl.misc;

import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.network.protocol.game.ServerboundChatCommandPacket;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.Zenith;
import org.zenith.base.Module;

@ModuleInfo(
   name = "PvpSafe",
   description = "module.pvpsafe.description",
   category = ModuleCategory.MISC
)
public final class PvpSafeModule extends Module {
   public static final PvpSafeModule INSTANCE = new PvpSafeModule();

   @EventTarget
   public void method1004(PacketEvent event) {
      if (event.method7099() && Zenith.getInstance().getServerDetector().method1635() && event.getPacket3() instanceof ServerboundChatCommandPacket(String str)) {
         String str2 = str;
         if (str2.contains("hub")) {
            event.setCancelled(true);
         }
      }
   }
}
