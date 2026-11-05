package org.zenith.utility.api;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class GhostActorModel {
   public static final String text = "neuraim_java_ghost_actor_v1";
   public static final String text2 = "neuraim_ghost_actor_v1.json";
   public static final int CONST_intValue = 0;
   public static final int CONST_intValue2 = 1;
   public static final int CONST_intValue3 = 2;
   public static final int CONST_intValue4 = 3;
   public static final int CONST_intValue5 = 4;
   private final String[] texts;
   private final float[] floatValues;
   private final int intValue;
   private final int intValue2;
   private final int intValue3;
   private final int intValue4;
   private final int intValue5;
   private final float[][] floatValues2;
   private final float[][] floatValues3;
   private final float[][] floatValues4;
   private final float[] floatValues5;
   private final float[] floatValues6;
   private final float[] floatValues7;
   private final float[] floatValues8;
   private final double doubleValue;
   private final double doubleValue2;
   private final double doubleValue3;
   private final double doubleValue4;
   private final double doubleValue5;
   private final double doubleValue6;
   private final double doubleValue7;
   private final double doubleValue8;

   private GhostActorModel(JsonObject json) {
      method7491("neuraim_java_ghost_actor_v1".equals(method7494(json, "format")), "unexpected ghost actor format");
      JsonObject json2 = method7492(json, "observation");
      JsonArray jsonArray = method7493(json2, "channels");
      this.texts = new String[jsonArray.size()];

      for (int i = 0; i < jsonArray.size(); i++) {
         this.texts[i] = jsonArray.get(i).getAsString();
      }

      this.intValue2 = this.texts.length;
      this.floatValues = method7497(json2, "scale", this.intValue2);
      this.intValue = method7496(json2, "frames");
      this.intValue5 = method7496(json2, "target_delay_ticks");
      method7491("concatenated_oldest_first".equals(method7494(json2, "layout")), "unsupported observation layout");
      method7491(this.intValue >= 1 && this.intValue2 >= 1, "empty observation contract");
      this.intValue3 = this.intValue * this.intValue2;
      JsonObject json3 = method7492(json, "network");
      this.intValue4 = method7496(json3, "hidden");
      method7491("relu".equals(method7494(json3, "activation")), "unsupported activation");
      JsonObject json4 = method7492(json3, "weights");
      this.floatValues2 = method7498(json4, "body.0.weight", this.intValue4, this.intValue3);
      this.floatValues5 = method7497(json4, "body.0.bias", this.intValue4);
      this.floatValues3 = method7498(json4, "body.2.weight", this.intValue4, this.intValue4);
      this.floatValues6 = method7497(json4, "body.2.bias", this.intValue4);
      JsonObject json5 = method7492(json, "action");
      JsonArray jsonArray2 = method7493(json5, "order");
      method7491(
         jsonArray2.size() == 5
            && "yaw_counts".equals(jsonArray2.get(0).getAsString())
            && "pitch_counts".equals(jsonArray2.get(1).getAsString())
            && "forward".equals(jsonArray2.get(2).getAsString())
            && "sideways".equals(jsonArray2.get(3).getAsString())
            && "hold".equals(jsonArray2.get(4).getAsString()),
         "unexpected action channel order"
      );
      this.floatValues4 = method7498(json4, "mean.weight", jsonArray2.size(), this.intValue4);
      this.floatValues7 = method7497(json4, "mean.bias", jsonArray2.size());
      this.floatValues8 = method7497(json5, "scale", jsonArray2.size());
      this.doubleValue = method7495(json5, "mouse_degrees_per_count");
      this.doubleValue2 = method7495(json5, "yaw_cap_counts");
      this.doubleValue3 = method7495(json5, "pitch_cap_counts");
      this.doubleValue4 = method7495(json5, "key_threshold");
      method7491(this.doubleValue > 0.0 && this.doubleValue2 > 0.0 && this.doubleValue3 > 0.0, "non-positive action geometry");
      JsonObject json6 = method7492(method7492(json, "server_side_rules"), "attack");
      this.doubleValue5 = method7495(json6, "recharge_per_tick");
      this.doubleValue6 = method7495(json6, "threshold_low");
      this.doubleValue7 = method7495(json6, "threshold_high");
      this.doubleValue8 = method7495(json6, "full_charge_probability");
      method7491(this.doubleValue5 > 0.0, "non-positive attack recharge");
   }

   public static GhostActorModel method7481(Path path) throws IOException {
      GhostActorModel ghostActorModel;
      try (BufferedReader bufferedReader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
         ghostActorModel = method7483(bufferedReader);
      }

      return ghostActorModel;
   }

   public static GhostActorModel method7482(InputStream resourceAsStream) throws IOException {
      GhostActorModel ghostActorModel;
      try (InputStreamReader inputStreamReader = new InputStreamReader(resourceAsStream, StandardCharsets.UTF_8)) {
         ghostActorModel = method7483(inputStreamReader);
      }

      return ghostActorModel;
   }

   private static GhostActorModel method7483(Reader bufferedReader) throws IOException {
      try {
         JsonObject json = (JsonObject)new Gson().fromJson(bufferedReader, JsonObject.class);
         method7491(json != null, "empty model JSON");
         return new GhostActorModel(json);
      } catch (RuntimeException e) {
         throw new IOException("invalid ghost actor: " + e.getMessage(), e);
      }
   }

   public static GhostActorModel method7484() {
      for (Path path : List.of(
         Path.of("models", "neuraim_ghost_actor_v1.json"),
         Path.of("run", "models", "neuraim_ghost_actor_v1.json"),
         Path.of("neuraim_ghost_actor_v1.json"),
         Path.of("..", "models", "neuraim_ghost_actor_v1.json")
      )) {
         if (Files.isRegularFile(path)) {
            try {
               GhostActorModel ghostActorModel = method7481(path);
               System.out.println("[Neuraim] Loaded ghost actor " + path.toAbsolutePath());
               return ghostActorModel;
            } catch (IOException e) {
               System.err.println("[Neuraim] Failed to load " + path + ": " + e.getMessage());
            }
         }
      }

      InputStream resourceAsStream = GhostActorModel.class.getClassLoader().getResourceAsStream("models/neuraim_ghost_actor_v1.json");
      if (resourceAsStream != null) {
         try {
            return method7482(resourceAsStream);
         } catch (IOException e) {
            System.err.println("[Neuraim] Bundled ghost actor is invalid: " + e.getMessage());
         }
      }

      System.err.println("[Neuraim] neuraim_ghost_actor_v1.json not found; TestRotationModeV4 disabled");
      return null;
   }

   public String[] method7485() {
      return (String[])this.texts.clone();
   }

   public int getIntValue() {
      return this.intValue;
   }

   public int getIntValue2() {
      return this.intValue2;
   }

   public int getIntValue5() {
      return this.intValue5;
   }

   public double getDoubleValue() {
      return this.doubleValue;
   }

   public double getDoubleValue5() {
      return this.doubleValue5;
   }

   public double getDoubleValue6() {
      return this.doubleValue6;
   }

   public double getDoubleValue7() {
      return this.doubleValue7;
   }

   public double getDoubleValue8() {
      return this.doubleValue8;
   }

   public GhostActorModel.Data method7486(float[][] floatses) {
      method7491(floatses != null && floatses.length == this.intValue, "history tick count mismatch");
      float[] floats = new float[this.intValue3];
      int n = 0;

      for (float[] floats2 : floatses) {
         method7491(floats2 != null && floats2.length == this.intValue2, "history channel width mismatch");

         for (int i = 0; i < this.intValue2; i++) {
            floats[n++] = floats2[i] / this.floatValues[i];
         }
      }

      float[] floats2 = method7489(method7488(this.floatValues2, this.floatValues5, floats, this.intValue4));
      float[] floats3 = method7489(method7488(this.floatValues3, this.floatValues6, floats2, this.intValue4));
      float[] floats4 = method7488(this.floatValues4, this.floatValues7, floats3, this.floatValues7.length);
      double[] doubles = new double[floats4.length];

      for (int i = 0; i < floats4.length; i++) {
         doubles[i] = Math.tanh(floats4[i]) * this.floatValues8[i];
      }

      boolean flag = doubles[4] > 0.0;
      long intValue = flag ? 0L : Math.round(method7490(doubles[0], -this.doubleValue2, this.doubleValue2));
      long l = flag ? 0L : Math.round(method7490(doubles[1], -this.doubleValue3, this.doubleValue3));
      return new GhostActorModel.Data((int)intValue, (int)l, this.method7487(doubles[2]), this.method7487(doubles[3]), flag);
   }

   private int method7487(double d) {
      if (d > this.doubleValue4) {
         return 1;
      } else {
         return d < -this.doubleValue4 ? -1 : 0;
      }
   }

   private static float[] method7488(float[][] floatValues2, float[] floatValues5, float[] floats, int length) {
      float[] floats2 = new float[length];

      for (int i = 0; i < length; i++) {
         float[] floats3 = floatValues2[i];
         float f = floatValues5[i];

         for (int j = 0; j < floats3.length; j++) {
            f += floats3[j] * floats[j];
         }

         floats2[i] = f;
      }

      return floats2;
   }

   private static float[] method7489(float[] floats) {
      for (int i = 0; i < floats.length; i++) {
         if (floats[i] < 0.0F) {
            floats[i] = 0.0F;
         }
      }

      return floats;
   }

   private static double method7490(double d, double d2, double d3) {
      return Math.max(d2, Math.min(d3, d));
   }

   private static void method7491(boolean flag, String str) {
      if (!flag) {
         throw new IllegalStateException(str);
      }
   }

   private static JsonObject method7492(JsonObject json, String memberName) {
      JsonElement jsonElement = json.get(memberName);
      method7491(jsonElement != null && jsonElement.isJsonObject(), "missing object " + memberName);
      return jsonElement.getAsJsonObject();
   }

   private static JsonArray method7493(JsonObject json, String memberName) {
      JsonElement jsonElement = json.get(memberName);
      method7491(jsonElement != null && jsonElement.isJsonArray(), "missing array " + memberName);
      return jsonElement.getAsJsonArray();
   }

   private static String method7494(JsonObject json, String memberName) {
      JsonElement jsonElement = json.get(memberName);
      method7491(jsonElement != null && jsonElement.isJsonPrimitive(), "missing string " + memberName);
      return jsonElement.getAsString();
   }

   private static double method7495(JsonObject json, String memberName) {
      JsonElement jsonElement = json.get(memberName);
      method7491(jsonElement != null && jsonElement.isJsonPrimitive(), "missing number " + memberName);
      return jsonElement.getAsDouble();
   }

   private static int method7496(JsonObject json, String memberName) {
      return (int)method7495(json, memberName);
   }

   private static float[] method7497(JsonObject json, String memberName, int size) {
      JsonArray jsonArray = method7493(json, memberName);
      method7491(jsonArray.size() == size, memberName + " must have " + size + " entries, got " + jsonArray.size());
      float[] floats = new float[size];

      for (int i = 0; i < size; i++) {
         floats[i] = jsonArray.get(i).getAsFloat();
      }

      return floats;
   }

   private static float[][] method7498(JsonObject json, String memberName, int size, int n) {
      JsonArray jsonArray = method7493(json, memberName);
      method7491(jsonArray.size() == size, memberName + " must have " + size + " rows, got " + jsonArray.size());
      float[][] floatses = new float[size][n];

      for (int i = 0; i < size; i++) {
         JsonArray jsonArray2 = jsonArray.get(i).getAsJsonArray();
         method7491(jsonArray2.size() == n, memberName + " row " + i + " must have " + n + " columns");

         for (int j = 0; j < n; j++) {
            floatses[i][j] = jsonArray2.get(j).getAsFloat();
         }
      }

      return floatses;
   }

   public static final class Data {
      private final int intValue;
      private final int intValue2;
      private final int intValue3;
      private final int intValue4;
      private final boolean flag;

      public int intValue() {
         return this.intValue;
      }

      public int intValue2() {
         return this.intValue2;
      }

      public int intValue3() {
         return this.intValue3;
      }

      public int intValue4() {
         return this.intValue4;
      }

      public boolean flag() {
         return this.flag;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.intValue2, other.intValue2) && java.util.Objects.equals(this.intValue3, other.intValue3) && java.util.Objects.equals(this.intValue4, other.intValue4) && java.util.Objects.equals(this.flag, other.flag);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(intValue, intValue2, intValue3, intValue4, flag);
      }

      @Override
      public String toString() {
         return "Data[" + "intValue=" + this.intValue + ", " + "intValue2=" + this.intValue2 + ", " + "intValue3=" + this.intValue3 + ", " + "intValue4=" + this.intValue4 + ", " + "flag=" + this.flag + "]";
      }

      public Data(int intValue, int n, int n2, int n3, boolean flag) {
         this.intValue = intValue;
         this.intValue2 = n;
         this.intValue3 = n2;
         this.intValue4 = n3;
         this.flag = flag;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public int getIntValue2() {
         return this.intValue2;
      }

      public int getIntValue3() {
         return this.intValue3;
      }

      public int getIntValue4() {
         return this.intValue4;
      }

      public boolean isFlag() {
         return this.flag;
      }
   }
}
