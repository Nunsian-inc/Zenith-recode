package org.zenith.utility.render;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.zenith.utility.misc.WardrobeModel;
import org.zenith.utility.misc.AnimationCurve;

public final class ModelJsonParser {
   private static final float[][] CONST_floatValues = new float[][]{
      {0.0F, 0.0F, 0.0F}, {0.0F, 0.0F, 0.0F}, {-5.0F, 2.0F, 0.0F}, {5.0F, 2.0F, 0.0F}, {-1.9F, 12.0F, 0.0F}, {1.9F, 12.0F, 0.0F}
   };
   private static final String[] texts = new String[]{"down", "up", "west", "north", "east", "south"};
   private static final float[][] CONST_floatValues2 = new float[][]{
      {0.0F, -1.0F, 0.0F}, {0.0F, 1.0F, 0.0F}, {-1.0F, 0.0F, 0.0F}, {0.0F, 0.0F, -1.0F}, {1.0F, 0.0F, 0.0F}, {0.0F, 0.0F, 1.0F}
   };
   private static final int[][] CONST_intValues = new int[][]{{5, 4, 0, 1}, {2, 3, 7, 6}, {0, 4, 7, 3}, {1, 0, 3, 2}, {5, 1, 2, 6}, {4, 5, 6, 7}};
   private static final float CONST_floatValue = 0.14375F;
   private static final float CONST_floatValue2 = 0.0125F;

   private ModelJsonParser() {
   }

