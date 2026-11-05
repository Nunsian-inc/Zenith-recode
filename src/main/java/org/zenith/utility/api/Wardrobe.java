package org.zenith.utility.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents.ClientStopping;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.EndTick;
import net.minecraft.world.item.ItemStack;
import org.zenith.Zenith;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.Setting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.game.FocusSlotEnum;
import org.zenith.utility.misc.HoveredData;
import org.zenith.utility.misc.WardrobeCategory;
import org.zenith.utility.misc.WardrobeSetInfo;

public final class Wardrobe {
   private static final int CONST_intValue = 1;
   private static final FocusSlotEnum[] focusSlotEnums = FocusSlotEnum.values();
   private static final long CONST_longValue = 500L;
   private static final long CONST_longValue2 = 10000L;
   public final BooleanSetting showSelf = new BooleanSetting("wardrobe.showSelf", "wardrobe.showSelf.desc", true);
   public final BooleanSetting showOthers = new BooleanSetting("wardrobe.showOthers", "wardrobe.showOthers.desc", true);
   public final BooleanSetting particles = new BooleanSetting("wardrobe.particles", "wardrobe.particles.desc", true);
   public final BooleanSetting armorOnSkins = new BooleanSetting("wardrobe.armorOnSkins", "wardrobe.armorOnSkins.desc", false);
   public final BooleanSetting clothesOverArmor = new BooleanSetting("wardrobe.clothesOverArmor", "wardrobe.clothesOverArmor.desc", true);
   public final SliderSetting petScale = new SliderSetting("wardrobe.petScale", 1.0F, 0.5F, 2.0F, 0.05F, "wardrobe.petScale.desc", "x", null, null);
   public final SliderSetting particleBudget = new SliderSetting(
      "wardrobe.particleBudget", 60.0F, 10.0F, 200.0F, 5.0F, "wardrobe.particleBudget.desc", "/s", null, null
   );
   private final Registry registry = new Registry();
   private final Path path;
   private final EnumMap<FocusSlotEnum, String> enumMap = new EnumMap<>(FocusSlotEnum.class);
   private final Map<String, EnumMap<FocusSlotEnum, String>> items = new HashMap<>();
   private final List<HoveredData> items2 = new ArrayList<>(8);
   private final List<Wardrobe.Data> items3 = new ArrayList<>();
   private HoveredData hoveredData;
   private boolean flag;
   private int version;
   private String text = "";
   private long longValue;

   public Wardrobe(Path path) {
      this.path = path.resolve("wardrobe").resolve("wardrobe.json");
      this.registry.load();
      this.load();
      this.text = this.method2514();
      ClientTickEvents.END_CLIENT_TICK.register((EndTick)var1x -> {
         this.tick();
         if (var1x.gui.overlay() == null) {
            this.registry.method1422(var1x, 2);
         }
      });
      ClientLifecycleEvents.CLIENT_STOPPING.register((ClientStopping)var1x -> {
         if (!this.method2514().equals(this.text)) {
            this.method2518();
         }
      });
   }

   public static Wardrobe method2488() {
      Zenith zenith = Zenith.getInstance();
      return zenith == null ? null : zenith.getWardrobe();
   }

   public Registry getRegistry() {
      return this.registry;
   }

   public List<Setting> method2489() {
      return List.of(this.showSelf, this.showOthers, this.particles, this.armorOnSkins, this.clothesOverArmor, this.petScale, this.particleBudget);
   }

   public int version() {
      return this.version;
   }

   public boolean method2490(HoveredData hoveredData) {
      return hoveredData != null && hoveredData.getText().equals(this.enumMap.get(hoveredData.getFocusSlotEnum()));
   }

   public HoveredData method2491(FocusSlotEnum key) {
      return this.registry.method1421(this.enumMap.get(key));
   }

   public void method2492(HoveredData hovered) {
      if (hovered != null) {
         this.enumMap.put(hovered.getFocusSlotEnum(), hovered.getText());
         if (hovered.getFocusSlotEnum() == FocusSlotEnum.CAPE) {
            this.enumMap.remove(FocusSlotEnum.BACK);
         } else if (hovered.getFocusSlotEnum() == FocusSlotEnum.BACK) {
            this.enumMap.remove(FocusSlotEnum.CAPE);
         }

         this.changed();
      }
   }

