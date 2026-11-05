package org.zenith.utility.mixin.optimization;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.renderer.blockentity.SpawnerRenderer;
import net.minecraft.client.renderer.blockentity.state.SpawnerRenderState;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.zenith.utility.render.Util27;

@Mixin({SpawnerRenderer.class})
public class MixinOptMobSpawnerRenderer {
   @WrapOperation(
      method = {"extractRenderState(Lnet/minecraft/world/level/block/entity/SpawnerBlockEntity;Lnet/minecraft/client/renderer/blockentity/state/SpawnerRenderState;FLnet/minecraft/world/phys/Vec3;Lnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/blockentity/TrialSpawnerRenderer;extractSpawnerData(Lnet/minecraft/client/renderer/blockentity/state/SpawnerRenderState;FLnet/minecraft/world/entity/Entity;Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;DD)V"
      )}
   )
   private void zenith$spawnerEntity(
      SpawnerRenderState spawnerRenderState,
      float alpha1,
      Entity entity,
      EntityRenderDispatcher entityRenderDispatcher,
      double d,
      double d2,
      Operation<Void> operation,
      SpawnerBlockEntity spawnerBlockEntity,
      SpawnerRenderState spawnerRenderState2,
      float f,
      Vec3 vec
   ) {
      Util27.method7106(spawnerBlockEntity, vec, spawnerRenderState, alpha1, entity, entityRenderDispatcher, d, d2, operation);
   }
}
