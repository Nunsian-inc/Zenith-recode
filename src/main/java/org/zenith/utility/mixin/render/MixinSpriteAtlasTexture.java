package org.zenith.utility.mixin.render;

import net.minecraft.client.renderer.texture.TextureAtlas;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.utility.misc.FoliageSprites;
import org.zenith.utility.render.AtlasProfiler;

@Mixin({TextureAtlas.class})
public class MixinSpriteAtlasTexture {
   @Inject(
      method = {"uploadInitialContents()V"},
      at = {@At("TAIL")}
   )
   private void zenith$applyAnisotropy(CallbackInfo var1) {
      AtlasProfiler.method7952((TextureAtlas)(Object)this);
      LoggerFactory.getLogger("Zenith/Render").info("snow foliage painted: {} sprites", FoliageSprites.intValue);
   }
}
