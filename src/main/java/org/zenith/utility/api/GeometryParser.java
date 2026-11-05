package org.zenith.utility.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import org.zenith.utility.misc.CosmeticSkeleton;

public final class GeometryParser {
   private static final float CONST_floatValue = 16.0F;

   private GeometryParser() {
   }

   public static CosmeticSkeleton method2261(String json, String str) {
      CosmeticSkeleton.Entry entry = new CosmeticSkeleton.Entry();
      int n = entry.method7147(str == null ? "" : str);
      JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
      JsonArray jsonArray = jsonObject.has("minecraft:geometry") && jsonObject.get("minecraft:geometry").isJsonArray()
         ? jsonObject.getAsJsonArray("minecraft:geometry")
         : new JsonArray();
      JsonObject jsonObject2 = null;

      for (JsonElement jsonElement : jsonArray) {
         if (jsonElement.isJsonObject()) {
            jsonObject2 = jsonElement.getAsJsonObject();
            break;
         }
      }

      if (jsonObject2 == null) {
         return entry.method7152();
      } else {
         JsonObject jsonObject3 = method2267(jsonObject2, "description");
         float f = method2269(jsonObject3, "texture_width", 64.0F);
         float f2 = method2269(jsonObject3, "texture_height", 64.0F);
         if (f <= 0.0F) {
            f = 64.0F;
         }

         if (f2 <= 0.0F) {
            f2 = 64.0F;
         }

         JsonArray jsonArray2 = jsonObject2.has("bones") && jsonObject2.get("bones").isJsonArray() ? jsonObject2.getAsJsonArray("bones") : new JsonArray();
         HashMap map = new HashMap();
         List<JsonObject> jsons = method2262(jsonArray2);

         for (JsonObject json2 : jsons) {
            String key = method2268(json2, "name", "bone" + map.size());
            String key2 = method2268(json2, "parent", null);
            Integer n2 = key2 == null ? null : (Integer)map.get(key2);
            float[] floats = method2271(json2.get("pivot"), 0.0F);
            float[] floats2 = method2271(json2.get("rotation"), 0.0F);
            int n3 = entry.method7146(key, n2 == null ? -1 : n2, -floats[0] / 16.0F, floats[1] / 16.0F, -floats[2] / 16.0F, floats2[0], floats2[1], floats2[2]);
            map.put(key, n3);
         }

         for (int i = 0; i < jsons.size(); i++) {
            JsonObject json2 = (JsonObject)jsons.get(i);
            if (json2.has("cubes") && json2.get("cubes").isJsonArray()) {
               for (JsonElement jsonElement : json2.getAsJsonArray("cubes")) {
                  if (jsonElement.isJsonObject()) {
                     method2263(entry, i, n, jsonElement.getAsJsonObject(), f, f2);
                  }
               }
            }
         }

         return entry.method7152();
      }
   }

   private static List<JsonObject> method2262(JsonArray jsonArray) {
      List<JsonObject> jsons = new ArrayList<>(jsonArray.size());

      for (JsonElement jsonElement : jsonArray) {
         if (jsonElement.isJsonObject()) {
            jsons.add(jsonElement.getAsJsonObject());
         }
      }

      List<JsonObject> jsons2 = new ArrayList<>(jsons.size());
      HashSet hashSet = new HashSet();
      boolean flag = true;

      while (!jsons.isEmpty() && flag) {
         flag = false;
         int n = 0;

         while (n < jsons.size()) {
            JsonObject json = (JsonObject)jsons.get(n);
            String str = method2268(json, "parent", null);
            if (str != null && !hashSet.contains(str)) {
               n++;
            } else {
               jsons2.add(json);
               hashSet.add(method2268(json, "name", ""));
               jsons.remove(n);
               flag = true;
            }
         }
      }

      jsons2.addAll(jsons);
      return jsons2;
   }

