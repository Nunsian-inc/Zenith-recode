package org.zenith.utility.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.zenith.utility.misc.CosmeticType;

public final class CosmeticPartInfo {
   private final String text;
   private final String text2;
   private final String text3;
   private final String text4;
   private final CosmeticType cosmeticType;
   private final CosmeticPartInfo.Type type;
   private final List<String> items;
   private final List<String> items2;
   private final String text5;
   private final int intValue;
   private final CosmeticPartInfo.Data data;
   private final CosmeticPartInfo.Data data2;
   private final String text6;

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

   public List<String> items() {
      return this.items;
   }

   public List<String> items2() {
      return this.items2;
   }

   public String text5() {
      return this.text5;
   }

   public int intValue() {
      return this.intValue;
   }

   public CosmeticPartInfo.Data data() {
      return this.data;
   }

   public CosmeticPartInfo.Data data2() {
      return this.data2;
   }

   public String text6() {
      return this.text6;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof CosmeticPartInfo other)) return false;
      return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.text3, other.text3) && java.util.Objects.equals(this.text4, other.text4) && java.util.Objects.equals(this.cosmeticType, other.cosmeticType) && java.util.Objects.equals(this.type, other.type) && java.util.Objects.equals(this.items, other.items) && java.util.Objects.equals(this.items2, other.items2) && java.util.Objects.equals(this.text5, other.text5) && java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.data, other.data) && java.util.Objects.equals(this.data2, other.data2) && java.util.Objects.equals(this.text6, other.text6);
   }

   @Override
   public int hashCode() {
      return java.util.Objects.hash(text, text2, text3, text4, cosmeticType, type, items, items2, text5, intValue, data, data2, text6);
   }

   @Override
   public String toString() {
      return "CosmeticPartInfo[" + "text=" + this.text + ", " + "text2=" + this.text2 + ", " + "text3=" + this.text3 + ", " + "text4=" + this.text4 + ", " + "cosmeticType=" + this.cosmeticType + ", " + "type=" + this.type + ", " + "items=" + this.items + ", " + "items2=" + this.items2 + ", " + "text5=" + this.text5 + ", " + "intValue=" + this.intValue + ", " + "data=" + this.data + ", " + "data2=" + this.data2 + ", " + "text6=" + this.text6 + "]";
   }

   public CosmeticPartInfo(
      String text,
      String str,
      String str2,
      String str3,
      CosmeticType cosmeticType,
      CosmeticPartInfo.Type type,
      List<String> items,
      List<String> strings,
      String str4,
      int intValue,
      CosmeticPartInfo.Data data,
      CosmeticPartInfo.Data data2,
      String str5
   ) {
      this.text = text;
      this.text2 = str;
      this.text3 = str2;
      this.text4 = str3;
      this.cosmeticType = cosmeticType;
      this.type = type;
      this.items = items;
      this.items2 = strings;
      this.text5 = str4;
      this.intValue = intValue;
      this.data = data;
      this.data2 = data2;
      this.text6 = str5;
   }

   public boolean method6492() {
      return this.cosmeticType.method1682() && this.type != CosmeticPartInfo.Type.UNKNOWN;
   }

   public String method6493() {
      return this.text2 != null && !this.text2.isBlank() ? this.text2 : this.text4;
   }

   public static CosmeticPartInfo method6494(JsonObject json, String str) {
      if (json == null) {
         return null;
      } else {
         String text = method6496(json, "id", "");
         if (text.isBlank()) {
            return null;
         } else {
            JsonObject jsonObject = json.has("locationRotation") && json.get("locationRotation").isJsonObject() ? json.getAsJsonObject("locationRotation") : null;
            return new CosmeticPartInfo(
               text,
               method6496(json, "name", ""),
               method6496(json, "categoryId", ""),
               str,
               CosmeticType.method1684(method6496(json, "type", "")),
               CosmeticPartInfo.Type.method6499(method6496(json, "sourceFormat", "")),
               method6498(json, "conflictGroups"),
               method6498(json, "hidePartBody"),
               method6496(json, "rarity", ""),
               method6497(json, "price"),
               CosmeticPartInfo.Data.method6500(method6495(jsonObject, "normal"), CosmeticPartInfo.Data.INSTANCE),
               CosmeticPartInfo.Data.method6500(method6495(jsonObject, "gui"), CosmeticPartInfo.Data.INSTANCE),
               method6496(json, "hash256", "")
            );
         }
      }
   }

   private static JsonObject method6495(JsonObject jsonObject, String memberName) {
      return jsonObject != null && jsonObject.has(memberName) && jsonObject.get(memberName).isJsonObject() ? jsonObject.getAsJsonObject(memberName) : null;
   }

   private static String method6496(JsonObject json, String memberName, String str) {
      try {
         return json.has(memberName) && json.get(memberName).isJsonPrimitive() ? json.get(memberName).getAsString() : str;
      } catch (RuntimeException e) {
         return str;
      }
   }

   private static int method6497(JsonObject json, String memberName) {
      try {
         return json.has(memberName) && json.get(memberName).isJsonPrimitive() ? json.get(memberName).getAsInt() : 0;
      } catch (RuntimeException e) {
         return 0;
      }
   }

   private static List<String> method6498(JsonObject json, String memberName) {
      ArrayList coll = new ArrayList(2);
      if (!json.has(memberName)) {
         return List.copyOf(coll);
      } else {
         JsonElement jsonElement = json.get(memberName);

         try {
            if (jsonElement.isJsonArray()) {
               for (JsonElement json2 : jsonElement.getAsJsonArray()) {
                  if (json2.isJsonPrimitive()) {
                     coll.add(json2.getAsString());
                  }
               }
            } else if (jsonElement.isJsonPrimitive()) {
               coll.add(jsonElement.getAsString());
            }
         } catch (RuntimeException e) {
         }

         return List.copyOf(coll);
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

   public List<String> getItems() {
      return this.items;
   }

   public List<String> getItems2() {
      return this.items2;
   }

   public String getText5() {
      return this.text5;
   }

   public int getIntValue() {
      return this.intValue;
   }

   public CosmeticPartInfo.Data getData() {
      return this.data;
   }

   public CosmeticPartInfo.Data getData2() {
      return this.data2;
   }

   public String getText6() {
      return this.text6;
   }

   public static final class Data {
      private final float floatValue;
      private final float floatValue2;
      private final float floatValue3;
      private final float floatValue4;
      private final float floatValue5;
      private final float floatValue6;
      private final float floatValue7;
      private final float floatValue8;
      private final float floatValue9;

      public float floatValue() {
         return this.floatValue;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      public float floatValue3() {
         return this.floatValue3;
      }

      public float floatValue4() {
         return this.floatValue4;
      }

      public float floatValue5() {
         return this.floatValue5;
      }

      public float floatValue6() {
         return this.floatValue6;
      }

      public float floatValue7() {
         return this.floatValue7;
      }

      public float floatValue8() {
         return this.floatValue8;
      }

      public float floatValue9() {
         return this.floatValue9;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.floatValue3, other.floatValue3) && java.util.Objects.equals(this.floatValue4, other.floatValue4) && java.util.Objects.equals(this.floatValue5, other.floatValue5) && java.util.Objects.equals(this.floatValue6, other.floatValue6) && java.util.Objects.equals(this.floatValue7, other.floatValue7) && java.util.Objects.equals(this.floatValue8, other.floatValue8) && java.util.Objects.equals(this.floatValue9, other.floatValue9);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(floatValue, floatValue2, floatValue3, floatValue4, floatValue5, floatValue6, floatValue7, floatValue8, floatValue9);
      }

      @Override
      public String toString() {
         return "Data[" + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "floatValue3=" + this.floatValue3 + ", " + "floatValue4=" + this.floatValue4 + ", " + "floatValue5=" + this.floatValue5 + ", " + "floatValue6=" + this.floatValue6 + ", " + "floatValue7=" + this.floatValue7 + ", " + "floatValue8=" + this.floatValue8 + ", " + "floatValue9=" + this.floatValue9 + "]";
      }
      public static final CosmeticPartInfo.Data INSTANCE = new CosmeticPartInfo.Data(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);

      public Data(float floatValue, float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8) {
         this.floatValue = floatValue;
         this.floatValue2 = f;
         this.floatValue3 = f2;
         this.floatValue4 = f3;
         this.floatValue5 = f4;
         this.floatValue6 = f5;
         this.floatValue7 = f6;
         this.floatValue8 = f7;
         this.floatValue9 = f8;
      }

      public static CosmeticPartInfo.Data method6500(JsonObject json, CosmeticPartInfo.Data data) {
         if (json == null) {
            return data;
         } else {
            float[] floats = method6501(json.get("location"), 0.0F);
            float[] floats2 = method6501(json.get("rotation"), 0.0F);
            float[] floats3 = method6501(json.get("scale"), 1.0F);
            return new CosmeticPartInfo.Data(floats[0], floats[1], floats[2], floats2[0], floats2[1], floats2[2], floats3[0], floats3[1], floats3[2]);
         }
      }

      private static float[] method6501(JsonElement jsonElement, float f) {
         float[] floats = new float[]{f, f, f};
         if (jsonElement != null && jsonElement.isJsonArray()) {
            JsonArray jsonArray = jsonElement.getAsJsonArray();

            for (int i = 0; i < 3 && i < jsonArray.size(); i++) {
               try {
                  floats[i] = jsonArray.get(i).getAsFloat();
               } catch (RuntimeException e) {
               }
            }

            return floats;
         } else {
            return floats;
         }
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public float getFloatValue2() {
         return this.floatValue2;
      }

      public float getFloatValue3() {
         return this.floatValue3;
      }

      public float getFloatValue4() {
         return this.floatValue4;
      }

      public float getFloatValue5() {
         return this.floatValue5;
      }

      public float getFloatValue6() {
         return this.floatValue6;
      }

      public float getFloatValue7() {
         return this.floatValue7;
      }

      public float getFloatValue8() {
         return this.floatValue8;
      }

      public float getFloatValue9() {
         return this.floatValue9;
      }
   }

   public static enum Type {
      WAVEFRONT,
      BEDROCK,
      TECHNE,
      UNKNOWN;

      public static CosmeticPartInfo.Type method6499(String str) {
         if (str == null) {
            return UNKNOWN;
         } else {
            String lowerCase = str.strip().toLowerCase(Locale.ROOT);

            return switch (lowerCase) {
               case "wavefront" -> WAVEFRONT;
               case "bedrock" -> BEDROCK;
               case "techne2", "techne" -> TECHNE;
               default -> UNKNOWN;
            };
         }
      }
   }
}
