package org.zenith.utility.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse.BodyHandler;
import java.net.http.HttpResponse.BodySubscriber;
import java.net.http.HttpResponse.BodySubscribers;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Flow.Subscription;
import java.util.function.Function;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zenith.utility.network.Cloud;

public final class StoreApi {
   private static final Logger logger = LoggerFactory.getLogger("Zenith/Store");
   private static final Duration duration = Duration.ofSeconds(30L);
   private static final long CONST_longValue = 33554432L;
   private static final Duration duration2 = Duration.ofSeconds(130L);
   public static final int CONST_intValue = 524288;
   public static final int CONST_intValue2 = 32;
   public static final String text = "config";
   public static final String text2 = "script";
   public static final String text3 = "PRIVATE";
   public static final String text4 = "FRIENDS";
   public static final String text5 = "PUBLIC";
   public static final String text6 = "CODE";
   private final Cloud cloud;
   private final HttpClient httpClient = HttpClients.method2670(Duration.ofSeconds(10L));
   private static final int CONST_intValue3 = 1024;
   private static final LinkedHashMap<UUID, StoreApi.Data5> linkedHashMap = new LinkedHashMap<UUID, StoreApi.Data5>(64, 0.75F, false) {
      @Override
      public boolean removeEldestEntry(java.util.Map.Entry<UUID, StoreApi.Data5> eldest) {
         return this.size() > 1024;
      }
   };

   public StoreApi(Cloud cloud) {
      this.cloud = cloud;
   }

   private static void method1958(UUID key, StoreApi.Data5 data5) {
      synchronized (linkedHashMap) {
         linkedHashMap.remove(key);
         linkedHashMap.put(key, data5);
      }
   }

   private static void method1959(UUID key) {
      synchronized (linkedHashMap) {
         linkedHashMap.remove(key);
      }
   }

   public static String reason(Throwable throwable) {
      Throwable throwable2 = throwable;

      while (throwable2.getCause() != null && throwable2.getCause() != throwable2) {
         throwable2 = throwable2.getCause();
      }

      String message = throwable2.getMessage();
      return message != null && !message.isBlank() ? message : throwable2.getClass().getSimpleName();
   }

   public static StoreApi.Data5 method1960(UUID uuid) {
      if (uuid == null) {
         return StoreApi.Data5.INSTANCE;
      } else {
         synchronized (linkedHashMap) {
            return linkedHashMap.getOrDefault(uuid, StoreApi.Data5.INSTANCE);
         }
      }
   }

   public CompletableFuture<StoreApi.Data2> method1961(UUID uuid, int n) {
      return this.method1989("POST", "market/" + uuid + "?price=" + Math.max(0, n), null).thenApply(StoreApi.Data2::method2003);
   }

   public CompletableFuture<StoreApi.Data2> method1962(UUID uuid) {
      return this.method1989("DELETE", "market/" + uuid, null).thenApply(StoreApi.Data2::method2003);
   }

   public CompletableFuture<List<StoreApi.Data4>> method1963() {
      return this.method1989("GET", "roster", null).thenApply(json -> {
         ArrayList arrayList = new ArrayList();
         JsonArray jsonArray = json.getAsJsonArray("online");
         if (jsonArray != null) {
            for (JsonElement jsonElement : jsonArray) {
               method2001(arrayList, jsonElement, StoreApi.Data4::method2005);
            }
         }

         return arrayList;
      });
   }

   public CompletableFuture<List<StoreApi.Data3>> method1964(long peerUid, int n) {
      return this.method1989("GET", "dm/" + peerUid + "?limit=" + n, null).thenApply(json -> {
         ArrayList var1x = new ArrayList();
         JsonArray jsonArray = json.getAsJsonArray("messages");
         if (jsonArray != null) {
            for (JsonElement jsonElement : jsonArray) {
               method2001(var1x, jsonElement, StoreApi.Data3::method2004);
            }
         }

         return var1x;
      });
   }

   public CompletableFuture<List<StoreApi.Data3>> method1965(int n) {
      return this.method1989("GET", "chat?limit=" + n, null).thenApply(json -> {
         ArrayList var1x = new ArrayList();
         JsonArray jsonArray = json.getAsJsonArray("messages");
         if (jsonArray != null) {
            for (JsonElement jsonElement : jsonArray) {
               method2001(var1x, jsonElement, StoreApi.Data3::method2004);
            }
         }

         return var1x;
      });
   }

