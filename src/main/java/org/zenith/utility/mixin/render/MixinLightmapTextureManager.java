package org.zenith.utility.mixin.render;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.renderer.Lightmap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;
import org.zenith.client.modules.impl.render.WorldModule;
import org.zenith.utility.misc.ModuleHookDefinitions;

// 26.3: lightmap inputs are computed in LightmapRenderStateExtractor
@Mixin({net.minecraft.client.renderer.LightmapRenderStateExtractor.class})
public class MixinLightmapTextureManager {
   @ModifyReturnValue(
      method = {"calculateDarknessScale(Lnet/minecraft/world/entity/LivingEntity;FF)F"},
      at = {@At("RETURN")}
   )
   private float zenith$darknessFactor(float returnValue) {
      return (float)ModuleHookDefinitions.method6118(ModuleHookDefinitions.class127, returnValue);
   }

   @ModifyExpressionValue(
      method = {"extract(Lnet/minecraft/client/renderer/state/LightmapRenderState;F)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/OptionInstance;get()Ljava/lang/Object;"
      )},
      slice = {@Slice(
         from = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/Options;gamma()Lnet/minecraft/client/OptionInstance;"
         )
      )}
   )
   private Object injectXRayFullBright(Object obj) {
      if (obj instanceof Double d) {
         float f = WorldModule.INSTANCE.method5644();
         Object obj2 = obj;
         if (f >= 0.0F) {
            obj2 = Math.max(d, (double)(f * 10.0F));
         }

         if (ModuleHookDefinitions.class1211.active() && obj2 instanceof Double returnValue) {
            obj2 = Math.max(0.0, Math.min(15.0, ModuleHookDefinitions.method6118(ModuleHookDefinitions.class1211, returnValue)));
         }

         return obj2;
      } else {
         return obj;
      }
   }
}
