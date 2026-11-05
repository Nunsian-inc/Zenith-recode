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
import java.util.Random;

public final class EndpointPipelineModel {
   public static final String text = "neuraim_java_endpoint_pipeline_v1";
   public static final String text2 = "neuraim_endpoint_pipeline_v1.json";
   private static final int CONST_intValue = 2;
   private static final int CONST_intValue2 = 14;
   private static final int CONST_intValue3 = 6;
   private static final float CONST_floatValue = 50.0F;
   private final int intValue;
   private final int intValue2;
   private final int intValue3;
   private final int intValue4;
   private final int intValue5;
   private final int intValue6;
   private final int intValue7;
   private final float floatValue;
   private final float floatValue2;
   private final float[] floatValues;
   private final float[] floatValues2;
   private final float[] floatValues3;
   private final float[] floatValues4;
   private final int[] intValues;
   private final float[][] floatValues5;
   private final float[][] floatValues6;
   private final float[][] floatValues7;
   private final float[] floatValues8;
   private final float[] floatValues9;
   private final float[] floatValues10;
   private final float[][] floatValues11;
   private final float[][] floatValues12;
   private final float[][] floatValues13;
   private final float[] floatValues14;
   private final float[] floatValues15;
   private final float[] floatValues16;
   private final float floatValue3;
   private final float floatValue4;
   private final float floatValue5;
   private final float floatValue6;
   private final float[][] floatValues17;
   private final float[][] floatValues18;
   private final float[][] floatValues19;
   private final float[] floatValues20;
   private final float[] floatValues21;
   private final float[] floatValues22;
   private final float[][] floatValues23;
   private final float[][] floatValues24;
   private final float[] floatValues25;
   private final float[] floatValues26;
   private final float[][] floatValues27;
   private final float[][] floatValues28;
   private final float[][] floatValues29;
   private final float[] floatValues30;
   private final float[] floatValues31;
   private final float[] floatValues32;

