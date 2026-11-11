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

public final class InventoryActionState implements MinecraftAccess {
   private static final int CONST_intValue = 20;
   private Predicate<ItemStack> predicate;
   private int intValue;
   private boolean flag;

   public boolean pending() {
      return this.predicate != null;
   }

   public boolean hasMatchingInventoryItem(Predicate<ItemStack> predicate) {
      return mc.player != null && this.findMatchingInventorySlot(predicate) != null;
   }

   public void requestInventoryMove(Predicate<ItemStack> predicate) {
      if (this.predicate == null && mc.player != null) {
         this.predicate = predicate;
         this.intValue = mc.player.tickCount;
         this.flag = false;
      }
   }

   public void cancel() {
      this.predicate = null;
      this.flag = false;
   }

   public void onMoveInput(MoveInputEvent event) {
      if (this.flag && this.predicate != null) {
         event.clearInput();
      }
   }

   public boolean tryCompleteInventoryMove() {
      if (this.predicate != null && mc.player != null && mc.gameMode != null) {
         if (mc.player.tickCount - this.intValue <= 20 && mc.player.containerMenu == mc.player.inventoryMenu) {
            Slot slot = this.findMatchingInventorySlot(this.predicate);
            if (slot == null) {
               this.cancel();
               return false;
            } else if (InventorySettingModule.INSTANCE.method2397() && this.isPlayerMoving()) {
               this.flag = true;
               if (mc.player.isSprinting()) {
                  mc.getConnection().send(new ServerboundPlayerCommandPacket(mc.player, Action.STOP_SPRINTING));
                  mc.player.setSprinting(false);
               }

               return false;
            } else {
               InventoryUtils.method901(slot, this.findDestinationHotbarSlot(), false);
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

   private boolean isPlayerMoving() {
      Input lastSentInput = mc.player.lastSentInput;
      return lastSentInput.jump() || lastSentInput.forward() || lastSentInput.backward() || lastSentInput.left() || lastSentInput.right() || mc.player.isSprinting();
   }

   private Slot findMatchingInventorySlot(Predicate<ItemStack> predicate) {
      for (Slot slot : mc.player.inventoryMenu.slots) {
         if (slot.index >= 9 && slot.index <= 35 && predicate.test(slot.getItem())) {
            return slot;
         }
      }

      return null;
   }

   private int findDestinationHotbarSlot() {
      for (int i = 0; i < 9; i++) {
         if (mc.player.getInventory().getItem(i).isEmpty()) {
            return i;
         }
      }

      int selectedSlot = mc.player.getInventory().getSelectedSlot();

      for (int i = 8; i >= 0; i--) {
         if (i != selectedSlot && !isProtectedHotbarItem(mc.player.getInventory().getItem(i))) {
            return i;
         }
      }

      return selectedSlot == 8 ? 7 : 8;
   }

   private static boolean isProtectedHotbarItem(ItemStack stack) {
      return stack.is(ItemTags.SWORDS)
         || stack.is(ItemTags.AXES)
         || stack.is(Items.TOTEM_OF_UNDYING)
         || stack.is(Items.GOLDEN_APPLE)
         || stack.is(Items.ENCHANTED_GOLDEN_APPLE)
         || stack.is(Items.END_CRYSTAL)
         || stack.is(Items.OBSIDIAN)
         || stack.is(Items.ENDER_PEARL)
         || stack.is(Items.COBWEB)
         || stack.is(Items.RESPAWN_ANCHOR)
         || stack.is(Items.GLOWSTONE)
         || stack.is(Items.MACE);
   }
}
