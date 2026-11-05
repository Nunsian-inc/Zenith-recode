package org.zenith.utility.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import org.zenith.utility.game.ItemMatcher;

public final class SborList {
   public static final float CONST_floatValue = -1.0F;
   private final String text;
   private final String text2;
   private final List<SborList.Data> items;

   public SborList(String text, String str, List<SborList.Data> coll) {
      this.text = text;
      this.text2 = str;
      this.items = List.copyOf(coll);
   }

   public String getName() {
      return this.text;
   }

   public String getText2() {
      return this.text2;
   }

   public List<SborList.Data> getItems() {
      return this.items;
   }

   public int size() {
      return this.items.size();
   }

   public boolean method1751(String anObject) {
      return this.text2 != null && this.text2.equals(anObject);
   }

   public static SborList method1752(String inputName, InventoryPreset set) {
      ArrayList coll = new ArrayList();

      for (int i = 0; i < 36; i++) {
         ItemMatcher itemMatcher = set.method870(i);
         if (itemMatcher != null && !itemMatcher.getItemStack().isEmpty()) {
            coll.add(
               new SborList.Data(
                  i,
                  itemMatcher.getText22(),
                  BuiltInRegistries.ITEM.getKey(itemMatcher.getItemStack().getItem()).toString(),
                  set.method877(i),
                  set.method879(i),
                  set.method881(i)
               )
            );
         }
      }

      return new SborList(inputName, set.getText4(), coll);
   }

   public int method1753(InventoryPreset set) {
      set.method876();
      int n = 0;

      for (SborList.Data data : this.items) {
         ItemMatcher key = set.method889(data.getText(), data.getText2());
         if (key != null && !key.getItemStack().isEmpty()) {
            set.method874(data.getIntValue(), key);
            if (data.count() > 0) {
               set.method878(data.getIntValue(), data.count());
            }

            set.method880(data.getIntValue(), data.getLongValue());
            if (data.getFloatValue() >= 0.0F) {
               set.method882(data.getIntValue(), data.getFloatValue());
            }
         } else {
            n++;
         }
      }

      return n;
   }

   public JsonObject save() {
      JsonObject json = new JsonObject();
      json.addProperty("name", this.text);
      json.addProperty("server", this.text2);
      JsonArray jsonArray = new JsonArray();

      for (SborList.Data data : this.items) {
         JsonObject json2 = new JsonObject();
         json2.addProperty("slot", data.getIntValue());
         json2.addProperty("search", data.getText());
         json2.addProperty("item", data.getText2());
         json2.addProperty("count", data.count());
         json2.addProperty("price", data.getLongValue());
         json2.addProperty("durability", data.getFloatValue());
         jsonArray.add(json2);
      }

      json.add("slots", jsonArray);
      return json;
   }

   public static SborList method1754(JsonObject jsonObject) {
      if (jsonObject != null && jsonObject.has("name")) {
         String string = jsonObject.get("name").getAsString();
         if (string != null && !string.isBlank()) {
            String str = jsonObject.has("server") ? jsonObject.get("server").getAsString() : null;
            ArrayList coll = new ArrayList();
            if (jsonObject.has("slots") && jsonObject.get("slots").isJsonArray()) {
               for (JsonElement json : jsonObject.getAsJsonArray("slots")) {
                  if (json.isJsonObject()) {
                     JsonObject jsonObject2 = json.getAsJsonObject();
                     if (jsonObject2.has("slot") && jsonObject2.has("search")) {
                        coll.add(
                           new SborList.Data(
                              jsonObject2.get("slot").getAsInt(),
                              jsonObject2.get("search").getAsString(),
                              jsonObject2.has("item") ? jsonObject2.get("item").getAsString() : null,
                              jsonObject2.has("count") ? jsonObject2.get("count").getAsInt() : 1,
                              jsonObject2.has("price") ? jsonObject2.get("price").getAsLong() : 0L,
                              jsonObject2.has("durability") ? jsonObject2.get("durability").getAsFloat() : -1.0F
                           )
                        );
                     }
                  }
               }
            }

            return new SborList(string, str, coll);
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   public static final class Data {
      private final int intValue;
      private final String text;
      private final String text2;
      private final int intValue2;
      private final long longValue;
      private final float floatValue;

      public int intValue() {
         return this.intValue;
      }

      public String text() {
         return this.text;
      }

      public String text2() {
         return this.text2;
      }

      public int intValue2() {
         return this.intValue2;
      }

      public long longValue() {
         return this.longValue;
      }

      public float floatValue() {
         return this.floatValue;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.intValue2, other.intValue2) && java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.floatValue, other.floatValue);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(intValue, text, text2, intValue2, longValue, floatValue);
      }

      @Override
      public String toString() {
         return "Data[" + "intValue=" + this.intValue + ", " + "text=" + this.text + ", " + "text2=" + this.text2 + ", " + "intValue2=" + this.intValue2 + ", " + "longValue=" + this.longValue + ", " + "floatValue=" + this.floatValue + "]";
      }

      public Data(int intValue, String text, String string, int n, long longValue, float floatValue) {
         this.intValue = intValue;
         this.text = text;
         this.text2 = string;
         this.intValue2 = n;
         this.longValue = longValue;
         this.floatValue = floatValue;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public String getText() {
         return this.text;
      }

      public String getText2() {
         return this.text2;
      }

      public int count() {
         return this.intValue2;
      }

      public long getLongValue() {
         return this.longValue;
      }

      public float getFloatValue() {
         return this.floatValue;
      }
   }
}