   private EndpointPipelineModel(JsonObject json) {
      method5711("neuraim_java_endpoint_pipeline_v1".equals(method5705(json, "format")), "unexpected endpoint model format");
      JsonObject json2 = method5703(json, "runtime");
      this.intValue = method5707(json2, "history_ticks");
      this.intValue2 = method5707(json2, "endpoint_horizon_ticks");
      this.intValue3 = method5707(json2, "planner_noise_refresh_ticks");
      this.floatValue = method5706(json2, "motor_magnitude_std_fraction");
      method5711(this.intValue == 20 && this.intValue2 == 5, "unsupported temporal contract");
      JsonObject json3 = method5703(json, "features");
      JsonArray jsonArray = method5704(json3, "columns");
      this.intValue4 = jsonArray.size();
      method5711(this.intValue4 == 55, "closed-loop planner requires 55 features");
      method5711("movement_input_jump".equals(jsonArray.get(47).getAsString()), "planner feature order mismatch at movement_input_jump");
      this.intValues = method5709(json3, "motor_feature_indices", 14);
      JsonObject json4 = method5703(json, "planner");
      JsonObject json5 = method5703(json4, "architecture");
      method5711(method5707(json5, "input_dim") == this.intValue4, "planner input width mismatch");
      this.intValue5 = method5707(json5, "hidden_dim");
      method5711(method5707(json5, "num_layers") == 1, "only one planner GRU layer is supported");
      JsonObject json6 = method5703(json4, "scales");
      this.floatValues = method5708(json6, "feature_mean", this.intValue4);
      this.floatValues2 = method5708(json6, "feature_scale", this.intValue4);
      this.floatValues3 = method5708(json6, "log_magnitude_mean", 2);
      this.floatValues4 = method5708(json6, "log_magnitude_scale", 2);

      for (float f : this.floatValues2) {
         method5711(f > 0.0F, "invalid feature scale");
      }

      JsonObject json7 = method5703(json4, "weights");
      this.floatValues5 = method5710(json7, "encoder.weight_ih_l0", 3 * this.intValue5, this.intValue4);
      this.floatValues6 = method5710(json7, "encoder.weight_hh_l0", 3 * this.intValue5, this.intValue5);
      this.floatValues8 = method5708(json7, "encoder.bias_ih_l0", 3 * this.intValue5);
      this.floatValues9 = method5708(json7, "encoder.bias_hh_l0", 3 * this.intValue5);
      this.floatValues7 = method5710(json7, "context.0.weight", this.intValue5, this.intValue5);
      this.floatValues10 = method5708(json7, "context.0.bias", this.intValue5);
      this.floatValues11 = method5710(json7, "mode_head.weight", 4, this.intValue5);
      this.floatValues14 = method5708(json7, "mode_head.bias", 4);
      this.floatValues12 = method5710(json7, "sign_head.weight", 2, this.intValue5);
      this.floatValues15 = method5708(json7, "sign_head.bias", 2);
      this.floatValues13 = method5710(json7, "magnitude_head.weight", 4, this.intValue5);
      this.floatValues16 = method5708(json7, "magnitude_head.bias", 4);
      JsonObject json8 = method5703(json, "motor");
      JsonObject json9 = method5703(json8, "architecture");
      this.intValue6 = method5707(json9, "hidden_dim");
      this.intValue7 = method5707(json9, "style_dim");
      this.floatValue2 = method5706(json9, "sign_prior_strength");
      method5711(method5707(json9, "history_ticks") == this.intValue, "motor history mismatch");
      JsonObject json10 = method5703(json8, "scales");
      this.floatValue3 = method5706(json10, "action_yaw");
      this.floatValue4 = method5706(json10, "action_pitch");
      this.floatValue5 = method5706(json10, "endpoint_yaw");
      this.floatValue6 = method5706(json10, "endpoint_pitch");
      JsonObject json11 = method5703(json8, "weights");
      this.floatValues17 = method5710(json11, "history_encoder.weight_ih_l0", 3 * this.intValue6, 14);
      this.floatValues18 = method5710(json11, "history_encoder.weight_hh_l0", 3 * this.intValue6, this.intValue6);
      this.floatValues20 = method5708(json11, "history_encoder.bias_ih_l0", 3 * this.intValue6);
      this.floatValues21 = method5708(json11, "history_encoder.bias_hh_l0", 3 * this.intValue6);
      this.floatValues19 = method5710(json11, "context.0.weight", this.intValue6, this.intValue6 + 2 + this.intValue7 + 12);
      this.floatValues22 = method5708(json11, "context.0.bias", this.intValue6);
      this.floatValues23 = method5710(json11, "decoder.weight_ih", 3 * this.intValue6, 17);
      this.floatValues24 = method5710(json11, "decoder.weight_hh", 3 * this.intValue6, this.intValue6);
      this.floatValues25 = method5708(json11, "decoder.bias_ih", 3 * this.intValue6);
      this.floatValues26 = method5708(json11, "decoder.bias_hh", 3 * this.intValue6);
      this.floatValues27 = method5710(json11, "active_head.weight", 2, this.intValue6);
      this.floatValues30 = method5708(json11, "active_head.bias", 2);
      this.floatValues28 = method5710(json11, "sign_head.weight", 2, this.intValue6);
      this.floatValues31 = method5708(json11, "sign_head.bias", 2);
      this.floatValues29 = method5710(json11, "magnitude_head.weight", 2, this.intValue6);
      this.floatValues32 = method5708(json11, "magnitude_head.bias", 2);
   }

   public static EndpointPipelineModel method5681(Path path) throws IOException {
      EndpointPipelineModel endpointPipelineModel;
      try (BufferedReader bufferedReader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
         endpointPipelineModel = method5683(bufferedReader);
      }

      return endpointPipelineModel;
   }

   public static EndpointPipelineModel method5682(InputStream resourceAsStream) throws IOException {
      EndpointPipelineModel endpointPipelineModel;
      try (InputStreamReader inputStreamReader = new InputStreamReader(resourceAsStream, StandardCharsets.UTF_8)) {
         endpointPipelineModel = method5683(inputStreamReader);
      }

      return endpointPipelineModel;
   }

