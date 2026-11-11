package org.zenith.utility.render;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import java.math.BigDecimal;
import java.math.RoundingMode;
import lombok.Generated;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket.Action;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerScoreEntry;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import org.apache.commons.lang3.StringUtils;
import org.zenith.MinecraftAccess;
import org.zenith.PacketEvent;
import org.zenith.StopWatch;
import org.zenith.client.events.UpdateEvent;
import org.zenith.utility.misc.CriticalsState;
import org.zenith.utility.network.UseItemUtils;

public class ServerDetector implements MinecraftAccess {
   private final StopWatch stopWatch = new StopWatch();
   private String text = "Vanilla";
   private boolean flag;
   private int anarchy;
   private boolean flag2;
   private boolean flag3 = false;
   private final CriticalsState criticalsState = new CriticalsState();

   public ServerDetector() {
      EventManager.register(this);
   }

   @EventTarget
   public void method1623(UpdateEvent event) {
      this.anarchy = this.method1631();
      this.text = this.method1630();
      this.flag2 = this.method1637();
      if (this.method1636()) {
         this.stopWatch.reset();
      }
   }

   public float method1624() {
      return method1627(this.criticalsState.method1712());
   }

   public float method1625() {
      return this.method1624();
   }

   public float method1626() {
      return (float)(this.criticalsState.getDoubleValue() / 5.0E7);
   }

   public static float method1627(double d) {
      BigDecimal bigDecimal = new BigDecimal(d);
      bigDecimal = bigDecimal.setScale(2, RoundingMode.HALF_UP);
      return bigDecimal.floatValue();
   }

   @EventTarget
   public void method1628(PacketEvent event) {
      if (!event.isIncoming() || !(event.getPacket() instanceof ClientboundLoginPacket) && !(event.getPacket() instanceof ClientboundRespawnPacket)) {
         if (event.isIncoming() && event.getPacket() instanceof ClientboundSetTimePacket setTimePacket) {
            this.criticalsState.method1706(setTimePacket.gameTime(), System.nanoTime());
         }
      } else {
         this.criticalsState.reset();
      }
   }

   @EventTarget(0)
   public void onPacket(PacketEvent event) {
      if (event.isOutgoing() && event.getPacket() instanceof net.minecraft.network.protocol.game.ServerboundAttackPacket) {
         this.criticalsState.method1707(System.nanoTime());
      }

      if (event.getPacket() instanceof ServerboundPlayerCommandPacket playerCommandPacket) {
         if (playerCommandPacket.getAction().equals(Action.START_SPRINTING)) {
            this.flag = true;
         } else if (playerCommandPacket.getAction().equals(Action.STOP_SPRINTING)) {
            this.flag = false;
         }
      }
   }

   private String method1630() {
      if (!UseItemUtils.method6526()
         && mc.getConnection() != null
         && mc.getConnection().getServerData() != null
         && mc.getConnection().serverBrand() != null) {
         String lowerCase = mc.getConnection().getServerData().ip.toLowerCase();
         String brand = mc.getConnection().serverBrand().toLowerCase();
         if (brand.contains("botfilter")) {
            return "FunTime";
         } else if (lowerCase.contains("funtime") || lowerCase.contains("skytime") || lowerCase.contains("space-times") || lowerCase.contains("funsky")) {
            return "CopyTime";
         } else if (method1634(brand)) {
            return "HolyWorld";
         } else {
            return lowerCase.contains("reallyworld") ? "ReallyWorld" : "Vanilla";
         }
      } else {
         return "Vanilla";
      }
   }

   private int method1631() {
      Scoreboard scoreboard = mc.level.getScoreboard();
      Objective displayObjective = scoreboard.getDisplayObjective(DisplaySlot.SIDEBAR);
      String text = this.text;
      switch (text) {
         case "FunTime":
            if (displayObjective != null) {
               String[] strings = displayObjective.getDisplayName().getString().split("-");
               if (strings.length > 1) {
                  return method1632(strings[1]);
               }
            }
         default:
            return -1;
         case "HolyWorld":
            return method1633(mc.level);
      }
   }

   private static int method1632(String str) {
      String str2 = str.trim();
      if (str2.isEmpty()) {
         return -1;
      } else {
         for (int i = 0; i < str2.length(); i++) {
            if (!Character.isDigit(str2.charAt(i))) {
               return -1;
            }
         }

         try {
            return Integer.parseInt(str2);
         } catch (NumberFormatException e) {
            return -1;
         }
      }
   }

   public static int method1633(Level world) {
      if (world == null) {
         return -1;
      } else {
         Scoreboard scoreboard = world.getScoreboard();
         Objective displayObjective = scoreboard.getDisplayObjective(DisplaySlot.SIDEBAR);
         if (displayObjective == null) {
            return -1;
         } else {
            for (PlayerScoreEntry listPlayerScore : scoreboard.listPlayerScores(displayObjective)) {
               String string = PlayerTeam.formatNameForTeam(scoreboard.getPlayersTeam(listPlayerScore.owner()), listPlayerScore.ownerName()).getString();
               if (!string.isEmpty()) {
                  String substringBetween = StringUtils.substringBetween(string, "#", " -◆-");
                  if (substringBetween != null && !substringBetween.isEmpty()) {
                     try {
                        return Integer.parseInt(substringBetween);
                     } catch (NumberFormatException e) {
                     }
                  }
               }
            }

            return -1;
         }
      }
   }

   public static boolean method1634(String brand) {
      if (brand == null) {
         return false;
      } else {
         String lowerCase = brand.toLowerCase();
         return lowerCase.contains("holyworld") || lowerCase.contains("holywоrld") || lowerCase.contains("leaf") || lowerCase.contains("vk.com/idwok");
      }
   }

   public boolean method1635() {
      return !this.stopWatch.hasElapsed(250L);
   }

   private boolean method1636() {
      return mc.gui.hud
         .getBossOverlay()
         .events
         .values()
         .stream()
         .map(event -> event.getName().getString().toLowerCase())
         .anyMatch(str -> str.contains("pvp") || str.contains("пвп"));
   }

   private boolean method1637() {
      return mc.gui.hud
         .getBossOverlay()
         .events
         .values()
         .stream()
         .map(event -> event.getName().getString().toLowerCase())
         .anyMatch(str -> (str.contains("pvp") || str.contains("пвп")) && (str.contains("0") || str.contains("1")));
   }

   public String method1638() {
      return mc.level.dimension().identifier().getPath();
   }

   public boolean method1639() {
      return this.text.equals("CopyTime") || this.text.equals("SpookyTime") || this.text.equals("FunTime");
   }

   public boolean method1640() {
      return this.text.equals("FunTime");
   }

   public boolean method1641() {
      return this.text.equals("ReallyWorld");
   }

   public boolean method1642() {
      return this.text.equals("HolyWorld");
   }

   public boolean method1643() {
      return this.text.equals("Vanilla");
   }

   @Generated
   public StopWatch getStopWatch() {
      return this.stopWatch;
   }

   @Generated
   public String getText() {
      return this.text;
   }

   @Generated
   public boolean isFlag() {
      return this.flag;
   }

   @Generated
   public int getAnarchy() {
      return this.anarchy;
   }

   @Generated
   public boolean isFlag2() {
      return this.flag2;
   }

   @Generated
   public boolean isFlag3() {
      return this.flag3;
   }

   @Generated
   public CriticalsState getCriticalsState() {
      return this.criticalsState;
   }
}
