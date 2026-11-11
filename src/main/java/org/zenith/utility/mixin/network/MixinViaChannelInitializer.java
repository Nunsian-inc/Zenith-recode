package org.zenith.utility.mixin.network;

import com.viaversion.viaversion.platform.ViaChannelInitializer;
import io.netty.channel.ChannelPipeline;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.utility.network.ViaInboundFlowControl;

@Mixin(value = ViaChannelInitializer.class, remap = false)
public class MixinViaChannelInitializer {
   @Inject(method = "reorderPipeline", at = @At("RETURN"))
   private static void zenith$restoreInboundFlowControl(ChannelPipeline pipeline, String compressor, String decompressor, CallbackInfo callbackInfo) {
      ViaInboundFlowControl.restore(pipeline);
   }
}
