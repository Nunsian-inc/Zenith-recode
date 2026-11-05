package org.zenith.utility.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class RegionUtil {
   private static final Logger logger = LoggerFactory.getLogger("Zenith/NetRegion");
   private static final String text = "net-region.json";
   private static final String text2 = "/net/region";
   private static final String text3 = "zenith.region";
   public static final String text4 = "api";
   public static final String text5 = "site";
   public static final String text6 = "files";
   public static final String text7 = "nexus";
   private static final Map<String, List<String>> items = Map.of(
      "api",
      List.of("https://api.zenithdlc.com", "https://api.zenithdlc.org"),
      "site",
      List.of("https://zenithdlc.com", "https://zenithdlc.org"),
      "files",
      List.of("https://launcher.zenithdlc.com:8443", "https://launcher.zenithdlc.org:8443"),
      "nexus",
      List.of("wss://api.zenithdlc.com/nexus/ws", "wss://api.zenithdlc.org/nexus/ws")
   );
   private static final Duration duration = Duration.ofSeconds(4L);
   private static final Duration duration2 = Duration.ofSeconds(6L);
   private static final long CONST_longValue = 21600L;
   private static final long CONST_longValue2 = 604800L;
   private static final long CONST_longValue3 = 300L;
   private static final long CONST_longValue4 = 600L;
   private static final int CONST_intValue = 8;
   private static final HttpClient httpClient = HttpClients.method2670(duration);
   private static final List<Runnable> items2 = new CopyOnWriteArrayList<>();
   private static volatile Map<String, List<String>> items3 = items;
   private static volatile String text8 = "default";
   private static volatile int version;
   private static volatile Path file;
   private static volatile Map<String, List<String>> items4;
   private static volatile String text9 = "default";
   private static volatile boolean flag;

   private RegionUtil() {
   }

   public static List<String> method2037() {
      return method2041("api");
   }

   public static List<String> method2038() {
      return method2041("site");
   }

   public static List<String> method2039() {
      return method2041("files");
   }

   public static List<String> method2040() {
      return method2041("nexus");
   }

   public static List<String> method2041(String key) {
      var strings = items3.get(key);
      return strings != null && !strings.isEmpty() ? strings : items.getOrDefault(key, List.of());
   }

   public static String getText8() {
      return text8;
   }

   public static int version() {
      return version;
   }

   public static void method2042(Runnable runnable) {
      items2.add(runnable);
   }

   public static List<String> method2043(List<String> strings, String host) {
      if (host != null && strings.contains(host) && !((String)strings.get(0)).equals(host)) {
         List<String> strings2 = new ArrayList<>(strings.size());
         strings2.add(host);

         for (String str : strings) {
            if (!str.equals(host)) {
               strings2.add(str);
            }
         }

         return strings2;
      } else {
         return strings;
      }
   }

   public static boolean method2044(String host) {
      if (host == null) {
         return false;
      } else {
         String lowerCase = host.toLowerCase(Locale.ROOT);
         return lowerCase.equals("zenithdlc.org") || lowerCase.endsWith(".zenithdlc.org") || lowerCase.equals("zenithdlc.com") || lowerCase.endsWith(".zenithdlc.com");
      }
   }

   public static synchronized void method2045(Path path) {
      if (!flag) {
         flag = true;
         String lowerCase = System.getProperty("zenith.region", "").strip().toLowerCase(Locale.ROOT);
         if (!lowerCase.equals("ru") && !lowerCase.equals("world")) {
            file = path.resolve("net-region.json");
            long l = method2046();
            if (l > 0L) {
               method2047(l);
            } else {
               refresh();
            }
         } else {
            method2054(lowerCase.equals("ru") ? method2053() : items, lowerCase, "-Dzenith.region");
         }
      }
   }

   private static long method2046() {
      JsonObject json = CloudJsonIo.method5992(file);
      if (json == null) {
         return 0L;
      } else {
         try {
            var map = method2050(json.getAsJsonObject("hosts"));
            if (map.isEmpty()) {
               return 0L;
            } else {
               long l = method2052(json.has("ttl") ? json.get("ttl").getAsLong() : 21600L);
               long l2 = json.has("fetched_at") ? json.get("fetched_at").getAsLong() : 0L;
               long l3 = (System.currentTimeMillis() - l2) / 1000L;
               String string = json.has("region") ? json.get("region").getAsString() : "default";
               if (l3 >= -60L && l3 < l) {
                  l3 = Math.max(0L, l3);
                  method2054(map, string, "кеш");
                  return l - l3;
               } else {
                  items4 = map;
                  text9 = string;
                  return 0L;
               }
            }
         } catch (RuntimeException e) {
            logger.warn("Регион: кеш {} не разобрался — спросим сайт", file);
            return 0L;
         }
      }
   }

   private static void method2047(long l) {
      CompletableFuture.delayedExecutor(Math.max(1L, l), TimeUnit.SECONDS).execute(RegionUtil::refresh);
   }

   private static void refresh() {
      method2048(method2037(), 0).whenComplete((json, throwable) -> {
         if (json == null) {
            var map = items4;
            if (map != null) {
               items4 = null;
               method2054(map, text9, "протухший кеш");
            }

            logger.info("Регион: сайт не ответил ({}), повтор через {} с", throwable == null ? "нет ответа" : throwable.getMessage(), 600L);
            method2047(600L);
         } else {
            var map = method2050(json.getAsJsonObject("hosts"));

            long l;
            try {
               l = method2052(json.has("ttl") ? json.get("ttl").getAsLong() : 21600L);
            } catch (RuntimeException e) {
               l = 21600L;
            }

            String string = json.has("region") && json.get("region").isJsonPrimitive() ? json.get("region").getAsString() : "default";
            items4 = null;
            method2054(map, string, "сайт");
            method2055(map, string, l);
            method2047(l);
         }
      });
   }

   private static CompletableFuture<JsonObject> method2048(List<String> strings, int n) {
      if (n >= strings.size()) {
         return CompletableFuture.failedFuture(new IllegalStateException("все домены молчат"));
      } else {
         HttpRequest httpRequest;
         try {
            httpRequest = HttpRequest.newBuilder(URI.create((String)strings.get(n) + "/net/region"))
               .timeout(duration2)
               .header("Accept", "application/json")
               .GET()
               .build();
         } catch (RuntimeException e) {
            return method2048(strings, n + 1);
         }

         return httpClient.sendAsync(httpRequest, BodyHandlers.ofByteArray()).handle((var2x, throwable) -> {
            JsonObject var4x = throwable == null ? method2049((HttpResponse<byte[]>)var2x) : null;
            return var4x != null ? CompletableFuture.completedFuture(var4x) : method2048(strings, n + 1);
         }).thenCompose(var0x -> (CompletionStage<JsonObject>)var0x);
      }
   }

   private static JsonObject method2049(HttpResponse<byte[]> httpResponse) {
      if (httpResponse.statusCode() / 100 != 2) {
         return null;
      } else {
         try {
            JsonObject jsonObject = JsonParser.parseString(new String((byte[])httpResponse.body(), StandardCharsets.UTF_8)).getAsJsonObject();
            if (jsonObject.has("status") && jsonObject.get("status").getAsInt() != 0) {
               return null;
            } else {
               return jsonObject.has("hosts") && jsonObject.get("hosts").isJsonObject() ? jsonObject : null;
            }
         } catch (RuntimeException e) {
            return null;
         }
      }
   }

   private static Map<String, List<String>> method2050(JsonObject jsonObject) {
      LinkedHashMap map = new LinkedHashMap();
      if (jsonObject == null) {
         return map;
      } else {
         for (String memberName : items.keySet()) {
            JsonElement jsonElement = jsonObject.get(memberName);
            if (jsonElement != null && jsonElement.isJsonArray()) {
               ArrayList coll = new ArrayList();

               for (JsonElement json : jsonElement.getAsJsonArray()) {
                  String str = json.isJsonPrimitive() ? method2051(memberName, json.getAsString()) : null;
                  if (str != null && !coll.contains(str) && coll.size() < 8) {
                     coll.add(str);
                  }
               }

               if (!coll.isEmpty()) {
                  map.put(memberName, List.copyOf(coll));
               }
            }
         }

         return map;
      }
   }

   public static String method2051(String memberName, String string) {
      if (string == null) {
         return null;
      } else {
         String str = string.strip();

         while (str.endsWith("/")) {
            str = str.substring(0, str.length() - 1);
         }

         URI uri;
         try {
            uri = new URI(str);
         } catch (Exception e) {
            return null;
         }

         String lowerCase = uri.getScheme() == null ? "" : uri.getScheme().toLowerCase(Locale.ROOT);
         boolean flag = memberName.equals("nexus");
         if (!lowerCase.equals(flag ? "wss" : "https")) {
            return null;
         } else if (uri.getRawUserInfo() == null && uri.getRawQuery() == null && uri.getRawFragment() == null) {
            return method2044(uri.getHost()) ? str : null;
         } else {
            return null;
         }
      }
   }

   private static long method2052(long l) {
      return Math.max(300L, Math.min(604800L, l <= 0L ? 21600L : l));
   }

   private static Map<String, List<String>> method2053() {
      LinkedHashMap map = new LinkedHashMap();
      items.forEach((key, strings) -> map.put(key, List.of(strings.get(1), strings.get(0))));
      return map;
   }

   private static synchronized void method2054(Map<String, List<String>> map, String string, String str) {
      LinkedHashMap map2 = new LinkedHashMap<>(items);
      map2.putAll(map);
      var map3 = Map.copyOf(map2);
      boolean flag = !map3.equals(items3);
      items3 = map3;
      text8 = string != null && !string.isBlank() ? string : "default";
      if (flag) {
         version++;
         logger.info("Регион: {} ({}), api первым {}", new Object[]{text8, str, method2041("api").get(0)});

         for (Runnable runnable : items2) {
            try {
               runnable.run();
            } catch (RuntimeException e) {
               logger.warn("Регион: слушатель упал", e);
            }
         }
      }
   }

   private static void method2055(Map<String, List<String>> map, String string, long l) {
      Path path = file;
      if (path != null) {
         JsonObject json = new JsonObject();
         json.addProperty("fetched_at", System.currentTimeMillis());
         json.addProperty("ttl", l);
         json.addProperty("region", string);
         JsonObject json2 = new JsonObject();
         map.forEach((var1x, var2x) -> {
            JsonArray jsonArray = new JsonArray();
            var2x.forEach(jsonArray::add);
            json2.add(var1x, jsonArray);
         });
         json.add("hosts", json2);
         CloudJsonIo.method5993(path, json);
      }
   }
}
