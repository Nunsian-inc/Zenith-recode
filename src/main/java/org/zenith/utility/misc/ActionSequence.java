package org.zenith.utility.misc;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.function.BooleanSupplier;
import lombok.Generated;

public class ActionSequence {
   private final StopWatch stopWatch = new StopWatch();
   private final List<ActionSequence.Entry3> items = Lists.newCopyOnWriteArrayList();
   private final List<ActionSequence.Entry4> items2 = Lists.newCopyOnWriteArrayList();
   private int intValue;
   private int intValue2;
   private boolean flag;
   private ActionSequence.Handler handler = new ActionSequence.Entry(1);

   public ActionSequence() {
      this.method4722();
   }

   public ActionSequence method4711(int intValue, ScheduledAction handler5) {
      return this.method4714(intValue, handler5, () -> true, 0);
   }

   public ActionSequence method4712(int intValue, ScheduledAction handler5, BooleanSupplier booleanSupplier) {
      return this.method4714(intValue, handler5, booleanSupplier, 0);
   }

   public ActionSequence method4713(int intValue, ScheduledAction handler5, int n) {
      return this.method4714(intValue, handler5, () -> true, n);
   }

   public ActionSequence method4714(int intValue, ScheduledAction handler5, BooleanSupplier booleanSupplier, int n) {
      this.items.add(new ActionSequence.Entry3(intValue, handler5, booleanSupplier, n));
      Collections.sort(this.items);
      return this;
   }

   public ActionSequence method4715(int ticks, ScheduledAction handler5) {
      return this.method4718(ticks, handler5, () -> true, 0);
   }

   public ActionSequence method4716(int ticks, ScheduledAction handler5, BooleanSupplier booleanSupplier) {
      return this.method4718(ticks, handler5, booleanSupplier, 0);
   }

   public ActionSequence method4717(int ticks, ScheduledAction handler5, int intValue) {
      return this.method4718(ticks, handler5, () -> true, intValue);
   }

   public ActionSequence method4718(int ticks, ScheduledAction handler5, BooleanSupplier booleanSupplier, int intValue) {
      this.items2.add(new ActionSequence.Entry4(ticks, handler5, booleanSupplier, intValue));
      Collections.sort(this.items2);
      return this;
   }

   public void method4719() {
      this.stopWatch.reset();
   }

   public void method4720() {
      this.intValue = 0;
      this.intValue2 = 0;
   }

   public ActionSequence method4721() {
      if (this.method4724()) {
         this.method4722();
      }

      return this;
   }

   public ActionSequence method4722() {
      this.items.clear();
      this.items2.clear();
      this.method4719();
      this.method4720();
      return this;
   }

   public void update() {
      if ((!this.items.isEmpty() || !this.items2.isEmpty()) && !this.flag) {
         this.items.forEach(entry3 -> {
            if (this.intValue < this.items.size()) {
               ActionSequence.Entry3 entry32 = this.items.get(this.intValue);
               if (entry32.getBooleanSupplier().getAsBoolean() && this.stopWatch.method317(entry32.getIntValue())) {
                  entry32.getHandler5().run();
                  this.intValue++;
                  this.method4719();
                  if (this.handler.method4725(this.intValue, this.items.size())) {
                     this.method4720();
                     this.handler.method4726();
                  }
               }
            }
         });
         this.items2.forEach(entry4 -> {
            if (this.intValue2 < this.items2.size()) {
               ActionSequence.Entry4 entry42 = this.items2.get(this.intValue2);
               if (entry42.getBooleanSupplier().getAsBoolean() && entry42.getTicks() <= 0) {
                  entry42.getHandler5().run();
                  this.intValue2++;
                  this.method4719();
                  if (this.handler.method4725(this.intValue2, this.items2.size())) {
                     this.method4720();
                     this.handler.method4726();
                  }
               }

               entry42.method4734();
            }
         });
         this.intValue = Math.min(this.intValue, this.items.size());
         this.intValue2 = Math.min(this.intValue2, this.items2.size());
      }
   }

   public ActionSequence method4723(ActionSequence.Handler handler) {
      this.handler = handler;
      return this;
   }

   public boolean method4724() {
      return this.intValue >= this.items.size() && this.intValue2 >= this.items2.size() && !this.flag && this.handler.method4727();
   }

   @Generated
   public StopWatch getStopWatch() {
      return this.stopWatch;
   }

   @Generated
   public List<ActionSequence.Entry3> getItems() {
      return this.items;
   }

   @Generated
   public List<ActionSequence.Entry4> getItems2() {
      return this.items2;
   }