   public void method2493(FocusSlotEnum focusSlotEnum) {
      if (this.enumMap.remove(focusSlotEnum) != null) {
         this.changed();
      }
   }

   public void method2494(HoveredData hovered) {
      if (this.method2490(hovered)) {
         this.method2493(hovered.getFocusSlotEnum());
      } else {
         this.method2492(hovered);
      }
   }

   public void method2495(WardrobeSetInfo wardrobeSetInfo) {
      if (wardrobeSetInfo != null) {
         for (String piece : wardrobeSetInfo.pieces()) {
            HoveredData hoveredData = this.registry.method1421(piece);
            if (hoveredData != null) {
               this.enumMap.put(hoveredData.getFocusSlotEnum(), hoveredData.getText());
               if (hoveredData.getFocusSlotEnum() == FocusSlotEnum.CAPE) {
                  this.enumMap.remove(FocusSlotEnum.BACK);
               } else if (hoveredData.getFocusSlotEnum() == FocusSlotEnum.BACK) {
                  this.enumMap.remove(FocusSlotEnum.CAPE);
               }
            }
         }

         this.enumMap.remove(FocusSlotEnum.CHARACTER);
         this.changed();
      }
   }

   public void method2496() {
      if (!this.enumMap.isEmpty()) {
         this.enumMap.clear();
         this.changed();
      }
   }

   public int method2497() {
      return this.enumMap.size();
   }

   public void setHoveredData(HoveredData hoveredData) {
      this.hoveredData = hoveredData;
   }

   public void setFlag(boolean flag) {
      this.flag = flag;
   }

   public boolean isFlag() {
      return this.flag;
   }

   public List<HoveredData> method2498(String name, boolean flag) {
      this.items2.clear();
      EnumMap enumMap = flag ? this.enumMap : (name == null ? null : this.items.get(name.toLowerCase(Locale.ROOT)));
      if (enumMap == null) {
         return this.items2;
      } else {
         HoveredData hoveredData = flag && this.flag ? this.hoveredData : null;

         for (FocusSlotEnum focusSlotEnum : focusSlotEnums) {
            HoveredData hoveredData2 = hoveredData != null && hoveredData.getFocusSlotEnum() == focusSlotEnum ? hoveredData : this.registry.method1421((String)enumMap.get(focusSlotEnum));
            if (hoveredData2 != null) {
               this.items2.add(hoveredData2);
            }
         }

         HoveredData hoveredData2 = this.method2499(this.items2, FocusSlotEnum.CAPE);
         HoveredData hoveredData3 = this.method2499(this.items2, FocusSlotEnum.BACK);
         if (hoveredData2 != null && hoveredData3 != null) {
            this.items2.remove(hoveredData == hoveredData3 ? hoveredData2 : hoveredData3);
         }

         return this.items2;
      }
   }

   public HoveredData method2499(List<HoveredData> hoveredDatas, FocusSlotEnum focusSlotEnum) {
      for (int i = 0; i < hoveredDatas.size(); i++) {
         if (((HoveredData)hoveredDatas.get(i)).getFocusSlotEnum() == focusSlotEnum) {
            return (HoveredData)hoveredDatas.get(i);
         }
      }

      return null;
   }

   public WardrobeSetInfo method2500(List<HoveredData> hoveredDatas) {
      if (this.method2499(hoveredDatas, FocusSlotEnum.CHARACTER) != null) {
         return null;
      } else {
         String str = null;
         int n = 0;

         for (HoveredData hoveredData : hoveredDatas) {
            if (hoveredData.method1546() == WardrobeCategory.CLOTHING) {
               if (hoveredData.getText2() == null) {
                  return null;
               }

               if (str == null) {
                  str = hoveredData.getText2();
               } else if (!str.equals(hoveredData.getText2())) {
                  return null;
               }

               n++;
            }
         }

         WardrobeSetInfo wardrobeSetInfo = this.registry.method1425(str);
         return wardrobeSetInfo != null && n == wardrobeSetInfo.pieces().size() && n > 0 ? wardrobeSetInfo : null;
      }
   }

   public void method2501(String str, Map<FocusSlotEnum, String> map) {
      if (str != null) {
         String lowerCase = str.toLowerCase(Locale.ROOT);
         if (map != null && !map.isEmpty()) {
            this.items.put(lowerCase, new EnumMap<>(map));
         } else {
            this.items.remove(lowerCase);
         }
      }
   }

