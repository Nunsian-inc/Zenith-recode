package org.zenith.utility.misc;

public class Vec2f {
   private final float floatValue;
   private final float floatValue2;

   public Vec2f(float floatValue, float f) {
      this.floatValue = floatValue;
      this.floatValue2 = f;
   }

   public float length() {
      return (float)Math.sqrt(this.floatValue * this.floatValue + this.floatValue2 * this.floatValue2);
   }

   public float getFloatValue() {
      return this.floatValue;
   }

   public float getFloatValue2() {
      return this.floatValue2;
   }

   public net.minecraft.world.phys.Vec2 toMinecraftVec2() {
      return new net.minecraft.world.phys.Vec2(this.floatValue, this.floatValue2);
   }

   public boolean isWithinBounds(float f) {
      return this.isWithinBounds(f, f);
   }

   public boolean isWithinBounds(float f, float f2) {
      return Math.abs(this.floatValue) <= f && Math.abs(this.floatValue2) <= f2;
   }
}
