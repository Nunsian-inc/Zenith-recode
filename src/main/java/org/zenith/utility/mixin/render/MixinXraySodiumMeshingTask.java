package org.zenith.utility.mixin.render;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.zenith.client.modules.impl.player.XrayBypassModule;

@Pseudo
@Mixin(targets = "net.caffeinemc.mods.sodium.client.render.chunk.compile.tasks.ChunkBuilderMeshingTask", remap = false)
public class MixinXraySodiumMeshingTask {
   @ModifyExpressionValue(method = "execute", at = @At(value = "INVOKE", target = "Lnet/caffeinemc/mods/sodium/client/world/LevelSlice;getBlockState(III)Lnet/minecraft/world/level/block/state/BlockState;"), remap = false)
   private BlockState zenith$xrayMeshState(BlockState state) {
      return XrayBypassModule.hidesBlock(state) ? Blocks.AIR.defaultBlockState() : state;
   }

   @ModifyExpressionValue(method = "execute", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;isSolidRender()Z"), remap = false)
   private boolean zenith$xraySectionVisibility(boolean solid) {
      return !XrayBypassModule.isVanillaRendering() && solid;
   }
}
