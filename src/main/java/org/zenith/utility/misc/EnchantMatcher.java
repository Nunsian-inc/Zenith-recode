package org.zenith.utility.misc;

import lombok.Generated;
import net.minecraft.world.item.ItemStack;

public abstract class EnchantMatcher {
   public final String text;
   public final String text2;
   public int intValue;

   public abstract boolean method1746(ItemStack stack);

   @Override
   public String toString() {
      return this.getClass().getSimpleName() + " [checked=" + this.text2 + ", level=" + this.intValue + "]";
   }

   @Generated
   public EnchantMatcher(String text, String str, int intValue) {
      this.text = text;
      this.text2 = str;
      this.intValue = intValue;
   }

   @Generated
   public String getName() {
      return this.text;
   }

   @Generated
   public String getText2() {
      return this.text2;
   }

   @Generated
   public int getIntValue() {
      return this.intValue;
   }

   @Generated
   public void setIntValue(int intValue) {
      this.intValue = intValue;
   }
}
