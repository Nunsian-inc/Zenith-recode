package org.zenith.base.bot.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.proxy.ProxyHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.concurrent.Future;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.server.network.EventLoopGroupHolder;
import org.jetbrains.annotations.Nullable;
import org.zenith.ChatLogger;
import org.zenith.base.bot.modules.api.BotModule;
import org.zenith.base.bot.net.BotProxy;

public final class HeadlessBots {
   private static final long MASS_CONNECT_STAGGER_MS = 500L;
   private static final int MAX_MASS_CONNECT_BOTS = 256;
   private static final Map<String, BotClient> BOTS = new ConcurrentHashMap<>();
   private static final Map<String, HeadlessBots.SavedBot> SAVED = new LinkedHashMap<>();
   private static final Map<String, JsonObject> PROFILES = new ConcurrentHashMap<>();
   private static final ScheduledExecutorService CONNECT_DELAY_EXECUTOR = Executors.newSingleThreadScheduledExecutor(task -> {
      Thread thread = new Thread(task, "hbot-connect-delay");
      thread.setDaemon(true);
      return thread;
   });
   private static final AtomicLong CONNECT_BATCH_GENERATION = new AtomicLong();
   private static volatile String lastAddress = "";
   private static final Set<String> SAVED_PROXIES = ConcurrentHashMap.newKeySet();
   private static final Map<String, HeadlessBots.ProxyPingState> PROXY_PINGS = new ConcurrentHashMap<>();
   private static final String PROXY_PING_TARGET_HOST = "mc.holyworld.ru";
   private static final int PROXY_PING_TARGET_PORT = 25565;
   private static final long PROXY_PING_INTERVAL_MS = 10000L;
   private static final long PROXY_PING_TIMEOUT_MS = 5000L;
   private static final int MAX_PENDING_PROXY_PINGS = 64;
   private static final ThreadPoolExecutor PROXY_PING_EXECUTOR = new ThreadPoolExecutor(
      2, 2, 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(64), new ThreadFactory() {
         private int index;

         @Override
         public synchronized Thread newThread(Runnable task) {
            Thread thread = new Thread(task, "hbot-proxy-ping-" + this.index++);
            thread.setDaemon(true);
            return thread;
         }
      }
   );

   private HeadlessBots() {
   }

   public static BotClient connect(String selected, String host, int port) {
      add(selected);
      setLastAddress(host + (port == 25565 ? "" : ":" + port));
      return connect(BotClientConfig.offline(selected, host, port, getProxy(selected), getProtocolVersion(selected)));
   }

   public static BotClient connect(BotClientConfig offline) {
      String str = key(offline.name());
      BotClient botClient = BOTS.get(str);
      if (botClient != null && botClient.getPhase() != BotPhase.DISCONNECTED) {
         return botClient;
      } else {
         BotClient start = BotClient.start(offline, new HeadlessBots.ChatReportingEvents());
         BOTS.put(str, start);
         applyProfile(start);
         return start;
      }
   }

   public static void connectAll(Collection<String> offlineSelectedNames, String host, int port) {
      if (offlineSelectedNames != null && !offlineSelectedNames.isEmpty()) {
         long incrementAndGet = CONNECT_BATCH_GENERATION.incrementAndGet();
         ArrayList arrayList = new ArrayList();

         for (String offlineSelectedName : offlineSelectedNames) {
            if (arrayList.size() >= 256) {
               break;
            }

            if (offlineSelectedName != null && !offlineSelectedName.isBlank()) {
               arrayList.add(offlineSelectedName.trim());
            }
         }

         if (!arrayList.isEmpty()) {
            connectMassBatch(arrayList, 0, host, port, incrementAndGet);
         }
      }
   }

   private static void connectMassBatch(List<String> strings, int n, String host, int port, long incrementAndGet) {
      if (CONNECT_BATCH_GENERATION.get() == incrementAndGet && n < strings.size()) {
         connect((String)strings.get(n), host, port);
         int n2 = n + 1;
         if (n2 < strings.size()) {
            CONNECT_DELAY_EXECUTOR.schedule(() -> connectMassBatch(strings, n2, host, port, incrementAndGet), 500L, TimeUnit.MILLISECONDS);
         }
      }
   }

