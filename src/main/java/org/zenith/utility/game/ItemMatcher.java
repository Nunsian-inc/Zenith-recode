package org.zenith.utility.game;

import lombok.Generated;
import net.minecraft.world.item.ItemStack;
import org.zenith.ZenithAccess;

public class ItemMatcher implements ZenithAccess {
   public ItemStack itemStack;
   public final String text;
   public final String text2;
   public final ItemMatcher.Type type;

   public ItemMatcher(ItemStack itemStack, String text, ItemMatcher.Type type) {
      this.itemStack = itemStack;
      this.text = text;
      this.text2 = text;
      this.type = type;
   }

   public ItemMatcher(ItemStack itemStack, String text, String str, ItemMatcher.Type type) {
      this.itemStack = itemStack;
      this.text = text;
      this.text2 = str;
      this.type = type;
   }

   public boolean method2672(ItemStack stack) {
      return stack != null && stack.getItem() == this.itemStack.getItem();
   }

   @Generated
   public ItemStack getItemStack() {
      return this.itemStack;
   }

   @Generated
   public String getText2() {
      return this.text;
   }

   @Generated
   public String getText22() {
      return this.text2;
   }

   @Generated
   public ItemMatcher.Type getType() {
      return this.type;
   }

   public static enum Type {
      FUNTIME,
      HOLLYWORLD,
      ANY;
   }
}
