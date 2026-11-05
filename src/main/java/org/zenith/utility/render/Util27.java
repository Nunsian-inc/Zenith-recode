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
import org.zenith.utility.misc.Util20;

public final class Util27 {
   private Util27() {
   }

   public static void method7106(
      BlockEntity blockEntity,
      Vec3 vec,
      SpawnerRenderState spawnerRenderState,
      float alpha1,
      Entity entity,
      EntityRenderDispatcher entityRenderDispatcher,
      double d,
      double d2,
      Operation<Void> operation
   ) {
      if (entity != null && OptimizationFlags.flag8 && vec != null) {
         BlockPos blockPos = blockEntity.getBlockPos();
         double d3 = blockPos.getX() + 0.5 - vec.x;
         double d4 = blockPos.getY() + 0.5 - vec.y;
         double d5 = blockPos.getZ() + 0.5 - vec.z;
         if (d3 * d3 + d4 * d4 + d5 * d5 > OptimizationFlags.doubleValue2) {
            spawnerRenderState.displayEntity = null;
            return;
         }
      }

      if (entity != null && OptimizationFlags.flag9) {
         Util20.Entry entry = Util20.method5139(blockEntity);
         long longValue = Util20.longValue;
         EntityRenderState entityRenderState = entry.entityRenderState;
         if (entityRenderState != null && entry.longValue4 == longValue && entry.entity == entity) {
            spawnerRenderState.displayEntity = entityRenderState;
            entityRenderState.lightCoords = spawnerRenderState.lightCoords;
            spawnerRenderState.spin = (float)Mth.lerp((double)alpha1, d, d2) * 10.0F;
            float scale = 0.53125F;
            float f = Math.max(entity.getBbWidth(), entity.getBbHeight());
            if (f > 1.0) {
               scale /= f;
            }

            spawnerRenderState.scale = scale;
         } else {
            operation.call(new Object[]{spawnerRenderState, alpha1, entity, entityRenderDispatcher, d, d2});
            entry.entityRenderState = spawnerRenderState.displayEntity;
            entry.entity = entity;
            entry.longValue4 = longValue;
         }
      } else {
         operation.call(new Object[]{spawnerRenderState, alpha1, entity, entityRenderDispatcher, d, d2});
      }
   }
}
