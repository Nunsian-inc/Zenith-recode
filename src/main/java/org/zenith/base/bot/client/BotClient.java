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

   private BotClient(BotClientConfig config, BotClientEvents events) {
      super("BotClient/" + config.name(), false);
      this.config = config;
      this.events = events;
      this.modules = new BotModuleManager(this);
      this.thread = new Thread(this::run, "bot-client-" + config.name());
      this.thread.setDaemon(true);
   }

   public static BotClient start(BotClientConfig offline, BotClientEvents chatReportingEvents) {
      BotClient botClient = new BotClient(offline, chatReportingEvents);
      botClient.thread.start();
      return botClient;
   }

   private void run() {
      try {
         BotConnection connect = BotConnection.connect(this.config.host(), this.config.port(), this.config.proxy(), this.config.protocolVersion());
         this.connection = connect;
         if (!this.running.get()) {
            connect.disconnect(Component.literal("connection cancelled"));
            return;
         }

         BotLoginHandler botLoginHandler = new BotLoginHandler(this, connect);
         this.setPhase(BotPhase.LOGIN);
         connect.startLogin(this.config.host(), this.config.port(), botLoginHandler);
         connect.send(new ServerboundHelloPacket(this.config.name(), this.config.uuid()));
      } catch (Exception e) {
         LOGGER.warn("Bot {} connect failed", this.config.name(), e);
         this.finishDisconnect(Component.literal(describeFailure(e)));
         return;
      }

      this.nextTickAtMs = Util.getMillis();
      this.lastTickEndMs = this.nextTickAtMs;

      while (this.running.get()) {
         this.getPacketApplyBatcher().processQueuedPackets();
         long millis = Util.getMillis();

         while (millis < this.nextTickAtMs && this.pollTask()) {
            millis = Util.getMillis();
         }

         if (millis >= this.nextTickAtMs) {
            int n = 0;

            while (millis >= this.nextTickAtMs && n++ < 10) {
               this.nextTickAtMs += 50L;
               synchronized (this.renderStateLock) {
                  this.tick();
                  this.lastTickEndMs = Util.getMillis();
               }
            }

            if (millis >= this.nextTickAtMs) {
               this.nextTickAtMs = millis + 50L;
            }
         } else {
            LockSupport.parkNanos(this, Math.min((this.nextTickAtMs - millis) * 1000000L, 2000000L));
         }
      }

      this.shutdownConnection();
      this.finishDisconnect(Component.literal("bot client stopped"));
   }

   private void tick() {
      BotConnection connection = this.connection;
      if (connection != null) {
         if (connection.isOpen()) {
            connection.tick();
         } else {
            connection.handleDisconnection();
         }
      }
   }

   private void shutdownConnection() {
      BotConnection connection = this.connection;
      if (connection != null) {
         if (connection.isOpen()) {
            connection.disconnect(Component.literal("bot client stopped"));
         }

         connection.handleDisconnection();
      }
   }

   private static String describeFailure(Exception e) {
      String message = e.getMessage();
      return message != null && !message.isBlank() ? message : e.getClass().getSimpleName();
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
      float f = (float)(Util.getMillis() - this.lastTickEndMs) / 50.0F;
      return f < 0.0F ? 0.0F : Math.min(f, 1.0F);
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

   public void addSentMessage(String anObject) {
      synchronized (this.sentMessages) {
         if (this.sentMessages.isEmpty() || !this.sentMessages.get(this.sentMessages.size() - 1).equals(anObject)) {
            this.sentMessages.add(anObject);
         }
      }
   }

   public void systemMessage(String str) {
      this.onChat(Component.literal("§7[§f" + this.config.name() + "§7] §e" + str));
   }

   public boolean sendChat(String text) {
      BotPlayHandler playHandler = this.playHandler;
      if (this.isJoined() && playHandler != null) {
         if (text.startsWith("/")) {
            playHandler.sendCommand(text.substring(1));
         } else {
            playHandler.sendChatMessage(text);
         }

         return true;
      } else {
         return false;
      }
   }

   public void disconnect(String text) {
      BotConnection connection = this.connection;
      if (connection != null && connection.isOpen()) {
         connection.disconnect(Component.literal(text));
      }

      if (this.running.compareAndSet(true, false)) {
         LockSupport.unpark(this.thread);
      }
   }

   public void setPhase(BotPhase phase) {
      BotPhase botPhase;
      synchronized (this.phaseLock) {
         botPhase = this.phase;
         if (botPhase == phase || botPhase == BotPhase.DISCONNECTED) {
            return;
         }

         this.phase = phase;
      }

      this.notifyEvents(() -> this.events.onPhaseChanged(this, botPhase, phase), "onPhaseChanged");
   }

   public void setPlayHandler(BotPlayHandler playHandler) {
      this.playHandler = playHandler;
   }

   public void onGameJoin(BotWorld world, BotPlayer player) {
      this.world = world;
      this.player = player;
      this.setPhase(BotPhase.PLAY);
      this.eventBus.call(new BotJoinEvent(this, world, player));
      this.notifyEvents(() -> this.events.onJoined(this), "onJoined");
   }

   public void onRespawn(BotWorld world, BotPlayer player) {
      boolean flag = this.world != world;
      this.world = world;
      this.player = player;
      if (flag) {
         this.eventBus.call(new BotJoinEvent(this, world, player));
      } else {
         this.eventBus.call(new BotRespawnEvent(this, world, player));
      }
   }

   public void onBotUpdate(BotWorld world, BotPlayer player) {
      this.rct.tick(world, player);
      this.eventBus.call(new BotUpdateEvent(this, world, player));
   }

   public void onWorldUnload() {
      this.world = null;
      this.player = null;
   }

   public void onChat(Component decorate) {
      synchronized (this.chatLog) {
         this.chatLog.addLast(new ChatMessage(LocalTime.now().format(CHAT_TIME_FORMAT), decorate));

         while (this.chatLog.size() > 200) {
            this.chatLog.removeFirst();
         }
      }

      this.rct.onGameMessage(decorate);
      this.eventBus.call(new BotChatEvent(this, decorate));
      this.notifyEvents(() -> this.events.onChat(this, decorate), "onChat");
   }

   public void onConnectionTerminated(DisconnectionDetails info) {
      this.finishDisconnect(info.reason());
      if (this.running.compareAndSet(true, false)) {
         LockSupport.unpark(this.thread);
      }
   }

   private void finishDisconnect(Component literal) {
      if (this.disconnectNotified.compareAndSet(false, true)) {
         this.setPhase(BotPhase.DISCONNECTED);
         this.eventBus.call(new BotDisconnectEvent(this, literal));

         try {
            this.modules.disableAll();
         } catch (RuntimeException e) {
            LOGGER.error("Bot {} module shutdown failed", this.config.name(), e);
         }

         this.world = null;
         this.player = null;
         this.notifyEvents(() -> this.events.onDisconnected(this, literal), "onDisconnected");
      }
   }

   private void notifyEvents(Runnable runnable, String str) {
      try {
         runnable.run();
      } catch (RuntimeException e) {
         LOGGER.error("Bot {} events callback {} failed", new Object[]{this.config.name(), str, e});
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
