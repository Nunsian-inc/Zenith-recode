package org.zenith.utility.api;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.net.http.WebSocket.Listener;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import org.zenith.utility.config.SiteSession;

public final class WebSocketClient {
   private static final Duration duration = Duration.ofSeconds(8L);
   private static final long CONST_longValue = 2000L;
   private static final long CONST_longValue2 = 30000L;
   private static final long CONST_longValue3 = 70000L;
   private static final long CONST_longValue4 = 25000L;
   private static final int CONST_intValue = 1048576;
   private final String text;
   private final Consumer<JsonObject> consumer;
   private final StringBuilder stringBuilder = new StringBuilder();
   private boolean flag;
   private volatile HttpClient httpClient;
   private volatile long longValue;
   private volatile WebSocket webSocket;
   private volatile boolean flag2;
   private volatile boolean flag3;
   private volatile long longValue2;
   private volatile long longValue3 = 2000L;
   private volatile long longValue4;
   private volatile long longValue5;
   private volatile String text2 = "";
   private volatile String host;
   private volatile int intValue;
   private static final int CONST_intValue2 = 2;
   private volatile int intValue2;
   private volatile long longValue6;
   private static final long CONST_longValue5 = 30000L;

   public WebSocketClient(String text, Consumer<JsonObject> consumer) {
      this.text = text;
      this.consumer = consumer;
   }

   public boolean isLive() {
      WebSocket webSocket = this.webSocket;
      return webSocket != null && !webSocket.isOutputClosed() && System.currentTimeMillis() - this.longValue4 < 70000L;
   }

   public void method491() {
      this.flag2 = true;
      String text = SiteSession.method2289();
      if (text.isEmpty()) {
         this.close();
      } else {
         if (!text.equals(this.text2) && this.webSocket != null) {
            this.close();
         }

         WebSocket webSocket = this.webSocket;
         long now = System.currentTimeMillis();
         if (webSocket != null && !webSocket.isOutputClosed()) {
            if (now - this.longValue4 >= 70000L) {
               this.close();
               this.method495(this.longValue);
               return;
            }

            if (now - this.longValue5 >= 25000L) {
               this.method496(webSocket, "{\"type\":\"ping\"}");
            }
         }

         if (!this.flag3 && !this.isLive() && now >= this.longValue2) {
            this.method492(text);
         }
      }
   }

   public void release() {
      this.flag2 = false;
      this.close();
   }

   private void method492(String text) {
      this.flag3 = true;
      this.text2 = text;
      long longValue = ++this.longValue;
      HttpClient httpClient = HttpClients.method2670(duration);
      this.httpClient = httpClient;
      String host = this.method493();

      CompletableFuture<java.net.http.WebSocket> async;
      try {
         URI uri = URI.create(host.replaceFirst("^http", "ws") + this.text);
         async = httpClient.newWebSocketBuilder().connectTimeout(duration).buildAsync(uri, new WebSocketClient.Entry(longValue, text));
      } catch (RuntimeException e) {
         this.flag3 = false;
         this.method494(host);
         this.method495(longValue);
         return;
      }

      async.whenComplete((var4x, var5x) -> {
         this.flag3 = false;
         if (var5x != null) {
            this.method494(host);
            this.method495(longValue);
         } else {
            this.host = host;
            this.intValue = 0;
            this.intValue2 = 0;
            if (longValue != this.longValue) {
               var4x.abort();
            } else {
               this.longValue4 = System.currentTimeMillis();
               this.longValue6 = this.longValue4;
               this.webSocket = var4x;
            }
         }
      });
   }

   private String method493() {
      String host = this.host;
      return host != null && RegionUtil.method2037().contains(host) ? host : SiteHttpClient.method2409();
   }

   private void method494(String host) {
      if (++this.intValue >= 2) {
         this.intValue = 0;
         var strings = RegionUtil.method2037();
         int indexOf = strings.indexOf(host);
         this.host = (String)strings.get(indexOf < 0 ? 0 : (indexOf + 1) % strings.size());
         if (++this.intValue2 < strings.size()) {
            this.longValue3 = 2000L;
         }
      }
   }

