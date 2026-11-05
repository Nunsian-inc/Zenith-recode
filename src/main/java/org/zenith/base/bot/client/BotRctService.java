package org.zenith.base.bot.client;

import java.util.Locale;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ContainerInput;
import org.zenith.base.bot.net.BotPlayHandler;
import org.zenith.base.bot.world.BotPlayer;
import org.zenith.base.bot.world.BotWorld;
import org.zenith.utility.misc.StopWatch;
import org.zenith.utility.render.ServerDetector;

public final class BotRctService {
   private static final int SOLO_MIN_ANARCHY = 1;
   private static final int DUO_MIN_ANARCHY = 18;
   private static final int DUO_MAX_ANARCHY = 38;
   private static final int TRIO_MIN_ANARCHY = 39;
   private static final int TRIO_MAX_ANARCHY = 57;
   private static final int CLAN_MIN_ANARCHY = 58;
   private static final int CLAN_MAX_ANARCHY = 74;
   private static final long COMMAND_INTERVAL_MS = 1500L;
   private static final long LOBBY_CONFIRM_MS = 1500L;
   private static final long MENU_CLICK_INTERVAL_MS = 500L;
   private static final long JOIN_TIMEOUT_MS = 10000L;
   private static final int MENU_CLICK_RETRY_LIMIT = 20;
   private static final String LITE_MENU_TITLE = "лайт анархии";
   private final BotClient client;
   private final StopWatch stopWatch = new StopWatch();
   private int anarchy;
   private boolean lobby;
   private boolean joining;
   private boolean hubCommandSent;
   private long lobbyDetectedAt;
   private boolean waitingForMenuUpdate;
   private int menuClickAttempts;
   private int clickedSyncId = -1;
   private int clickedInventorySize = -1;

   public BotRctService(BotClient client) {
      this.client = client;
   }

   public boolean isActive() {
      return this.anarchy != 0;
   }

   public int getTargetAnarchy() {
      return this.anarchy;
   }

   public boolean isHolyWorldHere() {
      BotPlayHandler playHandler = this.client.getPlayHandler();
      return playHandler != null && ServerDetector.method1634(playHandler.getBrand());
   }

   public int currentAnarchyHere() {
      return ServerDetector.method1633(this.client.getWorld());
   }

   public void reconnect(int nextInt) {
      if (this.client.isSameThread()) {
         this.reconnectNow(nextInt);
      } else {
         this.client.execute(() -> this.reconnectNow(nextInt));
      }
   }

   private void reconnectNow(int anarchy) {
      if (anarchy >= 1 && anarchy <= 74) {
         this.anarchy = anarchy;
         this.beginLobbyPhase();
      } else {
         this.client.systemMessage("RCT: неверный лайт " + anarchy);
      }
   }

   public void stop() {
      this.anarchy = 0;
      this.lobby = false;
      this.joining = false;
      this.hubCommandSent = false;
      this.lobbyDetectedAt = 0L;
      this.resetMenuUpdate();
   }

   public void onGameMessage(Component decorate) {
      if (this.anarchy != 0) {
         String lowerCase = decorate.getString().toLowerCase(Locale.ROOT);
         if (!lowerCase.contains("хаб") && lowerCase.contains("не удалось")) {
            this.client.systemMessage("RCT: на анархию " + this.anarchy + " нельзя зайти");
            this.stop();
         }
      }
   }