   public static WardrobeModel method107(String text, JsonObject jsonObject, Identifier identifier) {
      JsonObject json = method124(jsonObject, "texture");
      float[] floats = method129(json == null ? null : json.get("size"), 2, 64.0F);
      int intValue = Math.max(1, (int)floats[0]);
      int n = Math.max(1, (int)floats[1]);
      JsonArray jsonArray = method125(jsonObject, "bones");
      var jsons = method123(jsonArray);
      int n2 = Math.max(1, jsons.size());
      String[] texts = new String[n2];
      int[] intValues = new int[n2];
      float[] floatValues = new float[n2 * 3];
      float[] floatValues2 = new float[n2 * 3];
      int[] intValues2 = new int[n2];
      int[] intValues3 = new int[n2];
      WardrobeModel.Type2[] type2s = new WardrobeModel.Type2[n2];
      float[] floatValues3 = new float[n2 * 4];
      HashMap map = new HashMap();
      if (jsons.isEmpty()) {
         texts[0] = "root";
         intValues[0] = -1;
         intValues2[0] = -1;
         intValues3[0] = 6;
         type2s[0] = WardrobeModel.Type2.NONE;
      }

      for (int i = 0; i < jsons.size(); i++) {
         JsonObject jsonObject2 = (JsonObject)jsons.get(i);
         String key = method126(jsonObject2, "name", "bone" + i);
         texts[i] = key;
         Integer n3 = (Integer)map.get(method126(jsonObject2, "parent", ""));
         intValues[i] = n3 == null ? -1 : n3;
         float[] floats2 = method129(jsonObject2.get("pivot"), 3, 0.0F);
         float[] floats3 = method129(jsonObject2.get("rotation"), 3, 0.0F);

         for (int j = 0; j < 3; j++) {
            floatValues[i * 3 + j] = floats2[j];
            floatValues2[i * 3 + j] = (float)Math.toRadians(floats3[j]);
         }

         intValues2[i] = intValues[i] < 0 ? method121(method126(jsonObject2, "anchor", null)) : -1;
         intValues3[i] = intValues[i] >= 0 ? intValues3[intValues[i]] : (intValues2[i] >= 0 ? intValues2[i] : 6);
         JsonObject jsonObject3 = method124(jsonObject2, "driver");
         type2s[i] = jsonObject3 == null ? WardrobeModel.Type2.NONE : method122(method126(jsonObject3, "type", "none"));
         float[] floats4 = method129(jsonObject3 == null ? null : jsonObject3.get("args"), 4, 0.0F);
         System.arraycopy(floats4, 0, floatValues3, i * 4, 4);
         map.put(key, i);
      }

      ArrayList<ModelJsonParser.Data> datas = new ArrayList<>();

      for (int i = 0; i < jsons.size(); i++) {
         JsonArray jsonArray2 = method125((JsonObject)jsons.get(i), "cubes");
         float f = Float.NaN;
         if (intValues[i] < 0 && intValues2[i] == WardrobeModel.Type.RIGHT_ARM.ordinal()) {
            f = -1.0F;
         } else if (intValues[i] < 0 && intValues2[i] == WardrobeModel.Type.LEFT_ARM.ordinal()) {
            f = 1.0F;
         }

         for (JsonElement jsonElement : jsonArray2) {
            if (jsonElement.isJsonObject()) {
               method110(datas, i, jsonElement.getAsJsonObject(), intValue, n, f);
            }
         }
      }

      datas.sort((var1x, var2x) -> {
         int var3x = Integer.compare(intValues3[var1x.intValue], intValues3[var2x.intValue]);
         return var3x != 0 ? var3x : Integer.compare(var1x.intValue, var2x.intValue);
      });
      int size = datas.size();
      float[] floatValues4 = new float[size * 12];
      float[] floatValues5 = new float[size * 8];
      float[] floatValues6 = new float[size * 3];
      int[] intValues4 = new int[size];
      int[] intValues5 = new int[size];

      for (int i = 0; i < size; i++) {
         ModelJsonParser.Data data = (ModelJsonParser.Data)datas.get(i);
         System.arraycopy(data.floatValues, 0, floatValues4, i * 12, 12);
         System.arraycopy(data.floatValues2, 0, floatValues5, i * 8, 8);
         System.arraycopy(data.floatValues3, 0, floatValues6, i * 3, 3);
         intValues4[i] = data.intValue;
         intValues5[i] = data.intValue2;
      }

      if (text != null && (text.startsWith("clothing/") || text.startsWith("characters/") || text.startsWith("politicians/"))) {
         method108(floatValues4, intValues4, intValues, intValues2);
      }

      var items = method113(method124(jsonObject, "clips"), map, n2);
      var list = method115(method125(jsonObject, "effects"), map);
      WardrobeModel.Data3 data3 = method117(method124(jsonObject, "pet"));
      WardrobeModel.Data2 data2 = method118(method124(jsonObject, "weapon"), map);
      WardrobeModel.Data data = method119(method124(jsonObject, "skin"), map);
      float floatValue = method127(jsonObject, "lod", 24.0F);
      float[] floatValues7 = method112(floatValues4, intValues4, intValues, floatValues, floatValues2, intValues2);
      byte b = 0;

      for (JsonElement jsonElement : method125(jsonObject, "hides")) {
         String lowerCase = jsonElement.getAsString().toLowerCase(Locale.ROOT);

         b |= switch (lowerCase) {
            case "hat" -> 1;
            case "jacket" -> 2;
            case "sleeves" -> 4;
            case "pants" -> 8;
            case "cape" -> 16;
            default -> 0;
         };
      }

      return new WardrobeModel(
         text,
         identifier,
         intValue,
         n,
         texts,
         intValues,
         floatValues,
         floatValues2,
         intValues2,
         intValues3,
         type2s,
         floatValues3,
         floatValues4,
         floatValues5,
         floatValues6,
         intValues4,
         intValues5,
         items,
         list,
         data3,
         data2,
         data,
         floatValue,
         floatValues7,
         b
      );
   }

