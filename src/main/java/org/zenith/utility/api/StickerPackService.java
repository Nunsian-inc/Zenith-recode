package org.zenith.utility.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zenith.utility.misc.Coord;

public final class StickerPackService {
   private static final Logger logger = LoggerFactory.getLogger("Zenith/Site");
   private static final long CONST_longValue = 60000L;
   private static final StickerPackService INSTANCE = new StickerPackService();
   private static final long CONST_longValue2 = 3000L;
   private static final Pattern pattern = Pattern.compile("_a([0-9]{1,3})x([0-9]{1,2})$");
   private static final Pattern pattern2 = Pattern.compile("[a-z0-9_-]{1,64}");
   private static final Object obj = new Object();
   private static final Map<String, Object> items = new ConcurrentHashMap<>();
   private static final int CONST_intValue = 1024;
   private final List<StickerPackService.Data> items2 = new CopyOnWriteArrayList<>();
   private volatile String text = "";
   private volatile boolean loading;
   private volatile long longValue;
   private volatile int revision;
   private volatile StickerPackService.Data2 data2 = new StickerPackService.Data2("", new ConcurrentHashMap<>(), new AtomicInteger());

   public static boolean method5503(String string) {
      return string != null && pattern2.matcher(string).matches();
   }

   public static StickerPackService.Data3 method5504(String input) {
      if (input == null) {
         return null;
      } else {
         Object obj2 = items.get(input);
         if (obj2 == null) {
            if (items.size() >= 1024) {
               items.clear();
            }

            StickerPackService.Data3 data3 = method5505(input);
            obj2 = data3 == null ? obj : data3;
            items.put(input, obj2);
         }

         return obj2 == obj ? null : (StickerPackService.Data3)obj2;
      }
   }

   private static StickerPackService.Data3 method5505(String input) {
      Matcher matcher = pattern.matcher(input);
      if (!matcher.find()) {
         return null;
      } else {
         int intValue = Integer.parseInt(matcher.group(1));
         int columns = Integer.parseInt(matcher.group(2));
         return intValue >= 2 && columns >= 1 && columns <= intValue ? new StickerPackService.Data3(intValue, columns, (intValue + columns - 1) / columns) : null;
      }
   }

   private StickerPackService() {
   }

   public static StickerPackService getInstance() {
      return INSTANCE;
   }

   public List<StickerPackService.Data> getItems2() {
      return this.items2;
   }

   public boolean method5506() {
      return !this.items2.isEmpty();
   }

   public int revision() {
      return this.revision;
   }

   public String method5507(String text, String string) {
      String str = this.text;
      if (!str.isEmpty() && text != null && string != null) {
         StickerPackService.Data2 data2 = this.data2;
         if (data2.getText() != str || data2.getAtomicInteger().get() >= 4096) {
            data2 = new StickerPackService.Data2(str, new ConcurrentHashMap<>(), new AtomicInteger());
            this.data2 = data2;
         }

         var map = data2.getItems().get(text);
         String str2 = map == null ? null : (String)map.get(string);
         if (str2 == null) {
            if (!method5503(text) || !method5503(string)) {
               return "";
            }

            if (map == null) {
               map = data2.getItems().computeIfAbsent(text, str3 -> new ConcurrentHashMap<>());
            }

            str2 = str + text + "/" + string + ".png";
            if (map.putIfAbsent(string, str2) == null) {
               data2.getAtomicInteger().incrementAndGet();
            }
         }

         return str2;
      } else {
         return "";
      }
   }

   public void method5508() {
      if (!this.loading && this.items2.isEmpty()) {
         if (System.currentTimeMillis() - this.longValue >= 60000L) {
            this.loading = true;
            this.longValue = System.currentTimeMillis();
            SiteHttpClient.method2412("/chat/stickers", new JsonObject())
               .whenComplete(
                  (jsonObject, throwable) -> {
                     this.loading = false;
                     if (throwable == null) {
                        this.text = method5509(SiteHttpClient.method2417(jsonObject, "base", ""));
                        ArrayList arrayList = new ArrayList();
                        JsonArray jsonArray = SiteHttpClient.method2419(jsonObject, "packs");
                        if (jsonArray != null) {
                           for (JsonElement jsonElement : jsonArray) {
                              if (jsonElement.isJsonObject()) {
                                 JsonObject json = jsonElement.getAsJsonObject();
                                 String string = SiteHttpClient.method2417(json, "id", "");
                                 if (method5503(string)) {
                                    ArrayList coll = new ArrayList();
                                    JsonArray jsonArray2 = SiteHttpClient.method2419(json, "items");
                                    if (jsonArray2 != null) {
                                       for (JsonElement jsonElement2 : jsonArray2) {
                                          if (jsonElement2.isJsonPrimitive() && method5503(jsonElement2.getAsString())) {
                                             coll.add(jsonElement2.getAsString());
                                          }
                                       }
                                    }

                                    if (!coll.isEmpty()) {
                                       String str = SiteHttpClient.method2417(json, "cover", (String)coll.get(0));
                                       arrayList.add(
                                          new StickerPackService.Data(
                                             string,
                                             SiteHttpClient.method2417(json, "name", ""),
                                             method5503(str) ? str : (String)coll.get(0),
                                             List.copyOf(coll)
                                          )
                                       );
                                    }
                                 }
                              }
                           }
                        }

                        this.items2.clear();
                        this.items2.addAll(arrayList);
                        this.revision++;
                     }
                  }
               );
         }
      }
   }