   @Nullable
   public static BotClient get(String selected) {
      return BOTS.get(key(selected));
   }

   public static Collection<BotClient> all() {
      return BOTS.values();
   }

   public static boolean isOnline(String selectedName) {
      BotClient botClient = get(selectedName);
      return botClient != null && botClient.getPhase() != BotPhase.DISCONNECTED;
   }

   public static boolean disconnect(String selectedName) {
      BotClient botClient = BOTS.remove(key(selectedName));
      if (botClient == null) {
         return false;
      } else {
         botClient.disconnect("disconnected by user");
         return true;
      }
   }

   public static void disconnectAll() {
      CONNECT_BATCH_GENERATION.incrementAndGet();

      for (BotClient botClient : BOTS.values()) {
         botClient.disconnect("disconnected by user");
      }

      BOTS.clear();
   }

   public static boolean add(String selected) {
      if (selected != null && !selected.isBlank()) {
         String name = selected.trim();
         synchronized (SAVED) {
            HeadlessBots.SavedBot putIfAbsent = SAVED.putIfAbsent(key(name), new HeadlessBots.SavedBot(name, null, -1));
            return putIfAbsent == null;
         }
      } else {
         return false;
      }
   }

   public static void remove(String selectedName) {
      disconnect(selectedName);
      synchronized (SAVED) {
         SAVED.remove(key(selectedName));
      }

      PROFILES.remove(key(selectedName));
   }

   public static List<HeadlessBots.SavedBot> savedBots() {
      synchronized (SAVED) {
         return List.copyOf(SAVED.values());
      }
   }

   public static List<String> allNames() {
      ArrayList<String> strings = new ArrayList<>();
      HashSet hashSet = new HashSet();
      synchronized (SAVED) {
         for (HeadlessBots.SavedBot savedBot : SAVED.values()) {
            strings.add(savedBot.name());
            hashSet.add(key(savedBot.name()));
         }
      }

      for (BotClient botClient : BOTS.values()) {
         if (hashSet.add(key(botClient.name()))) {
            strings.add(botClient.name());
         }
      }

      return strings;
   }

   public static void setProxy(String selectionName, @Nullable String string) {
      String str = string != null && !string.isBlank() ? string.trim() : null;
      if (str != null) {
         addProxy(str);
      }

      synchronized (SAVED) {
         HeadlessBots.SavedBot savedBot = SAVED.get(key(selectionName));
         if (savedBot != null) {
            SAVED.put(key(selectionName), new HeadlessBots.SavedBot(savedBot.name(), str, savedBot.protocolVersion()));
         }
      }
   }

   @Nullable
   public static String getProxy(String selected) {
      synchronized (SAVED) {
         HeadlessBots.SavedBot savedBot = SAVED.get(key(selected));
         return savedBot == null ? null : savedBot.proxy();
      }
   }

   public static boolean hasProxy(String allName) {
      String proxy = getProxy(allName);
      return proxy != null && !proxy.isBlank();
   }

   public static void setProtocolVersion(String name, int protocol) {
      int protocolVersion = protocol <= 0 ? -1 : protocol;
      synchronized (SAVED) {
         HeadlessBots.SavedBot savedBot = SAVED.get(key(name));
         if (savedBot != null) {
            SAVED.put(key(name), new HeadlessBots.SavedBot(savedBot.name(), savedBot.proxy(), protocolVersion));
         }
      }
   }

   public static int getProtocolVersion(String selected) {
      synchronized (SAVED) {
         HeadlessBots.SavedBot savedBot = SAVED.get(key(selected));
         return savedBot == null ? -1 : savedBot.protocolVersion();
      }
   }

   public static List<String> getProxyPool() {
      ArrayList coll = new ArrayList();

      for (String proxy : SAVED_PROXIES) {
         addUniqueProxy(coll, proxy);
      }

      synchronized (SAVED) {
         for (HeadlessBots.SavedBot savedBot : SAVED.values()) {
            if (savedBot.proxy() != null) {
               addUniqueProxy(coll, savedBot.proxy());
            }
         }
      }

      return List.copyOf(coll);
   }

