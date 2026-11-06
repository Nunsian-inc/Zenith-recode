package org.zenith.utility.misc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public final class ScriptHook<C> {
   public static final int PRIORITY_EARLY = -100;
   public static final int PRIORITY_NORMAL = 0;
   public static final int PRIORITY_LATE = 100;
   private final String hookName;
   private final String description;
   private final Class<C> payloadType;
   private volatile ScriptHook.Subscription<C>[] subscriptions = emptyHandlers();
   private long sequence;

   private static <C> ScriptHook.Subscription<C>[] emptyHandlers() {
      return new ScriptHook.Subscription[0];
   }

   public ScriptHook(String hookName, Class<C> payloadType, String str) {
      this.hookName = hookName;
      this.payloadType = payloadType;
      this.description = str;
   }

   public String name() {
      return this.hookName;
   }

   public String getText2() {
      return this.description;
   }

   public Class<C> getClassValue() {
      return this.payloadType;
   }

   public boolean active() {
      return this.subscriptions.length != 0;
   }

   public C dispatch(C payload) {
      for (ScriptHook.Subscription subscription : this.subscriptions) {
         subscription.handler.handle((C)payload);
      }

      return (C)payload;
   }

   public synchronized Runnable subscribe(int priority, ScriptHook.Handler<C> handler) {
      ScriptHook.Subscription subscription = new ScriptHook.Subscription(priority, this.sequence++, handler);
      var subscriptions = new ArrayList<>(Arrays.asList(this.subscriptions));
      subscriptions.add(subscription);
      subscriptions.sort(Comparator.<ScriptHook.Subscription<?>>comparingInt(d -> d.getPriority()).thenComparingLong(d -> d.getSequence()));
      this.subscriptions = (ScriptHook.Subscription[])subscriptions.toArray(new ScriptHook.Subscription[0]);
      return () -> this.unsubscribe(subscription);
   }

   private synchronized void unsubscribe(ScriptHook.Subscription<C> subscription) {
      var subscriptions = new ArrayList<>(Arrays.asList(this.subscriptions));
      if (subscriptions.remove(subscription)) {
         this.subscriptions = (ScriptHook.Subscription[])subscriptions.toArray(new ScriptHook.Subscription[0]);
      }
   }

   private static final class Subscription<C> {
      private final int priority;
      private final long sequence;
      private final ScriptHook.Handler<C> handler;

      public int priority() {
         return this.priority;
      }

      public long sequence() {
         return this.sequence;
      }

      public ScriptHook.Handler<C> handler() {
         return this.handler;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Subscription<?> other)) return false;
         return java.util.Objects.equals(this.priority, other.priority) && java.util.Objects.equals(this.sequence, other.sequence) && java.util.Objects.equals(this.handler, other.handler);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(priority, sequence, handler);
      }

      @Override
      public String toString() {
         return "Data[" + "intValue=" + this.priority + ", " + "longValue=" + this.sequence + ", " + "handler=" + this.handler + "]";
      }

      private Subscription(int priority, long sequence, ScriptHook.Handler<C> handler) {
         this.priority = priority;
         this.sequence = sequence;
         this.handler = handler;
      }

      public int getPriority() {
         return this.priority;
      }

      public long getSequence() {
         return this.sequence;
      }

      public ScriptHook.Handler<C> getHandler() {
         return this.handler;
      }
   }

   @FunctionalInterface
   public interface Handler<C> {
      void handle(C payload);
   }
}
