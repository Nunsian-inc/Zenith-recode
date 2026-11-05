package org.zenith.utility.game;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.zenith.MinecraftAccess;

public final class Util14 implements MinecraftAccess {
   private static Level world;
   private static boolean flag;
   private static boolean flag2;

   private Util14() {
   }

   public static void method2637(Player player, ItemStack stack) {
      if (player != null && stack != null && !stack.isEmpty()) {
         if (mc.player != null && player != mc.player) {
            method2641();
            if (stack.getCount() > stack.getItem().getDefaultMaxStackSize()) {
               flag = true;
               flag2 = true;
            }

            if (stack.has(DataComponents.DAMAGE) && !stack.isDamageableItem()) {
               flag2 = true;
            }
         }
      }
   }

   public static boolean method2638(Player player) {
      method2641();
      return player == mc.player || !flag;
   }

   public static boolean method2639(Player livingEntity) {
      method2641();
      return livingEntity == mc.player || !flag2;
   }

   public static boolean method2640() {
      method2641();
      return flag2;
   }

   private static void method2641() {
      if (mc.level != world) {
         world = mc.level;
         flag = false;
         flag2 = false;
      }
   }
}