   public static boolean addProxy(String string) {
      String anotherString = string == null ? "" : string.trim();
      if (!anotherString.isBlank() && !containsProxy(SAVED_PROXIES, anotherString)) {
         SAVED_PROXIES.add(anotherString);
         requestProxyPing(anotherString, true);
         return true;
      } else {
         return false;
      }
   }

   public static boolean removeProxy(String str) {
      String anotherString = str == null ? "" : str.trim();
      if (anotherString.isBlank()) {
         return false;
      } else {
         boolean removeIf = SAVED_PROXIES.removeIf(var1x -> var1x.equalsIgnoreCase(anotherString));
         synchronized (SAVED) {
            for (Entry entry : SAVED.entrySet()) {
               HeadlessBots.SavedBot savedBot = (HeadlessBots.SavedBot)entry.getValue();
               if (savedBot.proxy() != null && savedBot.proxy().equalsIgnoreCase(anotherString)) {
                  entry.setValue(new HeadlessBots.SavedBot(savedBot.name(), null, savedBot.protocolVersion()));
                  removeIf = true;
               }
            }
         }

         PROXY_PINGS.remove(proxyPingKey(anotherString));
         return removeIf;
      }
   }

   public static int getProxyUseCount(String str) {
      String anotherString = str == null ? "" : str.trim();
      if (anotherString.isBlank()) {
         return 0;
      } else {
         int n = 0;
         synchronized (SAVED) {
            for (HeadlessBots.SavedBot savedBot : SAVED.values()) {
               if (savedBot.proxy() != null && savedBot.proxy().equalsIgnoreCase(anotherString)) {
                  n++;
               }
            }

            return n;
         }
      }
   }

   public static HeadlessBots.ProxyPingSnapshot getProxyPing(String proxyNoneKey) {
      String anotherString = proxyNoneKey == null ? "" : proxyNoneKey.trim();
      if (anotherString.isBlank()) {
         return new HeadlessBots.ProxyPingSnapshot(HeadlessBots.ProxyPingStatus.UNKNOWN, -1);
      } else {
         HeadlessBots.ProxyPingState proxyPingState = requestProxyPing(anotherString, false);
         return new HeadlessBots.ProxyPingSnapshot(proxyPingState.status, proxyPingState.latencyMs);
      }
   }

   private static HeadlessBots.ProxyPingState requestProxyPing(String anotherString, boolean flag) {
      String str = anotherString == null ? "" : anotherString.trim();
      if (str.isBlank()) {
         return new HeadlessBots.ProxyPingState();
      } else {
         HeadlessBots.ProxyPingState proxyPingState = PROXY_PINGS.computeIfAbsent(proxyPingKey(str), var0x -> new HeadlessBots.ProxyPingState());
         long now = System.currentTimeMillis();
         synchronized (proxyPingState) {
            if (proxyPingState.inFlight) {
               return proxyPingState;
            }

            if (!flag && proxyPingState.status != HeadlessBots.ProxyPingStatus.UNKNOWN && now - proxyPingState.lastStartedAt < 10000L) {
               return proxyPingState;
            }

            proxyPingState.inFlight = true;
            if (proxyPingState.status == HeadlessBots.ProxyPingStatus.UNKNOWN) {
               proxyPingState.status = HeadlessBots.ProxyPingStatus.CHECKING;
            }

            proxyPingState.lastStartedAt = now;
         }

         try {
            PROXY_PING_EXECUTOR.execute(() -> pingProxy(str, proxyPingState));
         } catch (RejectedExecutionException e) {
            synchronized (proxyPingState) {
               proxyPingState.latencyMs = -1;
               proxyPingState.status = HeadlessBots.ProxyPingStatus.FAILED;
               proxyPingState.inFlight = false;
            }
         }

         return proxyPingState;
      }
   }

