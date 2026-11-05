package org.zenith.base.figura.mixin.particle;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ParticleEngine.class)
public interface ParticleManagerAccessor {
   @Invoker("makeParticle")
   <T extends ParticleOptions> Particle figura$createParticle(T effect, double x, double y, double z, double velocityX, double velocityY, double velocityZ);
}
