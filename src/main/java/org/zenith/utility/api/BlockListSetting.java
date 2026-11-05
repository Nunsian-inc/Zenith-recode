package org.zenith.utility.api;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.zenith.client.settings.Setting;

public class BlockListSetting extends Setting {
   private List<String> items;
   private volatile int version;
   private static final Map<Block, String> items2 = new ConcurrentHashMap<>();
   private static final Map<Item, String> items3 = new ConcurrentHashMap<>();
   private volatile BlockListSetting.Data data;
   private static final Gson gson = new Gson();

   public void method1612(List<String> items) {
      this.items = items;
      this.version++;
   }

   public int getVersion() {
      return this.version;
   }

   public BlockListSetting(String key, List<String> items) {
      this(key, "", items, () -> true);
   }

   public BlockListSetting(String key, List<String> items, Supplier<Boolean> supplier) {
      this(key, "", items, supplier);
   }

   public BlockListSetting(String key, String description, List<String> items, Supplier<Boolean> supplier) {
      super(key, description);
      this.items = items;
   }

   public List<String> getItems() {
      return this.items;
   }

   public void method1613(String str) {
      this.items.add(str);
      this.version++;
   }

   public void remove(String str) {
      this.items.remove(str);
      this.version++;
   }

   public boolean contains(String str) {
      return this.method1614().contains(str);
   }

   private Set<String> method1614() {
      var items = this.items;
      int version = this.version;
      BlockListSetting.Data data = this.data;
      if (data == null || data.version() != version || data.getItems() != items) {
         Object[] objects = items.toArray();
         Set<String> strings = new HashSet<>(Math.max(16, objects.length * 2));

         for (Object obj : objects) {
            strings.add((String)obj);
         }

         data = new BlockListSetting.Data(version, items, strings);
         this.data = data;
      }

      return data.getItems2();
   }

   private static String method1615(Block block) {
      return items2.computeIfAbsent(block, var0x -> var0x.getDescriptionId().replace("block.minecraft.", ""));
   }

   private static String method1616(Item key) {
      return items3.computeIfAbsent(key, var0x -> var0x.getDescriptionId().replace("item.minecraft.", ""));
   }

   public void method1617(Block key) {
      this.method1613(key.getDescriptionId().replace("block.minecraft.", ""));
   }

   public void method1618(Item item) {
      this.method1613(item.getDescriptionId().replace("item.minecraft.", ""));
   }

   public void method1619(Block key) {
      this.remove(key.getDescriptionId().replace("block.minecraft.", ""));
   }

   public void method1620(Item item) {
      this.remove(item.getDescriptionId().replace("item.minecraft.", ""));
   }

   public boolean method1621(Block block) {
      return this.contains(method1615(block));
   }

   public boolean method1622(Item key) {
      return this.contains(method1616(key));
   }

   public void clear() {
      this.items.clear();
      this.version++;
   }

   @Override
   public void safe(JsonObject json) {
      json.add(String.valueOf(this.key), gson.toJsonTree(this.getItems()));
   }

   @Override
   public void load(JsonObject json) {
      Type type = (new TypeToken<List<String>>() {}).getType();
      JsonElement jsonElement = json.get(String.valueOf(this.key));
      if (jsonElement != null && jsonElement.isJsonArray()) {
         List items = (List)gson.fromJson(jsonElement, type);
         this.method1612(items);
      }
   }

   private static final class Data {
      private final int intValue;
      private final List<String> items;
      private final Set<String> items2;

      public int intValue() {
         return this.intValue;
      }

      public List<String> items() {
         return this.items;
      }

      public Set<String> items2() {
         return this.items2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.items, other.items) && java.util.Objects.equals(this.items2, other.items2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(intValue, items, items2);
      }

      @Override
      public String toString() {
         return "Data[" + "intValue=" + this.intValue + ", " + "items=" + this.items + ", " + "items2=" + this.items2 + "]";
      }

      private Data(int intValue, List<String> items, Set<String> strings) {
         this.intValue = intValue;
         this.items = items;
         this.items2 = strings;
      }

      public int version() {
         return this.intValue;
      }

      public List<String> getItems() {
         return this.items;
      }

      public Set<String> getItems2() {
         return this.items2;
      }
   }
}
