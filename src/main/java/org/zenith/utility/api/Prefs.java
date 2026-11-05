package org.zenith.utility.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.util.Util;

public final class Prefs {
   private static final String text = "nexus-friends-prefs.json";
   private final Path path;
   private final Map<String, Prefs.Entry> items = new ConcurrentHashMap<>();
   private volatile boolean dirty;
   private volatile boolean flag;
   private final Object obj = new Object();
   private long longValue;
   private long longValue2;

   public Prefs(Path path) {
      this.path = path.resolve("nexus-friends-prefs.json");
      this.load();
   }

   private Prefs.Entry method3305(String username) {
      return this.items.computeIfAbsent(key(username), str -> new Prefs.Entry());
   }

   private static String key(String username) {
      return username == null ? "" : username.toLowerCase(Locale.ROOT);
   }

   public void method3306(String username) {
      Prefs.Entry entry = this.method3305(username);
      if (entry.longValue2 == 0L) {
         entry.longValue2 = System.currentTimeMillis();
      }

      entry.longValue = System.currentTimeMillis();
   }

   public void method3307(String username) {
      Prefs.Entry entry = this.method3305(username);
      entry.longValue = System.currentTimeMillis();
      entry.longValue2 = 0L;
   }

   public void method3308() {
      for (Prefs.Entry entry : this.items.values()) {
         entry.longValue2 = 0L;
      }
   }

   public long method3309(String selected) {
      Prefs.Entry entry = this.items.get(key(selected));
      return entry != null && entry.longValue2 != 0L ? System.currentTimeMillis() - entry.longValue2 : 0L;
   }

   public long method3310(String selected) {
      Prefs.Entry entry = this.items.get(key(selected));
      return entry == null ? 0L : entry.longValue;
   }

   public long method3311(String selected) {
      Prefs.Entry entry = this.items.get(key(selected));
      return entry == null ? 0L : entry.longValue3;
   }

   public void method3312(String name, long uid) {
      if (uid > 0L) {
         Prefs.Entry entry = this.method3305(name);
         if (entry.longValue3 != uid) {
            entry.longValue3 = uid;
            this.save();
         }
      }
   }

   public String method3313(String selected) {
      Prefs.Entry entry = this.items.get(key(selected));
      return entry != null && entry.text != null ? entry.text : "";
   }

   public void method3314(String username, String text) {
      if (text != null && !text.isBlank()) {
         Prefs.Entry entry = this.method3305(username);
         if (!text.equals(entry.text)) {
            entry.text = text;
            this.save();
         }
      }
   }

   public boolean method3315(String username) {
      Prefs.Entry entry = this.items.get(key(username));
      return entry != null && entry.flag;
   }

   public boolean method3316(String username) {
      Prefs.Entry entry = this.items.get(key(username));
      return entry != null && entry.flag2;
   }

   public void method3317(String selected, boolean flag) {
      this.method3305(selected).flag = flag;
      this.save();
   }

   public void method3318(String selected, boolean flag) {
      Prefs.Entry entry = this.method3305(selected);
      entry.flag2 = flag;
      if (flag) {
         entry.flag = true;
      }

      this.save();
   }

   public boolean method3319(String selected) {
      Prefs.Entry entry = this.items.get(key(selected));
      return entry != null && entry.flag4;
   }

   public void method3320(String selected, boolean flag) {
      this.method3305(selected).flag4 = flag;
      this.save();
   }

   public boolean method3321(String username) {
      Prefs.Entry entry = this.items.get(key(username));
      return entry != null && (entry.flag3 || entry.flag2);
   }

   public void method3322(String voiceVolumeFor, boolean flag) {
      this.method3305(voiceVolumeFor).flag3 = flag;
      this.save();
   }

   public int method3323(String username) {
      Prefs.Entry entry = this.items.get(key(username));
      return entry == null ? 100 : entry.intValue;
   }

   public void method3324(String voiceVolumeFor, int n) {
      int intValue = Math.max(0, Math.min(200, n));
      Prefs.Entry entry = this.method3305(voiceVolumeFor);
      if (entry.intValue != intValue) {
         entry.intValue = intValue;
         this.save();
      }
   }

