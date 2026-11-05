package org.zenith.utility.game;

import lombok.Generated;
import net.minecraft.world.item.ItemStack;

public class ItemEntry {
   private final ItemStack itemStack;
   private final String text;
   private final int intValue;
   private final long longValue;
   private final long longValue2;

   public ItemEntry(ItemStack stack, String text, int intValue, long longValue) {
      this.itemStack = stack == null ? ItemStack.EMPTY : stack.copy();
      this.text = text == null ? "" : text;
      this.intValue = intValue;
      this.longValue = longValue;
      this.longValue2 = System.currentTimeMillis();
   }

   @Generated
   public ItemStack getItemStack() {
      return this.itemStack;
   }

   @Generated
   public String getText() {
      return this.text;
   }

   @Generated
   public int getIntValue() {
      return this.intValue;
   }

   @Generated
   public long getLongValue() {
      return this.longValue;
   }

   @Generated
   public long getLongValue2() {
      return this.longValue2;
   }
}
