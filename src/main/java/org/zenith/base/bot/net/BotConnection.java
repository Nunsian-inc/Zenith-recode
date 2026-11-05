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

   public static BotConnection connect(String host, int port, @Nullable String proxy) throws UnknownHostException {
      return connect(host, port, proxy, -1);
   }

   public static BotConnection connect(String host, int port, @Nullable String proxy, int viaProtocolVersion) throws UnknownHostException {
      BotConnection botConnection = new BotConnection();
      if (BotVia.isTranslationNeeded(viaProtocolVersion)) {
         if (BotVia.ensureInitialized()) {
            botConnection.viaProtocolVersion = viaProtocolVersion;
         } else {
            LOGGER.warn("ViaVersion unavailable, connecting to {}:{} with native protocol", host, port);
         }
      }

      BotProxy botProxy = BotProxy.parse(proxy);
      if (botProxy == null) {
         InetSocketAddress inetSocketAddress = resolve(host, port);
         botConnection.bootstrap(null).connect(inetSocketAddress.getAddress(), inetSocketAddress.getPort()).syncUninterruptibly();
      } else {
         ProxyHandler handler = botProxy.createHandler();
         botConnection.bootstrap(handler).connect(InetSocketAddress.createUnresolved(host, port)).syncUninterruptibly();
         handler.connectFuture().syncUninterruptibly();
      }

      return botConnection;
   }

   private static InetSocketAddress resolve(String host, int port) throws UnknownHostException {
      ServerAddress serverAddress = new ServerAddress(host, port);
      return ServerNameResolver.DEFAULT.resolveAddress(serverAddress).map(ResolvedServerAddress::asInetSocketAddress).orElseThrow(() -> new UnknownHostException(host));
   }

   private Bootstrap bootstrap(@Nullable final ProxyHandler handler) {
      return (Bootstrap)((Bootstrap)((Bootstrap)new Bootstrap().group((EventLoopGroup)IO_GROUP.get())).channel(NioSocketChannel.class))
         .handler(new ChannelInitializer<Channel>() {
            public void initChannel(Channel var1x) {
               try {
                  var1x.config().setOption(ChannelOption.TCP_NODELAY, true);
               } catch (ChannelException e) {
               }

               ChannelPipeline pipeline = var1x.pipeline();
               pipeline.addLast("timeout", new ReadTimeoutHandler(30));
               if (handler != null) {
                  pipeline.addLast("proxy", handler);
               }

               BotConnection.addClientHandlers(pipeline);
               BotConnection.this.addFlowControlHandler(pipeline);
               if (BotConnection.this.viaProtocolVersion > 0) {
                  BotVia.injectPipeline(var1x, BotConnection.this.viaProtocolVersion);
               }
            }
         });
   }

   private static void addClientHandlers(ChannelPipeline pipeline) {
      pipeline.addLast("splitter", new Varint21FrameDecoder(null))
         .addLast(new ChannelHandler[]{new FlowControlHandler()})
         .addLast("inbound_config", new Inbound())
         .addLast("prepender", new Varint21LengthFieldPrepender())
         .addLast("encoder", new PacketEncoder<ServerHandshakePacketListener>(HandshakeProtocols.SERVERBOUND));
   }

   private void addFlowControlHandler(ChannelPipeline pipeline) {
      pipeline.addLast("hackfix", new ChannelOutboundHandlerAdapter() {
         public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
            super.write(ctx, msg, promise);
         }
      }).addLast("packet_handler", this);
   }

   public void startLogin(String host, int port, ClientLoginPacketListener prePlayStateListener) {
      this.prePlayStateListener = prePlayStateListener;
      this.submit(var3x -> {
         var3x.transitionInbound(LoginProtocols.CLIENTBOUND, prePlayStateListener);
         var3x.sendImmediately(new ClientIntentionPacket(SharedConstants.getCurrentVersion().protocolVersion(), host, port, ClientIntent.LOGIN), null, true);
         var3x.transitionOutbound(LoginProtocols.SERVERBOUND);
      });
   }

   public void channelActive(ChannelHandlerContext ctx) throws Exception {
      super.channelActive(ctx);
      this.channel = ctx.channel();
      this.address = this.channel.remoteAddress();
      DisconnectionDetails pendingDisconnectionInfo = this.pendingDisconnectionInfo;
      if (pendingDisconnectionInfo != null) {
         this.disconnect(pendingDisconnectionInfo);
      }
   }

   public void channelInactive(ChannelHandlerContext ctx) {
      this.disconnect(Component.translatable("disconnect.endOfStream"));
   }

   public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
      if (cause instanceof SkipPacketEncoderException) {
         LOGGER.debug("Skipping packet due to errors", cause.getCause());
      } else {
         boolean flag = !this.errored;
         this.errored = true;
         if (this.channel != null && this.channel.isOpen()) {
            if (cause instanceof TimeoutException) {
               LOGGER.debug("Timeout", cause);
               this.disconnect(Component.translatable("disconnect.timeout"));
            } else {
               MutableComponent translatable = Component.translatable("disconnect.genericReason", "Internal Exception: " + cause);
               PacketListener packetListener = this.packetListener;
               DisconnectionDetails disconnectionInfo = packetListener != null ? packetListener.createDisconnectionInfo(translatable, cause) : new DisconnectionDetails(translatable);
               if (flag) {
                  LOGGER.debug("Failed to send packet", cause);
                  this.disconnect(disconnectionInfo);
                  this.tryDisableAutoRead();
               } else {
                  LOGGER.debug("Double fault", cause);
                  this.disconnect(disconnectionInfo);
               }
            }
         }
      }
   }

   public void channelRead0(ChannelHandlerContext channelHandlerContext, Packet<?> packet) {
      if (this.channel != null && this.channel.isOpen()) {
         PacketListener packetListener = this.packetListener;
         if (packetListener == null) {
            throw new IllegalStateException("Received a packet before the packet listener was initialized");
         } else if (packetListener.shouldHandleMessage(packet)) {
            if (packetListener instanceof BotCommonHandler botCommonHandler) {
               botCommonHandler.firePacketEvent(packet);
               if (packet instanceof BundlePacket bundlePacket) {
                  for (Object subPacket : (Iterable<?>)(Object)(bundlePacket.subPackets())) {
                     Packet subPacket2 = (Packet)subPacket;
                     botCommonHandler.firePacketEvent(subPacket2);
                  }
               }
            }

            try {
               handlePacket(packet, packetListener);
            } catch (RunningOnDifferentThreadException e) {
            } catch (RejectedExecutionException e) {
               this.disconnect(Component.translatable("multiplayer.disconnect.server_shutdown"));
            } catch (ClassCastException e) {
               LOGGER.error("Received {} that couldn't be processed", packet.getClass(), e);
               this.disconnect(Component.translatable("multiplayer.disconnect.invalid_packet"));
            }
         }
      }
   }

   private static <T extends PacketListener> void handlePacket(Packet<T> packet, PacketListener packetListener) {
      ((Packet)packet).handle(packetListener);
   }

   public <T extends PacketListener> void transitionInbound(ProtocolInfo<T> bind, T packetListener) {
      Objects.requireNonNull(packetListener, "packetListener");
      if (bind.flow() != PacketFlow.CLIENTBOUND) {
         throw new IllegalStateException("Invalid inbound protocol: " + bind.id());
      } else if (bind.id() != packetListener.protocol()) {
         throw new IllegalStateException("Listener protocol (" + packetListener.protocol() + ") does not match requested one " + bind);
      } else {
         this.packetListener = packetListener;
         this.prePlayStateListener = null;
         InboundConfigurationTask setupInboundProtocol = UnconfiguredPipelineHandler.setupInboundProtocol(bind);
         BundlerInfo bundlerInfo = bind.bundlerInfo();
         if (bundlerInfo != null) {
            PacketBundlePacker packetBundlePacker = new PacketBundlePacker(bundlerInfo);
            setupInboundProtocol = setupInboundProtocol.andThen(var1x -> var1x.pipeline().addAfter("decoder", "bundler", packetBundlePacker));
         }

         syncUninterruptibly(this.channel.writeAndFlush(setupInboundProtocol));
      }
   }

   public void transitionOutbound(ProtocolInfo<?> bind) {
      if (bind.flow() != PacketFlow.SERVERBOUND) {
         throw new IllegalStateException("Invalid outbound protocol: " + bind.id());
      } else {
         OutboundConfigurationTask setupOutboundProtocol = UnconfiguredPipelineHandler.setupOutboundProtocol(bind);
         BundlerInfo bundlerInfo = bind.bundlerInfo();
         if (bundlerInfo != null) {
            PacketBundleUnpacker packetBundleUnpacker = new PacketBundleUnpacker(bundlerInfo);
            setupOutboundProtocol = setupOutboundProtocol.andThen(var1x -> var1x.pipeline().addAfter("encoder", "unbundler", packetBundleUnpacker));
         }

         syncUninterruptibly(this.channel.writeAndFlush(setupOutboundProtocol));
      }
   }

   private static void syncUninterruptibly(ChannelFuture writeAndFlush) {
      try {
         writeAndFlush.syncUninterruptibly();
      } catch (Exception e) {
         if (!(e instanceof ClosedChannelException)) {
            throw e;
         }

         LOGGER.info("Connection closed during protocol change");
      }
   }

   public void send(Packet<?> resourcePackPacket) {
      this.send(resourcePackPacket, null);
   }

   public void send(Packet<?> resourcePackPacket, @Nullable ChannelFutureListener thenRun) {
      this.send(resourcePackPacket, thenRun, true);
   }

   public void send(Packet<?> resourcePackPacket, @Nullable ChannelFutureListener thenRun, boolean flag) {
      if (this.isOpen()) {
         this.handleQueuedTasks();
         this.sendImmediately(resourcePackPacket, thenRun, flag);
      } else {
         this.queuedTasks.add(var3x -> var3x.sendImmediately(resourcePackPacket, thenRun, flag));
      }
   }

   public void submit(Consumer<BotConnection> consumer) {
      if (this.isOpen()) {
         this.handleQueuedTasks();
         consumer.accept(this);
      } else {
         this.queuedTasks.add(consumer);
      }
   }

   private void sendImmediately(Packet<?> resourcePackPacket, @Nullable ChannelFutureListener thenRun, boolean flag) {
      if (this.channel.eventLoop().inEventLoop()) {
         this.sendInternal(resourcePackPacket, thenRun, flag);
      } else {
         this.channel.eventLoop().execute(() -> this.sendInternal(resourcePackPacket, thenRun, flag));
      }
   }

   private void sendInternal(Packet<?> resourcePackPacket, @Nullable ChannelFutureListener thenRun, boolean flag) {
      ChannelFuture writeAndFlush = flag ? this.channel.writeAndFlush(resourcePackPacket) : this.channel.write(resourcePackPacket);
      if (thenRun != null) {
         writeAndFlush.addListener(thenRun);
      }

      writeAndFlush.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
   }

   private void handleQueuedTasks() {
      if (this.channel != null && this.channel.isOpen()) {
         Consumer queuedTask;
         synchronized (this.queuedTasks) {
            while ((queuedTask = this.queuedTasks.poll()) != null) {
               queuedTask.accept(this);
            }
         }
      }
   }

   public void tick() {
      this.handleQueuedTasks();
      if (this.packetListener instanceof TickablePacketListener tickablePacketListener) {
         tickablePacketListener.tick();
      }

      if (!this.isOpen() && !this.disconnected) {
         this.handleDisconnection();
      }

      if (this.channel != null) {
         this.channel.flush();
      }
   }

   public void disconnect(Component literal) {
      this.disconnect(new DisconnectionDetails(literal));
   }

   public void disconnect(DisconnectionDetails pendingDisconnectionInfo) {
      if (this.channel == null) {
         this.pendingDisconnectionInfo = pendingDisconnectionInfo;
      }

      if (this.isOpen()) {
         this.channel.close().awaitUninterruptibly();
         this.disconnectionInfo = pendingDisconnectionInfo;
      }
   }

   public void setupEncryption(Cipher cipher, Cipher cipher2) {
      this.encrypted = true;
      this.channel.pipeline().addBefore("splitter", "decrypt", new CipherDecoder(cipher));
      this.channel.pipeline().addBefore("prepender", "encrypt", new CipherEncoder(cipher2));
   }

   public boolean isEncrypted() {
      return this.encrypted;
   }

   public void setCompressionThreshold(int compressionThreshold, boolean validateDecompressed) {
      if (compressionThreshold >= 0) {
         if (this.channel.pipeline().get("decompress") instanceof CompressionDecoder compressionDecoder) {
            compressionDecoder.setThreshold(compressionThreshold, validateDecompressed);
         } else {
            this.channel.pipeline().addAfter("splitter", "decompress", new CompressionDecoder(compressionThreshold, validateDecompressed));
         }

         if (this.channel.pipeline().get("compress") instanceof CompressionEncoder compressionEncoder) {
            compressionEncoder.setThreshold(compressionThreshold);
         } else {
            this.channel.pipeline().addAfter("prepender", "compress", new CompressionEncoder(compressionThreshold));
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
         PacketListener packetListener = this.packetListener;
         PacketListener prePlayStateListener = packetListener != null ? packetListener : this.prePlayStateListener;
         if (prePlayStateListener != null) {
            DisconnectionDetails nonNullElseGet = Objects.requireNonNullElseGet(
               this.disconnectionInfo, () -> new DisconnectionDetails(Component.translatable("multiplayer.disconnect.generic"))
            );
            prePlayStateListener.onDisconnect(nonNullElseGet);
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
