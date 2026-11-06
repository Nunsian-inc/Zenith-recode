package org.zenith.client.modules.impl.misc;

import com.darkmagician6.eventapi.EventTarget;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentSkipListMap;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.BrandPayload;
import net.minecraft.network.protocol.game.ClientboundCommandsPacket;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.zenith.ChatLogger;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.base.Module;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.settings.BooleanSetting;

@ModuleInfo(
   name = "ServerProbe",
   category = ModuleCategory.MISC,
   description = "module.serverprobe.description"
)
public class ServerProbeModule extends Module {
   public static final ServerProbeModule INSTANCE = new ServerProbeModule();
   private final BooleanSetting commands = new BooleanSetting("module.serverProbe.commands", true);
   private final BooleanSetting tracking = new BooleanSetting("module.serverProbe.tracking", true);
   private final BooleanSetting teleports = new BooleanSetting("module.serverProbe.teleports", true);
   private final BooleanSetting sounds = new BooleanSetting("module.serverProbe.sounds", true);
   private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss.SSS");
   private final Map<String, ServerProbeModule.Entry> items = new HashMap<>();
   private final Map<String, int[]> items2 = new ConcurrentSkipListMap<>();
   private final Set<String> items3 = new TreeSet<>();
   private PrintWriter printWriter;
   private boolean flag;
   private double doubleValue;
   private double doubleValue2 = Double.MAX_VALUE;
   private double doubleValue3;
   private double doubleValue4;

   private ServerProbeModule() {
   }

   @Override
   public void onEnable() {
      this.items.clear();
      this.items2.clear();
      this.items3.clear();
      this.doubleValue = 0.0;
      this.doubleValue2 = Double.MAX_VALUE;
      this.doubleValue3 = 0.0;
      this.doubleValue4 = 0.0;
      this.flag = true;
      super.onEnable();
      this.method6966();
      this.line("=== ServerProbe start " + simpleDateFormat.format(new Date()) + " ===");
      this.line("server: " + this.method6965());
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.method6963();
      this.method6967();
   }

   @EventTarget
   public void onPacket(PacketEvent event) {
      if (event.isIncoming()) {
         Packet packet3 = event.getPacket();
         if (packet3 instanceof ClientboundCommandsPacket && this.commands.isEnabled()) {
            this.flag = true;
         } else if (packet3 instanceof ClientboundCustomPayloadPacket customPayloadPacket) {
            if (customPayloadPacket.payload() instanceof BrandPayload brandPayload) {
               this.line("brand: " + brandPayload.brand());
            } else {
               this.line("payload: " + customPayloadPacket.payload().type().id());
            }
         } else if (packet3 instanceof ClientboundTeleportEntityPacket teleportEntityPacket && this.teleports.isEnabled()) {
            this.method6961(teleportEntityPacket);
         } else if (packet3 instanceof ClientboundRemoveEntitiesPacket removeEntitiesPacket && this.tracking.isEnabled()) {
            IntListIterator intListIterator = removeEntitiesPacket.entityIds().iterator();

            while (intListIterator.hasNext()) {
               int id = (Integer)intListIterator.next();
               this.method6960(id);
            }
         } else if (packet3 instanceof ClientboundSoundPacket clientboundSoundPacket && this.sounds.isEnabled()) {
            Vec3 vec = new Vec3(clientboundSoundPacket.getX(), clientboundSoundPacket.getY(), clientboundSoundPacket.getZ());
            double d = this.method6964(vec);
            if (!(d < 0.0)) {
               this.doubleValue3 = Math.max(this.doubleValue3, d);
               String key = clientboundSoundPacket.getSound().unwrapKey().map(resourceKey -> resourceKey.identifier().toString()).orElse("unknown");
               int[] ints = this.items2.computeIfAbsent(key, str -> new int[]{0, 0});
               ints[0]++;
               ints[1] = Math.max(ints[1], (int)d);
            }
         } else {
            if (packet3 instanceof ClientboundLevelParticlesPacket levelParticlesPacket && this.sounds.isEnabled()) {
               double d = this.method6964(new Vec3(levelParticlesPacket.x(), levelParticlesPacket.y(), levelParticlesPacket.z()));
               if (d > 0.0) {
                  this.doubleValue4 = Math.max(this.doubleValue4, d);
               }
            }
         }
      }
   }

   @EventTarget
   public void onUpdate(UpdateEvent event) {
      if (this.flag) {
         this.flag = false;
         this.method6962();
      }

      if (this.tracking.isEnabled() && mc.level != null && mc.player != null) {
         for (Player player : mc.level.players()) {
            if (player != mc.player) {
               String name = player.getGameProfile().name();
               ServerProbeModule.Entry entry = this.items.computeIfAbsent(name, str -> {
                  ServerProbeModule.Entry var3x = new ServerProbeModule.Entry();
                  double var4x = mc.player.distanceTo(player);
                  this.doubleValue = Math.max(this.doubleValue, var4x);
                  this.line(String.format(Locale.ROOT, "spawn %s at %.1f блоков", str, var4x));
                  return var3x;
               });
               entry.vec3d = player.position();
               entry.longValue = System.currentTimeMillis();
            }
         }
      }
   }