   public CompletableFuture<StoreApi.Data3> method1966(long openPeerUid, String text) {
      return this.method1989("POST", "dm/" + openPeerUid, text.getBytes(StandardCharsets.UTF_8)).thenApply(StoreApi.Data3::method2004);
   }

   public CompletableFuture<Void> method1967(long deleteMenuPeer, long deleteMenuMessage) {
      return this.method1989("DELETE", "dm/" + deleteMenuPeer + "?id=" + deleteMenuMessage, null).thenApply(json -> null);
   }

   public CompletableFuture<List<StoreApi.Data2>> method1968(String kind) {
      return this.method1989("GET", "list?kind=" + encode(kind), null).thenApply(StoreApi::method1987);
   }

   public CompletableFuture<List<StoreApi.Data2>> method1969(String kind, String str) {
      return this.method1989("GET", "catalog?scope=" + encode(kind) + "&kind=" + encode(str), null).thenApply(StoreApi::method1987);
   }

   public CompletableFuture<byte[]> method1970(UUID uuid) {
      return this.method1990("GET", "content/" + uuid, null);
   }

   public CompletableFuture<byte[]> method1971(UUID uuid) {
      return this.method1990("GET", "preview/" + uuid, null);
   }

   public CompletableFuture<StoreApi.Data2> method1972(String kind, String display, String name, String description, String str, byte[] allBytes, UUID uuid, long longValue) {
      return this.method1973(kind, display, name, description, str, allBytes, uuid, longValue, this.cloud.method7911());
   }

   public CompletableFuture<StoreApi.Data2> method1973(
      String kind, String display, String name, String description, String str, byte[] allBytes, UUID uuid, long longValue, String str2
   ) {
      JsonObject json = new JsonObject();
      json.addProperty("kind", kind);
      json.addProperty("name", display);
      json.addProperty("fileName", name);
      json.addProperty("description", description == null ? "" : description);
      json.addProperty("visibility", str);
      json.addProperty("server", str2 == null ? "" : str2);
      json.addProperty("ownerName", method1995());
      if (uuid != null) {
         json.addProperty("replace", uuid.toString());
         json.addProperty("version", longValue);
      }

      byte[] bytes = json.toString().getBytes(StandardCharsets.UTF_8);
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(bytes.length + allBytes.length + 4);
      byteArrayOutputStream.write(bytes.length >>> 24);
      byteArrayOutputStream.write(bytes.length >>> 16);
      byteArrayOutputStream.write(bytes.length >>> 8);
      byteArrayOutputStream.write(bytes.length);
      byteArrayOutputStream.writeBytes(bytes);
      byteArrayOutputStream.writeBytes(allBytes);
      return this.method1989("POST", "publish", byteArrayOutputStream.toByteArray()).thenApply(jsonObject -> StoreApi.Data2.method2003(jsonObject));
   }

   public CompletableFuture<StoreApi.Data2> method1974(UUID uuid, String anotherString, String str, String str2) {
      JsonObject json = new JsonObject();
      if (anotherString != null) {
         json.addProperty("name", anotherString);
      }

      if (str != null) {
         json.addProperty("description", str);
      }

      if (str2 != null) {
         json.addProperty("visibility", str2);
      }

      return this.method1989("POST", "edit/" + uuid, json.toString().getBytes(StandardCharsets.UTF_8)).thenApply(StoreApi.Data2::method2003);
   }

   public CompletableFuture<Void> method1975(UUID uuid) {
      return this.method1989("DELETE", "entry/" + uuid, null).thenApply(json -> null);
   }

   public CompletableFuture<StoreApi.Data2> method1976(UUID uuid, byte[] bytes) {
      return this.method1989("POST", "preview/" + uuid, bytes).thenApply(StoreApi.Data2::method2003);
   }

   public CompletableFuture<StoreApi.Data2> method1977(UUID uuid) {
      return this.method1989("DELETE", "preview/" + uuid, null).thenApply(StoreApi.Data2::method2003);
   }

