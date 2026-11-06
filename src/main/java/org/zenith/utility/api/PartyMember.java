package org.zenith.utility.api;

import com.darkmagician6.eventapi.EventManager;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zenith.MinecraftAccess;
import org.zenith.Zenith;
import org.zenith.client.events.CloudNotificationEvent;
import org.zenith.utility.game.FocusSlotEnum;
import org.zenith.utility.misc.HoveredData;
import org.zenith.utility.misc.PartyPlayerInfo;
import org.zenith.utility.network.Cloud;
import org.zenith.utility.network.CloudPackets;

public final class PartyMember implements MinecraftAccess {
   private static final Logger logger = LoggerFactory.getLogger("Zenith/Cloud");
   public static final int CONST_intValue = 6;
   private static final long CONST_longValue = 300000L;
   private static final int CONST_intValue2 = 8;
   private static final int CONST_intValue3 = 32;
   private static final int CONST_intValue4 = 128;
   private static final int CONST_intValue5 = 64;
   private static final int CONST_intValue6 = 256;
   private static final long CONST_longValue2 = 15000L;
   private final Cloud cloud;
   private volatile PartyMember.Data6 data6;
   private final Map<Long, PartyMember.Data12> items = new LinkedHashMap<>();
   private final Map<Long, PartyMember.Data4> items2 = new LinkedHashMap<>();
   private final Map<Long, PartyMember.Data2> items3 = new ConcurrentHashMap<>();
   private final Map<String, List<String>> items4 = new ConcurrentHashMap<>();
   private final List<PartyMember.Data7> items5 = new ArrayList<>();
   private final List<PartyMember.Data16> items6 = new ArrayList<>();
   private final List<PartyMember.Data13> items7 = new ArrayList<>();
   private volatile PartyMember.Data data;
   private volatile long longValue;
   private volatile PartyMember.Data14 data14;
   private volatile PartyMember.Data10 data10;
   private volatile long longValue2;
   private volatile int intValue;
   private volatile String text = "";
   private volatile PartyMember.Data2 data2 = PartyMember.Data2.INSTANCE;
   private int version;
   private final List<Consumer<PartyMember.Data15>> items8 = new ArrayList<>();
   private final List<Consumer<String>> items9 = new ArrayList<>();
   private volatile long longValue3;
   private volatile long longValue4;
   private long longValue5 = -1L;

   public PartyMember(Cloud cloud, Path path) {
      this.cloud = cloud;
   }

   public boolean method1135() {
      return this.cloud.method7860();
   }

   public int version() {
      return this.version;
   }

   public PartyMember.Data6 getData6() {
      return this.data6;
   }

   public boolean method1136() {
      return this.data6 != null;
   }

   public boolean method1137(long uid) {
      PartyMember.Data6 data6 = this.data6;
      return data6 != null && uid != this.cloud.getLongValue() && data6.method1216(uid) != null;
   }

   public synchronized List<PartyMember.Data12> method1138() {
      return new ArrayList<>(this.items.values());
   }

   public synchronized List<PartyMember.Data4> method1139() {
      var data4s = new ArrayList<>(this.items2.values());
      data4s.sort((data4, var1x) -> Long.compare(var1x.time(), data4.time()));
      return data4s;
   }

   public synchronized PartyMember.Data4 method1140(long death) {
      return this.items2.get(death);
   }

   public PartyMember.Data2 method1141(long uid) {
      return uid != 0L && uid == this.cloud.getLongValue() ? this.data2 : this.items3.getOrDefault(uid, PartyMember.Data2.INSTANCE);
   }

   public boolean method1142(long uid) {
      return this.items3.containsKey(uid);
   }

   public PartyMember.Data2 getData2() {
      return this.data2;
   }

   public List<String> method1143(String str) {
      return str == null ? List.of() : this.items4.getOrDefault(str.toLowerCase(Locale.ROOT), List.of());
   }

   public synchronized List<PartyMember.Data7> method1144() {
      this.method1206();
      return new ArrayList<>(this.items5);
   }

   public synchronized List<PartyMember.Data16> method1145() {
      this.method1206();
      return new ArrayList<>(this.items6);
   }

   public synchronized List<PartyMember.Data13> method1146() {
      this.method1206();
      return new ArrayList<>(this.items7);
   }

   public synchronized int method1147() {
      this.method1206();
      return this.items5.size() + this.items6.size() + this.items7.size();
   }

   public PartyMember.Data getData() {
      return this.data;
   }

