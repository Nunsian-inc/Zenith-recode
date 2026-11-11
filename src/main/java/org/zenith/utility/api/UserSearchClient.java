package org.zenith.utility.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.zenith.utility.config.SiteSession;
import org.zenith.utility.network.Cloud;

public final class UserSearchClient {
   private static final String text = "/user/search";
   private static final Duration duration = Duration.ofSeconds(8L);
   private static final int CONST_intValue = 2;
   private static final int CONST_intValue2 = 64;
   private static final HttpClient httpClient = HttpClients.method2670(Duration.ofSeconds(5L));
   private static final Map<String, List<UserSearchClient.Data>> items = new LinkedHashMap<>();
   private static volatile String host;

   private UserSearchClient() {
   }

   public static synchronized List<UserSearchClient.Data> method4758(String typedName) {
      return items.get(key(typedName));
   }

   public static boolean method4759(String typedName) {
      return typedName == null || typedName.strip().length() < 2;
   }

   public static CompletableFuture<List<UserSearchClient.Data>> method4760(String searchQuery, int n) {
      String typedName = searchQuery == null ? "" : searchQuery.strip();
      if (typedName.length() < 2) {
         return CompletableFuture.completedFuture(List.of());
      } else {
         var datas = method4758(typedName);
         if (datas != null) {
            return CompletableFuture.completedFuture(datas);
         } else {
            JsonObject json = new JsonObject();
            json.addProperty("query", typedName);
            json.addProperty("limit", Math.max(1, Math.min(25, n)));
            json.addProperty("token", SiteSession.method2289());
            byte[] bytes = json.toString().getBytes(StandardCharsets.UTF_8);
            return method4763(bytes, RegionUtil.method2043(RegionUtil.method2037(), host), 0).thenApply(var1x -> {
               var var2x = method4765(var1x);
               method4761(typedName, var2x);
               return var2x;
            });
         }
      }
   }

   private static synchronized void method4761(String typedName, List<UserSearchClient.Data> datas) {
      if (items.size() >= 64) {
         items.clear();
      }

      items.put(key(typedName), datas);
   }

   public static synchronized void method4762() {
      items.clear();
   }

   private static String key(String typedName) {
      return typedName == null ? "" : typedName.strip().toLowerCase(Locale.ROOT);
   }

   private static CompletableFuture<JsonObject> method4763(byte[] bytes, List<String> strings, int n) {
      String str = (String)strings.get(n);
      boolean flag = n + 1 >= strings.size();
      long accountUid = Cloud.accountUid();
      if (accountUid <= 0L) {
         return CompletableFuture.failedFuture(new IllegalStateException("аккаунт ещё не готов"));
      } else {
         long l = ServerClock.currentTimeMillis();

         HttpRequest httpRequest;
         String str2;
         try {
            Builder header = HttpRequest.newBuilder(URI.create(str + "/user/search"))
               .timeout(duration)
               .header("Content-Type", "application/json; charset=utf-8")
               .header("X-Nexus-Uid", Long.toString(accountUid))
               .header("X-Nexus-Time", Long.toString(l))
               .header("X-Nexus-Sign", method4766(accountUid, l, bytes));
            str2 = CloudTicket.method1373();
            if (str2 != null) {
               header.header("Authorization", "Nexus " + str2);
            }

            httpRequest = header.POST(BodyPublishers.ofByteArray(bytes)).build();
         } catch (RuntimeException e) {
            return CompletableFuture.failedFuture(e);
         }

         return httpClient.sendAsync(httpRequest, BodyHandlers.ofByteArray()).handle((httpResponse, var7x) -> {
            if (var7x != null) {
               return !flag ? method4763(bytes, strings, n + 1) : CompletableFuture.failedFuture(var7x);
            } else {
               ServerClock.syncFromHttpResponse((HttpResponse<?>)httpResponse);
               int statusCode = httpResponse.statusCode();
               JsonObject var9x = SiteHttpClient.method2415(httpResponse.body());
               if (SiteHttpClient.method2414(statusCode, var9x)) {
                  if (!flag) {
                     return method4763(bytes, strings, n + 1);
                  }

                  if (var9x == null) {
                     return CompletableFuture.failedFuture(new IllegalStateException("сайт ответил " + statusCode));
                  }
               }

               if (str2 != null && (statusCode == 401 || statusCode == 403)) {
                  CloudTicket.invalidate();
               }

               try {
                  JsonObject var10x = method4764(var9x);
                  if (statusCode / 100 != 2) {
                     return CompletableFuture.failedFuture(new IllegalStateException("сайт ответил " + statusCode));
                  } else {
                     host = str;
                     return CompletableFuture.completedFuture(var10x);
                  }
               } catch (RuntimeException var11x) {
                  return CompletableFuture.failedFuture(var11x);
               }
            }
         }).thenCompose(var0x -> (CompletionStage<JsonObject>)var0x);
      }
   }

   private static JsonObject method4764(JsonObject json) {
      int n;
      try {
         n = json.has("status") && !json.get("status").isJsonNull() ? json.get("status").getAsInt() : 0;
      } catch (RuntimeException e) {
         n = 1;
      }

      if (n == 0) {
         return json;
      } else {
         throw new IllegalStateException(json.has("message") && json.get("message").isJsonPrimitive() ? json.get("message").getAsString() : "отказ сайта");
      }
   }

   private static List<UserSearchClient.Data> method4765(JsonObject json) {
      ArrayList coll = new ArrayList();
      JsonArray jsonArray = json.getAsJsonArray("items");
      if (jsonArray == null) {
         return List.of();
      } else {
         for (JsonElement jsonElement : jsonArray) {
            try {
               JsonObject jsonObject = jsonElement.getAsJsonObject();
               String string = jsonObject.has("name") && !jsonObject.get("name").isJsonNull() ? jsonObject.get("name").getAsString() : "";
               long longValue = jsonObject.has("user_id") && !jsonObject.get("user_id").isJsonNull() ? jsonObject.get("user_id").getAsLong() : 0L;
               if (!string.isBlank() && string.length() <= 32) {
                  coll.add(new UserSearchClient.Data(longValue, string));
               }
            } catch (RuntimeException e) {
            }
         }

         return List.copyOf(coll);
      }
   }

   private static String method4766(long accountUid, long l, byte[] bytes) {
      try {
         Mac mac = Mac.getInstance("HmacSHA256");
         mac.init(new SecretKeySpec(Cloud.getText2().getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
         mac.update(("nexus-cloud-api-v1\n" + accountUid + "\n" + l + "\nPOST\n/user/search\n").getBytes(StandardCharsets.UTF_8));
         mac.update(MessageDigest.getInstance("SHA-256").digest(bytes));
         return Base64.getUrlEncoder().withoutPadding().encodeToString(mac.doFinal());
      } catch (Exception e) {
         throw new IllegalStateException("HmacSHA256 недоступен", e);
      }
   }

   static {
      RegionUtil.method2042(() -> host = null);
   }

   public static final class Data {
      private final long longValue;
      private final String text;

      public long longValue() {
         return this.longValue;
      }

      public String text() {
         return this.text;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.text, other.text);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(longValue, text);
      }

      @Override
      public String toString() {
         return "Data[" + "longValue=" + this.longValue + ", " + "text=" + this.text + "]";
      }

      public Data(long longValue, String text) {
         this.longValue = longValue;
         this.text = text;
      }

      public long uid() {
         return this.longValue;
      }

      public String name() {
         return this.text;
      }
   }
}
