package org.zenith.utility.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.zenith.utility.config.SiteSession;

public final class CloudChatClient {
   private static final int CONST_intValue = 200;
   private static final int CONST_intValue2 = 100;
   public static final int CONST_intValue3 = 500;
   private static final long CONST_longValue = 60000L;
   private static final long CONST_longValue2 = 5000L;
   private static final CloudChatClient INSTANCE = new CloudChatClient();
   private volatile List<CloudChatClient.Data3> messages = List.of();
   private volatile List<CloudChatClient.Data> online = List.of();
   private final Object obj = new Object();
   private volatile CloudChatClient.Data2 data2 = new CloudChatClient.Data2(0, 0, 0, 0);
   private volatile CloudChatClient.Data3 data3;
   private volatile long longValue;
   private volatile String text = "";
   private volatile boolean flag;
   private volatile String error = "";
   private volatile int revision;
   private volatile long longValue2;
   private volatile long longValue3;
   private volatile boolean loading;
   private final WebSocketClient webSocketClient = new WebSocketClient("/chat/ws", this::method2170);
   private volatile long longValue4;
   private volatile String owner = "";
   private static final Comparator<CloudChatClient.Data3> comparator = Comparator.comparingLong(CloudChatClient.Data3::getLongValue3)
      .thenComparingLong(CloudChatClient.Data3::getLongValue);

   private CloudChatClient() {
   }

   public static CloudChatClient getInstance() {
      return INSTANCE;
   }

   public List<CloudChatClient.Data3> method2164() {
      List<CloudChatClient.Data3> messages = this.messages;
      boolean flag = false;

      for (CloudChatClient.Data3 message : messages) {
         if (CloudDmClient.method4858(message.name())) {
            flag = true;
            break;
         }
      }

      if (!flag) {
         return messages;
      } else {
         List<Data3> data3s = new ArrayList<>(messages.size());

         for (CloudChatClient.Data3 message : messages) {
            if (!CloudDmClient.method4858(message.name())) {
               data3s.add(message);
            }
         }

         return data3s;
      }
   }

   public void method2165(long l) {
      if (this.longValue4 != 0L && System.currentTimeMillis() - this.longValue4 > l) {
         this.longValue4 = 0L;
         this.webSocketClient.release();
      }
   }

   public List<CloudChatClient.Data> getOnline() {
      return this.online;
   }

   public CloudChatClient.Data2 getData2() {
      return this.data2;
   }

   public CloudChatClient.Data3 getData3() {
      return this.data3;
   }

   public long getLongValue() {
      return this.longValue;
   }

   public String getText() {
      return this.text;
   }

   public boolean isFlag() {
      return this.flag;
   }

   public String error() {
      return this.error;
   }

   public int revision() {
      return this.revision;
   }

   public boolean isLive() {
      return this.webSocketClient.isLive();
   }

   public boolean method2166() {
      return this.longValue2 != 0L;
   }

   public void method2167() {
      String owner = SiteSession.method2289();
      if (!owner.equals(this.owner)) {
         this.owner = owner;
         this.longValue = 0L;
         this.text = "";
         this.flag = false;
         this.longValue3 = 0L;
         this.revision++;
         if (owner.isEmpty()) {
            this.webSocketClient.release();
         }
      }

      if (!owner.isEmpty()) {
         this.webSocketClient.method491();
         long now = System.currentTimeMillis();
         this.longValue4 = now;
         if (!this.loading && now - this.longValue3 > (this.longValue2 == 0L ? 5000L : 60000L)) {
            this.refresh();
         }
      }
   }

   public void release() {
      this.webSocketClient.release();
   }

