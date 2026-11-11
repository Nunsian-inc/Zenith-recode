package org.zenith.utility.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandler;
import java.net.http.HttpResponse.BodySubscriber;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Flow.Subscription;
import org.zenith.utility.config.SiteSession;

public final class SiteHttpClient {
   private static final Duration duration = Duration.ofSeconds(20L);
   private static final HttpClient httpClient = HttpClients.method2670(Duration.ofSeconds(10L));
   private static final int CONST_intValue = 4194304;
   private static volatile String host;

   private SiteHttpClient() {
   }

   public static String method2409() {
      return method2410().get(0);
   }

   public static List<String> method2410() {
      return RegionUtil.method2043(RegionUtil.method2037(), host);
   }

   public static CompletableFuture<JsonObject> method2411(String str, JsonObject json) {
      String str2 = SiteSession.method2289();
      if (str2.isEmpty()) {
         return CompletableFuture.failedFuture(new SiteHttpClient.Entry2("нет сессии сайта: войдите в лаунчере"));
      } else {
         json.addProperty("token", str2);
         byte[] bytes = json.toString().getBytes(StandardCharsets.UTF_8);
         return method2413(str, bytes, method2410(), 0);
      }
   }

   public static CompletableFuture<JsonObject> method2412(String str, JsonObject json) {
      String str2 = SiteSession.method2289();
      if (!str2.isEmpty()) {
         json.addProperty("token", str2);
      }

      return method2413(str, json.toString().getBytes(StandardCharsets.UTF_8), method2410(), 0);
   }

   private static CompletableFuture<JsonObject> method2413(String str, byte[] bytes, List<String> strings, int n) {
      String host2 = (String)strings.get(n);
      boolean flag = n + 1 >= strings.size();

      HttpRequest httpRequest;
      try {
         httpRequest = HttpRequest.newBuilder(URI.create(host2 + str))
            .timeout(duration)
            .header("Content-Type", "application/json; charset=utf-8")
            .POST(BodyPublishers.ofByteArray(bytes))
            .build();
      } catch (RuntimeException e) {
         return flag ? CompletableFuture.failedFuture(new SiteHttpClient.Entry2(method2424(e))) : method2413(str, bytes, strings, n + 1);
      }

      return httpClient.sendAsync(httpRequest, method2423(4194304)).handle((var6x, throwable) -> {
         if (throwable != null) {
            return !flag ? method2413(str, bytes, strings, n + 1) : CompletableFuture.failedFuture(new SiteHttpClient.Entry2(method2424(throwable)));
         } else {
            ServerClock.syncFromHttpResponse((HttpResponse<?>)var6x);
            int var8x = var6x.statusCode();
            JsonObject json = method2415(var6x.body());
            if (method2414(var8x, json)) {
               if (!flag) {
                  return method2413(str, bytes, strings, n + 1);
               }

               if (json == null) {
                  return CompletableFuture.failedFuture(new SiteHttpClient.Entry2("сайт ответил " + var8x, var8x));
               }
            }

            try {
               JsonObject json2 = method2416(var8x, json);
               host = host2;
               return CompletableFuture.completedFuture(json2);
            } catch (RuntimeException e) {
               return CompletableFuture.failedFuture(e);
            }
         }
      }).thenCompose(var0x -> (CompletionStage<JsonObject>)var0x);
   }

   public static boolean method2414(int statusCode, JsonObject json) {
      return json == null || statusCode >= 500 || statusCode == 429;
   }

   public static JsonObject method2415(byte[] body) {
      try {
         JsonElement string = JsonParser.parseString(new String(body, StandardCharsets.UTF_8));
         return string.isJsonObject() ? string.getAsJsonObject() : null;
      } catch (RuntimeException e) {
         return null;
      }
   }

   private static JsonObject method2416(int intValue, JsonObject json) {
      if (intValue == 401 || intValue == 403) {
         SiteSession.method2292();
      }

      int n;
      try {
         n = json.has("status") && !json.get("status").isJsonNull() ? json.get("status").getAsInt() : 0;
      } catch (RuntimeException e) {
         n = 1;
      }

      if (n == 0 && intValue / 100 == 2) {
         return json;
      } else {
         String memberName = n != 0 ? "message" : "detail";
         String string = json.has(memberName) && json.get(memberName).isJsonPrimitive() ? json.get(memberName).getAsString() : (n != 0 ? "отказ сайта" : "сайт ответил " + intValue);
         if (string.equalsIgnoreCase("Unauthorized")) {
            SiteSession.method2292();
         }

         throw new SiteHttpClient.Entry2(string, intValue);
      }
   }

