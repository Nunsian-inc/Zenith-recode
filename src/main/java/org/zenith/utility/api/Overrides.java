package org.zenith.utility.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zenith.utility.misc.CosmeticType;
import org.zenith.utility.misc.Util31;

public final class Overrides {
   private static final Logger logger = LoggerFactory.getLogger("Zenith/Cosmetics");
   private static final String text = "overrides.json";
   private static final String text2 = "/assets/zenith/cosmetics/overrides.json";
   private final Path path;
   private volatile Map<String, Overrides.Data> entries = Map.of();
   private volatile JsonObject jsonObject;
   private volatile JsonObject jsonObject2 = new JsonObject();
   private volatile int intValue;
   private volatile int intValue2;
   private volatile int intValue3;

   public Overrides(Path path) {
      this.path = path.resolve("overrides.json");
      JsonObject jsonObject2 = JsonIo.method1009(this.path);
      if (jsonObject2 != null) {
         this.jsonObject2 = jsonObject2;
      }

      this.load();
   }

   public Overrides.Data method6458(String text) {
      return text == null ? null : this.entries.get(text);
   }

   public boolean isEmpty() {
      return this.entries.isEmpty();
   }

   public boolean method6459(String text) {
      Overrides.Data data = this.method6458(text);
      return data != null && data.isFlag();
   }

   public String method6460(CosmeticItemInfo cosmeticItemInfo) {
      if (cosmeticItemInfo == null) {
         return "";
      } else {
         Overrides.Data data = this.method6458(cosmeticItemInfo.getText());
         return data != null && data.name() != null && !data.name().isBlank() ? data.name() : cosmeticItemInfo.method7749();
      }
   }

   public String method6461(CosmeticItemInfo cosmeticItemInfo) {
      if (cosmeticItemInfo == null) {
         return "";
      } else {
         Overrides.Data data = this.method6458(cosmeticItemInfo.getText());
         if (data != null && data.getText2() != null && !data.getText2().isBlank()) {
            return "-".equals(data.getText2()) ? "" : data.getText2();
         } else {
            return cosmeticItemInfo.getText5();
         }
      }
   }

   public int size() {
      return this.entries.size();
   }

   public Path getPath() {
      return this.path;
   }

   public CosmeticType method6462(CosmeticItemInfo cosmeticItemInfo) {
      if (cosmeticItemInfo == null) {
         return CosmeticType.UNKNOWN;
      } else {
         Overrides.Data data = this.method6458(cosmeticItemInfo.getText());
         return data != null && data.getCosmeticType() != null ? data.getCosmeticType() : cosmeticItemInfo.getCosmeticType();
      }
   }

   public Util31.Data method6463(String text, Util31.Data data) {
      Overrides.Data data2 = this.method6458(text);
      return data2 != null && data != null && !data2.empty()
         ? new Util31.Data(
            data.getFloatValue() + data2.getFloatValues()[0],
            data.getFloatValue2() + data2.getFloatValues()[1],
            data.getFloatValue3() + data2.getFloatValues()[2],
            data.getFloatValue4() + data2.getFloatValues2()[0],
            data.getFloatValue5() + data2.getFloatValues2()[1],
            data.getFloatValue6() + data2.getFloatValues2()[2],
            data.getFloatValue7() * data2.getFloatValues3()[0],
            data.getFloatValue8() * data2.getFloatValues3()[1],
            data.getFloatValue9() * data2.getFloatValues3()[2],
            data.isFlag()
         )
         : data;
   }

   public void method6464(String text, Overrides.Data data) {
      if (text != null && !text.isBlank()) {
         JsonObject deepCopy = this.jsonObject2.deepCopy();
         if (data != null && !data.empty()) {
            deepCopy.add(text, method6469(data));
         } else {
            deepCopy.add(text, new JsonObject());
         }

         this.jsonObject2 = deepCopy;
         this.save();
         this.load();
      }
   }

   public void clear() {
      this.jsonObject2 = new JsonObject();
      this.save();
      this.load();
   }

