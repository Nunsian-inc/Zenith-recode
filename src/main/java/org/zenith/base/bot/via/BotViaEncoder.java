package org.zenith.base.bot.via;

import com.viaversion.vialoader.netty.ViaEncoder;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.ProtocolPipeline;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import java.util.List;

public final class BotViaEncoder extends ViaEncoder {
   public BotViaEncoder(UserConnection userConnectionImpl) {
      super(userConnectionImpl);
   }

   @Override
   public void encode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) {
      super.encode(channelHandlerContext, byteBuf, list);
      ProtocolPipeline pipeline = this.connection.getProtocolInfo().getPipeline();
      if (pipeline.hasNonBaseProtocols() && !pipeline.contains(BotViaCompatibilityProtocol.class)) {
         pipeline.add(BotViaCompatibilityProtocol.INSTANCE);
      }
   }
}
