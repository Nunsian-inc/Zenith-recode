package org.zenith.client.modules.impl.misc;

import com.darkmagician6.eventapi.EventTarget;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundKeepAlivePacket;
import net.minecraft.network.protocol.common.ClientboundPingPacket;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.network.protocol.game.ServerboundPunchPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemPacket;
import org.zenith.ChatLogger;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.base.Module;

@ModuleInfo(
   name = "AntiCheatProbe",
   category = ModuleCategory.MISC,
   description = "module.anticheatprobe.description"
)
public class AntiCheatProbeModule extends Module {
   public static final AntiCheatProbeModule INSTANCE = new AntiCheatProbeModule();
   private static final long CONST_longValue = 80L;
   private PrintWriter printWriter;
   private long longValue;
   private long longValue2;
   private int intValue;
   private long longValue3;
   private int intValue2;
   private int intValue3;
   private int intValue4;
   private long longValue4;
   private long longValue5;
   private final List<Double> items = new ArrayList<>();
   private final List<Long> items2 = new ArrayList<>();
   private final List<AntiCheatProbeModule.Entry> items3 = new ArrayList<>();

   private AntiCheatProbeModule() {
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.longValue = System.currentTimeMillis();
      this.longValue2 = 0L;
      this.intValue = 0;
      this.longValue3 = 0L;
      this.intValue2 = 0;
      this.intValue3 = 0;
      this.intValue4 = 0;
      this.longValue4 = 0L;
      this.longValue5 = 0L;
      this.items.clear();
      this.items3.clear();
      this.items2.clear();
      this.method6980();
      this.line("=== AntiCheatProbe start " + System.currentTimeMillis() + " ===");
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.method6977();
      this.method6981();
   }

   @EventTarget
   public void onPacket(PacketEvent event) {
      Packet packet3 = event.getPacket();
      if (packet3 != null) {
         long now = System.currentTimeMillis();
         long l = now - this.longValue;
         if (!event.isIncoming()) {
            String text = this.method6975(packet3);
            if (text != null) {
               this.items3.add(new AntiCheatProbeModule.Entry(text, now));
            }
         } else {
            if (packet3 instanceof ClientboundPingPacket
               || packet3 instanceof ClientboundKeepAlivePacket
               || packet3 instanceof ClientboundPlayerPositionPacket
               || packet3 instanceof ClientboundEntityEventPacket
               || packet3 instanceof ClientboundSetTimePacket
               || packet3 instanceof ClientboundSystemChatPacket) {
               mc.execute(() -> this.method6974(packet3, now, l));
            }
         }
      }
   }

   private void method6974(Packet<?> packet3, long now, long l) {
      if (this.isEnabled()) {
         if (packet3 instanceof ClientboundPingPacket clientboundPingPacket) {
            long l2 = this.longValue2 == 0L ? 0L : now - this.longValue2;
            this.longValue2 = now;
            this.intValue++;
            this.items2.add(now);
            long l3 = clientboundPingPacket.getId() & 4294967295L;
            if (this.longValue3 != 0L && Math.abs(l3 - this.longValue3) > 4L) {
               this.intValue2++;
            }

            this.longValue3 = l3;
            if (l2 < 20L || l2 > 85L) {
               this.line(String.format("%6d PING id=%d gap=%dms", l, l3, l2));
            }
         } else if (packet3 instanceof ClientboundKeepAlivePacket) {
            this.intValue4++;
            this.line(l + " KEEPALIVE");
         } else if (packet3 instanceof ClientboundPlayerPositionPacket playerPositionPacket) {
            this.intValue3++;
            this.line(
               String.format(
                  "%6d CORRECTION id=%d pos=%.1f,%.1f,%.1f",
                  l,
                  playerPositionPacket.id(),
                  playerPositionPacket.change().position().x,
                  playerPositionPacket.change().position().y,
                  playerPositionPacket.change().position().z
               )
            );
         } else if (packet3 instanceof ClientboundEntityEventPacket entityEventPacket && mc.level != null && entityEventPacket.getEntity(mc.level) == mc.player) {
            this.line(l + " SELF_STATUS code=" + entityEventPacket.getEventId() + " (" + this.method6978(entityEventPacket.getEventId()) + ")");
         } else if (packet3 instanceof ClientboundSetTimePacket setTimePacket) {
            long gameTime = setTimePacket.gameTime();
            if (this.longValue4 != 0L) {
               long l2 = now - this.longValue4;
               long l3 = gameTime - this.longValue5;
               if (l3 > 0L && l2 > 0L) {
                  double d = l3 / (l2 / 1000.0);
                  if (d > 0.0 && d < 40.0) {
                     this.items.add(d);
                  }
               }
            }

            this.longValue4 = now;
            this.longValue5 = gameTime;
         } else if (packet3 instanceof ClientboundSystemChatPacket systemChatPacket) {
            String lowerCase = systemChatPacket.content().getString().replaceAll("§.", "").toLowerCase(Locale.ROOT);
            if (lowerCase.contains("бан")
               || lowerCase.contains("ban")
               || lowerCase.contains("подозр")
               || lowerCase.contains("чит")
               || lowerCase.contains("kick")
               || lowerCase.contains("флаг")) {
               this.line(l + " FLAGMSG| " + systemChatPacket.content().getString().replaceAll("§.", "").trim());
            }
         }
      }
   }

