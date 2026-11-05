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

   public static void method2637(Player var0, ItemStack var1) {
      if (var0 != null && var1 != null && !var1.isEmpty()) {
         if (mc.player != null && var0 != mc.player) {
            method2641();
            if (var1.getCount() > var1.getItem().getDefaultMaxStackSize()) {
               flag = true;
               flag2 = true;
            }

            if (var1.has(DataComponents.DAMAGE) && !var1.isDamageableItem()) {
               flag2 = true;
            }
         }
      }
   }

   public static boolean method2638(Player var0) {
      method2641();
      return var0 == mc.player || !flag;
   }

   public static boolean method2639(Player var0) {
      method2641();
      return var0 == mc.player || !flag2;
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
