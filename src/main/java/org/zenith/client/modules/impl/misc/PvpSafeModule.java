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
   public void method1004(PacketEvent var1) {
      if (var1.method7099() && Zenith.getInstance().getServerDetector().method1635() && var1.getPacket3() instanceof ServerboundChatCommandPacket(String var7)) {
         String var5 = var7;
         if (var5.contains("hub")) {
            var1.setCancelled(true);
         }
      }
   }
}
