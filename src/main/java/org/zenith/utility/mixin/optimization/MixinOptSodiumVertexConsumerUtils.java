package org.zenith.utility.mixin.optimization;

import com.mojang.blaze3d.vertex.VertexConsumer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.zenith.utility.misc.OptimizationFlags;

@Pseudo
@Mixin(
   targets = {"net/caffeinemc/mods/sodium/client/render/vertex/VertexConsumerUtils"},
   remap = false
)
public class MixinOptSodiumVertexConsumerUtils {
   @Unique
   private static volatile VertexConsumer zenith$lastWriter;

   @Inject(
      method = {"convertOrLog"},
      at = {@At("HEAD")},
      cancellable = true,
      remap = false,
      require = 0
   )
   private static void zenith$cachedWriter(VertexConsumer returnValue, CallbackInfoReturnable<Object> callbackInfoReturnable) {
      if (OptimizationFlags.flag14 && returnValue != null && returnValue == zenith$lastWriter) {
         callbackInfoReturnable.setReturnValue(returnValue);
      }
   }

   @Inject(
      method = {"convertOrLog"},
      at = {@At("RETURN")},
      remap = false,
      require = 0
   )
   private static void zenith$rememberWriter(VertexConsumer consumer, CallbackInfoReturnable<Object> callbackInfoReturnable) {
      if (callbackInfoReturnable.getReturnValue() == consumer) {
         zenith$lastWriter = consumer;
      }
   }
}
