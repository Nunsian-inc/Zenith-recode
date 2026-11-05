package org.zenith.client.events;

import com.darkmagician6.eventapi.events.callables.EventCancellable;
import lombok.Generated;

public class MouseButtonEvent extends EventCancellable {
   private final int intValue;
   private final int intValue2;

   @Generated
   public int getIntValue() {
      return this.intValue;
   }

   @Generated
   public int getIntValue2() {
      return this.intValue2;
   }

   @Generated
   public MouseButtonEvent(int intValue, int n) {
      this.intValue = intValue;
      this.intValue2 = n;
   }
}
