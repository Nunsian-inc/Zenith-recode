package org.zenith.utility.misc;

public class Vec2f {
   private final float floatValue;
   private final float floatValue2;

   public Vec2f(float floatValue, float f) {
      this.floatValue = floatValue;
      this.floatValue2 = f;
   }

   public float method5327() {
      return (float)Math.sqrt(this.floatValue * this.floatValue + this.floatValue2 * this.floatValue2);
   }

   public float getFloatValue() {
      return this.floatValue;
   }

   public float getFloatValue2() {
      return this.floatValue2;
   }

   public net.minecraft.world.phys.Vec2 method5328() {
      return new net.minecraft.world.phys.Vec2(this.floatValue, this.floatValue2);
   }

   public boolean method5329(float f) {
      return this.method5330(f, f);
   }

   public boolean method5330(float f, float f2) {
      return Math.abs(this.floatValue) <= f && Math.abs(this.floatValue2) <= f2;
   }
}
