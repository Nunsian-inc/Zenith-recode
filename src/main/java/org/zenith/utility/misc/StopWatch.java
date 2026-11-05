package org.zenith.utility.misc;

import lombok.Generated;

public class StopWatch {
   private long startTime;

   public StopWatch() {
      this.reset();
   }

   public boolean method317(double intValue) {
      return System.currentTimeMillis() - intValue >= this.startTime;
   }

   public boolean method318(double intValue) {
      boolean flag = this.method317(intValue);
      if (flag) {
         this.reset();
      }

      return flag;
   }

   public void reset() {
      this.startTime = System.currentTimeMillis();
   }

   public int method319() {
      return Math.toIntExact(System.currentTimeMillis() - this.startTime);
   }

   public void method320(long l) {
      this.startTime = System.currentTimeMillis() - l;
   }

   @Generated
   public long getStartTime() {
      return this.startTime;
   }
}
