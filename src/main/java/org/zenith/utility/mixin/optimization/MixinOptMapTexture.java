package org.zenith.utility.mixin.optimization;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import java.util.function.Supplier;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.MapTextureManager;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.utility.misc.MapAtlas;
import org.zenith.utility.misc.OptimizationFlags;
import org.zenith.utility.misc.MapAtlasAccess;

@Mixin(
   targets = {"net/minecraft/client/resources/MapTextureManager$MapInstance"}
)
public abstract class MixinOptMapTexture implements MapAtlasAccess.Handler2 {
   @Shadow
   private MapItemSavedData data;
   @Shadow
   private boolean requiresUpload;
   @Unique
   private MapAtlas.Data zenith$atlasSlot;

   @Nullable
   @Override
   public MapAtlas.Data zenith$getAtlasSlot() {
      return this.zenith$atlasSlot;
   }

   @WrapOperation(
      method = {"<init>(Lnet/minecraft/client/resources/MapTextureManager;ILnet/minecraft/world/level/saveddata/maps/MapItemSavedData;)V"},
      at = {@At(
         value = "NEW",
         target = "(Ljava/util/function/Supplier;IIZ)Lnet/minecraft/client/renderer/texture/DynamicTexture;"
      )}
   )
   private DynamicTexture zenith$allocateInAtlas(
      Supplier<String> supplier, int n, int n2, boolean flag, Operation<DynamicTexture> operation, @Local(argsOnly = true) MapTextureManager mapTextureManager
   ) {
      if (OptimizationFlags.flag18 && n == 128 && n2 == 128 && mapTextureManager instanceof MapAtlasAccess.Handler handler) {
         MapAtlas.Data zenith$atlasSlot = handler.zenith$getAtlasPool().method6151(handler.zenith$getTextureManager());
         if (zenith$atlasSlot != null) {
            this.zenith$atlasSlot = zenith$atlasSlot;
            return null;
         }
      }

      return (DynamicTexture)operation.call(new Object[]{supplier, n, n2, flag});
   }

   @WrapOperation(
      method = {"<init>(Lnet/minecraft/client/resources/MapTextureManager;ILnet/minecraft/world/level/saveddata/maps/MapItemSavedData;)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/resources/Identifier;withDefaultNamespace(Ljava/lang/String;)Lnet/minecraft/resources/Identifier;"
      )}
   )
   private Identifier zenith$atlasTextureId(String str, Operation<Identifier> operation) {
      return this.zenith$atlasSlot != null ? this.zenith$atlasSlot.method6153() : (Identifier)operation.call(new Object[]{str});
   }

   @WrapOperation(
      method = {"<init>(Lnet/minecraft/client/resources/MapTextureManager;ILnet/minecraft/world/level/saveddata/maps/MapItemSavedData;)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/texture/TextureManager;register(Lnet/minecraft/resources/Identifier;Lnet/minecraft/client/renderer/texture/AbstractTexture;)V"
      )}
   )
   private void zenith$skipRegister(TextureManager textureManager, Identifier id, AbstractTexture abstractTexture, Operation<Void> operation) {
      if (this.zenith$atlasSlot == null) {
         operation.call(new Object[]{textureManager, id, abstractTexture});
      }
   }

   @Inject(
      method = {"updateTextureIfNeeded()V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void zenith$updateAtlas(CallbackInfo callbackInfo) {
      MapAtlas.Data zenith$atlasSlot = this.zenith$atlasSlot;
      if (zenith$atlasSlot != null) {
         callbackInfo.cancel();
         if (this.requiresUpload) {
            zenith$atlasSlot.getMapAtlasPage().method5417(zenith$atlasSlot.getIntValue(), zenith$atlasSlot.getIntValue2(), this.data.colors);
            this.requiresUpload = false;
         }
      }
   }

   @Inject(
      method = {"close()V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void zenith$keepAtlas(CallbackInfo callbackInfo) {
      if (this.zenith$atlasSlot != null) {
         callbackInfo.cancel();
      }
   }
}
