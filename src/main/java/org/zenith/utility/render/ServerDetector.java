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
   public void method1623(UpdateEvent var1) {
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

   public static float method1627(double var0) {
      BigDecimal var2 = new BigDecimal(var0);
      var2 = var2.setScale(2, RoundingMode.HALF_UP);
      return var2.floatValue();
   }

   @EventTarget
   public void method1628(PacketEvent var1) {
      if (!var1.method7100() || !(var1.getPacket3() instanceof ClientboundLoginPacket) && !(var1.getPacket3() instanceof ClientboundRespawnPacket)) {
         if (var1.method7100() && var1.getPacket3() instanceof ClientboundSetTimePacket var2) {
            this.criticalsState.method1706(var2.gameTime(), System.nanoTime());
         }
      } else {
         this.criticalsState.reset();
      }
   }

   @EventTarget(0)
   public void onPacket(PacketEvent var1) {
      if (var1.method7099() && var1.getPacket3() instanceof net.minecraft.network.protocol.game.ServerboundAttackPacket) {
         this.criticalsState.method1707(System.nanoTime());
      }

      if (var1.getPacket3() instanceof ServerboundPlayerCommandPacket var4) {
         if (var4.getAction().equals(Action.START_SPRINTING)) {
            this.flag = true;
         } else if (var4.getAction().equals(Action.STOP_SPRINTING)) {
            this.flag = false;
         }
      }
   }

   private String method1630() {
      if (!UseItemUtils.method6526()
         && mc.getConnection() != null
         && mc.getConnection().getServerData() != null
         && mc.getConnection().serverBrand() != null) {
         String var1 = mc.getConnection().getServerData().ip.toLowerCase();
         String var2 = mc.getConnection().serverBrand().toLowerCase();
         if (var2.contains("botfilter")) {
            return "FunTime";
         } else if (var1.contains("funtime") || var1.contains("skytime") || var1.contains("space-times") || var1.contains("funsky")) {
            return "CopyTime";
         } else if (method1634(var2)) {
            return "HolyWorld";
         } else {
            return var1.contains("reallyworld") ? "ReallyWorld" : "Vanilla";
         }
      } else {
         return "Vanilla";
      }
   }

   private int method1631() {
      Scoreboard var1 = mc.level.getScoreboard();
      Objective var2 = var1.getDisplayObjective(DisplaySlot.SIDEBAR);
      String var3 = this.text;
      switch (var3) {
         case "FunTime":
            if (var2 != null) {
               String[] var5 = var2.getDisplayName().getString().split("-");
               if (var5.length > 1) {
                  return method1632(var5[1]);
               }
            }
         default:
            return -1;
         case "HolyWorld":
            return method1633(mc.level);
      }
   }

   private static int method1632(String var0) {
      String var1 = var0.trim();
      if (var1.isEmpty()) {
         return -1;
      } else {
         for (int var2 = 0; var2 < var1.length(); var2++) {
            if (!Character.isDigit(var1.charAt(var2))) {
               return -1;
            }
         }

         try {
            return Integer.parseInt(var1);
         } catch (NumberFormatException var3) {
            return -1;
         }
      }
   }

   public static int method1633(Level var0) {
      if (var0 == null) {
         return -1;
      } else {
         Scoreboard var1 = var0.getScoreboard();
         Objective var2 = var1.getDisplayObjective(DisplaySlot.SIDEBAR);
         if (var2 == null) {
            return -1;
         } else {
            for (PlayerScoreEntry var4 : var1.listPlayerScores(var2)) {
               String var5 = PlayerTeam.formatNameForTeam(var1.getPlayersTeam(var4.owner()), var4.ownerName()).getString();
               if (!var5.isEmpty()) {
                  String var6 = StringUtils.substringBetween(var5, "#", " -◆-");
                  if (var6 != null && !var6.isEmpty()) {
                     try {
                        return Integer.parseInt(var6);
                     } catch (NumberFormatException var8) {
                     }
                  }
               }
            }

            return -1;
         }
      }
   }

   public static boolean method1634(String var0) {
      if (var0 == null) {
         return false;
      } else {
         String var1 = var0.toLowerCase();
         return var1.contains("holyworld") || var1.contains("holywоrld") || var1.contains("leaf") || var1.contains("vk.com/idwok");
      }
   }

   public boolean method1635() {
      return !this.stopWatch.method7546(250L);
   }

   private boolean method1636() {
      return mc.gui.hud
         .getBossOverlay()
         .events
         .values()
         .stream()
         .map(var0 -> var0.getName().getString().toLowerCase())
         .anyMatch(var0 -> var0.contains("pvp") || var0.contains("пвп"));
   }

   private boolean method1637() {
      return mc.gui.hud
         .getBossOverlay()
         .events
         .values()
         .stream()
         .map(var0 -> var0.getName().getString().toLowerCase())
         .anyMatch(var0 -> (var0.contains("pvp") || var0.contains("пвп")) && (var0.contains("0") || var0.contains("1")));
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
