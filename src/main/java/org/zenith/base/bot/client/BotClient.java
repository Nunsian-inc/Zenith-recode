package org.zenith.base.bot.client;

import com.mojang.logging.LogUtils;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;
import net.minecraft.network.DisconnectionDetails;
import net.minecraft.network.PacketProcessor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.login.ServerboundHelloPacket;
import net.minecraft.util.Util;
import net.minecraft.util.thread.ReentrantBlockableEventLoop;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.zenith.base.bot.modules.BotModuleManager;
import org.zenith.base.bot.net.BotConnection;
import org.zenith.base.bot.net.BotLoginHandler;
import org.zenith.base.bot.net.BotPlayHandler;
import org.zenith.base.bot.world.BotPlayer;
import org.zenith.base.bot.world.BotWorld;
import org.zenith.client.events.BotChatEvent;
import org.zenith.client.events.BotDisconnectEvent;
import org.zenith.client.events.BotJoinEvent;
import org.zenith.client.events.BotRespawnEvent;
import org.zenith.client.events.BotUpdateEvent;

public final class BotClient extends ReentrantBlockableEventLoop<Runnable> {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final long TICK_LENGTH_MS = 50L;
   private static final int MAX_TICKS_PER_LOOP = 10;
   private static final long MAX_IDLE_PARK_NANOS = 2000000L;
   private static final DateTimeFormatter CHAT_TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");
   private static final int CHAT_LOG_LIMIT = 200;
   private final BotClientConfig config;
   private final BotClientEvents events;
   private final Thread thread;
   private final BotEventBus eventBus = new BotEventBus();
   private final BotModuleManager modules;
   private final BotRctService rct = new BotRctService(this);
   private final Deque<ChatMessage> chatLog = new ArrayDeque<>();
   private final List<String> sentMessages = new ArrayList<>();
   private final AtomicBoolean running = new AtomicBoolean(true);
   private final AtomicBoolean disconnectNotified = new AtomicBoolean();
   private final Object phaseLock = new Object();
   private final Object renderStateLock = new Object();
   private volatile long nextTickAtMs = Util.getMillis();
   private volatile long lastTickEndMs = Util.getMillis();
   private volatile BotPhase phase = BotPhase.CONNECTING;
   @Nullable
   private volatile BotConnection connection;
   @Nullable
   private volatile BotPlayHandler playHandler;
   @Nullable
   private volatile BotWorld world;
   @Nullable
   private volatile BotPlayer player;
   private PacketProcessor packetApplyBatcher;

   private BotClient(BotClientConfig var1, BotClientEvents var2) {
      super("BotClient/" + var1.name(), false);
      this.config = var1;
      this.events = var2;
      this.modules = new BotModuleManager(this);
      this.thread = new Thread(this::run, "bot-client-" + var1.name());
      this.thread.setDaemon(true);
   }

   public static BotClient start(BotClientConfig var0, BotClientEvents var1) {
      BotClient var2 = new BotClient(var0, var1);
      var2.thread.start();
      return var2;
   }

   private void run() {
      try {
         BotConnection var1 = BotConnection.connect(this.config.host(), this.config.port(), this.config.proxy(), this.config.protocolVersion());
         this.connection = var1;
         if (!this.running.get()) {
            var1.disconnect(Component.literal("connection cancelled"));
            return;
         }

         BotLoginHandler var2 = new BotLoginHandler(this, var1);
         this.setPhase(BotPhase.LOGIN);
         var1.startLogin(this.config.host(), this.config.port(), var2);
         var1.send(new ServerboundHelloPacket(this.config.name(), this.config.uuid()));
      } catch (Exception var7) {
         LOGGER.warn("Bot {} connect failed", this.config.name(), var7);
         this.finishDisconnect(Component.literal(describeFailure(var7)));
         return;
      }

      this.nextTickAtMs = Util.getMillis();
      this.lastTickEndMs = this.nextTickAtMs;

      while (this.running.get()) {
         this.getPacketApplyBatcher().processQueuedPackets();
         long var8 = Util.getMillis();

         while (var8 < this.nextTickAtMs && this.pollTask()) {
            var8 = Util.getMillis();
         }

         if (var8 >= this.nextTickAtMs) {
            int var3 = 0;

            while (var8 >= this.nextTickAtMs && var3++ < 10) {
               this.nextTickAtMs += 50L;
               synchronized (this.renderStateLock) {
                  this.tick();
                  this.lastTickEndMs = Util.getMillis();
               }
            }

            if (var8 >= this.nextTickAtMs) {
               this.nextTickAtMs = var8 + 50L;
            }
         } else {
            LockSupport.parkNanos(this, Math.min((this.nextTickAtMs - var8) * 1000000L, 2000000L));
         }
      }

      this.shutdownConnection();
      this.finishDisconnect(Component.literal("bot client stopped"));
   }

   private void tick() {
      BotConnection var1 = this.connection;
      if (var1 != null) {
         if (var1.isOpen()) {
            var1.tick();
         } else {
            var1.handleDisconnection();
         }
      }
   }

   private void shutdownConnection() {
      BotConnection var1 = this.connection;
      if (var1 != null) {
         if (var1.isOpen()) {
            var1.disconnect(Component.literal("bot client stopped"));
         }

         var1.handleDisconnection();
      }
   }

   private static String describeFailure(Exception var0) {
      String var1 = var0.getMessage();
      return var1 != null && !var1.isBlank() ? var1 : var0.getClass().getSimpleName();
   }

   @Override
   public String name() {
      return this.config.name();
   }

   public BotClientConfig getConfig() {
      return this.config;
   }

   public BotPhase getPhase() {
      return this.phase;
   }

   public boolean isJoined() {
      return this.phase == BotPhase.PLAY;
   }