   public CompletableFuture<int[]> method1978(UUID uuid) {
      return this.method1989("POST", "like/" + uuid, null).thenApply(jsonObject -> new int[]{method1999(jsonObject, "likes", 0), method2000(jsonObject, "liked") ? 1 : 0});
   }

   public CompletableFuture<List<StoreApi.Data>> method1979(UUID uuid, int n) {
      return this.method1989("POST", "codes/" + uuid + "?count=" + n, null).thenApply(StoreApi::method1988);
   }

   public CompletableFuture<List<StoreApi.Data>> method1980(UUID uuid) {
      return this.method1989("GET", "codes/" + uuid, null).thenApply(StoreApi::method1988);
   }

   public CompletableFuture<Void> method1981(UUID uuid, UUID uuid2) {
      return this.method1989("DELETE", "codes/" + uuid + "/" + uuid2, null).thenApply(json -> null);
   }

   public CompletableFuture<Map<String, byte[]>> method1982(List<String> strings) {
      JsonObject json = new JsonObject();
      JsonArray jsonArray = new JsonArray();
      strings.forEach(jsonArray::add);
      json.add("models", jsonArray);
      return this.method1989("POST", "wardrobe-keys", json.toString().getBytes(StandardCharsets.UTF_8)).thenApply(json2 -> {
         HashMap var1x = new HashMap();
         JsonObject var2x = json2.getAsJsonObject("keys");
         if (var2x != null) {
            for (Map.Entry entry : var2x.entrySet()) {
               try {
                  var1x.put((String)entry.getKey(), Base64.getDecoder().decode(((JsonElement)entry.getValue()).getAsString()));
               } catch (RuntimeException e) {
                  logger.debug("Склад: ключ гардероба {} пропущен: {}", entry.getKey(), e.toString());
               }
            }
         }

         return var1x;
      });
   }

   public String method1983() {
      String str = this.method1993();
      return str.substring(0, str.length() - "/store/".length());
   }

   public CompletableFuture<String> method1984(byte[] src, int expectedLength, int n) {
      if (src != null && src.length != 0) {
         if (src.length > 524288) {
            return CompletableFuture.failedFuture(new StoreApi.Entry(400, "картинка капчи больше 512 КБ"));
         } else {
            JsonObject json = new JsonObject();
            json.addProperty("image", Base64.getEncoder().encodeToString(src));
            json.addProperty("min_length", method1985(expectedLength));
            json.addProperty("max_length", method1985(n));
            return this.method1991("POST", "captcha", json.toString().getBytes(StandardCharsets.UTF_8), duration2).thenApply(bytes -> {
               JsonObject var1x = JsonParser.parseString(new String(bytes, StandardCharsets.UTF_8)).getAsJsonObject();
               return method1997(var1x, "answer", "");
            });
         }
      } else {
         return CompletableFuture.failedFuture(new StoreApi.Entry(400, "картинка капчи пуста"));
      }
   }

   private static int method1985(int expectedLength) {
      return Math.max(0, Math.min(32, expectedLength));
   }

   public CompletableFuture<StoreApi.Data2> method1986(String kind) {
      return this.method1989("POST", "redeem?code=" + encode(kind), null).thenApply(StoreApi.Data2::method2003);
   }

   private static List<StoreApi.Data2> method1987(JsonObject json) {
      ArrayList<StoreApi.Data2> data2s = new ArrayList<>();
      JsonArray jsonArray = json.getAsJsonArray("entries");
      if (jsonArray != null) {
         for (JsonElement jsonElement : jsonArray) {
            method2001(data2s, jsonElement, StoreApi.Data2::method2003);
         }
      }

      return data2s;
   }

   private static List<StoreApi.Data> method1988(JsonObject json) {
      ArrayList<StoreApi.Data> datas = new ArrayList<>();
      JsonArray jsonArray = json.getAsJsonArray("codes");
      if (jsonArray != null) {
         for (JsonElement jsonElement : jsonArray) {
            method2001(datas, jsonElement, StoreApi.Data::method2002);
         }
      }

      return datas;
   }

   private CompletableFuture<JsonObject> method1989(String str, String str2, byte[] bytes) {
      return this.method1990(str, str2, bytes).thenApply(bytes2 -> JsonParser.parseString(new String(bytes2, StandardCharsets.UTF_8)).getAsJsonObject());
   }

