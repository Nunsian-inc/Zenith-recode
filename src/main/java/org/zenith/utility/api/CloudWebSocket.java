package org.zenith.utility.api;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.net.http.WebSocket.Listener;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;

public final class CloudWebSocket {
   private static final int CONST_intValue = 256;
   public static final int CONST_intValue2 = -1;
   private static final int CONST_intValue3 = 8;
   private static final int CONST_intValue4 = 1048576;
   private static final HttpClient httpClient = HttpClients.method2670(Duration.ofSeconds(8L));
   private final URI uRI;
   private final BiConsumer<CloudWebSocket, ByteBuffer> biConsumer;
   private final Runnable runnable;
   private final BiConsumer<CloudWebSocket, String> biConsumer2;
   private final ArrayDeque<CloudWebSocket.Data> arrayDeque = new ArrayDeque<>();
   private boolean flag;
   private volatile WebSocket webSocket;
   private volatile boolean closed;
   private volatile long longValue = System.currentTimeMillis();
   private volatile long longValue2 = System.currentTimeMillis();
   private volatile long longValue3;
   private final AtomicBoolean atomicBoolean = new AtomicBoolean();
   private volatile long longValue4;
   private volatile int intValue = -1;
   private ByteBuffer byteBuffer;

   public CloudWebSocket(URI uRI, BiConsumer<CloudWebSocket, ByteBuffer> biConsumer, Runnable runnable, BiConsumer<CloudWebSocket, String> biConsumer2) {
      this.uRI = uRI;
      this.biConsumer = biConsumer;
      this.runnable = runnable;
      this.biConsumer2 = biConsumer2;
   }

   public void connect() {
      httpClient.newWebSocketBuilder().connectTimeout(Duration.ofSeconds(8L)).buildAsync(this.uRI, new CloudWebSocket.Entry()).whenComplete((webSocket, error) -> {
         if (error != null) {
            this.method1955("не удалось подключиться: " + method1956(error));
         } else if (this.closed) {
            webSocket.abort();
         } else {
            this.webSocket = webSocket;
            if (this.closed) {
               this.webSocket = null;
               webSocket.abort();
            } else {
               this.method1946();
               this.runnable.run();
            }
         }
      });
   }

   private void method1946() {
      boolean flag;
      synchronized (this.arrayDeque) {
         flag = !this.flag && !this.arrayDeque.isEmpty() && this.webSocket != null;
         if (flag) {
            this.flag = true;
         }
      }

      if (flag) {
         this.method1952();
      }
   }

   public boolean isOpen() {
      WebSocket webSocket = this.webSocket;
      return webSocket != null && !this.closed && !webSocket.isOutputClosed();
   }

   public long method1947() {
      return System.currentTimeMillis() - this.longValue;
   }

   public long method1948() {
      return System.currentTimeMillis() - this.longValue2;
   }

   public long method1949() {
      return System.currentTimeMillis() - this.longValue3;
   }

   public void ping() {
      WebSocket webSocket = this.webSocket;
      if (webSocket != null && !this.closed && this.atomicBoolean.compareAndSet(false, true)) {
         this.longValue2 = System.currentTimeMillis();
         this.longValue3 = this.longValue2;
         this.longValue4 = System.nanoTime();
         webSocket.sendPing(ByteBuffer.allocate(0)).whenComplete((var1x, error) -> {
            this.atomicBoolean.set(false);
            if (error != null) {
               this.method1955("ping не прошёл: " + method1956(error));
            }
         });
      }
   }

   public int getIntValue() {
      return this.intValue;
   }

   public void method1950(String text) {
      this.method1955(text);
   }

   public void method1951(ByteBuffer byteBuffer, int intValue) {
      if (!this.closed) {
         boolean flag;
         synchronized (this.arrayDeque) {
            if (intValue != -1) {
               if (intValue != 0) {
                  var iterator = this.arrayDeque.iterator();

                  while (iterator.hasNext()) {
                     if (((CloudWebSocket.Data)iterator.next()).intValue == intValue) {
                        iterator.remove();
                        break;
                     }
                  }
               } else if (this.arrayDeque.size() >= 256) {
                  this.arrayDeque.pollFirst();
               }
            } else {
               int n = 0;
               CloudWebSocket.Data data = null;

               for (CloudWebSocket.Data data2 : this.arrayDeque) {
                  if (data2.intValue == -1) {
                     if (data == null) {
                        data = data2;
                     }

                     n++;
                  }
               }

               if (n >= 8) {
                  this.arrayDeque.remove(data);
               }
            }

            this.arrayDeque.addLast(new CloudWebSocket.Data(byteBuffer, intValue));
            flag = !this.flag && this.webSocket != null;
            if (flag) {
               this.flag = true;
            }
         }

         if (flag) {
            this.method1952();
         }
      }
   }

   private void method1952() {
      WebSocket webSocket = this.webSocket;
      CloudWebSocket.Data pollFirst;
      synchronized (this.arrayDeque) {
         pollFirst = this.arrayDeque.pollFirst();
         if (pollFirst == null || webSocket == null || this.closed) {
            this.flag = false;
            return;
         }
      }

      this.longValue2 = System.currentTimeMillis();
      webSocket.sendBinary(pollFirst.byteBuffer, true).whenComplete((var1x, var2x) -> {
         if (var2x != null) {
            this.method1955("отправка не прошла: " + method1956(var2x));
         } else {
            this.method1952();
         }
      });
   }

