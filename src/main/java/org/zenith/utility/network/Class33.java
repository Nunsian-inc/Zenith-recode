package org.zenith.utility.network;

import java.util.function.Predicate;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket.Action;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.zenith.MinecraftAccess;
import org.zenith.client.events.MoveInputEvent;
import org.zenith.client.modules.impl.misc.InventorySettingModule;

public final class Class33 implements MinecraftAccess {
   private static final int CONST_intValue = 20;
   private Predicate<ItemStack> predicate;
   private int intValue;
   private boolean flag;

   public boolean pending() {
      return this.predicate != null;
   }

   public boolean method5302(Predicate<ItemStack> var1) {
      return mc.player != null && this.method5306(var1) != null;
   }

   public void method5303(Predicate<ItemStack> var1) {
      if (this.predicate == null && mc.player != null) {
         this.predicate = var1;
         this.intValue = mc.player.tickCount;
         this.flag = false;
      }
   }

   public void cancel() {
      this.predicate = null;
      this.flag = false;
   }

   public void onMoveInput(MoveInputEvent var1) {
      if (this.flag && this.predicate != null) {
         var1.method3044();
      }
   }

   public boolean method5304() {
      if (this.predicate != null && mc.player != null && mc.gameMode != null) {
         if (mc.player.tickCount - this.intValue <= 20 && mc.player.containerMenu == mc.player.inventoryMenu) {
            Slot var1 = this.method5306(this.predicate);
            if (var1 == null) {
               this.cancel();
               return false;
            } else if (InventorySettingModule.INSTANCE.method2397() && this.method5305()) {
               this.flag = true;
               if (mc.player.isSprinting()) {
                  mc.getConnection().send(new ServerboundPlayerCommandPacket(mc.player, Action.STOP_SPRINTING));
                  mc.player.setSprinting(false);
               }

               return false;
            } else {
               InventoryUtils.method901(var1, this.method5307(), false);
               InventoryUtils.closeScreen();
               this.cancel();
               return true;
            }
         } else {
            this.cancel();
            return false;
         }
      } else {
         return false;
      }
   }

   private boolean method5305() {
      Input var1 = mc.player.lastSentInput;
      return var1.jump() || var1.forward() || var1.backward() || var1.left() || var1.right() || mc.player.isSprinting();
   }

   private Slot method5306(Predicate<ItemStack> var1) {
      for (Slot var3 : mc.player.inventoryMenu.slots) {
         if (var3.index >= 9 && var3.index <= 35 && var1.test(var3.getItem())) {
            return var3;
         }
      }

      return null;
   }

   private int method5307() {
      for (int var1 = 0; var1 < 9; var1++) {
         if (mc.player.getInventory().getItem(var1).isEmpty()) {
            return var1;
         }
      }

      int var3 = mc.player.getInventory().getSelectedSlot();

      for (int var2 = 8; var2 >= 0; var2--) {
         if (var2 != var3 && !method5308(mc.player.getInventory().getItem(var2))) {
            return var2;
         }
      }

      return var3 == 8 ? 7 : 8;
   }

   private static boolean method5308(ItemStack var0) {
      return var0.is(ItemTags.SWORDS)
         || var0.is(ItemTags.AXES)
         || var0.is(Items.TOTEM_OF_UNDYING)
         || var0.is(Items.GOLDEN_APPLE)
         || var0.is(Items.ENCHANTED_GOLDEN_APPLE)
         || var0.is(Items.END_CRYSTAL)
         || var0.is(Items.OBSIDIAN)
         || var0.is(Items.ENDER_PEARL)
         || var0.is(Items.COBWEB)
         || var0.is(Items.RESPAWN_ANCHOR)
         || var0.is(Items.GLOWSTONE)
         || var0.is(Items.MACE);
   }
}
