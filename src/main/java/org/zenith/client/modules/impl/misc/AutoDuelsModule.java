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
   private static final Pattern PLAYER_NAME_PATTERN = Pattern.compile("^\\w{3,16}$");
   private static final long INVITE_INTERVAL_MS = 600L;
   private static final long PLAYER_INVITE_COOLDOWN_MS = 31000L;
   private static final double TELEPORT_DISTANCE = 500.0;
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
   private final StopWatch inviteTimer = new StopWatch();
   private final StopWatch inviteCycleTimer = new StopWatch();
   private final Set<String> invitedPlayers = new HashSet<>();
   private final Map<String, Long> lastInviteTimes = new HashMap<>();
   private int onlinePlayerCount;
   private double previousX;
   private double previousY;
   private double previousZ;

   private AutoDuelsModule() {
   }

   @EventTarget
   public void onUpdate(UpdateEvent event) {
      if (mc.player != null && mc.level != null && mc.player.connection != null) {
         double distanceMoved = Math.sqrt(
            Math.pow(this.previousX - mc.player.getX(), 2.0)
               + Math.pow(this.previousY - mc.player.getY(), 2.0)
               + Math.pow(this.previousZ - mc.player.getZ(), 2.0)
         );
         if (distanceMoved > 500.0) {
            this.setToggled(false);
         } else {
            this.previousX = mc.player.getX();
            this.previousY = mc.player.getY();
            this.previousZ = mc.player.getZ();
            if (this.inviteTimer.hasElapsed(600L)) {
               List<String> onlinePlayerNames = this.getOnlinePlayerNames();
               this.onlinePlayerCount = onlinePlayerNames.size();
               this.resetInviteCycleIfElapsed();
               Collections.shuffle(onlinePlayerNames);

               for (String name : onlinePlayerNames) {
                  if (!this.invitedPlayers.contains(name)
                     && !name.equals(mc.getUser().getName())
                     && (!this.noFriends.isEnabled() || !Zenith.getInstance().getFriends().method7627(name))) {
                     long lastInviteTime = this.lastInviteTimes.getOrDefault(name, 0L);
                     if (System.currentTimeMillis() - lastInviteTime >= 31000L) {
                        mc.player.connection.sendCommand("duel " + name);
                        this.invitedPlayers.add(name);
                        this.lastInviteTimes.put(name, System.currentTimeMillis());
                        this.inviteTimer.reset();
                        break;
                     }
                  }
               }
            } else {
               this.resetInviteCycleIfElapsed();
            }

            this.handleDuelScreens();
         }
      }
   }

   private void resetInviteCycleIfElapsed() {
      if (this.inviteCycleTimer.hasElapsed(80L * Math.max(1, this.onlinePlayerCount))) {
         this.invitedPlayers.clear();
         this.inviteCycleTimer.reset();
      }
   }

   private void handleDuelScreens() {
      if (mc.player.containerMenu instanceof ChestMenu chestMenu && mc.gui.screen() instanceof AbstractContainerScreen containerScreen) {
         String screenTitle = containerScreen.getTitle().getString();
         if (screenTitle.contains("Выбор набора")) {
            ArrayList selectedModeSlots = new ArrayList();

            for (int i = 0; i < this.mode.getItems2().size(); i++) {
               if (this.mode.method5227(i)) {
                  selectedModeSlots.add(i);
               }
            }

            Collections.shuffle(selectedModeSlots);
            if (!selectedModeSlots.isEmpty()) {
               mc.gameMode.handleContainerInput(chestMenu.containerId, (Integer)selectedModeSlots.getFirst(), 0, ContainerInput.PICKUP, mc.player);
            }
         } else if (screenTitle.contains("Настройка поединка")) {
            mc.gameMode.handleContainerInput(chestMenu.containerId, 0, 0, ContainerInput.PICKUP, mc.player);
         }
      }
   }

   @EventTarget
   public void onPacket(PacketEvent event) {
      if (event.isIncoming() && event.getPacket() instanceof ClientboundSystemChatPacket systemChatPacket) {
         String chatMessage = systemChatPacket.content().getString().toLowerCase(Locale.ROOT);
         boolean isDuelCountdown = chatMessage.contains("начало") && chatMessage.contains("через") && chatMessage.contains("секунд");
         boolean isDuelCommandRestriction = chatMessage.contains("дуэли » во время поединка запрещено использовать команды");
         if (isDuelCountdown || isDuelCommandRestriction) {
            mc.execute(() -> this.setToggled(false));
         }
      }
   }

   private List<String> getOnlinePlayerNames() {
      return mc.player
         .connection
         .getOnlinePlayers()
         .stream()
         .map(PlayerInfo::getProfile)
         .map(gameProfile -> gameProfile.name())
         .filter(input -> PLAYER_NAME_PATTERN.matcher(input).matches())
         .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
   }

   @Override
   public void onEnable() {
      if (mc.player != null && mc.level != null) {
         this.invitedPlayers.clear();
         this.lastInviteTimes.clear();
         this.inviteTimer.reset();
         this.inviteCycleTimer.reset();
         this.onlinePlayerCount = mc.player.connection == null ? 0 : this.getOnlinePlayerNames().size();
         this.previousX = mc.player.getX();
         this.previousY = mc.player.getY();
         this.previousZ = mc.player.getZ();
         super.onEnable();
      } else {
         this.setEnabled(false);
      }
   }
}
