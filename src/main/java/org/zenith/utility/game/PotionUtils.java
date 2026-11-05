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

   public static List<MobEffectInstance> method2321(ItemStack useItem) {
      ArrayList<MobEffectInstance> mobEffectInstances = new ArrayList<>();
      if (useItem != null && !useItem.isEmpty()) {
         PotionContents potionContents = useItem.get(DataComponents.POTION_CONTENTS);
         if (potionContents == null) {
            return mobEffectInstances;
         } else {
            for (MobEffectInstance allEffect : potionContents.getAllEffects()) {
               mobEffectInstances.add(allEffect);
            }

            return mobEffectInstances;
         }
      } else {
         return mobEffectInstances;
      }
   }

   public static boolean method2322(ItemStack useItem, Holder<MobEffect> effect) {
      for (MobEffectInstance mobEffectInstance : method2321(useItem)) {
         if (mobEffectInstance.getEffect().equals(effect)) {
            return true;
         }
      }

      return false;
   }

   public static boolean method2323(ItemStack useItem) {
      return useItem != null && !useItem.isEmpty() && useItem.is(Items.SPLASH_POTION);
   }

   public static boolean method2324(ItemStack useItem) {
      return useItem != null && !useItem.isEmpty() && useItem.is(Items.POTION);
   }

   public static int method2325(Predicate<ItemStack> predicate) {
      int n = method2326(predicate);
      if (n != -1) {
         return n;
      } else {
         for (int i = 9; i < 36; i++) {
            if (predicate.test(mc.player.getInventory().getItem(i))) {
               return i;
            }
         }

         return -1;
      }
   }

   public static int method2326(Predicate<ItemStack> predicate) {
      for (int i = 0; i < 9; i++) {
         if (predicate.test(mc.player.getInventory().getItem(i))) {
            return i;
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