   public void refresh() {
      if (!this.loading) {
         this.loading = true;
         this.longValue3 = System.currentTimeMillis();
         JsonObject json = new JsonObject();
         json.addProperty("limit", 100);
         SiteHttpClient.method2411("/chat/history", json)
            .whenComplete(
               (var1x, throwable) -> {
                  this.loading = false;
                  if (throwable != null) {
                     this.error = SiteHttpClient.method2424(throwable);
                     this.revision++;
                  } else {
                     this.longValue2 = System.currentTimeMillis();
                     this.error = "";
                     ArrayList arrayList = new ArrayList();
                     JsonArray jsonArray = SiteHttpClient.method2419(var1x, "messages");
                     if (jsonArray != null) {
                        for (JsonElement jsonElement : jsonArray) {
                           if (jsonElement.isJsonObject()) {
                              arrayList.add(method2175(jsonElement.getAsJsonObject()));
                           }
                        }
                     }

                     synchronized (this.obj) {
                        this.messages = method2174(method2173(arrayList, this.messages));
                     }

                     ArrayList coll = new ArrayList();
                     JsonArray jsonArray2 = SiteHttpClient.method2419(var1x, "online");
                     if (jsonArray2 != null) {
                        for (JsonElement jsonElement : jsonArray2) {
                           if (jsonElement.isJsonObject()) {
                              coll.add(method2176(jsonElement.getAsJsonObject()));
                           }
                        }
                     }

                     synchronized (this.obj) {
                        this.online = List.copyOf(coll);
                     }

                     JsonObject jsonObject = SiteHttpClient.method2418(var1x, "stats");
                     if (jsonObject != null) {
                        this.data2 = new CloudChatClient.Data2(
                           SiteHttpClient.method2421(jsonObject, "messages_total", 0),
                           SiteHttpClient.method2421(jsonObject, "messages_today", 0),
                           SiteHttpClient.method2421(jsonObject, "users_total", 0),
                           SiteHttpClient.method2421(jsonObject, "users_today", 0)
                        );
                     }

                     JsonObject jsonObject2 = SiteHttpClient.method2418(var1x, "pinned");
                     this.data3 = jsonObject2 == null ? null : method2175(jsonObject2);
                     JsonObject jsonObject3 = SiteHttpClient.method2418(var1x, "me");
                     if (jsonObject3 != null) {
                        this.longValue = SiteHttpClient.method2421(jsonObject3, "id", 0);
                        this.text = SiteHttpClient.method2417(jsonObject3, "role_label", "");
                        this.flag = SiteHttpClient.method2420(jsonObject3, "is_staff", false);
                     }

                     this.revision++;
                  }
               }
            );
      }
   }

   public CompletableFuture<Void> method2168(String text) {
      String str = text == null ? "" : text.strip();
      if (str.isEmpty()) {
         return CompletableFuture.completedFuture(null);
      } else {
         JsonObject json = new JsonObject();
         json.addProperty("body", str.length() > 500 ? str.substring(0, 500) : str);
         return SiteHttpClient.method2411("/chat/send", json).handle((var1x, var2x) -> {
            if (var2x != null) {
               this.error = SiteHttpClient.method2424(var2x);
               this.revision++;
               throw new SiteHttpClient.Entry2(this.error);
            } else {
               this.error = "";
               if (!this.webSocketClient.isLive()) {
                  JsonObject var3x = SiteHttpClient.method2418(var1x, "message_data");
                  if (var3x != null) {
                     this.method2171(method2175(var3x));
                  }
               }

               return null;
            }
         });
      }
   }

   public CompletableFuture<Void> method2169(long deleteMenuMessage) {
      if (deleteMenuMessage <= 0L) {
         return CompletableFuture.completedFuture(null);
      } else {
         JsonObject json = new JsonObject();
         json.addProperty("message_id", deleteMenuMessage);
         return SiteHttpClient.method2411("/chat/delete", json).handle((var3x, throwable) -> {
            if (throwable != null) {
               this.error = SiteHttpClient.method2424(throwable);
               this.revision++;
               throw new SiteHttpClient.Entry2(this.error);
            } else {
               this.error = "";
               if (!this.webSocketClient.isLive()) {
                  this.method2172(deleteMenuMessage);
                  CloudChatClient.Data3 data3 = this.data3;
                  if (data3 != null && data3.getLongValue() == deleteMenuMessage) {
                     this.data3 = null;
                  }

                  this.revision++;
               }

               return null;
            }
         });
      }
   }

