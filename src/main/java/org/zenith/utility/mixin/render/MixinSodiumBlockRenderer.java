package org.zenith.utility.mixin.render;

import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.utility.game.NoRenderBlocks;
import org.zenith.utility.render.CrystalAnimation;

@Pseudo
@Mixin(
   targets = {"net/caffeinemc/mods/sodium/client/render/chunk/compile/pipeline/BlockRenderer"},
   remap = false
)
public class MixinSodiumBlockRenderer {
   @Inject(
      method = {"renderModel"},
      at = {@At("HEAD")},
      cancellable = true,
      remap = false
   )
   private void zenith$thinGrass(BlockStateModel blockStateModel, BlockState blockState, BlockPos pos, BlockPos pos2, CallbackInfo callbackInfo) {
      if (NoRenderBlocks.method5237(blockState, pos) || CrystalAnimation.method4460(blockState)) {
         callbackInfo.cancel();
      }
   }
}