   private static void method108(float[] floatValues4, int[] intValues4, int[] intValues, int[] intValues2) {
      int ordinal = WardrobeModel.Type.RIGHT_ARM.ordinal();
      int n = WardrobeModel.Type.LEFT_ARM.ordinal();

      for (int i = 0; i < intValues4.length; i++) {
         int n2 = intValues4[i];
         if (intValues[n2] < 0 && (intValues2[n2] == ordinal || intValues2[n2] == n)) {
            float f = intValues2[n2] == ordinal ? -0.0625F : 0.0625F;

            for (int j = 0; j < 4; j++) {
               int n3 = i * 12 + j * 3;
               floatValues4[n3] = f + method109(floatValues4[n3] - f);
               floatValues4[n3 + 2] = method109(floatValues4[n3 + 2]);
            }
         }
      }
   }

   private static float method109(float sign) {
      float f = Math.abs(sign);
      if (f <= 0.14375F) {
         return sign;
      } else {
         float f2 = f - 0.14375F;
         float magnitude = 0.14375F + 0.0125F * f2 / (f2 + 0.0125F);
         return Math.copySign(magnitude, sign);
      }
   }

   private static void method110(List<ModelJsonParser.Data> datas, int intValue, JsonObject jsonObject, int n, int n2, float f) {
      float[] floats = method129(jsonObject.get("from"), 3, 0.0F);
      float[] floats2 = method129(jsonObject.get("size"), 3, 0.0F);
      float f2 = method127(jsonObject, "inflate", 0.0F);
      boolean flag = method128(jsonObject, "mirror");
      float[] floats3 = method129(jsonObject.get("rotation"), 3, 0.0F);
      boolean flag2 = floats3[0] == 0.0F && floats3[1] == 0.0F && floats3[2] == 0.0F;
      boolean flag3 = !Float.isNaN(f)
         && flag2
         && floats[0] - f2 < f
         && floats[0] + floats2[0] + f2 > f
         && floats[2] - f2 < 0.0F
         && floats[2] + floats2[2] + f2 > 0.0F;
      int n3 = (flag3 ? 32 : 0)
         | (method128(jsonObject, "glow") ? 1 : 0)
         | (method128(jsonObject, "translucent") ? 2 : 0)
         | (method128(jsonObject, "detail") ? 4 : 0)
         | (method128(jsonObject, "glass") ? 10 : 0)
         | (jsonObject.has("sparkle") && jsonObject.get("sparkle").isJsonPrimitive() && method128(jsonObject, "sparkle") ? 16 : 0);
      HashSet hashSet = new HashSet();
      if (jsonObject.has("sparkle") && jsonObject.get("sparkle").isJsonArray()) {
         for (JsonElement json : jsonObject.getAsJsonArray("sparkle")) {
            hashSet.add(json.getAsString());
         }
      }

      float f3 = floats[0] - f2;
      float f4 = floats[1] - f2;
      float f5 = floats[2] - f2;
      float f6 = floats[0] + floats2[0] + f2;
      float f7 = floats[1] + floats2[1] + f2;
      float f8 = floats[2] + floats2[2] + f2;
      if (flag) {
         float f9 = f6;
         f6 = f3;
         f3 = f9;
      }

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
      float[] floats4 = method129(jsonObject.get("rotation"), 3, 0.0F);
      Matrix3f rotationZYX = null;
      if (floats4[0] != 0.0F || floats4[1] != 0.0F || floats4[2] != 0.0F) {
         float[] floats5 = method129(jsonObject.get("origin"), 3, 0.0F);
         rotationZYX = new Matrix3f().rotationZYX((float)Math.toRadians(floats4[2]), (float)Math.toRadians(floats4[1]), (float)Math.toRadians(floats4[0]));
         Vector3f vec = new Vector3f();

         for (float[] floats6 : floatses) {
            vec.set(floats6[0] - floats5[0], floats6[1] - floats5[1], floats6[2] - floats5[2]);
            rotationZYX.transform(vec);
            floats6[0] = vec.x + floats5[0];
            floats6[1] = vec.y + floats5[1];
            floats6[2] = vec.z + floats5[2];
         }
      }

      float[][] floatses2 = method111(jsonObject, floats2);

      for (int i = 0; i < 6; i++) {
         float[] floats5 = floatses2[i];
         if (floats5 != null) {
            int[] ints = CONST_intValues[i];
            float[] floatValues = new float[12];

            for (int j = 0; j < 4; j++) {
               float[] floats6 = floatses[ints[flag ? 3 - j : j]];
               floatValues[j * 3] = floats6[0] / 16.0F;
               floatValues[j * 3 + 1] = floats6[1] / 16.0F;
               floatValues[j * 3 + 2] = floats6[2] / 16.0F;
            }

            float f9 = floats5[0] / n;
            float f10 = floats5[1] / n2;
            float f11 = floats5[2] / n;
            float f12 = floats5[3] / n2;
            float[] floatValues2 = flag
               ? new float[]{f11, f12, f9, f12, f9, f10, f11, f10}
               : new float[]{f11, f10, f9, f10, f9, f12, f11, f12};
            Vector3f vec = new Vector3f(CONST_floatValues2[i][0] * (flag ? -1 : 1), CONST_floatValues2[i][1], CONST_floatValues2[i][2]);
            if (rotationZYX != null) {
               rotationZYX.transform(vec);
            }

            int n4 = hashSet.contains(texts[i]) ? n3 | 16 : n3;
            datas.add(new ModelJsonParser.Data(intValue, n4, floatValues, floatValues2, new float[]{vec.x, vec.y, vec.z}));
         }
      }
   }

