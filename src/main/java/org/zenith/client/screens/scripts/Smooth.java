package org.zenith.client.screens.scripts;

public final class Smooth {
   private float value;
   private float target;
   private long last = System.nanoTime();
   private final float speed;
   private float min = -Float.MAX_VALUE;
   private float max = Float.MAX_VALUE;

   public Smooth(float speed) {
      this.speed = speed;
   }

   public float update() {
      long nanoTime = System.nanoTime();
      float f = Math.min(0.1F, (float)(nanoTime - this.last) / 1.0E9F);
      this.last = nanoTime;
      this.value = this.value + (this.target - this.value) * (1.0F - (float)Math.exp(-f * this.speed));
      if (Math.abs(this.target - this.value) < 0.05F) {
         this.value = this.target;
      }

      return this.value;
   }

   public float value() {
      return this.value;
   }

   public float target() {
      return this.target;
   }

   public void target(float target) {
      this.target = target;
   }

   public void add(float lineHeight) {
      this.target = Math.max(this.min, Math.min(this.max, this.target + lineHeight));
   }

   public void clamp(float min, float max) {
      this.min = min;
      this.max = max;
      this.target = Math.max(min, Math.min(max, this.target));
      this.value = Math.max(min, Math.min(max, this.value));
   }

   public void reset(float target) {
      this.value = target;
      this.target = target;
      this.last = System.nanoTime();
   }

   public static float press(long l, long l2) {
      if (l <= 0L) {
         return 0.0F;
      } else {
         float f = (float)(System.currentTimeMillis() - l) / (float)l2;
         return f >= 1.0F ? 0.0F : (1.0F - f) * (1.0F - f);
      }
   }
}
