package org.zenith.client.screens.nlgui.chat;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;
import org.zenith.Zenith;
import org.zenith.utility.api.Share;
import org.zenith.utility.misc.Coord;
import org.zenith.utility.misc.ServerNameUtils;

public final class ChatCompose {
   private static final int GLOBAL_BUDGET = 380;
   private static final int DIRECT_BUDGET = 860;
   private static final int NODE_BUDGET = 240;
   private static final int MAIN_SIZE = 36;
   private static final int ARMOR_FIRST = 36;
   private static final int OFFHAND_SLOT = 40;
   private static final EquipmentSlot[] ARMOR_SLOTS = new EquipmentSlot[]{EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD};

   private ChatCompose() {
   }

   private static Minecraft mc() {
      return Minecraft.getInstance();
   }

   public static String server() {
      Minecraft minecraft = mc();
      if (minecraft != null && minecraft.level != null) {
         ServerData currentServer = minecraft.getCurrentServer();
         if (currentServer != null && currentServer.isRealm()) {
            return "realms";
         } else if (currentServer != null && currentServer.ip != null && !currentServer.ip.isBlank()) {
            return ServerNameUtils.normalize(currentServer.ip);
         } else {
            return minecraft.isLocalServer() ? "singleplayer" : "";
         }
      } else {
         return "";
      }
   }

   public static String mode() {
      Minecraft minecraft = mc();
      if (minecraft != null && minecraft.level != null) {
         try {
            Scoreboard scoreboard = minecraft.level.getScoreboard();
            Objective displayObjective = scoreboard.getDisplayObjective(DisplaySlot.SIDEBAR);
            return displayObjective == null ? "" : ServerNameUtils.method5641(displayObjective.getDisplayName().getString());
         } catch (RuntimeException e) {
            return "";
         }
      } else {
         return "";
      }
   }

   public static String dimension() {
      Minecraft minecraft = mc();
      if (minecraft != null && minecraft.level != null) {
         String string = minecraft.level.dimension().identifier().toString();
         int indexOf = string.indexOf(58);
         return indexOf < 0 ? string : string.substring(indexOf + 1);
      } else {
         return "";
      }
   }

   public static String whereTag() {
      Zenith zenith = Zenith.getInstance();
      Share share = zenith == null ? null : zenith.getShare();
      if (share != null && !share.server.isEnabled()) {
         return "";
      } else {
         String str = server();
         return str.isEmpty() ? "" : Coord.method5171("at").method5177("s", str).method5177("m", mode()).method5177("d", dimension()).toString();
      }
   }

   public static boolean inWorld() {
      Minecraft minecraft = mc();
      return minecraft != null && minecraft.player != null && minecraft.level != null;
   }

   public static boolean hasItems() {
      Minecraft minecraft = mc();
      if (minecraft != null && minecraft.player != null) {
         Inventory inventory = minecraft.player.getInventory();

         for (int i = 0; i < inventory.getNonEquipmentItems().size() && i < 36; i++) {
            if (!inventory.getNonEquipmentItems().get(i).isEmpty()) {
               return true;
            }
         }

         for (EquipmentSlot slot : ARMOR_SLOTS) {
            if (!minecraft.player.getItemBySlot(slot).isEmpty()) {
               return true;
            }
         }

         return !minecraft.player.getItemBySlot(EquipmentSlot.OFFHAND).isEmpty();
      } else {
         return false;
      }
   }

   public static String coordsHere(String str) {
      Minecraft minecraft = mc();
      if (minecraft != null && minecraft.player != null) {
         BlockPos blockPosition = minecraft.player.blockPosition();
         return coords(blockPosition.getX(), blockPosition.getY(), blockPosition.getZ(), dimension(), str);
      } else {
         return "";
      }
   }

   public static String coords(int x, int y, int z, String dimension, String str) {
      return Coord.method5171("coord")
         .method5178("x", x)
         .method5178("y", y)
         .method5178("z", z)
         .method5177("d", dimension)
         .method5177("s", server())
         .method5177("n", str)
         .toString();
   }

   public static String inventory(boolean flag, boolean siteMode) {
      Minecraft minecraft = mc();
      if (minecraft != null && minecraft.player != null) {
         Inventory inventory = minecraft.player.getInventory();
         ArrayList arrayList = new ArrayList();

         for (int i = 0; i < inventory.getNonEquipmentItems().size() && i < 36; i++) {
            add(arrayList, i, inventory.getNonEquipmentItems().get(i));
         }

         for (int i = 0; i < ARMOR_SLOTS.length; i++) {
            add(arrayList, 36 + i, minecraft.player.getItemBySlot(ARMOR_SLOTS[i]));
         }

         ItemStack itemBySlot = minecraft.player.getItemBySlot(EquipmentSlot.OFFHAND);
         if (!itemBySlot.isEmpty()) {
            add(arrayList, 40, itemBySlot);
         }

         if (arrayList.isEmpty()) {
            return "";
         } else {
            int n = siteMode ? (flag ? 860 : 380) : 240;
            String str = pack(arrayList, n);
            return str == null ? "" : str;
         }
      } else {
         return "";
      }
   }

