package org.zenith;

import lombok.Generated;

public class StopWatch {
   private long startTime;

   public StopWatch() {
      this.reset();
   }

   public boolean hasElapsedAndReset(long l) {
      boolean flag = this.hasElapsed(l);
      if (flag) {
         this.reset();
      }

      return flag;
   }

   public boolean hasElapsedSinceFloat(float f) {
      return System.currentTimeMillis() - (long)f >= this.startTime;
   }

   public boolean hasElapsed(long l) {
      return System.currentTimeMillis() - l >= this.startTime;
   }

   public void reset() {
      this.startTime = System.currentTimeMillis();
   }

   public long elapsedMillis() {
      return System.currentTimeMillis() - this.startTime;
   }

   @Generated
   public long getStartTime() {
      return this.startTime;
   }

   @Generated
   public void setStartTime(long startTime) {
      this.startTime = startTime;
   }
}