   private static void method2263(CosmeticSkeleton.Entry entry, int intValue, int n, JsonObject jsonObject, float f, float f2) {
      float[] floats = method2271(jsonObject.get("origin"), 0.0F);
      float[] floats2 = method2271(jsonObject.get("size"), 0.0F);
      float f3 = method2269(jsonObject, "inflate", 0.0F);
      boolean flag = method2270(jsonObject, "mirror");
      float f4 = (-floats[0] - floats2[0] - f3) / 16.0F;
      float f5 = (floats[1] - f3) / 16.0F;
      float f6 = (-floats[2] - floats2[2] - f3) / 16.0F;
      float f7 = (-floats[0] + f3) / 16.0F;
      float f8 = (floats[1] + floats2[1] + f3) / 16.0F;
      float f9 = (-floats[2] + f3) / 16.0F;
      float[][] floatses = new float[][]{
         {f4, f5, f6},
         {f7, f5, f6},
         {f7, f8, f6},
         {f4, f8, f6},
         {f4, f5, f9},
         {f7, f5, f9},
         {f7, f8, f9},
         {f4, f8, f9}
      };
      float[] floats3 = method2271(jsonObject.get("rotation"), 0.0F);
      if (floats3[0] != 0.0F || floats3[1] != 0.0F || floats3[2] != 0.0F) {
         float[] floats4 = method2271(jsonObject.get("pivot"), 0.0F);
         float f10 = -floats4[0] / 16.0F;
         float f11 = floats4[1] / 16.0F;
         float f12 = -floats4[2] / 16.0F;
         method2266(floatses, f10, f11, f12, -floats3[0], floats3[1], -floats3[2]);
      }

      method2264(entry, intValue, n, floatses, 5, 4, 7, 6, method2265(jsonObject, "north", floats2, flag, 0, f, f2));
      method2264(entry, intValue, n, floatses, 0, 1, 2, 3, method2265(jsonObject, "south", floats2, flag, 1, f, f2));
      method2264(entry, intValue, n, floatses, 4, 0, 3, 7, method2265(jsonObject, "east", floats2, flag, 2, f, f2));
      method2264(entry, intValue, n, floatses, 1, 5, 6, 2, method2265(jsonObject, "west", floats2, flag, 3, f, f2));
      method2264(entry, intValue, n, floatses, 3, 2, 6, 7, method2265(jsonObject, "up", floats2, flag, 4, f, f2));
      method2264(entry, intValue, n, floatses, 4, 5, 1, 0, method2265(jsonObject, "down", floats2, flag, 5, f, f2));
   }

   private static void method2264(CosmeticSkeleton.Entry entry, int intValue, int n, float[][] floatses, int n2, int n3, int n4, int n5, float[] floatValues2) {
      if (floatValues2 != null) {
         int[] ints = new int[]{n2, n3, n4, n5};
         float[] floatValues = new float[12];

         for (int i = 0; i < 4; i++) {
            floatValues[i * 3] = floatses[ints[i]][0];
            floatValues[i * 3 + 1] = floatses[ints[i]][1];
            floatValues[i * 3 + 2] = floatses[ints[i]][2];
         }

         entry.method7148(intValue, n, floatValues, floatValues2, null);
      }
   }

