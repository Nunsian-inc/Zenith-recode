package org.zenith.utility.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.Minecraft;
import org.zenith.Zenith;
import org.zenith.client.screens.nlgui.NLMenuScreen;
import org.zenith.utility.config.SiteSession;
import org.zenith.utility.network.Cloud;
import org.zenith.utility.render.PartyManager;

public final class CloudDmClient {
   public static final int CONST_intValue = 1000;
   private static final int CONST_intValue2 = 100;
   private static final long CONST_longValue = 20000L;
   private static final long CONST_longValue2 = 30000L;
   private static final CloudDmClient INSTANCE = new CloudDmClient();
   private volatile List<CloudDmClient.Data2> items = List.of();
   private volatile List<CloudDmClient.Data> messages = List.of();
   private final Object obj = new Object();
   private volatile long longValue;
   private volatile String peerName = "";
   private volatile String error = "";
   private volatile int revision;
   private volatile int intValue;
   private volatile long longValue2;
   private volatile boolean flag;
   private volatile boolean flag2;
   private volatile boolean flag3;
   private volatile long longValue3;
   private final WebSocketClient webSocketClient = new WebSocketClient("/dm/ws", this::method4870);
   private volatile long longValue4;
   private volatile String owner = "";
   private volatile int intValue2;
   private static final Comparator<CloudDmClient.Data> comparator = Comparator.comparingLong(CloudDmClient.Data::getLongValue4)
      .thenComparingLong(CloudDmClient.Data::getLongValue);

   private CloudDmClient() {
   }

   public static CloudDmClient getInstance() {
      return INSTANCE;
   }

   public List<CloudDmClient.Data2> method4856() {
      List<CloudDmClient.Data2> items = this.items;
      boolean flag = false;

      for (CloudDmClient.Data2 data2 : items) {
         if (method4858(data2.getText())) {
            flag = true;
            break;
         }
      }

      if (!flag) {
         return items;
      } else {
         List<Data2> data2s = new ArrayList<>(items.size());

         for (CloudDmClient.Data2 data2 : items) {
            if (!method4858(data2.getText())) {
               data2s.add(data2);
            }
         }

         return data2s;
      }
   }

   public List<CloudDmClient.Data> method4857() {
      return method4858(this.peerName) ? List.of() : this.messages;
   }

   public static boolean method4858(String text) {
      if (text != null && !text.isEmpty()) {
         Zenith zenith = Zenith.getInstance();
         Cloud cloud = zenith == null ? null : zenith.getCloud();
         return cloud != null && cloud.prefs().method3316(text);
      } else {
         return false;
      }
   }

   private boolean method4859() {
      String owner = SiteSession.method2289();
      if (this.owner.isEmpty() && !owner.isEmpty()) {
         this.owner = owner;
      } else if (!owner.equals(this.owner)) {
         synchronized (this.obj) {
            this.owner = owner;
            this.intValue2++;
            this.items = List.of();
            this.messages = List.of();
         }

         this.longValue = 0L;
         this.peerName = "";
         this.error = "";
         this.intValue = 0;
         this.longValue2 = 0L;
         this.longValue3 = 0L;
         this.revision++;
         if (owner.isEmpty()) {
            this.webSocketClient.release();
         }
      }

      return !owner.isEmpty();
   }

   public void method4860() {
      if (this.method4859()) {
         this.webSocketClient.method491();
         if (!this.flag && System.currentTimeMillis() - this.longValue2 > 120000L) {
            this.method4864();
         }
      }
   }

   public void method4861(long l) {
      if (this.longValue4 != 0L && System.currentTimeMillis() - this.longValue4 > l) {
         this.longValue4 = 0L;
         this.webSocketClient.release();
      }
   }

   public long getLongValue() {
      return this.longValue;
   }

   public String getPeerName() {
      return this.peerName;
   }

   public boolean hasPeer() {
      return this.longValue != 0L;
   }

