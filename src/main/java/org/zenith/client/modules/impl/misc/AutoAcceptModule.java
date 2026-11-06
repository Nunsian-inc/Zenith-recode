package org.zenith.client.modules.impl.misc;

import com.darkmagician6.eventapi.EventTarget;
import java.util.Locale;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.client.settings.BooleanSetting;

@ModuleInfo(
   name = "AutoAccept",
   category = ModuleCategory.MISC,
   description = "module.autoaccept.description"
)
public final class AutoAcceptModule extends Module {
   public static final AutoAcceptModule INSTANCE = new AutoAcceptModule();
   private final BooleanSetting onlyFriend = new BooleanSetting("module.autoAccept.onlyFriend", "module.autoAccept.onlyFriend.desc", false);

   private AutoAcceptModule() {
   }

   @EventTarget
   public void onPacket(PacketEvent event) {
      if (mc.player != null && mc.level != null) {
         if (event.isIncoming()) {
            if (event.getPacket() instanceof ClientboundSystemChatPacket systemChatPacket) {
               String lowerCase = systemChatPacket.content().getString().toLowerCase(Locale.ROOT);
               if (lowerCase.contains("телепортироваться") || lowerCase.contains("has requested teleport") || lowerCase.contains("просит к вам телепортироваться")) {
                  mc.execute(() -> this.method7736(lowerCase));
               }
            }
         }
      }
   }

   private void method7736(String lowerCase) {
      if (mc.player != null) {
         if (this.onlyFriend.isEnabled()) {
            boolean flag = false;

            for (String str : Zenith.getInstance().getFriends().getItems()) {
               if (lowerCase.contains(str.toLowerCase(Locale.ROOT))) {
                  flag = true;
                  break;
               }
            }

            if (!flag) {
               return;
            }
         }

         mc.player.connection.sendCommand("tpaccept");
      }
   }
}
