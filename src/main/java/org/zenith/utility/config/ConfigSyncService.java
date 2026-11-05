package org.zenith.utility.config;

import java.io.File;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.HexFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ConcurrentHashMap.KeySetView;
import java.util.concurrent.atomic.AtomicInteger;
import org.zenith.Zenith;
import org.zenith.utility.api.Manager;
import org.zenith.utility.api.StoreApi;

public final class ConfigSyncService {
   private static final ConfigSyncService INSTANCE = new ConfigSyncService();
   private static final long CONST_longValue = 1200L;
   private static final long CONST_longValue2 = 30000L;
   private static final String text = "current_config";
   private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(task -> {
      Thread thread = new Thread(task, "zenith-config-sync");
      thread.setDaemon(true);
      return thread;
   });
   private final Map<String, ConfigSyncService.Type> items = new ConcurrentHashMap<>();
   private final Set<String> items2 = ConcurrentHashMap.newKeySet();
   private volatile List<StoreApi.Data2> items3 = List.of();
   private volatile Map<String, StoreApi.Data2> items4 = Map.of();
   private final Object obj = new Object();
   private final Map<UUID, ConfigSyncService.Data> items5 = new ConcurrentHashMap<>();
   private static final long CONST_longValue3 = 120000L;
   private final AtomicInteger atomicInteger = new AtomicInteger();
   private volatile ScheduledFuture<?> scheduledFuture;
   private volatile boolean flag;
   private volatile long longValue;
   private volatile String lastError = "";

   private ConfigSyncService() {
   }

   public static ConfigSyncService getInstance() {
      return INSTANCE;
   }

   public int revision() {
      return this.atomicInteger.get();
   }

   public boolean isFlag() {
      return this.flag;
   }

   public boolean method670() {
      return !this.items2.isEmpty() || this.scheduledFuture != null && !this.scheduledFuture.isDone();
   }

   public long getLongValue() {
      return this.longValue;
   }

   public String getLastError() {
      return this.lastError;
   }

   public ConfigSyncService.Type method671(String display) {
      return this.items.getOrDefault(key(display), this.flag ? ConfigSyncService.Type.SYNCED : ConfigSyncService.Type.LOCAL);
   }

   public List<StoreApi.Data2> getItems3() {
      return this.items3;
   }

   public StoreApi.Data2 method672(String display) {
      return this.items4.get(key(display));
   }

   private void method673(List<StoreApi.Data2> coll) {
      HashMap map = new HashMap();

      for (StoreApi.Data2 data2 : coll) {
         map.putIfAbsent(key(data2.name()), data2);
      }

      this.items3 = List.copyOf(coll);
      this.items4 = map;
   }

   private void method674(UUID uuid, StoreApi.Data2 data2) {
      synchronized (this.obj) {
         var coll = new ArrayList<>(this.items3);
         coll.removeIf(var1x -> var1x.getUUID().equals(uuid));
         if (data2 != null) {
            coll.add(data2);
         }

         this.method673(coll);
      }
   }

   public void method675() {
      ScheduledFuture scheduledFuture = this.scheduledFuture;
      if (scheduledFuture != null) {
         scheduledFuture.cancel(false);
      }

      this.scheduledFuture = this.scheduledExecutorService.schedule(this::run, 1200L, TimeUnit.MILLISECONDS);
   }

   public void method676() {
      if (System.currentTimeMillis() - this.longValue > 30000L && (this.scheduledFuture == null || this.scheduledFuture.isDone())) {
         this.scheduledFuture = this.scheduledExecutorService.schedule(this::run, 0L, TimeUnit.MILLISECONDS);
      }
   }

   public void method677(UUID uuid) {
      this.method674(uuid, null);
      this.bump();
   }

   public void method678(String argument, String anotherString) {
      StoreApi.Data2 data2 = this.method672(argument);
      StoreApi storeApi = store();
      this.items.remove(key(argument));
      if (data2 != null && storeApi != null) {
         this.items2.add(key(anotherString));
         this.items5.put(data2.getUUID(), new ConfigSyncService.Data(key(anotherString)));
         storeApi.method1974(data2.getUUID(), anotherString, null, null).whenComplete((var3x, var4x) -> {
            this.items2.remove(key(anotherString));
            if (var4x != null) {
               this.items5.remove(data2.getUUID());
               this.lastError = method687(var4x);
            } else {
               this.items5.put(var3x.getUUID(), new ConfigSyncService.Data(key(var3x.name())));
               this.method674(var3x.getUUID(), var3x);
            }

            this.bump();
            this.method675();
         });
      } else {
         this.method675();
      }
   }