   private static EndpointPipelineModel method5683(Reader bufferedReader) throws IOException {
      try {
         JsonObject json = (JsonObject)new Gson().fromJson(bufferedReader, JsonObject.class);
         method5711(json != null, "empty endpoint model JSON");
         return new EndpointPipelineModel(json);
      } catch (RuntimeException e) {
         throw new IOException("invalid Neuraim endpoint model: " + e.getMessage(), e);
      }
   }

   public static EndpointPipelineModel method5684() {
      for (Path path : List.of(
         Path.of("models", "neuraim_endpoint_pipeline_v1.json"),
         Path.of("run", "models", "neuraim_endpoint_pipeline_v1.json"),
         Path.of("neuraim_endpoint_pipeline_v1.json"),
         Path.of("..", "models", "neuraim_endpoint_pipeline_v1.json")
      )) {
         if (Files.isRegularFile(path)) {
            try {
               EndpointPipelineModel endpointPipelineModel = method5681(path);
               System.out.println("[Neuraim] Loaded endpoint pipeline " + path.toAbsolutePath());
               return endpointPipelineModel;
            } catch (IOException e) {
               System.err.println("[Neuraim] Failed to load " + path + ": " + e.getMessage());
            }
         }
      }

      InputStream resourceAsStream = EndpointPipelineModel.class.getClassLoader().getResourceAsStream("models/neuraim_endpoint_pipeline_v1.json");
      if (resourceAsStream != null) {
         try {
            return method5682(resourceAsStream);
         } catch (IOException e) {
            System.err.println("[Neuraim] Bundled endpoint pipeline is invalid: " + e.getMessage());
         }
      }

      System.err.println("[Neuraim] neuraim_endpoint_pipeline_v1.json not found; TestRotationModeV3 disabled");
      return null;
   }

   public int[] method5685(float[][] floatses, float[] floats) {
      this.method5691(floatses);
      method5711(floats != null && floats.length == 5, "planner noise width mismatch");
      float[] floats2 = new float[this.intValue5];

      for (float[] floats3 : floatses) {
         float[] floats4 = new float[this.intValue4];

         for (int i = 0; i < this.intValue4; i++) {
            floats4[i] = (floats3[i] - this.floatValues[i]) / this.floatValues2[i];
         }

         floats2 = method5692(floats4, floats2, this.floatValues5, this.floatValues6, this.floatValues8, this.floatValues9, this.intValue5);
      }

      float[] floats3 = method5698(method5693(this.floatValues7, this.floatValues10, floats2));
      float[] floats4 = method5693(this.floatValues11, this.floatValues14, floats3);
      float[] floats5 = method5693(this.floatValues12, this.floatValues15, floats3);
      float[] floats6 = method5693(this.floatValues13, this.floatValues16, floats3);
      float f = method5695(floats[0]);
      float[] floats7 = method5694(floats4);
      int n = 3;
      float f2 = 0.0F;

      for (int i = 0; i < floats7.length; i++) {
         f2 += floats7[i];
         if (f <= f2) {
            n = i;
            break;
         }
      }

      int[] ints = new int[2];

      for (int i = 0; i < 2; i++) {
         if ((n & 1 << i) != 0) {
            float f3 = method5695(floats[1 + i]) < method5697(floats5[i]) ? 1.0F : -1.0F;
            float f4 = -3.0F + 4.5F * method5697(floats6[2 + i]);
            float f5 = floats6[i] + (float)Math.exp(f4) * floats[3 + i];
            float x = f5 * this.floatValues4[i] + this.floatValues3[i];
            int n2 = method5702(Math.max(1.0, Math.expm1(x)));
            ints[i] = f3 > 0.0F ? n2 : -n2;
         }
      }

      return ints;
   }

   public int[] method5686(float[][] floatses, int[] ints, float[] floats, Random random) {
      method5711(random != null, "random is required");
      float[] floats2 = new float[2];
      float[] floats3 = new float[2];
      float[] floats4 = new float[2];

      for (int i = 0; i < 2; i++) {
         floats2[i] = random.nextFloat();
         floats3[i] = random.nextFloat();
         floats4[i] = (float)random.nextGaussian();
      }

      return this.method5687(floatses, ints, floats, floats2, floats3, floats4);
   }

