package org.zenith.client.events;

import lombok.Generated;
import org.zenith.CancellableEvent;

public class SprintUpdateEvent extends CancellableEvent {
   private boolean flag;

   @Generated
   public SprintUpdateEvent(boolean flag) {
      this.flag = flag;
   }

   @Generated
   public boolean isFlag2() {
      return this.flag;
   }

   @Generated
   public void setFlag2(boolean flag) {
      this.flag = flag;
   }
}
