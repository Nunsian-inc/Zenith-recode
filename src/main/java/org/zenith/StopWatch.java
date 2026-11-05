package org.zenith;

import lombok.Generated;

public class StopWatch {
   private long longValue;

   public StopWatch() {
      this.reset();
   }

   public boolean method7544(long l) {
      boolean flag = this.method7546(l);
      if (flag) {
         this.reset();
      }

      return flag;
   }

   public boolean method7545(float f) {
      return System.currentTimeMillis() - (long)f >= this.longValue;
   }

   public boolean method7546(long l) {
      return System.currentTimeMillis() - l >= this.longValue;
   }

   public void reset() {
      this.longValue = System.currentTimeMillis();
   }

   public long method7547() {
      return System.currentTimeMillis() - this.longValue;
   }

   @Generated
   public long getLongValue() {
      return this.longValue;
   }

   @Generated
   public void setLongValue(long longValue) {
      this.longValue = longValue;
   }
}