   private static float[][] method111(JsonObject jsonObject, float[] floats) {
      float[][] floatses = new float[6][];
      JsonElement jsonElement = jsonObject.get("faces");
      if (jsonElement != null && jsonElement.isJsonObject()) {
         JsonObject json = jsonElement.getAsJsonObject();

         for (int i = 0; i < 6; i++) {
            JsonElement jsonElement2 = json.get(texts[i]);
            if (jsonElement2 != null && jsonElement2.isJsonArray() && jsonElement2.getAsJsonArray().size() >= 4) {
               floatses[i] = method129(jsonElement2, 4, 0.0F);
            }
         }

         return floatses;
      } else {
         JsonElement jsonElement2 = jsonObject.get("uv");
         if (jsonElement2 != null && jsonElement2.isJsonArray()) {
            float[] floats2 = method129(jsonElement2, 2, 0.0F);
            float f = floats2[0];
            float f2 = floats2[1];
            float f3 = floats[0];
            float f4 = floats[1];
            float f5 = floats[2];
            float f6 = f + f5;
            float f7 = f + f5 + f3;
            float f8 = f + f5 + f3 + f3;
            float f9 = f + f5 + f3 + f5;
            float f10 = f + f5 + f3 + f5 + f3;
            float f11 = f2 + f5;
            float f12 = f2 + f5 + f4;
            floatses[0] = new float[]{f6, f2, f7, f11};
            floatses[1] = new float[]{f7, f11, f8, f2};
            floatses[2] = new float[]{f, f11, f6, f12};
            floatses[3] = new float[]{f6, f11, f7, f12};
            floatses[4] = new float[]{f7, f11, f9, f12};
            floatses[5] = new float[]{f9, f11, f10, f12};
            return floatses;
         } else {
            return floatses;
         }
      }
   }

