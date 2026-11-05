package org.zenith.utility.misc;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Generated;
import net.minecraft.client.Minecraft;
import org.zenith.client.events.Event1;
import org.zenith.client.events.Event5;
import org.zenith.client.events.HandleInputEvent;
import org.zenith.client.events.HandleInputPostEvent;
import org.zenith.client.events.MoveInputEvent;
import org.zenith.client.events.UpdateEvent;

public class TaskScheduler {
   private static final Logger logger = Logger.getLogger(TaskScheduler.class.getName());
   private final LinkedList<TaskScheduler.Entry2> linkedList = new LinkedList<>();

   private LinkedList<TaskScheduler.Entry2> getLinkedList() {
      return this.linkedList;
   }

   public TaskScheduler() {
      this(true);
   }

   public TaskScheduler(boolean flag) {
      if (flag) {
         EventManager.register(this);
         new TaskScheduler.Entry().start();
      }
   }

   public void method5102(Object obj) {
      if (obj != null) {
         var linkedList = this.getLinkedList();
         TaskScheduler.Entry2 entry2;
         synchronized (linkedList) {
            entry2 = (TaskScheduler.Entry2)linkedList.peek();
         }

         if (entry2 != null) {
            boolean flag = true;

            try {
               flag = entry2.method5124(obj);
            } catch (Throwable e) {
               flag = false;
            }

            if (flag) {
               synchronized (linkedList) {
                  if (linkedList.peek() == entry2) {
                     linkedList.poll();
                  } else {
                     linkedList.removeIf(var1x -> var1x == entry2);
                  }
               }
            }
         }
      }
   }

   @EventTarget
   public void method5103(Event1 event) {
      this.method5102(event);
   }

   @EventTarget
   public void method5104(Event5 event) {
      this.method5102(event);
   }

   @EventTarget(4)
   public void method5105(MoveInputEvent event) {
      this.method5102(event);
   }

   @EventTarget(0)
   public void method5106(UpdateEvent event) {
      this.method5102(event);
   }

   @EventTarget(3)
   public void method5107(HandleInputEvent event) {
      this.method5102(event);
   }

   @EventTarget(0)
   public void method5108(HandleInputPostEvent event) {
      this.method5102(event);
   }

   public TaskScheduler.Entry2 method5109(Class<?> classValue) {
      LinkedList<TaskScheduler.Entry2> linkedList = this.getLinkedList();
      synchronized (linkedList) {
         for (TaskScheduler.Entry2 linked : linkedList) {
            if (linked.getClassValue() == classValue) {
               return linked;
            }
         }

         return new TaskScheduler.Entry2(classValue);
      }
   }

   public void method5110(TaskScheduler.Entry2 task) {
      this.method5111(task, 0);
   }

   public void method5111(TaskScheduler.Entry2 task, int n) {
      if (task != null) {
         var linkedList = this.getLinkedList();
         synchronized (linkedList) {
            boolean anyMatch = linkedList.stream().anyMatch(var1x -> var1x == task);
            if (!anyMatch) {
               task.setIntValue3(n);
               if (n > this.method5112()) {
                  if (!linkedList.isEmpty()) {
                     linkedList.add(1, task);
                  } else {
                     linkedList.addFirst(task);
                  }
               } else {
                  linkedList.add(task);
               }
            }
         }
      }
   }

   private int method5112() {
      LinkedList<TaskScheduler.Entry2> linkedList = this.getLinkedList();
      int n = Integer.MIN_VALUE;

      for (TaskScheduler.Entry2 linked : linkedList) {
         if (linked.getIntValue3() > n) {
            n = linked.getIntValue3();
         }
      }

      return n == Integer.MIN_VALUE ? 0 : n;
   }

   public void method5113() {
      var linkedList = this.getLinkedList();
      synchronized (linkedList) {
         linkedList.clear();
         logger.warning("All tasks cleared manually. tasks.size=0");
      }
   }

   public void method5114(Class<?> clazz) {
      var linkedList = this.getLinkedList();
      synchronized (linkedList) {
         linkedList.removeIf(var1x -> var1x.getClassValue() == clazz);
         logger.info("Tasks cleared for owner: " + clazz.getSimpleName());
      }
   }

