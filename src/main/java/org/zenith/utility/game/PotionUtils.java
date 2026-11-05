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

   public static List<MobEffectInstance> method2321(ItemStack var0) {
      ArrayList<MobEffectInstance> var1 = new ArrayList<>();
      if (var0 != null && !var0.isEmpty()) {
         PotionContents var2 = var0.get(DataComponents.POTION_CONTENTS);
         if (var2 == null) {
            return var1;
         } else {
            for (MobEffectInstance var4 : var2.getAllEffects()) {
               var1.add(var4);
            }

            return var1;
         }
      } else {
         return var1;
      }
   }

   public static boolean method2322(ItemStack var0, Holder<MobEffect> var1) {
      for (MobEffectInstance var3 : method2321(var0)) {
         if (var3.getEffect().equals(var1)) {
            return true;
         }
      }

      return false;
   }

   public static boolean method2323(ItemStack var0) {
      return var0 != null && !var0.isEmpty() && var0.is(Items.SPLASH_POTION);
   }

   public static boolean method2324(ItemStack var0) {
      return var0 != null && !var0.isEmpty() && var0.is(Items.POTION);
   }

   public static int method2325(Predicate<ItemStack> var0) {
      int var1 = method2326(var0);
      if (var1 != -1) {
         return var1;
      } else {
         for (int var2 = 9; var2 < 36; var2++) {
            if (var0.test(mc.player.getInventory().getItem(var2))) {
               return var2;
            }
         }

         return -1;
      }
   }

   public static int method2326(Predicate<ItemStack> var0) {
      for (int var1 = 0; var1 < 9; var1++) {
         if (var0.test(mc.player.getInventory().getItem(var1))) {
            return var1;
         }
      }

      return -1;
   }

   public static int method2327(int var0) {
      return var0 < 9 ? 36 + var0 : var0;
   }

   public static void method2328(int var0, int var1) {
      mc.gameMode.handleContainerInput(mc.player.inventoryMenu.containerId, method2327(var0), var1, ContainerInput.SWAP, mc.player);
      InventoryUtils.closeScreen();
   }

   public static void method2329(int var0) {
      if (mc.player.getInventory().getSelectedSlot() != var0) {
         mc.player.getInventory().setSelectedSlot(var0);
      }

      mc.gameMode.ensureHasSentCarriedItem();
   }
}
