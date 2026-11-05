package org.zenith.base.figura.mixin.particle;

import net.minecraft.client.particle.Particle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Particle.class)
public interface FiguraParticleAccessor {
   @Accessor("xo")
   void figura$setPrevPosX(double var1);

   @Accessor("yo")
   void figura$setPrevPosY(double var1);

   @Accessor("zo")
   void figura$setPrevPosZ(double var1);

   @Accessor("x")
   double figura$getX();

   @Accessor("y")
   double figura$getY();

   @Accessor("z")
   double figura$getZ();

   @Accessor("xd")
   double figura$getVelocityX();

   @Accessor("yd")
   double figura$getVelocityY();

   @Accessor("zd")
   double figura$getVelocityZ();

   @Accessor("bbWidth")
   float figura$getSpacingXZ();

   @Accessor("gravity")
   float figura$getGravity();

   @Accessor("gravity")
   void figura$setGravity(float var1);

   @Accessor("hasPhysics")
   boolean figura$hasPhysics();

   @Accessor("hasPhysics")
   void figura$setPhysics(boolean var1);
}
