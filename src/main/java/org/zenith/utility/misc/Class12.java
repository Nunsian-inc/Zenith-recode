package org.zenith.utility.misc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public final class Class12<C> {
   public static final int CONST_intValue = -100;
   public static final int CONST_intValue2 = 0;
   public static final int CONST_intValue3 = 100;
   private final String text;
   private final String text2;
   private final Class<C> classValue;
   private volatile Class12.Data<C>[] datas = method1644();
   private long longValue;

   private static <C> Class12.Data<C>[] method1644() {
      return new Class12.Data[0];
   }

   public Class12(String text, Class<C> classValue, String str) {
      this.text = text;
      this.classValue = classValue;
      this.text2 = str;
   }

   public String name() {
      return this.text;
   }

   public String getText2() {
      return this.text2;
   }

   public Class<C> getClassValue() {
      return this.classValue;
   }

   public boolean active() {
      return this.datas.length != 0;
   }

   public C method1645(C entry) {
      for (Class12.Data data : this.datas) {
         data.handler.handle((C)entry);
      }

      return (C)entry;
   }

   public synchronized Runnable method1646(int intValue, Class12.Handler<C> handler) {
      Class12.Data data = new Class12.Data(intValue, this.longValue++, handler);
      var datas = new ArrayList<>(Arrays.asList(this.datas));
      datas.add(data);
      datas.sort(Comparator.<Class12.Data<?>>comparingInt(d -> d.getIntValue()).thenComparingLong(d -> d.getLongValue()));
      this.datas = (Class12.Data[])datas.toArray(new Class12.Data[0]);
      return () -> this.method1647(data);
   }

   private synchronized void method1647(Class12.Data<C> data) {
      var datas = new ArrayList<>(Arrays.asList(this.datas));
      if (datas.remove(data)) {
         this.datas = (Class12.Data[])datas.toArray(new Class12.Data[0]);
      }
   }

   private static final class Data<C> {
      private final int intValue;
      private final long longValue;
      private final Class12.Handler<C> handler;

      public int intValue() {
         return this.intValue;
      }

      public long longValue() {
         return this.longValue;
      }

      public Class12.Handler<C> handler() {
         return this.handler;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data<?> other)) return false;
         return java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.handler, other.handler);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(intValue, longValue, handler);
      }

      @Override
      public String toString() {
         return "Data[" + "intValue=" + this.intValue + ", " + "longValue=" + this.longValue + ", " + "handler=" + this.handler + "]";
      }

      private Data(int intValue, long longValue, Class12.Handler<C> handler) {
         this.intValue = intValue;
         this.longValue = longValue;
         this.handler = handler;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public long getLongValue() {
         return this.longValue;
      }

      public Class12.Handler<C> getHandler() {
         return this.handler;
      }
   }

   @FunctionalInterface
   public interface Handler<C> {
      void handle(C entry);
   }
}
