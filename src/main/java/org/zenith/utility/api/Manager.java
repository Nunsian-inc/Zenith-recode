package org.zenith.utility.api;

import com.darkmagician6.eventapi.EventManager;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import lombok.Generated;
import org.zenith.MinecraftAccess;
import org.zenith.ModuleCategory;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.utility.config.ConfigSyncService;
import org.zenith.utility.misc.AesGcmCrypto;

public class Manager implements MinecraftAccess {
   public static final File file = new File(Zenith.file, "configs");
   private static final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(task -> {
      Thread thread = new Thread(task, "Config-AutoSave");
      thread.setDaemon(true);
      thread.setPriority(1);
      return thread;
   });
   private static final ExecutorService executorService = Executors.newSingleThreadExecutor(task -> {
      Thread thread = new Thread(task, "Config-IO");
      thread.setDaemon(true);
      thread.setPriority(4);
      return thread;
   });
   private final AtomicInteger atomicInteger = new AtomicInteger();
   private final Set<String> items = ConcurrentHashMap.newKeySet();
   private final String text = "siMunids";
   private static final long CONST_longValue = 400L;
   private final AtomicBoolean atomicBoolean = new AtomicBoolean();
   private static final Object obj = new Object();
   private final AtomicLong atomicLong = new AtomicLong();
   private final Map<String, Long> items2 = new HashMap<>();
   private final Map<String, Object[]> items3 = new ConcurrentHashMap<>();

   public Manager() {
      file.mkdirs();
      this.method7766("current_config");
      EventManager.register(this);
      scheduledExecutorService.scheduleAtFixedRate(() -> {
         try {
            this.method7789();
         } catch (Exception e) {
         }
      }, 5L, 5L, TimeUnit.MINUTES);
   }

   public boolean method7766(String argument) {
      return this.method7767(argument, Manager.Type.ALL);
   }

   public boolean method7767(String argument, Manager.Type mode) {
      try {
         if (argument == null) {
            return false;
         } else {
            ConfigFile configFile = this.method7783(argument);
            if (configFile == null) {
               return false;
            } else {
               try {
                  boolean flag;
                  try (BufferedReader bufferedReader = new BufferedReader(new FileReader(configFile.getFile()))) {
                     String line = bufferedReader.readLine();
                     if (line == null || line.isBlank()) {
                        return false;
                     }

                     flag = this.method7775(this.method7774(line), mode, configFile);
                  }

                  return flag;
               } catch (Exception e) {
                  e.printStackTrace();
                  return false;
               }
            }
         }
      } catch (Exception e) {
         return false;
      }
   }

   public CompletableFuture<Boolean> method7768(String appliedName, Manager.Type mode) {
      return CompletableFuture.<Object[]>supplyAsync(() -> {
         ConfigFile var2x = this.method7783(appliedName);
         if (var2x == null) {
            return null;
         } else {
            try {
               Object[] objects;
               try (BufferedReader bufferedReader = new BufferedReader(new FileReader(var2x.getFile()))) {
                  String line = bufferedReader.readLine();
                  if (line == null || line.isBlank()) {
                     return null;
                  }

                  objects = new Object[]{var2x, this.method7774(line)};
               }

               return objects;
            } catch (Exception e) {
               return null;
            }
         }
      }, executorService).thenApplyAsync(var2x -> {
         if (var2x == null) {
            return false;
         } else {
            try {
               return this.method7775((JsonObject)var2x[1], mode, (ConfigFile)var2x[0]);
            } catch (Exception e) {
               e.printStackTrace();
               return false;
            }
         }
      }, mc);
   }