   public int method5115() {
      var linkedList = this.getLinkedList();
      synchronized (linkedList) {
         return linkedList.size();
      }
   }

   public boolean method5116() {
      var linkedList = this.getLinkedList();
      synchronized (linkedList) {
         boolean empty = linkedList.isEmpty();
         logger.finest(() -> "isFinished() -> " + empty);
         return empty;
      }
   }

   public boolean method5117(Class<?> clazz) {
      var linkedList = this.getLinkedList();
      synchronized (linkedList) {
         return linkedList.stream().filter(var1x -> var1x.getClassValue() == clazz).allMatch(TaskScheduler.Entry2::method5125);
      }
   }

   public boolean method5118(Class<?> clazz) {
      var linkedList = this.getLinkedList();
      synchronized (linkedList) {
         TaskScheduler.Entry2 entry2 = (TaskScheduler.Entry2)linkedList.peek();
         if (entry2 == null) {
            return false;
         } else {
            Class clazz2 = entry2.method5127();
            return clazz2 == clazz;
         }
      }
   }

   static {
      logger.setLevel(Level.INFO);
   }

   public static class Entry {
      private Thread thread;
      private volatile boolean running;

      public void start() {
         if (!this.running) {
            this.running = true;
            this.thread = new Thread(() -> {
               while (this.running) {
                  try {
                     try {
                        if (Minecraft.getInstance().player != null) {
                           Minecraft.getInstance().execute(() -> EventManager.call(new Event1()));
                        }
                     } catch (Exception e) {
                        e.printStackTrace();
                     }

                     int nextInt = ThreadLocalRandom.current().nextInt(40, 60);
                     Thread.sleep(nextInt);
                  } catch (InterruptedException e) {
                  }
               }
            }, "RandomEventCaller");
            this.thread.setDaemon(true);
            this.thread.start();
         }
      }

      public void stop() {
         this.running = false;
         if (this.thread != null) {
            this.thread.interrupt();
            this.thread = null;
         }
      }
   }

   public static class Entry2 {
      private static final AtomicLong atomicLong = new AtomicLong(1L);
      private final long longValue = atomicLong.getAndIncrement();
      private final Class<?> classValue;
      private final Queue<TaskScheduler.Entry2.Entry<?>> queue = new LinkedList<>();
      private final LinkedList<TaskScheduler.Entry2.Entry<?>> linkedList = new LinkedList<>();
      private int intValue = 0;
      private int intValue2 = Integer.MAX_VALUE;
      private int intValue3 = 0;

      public Entry2() {
         this.classValue = TaskScheduler.class;
      }

      public Entry2(Class<?> classValue) {
         this.classValue = classValue;
      }

      public TaskScheduler.Entry2 method5119(int n) {
         this.intValue2 = Math.max(1, n);
         TaskScheduler.logger.finest(this.method5126() + " set maxIdleTicks=" + this.intValue2);
         return this;
      }

      public <E> TaskScheduler.Entry2 method5120(Class<E> classValue, TaskScheduler.Entry2.Handler<E> handler) {
         return this.method5121(classValue, handler, 0);
      }

      public <E> TaskScheduler.Entry2 method5121(Class<E> classValue, TaskScheduler.Entry2.Handler<E> handler, int n) {
         TaskScheduler.Entry2.Entry entry = new TaskScheduler.Entry2.Entry(classValue, handler, this.queue.size() + 1, false, n);
         if (n > this.method5122()) {
            LinkedList queue = (LinkedList)this.queue;
            queue.addFirst(entry);
         } else {
            this.queue.add(entry);
         }

         TaskScheduler.logger.finest(this.method5126() + " scheduled step#" + entry.intValue + " for event=" + classValue.getSimpleName());
         return this;
      }

      private int method5122() {
         int n = Integer.MIN_VALUE;

         for (TaskScheduler.Entry2.Entry entry : this.queue) {
            if (entry.intValue2 > n) {
               n = entry.intValue2;
            }
         }

         return n == Integer.MIN_VALUE ? 0 : n;
      }

      public <E> TaskScheduler.Entry2 method5123(Class<E> classValue, TaskScheduler.Entry2.Handler<E> handler) {
         TaskScheduler.Entry2.Entry entry = new TaskScheduler.Entry2.Entry(classValue, handler, this.linkedList.size() + 1, true, 0);
         this.linkedList.add(entry);
         TaskScheduler.logger.finest(this.method5126() + " scheduled PERSISTENT step#" + entry.intValue + " for event=" + classValue.getSimpleName());
         return this;
      }

