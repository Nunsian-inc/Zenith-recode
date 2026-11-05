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
   public static int method688(Item var0) {
      return method691(var0, 0, 35);
   }

   public static int method689(Item var0) {
      return method691(var0, 0, 8);
   }

   public static int method690(Item var0) {
      return method691(var0, 9, 35);
   }

   public static int method691(Item var0, int var1, int var2) {
      for (int var3 = var2; var3 >= var1; var3--) {
         if (mc.player.getInventory().getItem(var3).getItem() == var0) {
            return var3;
         }
      }

      return -1;
   }

   public static int method692(int var0, int var1) {
      for (int var2 = var1; var2 >= var0; var2--) {
         if (mc.player.getInventory().getItem(var2).isEmpty()) {
            return var2;
         }
      }

      return -1;
   }

   public static void method693(Util1.Type2 var0, int var1, int var2) {
      if (var1 != -1 && var2 != -1) {
         switch (var0) {
            case NORMAL:
               mc.player.getInventory().setSelectedSlot(var1);
               break;
            case SILENT:
               mc.player.getInventory().setSelectedSlot(var1);
               PacketUtils.sendPacket(new ServerboundSetCarriedItemPacket(var1));
               break;
            case ALTERNATIVE:
               method695(var1, var2);
         }
      }
   }

   public static void method694(Util1.Type2 var0, int var1, int var2) {
      if (var1 != -1 && var2 != -1) {
         switch (var0) {
            case NORMAL:
               mc.player.getInventory().setSelectedSlot(var2);
               break;
            case SILENT:
               mc.player.getInventory().setSelectedSlot(var2);
               PacketUtils.sendPacket(new ServerboundSetCarriedItemPacket(var2));
               break;
            case ALTERNATIVE:
               method695(var1, var2);
         }
      }
   }

   public static void method695(int var0, int var1) {
      if (var0 != -1 && var1 != -1) {
         mc.gameMode.handleContainerInput(mc.player.inventoryMenu.containerId, var0, 0, ContainerInput.PICKUP, mc.player);
         mc.gameMode.handleContainerInput(mc.player.inventoryMenu.containerId, var1, 0, ContainerInput.PICKUP, mc.player);
         mc.gameMode.handleContainerInput(mc.player.inventoryMenu.containerId, var0, 0, ContainerInput.PICKUP, mc.player);
      }
   }

   public static void method696(int var0, int var1) {
      if (var0 != -1 && var1 != -1) {
         mc.gameMode.handleContainerInput(mc.player.inventoryMenu.containerId, method698(var0), 0, ContainerInput.PICKUP, mc.player);
         mc.gameMode.handleContainerInput(mc.player.inventoryMenu.containerId, method698(var1), 0, ContainerInput.PICKUP, mc.player);
         mc.gameMode.handleContainerInput(mc.player.inventoryMenu.containerId, method698(var0), 0, ContainerInput.PICKUP, mc.player);
      }
   }

   public static void method697(int var0, int var1) {
      if (var0 != -1 && var1 != -1) {
         method696(var0, var1);
      }
   }

   public static int method698(int var0) {
      return var0 >= 0 && var0 <= 8 ? 36 + var0 : var0;
   }

   public static void method699(Util1.Type var0) {
      switch (var0) {
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