   public String error() {
      return this.error;
   }

   public int revision() {
      return this.revision;
   }

   public int getIntValue() {
      return this.intValue;
   }

   public boolean isLive() {
      return this.webSocketClient.isLive();
   }

   public boolean isFlag2() {
      return this.flag2;
   }

   public void method4862() {
      this.error = "";
   }

   public void method4863() {
      if (this.method4859()) {
         this.webSocketClient.method491();
         long now = System.currentTimeMillis();
         this.longValue4 = now;
         if (!this.flag && now - this.longValue2 > 20000L) {
            this.method4864();
         }
      }
   }

   public void release() {
      this.webSocketClient.release();
   }

   public void method4864() {
      if (!this.flag) {
         this.flag = true;
         this.longValue2 = System.currentTimeMillis();
         int n = this.intValue2;
         SiteHttpClient.method2411("/dm/threads", new JsonObject())
            .whenComplete(
               (jsonObject, throwable) -> {
                  this.flag = false;
                  if (n == this.intValue2) {
                     if (throwable != null) {
                        this.error = SiteHttpClient.method2424(throwable);
                        this.revision++;
                     } else {
                        this.error = "";
                        ArrayList coll = new ArrayList();
                        JsonArray jsonArray = SiteHttpClient.method2419(jsonObject, "threads");
                        if (jsonArray != null) {
                           for (JsonElement jsonElement : jsonArray) {
                              if (jsonElement.isJsonObject()) {
                                 JsonObject json = jsonElement.getAsJsonObject();
                                 coll.add(
                                    new CloudDmClient.Data2(
                                       SiteHttpClient.method2421(json, "peer_id", 0),
                                       SiteHttpClient.method2417(json, "peer_name", ""),
                                       SiteHttpClient.method2417(json, "peer_avatar_path", null),
                                       SiteHttpClient.method2417(json, "last_body", ""),
                                       SiteHttpClient.method2422(SiteHttpClient.method2417(json, "last_at", "")),
                                       SiteHttpClient.method2420(json, "last_mine", false),
                                       SiteHttpClient.method2421(json, "unread", 0)
                                    )
                                 );
                              }
                           }
                        }

                        synchronized (this.obj) {
                           if (n != this.intValue2) {
                              return;
                           }

                           this.items = List.copyOf(coll);
                        }

                        this.intValue = SiteHttpClient.method2421(jsonObject, "unread_total", 0);
                        this.revision++;
                     }
                  }
               }
            );
      }
   }

   public void method4865(long longValue, String peerName) {
      if (longValue != this.longValue) {
         this.longValue = longValue;
         this.peerName = peerName == null ? "" : peerName;
         synchronized (this.obj) {
            this.messages = List.of();
         }

         this.revision++;
         if (longValue != 0L) {
            this.method4866(longValue);
         }
      }
   }

   public void close() {
      this.longValue = 0L;
      this.peerName = "";
      synchronized (this.obj) {
         this.messages = List.of();
      }

      this.revision++;
   }

   private void method4866(long longValue) {
      if (!this.flag2) {
         this.flag2 = true;
         JsonObject json = new JsonObject();
         json.addProperty("peer_id", longValue);
         json.addProperty("limit", 100);
         int n = this.intValue2;
         SiteHttpClient.method2411("/dm/history", json).whenComplete((var4x, throwable) -> {
            this.flag2 = false;
            if (n == this.intValue2) {
               long l = this.longValue;
               boolean flag = longValue != l;
               if (throwable != null) {
                  this.error = SiteHttpClient.method2424(throwable);
                  this.revision++;
                  if (flag && l != 0L && this.messages.isEmpty()) {
                     this.method4866(l);
                  }
               } else if (flag) {
                  if (l != 0L && this.messages.isEmpty()) {
                     this.method4866(l);
                  }
               } else {
                  this.error = "";
                  ArrayList arrayList = new ArrayList();
                  JsonArray jsonArray = SiteHttpClient.method2419(var4x, "messages");
                  if (jsonArray != null) {
                     for (JsonElement jsonElement : jsonArray) {
                        if (jsonElement.isJsonObject()) {
                           arrayList.add(method4876(jsonElement.getAsJsonObject()));
                        }
                     }
                  }

                  synchronized (this.obj) {
                     if (n != this.intValue2) {
                        return;
                     }

                     this.messages = List.copyOf(method4873(arrayList, this.messages));
                  }

                  JsonObject jsonObject = SiteHttpClient.method2418(var4x, "peer");
                  if (jsonObject != null) {
                     this.peerName = SiteHttpClient.method2417(jsonObject, "name", this.peerName);
                  }

                  this.method4875(longValue);
                  this.revision++;
               }
            }
         });
      }
   }

