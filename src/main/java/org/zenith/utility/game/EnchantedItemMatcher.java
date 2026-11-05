package org.zenith.utility.game;

import java.util.ArrayList;
import net.minecraft.world.item.ItemStack;
import org.zenith.utility.misc.EnchantMatcher;

public class EnchantedItemMatcher extends ItemMatcher {
   public final ArrayList<EnchantMatcher> arrayList = new ArrayList<>();

   public EnchantedItemMatcher(ItemStack itemStack, String text, String str, ItemMatcher.Type type) {
      super(itemStack, text, str, type);
   }

   public EnchantedItemMatcher(ItemStack defaultInstance, String text, ItemMatcher.Type type) {
      super(defaultInstance, text, type);
   }

   @Override
   public boolean method2672(ItemStack stack) {
      if (!super.method2672(stack)) {
         return false;
      } else {
         for (EnchantMatcher array : this.arrayList) {
            if (!array.method1746(stack)) {
               return false;
            }
         }

         return true;
      }
   }

   public void method4844(EnchantMatcher enchantMatcher) {
      this.arrayList.add(enchantMatcher);
   }

   public ArrayList<EnchantMatcher> getArrayList() {
      return this.arrayList;
   }
}
