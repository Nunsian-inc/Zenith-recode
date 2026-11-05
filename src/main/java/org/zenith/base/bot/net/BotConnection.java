package org.zenith.base.bot.net;

import com.google.common.base.Suppliers;
import com.google.common.collect.Queues;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.logging.LogUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.flow.FlowControlHandler;
import io.netty.handler.proxy.ProxyHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.TimeoutException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.channels.ClosedChannelException;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.RejectedExecutionException;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.crypto.Cipher;
import net.minecraft.SharedConstants;
import net.minecraft.client.multiplayer.resolver.ResolvedServerAddress;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.client.multiplayer.resolver.ServerNameResolver;
import net.minecraft.network.CipherDecoder;
import net.minecraft.network.CipherEncoder;
import net.minecraft.network.CompressionDecoder;
import net.minecraft.network.CompressionEncoder;
import net.minecraft.network.DisconnectionDetails;
import net.minecraft.network.PacketBundlePacker;
import net.minecraft.network.PacketBundleUnpacker;
import net.minecraft.network.PacketEncoder;
import net.minecraft.network.PacketListener;
import net.minecraft.network.ProtocolInfo;
import net.minecraft.network.SkipPacketEncoderException;
import net.minecraft.network.TickablePacketListener;
import net.minecraft.network.UnconfiguredPipelineHandler;
import net.minecraft.network.UnconfiguredPipelineHandler.Inbound;
import net.minecraft.network.UnconfiguredPipelineHandler.InboundConfigurationTask;
import net.minecraft.network.UnconfiguredPipelineHandler.OutboundConfigurationTask;
import net.minecraft.network.Varint21FrameDecoder;
import net.minecraft.network.Varint21LengthFieldPrepender;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.BundlePacket;
import net.minecraft.network.protocol.BundlerInfo;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.handshake.ClientIntent;
import net.minecraft.network.protocol.handshake.ClientIntentionPacket;
import net.minecraft.network.protocol.handshake.HandshakeProtocols;
import net.minecraft.network.protocol.handshake.ServerHandshakePacketListener;
import net.minecraft.network.protocol.login.ClientLoginPacketListener;
import net.minecraft.network.protocol.login.LoginProtocols;
import net.minecraft.server.RunningOnDifferentThreadException;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.zenith.base.bot.via.BotVia;

public final class BotConnection extends SimpleChannelInboundHandler<Packet<?>> {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int READ_TIMEOUT_SECONDS = 30;
   private static final Supplier<NioEventLoopGroup> IO_GROUP = Suppliers.memoize(
      () -> new NioEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Bot Netty IO #%d").setDaemon(true).build())
   );
   private final Queue<Consumer<BotConnection>> queuedTasks = Queues.newConcurrentLinkedQueue();
   private Channel channel;
   private SocketAddress address;
   @Nullable
   private volatile PacketListener prePlayStateListener;
   @Nullable
   private volatile PacketListener packetListener;
   @Nullable
   private DisconnectionDetails disconnectionInfo;
   @Nullable
   private volatile DisconnectionDetails pendingDisconnectionInfo;
   private boolean encrypted;
   private boolean disconnected;
   private boolean errored;
   private int viaProtocolVersion = -1;

   private BotConnection() {
   }

   public static BotConnection connect(String var0, int var1, @Nullable String var2) throws UnknownHostException {
      return connect(var0, var1, var2, -1);
   }

   public static BotConnection connect(String var0, int var1, @Nullable String var2, int var3) throws UnknownHostException {
      BotConnection var4 = new BotConnection();
      if (BotVia.isTranslationNeeded(var3)) {
         if (BotVia.ensureInitialized()) {
            var4.viaProtocolVersion = var3;
         } else {
            LOGGER.warn("ViaVersion unavailable, connecting to {}:{} with native protocol", var0, var1);
         }
      }

      BotProxy var5 = BotProxy.parse(var2);
      if (var5 == null) {
         InetSocketAddress var6 = resolve(var0, var1);
         var4.bootstrap(null).connect(var6.getAddress(), var6.getPort()).syncUninterruptibly();
      } else {
         ProxyHandler var7 = var5.createHandler();
         var4.bootstrap(var7).connect(InetSocketAddress.createUnresolved(var0, var1)).syncUninterruptibly();
         var7.connectFuture().syncUninterruptibly();
      }

      return var4;
   }

