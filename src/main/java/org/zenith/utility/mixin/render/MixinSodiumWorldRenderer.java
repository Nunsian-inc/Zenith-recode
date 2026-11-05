package org.zenith.utility.mixin.render;

import java.lang.reflect.Field;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(
   targets = {"net/caffeinemc/mods/sodium/client/render/SodiumWorldRenderer"},
   remap = false
)
public class MixinSodiumWorldRenderer {
   @Unique
   private static Field zenith$renderSectionManagerField;
   @Unique
   private static boolean zenith$renderSectionManagerLookupFailed;

   @Inject(
      method = {"scheduleRebuildForChunk"},
      at = {@At("HEAD")},
      cancellable = true,
      remap = false
   )
   private void zenith$skipUninitializedRebuild(int n, int n2, int n3, boolean flag, CallbackInfo callbackInfo) {
      if (this.zenith$isRenderSectionManagerMissing()) {
         callbackInfo.cancel();
      }
   }

   @Unique
   private boolean zenith$isRenderSectionManagerMissing() {
      if (zenith$renderSectionManagerLookupFailed) {
         return false;
      } else {
         Field field = zenith$renderSectionManagerField;
         if (field == null) {
            try {
               field = this.getClass().getDeclaredField("renderSectionManager");
               field.setAccessible(true);
               zenith$renderSectionManagerField = field;
            } catch (Exception e) {
               zenith$renderSectionManagerLookupFailed = true;
               return false;
            }
         }

         try {
            return field.get(this) == null;
         } catch (Exception e) {
            return false;
         }
      }
   }
}
