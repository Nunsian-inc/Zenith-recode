package org.zenith.utility.mixin.network;

import io.netty.channel.Channel;
import io.netty.handler.proxy.ProxyHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.Zenith;

@Mixin(
   targets = {"net/minecraft/network/Connection$1"}
)
public class MixinClientConnectionChannelInit {
   @Inject(
      method = {"initChannel(Lio/netty/channel/Channel;)V"},
      at = {@At("TAIL")}
   )
   private void zenith$addProxy(Channel channel, CallbackInfo callbackInfo) {
      Zenith zenith = Zenith.getInstance();
      if (zenith != null && zenith.getProxies() != null) {
         ProxyHandler handler = zenith.getProxies().createHandler();
         if (handler != null) {
            channel.pipeline().addFirst("zenith_proxy", handler);
         }
      }
   }
}
