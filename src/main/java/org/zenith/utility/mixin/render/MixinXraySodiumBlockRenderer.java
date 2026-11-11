package org.zenith.utility.mixin.render;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.zenith.client.modules.impl.player.XrayBypassModule;

@Pseudo
@Mixin(targets = "net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer", remap = false)
public class MixinXraySodiumBlockRenderer {
   @Unique private static final float[] zenith$xrayBrightness = {1.0F, 1.0F, 1.0F, 1.0F};

   @ModifyArg(method = "renderModel", at = @At(value = "INVOKE", target = "Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/pipeline/BlockRenderer;prepareCulling(Z)V"), index = 0, remap = false)
   private boolean zenith$xrayFaces(boolean cull) {
      return !XrayBypassModule.isVanillaRendering() && cull;
   }

   @ModifyArg(method = "processQuad", at = @At(value = "INVOKE", target = "Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/pipeline/BlockRenderer;shadeQuad(Lnet/caffeinemc/mods/sodium/client/render/model/MutableQuadViewImpl;Lnet/caffeinemc/mods/sodium/client/model/light/LightMode;ZLnet/caffeinemc/mods/sodium/client/render/model/SodiumShadeMode;)V"), index = 2, remap = false)
   private boolean zenith$xrayLight(boolean emissive) {
      return XrayBypassModule.isVanillaRendering() || emissive;
   }

   @ModifyArg(method = "processQuad", at = @At(value = "INVOKE", target = "Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/pipeline/BlockRenderer;bufferQuad(Lnet/caffeinemc/mods/sodium/client/render/model/MutableQuadViewImpl;[FLnet/caffeinemc/mods/sodium/client/render/chunk/terrain/material/Material;)V"), index = 1, remap = false)
   private float[] zenith$xrayAmbientLight(float[] brightness) {
      return XrayBypassModule.isVanillaRendering() ? zenith$xrayBrightness : brightness;
   }
}
