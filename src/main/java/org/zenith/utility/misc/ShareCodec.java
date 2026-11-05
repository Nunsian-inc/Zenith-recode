package org.zenith.utility.misc;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.zenith.utility.api.AvatarUtil;
import org.zenith.utility.network.CloudPackets;

public final class ShareCodec {
   public static final int CONST_intValue = 63;
   private static final String text = "@fb:";
   private static final String text2 = "@fw:";
   private static final String text3 = "@fp:";
   private static final int CONST_intValue2 = 32;
   private static final long CONST_longValue = 5000L;
   private static volatile Map<String, String> items;
   private static volatile long longValue;
   private static final Map<Long, ShareCodec.Data> items2 = new ConcurrentHashMap<>();

   private ShareCodec() {
   }

   public static List<CloudPackets.Data9> method6721(String str, String str2, String str3, float f) {
      ArrayList arrayList = new ArrayList(3);
      method6722(arrayList, "@fb:", str, 0);
      method6722(arrayList, "@fw:", str2, 0);
      method6722(arrayList, "@fp:", str3, Float.floatToIntBits(f));
      return arrayList;
   }

   private static void method6722(List<CloudPackets.Data9> data9s, String str, String str2, int floatToIntBits) {
      if (str2 != null && !str2.isBlank()) {
         data9s.add(new CloudPackets.Data9(str + method6728(str2), 63, floatToIntBits));
      }
   }

   public static boolean method6723(String text) {
      return !(text == null || !text.startsWith("@fb:") && !text.startsWith("@fw:") && !text.startsWith("@fp:"));
   }

   public static ShareCodec.Data2 method6724(long uid, List<CloudPackets.Data9> items) {
      if (items != null) {
         boolean empty;
         label54: {
            if (items != null) {
               if (items.isEmpty()) {
                  empty = true;
                  break label54;
               }
            }

            empty = items.isEmpty();
         }

         if (!empty) {
            ShareCodec.Data data = items2.get(uid);
            if (data != null && data.getItems() == items) {
               return data.getData2();
            }

            String text = "";
            String str = "";
            String str2 = "";
            float floatValue = 0.4F;
            boolean flag = false;

            for (CloudPackets.Data9 data9 : items) {
               String text2 = data9.getText();
               if (data9.getIntValue() == 63 && text2 != null) {
                  if (text2.startsWith("@fb:")) {
                     text = method6726(text2.substring("@fb:".length()));
                     flag |= text.isEmpty();
                  } else if (text2.startsWith("@fw:")) {
                     str = method6726(text2.substring("@fw:".length()));
                     flag |= str.isEmpty();
                  } else if (text2.startsWith("@fp:")) {
                     str2 = method6726(text2.substring("@fp:".length()));
                     flag |= str2.isEmpty();
                     float intBitsToFloat;
                     floatValue = Float.isFinite(intBitsToFloat = Float.intBitsToFloat(data9.getIntValue2())) ? Math.max(0.1F, Math.min(2.0F, intBitsToFloat)) : 0.4F;
                  }
               }
            }

            ShareCodec.Data2 data2 = new ShareCodec.Data2(text, str, str2, floatValue);
            if (!flag) {
               items2.put(uid, new ShareCodec.Data(items, data2));
            }

            return data2;
         }
      }

      items2.remove(uid);
      return ShareCodec.Data2.INSTANCE;
   }

   public static void method6725(Set<Long> longs) {
      items2.keySet().retainAll(longs);
   }

   private static String method6726(String key) {
      var map = items;
      long now = System.currentTimeMillis();
      if (map != null) {
         boolean containsKey;
         label22: {
            if (map != null) {
               if (map.isEmpty()) {
                  containsKey = false;
                  break label22;
               }
            }

            containsKey = map.containsKey(key);
         }

         if (containsKey || now - longValue < 5000L) {
            return map.getOrDefault(key, "");
         }
      }

      map = method6727();
      longValue = now;
      return map.getOrDefault(key, "");
   }

   private static synchronized Map<String, String> method6727() {
      HashMap map = new HashMap();

      for (AvatarUtil.Data data : AvatarUtil.method6671()) {
         String str = org.zenith.utility.config.AvatarUtil.method7257(data.getPath());
         if (!str.isBlank()) {
            map.put(method6728(str), str);
         }
      }

      items = map;
      return map;
   }

   public static String method6728(String str) {
      try {
         byte[] digest = MessageDigest.getInstance("SHA-256").digest(str.toLowerCase(Locale.ROOT).getBytes(StandardCharsets.UTF_8));
         return HexFormat.of().formatHex(digest).substring(0, 32);
      } catch (NoSuchAlgorithmException e) {
         throw new IllegalStateException("SHA-256 недоступен", e);
      }
   }

   private static final class Data {
      private final List<CloudPackets.Data9> items;
      private final ShareCodec.Data2 data2;

      public List<CloudPackets.Data9> items() {
         return this.items;
      }

      public ShareCodec.Data2 data2() {
         return this.data2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.items, other.items) && java.util.Objects.equals(this.data2, other.data2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(items, data2);
      }

      @Override
      public String toString() {
         return "Data[" + "items=" + this.items + ", " + "data2=" + this.data2 + "]";
      }

      private Data(List<CloudPackets.Data9> items, ShareCodec.Data2 data2) {
         this.items = items;
         this.data2 = data2;
      }

      public List<CloudPackets.Data9> getItems() {
         return this.items;
      }

      public ShareCodec.Data2 getData2() {
         return this.data2;
      }
   }

   public static final class Data2 {
      private final String text;
      private final String text2;
      private final String text3;
      private final float floatValue;

      public String text() {
         return this.text;
      }

      public String text2() {
         return this.text2;
      }

      public String text3() {
         return this.text3;
      }

      public float floatValue() {
         return this.floatValue;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data2 other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.text3, other.text3) && java.util.Objects.equals(this.floatValue, other.floatValue);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, text2, text3, floatValue);
      }

      @Override
      public String toString() {
         return "Data2[" + "text=" + this.text + ", " + "text2=" + this.text2 + ", " + "text3=" + this.text3 + ", " + "floatValue=" + this.floatValue + "]";
      }
      public static final ShareCodec.Data2 INSTANCE = new ShareCodec.Data2("", "", "", 0.4F);

      public Data2(String text, String str, String str2, float floatValue) {
         this.text = text;
         this.text2 = str;
         this.text3 = str2;
         this.floatValue = floatValue;
      }

      public String body() {
         return this.text;
      }

      public String getText2() {
         return this.text2;
      }

      public String getText3() {
         return this.text3;
      }

      public float getFloatValue() {
         return this.floatValue;
      }
   }
}
