package org.zenith.utility.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.client.Minecraft;
import org.slf4j.Logger;
import org.zenith.Zenith;
import org.zenith.client.scripting.Script;
import org.zenith.client.scripting.Storage;
import org.zenith.utility.misc.ScriptHeaderParser;

public final class Server {
   public static final List<String> items = List.of(
      "get_api_docs",
      "list_scripts",
      "read_file",
      "write_file",
      "create_script",
      "delete_file",
      "reload_script",
      "set_script_enabled",
      "check_syntax",
      "get_logs",
      "run_js",
      "game_state"
   );
   private static final Logger logger = LogUtils.getLogger();
   private static final Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
   private static final int CONST_intValue = 25610;
   private static final int CONST_intValue2 = 1048576;
   private static final String text = "2025-06-18";
   private static final Server INSTANCE = new Server();
   private final Path path;
   private boolean enabled;
   private int port = 25610;
   private String text2 = "";
   private ServerSocket serverSocket;
   private ExecutorService executorService;
   private Thread thread;
   private final AtomicInteger atomicInteger = new AtomicInteger();

   private Server() {
      this.path = Zenith.file.toPath().resolve("scripts").resolve(".mcp-server.json");
      this.method220();
   }

   public static Server getInstance() {
      return INSTANCE;
   }

   public boolean isRunning() {
      return this.serverSocket != null && !this.serverSocket.isClosed();
   }

   public int port() {
      return this.port;
   }

   public int method215() {
      return this.atomicInteger.get();
   }

   public String url() {
      return "http://127.0.0.1:" + this.port + "/mcp/" + this.text2;
   }

   public String method216() {
      return "claude mcp add --transport http zenith " + this.url();
   }

   public void method217() {
      if (this.enabled) {
         this.start();
      }
   }

   public synchronized void setEnabled(boolean enabled) {
      this.enabled = enabled;
      if (enabled) {
         this.start();
      } else {
         this.stop();
      }

      this.method221();
   }

   public synchronized void method218() {
      this.text2 = method219();
      this.method221();
      this.method222();
   }

   private static String method219() {
      byte[] bytes = new byte[24];
      new SecureRandom().nextBytes(bytes);
      return HexFormat.of().formatHex(bytes);
   }

   private void method220() {
      try {
         if (Files.isRegularFile(this.path)) {
            JsonObject jsonObject = JsonParser.parseString(Files.readString(this.path, StandardCharsets.UTF_8)).getAsJsonObject();
            this.enabled = jsonObject.has("enabled") && jsonObject.get("enabled").getAsBoolean();
            this.port = jsonObject.has("port") ? jsonObject.get("port").getAsInt() : 25610;
            this.text2 = jsonObject.has("token") ? jsonObject.get("token").getAsString() : "";
         }
      } catch (Exception e) {
         logger.warn("MCP: не удалось прочитать настройки", e);
      }

      if (this.text2.length() < 32) {
         this.text2 = method219();
      }
   }

   private void method221() {
      try {
         JsonObject json = new JsonObject();
         json.addProperty("enabled", this.enabled);
         json.addProperty("port", this.port);
         json.addProperty("token", this.text2);
         Files.createDirectories(this.path.getParent());
         Files.writeString(this.path, gson.toJson(json), StandardCharsets.UTF_8);
      } catch (IOException e) {
         logger.warn("MCP: не удалось сохранить настройки", e);
      }
   }

   private synchronized void start() {
      if (!this.isRunning()) {
         InetAddress loopbackAddress = InetAddress.getLoopbackAddress();

         for (int i = 0; i < 10; i++) {
            try {
               this.serverSocket = new ServerSocket(this.port + i, 16, loopbackAddress);
               this.port += i;
               break;
            } catch (IOException e) {
               this.serverSocket = null;
            }
         }

         if (this.serverSocket == null) {
            logger.warn("MCP: нет свободного порта рядом с {}", this.port);
         } else {
            this.executorService = Executors.newFixedThreadPool(2, task -> {
               Thread var1x = new Thread(task, "Zenith-MCP");
               var1x.setDaemon(true);
               return var1x;
            });
            ServerSocket serverSocket = this.serverSocket;
            this.thread = new Thread(() -> {
               while (!serverSocket.isClosed()) {
                  try {
                     Socket var2x = serverSocket.accept();
                     var2x.setSoTimeout(15000);
                     this.executorService.submit(() -> this.method223(var2x));
                  } catch (IOException e) {
                     if (!serverSocket.isClosed()) {
                        logger.warn("MCP: ошибка приёма", e);
                     }
                  }
               }
            }, "Zenith-MCP-accept");
            this.thread.setDaemon(true);
            this.thread.start();
            this.method221();
            this.method222();
            logger.info("MCP: слушаю {}", "http://127.0.0.1:" + this.port + "/mcp/<token>");
         }
      }
   }

