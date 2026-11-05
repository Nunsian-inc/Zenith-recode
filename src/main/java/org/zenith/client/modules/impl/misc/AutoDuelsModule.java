package org.zenith.client.modules.impl.misc;

import com.darkmagician6.eventapi.EventTarget;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ContainerInput;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.StopWatch;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.MultiSelectSetting;

@ModuleInfo(
   name = "AutoDuels",
   category = ModuleCategory.MISC,
   description = "module.autoduels.description"
)
public final class AutoDuelsModule extends Module {
   public static final AutoDuelsModule INSTANCE = new AutoDuelsModule();
   private static final Pattern pattern = Pattern.compile("^\\w{3,16}$");
   private static final long CONST_longValue = 600L;
   private static final long CONST_longValue2 = 31000L;
   private static final double CONST_doubleValue = 500.0;
   private final MultiSelectSetting mode = MultiSelectSetting.method5224(
      "module.autoDuels.mode",
      "module.autoDuels.mode.desc",
      List.of(
         "module.autoDuels.shield",
         "module.autoDuels.shipi",
         "module.autoDuels.bow",
         "module.autoDuels.totem",
         "module.autoDuels.noDebuff",
         "module.autoDuels.balls",
         "module.autoDuels.classik",
         "module.autoDuels.cheats",
         "module.autoDuels.nezer"
      )
   );
   private final BooleanSetting noFriends = new BooleanSetting("module.autoDuels.noFriends", "module.autoDuels.noFriends.desc", true);
   private final StopWatch stopWatch = new StopWatch();
   private final StopWatch stopWatch2 = new StopWatch();
   private final Set<String> items = new HashSet<>();
   private final Map<String, Long> items2 = new HashMap<>();
   private int intValue;
   private double doubleValue;
   private double doubleValue2;
   private double doubleValue3;

   private AutoDuelsModule() {
   }

   @EventTarget
   public void onUpdate(UpdateEvent event) {
      if (mc.player != null && mc.level != null && mc.player.connection != null) {
         double d = Math.sqrt(
            Math.pow(this.doubleValue - mc.player.getX(), 2.0)
               + Math.pow(this.doubleValue2 - mc.player.getY(), 2.0)
               + Math.pow(this.doubleValue3 - mc.player.getZ(), 2.0)
         );
         if (d > 500.0) {
            this.setToggled(false);
         } else {
            this.doubleValue = mc.player.getX();
            this.doubleValue2 = mc.player.getY();
            this.doubleValue3 = mc.player.getZ();
            if (this.stopWatch.method7546(600L)) {
               List<String> list = this.method5981();
               this.intValue = list.size();
               this.method5978();
               Collections.shuffle(list);

               for (String name : list) {
                  if (!this.items.contains(name)
                     && !name.equals(mc.getUser().getName())
                     && (!this.noFriends.isEnabled() || !Zenith.getInstance().getFriends().method7627(name))) {
                     long l = this.items2.getOrDefault(name, 0L);
                     if (System.currentTimeMillis() - l >= 31000L) {
                        mc.player.connection.sendCommand("duel " + name);
                        this.items.add(name);
                        this.items2.put(name, System.currentTimeMillis());
                        this.stopWatch.reset();
                        break;
                     }
                  }
               }
            } else {
               this.method5978();
            }

            this.method5979();
         }
      }
   }

   private void method5978() {
      if (this.stopWatch2.method7546(80L * Math.max(1, this.intValue))) {
         this.items.clear();
         this.stopWatch2.reset();
      }
   }

   private void method5979() {
      if (mc.player.containerMenu instanceof ChestMenu chestMenu && mc.gui.screen() instanceof AbstractContainerScreen containerScreen) {
         String string = containerScreen.getTitle().getString();
         if (string.contains("Выбор набора")) {
            ArrayList list = new ArrayList();

            for (int i = 0; i < this.mode.getItems2().size(); i++) {
               if (this.mode.method5227(i)) {
                  list.add(i);
               }
            }

            Collections.shuffle(list);
            if (!list.isEmpty()) {
               mc.gameMode.handleContainerInput(chestMenu.containerId, (Integer)list.getFirst(), 0, ContainerInput.PICKUP, mc.player);
            }
         } else if (string.contains("Настройка поединка")) {
            mc.gameMode.handleContainerInput(chestMenu.containerId, 0, 0, ContainerInput.PICKUP, mc.player);
         }
      }
   }

   @EventTarget
   public void method5980(PacketEvent event) {
      if (event.method7100() && event.getPacket3() instanceof ClientboundSystemChatPacket systemChatPacket) {
         String lowerCase = systemChatPacket.content().getString().toLowerCase(Locale.ROOT);
         boolean flag = lowerCase.contains("начало") && lowerCase.contains("через") && lowerCase.contains("секунд");
         boolean contains = lowerCase.contains("дуэли » во время поединка запрещено использовать команды");
         if (flag || contains) {
            mc.execute(() -> this.setToggled(false));
         }
      }
   }

   private List<String> method5981() {
      return mc.player
         .connection
         .getOnlinePlayers()
         .stream()
         .map(PlayerInfo::getProfile)
         .map(gameProfile -> gameProfile.name())
         .filter(input -> pattern.matcher(input).matches())
         .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
   }

   @Override
   public void onEnable() {
      if (mc.player != null && mc.level != null) {
         this.items.clear();
         this.items2.clear();
         this.stopWatch.reset();
         this.stopWatch2.reset();
         this.intValue = mc.player.connection == null ? 0 : this.method5981().size();
         this.doubleValue = mc.player.getX();
         this.doubleValue2 = mc.player.getY();
         this.doubleValue3 = mc.player.getZ();
         super.onEnable();
      } else {
         this.setEnabled(false);
      }
   }
}
