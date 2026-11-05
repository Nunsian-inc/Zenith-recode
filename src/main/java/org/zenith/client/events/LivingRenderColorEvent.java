package org.zenith.client.events;

import lombok.Generated;
import org.zenith.CancellableEvent;

public class LivingRenderColorEvent extends CancellableEvent {
   private int color;

   @Generated
   public int getColor() {
      return this.color;
   }

   @Generated
   public void setColor(int color) {
      this.color = color;
   }

   @Generated
   public LivingRenderColorEvent(int color) {
      this.color = color;
   }
}