      public void cancel() {
         this.queue.clear();
         this.linkedList.clear();
      }

      public boolean method5124(Object obj) {
         if (obj == null) {
            return false;
         } else {
            TaskScheduler.Entry2.Entry entry = this.queue.peek();
            if (entry == null) {
               TaskScheduler.logger.info(this.method5126() + "task COMPLETE.");
               return true;
            } else {
               for (TaskScheduler.Entry2.Entry linked : this.linkedList) {
                  if (linked.classValue.isInstance(obj)) {
                     try {
                        linked.method5128(obj);
                     } catch (Throwable e) {
                        e.printStackTrace();
                     }
                  }
               }

               boolean flag = false;
               if (entry.classValue.isInstance(obj)) {
                  boolean flag2;
                  try {
                     flag2 = entry.method5128(obj);
                  } catch (Throwable e) {
                     e.printStackTrace();
                     flag2 = true;
                  }

                  if (flag2) {
                     this.queue.poll();
                     flag = true;
                     TaskScheduler.logger.info(this.method5126() + " step#" + entry.intValue + " DONE size=" + this.queue.size());
                  } else {
                     TaskScheduler.logger.finest(() -> this.method5126() + " step#" + entry.intValue + " NOT done");
                  }
               }

               if (flag) {
                  this.intValue = 0;
                  boolean empty = this.queue.isEmpty();
                  if (empty) {
                     TaskScheduler.logger.info(this.method5126() + "TASK COMPLETE.");
                  }

                  return empty;
               } else {
                  if (this.intValue2 != Integer.MAX_VALUE) {
                     this.intValue++;
                  }

                  if (this.intValue > this.intValue2) {
                     this.queue.clear();
                     this.linkedList.clear();
                     return true;
                  } else {
                     return false;
                  }
               }
            }
         }
      }

      public boolean method5125() {
         boolean empty = this.queue.isEmpty();
         TaskScheduler.logger.finest(() -> this.method5126() + " isCompleted() -> " + empty);
         return empty;
      }

      private String method5126() {
         return "[Task#" + this.longValue + "@" + (this.classValue != null ? this.classValue.getSimpleName() : "Unknown") + "]";
      }

      @Override
      public String toString() {
         return "ScriptTask{id="
            + this.longValue
            + ", owner="
            + (this.classValue != null ? this.classValue.getSimpleName() : "null")
            + ", stepsLeft="
            + this.queue.size()
            + ", persistent="
            + this.linkedList.size()
            + ", idleTicks="
            + this.intValue
            + "/"
            + this.intValue2
            + ", priority="
            + this.intValue3
            + "}";
      }

      public Class<?> method5127() {
         TaskScheduler.Entry2.Entry entry = this.queue.peek();
         return entry == null ? null : entry.classValue;
      }

      @Generated
      public long getLongValue() {
         return this.longValue;
      }

      @Generated
      public Class<?> getClassValue() {
         return this.classValue;
      }

      @Generated
      public int getIntValue3() {
         return this.intValue3;
      }

      @Generated
      public void setIntValue3(int n) {
         this.intValue3 = n;
      }

      private static class Entry<E> {
         private final Class<E> classValue;
         private final TaskScheduler.Entry2.Handler<E> handler;
         private final int intValue;
         private final boolean flag;
         private final int intValue2;

         public Entry(Class<E> classValue, TaskScheduler.Entry2.Handler<E> handler, int intValue, boolean flag, int n) {
            this.classValue = classValue;
            this.handler = handler;
            this.intValue = intValue;
            this.flag = flag;
            this.intValue2 = n;
         }

         public boolean method5128(Object obj) {
            return this.handler.accept(this.classValue.cast(obj));
         }

         @Override
         public String toString() {
            return "Step{index="
               + this.intValue
               + ", event="
               + this.classValue.getSimpleName()
               + ", persistent="
               + this.flag
               + ", priority="
               + this.intValue2
               + "}";
         }
      }

      @FunctionalInterface
      public interface Handler<E> {
         boolean accept(E value);
      }
   }
}