   private synchronized boolean method1953() {
      if (this.closed) {
         return false;
      } else {
         this.closed = true;
         return true;
      }
   }

   public void method1954(String str) {
      if (this.method1953()) {
         WebSocket webSocket = this.webSocket;
         this.webSocket = null;
         if (webSocket != null) {
            webSocket.sendClose(1000, str).orTimeout(3L, TimeUnit.SECONDS).whenComplete((var1x, var2x) -> webSocket.abort());
         }

         synchronized (this.arrayDeque) {
            this.arrayDeque.clear();
            this.flag = false;
         }
      }
   }

   private void method1955(String text) {
      if (this.method1953()) {
         WebSocket webSocket = this.webSocket;
         this.webSocket = null;
         if (webSocket != null) {
            webSocket.abort();
         }

         synchronized (this.arrayDeque) {
            this.arrayDeque.clear();
            this.flag = false;
         }

         this.biConsumer2.accept(this, text);
      }
   }

   public static String method1956(Throwable error) {
      Throwable throwable = error;

      while (throwable.getCause() != null) {
         throwable = throwable.getCause();
      }

      String message = throwable.getMessage();
      return message != null && !message.isBlank() ? message : throwable.getClass().getSimpleName();
   }

   private static final class Data {
      private final ByteBuffer byteBuffer;
      private final int intValue;

      public ByteBuffer byteBuffer() {
         return this.byteBuffer;
      }

      public int intValue() {
         return this.intValue;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.byteBuffer, other.byteBuffer) && java.util.Objects.equals(this.intValue, other.intValue);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(byteBuffer, intValue);
      }

      @Override
      public String toString() {
         return "Data[" + "byteBuffer=" + this.byteBuffer + ", " + "intValue=" + this.intValue + "]";
      }

      private Data(ByteBuffer byteBuffer, int intValue) {
         this.byteBuffer = byteBuffer;
         this.intValue = intValue;
      }

      public ByteBuffer getByteBuffer() {
         return this.byteBuffer;
      }

      public int getIntValue() {
         return this.intValue;
      }
   }

   private final class Entry implements Listener {
      @Override
      public void onOpen(WebSocket webSocket) {
         CloudWebSocket.this.longValue = System.currentTimeMillis();
         webSocket.request(1L);
      }

      @Override
      public CompletionStage<?> onBinary(WebSocket webSocket, ByteBuffer data, boolean last) {
         CloudWebSocket.this.longValue = System.currentTimeMillis();
         ByteBuffer byteBuffer = this.method1957(data, last);
         if (byteBuffer != null) {
            try {
               CloudWebSocket.this.biConsumer.accept(CloudWebSocket.this, byteBuffer);
            } catch (Throwable e) {
               CloudWebSocket.this.method1955("разбор кадра: " + CloudWebSocket.method1956(e));
               return null;
            }
         }

         webSocket.request(1L);
         return null;
      }

      @Override
      public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
         webSocket.request(1L);
         return null;
      }

      @Override
      public CompletionStage<?> onPing(WebSocket webSocket, ByteBuffer message) {
         CloudWebSocket.this.longValue = System.currentTimeMillis();
         webSocket.request(1L);
         return null;
      }

      @Override
      public CompletionStage<?> onPong(WebSocket webSocket, ByteBuffer message) {
         CloudWebSocket.this.longValue = System.currentTimeMillis();
         long l = CloudWebSocket.this.longValue4;
         if (l != 0L) {
            CloudWebSocket.this.longValue4 = 0L;
            CloudWebSocket.this.intValue = (int)Math.min(9999L, (System.nanoTime() - l) / 1000000L);
         }

         webSocket.request(1L);
         return null;
      }

      @Override
      public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
         CloudWebSocket.this.method1955("соединение закрыто (" + statusCode + (reason != null && !reason.isBlank() ? ", " + reason : "") + ")");
         return null;
      }

      @Override
      public void onError(WebSocket webSocket, Throwable error) {
         CloudWebSocket.this.method1955("обрыв: " + CloudWebSocket.method1956(error));
      }

      private ByteBuffer method1957(ByteBuffer data, boolean last) {
         if (last && CloudWebSocket.this.byteBuffer == null) {
            return data;
         } else if ((CloudWebSocket.this.byteBuffer == null ? 0 : CloudWebSocket.this.byteBuffer.position()) + data.remaining() > 1048576) {
            CloudWebSocket.this.byteBuffer = null;
            CloudWebSocket.this.method1955("кадр больше допустимого");
            return null;
         } else {
            if (CloudWebSocket.this.byteBuffer == null) {
               CloudWebSocket.this.byteBuffer = ByteBuffer.allocate(data.remaining()).put(data);
            } else {
               ByteBuffer allocate = ByteBuffer.allocate(CloudWebSocket.this.byteBuffer.position() + data.remaining());
               CloudWebSocket.this.byteBuffer.flip();
               allocate.put(CloudWebSocket.this.byteBuffer).put(data);
               CloudWebSocket.this.byteBuffer = allocate;
            }

            if (!last) {
               return null;
            } else {
               ByteBuffer flip = CloudWebSocket.this.byteBuffer.flip();
               CloudWebSocket.this.byteBuffer = null;
               return flip;
            }
         }
      }
   }
}
