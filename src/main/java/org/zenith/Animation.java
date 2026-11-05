package org.zenith;

import lombok.Generated;

public class Animation {
   private long duration;
   private float value;
   private Easing easing;
   private long startTime;
   private float floatValue;
   private float floatValue2;
   private boolean done;
   private boolean flag;

   public Animation(long duration, float floatValue, Easing easing) {
      this.duration = duration;
      this.easing = easing;
      this.value = floatValue;
      this.floatValue = floatValue;
      this.floatValue2 = floatValue;
      this.done = true;
   }

   public Animation(long duration, Easing easing) {
      this(duration, 0.0F, easing);
   }

   public void method2807(boolean visible) {
      this.method2808(visible ? 1.0F : 0.0F);
   }

   public float method2808(float floatValue) {
      long now = System.currentTimeMillis();
      if (floatValue != this.floatValue2) {
         this.floatValue = this.value;
         this.floatValue2 = floatValue;
         this.startTime = now;
         this.done = false;
      }

      long l = now - this.startTime;
      if (l >= this.duration) {
         this.value = this.floatValue2;
         this.done = true;
         return this.value;
      } else {
         float f = (float)l / (float)this.duration;
         float ease = this.easing.ease(f, 0.0F, 1.0F, 1.0F);
         this.value = this.floatValue + (this.floatValue2 - this.floatValue) * ease;
         return this.value;
      }
   }

   public void method2809(float floatValue) {
      this.value = floatValue;
      this.floatValue = floatValue;
      this.floatValue2 = floatValue;
      this.done = true;
   }

   public void reset(float floatValue) {
      this.value = floatValue;
      this.floatValue = floatValue;
      this.floatValue2 = floatValue;
      this.done = true;
   }

   public void reset() {
      this.reset(0.0F);
   }

   public void method2810(float lastX) {
      if (lastX != this.floatValue2) {
         this.floatValue = this.value;
         this.floatValue2 = lastX;
         this.startTime = System.currentTimeMillis();
         this.done = false;
      }
   }

   public float update() {
      return this.method2808(this.floatValue2);
   }

   @Generated
   public long getDuration() {
      return this.duration;
   }

   @Generated
   public float getValue() {
      return this.value;
   }

   @Generated
   public Easing getEasing() {
      return this.easing;
   }

   @Generated
   public long getStartTime() {
      return this.startTime;
   }

   @Generated
   public float getFloatValue() {
      return this.floatValue;
   }

   @Generated
   public float getFloatValue2() {
      return this.floatValue2;
   }

   @Generated
   public boolean isDone() {
      return this.done;
   }

   @Generated
   public boolean isFlag() {
      return this.flag;
   }

   @Generated
   public void setDuration(long duration) {
      this.duration = duration;
   }

   @Generated
   public void setEasing(Easing easing) {
      this.easing = easing;
   }

   @Generated
   public void setStartTime(long startTime) {
      this.startTime = startTime;
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
   public void setDone(boolean done) {
      this.done = done;
   }

   @Generated
   public void setFlag(boolean flag) {
      this.flag = flag;
   }
}