   public void save() {
      this.dirty = true;
   }

   public void flush() {
      if (this.dirty && !this.flag) {
         this.dirty = false;
         this.flag = true;
         long l = ++this.longValue;
         JsonObject json = this.method3327();
         Util.ioPool().execute(() -> {
            try {
               this.method3326(l, json);
            } finally {
               this.flag = false;
            }
         });
      }
   }

   public void method3325() {
      this.dirty = false;
      this.method3326(++this.longValue, this.method3327());
   }

   private void method3326(long l, JsonObject json) {
      synchronized (this.obj) {
         if (l > this.longValue2) {
            if (CloudJsonIo.method5993(this.path, json)) {
               this.longValue2 = l;
            }
         }
      }
   }

   private JsonObject method3327() {
      JsonObject json = new JsonObject();
      this.items
         .forEach(
            (var1x, entry) -> {
               if (entry.longValue != 0L
                  || entry.flag
                  || entry.flag2
                  || entry.longValue3 != 0L
                  || entry.text != null && !entry.text.isEmpty()
                  || entry.flag3
                  || entry.intValue != 100
                  || entry.flag4) {
                  JsonObject json2 = new JsonObject();
                  json2.addProperty("lastSeen", entry.longValue);
                  if (entry.longValue3 > 0L) {
                     json2.addProperty("uid", entry.longValue3);
                  }

                  if (entry.text != null && !entry.text.isEmpty()) {
                     json2.addProperty("mcName", entry.text);
                  }

                  if (entry.flag) {
                     json2.addProperty("muted", true);
                  }

                  if (entry.flag2) {
                     json2.addProperty("blocked", true);
                  }

                  if (entry.flag3) {
                     json2.addProperty("voiceMuted", true);
                  }

                  if (entry.intValue != 100) {
                     json2.addProperty("voiceVolume", entry.intValue);
                  }

                  if (entry.flag4) {
                     json2.addProperty("favorite", true);
                  }

                  json.add(var1x, json2);
               }
            }
         );
      return json;
   }

   private void load() {
      JsonObject json = CloudJsonIo.method5992(this.path);
      if (json != null) {
         json.entrySet().forEach(var1x -> {
            if (((JsonElement)var1x.getValue()).isJsonObject()) {
               JsonObject jsonObject = ((JsonElement)var1x.getValue()).getAsJsonObject();

               try {
                  this.items.put((String)var1x.getKey(), method3328(jsonObject));
               } catch (RuntimeException e) {
               }
            }
         });
      }
   }

   private static Prefs.Entry method3328(JsonObject jsonObject) {
      Prefs.Entry entry = new Prefs.Entry();
      entry.longValue = jsonObject.has("lastSeen") ? jsonObject.get("lastSeen").getAsLong() : 0L;
      entry.flag = jsonObject.has("muted") && jsonObject.get("muted").getAsBoolean();
      entry.flag2 = jsonObject.has("blocked") && jsonObject.get("blocked").getAsBoolean();
      entry.longValue3 = jsonObject.has("uid") ? jsonObject.get("uid").getAsLong() : 0L;
      entry.text = jsonObject.has("mcName") ? jsonObject.get("mcName").getAsString() : "";
      entry.flag3 = jsonObject.has("voiceMuted") && jsonObject.get("voiceMuted").getAsBoolean();
      entry.intValue = jsonObject.has("voiceVolume") ? Math.max(0, Math.min(200, jsonObject.get("voiceVolume").getAsInt())) : 100;
      entry.flag4 = jsonObject.has("favorite") && jsonObject.get("favorite").getAsBoolean();
      return entry;
   }

   public static final class Entry {
      public long longValue;
      public long longValue2;
      public boolean flag;
      public volatile boolean flag2;
      public long longValue3;
      public String text = "";
      public volatile boolean flag3;
      public volatile int intValue = 100;
      public boolean flag4;
   }
}