   private void load() {
      HashMap map = new HashMap();
      this.intValue = method6467(method6468(), map);
      this.intValue2 = method6467(this.jsonObject, map);
      this.intValue3 = method6467(this.jsonObject2, map);
      this.entries = Map.copyOf(map);
      logger.info("Косметика: подгонка посадки — {} из сборки, {} с узла, {} своих", new Object[]{this.intValue, this.intValue2, this.intValue3});
   }

   public String method6465() {
      return this.intValue + " из сборки, " + this.intValue2 + " с узла, " + this.intValue3 + " своих";
   }

   public int method6466(byte[] bytes) {
      if (bytes != null && bytes.length != 0) {
         try {
            JsonElement string = JsonParser.parseString(new String(bytes, StandardCharsets.UTF_8));
            if (!string.isJsonObject()) {
               return -1;
            } else {
               this.jsonObject = string.getAsJsonObject();
               this.load();
               return this.jsonObject.size();
            }
         } catch (RuntimeException e) {
            logger.warn("Косметика: подгонка с узла не разобралась: {}", e.toString());
            return -1;
         }
      } else {
         return -1;
      }
   }

   public JsonObject getJsonObject2() {
      return this.jsonObject2;
   }

   private static int method6467(JsonObject jsonObject, Map<String, Overrides.Data> map) {
      if (jsonObject == null) {
         return 0;
      } else {
         int n = 0;

         for (Entry entry : jsonObject.entrySet()) {
            if (((JsonElement)entry.getValue()).isJsonObject()) {
               JsonObject json = ((JsonElement)entry.getValue()).getAsJsonObject();
               Overrides.Data data = new Overrides.Data(
                  method6471(json),
                  method6473(json, "offset", 0.0F),
                  method6473(json, "rotation", 0.0F),
                  method6473(json, "scale", 1.0F),
                  json.has("hidden") && json.get("hidden").isJsonPrimitive() && json.get("hidden").getAsBoolean(),
                  method6472(json, "name"),
                  method6472(json, "rarity")
               );
               n++;
               if (data.empty()) {
                  map.remove(entry.getKey());
               } else {
                  map.put((String)entry.getKey(), data);
               }
            }
         }

         return n;
      }
   }

   private static JsonObject method6468() {
      try {
         JsonObject jsonObject;
         try (InputStream resourceAsStream = Overrides.class.getResourceAsStream("/assets/zenith/cosmetics/overrides.json")) {
            if (resourceAsStream == null) {
               return null;
            }

            jsonObject = JsonParser.parseString(new String(resourceAsStream.readAllBytes(), StandardCharsets.UTF_8)).getAsJsonObject();
         }

         return jsonObject;
      } catch (Exception e) {
         logger.warn("Косметика: подгонка из сборки не прочиталась: {}", e.toString());
         return null;
      }
   }

   private void save() {
      JsonIo.method1010(this.path, this.jsonObject2);
   }

   private static JsonObject method6469(Overrides.Data data) {
      JsonObject json = new JsonObject();
      if (data.getCosmeticType() != null) {
         json.addProperty("type", data.getCosmeticType().name());
      }

      if (data.isFlag()) {
         json.addProperty("hidden", true);
      }

      if (data.name() != null && !data.name().isBlank()) {
         json.addProperty("name", data.name());
      }

      if (data.getText2() != null && !data.getText2().isBlank()) {
         json.addProperty("rarity", data.getText2());
      }

      json.add("offset", method6470(data.getFloatValues()));
      json.add("rotation", method6470(data.getFloatValues2()));
      json.add("scale", method6470(data.getFloatValues3()));
      return json;
   }

   private static JsonArray method6470(float[] floatValues) {
      JsonArray jsonArray = new JsonArray(3);

      for (float floatValue : floatValues) {
         jsonArray.add(floatValue);
      }

      return jsonArray;
   }

