package org.zenith.utility.misc;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;

public final class Util10 {
   private Util10() {
   }

   public static boolean method2177(ItemStack mainHandItem) {
      return mainHandItem.is(ItemTags.PICKAXES);
   }

   public static boolean method2178(ItemStack mainHandItem) {
      return mainHandItem.is(ItemTags.SWORDS);
   }

   public static boolean method2179(ItemStack stack) {
      return stack.is(ItemTags.AXES);
   }

   public static boolean method2180(ItemStack stack) {
      return stack.is(ItemTags.SHOVELS);
   }

   public static boolean method2181(ItemStack itemBySlot) {
      return method2183(itemBySlot) || method2182(itemBySlot) || method2184(itemBySlot) || method2185(itemBySlot);
   }

   public static boolean method2182(ItemStack itemBySlot) {
      return itemBySlot.is(ItemTags.CHEST_ARMOR);
   }

   public static boolean method2183(ItemStack itemBySlot) {
      return itemBySlot.is(ItemTags.HEAD_ARMOR);
   }

   public static boolean method2184(ItemStack itemBySlot) {
      return itemBySlot.is(ItemTags.LEG_ARMOR);
   }

   public static boolean method2185(ItemStack itemBySlot) {
      return itemBySlot.is(ItemTags.FOOT_ARMOR);
   }
}