   private static void pingProxy(String proxy, HeadlessBots.ProxyPingState proxyPingState) {
      long nanoTime = System.nanoTime();

      try {
         BotProxy botProxy = BotProxy.parse(proxy);
         if (botProxy == null) {
            throw new IllegalArgumentException("Invalid proxy");
         }

         final ProxyHandler handler = botProxy.createHandler();
         InetSocketAddress unresolved = InetSocketAddress.createUnresolved("mc.holyworld.ru", 25565);
         EventLoopGroup eventLoopGroup = EventLoopGroupHolder.remote(false).eventLoopGroup();
         Bootstrap bootstrap = (Bootstrap)((Bootstrap)((Bootstrap)((Bootstrap)new Bootstrap().group(eventLoopGroup)).channel(NioSocketChannel.class))
               .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000))
            .handler(new ChannelInitializer<Channel>() {
               public void initChannel(Channel channel) {
                  channel.pipeline().addLast("timeout", new ReadTimeoutHandler((int)Math.ceil(5.0)));
                  channel.pipeline().addLast("proxy", handler);
               }
            });
         ChannelFuture connect = bootstrap.connect(unresolved);
         Channel channel = connect.channel();

         try {
            if (!connect.await(5000L)) {
               connect.cancel(true);
               throw new SocketTimeoutException("Proxy connect timeout");
            }

            if (!connect.isSuccess()) {
               throw asException(connect.cause());
            }

            Future connectFuture = handler.connectFuture();
            if (!connectFuture.await(5000L)) {
               throw new SocketTimeoutException("Proxy handshake timeout");
            }

            if (!connectFuture.isSuccess()) {
               throw asException(connectFuture.cause());
            }
         } finally {
            channel.close();
         }

         int latencyMs = Math.max(1, Math.round((float)(System.nanoTime() - nanoTime) / 1000000.0F));
         synchronized (proxyPingState) {
            proxyPingState.latencyMs = latencyMs;
            proxyPingState.status = HeadlessBots.ProxyPingStatus.OK;
            proxyPingState.inFlight = false;
         }
      } catch (Exception e) {
         synchronized (proxyPingState) {
            proxyPingState.latencyMs = -1;
            proxyPingState.status = HeadlessBots.ProxyPingStatus.FAILED;
            proxyPingState.inFlight = false;
         }
      }
   }

   private static Exception asException(Throwable cause) {
      return cause instanceof Exception e ? e : new Exception(cause);
   }

   private static String proxyPingKey(String anotherString) {
      return anotherString == null ? "" : anotherString.trim().toLowerCase(Locale.ROOT);
   }

   private static void addUniqueProxy(List<String> coll, String proxy) {
      String anotherString = proxy == null ? "" : proxy.trim();
      if (!anotherString.isBlank() && !containsProxy(coll, anotherString)) {
         coll.add(anotherString);
      }
   }

   private static boolean containsProxy(Collection<String> coll, String anotherString) {
      for (String str : coll) {
         if (str.equalsIgnoreCase(anotherString)) {
            return true;
         }
      }

      return false;
   }

   public static String getLastAddress() {
      return lastAddress;
   }

   public static void setLastAddress(String string) {
      lastAddress = string == null ? "" : string.trim();
   }

   private static void applyProfile(BotClient start) {
      JsonObject json = PROFILES.get(key(start.name()));
      if (json != null) {
         start.execute(() -> {
            for (BotModule botModule : start.getModules().getModules()) {
               if (json.has(botModule.getName()) && json.get(botModule.getName()).isJsonObject()) {
                  botModule.load(json.getAsJsonObject(botModule.getName()));
               }
            }
         });
      }
   }

   public static void persistProfile(BotClient botClient) {
      JsonObject json = new JsonObject();

      for (BotModule botModule : botClient.getModules().getModules()) {
         json.add(botModule.getName(), botModule.save());
      }

      PROFILES.put(key(botClient.name()), json);
   }

   public static void setModuleEnabled(String botName, String supportedModuleName, boolean equalsIgnoreCase) {
      BotClient botClient = get(botName);
      if (botClient != null && botClient.getPhase() != BotPhase.DISCONNECTED) {
         botClient.execute(() -> {
            botClient.getModules().setEnabled(supportedModuleName, equalsIgnoreCase);
            persistProfile(botClient);
         });
      } else {
         JsonObject json = PROFILES.computeIfAbsent(key(botName), var0x -> new JsonObject());
         JsonObject jsonObject = json.has(supportedModuleName) && json.get(supportedModuleName).isJsonObject() ? json.getAsJsonObject(supportedModuleName) : new JsonObject();
         jsonObject.addProperty("enabled", equalsIgnoreCase);
         json.add(supportedModuleName, jsonObject);
      }
   }

   public static boolean isModuleEnabledInProfile(String moduleMenuBotName, String supportedModuleName) {
      JsonObject json = PROFILES.get(key(moduleMenuBotName));
      if (json != null && json.has(supportedModuleName) && json.get(supportedModuleName).isJsonObject()) {
         JsonObject jsonObject = json.getAsJsonObject(supportedModuleName);
         return jsonObject.has("enabled") && jsonObject.get("enabled").getAsBoolean();
      } else {
         return false;
      }
   }

   public static JsonObject saveState() {
      JsonObject json = new JsonObject();
      JsonArray jsonArray = new JsonArray();
      synchronized (SAVED) {
         for (HeadlessBots.SavedBot savedBot : SAVED.values()) {
            JsonObject json2 = new JsonObject();
            json2.addProperty("name", savedBot.name());
            if (savedBot.proxy() != null) {
               json2.addProperty("proxy", savedBot.proxy());
            }

            if (savedBot.protocolVersion() > 0) {
               json2.addProperty("version", savedBot.protocolVersion());
            }

            jsonArray.add(json2);
         }
      }

      json.add("savedBots", jsonArray);
      JsonObject json2 = new JsonObject();

      for (Entry entry : PROFILES.entrySet()) {
         json2.add((String)entry.getKey(), (JsonElement)entry.getValue());
      }

      json.add("botProfiles", json2);
      JsonArray jsonArray2 = new JsonArray();

      for (String string : getProxyPool()) {
         jsonArray2.add(string);
      }

      json.add("proxyPool", jsonArray2);
      json.addProperty("lastAddress", lastAddress);
      return json;
   }

   public static void loadState(JsonObject jsonObject) {
      if (jsonObject != null) {
         if (jsonObject.has("BotData") && jsonObject.get("BotData").isJsonObject()) {
            jsonObject = jsonObject.getAsJsonObject("BotData");
         }

         if (jsonObject.has("savedBots") && jsonObject.get("savedBots").isJsonArray()) {
            JsonArray jsonArray = jsonObject.getAsJsonArray("savedBots");
            synchronized (SAVED) {
               for (int i = 0; i < jsonArray.size(); i++) {
                  try {
                     if (jsonArray.get(i).isJsonObject()) {
                        JsonObject json = jsonArray.get(i).getAsJsonObject();
                        String string = json.get("name").getAsString();
                        String proxy = json.has("proxy") ? json.get("proxy").getAsString() : null;
                        int protocolVersion = json.has("version") ? json.get("version").getAsInt() : -1;
                        if (string != null && !string.isBlank()) {
                           SAVED.putIfAbsent(key(string), new HeadlessBots.SavedBot(string.trim(), proxy, protocolVersion));
                           if (proxy != null && !proxy.isBlank()) {
                              addProxy(proxy);
                           }
                        }
                     } else {
                        String string = jsonArray.get(i).getAsString();
                        if (string != null && !string.isBlank()) {
                           SAVED.putIfAbsent(key(string), new HeadlessBots.SavedBot(string.trim(), null, -1));
                        }
                     }
                  } catch (Exception e) {
                  }
               }
            }
         }

         if (jsonObject.has("botProfiles") && jsonObject.get("botProfiles").isJsonObject()) {
            JsonObject json = jsonObject.getAsJsonObject("botProfiles");

            for (String memberName : json.keySet()) {
               if (json.get(memberName).isJsonObject()) {
                  PROFILES.put(memberName.toLowerCase(Locale.ROOT), json.getAsJsonObject(memberName));
               }
            }
         }

         if (jsonObject.has("proxyPool") && jsonObject.get("proxyPool").isJsonArray()) {
            JsonArray jsonArray = jsonObject.getAsJsonArray("proxyPool");

            for (int i = 0; i < jsonArray.size(); i++) {
               try {
                  addProxy(jsonArray.get(i).getAsString());
               } catch (Exception e) {
               }
            }
         }

         if (jsonObject.has("lastAddress")) {
            setLastAddress(jsonObject.get("lastAddress").getAsString());
         }
      }
   }

   public static void loadPersistentState() {
      loadState(BotPersistence.load());
   }

   public static void savePersistentState() {
      try {
         BotPersistence.save(saveState());
      } catch (Exception e) {
      }
   }

   private static String key(String name) {
      return name.toLowerCase(Locale.ROOT);
   }

   private static final class ChatReportingEvents implements BotClientEvents {
      @Override
      public void onPhaseChanged(BotClient botClient, BotPhase botPhase, BotPhase botPhase2) {
         info("[бот " + botClient.name() + "] фаза: " + botPhase2);
      }

      @Override
      public void onJoined(BotClient botClient) {
         info("[бот " + botClient.name() + "] зашёл в мир");
      }

      @Override
      public void onDisconnected(BotClient botClient, Component component) {
         HeadlessBots.BOTS.remove(botClient.name().toLowerCase(Locale.ROOT), botClient);
         info("[бот " + botClient.name() + "] отключён: " + component.getString());
      }

      private static void info(String string) {
         Minecraft minecraft = Minecraft.getInstance();
         if (minecraft != null) {
            minecraft.execute(() -> ChatLogger.method2020(ChatLogger.Type.INFO, string));
         }
      }
   }

   public static final class ProxyPingSnapshot {
      private final HeadlessBots.ProxyPingStatus status;
      private final int latencyMs;

      public HeadlessBots.ProxyPingStatus status() {
         return this.status;
      }

      public int latencyMs() {
         return this.latencyMs;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof ProxyPingSnapshot other)) return false;
         return java.util.Objects.equals(this.status, other.status) && java.util.Objects.equals(this.latencyMs, other.latencyMs);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(status, latencyMs);
      }

      @Override
      public String toString() {
         return "ProxyPingSnapshot[" + "status=" + this.status + ", " + "latencyMs=" + this.latencyMs + "]";
      }

      public ProxyPingSnapshot(HeadlessBots.ProxyPingStatus status, int latencyMs) {
         this.status = status;
         this.latencyMs = latencyMs;
      }

      public String label() {
         return switch (this.status) {
            case UNKNOWN -> "--";
            case CHECKING -> this.latencyMs >= 0 ? this.latencyMs + "ms" : "--";
            case OK -> this.latencyMs + "ms";
            case FAILED -> "err";
         };
      }
   }

   private static final class ProxyPingState {
      private volatile HeadlessBots.ProxyPingStatus status = HeadlessBots.ProxyPingStatus.UNKNOWN;
      private volatile int latencyMs = -1;
      private volatile long lastStartedAt;
      private volatile boolean inFlight;
   }

   public static enum ProxyPingStatus {
      UNKNOWN,
      CHECKING,
      OK,
      FAILED;
   }

   public static final class SavedBot {
      private final String name;
      @Nullable
      private final String proxy;
      private final int protocolVersion;

      public String name() {
         return this.name;
      }

      public String proxy() {
         return this.proxy;
      }

      public int protocolVersion() {
         return this.protocolVersion;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof SavedBot other)) return false;
         return java.util.Objects.equals(this.name, other.name) && java.util.Objects.equals(this.proxy, other.proxy) && java.util.Objects.equals(this.protocolVersion, other.protocolVersion);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(name, proxy, protocolVersion);
      }

      @Override
      public String toString() {
         return "SavedBot[" + "name=" + this.name + ", " + "proxy=" + this.proxy + ", " + "protocolVersion=" + this.protocolVersion + "]";
      }

      public SavedBot(String name, @Nullable String proxy, int protocolVersion) {
         this.name = name;
         this.proxy = proxy;
         this.protocolVersion = protocolVersion;
      }
   }
}