   private CompletableFuture<byte[]> method1990(String str, String str2, byte[] bytes) {
      return this.method1991(str, str2, bytes, duration);
   }

   private CompletableFuture<byte[]> method1991(String str, String str2, byte[] bytes, Duration duration2) {
      String str3;
      try {
         str3 = this.method1993();
      } catch (RuntimeException e) {
         return CompletableFuture.failedFuture(e);
      }

      String str4 = str3 + str2;
      String str5 = "/store/" + str2;
      byte[] input = bytes == null ? new byte[0] : bytes;
      long l = Util18.method4489();
      long accountUid2 = accountUid();
      if (accountUid2 <= 0L) {
         return CompletableFuture.failedFuture(new StoreApi.Entry(401, "аккаунт ещё не готов"));
      } else {
         HttpRequest httpRequest;
         String str6;
         try {
            Builder header = HttpRequest.newBuilder(URI.create(str4))
               .timeout(duration2)
               .header("X-Nexus-Uid", Long.toString(accountUid2))
               .header("X-Nexus-Time", Long.toString(l))
               .header("X-Nexus-Sign", this.method1996(accountUid2, l, str, str5, input));
            str6 = CloudTicket.method1373();
            if (str6 != null) {
               header.header("Authorization", "Nexus " + str6);
            }

            if (bytes == null) {
               header.method(str, BodyPublishers.noBody());
            } else {
               header.header("Content-Type", "application/octet-stream").method(str, BodyPublishers.ofByteArray(bytes));
            }

            httpRequest = header.build();
         } catch (RuntimeException e) {
            return CompletableFuture.failedFuture(e);
         }

         BodyHandler<byte[]> bodyHandler = responseInfo -> new StoreApi.Entry2(33554432L);
         return this.httpClient.sendAsync(httpRequest, bodyHandler).thenApply(var1x -> {
            Util18.method4491((HttpResponse<?>)var1x);
            if (var1x.statusCode() / 100 == 2) {
               return (byte[])var1x.body();
            } else {
               if (str6 != null && (var1x.statusCode() == 401 || var1x.statusCode() == 403)) {
                  CloudTicket.invalidate();
               }

               throw new StoreApi.Entry(var1x.statusCode(), method1992((byte[])var1x.body(), var1x.statusCode()));
            }
         });
      }
   }

   private static String method1992(byte[] body, int statusCode) {
      try {
         JsonObject jsonObject = JsonParser.parseString(new String(body, StandardCharsets.UTF_8)).getAsJsonObject();
         if (jsonObject.has("error")) {
            return jsonObject.get("error").getAsString();
         }
      } catch (RuntimeException e) {
      }

      return "узел ответил " + statusCode;
   }

   private String method1993() {
      String str = method1994(this.cloud.method7870());
      if (str.isEmpty()) {
         throw new StoreApi.Entry(503, "адрес облака не задан");
      } else {
         return str + "/store/";
      }
   }

   public static String method1994(String str) {
      if (str != null && !str.isBlank()) {
         String str2 = str.strip();
         String str3 = str2.startsWith("wss://") ? "https://" + str2.substring(6) : (str2.startsWith("ws://") ? "http://" + str2.substring(5) : str2);
         int lastIndexOf = str3.lastIndexOf("/ws");
         String str4 = lastIndexOf < 0 ? str3 : str3.substring(0, lastIndexOf);

         while (str4.endsWith("/")) {
            str4 = str4.substring(0, str4.length() - 1);
         }

         return str4;
      } else {
         return "";
      }
   }

   private static long accountUid() {
      return Cloud.accountUid();
   }

   private static String method1995() {
      return Cloud.accountName();
   }