   private static CosmeticType method6471(JsonObject json) {
      if (json.has("type") && json.get("type").isJsonPrimitive()) {
         try {
            CosmeticType cosmeticType = CosmeticType.valueOf(json.get("type").getAsString().strip().toUpperCase(Locale.ROOT));
            return cosmeticType == CosmeticType.UNKNOWN ? null : cosmeticType;
         } catch (RuntimeException e) {
            return null;
         }
      } else {
         return null;
      }
   }

   private static String method6472(JsonObject json, String memberName) {
      try {
         return json.has(memberName) && json.get(memberName).isJsonPrimitive() ? json.get(memberName).getAsString() : "";
      } catch (RuntimeException e) {
         return "";
      }
   }

   private static float[] method6473(JsonObject json, String memberName, float f) {
      float[] floats = new float[]{f, f, f};
      if (json.has(memberName) && json.get(memberName).isJsonArray()) {
         JsonArray jsonArray = json.getAsJsonArray(memberName);

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

   public static final class Data {
      private final CosmeticType cosmeticType;
      private final float[] floatValues;
      private final float[] floatValues2;
      private final float[] floatValues3;
      private final boolean flag;
      private final String text;
      private final String text2;

      public CosmeticType cosmeticType() {
         return this.cosmeticType;
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

      public boolean flag() {
         return this.flag;
      }

      public String text() {
         return this.text;
      }

      public String text2() {
         return this.text2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.cosmeticType, other.cosmeticType) && java.util.Objects.equals(this.floatValues, other.floatValues) && java.util.Objects.equals(this.floatValues2, other.floatValues2) && java.util.Objects.equals(this.floatValues3, other.floatValues3) && java.util.Objects.equals(this.flag, other.flag) && java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(cosmeticType, floatValues, floatValues2, floatValues3, flag, text, text2);
      }

      @Override
      public String toString() {
         return "Data[" + "cosmeticType=" + this.cosmeticType + ", " + "floatValues=" + this.floatValues + ", " + "floatValues2=" + this.floatValues2 + ", " + "floatValues3=" + this.floatValues3 + ", " + "flag=" + this.flag + ", " + "text=" + this.text + ", " + "text2=" + this.text2 + "]";
      }
      public static final float[] CONST_floatValues = new float[]{0.0F, 0.0F, 0.0F};
      public static final float[] CONST_floatValues2 = new float[]{1.0F, 1.0F, 1.0F};

      public Data(CosmeticType cosmeticType, float[] floatValues, float[] floatValues2, float[] floatValues3) {
         this(cosmeticType, floatValues, floatValues2, floatValues3, false, "", "");
      }

      public Data(CosmeticType cosmeticType, float[] floatValues, float[] floatValues2, float[] floatValues3, boolean flag) {
         this(cosmeticType, floatValues, floatValues2, floatValues3, flag, "", "");
      }

      public Data(CosmeticType cosmeticType, float[] floatValues, float[] floatValues2, float[] floatValues3, boolean flag, String text, String str) {
         this.cosmeticType = cosmeticType;
         this.floatValues = floatValues;
         this.floatValues2 = floatValues2;
         this.floatValues3 = floatValues3;
         this.flag = flag;
         this.text = text;
         this.text2 = str;
      }

      public boolean empty() {
         return !this.flag
            && this.cosmeticType == null
            && method6474(this.text)
            && method6474(this.text2)
            && method6475(this.floatValues, CONST_floatValues)
            && method6475(this.floatValues2, CONST_floatValues)
            && method6475(this.floatValues3, CONST_floatValues2);
      }

      private static boolean method6474(String text) {
         return text == null || text.isBlank();
      }

      private static boolean method6475(float[] floatValues, float[] floats) {
         return floatValues[0] == floats[0] && floatValues[1] == floats[1] && floatValues[2] == floats[2];
      }

      public CosmeticType getCosmeticType() {
         return this.cosmeticType;
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

      public boolean isFlag() {
         return this.flag;
      }

      public String name() {
         return this.text;
      }

      public String getText2() {
         return this.text2;
      }
   }
}
