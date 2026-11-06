package org.zenith.utility.misc;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;

public final class ItemStackTags {
   private ItemStackTags() {
   }

   public static boolean isPickaxe(ItemStack stack) {
      return stack.is(ItemTags.PICKAXES);
   }

   public static boolean isSword(ItemStack stack) {
      return stack.is(ItemTags.SWORDS);
   }

   public static boolean isAxe(ItemStack stack) {
      return stack.is(ItemTags.AXES);
   }

   public static boolean isShovel(ItemStack stack) {
      return stack.is(ItemTags.SHOVELS);
   }

   public static boolean isArmor(ItemStack stack) {
      return isHeadArmor(stack) || isChestArmor(stack) || isLegArmor(stack) || isFootArmor(stack);
   }

   public static boolean isChestArmor(ItemStack stack) {
      return stack.is(ItemTags.CHEST_ARMOR);
   }

   public static boolean isHeadArmor(ItemStack stack) {
      return stack.is(ItemTags.HEAD_ARMOR);
   }

   public static boolean isLegArmor(ItemStack stack) {
      return stack.is(ItemTags.LEG_ARMOR);
   }

   public static boolean isFootArmor(ItemStack stack) {
      return stack.is(ItemTags.FOOT_ARMOR);
   }
}