   private static String method5509(String str) {
      if (str != null && !str.isBlank()) {
         try {
            URI uri = URI.create(str.strip());
            if ("https".equalsIgnoreCase(uri.getScheme())
               && uri.getRawUserInfo() == null
               && uri.getRawQuery() == null
               && uri.getRawFragment() == null
               && RegionUtil.method2044(uri.getHost())) {
               String string = uri.toString();
               return string.endsWith("/") ? string : string + "/";
            } else {
               logger.warn("Стикеры: корень адресов с сайта отвергнут: {}", str.length() > 200 ? str.substring(0, 200) + "…" : str);
               return "";
            }
         } catch (RuntimeException e) {
            return "";
         }
      } else {
         return "";
      }
   }

   public static String method5510(String text, String dimension) {
      return Coord.method5171("st").method5177("p", text).method5177("i", dimension).toString();
   }

   public static final class Data {
      private final String text;
      private final String text2;
      private final String text3;
      private final List<String> items;

      public String text() {
         return this.text;
      }

      public String text2() {
         return this.text2;
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
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.text3, other.text3) && java.util.Objects.equals(this.items, other.items);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, text2, text3, items);
      }

      @Override
      public String toString() {
         return "Data[" + "text=" + this.text + ", " + "text2=" + this.text2 + ", " + "text3=" + this.text3 + ", " + "items=" + this.items + "]";
      }

      public Data(String text, String str, String str2, List<String> items) {
         this.text = text;
         this.text2 = str;
         this.text3 = str2;
         this.items = items;
      }

      public String getText() {
         return this.text;
      }

      public String name() {
         return this.text2;
      }

      public String getText3() {
         return this.text3;
      }

      public List<String> getItems() {
         return this.items;
      }
   }

   private static final class Data2 {
      private final String text;
      private final Map<String, Map<String, String>> items;
      private final AtomicInteger atomicInteger;

      public String text() {
         return this.text;
      }

      public Map<String, Map<String, String>> items() {
         return this.items;
      }

      public AtomicInteger atomicInteger() {
         return this.atomicInteger;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data2 other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.items, other.items) && java.util.Objects.equals(this.atomicInteger, other.atomicInteger);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, items, atomicInteger);
      }

      @Override
      public String toString() {
         return "Data2[" + "text=" + this.text + ", " + "items=" + this.items + ", " + "atomicInteger=" + this.atomicInteger + "]";
      }

      private Data2(String text, Map<String, Map<String, String>> items, AtomicInteger atomicInteger) {
         this.text = text;
         this.items = items;
         this.atomicInteger = atomicInteger;
      }

      public String getText() {
         return this.text;
      }

      public Map<String, Map<String, String>> getItems() {
         return this.items;
      }

      public AtomicInteger getAtomicInteger() {
         return this.atomicInteger;
      }
   }

   public static final class Data3 {
      private final int intValue;
      private final int columns;
      private final int intValue2;

      public int intValue() {
         return this.intValue;
      }

      public int columns() {
         return this.columns;
      }

      public int intValue2() {
         return this.intValue2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data3 other)) return false;
         return java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.columns, other.columns) && java.util.Objects.equals(this.intValue2, other.intValue2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(intValue, columns, intValue2);
      }

      @Override
      public String toString() {
         return "Data3[" + "intValue=" + this.intValue + ", " + "columns=" + this.columns + ", " + "intValue2=" + this.intValue2 + "]";
      }

      public Data3(int intValue, int columns, int n) {
         this.intValue = intValue;
         this.columns = columns;
         this.intValue2 = n;
      }

      public int method5511(long now) {
         long l = Math.max(16L, 3000L / Math.max(1, this.intValue));
         return Math.floorMod(now / l, this.intValue);
      }

      public int getIntValue() {
         return this.intValue;
      }

      public int rows() {
         return this.intValue2;
      }
   }
}