   public CompletableFuture<Boolean> method7769(byte[] buf, Manager.Type mode) {
      return CompletableFuture.<JsonObject>supplyAsync(() -> {
         if (buf != null && buf.length != 0) {
            try {
               JsonObject json;
               try (BufferedReader var2x = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(buf), StandardCharsets.UTF_8))) {
                  String line = var2x.readLine();
                  json = line != null && !line.isBlank() ? this.method7774(line) : null;
               }

               return json;
            } catch (Exception e) {
               return null;
            }
         } else {
            return null;
         }
      }, executorService).thenApplyAsync(var2x -> var2x != null && this.method7775(var2x, mode, null), mc);
   }

   public int method7770() {
      return this.atomicInteger.get();
   }

   private static File method7771(String selectedLocal) {
      String str = selectedLocal == null ? "" : selectedLocal.replace("." + "Zenith".toLowerCase(), "");
      return new File(file, str + "." + "Zenith".toLowerCase());
   }

   public Manager.Data method7772(String selectedLocal) {
      File file = method7771(selectedLocal);
      Object[] objects = this.items3.get(file.getAbsolutePath());
      if (objects != null && (Long)objects[0] == file.lastModified()) {
         return (Manager.Data)objects[1];
      } else {
         if (this.items.add(file.getAbsolutePath())) {
            executorService.execute(() -> {
               try {
                  this.method7784(selectedLocal);
               } finally {
                  this.items.remove(file.getAbsolutePath());
                  this.atomicInteger.incrementAndGet();
               }
            });
         }

         return objects == null ? null : (Manager.Data)objects[1];
      }
   }

   public boolean method7773(byte[] buf, Manager.Type mode) {
      if (buf != null && buf.length != 0) {
         try {
            boolean flag;
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(buf), StandardCharsets.UTF_8))) {
               String line = bufferedReader.readLine();
               if (line == null || line.isBlank()) {
                  return false;
               }

               flag = this.method7775(this.method7774(line), mode, null);
            }

            return flag;
         } catch (Exception e) {
            return false;
         }
      } else {
         return false;
      }
   }

   private JsonObject method7774(String line) throws Exception {
      byte[] allBytes = Base64.getDecoder().decode(line);
      byte[] bytes = AesGcmCrypto.method5340(allBytes, "siMunids");
      String str = new String(bytes, StandardCharsets.UTF_8);
      return JsonParser.parseString(str).getAsJsonObject();
   }

   private boolean method7775(JsonObject json, Manager.Type mode, ConfigFile configFile) {
      if (mode == Manager.Type.ONLY_THEMES) {
         JsonObject json2 = new JsonObject();
         if (json.has("Styles")) {
            json2.add("Styles", json.get("Styles"));
         }

         this.method7777(configFile, json2);
         this.method7778();
         return true;
      } else {
         HashMap map = new HashMap();

         for (Module moduleValue : Zenith.getInstance().getModuleManager().getItems()) {
            map.put(moduleValue.getId(), new int[]{moduleValue.getKeyCode(), moduleValue.getKeyModifiers()});
         }

         this.method7777(configFile, json);
         JsonObject jsonObject = json.has("Modules") && json.get("Modules").isJsonObject() ? json.getAsJsonObject("Modules") : null;

         for (Module moduleValue : Zenith.getInstance().getModuleManager().getItems()) {
            int[] ints = (int[])map.get(moduleValue.getId());
            if (ints != null) {
               if (mode == Manager.Type.IGNORE_BINDS) {
                  moduleValue.setKeyCode(ints[0]);
                  moduleValue.setKeyModifiers(ints[1]);
               } else if (method7776(jsonObject, moduleValue)) {
                  moduleValue.setKeyModifiers(ints[1]);
               }
            }
         }

         this.method7778();
         return true;
      }
   }

   private static boolean method7776(JsonObject jsonObject, Module moduleValue) {
      if (jsonObject == null) {
         return false;
      } else {
         JsonElement jsonElement = jsonObject.get(moduleValue.getId());
         if ((jsonElement == null || !jsonElement.isJsonObject()) && moduleValue.getId().startsWith("zenith:")) {
            jsonElement = jsonObject.get(moduleValue.getName());
         }

         return jsonElement != null && jsonElement.isJsonObject() && !jsonElement.getAsJsonObject().has("keyCode");
      }
   }

   private void method7777(ConfigFile configFile, JsonObject json) {
      if (configFile == null) {
         ConfigFile.loadState(json);
      } else {
         configFile.load(json);
      }
   }

   private void method7778() {
      if (mc.player != null && Zenith.getInstance().getCommandManager() != null) {
         try {
            Zenith.getInstance().getCommandManager().getDispatcher().execute("binds list", Zenith.getInstance().getCommandManager().getSource());
         } catch (Exception e) {
         }
      }
   }

   public boolean method7779(String argument) {
      return this.method7780(argument, true, true, true);
   }

   public boolean method7780(String argument, boolean flag, boolean flag2, boolean flag3) {
      try {
         if (argument == null) {
            return false;
         } else {
            ConfigFile configFile = this.method7783(argument);
            if (configFile == null) {
               configFile = new ConfigFile(argument);
            }

            long incrementAndGet = this.atomicLong.incrementAndGet();
            JsonObject jsonElement = configFile.save();
            if (jsonElement == null) {
               return false;
            } else {
               this.method7782(jsonElement, flag, flag2, flag3);
               this.method7781(argument, configFile, jsonElement, incrementAndGet);
               return true;
            }
         }
      } catch (Exception e) {
         return false;
      }
   }

   private void method7781(String argument, ConfigFile configFile, JsonObject jsonElement, long incrementAndGet) throws Exception {
      String json = new GsonBuilder().setPrettyPrinting().create().toJson(jsonElement);
      String encodeToString = Base64.getEncoder().encodeToString(AesGcmCrypto.method5339(json.getBytes(), "siMunids"));
      byte[] bytes = encodeToString.getBytes(StandardCharsets.US_ASCII);
      Path absolutePath = configFile.getFile().toPath().toAbsolutePath();
      Path path = Zenith.file.toPath().toAbsolutePath().resolve(absolutePath.getFileName() + ".tmp");
      String string = absolutePath.toString();
      synchronized (obj) {
         Long l = this.items2.get(string);
         if (l != null && l >= incrementAndGet) {
            return;
         }

         Files.write(path, bytes);

         try {
            Files.move(path, absolutePath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
         } catch (IOException e) {
            Files.write(absolutePath, bytes);
            Files.deleteIfExists(path);
         }

         this.items2.put(string, incrementAndGet);
      }

      if (!argument.replace("." + "Zenith".toLowerCase(), "").equalsIgnoreCase("current_config")) {
         ConfigSyncService.getInstance().method675();
      }
   }

   private void method7782(JsonObject jsonElement, boolean flag, boolean flag2, boolean flag3) {
      if (!flag) {
         jsonElement.remove("Styles");
         jsonElement.remove("FiguraData");
         jsonElement.remove("PetData");
         jsonElement.remove("Wardrobe");
         jsonElement.remove("Language");
         jsonElement.remove("Interface");
      }

      if (!flag2) {
         jsonElement.remove("BotData");
      }

      JsonObject jsonObject = jsonElement.has("Modules") && jsonElement.get("Modules").isJsonObject() ? jsonElement.getAsJsonObject("Modules") : null;
      if (jsonObject != null) {
         for (Module moduleValue : Zenith.getInstance().getModuleManager().getItems()) {
            ModuleCategory category = moduleValue.getCategory();
            boolean flag4 = category == ModuleCategory.RENDER || category == ModuleCategory.THEMES;
            if ((!flag4 || flag) && (flag4 || flag2)) {
               if (!flag3 && jsonObject.has(moduleValue.getId()) && jsonObject.get(moduleValue.getId()).isJsonObject()) {
                  jsonObject.getAsJsonObject(moduleValue.getId()).remove("keyCode");
                  jsonObject.getAsJsonObject(moduleValue.getId()).remove("keyModifiers");
               }
            } else {
               jsonObject.remove(moduleValue.getId());
            }
         }
      }
   }

   public ConfigFile method7783(String argument) {
      if (argument == null) {
         return null;
      } else {
         argument = argument.replace("." + "Zenith".toLowerCase(), "");
         File file2 = new File(file, argument + "." + "Zenith".toLowerCase());
         return file2.exists() ? new ConfigFile(argument) : null;
      }
   }

   public Manager.Data method7784(String selectedLocal) {
      ConfigFile configFile = this.method7783(selectedLocal);
      if (configFile == null) {
         this.items3.put(method7771(selectedLocal).getAbsolutePath(), new Object[]{0L, Manager.Data.INSTANCE});
         return Manager.Data.INSTANCE;
      } else {
         File file = configFile.getFile();
         String absolutePath = file.getAbsolutePath();
         Object[] objects = this.items3.get(absolutePath);
         if (objects != null && (Long)objects[0] == file.lastModified()) {
            return (Manager.Data)objects[1];
         } else {
            Manager.Data data = Manager.Data.INSTANCE;

            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
               String line = bufferedReader.readLine();
               if (line != null && !line.isBlank()) {
                  JsonObject json = this.method7774(line);
                  HashSet coll = new HashSet();
                  int intValue = 0;
                  if (json.has("Modules") && json.get("Modules").isJsonObject()) {
                     for (Entry entry : json.getAsJsonObject("Modules").entrySet()) {
                        if (((JsonElement)entry.getValue()).isJsonObject()) {
                           JsonObject jsonObject = ((JsonElement)entry.getValue()).getAsJsonObject();
                           if (jsonObject.has("enabled") && jsonObject.get("enabled").getAsBoolean()) {
                              coll.add((String)entry.getKey());
                           }

                           if (jsonObject.has("keyCode") && jsonObject.get("keyCode").getAsInt() > 0) {
                              intValue++;
                           }
                        }
                     }
                  }

                  data = new Manager.Data(Set.copyOf(coll), intValue, json.has("Styles"));
               }
            } catch (Exception e) {
            }

            this.items3.put(absolutePath, new Object[]{file.lastModified(), data});
            return data;
         }
      }
   }

   public List<String> method7785() {
      File[] listFiles = file.listFiles();
      ArrayList<String> strings = new ArrayList<>();
      if (listFiles != null) {
         for (File listFile : listFiles) {
            strings.add(listFile.getName());
         }
      }

      return strings;
   }

   public boolean method7786(String selectedLocal) {
      if (selectedLocal == null) {
         return false;
      } else {
         ConfigFile configFile = this.method7783(selectedLocal);
         if (configFile == null) {
            return false;
         } else {
            File file = configFile.getFile();
            boolean flag = file.exists() && file.delete();
            if (flag) {
               ConfigSyncService.getInstance().method680(selectedLocal.replace("." + "Zenith".toLowerCase(), ""));
            }

            return flag;
         }
      }
   }

   public synchronized boolean method7787(String display, String str) {
      if (display != null && str != null) {
         String argument = display.replace("." + "Zenith".toLowerCase(), "").trim();
         String anotherString = str.replace("." + "Zenith".toLowerCase(), "").trim();
         if (argument.isEmpty() || anotherString.isEmpty()) {
            return false;
         } else if (argument.equalsIgnoreCase(anotherString)) {
            return true;
         } else {
            ConfigFile configFile = this.method7783(argument);
            if (configFile != null && this.method7783(anotherString) == null) {
               try {
                  ConfigFile configFile2 = new ConfigFile(anotherString);
                  Files.copy(configFile.getFile().toPath(), configFile2.getFile().toPath(), StandardCopyOption.REPLACE_EXISTING);
                  if (!configFile.getFile().delete()) {
                     configFile2.getFile().delete();
                     return false;
                  } else {
                     ConfigSyncService.getInstance().method678(argument, anotherString);
                     return true;
                  }
               } catch (Exception e) {
                  return false;
               }
            } else {
               return false;
            }
         }
      } else {
         return false;
      }
   }

   public void save() {
      ConfigSyncService.getInstance().method676();
      this.method7779("current_config");
   }

   public void method7788() {
      if (this.atomicBoolean.compareAndSet(false, true)) {
         scheduledExecutorService.schedule(() -> {
            this.atomicBoolean.set(false);

            try {
               this.method7789();
            } catch (Exception e) {
            }
         }, 400L, TimeUnit.MILLISECONDS);
      }
   }

   private void method7789() {
      ConfigSyncService.getInstance().method676();
      mc.execute(() -> {
         try {
            ConfigFile configFile = this.method7783("current_config");
            if (configFile == null) {
               configFile = new ConfigFile("current_config");
            }

            long incrementAndGet = this.atomicLong.incrementAndGet();
            JsonObject jsonElement = configFile.save();
            if (jsonElement == null) {
               return;
            }

            ConfigFile configFile2 = configFile;
            executorService.execute(() -> {
               try {
                  this.method7781("current_config", configFile2, jsonElement, incrementAndGet);
               } catch (Exception var6x) {
               }
            });
         } catch (Exception e) {
         }
      });
   }

   public boolean method7790() {
      return this.atomicBoolean.get();
   }

   @Generated
   public AtomicInteger getAtomicInteger() {
      return this.atomicInteger;
   }

   @Generated
   public Set<String> getItems() {
      return this.items;
   }

   @Generated
   public String method7791() {
      return "siMunids";
   }

   @Generated
   public AtomicBoolean getAtomicBoolean() {
      return this.atomicBoolean;
   }

   @Generated
   public AtomicLong getAtomicLong() {
      return this.atomicLong;
   }

   @Generated
   public Map<String, Long> getItems2() {
      return this.items2;
   }

   @Generated
   public Map<String, Object[]> getItems3() {
      return this.items3;
   }

   public static final class Data {
      private final Set<String> items;
      private final int intValue;
      private final boolean flag;

      public Set<String> items() {
         return this.items;
      }

      public int intValue() {
         return this.intValue;
      }

      public boolean flag() {
         return this.flag;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.items, other.items) && java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.flag, other.flag);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(items, intValue, flag);
      }

      @Override
      public String toString() {
         return "Data[" + "items=" + this.items + ", " + "intValue=" + this.intValue + ", " + "flag=" + this.flag + "]";
      }
      public static final Manager.Data INSTANCE = new Manager.Data(Set.of(), 0, false);

      public Data(Set<String> items, int intValue, boolean flag) {
         this.items = items;
         this.intValue = intValue;
         this.flag = flag;
      }

      public Set<String> getItems() {
         return this.items;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public boolean isFlag() {
         return this.flag;
      }
   }

   public static enum Type {
      ALL,
      IGNORE_BINDS,
      ONLY_THEMES;
   }
}