   public void method679(StoreApi.Data2 data2) {
      this.method674(data2.getUUID(), data2.mine() ? data2 : null);
      this.bump();
   }

   public void method680(String display) {
      StoreApi.Data2 data2 = this.method672(display);
      this.items.remove(key(display));
      if (data2 != null) {
         synchronized (this.obj) {
            var coll = new ArrayList<>(this.items3);
            coll.remove(data2);
            this.method673(coll);
         }

         StoreApi storeApi = store();
         if (storeApi != null) {
            this.items5.put(data2.getUUID(), new ConfigSyncService.Data(""));
            storeApi.method1975(data2.getUUID()).whenComplete((var2x, throwable) -> {
               if (throwable != null) {
                  this.items5.remove(data2.getUUID());
                  this.lastError = method687(throwable);
               }

               this.bump();
            });
         }
      }
   }

   private void run() {
      this.longValue = System.currentTimeMillis();
      StoreApi storeApi = store();
      if (storeApi == null) {
         this.flag = false;
         this.bump();
      } else {
         List<StoreApi.Data2> data2s;
         try {
            data2s = storeApi.method1968("config").get(15L, TimeUnit.SECONDS);
         } catch (Exception e) {
            this.flag = false;
            this.lastError = method687(e);
            this.bump();
            return;
         }

         this.flag = true;
         this.lastError = "";
         HashSet hashSet = new HashSet();

         for (StoreApi.Data2 data2 : data2s) {
            hashSet.add(data2.getUUID());
         }

         long now = System.currentTimeMillis();
         this.items5.entrySet().removeIf(var3x -> !hashSet.contains(var3x.getKey()) || now - var3x.getValue().getLongValue() > 120000L);
         ArrayList coll = new ArrayList();

         for (StoreApi.Data2 data2 : data2s) {
            if (data2.mine() && "config".equalsIgnoreCase(data2.kind())) {
               ConfigSyncService.Data data = this.items5.get(data2.getUUID());
               if (data != null) {
                  if (data.getText().isEmpty() || !data.getText().equals(key(data2.name()))) {
                     continue;
                  }

                  this.items5.remove(data2.getUUID());
               }

               coll.add(data2);
            }
         }

         synchronized (this.obj) {
            this.method673(coll);
         }

         File file = Manager.file;
         String suffix = "." + "Zenith".toLowerCase(Locale.ROOT);
         File[] listFiles = file == null ? null : file.listFiles();
         KeySetView keySet = ConcurrentHashMap.newKeySet();
         if (listFiles != null) {
            for (File listFile : listFiles) {
               String name = listFile.getName();
               if (listFile.isFile() && name.toLowerCase(Locale.ROOT).endsWith(suffix)) {
                  String str = name.substring(0, name.length() - suffix.length());
                  if (!str.equalsIgnoreCase("current_config")) {
                     keySet.add(key(str));
                     if (!this.method684(key(str))) {
                        this.method681(storeApi, listFile, str);
                     }
                  }
               }
            }
         }

         for (StoreApi.Data2 data2 : this.items3) {
            if (!this.items5.containsKey(data2.getUUID())
               && !keySet.contains(key(data2.name()))
               && !keySet.contains(key(method685(data2.name())))
               && !key(data2.name()).equals("current_config")) {
               this.method683(storeApi, data2, new File(file, method685(data2.name()) + suffix));
            }
         }

         this.bump();
      }
   }

   private void method681(StoreApi storeApi, File listFile, String display) {
      String str = key(display);
      if (!this.items2.contains(str)) {
         byte[] allBytes;
         try {
            allBytes = Files.readAllBytes(listFile.toPath());
         } catch (Exception e) {
            this.items.put(str, ConfigSyncService.Type.ERROR);
            return;
         }

         StoreApi.Data2 data2 = this.method672(display);
         if (data2 == null) {
            this.method682(storeApi, str, display, listFile.getName(), allBytes, null, 0L, "PRIVATE", "");
         } else if (method686(allBytes).equalsIgnoreCase(data2.getText6())) {
            this.items.put(str, ConfigSyncService.Type.SYNCED);
         } else {
            if (listFile.lastModified() >= data2.getLongValue4()) {
               this.method682(storeApi, str, display, data2.fileName(), allBytes, data2.getUUID(), data2.getLongValue2(), data2.getText7(), data2.description());
            } else {
               this.method683(storeApi, data2, listFile);
            }
         }
      }
   }

