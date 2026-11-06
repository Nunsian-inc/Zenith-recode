package org.zenith.client.modules.impl.misc;

import com.darkmagician6.eventapi.EventTarget;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket.Entry;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetObjectivePacket;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import net.minecraft.network.protocol.game.ClientboundSetScorePacket;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundTabListPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerScoreEntry;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import org.zenith.ChatLogger;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.base.Module;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.settings.BooleanSetting;

@ModuleInfo(
   name = "PacketAudit",
   category = ModuleCategory.MISC,
   description = "module.packetaudit.description"
)
public class PacketAuditModule extends Module {
   public static final PacketAuditModule INSTANCE = new PacketAuditModule();
   private final BooleanSetting census = new BooleanSetting("module.packetAudit.census", true);
   private final BooleanSetting details = new BooleanSetting("module.packetAudit.details", true);
   private final BooleanSetting raw = new BooleanSetting("module.packetAudit.raw", true);
   private final BooleanSetting outgoing = new BooleanSetting("module.packetAudit.outgoing", true);
   private final BooleanSetting snapshots = new BooleanSetting("module.packetAudit.snapshots", true);
   private static final int CONST_intValue = 40;
   private static final int CONST_intValue2 = 4;
   private static final int CONST_intValue3 = 400;
   private static final long CONST_longValue = 30000L;
   private static final Set<String> items = Set.of(
      "CommonPingS2CPacket", "KeepAliveS2CPacket", "WorldTimeUpdateS2CPacket", "ChunkDataS2CPacket", "LightUpdateS2CPacket", "ChunkDeltaUpdateS2CPacket"
   );
   private static final Pattern pattern = Pattern.compile("§.");
   private final Map<String, AtomicInteger> items2 = new ConcurrentHashMap<>();
   private final Map<String, Set<String>> items3 = new LinkedHashMap<>();
   private final Map<String, AtomicInteger> items4 = new ConcurrentHashMap<>();
   private final Map<String, AtomicInteger> items5 = new ConcurrentHashMap<>();
   private long lastSnapshot;
   private PrintWriter printWriter;
   private long longValue;

   private PacketAuditModule() {
   }

   @Override
   public void onEnable() {
      this.items2.clear();
      this.items3.clear();
      this.items4.clear();
      this.items5.clear();
      super.onEnable();
      this.longValue = System.currentTimeMillis();
      this.method252();
      this.line("=== PacketAudit start ===");
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.method251();
      this.method253();
   }