   private void method2170(JsonObject jsonObject) {
      String str = SiteHttpClient.method2417(jsonObject, "type", "");
      switch (str) {
         case "message":
            JsonObject json = SiteHttpClient.method2418(jsonObject, "msg");
            if (json != null) {
               this.method2171(method2175(json));
            }
            break;
         case "delete":
            long deleteMenuMessage = SiteHttpClient.method2421(jsonObject, "message_id", 0);
            this.method2172(deleteMenuMessage);
            CloudChatClient.Data3 data3 = this.data3;
            if (data3 != null && data3.getLongValue() == deleteMenuMessage) {
               this.data3 = null;
            }

            this.revision++;
            break;
         case "pin":
            JsonObject jsonObject2 = SiteHttpClient.method2418(jsonObject, "pinned");
            this.data3 = jsonObject2 == null ? null : method2175(jsonObject2);
            this.revision++;
            break;
         case "join":
            JsonObject jsonObject3 = SiteHttpClient.method2418(jsonObject, "member");
            if (jsonObject3 != null) {
               CloudChatClient.Data data = method2176(jsonObject3);
               synchronized (this.obj) {
                  var coll = new ArrayList<>(this.online);
                  coll.removeIf(var1x -> var1x.getLongValue() == data.getLongValue());
                  coll.add(data);
                  this.online = List.copyOf(coll);
               }

               this.revision++;
            }
            break;
         case "leave":
            long l = SiteHttpClient.method2421(jsonObject, "user_id", 0);
            synchronized (this.obj) {
               var coll = new ArrayList<>(this.online);
               if (coll.removeIf(var2x -> var2x.getLongValue() == l)) {
                  this.online = List.copyOf(coll);
               }
            }

            this.revision++;
      }
   }

   private void method2171(CloudChatClient.Data3 data3) {
      synchronized (this.obj) {
         List<CloudChatClient.Data3> messages = this.messages;

         for (CloudChatClient.Data3 message : messages) {
            if (message.getLongValue() == data3.getLongValue()) {
               return;
            }
         }

         List<Data3> data3s = new ArrayList<>(messages.size() + 1);
         data3s.addAll(messages);
         data3s.add(data3);
         this.messages = method2174(data3s);
      }

      this.revision++;
   }

   private void method2172(long deleteMenuMessage) {
      synchronized (this.obj) {
         var coll = new ArrayList<>(this.messages);
         if (coll.removeIf(data3 -> data3.getLongValue() == deleteMenuMessage)) {
            this.messages = List.copyOf(coll);
         }
      }
   }

   public static List<CloudChatClient.Data3> method2173(List<CloudChatClient.Data3> data3s, List<CloudChatClient.Data3> messages) {
      long l = 0L;
      long l2 = 0L;
      LinkedHashMap map = new LinkedHashMap(data3s.size() + 8);

      for (CloudChatClient.Data3 data3 : data3s) {
         l = Math.max(l, data3.getLongValue());
         l2 = Math.max(l2, data3.getLongValue3());
         map.put(data3.getLongValue(), data3);
      }

      for (CloudChatClient.Data3 message : messages) {
         if (!map.containsKey(message.getLongValue()) && (data3s.isEmpty() || message.getLongValue() > l || message.getLongValue3() > l2)) {
            map.put(message.getLongValue(), message);
         }
      }

      var arrayList = new ArrayList<>(map.values());
      arrayList.sort(comparator);
      return arrayList;
   }

   private static List<CloudChatClient.Data3> method2174(List<CloudChatClient.Data3> data3s) {
      int size = data3s.size();
      return List.copyOf(size > 200 ? data3s.subList(size - 200, size) : data3s);
   }

   private static CloudChatClient.Data3 method2175(JsonObject jsonObject) {
      return new CloudChatClient.Data3(
         SiteHttpClient.method2421(jsonObject, "id", 0),
         SiteHttpClient.method2421(jsonObject, "user_id", 0),
         SiteHttpClient.method2417(jsonObject, "name", "—"),
         SiteHttpClient.method2417(jsonObject, "role", "User"),
         SiteHttpClient.method2417(jsonObject, "avatar_path", null),
         SiteHttpClient.method2417(jsonObject, "body", ""),
         SiteHttpClient.method2422(SiteHttpClient.method2417(jsonObject, "created_at", "")),
         SiteHttpClient.method2420(jsonObject, "pinned", false),
         SiteHttpClient.method2417(jsonObject, "title", "")
      );
   }

   private static CloudChatClient.Data method2176(JsonObject jsonObject) {
      return new CloudChatClient.Data(
         SiteHttpClient.method2421(jsonObject, "user_id", 0),
         SiteHttpClient.method2417(jsonObject, "name", "—"),
         SiteHttpClient.method2417(jsonObject, "role", "User"),
         SiteHttpClient.method2417(jsonObject, "avatar_path", null)
      );
   }

