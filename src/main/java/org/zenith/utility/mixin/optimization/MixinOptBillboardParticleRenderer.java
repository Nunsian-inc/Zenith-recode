package org.zenith.utility.mixin.optimization;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Camera;
import net.minecraft.client.particle.QuadParticleGroup;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.zenith.utility.misc.OptimizationFlags;
import org.zenith.utility.render.FluidColorCache;

@Mixin({QuadParticleGroup.class})
public abstract class MixinOptBillboardParticleRenderer {
   @WrapOperation(
      method = {"extractRenderState(Lnet/minecraft/client/renderer/culling/Frustum;Lnet/minecraft/client/Camera;F)Lnet/minecraft/client/renderer/state/level/ParticleGroupRenderState;"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/culling/Frustum;pointInFrustum(DDD)Z"
      )}
   )
   private boolean zenith$lod(
      Frustum frustum, double d, double d2, double d3, Operation<Boolean> operation, Frustum frustum2, Camera camera, float f, @Local SingleQuadParticle singleQuadParticle
   ) {
      if (!(Boolean)operation.call(new Object[]{frustum, d, d2, d3})) {
         return false;
      } else if (!OptimizationFlags.flag3) {
         return true;
      } else {
         Vec3 position = camera.position();
         double d4 = d - position.x;
         double d5 = d2 - position.y;
         double d6 = d3 - position.z;
         return FluidColorCache.method7102(singleQuadParticle, d4 * d4 + d5 * d5 + d6 * d6);
      }
   }
}
