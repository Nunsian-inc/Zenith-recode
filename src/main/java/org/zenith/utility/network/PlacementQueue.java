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

   public static int method2811(InteractionHand hand) {
      return hand == InteractionHand.OFF_HAND ? 40 : mc.player.getInventory().getSelectedSlot();
   }

   public void method2812(long l, int intValue, Item rawType, int n) {
      if (mc.player != null && !mc.player.isCreative()) {
         PlacementQueue.Data data = this.items.put(l, new PlacementQueue.Data(intValue, rawType, n));
         if (data != null) {
            this.method2817(data);
         }
      }
   }

   public void method2813(long l) {
      this.items.remove(l);
   }

   public void method2814(long l) {
      PlacementQueue.Data data = this.items.remove(l);
      if (data != null) {
         this.method2817(data);
      }
   }

   public void method2815(Packet<?> packet3) {
      if (packet3 instanceof ClientboundContainerSetSlotPacket containerSetSlotPacket) {
         if (containerSetSlotPacket.getContainerId() == 0) {
            int n = method2818(containerSetSlotPacket.getSlot());
            if (n >= 0) {
               this.queue.add(n);
            }
         } else if (containerSetSlotPacket.getContainerId() == -2) {
            this.queue.add(containerSetSlotPacket.getSlot());
         }
      } else if (packet3 instanceof ClientboundSetPlayerInventoryPacket playerInventoryPacket) {
         this.queue.add(playerInventoryPacket.slot());
      } else if (packet3 instanceof ClientboundContainerSetContentPacket setContentPacket && setContentPacket.containerId() == 0) {
         this.queue.add(Integer.MIN_VALUE);
      }
   }

   public void method2816(int tickCount) {
      Integer n;
      while ((n = this.queue.poll()) != null) {
         if (n == Integer.MIN_VALUE) {
            this.items.clear();
         } else {
            int n2 = n;
            this.items.values().removeIf(var1x -> var1x.getIntValue() == n2);
         }
      }

      var iterator = this.items.values().iterator();

      while (iterator.hasNext()) {
         PlacementQueue.Data data = (PlacementQueue.Data)iterator.next();
         if (data.getIntValue2() < tickCount) {
            iterator.remove();
            this.method2817(data);
         }
      }
   }

   public void clear() {
      this.items.clear();
      this.queue.clear();
   }

   private void method2817(PlacementQueue.Data data) {
      if (mc.player != null) {
         Inventory inventory = mc.player.getInventory();
         ItemStack stack = inventory.getItem(data.getIntValue());
         if (stack.isEmpty()) {
            inventory.setItem(data.getIntValue(), new ItemStack(data.getItem()));
         } else if (stack.is(data.getItem()) && stack.getCount() < stack.getMaxStackSize()) {
            stack.grow(1);
         }
      }
   }

   private static int method2818(int slot) {
      if (slot >= 36 && slot <= 44) {
         return slot - 36;
      } else if (slot >= 9 && slot <= 35) {
         return slot;
      } else {
         return slot == 45 ? 40 : -1;
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

      private Data(int intValue, Item rawType, int n) {
         this.intValue = intValue;
         this.item = rawType;
         this.intValue2 = n;
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
