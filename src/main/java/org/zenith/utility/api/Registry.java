package org.zenith.utility.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.platform.NativeImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zenith.utility.game.FocusSlotEnum;
import org.zenith.utility.misc.Class15;
import org.zenith.utility.misc.HoveredData;
import org.zenith.utility.misc.Rarity;
import org.zenith.utility.misc.WardrobeCategory;
import org.zenith.utility.misc.WardrobeSetInfo;
import org.zenith.utility.render.ModelJsonParser;

public final class Registry {
   private static final Logger logger = LoggerFactory.getLogger("Zenith/Wardrobe");
   private static final String text = "/assets/zenith/wardrobe/";
   private final Map<String, HoveredData> items = new LinkedHashMap<>();
   private final Map<String, WardrobeSetInfo> items2 = new LinkedHashMap<>();
   private final Map<String, Class15> items3 = new ConcurrentHashMap<>();
   private final Set<String> items4 = ConcurrentHashMap.newKeySet();
   private final Set<String> items5 = ConcurrentHashMap.newKeySet();
   private final Map<String, Long> items6 = new ConcurrentHashMap<>();
   private final Queue<Registry.Data> queue = new ConcurrentLinkedQueue<>();
   private final Index2 index2 = new Index2();
   private volatile String text2;
   private final ExecutorService executorService = Executors.newFixedThreadPool(2, task -> {
      Thread thread = new Thread(task, "zenith-wardrobe-loader");
      thread.setDaemon(true);
      thread.setPriority(1);
      return thread;
   });

   public void load() {
      this.items.clear();
      this.items2.clear();
      this.items3.clear();
      this.items4.clear();
      JsonObject json = method1429("/assets/zenith/wardrobe/registry.json");
      if (json == null) {
         logger.warn("Гардероб: registry.json не найден");
      } else {
         for (JsonElement jsonElement : json.has("items") && json.get("items").isJsonArray() ? json.getAsJsonArray("items") : new JsonArray()) {
            if (jsonElement.isJsonObject()) {
               JsonObject jsonObject = jsonElement.getAsJsonObject();
               String text = method1430(jsonObject, "id");
               FocusSlotEnum focusSlotEnum = FocusSlotEnum.method546(method1430(jsonObject, "slot"));
               if (text != null && focusSlotEnum != null) {
                  String str = method1430(jsonObject, "model");
                  this.items
                     .put(
                        text,
                        new HoveredData(
                           text,
                           focusSlotEnum,
                           Rarity.method4709(method1430(jsonObject, "rarity")),
                           method1430(jsonObject, "set"),
                           str == null ? text : str,
                           method1430(jsonObject, "theme")
                        )
                     );
               }
            }
         }

         for (JsonElement jsonElement : json.has("sets") && json.get("sets").isJsonArray() ? json.getAsJsonArray("sets") : new JsonArray()) {
            if (jsonElement.isJsonObject()) {
               JsonObject jsonObject = jsonElement.getAsJsonObject();
               String text = method1430(jsonObject, "id");
               if (text != null) {
                  ArrayList list = new ArrayList();

                  for (HoveredData hoveredData : this.items.values()) {
                     if (text.equals(hoveredData.getText2())) {
                        list.add(hoveredData.getText());
                     }
                  }

                  int intValue = 16777215;

                  try {
                     intValue = Integer.parseInt(method1430(jsonObject, "color").replace("#", ""), 16);
                  } catch (RuntimeException e) {
                     intValue = 16777215;
                  }

                  this.items2.put(text, new WardrobeSetInfo(text, Collections.unmodifiableList(list), intValue, method1430(jsonObject, "particle")));
               }
            }
         }

         logger.info("Гардероб: {} предметов, {} сетов", this.items.size(), this.items2.size());
      }
   }

   public HoveredData method1421(String string) {
      return string == null ? null : this.items.get(string);
   }

   public void method1422(Minecraft minecraft, int n) {
      if (minecraft.getTextureManager() != null) {
         Registry.Data data;
         for (int i = 0; i < n && (data = this.queue.poll()) != null; i++) {
            minecraft.getTextureManager()
               .register(data.getClass15().identifier, new DynamicTexture(() -> "zenith wardrobe", data.getNativeImage()));
            this.items3.put(data.name(), data.getClass15());
            this.items5.remove(data.name());
         }
      }
   }

