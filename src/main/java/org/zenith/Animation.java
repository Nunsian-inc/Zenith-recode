package org.zenith;

import lombok.Generated;

public class Animation {
   private long duration;
   private float value;
   private Easing easing;
   private long startTime;
   private float startValue;
   private float targetValue;
   private boolean done;
   private boolean flag;

   public Animation(long duration, float initialValue, Easing easing) {
      this.duration = duration;
      this.easing = easing;
      this.value = initialValue;
      this.startValue = initialValue;
      this.targetValue = initialValue;
      this.done = true;
   }

   public Animation(long duration, Easing easing) {
      this(duration, 0.0F, easing);
   }

   public void updateVisibility(boolean visible) {
      this.animateTo(visible ? 1.0F : 0.0F);
   }

   public float animateTo(float targetValue) {
      long now = System.currentTimeMillis();
      if (targetValue != this.targetValue) {
         this.startValue = this.value;
         this.targetValue = targetValue;
         this.startTime = now;
         this.done = false;
      }

      long elapsed = now - this.startTime;
      if (elapsed >= this.duration) {
         this.value = this.targetValue;
         this.done = true;
         return this.value;
      } else {
         float progress = (float)elapsed / (float)this.duration;
         float easedProgress = this.easing.ease(progress, 0.0F, 1.0F, 1.0F);
         this.value = this.startValue + (this.targetValue - this.startValue) * easedProgress;
         return this.value;
      }
   }

   public void snapTo(float value) {
      this.value = value;
      this.startValue = value;
      this.targetValue = value;
      this.done = true;
   }

   public void reset(float value) {
      this.value = value;
      this.startValue = value;
      this.targetValue = value;
      this.done = true;
   }

   public void reset() {
      this.reset(0.0F);
   }

   public void setTarget(float targetValue) {
      if (targetValue != this.targetValue) {
         this.startValue = this.value;
         this.targetValue = targetValue;
         this.startTime = System.currentTimeMillis();
         this.done = false;
      }
   }

   public float update() {
      return this.animateTo(this.targetValue);
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
      return this.startValue;
   }

   @Generated
   public float getFloatValue2() {
      return this.targetValue;
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
   public void setFloatValue(float startValue) {
      this.startValue = startValue;
   }

   @Generated
   public void setFloatValue2(float targetValue) {
      this.targetValue = targetValue;
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
