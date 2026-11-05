package org.zenith.utility.network;

import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.network.protocol.game.ServerboundPunchPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.Item;
import org.zenith.MinecraftAccess;
import org.zenith.utility.misc.PacketUtils;

@Deprecated
public class Util1 implements MinecraftAccess {
   public static int method688(Item item) {
      return method691(item, 0, 35);
   }

   public static int method689(Item item) {
      return method691(item, 0, 8);
   }

   public static int method690(Item item) {
      return method691(item, 9, 35);
   }

   public static int method691(Item item, int n, int n2) {
      for (int i = n2; i >= n; i--) {
         if (mc.player.getInventory().getItem(i).getItem() == item) {
            return i;
         }
      }

      return -1;
   }

   public static int method692(int n, int n2) {
      for (int i = n2; i >= n; i--) {
         if (mc.player.getInventory().getItem(i).isEmpty()) {
            return i;
         }
      }

      return -1;
   }

   public static void method693(Util1.Type2 type2, int selected, int slotNum) {
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
               method695(selected, slotNum);
         }
      }
   }

   public static void method694(Util1.Type2 type2, int selected, int n) {
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
               method695(selected, n);
         }
      }
   }

   public static void method695(int selected, int slotNum) {
      if (selected != -1 && slotNum != -1) {
         mc.gameMode.handleContainerInput(mc.player.inventoryMenu.containerId, selected, 0, ContainerInput.PICKUP, mc.player);
         mc.gameMode.handleContainerInput(mc.player.inventoryMenu.containerId, slotNum, 0, ContainerInput.PICKUP, mc.player);
         mc.gameMode.handleContainerInput(mc.player.inventoryMenu.containerId, selected, 0, ContainerInput.PICKUP, mc.player);
      }
   }

   public static void method696(int n, int n2) {
      if (n != -1 && n2 != -1) {
         mc.gameMode.handleContainerInput(mc.player.inventoryMenu.containerId, method698(n), 0, ContainerInput.PICKUP, mc.player);
         mc.gameMode.handleContainerInput(mc.player.inventoryMenu.containerId, method698(n2), 0, ContainerInput.PICKUP, mc.player);
         mc.gameMode.handleContainerInput(mc.player.inventoryMenu.containerId, method698(n), 0, ContainerInput.PICKUP, mc.player);
      }
   }

   public static void method697(int n, int n2) {
      if (n != -1 && n2 != -1) {
         method696(n, n2);
      }
   }

   public static int method698(int n) {
      return n >= 0 && n <= 8 ? 36 + n : n;
   }

   public static void method699(Util1.Type type) {
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

   public static enum Type {
      MAINHAND,
      OFFHAND,
      PACKET,
      NONE;
   }

   public static enum Type2 {
      NORMAL,
      SILENT,
      ALTERNATIVE,
      NONE;
   }
}
