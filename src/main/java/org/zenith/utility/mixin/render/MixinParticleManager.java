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
      ParticleOptions var1, double var2, double var4, double var6, double var8, double var10, double var12, CallbackInfoReturnable<Particle> var14
   ) {
      if (this.level == null) {
         var14.setReturnValue(null);
      }
   }

   @Inject(
      method = {"createTrackingEmitter(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/core/particles/ParticleOptions;I)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void zenith$hideTotemEmitter(Entity var1, ParticleOptions var2, int var3, CallbackInfo var4) {
      if (var2.getType() == ParticleTypes.TOTEM_OF_UNDYING && NoRenderModule.INSTANCE.method4532()) {
         var4.cancel();
      }
   }

   @Inject(
      method = {"createParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)Lnet/minecraft/client/particle/Particle;"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void zenith$hideTotemParticle(
      ParticleOptions var1, double var2, double var4, double var6, double var8, double var10, double var12, CallbackInfoReturnable<Particle> var14
   ) {
      if (var1.getType() == ParticleTypes.TOTEM_OF_UNDYING && NoRenderModule.INSTANCE.method4532()) {
         var14.setReturnValue(null);
      }
   }

   @Inject(
      method = {"add(Lnet/minecraft/client/particle/Particle;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void zenith$dropWorldlessParticle(Particle var1, CallbackInfo var2) {
      if (var1 == null || ((ParticleAccessor)(Object)var1).zenith$getWorld() == null) {
         var2.cancel();
      }
   }
}
