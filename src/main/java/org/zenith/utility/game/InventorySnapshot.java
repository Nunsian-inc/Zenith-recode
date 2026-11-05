package org.zenith.utility.game;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.item.ItemStack;

public class InventorySnapshot {
   private static final int CONST_intValue = 100;
   private final List<ItemEntry> items = new ArrayList<>();

   public void method1862(ItemStack itemStack, String text, int intValue, long longValue) {
      if (itemStack != null && !itemStack.isEmpty()) {
         if (intValue > 0) {
            ItemStack stack = itemStack.copy();
            stack.setCount(intValue);
            this.items.add(0, new ItemEntry(stack, text, intValue, Math.max(0L, longValue)));

            while (this.items.size() > 100) {
               this.items.removeLast();
            }
         }
      }
   }

   public List<ItemEntry> method1863() {
      return List.copyOf(this.items);
   }
}