   public int[] method5687(float[][] floatses, int[] ints, float[] floats, float[] floats2, float[] floats3, float[] floats4) {
      this.method5691(floatses);
      method5711(ints != null && ints.length == 2, "endpoint width mismatch");
      method5711(floats != null && floats.length == this.intValue7, "motor style width mismatch");
      method5711(floats2 != null && floats2.length == 2, "active noise width mismatch");
      method5711(floats3 != null && floats3.length == 2, "sign noise width mismatch");
      method5711(floats4 != null && floats4.length == 2, "magnitude noise width mismatch");
      float[] floats5 = new float[]{
         this.floatValue3,
         this.floatValue4,
         this.floatValue3,
         this.floatValue4,
         this.floatValue3,
         this.floatValue4,
         50.0F,
         50.0F,
         50.0F,
         50.0F,
         1.0F,
         1.0F,
         this.floatValue3,
         this.floatValue4
      };
      float[] floats6 = new float[this.intValue6];

      for (float[] floats7 : floatses) {
         float[] floats8 = new float[14];

         for (int i = 0; i < floats8.length; i++) {
            floats8[i] = floats7[this.intValues[i]] / floats5[i];
         }

         floats6 = method5692(floats8, floats6, this.floatValues17, this.floatValues18, this.floatValues20, this.floatValues21, this.intValue6);
      }

      float[][] floatses2 = this.method5688(floatses);
      float[] floats7 = new float[]{
         this.floatValue3, this.floatValue3, 50.0F, 50.0F, 1.0F, this.floatValue3, this.floatValue4, this.floatValue4, 50.0F, 50.0F, 1.0F, this.floatValue4
      };
      float[] floats8 = new float[12];
      int n = 0;

      for (int i = 0; i < 2; i++) {
         for (int j = 0; j < 6; j++) {
            floats8[n] = floatses2[i][j] / floats7[n++];
         }
      }

      float[] floats9 = new float[]{ints[0] / this.floatValue5, ints[1] / this.floatValue6};
      float[] floats10 = method5700(floats6, floats9, floats, floats8);
      floats6 = method5699(method5693(this.floatValues19, this.floatValues22, floats10));
      float[] floats11 = floatses[floatses.length - 1];
      float[] floats12 = method5700(floats9, floats8, new float[]{floats11[27] / this.floatValue3, floats11[28] / this.floatValue4}, new float[]{0.2F});
      floats6 = method5692(floats12, floats6, this.floatValues23, this.floatValues24, this.floatValues25, this.floatValues26, this.intValue6);
      float[] floats13 = method5693(this.floatValues27, this.floatValues30, floats6);
      float[] floats14 = method5693(this.floatValues28, this.floatValues31, floats6);
      float[] floats15 = method5693(this.floatValues29, this.floatValues32, floats6);
      float[] floats16 = new float[]{this.floatValue3, this.floatValue4};
      int[] ints2 = new int[2];

      for (int i = 0; i < 2; i++) {
         if (!(floats2[i] >= method5697(floats13[i]))) {
            float f = Math.signum((float)ints[i]);
            boolean flag = floats3[i] < method5697(floats14[i] + this.floatValue2 * f);
            double d = Math.max(1.0, Math.abs(ints[i]) / 5.0 + Math.tanh(floats15[i]) * floats16[i] * 0.5);
            double d2 = d + floats16[i] * this.floatValue * floats4[i];
            int n2 = Math.max(1, method5702(d2));
            ints2[i] = flag ? n2 : -n2;
         }
      }

      return ints2;
   }

