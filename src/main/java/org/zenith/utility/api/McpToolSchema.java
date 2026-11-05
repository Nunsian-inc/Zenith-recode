package org.zenith.utility.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import net.minecraft.client.Minecraft;
import org.zenith.Zenith;
import org.zenith.client.scripting.Module1;
import org.zenith.client.scripting.Script;
import org.zenith.client.scripting.Storage;

public final class McpToolSchema {
   private static final Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

   private McpToolSchema() {
   }

   private static void method5498(JsonArray jsonArray, String str, String str2, String... strings) {
      JsonObject json = new JsonObject();
      json.addProperty("name", str);
      json.addProperty("description", str2);
      JsonObject json2 = new JsonObject();
      json2.addProperty("type", "object");
      JsonObject json3 = new JsonObject();
      JsonArray jsonArray2 = new JsonArray();

      for (String str3 : strings) {
         boolean endsWith = str3.endsWith("?");
         String[] strings2 = (endsWith ? str3.substring(0, str3.length() - 1) : str3).split(":");
         JsonObject json4 = new JsonObject();
         json4.addProperty("type", strings2[1]);
         json4.addProperty("description", strings2.length > 2 ? strings2[2] : strings2[0]);
         json3.add(strings2[0], json4);
         if (!endsWith) {
            jsonArray2.add(strings2[0]);
         }
      }

      json2.add("properties", json3);
      json2.add("required", jsonArray2);
      json.add("inputSchema", json2);
      jsonArray.add(json);
   }

   public static JsonArray method5499() {
      JsonArray jsonArray = new JsonArray();
      method5498(jsonArray, "get_api_docs", "Полная справка по JS API скриптов Zenith. Начни с неё.");
      method5498(jsonArray, "list_scripts", "Все скрипты: состояние, включён ли, ошибка, модули, файлы.");
      method5498(jsonArray, "read_file", "Прочитать файл скрипта.", "script:string:id скрипта", "path:string:путь внутри папки скрипта, например main.js");
      method5498(
         jsonArray,
         "write_file",
         "Записать файл скрипта. Скрипт сразу перезагружается, в ответе — его состояние и ошибка.",
         "script:string:id скрипта",
         "path:string:путь внутри папки скрипта",
         "content:string:новое содержимое"
      );
      method5498(
         jsonArray,
         "create_script",
         "Создать скрипт-папку из шаблона (" + Server.method229() + ") и включить его.",
         "name:string:имя скрипта",
         "template:string:шаблон?"
      );
      method5498(jsonArray, "delete_file", "Удалить файл скрипта (кроме main.js).", "script:string:id скрипта", "path:string:путь внутри папки скрипта");
      method5498(jsonArray, "reload_script", "Перезагрузить скрипт.", "script:string:id скрипта");
      method5498(jsonArray, "set_script_enabled", "Включить или выключить скрипт.", "script:string:id скрипта", "enabled:boolean:включить?");
      method5498(jsonArray, "check_syntax", "Проверить синтаксис JS без записи.", "code:string:код");
      method5498(jsonArray, "get_logs", "Последние строки консоли скриптов.", "script:string:id скрипта?", "limit:number:сколько строк, до 300?");
      method5498(
         jsonArray, "run_js", "Выполнить JS в живом окружении скрипта (или в общей консоли). Возвращает результат.", "code:string:код", "script:string:id скрипта?"
      );
      method5498(jsonArray, "game_state", "Состояние игры: мир, сервер, позиция и здоровье игрока, FPS, экран.");
      return jsonArray;
   }

   private static Storage host() {
      Storage storage = Zenith.getInstance().getStorage();
      if (storage == null) {
         throw new IllegalStateException("скриптовый движок не запущен");
      } else {
         return storage;
      }
   }

   private static String method5500(JsonObject json, String memberName) {
      if (json.has(memberName) && !json.get(memberName).isJsonNull()) {
         return json.get(memberName).getAsString();
      } else {
         throw new IllegalArgumentException("нет параметра " + memberName);
      }
   }

   private static JsonObject method5501(Storage storage, Script script) {
      JsonObject json = new JsonObject();
      json.addProperty("id", script.getId());
      json.addProperty("name", script.method1403());
      json.addProperty("version", script.getVersion());
      json.addProperty("state", script.getType2().name().toLowerCase());
      json.addProperty("enabled", storage.method1297(script));
      json.addProperty("error", script.getError());
      JsonArray jsonArray = new JsonArray();

      for (Module1 module1 : script.getItems3().values()) {
         JsonObject json2 = new JsonObject();
         json2.addProperty("name", module1.getName());
         json2.addProperty("enabled", module1.isEnabled());
         json2.addProperty("settings", module1.getSettings().size());
         jsonArray.add(json2);
      }

      json.add("modules", jsonArray);
      JsonArray jsonArray2 = new JsonArray();
      Path path = script.getPath() == null ? storage.getPath() : script.getPath();

      for (Path path2 : storage.method1345(script)) {
         jsonArray2.add(path.relativize(path2).toString().replace('\\', '/'));
      }

      json.add("files", jsonArray2);
      return json;
   }