   private static float[] method2265(JsonObject jsonObject, String memberName, float[] floats, boolean flag, int n, float f, float f2) {
      JsonElement jsonElement = jsonObject.get("uv");
      if (jsonElement == null) {
         return null;
      } else {
         float f3;
         float f4;
         float f5;
         float f6;
         if (jsonElement.isJsonArray()) {
            float[] floats2 = method2271(jsonElement, 0.0F);
            float f7 = floats[2];
            float f8 = floats[0];
            float f9 = floats[1];
            switch (n) {
               case 0:
                  f3 = floats2[0] + f7 + f8 + f7;
                  f4 = floats2[1] + f7;
                  f5 = f8;
                  f6 = f9;
                  break;
               case 1:
                  f3 = floats2[0] + f7;
                  f4 = floats2[1] + f7;
                  f5 = f8;
                  f6 = f9;
                  break;
               case 2:
                  f3 = floats2[0];
                  f4 = floats2[1] + f7;
                  f5 = f7;
                  f6 = f9;
                  break;
               case 3:
                  f3 = floats2[0] + f7 + f8;
                  f4 = floats2[1] + f7;
                  f5 = f7;
                  f6 = f9;
                  break;
               case 4:
                  f3 = floats2[0] + f7;
                  f4 = floats2[1];
                  f5 = f8;
                  f6 = f7;
                  break;
               default:
                  f3 = floats2[0] + f7 + f8;
                  f4 = floats2[1];
                  f5 = f8;
                  f6 = f7;
            }
         } else {
            if (!jsonElement.isJsonObject()) {
               return null;
            }

            JsonObject json = jsonElement.getAsJsonObject();
            if (!json.has(memberName) || !json.get(memberName).isJsonObject()) {
               return null;
            }

            JsonObject jsonObject2 = json.getAsJsonObject(memberName);
            float[] floats2 = method2271(jsonObject2.get("uv"), 0.0F);
            float[] floats3 = method2271(jsonObject2.get("uv_size"), 0.0F);
            f3 = floats2[0];
            f4 = floats2[1];
            f5 = floats3[0];
            f6 = floats3[1];
         }

         float f7 = f3 / f;
         float f8 = f4 / f2;
         float f9 = (f3 + f5) / f;
         float f10 = (f4 + f6) / f2;
         if (flag) {
            float f11 = f7;
            f7 = f9;
            f9 = f11;
         }

         return new float[]{f7, f10, f9, f10, f9, f8, f7, f8};
      }
   }

   private static void method2266(float[][] floatses, float f, float f2, float f3, float angdeg, float angdeg2, float angdeg3) {
      double d = Math.toRadians(angdeg);
      double d2 = Math.toRadians(angdeg2);
      double d3 = Math.toRadians(angdeg3);
      float f4 = (float)Math.sin(d);
      float f5 = (float)Math.cos(d);
      float f6 = (float)Math.sin(d2);
      float f7 = (float)Math.cos(d2);
      float f8 = (float)Math.sin(d3);
      float f9 = (float)Math.cos(d3);

      for (float[] floats : floatses) {
         float f10 = floats[0] - f;
         float f11 = floats[1] - f2;
         float f12 = floats[2] - f3;
         float f13 = f10 * f9 - f11 * f8;
         float f14 = f10 * f8 + f11 * f9;
         float f15 = f13 * f7 + f12 * f6;
         float f16 = -f13 * f6 + f12 * f7;
         float f17 = f14 * f5 - f16 * f4;
         float f18 = f14 * f4 + f16 * f5;
         floats[0] = f15 + f;
         floats[1] = f17 + f2;
         floats[2] = f18 + f3;
      }
   }

   private static JsonObject method2267(JsonObject json, String memberName) {
      return json != null && json.has(memberName) && json.get(memberName).isJsonObject() ? json.getAsJsonObject(memberName) : null;
   }

   private static String method2268(JsonObject json, String memberName, String str) {
      try {
         return json != null && json.has(memberName) && json.get(memberName).isJsonPrimitive() ? json.get(memberName).getAsString() : str;
      } catch (RuntimeException e) {
         return str;
      }
   }

   private static float method2269(JsonObject jsonObject, String memberName, float f) {
      try {
         return jsonObject != null && jsonObject.has(memberName) && jsonObject.get(memberName).isJsonPrimitive() ? jsonObject.get(memberName).getAsFloat() : f;
      } catch (RuntimeException e) {
         return f;
      }
   }

   private static boolean method2270(JsonObject jsonObject, String memberName) {
      try {
         return jsonObject != null && jsonObject.has(memberName) && jsonObject.get(memberName).isJsonPrimitive() && jsonObject.get(memberName).getAsBoolean();
      } catch (RuntimeException e) {
         return false;
      }
   }

   private static float[] method2271(JsonElement jsonElement, float f) {
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
}