   private float[][] method5688(float[][] floatses) {
      float[] floats = floatses[floatses.length - 1];
      float[][] floatses2 = new float[2][6];
      int[] ints = new int[]{27, 28};
      int[] ints2 = new int[]{29, 30};
      int[] ints3 = new int[]{33, 34};
      int[] ints4 = new int[]{35, 36};
      int[] ints5 = new int[]{41, 42};

      for (int i = 0; i < 2; i++) {
         float f = 0.0F;

         for (int j = floatses.length - 1; j >= 0; j--) {
            float f2 = floatses[j][ints[i]];
            if (f2 != 0.0F) {
               f = Math.signum(f2);
               break;
            }
         }

         floatses2[i][0] = floats[ints[i]];
         floatses2[i][1] = floats[ints2[i]];
         floatses2[i][2] = floats[ints3[i]];
         floatses2[i][3] = f == 0.0F ? 50.0F : floats[ints4[i]];
         floatses2[i][4] = f;
         floatses2[i][5] = floats[ints5[i]];
      }

      return floatses2;
   }

   public float[] method5689(Random random) {
      return method5701(random, 5);
   }

   public float[] method5690(Random random) {
      return method5701(random, this.intValue7);
   }

   public int getIntValue4() {
      return this.intValue4;
   }

   public int getIntValue() {
      return this.intValue;
   }

   public int getIntValue3() {
      return this.intValue3;
   }

   private void method5691(float[][] floatses) {
      method5711(floatses != null && floatses.length == this.intValue, "history tick count mismatch");

      for (float[] floats : floatses) {
         method5711(floats != null && floats.length == this.intValue4, "history feature width mismatch");

         for (float f : floats) {
            method5711(Float.isFinite(f), "non-finite history feature");
         }
      }
   }

   private static float[] method5692(float[] floats, float[] floats2, float[][] floatValues5, float[][] floatValues6, float[] floatValues8, float[] floatValues9, int n) {
      float[] floats3 = method5693(floatValues5, floatValues8, floats);
      float[] floats4 = method5693(floatValues6, floatValues9, floats2);
      float[] floats5 = new float[n];

      for (int i = 0; i < n; i++) {
         float f = method5697(floats3[i] + floats4[i]);
         float f2 = method5697(floats3[n + i] + floats4[n + i]);
         float tanh = (float)Math.tanh(floats3[2 * n + i] + f * floats4[2 * n + i]);
         floats5[i] = (1.0F - f2) * tanh + f2 * floats2[i];
      }

      return floats5;
   }

   private static float[] method5693(float[][] floatValues7, float[] floatValues10, float[] floats) {
      float[] floats2 = new float[floatValues7.length];

      for (int i = 0; i < floatValues7.length; i++) {
         float f = floatValues10[i];

         for (int j = 0; j < floats.length; j++) {
            f += floatValues7[i][j] * floats[j];
         }

         floats2[i] = f;
      }

      return floats2;
   }

   private static float[] method5694(float[] floats) {
      float f = -Float.MAX_VALUE;

      for (float f2 : floats) {
         f = Math.max(f, f2);
      }

      float[] floats2 = new float[floats.length];
      float f2 = 0.0F;

      for (int i = 0; i < floats.length; i++) {
         floats2[i] = (float)Math.exp(floats[i] - f);
         f2 += floats2[i];
      }

      for (int i = 0; i < floats2.length; i++) {
         floats2[i] /= f2;
      }

      return floats2;
   }

   private static float method5695(float f) {
      return Math.max(1.0E-7F, Math.min(0.9999999F, 0.5F * (1.0F + method5696(f * 0.70710677F))));
   }

   private static float method5696(float f) {
      float f2 = f < 0.0F ? -1.0F : 1.0F;
      double d = Math.abs((double)f);
      double d2 = 1.0 / (1.0 + 0.5 * d);
      double d3 = d2
         * Math.exp(
            -d * d
               - 1.26551223
               + d2
                  * (
                     1.00002368
                        + d2
                           * (
                              0.37409196
                                 + d2
                                    * (
                                       0.09678418
                                          + d2
                                             * (
                                                -0.18628806
                                                   + d2
                                                      * (0.27886807 + d2 * (-1.13520398 + d2 * (1.48851587 + d2 * (-0.82215223 + d2 * 0.17087277))))
                                             )
                                    )
                           )
                  )
         );
      return (float)(f2 * (1.0 - d3));
   }

   private static float method5697(float f) {
      return (float)(1.0 / (1.0 + Math.exp(-f)));
   }

