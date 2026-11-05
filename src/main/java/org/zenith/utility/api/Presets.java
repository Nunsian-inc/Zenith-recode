package org.zenith.utility.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;

public final class Presets {
   private static final String text = "presets.json";
   private static final int CONST_intValue = 64;
   private static final int CONST_intValue2 = 32;
   private final Path path;
   private final List<Presets.Data> items = new ArrayList<>();

   public Presets(Path path) {
      this.path = path.resolve("presets.json");
      this.load();
   }

   public List<Presets.Data> all() {
      return List.copyOf(this.items);
   }

   public int size() {
      return this.items.size();
   }

   public boolean isFull() {
      return this.items.size() >= 64;
   }

   public boolean method7737(String string, Collection<String> strings) {
      String text = clean(string);
      if (!text.isEmpty() && strings != null && !strings.isEmpty()) {
         var items = List.copyOf(new LinkedHashSet(strings));
         int indexOf = this.indexOf(text);
         if (indexOf >= 0) {
            this.items.set(indexOf, new Presets.Data(text, items));
            this.method7741();
            return true;
         } else if (this.isFull()) {
            return false;
         } else {
            this.items.add(new Presets.Data(text, items));
            this.method7741();
            return true;
         }
      } else {
         return false;
      }
   }

   public boolean method7738(String text) {
      int indexOf = this.indexOf(text);
      if (indexOf < 0) {
         return false;
      } else {
         this.items.remove(indexOf);
         this.method7741();
         return true;
      }
   }

   public Presets.Data method7739(String text) {
      int indexOf = this.indexOf(text);
      return indexOf < 0 ? null : this.items.get(indexOf);
   }

   public String method7740() {
      for (int i = 1; i <= 65; i++) {
         String text = "Набор " + i;
         if (this.indexOf(text) < 0) {
            return text;
         }
      }

      return "Набор";
   }

   private int indexOf(String text) {
      String lowerCase = clean(text).toLowerCase(Locale.ROOT);
      if (lowerCase.isEmpty()) {
         return -1;
      } else {
         for (int i = 0; i < this.items.size(); i++) {
            if (this.items.get(i).name().toLowerCase(Locale.ROOT).equals(lowerCase)) {
               return i;
            }
         }

         return -1;
      }
   }

   private static String clean(String string) {
      if (string == null) {
         return "";
      } else {
         String str = string.strip();
         return str.length() > 32 ? str.substring(0, 32).strip() : str;
      }
   }

   private void load() {
      JsonObject json = JsonIo.method1009(this.path);
      if (json != null && json.has("presets") && json.get("presets").isJsonArray()) {
         for (JsonElement jsonElement : json.getAsJsonArray("presets")) {
            if (jsonElement.isJsonObject()) {
               JsonObject jsonObject = jsonElement.getAsJsonObject();
               String text = jsonObject.has("name") && jsonObject.get("name").isJsonPrimitive() ? clean(jsonObject.get("name").getAsString()) : "";
               if (!text.isEmpty() && jsonObject.has("items") && jsonObject.get("items").isJsonArray()) {
                  ArrayList items = new ArrayList();

                  for (JsonElement json2 : jsonObject.getAsJsonArray("items")) {
                     if (json2.isJsonPrimitive()) {
                        items.add(json2.getAsString());
                     }
                  }

                  if (!items.isEmpty() && this.items.size() < 64) {
                     this.items.add(new Presets.Data(text, items));
                  }
               }
            }
         }
      }
   }

   private void method7741() {
      JsonObject json = new JsonObject();
      JsonArray jsonArray = new JsonArray();

      for (Presets.Data data : this.items) {
         JsonObject json2 = new JsonObject();
         json2.addProperty("name", data.name());
         JsonArray jsonArray2 = new JsonArray();

         for (String string : data.getItems()) {
            jsonArray2.add(string);
         }

         json2.add("items", jsonArray2);
         jsonArray.add(json2);
      }

      json.add("presets", jsonArray);
      JsonIo.method1010(this.path, json);
   }

   public static final class Data {
      private final String text;
      private final List<String> items;

      public String text() {
         return this.text;
      }

      public List<String> items() {
         return this.items;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.items, other.items);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, items);
      }

      @Override
      public String toString() {
         return "Data[" + "text=" + this.text + ", " + "items=" + this.items + "]";
      }

      public Data(String text, List<String> items) {
         items = List.copyOf(items);
         this.text = text;
         this.items = items;
      }

      public String name() {
         return this.text;
      }

      public List<String> getItems() {
         return this.items;
      }
   }
}
