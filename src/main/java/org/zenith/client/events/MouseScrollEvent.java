package org.zenith.client.events;

import com.darkmagician6.eventapi.events.callables.EventCancellable;
import lombok.Generated;

public class MouseScrollEvent extends EventCancellable {
   private double doubleValue;
   private double doubleValue2;

   @Generated
   public double getDoubleValue() {
      return this.doubleValue;
   }

   @Generated
   public double getDoubleValue2() {
      return this.doubleValue2;
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
   public MouseScrollEvent(double doubleValue, double d) {
      this.doubleValue = doubleValue;
      this.doubleValue2 = d;
   }
}