   public boolean method1148(long uid) {
      PartyMember.Data data = this.data;
      if (data != null && uid != this.cloud.getLongValue()) {
         for (PartyMember.Data3 data3 : data.getItems()) {
            if (data3.uid() == uid) {
               return !this.cloud.prefs().method3316(data3.username());
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public long getLongValue() {
      return this.longValue;
   }

   public PartyMember.Data14 getData14() {
      return this.data14;
   }

   public PartyMember.Data10 getData10() {
      return this.data10;
   }

   public String getText() {
      return this.text;
   }

   public String method1149(long uid) {
      if (uid == this.cloud.getLongValue()) {
         return Cloud.accountName();
      } else {
         PartyPlayerInfo partyPlayerInfo = this.cloud.state().method8040(uid);
         if (partyPlayerInfo != null) {
            return partyPlayerInfo.username();
         } else {
            PartyMember.Data6 data6 = this.data6;
            if (data6 != null) {
               PartyMember.Data3 data3 = data6.method1216(uid);
               if (data3 != null) {
                  return data3.username();
               }
            }

            return "#" + uid;
         }
      }
   }

   public void method1150() {
      this.version++;
   }

   public synchronized void method1151() {
      this.data6 = null;
      this.items.clear();
      this.items3.clear();
      this.items4.clear();
      this.data = null;
      this.longValue = 0L;
      this.items6.clear();
      this.items5.clear();
      this.items8.clear();
      this.items9.clear();
      this.longValue3 = 0L;
      this.version++;
   }

   public void method1152(String string) {
      JsonObject json = new JsonObject();
      json.addProperty("name", method1211(string, 24));
      this.cloud.method7861(2, json);
   }

   public void method1153(long uid) {
      if (this.data6 == null) {
         if (this.method1135()) {
            this.longValue3 = uid;
            this.longValue4 = System.currentTimeMillis();
            this.method1152("");
         }
      } else {
         this.cloud.method7861(3, method1208(uid));
      }
   }

   public synchronized void method1154(PartyMember.Data7 data7) {
      this.items5.removeIf(var1x -> var1x.getLongValue() == data7.getLongValue());
      this.longValue3 = 0L;
      JsonObject json = new JsonObject();
      json.addProperty("id", data7.getLongValue());
      this.cloud.method7861(4, json);
      this.version++;
   }

   public synchronized void method1155(PartyMember.Data7 data7) {
      this.items5.removeIf(var1x -> var1x.getLongValue() == data7.getLongValue());
      JsonObject json = new JsonObject();
      json.addProperty("id", data7.getLongValue());
      this.cloud.method7861(5, json);
      this.version++;
   }

   public void method1156() {
      this.cloud.method7861(6, new JsonObject());
   }

   public void method1157(long uid) {
      this.cloud.method7861(7, method1208(uid));
   }

   public void method1158(long uid) {
      this.cloud.method7861(8, method1208(uid));
   }

   public void method1159(String string) {
      JsonObject json = new JsonObject();
      json.addProperty("name", method1211(string, 24));
      this.cloud.method7861(9, json);
   }

   public void method1160(long l, String str, String str2, double x, double y, double z, String name, String key, int color) {
      JsonObject json = new JsonObject();
      if (l > 0L) {
         json.addProperty("id", l);
      }

      json.addProperty("server", str);
      json.addProperty("dim", str2);
      json.addProperty("x", x);
      json.addProperty("y", y);
      json.addProperty("z", z);
      json.addProperty("name", method1211(name, 32));
      json.addProperty("icon", key);
      json.addProperty("color", color);
      this.cloud.method7861(10, json);
   }

   public void method1161(long longValue) {
      JsonObject json = new JsonObject();
      json.addProperty("id", longValue);
      this.cloud.method7861(11, json);
   }

   public void method1162(String server, String dimension, double doubleValue, double d, double d2, String string, List<PartyMember.Data11> items, int intValue) {
      JsonObject json = new JsonObject();
      json.addProperty("server", server);
      json.addProperty("dim", dimension);
      json.addProperty("x", doubleValue);
      json.addProperty("y", d);
      json.addProperty("z", d2);
      json.addProperty("cause", method1211(string, 120));
      json.addProperty("xp", intValue);
      JsonArray jsonArray = new JsonArray();

      for (PartyMember.Data11 data11 : items) {
         if (jsonArray.size() >= 64) {
            break;
         }

         JsonArray jsonArray2 = new JsonArray();
         jsonArray2.add(data11.getText());
         jsonArray2.add(data11.count());
         jsonArray2.add(data11.getIntValue2());
         jsonArray2.add(data11.getIntValue3());
         jsonArray2.add(data11.isFlag() ? 1 : 0);
         jsonArray.add(jsonArray2);
      }

      json.add("items", jsonArray);
      this.cloud.method7861(12, json);
   }

   public synchronized void method1163(String server, String dimension, double doubleValue, double d, double d2, String string, List<PartyMember.Data11> items, int intValue) {
      PartyMember.Data4 data4 = new PartyMember.Data4(
         this.longValue5--,
         this.cloud.getLongValue() == 0L ? Cloud.accountUid() : this.cloud.getLongValue(),
         Cloud.accountName(),
         server,
         dimension,
         doubleValue,
         d,
         d2,
         string,
         List.copyOf(items),
         intValue,
         System.currentTimeMillis()
      );
      this.items2.put(data4.getLongValue(), data4);
      this.method1191();
      this.version++;
   }

   public synchronized void method1164(long longValue) {
      PartyMember.Data4 data4 = this.items2.get(longValue);
      if (data4 != null) {
         if (data4.getLongValue() > 0L && data4.uid() == this.cloud.getLongValue() && this.method1135()) {
            JsonObject json = new JsonObject();
            json.addProperty("id", longValue);
            this.cloud.method7861(24, json);
         } else {
            this.items2.remove(longValue);
            this.version++;
         }
      }
   }

   public void method1165(PartyMember.Data2 data2) {
      this.data2 = data2;
      JsonObject json = new JsonObject();
      json.addProperty("status", data2.getText());
      json.addProperty("text", method1211(data2.text(), 48));
      json.addProperty("join", method1211(data2.getText3(), 80));
      json.addProperty("mode", method1211(data2.mode(), 40));
      json.addProperty("server", method1211(data2.server(), 64));
      json.addProperty("since", data2.getSince());
      this.cloud.method7861(1, json);
   }

   public void method1166(Collection<String> strings) {
      JsonObject json = new JsonObject();
      JsonArray jsonArray = new JsonArray();

      for (String string : strings) {
         if (jsonArray.size() < 24) {
            jsonArray.add(string);
         }
      }

      json.add("items", jsonArray);
      this.cloud.method7861(13, json);
   }

   public synchronized void method1167(String string, Collection<String> strings, Consumer<String> consumer) {
      JsonObject json = new JsonObject();
      json.addProperty("name", method1211(string, 32));
      JsonArray jsonArray = new JsonArray();

      for (String str : strings) {
         if (jsonArray.size() < 24) {
            jsonArray.add(str);
         }
      }

      json.add("items", jsonArray);
      if (consumer != null && this.method1135()) {
         this.items9.add(consumer);
      }

      this.cloud.method7861(22, json);
   }

   public synchronized void method1168(String str, Consumer<PartyMember.Data15> consumer) {
      JsonObject json = new JsonObject();
      json.addProperty("code", str.trim().toUpperCase(Locale.ROOT));
      if (consumer != null && this.method1135()) {
         this.items8.add(consumer);
      }

      this.cloud.method7861(23, json);
   }

   public void method1169(int n, int n2) {
      JsonObject json = new JsonObject();
      json.addProperty("kills", n);
      json.addProperty("deaths", n2);
      this.cloud.method7861(14, json);
   }

   public void method1170(String str) {
      JsonObject json = new JsonObject();
      if (str != null && !str.isEmpty()) {
         json.addProperty("week", str);
      }

      this.cloud.method7861(15, json);
   }

   public void method1171(String string) {
      JsonObject json = new JsonObject();
      json.addProperty("name", method1211(string, 24));
      this.cloud.method7861(16, json);
   }

   public synchronized void method1172(String text) {
      String upperCase = text.trim().toUpperCase(Locale.ROOT);
      this.items6.removeIf(var1x -> var1x.getText().equals(upperCase));
      JsonObject json = new JsonObject();
      json.addProperty("code", upperCase);
      this.cloud.method7861(17, json);
      this.version++;
   }

   public synchronized void method1173(PartyMember.Data16 data16) {
      this.items6.remove(data16);
      this.version++;
   }

   public void method1174() {
      this.cloud.method7861(18, new JsonObject());
   }

   public void method1175(long uid) {
      this.cloud.method7861(19, method1208(uid));
   }

   public void method1176(long longValue) {
      this.longValue = longValue;
      this.cloud.method7861(20, method1208(longValue));
      this.version++;
   }

   public void method1177(long uid, String string, String mode, String str, String string2) {
      JsonObject json = method1208(uid);
      json.addProperty("join", method1211(string, 80));
      json.addProperty("mode", method1211(mode, 40));
      json.addProperty("server", method1211(str, 64));
      json.addProperty("note", method1211(string2, 60));
      this.cloud.method7861(21, json);
   }

   public synchronized void method1178(PartyMember.Data13 data13) {
      this.items7.remove(data13);
      this.version++;
   }

   public int getIntValue() {
      return this.intValue;
   }

   public long getLongValue2() {
      return this.longValue2;
   }

   public void method1179(JsonObject json) {
      this.cloud.method7861(50, json);
   }

   public void method1180(CloudPackets.Data18 data18) {
      JsonObject jsonObject = data18.getJsonObject();

      try {
         switch (data18.kind()) {
            case 1:
               this.items3.put(data18.uid(), method1201(jsonObject));
               break;
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 22:
            case 23:
            case 25:
            case 26:
            case 27:
            case 28:
            case 29:
            case 30:
            case 31:
            case 32:
            case 33:
            case 34:
            case 35:
            case 36:
            case 37:
            case 38:
            case 39:
            default:
               return;
            case 10:
               this.method1186(jsonObject);
               break;
            case 11:
               this.method1188(jsonObject);
               break;
            case 12:
               this.method1190(jsonObject);
               break;
            case 13:
               this.method1193(data18, jsonObject);
               break;
            case 21:
               this.method1197(data18, jsonObject);
               break;
            case 24:
               this.method1192(jsonObject);
               break;
            case 40:
               this.method1181(jsonObject);
               break;
            case 41:
               this.method1184(data18, jsonObject);
               break;
            case 42:
               this.method1185(jsonObject);
               break;
            case 43:
               this.method1189(jsonObject);
               break;
            case 44:
               this.data14 = method1204(jsonObject);
               break;
            case 45:
               this.method1195(jsonObject);
               break;
            case 46:
               this.method1196(data18, jsonObject);
               break;
            case 47:
               this.method1198(jsonObject);
               break;
            case 48:
               this.method1199(jsonObject);
               break;
            case 49:
               this.method1200(jsonObject);
            case 50:
               break;
            case 51:
               this.longValue2 = data18.uid();
               this.intValue++;
         }
      } catch (RuntimeException e) {
         logger.warn("Облако: событие {} от {}#{} не разобрано — {}", new Object[]{data18.kind(), data18.username(), data18.uid(), e.toString()});
         return;
      }

      synchronized (this) {
         this.version++;
      }

      EventManager.call(new CloudNotificationEvent(data18));
   }

   private void method1181(JsonObject jsonObject) {
      long longValue = jsonObject.has("id") ? jsonObject.get("id").getAsLong() : 0L;
      if (longValue == 0L) {
         this.data6 = null;
         synchronized (this) {
            this.items.clear();
         }

         this.cloud.state().method8043(Set.of());
      } else {
         ArrayList list = new ArrayList();
         HashSet hashSet = new HashSet();

         for (JsonElement json : jsonObject.getAsJsonArray("members")) {
            JsonObject jsonObject2 = json.getAsJsonObject();
            PartyMember.Data3 data3 = new PartyMember.Data3(
               jsonObject2.get("uid").getAsLong(), method1209(jsonObject2, "name"), method1209(jsonObject2, "mc"), jsonObject2.has("online") && jsonObject2.get("online").getAsBoolean()
            );
            list.add(data3);
            if (data3.uid() != this.cloud.getLongValue() && data3.isFlag()) {
               hashSet.add(data3.uid());
               if (this.cloud.state().method8039(data3.uid()) == null) {
                  this.cloud.state().method8042(data3.uid(), data3.username(), data3.getText2());
               }
            }
         }

         ArrayList arrayList = new ArrayList();
         if (jsonObject.has("invited")) {
            for (JsonElement json : jsonObject.getAsJsonArray("invited")) {
               JsonObject jsonObject2 = json.getAsJsonObject();
               arrayList.add(new PartyMember.Data8(jsonObject2.get("uid").getAsLong(), method1209(jsonObject2, "name")));
            }
         }

         this.cloud.state().method8043(hashSet);
         PartyMember.Data6 data6 = this.data6;
         this.data6 = new PartyMember.Data6(
            longValue,
            method1209(jsonObject, "name"),
            jsonObject.get("leader").getAsLong(),
            jsonObject.has("created") ? jsonObject.get("created").getAsLong() : 0L,
            Collections.unmodifiableList(list),
            Collections.unmodifiableList(arrayList)
         );
         if (data6 == null || data6.getLongValue() != longValue) {
            synchronized (this) {
               this.items5.removeIf(var2x -> var2x.getLongValue() == longValue);
            }

            long uid = this.longValue3;
            if (uid != 0L) {
               this.longValue3 = 0L;
               if (System.currentTimeMillis() - this.longValue4 <= 15000L) {
                  this.cloud.method7861(3, method1208(uid));
               }
            }
         }
      }
   }

   private boolean method1182(CloudPackets.Data18 data18) {
      return this.cloud.prefs().method3316(data18.username());
   }

   private static boolean method1183(long longValue, CloudPackets.Data18 data18) {
      return data18.uid() != 0L && longValue == data18.uid();
   }

   private synchronized void method1184(CloudPackets.Data18 data18, JsonObject jsonObject) {
      long longValue = jsonObject.get("id").getAsLong();
      if (!this.method1182(data18)) {
         this.items5.removeIf(var3x -> var3x.getLongValue() == longValue || method1183(var3x.getLongValue2(), data18));
         ArrayList items = new ArrayList();
         if (jsonObject.has("members")) {
            for (JsonElement json : jsonObject.getAsJsonArray("members")) {
               if (items.size() >= 12) {
                  break;
               }

               items.add(method1211(json.getAsString(), 32));
            }
         }

         this.items5.add(new PartyMember.Data7(longValue, method1209(jsonObject, "name"), data18.uid(), data18.username(), items, System.currentTimeMillis()));
         method1207(this.items5);
      }
   }

   private synchronized void method1185(JsonObject jsonObject) {
      this.items.clear();

      for (JsonElement json : jsonObject.getAsJsonArray("marks")) {
         PartyMember.Data12 data12 = method1202(json.getAsJsonObject());
         this.items.put(data12.getLongValue(), data12);
      }

      this.method1187();
   }

   private synchronized void method1186(JsonObject jsonObject) {
      PartyMember.Data12 data12 = method1202(jsonObject);
      this.items.put(data12.getLongValue(), data12);
      this.method1187();
   }

   private void method1187() {
      var iterator = this.items.keySet().iterator();

      while (this.items.size() > 128 && iterator.hasNext()) {
         iterator.next();
         iterator.remove();
      }
   }

   private synchronized void method1188(JsonObject jsonObject) {
      this.items.remove(jsonObject.get("id").getAsLong());
   }

   private synchronized void method1189(JsonObject jsonObject) {
      this.items2.values().removeIf(data4 -> data4.getLongValue() > 0L);

      for (JsonElement json : jsonObject.getAsJsonArray("deaths")) {
         PartyMember.Data4 data4 = method1203(json.getAsJsonObject());
         this.items2.put(data4.getLongValue(), data4);
      }

      this.method1191();
   }

   private synchronized void method1190(JsonObject jsonObject) {
      PartyMember.Data4 data4 = method1203(jsonObject);
      this.items2.put(data4.getLongValue(), data4);
      this.method1191();
   }

   private void method1191() {
      while (this.items2.size() > 32) {
         PartyMember.Data4 data4 = null;

         for (PartyMember.Data4 data42 : this.items2.values()) {
            if (data4 == null || data42.time() < data4.time()) {
               data4 = data42;
            }
         }

         this.items2.remove(data4.getLongValue());
      }
   }

   private synchronized void method1192(JsonObject jsonObject) {
      this.items2.remove(jsonObject.get("id").getAsLong());
   }

   private void method1193(CloudPackets.Data18 data18, JsonObject jsonObject) {
      String str = method1209(jsonObject, "mc");
      if (str.isEmpty()) {
         logger.warn("Гардероб: образ от {}#{} пришёл без ника Minecraft — не к кому привязать", data18.username(), data18.uid());
      } else {
         List<String> coll = method1210(jsonObject);
         var strings = this.items4.put(str.toLowerCase(Locale.ROOT), List.copyOf(coll));
         Wardrobe wardrobe = Wardrobe.method2488();
         if (wardrobe != null) {
            var map = method1194(wardrobe, coll);
            wardrobe.method2501(str, map);
            if (!coll.equals(strings)) {
               ArrayList arrayList = new ArrayList();

               for (String string : coll) {
                  if (wardrobe.getRegistry().method1421(string) == null) {
                     arrayList.add(string);
                  }
               }

               if (arrayList.isEmpty()) {
                  logger.info("Гардероб: образ {}#{} (ник {}) — {} вещей", new Object[]{data18.username(), data18.uid(), str, map.size()});
               } else {
                  logger.warn(
                     "Гардероб: образ {}#{} (ник {}) — {} вещей, неизвестны реестру: {}", new Object[]{data18.username(), data18.uid(), str, map.size(), arrayList}
                  );
               }
            }
         }
      }
   }

   public static Map<FocusSlotEnum, String> method1194(Wardrobe wardrobe, Collection<String> coll) {
      EnumMap map = new EnumMap<>(FocusSlotEnum.class);

      for (String string : coll) {
         HoveredData hoveredData = wardrobe.getRegistry().method1421(string);
         if (hoveredData != null) {
            map.put(hoveredData.getFocusSlotEnum(), hoveredData.getText());
         }
      }

      return map;
   }

   private void method1195(JsonObject jsonObject) {
      String text = method1209(jsonObject, "code");
      if (text.isEmpty()) {
         this.data = null;
      } else {
         ArrayList list = new ArrayList();

         for (JsonElement json : jsonObject.getAsJsonArray("members")) {
            JsonObject jsonObject2 = json.getAsJsonObject();
            list.add(new PartyMember.Data3(jsonObject2.get("uid").getAsLong(), method1209(jsonObject2, "name"), method1209(jsonObject2, "mc"), true));
         }

         this.data = new PartyMember.Data(text, method1209(jsonObject, "name"), jsonObject.get("owner").getAsLong(), Collections.unmodifiableList(list));
      }
   }

   private synchronized void method1196(CloudPackets.Data18 data18, JsonObject jsonObject) {
      String anObject = method1209(jsonObject, "code");
      if (!this.method1182(data18)) {
         this.items6.removeIf(var2x -> var2x.getText().equals(anObject) || method1183(var2x.getLongValue(), data18));
         this.items6
            .add(
               new PartyMember.Data16(
                  anObject,
                  method1209(jsonObject, "name"),
                  data18.uid(),
                  data18.username(),
                  jsonObject.has("members") ? jsonObject.get("members").getAsInt() : 0,
                  System.currentTimeMillis()
               )
            );
         method1207(this.items6);
      }
   }

   private synchronized void method1197(CloudPackets.Data18 data18, JsonObject jsonObject) {
      if (!this.method1182(data18)) {
         this.items7.removeIf(var1x -> var1x.getLongValue() == data18.uid());
         this.items7
            .add(
               new PartyMember.Data13(
                  data18.uid(),
                  data18.username(),
                  method1209(jsonObject, "mc"),
                  method1209(jsonObject, "join"),
                  method1209(jsonObject, "mode"),
                  method1209(jsonObject, "server"),
                  method1209(jsonObject, "note"),
                  System.currentTimeMillis()
               )
            );
         method1207(this.items7);
      }
   }

   private void method1198(JsonObject jsonObject) {
      String text = method1209(jsonObject, "code");
      this.text = text;
      ArrayList<Consumer> consumers;
      synchronized (this) {
         consumers = new ArrayList<>(this.items9);
         this.items9.clear();
      }

      for (Consumer consumer : consumers) {
         consumer.accept(text);
      }
   }

   private void method1199(JsonObject jsonObject) {
      var coll = method1210(jsonObject);
      PartyMember.Data15 data15 = new PartyMember.Data15(
         method1209(jsonObject, "code"),
         method1209(jsonObject, "name"),
         jsonObject.has("owner") ? jsonObject.get("owner").getAsLong() : 0L,
         method1209(jsonObject, "ownerName"),
         List.copyOf(coll)
      );
      ArrayList<Consumer> consumers;
      synchronized (this) {
         consumers = new ArrayList<>(this.items8);
         this.items8.clear();
      }

      for (Consumer consumer : consumers) {
         consumer.accept(data15);
      }
   }

   private void method1200(JsonObject jsonObject) {
      String anObject = method1209(jsonObject, "code");
      long longValue = jsonObject.has("uid") ? jsonObject.get("uid").getAsLong() : 0L;
      this.data10 = new PartyMember.Data10(anObject, longValue, System.currentTimeMillis());
      if ("look_missing".equals(anObject)) {
         ArrayList<Consumer> consumers;
         synchronized (this) {
            consumers = new ArrayList<>(this.items8);
            this.items8.clear();
         }

         for (Consumer consumer : consumers) {
            consumer.accept(null);
         }
      }

      if ("mate_gone".equals(anObject) && longValue != 0L) {
         PartyMember.Data6 data6 = this.data6;
         if (data6 == null || data6.method1216(longValue) == null) {
            HashSet hashSet = new HashSet();

            for (PartyPlayerInfo partyPlayerInfo : this.cloud.state().method8041()) {
               if (partyPlayerInfo.uid() != longValue) {
                  hashSet.add(partyPlayerInfo.uid());
               }
            }

            this.cloud.state().method8043(hashSet);
         }
      }
   }

   private static PartyMember.Data2 method1201(JsonObject jsonObject) {
      return new PartyMember.Data2(
         method1209(jsonObject, "status"),
         method1209(jsonObject, "text"),
         method1209(jsonObject, "join"),
         method1209(jsonObject, "mode"),
         method1209(jsonObject, "server"),
         jsonObject.has("since") ? jsonObject.get("since").getAsLong() : 0L
      );
   }

   private static PartyMember.Data12 method1202(JsonObject jsonObject) {
      return new PartyMember.Data12(
         jsonObject.get("id").getAsLong(),
         method1209(jsonObject, "server"),
         method1209(jsonObject, "dim"),
         jsonObject.get("x").getAsDouble(),
         jsonObject.get("y").getAsDouble(),
         jsonObject.get("z").getAsDouble(),
         method1209(jsonObject, "name"),
         method1209(jsonObject, "icon"),
         jsonObject.has("color") ? jsonObject.get("color").getAsInt() : -1,
         jsonObject.has("author") ? jsonObject.get("author").getAsLong() : 0L,
         method1209(jsonObject, "authorName"),
         jsonObject.has("time") ? jsonObject.get("time").getAsLong() : 0L
      );
   }

   private static PartyMember.Data4 method1203(JsonObject jsonObject) {
      ArrayList coll = new ArrayList();
      if (jsonObject.has("items")) {
         for (JsonElement json : jsonObject.getAsJsonArray("items")) {
            if (coll.size() >= 64) {
               break;
            }

            JsonArray jsonArray = json.getAsJsonArray();
            coll.add(
               new PartyMember.Data11(
                  method1211(jsonArray.get(0).getAsString(), 256),
                  jsonArray.get(1).getAsInt(),
                  jsonArray.size() > 2 ? jsonArray.get(2).getAsInt() : 0,
                  jsonArray.size() > 3 ? jsonArray.get(3).getAsInt() : 0,
                  jsonArray.size() > 4 && jsonArray.get(4).getAsInt() != 0
               )
            );
         }
      }

      return new PartyMember.Data4(
         jsonObject.get("id").getAsLong(),
         jsonObject.get("uid").getAsLong(),
         method1209(jsonObject, "name"),
         method1209(jsonObject, "server"),
         method1209(jsonObject, "dim"),
         jsonObject.get("x").getAsDouble(),
         jsonObject.get("y").getAsDouble(),
         jsonObject.get("z").getAsDouble(),
         method1209(jsonObject, "cause"),
         List.copyOf(coll),
         jsonObject.has("xp") ? jsonObject.get("xp").getAsInt() : 0,
         jsonObject.has("time") ? jsonObject.get("time").getAsLong() : 0L
      );
   }

   private static PartyMember.Data14 method1204(JsonObject jsonObject) {
      ArrayList coll = new ArrayList();
      if (jsonObject.has("weeks")) {
         for (JsonElement json : jsonObject.getAsJsonArray("weeks")) {
            coll.add(json.getAsString());
         }
      }

      ArrayList arrayList = new ArrayList();

      for (JsonElement json : jsonObject.getAsJsonArray("entries")) {
         JsonObject jsonObject2 = json.getAsJsonObject();
         arrayList.add(
            new PartyMember.Data5(
               jsonObject2.get("uid").getAsLong(),
               method1209(jsonObject2, "name"),
               jsonObject2.has("self") && jsonObject2.get("self").getAsBoolean(),
               jsonObject2.has("online") && jsonObject2.get("online").getAsBoolean(),
               method1205(jsonObject2.getAsJsonObject("servers")),
               method1205(jsonObject2.getAsJsonObject("prev"))
            )
         );
      }

      return new PartyMember.Data14(
         method1209(jsonObject, "week"), method1209(jsonObject, "prevWeek"), method1209(jsonObject, "current"), List.copyOf(coll), List.copyOf(arrayList), System.currentTimeMillis()
      );
   }

   private static Map<String, PartyMember.Data9> method1205(JsonObject jsonObject) {
      LinkedHashMap map = new LinkedHashMap();
      if (jsonObject != null) {
         for (Entry entry : jsonObject.entrySet()) {
            JsonObject json = ((JsonElement)entry.getValue()).getAsJsonObject();
            map.put((String)entry.getKey(), new PartyMember.Data9(json.get("ms").getAsLong(), json.get("kills").getAsLong(), json.get("deaths").getAsLong()));
         }
      }

      return map;
   }

   private synchronized void method1206() {
      long l = System.currentTimeMillis() - 300000L;
      this.items5.removeIf(data7 -> data7.getLongValue3() < l);
      this.items6.removeIf(data16 -> data16.getLongValue2() < l);
      this.items7.removeIf(data13 -> data13.getLongValue2() < l);
   }

   private static <T> void method1207(List<T> list) {
      while (list.size() > 8) {
         list.remove(0);
      }
   }

   private static JsonObject method1208(long uid) {
      JsonObject json = new JsonObject();
      json.addProperty("uid", uid);
      return json;
   }

   public static String method1209(JsonObject jsonObject, String memberName) {
      JsonElement jsonElement = jsonObject.get(memberName);
      if (jsonElement != null && jsonElement.isJsonPrimitive()) {
         String string = jsonElement.getAsString();
         return string.length() > 256 ? string.substring(0, 256) : string;
      } else {
         return "";
      }
   }

   private static List<String> method1210(JsonObject jsonObject) {
      ArrayList<String> strings = new ArrayList<>();

      for (JsonElement json : jsonObject.getAsJsonArray("items")) {
         if (strings.size() >= 64) {
            break;
         }

         strings.add(method1211(json.getAsString(), 256));
      }

      return strings;
   }

   private static String method1211(String string, int endIndex) {
      if (string == null) {
         return "";
      } else {
         String str = string.strip();
         return str.length() > endIndex ? str.substring(0, endIndex) : str;
      }
   }

   public static Zenith method1212() {
      return Zenith.getInstance();
   }

   public static final class Data {
      private final String text;
      private final String text2;
      private final long longValue;
      private final List<PartyMember.Data3> items;

      public String text() {
         return this.text;
      }

      public String text2() {
         return this.text2;
      }

      public long longValue() {
         return this.longValue;
      }

      public List<PartyMember.Data3> items() {
         return this.items;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.items, other.items);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, text2, longValue, items);
      }

      @Override
      public String toString() {
         return "Data[" + "text=" + this.text + ", " + "text2=" + this.text2 + ", " + "longValue=" + this.longValue + ", " + "items=" + this.items + "]";
      }

      public Data(String text, String str, long longValue, List<PartyMember.Data3> items) {
         this.text = text;
         this.text2 = str;
         this.longValue = longValue;
         this.items = items;
      }

      public String getText() {
         return this.text;
      }

      public String name() {
         return this.text2;
      }

      public long getLongValue() {
         return this.longValue;
      }

      public List<PartyMember.Data3> getItems() {
         return this.items;
      }
   }

   public static final class Data10 {
      private final String text;
      private final long longValue;
      private final long longValue2;

      public String text() {
         return this.text;
      }

      public long longValue() {
         return this.longValue;
      }

      public long longValue2() {
         return this.longValue2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data10 other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.longValue2, other.longValue2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, longValue, longValue2);
      }

      @Override
      public String toString() {
         return "Data10[" + "text=" + this.text + ", " + "longValue=" + this.longValue + ", " + "longValue2=" + this.longValue2 + "]";
      }

      public Data10(String text, long longValue, long now) {
         this.text = text;
         this.longValue = longValue;
         this.longValue2 = now;
      }

      public String getText() {
         return this.text;
      }

      public long uid() {
         return this.longValue;
      }

      public long time() {
         return this.longValue2;
      }
   }

   public static final class Data11 {
      private final String text;
      private final int intValue;
      private final int intValue2;
      private final int intValue3;
      private final boolean flag;

      public String text() {
         return this.text;
      }

      public int intValue() {
         return this.intValue;
      }

      public int intValue2() {
         return this.intValue2;
      }

      public int intValue3() {
         return this.intValue3;
      }

      public boolean flag() {
         return this.flag;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data11 other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.intValue2, other.intValue2) && java.util.Objects.equals(this.intValue3, other.intValue3) && java.util.Objects.equals(this.flag, other.flag);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, intValue, intValue2, intValue3, flag);
      }

