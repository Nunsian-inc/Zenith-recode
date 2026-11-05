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
import java.util.Arrays;
import java.util.List;

public final class CenterMotorModel {
   public static final String text = "neuraim_java_center_motor_v2";
   public static final String text2 = "neuraim_center_motor_v2.json";
   private final int intValue;
   private final int intValue2;
   private final int intValue3;
   private final int intValue4;
   private final int intValue5;
   private final int[] intValues;
   private final int intValue6;
   private final int intValue7;
   private final float[] floatValues;
   private final float[] floatValues2;
   private final float[] floatValues3;
   private final float[] floatValues4;
   private final float[] floatValues5;
   private final float[][][] floatValues6;
   private final float[][][] floatValues7;
   private final float[][] floatValues8;
   private final float[][] floatValues9;
   private final float[][] floatValues10;
   private final float[][] floatValues11;
   private final float[][] floatValues12;
   private final float[] floatValues13;
   private final float[] floatValues14;
   private final float[] floatValues15;
   private final int[] intValues2 = new int[2];

   private CenterMotorModel(JsonObject json) {
      method4511("neuraim_java_center_motor_v2".equals(method4506(json, "format")), "unexpected center motor format");
      JsonObject json2 = method4504(json, "runtime");
      JsonObject json3 = method4504(json, "features");
      this.intValue = method4507(json2, "history_ticks");
      this.intValue2 = method4507(json2, "raw_input_dim");
      this.intValue3 = method4507(json2, "model_input_dim");
      this.intValues = method4509(json3, "model_feature_indices", this.intValue3);
      this.intValue6 = method4507(json3, "action_yaw_raw_index");
      this.intValue7 = method4507(json3, "action_pitch_raw_index");
      this.floatValues = method4508(json3, "mean", this.intValue3);
      this.floatValues2 = method4508(json3, "scale", this.intValue3);
      JsonObject json4 = method4504(json, "architecture");
      JsonObject json5 = method4504(json, "weights");
      this.intValue4 = method4507(json4, "hidden_dim");
      this.intValue5 = method4507(json4, "num_layers");
      method4511(this.intValue == 16 && this.intValue3 == 34 && this.intValue2 == 54 && this.intValue5 == 2, "unsupported architecture");
      this.floatValues6 = new float[this.intValue5][][];
      this.floatValues7 = new float[this.intValue5][][];
      this.floatValues8 = new float[this.intValue5][];
      this.floatValues9 = new float[this.intValue5][];

      for (int i = 0; i < this.intValue5; i++) {
         int n = i == 0 ? this.intValue3 : this.intValue4;
         this.floatValues6[i] = method4510(json5, "encoder.weight_ih_l" + i, 3 * this.intValue4, n);
         this.floatValues7[i] = method4510(json5, "encoder.weight_hh_l" + i, 3 * this.intValue4, this.intValue4);
         this.floatValues8[i] = method4508(json5, "encoder.bias_ih_l" + i, 3 * this.intValue4);
         this.floatValues9[i] = method4508(json5, "encoder.bias_hh_l" + i, 3 * this.intValue4);
      }

      this.floatValues3 = method4508(json5, "norm.weight", this.intValue4);
      this.floatValues4 = method4508(json5, "norm.bias", this.intValue4);
      this.floatValues10 = method4510(json5, "shared.0.weight", this.intValue4, this.intValue4);
      this.floatValues13 = method4508(json5, "shared.0.bias", this.intValue4);
      this.floatValues11 = method4510(json5, "mode_head.weight", 10, this.intValue4);
      this.floatValues14 = method4508(json5, "mode_head.bias", 10);
      this.floatValues12 = method4510(json5, "magnitude_head.weight", 2, this.intValue4);
      this.floatValues15 = method4508(json5, "magnitude_head.bias", 2);
      this.floatValues5 = method4508(json5, "action_scales", 2);
   }

   public static CenterMotorModel method4492(Path path) throws IOException {
      CenterMotorModel centerMotorModel;
      try (BufferedReader bufferedReader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
         centerMotorModel = method4494(bufferedReader);
      }

      return centerMotorModel;
   }

   public static CenterMotorModel method4493(InputStream resourceAsStream) throws IOException {
      CenterMotorModel centerMotorModel;
      try (InputStreamReader inputStreamReader = new InputStreamReader(resourceAsStream, StandardCharsets.UTF_8)) {
         centerMotorModel = method4494(inputStreamReader);
      }

      return centerMotorModel;
   }

