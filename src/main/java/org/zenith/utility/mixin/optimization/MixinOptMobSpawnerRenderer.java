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
      SpawnerRenderState var1,
      float var2,
      Entity var3,
      EntityRenderDispatcher var4,
      double var5,
      double var7,
      Operation<Void> var9,
      SpawnerBlockEntity var10,
      SpawnerRenderState var11,
      float var12,
      Vec3 var13
   ) {
      Util27.method7106(var10, var13, var1, var2, var3, var4, var5, var7, var9);
   }
}
