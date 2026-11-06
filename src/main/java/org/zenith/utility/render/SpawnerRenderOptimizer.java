package org.zenith.utility.render;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.renderer.blockentity.state.SpawnerRenderState;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.zenith.utility.misc.OptimizationFlags;
import org.zenith.utility.misc.BlockEntityOptimizationState;

public final class SpawnerRenderOptimizer {
   private SpawnerRenderOptimizer() {
   }

   public static void extractDisplayEntity(
      BlockEntity blockEntity,
      Vec3 cameraPosition,
      SpawnerRenderState spawnerRenderState,
      float partialTick,
      Entity displayEntity,
      EntityRenderDispatcher entityRenderDispatcher,
      double previousSpin,
      double spin,
      Operation<Void> originalOperation
   ) {
      if (displayEntity != null && OptimizationFlags.flag8 && cameraPosition != null) {
         BlockPos blockPos = blockEntity.getBlockPos();
         double deltaX = blockPos.getX() + 0.5 - cameraPosition.x;
         double deltaY = blockPos.getY() + 0.5 - cameraPosition.y;
         double deltaZ = blockPos.getZ() + 0.5 - cameraPosition.z;
         if (deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ > OptimizationFlags.doubleValue2) {
            spawnerRenderState.displayEntity = null;
            return;
         }
      }

      if (displayEntity != null && OptimizationFlags.flag9) {
         BlockEntityOptimizationState.Entry cacheEntry = BlockEntityOptimizationState.getOptimizationData(blockEntity);
         long frameId = BlockEntityOptimizationState.longValue;
         EntityRenderState cachedRenderState = cacheEntry.entityRenderState;
         if (cachedRenderState != null && cacheEntry.longValue4 == frameId && cacheEntry.entity == displayEntity) {
            spawnerRenderState.displayEntity = cachedRenderState;
            cachedRenderState.lightCoords = spawnerRenderState.lightCoords;
            spawnerRenderState.spin = (float)Mth.lerp((double)partialTick, previousSpin, spin) * 10.0F;
            float scale = 0.53125F;
            float maxEntityDimension = Math.max(displayEntity.getBbWidth(), displayEntity.getBbHeight());
            if (maxEntityDimension > 1.0) {
               scale /= maxEntityDimension;
            }

            spawnerRenderState.scale = scale;
         } else {
            originalOperation.call(new Object[]{spawnerRenderState, partialTick, displayEntity, entityRenderDispatcher, previousSpin, spin});
            cacheEntry.entityRenderState = spawnerRenderState.displayEntity;
            cacheEntry.entity = displayEntity;
            cacheEntry.longValue4 = frameId;
         }
      } else {
         originalOperation.call(new Object[]{spawnerRenderState, partialTick, displayEntity, entityRenderDispatcher, previousSpin, spin});
      }
   }
}
