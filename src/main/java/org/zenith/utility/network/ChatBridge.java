package org.zenith.utility.network;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundDisguisedChatPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerChatPacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;
import org.zenith.MinecraftAccess;
import org.zenith.PacketEvent;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.client.events.UpdateEvent;

public final class ChatBridge implements MinecraftAccess {
   private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
   private static final Pattern pattern = Pattern.compile("§.");
   private static final long CONST_longValue = 200L;
   private static final long CONST_longValue2 = 200L;
   private static final long CONST_longValue3 = 33554432L;
   private final File file;
   private final File file2;
   private final File file3;
   private PrintWriter printWriter;
   private int intValue;
   private long lastPoll;
   private long longValue;
   private boolean dirty;
   private long longValue2;
   private long longValue3;

   public ChatBridge() {
      this.file = new File(Zenith.file, "bridge");
      this.file2 = new File(this.file, "in.txt");
      this.file3 = new File(this.file, "out.log");
      this.longValue2 = -1L;
      this.longValue3 = -1L;
      if (!this.file.exists()) {
         this.file.mkdirs();
      }

      this.method509();
      this.intValue = this.method498();
      this.method508();
      EventManager.register(this);
      this.write("bridge ready");
      this.flush();
      Runtime.getRuntime().addShutdownHook(new Thread(this::flush, "zenith-bridge-flush"));
   }

   @EventTarget
   public void onUpdate(UpdateEvent event) {
      long now = System.currentTimeMillis();
      if (now - this.lastPoll >= 200L) {
         this.lastPoll = now;
         this.method499();
         this.flush();
      }
   }

   @EventTarget
   public void onPacket(PacketEvent event) {
      if (event.isIncoming()) {
         Packet packet3 = event.getPacket();
         Component content = null;
         if (packet3 instanceof ClientboundSystemChatPacket systemChatPacket) {
            content = systemChatPacket.content();
         } else if (packet3 instanceof ClientboundDisguisedChatPacket disguisedChatPacket) {
            content = disguisedChatPacket.message();
         } else if (packet3 instanceof ClientboundPlayerChatPacket playerChatPacket) {
            content = playerChatPacket.unsignedContent().orElse(null);
         }

         if (content != null) {
            String str = pattern.matcher(content.getString()).replaceAll("").trim();
            if (!str.isEmpty()) {
               this.write("chat| " + str);
            }
         }
      }
   }

   private int method498() {
      if (!this.file2.exists()) {
         return 0;
      } else {
         try {
            return Files.readAllLines(this.file2.toPath(), StandardCharsets.UTF_8).size();
         } catch (Exception e) {
            return 0;
         }
      }
   }

   private void method499() {
      if (this.file2.exists()) {
         long length = this.file2.length();
         long lastModified = this.file2.lastModified();
         if (length != this.longValue2 || lastModified != this.longValue3) {
            List allLines;
            try {
               allLines = Files.readAllLines(this.file2.toPath(), StandardCharsets.UTF_8);
            } catch (Exception e) {
               return;
            }

            this.longValue2 = length;
            this.longValue3 = lastModified;
            if (allLines.size() <= this.intValue) {
               if (allLines.size() < this.intValue) {
                  this.intValue = 0;
                  this.longValue2 = -1L;
               }
            } else {
               for (int i = this.intValue; i < allLines.size(); i++) {
                  String str = ((String)allLines.get(i)).trim();
                  this.intValue = i + 1;
                  if (!str.isEmpty() && !str.startsWith("#")) {
                     try {
                        this.method500(str);
                     } catch (Exception e) {
                        this.write("error| " + str + " -> " + e);
                     }
                  }
               }
            }
         }
      }
   }

   private void method500(String str) {
      String[] strings = str.split("\\s+", 2);
      String lowerCase = strings[0].toLowerCase(Locale.ROOT);
      String content = strings.length > 1 ? strings[1] : "";
      this.write("> " + str);
      switch (lowerCase) {
         case "cmd":
            if (mc.getConnection() == null) {
               return;
            }

            mc.getConnection().sendCommand(content.startsWith("/") ? content.substring(1) : content);
            break;
         case "chat":
            if (mc.getConnection() == null) {
               return;
            }

            mc.getConnection().sendChat(content);
            break;
         case "module":
            this.method501(content);
            break;
         case "pos":
            this.method502();
            break;
         case "players":
            this.method503();
            break;
         case "screen":
            this.method504();
            break;
         case "click":
            this.method506(content);
            break;
         case "close":
            if (mc.player != null) {
               mc.player.closeContainer();
            }
            break;
         case "names":
            this.write("names| " + Names.size());
            break;
         case "target":
            this.method505();
            break;
         default:
            this.write("unknown| " + lowerCase);
      }
   }