   private static float[] method112(float[] floatValues4, int[] intValues4, int[] intValues, float[] floatValues, float[] floatValues2, int[] intValues2) {
      int length = intValues.length;
      Matrix4f[] matrixes = new Matrix4f[length];

      for (int i = 0; i < length; i++) {
         Matrix4f matrix = intValues[i] >= 0 ? new Matrix4f(matrixes[intValues[i]]) : new Matrix4f();
         if (intValues[i] < 0 && intValues2[i] >= 0) {
            float[] floats = CONST_floatValues[intValues2[i]];
            matrix.translate(floats[0] / 16.0F, floats[1] / 16.0F, floats[2] / 16.0F);
         } else {
            matrix.translate(floatValues[i * 3] / 16.0F, floatValues[i * 3 + 1] / 16.0F, floatValues[i * 3 + 2] / 16.0F);
         }

         matrix.rotateZYX(floatValues2[i * 3 + 2], floatValues2[i * 3 + 1], floatValues2[i * 3]);
         matrixes[i] = matrix;
      }

      float[] floats = new float[]{Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE};
      Vector3f vec = new Vector3f();

      for (int i = 0; i < intValues4.length; i++) {
         Matrix4f matrix = matrixes[intValues4[i]];

         for (int j = 0; j < 4; j++) {
            int n = i * 12 + j * 3;
            matrix.transformPosition(floatValues4[n], floatValues4[n + 1], floatValues4[n + 2], vec);
            floats[0] = Math.min(floats[0], vec.x);
            floats[1] = Math.min(floats[1], vec.y);
            floats[2] = Math.min(floats[2], vec.z);
            floats[3] = Math.max(floats[3], vec.x);
            floats[4] = Math.max(floats[4], vec.y);
            floats[5] = Math.max(floats[5], vec.z);
         }
      }

      return intValues4.length == 0 ? new float[6] : floats;
   }

   private static Map<String, AnimationCurve> method113(JsonObject json, Map<String, Integer> map, int n) {
      if (json == null) {
         return Collections.emptyMap();
      } else {
         LinkedHashMap map2 = new LinkedHashMap();

         for (Entry entry : json.entrySet()) {
            if (((JsonElement)entry.getValue()).isJsonObject()) {
               JsonObject jsonObject = ((JsonElement)entry.getValue()).getAsJsonObject();
               HashMap map3 = new HashMap();
               JsonObject json2 = method124(jsonObject, "bones");
               float f = 0.0F;
               if (json2 != null) {
                  for (Entry entry2 : json2.entrySet()) {
                     Integer key = (Integer)map.get(entry2.getKey());
                     if (key != null && ((JsonElement)entry2.getValue()).isJsonObject()) {
                        JsonObject jsonObject2 = ((JsonElement)entry2.getValue()).getAsJsonObject();
                        boolean flag = !method128(jsonObject2, "linear");
                        AnimationCurve.Entry[] entries = new AnimationCurve.Entry[]{
                           method114(jsonObject2.get("rotation"), flag, 0.0F),
                           method114(jsonObject2.get("position"), flag, 0.0F),
                           method114(jsonObject2.get("scale"), flag, 1.0F)
                        };

                        for (AnimationCurve.Entry entry3 : entries) {
                           if (entry3 != null) {
                              f = Math.max(f, entry3.floatValues[entry3.floatValues.length - 1]);
                           }
                        }

                        map3.put(key, entries);
                     }
                  }
               }

               float f2 = method127(jsonObject, "length", f);
               boolean flag = !jsonObject.has("loop") || method128(jsonObject, "loop");
               map2.put((String)entry.getKey(), new AnimationCurve((String)entry.getKey(), f2 <= 0.0F ? f : f2, flag, map3, n));
            }
         }

         return map2;
      }
   }