   public float getTickDelta() {
      float var1 = (float)(Util.getMillis() - this.lastTickEndMs) / 50.0F;
      return var1 < 0.0F ? 0.0F : Math.min(var1, 1.0F);
   }

   public Object getRenderStateLock() {
      return this.renderStateLock;
   }

   @Nullable
   public BotWorld getWorld() {
      return this.world;
   }

   @Nullable
   public BotPlayer getPlayer() {
      return this.player;
   }

   @Nullable
   public BotPlayHandler getPlayHandler() {
      return this.playHandler;
   }

   @Nullable
   public BotConnection getConnection() {
      return this.connection;
   }

   public BotEventBus getEventBus() {
      return this.eventBus;
   }

   public BotModuleManager getModules() {
      return this.modules;
   }

   public BotRctService getRct() {
      return this.rct;
   }

   public String getAddress() {
      return this.config.host() + ":" + this.config.port();
   }

   public List<ChatMessage> getChatLog() {
      synchronized (this.chatLog) {
         return new ArrayList<>(this.chatLog);
      }
   }

   public List<String> getSentMessages() {
      synchronized (this.sentMessages) {
         return new ArrayList<>(this.sentMessages);
      }
   }

   public void addSentMessage(String var1) {
      synchronized (this.sentMessages) {
         if (this.sentMessages.isEmpty() || !this.sentMessages.get(this.sentMessages.size() - 1).equals(var1)) {
            this.sentMessages.add(var1);
         }
      }
   }

   public void systemMessage(String var1) {
      this.onChat(Component.literal("§7[§f" + this.config.name() + "§7] §e" + var1));
   }

   public boolean sendChat(String var1) {
      BotPlayHandler var2 = this.playHandler;
      if (this.isJoined() && var2 != null) {
         if (var1.startsWith("/")) {
            var2.sendCommand(var1.substring(1));
         } else {
            var2.sendChatMessage(var1);
         }

         return true;
      } else {
         return false;
      }
   }

   public void disconnect(String var1) {
      BotConnection var2 = this.connection;
      if (var2 != null && var2.isOpen()) {
         var2.disconnect(Component.literal(var1));
      }

      if (this.running.compareAndSet(true, false)) {
         LockSupport.unpark(this.thread);
      }
   }

   public void setPhase(BotPhase var1) {
      BotPhase var2;
      synchronized (this.phaseLock) {
         var2 = this.phase;
         if (var2 == var1 || var2 == BotPhase.DISCONNECTED) {
            return;
         }

         this.phase = var1;
      }

      this.notifyEvents(() -> this.events.onPhaseChanged(this, var2, var1), "onPhaseChanged");
   }

   public void setPlayHandler(BotPlayHandler var1) {
      this.playHandler = var1;
   }

   public void onGameJoin(BotWorld var1, BotPlayer var2) {
      this.world = var1;
      this.player = var2;
      this.setPhase(BotPhase.PLAY);
      this.eventBus.call(new BotJoinEvent(this, var1, var2));
      this.notifyEvents(() -> this.events.onJoined(this), "onJoined");
   }

   public void onRespawn(BotWorld var1, BotPlayer var2) {
      boolean var3 = this.world != var1;
      this.world = var1;
      this.player = var2;
      if (var3) {
         this.eventBus.call(new BotJoinEvent(this, var1, var2));
      } else {
         this.eventBus.call(new BotRespawnEvent(this, var1, var2));
      }
   }

   public void onBotUpdate(BotWorld var1, BotPlayer var2) {
      this.rct.tick(var1, var2);
      this.eventBus.call(new BotUpdateEvent(this, var1, var2));
   }

   public void onWorldUnload() {
      this.world = null;
      this.player = null;
   }

   public void onChat(Component var1) {
      synchronized (this.chatLog) {
         this.chatLog.addLast(new ChatMessage(LocalTime.now().format(CHAT_TIME_FORMAT), var1));

         while (this.chatLog.size() > 200) {
            this.chatLog.removeFirst();
         }
      }

      this.rct.onGameMessage(var1);
      this.eventBus.call(new BotChatEvent(this, var1));
      this.notifyEvents(() -> this.events.onChat(this, var1), "onChat");
   }

   public void onConnectionTerminated(DisconnectionDetails var1) {
      this.finishDisconnect(var1.reason());
      if (this.running.compareAndSet(true, false)) {
         LockSupport.unpark(this.thread);
      }
   }

   private void finishDisconnect(Component var1) {
      if (this.disconnectNotified.compareAndSet(false, true)) {
         this.setPhase(BotPhase.DISCONNECTED);
         this.eventBus.call(new BotDisconnectEvent(this, var1));

         try {
            this.modules.disableAll();
         } catch (RuntimeException var3) {
            LOGGER.error("Bot {} module shutdown failed", this.config.name(), var3);
         }

         this.world = null;
         this.player = null;
         this.notifyEvents(() -> this.events.onDisconnected(this, var1), "onDisconnected");
      }
   }

   private void notifyEvents(Runnable var1, String var2) {
      try {
         var1.run();
      } catch (RuntimeException var4) {
         LOGGER.error("Bot {} events callback {} failed", new Object[]{this.config.name(), var2, var4});
      }
   }

   @Override
   public Runnable wrapRunnable(Runnable runnable) {
      return runnable;
   }

   @Override
   public boolean shouldRun(Runnable task) {
      return true;
   }

   @Override
   public Thread getRunningThread() {
      return this.thread;
   }

   public synchronized PacketProcessor getPacketApplyBatcher() {
      if (this.packetApplyBatcher == null) {
         this.packetApplyBatcher = new PacketProcessor(this.thread);
      }

      return this.packetApplyBatcher;
   }
}
