package org.zenith.utility.misc;

import java.util.ArrayDeque;
import java.util.Arrays;

public final class CriticalsState {
   private static final double CONST_doubleValue = 5.0E7;
   private static final double CONST_doubleValue2 = 5.0E7;
   private static final double CONST_doubleValue3 = 1.0E9;
   private static final double CONST_doubleValue4 = 2.0E7;
   private static final double CONST_doubleValue5 = 5.0E9;
   private static final double CONST_doubleValue6 = 0.2;
   private static final double CONST_doubleValue7 = 0.2;
   private static final double CONST_doubleValue8 = 0.2;
   private static final double CONST_doubleValue9 = 2.0;
   private static final int CONST_intValue = 9;
   private final ArrayDeque<Double> arrayDeque = new ArrayDeque<>(9);
   private double doubleValue = 5.0E7;
   private double doubleValue2;
   private double doubleValue3;
   private double doubleValue4;
   private long longValue;
   private long longValue2;
   private long longValue3 = Long.MIN_VALUE;
   private boolean flag;

   public synchronized void method1706(long gameTime, long nanoTime) {
      this.method1714(nanoTime);
      if (this.longValue2 != 0L && gameTime > this.longValue3) {
         long l = nanoTime - this.longValue2;
         long l2 = gameTime - this.longValue3;
         double d = (double)l / l2;
         if (l > 0L && Double.isFinite(d) && d >= 2.0E7 && d <= 5.0E9) {
            this.method1715(d);
            double d2 = this.method1716();
            this.doubleValue2 = this.doubleValue2 + (Math.abs(d - d2) - this.doubleValue2) * 0.2;
            double d3 = method1717(d2, 5.0E7, 1.0E9);
            double doubleValue = method1717(Math.max(d3, d - this.doubleValue2), 5.0E7, 1.0E9);
            if (doubleValue > this.doubleValue) {
               this.doubleValue = doubleValue;
            } else {
               this.doubleValue = this.doubleValue + (d3 - this.doubleValue) * 0.2;
            }
         }
      } else if (this.longValue3 != Long.MIN_VALUE && gameTime <= this.longValue3) {
         this.arrayDeque.clear();
         this.doubleValue2 = 0.0;
      }

      this.longValue3 = gameTime;
      this.longValue2 = nanoTime;
   }

   public synchronized void method1707(long nanoTime) {
      this.method1714(nanoTime);
      this.doubleValue4 = this.doubleValue3;
      this.flag = true;
   }

   public synchronized boolean method1708(double doubleValue, double d) {
      return this.flag && !(doubleValue <= 0.0) ? this.method1713(System.nanoTime()) + Math.max(0.0, d) >= doubleValue : true;
   }

   public synchronized long method1709(double doubleValue, double d) {
      if (this.flag && !(doubleValue <= 0.0)) {
         double d2 = doubleValue - this.method1713(System.nanoTime()) - Math.max(0.0, d);
         if (d2 <= 0.0) {
            return 0L;
         } else {
            double d3 = d2 * this.doubleValue;
            return d3 >= 9.223372E18F ? Long.MAX_VALUE : (long)Math.ceil(d3);
         }
      } else {
         return 0L;
      }
   }

   public synchronized double method1710() {
      if (!this.flag) {
         return Double.POSITIVE_INFINITY;
      } else {
         this.method1714(System.nanoTime());
         return Math.max(0.0, this.doubleValue3 - this.doubleValue4);
      }
   }

   public synchronized double method1711() {
      return !this.flag ? Double.POSITIVE_INFINITY : this.method1713(System.nanoTime());
   }

   public synchronized double getDoubleValue() {
      return this.doubleValue;
   }

   public synchronized float method1712() {
      return (float)Math.min(20.0, 1.0E9 / this.doubleValue);
   }

   public synchronized void reset() {
      this.arrayDeque.clear();
      this.doubleValue = 5.0E7;
      this.doubleValue2 = 0.0;
      this.doubleValue3 = 0.0;
      this.flag = false;
      this.doubleValue4 = 0.0;
      this.longValue = 0L;
      this.longValue2 = 0L;
      this.longValue3 = Long.MIN_VALUE;
   }

   private double method1713(long nanoTime) {
      this.method1714(nanoTime);
      double d = Math.max(0.0, this.doubleValue3 - this.doubleValue4);
      double d2 = 0.2 + this.doubleValue2 / this.doubleValue;
      return Math.max(0.0, d - method1717(d2, 0.2, 2.0));
   }

   private void method1714(long longValue) {
      if (this.longValue != 0L && longValue > this.longValue) {
         this.doubleValue3 = this.doubleValue3 + (longValue - this.longValue) / this.doubleValue;
      }

      this.longValue = longValue;
   }

   private void method1715(double d) {
      if (this.arrayDeque.size() >= 9) {
         this.arrayDeque.removeFirst();
      }

      this.arrayDeque.addLast(d);
   }

   private double method1716() {
      double[] doubles = new double[this.arrayDeque.size()];
      int n = 0;

      for (double d : this.arrayDeque) {
         doubles[n++] = d;
      }

      Arrays.sort(doubles);
      int n2 = doubles.length / 2;
      return doubles.length % 2 == 0 ? (doubles[n2 - 1] + doubles[n2]) * 0.5 : doubles[n2];
   }

   private static double method1717(double d, double d2, double d3) {
      return Math.max(d2, Math.min(d3, d));
   }
}