      @Override
      public String toString() {
         return "Data11[" + "text=" + this.text + ", " + "intValue=" + this.intValue + ", " + "intValue2=" + this.intValue2 + ", " + "intValue3=" + this.intValue3 + ", " + "flag=" + this.flag + "]";
      }

      public Data11(String text, int intValue, int n, int n2, boolean flag) {
         this.text = text;
         this.intValue = intValue;
         this.intValue2 = n;
         this.intValue3 = n2;
         this.flag = flag;
      }

      public String getText() {
         return this.text;
      }

      public int count() {
         return this.intValue;
      }

      public int getIntValue2() {
         return this.intValue2;
      }

      public int getIntValue3() {
         return this.intValue3;
      }

      public boolean isFlag() {
         return this.flag;
      }
   }

   public static final class Data12 {
      private final long longValue;
      private final String text;
      private final String text2;
      private final double doubleValue;
      private final double doubleValue2;
      private final double doubleValue3;
      private final String text3;
      private final String text4;
      private final int color;
      private final long longValue2;
      private final String text5;
      private final long longValue3;

      public long longValue() {
         return this.longValue;
      }

      public String text() {
         return this.text;
      }

      public String text2() {
         return this.text2;
      }

      public double doubleValue() {
         return this.doubleValue;
      }

      public double doubleValue2() {
         return this.doubleValue2;
      }

      public double doubleValue3() {
         return this.doubleValue3;
      }

      public String text3() {
         return this.text3;
      }

      public String text4() {
         return this.text4;
      }

      public int color() {
         return this.color;
      }

      public long longValue2() {
         return this.longValue2;
      }

      public String text5() {
         return this.text5;
      }

      public long longValue3() {
         return this.longValue3;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data12 other)) return false;
         return java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.doubleValue, other.doubleValue) && java.util.Objects.equals(this.doubleValue2, other.doubleValue2) && java.util.Objects.equals(this.doubleValue3, other.doubleValue3) && java.util.Objects.equals(this.text3, other.text3) && java.util.Objects.equals(this.text4, other.text4) && java.util.Objects.equals(this.color, other.color) && java.util.Objects.equals(this.longValue2, other.longValue2) && java.util.Objects.equals(this.text5, other.text5) && java.util.Objects.equals(this.longValue3, other.longValue3);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(longValue, text, text2, doubleValue, doubleValue2, doubleValue3, text3, text4, color, longValue2, text5, longValue3);
      }

      @Override
      public String toString() {
         return "Data12[" + "longValue=" + this.longValue + ", " + "text=" + this.text + ", " + "text2=" + this.text2 + ", " + "doubleValue=" + this.doubleValue + ", " + "doubleValue2=" + this.doubleValue2 + ", " + "doubleValue3=" + this.doubleValue3 + ", " + "text3=" + this.text3 + ", " + "text4=" + this.text4 + ", " + "color=" + this.color + ", " + "longValue2=" + this.longValue2 + ", " + "text5=" + this.text5 + ", " + "longValue3=" + this.longValue3 + "]";
      }

      public Data12(
         long longValue,
         String text,
         String str,
         double doubleValue,
         double d,
         double d2,
         String str2,
         String str3,
         int color,
         long l,
         String str4,
         long l2
      ) {
         this.longValue = longValue;
         this.text = text;
         this.text2 = str;
         this.doubleValue = doubleValue;
         this.doubleValue2 = d;
         this.doubleValue3 = d2;
         this.text3 = str2;
         this.text4 = str3;
         this.color = color;
         this.longValue2 = l;
         this.text5 = str4;
         this.longValue3 = l2;
      }

      public long getLongValue() {
         return this.longValue;
      }

      public String server() {
         return this.text;
      }

      public String dimension() {
         return this.text2;
      }

      public double getDoubleValue() {
         return this.doubleValue;
      }

      public double getDoubleValue2() {
         return this.doubleValue2;
      }

      public double getDoubleValue3() {
         return this.doubleValue3;
      }

      public String name() {
         return this.text3;
      }

      public String getText4() {
         return this.text4;
      }

      public long getLongValue2() {
         return this.longValue2;
      }

      public String getText5() {
         return this.text5;
      }

      public long time() {
         return this.longValue3;
      }
   }

   public static final class Data13 {
      private final long longValue;
      private final String text;
      private final String text2;
      private final String text3;
      private final String text4;
      private final String text5;
      private final String text6;
      private final long longValue2;

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

      public String text4() {
         return this.text4;
      }

      public String text5() {
         return this.text5;
      }

      public String text6() {
         return this.text6;
      }

      public long longValue2() {
         return this.longValue2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data13 other)) return false;
         return java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.text3, other.text3) && java.util.Objects.equals(this.text4, other.text4) && java.util.Objects.equals(this.text5, other.text5) && java.util.Objects.equals(this.text6, other.text6) && java.util.Objects.equals(this.longValue2, other.longValue2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(longValue, text, text2, text3, text4, text5, text6, longValue2);
      }

      @Override
      public String toString() {
         return "Data13[" + "longValue=" + this.longValue + ", " + "text=" + this.text + ", " + "text2=" + this.text2 + ", " + "text3=" + this.text3 + ", " + "text4=" + this.text4 + ", " + "text5=" + this.text5 + ", " + "text6=" + this.text6 + ", " + "longValue2=" + this.longValue2 + "]";
      }

      public Data13(long longValue, String text, String str, String str2, String str3, String str4, String str5, long now) {
         this.longValue = longValue;
         this.text = text;
         this.text2 = str;
         this.text3 = str2;
         this.text4 = str3;
         this.text5 = str4;
         this.text6 = str5;
         this.longValue2 = now;
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

      public String mode() {
         return this.text4;
      }

      public String server() {
         return this.text5;
      }

      public String note() {
         return this.text6;
      }

      public long getLongValue2() {
         return this.longValue2;
      }
   }

   public static final class Data14 {
      private final String text;
      private final String text2;
      private final String current;
      private final List<String> items;
      private final List<PartyMember.Data5> items2;
      private final long longValue;

      public String text() {
         return this.text;
      }

      public String text2() {
         return this.text2;
      }

      public String current() {
         return this.current;
      }

      public List<String> items() {
         return this.items;
      }

      public List<PartyMember.Data5> items2() {
         return this.items2;
      }

      public long longValue() {
         return this.longValue;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data14 other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.current, other.current) && java.util.Objects.equals(this.items, other.items) && java.util.Objects.equals(this.items2, other.items2) && java.util.Objects.equals(this.longValue, other.longValue);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, text2, current, items, items2, longValue);
      }

      @Override
      public String toString() {
         return "Data14[" + "text=" + this.text + ", " + "text2=" + this.text2 + ", " + "current=" + this.current + ", " + "items=" + this.items + ", " + "items2=" + this.items2 + ", " + "longValue=" + this.longValue + "]";
      }

      public Data14(String text, String str, String current, List<String> items, List<PartyMember.Data5> data5s, long longValue) {
         this.text = text;
         this.text2 = str;
         this.current = current;
         this.items = items;
         this.items2 = data5s;
         this.longValue = longValue;
      }

      public PartyMember.Data5 method1219() {
         for (PartyMember.Data5 data5 : this.items2) {
            if (data5.isFlag()) {
               return data5;
            }
         }

         return null;
      }

      public String getText() {
         return this.text;
      }

      public String getText2() {
         return this.text2;
      }

      public String getCurrent() {
         return this.current;
      }

      public List<String> getItems() {
         return this.items;
      }

      public List<PartyMember.Data5> getItems2() {
         return this.items2;
      }

      public long getLongValue() {
         return this.longValue;
      }
   }

   public static final class Data15 {
      private final String text;
      private final String text2;
      private final long longValue;
      private final String text3;
      private final List<String> items;

      public String text() {
         return this.text;
      }

      public String text2() {
         return this.text2;
      }

      public long longValue() {
         return this.longValue;
      }

      public String text3() {
         return this.text3;
      }

      public List<String> items() {
         return this.items;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data15 other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.text3, other.text3) && java.util.Objects.equals(this.items, other.items);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, text2, longValue, text3, items);
      }

      @Override
      public String toString() {
         return "Data15[" + "text=" + this.text + ", " + "text2=" + this.text2 + ", " + "longValue=" + this.longValue + ", " + "text3=" + this.text3 + ", " + "items=" + this.items + "]";
      }

      public Data15(String text, String str, long longValue, String str2, List<String> items) {
         this.text = text;
         this.text2 = str;
         this.longValue = longValue;
         this.text3 = str2;
         this.items = items;
      }

      public String getText() {
         return this.text;
      }

      public String name() {
         return this.text2;
      }

      public long getLongValue() {
         return this.longValue;
      }

      public String getText3() {
         return this.text3;
      }

      public List<String> getItems() {
         return this.items;
      }
   }

   public static final class Data16 {
      private final String text;
      private final String text2;
      private final long longValue;
      private final String text3;
      private final int intValue;
      private final long longValue2;

      public String text() {
         return this.text;
      }

      public String text2() {
         return this.text2;
      }

      public long longValue() {
         return this.longValue;
      }

      public String text3() {
         return this.text3;
      }

      public int intValue() {
         return this.intValue;
      }

      public long longValue2() {
         return this.longValue2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data16 other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.text3, other.text3) && java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.longValue2, other.longValue2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, text2, longValue, text3, intValue, longValue2);
      }

      @Override
      public String toString() {
         return "Data16[" + "text=" + this.text + ", " + "text2=" + this.text2 + ", " + "longValue=" + this.longValue + ", " + "text3=" + this.text3 + ", " + "intValue=" + this.intValue + ", " + "longValue2=" + this.longValue2 + "]";
      }

      public Data16(String text, String str, long longValue, String username, int intValue, long now) {
         this.text = text;
         this.text2 = str;
         this.longValue = longValue;
         this.text3 = username;
         this.intValue = intValue;
         this.longValue2 = now;
      }

      public String getText() {
         return this.text;
      }

      public String name() {
         return this.text2;
      }

      public long getLongValue() {
         return this.longValue;
      }

      public String getText3() {
         return this.text3;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public long getLongValue2() {
         return this.longValue2;
      }
   }

   public static final class Data2 {
      private final String text;
      private final String text2;
      private final String text3;
      private final String text4;
      private final String text5;
      private final long since;

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

      public long since() {
         return this.since;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data2 other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.text3, other.text3) && java.util.Objects.equals(this.text4, other.text4) && java.util.Objects.equals(this.text5, other.text5) && java.util.Objects.equals(this.since, other.since);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, text2, text3, text4, text5, since);
      }

      @Override
      public String toString() {
         return "Data2[" + "text=" + this.text + ", " + "text2=" + this.text2 + ", " + "text3=" + this.text3 + ", " + "text4=" + this.text4 + ", " + "text5=" + this.text5 + ", " + "since=" + this.since + "]";
      }
      public static final PartyMember.Data2 INSTANCE = new PartyMember.Data2("online", "", "", "", "", 0L);

      public Data2(String text, String str, String str2, String mode, String str3, long since) {
         this.text = text;
         this.text2 = str;
         this.text3 = str2;
         this.text4 = mode;
         this.text5 = str3;
         this.since = since;
      }

      public String getText() {
         return this.text;
      }

      public String text() {
         return this.text2;
      }

      public String getText3() {
         return this.text3;
      }

      public String mode() {
         return this.text4;
      }

      public String server() {
         return this.text5;
      }

      public long getSince() {
         return this.since;
      }
   }

   public static final class Data3 {
      private final long longValue;
      private final String text;
      private final String text2;
      private final boolean flag;

      public long longValue() {
         return this.longValue;
      }

      public String text() {
         return this.text;
      }

      public String text2() {
         return this.text2;
      }

      public boolean flag() {
         return this.flag;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data3 other)) return false;
         return java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.flag, other.flag);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(longValue, text, text2, flag);
      }

      @Override
      public String toString() {
         return "Data3[" + "longValue=" + this.longValue + ", " + "text=" + this.text + ", " + "text2=" + this.text2 + ", " + "flag=" + this.flag + "]";
      }

      public Data3(long longValue, String text, String str, boolean flag) {
         this.longValue = longValue;
         this.text = text;
         this.text2 = str;
         this.flag = flag;
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

      public boolean isFlag() {
         return this.flag;
      }
   }

   public static final class Data4 {
      private final long longValue;
      private final long longValue2;
      private final String text;
      private final String text2;
      private final String text3;
      private final double doubleValue;
      private final double doubleValue2;
      private final double doubleValue3;
      private final String text4;
      private final List<PartyMember.Data11> items;
      private final int intValue;
      private final long longValue3;

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

      public double doubleValue() {
         return this.doubleValue;
      }

      public double doubleValue2() {
         return this.doubleValue2;
      }

      public double doubleValue3() {
         return this.doubleValue3;
      }

      public String text4() {
         return this.text4;
      }

      public List<PartyMember.Data11> items() {
         return this.items;
      }

      public int intValue() {
         return this.intValue;
      }

      public long longValue3() {
         return this.longValue3;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data4 other)) return false;
         return java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.longValue2, other.longValue2) && java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.text3, other.text3) && java.util.Objects.equals(this.doubleValue, other.doubleValue) && java.util.Objects.equals(this.doubleValue2, other.doubleValue2) && java.util.Objects.equals(this.doubleValue3, other.doubleValue3) && java.util.Objects.equals(this.text4, other.text4) && java.util.Objects.equals(this.items, other.items) && java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.longValue3, other.longValue3);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(longValue, longValue2, text, text2, text3, doubleValue, doubleValue2, doubleValue3, text4, items, intValue, longValue3);
      }

      @Override
      public String toString() {
         return "Data4[" + "longValue=" + this.longValue + ", " + "longValue2=" + this.longValue2 + ", " + "text=" + this.text + ", " + "text2=" + this.text2 + ", " + "text3=" + this.text3 + ", " + "doubleValue=" + this.doubleValue + ", " + "doubleValue2=" + this.doubleValue2 + ", " + "doubleValue3=" + this.doubleValue3 + ", " + "text4=" + this.text4 + ", " + "items=" + this.items + ", " + "intValue=" + this.intValue + ", " + "longValue3=" + this.longValue3 + "]";
      }

      public Data4(
         long longValue,
         long accountUid,
         String text,
         String server,
         String dimension,
         double doubleValue,
         double d,
         double d2,
         String string,
         List<PartyMember.Data11> items,
         int intValue,
         long now
      ) {
         this.longValue = longValue;
         this.longValue2 = accountUid;
         this.text = text;
         this.text2 = server;
         this.text3 = dimension;
         this.doubleValue = doubleValue;
         this.doubleValue2 = d;
         this.doubleValue3 = d2;
         this.text4 = string;
         this.items = items;
         this.intValue = intValue;
         this.longValue3 = now;
      }

      public long getLongValue() {
         return this.longValue;
      }

      public long uid() {
         return this.longValue2;
      }

      public String name() {
         return this.text;
      }

      public String server() {
         return this.text2;
      }

      public String dimension() {
         return this.text3;
      }

      public double getDoubleValue() {
         return this.doubleValue;
      }

      public double getDoubleValue2() {
         return this.doubleValue2;
      }

      public double getDoubleValue3() {
         return this.doubleValue3;
      }

      public String getText4() {
         return this.text4;
      }

      public List<PartyMember.Data11> getItems() {
         return this.items;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public long time() {
         return this.longValue3;
      }
   }

   public static final class Data5 {
      private final long longValue;
      private final String text;
      private final boolean flag;
      private final boolean flag2;
      private final Map<String, PartyMember.Data9> items;
      private final Map<String, PartyMember.Data9> items2;

      public long longValue() {
         return this.longValue;
      }

      public String text() {
         return this.text;
      }

      public boolean flag() {
         return this.flag;
      }

      public boolean flag2() {
         return this.flag2;
      }

      public Map<String, PartyMember.Data9> items() {
         return this.items;
      }

      public Map<String, PartyMember.Data9> items2() {
         return this.items2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data5 other)) return false;
         return java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.flag, other.flag) && java.util.Objects.equals(this.flag2, other.flag2) && java.util.Objects.equals(this.items, other.items) && java.util.Objects.equals(this.items2, other.items2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(longValue, text, flag, flag2, items, items2);
      }

      @Override
      public String toString() {
         return "Data5[" + "longValue=" + this.longValue + ", " + "text=" + this.text + ", " + "flag=" + this.flag + ", " + "flag2=" + this.flag2 + ", " + "items=" + this.items + ", " + "items2=" + this.items2 + "]";
      }

      public Data5(long longValue, String text, boolean flag, boolean flag2, Map<String, PartyMember.Data9> items, Map<String, PartyMember.Data9> map) {
         this.longValue = longValue;
         this.text = text;
         this.flag = flag;
         this.flag2 = flag2;
         this.items = items;
         this.items2 = map;
      }

      public PartyMember.Data9 method1213() {
         PartyMember.Data9 data9 = PartyMember.Data9.INSTANCE;

         for (PartyMember.Data9 data92 : this.items.values()) {
            data9 = data9.method1218(data92);
         }

         return data9;
      }

      public PartyMember.Data9 method1214() {
         PartyMember.Data9 data9 = PartyMember.Data9.INSTANCE;

         for (PartyMember.Data9 data92 : this.items2.values()) {
            data9 = data9.method1218(data92);
         }

         return data9;
      }

      public long uid() {
         return this.longValue;
      }

      public String name() {
         return this.text;
      }

      public boolean isFlag() {
         return this.flag;
      }

      public boolean isFlag2() {
         return this.flag2;
      }

      public Map<String, PartyMember.Data9> getItems() {
         return this.items;
      }

      public Map<String, PartyMember.Data9> getItems2() {
         return this.items2;
      }
   }

   public static final class Data6 {
      private final long longValue;
      private final String text;
      private final long longValue2;
      private final long longValue3;
      private final List<PartyMember.Data3> items;
      private final List<PartyMember.Data8> items2;

      public long longValue() {
         return this.longValue;
      }

      public String text() {
         return this.text;
      }

      public long longValue2() {
         return this.longValue2;
      }

      public long longValue3() {
         return this.longValue3;
      }

      public List<PartyMember.Data3> items() {
         return this.items;
      }

      public List<PartyMember.Data8> items2() {
         return this.items2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data6 other)) return false;
         return java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.longValue2, other.longValue2) && java.util.Objects.equals(this.longValue3, other.longValue3) && java.util.Objects.equals(this.items, other.items) && java.util.Objects.equals(this.items2, other.items2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(longValue, text, longValue2, longValue3, items, items2);
      }

      @Override
      public String toString() {
         return "Data6[" + "longValue=" + this.longValue + ", " + "text=" + this.text + ", " + "longValue2=" + this.longValue2 + ", " + "longValue3=" + this.longValue3 + ", " + "items=" + this.items + ", " + "items2=" + this.items2 + "]";
      }

      public Data6(long longValue, String text, long l, long l2, List<PartyMember.Data3> items, List<PartyMember.Data8> unmodifiableList) {
         this.longValue = longValue;
         this.text = text;
         this.longValue2 = l;
         this.longValue3 = l2;
         this.items = items;
         this.items2 = unmodifiableList;
      }

      public boolean method1215(long uid) {
         return this.longValue2 == uid;
      }

      public PartyMember.Data3 method1216(long uid) {
         for (PartyMember.Data3 data3 : this.items) {
            if (data3.uid() == uid) {
               return data3;
            }
         }

         return null;
      }

      public int method1217() {
         int n = 0;

         for (PartyMember.Data3 data3 : this.items) {
            if (data3.isFlag()) {
               n++;
            }
         }

         return n;
      }

      public long getLongValue() {
         return this.longValue;
      }

      public String name() {
         return this.text;
      }

      public long getLongValue2() {
         return this.longValue2;
      }

      public long getLongValue3() {
         return this.longValue3;
      }

      public List<PartyMember.Data3> getItems() {
         return this.items;
      }

      public List<PartyMember.Data8> getItems2() {
         return this.items2;
      }
   }

   public static final class Data7 {
      private final long longValue;
      private final String text;
      private final long longValue2;
      private final String text2;
      private final List<String> items;
      private final long longValue3;

      public long longValue() {
         return this.longValue;
      }

      public String text() {
         return this.text;
      }

      public long longValue2() {
         return this.longValue2;
      }

      public String text2() {
         return this.text2;
      }

      public List<String> items() {
         return this.items;
      }

      public long longValue3() {
         return this.longValue3;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data7 other)) return false;
         return java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.longValue2, other.longValue2) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.items, other.items) && java.util.Objects.equals(this.longValue3, other.longValue3);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(longValue, text, longValue2, text2, items, longValue3);
      }

      @Override
      public String toString() {
         return "Data7[" + "longValue=" + this.longValue + ", " + "text=" + this.text + ", " + "longValue2=" + this.longValue2 + ", " + "text2=" + this.text2 + ", " + "items=" + this.items + ", " + "longValue3=" + this.longValue3 + "]";
      }

      public Data7(long longValue, String text, long uid, String username, List<String> items, long now) {
         this.longValue = longValue;
         this.text = text;
         this.longValue2 = uid;
         this.text2 = username;
         this.items = items;
         this.longValue3 = now;
      }

      public long getLongValue() {
         return this.longValue;
      }

      public String name() {
         return this.text;
      }

      public long getLongValue2() {
         return this.longValue2;
      }

      public String getText2() {
         return this.text2;
      }

      public List<String> getItems() {
         return this.items;
      }

      public long getLongValue3() {
         return this.longValue3;
      }
   }

   public static final class Data8 {
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
         if (!(o instanceof Data8 other)) return false;
         return java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.text, other.text);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(longValue, text);
      }

      @Override
      public String toString() {
         return "Data8[" + "longValue=" + this.longValue + ", " + "text=" + this.text + "]";
      }

      public Data8(long longValue, String text) {
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

   public static final class Data9 {
      private final long longValue;
      private final long longValue2;
      private final long longValue3;

      public long longValue() {
         return this.longValue;
      }

      public long longValue2() {
         return this.longValue2;
      }

      public long longValue3() {
         return this.longValue3;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data9 other)) return false;
         return java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.longValue2, other.longValue2) && java.util.Objects.equals(this.longValue3, other.longValue3);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(longValue, longValue2, longValue3);
      }

      @Override
      public String toString() {
         return "Data9[" + "longValue=" + this.longValue + ", " + "longValue2=" + this.longValue2 + ", " + "longValue3=" + this.longValue3 + "]";
      }
      public static final PartyMember.Data9 INSTANCE = new PartyMember.Data9(0L, 0L, 0L);

      public Data9(long longValue, long l, long l2) {
         this.longValue = longValue;
         this.longValue2 = l;
         this.longValue3 = l2;
      }

      public PartyMember.Data9 method1218(PartyMember.Data9 data9) {
         return new PartyMember.Data9(this.longValue + data9.longValue, this.longValue2 + data9.longValue2, this.longValue3 + data9.longValue3);
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
   }
}
