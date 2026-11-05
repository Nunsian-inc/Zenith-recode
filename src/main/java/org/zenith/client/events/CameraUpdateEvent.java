package org.zenith.client.events;

import lombok.Generated;
import org.zenith.CancellableEvent;
import org.zenith.Rotation;

public class CameraUpdateEvent extends CancellableEvent {
   private boolean flag;
   private float floatValue;
   private Rotation rotation;

   @Generated
   public boolean isFlag() {
      return this.flag;
   }

   @Generated
   public float getFloatValue() {
      return this.floatValue;
   }

   @Generated
   public Rotation getRotation() {
      return this.rotation;
   }

   @Generated
   public void setFlag(boolean flag) {
      this.flag = flag;
   }

   @Generated
   public void setFloatValue(float floatValue) {
      this.floatValue = floatValue;
   }

   @Generated
   public void setRotation(Rotation rotation) {
      this.rotation = rotation;
   }

   @Generated
   public CameraUpdateEvent(boolean flag, float floatValue, Rotation rotation) {
      this.flag = flag;
      this.floatValue = floatValue;
      this.rotation = rotation;
   }
}