   private static InetSocketAddress resolve(String var0, int var1) throws UnknownHostException {
      ServerAddress var2 = new ServerAddress(var0, var1);
      return ServerNameResolver.DEFAULT.resolveAddress(var2).map(ResolvedServerAddress::asInetSocketAddress).orElseThrow(() -> new UnknownHostException(var0));
   }

   private Bootstrap bootstrap(@Nullable final ProxyHandler var1) {
      return (Bootstrap)((Bootstrap)((Bootstrap)new Bootstrap().group((EventLoopGroup)IO_GROUP.get())).channel(NioSocketChannel.class))
         .handler(new ChannelInitializer<Channel>() {
            public void initChannel(Channel var1x) {
               try {
                  var1x.config().setOption(ChannelOption.TCP_NODELAY, true);
               } catch (ChannelException var3) {
               }

               ChannelPipeline var2 = var1x.pipeline();
               var2.addLast("timeout", new ReadTimeoutHandler(30));
               if (var1 != null) {
                  var2.addLast("proxy", var1);
               }

               BotConnection.addClientHandlers(var2);
               BotConnection.this.addFlowControlHandler(var2);
               if (BotConnection.this.viaProtocolVersion > 0) {
                  BotVia.injectPipeline(var1x, BotConnection.this.viaProtocolVersion);
               }
            }
         });
   }

   private static void addClientHandlers(ChannelPipeline var0) {
      var0.addLast("splitter", new Varint21FrameDecoder(null))
         .addLast(new ChannelHandler[]{new FlowControlHandler()})
         .addLast("inbound_config", new Inbound())
         .addLast("prepender", new Varint21LengthFieldPrepender())
         .addLast("encoder", new PacketEncoder<ServerHandshakePacketListener>(HandshakeProtocols.SERVERBOUND));
   }

   private void addFlowControlHandler(ChannelPipeline var1) {
      var1.addLast("hackfix", new ChannelOutboundHandlerAdapter() {
         public void write(ChannelHandlerContext var1, Object var2, ChannelPromise var3) throws Exception {
            super.write(var1, var2, var3);
         }
      }).addLast("packet_handler", this);
   }

   public void startLogin(String var1, int var2, ClientLoginPacketListener var3) {
      this.prePlayStateListener = var3;
      this.submit(var3x -> {
         var3x.transitionInbound(LoginProtocols.CLIENTBOUND, var3);
         var3x.sendImmediately(new ClientIntentionPacket(SharedConstants.getCurrentVersion().protocolVersion(), var1, var2, ClientIntent.LOGIN), null, true);
         var3x.transitionOutbound(LoginProtocols.SERVERBOUND);
      });
   }

   public void channelActive(ChannelHandlerContext var1) throws Exception {
      super.channelActive(var1);
      this.channel = var1.channel();
      this.address = this.channel.remoteAddress();
      DisconnectionDetails var2 = this.pendingDisconnectionInfo;
      if (var2 != null) {
         this.disconnect(var2);
      }
   }

   public void channelInactive(ChannelHandlerContext var1) {
      this.disconnect(Component.translatable("disconnect.endOfStream"));
   }

   public void exceptionCaught(ChannelHandlerContext var1, Throwable var2) {
      if (var2 instanceof SkipPacketEncoderException) {
         LOGGER.debug("Skipping packet due to errors", var2.getCause());
      } else {
         boolean var3 = !this.errored;
         this.errored = true;
         if (this.channel != null && this.channel.isOpen()) {
            if (var2 instanceof TimeoutException) {
               LOGGER.debug("Timeout", var2);
               this.disconnect(Component.translatable("disconnect.timeout"));
            } else {
               MutableComponent var4 = Component.translatable("disconnect.genericReason", "Internal Exception: " + var2);
               PacketListener var5 = this.packetListener;
               DisconnectionDetails var6 = var5 != null ? var5.createDisconnectionInfo(var4, var2) : new DisconnectionDetails(var4);
               if (var3) {
                  LOGGER.debug("Failed to send packet", var2);
                  this.disconnect(var6);
                  this.tryDisableAutoRead();
               } else {
                  LOGGER.debug("Double fault", var2);
                  this.disconnect(var6);
               }
            }
         }
      }
   }

