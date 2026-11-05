package org.zenith.utility.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public final class Friends {
   private final Path path;
   private final Set<String> items = new LinkedHashSet<>();
   private Friends.Handler handler;

   public Friends(Path path) {
      this.path = path.resolve("nexus-friends.json");
      this.load();
   }

   public void setHandler(Friends.Handler handler) {
      this.handler = handler;
   }

   public boolean method6442(String username) {
      return username != null && !this.items.isEmpty() ? this.items.contains(username.toLowerCase(Locale.ROOT)) : false;
   }

   public boolean add(String username) {
      String lowerCase = username.toLowerCase(Locale.ROOT);
      if (!this.isFull() && this.items.add(lowerCase)) {
         if (this.handler != null) {
            this.handler.method6445(lowerCase);
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean method6443(String selected) {
      String lowerCase = selected.toLowerCase(Locale.ROOT);
      if (!this.items.remove(lowerCase)) {
         return false;
      } else {
         if (this.handler != null) {
            this.handler.method6446(lowerCase);
         }

         return true;
      }
   }

   public boolean method6444(Collection<String> strings) {
      LinkedHashSet<String> strings2 = new LinkedHashSet();

      for (String str : strings) {
         if (str != null && !str.isBlank()) {
            strings2.add(str.toLowerCase(Locale.ROOT));
         }
      }

      if (strings2.equals(this.items)) {
         return false;
      } else if (strings2.size() >= this.items.size()) {
         this.items.clear();
         this.items.addAll(strings2);
         return true;
      } else {
         boolean flag = false;

         for (String str : strings2) {
            if (this.items.size() >= 200) {
               break;
            }

            flag |= this.items.add(str);
         }

         return flag;
      }
   }

   public boolean isFull() {
      return this.items.size() >= 200;
   }

   public List<String> all() {
      return new ArrayList<>(this.items);
   }

   public int size() {
      return this.items.size();
   }

   public void clear() {
      this.items.clear();
      if (this.handler != null) {
         this.handler.method6447();
      }
   }

   public void save() {
      JsonArray jsonArray = new JsonArray();
      this.items.forEach(jsonArray::add);
      JsonObject json = new JsonObject();
      json.add("friends", jsonArray);
      CloudJsonIo.method5993(this.path, json);
   }

   private void load() {
      JsonObject json = CloudJsonIo.method5992(this.path);
      if (json != null && json.has("friends") && json.get("friends").isJsonArray()) {
         json.getAsJsonArray("friends").forEach(var1x -> {
            if (var1x.isJsonPrimitive() && this.items.size() < 200) {
               String string = var1x.getAsString();
               if (!string.isBlank() && string.length() <= 32) {
                  this.items.add(string.toLowerCase(Locale.ROOT));
               }
            }
         });
      }
   }

   public interface Handler {
      void method6445(String lowerCase);

      void method6446(String lowerCase);

      void method6447();
   }
}
