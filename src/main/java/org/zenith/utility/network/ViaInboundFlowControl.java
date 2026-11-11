package org.zenith.utility.network;

import com.viaversion.viafabricplus.protocoltranslator.netty.NoReadFlowControlHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.flow.FlowControlHandler;
import java.util.List;

public final class ViaInboundFlowControl {
   private ViaInboundFlowControl() {
   }

   public static void restore(ChannelPipeline pipeline) {
      List<String> names = pipeline.names();
      String oldName = pipeline.get("zenith_via_flow_control") != null ? "zenith_via_flow_control" : "via-flow-control";
      if (!(pipeline.get(oldName) instanceof FlowControlHandler) || !names.contains("via-decoder")
         || names.indexOf(oldName) > names.indexOf("via-decoder")) {
         return;
      }

      // Перестановка после включения сжатия не должна оставлять очередь перед декодером Via.
      String newName = oldName.equals("via-flow-control") ? "zenith_via_flow_control" : "via-flow-control";
      NoReadFlowControlHandler replacement = new NoReadFlowControlHandler();
      pipeline.addAfter("via-decoder", newName, replacement);
      ChannelHandlerContext replacementContext = pipeline.context(newName);
      // handlerRemoved выгружает очередь. Передаём её напрямую, без повторного сжатия и перевода Via.
      pipeline.replace(oldName, "zenith_via_flow_transfer", new ChannelInboundHandlerAdapter() {
         @Override
         public void channelRead(ChannelHandlerContext context, Object message) throws Exception {
            replacement.channelRead(replacementContext, message);
         }

         @Override
         public void channelReadComplete(ChannelHandlerContext context) throws Exception {
            replacement.channelReadComplete(replacementContext);
         }
      });
      pipeline.remove("zenith_via_flow_transfer");
   }
}