   public void method2502() {
      this.items.clear();
   }

   public int method2503(Collection<String> items) {
      EnumMap map = new EnumMap<>(FocusSlotEnum.class);

      for (String string : items) {
         HoveredData hoveredData = this.registry.method1421(string);
         if (hoveredData != null) {
            map.put(hoveredData.getFocusSlotEnum(), hoveredData.getText());
         }
      }

      if (map.containsKey(FocusSlotEnum.CAPE)) {
         map.remove(FocusSlotEnum.BACK);
      }

      this.enumMap.clear();
      this.enumMap.putAll(map);
      this.changed();
      return map.size();
   }

   public Map<FocusSlotEnum, String> method2504() {
      return new EnumMap<>(this.enumMap);
   }

   public List<Wardrobe.Data> method2505() {
      return List.copyOf(this.items3);
   }

   public Wardrobe.Data method2506(String replacement) {
      String text = replacement == null ? "" : replacement.strip();
      if (!text.isEmpty() && !this.enumMap.isEmpty()) {
         Wardrobe.Data data = new Wardrobe.Data(text, new EnumMap<>(this.enumMap), System.currentTimeMillis());
         this.items3.removeIf(var1x -> var1x.name().equalsIgnoreCase(text));
         this.items3.add(0, data);
         this.changed();
         return data;
      } else {
         return null;
      }
   }

   public boolean method2507(String replacement) {
      String anotherString = replacement == null ? "" : replacement.strip();

      for (Wardrobe.Data data : this.items3) {
         if (data.name().equalsIgnoreCase(anotherString)) {
            return true;
         }
      }

      return false;
   }

   public int method2508(Wardrobe.Data data) {
      return data == null ? 0 : this.method2503(data.getItems().values());
   }

   public void method2509(Wardrobe.Data data) {
      if (this.items3.remove(data)) {
         this.changed();
      }
   }

   public boolean method2510(Wardrobe.Data data) {
      return data != null && !this.enumMap.isEmpty() && this.enumMap.equals(data.getItems());
   }

   public JsonObject method2511() {
      return this.method2519();
   }

   public void method2512(JsonObject jsonObject) {
      if (jsonObject != null) {
         this.method2517(jsonObject, jsonObject.has("presets"));
         this.changed();
      }
   }

   public boolean method2513(ItemStack mainHandItem) {
      FocusSlotEnum key = FocusSlotEnum.method545(mainHandItem);
      return key != null && this.enumMap.containsKey(key);
   }

   private void changed() {
      this.version++;
      this.method2518();
   }

   private void tick() {
      long now = System.currentTimeMillis();
      if (now >= this.longValue && !this.method2514().equals(this.text)) {
         this.longValue = now + (this.method2518() ? 500L : 10000L);
      }
   }

   private String method2514() {
      StringBuilder builder = new StringBuilder();

      for (Setting setting : this.method2489()) {
         if (setting instanceof BooleanSetting booleanSetting) {
            builder.append(booleanSetting.isEnabled()).append('|');
         } else if (setting instanceof SliderSetting sliderSetting) {
            builder.append(sliderSetting.getValue3()).append('|');
         }
      }

      return builder.toString();
   }

   private void load() {
      JsonObject jsonObject = JsonIo.method1009(this.path);
      if (jsonObject != null) {
         this.method2517(jsonObject, true);
      }
   }

   private EnumMap<FocusSlotEnum, String> method2515(JsonObject jsonObject) {
      EnumMap map = new EnumMap<>(FocusSlotEnum.class);

      for (Entry entry : jsonObject.entrySet()) {
         FocusSlotEnum key = FocusSlotEnum.method546((String)entry.getKey());
         if (key != null && ((JsonElement)entry.getValue()).isJsonPrimitive()) {
            HoveredData hoveredData = this.registry.method1421(((JsonElement)entry.getValue()).getAsString());
            if (hoveredData != null && hoveredData.getFocusSlotEnum() == key) {
               map.put(key, hoveredData.getText());
            }
         }
      }

      return map;
   }

