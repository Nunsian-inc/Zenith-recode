package org.zenith.client.screens.nlgui.chat;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.item.ItemStack;

public final class CloudInventoryData {
   public static final int MAIN_SIZE = 36;
   public static final int ARMOR_SIZE = 4;
   private final List<ItemStack> main;
   private final List<ItemStack> armor;
   private final ItemStack offhand;

   public CloudInventoryData(List<ItemStack> stacks, List<ItemStack> stacks2, ItemStack stack) {
      this.main = List.copyOf(padCopy(stacks, 36));
      this.armor = List.copyOf(padCopy(stacks2, 4));
      this.offhand = stack == null ? ItemStack.EMPTY : stack.copy();
   }

   public List<ItemStack> main() {
      return this.main;
   }

   public List<ItemStack> armor() {
      return this.armor;
   }

   public ItemStack offhand() {
      return this.offhand;
   }

   public ItemStack hotbar(int n) {
      return n >= 0 && n < 9 ? this.main.get(n) : ItemStack.EMPTY;
   }

   public ItemStack inventory(int n) {
      int n2 = 9 + n;
      return n >= 0 && n2 < 36 ? this.main.get(n2) : ItemStack.EMPTY;
   }

   public int nonEmptyInventoryCount() {
      int n = 0;

      for (int i = 9; i < 36; i++) {
         if (!this.main.get(i).isEmpty()) {
            n++;
         }
      }

      return n;
   }

   public int nonEmptyHotbarCount() {
      int n = 0;

      for (int i = 0; i < 9; i++) {
         if (!this.main.get(i).isEmpty()) {
            n++;
         }
      }

      return n;
   }

   private static List<ItemStack> padCopy(List<ItemStack> stacks, int initialCapacity) {
      List<ItemStack> stacks2 = new ArrayList<>(initialCapacity);
      if (stacks != null) {
         for (ItemStack stack : stacks) {
            stacks2.add(stack == null ? ItemStack.EMPTY : stack.copy());
         }
      }

      while (stacks2.size() < initialCapacity) {
         stacks2.add(ItemStack.EMPTY);
      }

      return (List<ItemStack>)(stacks2.size() > initialCapacity ? stacks2.subList(0, initialCapacity) : stacks2);
   }
}