   public void tick(BotWorld world, BotPlayer player) {
      if (this.anarchy != 0) {
         BotPlayHandler playHandler = this.client.getPlayHandler();
         if (playHandler != null && world != null && player != null) {
            if (!this.isHolyWorldHere()) {
               this.stop();
            } else {
               int currentAnarchyHere = this.currentAnarchyHere();
               if (this.lobby) {
                  this.resetMenuUpdate();
                  if (!this.hubCommandSent) {
                     if (this.stopWatch.method318(1500.0)) {
                        playHandler.sendCommand("hub");
                        this.hubCommandSent = true;
                        this.lobbyDetectedAt = 0L;
                     }
                  } else if (currentAnarchyHere == -1) {
                     if (this.lobbyDetectedAt == 0L) {
                        this.lobbyDetectedAt = System.currentTimeMillis();
                     }

                     if (System.currentTimeMillis() - this.lobbyDetectedAt >= 1500L) {
                        this.lobby = false;
                        this.hubCommandSent = false;
                        this.lobbyDetectedAt = 0L;
                        this.stopWatch.reset();
                     }
                  } else {
                     this.lobbyDetectedAt = 0L;
                     if (this.stopWatch.method318(1500.0)) {
                        playHandler.sendCommand("hub");
                     }
                  }
               } else if (currentAnarchyHere == this.anarchy) {
                  this.closeLiteMenu(player, playHandler);
                  this.stop();
               } else if (currentAnarchyHere != -1) {
                  this.closeLiteMenu(player, playHandler);
                  this.beginLobbyPhase();
               } else if (this.joining) {
                  this.resetMenuUpdate();
                  if (this.stopWatch.method317(10000.0)) {
                     this.beginLobbyPhase();
                  }
               } else {
                  ChestMenu liteMenu = this.getLiteMenu(player, playHandler);
                  if (liteMenu != null) {
                     if (this.waitingForMenuUpdate && this.hasMenuUpdated(liteMenu)) {
                        this.resetMenuUpdate();
                     }

                     if (this.stopWatch.method318(500.0)) {
                        boolean flag = liteMenu.getContainer().getContainerSize() < 10;
                        int[] anarchyMenuSlots = this.getAnarchyMenuSlots(this.anarchy);
                        if (flag) {
                           if (this.menuClickAttempts >= 20) {
                              this.restartLiteMenu(player, playHandler);
                              return;
                           }

                           playHandler.getInteractionManager().clickSlot(liteMenu.containerId, anarchyMenuSlots[0], 0, ContainerInput.PICKUP, player);
                           this.rememberMenuClick(liteMenu);
                        } else {
                           this.resetMenuUpdate();
                           playHandler.getInteractionManager().clickSlot(liteMenu.containerId, 18 + this.anarchy - anarchyMenuSlots[1], 0, ContainerInput.PICKUP, player);
                           player.closeContainer();
                           this.joining = true;
                           this.stopWatch.reset();
                        }
                     }
                  } else {
                     this.resetMenuUpdate();
                     if (this.stopWatch.method318(1500.0)) {
                        playHandler.sendCommand("lite");
                     }
                  }
               }
            }
         }
      }
   }

   private void rememberMenuClick(ChestMenu liteMenu) {
      this.waitingForMenuUpdate = true;
      this.menuClickAttempts++;
      this.clickedSyncId = liteMenu.containerId;
      this.clickedInventorySize = liteMenu.getContainer().getContainerSize();
   }

   private boolean hasMenuUpdated(ChestMenu liteMenu) {
      return liteMenu.containerId != this.clickedSyncId || liteMenu.getContainer().getContainerSize() != this.clickedInventorySize;
   }

   private void restartLiteMenu(BotPlayer player, BotPlayHandler playHandler) {
      this.resetMenuUpdate();
      player.closeContainer();
      playHandler.sendCommand("lite");
      this.stopWatch.reset();
   }

   private void resetMenuUpdate() {
      this.waitingForMenuUpdate = false;
      this.menuClickAttempts = 0;
      this.clickedSyncId = -1;
      this.clickedInventorySize = -1;
   }

   private void beginLobbyPhase() {
      this.lobby = true;
      this.joining = false;
      this.hubCommandSent = false;
      this.lobbyDetectedAt = 0L;
      this.resetMenuUpdate();
      this.stopWatch.method320(1500L);
   }

   private ChestMenu getLiteMenu(BotPlayer player, BotPlayHandler playHandler) {
      Component currentScreenTitle = playHandler.getCurrentScreenTitle();
      if (currentScreenTitle != null && currentScreenTitle.getString().toLowerCase(Locale.ROOT).contains("лайт анархии")) {
         return player.containerMenu instanceof ChestMenu chestMenu ? chestMenu : null;
      } else {
         return null;
      }
   }

   private void closeLiteMenu(BotPlayer player, BotPlayHandler playHandler) {
      if (this.getLiteMenu(player, playHandler) != null) {
         player.closeContainer();
      }
   }

   private int[] getAnarchyMenuSlots(int anarchy) {
      if (anarchy >= 18 && anarchy <= 38) {
         return new int[]{1, 18};
      } else if (anarchy >= 39 && anarchy <= 57) {
         return new int[]{2, 39};
      } else {
         return anarchy >= 58 && anarchy <= 74 ? new int[]{3, 58} : new int[]{0, 1};
      }
   }
}
