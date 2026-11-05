package org.zenith.client.events;

import com.darkmagician6.eventapi.events.callables.EventCancellable;
import lombok.Generated;

public class MouseRotationEvent extends EventCancellable {
   public double doubleValue;
   public double doubleValue2;
   public double doubleValue3;
   public double doubleValue4;

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
   public double getDoubleValue4() {
      return this.doubleValue4;
   }

   @Generated
   public void setDoubleValue(double doubleValue) {
      this.doubleValue = doubleValue;
   }

   @Generated
   public void setDoubleValue2(double d) {
      this.doubleValue2 = d;
   }

   @Generated
   public void setDoubleValue3(double d) {
      this.doubleValue3 = d;
   }

   @Generated
   public void setDoubleValue4(double d) {
      this.doubleValue4 = d;
   }

   @Generated
   public MouseRotationEvent(double doubleValue, double d, double accumulatedDX, double accumulatedDY) {
      this.doubleValue = doubleValue;
      this.doubleValue2 = d;
      this.doubleValue3 = accumulatedDX;
      this.doubleValue4 = accumulatedDY;
   }
}
