package org.zenith.utility.misc;

import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;
import net.minecraft.client.Minecraft;
import org.zenith.base.Module;

public class TickingList<T> {
   private int intValue = 0;
   private final PriorityBlockingQueue<TickingList.Entry<T>> priorityBlockingQueue = new PriorityBlockingQueue<>(
      11, Comparator.comparingInt(entry -> -entry.intValue2)
   );

   public void method13(int n) {
      this.intValue += n;
   }

   public void tick() {
      this.method13(1);
   }

   public void method14(TickingList.Entry<T> entry) {
      this.priorityBlockingQueue.removeIf(var1x -> var1x.module == entry.module);
      entry.intValue = entry.intValue + this.intValue;
      this.priorityBlockingQueue.add(entry);
   }

   public T method15() {
      TickingList.Entry entry = this.priorityBlockingQueue.peek();
      if (entry == null) {
         return null;
      } else {
         if (Minecraft.getInstance().isSameThread()) {
            while (entry != null && (entry.intValue <= this.intValue || !entry.module.isEnabled())) {
               this.priorityBlockingQueue.poll();
               entry = this.priorityBlockingQueue.peek();
            }
         }

         return entry != null ? (T)entry.obj : null;
      }
   }

   public TickingList.Entry<T> method16() {
      TickingList.Entry entry = this.priorityBlockingQueue.peek();
      if (entry == null) {
         return null;
      } else {
         if (Minecraft.getInstance().isSameThread()) {
            while (entry != null && (entry.intValue <= this.intValue || !entry.module.isEnabled())) {
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
      public int intValue;
      public final int intValue2;
      public final Module module;
      public final T obj;

      public Entry(int intValue, int priority, Module moduleValue, T rotationRequest) {
         this.intValue = intValue;
         this.intValue2 = priority;
         this.module = moduleValue;
         this.obj = (T)rotationRequest;
      }
   }
}
