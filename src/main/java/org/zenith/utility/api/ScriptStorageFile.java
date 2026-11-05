package org.zenith.utility.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import org.slf4j.LoggerFactory;
import org.zenith.client.scripting.ScriptValueUtils;

public final class ScriptStorageFile {
   public static final int CONST_intValue = 262144;
   private static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();
   private final Path path;
   private JsonObject jsonObject = new JsonObject();
   private boolean dirty;
   private int size;
   private int intValue;

   public ScriptStorageFile(Path path) {
      this.path = path;

      try {
         if (Files.isRegularFile(path)) {
            if (Files.size(path) > 262144L) {
               Path sibling = path.resolveSibling(path.getFileName() + ".too-big-" + System.currentTimeMillis());
               Files.move(path, sibling, StandardCopyOption.REPLACE_EXISTING);
               LoggerFactory.getLogger("ZenithScripts").warn("Хранилище {} больше {} КБ — сохранено как {}", new Object[]{path, 256, sibling.getFileName()});
            } else {
               JsonElement string = JsonParser.parseString(Files.readString(path, StandardCharsets.UTF_8));
               if (string.isJsonObject()) {
                  this.jsonObject = string.getAsJsonObject();
               }
            }
         }
      } catch (Exception e) {
         try {
            if (Files.isRegularFile(path)) {
               Files.copy(path, path.resolveSibling(path.getFileName() + ".broken-" + System.currentTimeMillis()), StandardCopyOption.REPLACE_EXISTING);
            }
         } catch (Exception ex) {
         }
      }

      this.size = method1005(gson.toJson(this.jsonObject));

      for (JsonElement jsonElement : this.jsonObject.asMap().values()) {
         if (!jsonElement.isJsonNull()) {
            this.intValue++;
         }
      }
   }

   public static int method1005(CharSequence json) {
      int n = 0;
      int n2 = 0;

      for (int i = json.length(); n2 < i; n2++) {
         char charAt = json.charAt(n2);
         if (charAt < 128) {
            n++;
         } else if (charAt < 2048) {
            n += 2;
         } else if (Character.isHighSurrogate(charAt) && n2 + 1 < i && Character.isLowSurrogate(json.charAt(n2 + 1))) {
            n += 4;
            n2++;
         } else {
            n += 3;
         }
      }

      return n;
   }

   public JsonElement method1006(String memberName) {
      return this.jsonObject.get(memberName);
   }

   public Set<String> method1007() {
      return this.jsonObject.keySet();
   }

   public void method1008(String property, JsonElement json) {
      if (property.length() > 128) {
         throw ScriptValueUtils.method6251("ключ длиннее 128 символов");
      } else {
         JsonElement jsonElement = this.jsonObject.get(property);
         boolean flag = jsonElement != null && !jsonElement.isJsonNull();
         boolean flag2 = json != null && !json.isJsonNull();
         int n = !flag && !flag2 ? 0 : method1005(gson.toJson(property)) + 1;
         int n2 = flag ? n + method1005(gson.toJson(jsonElement)) : 0;
         int n3 = flag2 ? n + method1005(gson.toJson(json)) : 0;
         int intValue = this.intValue - (flag ? 1 : 0) + (flag2 ? 1 : 0);
         int size = this.size - n2 + n3 - Math.max(0, this.intValue - 1) + Math.max(0, intValue - 1);
         if (size > 262144) {
            throw ScriptValueUtils.method6251("хранилище скрипта больше 256 КБ");
         } else {
            if (flag2) {
               this.jsonObject.add(property, json);
            } else {
               this.jsonObject.remove(property);
            }

            this.intValue = intValue;
            this.size = size;
            this.dirty = true;
         }
      }
   }

   public int size() {
      return this.size;
   }

   public void flush() {
      if (this.dirty) {
         this.dirty = false;

         try {
            Files.createDirectories(this.path.getParent());
            Path sibling = this.path.resolveSibling(this.path.getFileName() + ".tmp");
            Files.writeString(sibling, gson.toJson(this.jsonObject), StandardCharsets.UTF_8);
            Files.move(sibling, this.path, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
         } catch (Exception e) {
            this.dirty = true;
         }
      }
   }
}
