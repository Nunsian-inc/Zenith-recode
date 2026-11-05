package org.zenith;

import lombok.Generated;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.zenith.utility.misc.Vec2f;

public class Rotation {
   private final float floatValue;
   private final float floatValue2;
   private boolean flag;
   public static final Rotation INSTANCE = new Rotation(0.0F, 0.0F);

   public Rotation(float yRot, float xRot) {
      this(yRot, xRot, false);
   }

   public Rotation(float floatValue, float xRot, boolean flag) {
      this.floatValue = floatValue;
      this.floatValue2 = Mth.clamp(xRot, -90.0F, 90.0F);
      this.flag = flag;
   }

   public static Rotation method3128(Vec3 vec, Vec3 eyePosition) {
      return method3129(vec.subtract(eyePosition));
   }

   public static Rotation method3129(Vec3 vec) {
      double x = vec.x;
      double y = vec.y;
      double z = vec.z;
      return new Rotation(
         (float)Mth.wrapDegrees(Math.toDegrees(Math.atan2(z, x)) - 90.0),
         (float)Mth.wrapDegrees(-Math.toDegrees(Math.atan2(y, Math.sqrt(x * x + z * z))))
      );
   }

   public float method3130(Rotation rotation) {
      return Math.min(this.method3131(rotation).method5327(), 180.0F);
   }

   public Vec2f method3131(Rotation rotation) {
      return new Vec2f(this.method3133(rotation.floatValue, this.floatValue), this.method3133(rotation.floatValue2, this.floatValue2));
   }

   public Vec2f method3132(Rotation rotation) {
      return new Vec2f(rotation.floatValue - this.floatValue, rotation.floatValue2 - this.floatValue2);
   }

   private float method3133(float floatValue, float f) {
      return Mth.wrapDegrees(floatValue - f);
   }

   public boolean method3134(Rotation rotation, float f) {
      return this.method3130(rotation) <= f;
   }

   public boolean isFlag() {
      return this.flag;
   }

   public Vec3 method3135() {
      return Vec3.directionFromRotation(this.floatValue2, this.floatValue);
   }

   public Rotation method3136(Rotation rotation, float f, float f2) {
      Vec2f vec2f = this.method3131(rotation);
      float f3 = vec2f.method5327();
      if (!(f3 <= 0.0F) && !Float.isNaN(f3)) {
         float max = Math.abs(vec2f.getFloatValue() / f3) * f;
         float max2 = Math.abs(vec2f.getFloatValue2() / f3) * f2;
         float f4 = Mth.clamp(vec2f.getFloatValue(), -max, max);
         float f5 = Mth.clamp(vec2f.getFloatValue2(), -max2, max2);
         return new Rotation(this.floatValue + f4, this.floatValue2 + f5);
      } else {
         return this;
      }
   }

   public boolean method3137() {
      return Float.isInfinite(this.floatValue) || Float.isNaN(this.floatValue) || Float.isInfinite(this.floatValue2) || Float.isNaN(this.floatValue2);
   }

   public static float method3138() {
      double d = Minecraft.getInstance().options.sensitivity().get() * 0.6F + 0.2F;
      return (float)(d * d * d * 8.0 * 0.15F);
   }

   public Rotation method3139(Rotation rotation) {
      if (!this.flag && !this.equals(rotation)) {
         Vec2f vec2f = rotation.method3131(this);
         float f = method3138();
         if (!Float.isNaN(f) && !Float.isInfinite(f) && !(f <= 0.0F)) {
            float f2 = Math.round(vec2f.getFloatValue() / f) * f;
            float f3 = Math.round(vec2f.getFloatValue2() / f) * f;
            return new Rotation(rotation.getFloatValue() + f2, rotation.getFloatValue2() + f3, true);
         } else {
            return this;
         }
      } else {
         return this;
      }
   }

   public Rotation method3140(float floatValue, float random) {
      return new Rotation(this.floatValue + floatValue, Mth.clamp(this.floatValue2 + random, -90.0F, 90.0F));
   }

   public Rotation method3141(Vec2f vec2f) {
      return new Rotation(this.floatValue + vec2f.getFloatValue(), Mth.clamp(this.floatValue2 + vec2f.getFloatValue2(), -90.0F, 90.0F));
   }

   @Override
   public boolean equals(Object obj) {
      return !(obj instanceof Rotation rotation) ? false : rotation.floatValue == this.floatValue && rotation.floatValue2 == this.floatValue2;
   }

   @Generated
   public float getFloatValue() {
      return this.floatValue;
   }

   @Generated
   public float getFloatValue2() {
      return this.floatValue2;
   }
}