   private void method495(long longValue) {
      if (longValue == this.longValue) {
         this.webSocket = null;
         long l = this.longValue6;
         this.longValue6 = 0L;
         if (l != 0L && System.currentTimeMillis() - l >= 30000L) {
            this.longValue3 = 2000L;
         }

         long l2 = this.longValue3;
         this.longValue2 = System.currentTimeMillis() + Math.round(l2 * (0.8 + ThreadLocalRandom.current().nextDouble() * 0.4));
         this.longValue3 = Math.min(30000L, l2 * 2L);
      }
   }

   private void close() {
      this.longValue++;
      WebSocket webSocket = this.webSocket;
      this.webSocket = null;
      this.text2 = "";
      synchronized (this.stringBuilder) {
         this.stringBuilder.setLength(0);
         this.flag = false;
      }

      if (webSocket != null) {
         try {
            webSocket.abort();
         } catch (RuntimeException e) {
         }
      }

      this.httpClient = null;
   }

   private void method496(WebSocket webSocket, String string) {
      try {
         this.longValue5 = System.currentTimeMillis();
         webSocket.sendText(string, true);
      } catch (RuntimeException e) {
         this.method495(this.longValue);
      }
   }

   private void method497(WebSocket webSocket, String string) {
      JsonObject jsonObject;
      try {
         jsonObject = JsonParser.parseString(string).getAsJsonObject();
      } catch (RuntimeException e) {
         return;
      }

      String str = SiteHttpClient.method2417(jsonObject, "type", "");
      if (str.equals("ping")) {
         this.method496(webSocket, "{\"type\":\"pong\"}");
      } else if (!str.equals("pong")) {
         try {
            this.consumer.accept(jsonObject);
         } catch (RuntimeException e) {
         }
      }
   }

   private final class Entry implements Listener {
      private final long longValue;
      private final String text;

      public Entry(long longValue, String text) {
         this.longValue = longValue;
         this.text = text;
      }

      @Override
      public void onOpen(WebSocket webSocket) {
         WebSocketClient.this.longValue4 = System.currentTimeMillis();
         JsonObject json = new JsonObject();
         json.addProperty("type", "auth");
         json.addProperty("token", this.text);
         WebSocketClient.this.method496(webSocket, json.toString());
         webSocket.request(1L);
      }

      @Override
      public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
         if (this.longValue != WebSocketClient.this.longValue) {
            return null;
         } else {
            WebSocketClient.this.longValue4 = System.currentTimeMillis();
            String string = null;
            synchronized (WebSocketClient.this.stringBuilder) {
               if (!WebSocketClient.this.flag && WebSocketClient.this.stringBuilder.length() + data.length() > 1048576) {
                  WebSocketClient.this.stringBuilder.setLength(0);
                  WebSocketClient.this.flag = true;
               }

               if (!WebSocketClient.this.flag) {
                  WebSocketClient.this.stringBuilder.append(data);
               }

               if (last) {
                  if (!WebSocketClient.this.flag) {
                     string = WebSocketClient.this.stringBuilder.toString();
                  }

                  WebSocketClient.this.stringBuilder.setLength(0);
                  WebSocketClient.this.flag = false;
               }
            }

            webSocket.request(1L);
            if (string != null) {
               WebSocketClient.this.method497(webSocket, string);
            }

            return null;
         }
      }

      @Override
      public CompletionStage<?> onPing(WebSocket webSocket, ByteBuffer message) {
         if (this.longValue == WebSocketClient.this.longValue) {
            WebSocketClient.this.longValue4 = System.currentTimeMillis();
         }

         webSocket.request(1L);
         return null;
      }

      @Override
      public CompletionStage<?> onPong(WebSocket webSocket, ByteBuffer message) {
         if (this.longValue == WebSocketClient.this.longValue) {
            WebSocketClient.this.longValue4 = System.currentTimeMillis();
         }

         webSocket.request(1L);
         return null;
      }

      @Override
      public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
         this.done();
         return null;
      }

      @Override
      public void onError(WebSocket webSocket, Throwable error) {
         this.done();
      }

      private void done() {
         if (this.longValue == WebSocketClient.this.longValue) {
            if (WebSocketClient.this.flag2) {
               WebSocketClient.this.method495(this.longValue);
            } else {
               WebSocketClient.this.webSocket = null;
            }
         }
      }
   }
}