   private String method6975(Packet<?> packet3) {
      if (packet3 instanceof ServerboundInteractPacket || packet3 instanceof net.minecraft.network.protocol.game.ServerboundAttackPacket) {
         return "ATTACK/USE_ENTITY";
      } else if (packet3 instanceof ServerboundPunchPacket) {
         return "SWING";
      } else if (packet3 instanceof ServerboundUseItemOnPacket) {
         return "PLACE/USE_BLOCK";
      } else if (packet3 instanceof ServerboundUseItemPacket) {
         return "USE_ITEM";
      } else if (packet3 instanceof ServerboundContainerClickPacket) {
         return "INV_CLICK";
      } else if (packet3 instanceof ServerboundContainerClosePacket) {
         return "INV_CLOSE";
      } else if (packet3 instanceof ServerboundSetCarriedItemPacket) {
         return "HOTBAR";
      } else if (packet3 instanceof ServerboundPlayerActionPacket playerActionPacket) {
         return switch (playerActionPacket.getAction()) {
            case START_DESTROY_BLOCK, STOP_DESTROY_BLOCK, ABORT_DESTROY_BLOCK -> "DIG";
            case DROP_ITEM, DROP_ALL_ITEMS -> "DROP";
            case SWAP_ITEM_WITH_OFFHAND -> "SWAP_HAND";
            case RELEASE_USE_ITEM -> "RELEASE_USE";
            default -> "ACTION";
         };
      } else {
         return null;
      }
   }

   private int method6976(long longValue) {
      int binarySearch = Collections.binarySearch(this.items2, longValue);
      if (binarySearch < 0) {
         binarySearch = -binarySearch - 1;
      }

      int n = 0;

      for (int i = binarySearch; i < this.items2.size(); i++) {
         long l = this.items2.get(i);
         if (l > longValue + 80L) {
            break;
         }

         n++;
      }

      return n;
   }

   private void method6977() {
      long l = Math.max(1L, (System.currentTimeMillis() - this.longValue) / 1000L);
      this.line("=== summary (" + l + "s) ===");
      this.line("pings: " + this.intValue + " (" + this.method6979((double)this.intValue / l) + "/s)");
      this.line("keepAlives: " + this.intValue4);
      this.line("corrections(rubber-band): " + this.intValue3);
      this.line("ping-id resets: " + this.intValue2);
      if (!this.items.isEmpty()) {
         double d = 0.0;
         double d2 = 99.0;
         double d3 = 0.0;

         for (double d4 : this.items) {
            d += d4;
            d2 = Math.min(d2, d4);
            d3 = Math.max(d3, d4);
         }

         this.line(
            "server TPS: avg "
               + this.method6979(d / this.items.size())
               + " min "
               + this.method6979(d2)
               + " max "
               + this.method6979(d3)
               + " (samples "
               + this.items.size()
               + ")"
         );
      }

      LinkedHashMap map = new LinkedHashMap();

      for (AntiCheatProbeModule.Entry entry : this.items3) {
         int[] ints = (int[])map.computeIfAbsent(entry.text, obj -> new int[2]);
         ints[0]++;
         ints[1] += this.method6976(entry.longValue);
      }

      this.line("--- бракетирование: пингов в 80мс после действия ---");

      for (Object obj : (Iterable<?>)(Object)(map.entrySet())) {
         Map.Entry entry = (Map.Entry)obj;
         int n = ((int[])entry.getValue())[0];
         double d = n == 0 ? 0.0 : (double)((int[])entry.getValue())[1] / n;
         this.line(String.format(Locale.ROOT, "  %-18s n=%-4d pings=%.2f", entry.getKey(), n, d));
      }

      this.line("(фон ~1.3-1.6 пинга/80мс; заметно выше = действие тик-пинится)");
      ChatLogger.method2019("AntiCheatProbe: pings " + this.intValue + ", действий " + this.items3.size() + ", TPS замеров " + this.items.size());
   }

   private String method6978(int eventId) {
      return switch (eventId) {
         case 2, 33 -> "hurt";
         case 3 -> "death";
         case 9 -> "eat_done";
         case 29 -> "shield_disable";
         case 30 -> "shield_block";
         case 35 -> "totem";
         default -> "?";
      };
   }

   private String method6979(double d) {
      return String.format(Locale.ROOT, "%.2f", d);
   }

   private void method6980() {
      try {
         File file = new File(Minecraft.getInstance().gameDirectory, "zenith-itemdump");
         if (!file.exists() && !file.mkdirs()) {
            return;
         }

         File file2 = new File(file, "anticheat-" + System.currentTimeMillis() + ".log");
         this.printWriter = new PrintWriter(new FileWriter(file2, true), true);
         ChatLogger.method2019("AntiCheatProbe: " + file2.getName());
      } catch (Exception e) {
         this.printWriter = null;
      }
   }

   private void line(String x) {
      if (this.printWriter != null) {
         this.printWriter.println(x);
      }
   }

   private void method6981() {
      if (this.printWriter != null) {
         try {
            this.printWriter.close();
         } catch (Exception e) {
         }

         this.printWriter = null;
      }
   }

   private static final class Entry {
      public final String text;
      public final long longValue;

      public Entry(String text, long longValue) {
         this.text = text;
         this.longValue = longValue;
      }
   }
}