   private static JsonObject method2516(Map<FocusSlotEnum, String> enumMap) {
      JsonObject json = new JsonObject();

      for (Entry entry : enumMap.entrySet()) {
         json.addProperty(((FocusSlotEnum)entry.getKey()).name(), (String)entry.getValue());
      }

      return json;
   }

   private void method2517(JsonObject jsonObject, boolean has) {
      try {
         if (jsonObject.has("equipped") && jsonObject.get("equipped").isJsonObject()) {
            EnumMap map = this.method2515(jsonObject.getAsJsonObject("equipped"));
            this.enumMap.clear();
            this.enumMap.putAll(map);
         }
      } catch (RuntimeException e) {
         this.enumMap.clear();
      }

      if (has && jsonObject.has("presets") && jsonObject.get("presets").isJsonArray()) {
         this.items3.clear();

         for (JsonElement json : jsonObject.getAsJsonArray("presets")) {
            try {
               if (json.isJsonObject()) {
                  JsonObject jsonObject2 = json.getAsJsonObject();
                  String string = jsonObject2.has("name") ? jsonObject2.get("name").getAsString() : "";
                  if (!string.isBlank() && jsonObject2.has("items") && jsonObject2.get("items").isJsonObject()) {
                     EnumMap items = this.method2515(jsonObject2.getAsJsonObject("items"));
                     if (!items.isEmpty()) {
                        this.items3.add(new Wardrobe.Data(string, items, jsonObject2.has("createdAt") ? jsonObject2.get("createdAt").getAsLong() : 0L));
                     }
                  }
               }
            } catch (RuntimeException e) {
            }
         }
      }

      JsonObject json = jsonObject.has("settings") && jsonObject.get("settings").isJsonObject() ? jsonObject.getAsJsonObject("settings") : new JsonObject();

      for (Setting setting : this.method2489()) {
         try {
            JsonElement jsonElement = json.get(setting.getKey());
            if (jsonElement != null && jsonElement.isJsonPrimitive()) {
               if (setting instanceof BooleanSetting booleanSetting) {
                  booleanSetting.setEnabled(jsonElement.getAsBoolean());
               } else if (setting instanceof SliderSetting sliderSetting) {
                  sliderSetting.method2400(jsonElement.getAsFloat());
               }
            }
         } catch (RuntimeException e) {
         }
      }
   }

   private boolean method2518() {
      if (JsonIo.method1010(this.path, this.method2519())) {
         this.text = this.method2514();
         return true;
      } else {
         return false;
      }
   }

   private JsonObject method2519() {
      JsonObject json = new JsonObject();
      json.addProperty("format", 1);
      json.add("equipped", method2516(this.enumMap));
      JsonArray jsonArray = new JsonArray();

      for (Wardrobe.Data data : this.items3) {
         JsonObject json2 = new JsonObject();
         json2.addProperty("name", data.name());
         json2.addProperty("createdAt", data.getLongValue());
         json2.add("items", method2516(data.getItems()));
         jsonArray.add(json2);
      }

      json.add("presets", jsonArray);
      JsonObject json2 = new JsonObject();

      for (Setting setting : this.method2489()) {
         if (setting instanceof BooleanSetting booleanSetting) {
            json2.addProperty(setting.getKey(), booleanSetting.isEnabled());
         } else if (setting instanceof SliderSetting sliderSetting) {
            json2.addProperty(setting.getKey(), sliderSetting.getValue3());
         }
      }

      json.add("settings", json2);
      return json;
   }

   public static final class Data {
      private final String text;
      private final Map<FocusSlotEnum, String> items;
      private final long longValue;

      public String text() {
         return this.text;
      }

      public Map<FocusSlotEnum, String> items() {
         return this.items;
      }

      public long longValue() {
         return this.longValue;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.items, other.items) && java.util.Objects.equals(this.longValue, other.longValue);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, items, longValue);
      }

      @Override
      public String toString() {
         return "Data[" + "text=" + this.text + ", " + "items=" + this.items + ", " + "longValue=" + this.longValue + "]";
      }

      public Data(String text, Map<FocusSlotEnum, String> items, long longValue) {
         this.text = text;
         this.items = items;
         this.longValue = longValue;
      }

      public String name() {
         return this.text;
      }

      public Map<FocusSlotEnum, String> getItems() {
         return this.items;
      }

      public long getLongValue() {
         return this.longValue;
      }
   }
}