   public void channelRead0(ChannelHandlerContext var1, Packet<?> var2) {
      if (this.channel != null && this.channel.isOpen()) {
         PacketListener var3 = this.packetListener;
         if (var3 == null) {
            throw new IllegalStateException("Received a packet before the packet listener was initialized");
         } else if (var3.shouldHandleMessage(var2)) {
            if (var3 instanceof BotCommonHandler var4) {
               var4.firePacketEvent(var2);
               if (var2 instanceof BundlePacket var5) {
                  for (Object var7_o : (Iterable<?>)(Object)(var5.subPackets())) {
                     Packet var7 = (Packet)var7_o;
                     var4.firePacketEvent(var7);
                  }
               }
            }

            try {
               handlePacket(var2, var3);
            } catch (RunningOnDifferentThreadException var8) {
            } catch (RejectedExecutionException var9) {
               this.disconnect(Component.translatable("multiplayer.disconnect.server_shutdown"));
            } catch (ClassCastException var10) {
               LOGGER.error("Received {} that couldn't be processed", var2.getClass(), var10);
               this.disconnect(Component.translatable("multiplayer.disconnect.invalid_packet"));
            }
         }
      }
   }

   private static <T extends PacketListener> void handlePacket(Packet<T> var0, PacketListener var1) {
      ((Packet)var0).handle(var1);
   }

   public <T extends PacketListener> void transitionInbound(ProtocolInfo<T> var1, T var2) {
      Objects.requireNonNull(var2, "packetListener");
      if (var1.flow() != PacketFlow.CLIENTBOUND) {
         throw new IllegalStateException("Invalid inbound protocol: " + var1.id());
      } else if (var1.id() != var2.protocol()) {
         throw new IllegalStateException("Listener protocol (" + var2.protocol() + ") does not match requested one " + var1);
      } else {
         this.packetListener = var2;
         this.prePlayStateListener = null;
         InboundConfigurationTask var3 = UnconfiguredPipelineHandler.setupInboundProtocol(var1);
         BundlerInfo var4 = var1.bundlerInfo();
         if (var4 != null) {
            PacketBundlePacker var5 = new PacketBundlePacker(var4);
            var3 = var3.andThen(var1x -> var1x.pipeline().addAfter("decoder", "bundler", var5));
         }

         syncUninterruptibly(this.channel.writeAndFlush(var3));
      }
   }

   public void transitionOutbound(ProtocolInfo<?> var1) {
      if (var1.flow() != PacketFlow.SERVERBOUND) {
         throw new IllegalStateException("Invalid outbound protocol: " + var1.id());
      } else {
         OutboundConfigurationTask var2 = UnconfiguredPipelineHandler.setupOutboundProtocol(var1);
         BundlerInfo var3 = var1.bundlerInfo();
         if (var3 != null) {
            PacketBundleUnpacker var4 = new PacketBundleUnpacker(var3);
            var2 = var2.andThen(var1x -> var1x.pipeline().addAfter("encoder", "unbundler", var4));
         }

         syncUninterruptibly(this.channel.writeAndFlush(var2));
      }
   }

   private static void syncUninterruptibly(ChannelFuture var0) {
      try {
         var0.syncUninterruptibly();
      } catch (Exception var2) {
         if (!(var2 instanceof ClosedChannelException)) {
            throw var2;
         }

         LOGGER.info("Connection closed during protocol change");
      }
   }

   public void send(Packet<?> var1) {
      this.send(var1, null);
   }

   public void send(Packet<?> var1, @Nullable ChannelFutureListener var2) {
      this.send(var1, var2, true);
   }

   public void send(Packet<?> var1, @Nullable ChannelFutureListener var2, boolean var3) {
      if (this.isOpen()) {
         this.handleQueuedTasks();
         this.sendImmediately(var1, var2, var3);
      } else {
         this.queuedTasks.add(var3x -> var3x.sendImmediately(var1, var2, var3));
      }
   }

