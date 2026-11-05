package org.zenith.client.events;

import org.zenith.CancellableEvent;

public class Event4 extends CancellableEvent {
   private int intValue;

   public int getIntValue2() {
      return this.intValue;
   }

   public void method1537(int n) {
      this.intValue = Math.max(0, n);
   }
}