   @EventTarget
   public void onPacket(PacketEvent event) {
      Packet packet3 = event.getPacket();
      if (packet3 != null) {
         if (!event.isIncoming()) {
            if (this.outgoing.isEnabled()) {
               String simpleName = packet3.getClass().getSimpleName();
               this.items5.computeIfAbsent(simpleName, str -> new AtomicInteger()).incrementAndGet();
               this.method247("sent:" + simpleName, packet3);
            }
         } else {
            this.method247(packet3.getClass().getSimpleName(), packet3);
            if (this.census.isEnabled()) {
               this.items2.computeIfAbsent(packet3.getClass().getSimpleName(), str -> new AtomicInteger()).incrementAndGet();
            }

            if (this.details.isEnabled()) {
               if (packet3 instanceof ClientboundSetObjectivePacket setObjectivePacket) {
                  this.method249("scoreboard.objective", setObjectivePacket.getObjectiveName() + " = " + this.method250(setObjectivePacket.getDisplayName()));
               } else if (packet3 instanceof ClientboundSetScorePacket setScorePacket) {
                  this.method249(
                     "scoreboard.score",
                     setScorePacket.objectiveName()
                        + " "
                        + setScorePacket.owner()
                        + " = "
                        + setScorePacket.score()
                        + setScorePacket.display().map(var1x -> " [" + this.method250(var1x) + "]").orElse("")
                  );
               } else if (packet3 instanceof ClientboundSetPlayerTeamPacket setPlayerTeamPacket) {
                  this.method249("team", setPlayerTeamPacket.getName() + " " + setPlayerTeamPacket.getTeamAction() + " members=" + setPlayerTeamPacket.getPlayers());
               } else if (packet3 instanceof ClientboundTabListPacket tabListPacket) {
                  this.method249("tab.header", this.method250(tabListPacket.header()));
                  this.method249("tab.footer", this.method250(tabListPacket.footer()));
               } else if (packet3 instanceof ClientboundPlayerInfoUpdatePacket playerInfoUpdatePacket) {
                  for (Entry entry : playerInfoUpdatePacket.entries()) {
                     if (entry.displayName() != null) {
                        this.method249("tab.entry", this.method250(entry.displayName()) + " ping=" + entry.latency());
                     }
                  }
               } else if (packet3 instanceof ClientboundOpenScreenPacket openScreenPacket) {
                  this.method249("screen.open", this.method250(openScreenPacket.getTitle()) + " type=" + openScreenPacket.getType());
               } else if (packet3 instanceof ClientboundSetTitleTextPacket setTitleTextPacket) {
                  this.method249("title", this.method250(setTitleTextPacket.text()));
               } else if (packet3 instanceof ClientboundSetSubtitleTextPacket setSubtitleTextPacket) {
                  this.method249("subtitle", this.method250(setSubtitleTextPacket.text()));
               } else if (packet3 instanceof ClientboundSetActionBarTextPacket setActionBarTextPacket) {
                  this.method249("actionbar", this.method250(setActionBarTextPacket.text()));
               } else if (packet3 instanceof ClientboundCustomPayloadPacket customPayloadPacket) {
                  this.method249("payload", customPayloadPacket.payload().type().id().toString());
               }
            }
         }
      }
   }

   @EventTarget
   public void onUpdate(UpdateEvent event) {
      if (this.snapshots.isEnabled()) {
         long now = System.currentTimeMillis();
         if (now - this.lastSnapshot >= 30000L) {
            this.lastSnapshot = now;
            this.method246();
         }
      }
   }

   private void method246() {
      if (mc.getConnection() != null && mc.level != null) {
         this.line("--- snapshot ---");
         Collection<PlayerInfo> onlinePlayers = mc.getConnection().getOnlinePlayers();
         this.line("tab.size| " + onlinePlayers.size());

         for (PlayerInfo onlinePlayer : onlinePlayers) {
            PlayerTeam team = onlinePlayer.getTeam();
            this.line(
               "tab| "
                  + onlinePlayer.getProfile().name()
                  + " uuid="
                  + onlinePlayer.getProfile().id()
                  + " ping="
                  + onlinePlayer.getLatency()
                  + " mode="
                  + onlinePlayer.getGameMode()
                  + " team="
                  + (team == null ? "-" : team.getName())
                  + " display="
                  + this.method250(onlinePlayer.getTabListDisplayName())
            );
         }

         Scoreboard scoreboard = mc.level.getScoreboard();

         for (Objective objective : scoreboard.getObjectives()) {
            this.line("obj| " + objective.getName() + " crit=" + objective.getCriteria().getName() + " display=" + this.method250(objective.getDisplayName()));

            for (PlayerScoreEntry listPlayerScore : scoreboard.listPlayerScores(objective)) {
               this.line("score| " + objective.getName() + " " + listPlayerScore.owner() + " = " + listPlayerScore.value());
            }
         }

         for (PlayerTeam playerTeam : scoreboard.getPlayerTeams()) {
            this.line(
               "team.full| "
                  + playerTeam.getName()
                  + " prefix="
                  + this.method250(playerTeam.getPlayerPrefix())
                  + " suffix="
                  + this.method250(playerTeam.getPlayerSuffix())
                  + " color="
                  + playerTeam.getColor()
                  + " members="
                  + playerTeam.getPlayers()
            );
         }

         LinkedHashMap map = new LinkedHashMap();

         for (Entity entity : mc.level.entitiesForRendering()) {
            map.merge(entity.getType().toString(), 1, (a, b) -> (Integer)a + (Integer)b);
         }

         this.line("entities| " + map);
      }
   }