   public CompletableFuture<CloudDmClient.Data> method4867(long openPeerUid, String text) {
      String str = text == null ? "" : text.strip();
      if (str.isEmpty()) {
         return CompletableFuture.failedFuture(new SiteHttpClient.Entry2("пустое сообщение"));
      } else {
         JsonObject json = new JsonObject();
         json.addProperty("peer_id", openPeerUid);
         json.addProperty("body", str.length() > 1000 ? str.substring(0, 1000) : str);
         return SiteHttpClient.method2411("/dm/send", json).handle((var3x, var4x) -> {
            if (var4x != null) {
               this.error = SiteHttpClient.method2424(var4x);
               this.revision++;
               throw new SiteHttpClient.Entry2(this.error);
            } else {
               this.error = "";
               CloudDmClient.Data var5x = method4876(SiteHttpClient.method2418(var3x, "message_data"));
               if (!this.webSocketClient.isLive() && openPeerUid == this.longValue) {
                  this.method4872(var5x);
               }

               this.longValue2 = 0L;
               return var5x;
            }
         });
      }
   }

   public CompletableFuture<Integer> method4868() {
      int n = this.intValue2;
      return SiteHttpClient.method2411("/dm/unread", new JsonObject()).thenApply(jsonObject -> {
         int intValue = SiteHttpClient.method2421(jsonObject, "unread_total", 0);
         if (n == this.intValue2) {
            this.intValue = intValue;
         }

         return intValue;
      });
   }

   public int method4869() {
      if (!this.method4859()) {
         return 0;
      } else {
         long now = System.currentTimeMillis();
         if (!this.flag3 && now - this.longValue3 > 30000L) {
            this.flag3 = true;
            this.longValue3 = now;
            this.method4868().whenComplete((var1x, throwable) -> this.flag3 = false);
         }

         return this.intValue;
      }
   }

   private void method4870(JsonObject jsonObject) {
      String str = SiteHttpClient.method2417(jsonObject, "type", "");
      if (str.equals("dm")) {
         JsonObject json = SiteHttpClient.method2418(jsonObject, "msg");
         if (json != null) {
            CloudDmClient.Data data = method4876(json);
            this.method4871(data);
            if (data.getLongValue2() == this.longValue || data.getLongValue3() == this.longValue) {
               this.method4872(data);
            }

            this.longValue2 = 0L;
            this.revision++;
         }
      } else {
         if (str.equals("read")) {
            long l = SiteHttpClient.method2421(jsonObject, "peer_id", 0);
            if (l == this.longValue) {
               this.method4874();
            }

            this.revision++;
         }
      }
   }