   private void method501(String content) {
      String[] strings = content.split("\\s+");
      if (strings.length != 0 && !strings[0].isEmpty()) {
         Module moduleValue = Zenith.getInstance().getModuleManager().findByName(strings[0]);
         if (moduleValue == null) {
            this.write("module| не найден " + strings[0]);
         } else {
            boolean equalsIgnoreCase = strings.length < 2 ? !moduleValue.isEnabled() : strings[1].equalsIgnoreCase("on");
            moduleValue.setToggled(equalsIgnoreCase);
            this.write("module| " + moduleValue.getName() + " = " + moduleValue.isEnabled());
         }
      }
   }

   private void method502() {
      if (mc.player != null && mc.level != null) {
         this.write(
            String.format(
               Locale.ROOT,
               "pos| %.1f %.1f %.1f dim=%s server=%s",
               mc.player.getX(),
               mc.player.getY(),
               mc.player.getZ(),
               mc.level.dimension().identifier(),
               mc.getCurrentServer() == null ? "-" : mc.getCurrentServer().ip
            )
         );
      } else {
         this.write("pos| нет мира");
      }
   }

   private void method503() {
      if (mc.level != null && mc.player != null) {
         for (Player player : mc.level.players()) {
            if (player != mc.player) {
               this.write(
                  String.format(
                     Locale.ROOT,
                     "player| %s %.0f %.0f %.0f dist=%.1f hand=%s",
                     player.getGameProfile().name(),
                     player.getX(),
                     player.getY(),
                     player.getZ(),
                     mc.player.distanceTo(player),
                     this.method507(player.getMainHandItem())
                  )
               );
            }
         }
      } else {
         this.write("players| нет мира");
      }
   }

   private void method504() {
      if (mc.player != null && mc.player.containerMenu != null) {
         AbstractContainerMenu containerMenu = mc.player.containerMenu;
         this.write("screen| syncId=" + containerMenu.containerId + " slots=" + containerMenu.slots.size());
         int n = Math.max(0, containerMenu.slots.size() - 36);

         for (int i = 0; i < n; i++) {
            ItemStack mainHandItem = containerMenu.getSlot(i).getItem();
            if (!mainHandItem.isEmpty()) {
               this.write("slot| " + i + " " + this.method507(mainHandItem));
            }
         }
      } else {
         this.write("screen| нет окна");
      }
   }

   private void method505() {
      if (mc.crosshairPickEntity instanceof Player player) {
         this.write(
            "target| " + player.getGameProfile().name() + " main=" + this.method507(player.getMainHandItem()) + " off=" + this.method507(player.getOffhandItem())
         );
      } else {
         this.write("target| нет цели");
      }
   }

   private void method506(String content) {
      if (mc.player != null && mc.gameMode != null && mc.player.containerMenu != null) {
         String[] strings = content.split("\\s+");
         int slotNum = Integer.parseInt(strings[0]);
         int buttonNum = strings.length > 1 ? Integer.parseInt(strings[1]) : 0;
         mc.gameMode.handleContainerInput(mc.player.containerMenu.containerId, slotNum, buttonNum, ContainerInput.PICKUP, mc.player);
         this.write("click| " + slotNum + " button=" + buttonNum);
      }
   }

   private String method507(ItemStack mainHandItem) {
      if (mainHandItem != null && !mainHandItem.isEmpty()) {
         StringBuilder builder = new StringBuilder(mainHandItem.getItem().toString());
         builder.append(" x").append(mainHandItem.getCount());
         String str = pattern.matcher(mainHandItem.getHoverName().getString()).replaceAll("");
         builder.append(" \"").append(str).append('"');
         var elements = Names.method4519(mainHandItem);
         if (!elements.isEmpty()) {
            builder.append(" ench=").append(String.join(",", elements));
         }

         ItemLore itemLore = mainHandItem.get(DataComponents.LORE);
         if (itemLore != null && !itemLore.lines().isEmpty()) {
            builder.append(" lore=").append(itemLore.lines().size());
         }

         return builder.toString();
      } else {
         return "-";
      }
   }

   private synchronized void write(String str) {
      if (this.printWriter == null) {
         this.method508();
      }

      if (this.printWriter != null) {
         this.printWriter.println("[" + dateTimeFormatter.format(LocalTime.now()) + "] " + str);
         this.dirty = true;
         if (System.currentTimeMillis() - this.longValue >= 200L) {
            this.flush();
         }
      }
   }

   private synchronized void flush() {
      this.longValue = System.currentTimeMillis();
      if (this.dirty && this.printWriter != null) {
         this.dirty = false;
         this.printWriter.flush();
      }
   }

   private void method508() {
      try {
         this.printWriter = new PrintWriter(new BufferedWriter(new FileWriter(this.file3, true)), false);
      } catch (Exception e) {
         this.printWriter = null;
      }
   }

   private void method509() {
      try {
         if (this.file3.length() <= 33554432L) {
            return;
         }

         File file = new File(this.file, "out.old.log");
         Files.deleteIfExists(file.toPath());
         this.file3.renameTo(file);
      } catch (Exception e) {
      }
   }
}
