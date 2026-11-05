package org.zenith.client.screens.nlgui.cloud;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.zenith.Zenith;
import org.zenith.utility.api.StoreApi;

public final class CloudLibrary {
   private static final String FILE_NAME = "cloud-library.json";
   private static final int MAX_ENTRIES = 512;
   private final Map<UUID, StoreApi.Data2> entries = new LinkedHashMap<>();
   private final File file;

   public CloudLibrary() {
      this.file = new File(Zenith.file, "cloud-library.json");
      this.load();
   }

   public synchronized List<StoreApi.Data2> all() {
      return new ArrayList<>(this.entries.values());
   }

   public synchronized boolean contains(UUID uuid) {
      return uuid != null && this.entries.containsKey(uuid);
   }

   public synchronized void remember(StoreApi.Data2 cloudTwin) {
      if (cloudTwin != null) {
         this.entries.remove(cloudTwin.getUUID());
         this.entries.put(cloudTwin.getUUID(), cloudTwin);

         while (this.entries.size() > 512) {
            this.entries.remove(this.entries.keySet().iterator().next());
         }

         this.save();
      }
   }

   public synchronized void forget(UUID uuid) {
      if (uuid != null && this.entries.remove(uuid) != null) {
         this.save();
      }
   }

   private void load() {
      if (this.file.isFile()) {
         try {
            JsonElement string = JsonParser.parseString(Files.readString(this.file.toPath(), StandardCharsets.UTF_8));
            if (!string.isJsonArray()) {
               return;
            }

            for (JsonElement json : string.getAsJsonArray()) {
               if (json.isJsonObject()) {
                  try {
                     StoreApi.Data2 data2 = StoreApi.Data2.method2003(json.getAsJsonObject());
                     this.entries.put(data2.getUUID(), data2);
                  } catch (Exception e) {
                  }
               }
            }
         } catch (Exception e) {
         }
      }
   }

   private void save() {
      try {
         JsonArray jsonArray = new JsonArray();

         for (StoreApi.Data2 data2 : this.entries.values()) {
            jsonArray.add(toJson(data2));
         }

         Files.createDirectories(this.file.toPath().getParent());
         Files.writeString(this.file.toPath(), jsonArray.toString(), StandardCharsets.UTF_8);
      } catch (Exception e) {
      }
   }

   private static JsonObject toJson(StoreApi.Data2 data2) {
      JsonObject json = new JsonObject();
      json.addProperty("id", data2.getUUID().toString());
      json.addProperty("kind", data2.kind());
      json.addProperty("name", data2.name());
      json.addProperty("fileName", data2.fileName());
      json.addProperty("description", data2.description());
      json.addProperty("server", data2.server());
      json.addProperty("sizeBytes", data2.getLongValue());
      json.addProperty("sha256", data2.getText6());
      json.addProperty("visibility", data2.getText7());
      json.addProperty("version", data2.getLongValue2());
      json.addProperty("createdAt", data2.getLongValue3());
      json.addProperty("updatedAt", data2.getLongValue4());
      json.addProperty("ownerUid", data2.getLongValue5());
      json.addProperty("ownerName", data2.getText8());
      json.addProperty("mine", data2.mine());
      json.addProperty("hasPreview", data2.hasPreview());
      json.addProperty("likes", data2.getIntValue());
      json.addProperty("liked", data2.isFlag3());
      json.addProperty("codes", data2.getIntValue2());
      return json;
   }
}
