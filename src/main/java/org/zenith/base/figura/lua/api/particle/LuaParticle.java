package org.zenith.base.figura.lua.api.particle;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SingleQuadParticle;
import org.zenith.base.figura.lua.LuaWhitelist;
import org.zenith.base.figura.lua.docs.LuaTypeDoc;
import org.zenith.base.figura.math.vector.FiguraVec3;
import org.zenith.base.figura.math.vector.FiguraVec4;
import org.zenith.base.figura.mixin.particle.BillboardParticleAccessor;
import org.zenith.base.figura.mixin.particle.FiguraParticleAccessor;
import org.zenith.base.figura.utils.LuaUtils;

@LuaWhitelist
@LuaTypeDoc(name = "Particle", value = "particle")
public final class LuaParticle {
   private final String name;
   private final Particle particle;
   private final ParticleAPI owner;
   private float power;

   LuaParticle(String name, Particle particle, ParticleAPI owner) {
      this.name = name;
      this.particle = particle;
      this.owner = owner;
   }

   private FiguraParticleAccessor accessor() {
      return (FiguraParticleAccessor)this.particle;
   }

   @LuaWhitelist
   public LuaParticle spawn() {
      this.owner.spawn(this.particle);
      return this;
   }

   @LuaWhitelist
   public LuaParticle remove() {
      this.particle.remove();
      this.owner.forget(this.particle);
      return this;
   }

   @LuaWhitelist
   public boolean isAlive() {
      return this.particle.isAlive();
   }

   @LuaWhitelist
   public FiguraVec3 getPos() {
      FiguraParticleAccessor value = this.accessor();
      return FiguraVec3.of(value.figura$getX(), value.figura$getY(), value.figura$getZ());
   }

   @LuaWhitelist
   public LuaParticle setPos(Object x, Double y, Double z) {
      FiguraVec3 value = LuaUtils.parseVec3("setPos", x, y, z);
      this.particle.setPos(value.x, value.y, value.z);
      FiguraParticleAccessor accessor = this.accessor();
      accessor.figura$setPrevPosX(value.x);
      accessor.figura$setPrevPosY(value.y);
      accessor.figura$setPrevPosZ(value.z);
      return this;
   }

   @LuaWhitelist
   public LuaParticle pos(Object x, Double y, Double z) {
      return this.setPos(x, y, z);
   }

   @LuaWhitelist
   public FiguraVec3 getVelocity() {
      FiguraParticleAccessor value = this.accessor();
      return FiguraVec3.of(value.figura$getVelocityX(), value.figura$getVelocityY(), value.figura$getVelocityZ());
   }

   @LuaWhitelist
   public LuaParticle setVelocity(Object x, Double y, Double z) {
      FiguraVec3 value = LuaUtils.parseVec3("setVelocity", x, y, z);
      this.particle.setParticleSpeed(value.x, value.y, value.z);
      return this;
   }

   @LuaWhitelist
   public LuaParticle velocity(Object x, Double y, Double z) {
      return this.setVelocity(x, y, z);
   }

   @LuaWhitelist
   public FiguraVec4 getColor() {
      if (this.particle instanceof SingleQuadParticle billboard) {
         BillboardParticleAccessor value = (BillboardParticleAccessor)billboard;
         return FiguraVec4.of(value.figura$getRed(), value.figura$getGreen(), value.figura$getBlue(), value.figura$getAlpha());
      } else {
         return FiguraVec4.of(1.0, 1.0, 1.0, 1.0);
      }
   }

   @LuaWhitelist
   public LuaParticle setColor(Object red, Double green, Double blue, Double alpha) {
      FiguraVec4 value = LuaUtils.parseVec4("setColor", red, green, blue, alpha, 1.0, 1.0, 1.0, 1.0);
      if (this.particle instanceof SingleQuadParticle billboard) {
         billboard.setColor((float)value.x, (float)value.y, (float)value.z);
         ((BillboardParticleAccessor)billboard).figura$setAlpha((float)value.w);
      }

      return this;
   }

   @LuaWhitelist
   public LuaParticle color(Object r, Double g, Double b, Double a) {
      return this.setColor(r, g, b, a);
   }

   @LuaWhitelist
   public int getLifetime() {
      return this.particle.getLifetime();
   }

   @LuaWhitelist
   public LuaParticle setLifetime(int lifetime) {
      this.particle.setLifetime(Math.max(lifetime, 0));
      return this;
   }

   @LuaWhitelist
   public LuaParticle lifetime(int lifetime) {
      return this.setLifetime(lifetime);
   }

   @LuaWhitelist
   public float getPower() {
      return this.power;
   }

   @LuaWhitelist
   public LuaParticle setPower(float power) {
      this.particle.setPower(power);
      this.power = power;
      return this;
   }

   @LuaWhitelist
   public LuaParticle power(float power) {
      return this.setPower(power);
   }

   @LuaWhitelist
   public float getScale() {
      return this.accessor().figura$getSpacingXZ() / 0.2F;
   }

   @LuaWhitelist
   public float getSize() {
      return this.getScale();
   }

   @LuaWhitelist
   public LuaParticle setScale(float scale) {
      if (this.particle instanceof SingleQuadParticle billboard) {
         ((BillboardParticleAccessor)billboard).figura$setScale(0.2F);
      }

      this.particle.scale(scale);
      return this;
   }

   @LuaWhitelist
   public LuaParticle scale(float scale) {
      return this.setScale(scale);
   }

   @LuaWhitelist
   public LuaParticle setSize(float scale) {
      return this.setScale(scale);
   }

   @LuaWhitelist
   public LuaParticle size(float scale) {
      return this.setScale(scale);
   }

   @LuaWhitelist
   public float getGravity() {
      return this.accessor().figura$getGravity();
   }

   @LuaWhitelist
   public LuaParticle setGravity(float gravity) {
      this.accessor().figura$setGravity(gravity);
      return this;
   }

   @LuaWhitelist
   public LuaParticle gravity(float gravity) {
      return this.setGravity(gravity);
   }

   @LuaWhitelist
   public boolean hasPhysics() {
      return this.accessor().figura$hasPhysics();
   }

   @LuaWhitelist
   public LuaParticle setPhysics(boolean physics) {
      this.accessor().figura$setPhysics(physics);
      return this;
   }

   @LuaWhitelist
   public LuaParticle physics(boolean physics) {
      return this.setPhysics(physics);
   }

   @Override
   public String toString() {
      return this.name + " (Particle)";
   }
}
