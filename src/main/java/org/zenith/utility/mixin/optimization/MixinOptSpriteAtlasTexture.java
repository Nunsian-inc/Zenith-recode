package org.zenith.utility.mixin.optimization;

import net.minecraft.client.renderer.texture.SpriteLoader.Preparations;
import net.minecraft.client.renderer.texture.TextureAtlas;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.utility.misc.Util20;

@Mixin({TextureAtlas.class})
public class MixinOptSpriteAtlasTexture {
   @Inject(
      method = {"upload(Lnet/minecraft/client/renderer/texture/SpriteLoader$Preparations;)V"},
      at = {@At("RETURN")}
   )
   private void zenith$onCreate(Preparations preparations, CallbackInfo callbackInfo) {
      Util20.method5135();
   }

   @Inject(
      method = {"clearTextureData()V"},
      at = {@At("RETURN")}
   )
   private void zenith$onClear(CallbackInfo callbackInfo) {
      Util20.method5135();
   }
}