   private void method4871(CloudDmClient.Data data) {
      Zenith zenith = Zenith.getInstance();
      if (zenith != null && zenith.getEntry() != null) {
         long l;
         try {
            l = Long.parseLong(zenith.getEntry().getText2().trim());
         } catch (RuntimeException e) {
            return;
         }

         if (data.getLongValue2() != l) {
            Share share = zenith.getShare();
            if (share == null || share.notifyMessages.isEnabled()) {
               PartyManager partyManager = PartyManager.method17();
               if (partyManager == null || !partyManager.method32()) {
                  String text = "";

                  for (CloudDmClient.Data2 data2 : this.items) {
                     if (data2.getLongValue() == data.getLongValue2()) {
                        text = data2.getText();
                        break;
                     }
                  }

                  if (text.isEmpty() && data.getLongValue2() == this.longValue) {
                     text = this.peerName;
                  }

                  if (!method4858(text)) {
                     Minecraft minecraft = Minecraft.getInstance();
                     boolean flag = data.getLongValue2() == this.longValue && minecraft != null && minecraft.gui.screen() instanceof NLMenuScreen;
                     if (!flag) {
                        String body = data.body() == null ? "" : data.body();
                        if (body.contains("[z:")) {
                           body = zenith.getLocalization().translate("cloud.dm.attachment");
                        }

                        String str = ((text.isEmpty() ? zenith.getLocalization().translate("cloud.dm.someone") : text)
                              + ": "
                              + (body.length() > 60 ? body.substring(0, 60) + "…" : body))
                           .replace('§', ' ');
                        if (minecraft != null) {
                           minecraft.execute(() -> Cloud.method7914(str));
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private void method4872(CloudDmClient.Data data) {
      synchronized (this.obj) {
         List<CloudDmClient.Data> messages = this.messages;

         for (CloudDmClient.Data message : messages) {
            if (message.getLongValue() == data.getLongValue()) {
               return;
            }
         }

         List<Data> coll = new ArrayList<>(messages.size() + 1);
         coll.addAll(messages);
         coll.add(data);
         this.messages = List.copyOf(coll);
      }

      this.revision++;
   }

   private static List<CloudDmClient.Data> method4873(List<CloudDmClient.Data> datas, List<CloudDmClient.Data> messages) {
      long l = 0L;
      long l2 = 0L;
      LinkedHashMap map = new LinkedHashMap(datas.size() + 8);

      for (CloudDmClient.Data data : datas) {
         l = Math.max(l, data.getLongValue());
         l2 = Math.max(l2, data.getLongValue4());
         map.put(data.getLongValue(), data);
      }

      for (CloudDmClient.Data message : messages) {
         if (!map.containsKey(message.getLongValue()) && (datas.isEmpty() || message.getLongValue() > l || message.getLongValue4() > l2)) {
            map.put(message.getLongValue(), message);
         }
      }

      var arrayList = new ArrayList<>(map.values());
      arrayList.sort(comparator);
      return arrayList;
   }

   private void method4874() {
      synchronized (this.obj) {
         var messages = this.messages;
         ArrayList coll = null;

         for (int i = 0; i < messages.size(); i++) {
            CloudDmClient.Data data = (CloudDmClient.Data)messages.get(i);
            if (!data.read()) {
               if (coll == null) {
                  coll = new ArrayList(messages);
               }

               coll.set(i, new CloudDmClient.Data(data.getLongValue(), data.getLongValue2(), data.getLongValue3(), data.body(), data.getLongValue4(), true));
            }
         }

         if (coll != null) {
            this.messages = List.copyOf(coll);
         }
      }
   }

   private void method4875(long longValue) {
      synchronized (this.obj) {
         var items = this.items;

         for (int i = 0; i < items.size(); i++) {
            CloudDmClient.Data2 data2 = (CloudDmClient.Data2)items.get(i);
            if (data2.getLongValue() == longValue && data2.getIntValue() > 0) {
               this.intValue = Math.max(0, this.intValue - data2.getIntValue());
               var coll = new ArrayList<>(items);
               coll.set(
                  i, new CloudDmClient.Data2(data2.getLongValue(), data2.getText(), data2.getText2(), data2.getText3(), data2.getLongValue2(), data2.isFlag(), 0)
               );
               this.items = List.copyOf(coll);
               return;
            }
         }
      }
   }

   private static CloudDmClient.Data method4876(JsonObject jsonObject) {
      return jsonObject == null
         ? new CloudDmClient.Data(0L, 0L, 0L, "", 0L, false)
         : new CloudDmClient.Data(
            SiteHttpClient.method2421(jsonObject, "id", 0),
            SiteHttpClient.method2421(jsonObject, "sender_id", 0),
            SiteHttpClient.method2421(jsonObject, "recipient_id", 0),
            SiteHttpClient.method2417(jsonObject, "body", ""),
            SiteHttpClient.method2422(SiteHttpClient.method2417(jsonObject, "created_at", "")),
            jsonObject.has("read_at") && !jsonObject.get("read_at").isJsonNull()
         );
   }

   public static final class Data {
      private final long longValue;
      private final long longValue2;
      private final long longValue3;
      private final String text;
      private final long longValue4;
      private final boolean flag;

      public long longValue() {
         return this.longValue;
      }

      public long longValue2() {
         return this.longValue2;
      }

      public long longValue3() {
         return this.longValue3;
      }

      public String text() {
         return this.text;
      }

      public long longValue4() {
         return this.longValue4;
      }

      public boolean flag() {
         return this.flag;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.longValue2, other.longValue2) && java.util.Objects.equals(this.longValue3, other.longValue3) && java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.longValue4, other.longValue4) && java.util.Objects.equals(this.flag, other.flag);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(longValue, longValue2, longValue3, text, longValue4, flag);
      }

      @Override
      public String toString() {
         return "Data[" + "longValue=" + this.longValue + ", " + "longValue2=" + this.longValue2 + ", " + "longValue3=" + this.longValue3 + ", " + "text=" + this.text + ", " + "longValue4=" + this.longValue4 + ", " + "flag=" + this.flag + "]";
      }

      public Data(long longValue, long l, long l2, String text, long l3, boolean flag) {
         this.longValue = longValue;
         this.longValue2 = l;
         this.longValue3 = l2;
         this.text = text;
         this.longValue4 = l3;
         this.flag = flag;
      }

      public long getLongValue() {
         return this.longValue;
      }

      public long getLongValue2() {
         return this.longValue2;
      }

      public long getLongValue3() {
         return this.longValue3;
      }

      public String body() {
         return this.text;
      }

      public long getLongValue4() {
         return this.longValue4;
      }

      public boolean read() {
         return this.flag;
      }
   }

   public static final class Data2 {
      private final long longValue;
      private final String text;
      private final String text2;
      private final String text3;
      private final long longValue2;
      private final boolean flag;
      private final int intValue;

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

      public long longValue2() {
         return this.longValue2;
      }

      public boolean flag() {
         return this.flag;
      }

      public int intValue() {
         return this.intValue;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data2 other)) return false;
         return java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.text3, other.text3) && java.util.Objects.equals(this.longValue2, other.longValue2) && java.util.Objects.equals(this.flag, other.flag) && java.util.Objects.equals(this.intValue, other.intValue);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(longValue, text, text2, text3, longValue2, flag, intValue);
      }

      @Override
      public String toString() {
         return "Data2[" + "longValue=" + this.longValue + ", " + "text=" + this.text + ", " + "text2=" + this.text2 + ", " + "text3=" + this.text3 + ", " + "longValue2=" + this.longValue2 + ", " + "flag=" + this.flag + ", " + "intValue=" + this.intValue + "]";
      }

      public Data2(long longValue, String text, String str, String str2, long l, boolean flag, int intValue) {
         this.longValue = longValue;
         this.text = text;
         this.text2 = str;
         this.text3 = str2;
         this.longValue2 = l;
         this.flag = flag;
         this.intValue = intValue;
      }

      public long getLongValue() {
         return this.longValue;
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

      public long getLongValue2() {
         return this.longValue2;
      }

      public boolean isFlag() {
         return this.flag;
      }

      public int getIntValue() {
         return this.intValue;
      }
   }
}
