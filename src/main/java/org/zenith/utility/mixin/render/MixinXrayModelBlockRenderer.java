package org.zenith.utility.mixin.render;

import com.mojang.blaze3d.vertex.QuadInstance;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.BlockQuadOutput;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.zenith.client.modules.impl.player.XrayBypassModule;

@Mixin(ModelBlockRenderer.class)
public class MixinXrayModelBlockRenderer {
   @Shadow @Final private QuadInstance quadInstance;

   @Inject(method = "shouldRenderFace", at = @At("HEAD"), cancellable = true)
   private void zenith$xrayFaces(BlockAndTintGetter level, BlockState state, Direction direction, BlockPos pos, CallbackInfoReturnable<Boolean> callback) {
      if (XrayBypassModule.isVanillaRendering()) {
         callback.setReturnValue(true);
      }
   }

   @Inject(method = "putQuadWithTint", at = @At("HEAD"))
   private void zenith$xrayLight(BlockQuadOutput output, float x, float y, float z, BlockAndTintGetter level, BlockState state, BlockPos pos, BakedQuad quad, CallbackInfo callback) {
      if (XrayBypassModule.isVanillaRendering()) {
         this.quadInstance.setColor(-1);
         this.quadInstance.setLightCoords(0xF000F0);
      }
   }
}