   public synchronized void stop() {
      try {
         if (this.serverSocket != null) {
            this.serverSocket.close();
         }
      } catch (IOException e) {
      }

      this.serverSocket = null;
      if (this.executorService != null) {
         this.executorService.shutdownNow();
      }

      this.executorService = null;
   }

   private void method222() {
      Path parent = this.path.getParent();

      try {
         JsonObject json = new JsonObject();
         json.addProperty("type", "http");
         json.addProperty("url", this.url());
         JsonObject json2 = new JsonObject();
         json2.add("zenith", json);
         JsonObject jsonElement = new JsonObject();
         jsonElement.add("mcpServers", json2);
         Files.writeString(parent.resolve(".mcp.json"), gson.toJson(jsonElement), StandardCharsets.UTF_8);
         String csq = "# Zenith scripts — гайд для ИИ-агента\n\nВ этой папке лежат JS-скрипты клиента Zenith. Пока игра запущена и MCP включён\n(меню → Scripts → MCP), клиент отвечает по адресу из `.mcp.json`. Пользуйся инструментами,\nа не правь файлы вслепую:\n\n1. `get_api_docs` — прочитай API: оно своё, не браузерное и не Node.\n2. `list_scripts` — что есть, что включено, что упало.\n3. `read_file` / `write_file` — запись сразу перезагружает скрипт и возвращает ошибку, если она есть.\n4. `check_syntax` — проверить код без записи.\n5. `get_logs` — вывод print/console и ошибки; `run_js` — выполнить код в живом окружении скрипта.\n\nСкрипт — папка `<имя>/main.js`. Язык — ES6 (Rhino): нет class/async/spread.\nJava недоступна вовсе, только API клиента. Лимит — 250 мс на вызов.\nОбязательно прочитай раздел «Частые ошибки» в API: у сущностей методы (`player.x()`), у предметов и блоков — поля\n(`item.block`, `block.air`); `player.place` ставит блок только к опоре; долгие действия — по шагу за тик в `events.update`.\nМодуль из `Module(...)` включается в меню клиента в своей категории, переключатель скрипта только запускает скрипт.\n";
         Files.writeString(parent.resolve("CLAUDE.md"), csq, StandardCharsets.UTF_8);
         Files.writeString(parent.resolve("AGENTS.md"), csq, StandardCharsets.UTF_8);
      } catch (IOException e) {
         logger.warn("MCP: не удалось записать файлы для агентов", e);
      }
   }