   private static void add(List<String> strings, int n, ItemStack itemBySlot) {
      if (itemBySlot != null && !itemBySlot.isEmpty()) {
         Identifier key = BuiltInRegistries.ITEM.getKey(itemBySlot.getItem());
         String path = key.getNamespace().equals("minecraft") ? key.getPath() : key.toString();
         StringBuilder builder = new StringBuilder().append(n).append(':').append(path).append(':').append(Math.max(1, Math.min(127, itemBySlot.getCount())));
         if (itemBySlot.hasFoil()) {
            builder.append(":g");
         }

         strings.add(builder.toString());
      }
   }

   private static String pack(List<String> strings, int n) {
      var elements = new ArrayList<>(strings);
      String dimension = server();

      while (true) {
         String string = Coord.method5171("inv").method5177("s", dimension).method5177("v", encode(String.join(";", elements))).toString();
         if (string.length() <= n) {
            return string;
         }

         int index = lastBackpack(elements);
         if (index < 0) {
            return null;
         }

         elements.remove(index);
      }
   }

   private static int lastBackpack(List<String> elements) {
      for (int i = elements.size() - 1; i >= 0; i--) {
         int n = slotOf((String)elements.get(i));
         if (n >= 9 && n < 36) {
            return i;
         }
      }

      for (int i = elements.size() - 1; i >= 0; i--) {
         int n = slotOf((String)elements.get(i));
         if (n >= 0 && n < 9) {
            return i;
         }
      }

      return -1;
   }

   private static int slotOf(String str) {
      int indexOf = str.indexOf(58);
      if (indexOf <= 0) {
         return -1;
      } else {
         try {
            return Integer.parseInt(str.substring(0, indexOf));
         } catch (NumberFormatException e) {
            return -1;
         }
      }
   }

   private static String encode(String str) {
      byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
      Deflater deflater = new Deflater(9);

      String str2;
      try {
         deflater.setInput(bytes);
         deflater.finish();
         byte[] output = new byte[Math.max(64, bytes.length)];
         int deflate = deflater.deflate(output);
         if (deflater.finished()) {
            byte[] src = new byte[deflate];
            System.arraycopy(output, 0, src, 0, deflate);
            return "1" + Base64.getUrlEncoder().withoutPadding().encodeToString(src);
         }

         str2 = "0" + Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
      } finally {
         deflater.end();
      }

      return str2;
   }

   private static String decode(String str) {
      if (str != null && str.length() >= 2) {
         char charAt = str.charAt(0);

         byte[] bytes;
         try {
            bytes = Base64.getUrlDecoder().decode(str.substring(1));
         } catch (IllegalArgumentException e) {
            return "";
         }

         if (charAt == '0') {
            return new String(bytes, StandardCharsets.UTF_8);
         } else {
            Inflater inflater = new Inflater();

            String str2;
            try {
               inflater.setInput(bytes);
               byte[] output = new byte[4096];
               int inflate = inflater.inflate(output);
               return new String(output, 0, inflate, StandardCharsets.UTF_8);
            } catch (DataFormatException e) {
               str2 = "";
            } finally {
               inflater.end();
            }

            return str2;
         }
      } else {
         return "";
      }
   }

   public static CloudInventoryData inventoryOf(Coord card) {
      if (card == null) {
         return null;
      } else {
         String str = decode(card.method5169("v", ""));
         if (str.isEmpty()) {
            return null;
         } else {
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();

            for (int i = 0; i < 36; i++) {
               arrayList.add(ItemStack.EMPTY);
            }

            for (int i = 0; i < 4; i++) {
               arrayList2.add(ItemStack.EMPTY);
            }

            ItemStack stack = ItemStack.EMPTY;

            for (String str2 : str.split(";")) {
               String[] original = str2.split(":");
               if (original.length >= 3) {
                  boolean flag = original.length > 3 && "g".equals(original[original.length - 1]);
                  int to = flag ? original.length - 2 : original.length - 1;
                  if (to >= 2) {
                     int index;
                     int n;
                     try {
                        index = Integer.parseInt(original[0]);
                        n = Integer.parseInt(original[to]);
                     } catch (NumberFormatException e) {
                        continue;
                     }

                     String str3 = String.join(":", Arrays.copyOfRange(original, 1, to));
                     Identifier tryParse = Identifier.tryParse(str3.indexOf(58) < 0 ? "minecraft:" + str3 : str3);
                     if (tryParse != null && BuiltInRegistries.ITEM.containsKey(tryParse)) {
                        Item item = BuiltInRegistries.ITEM.getValue(tryParse);
                        ItemStack stack2 = new ItemStack(item, Math.max(1, Math.min(127, n)));
                        if (flag) {
                           stack2.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
                        }

                        if (index >= 0 && index < 36) {
                           arrayList.set(index, stack2);
                        } else if (index >= 36 && index < 40) {
                           arrayList2.set(index - 36, stack2);
                        } else if (index == 40) {
                           stack = stack2;
                        }
                     }
                  }
               }
            }

            return new CloudInventoryData(arrayList, arrayList2, stack);
         }
      }
   }

   public static String dimensionLabel(String str) {
      if (str == null) {
         return "";
      } else {
         String lowerCase = str.toLowerCase(Locale.ROOT);

         return switch (lowerCase) {
            case "overworld", "minecraft:overworld" -> "Верхний мир";
            case "the_nether", "minecraft:the_nether" -> "Ад";
            case "the_end", "minecraft:the_end" -> "Край";
            default -> str;
         };
      }
   }
}
