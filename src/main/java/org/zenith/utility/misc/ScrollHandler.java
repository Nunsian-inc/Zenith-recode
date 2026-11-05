package org.zenith.utility.misc;

import lombok.Generated;
import org.zenith.MinecraftAccess;

public class ScrollHandler implements MinecraftAccess {
   private double doubleValue;
   private double value = 0.0;
   private double doubleValue2 = 0.0;
   private double doubleValue3 = 8.0;
   private static final double CONST_doubleValue = 0.4;
   public static final double CONST_doubleValue2 = 1.0;

   public void update() {
      this.doubleValue2 = Math.max(Math.min(this.doubleValue2, 0.0), -this.doubleValue);
      double d = this.doubleValue2 - this.value;
      this.value += d * 0.4;
      if (Math.abs(d) < 0.1) {
         this.value = this.doubleValue2;
      }
   }

   public double method6103() {
      return -this.value;
   }

   public void method6104(double d) {
      this.doubleValue2 = this.doubleValue2 + d * this.doubleValue3;
   }

   @Generated
   public double getDoubleValue() {
      return this.doubleValue;
   }

   @Generated
   public double getDoubleValue2() {
      return this.doubleValue2;
   }

   @Generated
   public double getDoubleValue3() {
      return this.doubleValue3;
   }

   @Generated
   public void setDoubleValue(double doubleValue) {
      this.doubleValue = doubleValue;
   }

   @Generated
   public void setValue(double d) {
      this.value = d;
   }

   @Generated
   public void setDoubleValue2(double d) {
      this.doubleValue2 = d;
   }

   @Generated
   public void setDoubleValue3(double d) {
      this.doubleValue3 = d;
   }
}
