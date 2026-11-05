package org.zenith.utility.misc;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import java.util.Locale;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.world.inventory.ContainerInput;
import org.zenith.PacketEvent;
import org.zenith.Zenith;
import org.zenith.ZenithAccess;
import org.zenith.client.events.UpdateEvent;
import org.zenith.utility.network.InventoryUtils;

public class HolyWorldServerTracker implements ZenithAccess {
   private static final int CONST_intValue = 1;
   private static final int CONST_intValue2 = 10;
   private static final int CONST_intValue3 = 19;
   private static final int CONST_intValue4 = 20;
   private static final int CONST_intValue5 = 29;
   private static final int CONST_intValue6 = 30;
   private static final int CONST_intValue7 = 36;
   private final org.zenith.StopWatch stopWatch = new org.zenith.StopWatch();
   private boolean lobby;
   private int anarchy;

   public HolyWorldServerTracker() {
      EventManager.register(this);
   }

   public boolean isActive() {
      return this.anarchy != 0;
   }

   public boolean isHolyWorldHere() {
      return Zenith.getInstance().getServerDetector().method1642();
   }

   public boolean method6149() {
      return Zenith.getInstance().getServerDetector().method1635();
   }

   public int currentAnarchyHere() {
      return Zenith.getInstance().getServerDetector().getAnarchy();
   }

   @EventTarget
   public void onPacket(PacketEvent event) {
      if (this.anarchy != 0 && event.method7100()) {
         if (event.getPacket3() instanceof ClientboundSystemChatPacket systemChatPacket) {
            String lowerCase = systemChatPacket.content().getString().toLowerCase(Locale.ROOT);
            if (!lowerCase.contains("хаб") && lowerCase.contains("не удалось")) {
               mc.execute(() -> {
                  if (this.anarchy != 0) {
                     HudHolder.method6969().method6972("0", Component.literal(" На данную анархию " + ChatFormatting.RED + "нельзя" + ChatFormatting.RESET + " зайти"));
                     this.anarchy = 0;
                     this.lobby = false;
                  }
               });
            }
         }
      }
   }

   @EventTarget
   public void method6150(UpdateEvent event) {
      if (this.anarchy != 0 && mc.player != null && mc.player.connection != null && mc.level != null) {
         if (this.isHolyWorldHere() && !this.method6149()) {
            int currentAnarchyHere = this.currentAnarchyHere();
            if (this.lobby) {
               if (currentAnarchyHere == -1) {
                  this.lobby = false;
                  this.stopWatch.reset();
               } else if (this.stopWatch.method7544(1500L)) {
                  mc.player.connection.sendCommand("hub");
               }
            } else if (currentAnarchyHere == this.anarchy) {
               this.anarchy = 0;
            } else if (mc.gui.screen() instanceof ContainerScreen containerScreen && containerScreen.getTitle().getString().toLowerCase(Locale.ROOT).contains("лайт анархии")
               )
             {
               if (System.currentTimeMillis() - ((Handler1)mc.gui.screen()).zenithDLC$callGetStartTime() > 4000L) {
                  mc.player.closeContainer();
               } else if (this.stopWatch.method7546(500L)) {
                  byte index = 0;
                  byte b = 1;
                  if (this.anarchy >= 10 && this.anarchy <= 19) {
                     index = 1;
                     b = 10;
                  } else if (this.anarchy >= 20 && this.anarchy <= 29) {
                     index = 2;
                     b = 20;
                  } else if (this.anarchy >= 30 && this.anarchy <= 36) {
                     index = 3;
                     b = 30;
                  }

                  if (containerScreen.getMenu().getContainer().getContainerSize() < 10) {
                     InventoryUtils.method904(index, 0, ContainerInput.PICKUP, false);
                  } else {
                     InventoryUtils.method904(18 + this.anarchy - b, 0, ContainerInput.PICKUP, false);
                     mc.player.closeContainer();
                  }
               }
            } else {
               if (this.stopWatch.method7544(3000L)) {
                  mc.player.connection.sendCommand("lite");
               }
            }
         } else {
            this.anarchy = 0;
            this.lobby = false;
         }
      }
   }

   public void reconnect(int anarchy) {
      if (anarchy >= 1 && anarchy <= 36) {
         this.anarchy = anarchy;
         this.lobby = true;
         this.stopWatch.reset();
      } else {
         HudHolder.method6969().method6972("[RCT]", Component.literal(" Не верный " + ChatFormatting.RED + "лайт"));
      }
   }
}
