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
      BlockEntity var0,
      Vec3 var1,
      SpawnerRenderState var2,
      float var3,
      Entity var4,
      EntityRenderDispatcher var5,
      double var6,
      double var8,
      Operation<Void> var10
   ) {
      if (var4 != null && OptimizationFlags.flag8 && var1 != null) {
         BlockPos var11 = var0.getBlockPos();
         double var12 = var11.getX() + 0.5 - var1.x;
         double var14 = var11.getY() + 0.5 - var1.y;
         double var16 = var11.getZ() + 0.5 - var1.z;
         if (var12 * var12 + var14 * var14 + var16 * var16 > OptimizationFlags.doubleValue2) {
            var2.displayEntity = null;
            return;
         }
      }

      if (var4 != null && OptimizationFlags.flag9) {
         Util20.Entry var18 = Util20.method5139(var0);
         long var19 = Util20.longValue;
         EntityRenderState var20 = var18.entityRenderState;
         if (var20 != null && var18.longValue4 == var19 && var18.entity == var4) {
            var2.displayEntity = var20;
            var20.lightCoords = var2.lightCoords;
            var2.spin = (float)Mth.lerp((double)var3, var6, var8) * 10.0F;
            float var15 = 0.53125F;
            float var21 = Math.max(var4.getBbWidth(), var4.getBbHeight());
            if (var21 > 1.0) {
               var15 /= var21;
            }

            var2.scale = var15;
         } else {
            var10.call(new Object[]{var2, var3, var4, var5, var6, var8});
            var18.entityRenderState = var2.displayEntity;
            var18.entity = var4;
            var18.longValue4 = var19;
         }
      } else {
         var10.call(new Object[]{var2, var3, var4, var5, var6, var8});
      }
   }
}
