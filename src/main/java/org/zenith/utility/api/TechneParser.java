package org.zenith.utility.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import org.zenith.utility.misc.CosmeticSkeleton;

public final class TechneParser {
   private static final float CONST_floatValue = 16.0F;

   private TechneParser() {
   }

   public static CosmeticSkeleton method7430(String json) {
      return method7431(json, null);
   }

   public static CosmeticSkeleton method7431(String json, String str) {
      CosmeticSkeleton.Entry entry = new CosmeticSkeleton.Entry();
      JsonObject json2 = null;

      try {
         json2 = method7439(JsonParser.parseString(json).getAsJsonObject(), "Techne");
      } catch (RuntimeException e) {
      }

      if (json2 == null) {
         return entry.method7152();
      } else {
         HashSet hashSet = new HashSet();
         int n = 0;

         for (JsonElement jsonElement : method7437(json2.get("Models"))) {
            if (jsonElement.isJsonObject()) {
               JsonObject jsonObject = jsonElement.getAsJsonObject();
               JsonObject json3 = method7439(jsonObject, "Model");
               method7432(entry, json3 == null ? jsonObject : json3, str, hashSet, n++);
            }
         }

         return entry.method7152();
      }
   }

   private static void method7432(CosmeticSkeleton.Entry entry, JsonObject jsonObject, String str, Set<String> strings, int n) {
      String str2 = str != null && !str.isBlank() ? str : method7440(jsonObject, "@texture", "");
      int n2 = entry.method7147(str2);
      float[] floats = method7441(method7440(jsonObject, "TextureSize", ""), 2, 0.0F);
      float f = floats[0] > 0.0F ? floats[0] : 64.0F;
      float f2 = floats[1] > 0.0F ? floats[1] : 32.0F;
      float[] floats2 = method7441(method7440(jsonObject, "GlScale", ""), 3, 1.0F);

      for (int i = 0; i < 3; i++) {
         if (floats2[i] == 0.0F) {
            floats2[i] = 1.0F;
         }
      }

      int intValue = entry.method7146(method7438(strings, method7440(jsonObject, "Name", "model" + n)), -1, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
      JsonObject json = method7439(jsonObject, "Geometry");
      if (json != null) {
         for (JsonElement jsonElement : method7437(json.get("Shape"))) {
            if (jsonElement.isJsonObject()) {
               method7433(entry, intValue, n2, jsonElement.getAsJsonObject(), strings, floats2, f, f2);
            }
         }
      }
   }

   private static void method7433(CosmeticSkeleton.Entry entry, int intValue, int n, JsonObject jsonObject, Set<String> strings, float[] floats, float f, float f2) {
      float[] floats2 = method7441(method7440(jsonObject, "Position", ""), 3, 0.0F);
      float[] floats3 = method7441(method7440(jsonObject, "Offset", ""), 3, 0.0F);
      float[] floats4 = method7441(method7440(jsonObject, "Size", ""), 3, 0.0F);
      float[] floats5 = method7441(method7440(jsonObject, "Rotation", ""), 3, 0.0F);
      float[] floats6 = method7441(method7440(jsonObject, "TextureOffset", ""), 2, 0.0F);
      boolean flag = "true".equals(method7440(jsonObject, "IsMirrored", "False").strip().toLowerCase(Locale.ROOT));
      int intValue2 = entry.method7146(
         method7438(strings, method7440(jsonObject, "@Name", "shape")),
         intValue,
         method7436(floats2[0], floats[0]),
         method7436(floats2[1], floats[1]),
         method7436(floats2[2], floats[2]),
         (float)(-Math.toDegrees(floats5[0])),
         (float)Math.toDegrees(floats5[1]),
         (float)(-Math.toDegrees(floats5[2]))
      );
      float[] floats7 = new float[3];
      float[] floats8 = new float[3];

      for (int i = 0; i < 3; i++) {
         float f3 = method7436(floats2[i] + floats3[i], floats[i]);
         float f4 = method7436(floats2[i] + floats3[i] + floats4[i], floats[i]);
         floats7[i] = Math.min(f3, f4);
         floats8[i] = Math.max(f3, f4);
      }

      float f3 = floats7[0];
      float f4 = floats7[1];
      float f5 = floats7[2];
      float f6 = floats8[0];
      float f7 = floats8[1];
      float f8 = floats8[2];
      float[][] floatses = new float[][]{
         {f3, f4, f5},
         {f6, f4, f5},
         {f6, f7, f5},
         {f3, f7, f5},
         {f3, f4, f8},
         {f6, f4, f8},
         {f6, f7, f8},
         {f3, f7, f8}
      };
      method7434(entry, intValue2, n, floatses, 5, 4, 7, 6, method7435(floats6, floats4, flag, 0, f, f2));
      method7434(entry, intValue2, n, floatses, 0, 1, 2, 3, method7435(floats6, floats4, flag, 1, f, f2));
      method7434(entry, intValue2, n, floatses, 4, 0, 3, 7, method7435(floats6, floats4, flag, 2, f, f2));
      method7434(entry, intValue2, n, floatses, 1, 5, 6, 2, method7435(floats6, floats4, flag, 3, f, f2));
      method7434(entry, intValue2, n, floatses, 3, 2, 6, 7, method7435(floats6, floats4, flag, 4, f, f2));
      method7434(entry, intValue2, n, floatses, 4, 5, 1, 0, method7435(floats6, floats4, flag, 5, f, f2));
   }

   private static void method7434(CosmeticSkeleton.Entry entry, int intValue, int n, float[][] floatses, int n2, int n3, int n4, int n5, float[] floatValues2) {
      int[] ints = new int[]{n2, n3, n4, n5};
      float[] floatValues = new float[12];

      for (int i = 0; i < 4; i++) {
         floatValues[i * 3] = floatses[ints[i]][0];
         floatValues[i * 3 + 1] = floatses[ints[i]][1];
         floatValues[i * 3 + 2] = floatses[ints[i]][2];
      }

      entry.method7148(intValue, n, floatValues, floatValues2, null);
   }

   private static float[] method7435(float[] floats, float[] floats2, boolean flag, int n, float f, float f2) {
      float f3 = floats2[0];
      float f4 = floats2[1];
      float f5 = floats2[2];
      float f6;
      float f7;
      float f8;
      float f9;
      switch (n) {
         case 0:
            f6 = floats[0] + f5 + f3 + f5;
            f7 = floats[1] + f5;
            f8 = f3;
            f9 = f4;
            break;
         case 1:
            f6 = floats[0] + f5;
            f7 = floats[1] + f5;
            f8 = f3;
            f9 = f4;
            break;
         case 2:
            f6 = floats[0];
            f7 = floats[1] + f5;
            f8 = f5;
            f9 = f4;
            break;
         case 3:
            f6 = floats[0] + f5 + f3;
            f7 = floats[1] + f5;
            f8 = f5;
            f9 = f4;
            break;
         case 4:
            f6 = floats[0] + f5;
            f7 = floats[1];
            f8 = f3;
            f9 = f5;
            break;
         default:
            f6 = floats[0] + f5 + f3;
            f7 = floats[1];
            f8 = f3;
            f9 = f5;
      }

      float f10 = f6 / f;
      float f11 = f7 / f2;
      float f12 = (f6 + f8) / f;
      float f13 = (f7 + f9) / f2;
      if (flag) {
         float f14 = f10;
         f10 = f12;
         f12 = f14;
      }

      return new float[]{f10, f13, f12, f13, f12, f11, f10, f11};
   }

   private static float method7436(float f, float f2) {
      return -f * f2 / 16.0F;
   }

   private static JsonArray method7437(JsonElement jsonElement) {
      if (jsonElement == null || jsonElement.isJsonNull()) {
         return new JsonArray();
      } else if (jsonElement.isJsonArray()) {
         return jsonElement.getAsJsonArray();
      } else {
         JsonArray jsonArray = new JsonArray(1);
         jsonArray.add(jsonElement);
         return jsonArray;
      }
   }

   private static String method7438(Set<String> strings, String str) {
      String str2 = str != null && !str.isBlank() ? str : "bone";
      String str3 = str2;
      int n = 2;

      while (!strings.add(str3)) {
         str3 = str2 + "#" + n++;
      }

      return str3;
   }

   private static JsonObject method7439(JsonObject jsonObject, String memberName) {
      return jsonObject != null && jsonObject.has(memberName) && jsonObject.get(memberName).isJsonObject() ? jsonObject.getAsJsonObject(memberName) : null;
   }

   private static String method7440(JsonObject jsonObject, String memberName, String str) {
      try {
         return jsonObject != null && jsonObject.has(memberName) && jsonObject.get(memberName).isJsonPrimitive() ? jsonObject.get(memberName).getAsString() : str;
      } catch (RuntimeException e) {
         return str;
      }
   }

   private static float[] method7441(String str, int n, float f) {
      float[] floats = new float[n];
      Arrays.fill(floats, f);
      if (str != null && !str.isBlank()) {
         String[] strings = str.split(",");

         for (int i = 0; i < n && i < strings.length; i++) {
            try {
               floats[i] = Float.parseFloat(strings[i].strip());
            } catch (NumberFormatException e) {
            }
         }

         return floats;
      } else {
         return floats;
      }
   }
}