   private static CenterMotorModel method4494(Reader bufferedReader) throws IOException {
      try {
         JsonObject json = (JsonObject)new Gson().fromJson(bufferedReader, JsonObject.class);
         method4511(json != null, "empty model JSON");
         return new CenterMotorModel(json);
      } catch (RuntimeException e) {
         throw new IOException("invalid center motor: " + e.getMessage(), e);
      }
   }

   public static CenterMotorModel method4495() {
      for (Path path : List.of(
         Path.of("models", "neuraim_center_motor_v2.json"),
         Path.of("run", "models", "neuraim_center_motor_v2.json"),
         Path.of("neuraim_center_motor_v2.json"),
         Path.of("..", "models", "neuraim_center_motor_v2.json")
      )) {
         if (Files.isRegularFile(path)) {
            try {
               CenterMotorModel centerMotorModel = method4492(path);
               System.out.println("[Neuraim] Loaded center motor " + path.toAbsolutePath());
               return centerMotorModel;
            } catch (IOException e) {
               System.err.println("[Neuraim] Failed to load " + path + ": " + e.getMessage());
            }
         }
      }

      InputStream resourceAsStream = CenterMotorModel.class.getClassLoader().getResourceAsStream("models/neuraim_center_motor_v2.json");
      if (resourceAsStream != null) {
         try {
            return method4493(resourceAsStream);
         } catch (IOException e) {
            System.err.println("[Neuraim] Bundled center motor is invalid: " + e.getMessage());
         }
      }

      System.err.println("[Neuraim] neuraim_center_motor_v2.json not found; TestRotationModeV3 disabled");
      return null;
   }

   public int getIntValue() {
      return this.intValue;
   }

   public int getIntValue2() {
      return this.intValue2;
   }

   public int[] method4496(float[][] floatses) {
      method4511(floatses != null && floatses.length == this.intValue, "history tick count mismatch");
      float[][] floatses2 = new float[this.intValue5][this.intValue4];

      for (float[] floats : floatses) {
         method4511(floats != null && floats.length == this.intValue2, "history feature width mismatch");
         float[] floats2 = new float[this.intValue3];

         for (int i = 0; i < this.intValue3; i++) {
            floats2[i] = (floats[this.intValues[i]] - this.floatValues[i]) / this.floatValues2[i];
         }

         for (int i = 0; i < this.intValue5; i++) {
            floatses2[i] = method4498(
               floats2, floatses2[i], this.floatValues6[i], this.floatValues7[i], this.floatValues8[i], this.floatValues9[i], this.intValue4
            );
            floats2 = floatses2[i];
         }
      }

      float[] floats = method4502(method4499(this.floatValues10, this.floatValues13, method4500(floatses2[this.intValue5 - 1], this.floatValues3, this.floatValues4)));
      float[] floats2 = method4499(this.floatValues11, this.floatValues14, floats);
      float[] floats3 = method4499(this.floatValues12, this.floatValues15, floats);
      if (this.intValues2[0] == 0 && this.intValues2[1] == 0) {
         this.method4497(floatses);
      }

      int[] ints = new int[2];

      for (int i = 0; i < 2; i++) {
         int n = 0;

         for (int j = 1; j < 5; j++) {
            if (floats2[i * 5 + j] > floats2[i * 5 + n]) {
               n = j;
            }
         }

         int n2 = this.intValues2[i] == 0 ? 1 : this.intValues2[i];

         int n3 = switch (n) {
            case 1 -> n2;
            case 2 -> -n2;
            case 3 -> 1;
            case 4 -> -1;
            default -> 0;
         };
         if (n3 == 0) {
            ints[i] = 0;
         } else {
            int n4 = method4503(1.0 + Math.exp(Math.max(-5.0, Math.min(5.0, (double)floats3[i]))) * this.floatValues5[i]);
            ints[i] = n3 * n4;
            this.intValues2[i] = n3;
         }
      }

      return ints;
   }

   public void reset() {
      Arrays.fill(this.intValues2, 0);
   }

   private void method4497(float[][] floatses) {
      int[] ints = new int[]{this.intValue6, this.intValue7};

      for (int i = 0; i < 2; i++) {
         for (int j = this.intValue - 1; j >= 0; j--) {
            float f = floatses[j][ints[i]];
            if (f != 0.0F) {
               this.intValues2[i] = f > 0.0F ? 1 : -1;
               break;
            }
         }
      }
   }