   private void method223(Socket socket) {
      try {
         Socket socket2 = socket;

         label166: {
            label167: {
               label168: {
                  label185: {
                     label169: {
                        label170: {
                           try {
                              InputStream inputStream = socket.getInputStream();
                              String str = method224(inputStream);
                              if (str == null) {
                                 break label185;
                              }

                              String[] strings = str.split("\r\n");
                              String[] strings2 = strings[0].split(" ");
                              if (strings2.length < 2) {
                                 method225(socket, 400, null);
                                 break label168;
                              }

                              LinkedHashMap map = new LinkedHashMap();

                              for (int i = 1; i < strings.length; i++) {
                                 int indexOf = strings[i].indexOf(58);
                                 if (indexOf > 0) {
                                    map.put(strings[i].substring(0, indexOf).trim().toLowerCase(Locale.ROOT), strings[i].substring(indexOf + 1).trim());
                                 }
                              }

                              String str2 = ((String)map.getOrDefault("host", ""));
                              if (!map.containsKey("origin")
                                 && !map.containsKey("sec-fetch-site")
                                 && (str2.equals("127.0.0.1:" + this.port) || str2.equals("localhost:" + this.port))) {
                                 if (!MessageDigest.isEqual(("/mcp/" + this.text2).getBytes(StandardCharsets.UTF_8), strings2[1].getBytes(StandardCharsets.UTF_8))) {
                                    method225(socket, 404, null);
                                    break label167;
                                 }

                                 if (!"POST".equals(strings2[0])) {
                                    method225(socket, 405, null);
                                    break label166;
                                 }

                                 int len = Integer.parseInt(((String)map.getOrDefault("content-length", "0")));
                                 if (len > 0 && len <= 1048576) {
                                    byte[] nBytes = inputStream.readNBytes(len);
                                    this.atomicInteger.incrementAndGet();
                                    JsonElement string = JsonParser.parseString(new String(nBytes, StandardCharsets.UTF_8));
                                    if (string.isJsonArray()) {
                                       JsonArray jsonArray = new JsonArray();

                                       for (JsonElement json : string.getAsJsonArray()) {
                                          JsonObject json2 = this.method226(json.getAsJsonObject());
                                          if (json2 != null) {
                                             jsonArray.add(json2);
                                          }
                                       }

                                       method225(socket, jsonArray.isEmpty() ? 202 : 200, jsonArray.isEmpty() ? null : jsonArray);
                                    } else {
                                       JsonObject jsonElement = this.method226(string.getAsJsonObject());
                                       method225(socket, jsonElement == null ? 202 : 200, jsonElement);
                                    }
                                    break label169;
                                 }

                                 method225(socket, 413, null);
                                 break label170;
                              }

                              method225(socket, 403, null);
                           } catch (Throwable e) {
                              if (socket != null) {
                                 try {
                                    socket2.close();
                                 } catch (Throwable ex) {
                                    e.addSuppressed(ex);
                                 }
                              }

                              throw e;
                           }

                           if (socket != null) {
                              socket.close();
                           }

                           return;
                        }

                        if (socket != null) {
                           socket.close();
                        }

                        return;
                     }

                     if (socket != null) {
                        socket.close();
                     }

                     return;
                  }

                  if (socket != null) {
                     socket.close();
                  }

                  return;
               }

               if (socket != null) {
                  socket.close();
               }

               return;
            }

            if (socket != null) {
               socket.close();
            }

            return;
         }

         if (socket != null) {
            socket.close();
         }
      } catch (Exception e) {
         logger.warn("MCP: ошибка запроса", e);
      }
   }

   private static String method224(InputStream inputStream) throws IOException {
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      int n = 0;

      while (byteArrayOutputStream.size() < 16384) {
         int n2 = inputStream.read();
         if (n2 < 0) {
            return null;
         }

         byteArrayOutputStream.write(n2);

         n = switch (n) {
            case 0 -> n2 == 13 ? 1 : 0;
            case 1 -> n2 == 10 ? 2 : 0;
            case 2 -> n2 == 13 ? 3 : 0;
            case 3 -> n2 == 10 ? 4 : 0;
            default -> 0;
         };
         if (n == 4) {
            return byteArrayOutputStream.toString(StandardCharsets.ISO_8859_1).trim();
         }
      }

      return null;
   }

   private static void method225(Socket socket, int n, JsonElement jsonElement) throws IOException {
      byte[] bytes = jsonElement == null ? new byte[0] : gson.toJson(jsonElement).getBytes(StandardCharsets.UTF_8);

      String str = switch (n) {
         case 200 -> "OK";
         case 202 -> "Accepted";
         case 400 -> "Bad Request";
         case 403 -> "Forbidden";
         case 404 -> "Not Found";
         case 405 -> "Method Not Allowed";
         case 413 -> "Payload Too Large";
         default -> "Error";
      };
      String str2 = "HTTP/1.1 " + n + " " + str + "\r\nContent-Type: application/json\r\nContent-Length: " + bytes.length + "\r\nConnection: close\r\n\r\n";
      OutputStream outputStream = socket.getOutputStream();
      outputStream.write(str2.getBytes(StandardCharsets.ISO_8859_1));
      outputStream.write(bytes);
      outputStream.flush();
   }