   private void method1423(String text) {
      Long l = this.items6.get(text);
      if (l == null || System.nanoTime() - l >= 15000000000L) {
         if (this.items5.add(text)) {
            this.executorService.execute(() -> this.load(text));
         }
      }
   }

   private void load(String text) {
      try {
         Index2.Data data = this.index2.method6140(text);
         JsonObject jsonObject = JsonParser.parseString(new String(data.getByteValues(), StandardCharsets.UTF_8)).getAsJsonObject();
         Identifier identifier = Identifier.fromNamespaceAndPath("zenith", "wardrobe/dyn/" + text);
         Class15 class2 = ModelJsonParser.method107(text, jsonObject, identifier);
         NativeImage nativeImage = NativeImage.read(data.getByteValues2());
         this.items6.remove(text);
         this.queue.add(new Registry.Data(text, class2, nativeImage));
         this.text2 = null;
      } catch (Index2.Entry e) {
         this.items6.put(text, System.nanoTime());
         this.items5.remove(text);
         String message = e.getMessage();
         if (!Objects.equals(message, this.text2)) {
            this.text2 = message;
            logger.warn("Гардероб: модель {} отложена — {}", text, message);
         }
      } catch (LinkageError | Exception e) {
         this.items4.add(text);
         this.items5.remove(text);
         logger.error("Гардероб: модель {} не загружена", text, e);
      }
   }

   public List<HoveredData> all() {
      return new ArrayList<>(this.items.values());
   }

   public List<HoveredData> method1424(WardrobeCategory wardrobeCategory) {
      ArrayList<HoveredData> hoveredDatas = new ArrayList<>();

      for (HoveredData hoveredData : this.items.values()) {
         if (hoveredData.method1546() == wardrobeCategory) {
            hoveredDatas.add(hoveredData);
         }
      }

      return hoveredDatas;
   }

   public WardrobeSetInfo method1425(String str) {
      return str == null ? null : this.items2.get(str);
   }

   public List<WardrobeSetInfo> method1426() {
      return new ArrayList<>(this.items2.values());
   }

   public Class15 method1427(HoveredData hoveredData) {
      if (hoveredData == null) {
         return null;
      } else {
         Class15 class2 = this.items3.get(hoveredData.getText3());
         if (class2 == null && !this.items4.contains(hoveredData.getText3())) {
            this.method1423(hoveredData.getText3());
         }

         return class2;
      }
   }

   public void method1428() {
      this.items3.clear();
      this.items4.clear();
      this.items6.clear();
   }

   private static JsonObject method1429(String name) {
      try {
         JsonObject jsonObject;
         try (InputStream resourceAsStream = Registry.class.getResourceAsStream(name)) {
            if (resourceAsStream == null) {
               return null;
            }

            try (InputStreamReader inputStreamReader = new InputStreamReader(resourceAsStream, StandardCharsets.UTF_8)) {
               JsonElement reader = JsonParser.parseReader(inputStreamReader);
               jsonObject = reader.isJsonObject() ? reader.getAsJsonObject() : null;
            }
         }

         return jsonObject;
      } catch (RuntimeException | IOException e) {
         logger.error("Гардероб: не прочитан {}", name, e);
         return null;
      }
   }

   private static String method1430(JsonObject jsonObject, String memberName) {
      try {
         return jsonObject.has(memberName) && jsonObject.get(memberName).isJsonPrimitive() ? jsonObject.get(memberName).getAsString() : null;
      } catch (RuntimeException e) {
         return null;
      }
   }

   private static final class Data {
      private final String text;
      private final Class15 class15;
      private final NativeImage nativeImage;

      public String text() {
         return this.text;
      }

      public Class15 class15() {
         return this.class15;
      }

      public NativeImage nativeImage() {
         return this.nativeImage;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.class15, other.class15) && java.util.Objects.equals(this.nativeImage, other.nativeImage);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, class15, nativeImage);
      }

      @Override
      public String toString() {
         return "Data[" + "text=" + this.text + ", " + "class15=" + this.class15 + ", " + "nativeImage=" + this.nativeImage + "]";
      }

      private Data(String text, Class15 class2, NativeImage nativeImage) {
         this.text = text;
         this.class15 = class2;
         this.nativeImage = nativeImage;
      }

      public String name() {
         return this.text;
      }

      public Class15 getClass15() {
         return this.class15;
      }

      public NativeImage getNativeImage() {
         return this.nativeImage;
      }
   }
}