   private String method1996(long l, long l2, String str, String str2, byte[] input) {
      try {
         Mac mac = Mac.getInstance("HmacSHA256");
         mac.init(new SecretKeySpec(Cloud.getText2().getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
         mac.update(("nexus-cloud-api-v1\n" + l + "\n" + l2 + "\n" + str + "\n" + str2 + "\n").getBytes(StandardCharsets.UTF_8));
         mac.update(MessageDigest.getInstance("SHA-256").digest(input));
         return Base64.getUrlEncoder().withoutPadding().encodeToString(mac.doFinal());
      } catch (Exception e) {
         throw new IllegalStateException("HmacSHA256 недоступен", e);
      }
   }

   private static String method1997(JsonObject jsonObject, String memberName, String str) {
      try {
         return jsonObject.has(memberName) && !jsonObject.get(memberName).isJsonNull() ? jsonObject.get(memberName).getAsString() : str;
      } catch (RuntimeException e) {
         return str;
      }
   }

   private static long method1998(JsonObject jsonObject, String memberName, long l) {
      try {
         return jsonObject.has(memberName) && !jsonObject.get(memberName).isJsonNull() ? jsonObject.get(memberName).getAsLong() : l;
      } catch (RuntimeException e) {
         return l;
      }
   }

   private static int method1999(JsonObject jsonObject, String memberName, int n) {
      try {
         return jsonObject.has(memberName) && !jsonObject.get(memberName).isJsonNull() ? jsonObject.get(memberName).getAsInt() : n;
      } catch (RuntimeException e) {
         return n;
      }
   }

   private static boolean method2000(JsonObject jsonObject, String memberName) {
      try {
         return jsonObject.has(memberName) && !jsonObject.get(memberName).isJsonNull() && jsonObject.get(memberName).getAsBoolean();
      } catch (RuntimeException e) {
         return false;
      }
   }

   private static <T> void method2001(List<T> list, JsonElement jsonElement, Function<JsonObject, T> function) {
      try {
         if (jsonElement != null && jsonElement.isJsonObject()) {
            list.add(function.apply(jsonElement.getAsJsonObject()));
         }
      } catch (RuntimeException e) {
         logger.debug("Склад: запись пропущена: {}", e.toString());
      }
   }

   private static String encode(String kind) {
      return URLEncoder.encode(kind == null ? "" : kind, StandardCharsets.UTF_8);
   }

   public static final class Data {
      private final UUID uUID;
      private final String text;
      private final String text2;
      private final long longValue;
      private final Long longValue2;

      public UUID uUID() {
         return this.uUID;
      }

      public String text() {
         return this.text;
      }

      public String text2() {
         return this.text2;
      }

      public long longValue() {
         return this.longValue;
      }

      public Long longValue2() {
         return this.longValue2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.uUID, other.uUID) && java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.longValue2, other.longValue2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(uUID, text, text2, longValue, longValue2);
      }

      @Override
      public String toString() {
         return "Data[" + "uUID=" + this.uUID + ", " + "text=" + this.text + ", " + "text2=" + this.text2 + ", " + "longValue=" + this.longValue + ", " + "longValue2=" + this.longValue2 + "]";
      }

      public Data(UUID uUID, String text, String str, long longValue, Long l) {
         this.uUID = uUID;
         this.text = text;
         this.text2 = str;
         this.longValue = longValue;
         this.longValue2 = l;
      }

      public static StoreApi.Data method2002(JsonObject jsonObject) {
         long l = StoreApi.method1998(jsonObject, "redeemedAt", -1L);
         return new StoreApi.Data(
            UUID.fromString(jsonObject.get("id").getAsString()),
            StoreApi.method1997(jsonObject, "code", ""),
            StoreApi.method1997(jsonObject, "status", "ACTIVE"),
            StoreApi.method1998(jsonObject, "createdAt", 0L),
            l < 0L ? null : l
         );
      }

      public UUID getUUID() {
         return this.uUID;
      }

      public String getText() {
         return this.text;
      }

      public String getText2() {
         return this.text2;
      }

      public long getLongValue() {
         return this.longValue;
      }

      public Long getLongValue2() {
         return this.longValue2;
      }
   }

   public static final class Data2 {
      private final UUID uUID;
      private final String text;
      private final String text2;
      private final String text3;
      private final String text4;
      private final String text5;
      private final long longValue;
      private final String text6;
      private final String text7;
      private final long longValue2;
      private final long longValue3;
      private final long longValue4;
      private final long longValue5;
      private final String text8;
      private final boolean flag;
      private final boolean flag2;
      private final int intValue;
      private final boolean flag3;
      private final int intValue2;

      public UUID uUID() {
         return this.uUID;
      }

      public String text() {
         return this.text;
      }

      public String text2() {
         return this.text2;
      }

      public String text3() {
         return this.text3;
      }

      public String text4() {
         return this.text4;
      }

      public String text5() {
         return this.text5;
      }

      public long longValue() {
         return this.longValue;
      }

      public String text6() {
         return this.text6;
      }

      public String text7() {
         return this.text7;
      }

      public long longValue2() {
         return this.longValue2;
      }

      public long longValue3() {
         return this.longValue3;
      }

      public long longValue4() {
         return this.longValue4;
      }

      public long longValue5() {
         return this.longValue5;
      }

      public String text8() {
         return this.text8;
      }

      public boolean flag() {
         return this.flag;
      }

      public boolean flag2() {
         return this.flag2;
      }

      public int intValue() {
         return this.intValue;
      }

      public boolean flag3() {
         return this.flag3;
      }

      public int intValue2() {
         return this.intValue2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data2 other)) return false;
         return java.util.Objects.equals(this.uUID, other.uUID) && java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.text3, other.text3) && java.util.Objects.equals(this.text4, other.text4) && java.util.Objects.equals(this.text5, other.text5) && java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.text6, other.text6) && java.util.Objects.equals(this.text7, other.text7) && java.util.Objects.equals(this.longValue2, other.longValue2) && java.util.Objects.equals(this.longValue3, other.longValue3) && java.util.Objects.equals(this.longValue4, other.longValue4) && java.util.Objects.equals(this.longValue5, other.longValue5) && java.util.Objects.equals(this.text8, other.text8) && java.util.Objects.equals(this.flag, other.flag) && java.util.Objects.equals(this.flag2, other.flag2) && java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.flag3, other.flag3) && java.util.Objects.equals(this.intValue2, other.intValue2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(uUID, text, text2, text3, text4, text5, longValue, text6, text7, longValue2, longValue3, longValue4, longValue5, text8, flag, flag2, intValue, flag3, intValue2);
      }

      @Override
      public String toString() {
         return "Data2[" + "uUID=" + this.uUID + ", " + "text=" + this.text + ", " + "text2=" + this.text2 + ", " + "text3=" + this.text3 + ", " + "text4=" + this.text4 + ", " + "text5=" + this.text5 + ", " + "longValue=" + this.longValue + ", " + "text6=" + this.text6 + ", " + "text7=" + this.text7 + ", " + "longValue2=" + this.longValue2 + ", " + "longValue3=" + this.longValue3 + ", " + "longValue4=" + this.longValue4 + ", " + "longValue5=" + this.longValue5 + ", " + "text8=" + this.text8 + ", " + "flag=" + this.flag + ", " + "flag2=" + this.flag2 + ", " + "intValue=" + this.intValue + ", " + "flag3=" + this.flag3 + ", " + "intValue2=" + this.intValue2 + "]";
      }

      public Data2(
         UUID uUID,
         String text,
         String name,
         String fileName,
         String description,
         String server,
         long longValue,
         String str,
         String str2,
         long l,
         long l2,
         long l3,
         long l4,
         String str3,
         boolean flag,
         boolean hasPreview,
         int intValue,
         boolean flag2,
         int n
      ) {
         this.uUID = uUID;
         this.text = text;
         this.text2 = name;
         this.text3 = fileName;
         this.text4 = description;
         this.text5 = server;
         this.longValue = longValue;
         this.text6 = str;
         this.text7 = str2;
         this.longValue2 = l;
         this.longValue3 = l2;
         this.longValue4 = l3;
         this.longValue5 = l4;
         this.text8 = str3;
         this.flag = flag;
         this.flag2 = hasPreview;
         this.intValue = intValue;
         this.flag3 = flag2;
         this.intValue2 = n;
      }

      public static StoreApi.Data2 method2003(JsonObject jsonObject) {
         UUID key = UUID.fromString(jsonObject.get("id").getAsString());
         if (jsonObject.has("market") && jsonObject.get("market").isJsonObject()) {
            JsonObject json = jsonObject.getAsJsonObject("market");
            StoreApi.method1958(
               key,
               new StoreApi.Data5(
                  StoreApi.method1997(json, "status", ""),
                  StoreApi.method1999(json, "price", 0),
                  StoreApi.method2000(json, "sealed"),
                  StoreApi.method1997(json, "reason", "")
               )
            );
         } else {
            StoreApi.method1959(key);
         }

         return new StoreApi.Data2(
            key,
            StoreApi.method1997(jsonObject, "kind", "config"),
            StoreApi.method1997(jsonObject, "name", ""),
            StoreApi.method1997(jsonObject, "fileName", ""),
            StoreApi.method1997(jsonObject, "description", ""),
            StoreApi.method1997(jsonObject, "server", ""),
            StoreApi.method1998(jsonObject, "sizeBytes", 0L),
            StoreApi.method1997(jsonObject, "sha256", ""),
            StoreApi.method1997(jsonObject, "visibility", "PRIVATE"),
            StoreApi.method1998(jsonObject, "version", 1L),
            StoreApi.method1998(jsonObject, "createdAt", 0L),
            StoreApi.method1998(jsonObject, "updatedAt", 0L),
            StoreApi.method1998(jsonObject, "ownerUid", 0L),
            StoreApi.method1997(jsonObject, "ownerName", ""),
            StoreApi.method2000(jsonObject, "mine"),
            StoreApi.method2000(jsonObject, "hasPreview"),
            StoreApi.method1999(jsonObject, "likes", 0),
            StoreApi.method2000(jsonObject, "liked"),
            StoreApi.method1999(jsonObject, "codes", 0)
         );
      }

      public UUID getUUID() {
         return this.uUID;
      }

      public String kind() {
         return this.text;
      }

      public String name() {
         return this.text2;
      }

      public String fileName() {
         return this.text3;
      }

      public String description() {
         return this.text4;
      }

      public String server() {
         return this.text5;
      }

      public long getLongValue() {
         return this.longValue;
      }

      public String getText6() {
         return this.text6;
      }

      public String getText7() {
         return this.text7;
      }

      public long getLongValue2() {
         return this.longValue2;
      }

      public long getLongValue3() {
         return this.longValue3;
      }

      public long getLongValue4() {
         return this.longValue4;
      }

      public long getLongValue5() {
         return this.longValue5;
      }

      public String getText8() {
         return this.text8;
      }

      public boolean mine() {
         return this.flag;
      }

      public boolean hasPreview() {
         return this.flag2;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public boolean isFlag3() {
         return this.flag3;
      }

      public int getIntValue2() {
         return this.intValue2;
      }
   }

   public static final class Data3 {
      private final long longValue;
      private final long longValue2;
      private final String text;
      private final long longValue3;
      private final String text2;

      public long longValue() {
         return this.longValue;
      }

      public long longValue2() {
         return this.longValue2;
      }

      public long longValue3() {
         return this.longValue3;
      }

      public String text2() {
         return this.text2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data3 other)) return false;
         return java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.longValue2, other.longValue2) && java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.longValue3, other.longValue3) && java.util.Objects.equals(this.text2, other.text2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(longValue, longValue2, text, longValue3, text2);
      }

      @Override
      public String toString() {
         return "Data3[" + "longValue=" + this.longValue + ", " + "longValue2=" + this.longValue2 + ", " + "text=" + this.text + ", " + "longValue3=" + this.longValue3 + ", " + "text2=" + this.text2 + "]";
      }

      public Data3(long longValue, long l, String text, long l2, String str) {
         this.longValue = longValue;
         this.longValue2 = l;
         this.text = text;
         this.longValue3 = l2;
         this.text2 = str;
      }

      public static StoreApi.Data3 method2004(JsonObject jsonObject) {
         return new StoreApi.Data3(
            StoreApi.method1998(jsonObject, "id", 0L),
            StoreApi.method1998(jsonObject, "from", 0L),
            StoreApi.method1997(jsonObject, "fromName", ""),
            StoreApi.method1998(jsonObject, "time", 0L),
            StoreApi.method1997(jsonObject, "text", "")
         );
      }

      public long getLongValue() {
         return this.longValue;
      }

      public long getLongValue2() {
         return this.longValue2;
      }

      public String getText() {
         return this.text;
      }

      public long time() {
         return this.longValue3;
      }

      public String text() {
         return this.text2;
      }
   }

   public static final class Data4 {
      private final long longValue;
      private final String text;
      private final String text2;

      public long longValue() {
         return this.longValue;
      }

      public String text() {
         return this.text;
      }

      public String text2() {
         return this.text2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data4 other)) return false;
         return java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(longValue, text, text2);
      }

      @Override
      public String toString() {
         return "Data4[" + "longValue=" + this.longValue + ", " + "text=" + this.text + ", " + "text2=" + this.text2 + "]";
      }

      public Data4(long longValue, String text, String str) {
         this.longValue = longValue;
         this.text = text;
         this.text2 = str;
      }

      public static StoreApi.Data4 method2005(JsonObject jsonObject) {
         return new StoreApi.Data4(StoreApi.method1998(jsonObject, "uid", 0L), StoreApi.method1997(jsonObject, "username", ""), StoreApi.method1997(jsonObject, "mcName", ""));
      }

      public long uid() {
         return this.longValue;
      }

      public String username() {
         return this.text;
      }

      public String getText2() {
         return this.text2;
      }
   }

   public static final class Data5 {
      private final String text;
      private final int intValue;
      private final boolean flag;
      private final String text2;

      public String text() {
         return this.text;
      }

      public int intValue() {
         return this.intValue;
      }

      public boolean flag() {
         return this.flag;
      }

      public String text2() {
         return this.text2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data5 other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.flag, other.flag) && java.util.Objects.equals(this.text2, other.text2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, intValue, flag, text2);
      }

      @Override
      public String toString() {
         return "Data5[" + "text=" + this.text + ", " + "intValue=" + this.intValue + ", " + "flag=" + this.flag + ", " + "text2=" + this.text2 + "]";
      }
      public static final StoreApi.Data5 INSTANCE = new StoreApi.Data5("", 0, false, "");

      public Data5(String text, int intValue, boolean flag, String str) {
         this.text = text;
         this.intValue = intValue;
         this.flag = flag;
         this.text2 = str;
      }

      public boolean pending() {
         return "PENDING".equals(this.text);
      }

      public boolean method2007() {
         return "APPROVED".equals(this.text);
      }

      public boolean method2008() {
         return "REJECTED".equals(this.text);
      }

      public boolean method2009() {
         return this.intValue > 0;
      }

      public String getText() {
         return this.text;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public boolean isFlag() {
         return this.flag;
      }

      public String getText2() {
         return this.text2;
      }
   }

   public static final class Entry extends RuntimeException {
      private final int intValue;

      public Entry(int intValue, String message) {
         super(message);
         this.intValue = intValue;
      }

      public int getIntValue() {
         return this.intValue;
      }
   }

   private static final class Entry2 implements BodySubscriber<byte[]> {
      private final BodySubscriber<byte[]> bodySubscriber = BodySubscribers.ofByteArray();
      private final long longValue;
      private Subscription subscription;
      private long longValue2;
      private boolean flag;

      public Entry2(long longValue) {
         this.longValue = longValue;
      }

      @Override
      public CompletionStage<byte[]> getBody() {
         return this.bodySubscriber.getBody();
      }

      @Override
      public void onSubscribe(Subscription subscription) {
         this.subscription = subscription;
         this.bodySubscriber.onSubscribe(subscription);
      }

      public void onNext(List<ByteBuffer> byteBuffers) {
         if (!this.flag) {
            for (ByteBuffer byteBuffer : byteBuffers) {
               this.longValue2 = this.longValue2 + byteBuffer.remaining();
            }

            if (this.longValue2 > this.longValue) {
               this.flag = true;
               this.subscription.cancel();
               this.bodySubscriber.onError(new StoreApi.Entry(413, "ответ узла больше " + (this.longValue >> 20) + " МБ"));
            } else {
               this.bodySubscriber.onNext(byteBuffers);
            }
         }
      }

      @Override
      public void onError(Throwable throwable) {
         if (!this.flag) {
            this.bodySubscriber.onError(throwable);
         }
      }

      @Override
      public void onComplete() {
         if (!this.flag) {
            this.bodySubscriber.onComplete();
         }
      }
   }
}
