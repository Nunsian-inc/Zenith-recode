package org.zenith.utility.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class BurstSumModel {
   private static final String[] texts = new String[]{
      "target_center_delta_yaw_count_units",
      "target_center_delta_pitch_count_units",
      "target_box_min_delta_yaw_count_units",
      "target_box_max_delta_yaw_count_units",
      "target_box_min_delta_pitch_count_units",
      "target_box_max_delta_pitch_count_units",
      "target_box_width_yaw_count_units",
      "target_box_height_pitch_count_units",
      "target_center_distance",
      "sim_2t_target_center_delta_yaw_count_units",
      "sim_2t_target_center_delta_pitch_count_units",
      "target_motion_x",
      "target_motion_y",
      "target_motion_z",
      "player_motion_x",
      "player_motion_y",
      "player_motion_z"
   };
   private static final String text = "/assets/zenith/aimpipe/burst_sum_model.json";
   private static final Path[] paths = new Path[]{Path.of("burst_sum_model_6t.json"), Path.of("burst_sum_model.json")};
   private static final Path[] paths2 = new Path[]{
      Path.of("..", "scripts", "out", "burst_sum_model_6t.json"),
      Path.of("scripts", "out", "burst_sum_model_6t.json"),
      Path.of("..", "scripts", "test", "aimpipe", "out", "burst_sum_model.json"),
      Path.of("scripts", "test", "aimpipe", "out", "burst_sum_model.json")
   };
   private final BurstSumModel.Data[] datas;
   private final float[] floatValues;
   private final float[] floatValues2;
   private final float floatValue;
   private final float[][] floatValues3;
   private final float[] floatValues4;
   private final float[][] floatValues5;
   private final float[] floatValues6;
   private final float[][] floatValues7;
   private final float[] floatValues8;

   private BurstSumModel(
      BurstSumModel.Data[] datas,
      float[] floatValues,
      float[] floatValues2,
      float floatValue,
      float[][] floatValues3,
      float[] floatValues4,
      float[][] floatValues5,
      float[] floatValues6,
      float[][] floatValues7,
      float[] floatValues8
   ) {
      this.datas = datas;
      this.floatValues = floatValues;
      this.floatValues2 = floatValues2;
      this.floatValue = floatValue;
      this.floatValues3 = floatValues3;
      this.floatValues4 = floatValues4;
      this.floatValues5 = floatValues5;
      this.floatValues6 = floatValues6;
      this.floatValues7 = floatValues7;
      this.floatValues8 = floatValues8;
   }

   public int[] method479(float[] floats) {
      float[] floats2 = this.method480(floats);
      if (floats2 == null) {
         return null;
      } else {
         float[] floats3 = method487(floats2, this.floatValues3, this.floatValues4, true);
         float[] floats4 = method487(floats3, this.floatValues5, this.floatValues6, true);
         float[] floats5 = method487(floats4, this.floatValues7, this.floatValues8, false);
         int[] ints = new int[2];

         for (int i = 0; i < ints.length; i++) {
            double d = floats5[i] * this.floatValues2[i] + this.floatValues[i];
            d = Math.signum(d) * Math.expm1(Math.abs(d)) * this.floatValue;
            if (!Double.isFinite(d) || d < -2.1474836E9F || d > 2.147483647E9) {
               return null;
            }

            ints[i] = (int)Math.round(d);
         }

         return ints;
      }
   }

   private float[] method480(float[] floats) {
      if (floats != null && floats.length == this.datas.length) {
         float[] floats2 = new float[this.datas.length];

         for (int i = 0; i < floats2.length; i++) {
            float f = method486(floats[i], this.datas[i]);
            f = (f - this.datas[i].getFloatValue2()) / this.datas[i].getFloatValue3();
            if (!Float.isFinite(f)) {
               return null;
            }

            floats2[i] = f;
         }

         return floats2;
      } else {
         return null;
      }
   }

   public static BurstSumModel method481() {
      for (Path path : paths) {
         BurstSumModel burstSumModel = method482(path);
         if (burstSumModel != null) {
            return burstSumModel;
         }
      }

      for (Path path : paths2) {
         BurstSumModel burstSumModel = method482(path);
         if (burstSumModel != null) {
            return burstSumModel;
         }
      }

      return method483();
   }

   private static BurstSumModel method482(Path path) {
      try {
         return !Files.isRegularFile(path) ? null : method484(Files.readString(path, StandardCharsets.UTF_8), path.toAbsolutePath().toString());
      } catch (Throwable e) {
         System.err.println("[BurstSumModel] failed to load " + path + ": " + e);
         return null;
      }
   }

   private static BurstSumModel method483() {
      try {
         BurstSumModel burstSumModel;
         try (InputStream resourceAsStream = BurstSumModel.class.getResourceAsStream("/assets/zenith/aimpipe/burst_sum_model.json")) {
            if (resourceAsStream == null) {
               return null;
            }

            burstSumModel = method484(new String(resourceAsStream.readAllBytes(), StandardCharsets.UTF_8), "/assets/zenith/aimpipe/burst_sum_model.json");
         }

         return burstSumModel;
      } catch (Throwable e) {
         System.err.println("[BurstSumModel] failed to load /assets/zenith/aimpipe/burst_sum_model.json: " + e);
         return null;
      }
   }

   private static BurstSumModel method484(String string, String str) {
      JsonObject jsonObject = JsonParser.parseString(string).getAsJsonObject();
      if (jsonObject.get("format_version").getAsInt() != 1) {
         throw new IllegalStateException("unsupported burst-sum model format");
      } else {
         JsonArray jsonArray = jsonObject.getAsJsonArray("feature_spec");
         if (jsonArray.size() != texts.length) {
            throw new IllegalStateException("burst-sum feature count mismatch");
         } else {
            BurstSumModel.Data[] datas = new BurstSumModel.Data[jsonArray.size()];

            for (int i = 0; i < datas.length; i++) {
               JsonObject json = jsonArray.get(i).getAsJsonObject();
               if (!texts[i].equals(json.get("name").getAsString())) {
                  throw new IllegalStateException("burst-sum feature order mismatch at " + i);
               }

               datas[i] = new BurstSumModel.Data(
                  json.get("transform").getAsString(), json.get("scale").getAsFloat(), json.get("mean").getAsFloat(), json.get("std").getAsFloat()
               );
               if (!Float.isFinite(datas[i].getFloatValue3()) || datas[i].getFloatValue3() <= 0.0F) {
                  throw new IllegalStateException("invalid burst-sum feature normalization");
               }
            }

            JsonObject json = jsonObject.has("output_transform") ? jsonObject.getAsJsonObject("output_transform") : jsonObject.getAsJsonObject("output");
            if (json.has("type") && !"signed_mu".equals(json.get("type").getAsString())) {
               throw new IllegalStateException("unsupported burst-sum output transform");
            } else {
               float floatValue = json.get("scale").getAsFloat();
               float[] floatValues = method488(json.getAsJsonArray("mean"));
               float[] floatValues2 = method488(json.getAsJsonArray("std"));
               float[][] floatValues3;
               float[] floatValues4;
               float[][] floatses;
               float[] floats;
               float[][] floatValues32;
               float[] floatValues42;
               if (jsonObject.has("network")) {
                  JsonObject jsonObject2 = jsonObject.getAsJsonObject("network");
                  if (!"tanh".equals(jsonObject2.get("activation").getAsString())) {
                     throw new IllegalStateException("unsupported burst-sum activation");
                  }

                  JsonArray jsonArray2 = jsonObject2.getAsJsonArray("layers");
                  if (jsonArray2.size() != 3) {
                     throw new IllegalStateException("burst-sum network must contain three layers");
                  }

                  JsonObject jsonObject3 = jsonArray2.get(0).getAsJsonObject();
                  JsonObject jsonObject4 = jsonArray2.get(1).getAsJsonObject();
                  JsonObject jsonObject5 = jsonArray2.get(2).getAsJsonObject();
                  floatValues3 = method489(jsonObject3.getAsJsonArray("weight"));
                  floatValues4 = method488(jsonObject3.getAsJsonArray("bias"));
                  floatses = method489(jsonObject4.getAsJsonArray("weight"));
                  floats = method488(jsonObject4.getAsJsonArray("bias"));
                  floatValues32 = method489(jsonObject5.getAsJsonArray("weight"));
                  floatValues42 = method488(jsonObject5.getAsJsonArray("bias"));
               } else {
                  JsonObject jsonObject2 = jsonObject.getAsJsonObject("weights");
                  floatValues3 = method489(jsonObject2.getAsJsonArray("layer_0_weight"));
                  floatValues4 = method488(jsonObject2.getAsJsonArray("layer_0_bias"));
                  floatses = method489(jsonObject2.getAsJsonArray("layer_1_weight"));
                  floats = method488(jsonObject2.getAsJsonArray("layer_1_bias"));
                  floatValues32 = method489(jsonObject2.getAsJsonArray("layer_2_weight"));
                  floatValues42 = method488(jsonObject2.getAsJsonArray("layer_2_bias"));
               }

               method485(floatValues3, floatValues4, datas.length, "layer 0");
               method485(floatses, floats, floatValues4.length, "layer 1");
               method485(floatValues32, floatValues42, floats.length, "layer 2");
               if (floatValues42.length == 2 && floatValues.length == 2 && floatValues2.length == 2 && Float.isFinite(floatValue) && !(floatValue <= 0.0F)) {
                  BurstSumModel burstSumModel = new BurstSumModel(datas, floatValues, floatValues2, floatValue, floatValues3, floatValues4, floatses, floats, floatValues32, floatValues42);
                  System.out.println("[BurstSumModel] loaded " + str + " (features=" + datas.length + ", hidden=" + floatValues4.length + ")");
                  return burstSumModel;
               } else {
                  throw new IllegalStateException("invalid burst-sum output shape");
               }
            }
         }
      }
   }

   private static void method485(float[][] floatValues3, float[] floatValues4, int length, String str) {
      if (floatValues3.length != floatValues4.length) {
         throw new IllegalStateException(str + " output size mismatch");
      } else {
         for (float[] floats : floatValues3) {
            if (floats.length != length) {
               throw new IllegalStateException(str + " input size mismatch");
            }
         }
      }
   }

   private static float method486(float f, BurstSumModel.Data data) {
      String text = data.getText();

      return switch (text) {
         case "mu" -> (float)(Math.signum(f) * Math.log1p(Math.abs(f) / data.getFloatValue()));
         case "log" -> (float)Math.log1p(Math.max(f, 0.0F));
         case "lin", "linear" -> f;
         default -> throw new IllegalStateException("unknown burst-sum feature transform " + data.getText());
      };
   }

   private static float[] method487(float[] floats, float[][] floatValues3, float[] floatValues4, boolean flag) {
      float[] floats2 = new float[floatValues4.length];

      for (int i = 0; i < floats2.length; i++) {
         double x = floatValues4[i];

         for (int j = 0; j < floats.length; j++) {
            x += floatValues3[i][j] * floats[j];
         }

         floats2[i] = flag ? (float)Math.tanh(x) : (float)x;
      }

      return floats2;
   }

   private static float[] method488(JsonArray jsonArray) {
      float[] floats = new float[jsonArray.size()];

      for (int i = 0; i < floats.length; i++) {
         floats[i] = jsonArray.get(i).getAsFloat();
      }

      return floats;
   }

   private static float[][] method489(JsonArray jsonArray) {
      float[][] floatses = new float[jsonArray.size()][];

      for (int i = 0; i < floatses.length; i++) {
         floatses[i] = method488(jsonArray.get(i).getAsJsonArray());
      }

      return floatses;
   }

   private static final class Data {
      private final String text;
      private final float floatValue;
      private final float floatValue2;
      private final float floatValue3;

      public String text() {
         return this.text;
      }

      public float floatValue() {
         return this.floatValue;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      public float floatValue3() {
         return this.floatValue3;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.floatValue3, other.floatValue3);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, floatValue, floatValue2, floatValue3);
      }

      @Override
      public String toString() {
         return "Data[" + "text=" + this.text + ", " + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "floatValue3=" + this.floatValue3 + "]";
      }

      private Data(String text, float floatValue, float f, float f2) {
         this.text = text;
         this.floatValue = floatValue;
         this.floatValue2 = f;
         this.floatValue3 = f2;
      }

      public String getText() {
         return this.text;
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
   }
}
