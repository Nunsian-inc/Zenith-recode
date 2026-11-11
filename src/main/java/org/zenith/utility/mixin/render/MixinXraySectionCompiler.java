package org.zenith.utility.mixin.render;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.renderer.chunk.SectionCompiler;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.zenith.client.modules.impl.player.XrayBypassModule;

@Mixin(SectionCompiler.class)
public class MixinXraySectionCompiler {
   @ModifyExpressionValue(method = "compile", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/chunk/RenderSectionRegion;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"))
   private BlockState zenith$xrayMeshState(BlockState state) {
      // Подмена только локального аргумента компилятора: мир и коллизии остаются нетронутыми.
      return XrayBypassModule.hidesBlock(state) ? Blocks.AIR.defaultBlockState() : state;
   }

   @ModifyExpressionValue(method = "compile", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;isSolidRender()Z"))
   private boolean zenith$xraySectionVisibility(boolean solid) {
      return !XrayBypassModule.isVanillaRendering() && solid;
   }
}
