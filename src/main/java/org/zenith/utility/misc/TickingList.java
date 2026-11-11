package org.zenith.utility.misc;

import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;
import net.minecraft.client.Minecraft;
import org.zenith.base.Module;

public class TickingList<T> {
   private int currentTick = 0;
   private final PriorityBlockingQueue<TickingList.Entry<T>> priorityBlockingQueue = new PriorityBlockingQueue<>(
      11, Comparator.comparingInt(entry -> -entry.priority)
   );

   public void advance(int n) {
      this.currentTick += n;
   }

   public void tick() {
      this.advance(1);
   }

   public void addOrReplace(TickingList.Entry<T> entry) {
      this.priorityBlockingQueue.removeIf(var1x -> var1x.module == entry.module);
      entry.expiresAt = entry.expiresAt + this.currentTick;
      this.priorityBlockingQueue.add(entry);
   }

   public T peekValue() {
      TickingList.Entry entry = this.priorityBlockingQueue.peek();
      if (entry == null) {
         return null;
      } else {
         if (Minecraft.getInstance().isSameThread()) {
            while (entry != null && (entry.expiresAt <= this.currentTick || !entry.module.isEnabled())) {
               this.priorityBlockingQueue.poll();
               entry = this.priorityBlockingQueue.peek();
            }
         }

         return entry != null ? (T)entry.value : null;
      }
   }

   public TickingList.Entry<T> peekEntry() {
      TickingList.Entry entry = this.priorityBlockingQueue.peek();
      if (entry == null) {
         return null;
      } else {
         if (Minecraft.getInstance().isSameThread()) {
            while (entry != null && (entry.expiresAt <= this.currentTick || !entry.module.isEnabled())) {
               this.priorityBlockingQueue.poll();
               entry = this.priorityBlockingQueue.peek();
            }
         }

         return entry;
      }
   }

   public void clear() {
      this.priorityBlockingQueue.clear();
   }

   public static class Entry<T> {
      public int expiresAt;
      public final int priority;
      public final Module module;
      public final T value;

      public Entry(int expiresAt, int priority, Module moduleValue, T rotationRequest) {
         this.expiresAt = expiresAt;
         this.priority = priority;
         this.module = moduleValue;
         this.value = (T)rotationRequest;
      }
   }
}
