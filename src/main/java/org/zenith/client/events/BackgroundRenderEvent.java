package org.zenith.client.events;

import com.darkmagician6.eventapi.events.callables.EventCancellable;
import lombok.Generated;

public class BackgroundRenderEvent extends EventCancellable {
   private float floatValue;
   private int color;
   private int intValue;
   private float floatValue2;

   @Generated
   public float getFloatValue() {
      return this.floatValue;
   }

   @Generated
   public int getColor() {
      return this.color;
   }

   @Generated
   public int getIntValue() {
      return this.intValue;
   }

   @Generated
   public float getFloatValue2() {
      return this.floatValue2;
   }

   @Generated
   public void setFloatValue(float floatValue) {
      this.floatValue = floatValue;
   }

   @Generated
   public void setColor(int color) {
      this.color = color;
   }

   @Generated
   public void setIntValue(int intValue) {
      this.intValue = intValue;
   }

   @Generated
   public void setFloatValue2(float renderDistanceEnd) {
      this.floatValue2 = renderDistanceEnd;
   }
}