   public void submit(Consumer<BotConnection> var1) {
      if (this.isOpen()) {
         this.handleQueuedTasks();
         var1.accept(this);
      } else {
         this.queuedTasks.add(var1);
      }
   }

   private void sendImmediately(Packet<?> var1, @Nullable ChannelFutureListener var2, boolean var3) {
      if (this.channel.eventLoop().inEventLoop()) {
         this.sendInternal(var1, var2, var3);
      } else {
         this.channel.eventLoop().execute(() -> this.sendInternal(var1, var2, var3));
      }
   }

   private void sendInternal(Packet<?> var1, @Nullable ChannelFutureListener var2, boolean var3) {
      ChannelFuture var4 = var3 ? this.channel.writeAndFlush(var1) : this.channel.write(var1);
      if (var2 != null) {
         var4.addListener(var2);
      }

      var4.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
   }

   private void handleQueuedTasks() {
      if (this.channel != null && this.channel.isOpen()) {
         Consumer var2;
         synchronized (this.queuedTasks) {
            while ((var2 = this.queuedTasks.poll()) != null) {
               var2.accept(this);
            }
         }
      }
   }

   public void tick() {
      this.handleQueuedTasks();
      if (this.packetListener instanceof TickablePacketListener var1) {
         var1.tick();
      }

      if (!this.isOpen() && !this.disconnected) {
         this.handleDisconnection();
      }

      if (this.channel != null) {
         this.channel.flush();
      }
   }

   public void disconnect(Component var1) {
      this.disconnect(new DisconnectionDetails(var1));
   }

   public void disconnect(DisconnectionDetails var1) {
      if (this.channel == null) {
         this.pendingDisconnectionInfo = var1;
      }

      if (this.isOpen()) {
         this.channel.close().awaitUninterruptibly();
         this.disconnectionInfo = var1;
      }
   }

   public void setupEncryption(Cipher var1, Cipher var2) {
      this.encrypted = true;
      this.channel.pipeline().addBefore("splitter", "decrypt", new CipherDecoder(var1));
      this.channel.pipeline().addBefore("prepender", "encrypt", new CipherEncoder(var2));
   }

   public boolean isEncrypted() {
      return this.encrypted;
   }

   public void setCompressionThreshold(int var1, boolean var2) {
      if (var1 >= 0) {
         if (this.channel.pipeline().get("decompress") instanceof CompressionDecoder var3) {
            var3.setThreshold(var1, var2);
         } else {
            this.channel.pipeline().addAfter("splitter", "decompress", new CompressionDecoder(var1, var2));
         }

         if (this.channel.pipeline().get("compress") instanceof CompressionEncoder var5) {
            var5.setThreshold(var1);
         } else {
            this.channel.pipeline().addAfter("prepender", "compress", new CompressionEncoder(var1));
         }
      } else {
         if (this.channel.pipeline().get("decompress") instanceof CompressionDecoder) {
            this.channel.pipeline().remove("decompress");
         }

         if (this.channel.pipeline().get("compress") instanceof CompressionEncoder) {
            this.channel.pipeline().remove("compress");
         }
      }
   }

   public void handleDisconnection() {
      if (this.channel != null && !this.channel.isOpen()) {
         if (this.disconnected) {
            LOGGER.warn("handleDisconnection() called twice");
            return;
         }

         this.disconnected = true;
         PacketListener var1 = this.packetListener;
         PacketListener var2 = var1 != null ? var1 : this.prePlayStateListener;
         if (var2 != null) {
            DisconnectionDetails var3 = Objects.requireNonNullElseGet(
               this.disconnectionInfo, () -> new DisconnectionDetails(Component.translatable("multiplayer.disconnect.generic"))
            );
            var2.onDisconnect(var3);
         }
      }
   }

   public void tryDisableAutoRead() {
      if (this.channel != null) {
         this.channel.config().setAutoRead(false);
      }
   }

   public boolean isOpen() {
      return this.channel != null && this.channel.isOpen();
   }

   @Nullable
   public PacketListener getPacketListener() {
      return this.packetListener;
   }

   @Nullable
   public DisconnectionDetails getDisconnectionInfo() {
      return this.disconnectionInfo;
   }

   public SocketAddress getAddress() {
      return this.address;
   }
}