   private static float[] method4498(float[] floats, float[] floats2, float[][] floatValues10, float[][] floatses, float[] floatValues13, float[] floatValues132, int n) {
      float[] floats3 = method4499(floatValues10, floatValues13, floats);
      float[] floats4 = method4499(floatses, floatValues132, floats2);
      float[] floats5 = new float[n];

      for (int i = 0; i < n; i++) {
         float f = method4501(floats3[i] + floats4[i]);
         float f2 = method4501(floats3[n + i] + floats4[n + i]);
         float tanh = (float)Math.tanh(floats3[2 * n + i] + f * floats4[2 * n + i]);
         floats5[i] = (1.0F - f2) * tanh + f2 * floats2[i];
      }

      return floats5;
   }

   private static float[] method4499(float[][] floatValues10, float[] floatValues13, float[] floats) {
      float[] floats2 = new float[floatValues10.length];

      for (int i = 0; i < floatValues10.length; i++) {
         float f = floatValues13[i];

         for (int j = 0; j < floats.length; j++) {
            f += floatValues10[i][j] * floats[j];
         }

         floats2[i] = f;
      }

      return floats2;
   }

   private static float[] method4500(float[] floats, float[] floatValues3, float[] floatValues4) {
      float f = 0.0F;

      for (float f2 : floats) {
         f += f2;
      }

      f /= floats.length;
      float f2 = 0.0F;

      for (float f3 : floats) {
         float f4 = f3 - f;
         f2 += f4 * f4;
      }

      f2 /= floats.length;
      float f3 = (float)(1.0 / Math.sqrt(f2 + 1.0E-5));
      float[] floats2 = new float[floats.length];

      for (int i = 0; i < floats.length; i++) {
         floats2[i] = (floats[i] - f) * f3 * floatValues3[i] + floatValues4[i];
      }

      return floats2;
   }

   private static float method4501(float f) {
      return (float)(1.0 / (1.0 + Math.exp(-f)));
   }

   private static float[] method4502(float[] floats) {
      for (int i = 0; i < floats.length; i++) {
         floats[i] *= method4501(floats[i]);
      }

      return floats;
   }

   private static int method4503(double d) {
      return Double.isFinite(d) ? (int)Math.rint(d) : 0;
   }

   private static JsonObject method4504(JsonObject json, String memberName) {
      JsonElement jsonElement = json.get(memberName);
      method4511(jsonElement != null && jsonElement.isJsonObject(), "missing object " + memberName);
      return jsonElement.getAsJsonObject();
   }

   private static JsonArray method4505(JsonObject json, String memberName) {
      JsonElement jsonElement = json.get(memberName);
      method4511(jsonElement != null && jsonElement.isJsonArray(), "missing array " + memberName);
      return jsonElement.getAsJsonArray();
   }

   private static String method4506(JsonObject json, String memberName) {
      return json.get(memberName).getAsString();
   }

   private static int method4507(JsonObject json, String memberName) {
      return json.get(memberName).getAsInt();
   }

   private static float[] method4508(JsonObject json, String memberName, int n) {
      JsonArray jsonArray = method4505(json, memberName);
      method4511(jsonArray.size() == n, memberName + " length mismatch");
      float[] floats = new float[n];

      for (int i = 0; i < n; i++) {
         floats[i] = jsonArray.get(i).getAsFloat();
         method4511(Float.isFinite(floats[i]), "non-finite " + memberName);
      }

      return floats;
   }

   private static int[] method4509(JsonObject json, String memberName, int n) {
      JsonArray jsonArray = method4505(json, memberName);
      method4511(jsonArray.size() == n, memberName + " length mismatch");
      int[] ints = new int[n];

      for (int i = 0; i < n; i++) {
         ints[i] = jsonArray.get(i).getAsInt();
      }

      return ints;
   }

   private static float[][] method4510(JsonObject json, String memberName, int n, int n2) {
      JsonArray jsonArray = method4505(json, memberName);
      method4511(jsonArray.size() == n, memberName + " rows mismatch");
      float[][] floatses = new float[n][n2];

      for (int i = 0; i < n; i++) {
         JsonArray jsonArray2 = jsonArray.get(i).getAsJsonArray();
         method4511(jsonArray2.size() == n2, memberName + " cols mismatch");

         for (int j = 0; j < n2; j++) {
            floatses[i][j] = jsonArray2.get(j).getAsFloat();
         }
      }

      return floatses;
   }

   private static void method4511(boolean finite, String str) {
      if (!finite) {
         throw new IllegalArgumentException(str);
      }
   }
}
