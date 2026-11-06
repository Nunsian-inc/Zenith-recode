package org.zenith.utility.game;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import org.zenith.MinecraftAccess;
import org.zenith.utility.network.InventoryUtils;

public final class PotionUtils implements MinecraftAccess {
   private PotionUtils() {
   }

   public static List<MobEffectInstance> method2321(ItemStack stack) {
      ArrayList<MobEffectInstance> effects = new ArrayList<>();
      if (stack != null && !stack.isEmpty()) {
         PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);
         if (contents == null) {
            return effects;
         } else {
            for (MobEffectInstance effect : contents.getAllEffects()) {
               effects.add(effect);
            }

            return effects;
         }
      } else {
         return effects;
      }
   }

   public static boolean method2322(ItemStack stack, Holder<MobEffect> effect) {
      for (MobEffectInstance mobEffectInstance : method2321(stack)) {
         if (mobEffectInstance.getEffect().equals(effect)) {
            return true;
         }
      }

      return false;
   }

   public static boolean method2323(ItemStack stack) {
      return stack != null && !stack.isEmpty() && stack.is(Items.SPLASH_POTION);
   }

   public static boolean method2324(ItemStack stack) {
      return stack != null && !stack.isEmpty() && stack.is(Items.POTION);
   }

   public static int method2325(Predicate<ItemStack> predicate) {
      int hotbarSlot = method2326(predicate);
      if (hotbarSlot != -1) {
         return hotbarSlot;
      } else {
         for (int slot = 9; slot < 36; slot++) {
            if (predicate.test(mc.player.getInventory().getItem(slot))) {
               return slot;
            }
         }

         return -1;
      }
   }

   public static int method2326(Predicate<ItemStack> predicate) {
      for (int slot = 0; slot < 9; slot++) {
         if (predicate.test(mc.player.getInventory().getItem(slot))) {
            return slot;
         }
      }

      return -1;
   }

   public static int method2327(int slot) {
      return slot < 9 ? 36 + slot : slot;
   }

   public static void method2328(int slot, int selectedSlot) {
      mc.gameMode.handleContainerInput(mc.player.inventoryMenu.containerId, method2327(slot), selectedSlot, ContainerInput.SWAP, mc.player);
      InventoryUtils.closeScreen();
   }

   public static void method2329(int slot) {
      if (mc.player.getInventory().getSelectedSlot() != slot) {
         mc.player.getInventory().setSelectedSlot(slot);
      }

      mc.gameMode.ensureHasSentCarriedItem();
   }
}
