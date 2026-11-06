package org.zenith.utility.misc;

import lombok.Generated;
import net.minecraft.world.phys.Vec3;
import org.zenith.ZenithColor;

public abstract class Particle implements RenderableParticle {
   public Vec3 vec3d;
   public Vec3 vec3d2;
   public Vec3 vec3d3;
   public int intValue;
   public int intValue2;
   public final int intValue3;
   public final float floatValue;
   public ZenithColor zenithColor;
   public final String text;
   public float floatValue2;
   public final float floatValue3;

   public Particle(Vec3 vec3d, Vec3 vec3d3, int intValue, float floatValue, ZenithColor zenithColor, String text, float nextFloat, float f) {
      this.vec3d = vec3d;
      this.vec3d2 = vec3d;
      this.vec3d3 = vec3d3;
      this.intValue = intValue;
      this.intValue2 = intValue;
      this.intValue3 = intValue;
      this.floatValue = floatValue;
      this.zenithColor = zenithColor;
      this.text = text;
      this.floatValue2 = nextFloat;
      this.floatValue3 = f;
   }

   @Override
   public boolean isExpired() {
      return this.intValue <= 0;
   }

   public void method6570() {
      this.floatValue2 = this.floatValue2 + this.floatValue3;
   }

   public float method6571(float f) {
      float f2 = this.intValue2 + (this.intValue - this.intValue2) * f;
      return 1.0F - f2 / this.intValue3;
   }

   public void method6572(Vec3 vec) {
      double d = this.vec3d.distanceTo(vec);
      this.intValue -= d > 64.0 ? 8 : 1;
   }

   @Generated
   @Override
   public Vec3 getPosition() {
      return this.vec3d;
   }

   @Generated
   @Override
   public Vec3 getPreviousPosition() {
      return this.vec3d2;
   }

   @Generated
   public Vec3 getVec3d3() {
      return this.vec3d3;
   }

   @Generated
   public int getIntValue() {
      return this.intValue;
   }

   @Generated
   public int getIntValue2() {
      return this.intValue2;
   }

   @Generated
   public int getIntValue3() {
      return this.intValue3;
   }

   @Generated
   @Override
   public float getSize() {
      return this.floatValue;
   }

   @Generated
   @Override
   public ZenithColor getColor() {
      return this.zenithColor;
   }

   @Generated
   @Override
   public String getTextureKey() {
      return this.text;
   }

   @Generated
   @Override
   public float getRotationDegrees() {
      return this.floatValue2;
   }

   @Generated
   public float getFloatValue3() {
      return this.floatValue3;
   }

   @Generated
   public void setVec3d(Vec3 vec3d) {
      this.vec3d = vec3d;
   }

   @Generated
   public void setVec3d2(Vec3 vec3d2) {
      this.vec3d2 = vec3d2;
   }

   @Generated
   public void setVec3d3(Vec3 vec3d3) {
      this.vec3d3 = vec3d3;
   }

   @Generated
   public void setIntValue(int intValue) {
      this.intValue = intValue;
   }

   @Generated
   public void setIntValue2(int n) {
      this.intValue2 = n;
   }

   @Generated
   public void setZenithColor(ZenithColor zenithColor) {
      this.zenithColor = zenithColor;
   }

   @Generated
   public void setFloatValue2(float f) {
      this.floatValue2 = f;
   }
}