   private static AnimationCurve.Entry method114(JsonElement jsonElement, boolean flag, float f) {
      if (jsonElement != null && jsonElement.isJsonArray()) {
         JsonArray jsonArray = jsonElement.getAsJsonArray();
         ArrayList<float[]> floatses = new ArrayList<>(jsonArray.size());

         for (JsonElement json : jsonArray) {
            if (json.isJsonArray()) {
               JsonArray jsonArray2 = json.getAsJsonArray();
               float f2 = jsonArray2.size() > 0 ? jsonArray2.get(0).getAsFloat() : 0.0F;
               float f3 = jsonArray2.size() > 1 ? jsonArray2.get(1).getAsFloat() : f;
               float f4 = jsonArray2.size() > 2 ? jsonArray2.get(2).getAsFloat() : f3;
               float f5 = jsonArray2.size() > 3 ? jsonArray2.get(3).getAsFloat() : f4;
               floatses.add(new float[]{f2, f3, f4, f5});
            }
         }

         if (floatses.isEmpty()) {
            return null;
         } else {
            floatses.sort((var0x, var1x) -> Float.compare(var0x[0], var1x[0]));
            float[] floatValues = new float[floatses.size()];
            float[] floatValues2 = new float[floatses.size() * 3];

            for (int i = 0; i < floatses.size(); i++) {
               float[] floats = (float[])floatses.get(i);
               floatValues[i] = floats[0];
               floatValues2[i * 3] = floats[1];
               floatValues2[i * 3 + 1] = floats[2];
               floatValues2[i * 3 + 2] = floats[3];
            }

            return new AnimationCurve.Entry(floatValues, floatValues2, flag);
         }
      } else {
         return null;
      }
   }

   private static List<WardrobeModel.Data4> method115(JsonArray jsonArray, Map<String, Integer> map) {
      ArrayList<WardrobeModel.Data4> data4s = new ArrayList<>();

      for (JsonElement jsonElement : jsonArray) {
         if (jsonElement.isJsonObject()) {
            WardrobeModel.Data4 data4 = method116(jsonElement.getAsJsonObject(), map);
            if (data4 != null) {
               data4s.add(data4);
            }
         }
      }

      return data4s;
   }

   private static WardrobeModel.Data4 method116(JsonObject jsonObject, Map<String, Integer> map) {
      ParticleOptions particleEffect = method120(method126(jsonObject, "particle", null));
      if (particleEffect == null) {
         return null;
      } else {
         Integer n = (Integer)map.get(method126(jsonObject, "bone", ""));
         float[] floats = method129(jsonObject.get("offset"), 3, 0.0F);
         float[] floats2 = method129(jsonObject.get("velocity"), 3, 0.0F);

         WardrobeModel.Type3 type3;
         try {
            type3 = WardrobeModel.Type3.valueOf(method126(jsonObject, "when", "always").toUpperCase(Locale.ROOT));
         } catch (IllegalArgumentException e) {
            type3 = WardrobeModel.Type3.ALWAYS;
         }

         return new WardrobeModel.Data4(
            particleEffect,
            n == null ? -1 : n,
            floats[0],
            floats[1],
            floats[2],
            method127(jsonObject, "rate", 1.0F),
            floats2[0],
            floats2[1],
            floats2[2],
            method127(jsonObject, "spread", 0.05F),
            type3
         );
      }
   }

   private static WardrobeModel.Data3 method117(JsonObject jsonObject) {
      if (jsonObject == null) {
         return null;
      } else {
         WardrobeModel.Type4 type4;
         try {
            type4 = WardrobeModel.Type4.valueOf(method126(jsonObject, "kind", "walker").toUpperCase(Locale.ROOT));
         } catch (IllegalArgumentException e) {
            type4 = WardrobeModel.Type4.WALKER;
         }

         ArrayList coll = new ArrayList();

         for (JsonElement jsonElement : method125(jsonObject, "behaviors")) {
            coll.add(jsonElement.getAsString().toLowerCase(Locale.ROOT));
         }

         return new WardrobeModel.Data3(
            type4,
            method127(jsonObject, "scale", 1.0F),
            method127(jsonObject, "speed", 1.0F),
            method127(jsonObject, "hover", 0.0F),
            method126(jsonObject, "quirk", ""),
            method120(method126(jsonObject, "accent", null)),
            List.copyOf(coll)
         );
      }
   }

