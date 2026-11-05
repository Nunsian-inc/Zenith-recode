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
import org.zenith.utility.misc.Util30;

@Mixin(
   targets = {"net/minecraft/client/resources/MapTextureManager$MapInstance"}
)
public abstract class MixinOptMapTexture implements Util30.Handler2 {
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
      Supplier<String> var1, int var2, int var3, boolean var4, Operation<DynamicTexture> var5, @Local(argsOnly = true) MapTextureManager var6
   ) {
      if (OptimizationFlags.flag18 && var2 == 128 && var3 == 128 && var6 instanceof Util30.Handler var7) {
         MapAtlas.Data var8 = var7.zenith$getAtlasPool().method6151(var7.zenith$getTextureManager());
         if (var8 != null) {
            this.zenith$atlasSlot = var8;
            return null;
         }
      }

      return (DynamicTexture)var5.call(new Object[]{var1, var2, var3, var4});
   }

   @WrapOperation(
      method = {"<init>(Lnet/minecraft/client/resources/MapTextureManager;ILnet/minecraft/world/level/saveddata/maps/MapItemSavedData;)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/resources/Identifier;withDefaultNamespace(Ljava/lang/String;)Lnet/minecraft/resources/Identifier;"
      )}
   )
   private Identifier zenith$atlasTextureId(String var1, Operation<Identifier> var2) {
      return this.zenith$atlasSlot != null ? this.zenith$atlasSlot.method6153() : (Identifier)var2.call(new Object[]{var1});
   }

   @WrapOperation(
      method = {"<init>(Lnet/minecraft/client/resources/MapTextureManager;ILnet/minecraft/world/level/saveddata/maps/MapItemSavedData;)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/texture/TextureManager;register(Lnet/minecraft/resources/Identifier;Lnet/minecraft/client/renderer/texture/AbstractTexture;)V"
      )}
   )
   private void zenith$skipRegister(TextureManager var1, Identifier var2, AbstractTexture var3, Operation<Void> var4) {
      if (this.zenith$atlasSlot == null) {
         var4.call(new Object[]{var1, var2, var3});
      }
   }

   @Inject(
      method = {"updateTextureIfNeeded()V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void zenith$updateAtlas(CallbackInfo var1) {
      MapAtlas.Data var2 = this.zenith$atlasSlot;
      if (var2 != null) {
         var1.cancel();
         if (this.requiresUpload) {
            var2.getMapAtlasPage().method5417(var2.getIntValue(), var2.getIntValue2(), this.data.colors);
            this.requiresUpload = false;
         }
      }
   }

   @Inject(
      method = {"close()V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void zenith$keepAtlas(CallbackInfo var1) {
      if (this.zenith$atlasSlot != null) {
         var1.cancel();
      }
   }
}
