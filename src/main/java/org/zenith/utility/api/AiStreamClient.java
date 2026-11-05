package org.zenith.utility.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import org.zenith.utility.misc.ShownProvider;

public final class AiStreamClient {
   private static final Set<String> items = Set.of("claude-opus-5-5", "claude-sonnet-5-5", "claude-fable-5-1", "claude-opus-5");
   private static final HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(15L)).followRedirects(Redirect.NORMAL).build();

   private AiStreamClient() {
   }

   public static String method4739(
      ShownProvider shownProvider, String str, String string, String str2, String str3, List<AiStreamClient.Data> datas, Consumer<String> consumer, BooleanSupplier booleanSupplier
   ) throws IOException, InterruptedException {
      HttpRequest httpRequest = switch (shownProvider.getType()) {
         case ANTHROPIC -> method4744(str, string, str2, str3, datas);
         case GEMINI -> method4746(str, string, str2, str3, datas);
         case OPENAI -> method4745(shownProvider, str, string, str2, str3, datas);
      };
      HttpResponse httpResponse = method4740(httpClient.sendAsync(httpRequest, BodyHandlers.ofInputStream()), booleanSupplier);
      StringBuilder builder = new StringBuilder();
      if (httpResponse == null) {
         return builder.toString();
      } else {
         AtomicBoolean flag = new AtomicBoolean();

         try (
            InputStream body = (InputStream)httpResponse.body();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(body, StandardCharsets.UTF_8));
         ) {
            method4742(body, booleanSupplier, flag);
            if (httpResponse.statusCode() / 100 != 2) {
               StringBuilder builder2 = new StringBuilder();

               String str4;
               while ((str4 = method4743(bufferedReader, booleanSupplier)) != null && builder2.length() < 8000) {
                  builder2.append(str4).append('\n');
               }

               throw new AiStreamClient.Entry(method4747(httpResponse.statusCode(), builder2.toString()));
            }

            String str4 = "";

            String str5;
            while ((str5 = method4743(bufferedReader, booleanSupplier)) != null && !booleanSupplier.getAsBoolean()) {
               if (str5.startsWith("data:")) {
                  String json = str5.substring(5).trim();
                  if (!json.isEmpty() && !json.equals("[DONE]")) {
                     JsonObject jsonObject;
                     try {
                        jsonObject = JsonParser.parseString(json).getAsJsonObject();
                     } catch (RuntimeException e) {
                        continue;
                     }

                     String var47_1 = switch (shownProvider.getType()) {
                        case ANTHROPIC -> {
                           String str6 = method4749(jsonObject, "type");
                           if (str6.equals("error")) {
                              throw new AiStreamClient.Entry(method4748(jsonObject));
                           }

                           if (str6.equals("message_delta") && jsonObject.has("delta")) {
                              str4 = method4749(jsonObject.getAsJsonObject("delta"), "stop_reason");
                           }

                           if (str6.equals("content_block_delta") && jsonObject.has("delta")) {
                              JsonObject jsonObject2 = jsonObject.getAsJsonObject("delta");
                              yield method4749(jsonObject2, "type").equals("text_delta") ? method4749(jsonObject2, "text") : "";
                           } else {
                              yield "";
                           }
                        }
                        case GEMINI -> {
                           if (jsonObject.has("error")) {
                              throw new AiStreamClient.Entry(method4748(jsonObject));
                           }

                           StringBuilder builder2 = new StringBuilder();

                           for (JsonElement jsonElement : jsonObject.has("candidates") ? jsonObject.getAsJsonArray("candidates") : new JsonArray()) {
                              JsonObject jsonObject2 = jsonElement.getAsJsonObject();
                              if (jsonObject2.has("content")) {
                                 JsonObject jsonObject3 = jsonObject2.getAsJsonObject("content");
                                 if (jsonObject3.has("parts")) {
                                    for (JsonElement json2 : jsonObject3.getAsJsonArray("parts")) {
                                       JsonObject jsonObject4 = json2.getAsJsonObject();
                                       if (!jsonObject4.has("thought") || !jsonObject4.get("thought").getAsBoolean()) {
                                          builder2.append(method4749(jsonObject4, "text"));
                                       }
                                    }
                                 }
                              }
                           }

                           yield builder2.toString();
                        }
                        case OPENAI -> {
                           if (jsonObject.has("error")) {
                              throw new AiStreamClient.Entry(method4748(jsonObject));
                           }

                           JsonArray jsonArray = jsonObject.has("choices") ? jsonObject.getAsJsonArray("choices") : new JsonArray();
                           if (jsonArray.isEmpty()) {
                              yield "";
                           } else {
                              JsonObject jsonObject2 = jsonArray.get(0).getAsJsonObject();
                              yield jsonObject2.has("delta") && jsonObject2.get("delta").isJsonObject() ? method4749(jsonObject2.getAsJsonObject("delta"), "content") : "";
                           }
                        }
                     };

                     String str6 = var47_1;
                     if (!str6.isEmpty()) {
                        builder.append((String)str6);
                        consumer.accept(str6);
                     }
                  }
               }
            }

            if (str4.equals("refusal")) {
               throw new AiStreamClient.Entry("модель отказалась отвечать на этот запрос");
            }
         } finally {
            flag.set(true);
         }

         return builder.toString();
      }
   }

   private static HttpResponse<InputStream> method4740(CompletableFuture<HttpResponse<InputStream>> sendAsync, BooleanSupplier booleanSupplier) throws IOException, InterruptedException {
      while (!booleanSupplier.getAsBoolean()) {
         try {
            return (HttpResponse<InputStream>)sendAsync.get(100L, TimeUnit.MILLISECONDS);
         } catch (TimeoutException e) {
         } catch (ExecutionException e) {
            Throwable cause = e.getCause();

            while (cause instanceof CompletionException && cause.getCause() != null) {
               cause = cause.getCause();
            }

            if (cause instanceof IOException ioException) {
               throw ioException;
            }

            if (cause instanceof RuntimeException runtimeException) {
               throw runtimeException;
            }

            if (cause instanceof Error error) {
               throw error;
            }

            throw new IOException(cause == null ? e.getMessage() : cause.getMessage(), cause);
         }
      }

      sendAsync.cancel(true);
      sendAsync.thenAccept(AiStreamClient::method4741);
      return null;
   }

   private static void method4741(HttpResponse<InputStream> httpResponse) {
      try {
         httpResponse.body().close();
      } catch (IOException e) {
      }
   }

   private static void method4742(InputStream body, BooleanSupplier booleanSupplier, AtomicBoolean flag) {
      Thread.ofVirtual().name("zenith-ai-cancel").start(() -> {
         try {
            while (!flag.get()) {
               if (booleanSupplier.getAsBoolean()) {
                  body.close();
                  return;
               }

               Thread.sleep(100L);
            }
         } catch (InterruptedException | IOException e) {
         }
      });
   }

   private static String method4743(BufferedReader bufferedReader, BooleanSupplier booleanSupplier) throws IOException {
      try {
         return bufferedReader.readLine();
      } catch (IOException e) {
         if (booleanSupplier.getAsBoolean()) {
            return null;
         } else {
            throw e;
         }
      }
   }

   private static HttpRequest method4744(String str, String string, String str2, String str3, List<AiStreamClient.Data> datas) {
      JsonObject json = new JsonObject();
      json.addProperty("model", string);
      json.addProperty("max_tokens", 32000);
      json.addProperty("stream", true);
      json.addProperty("system", str3);
      JsonArray jsonArray = new JsonArray();

      for (AiStreamClient.Data data : datas) {
         JsonObject json2 = new JsonObject();
         json2.addProperty("role", data.isFlag() ? "user" : "assistant");
         if (data.getItems().isEmpty()) {
            json2.addProperty("content", data.text());
         } else {
            JsonArray jsonArray2 = new JsonArray();

            for (AiStreamClient.Data2 data2 : data.getItems()) {
               JsonObject json3 = new JsonObject();
               json3.addProperty("type", "base64");
               json3.addProperty("media_type", data2.getText());
               json3.addProperty("data", data2.getText2());
               JsonObject json4 = new JsonObject();
               json4.addProperty("type", "image");
               json4.add("source", json3);
               jsonArray2.add(json4);
            }

            JsonObject json3 = new JsonObject();
            json3.addProperty("type", "text");
            json3.addProperty("text", data.text().isBlank() ? "(картинка)" : data.text());
            jsonArray2.add(json3);
            json2.add("content", jsonArray2);
         }

         jsonArray.add(json2);
      }

      json.add("messages", jsonArray);
      Builder header = HttpRequest.newBuilder(URI.create(str2))
         .timeout(Duration.ofMinutes(10L))
         .header("content-type", "application/json")
         .header("anthropic-version", "2023-06-01")
         .header("x-api-key", str);
      if (items.contains(string)) {
         json.addProperty("fallbacks", "default");
         header.header("anthropic-beta", "server-side-fallback-2026-07-01");
      }

      return header.POST(BodyPublishers.ofString(json.toString(), StandardCharsets.UTF_8)).build();
   }

   private static HttpRequest method4745(ShownProvider shownProvider, String str, String string, String string2, String str2, List<AiStreamClient.Data> datas) {
      JsonObject json = new JsonObject();
      json.addProperty("model", AiSettingsStore.clean(string));
      json.addProperty("stream", true);
      JsonArray jsonArray = new JsonArray();
      JsonObject json2 = new JsonObject();
      json2.addProperty("role", "system");
      json2.addProperty("content", str2);
      jsonArray.add(json2);

      for (AiStreamClient.Data data : datas) {
         JsonObject json3 = new JsonObject();
         json3.addProperty("role", data.isFlag() ? "user" : "assistant");
         if (data.getItems().isEmpty()) {
            json3.addProperty("content", data.text());
         } else {
            JsonArray jsonArray2 = new JsonArray();
            JsonObject json4 = new JsonObject();
            json4.addProperty("type", "text");
            json4.addProperty("text", data.text().isBlank() ? "(картинка)" : data.text());
            jsonArray2.add(json4);

            for (AiStreamClient.Data2 data2 : data.getItems()) {
               JsonObject json5 = new JsonObject();
               json5.addProperty("url", "data:" + data2.getText() + ";base64," + data2.getText2());
               JsonObject json6 = new JsonObject();
               json6.addProperty("type", "image_url");
               json6.add("image_url", json5);
               jsonArray2.add(json6);
            }

            json3.add("content", jsonArray2);
         }

         jsonArray.add(json3);
      }

      json.add("messages", jsonArray);
      String clean = AiSettingsStore.clean(string2);

      while (clean.endsWith("/")) {
         clean = clean.substring(0, clean.length() - 1);
      }

      if (!clean.endsWith("/chat/completions")) {
         clean = clean + "/chat/completions";
      }

      Builder header = HttpRequest.newBuilder(URI.create(clean)).timeout(Duration.ofMinutes(10L)).header("Content-Type", "application/json");
      if (str != null && !str.isBlank()) {
         header.header("Authorization", "Bearer " + str);
      }

      if (shownProvider == ShownProvider.INSTANCE6) {
         header.header("HTTP-Referer", "https://zenithdlc.com").header("X-Title", "Zenith Client");
      }

      return header.POST(BodyPublishers.ofString(json.toString(), StandardCharsets.UTF_8)).build();
   }

   private static HttpRequest method4746(String str, String string, String str2, String str3, List<AiStreamClient.Data> datas) {
      JsonObject json = new JsonObject();
      JsonObject json2 = new JsonObject();
      JsonArray jsonArray = new JsonArray();
      JsonObject json3 = new JsonObject();
      json3.addProperty("text", str3);
      jsonArray.add(json3);
      json2.add("parts", jsonArray);
      json.add("systemInstruction", json2);
      JsonArray jsonArray2 = new JsonArray();

      for (AiStreamClient.Data data : datas) {
         JsonObject json4 = new JsonObject();
         json4.addProperty("role", data.isFlag() ? "user" : "model");
         JsonArray jsonArray3 = new JsonArray();

         for (AiStreamClient.Data2 data2 : data.getItems()) {
            JsonObject json5 = new JsonObject();
            json5.addProperty("mime_type", data2.getText());
            json5.addProperty("data", data2.getText2());
            JsonObject json6 = new JsonObject();
            json6.add("inline_data", json5);
            jsonArray3.add(json6);
         }

         JsonObject json5 = new JsonObject();
         json5.addProperty("text", data.text().isBlank() ? "(картинка)" : data.text());
         jsonArray3.add(json5);
         json4.add("parts", jsonArray3);
         jsonArray2.add(json4);
      }

      json.add("contents", jsonArray2);
      String str4 = str2.endsWith("/") ? str2 : str2 + "/";
      String str5 = str4 + URLEncoder.encode(string, StandardCharsets.UTF_8) + ":streamGenerateContent?alt=sse";
      return HttpRequest.newBuilder(URI.create(str5))
         .timeout(Duration.ofMinutes(10L))
         .header("Content-Type", "application/json")
         .header("x-goog-api-key", str)
         .POST(BodyPublishers.ofString(json.toString(), StandardCharsets.UTF_8))
         .build();
   }

   private static String method4747(int statusCode, String string) {
      String str = "";
      String lowerCase = string.stripLeading().toLowerCase(Locale.ROOT);
      if (!lowerCase.startsWith("<!doctype") && !lowerCase.startsWith("<html")) {
         try {
            JsonElement jsonElement = JsonParser.parseString(string.trim());
            if (jsonElement.isJsonArray() && !jsonElement.getAsJsonArray().isEmpty()) {
               jsonElement = jsonElement.getAsJsonArray().get(0);
            }

            if (jsonElement.isJsonObject()) {
               str = method4748(jsonElement.getAsJsonObject());
            }
         } catch (RuntimeException e) {
         }

         if (str.isEmpty()) {
            str = string.strip();
         }

         if (str.length() > 300) {
            str = str.substring(0, 300) + "…";
         }
         String str2 = switch (statusCode) {
            case 400 -> "запрос отклонён";
            case 401, 403 -> "ключ не подошёл";
            case 404 -> "модель или адрес не найдены";
            case 429 -> "лимит запросов или кончились деньги на счёте";
            default -> statusCode >= 500 ? "сервис нейросети недоступен" : "ошибка " + statusCode;
         };
         return str2 + (str.isEmpty() ? "" : ": " + str);
      } else {
         return (statusCode == 404 ? "адрес не найден" : "ошибка " + statusCode)
            + ": сервер вернул веб-страницу вместо ответа API — проверь адрес (должен вести на /v1, без лишних символов в конце)";
      }
   }

   private static String method4748(JsonObject jsonObject) {
      if (jsonObject.has("error")) {
         JsonElement jsonElement = jsonObject.get("error");
         if (jsonElement.isJsonObject()) {
            String str = method4749(jsonElement.getAsJsonObject(), "message");
            if (!str.isEmpty()) {
               return str;
            }
         } else if (jsonElement.isJsonPrimitive()) {
            return jsonElement.getAsString();
         }
      }

      return method4749(jsonObject, "message");
   }

   private static String method4749(JsonObject jsonObject, String memberName) {
      return jsonObject != null && jsonObject.has(memberName) && jsonObject.get(memberName).isJsonPrimitive() ? jsonObject.get(memberName).getAsString() : "";
   }

   public static final class Data {
      private final boolean flag;
      private final String text;
      private final List<AiStreamClient.Data2> items;

      public boolean flag() {
         return this.flag;
      }

      public String text() {
         return this.text;
      }

      public List<AiStreamClient.Data2> items() {
         return this.items;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.flag, other.flag) && java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.items, other.items);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(flag, text, items);
      }

      @Override
      public String toString() {
         return "Data[" + "flag=" + this.flag + ", " + "text=" + this.text + ", " + "items=" + this.items + "]";
      }

      public Data(boolean flag, String text) {
         this(flag, text, List.of());
      }

      public Data(boolean flag, String text, List<AiStreamClient.Data2> items) {
         this.flag = flag;
         this.text = text;
         this.items = items;
      }

      public boolean isFlag() {
         return this.flag;
      }

      public List<AiStreamClient.Data2> getItems() {
         return this.items;
      }
   }

   public static final class Data2 {
      private final String text;
      private final String text2;

      public String text() {
         return this.text;
      }

      public String text2() {
         return this.text2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data2 other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, text2);
      }

      @Override
      public String toString() {
         return "Data2[" + "text=" + this.text + ", " + "text2=" + this.text2 + "]";
      }

      public Data2(String text, String encodeToString) {
         this.text = text;
         this.text2 = encodeToString;
      }

      public String getText() {
         return this.text;
      }

      public String getText2() {
         return this.text2;
      }
   }

   public static final class Entry extends IOException {
      public Entry(String message) {
         super(message);
      }
   }
}
