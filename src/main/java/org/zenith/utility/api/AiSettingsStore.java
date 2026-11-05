package org.zenith.utility.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.zenith.Zenith;
import org.zenith.utility.misc.AesGcmCrypto;
import org.zenith.utility.misc.ShownProvider;

public final class AiSettingsStore {
   private static AiSettingsStore INSTANCE;
   private static final ExecutorService executorService = Executors.newSingleThreadExecutor(task -> {
      Thread thread = new Thread(task, "zenith-ai-settings");
      thread.setDaemon(true);
      return thread;
   });
   private String text;
   private boolean flag;
   private final File file;
   private final Map<ShownProvider, String> items;
   private final Map<ShownProvider, String> items2;
   private final Map<ShownProvider, String> items3;
   private ShownProvider shownProvider;
   private boolean flag2;
   private boolean flag3;

   public AiSettingsStore() {
      this.file = new File(Zenith.file, "ai.zenith");
      this.items = new HashMap<>();
      this.items2 = new HashMap<>();
      this.items3 = new HashMap<>();
      this.shownProvider = ShownProvider.INSTANCE;
      this.flag2 = true;
      this.flag3 = true;
   }

   public static synchronized AiSettingsStore method4467() {
      if (INSTANCE == null) {
         INSTANCE = new AiSettingsStore();
         INSTANCE.load();
      }

      return INSTANCE;
   }

   private static String password() {
      return "zenith-ai:" + System.getProperty("user.name", "") + ":" + System.getProperty("os.name", "");
   }

   private void load() {
      if (this.file.isFile()) {
         try {
            byte[] bytes = AesGcmCrypto.method5340(Files.readAllBytes(this.file.toPath()), password());
            JsonObject jsonObject = JsonParser.parseString(new String(bytes, StandardCharsets.UTF_8)).getAsJsonObject();
            if (jsonObject.has("customProviders")) {
               for (JsonElement json : jsonObject.getAsJsonArray("customProviders")) {
                  JsonObject jsonObject2 = json.getAsJsonObject();
                  ShownProvider.method7333(
                     ShownProvider.method7332(
                        jsonObject2.get("id").getAsString(),
                        jsonObject2.get("title").getAsString(),
                        clean(jsonObject2.get("endpoint").getAsString()),
                        jsonObject2.has("model") ? clean(jsonObject2.get("model").getAsString()) : ""
                     )
                  );
               }
            }

            if (jsonObject.has("provider")) {
               this.shownProvider = ShownProvider.method7337(jsonObject.get("provider").getAsString());
            }

            if (jsonObject.has("autoApply")) {
               this.flag2 = jsonObject.get("autoApply").getAsBoolean();
            }

            if (jsonObject.has("autoFix")) {
               this.flag3 = jsonObject.get("autoFix").getAsBoolean();
            }

            for (ShownProvider shownProvider : ShownProvider.method7336()) {
               if (jsonObject.has(shownProvider.getText())) {
                  JsonObject json = jsonObject.getAsJsonObject(shownProvider.getText());
                  if (json.has("key")) {
                     this.items.put(shownProvider, clean(json.get("key").getAsString()));
                  }

                  if (json.has("model")) {
                     this.items2.put(shownProvider, clean(json.get("model").getAsString()));
                  }

                  if (json.has("endpoint")) {
                     this.items3.put(shownProvider, clean(json.get("endpoint").getAsString()));
                  }
               }
            }

            if (jsonObject.has("velorai") && ShownProvider.method7335().isEmpty()) {
               JsonObject json = jsonObject.getAsJsonObject("velorai");
               if (json.has("key")) {
                  String string = json.has("endpoint") ? json.get("endpoint").getAsString() : "https://velorai.lol/v1";
                  String str = json.has("model") ? json.get("model").getAsString() : "z-ai/glm-5.3-uncensored";
                  String host = URI.create(string).getHost();
                  ShownProvider shownProvider = ShownProvider.method7332("c_legacy", host == null ? "API" : host, string, str);
                  ShownProvider.method7333(shownProvider);
                  this.items.put(shownProvider, json.get("key").getAsString());
                  this.items2.put(shownProvider, str);
                  if ("velorai".equals(jsonObject.has("provider") ? jsonObject.get("provider").getAsString() : "")) {
                     this.shownProvider = shownProvider;
                  }

                  this.save();
               }
            }
         } catch (Exception e) {
         }
      }
   }