   @Generated
   public int getIntValue() {
      return this.intValue;
   }

   @Generated
   public int getIntValue2() {
      return this.intValue2;
   }

   @Generated
   public boolean isFlag() {
      return this.flag;
   }

   @Generated
   public ActionSequence.Handler getHandler() {
      return this.handler;
   }

   @Generated
   public void setIntValue(int intValue) {
      this.intValue = intValue;
   }

   @Generated
   public void setIntValue2(int n) {
      this.intValue2 = n;
   }

   @Generated
   public void setFlag(boolean flag) {
      this.flag = flag;
   }

   public static class Entry implements ActionSequence.Handler {
      private final int intValue;
      private int intValue2;

      public Entry(int n) {
         this.intValue = n - 1;
      }

      @Override
      public boolean method4725(int n, int n2) {
         return n >= n2 && this.intValue2 < this.intValue;
      }

      @Override
      public void method4726() {
         this.intValue2++;
      }

      @Override
      public boolean method4727() {
         return this.intValue2 >= this.intValue;
      }
   }

   public static class Entry2 implements ActionSequence.Handler {
      @Override
      public boolean method4725(int n, int n2) {
         return n >= n2;
      }

      @Override
      public void method4726() {
      }

      @Override
      public boolean method4727() {
         return false;
      }
   }

   public static final class Entry3 implements Comparable<ActionSequence.Entry3> {
      private int intValue;
      private ScheduledAction handler5;
      private BooleanSupplier booleanSupplier;
      private int intValue2;

      public Entry3(int intValue, ScheduledAction handler5, BooleanSupplier booleanSupplier, int n) {
         this.intValue = intValue;
         this.handler5 = handler5;
         this.booleanSupplier = booleanSupplier;
         this.intValue2 = n;
      }

      public int compareTo(ActionSequence.Entry3 entry3) {
         return Integer.compare(entry3.getIntValue2(), this.getIntValue2());
      }

      @Generated
      public int getIntValue() {
         return this.intValue;
      }

      @Generated
      public ScheduledAction getHandler5() {
         return this.handler5;
      }

      @Generated
      public BooleanSupplier getBooleanSupplier() {
         return this.booleanSupplier;
      }

      @Generated
      public int getIntValue2() {
         return this.intValue2;
      }

      @Generated
      public ActionSequence.Entry3 method4729(int intValue) {
         this.intValue = intValue;
         return this;
      }

      @Generated
      public ActionSequence.Entry3 method4730(ScheduledAction handler5) {
         this.handler5 = handler5;
         return this;
      }

      @Generated
      public ActionSequence.Entry3 method4731(BooleanSupplier booleanSupplier) {
         this.booleanSupplier = booleanSupplier;
         return this;
      }

      @Generated
      public ActionSequence.Entry3 method4732(int n) {
         this.intValue2 = n;
         return this;
      }
   }

   public static final class Entry4 implements Comparable<ActionSequence.Entry4> {
      private int ticks;
      private ScheduledAction handler5;
      private BooleanSupplier booleanSupplier;
      private int intValue;

      public Entry4(int ticks, ScheduledAction handler5, BooleanSupplier booleanSupplier, int intValue) {
         this.ticks = ticks;
         this.handler5 = handler5;
         this.booleanSupplier = booleanSupplier;
         this.intValue = intValue;
      }

      public int compareTo(ActionSequence.Entry4 entry4) {
         return Integer.compare(entry4.getIntValue(), this.getIntValue());
      }

      public void method4734() {
         this.ticks--;
      }

      @Generated
      public int getTicks() {
         return this.ticks;
      }

      @Generated
      public ScheduledAction getHandler5() {
         return this.handler5;
      }

      @Generated
      public BooleanSupplier getBooleanSupplier() {
         return this.booleanSupplier;
      }

      @Generated
      public int getIntValue() {
         return this.intValue;
      }

      @Generated
      public ActionSequence.Entry4 method4735(int ticks) {
         this.ticks = ticks;
         return this;
      }

      @Generated
      public ActionSequence.Entry4 method4736(ScheduledAction handler5) {
         this.handler5 = handler5;
         return this;
      }

      @Generated
      public ActionSequence.Entry4 method4737(BooleanSupplier booleanSupplier) {
         this.booleanSupplier = booleanSupplier;
         return this;
      }

      @Generated
      public ActionSequence.Entry4 method4738(int intValue) {
         this.intValue = intValue;
         return this;
      }
   }

   public interface Handler {
      boolean method4725(int intValue, int size);

      void method4726();

      boolean method4727();
   }
}