   public static String method2417(JsonObject jsonObject, String memberName, String peerName) {
      JsonElement jsonElement = jsonObject == null ? null : jsonObject.get(memberName);
      return jsonElement != null && jsonElement.isJsonPrimitive() ? jsonElement.getAsString() : peerName;
   }

   public static JsonObject method2418(JsonObject jsonObject, String memberName) {
      JsonElement jsonElement = jsonObject == null ? null : jsonObject.get(memberName);
      return jsonElement != null && jsonElement.isJsonObject() ? jsonElement.getAsJsonObject() : null;
   }

   public static JsonArray method2419(JsonObject jsonObject, String memberName) {
      JsonElement jsonElement = jsonObject == null ? null : jsonObject.get(memberName);
      return jsonElement != null && jsonElement.isJsonArray() ? jsonElement.getAsJsonArray() : null;
   }

   public static boolean method2420(JsonObject jsonObject, String memberName, boolean flag) {
      JsonElement jsonElement = jsonObject == null ? null : jsonObject.get(memberName);
      if (jsonElement != null && jsonElement.isJsonPrimitive()) {
         JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();

         try {
            return jsonPrimitive.isNumber() ? jsonPrimitive.getAsDouble() != 0.0 : jsonPrimitive.getAsBoolean();
         } catch (RuntimeException e) {
            return flag;
         }
      } else {
         return flag;
      }
   }

   public static int method2421(JsonObject jsonObject, String memberName, int n) {
      try {
         return jsonObject != null && jsonObject.has(memberName) && !jsonObject.get(memberName).isJsonNull() ? jsonObject.get(memberName).getAsInt() : n;
      } catch (RuntimeException e) {
         return n;
      }
   }

   public static long method2422(String text) {
      if (text != null && !text.isBlank()) {
         try {
            return Instant.parse(text).toEpochMilli();
         } catch (DateTimeParseException e) {
            try {
               return Instant.parse(text + "Z").toEpochMilli();
            } catch (DateTimeParseException ex) {
               return 0L;
            }
         }
      } else {
         return 0L;
      }
   }

   public static BodyHandler<byte[]> method2423(int n) {
      return responseInfo -> {
         long l = responseInfo.headers().firstValueAsLong("Content-Length").orElse(-1L);
         return new SiteHttpClient.Entry(l > n ? -1 : n);
      };
   }

   public static String method2424(Throwable throwable) {
      Throwable throwable2 = throwable;

      while (throwable2.getCause() != null && throwable2.getCause() != throwable2) {
         throwable2 = throwable2.getCause();
      }

      String message = throwable2.getMessage();
      return message != null && !message.isBlank() ? message : throwable2.getClass().getSimpleName();
   }

   static {
      RegionUtil.method2042(() -> host = null);
   }

   private static final class Entry implements BodySubscriber<byte[]> {
      private final int intValue;
      private final CompletableFuture<byte[]> completableFuture = new CompletableFuture<>();
      private final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      private Subscription subscription;

      public Entry(int intValue) {
         this.intValue = intValue;
      }

      @Override
      public CompletionStage<byte[]> getBody() {
         return this.completableFuture;
      }

      @Override
      public void onSubscribe(Subscription subscription) {
         this.subscription = subscription;
         if (this.intValue < 0) {
            subscription.cancel();
            this.completableFuture.completeExceptionally(new IOException("ответ больше потолка"));
         } else {
            subscription.request(Long.MAX_VALUE);
         }
      }

      public void onNext(List<ByteBuffer> byteBuffers) {
         if (!this.completableFuture.isDone()) {
            for (ByteBuffer byteBuffer : byteBuffers) {
               int remaining = byteBuffer.remaining();
               if ((long)this.byteArrayOutputStream.size() + remaining > this.intValue) {
                  this.subscription.cancel();
                  this.completableFuture.completeExceptionally(new IOException("ответ больше потолка"));
                  return;
               }

               byte[] dst = new byte[remaining];
               byteBuffer.get(dst);
               this.byteArrayOutputStream.write(dst, 0, remaining);
            }
         }
      }

      @Override
      public void onError(Throwable ex) {
         this.completableFuture.completeExceptionally(ex);
      }

      @Override
      public void onComplete() {
         this.completableFuture.complete(this.byteArrayOutputStream.toByteArray());
      }
   }

   public static final class Entry2 extends RuntimeException {
      private final int intValue;

      public Entry2(String error) {
         this(error, 0);
      }

      public Entry2(String string, int intValue) {
         super(string);
         this.intValue = intValue;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public boolean method2426() {
         return this.intValue == 401 || this.intValue == 403 || "Unauthorized".equalsIgnoreCase(this.getMessage());
      }
   }
}
