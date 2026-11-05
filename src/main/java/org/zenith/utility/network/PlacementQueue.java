package org.zenith.utility.network;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ClientboundSetPlayerInventoryPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.zenith.MinecraftAccess;

public final class PlacementQueue implements MinecraftAccess {
   private static final int CONST_intValue = Integer.MIN_VALUE;
   private final Map<Long, PlacementQueue.Data> items = new HashMap<>();
   private final Queue<Integer> queue = new ConcurrentLinkedQueue<>();

   public static int method2811(InteractionHand var0) {
      return var0 == InteractionHand.OFF_HAND ? 40 : mc.player.getInventory().getSelectedSlot();
   }

   public void method2812(long var1, int var3, Item var4, int var5) {
      if (mc.player != null && !mc.player.isCreative()) {
         PlacementQueue.Data var6 = this.items.put(var1, new PlacementQueue.Data(var3, var4, var5));
         if (var6 != null) {
            this.method2817(var6);
         }
      }
   }

   public void method2813(long var1) {
      this.items.remove(var1);
   }

   public void method2814(long var1) {
      PlacementQueue.Data var3 = this.items.remove(var1);
      if (var3 != null) {
         this.method2817(var3);
      }
   }

   public void method2815(Packet<?> var1) {
      if (var1 instanceof ClientboundContainerSetSlotPacket var2) {
         if (var2.getContainerId() == 0) {
            int var5 = method2818(var2.getSlot());
            if (var5 >= 0) {
               this.queue.add(var5);
            }
         } else if (var2.getContainerId() == -2) {
            this.queue.add(var2.getSlot());
         }
      } else if (var1 instanceof ClientboundSetPlayerInventoryPacket var3) {
         this.queue.add(var3.slot());
      } else if (var1 instanceof ClientboundContainerSetContentPacket var4 && var4.containerId() == 0) {
         this.queue.add(Integer.MIN_VALUE);
      }
   }

   public void method2816(int var1) {
      Integer var2;
      while ((var2 = this.queue.poll()) != null) {
         if (var2 == Integer.MIN_VALUE) {
            this.items.clear();
         } else {
            int var3 = var2;
            this.items.values().removeIf(var1x -> var1x.getIntValue() == var3);
         }
      }

      var var5 = this.items.values().iterator();

      while (var5.hasNext()) {
         PlacementQueue.Data var4 = (PlacementQueue.Data)var5.next();
         if (var4.getIntValue2() < var1) {
            var5.remove();
            this.method2817(var4);
         }
      }
   }

   public void clear() {
      this.items.clear();
      this.queue.clear();
   }

   private void method2817(PlacementQueue.Data var1) {
      if (mc.player != null) {
         Inventory var2 = mc.player.getInventory();
         ItemStack var3 = var2.getItem(var1.getIntValue());
         if (var3.isEmpty()) {
            var2.setItem(var1.getIntValue(), new ItemStack(var1.getItem()));
         } else if (var3.is(var1.getItem()) && var3.getCount() < var3.getMaxStackSize()) {
            var3.grow(1);
         }
      }
   }

   private static int method2818(int var0) {
      if (var0 >= 36 && var0 <= 44) {
         return var0 - 36;
      } else if (var0 >= 9 && var0 <= 35) {
         return var0;
      } else {
         return var0 == 45 ? 40 : -1;
      }
   }

   private static final class Data {
      private final int intValue;
      private final Item item;
      private final int intValue2;

      public int intValue() {
         return this.intValue;
      }

      public Item item() {
         return this.item;
      }

      public int intValue2() {
         return this.intValue2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.item, other.item) && java.util.Objects.equals(this.intValue2, other.intValue2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(intValue, item, intValue2);
      }

      @Override
      public String toString() {
         return "Data[" + "intValue=" + this.intValue + ", " + "item=" + this.item + ", " + "intValue2=" + this.intValue2 + "]";
      }

      private Data(int var1, Item var2, int var3) {
         this.intValue = var1;
         this.item = var2;
         this.intValue2 = var3;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public Item getItem() {
         return this.item;
      }

      public int getIntValue2() {
         return this.intValue2;
      }
   }
}