   private JsonObject method226(JsonObject jsonObject) {
      JsonElement jsonElement = jsonObject.get("id");
      String string = jsonObject.has("method") ? jsonObject.get("method").getAsString() : "";
      if (jsonElement != null && !jsonElement.isJsonNull()) {
         JsonObject json = new JsonObject();
         json.addProperty("jsonrpc", "2.0");
         json.add("id", jsonElement);

         try {
            JsonObject jsonObject2 = jsonObject.has("params") && jsonObject.get("params").isJsonObject() ? jsonObject.getAsJsonObject("params") : new JsonObject();
            switch (string) {
               case "initialize":
                  JsonObject json2 = new JsonObject();
                  json2.addProperty("protocolVersion", jsonObject2.has("protocolVersion") ? jsonObject2.get("protocolVersion").getAsString() : "2025-06-18");
                  JsonObject json3 = new JsonObject();
                  json3.add("tools", new JsonObject());
                  json2.add("capabilities", json3);
                  JsonObject json4 = new JsonObject();
                  json4.addProperty("name", "zenith-scripts");
                  json4.addProperty("version", String.valueOf(3));
                  json2.add("serverInfo", json4);
                  json2.addProperty("instructions", "Сначала get_api_docs, затем list_scripts. write_file сразу перезагружает скрипт и возвращает ошибку.");
                  json.add("result", json2);
                  break;
               case "ping":
                  json.add("result", new JsonObject());
                  break;
               case "tools/list":
                  JsonObject json5 = new JsonObject();
                  json5.add("tools", McpToolSchema.method5499());
                  json.add("result", json5);
                  break;
               case "tools/call":
                  String str = jsonObject2.get("name").getAsString();
                  JsonObject jsonObject3 = jsonObject2.has("arguments") && jsonObject2.get("arguments").isJsonObject() ? jsonObject2.getAsJsonObject("arguments") : new JsonObject();
                  json.add("result", this.method227(str, jsonObject3));
                  break;
               default:
                  JsonObject json6 = new JsonObject();
                  json6.addProperty("code", 32935);
                  json6.addProperty("message", "метод не найден: " + string);
                  json.add("error", json6);
            }
         } catch (Exception e) {
            JsonObject json2 = new JsonObject();
            json2.addProperty("code", 32933);
            json2.addProperty("message", String.valueOf(e.getMessage()));
            json.add("error", json2);
         }

         return json;
      } else {
         return null;
      }
   }

   private JsonObject method227(String str, JsonObject json) {
      JsonObject json2 = new JsonObject();
      JsonArray jsonArray = new JsonArray();
      boolean flag = false;

      String str2;
      try {
         str2 = method228(() -> McpToolSchema.method5502(str, json));
      } catch (Exception e) {
         str2 = String.valueOf(e.getMessage() == null ? e : e.getMessage());
         flag = true;
      }

      JsonObject json3 = new JsonObject();
      json3.addProperty("type", "text");
      json3.addProperty("text", str2);
      jsonArray.add(json3);
      json2.add("content", jsonArray);
      json2.addProperty("isError", flag);
      return json2;
   }

   private static String method228(Server.Handler handler) throws Exception {
      Minecraft minecraft = Minecraft.getInstance();
      CompletableFuture future = new CompletableFuture();
      minecraft.execute(() -> {
         try {
            future.complete(handler.run());
         } catch (Throwable e) {
            future.completeExceptionally(e);
         }
      });

      try {
         return (String)future.get(5L, TimeUnit.SECONDS);
      } catch (ExecutionException e) {
         Throwable cause = e.getCause();
         throw (Exception)(cause instanceof Exception e2 ? e2 : new RuntimeException(cause));
      }
   }

   public static String method229() {
      return String.join(", ", ScriptHeaderParser.items);
   }

   public static Script method230(Storage storage, JsonObject json) {
      String string = json.has("script") ? json.get("script").getAsString() : "";
      Script script = storage.method1294(string);
      if (script == null) {
         throw new IllegalArgumentException("скрипт не найден: " + string);
      } else {
         return script;
      }
   }

   @FunctionalInterface
   private interface Handler {
      String run() throws Exception;
   }
}
