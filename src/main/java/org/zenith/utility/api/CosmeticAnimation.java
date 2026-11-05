package org.zenith.utility.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public final class CosmeticAnimation {
   private static final float CONST_floatValue = 16.0F;
   private final CosmeticAnimation.Entry3 entry3;
   private final CosmeticAnimation.Entry3 entry32;
   private static final String[] texts = new String[]{"idle", "stand", "loop"};
   private static final String[] texts2 = new String[]{"walk", "run", "move", "fly", "swim"};

   private CosmeticAnimation(CosmeticAnimation.Entry3 entry3, CosmeticAnimation.Entry3 entry32) {
      this.entry3 = entry3;
      this.entry32 = entry32;
   }

   public String name() {
      return this.entry3.text;
   }

   public String method7806() {
      return this.entry32 == null ? "" : this.entry32.text;
   }

   public boolean method7807() {
      return this.entry3.flag;
   }

   public float method7808() {
      return this.entry3.floatValue;
   }

   public boolean method7809(String name, float f, float[] floats) {
      return this.method7810(name, f, false, floats);
   }

   public boolean method7810(String name, float f, boolean flag, float[] floats) {
      CosmeticAnimation.Entry3 entry3 = flag && this.entry32 != null ? this.entry32 : this.entry3;
      CosmeticAnimation.Entry2 entry2 = entry3.items.get(name);
      if (entry2 != null && floats != null && floats.length >= 9) {
         float f2 = entry3.method7825(f);
         method7811(entry2.entry, f2, floats, 0, 0.0F);
         method7811(entry2.entry2, f2, floats, 3, 0.0F);
         method7811(entry2.entry3, f2, floats, 6, 1.0F);
         floats[3] = -floats[3] / 16.0F;
         floats[4] /= 16.0F;
         floats[5] = -floats[5] / 16.0F;
         return true;
      } else {
         return false;
      }
   }

   private static void method7811(CosmeticAnimation.Entry entry, float f, float[] floats, int n, float f2) {
      if (entry == null) {
         floats[n] = f2;
         floats[n + 1] = f2;
         floats[n + 2] = f2;
      } else {
         float[] floatValues = entry.floatValues;
         int n2 = floatValues.length - 1;
         if (f <= floatValues[0]) {
            method7812(entry.floatValues3, 0, floats, n);
         } else if (f >= floatValues[n2]) {
            method7812(entry.floatValues2, n2, floats, n);
         } else {
            int n3 = 0;

            while (n3 < n2 && floatValues[n3 + 1] < f) {
               n3++;
            }

            float f3 = floatValues[n3];
            float f4 = floatValues[n3 + 1];
            float f5 = f4 - f3 <= 1.0E-6F ? 0.0F : (f - f3) / (f4 - f3);
            if (entry.flags[n3]) {
               int n4 = Math.max(n3 - 1, 0);
               int n5 = Math.min(n3 + 2, n2);

               for (int i = 0; i < 3; i++) {
                  floats[n + i] = method7813(
                     entry.floatValues3[n4 * 3 + i],
                     entry.floatValues3[n3 * 3 + i],
                     entry.floatValues2[(n3 + 1) * 3 + i],
                     entry.floatValues2[n5 * 3 + i],
                     f5
                  );
               }
            } else {
               for (int i = 0; i < 3; i++) {
                  float f6 = entry.floatValues3[n3 * 3 + i];
                  float f7 = entry.floatValues2[(n3 + 1) * 3 + i];
                  floats[n + i] = f6 + (f7 - f6) * f5;
               }
            }
         }
      }
   }

   private static void method7812(float[] floatValues3, int n, float[] floats, int n2) {
      floats[n2] = floatValues3[n * 3];
      floats[n2 + 1] = floatValues3[n * 3 + 1];
      floats[n2 + 2] = floatValues3[n * 3 + 2];
   }

   private static float method7813(float f, float f2, float f3, float f4, float f5) {
      float f6 = f5 * f5;
      float f7 = f6 * f5;
      return 0.5F
         * (2.0F * f2 + (f3 - f) * f5 + (2.0F * f - 5.0F * f2 + 4.0F * f3 - f4) * f6 + (3.0F * f2 - f - 3.0F * f3 + f4) * f7);
   }

   public static CosmeticAnimation method7814(String json) {
      if (json != null && !json.isBlank()) {
         try {
            JsonElement string = JsonParser.parseString(json);
            if (!string.isJsonObject()) {
               return null;
            } else {
               JsonElement jsonElement = string.getAsJsonObject().get("animations");
               if (jsonElement != null && jsonElement.isJsonObject()) {
                  LinkedHashMap map = new LinkedHashMap();

                  for (Map.Entry entry : jsonElement.getAsJsonObject().entrySet()) {
                     if (((JsonElement)entry.getValue()).isJsonObject()) {
                        map.put((String)entry.getKey(), method7815((String)entry.getKey(), ((JsonElement)entry.getValue()).getAsJsonObject()));
                     }
                  }

                  CosmeticAnimation.Entry3 entry3 = method7816(map, texts, true);
                  return entry3 == null ? null : new CosmeticAnimation(entry3, method7816(map, texts2, false));
               } else {
                  return null;
               }
            }
         } catch (RuntimeException e) {
            return null;
         }
      } else {
         return null;
      }
   }

   private static CosmeticAnimation.Entry3 method7815(String key, JsonObject jsonObject) {
      HashMap items = new HashMap();
      float f = method7817(jsonObject, items);
      float f2 = method7823(jsonObject, "animation_length", f);
      return new CosmeticAnimation.Entry3(key, method7824(jsonObject, "loop"), f2 <= 0.0F ? f : f2, items);
   }

   private static CosmeticAnimation.Entry3 method7816(Map<String, CosmeticAnimation.Entry3> map, String[] texts, boolean flag) {
      for (int i = 0; i < 2; i++) {
         boolean flag2 = i == 0;

         for (String text : texts) {
            for (CosmeticAnimation.Entry3 entry3 : map.values()) {
               String lowerCase = entry3.text.toLowerCase(Locale.ROOT);
               if (lowerCase.equals(text) && !lowerCase.contains("rare") && (entry3.flag || !flag2)) {
                  return entry3;
               }
            }
         }

         for (String text : texts) {
            for (CosmeticAnimation.Entry3 entry3 : map.values()) {
               String lowerCase = entry3.text.toLowerCase(Locale.ROOT);
               if (lowerCase.contains(text) && !lowerCase.contains("rare") && (entry3.flag || !flag2)) {
                  return entry3;
               }
            }
         }
      }

      if (!flag) {
         return null;
      } else {
         CosmeticAnimation.Entry3 entry3 = null;

         for (CosmeticAnimation.Entry3 entry32 : map.values()) {
            if (entry3 == null) {
               entry3 = entry32;
            }

            if (entry32.flag) {
               return entry32;
            }
         }

         return entry3;
      }
   }

   private static float method7817(JsonObject jsonObject, Map<String, CosmeticAnimation.Entry2> items) {
      JsonElement jsonElement = jsonObject.get("bones");
      if (jsonElement != null && jsonElement.isJsonObject()) {
         float f = 0.0F;

         for (Map.Entry entry : jsonElement.getAsJsonObject().entrySet()) {
            if (((JsonElement)entry.getValue()).isJsonObject()) {
               JsonObject json = ((JsonElement)entry.getValue()).getAsJsonObject();
               CosmeticAnimation.Entry2 entry2 = new CosmeticAnimation.Entry2();
               entry2.entry = method7819(json.get("rotation"));
               entry2.entry2 = method7819(json.get("position"));
               entry2.entry3 = method7819(json.get("scale"));
               if (entry2.entry != null || entry2.entry2 != null || entry2.entry3 != null) {
                  f = Math.max(f, Math.max(method7818(entry2.entry), Math.max(method7818(entry2.entry2), method7818(entry2.entry3))));
                  items.put((String)entry.getKey(), entry2);
               }
            }
         }

         return f;
      } else {
         return 0.0F;
      }
   }

   private static float method7818(CosmeticAnimation.Entry entry) {
      return entry == null ? 0.0F : entry.floatValues[entry.floatValues.length - 1];
   }

   private static CosmeticAnimation.Entry method7819(JsonElement jsonElement) {
      if (jsonElement == null) {
         return null;
      } else if (jsonElement.isJsonArray()) {
         float[] floatValues2 = method7820(jsonElement, 0.0F);
         return new CosmeticAnimation.Entry(new float[]{0.0F}, floatValues2, (float[])floatValues2.clone(), new boolean[]{false});
      } else if (!jsonElement.isJsonObject()) {
         return null;
      } else {
         JsonObject jsonObject = jsonElement.getAsJsonObject();
         ArrayList<float[]> floatses = new ArrayList<>(jsonObject.size());

         for (Map.Entry entry : jsonObject.entrySet()) {
            float f;
            try {
               f = Float.parseFloat((String)entry.getKey());
            } catch (NumberFormatException e) {
               continue;
            }

            JsonElement jsonElement2 = (JsonElement)entry.getValue();
            boolean equalsIgnoreCase = false;
            float[] floats;
            float[] floats2;
            if (jsonElement2.isJsonObject()) {
               JsonObject json = jsonElement2.getAsJsonObject();
               JsonElement jsonElement3 = json.get("post");
               JsonElement jsonElement4 = json.get("pre");
               floats2 = method7820(jsonElement3 != null ? jsonElement3 : jsonElement4, 0.0F);
               floats = jsonElement4 != null ? method7820(jsonElement4, 0.0F) : (float[])floats2.clone();
               equalsIgnoreCase = "catmullrom".equalsIgnoreCase(method7822(json, "lerp_mode"));
            } else {
               floats2 = method7820(jsonElement2, 0.0F);
               floats = (float[])floats2.clone();
            }

            floatses.add(new float[]{f, floats[0], floats[1], floats[2], floats2[0], floats2[1], floats2[2], equalsIgnoreCase ? 1.0F : 0.0F});
         }

         if (floatses.isEmpty()) {
            return null;
         } else {
            floatses.sort((var0x, var1x) -> Float.compare(var0x[0], var1x[0]));
            int size = floatses.size();
            float[] floatValues = new float[size];
            float[] floatValues2 = new float[size * 3];
            float[] floatValues3 = new float[size * 3];
            boolean[] flags = new boolean[size];

            for (int i = 0; i < size; i++) {
               float[] floats = (float[])floatses.get(i);
               floatValues[i] = floats[0];
               floatValues2[i * 3] = floats[1];
               floatValues2[i * 3 + 1] = floats[2];
               floatValues2[i * 3 + 2] = floats[3];
               floatValues3[i * 3] = floats[4];
               floatValues3[i * 3 + 1] = floats[5];
               floatValues3[i * 3 + 2] = floats[6];
               flags[i] = floats[7] != 0.0F;
            }

            return new CosmeticAnimation.Entry(floatValues, floatValues2, floatValues3, flags);
         }
      }
   }

   private static float[] method7820(JsonElement jsonElement, float f) {
      float[] floats = new float[]{f, f, f};
      if (jsonElement == null) {
         return floats;
      } else if (jsonElement.isJsonPrimitive()) {
         float f2 = method7821(jsonElement, f);
         floats[0] = f2;
         floats[1] = f2;
         floats[2] = f2;
         return floats;
      } else if (!jsonElement.isJsonArray()) {
         return floats;
      } else {
         JsonArray jsonArray = jsonElement.getAsJsonArray();

         for (int i = 0; i < 3 && i < jsonArray.size(); i++) {
            floats[i] = method7821(jsonArray.get(i), f);
         }

         return floats;
      }
   }

   private static float method7821(JsonElement jsonElement, float f) {
      try {
         return jsonElement.getAsFloat();
      } catch (RuntimeException e) {
         return f;
      }
   }

   private static String method7822(JsonObject json, String memberName) {
      try {
         return json.has(memberName) && json.get(memberName).isJsonPrimitive() ? json.get(memberName).getAsString() : null;
      } catch (RuntimeException e) {
         return null;
      }
   }

   private static float method7823(JsonObject jsonObject, String memberName, float f) {
      try {
         return jsonObject.has(memberName) && jsonObject.get(memberName).isJsonPrimitive() ? jsonObject.get(memberName).getAsFloat() : f;
      } catch (RuntimeException e) {
         return f;
      }
   }

   private static boolean method7824(JsonObject jsonObject, String memberName) {
      try {
         return jsonObject.has(memberName) && jsonObject.get(memberName).isJsonPrimitive() && jsonObject.get(memberName).getAsBoolean();
      } catch (RuntimeException e) {
         return false;
      }
   }

   private static final class Entry {
      private final float[] floatValues;
      private final float[] floatValues2;
      private final float[] floatValues3;
      private final boolean[] flags;

      private Entry(float[] floatValues, float[] floatValues2, float[] floatValues3, boolean[] flags) {
         this.floatValues = floatValues;
         this.floatValues2 = floatValues2;
         this.floatValues3 = floatValues3;
         this.flags = flags;
      }
   }

   private static final class Entry2 {
      private CosmeticAnimation.Entry entry;
      private CosmeticAnimation.Entry entry2;
      private CosmeticAnimation.Entry entry3;
   }

   private static final class Entry3 {
      private final String text;
      private final boolean flag;
      private final float floatValue;
      private final Map<String, CosmeticAnimation.Entry2> items;

      private Entry3(String text, boolean flag, float floatValue, Map<String, CosmeticAnimation.Entry2> items) {
         this.text = text;
         this.flag = flag;
         this.floatValue = floatValue;
         this.items = items;
      }

      private float method7825(float f) {
         if (this.floatValue <= 0.0F) {
            return 0.0F;
         } else {
            float f2 = f % this.floatValue;
            return f2 < 0.0F ? f2 + this.floatValue : f2;
         }
      }
   }
}
