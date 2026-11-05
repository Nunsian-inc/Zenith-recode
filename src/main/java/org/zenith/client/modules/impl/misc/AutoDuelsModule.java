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
   public void onUpdate(UpdateEvent var1) {
      if (mc.player != null && mc.level != null && mc.player.connection != null) {
         double var2 = Math.sqrt(
            Math.pow(this.doubleValue - mc.player.getX(), 2.0)
               + Math.pow(this.doubleValue2 - mc.player.getY(), 2.0)
               + Math.pow(this.doubleValue3 - mc.player.getZ(), 2.0)
         );
         if (var2 > 500.0) {
            this.setToggled(false);
         } else {
            this.doubleValue = mc.player.getX();
            this.doubleValue2 = mc.player.getY();
            this.doubleValue3 = mc.player.getZ();
            if (this.stopWatch.method7546(600L)) {
               List<String> var4 = this.method5981();
               this.intValue = var4.size();
               this.method5978();
               Collections.shuffle(var4);

               for (Object var6_o : (Iterable<?>)(Object)(var4)) {
                  String var6 = (String)var6_o;
                  if (!this.items.contains(var6)
                     && !var6.equals(mc.getUser().getName())
                     && (!this.noFriends.isEnabled() || !Zenith.getInstance().getFriends().method7627(var6))) {
                     long var7 = this.items2.getOrDefault(var6, 0L);
                     if (System.currentTimeMillis() - var7 >= 31000L) {
                        mc.player.connection.sendCommand("duel " + var6);
                        this.items.add(var6);
                        this.items2.put(var6, System.currentTimeMillis());
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
      if (mc.player.containerMenu instanceof ChestMenu var1 && mc.gui.screen() instanceof AbstractContainerScreen var2) {
         String var7 = var2.getTitle().getString();
         if (var7.contains("Выбор набора")) {
            ArrayList var4 = new ArrayList();

            for (int var5 = 0; var5 < this.mode.getItems2().size(); var5++) {
               if (this.mode.method5227(var5)) {
                  var4.add(var5);
               }
            }

            Collections.shuffle(var4);
            if (!var4.isEmpty()) {
               mc.gameMode.handleContainerInput(var1.containerId, (Integer)var4.getFirst(), 0, ContainerInput.PICKUP, mc.player);
            }
         } else if (var7.contains("Настройка поединка")) {
            mc.gameMode.handleContainerInput(var1.containerId, 0, 0, ContainerInput.PICKUP, mc.player);
         }
      }
   }

   @EventTarget
   public void method5980(PacketEvent var1) {
      if (var1.method7100() && var1.getPacket3() instanceof ClientboundSystemChatPacket var2) {
         String var6 = var2.content().getString().toLowerCase(Locale.ROOT);
         boolean var4 = var6.contains("начало") && var6.contains("через") && var6.contains("секунд");
         boolean var5 = var6.contains("дуэли » во время поединка запрещено использовать команды");
         if (var4 || var5) {
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
         .map(var0 -> var0.name())
         .filter(var0 -> pattern.matcher(var0).matches())
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