   private static WardrobeModel.Data2 method118(JsonObject jsonObject, Map<String, Integer> map) {
      if (jsonObject == null) {
         return null;
      } else {
         float[] floats = method129(jsonObject.get("grip"), 3, 0.0F);
         Integer n = (Integer)map.get(method126(jsonObject, "arrowBone", ""));
         return new WardrobeModel.Data2(
            floats[0],
            floats[1],
            floats[2],
            method127(jsonObject, "angle", 0.0F),
            method127(jsonObject, "roll", 0.0F),
            method127(jsonObject, "scale", 1.0F),
            method120(method126(jsonObject, "trail", null)),
            method120(method126(jsonObject, "hit", null)),
            n == null ? -1 : n
         );
      }
   }

   private static WardrobeModel.Data method119(JsonObject jsonObject, Map<String, Integer> map) {
      if (jsonObject == null) {
         return null;
      } else {
         float[] floats = new float[18];
         boolean flag = false;
         JsonObject json = method124(jsonObject, "idlePose");
         if (json != null) {
            String[] strings = new String[]{"head", "body", "right_arm", "left_arm", "right_leg", "left_leg"};

            for (int i = 0; i < strings.length; i++) {
               if (json.has(strings[i])) {
                  float[] floats2 = method129(json.get(strings[i]), 3, 0.0F);

                  for (int j = 0; j < 3; j++) {
                     floats[i * 3 + j] = (float)Math.toRadians(floats2[j]);
                  }

                  flag = true;
               }
            }
         }

         return new WardrobeModel.Data(flag ? floats : null, !jsonObject.has("hideArmor") || method128(jsonObject, "hideArmor"), method115(method125(jsonObject, "aura"), map));
      }
   }

   public static ParticleOptions method120(String str) {
      if (str != null && !str.isBlank()) {
         String lowerCase = str.strip().toLowerCase(Locale.ROOT);
         if (lowerCase.startsWith("dust:")) {
            String[] strings = lowerCase.split(":");

            try {
               int color = Integer.parseInt(strings[1].replace("#", ""), 16);
               float scale = strings.length > 2 ? Float.parseFloat(strings[2]) : 1.0F;
               return new DustParticleOptions(color, scale);
            } catch (RuntimeException e) {
               return null;
            }
         } else {
            Identifier tryParse = Identifier.tryParse(lowerCase.contains(":") ? lowerCase : "minecraft:" + lowerCase);
            if (tryParse == null) {
               return null;
            } else {
               ParticleType particleType = BuiltInRegistries.PARTICLE_TYPE.getValue(tryParse);
               return particleType instanceof SimpleParticleType simpleParticleType ? simpleParticleType : null;
            }
         }
      } else {
         return null;
      }
   }

   private static int method121(String str) {
      if (str == null) {
         return -1;
      } else {
         try {
            return WardrobeModel.Type.valueOf(str.strip().toUpperCase(Locale.ROOT)).ordinal();
         } catch (IllegalArgumentException e) {
            return -1;
         }
      }
   }

   private static WardrobeModel.Type2 method122(String str) {
      try {
         return WardrobeModel.Type2.valueOf(str.strip().toUpperCase(Locale.ROOT));
      } catch (IllegalArgumentException e) {
         return WardrobeModel.Type2.NONE;
      }
   }

   private static List<JsonObject> method123(JsonArray jsonArray) {
      ArrayList arrayList = new ArrayList();

      for (JsonElement jsonElement : jsonArray) {
         if (jsonElement.isJsonObject()) {
            arrayList.add(jsonElement.getAsJsonObject());
         }
      }

      List<JsonObject> jsons = new ArrayList<>(arrayList.size());
      HashSet hashSet = new HashSet();
      boolean flag = true;

      while (!arrayList.isEmpty() && flag) {
         flag = false;
         int index = 0;

         while (index < arrayList.size()) {
            JsonObject jsonObject = (JsonObject)arrayList.get(index);
            String str = method126(jsonObject, "parent", null);
            if (str != null && !hashSet.contains(str)) {
               index++;
            } else {
               jsons.add(jsonObject);
               hashSet.add(method126(jsonObject, "name", ""));
               arrayList.remove(index);
               flag = true;
            }
         }
      }

      jsons.addAll(arrayList);
      return jsons;
   }

