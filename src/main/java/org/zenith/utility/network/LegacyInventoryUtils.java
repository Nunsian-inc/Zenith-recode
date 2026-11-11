package org.zenith.utility.network;

import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.network.protocol.game.ServerboundPunchPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.Item;
import org.zenith.MinecraftAccess;
import org.zenith.utility.misc.PacketUtils;

@Deprecated
public class LegacyInventoryUtils implements MinecraftAccess {
   public static int findItemSlot(Item item) {
      return findItemSlotInRange(item, 0, 35);
   }

   public static int findHotbarItemSlot(Item item) {
      return findItemSlotInRange(item, 0, 8);
   }

   public static int findInventoryItemSlot(Item item) {
      return findItemSlotInRange(item, 9, 35);
   }

   public static int findItemSlotInRange(Item item, int n, int n2) {
      for (int i = n2; i >= n; i--) {
         if (mc.player.getInventory().getItem(i).getItem() == item) {
            return i;
         }
      }

      return -1;
   }

   public static int findEmptySlotInRange(int n, int n2) {
      for (int i = n2; i >= n; i--) {
         if (mc.player.getInventory().getItem(i).isEmpty()) {
            return i;
         }
      }

      return -1;
   }

   public static void selectSlot(LegacyInventoryUtils.SlotSwapMode type2, int selected, int slotNum) {
      if (selected != -1 && slotNum != -1) {
         switch (type2) {
            case NORMAL:
               mc.player.getInventory().setSelectedSlot(selected);
               break;
            case SILENT:
               mc.player.getInventory().setSelectedSlot(selected);
               PacketUtils.sendPacket(new ServerboundSetCarriedItemPacket(selected));
               break;
            case ALTERNATIVE:
               swapContainerSlots(selected, slotNum);
         }
      }
   }

   public static void restoreSlot(LegacyInventoryUtils.SlotSwapMode type2, int selected, int n) {
      if (selected != -1 && n != -1) {
         switch (type2) {
            case NORMAL:
               mc.player.getInventory().setSelectedSlot(n);
               break;
            case SILENT:
               mc.player.getInventory().setSelectedSlot(n);
               PacketUtils.sendPacket(new ServerboundSetCarriedItemPacket(n));
               break;
            case ALTERNATIVE:
               swapContainerSlots(selected, n);
         }
      }
   }

   public static void swapContainerSlots(int selected, int slotNum) {
      if (selected != -1 && slotNum != -1) {
         mc.gameMode.handleContainerInput(mc.player.inventoryMenu.containerId, selected, 0, ContainerInput.PICKUP, mc.player);
         mc.gameMode.handleContainerInput(mc.player.inventoryMenu.containerId, slotNum, 0, ContainerInput.PICKUP, mc.player);
         mc.gameMode.handleContainerInput(mc.player.inventoryMenu.containerId, selected, 0, ContainerInput.PICKUP, mc.player);
      }
   }

   public static void swapInventorySlots(int n, int n2) {
      if (n != -1 && n2 != -1) {
         mc.gameMode.handleContainerInput(mc.player.inventoryMenu.containerId, toContainerSlot(n), 0, ContainerInput.PICKUP, mc.player);
         mc.gameMode.handleContainerInput(mc.player.inventoryMenu.containerId, toContainerSlot(n2), 0, ContainerInput.PICKUP, mc.player);
         mc.gameMode.handleContainerInput(mc.player.inventoryMenu.containerId, toContainerSlot(n), 0, ContainerInput.PICKUP, mc.player);
      }
   }

   public static void swapSlots(int n, int n2) {
      if (n != -1 && n2 != -1) {
         swapInventorySlots(n, n2);
      }
   }

   public static int toContainerSlot(int n) {
      return n >= 0 && n <= 8 ? 36 + n : n;
   }

   public static void swing(LegacyInventoryUtils.SwingMode type) {
      switch (type) {
         case MAINHAND:
            org.zenith.utility.game.SwingCompat.swing(mc.player, InteractionHand.MAIN_HAND);
            break;
         case OFFHAND:
            org.zenith.utility.game.SwingCompat.swing(mc.player, InteractionHand.OFF_HAND);
            break;
         case PACKET:
            PacketUtils.sendPacket(ServerboundPunchPacket.INSTANCE);
      }
   }

   public static enum SwingMode {
      MAINHAND,
      OFFHAND,
      PACKET,
      NONE;
   }

   public static enum SlotSwapMode {
      NORMAL,
      SILENT,
      ALTERNATIVE,
      NONE;
   }
}
