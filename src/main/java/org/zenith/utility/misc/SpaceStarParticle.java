package org.zenith.utility.misc;

import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.world.phys.Vec3;
import org.zenith.ZenithColor;

public class SpaceStarParticle extends Particle {
   private static final String text = "particle.texture.spaceStar";
   private final double doubleValue;
   private final double doubleValue2;
   private final double doubleValue3;
   private final double doubleValue4;
   private final double doubleValue5;
   private final double doubleValue6;
   private final double doubleValue7;
   private final double doubleValue8;
   private final double doubleValue9;
   private final float floatValue;
   private final float floatValue2;
   private final float floatValue3;
   private double doubleValue10;
   private float floatValue4 = 1.0F;

   private SpaceStarParticle(
      Vec3 vec3d,
      int intValue,
      float floatValue,
      ZenithColor zenithColor,
      float nextFloat,
      float f,
      double doubleValue,
      double nextDouble,
      double d,
      double d2,
      double d3,
      double d4,
      double d5,
      double d6,
      double d7,
      float floatValue2,
      float f2,
      float f3
   ) {
      super(vec3d, Vec3.ZERO, intValue, floatValue, zenithColor, "particle.texture.spaceStar", nextFloat, f);
      this.doubleValue = doubleValue;
      this.doubleValue2 = nextDouble;
      this.doubleValue3 = d;
      this.doubleValue4 = d2;
      this.doubleValue5 = d3;
      this.doubleValue6 = d4;
      this.doubleValue7 = d5;
      this.doubleValue8 = d6;
      this.doubleValue9 = d7;
      this.floatValue = floatValue2;
      this.floatValue2 = f2;
      this.floatValue3 = f3;
   }

   public static SpaceStarParticle method3077(Vec3 vec3d, float f, int intValue, float f2, ZenithColor zenithColor) {
      ThreadLocalRandom current = ThreadLocalRandom.current();
      double d = Math.pow(current.nextDouble(0.08, 1.0), 0.72);
      double doubleValue = Math.max(6.0, f * d);
      double nextDouble = current.nextDouble(-f * 0.34, f * 0.42);
      double nextDouble2 = current.nextDouble(Math.PI * 2);
      double d2 = current.nextInt(4) * (Math.PI / 2);
      double d3 = current.nextDouble(0.0015, 0.0075) * (current.nextBoolean() ? 1.0 : -1.0);
      double nextDouble3 = current.nextDouble(Math.PI * 2);
      double nextDouble4 = current.nextDouble(0.015, 0.035);
      double nextDouble5 = current.nextDouble(1.2, 4.8);
      double nextDouble6 = current.nextDouble(0.8, 3.6);
      float floatValue = f2 * current.nextFloat(0.55F, 1.65F);
      float nextFloat = current.nextFloat(0.0F, 360.0F);
      float nextFloat2 = current.nextFloat(-0.7F, 0.7F);
      float nextFloat3 = current.nextFloat(0.65F, 1.35F);
      float nextFloat4 = current.nextFloat(0.05F, 0.12F);
      float nextFloat5 = current.nextFloat(0.0F, (float) (Math.PI * 2));
      SpaceStarParticle spaceStarParticle = new SpaceStarParticle(
         vec3d, intValue, floatValue, zenithColor, nextFloat, nextFloat2, doubleValue, nextDouble, nextDouble2, d2, d3, nextDouble3, nextDouble4, nextDouble5, nextDouble6, nextFloat3, nextFloat4, nextFloat5
      );
      spaceStarParticle.doubleValue10 = current.nextDouble(0.0, 260.0);
      spaceStarParticle.intValue = current.nextInt(Math.max(2, intValue / 3), intValue);
      spaceStarParticle.intValue2 = spaceStarParticle.intValue;
      spaceStarParticle.vec3d = spaceStarParticle.method3079(vec3d);
      spaceStarParticle.vec3d2 = spaceStarParticle.vec3d;
      return spaceStarParticle;
   }

   public void method3078(Vec3 vec3d, float f) {
      this.intValue2 = this.intValue;
      this.vec3d2 = this.vec3d;
      this.intValue--;
      if (this.intValue > 0) {
         this.floatValue4 = Math.max(0.05F, f);
         this.doubleValue10 = this.doubleValue10 + this.floatValue4;
         this.vec3d = this.method3079(vec3d);
         this.vec3d3 = this.vec3d.subtract(this.vec3d2);
         this.method6570();
      }
   }

   private Vec3 method3079(Vec3 vec3d) {
      double d = this.doubleValue10;
      double d2 = this.doubleValue * 0.055;
      double d3 = this.doubleValue3 + this.doubleValue4 + d2 + d * this.doubleValue5;
      double d4 = this.doubleValue + Math.sin(d * this.doubleValue7 + this.doubleValue6) * this.doubleValue8;
      double x = Math.cos(d3) * d4;
      double z = Math.sin(d3) * d4;
      double y = 4.5
         + this.doubleValue2
         + Math.sin(d * this.doubleValue7 * 1.7 + this.doubleValue6) * this.doubleValue9
         + Math.sin(d3 * 2.0 + this.doubleValue6) * 0.85;
      return vec3d.add(x, y, z);
   }

   @Override
   public float getAlpha() {
      return this.getInterpolatedAlpha(1.0F);
   }

   @Override
   public float getInterpolatedAlpha(float f) {
      float f2 = this.method6571(f);
      float f3 = Math.min(f2 / 0.12F, 1.0F);
      float f4 = f2 > 0.82F ? 1.0F - (f2 - 0.82F) / 0.18F : 1.0F;
      float f5 = 0.62F + 0.38F * this.method3080(f);
      return Math.max(0.0F, f3 * f4 * f5);
   }

   public float method3080(float f) {
      return (float)((Math.sin((this.doubleValue10 + f * this.floatValue4) * this.floatValue2 + this.floatValue3) + 1.0) * 0.5);
   }

   public float method3081(float f) {
      return this.floatValue * (4.0F + this.method3080(f) * 2.5F) * this.floatValue;
   }

   public float method3082(float f) {
      return this.floatValue * (0.65F + this.method3080(f) * 0.35F);
   }

   public float method3083(float f) {
      return this.floatValue * (1.35F + this.method3080(f) * 0.45F);
   }

   public float method3084(float f) {
      return Math.min(10.0F, (float)this.vec3d3.length() * 18.0F * f);
   }
}
