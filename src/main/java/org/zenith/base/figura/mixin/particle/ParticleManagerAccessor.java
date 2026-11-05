package org.zenith.base.figura.mixin.particle;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ParticleEngine.class)
public interface ParticleManagerAccessor {
   @Invoker("makeParticle")
   <T extends ParticleOptions> Particle figura$createParticle(T var1, double var2, double var4, double var6, double var8, double var10, double var12);
}