   private void method682(StoreApi storeApi, String str, String display, String name, byte[] allBytes, UUID uuid, long longValue, String str2, String description) {
      this.items2.add(str);
      this.items.put(str, ConfigSyncService.Type.UPLOADING);
      this.bump();

      try {
         StoreApi.Data2 data2 = storeApi.method1972("config", display, name, description, str2, allBytes, uuid, longValue).get(30L, TimeUnit.SECONDS);
         this.method674(data2.getUUID(), data2);
         this.items.put(str, ConfigSyncService.Type.SYNCED);
      } catch (Exception e) {
         this.items.put(str, ConfigSyncService.Type.ERROR);
         this.lastError = method687(e);
      } finally {
         this.items2.remove(str);
         this.bump();
      }
   }

   private void method683(StoreApi storeApi, StoreApi.Data2 data2, File file) {
      String str = key(data2.name());
      if (this.items2.add(str)) {
         this.items.put(str, ConfigSyncService.Type.DOWNLOADING);
         this.bump();

         try {
            byte[] bytes = storeApi.method1970(data2.getUUID()).get(30L, TimeUnit.SECONDS);
            file.getParentFile().mkdirs();
            Files.write(file.toPath(), bytes);
            file.setLastModified(Math.max(data2.getLongValue4(), 0L));
            this.items.put(str, ConfigSyncService.Type.SYNCED);
         } catch (Exception e) {
            this.items.put(str, ConfigSyncService.Type.ERROR);
            this.lastError = method687(e);
         } finally {
            this.items2.remove(str);
            this.bump();
         }
      }
   }

   private boolean method684(String key) {
      for (ConfigSyncService.Data data : this.items5.values()) {
         if (!data.getText().isEmpty() && data.getText().equals(key)) {
            return true;
         }
      }

      return false;
   }

   private void bump() {
      this.atomicInteger.incrementAndGet();
   }

   private static StoreApi store() {
      return Zenith.getInstance() == null ? null : Zenith.getInstance().getStoreApi();
   }

   private static String key(String name) {
      return name == null ? "" : name.strip().toLowerCase(Locale.ROOT);
   }

   private static String method685(String name) {
      StringBuilder builder = new StringBuilder();

      for (char ch : (name == null ? "config" : name.strip()).toCharArray()) {
         builder.append(ch >= ' ' && "\\/:*?\"<>|".indexOf(ch) < 0 ? ch : '_');
      }

      String str = builder.toString().strip();
      return str.isEmpty() ? "config" : str;
   }

   private static String method686(byte[] allBytes) {
      try {
         return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(allBytes));
      } catch (Exception e) {
         return "";
      }
   }

   private static String method687(Throwable throwable) {
      Throwable throwable2 = throwable;

      while (throwable2.getCause() != null && throwable2.getCause() != throwable2) {
         throwable2 = throwable2.getCause();
      }

      String message = throwable2.getMessage();
      return message != null && !message.isBlank() ? message : throwable2.getClass().getSimpleName();
   }

   private static final class Data {
      private final String text;
      private final long longValue;

      public String text() {
         return this.text;
      }

      public long longValue() {
         return this.longValue;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.longValue, other.longValue);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, longValue);
      }

      @Override
      public String toString() {
         return "Data[" + "text=" + this.text + ", " + "longValue=" + this.longValue + "]";
      }

      public Data(String key) {
         this(key, System.currentTimeMillis());
      }

      private Data(String text, long longValue) {
         this.text = text;
         this.longValue = longValue;
      }

      public String getText() {
         return this.text;
      }

      public long getLongValue() {
         return this.longValue;
      }
   }

   public static enum Type {
      SYNCED,
      UPLOADING,
      DOWNLOADING,
      ERROR,
      LOCAL;
   }
}