   private static JsonObject method124(JsonObject jsonObject, String memberName) {
      return jsonObject != null && jsonObject.has(memberName) && jsonObject.get(memberName).isJsonObject() ? jsonObject.getAsJsonObject(memberName) : null;
   }

   private static JsonArray method125(JsonObject jsonObject, String memberName) {
      return jsonObject != null && jsonObject.has(memberName) && jsonObject.get(memberName).isJsonArray() ? jsonObject.getAsJsonArray(memberName) : new JsonArray();
   }

   private static String method126(JsonObject jsonObject, String memberName, String str) {
      try {
         return jsonObject != null && jsonObject.has(memberName) && jsonObject.get(memberName).isJsonPrimitive() ? jsonObject.get(memberName).getAsString() : str;
      } catch (RuntimeException e) {
         return str;
      }
   }

   private static float method127(JsonObject jsonObject, String memberName, float f) {
      try {
         return jsonObject != null && jsonObject.has(memberName) && jsonObject.get(memberName).isJsonPrimitive() ? jsonObject.get(memberName).getAsFloat() : f;
      } catch (RuntimeException e) {
         return f;
      }
   }

   private static boolean method128(JsonObject jsonObject, String memberName) {
      try {
         return jsonObject != null && jsonObject.has(memberName) && jsonObject.get(memberName).isJsonPrimitive() && jsonObject.get(memberName).getAsBoolean();
      } catch (RuntimeException e) {
         return false;
      }
   }

   private static float[] method129(JsonElement jsonElement, int n, float f) {
      float[] floats = new float[n];
      Arrays.fill(floats, f);
      if (jsonElement != null && jsonElement.isJsonArray()) {
         JsonArray jsonArray = jsonElement.getAsJsonArray();

         for (int i = 0; i < n && i < jsonArray.size(); i++) {
            try {
               floats[i] = jsonArray.get(i).getAsFloat();
            } catch (RuntimeException e) {
               floats[i] = f;
            }
         }

         return floats;
      } else {
         return floats;
      }
   }

   private static final class Data {
      private final int intValue;
      private final int intValue2;
      private final float[] floatValues;
      private final float[] floatValues2;
      private final float[] floatValues3;

      public int intValue() {
         return this.intValue;
      }

      public int intValue2() {
         return this.intValue2;
      }

      public float[] floatValues() {
         return this.floatValues;
      }

      public float[] floatValues2() {
         return this.floatValues2;
      }

      public float[] floatValues3() {
         return this.floatValues3;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.intValue2, other.intValue2) && java.util.Objects.equals(this.floatValues, other.floatValues) && java.util.Objects.equals(this.floatValues2, other.floatValues2) && java.util.Objects.equals(this.floatValues3, other.floatValues3);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(intValue, intValue2, floatValues, floatValues2, floatValues3);
      }

      @Override
      public String toString() {
         return "Data[" + "intValue=" + this.intValue + ", " + "intValue2=" + this.intValue2 + ", " + "floatValues=" + this.floatValues + ", " + "floatValues2=" + this.floatValues2 + ", " + "floatValues3=" + this.floatValues3 + "]";
      }

      private Data(int intValue, int n, float[] floatValues, float[] floatValues2, float[] floatValues3) {
         this.intValue = intValue;
         this.intValue2 = n;
         this.floatValues = floatValues;
         this.floatValues2 = floatValues2;
         this.floatValues3 = floatValues3;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public int getIntValue2() {
         return this.intValue2;
      }

      public float[] getFloatValues() {
         return this.floatValues;
      }

      public float[] getFloatValues2() {
         return this.floatValues2;
      }

      public float[] getFloatValues3() {
         return this.floatValues3;
      }
   }
}
