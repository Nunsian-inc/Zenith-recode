package org.zenith.client.events;

import com.darkmagician6.eventapi.events.Event;
import lombok.Generated;

public class DirectionEvent implements Event {
   private float floatValue;
   private float floatValue2;

   @Generated
   public float getFloatValue() {
      return this.floatValue;
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
   public void setFloatValue2(float f) {
      this.floatValue2 = f;
   }

   @Generated
   @Override
   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof DirectionEvent event)) {
         return false;
      } else if (!event.method490(this)) {
         return false;
      } else {
         return Float.compare(this.getFloatValue(), event.getFloatValue()) != 0 ? false : Float.compare(this.getFloatValue2(), event.getFloatValue2()) == 0;
      }
   }

   @Generated
   public boolean method490(Object obj) {
      return obj instanceof DirectionEvent;
   }

   @Generated
   @Override
   public int hashCode() {
      byte b = 59;
      int n = 1;
      n = n * 59 + Float.floatToIntBits(this.getFloatValue());
      return n * 59 + Float.floatToIntBits(this.getFloatValue2());
   }

   @Generated
   @Override
   public String toString() {
      return "EventDirection(yaw=" + this.getFloatValue() + ", pitch=" + this.getFloatValue2() + ")";
   }

   @Generated
   public DirectionEvent(float floatValue, float f) {
      this.floatValue = floatValue;
      this.floatValue2 = f;
   }
}
