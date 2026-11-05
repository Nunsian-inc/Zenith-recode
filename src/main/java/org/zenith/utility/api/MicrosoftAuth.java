package org.zenith.utility.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public final class MicrosoftAuth {
   public static final String text = "54fd49e4-2103-4044-9603-2b028c814ec3";
   private static final String text2 = "XboxLive.signin XboxLive.offline_access";
   private static final Duration duration = Duration.ofSeconds(30L);
   private static final String text3 = "Zenith/3.5";
   private static final Executor executor = Executors.newCachedThreadPool(task -> {
      Thread thread = new Thread(task, "Zenith Microsoft Auth");
      thread.setDaemon(true);
      return thread;
   });
   private static final HttpClient httpClient = HttpClients.method2671(duration, Redirect.NORMAL);

   private MicrosoftAuth() {
   }

   public static CompletableFuture<MicrosoftAuth.Data> method3056() {
      String string = "client_id=54fd49e4-2103-4044-9603-2b028c814ec3&scope=" + encode("XboxLive.signin XboxLive.offline_access");
      return method3067("https://login.microsoftonline.com/consumers/oauth2/v2.0/devicecode", "application/x-www-form-urlencoded", string)
         .thenApply(
            var0x -> new MicrosoftAuth.Data(
               var0x.get("user_code").getAsString(),
               var0x.get("verification_uri").getAsString(),
               var0x.get("device_code").getAsString(),
               var0x.has("interval") ? var0x.get("interval").getAsInt() : 5,
               var0x.has("expires_in") ? var0x.get("expires_in").getAsInt() : 900
            )
         );
   }

   public static CompletableFuture<MicrosoftAuth.Data2> method3057(MicrosoftAuth.Data deviceCode, Consumer<String> consumer) {
      return method3058(deviceCode, consumer, () -> false);
   }

   public static CompletableFuture<MicrosoftAuth.Data2> method3058(MicrosoftAuth.Data deviceCode, Consumer<String> consumer, BooleanSupplier booleanSupplier) {
      return CompletableFuture.<String[]>supplyAsync(() -> {
         long var2x = System.currentTimeMillis() + deviceCode.getIntValue2() * 1000L;
         long millis = Math.max(1, deviceCode.getIntValue()) * 1000L;

         while (System.currentTimeMillis() < var2x) {
            if (booleanSupplier.getAsBoolean()) {
               throw new CancellationException("Login cancelled");
            }

            method3070(millis);
            if (booleanSupplier.getAsBoolean()) {
               throw new CancellationException("Login cancelled");
            }

            JsonObject json = method3066(deviceCode.getText3());
            if (json != null) {
               if (json.has("access_token")) {
                  return new String[]{json.get("access_token").getAsString(), json.has("refresh_token") ? json.get("refresh_token").getAsString() : ""};
               }

               String string = json.has("error") ? json.get("error").getAsString() : "";
               switch (string) {
                  case "authorization_pending":
                     break;
                  case "slow_down":
                     millis += 5000L;
                     break;
                  case "authorization_declined":
                     throw new IllegalStateException("Login declined");
                  case "expired_token":
                     throw new IllegalStateException("Code expired");
                  default:
                     throw new IllegalStateException("Microsoft error: " + string);
               }
            }
         }

         throw new IllegalStateException("Code expired");
      }, executor).thenCompose(var1x -> {
         consumer.accept("Signing in to Xbox Live...");
         return method3060(var1x[0], var1x[1], consumer);
      });
   }

   public static CompletableFuture<MicrosoftAuth.Data2> method3059(String str, Consumer<String> consumer) {
      String string = "client_id=54fd49e4-2103-4044-9603-2b028c814ec3&refresh_token="
         + encode(str)
         + "&grant_type=refresh_token&scope="
         + encode("XboxLive.signin XboxLive.offline_access");
      return method3067("https://login.microsoftonline.com/consumers/oauth2/v2.0/token", "application/x-www-form-urlencoded", string)
         .thenCompose(
            var2x -> method3060(var2x.get("access_token").getAsString(), var2x.has("refresh_token") ? var2x.get("refresh_token").getAsString() : str, consumer)
         );
   }

   private static CompletableFuture<MicrosoftAuth.Data2> method3060(String string, String str, Consumer<String> consumer) {
      return method3061(string).thenCompose(var1x -> {
         consumer.accept("Requesting XSTS token...");
         return method3062(var1x[0]).thenApply(var0xx -> new String[]{var0xx[0], var0xx[1]});
      }).thenCompose(var1x -> {
         consumer.accept("Authenticating with Minecraft...");
         return method3064(var1x[0], var1x[1]);
      }).thenCompose(var2x -> {
         consumer.accept("Loading profile...");
         return method3065(var2x).thenApply(var2xx -> new MicrosoftAuth.Data2(var2xx[0], method3069(var2xx[1]), var2x, str));
      });
   }

   private static CompletableFuture<String[]> method3061(String string) {
      JsonObject json = new JsonObject();
      json.addProperty("AuthMethod", "RPS");
      json.addProperty("SiteName", "user.auth.xboxlive.com");
      json.addProperty("RpsTicket", "d=" + string);
      JsonObject json2 = new JsonObject();
      json2.add("Properties", json);
      json2.addProperty("RelyingParty", "http://auth.xboxlive.com");
      json2.addProperty("TokenType", "JWT");
      return method3067("https://user.auth.xboxlive.com/user/authenticate", "application/json", json2.toString()).thenApply(MicrosoftAuth::method3063);
   }

   private static CompletableFuture<String[]> method3062(String string) {
      JsonArray jsonArray = new JsonArray();
      jsonArray.add(string);
      JsonObject json = new JsonObject();
      json.add("UserTokens", jsonArray);
      json.addProperty("SandboxId", "RETAIL");
      JsonObject json2 = new JsonObject();
      json2.add("Properties", json);
      json2.addProperty("RelyingParty", "rp://api.minecraftservices.com/");
      json2.addProperty("TokenType", "JWT");
      return method3067("https://xsts.auth.xboxlive.com/xsts/authorize", "application/json", json2.toString()).thenApply(MicrosoftAuth::method3063);
   }

   private static String[] method3063(JsonObject json) {
      String string = json.get("Token").getAsString();
      String str = json.getAsJsonObject("DisplayClaims").getAsJsonArray("xui").get(0).getAsJsonObject().get("uhs").getAsString();
      return new String[]{string, str};
   }

   private static CompletableFuture<String> method3064(String str, String str2) {
      JsonObject json = new JsonObject();
      json.addProperty("identityToken", "XBL3.0 x=" + str2 + ";" + str);
      return method3067("https://api.minecraftservices.com/authentication/login_with_xbox", "application/json", json.toString())
         .thenApply(var0x -> var0x.get("access_token").getAsString());
   }

   private static CompletableFuture<String[]> method3065(String str) {
      HttpRequest httpRequest = HttpRequest.newBuilder()
         .uri(URI.create("https://api.minecraftservices.com/minecraft/profile"))
         .header("Authorization", "Bearer " + str)
         .header("Accept", "application/json")
         .header("User-Agent", "Zenith/3.5")
         .timeout(duration)
         .GET()
         .build();
      return httpClient.sendAsync(httpRequest, BodyHandlers.ofString()).thenApplyAsync(var0x -> {
         if (var0x.statusCode() == 404) {
            throw new IllegalStateException("This account does not own Minecraft");
         } else {
            JsonObject var1x = method3068((HttpResponse<String>)var0x, "profile");
            return new String[]{var1x.get("name").getAsString(), var1x.get("id").getAsString()};
         }
      }, executor);
   }

   private static JsonObject method3066(String str) {
      String body = "grant_type=urn:ietf:params:oauth:grant-type:device_code&client_id=54fd49e4-2103-4044-9603-2b028c814ec3&device_code=" + encode(str);
      HttpRequest httpRequest = HttpRequest.newBuilder()
         .uri(URI.create("https://login.microsoftonline.com/consumers/oauth2/v2.0/token"))
         .header("Content-Type", "application/x-www-form-urlencoded")
         .header("Accept", "application/json")
         .header("User-Agent", "Zenith/3.5")
         .timeout(duration)
         .POST(BodyPublishers.ofString(body))
         .build();

      try {
         HttpResponse send = httpClient.send(httpRequest, BodyHandlers.ofString());
         return JsonParser.parseString((String)send.body()).getAsJsonObject();
      } catch (Exception e) {
         return null;
      }
   }

   private static CompletableFuture<JsonObject> method3067(String str, String str2, String string) {
      HttpRequest httpRequest = HttpRequest.newBuilder()
         .uri(URI.create(str))
         .header("Content-Type", str2)
         .header("Accept", "application/json")
         .header("User-Agent", "Zenith/3.5")
         .timeout(duration)
         .POST(BodyPublishers.ofString(string))
         .build();
      return httpClient.sendAsync(httpRequest, BodyHandlers.ofString()).thenApplyAsync(var1x -> method3068((HttpResponse<String>)var1x, str), executor);
   }

   private static JsonObject method3068(HttpResponse<String> httpResponse, String str) {
      if (httpResponse.statusCode() / 100 != 2) {
         throw new IllegalStateException("HTTP " + httpResponse.statusCode() + " from " + str);
      } else {
         return JsonParser.parseString((String)httpResponse.body()).getAsJsonObject();
      }
   }

   private static UUID method3069(String str) {
      String replaceFirst = str.length() == 32
         ? str.replaceFirst("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5")
         : str;
      return UUID.fromString(replaceFirst);
   }

   private static String encode(String str) {
      return URLEncoder.encode(str, StandardCharsets.UTF_8);
   }

   private static void method3070(long millis) {
      try {
         Thread.sleep(millis);
      } catch (InterruptedException e) {
         Thread.currentThread().interrupt();
         throw new IllegalStateException("Interrupted");
      }
   }

   public static final class Data {
      private final String text;
      private final String text2;
      private final String text3;
      private final int intValue;
      private final int intValue2;

      public String text() {
         return this.text;
      }

      public String text2() {
         return this.text2;
      }

      public String text3() {
         return this.text3;
      }

      public int intValue() {
         return this.intValue;
      }

      public int intValue2() {
         return this.intValue2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.text3, other.text3) && java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.intValue2, other.intValue2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, text2, text3, intValue, intValue2);
      }

      @Override
      public String toString() {
         return "Data[" + "text=" + this.text + ", " + "text2=" + this.text2 + ", " + "text3=" + this.text3 + ", " + "intValue=" + this.intValue + ", " + "intValue2=" + this.intValue2 + "]";
      }

      public Data(String text, String string, String str, int intValue, int n) {
         this.text = text;
         this.text2 = string;
         this.text3 = str;
         this.intValue = intValue;
         this.intValue2 = n;
      }

      public String getText() {
         return this.text;
      }

      public String getText2() {
         return this.text2;
      }

      public String getText3() {
         return this.text3;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public int getIntValue2() {
         return this.intValue2;
      }
   }

   public static final class Data2 {
      private final String text;
      private final UUID uUID;
      private final String text2;
      private final String text3;

      public String text() {
         return this.text;
      }

      public UUID uUID() {
         return this.uUID;
      }

      public String text2() {
         return this.text2;
      }

      public String text3() {
         return this.text3;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data2 other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.uUID, other.uUID) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.text3, other.text3);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, uUID, text2, text3);
      }

      @Override
      public String toString() {
         return "Data2[" + "text=" + this.text + ", " + "uUID=" + this.uUID + ", " + "text2=" + this.text2 + ", " + "text3=" + this.text3 + "]";
      }

      public Data2(String text, UUID uUID, String str, String str2) {
         this.text = text;
         this.uUID = uUID;
         this.text2 = str;
         this.text3 = str2;
      }

      public String name() {
         return this.text;
      }

      public UUID uuid() {
         return this.uUID;
      }

      public String getText2() {
         return this.text2;
      }

      public String getText3() {
         return this.text3;
      }
   }
}
