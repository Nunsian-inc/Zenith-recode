package org.zenith.utility.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.zenith.utility.misc.CosmeticType;

public final class CosmeticItemInfo {
   private final String text;
   private final String text2;
   private final String text3;
   private final String text4;
   private final CosmeticType cosmeticType;
   private final CosmeticPartInfo.Type type;
   private final String text5;
   private final int intValue;
   private final String text6;
   private final int intValue2;
   private final String text7;
   private final List<String> items;
   private final List<String> items2;
   private final CosmeticPartInfo.Data data;
   private final CosmeticPartInfo.Data data2;

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

   public CosmeticType cosmeticType() {
      return this.cosmeticType;
   }

   public CosmeticPartInfo.Type type() {
      return this.type;
   }

   public String text5() {
      return this.text5;
   }

   public int intValue() {
      return this.intValue;
   }

   public String text6() {
      return this.text6;
   }

   public int intValue2() {
      return this.intValue2;
   }

   public String text7() {
      return this.text7;
   }

   public List<String> items() {
      return this.items;
   }

   public List<String> items2() {
      return this.items2;
   }

   public CosmeticPartInfo.Data data() {
      return this.data;
   }

   public CosmeticPartInfo.Data data2() {
      return this.data2;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof CosmeticItemInfo other)) return false;
      return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.text3, other.text3) && java.util.Objects.equals(this.text4, other.text4) && java.util.Objects.equals(this.cosmeticType, other.cosmeticType) && java.util.Objects.equals(this.type, other.type) && java.util.Objects.equals(this.text5, other.text5) && java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.text6, other.text6) && java.util.Objects.equals(this.intValue2, other.intValue2) && java.util.Objects.equals(this.text7, other.text7) && java.util.Objects.equals(this.items, other.items) && java.util.Objects.equals(this.items2, other.items2) && java.util.Objects.equals(this.data, other.data) && java.util.Objects.equals(this.data2, other.data2);
   }

   @Override
   public int hashCode() {
      return java.util.Objects.hash(text, text2, text3, text4, cosmeticType, type, text5, intValue, text6, intValue2, text7, items, items2, data, data2);
   }

   @Override
   public String toString() {
      return "CosmeticItemInfo[" + "text=" + this.text + ", " + "text2=" + this.text2 + ", " + "text3=" + this.text3 + ", " + "text4=" + this.text4 + ", " + "cosmeticType=" + this.cosmeticType + ", " + "type=" + this.type + ", " + "text5=" + this.text5 + ", " + "intValue=" + this.intValue + ", " + "text6=" + this.text6 + ", " + "intValue2=" + this.intValue2 + ", " + "text7=" + this.text7 + ", " + "items=" + this.items + ", " + "items2=" + this.items2 + ", " + "data=" + this.data + ", " + "data2=" + this.data2 + "]";
   }
   private static final int CONST_intValue = 9;

   public CosmeticItemInfo(
      String text,
      String str,
      String str2,
      String str3,
      CosmeticType cosmeticType,
      CosmeticPartInfo.Type type,
      String str4,
      int intValue,
      String str5,
      int n,
      String str6,
      List<String> items,
      List<String> strings,
      CosmeticPartInfo.Data data,
      CosmeticPartInfo.Data data2
   ) {
      this.text = text;
      this.text2 = str;
      this.text3 = str2;
      this.text4 = str3;
      this.cosmeticType = cosmeticType;
      this.type = type;
      this.text5 = str4;
      this.intValue = intValue;
      this.text6 = str5;
      this.intValue2 = n;
      this.text7 = str6;
      this.items = items;
      this.items2 = strings;
      this.data = data;
      this.data2 = data2;
   }

   public static CosmeticItemInfo method7748(JsonObject jsonObject) {
      if (jsonObject == null) {
         return null;
      } else {
         String text = method7754(jsonObject, "id");
         String str = method7754(jsonObject, "hash");
         return !text.isBlank() && !str.isBlank()
            ? new CosmeticItemInfo(
               text,
               method7754(jsonObject, "name"),
               method7754(jsonObject, "category"),
               method7754(jsonObject, "categoryId"),
               CosmeticType.method1684(method7754(jsonObject, "type")),
               CosmeticPartInfo.Type.method6499(method7754(jsonObject, "format")),
               method7754(jsonObject, "rarity"),
               method7755(jsonObject, "price"),
               str,
               method7755(jsonObject, "size"),
               method7754(jsonObject, "texture"),
               method7756(jsonObject, "conflictGroups"),
               method7756(jsonObject, "hiddenParts"),
               method7753(jsonObject, "normal"),
               method7753(jsonObject, "gui")
            )
            : null;
      }
   }

   public String method7749() {
      if (this.text2 != null && !this.text2.isBlank()) {
         return this.text2;
      } else {
         return this.text3 != null && !this.text3.isBlank() ? this.text3 : this.text;
      }
   }

   public boolean method7750() {
      return this.cosmeticType.method1682() && this.type != CosmeticPartInfo.Type.UNKNOWN;
   }

   public boolean method7751() {
      if (!this.method7752()) {
         return false;
      } else {
         boolean flag = false;
         boolean flag2 = false;

         for (int i = 0; i < this.items.size(); i++) {
            String str = this.items.get(i);
            if (str != null) {
               String lowerCase = str.toLowerCase(Locale.ROOT);
               flag |= lowerCase.startsWith("left");
               flag2 |= lowerCase.startsWith("right");
            }
         }

         return flag && flag2;
      }
   }

   public boolean method7752() {
      return this.cosmeticType == CosmeticType.LEFT_ARM
         || this.cosmeticType == CosmeticType.RIGHT_ARM
         || this.cosmeticType == CosmeticType.LEFT_LEG
         || this.cosmeticType == CosmeticType.RIGHT_LEG;
   }

   private static CosmeticPartInfo.Data method7753(JsonObject jsonObject, String memberName) {
      if (jsonObject.has(memberName) && jsonObject.get(memberName).isJsonArray()) {
         JsonArray jsonArray = jsonObject.getAsJsonArray(memberName);
         if (jsonArray.size() < 9) {
            return CosmeticPartInfo.Data.INSTANCE;
         } else {
            float[] floats = new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F};

            for (int i = 0; i < 9; i++) {
               try {
                  floats[i] = jsonArray.get(i).getAsFloat();
               } catch (RuntimeException e) {
               }
            }

            return new CosmeticPartInfo.Data(floats[0], floats[1], floats[2], floats[3], floats[4], floats[5], floats[6], floats[7], floats[8]);
         }
      } else {
         return CosmeticPartInfo.Data.INSTANCE;
      }
   }

   private static String method7754(JsonObject jsonObject, String memberName) {
      try {
         return jsonObject.has(memberName) && jsonObject.get(memberName).isJsonPrimitive() ? jsonObject.get(memberName).getAsString() : "";
      } catch (RuntimeException e) {
         return "";
      }
   }

   private static int method7755(JsonObject jsonObject, String memberName) {
      try {
         return jsonObject.has(memberName) && jsonObject.get(memberName).isJsonPrimitive() ? jsonObject.get(memberName).getAsInt() : 0;
      } catch (RuntimeException e) {
         return 0;
      }
   }

   private static List<String> method7756(JsonObject jsonObject, String memberName) {
      if (jsonObject.has(memberName) && jsonObject.get(memberName).isJsonArray()) {
         ArrayList coll = new ArrayList(2);

         for (JsonElement json : jsonObject.getAsJsonArray(memberName)) {
            if (json.isJsonPrimitive()) {
               coll.add(json.getAsString());
            }
         }

         return List.copyOf(coll);
      } else {
         return List.of();
      }
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

   public String getText4() {
      return this.text4;
   }

   public CosmeticType getCosmeticType() {
      return this.cosmeticType;
   }

   public CosmeticPartInfo.Type getType() {
      return this.type;
   }

   public String getText5() {
      return this.text5;
   }

   public int getIntValue() {
      return this.intValue;
   }

   public String getText6() {
      return this.text6;
   }

   public int size() {
      return this.intValue2;
   }

   public String getText7() {
      return this.text7;
   }

   public List<String> getItems() {
      return this.items;
   }

   public List<String> getItems2() {
      return this.items2;
   }

   public CosmeticPartInfo.Data getData() {
      return this.data;
   }

   public CosmeticPartInfo.Data getData2() {
      return this.data2;
   }
}
