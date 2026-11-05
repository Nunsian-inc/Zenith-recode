package org.zenith.utility.mixin.render;

import com.mojang.blaze3d.platform.NativeImage;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.utility.misc.FoliageSprites;

@Mixin({SpriteContents.class})
public class MixinSpriteContents {
   @Shadow
   @Final
   private NativeImage originalImage;

   @Inject(
      method = {"<init>(Lnet/minecraft/resources/Identifier;Lnet/minecraft/client/resources/metadata/animation/FrameSize;Lcom/mojang/blaze3d/platform/NativeImage;Ljava/util/Optional;Ljava/util/List;Ljava/util/Optional;)V"},
      at = {@At("RETURN")}
   )
   private void zenith$snowCap(Identifier var1, FrameSize var2, NativeImage var3, Optional<?> var4, List<?> var5, Optional<?> var6, CallbackInfo var7) {
      if (FoliageSprites.method1431(var1)) {
         FoliageSprites.method1432(var1, this.originalImage);
      }
   }
}