   public static final class Data {
      private final long longValue;
      private final String text;
      private final String text2;
      private final String text3;

      public long longValue() {
         return this.longValue;
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

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.text3, other.text3);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(longValue, text, text2, text3);
      }

      @Override
      public String toString() {
         return "Data[" + "longValue=" + this.longValue + ", " + "text=" + this.text + ", " + "text2=" + this.text2 + ", " + "text3=" + this.text3 + "]";
      }

      public Data(long longValue, String text, String str, String str2) {
         this.longValue = longValue;
         this.text = text;
         this.text2 = str;
         this.text3 = str2;
      }

      public long getLongValue() {
         return this.longValue;
      }

      public String name() {
         return this.text;
      }

      public String role() {
         return this.text2;
      }

      public String getText3() {
         return this.text3;
      }
   }

   public static final class Data2 {
      private final int intValue;
      private final int intValue2;
      private final int intValue3;
      private final int intValue4;

      public int intValue() {
         return this.intValue;
      }

      public int intValue2() {
         return this.intValue2;
      }

      public int intValue3() {
         return this.intValue3;
      }

      public int intValue4() {
         return this.intValue4;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data2 other)) return false;
         return java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.intValue2, other.intValue2) && java.util.Objects.equals(this.intValue3, other.intValue3) && java.util.Objects.equals(this.intValue4, other.intValue4);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(intValue, intValue2, intValue3, intValue4);
      }

      @Override
      public String toString() {
         return "Data2[" + "intValue=" + this.intValue + ", " + "intValue2=" + this.intValue2 + ", " + "intValue3=" + this.intValue3 + ", " + "intValue4=" + this.intValue4 + "]";
      }

      public Data2(int intValue, int n, int n2, int n3) {
         this.intValue = intValue;
         this.intValue2 = n;
         this.intValue3 = n2;
         this.intValue4 = n3;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public int getIntValue2() {
         return this.intValue2;
      }

      public int getIntValue3() {
         return this.intValue3;
      }

      public int getIntValue4() {
         return this.intValue4;
      }
   }

   public static final class Data3 {
      private final long longValue;
      private final long longValue2;
      private final String text;
      private final String text2;
      private final String text3;
      private final String text4;
      private final long longValue3;
      private final boolean flag;
      private final String text5;

      public long longValue() {
         return this.longValue;
      }

      public long longValue2() {
         return this.longValue2;
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

      public long longValue3() {
         return this.longValue3;
      }

      public boolean flag() {
         return this.flag;
      }

      public String text5() {
         return this.text5;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data3 other)) return false;
         return java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.longValue2, other.longValue2) && java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.text3, other.text3) && java.util.Objects.equals(this.text4, other.text4) && java.util.Objects.equals(this.longValue3, other.longValue3) && java.util.Objects.equals(this.flag, other.flag) && java.util.Objects.equals(this.text5, other.text5);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(longValue, longValue2, text, text2, text3, text4, longValue3, flag, text5);
      }

      @Override
      public String toString() {
         return "Data3[" + "longValue=" + this.longValue + ", " + "longValue2=" + this.longValue2 + ", " + "text=" + this.text + ", " + "text2=" + this.text2 + ", " + "text3=" + this.text3 + ", " + "text4=" + this.text4 + ", " + "longValue3=" + this.longValue3 + ", " + "flag=" + this.flag + ", " + "text5=" + this.text5 + "]";
      }

      public Data3(long longValue, long l, String text, String str, String str2, String str3, long l2, boolean flag, String str4) {
         this.longValue = longValue;
         this.longValue2 = l;
         this.text = text;
         this.text2 = str;
         this.text3 = str2;
         this.text4 = str3;
         this.longValue3 = l2;
         this.flag = flag;
         this.text5 = str4;
      }

      public long getLongValue() {
         return this.longValue;
      }

      public long getLongValue2() {
         return this.longValue2;
      }

      public String name() {
         return this.text;
      }

      public String role() {
         return this.text2;
      }

      public String getText3() {
         return this.text3;
      }

      public String body() {
         return this.text4;
      }

      public long getLongValue3() {
         return this.longValue3;
      }

      public boolean isFlag() {
         return this.flag;
      }

      public String title() {
         return this.text5;
      }
   }
}