   private static float[] method5698(float[] floats) {
      for (int i = 0; i < floats.length; i++) {
         if (floats[i] < 0.0F) {
            floats[i] = (float)Math.expm1(floats[i]);
         }
      }

      return floats;
   }

   private static float[] method5699(float[] floats) {
      for (int i = 0; i < floats.length; i++) {
         floats[i] = (float)Math.tanh(floats[i]);
      }

      return floats;
   }

   private static float[] method5700(float[]... floatses) {
      int n = 0;

      for (float[] floats : floatses) {
         n += floats.length;
      }

      float[] floats = new float[n];
      int n2 = 0;

      for (float[] floats2 : floatses) {
         System.arraycopy(floats2, 0, floats, n2, floats2.length);
         n2 += floats2.length;
      }

      return floats;
   }

   private static float[] method5701(Random random, int n) {
      float[] floats = new float[n];

      for (int i = 0; i < n; i++) {
         floats[i] = (float)random.nextGaussian();
      }

      return floats;
   }

   private static int method5702(double d) {
      if (!Double.isFinite(d)) {
         return 0;
      } else if (d >= 2.147483647E9) {
         return Integer.MAX_VALUE;
      } else {
         return d <= -2.1474836E9F ? Integer.MIN_VALUE : (int)Math.round(d);
      }
   }

   private static JsonObject method5703(JsonObject json, String memberName) {
      JsonElement jsonElement = json.get(memberName);
      method5711(jsonElement != null && jsonElement.isJsonObject(), "missing object " + memberName);
      return jsonElement.getAsJsonObject();
   }

   private static JsonArray method5704(JsonObject json, String memberName) {
      JsonElement jsonElement = json.get(memberName);
      method5711(jsonElement != null && jsonElement.isJsonArray(), "missing array " + memberName);
      return jsonElement.getAsJsonArray();
   }

   private static String method5705(JsonObject json, String memberName) {
      JsonElement jsonElement = json.get(memberName);
      method5711(jsonElement != null && jsonElement.isJsonPrimitive(), "missing string " + memberName);
      return jsonElement.getAsString();
   }

   private static float method5706(JsonObject json, String memberName) {
      JsonElement jsonElement = json.get(memberName);
      method5711(jsonElement != null && jsonElement.isJsonPrimitive(), "missing number " + memberName);
      float f = jsonElement.getAsFloat();
      method5711(Float.isFinite(f), "non-finite " + memberName);
      return f;
   }

   private static int method5707(JsonObject json, String memberName) {
      return Math.round(method5706(json, memberName));
   }

   private static float[] method5708(JsonObject json, String memberName, int n) {
      JsonArray jsonArray = method5704(json, memberName);
      method5711(jsonArray.size() == n, memberName + " length mismatch");
      float[] floats = new float[n];

      for (int i = 0; i < n; i++) {
         floats[i] = jsonArray.get(i).getAsFloat();
         method5711(Float.isFinite(floats[i]), "non-finite " + memberName);
      }

      return floats;
   }

   private static int[] method5709(JsonObject json, String memberName, int n) {
      JsonArray jsonArray = method5704(json, memberName);
      method5711(jsonArray.size() == n, memberName + " length mismatch");
      int[] ints = new int[n];

      for (int i = 0; i < n; i++) {
         ints[i] = jsonArray.get(i).getAsInt();
      }

      return ints;
   }

   private static float[][] method5710(JsonObject json, String memberName, int n, int n2) {
      JsonArray jsonArray = method5704(json, memberName);
      method5711(jsonArray.size() == n, memberName + " rows mismatch");
      float[][] floatses = new float[n][n2];

      for (int i = 0; i < n; i++) {
         JsonArray jsonArray2 = jsonArray.get(i).getAsJsonArray();
         method5711(jsonArray2.size() == n2, memberName + " columns mismatch");

         for (int j = 0; j < n2; j++) {
            floatses[i][j] = jsonArray2.get(j).getAsFloat();
         }
      }

      return floatses;
   }

   private static void method5711(boolean finite, String str) {
      if (!finite) {
         throw new IllegalArgumentException(str);
      }
   }
}