   public static String method5502(String str, JsonObject json) throws Exception {
      Storage storage = host();
      switch (str) {
         case "get_api_docs":
            return Files.readString(storage.getPath().resolve("API.md"), StandardCharsets.UTF_8);
         case "list_scripts":
            JsonArray jsonArray = new JsonArray();

            for (Script script : storage.method1293()) {
               jsonArray.add(method5501(storage, script));
            }

            return gson.toJson(jsonArray);
         case "read_file":
            Script workspace = Server.method230(storage, json);
            Path path2 = storage.method1346(workspace, method5500(json, "path"));
            return storage.method1306(path2);
         case "write_file":
            Script script = Server.method230(storage, json);
            Path file = storage.method1346(script, method5500(json, "path"));
            String lowerCase = file.getFileName().toString().toLowerCase();
            if (!lowerCase.endsWith(".js") && !lowerCase.endsWith(".json") && !lowerCase.endsWith(".md") && !lowerCase.endsWith(".txt")) {
               throw new IllegalArgumentException("можно писать только .js, .json, .md и .txt");
            }

            storage.method1347(script, file, method5500(json, "content"));
            storage.method1299(script);
            return gson.toJson(method5501(storage, script));
         case "create_script":
            Script workspace2 = storage.method1340(method5500(json, "name"), json.has("template") ? json.get("template").getAsString() : "empty");
            storage.method1298(workspace2, true);
            return gson.toJson(method5501(storage, workspace2));
         case "delete_file":
            Script workspace3 = Server.method230(storage, json);
            Path path = storage.method1346(workspace3, method5500(json, "path"));
            if (path.equals(workspace3.getPath2().toAbsolutePath().normalize())) {
               throw new IllegalArgumentException("main.js удалить нельзя");
            }

            Files.deleteIfExists(path);
            storage.method1299(workspace3);
            return "удалён";
         case "reload_script":
            Script workspace4 = Server.method230(storage, json);
            storage.method1299(workspace4);
            return gson.toJson(method5501(storage, workspace4));
         case "set_script_enabled":
            Script workspace5 = Server.method230(storage, json);
            storage.method1298(workspace5, json.has("enabled") && json.get("enabled").getAsBoolean());
            return gson.toJson(method5501(storage, workspace5));
         case "check_syntax":
            try {
               storage.method1292(method5500(json, "code"));
               return "ok";
            } catch (Exception e) {
               return "ошибка: " + Storage.method1325(e);
            }
         case "get_logs":
            Script workspace6 = json.has("script") ? storage.method1294(json.get("script").getAsString()) : null;
            int n = json.has("limit") ? Math.max(1, Math.min(300, json.get("limit").getAsInt())) : 60;
            var data2s = storage.method1349(workspace6);
            StringBuilder builder = new StringBuilder();

            for (int i = Math.max(0, data2s.size() - n); i < data2s.size(); i++) {
               Storage.Data2 data2 = (Storage.Data2)data2s.get(i);
               builder.append('[').append(data2.getText2()).append("] ").append(data2.getText()).append(": ").append(data2.text()).append('\n');
            }

            return builder.isEmpty() ? "пусто" : builder.toString();
         case "run_js":
            Script script2 = json.has("script") ? storage.method1294(json.get("script").getAsString()) : null;

            try {
               return String.valueOf(storage.method1341(script2 != null && script2.isRunning() ? script2 : null, method5500(json, "code")));
            } catch (Throwable e) {
               return "ошибка: " + Storage.method1325(e);
            }
         case "game_state":
            Minecraft minecraft = Minecraft.getInstance();
            JsonObject jsonElement = new JsonObject();
            jsonElement.addProperty("inGame", minecraft.level != null && minecraft.player != null);
            jsonElement.addProperty("server", minecraft.getCurrentServer() == null ? "" : minecraft.getCurrentServer().ip);
            jsonElement.addProperty("fps", minecraft.getFps());
            jsonElement.addProperty("screen", minecraft.gui.screen() == null ? "" : minecraft.gui.screen().getClass().getSimpleName());
            if (minecraft.player != null) {
               jsonElement.addProperty("x", minecraft.player.getX());
               jsonElement.addProperty("y", minecraft.player.getY());
               jsonElement.addProperty("z", minecraft.player.getZ());
               jsonElement.addProperty("health", minecraft.player.getHealth());
               jsonElement.addProperty("dimension", minecraft.level.dimension().identifier().toString());
            }

            return gson.toJson(jsonElement);
         default:
            throw new IllegalArgumentException("неизвестный инструмент: " + str);
      }
   }
}
