package org.zenith.client.modules.impl.combat;

import com.darkmagician6.eventapi.EventTarget;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.world.entity.player.Player;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.StopWatch;
import org.zenith.base.Module;
import org.zenith.client.events.AttackEvent;

@ModuleInfo(
   name = "AntiBot",
   category = ModuleCategory.COMBAT,
   description = "module.antibot.description"
)
public final class AntiBotModule extends Module {
   public static final AntiBotModule INSTANCE = new AntiBotModule();
   private final List<Player> items = new ArrayList<>();
   private final StopWatch stopWatch = new StopWatch();
   private final Set<Integer> items2 = HashSet.newHashSet(4);

   private AntiBotModule() {
   }

   @EventTarget
   public void method2784(AttackEvent event) {
   }

   @Override
   public void onEnable() {
      super.onEnable();
      if (!this.items.isEmpty()) {
         this.items.clear();
      }
   }

   @Override
   public void onDisable() {
      super.onDisable();
      if (!this.items.isEmpty()) {
         this.items.clear();
      }
   }

   public boolean method2785(Player player) {
      if (!this.isEnabled()) {
         return false;
      } else if (mc.player == null) {
         return false;
      } else {
         double d = mc.player.distanceTo(player);
         if (d < 8.0) {
            if (mc.getConnection() == null) {
               return false;
            } else {
               PlayerInfo playerInfo = mc.getConnection().getPlayerInfo(player.getUUID());
               if (playerInfo == null) {
                  return true;
               } else {
                  return playerInfo.getTabListDisplayName() == null ? true : playerInfo.getTabListDisplayName().getString().equals(player.getGameProfile().name());
               }
            }
         } else {
            return false;
         }
      }
   }
}