   public synchronized void save() {
      JsonObject json = new JsonObject();
      json.addProperty("provider", this.shownProvider.getText());
      json.addProperty("autoApply", this.flag2);
      json.addProperty("autoFix", this.flag3);
      JsonArray jsonArray = new JsonArray();

      for (ShownProvider shownProvider : ShownProvider.method7335()) {
         JsonObject json2 = new JsonObject();
         json2.addProperty("id", shownProvider.getText());
         json2.addProperty("title", shownProvider.title());
         json2.addProperty("endpoint", shownProvider.getText4());
         json2.addProperty("model", shownProvider.getItems3().get(0));
         jsonArray.add(json2);
      }

      json.add("customProviders", jsonArray);

      for (ShownProvider shownProvider : ShownProvider.method7336()) {
         JsonObject json2 = new JsonObject();
         if (!this.method4469(shownProvider).isEmpty()) {
            json2.addProperty("key", this.method4469(shownProvider));
         }

         if (this.items2.containsKey(shownProvider)) {
            json2.addProperty("model", this.items2.get(shownProvider));
         }

         if (this.items3.containsKey(shownProvider)) {
            json2.addProperty("endpoint", this.items3.get(shownProvider));
         }

         if (json2.size() > 0) {
            json.add(shownProvider.getText(), json2);
         }
      }

      this.text = json.toString();
      if (!this.flag) {
         this.flag = true;
         executorService.execute(this::flush);
      }
   }

   private void flush() {
      while (true) {
         String text;
         synchronized (this) {
            text = this.text;
            this.text = null;
            if (text == null) {
               this.flag = false;
               return;
            }
         }

         try {
            Files.write(this.file.toPath(), AesGcmCrypto.method5339(text.getBytes(StandardCharsets.UTF_8), password()));
         } catch (Exception e) {
         }
      }
   }

   public synchronized ShownProvider getShownProvider() {
      return this.shownProvider;
   }

   public synchronized void method4468(ShownProvider shownProvider) {
      if (!this.shownProvider.equals(shownProvider)) {
         this.shownProvider = shownProvider;
         this.save();
      }
   }

   public synchronized String method4469(ShownProvider shownProvider) {
      return this.items.getOrDefault(shownProvider, "");
   }

   public synchronized void method4470(ShownProvider shownProvider, String string) {
      if (string != null && !string.isBlank()) {
         this.items.put(shownProvider, clean(string));
      } else {
         this.items.remove(shownProvider);
      }

      this.save();
   }

   public synchronized boolean method4471(ShownProvider shownProvider) {
      return !shownProvider.isFlag() || !this.method4469(shownProvider).isEmpty();
   }

   public synchronized String method4472(ShownProvider shownProvider) {
      String str = this.items2.get(shownProvider);
      return str != null && !str.isBlank() ? str : shownProvider.getItems3().get(0);
   }

   public synchronized void method4473(ShownProvider shownProvider, String string) {
      if (string != null && !string.isBlank()) {
         this.items2.put(shownProvider, clean(string));
      } else {
         this.items2.remove(shownProvider);
      }

      this.save();
   }

   public synchronized String method4474(ShownProvider shownProvider) {
      String str = this.items3.get(shownProvider);
      return str != null && !str.isBlank() ? str : shownProvider.getText4();
   }

   public synchronized void method4475(ShownProvider shownProvider, String string) {
      if (string != null && !string.isBlank() && !string.strip().equals(shownProvider.getText4())) {
         this.items3.put(shownProvider, clean(string));
      } else {
         this.items3.remove(shownProvider);
      }

      this.save();
   }

   public synchronized ShownProvider method4476(String str, String string, String string2, String string3) {
      String string4 = "c_" + Long.toString(System.currentTimeMillis(), 36);
      ShownProvider shownProvider = ShownProvider.method7332(string4, str.strip(), clean(string), clean(string3));
      ShownProvider.method7333(shownProvider);
      if (string2 != null && !string2.isBlank()) {
         this.items.put(shownProvider, clean(string2));
      }

      if (string3 != null && !string3.isBlank()) {
         this.items2.put(shownProvider, clean(string3));
      }

      this.shownProvider = shownProvider;
      this.save();
      return shownProvider;
   }

   public synchronized void method4477(ShownProvider shownProvider) {
      if (shownProvider != null && shownProvider.isFlag2()) {
         ShownProvider.method7334(shownProvider);
         this.items.remove(shownProvider);
         this.items2.remove(shownProvider);
         this.items3.remove(shownProvider);
         if (this.shownProvider.equals(shownProvider)) {
            this.shownProvider = ShownProvider.INSTANCE;
         }

         this.save();
      }
   }

   public synchronized boolean isFlag2() {
      return this.flag2;
   }

   public synchronized void method4478(boolean flag) {
      this.flag2 = flag;
      this.save();
   }

   public synchronized boolean isFlag3() {
      return this.flag3;
   }

   public synchronized void method4479(boolean flag) {
      this.flag3 = flag;
      this.save();
   }

   public static String clean(String string) {
      if (string == null) {
         return "";
      } else {
         String str = string.strip();

         while (!str.isEmpty() && "`'\".,;:".indexOf(str.charAt(0)) >= 0) {
            str = str.substring(1).strip();
         }

         while (!str.isEmpty() && "`'\".,;:".indexOf(str.charAt(str.length() - 1)) >= 0) {
            str = str.substring(0, str.length() - 1).strip();
         }

         return str;
      }
   }

   public static String method4480(String str) {
      if (str != null && !str.isEmpty()) {
         return str.length() <= 10 ? "••••" : str.substring(0, 5) + "••••" + str.substring(str.length() - 4);
      } else {
         return "";
      }
   }
}
