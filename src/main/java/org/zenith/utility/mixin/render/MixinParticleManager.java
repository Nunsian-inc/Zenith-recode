package org.zenith.utility.mixin.render;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.zenith.client.modules.impl.render.NoRenderModule;
import org.zenith.utility.mixin.accessors.ParticleAccessor;

@Mixin({ParticleEngine.class})
public class MixinParticleManager {
   @Shadow
   public ClientLevel level;

   @Inject(
      method = {"createParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)Lnet/minecraft/client/particle/Particle;"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void zenith$dropParticleWithoutManagerWorld(
      ParticleOptions particleOptions, double d, double d2, double d3, double d4, double d5, double d6, CallbackInfoReturnable<Particle> callbackInfoReturnable
   ) {
      if (this.level == null) {
         callbackInfoReturnable.setReturnValue(null);
      }
   }

   @Inject(
      method = {"createTrackingEmitter(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/core/particles/ParticleOptions;I)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void zenith$hideTotemEmitter(Entity entity, ParticleOptions particleOptions, int n, CallbackInfo callbackInfo) {
      if (particleOptions.getType() == ParticleTypes.TOTEM_OF_UNDYING && NoRenderModule.INSTANCE.method4532()) {
         callbackInfo.cancel();
      }
   }

   @Inject(
      method = {"createParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)Lnet/minecraft/client/particle/Particle;"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void zenith$hideTotemParticle(
      ParticleOptions particleOptions, double d, double d2, double d3, double d4, double d5, double d6, CallbackInfoReturnable<Particle> callbackInfoReturnable
   ) {
      if (particleOptions.getType() == ParticleTypes.TOTEM_OF_UNDYING && NoRenderModule.INSTANCE.method4532()) {
         callbackInfoReturnable.setReturnValue(null);
      }
   }

   @Inject(
      method = {"add(Lnet/minecraft/client/particle/Particle;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void zenith$dropWorldlessParticle(Particle particle, CallbackInfo callbackInfo) {
      if (particle == null || ((ParticleAccessor)(Object)particle).zenith$getWorld() == null) {
         callbackInfo.cancel();
      }
   }
}