   private void method6960(int id) {
      if (mc.level != null && mc.player != null) {
         if (mc.level.getEntity(id) instanceof Player entity && entity != mc.player) {
            double d = mc.player.distanceTo(entity);
            this.doubleValue2 = Math.min(this.doubleValue2, d);
            this.line(
               String.format(
                  Locale.ROOT,
                  "despawn %s at %.1f блоков, последняя точка %.0f %.0f %.0f",
                  entity.getGameProfile().name(),
                  d,
                  entity.getX(),
                  entity.getY(),
                  entity.getZ()
               )
            );
         }
      }
   }

   private void method6961(ClientboundTeleportEntityPacket teleportEntityPacket) {
      if (mc.level != null && mc.player != null) {
         Entity entity = mc.level.getEntity(teleportEntityPacket.id());
         if (entity instanceof Player player) {
            Vec3 position = teleportEntityPacket.change().position();
            double d = entity.position().distanceTo(position);
            if (!(d < 8.0)) {
               this.line(
                  String.format(
                     Locale.ROOT,
                     "teleport %s %.0f %.0f %.0f -> %.0f %.0f %.0f (%.0f блоков)",
                     player.getGameProfile().name(),
                     entity.getX(),
                     entity.getY(),
                     entity.getZ(),
                     position.x,
                     position.y,
                     position.z,
                     d
                  )
               );
            }
         }
      }
   }

   private void method6962() {
      if (mc.getConnection() != null) {
         RootCommandNode root = mc.getConnection().getCommands().getRoot();
         ArrayList list = new ArrayList();

         for (Object obj : (Iterable<?>)(Object)(root.getChildren())) {
            CommandNode commandNode = (CommandNode)obj;
            String name = commandNode.getName();
            list.add(name);
            int indexOf = name.indexOf(58);
            if (indexOf > 0) {
               this.items3.add(name.substring(0, indexOf));
            }
         }

         Collections.sort(list);
         this.line("commands (" + list.size() + "): " + String.join(", ", list));
         this.line("namespaces (" + this.items3.size() + "): " + String.join(", ", this.items3));
      }
   }

   private void method6963() {
      this.line("=== ServerProbe report ===");
      this.line(String.format(Locale.ROOT, "радиус появления игроков: до %.1f блоков", this.doubleValue));
      this.line(String.format(Locale.ROOT, "ближайшее исчезновение: %.1f блоков", this.doubleValue2 == Double.MAX_VALUE ? -1.0 : this.doubleValue2));
      this.line(String.format(Locale.ROOT, "звук слышен до %.1f блоков, частицы до %.1f", this.doubleValue3, this.doubleValue4));
      this.line("плагинные namespace: " + (this.items3.isEmpty() ? "нет" : String.join(", ", this.items3)));
      this.line("звуки по дальности:");
      var entries = new ArrayList<>(this.items2.entrySet());
      entries.sort((entry, var1x) -> Integer.compare(((int[])var1x.getValue())[1], ((int[])entry.getValue())[1]));

      for (int i = 0; i < Math.min(25, entries.size()); i++) {
         Map.Entry entry = (Map.Entry)entries.get(i);
         this.line("  " + (String)entry.getKey() + " x" + ((int[])entry.getValue())[0] + " до " + ((int[])entry.getValue())[1] + " блоков");
      }

      ChatLogger.method2019("ServerProbe: отчёт записан, namespace " + this.items3.size());
   }

   private double method6964(Vec3 vec) {
      return mc.player == null ? -1.0 : mc.player.position().distanceTo(vec);
   }

   private String method6965() {
      return mc.getCurrentServer() == null ? "singleplayer" : mc.getCurrentServer().ip;
   }

   private void method6966() {
      try {
         File file = new File(Minecraft.getInstance().gameDirectory, "zenith-itemdump");
         if (!file.exists() && !file.mkdirs()) {
            return;
         }

         File file2 = new File(file, "serverprobe-" + System.currentTimeMillis() + ".log");
         this.printWriter = new PrintWriter(new FileWriter(file2, true), true);
         ChatLogger.method2019("ServerProbe: лог " + file2.getAbsolutePath());
      } catch (Exception e) {
         this.printWriter = null;
      }
   }

   private void line(String x) {
      System.out.println("[ServerProbe] " + x);
      if (this.printWriter != null) {
         this.printWriter.println(x);
      }
   }

   private void method6967() {
      if (this.printWriter != null) {
         try {
            this.printWriter.close();
         } catch (Exception e) {
         }

         this.printWriter = null;
      }
   }

   private static final class Entry {
      public Vec3 vec3d;
      public long longValue;
   }
}