   private void method247(String simpleName, Packet<?> packet3) {
      if (this.raw.isEnabled()) {
         if (!items.contains(simpleName.replace("sent:", ""))) {
            AtomicInteger counter = this.items4.computeIfAbsent(simpleName, str -> new AtomicInteger());
            if (counter.incrementAndGet() <= 4) {
               this.line("raw| " + simpleName + " " + this.method248(packet3));
            }
         }
      }
   }

   private String method248(Object packet3) {
      StringBuilder builder = new StringBuilder();

      for (Class clazz = packet3.getClass(); clazz != null && clazz != Object.class; clazz = clazz.getSuperclass()) {
         for (Field declaredField : clazz.getDeclaredFields()) {
            if (!Modifier.isStatic(declaredField.getModifiers())) {
               try {
                  declaredField.setAccessible(true);
                  Object obj = declaredField.get(packet3);
                  String str = obj instanceof Component displayName ? this.method250(displayName) : String.valueOf(obj);
                  if (str.length() > 400) {
                     str = str.substring(0, 400) + "...";
                  }

                  builder.append(declaredField.getName()).append('=').append(str).append("; ");
               } catch (Throwable e) {
               }
            }
         }
      }

      return builder.toString();
   }

   private void method249(String key, String string) {
      if (string != null && !string.isBlank()) {
         var strings = this.items3.computeIfAbsent(key, str -> new HashSet<>());
         if (strings.size() < 40 && strings.add(string)) {
            this.line(key + "| " + string);
         }
      }
   }

   private String method250(Component displayName) {
      return displayName == null ? "" : pattern.matcher(displayName.getString()).replaceAll("").trim();
   }

   private void method251() {
      long l = Math.max(1L, (System.currentTimeMillis() - this.longValue) / 1000L);
      this.line("=== PacketAudit report (" + l + " c) ===");
      ArrayList<java.util.Map.Entry> entries = new ArrayList<>(this.items2.entrySet());
      entries.sort((entry, var1x) -> Integer.compare(((AtomicInteger)var1x.getValue()).get(), ((AtomicInteger)entry.getValue()).get()));

      for (java.util.Map.Entry entry : entries) {
         this.line(
            String.format("%-46s %6d  %.1f/с", entry.getKey(), ((AtomicInteger)entry.getValue()).get(), (double)((AtomicInteger)entry.getValue()).get() / l)
         );
      }

      if (!this.items5.isEmpty()) {
         this.line("--- исходящие ---");
         ArrayList<java.util.Map.Entry> entries2 = new ArrayList<>(this.items5.entrySet());
         entries2.sort((entry, var1x) -> Integer.compare(((AtomicInteger)var1x.getValue()).get(), ((AtomicInteger)entry.getValue()).get()));

         for (java.util.Map.Entry entry : entries2) {
            this.line(
               String.format("%-46s %6d  %.1f/s", entry.getKey(), ((AtomicInteger)entry.getValue()).get(), (double)((AtomicInteger)entry.getValue()).get() / l)
            );
         }
      }

      this.method246();
      ChatLogger.method2019("PacketAudit: типов " + this.items2.size() + ", отчёт записан");
   }

   private void method252() {
      try {
         File file = new File(Minecraft.getInstance().gameDirectory, "zenith-itemdump");
         if (!file.exists() && !file.mkdirs()) {
            return;
         }

         File file2 = new File(file, "packetaudit-" + System.currentTimeMillis() + ".log");
         this.printWriter = new PrintWriter(new FileWriter(file2, true), true);
         ChatLogger.method2019("PacketAudit: " + file2.getName());
      } catch (Exception e) {
         this.printWriter = null;
      }
   }

   private void line(String x) {
      if (this.printWriter != null) {
         this.printWriter.println(x);
      }
   }

   private void method253() {
      if (this.printWriter != null) {
         try {
            this.printWriter.close();
         } catch (Exception e) {
         }

         this.printWriter = null;
      }
   }
}
