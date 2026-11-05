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

public final class TandemModelData {
   public static final String text = "neuraim_java_contract_tandem_v1";
   public static final String text2 = "neuraim_contract_tandem_v1.json";
   private static final int CONST_intValue = 2;
   private static final int CONST_intValue2 = 6;
   private static final int CONST_intValue3 = 14;
   private static final int CONST_intValue4 = 6;
   private static final int CONST_intValue5 = 5;
   private static final float CONST_floatValue = 50.0F;
   private final int intValue;
   private final int intValue2;
   private final int intValue3;
   private final int intValue4;
   private final int intValue5;
   private final int intValue6;
   private final int intValue7;
   private final int intValue8;
   private final int intValue9;
   private final int intValue10;
   private final int intValue11;
   private final int intValue12;
   private final int intValue13;
   private final float floatValue;
   private final float floatValue2;
   private final float floatValue3;
   private final int[] intValues;
   private final int[] intValues2;
   private final float[] floatValues;
   private final float[] floatValues2;
   private final float[] floatValues3;
   private final float[] floatValues4;
   private final float[] floatValues5;
   private final float[] floatValues6;
   private final float[][][] floatValues7;
   private final float[][][] floatValues8;
   private final float[][] floatValues9;
   private final float[][] floatValues10;
   private final float[] floatValues11;
   private final float[] floatValues12;
   private final float[][] floatValues13;
   private final float[][] floatValues14;
   private final float[][] floatValues15;
   private final float[] floatValues16;
   private final float[] floatValues17;
   private final float[] floatValues18;
   private final float[][][] floatValues19;
   private final float[][][] floatValues20;
   private final float[][] floatValues21;
   private final float[][] floatValues22;
   private final float[][] floatValues23;
   private final float[][] floatValues24;
   private final float[][] floatValues25;
   private final float[][] floatValues26;
   private final float[][] floatValues27;
   private final float[][] floatValues28;
   private final float[][] floatValues29;
   private final float[] floatValues30;
   private final float[] floatValues31;
   private final float[] floatValues32;
   private final float[] floatValues33;
   private final float[] floatValues34;
   private final float[] floatValues35;
   private final float[] floatValues36;
   private final float[] floatValues37;
   private final float[] floatValues38;

   private TandemModelData(JsonObject json) {
      method3188("neuraim_java_contract_tandem_v1".equals(method3182(json, "format")), "unexpected contract model format");
      JsonObject json2 = method3180(json, "runtime");
      this.intValue = method3184(json2, "history_ticks");
      this.intValue2 = method3184(json2, "block_ticks");
      this.floatValue = method3183(json2, "shared_noise_scale");
      method3188(this.intValue == 20 && this.intValue2 == 4, "unsupported temporal contract");
      method3188("argmax".equals(method3182(json2, "hit_selection")), "hit mode mismatch");
      method3188("argmax".equals(method3182(json2, "component_selection")), "component mode mismatch");
      JsonObject json3 = method3180(json, "features");
      this.intValue3 = method3181(json3, "raw_columns").size();
      this.intValue4 = method3181(json3, "planner_columns").size();
      method3188(this.intValue3 == 54 && this.intValue4 == 34, "feature width mismatch");
      this.intValues = method3186(json3, "planner_feature_indices", this.intValue4);
      this.intValues2 = method3186(json3, "motor_feature_indices", 14);
      JsonObject json4 = method3180(json, "planner");
      JsonObject json5 = method3180(json4, "architecture");
      method3188(method3184(json5, "input_dim") == this.intValue4, "planner input mismatch");
      this.intValue5 = method3184(json5, "hidden_dim");
      this.intValue6 = method3184(json5, "num_layers");
      this.intValue7 = method3184(json5, "mixture_components");
      this.intValue8 = method3184(json5, "residual_rank");
      method3188(this.intValue6 == 2 && this.intValue8 == 2, "unsupported planner architecture");
      JsonObject json6 = method3180(json4, "scales");
      this.floatValues = method3185(json6, "feature_mean", this.intValue4);
      this.floatValues2 = method3185(json6, "feature_scale", this.intValue4);
      this.floatValues3 = method3185(json6, "contract_mean", 6);
      this.floatValues4 = method3185(json6, "contract_scale", 6);
      this.floatValues5 = method3185(json6, "contract_min", 6);
      this.floatValues6 = method3185(json6, "contract_max", 6);
      JsonObject json7 = method3180(json4, "weights");
      this.floatValues7 = new float[this.intValue6][][];
      this.floatValues8 = new float[this.intValue6][][];
      this.floatValues9 = new float[this.intValue6][];
      this.floatValues10 = new float[this.intValue6][];

      for (int i = 0; i < this.intValue6; i++) {
         int n = i == 0 ? this.intValue4 : this.intValue5;
         this.floatValues7[i] = method3187(json7, "encoder.weight_ih_l" + i, 3 * this.intValue5, n);
         this.floatValues8[i] = method3187(json7, "encoder.weight_hh_l" + i, 3 * this.intValue5, this.intValue5);
         this.floatValues9[i] = method3185(json7, "encoder.bias_ih_l" + i, 3 * this.intValue5);
         this.floatValues10[i] = method3185(json7, "encoder.bias_hh_l" + i, 3 * this.intValue5);
      }

      this.floatValues11 = method3185(json7, "context.0.weight", this.intValue5);
      this.floatValues12 = method3185(json7, "context.0.bias", this.intValue5);
      this.floatValues13 = method3187(json7, "context.1.weight", this.intValue5, this.intValue5);
      this.floatValues16 = method3185(json7, "context.1.bias", this.intValue5);
      this.floatValues14 = method3187(json7, "hit_head.weight", 5, this.intValue5);
      this.floatValues17 = method3185(json7, "hit_head.bias", 5);
      int n = 7 + 6 * this.intValue8 + 6;
      this.floatValues15 = method3187(json7, "contract_head.weight", 5 * this.intValue7 * n, this.intValue5);
      this.floatValues18 = method3185(json7, "contract_head.bias", 5 * this.intValue7 * n);
      JsonObject json8 = method3180(json, "motor");
      JsonObject json9 = method3180(json8, "architecture");
      this.intValue9 = method3184(json9, "hidden_dim");
      this.intValue10 = method3184(json9, "history_layers");
      this.intValue11 = method3184(json9, "contract_dim");
      this.intValue12 = method3184(json9, "mixture_components");
      this.intValue13 = method3184(json9, "maximum_magnitude");
      method3188(this.intValue10 == 2, "unsupported motor architecture");
      JsonObject json10 = method3180(json8, "action_scales");
      this.floatValue2 = method3183(json10, "yaw");
      this.floatValue3 = method3183(json10, "pitch");
      JsonObject json11 = method3180(json8, "weights");
      this.floatValues19 = new float[this.intValue10][][];
      this.floatValues20 = new float[this.intValue10][][];
      this.floatValues21 = new float[this.intValue10][];
      this.floatValues22 = new float[this.intValue10][];

      for (int i = 0; i < this.intValue10; i++) {
         int n2 = i == 0 ? 14 : this.intValue9;
         this.floatValues19[i] = method3187(json11, "history_encoder.weight_ih_l" + i, 3 * this.intValue9, n2);
         this.floatValues20[i] = method3187(json11, "history_encoder.weight_hh_l" + i, 3 * this.intValue9, this.intValue9);
         this.floatValues21[i] = method3185(json11, "history_encoder.bias_ih_l" + i, 3 * this.intValue9);
         this.floatValues22[i] = method3185(json11, "history_encoder.bias_hh_l" + i, 3 * this.intValue9);
      }

      this.floatValues23 = method3187(json11, "initial_hidden.0.weight", this.intValue9, this.intValue9 + 12);
      this.floatValues30 = method3185(json11, "initial_hidden.0.bias", this.intValue9);
      this.floatValues24 = method3187(json11, "contract_encoder.0.weight", this.intValue11, 8);
      this.floatValues31 = method3185(json11, "contract_encoder.0.bias", this.intValue11);
      this.floatValues25 = method3187(json11, "contract_encoder.2.weight", this.intValue11, this.intValue11);
      this.floatValues32 = method3185(json11, "contract_encoder.2.bias", this.intValue11);
      this.floatValues33 = method3185(json11, "contract_encoder.4.weight", this.intValue11);
      this.floatValues34 = method3185(json11, "contract_encoder.4.bias", this.intValue11);
      this.floatValues26 = method3187(json11, "decoder.weight_ih", 3 * this.intValue9, 21 + this.intValue11);
      this.floatValues27 = method3187(json11, "decoder.weight_hh", 3 * this.intValue9, this.intValue9);
      this.floatValues35 = method3185(json11, "decoder.bias_ih", 3 * this.intValue9);
      this.floatValues36 = method3185(json11, "decoder.bias_hh", 3 * this.intValue9);
      this.floatValues28 = method3187(json11, "mode_head.weight", 10, this.intValue9);
      this.floatValues37 = method3185(json11, "mode_head.bias", 10);
      this.floatValues29 = method3187(json11, "mixture_head.weight", 2 * this.intValue12 * 3, this.intValue9);
      this.floatValues38 = method3185(json11, "mixture_head.bias", 2 * this.intValue12 * 3);
   }

   public static TandemModelData method3152(Path path) throws IOException {
      TandemModelData tandemModelData;
      try (BufferedReader bufferedReader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
         tandemModelData = method3154(bufferedReader);
      }

      return tandemModelData;
   }

   public static TandemModelData method3153(InputStream resourceAsStream) throws IOException {
      TandemModelData tandemModelData;
      try (InputStreamReader inputStreamReader = new InputStreamReader(resourceAsStream, StandardCharsets.UTF_8)) {
         tandemModelData = method3154(inputStreamReader);
      }

      return tandemModelData;
   }

   private static TandemModelData method3154(Reader bufferedReader) throws IOException {
      try {
         JsonObject json = (JsonObject)new Gson().fromJson(bufferedReader, JsonObject.class);
         method3188(json != null, "empty model JSON");
         return new TandemModelData(json);
      } catch (RuntimeException e) {
         throw new IOException("invalid Neuraim contract model: " + e.getMessage(), e);
      }
   }

   public static TandemModelData method3155() {
      for (Path path : List.of(
         Path.of("models", "neuraim_contract_tandem_v1.json"),
         Path.of("run", "models", "neuraim_contract_tandem_v1.json"),
         Path.of("neuraim_contract_tandem_v1.json"),
         Path.of("..", "models", "neuraim_contract_tandem_v1.json")
      )) {
         if (Files.isRegularFile(path)) {
            try {
               TandemModelData tandemModelData = method3152(path);
               System.out.println("[Neuraim] Loaded contract tandem " + path.toAbsolutePath());
               return tandemModelData;
            } catch (IOException e) {
               System.err.println("[Neuraim] Failed to load " + path + ": " + e.getMessage());
            }
         }
      }

      InputStream resourceAsStream = TandemModelData.class.getClassLoader().getResourceAsStream("models/neuraim_contract_tandem_v1.json");
      if (resourceAsStream != null) {
         try {
            return method3153(resourceAsStream);
         } catch (IOException e) {
            System.err.println("[Neuraim] Bundled contract tandem is invalid: " + e.getMessage());
         }
      }

      System.err.println("[Neuraim] neuraim_contract_tandem_v1.json not found; TestRotationModeV3 disabled");
      return null;
   }

   public TandemModelData.Entry2 method3156(float[][] floatses) {
      this.method3166(floatses);
      float[][] floatses2 = new float[this.intValue10][this.intValue9];

      for (float[] floats : floatses) {
         float[] floatValues = this.method3162(floats);

         for (int i = 0; i < this.intValue10; i++) {
            floatses2[i] = method3167(
               floatValues, floatses2[i], this.floatValues19[i], this.floatValues20[i], this.floatValues21[i], this.floatValues22[i], this.intValue9
            );
            floatValues = floatses2[i];
         }
      }

      float[][] floatValues2 = this.method3163(floatses);
      float[] floatValues = method3176(method3168(this.floatValues23, this.floatValues30, method3177(floatses2[this.intValue10 - 1], this.method3164(floatValues2))));
      return new TandemModelData.Entry2(floatValues, floatValues2);
   }

   public TandemModelData.Entry method3157(TandemModelData.Entry2 entry2, float[][] floatses, Random random) {
      method3188(random != null, "random is required");
      return this.method3158(entry2, floatses, new float[]{(float)random.nextGaussian(), (float)random.nextGaussian()});
   }

   public TandemModelData.Entry method3158(TandemModelData.Entry2 entry2, float[][] floatses, float[] floats) {
      method3188(entry2 != null, "session is required");
      this.method3166(floatses);
      method3188(floats != null && floats.length == this.intValue8, "noise width mismatch");
      TandemModelData.Entry3 entry3 = this.method3159(floatses, floats);
      float[] floats2 = this.method3160(entry3);
      float[] floats3 = (float[])entry3.floatValues.clone();
      float[] floats4 = (float[])entry3.floatValues3.clone();
      int[][] intValues = new int[this.intValue2][2];

      for (int i = 0; i < this.intValue2; i++) {
         float[] floatValues = this.method3161(entry2, entry3, floats2, floats3, floats4, i);
         entry2.floatValues = method3167(floatValues, entry2.floatValues, this.floatValues26, this.floatValues27, this.floatValues35, this.floatValues36, this.intValue9);
         float[] floats5 = method3168(this.floatValues28, this.floatValues37, entry2.floatValues);
         float[] floats6 = method3168(this.floatValues29, this.floatValues38, entry2.floatValues);

         for (int j = 0; j < 2; j++) {
            int n = method3170(floats5, j * 5, 5);
            int n2 = method3171(floats6, j * this.intValue12 * 3, this.intValue12, 3);
            float f = method3174(floats6[j * this.intValue12 * 3 + n2 * 3 + 1]);
            f = Math.min(f, (float)Math.log(this.intValue13));
            int n3 = method3179(method3178(Math.exp(f)), 1, this.intValue13);
            intValues[i][j] = method3172(n, entry2.floatValues2[j][4]) * n3;
         }

         method3165(entry2.floatValues2, intValues[i]);

         for (int j = 0; j < 2; j++) {
            floats3[j] -= intValues[i][j];
            floats4[j] = entry3.flag && i <= entry3.intValue ? floats4[j] - intValues[i][j] : 0.0F;
         }
      }

      return new TandemModelData.Entry(entry3, intValues);
   }

   private TandemModelData.Entry3 method3159(float[][] floatses, float[] floats) {
      float[][] floatses2 = new float[this.intValue6][this.intValue5];

      for (float[] floats2 : floatses) {
         float[] floatValues = new float[this.intValue4];

         for (int i = 0; i < this.intValue4; i++) {
            floatValues[i] = (floats2[this.intValues[i]] - this.floatValues[i]) / this.floatValues2[i];
         }

         for (int i = 0; i < this.intValue6; i++) {
            floatses2[i] = method3167(
               floatValues, floatses2[i], this.floatValues7[i], this.floatValues8[i], this.floatValues9[i], this.floatValues10[i], this.intValue5
            );
            floatValues = floatses2[i];
         }
      }

      float[] floatValues = method3175(
         method3168(this.floatValues13, this.floatValues16, method3169(floatses2[this.intValue6 - 1], this.floatValues11, this.floatValues12))
      );
      int intValue = method3170(method3168(this.floatValues14, this.floatValues17, floatValues), 0, 5);
      float[] floats2 = method3168(this.floatValues15, this.floatValues18, floatValues);
      int n = 7 + 6 * this.intValue8 + 6;
      int n2 = intValue * this.intValue7 * n;
      int n3 = 0;
      float f = -Float.MAX_VALUE;

      for (int i = 0; i < this.intValue7; i++) {
         float f2 = floats2[n2 + i * n];
         if (f2 > f) {
            f = f2;
            n3 = i;
         }
      }

      int n4 = n2 + n3 * n;
      float[] floats3 = new float[6];

      for (int i = 0; i < 6; i++) {
         float f2 = floats2[n4 + 1 + i];

         for (int j = 0; j < this.intValue8; j++) {
            float f3 = 0.9F * (float)Math.tanh(floats2[n4 + 1 + 6 + i * this.intValue8 + j]);
            f2 += this.floatValue * f3 * floats[j];
         }

         float sign = f2 * this.floatValues4[i] + this.floatValues3[i];
         sign = Math.max(this.floatValues5[i], Math.min(this.floatValues6[i], sign));
         floats3[i] = method3178(Math.copySign(Math.expm1(Math.abs(sign)), (double)sign));
      }

      boolean flag = intValue != this.intValue2;
      return new TandemModelData.Entry3(
         intValue, n3, flag, new float[]{floats3[0], floats3[1]}, new float[]{floats3[2], floats3[3]}, flag ? new float[]{floats3[4], floats3[5]} : new float[2]
      );
   }

   private float[] method3160(TandemModelData.Entry3 entry3) {
      float[] floats = new float[]{this.floatValue2, this.floatValue3};
      float[] floatValues = new float[8];

      for (int i = 0; i < 2; i++) {
         floatValues[i] = entry3.floatValues[i] / (floats[i] * this.intValue2);
         floatValues[2 + i] = entry3.floatValues2[i] / floats[i];
         floatValues[4 + i] = entry3.floatValues3[i] / (floats[i] * this.intValue2);
      }

      floatValues[6] = (float)entry3.intValue / this.intValue2;
      floatValues[7] = entry3.flag ? 1.0F : 0.0F;
      float[] floatValues2 = method3175(method3168(this.floatValues24, this.floatValues31, floatValues));
      float[] floats2 = method3175(method3168(this.floatValues25, this.floatValues32, floatValues2));
      return method3169(floats2, this.floatValues33, this.floatValues34);
   }

   private float[] method3161(TandemModelData.Entry2 entry2, TandemModelData.Entry3 entry3, float[] floats, float[] floats2, float[] floats3, int n) {
      float[] floats4 = new float[]{this.floatValue2, this.floatValue3};
      float[] floats5 = new float[2];
      float[] floats6 = new float[2];
      float[] floats7 = new float[2];

      for (int i = 0; i < 2; i++) {
         floats5[i] = entry2.floatValues2[i][0] / floats4[i];
         floats6[i] = floats2[i] / (floats4[i] * this.intValue2);
         floats7[i] = floats3[i] / (floats4[i] * this.intValue2);
      }

      float f = (float)(this.intValue2 - n) / this.intValue2;
      float f2 = Math.max(0.0F, Math.min((float)this.intValue2, entry3.intValue - n + 1.0F)) / this.intValue2;
      return method3177(floats5, this.method3164(entry2.floatValues2), floats6, floats7, new float[]{f, f2, entry3.flag ? 1.0F : 0.0F}, floats);
   }

   private float[] method3162(float[] floats) {
      float[] floats2 = new float[]{
         this.floatValue2,
         this.floatValue3,
         this.floatValue2,
         this.floatValue3,
         this.floatValue2,
         this.floatValue3,
         50.0F,
         50.0F,
         50.0F,
         50.0F,
         1.0F,
         1.0F,
         this.floatValue2,
         this.floatValue3
      };
      float[] floats3 = new float[14];

      for (int i = 0; i < floats3.length; i++) {
         floats3[i] = floats[this.intValues2[i]] / floats2[i];
      }

      return floats3;
   }

   private float[][] method3163(float[][] floatses) {
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
            if (floatses[j][ints[i]] != 0.0F) {
               f = Math.signum(floatses[j][ints[i]]);
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

   private float[] method3164(float[][] floatValues2) {
      float[] floats = new float[]{
         this.floatValue2, this.floatValue2, 50.0F, 50.0F, 1.0F, this.floatValue2, this.floatValue3, this.floatValue3, 50.0F, 50.0F, 1.0F, this.floatValue3
      };
      float[] floats2 = new float[12];
      int n = 0;

      for (float[] floats3 : floatValues2) {
         for (float f : floats3) {
            floats2[n] = f / floats[n++];
         }
      }

      return floats2;
   }

   private static void method3165(float[][] floatValues2, int[] ints) {
      for (int i = 0; i < 2; i++) {
         float f = floatValues2[i][0];
         float f2 = floatValues2[i][1];
         float f3 = Math.signum((float)ints[i]);
         boolean flag = f3 != 0.0F;
         boolean flag2 = flag && floatValues2[i][4] != 0.0F && f3 != floatValues2[i][4];
         float f4 = ints[i] - f;
         float f5 = flag ? floatValues2[i][2] + 1.0F : 0.0F;
         float f6 = flag2 ? 0.0F : Math.min(50.0F, floatValues2[i][3] + 1.0F);
         boolean flag3 = floatValues2[i][4] == 0.0F || f3 != floatValues2[i][4];
         float f7 = !flag ? 0.0F : (flag3 ? Math.abs(ints[i]) : Math.max(floatValues2[i][5], (float)Math.abs(ints[i])));
         floatValues2[i][0] = ints[i];
         floatValues2[i][1] = f4;
         floatValues2[i][2] = f5;
         floatValues2[i][3] = f6;
         if (flag) {
            floatValues2[i][4] = f3;
         }

         floatValues2[i][5] = f7;
         if (!Float.isFinite(f2)) {
            throw new IllegalStateException("invalid motor state");
         }
      }
   }

   public int getIntValue() {
      return this.intValue;
   }

   public int getIntValue3() {
      return this.intValue3;
   }

   public int getIntValue2() {
      return this.intValue2;
   }

   private void method3166(float[][] floatses) {
      method3188(floatses != null && floatses.length == this.intValue, "history tick count mismatch");

      for (float[] floats : floatses) {
         method3188(floats != null && floats.length == this.intValue3, "history feature width mismatch");

         for (float f : floats) {
            method3188(Float.isFinite(f), "non-finite history feature");
         }
      }
   }

   private static float[] method3167(float[] floatValues, float[] floats, float[][] floatValues26, float[][] floatValues27, float[] floatValues35, float[] floatValues36, int n) {
      float[] floats2 = method3168(floatValues26, floatValues35, floatValues);
      float[] floats3 = method3168(floatValues27, floatValues36, floats);
      float[] floats4 = new float[n];

      for (int i = 0; i < n; i++) {
         float f = method3173(floats2[i] + floats3[i]);
         float f2 = method3173(floats2[n + i] + floats3[n + i]);
         float tanh = (float)Math.tanh(floats2[2 * n + i] + f * floats3[2 * n + i]);
         floats4[i] = (1.0F - f2) * tanh + f2 * floats[i];
      }

      return floats4;
   }

   private static float[] method3168(float[][] floatValues23, float[] floatValues30, float[] floatValues) {
      float[] floats = new float[floatValues23.length];

      for (int i = 0; i < floatValues23.length; i++) {
         float f = floatValues30[i];

         for (int j = 0; j < floatValues.length; j++) {
            f += floatValues23[i][j] * floatValues[j];
         }

         floats[i] = f;
      }

      return floats;
   }

   private static float[] method3169(float[] floats, float[] floatValues11, float[] floatValues12) {
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

      for (int i = 0; i < floats2.length; i++) {
         floats2[i] = (floats[i] - f) * f3 * floatValues11[i] + floatValues12[i];
      }

      return floats2;
   }

   private static int method3170(float[] floats, int n, int n2) {
      int n3 = 0;

      for (int i = 1; i < n2; i++) {
         if (floats[n + i] > floats[n + n3]) {
            n3 = i;
         }
      }

      return n3;
   }

   private static int method3171(float[] floats, int n, int n2, int n3) {
      int n4 = 0;

      for (int i = 1; i < n2; i++) {
         if (floats[n + i * n3] > floats[n + n4 * n3]) {
            n4 = i;
         }
      }

      return n4;
   }

   private static int method3172(int n, float f) {
      int n2 = f == 0.0F ? 1 : (f > 0.0F ? 1 : -1);

      return switch (n) {
         case 1 -> n2;
         case 2 -> -n2;
         case 3 -> 1;
         case 4 -> -1;
         default -> 0;
      };
   }

   private static float method3173(float f) {
      return (float)(1.0 / (1.0 + Math.exp(-f)));
   }

   private static float method3174(float f) {
      return f > 20.0F ? f : (float)Math.log1p(Math.exp(f));
   }

   private static float[] method3175(float[] floats) {
      for (int i = 0; i < floats.length; i++) {
         floats[i] *= method3173(floats[i]);
      }

      return floats;
   }

   private static float[] method3176(float[] floats) {
      for (int i = 0; i < floats.length; i++) {
         floats[i] = (float)Math.tanh(floats[i]);
      }

      return floats;
   }

   private static float[] method3177(float[]... floatses) {
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

   private static int method3178(double exp) {
      return !Double.isFinite(exp) ? 0 : (int)Math.rint(exp);
   }

   private static int method3179(int n, int n2, int n3) {
      return Math.max(n2, Math.min(n3, n));
   }

   private static JsonObject method3180(JsonObject json, String memberName) {
      JsonElement jsonElement = json.get(memberName);
      method3188(jsonElement != null && jsonElement.isJsonObject(), "missing object " + memberName);
      return jsonElement.getAsJsonObject();
   }

   private static JsonArray method3181(JsonObject json, String memberName) {
      JsonElement jsonElement = json.get(memberName);
      method3188(jsonElement != null && jsonElement.isJsonArray(), "missing array " + memberName);
      return jsonElement.getAsJsonArray();
   }

   private static String method3182(JsonObject json, String memberName) {
      JsonElement jsonElement = json.get(memberName);
      method3188(jsonElement != null && jsonElement.isJsonPrimitive(), "missing string " + memberName);
      return jsonElement.getAsString();
   }

   private static float method3183(JsonObject json, String memberName) {
      JsonElement jsonElement = json.get(memberName);
      method3188(jsonElement != null && jsonElement.isJsonPrimitive(), "missing number " + memberName);
      float f = jsonElement.getAsFloat();
      method3188(Float.isFinite(f), "non-finite " + memberName);
      return f;
   }

   private static int method3184(JsonObject json, String memberName) {
      return Math.round(method3183(json, memberName));
   }

   private static float[] method3185(JsonObject json, String memberName, int n) {
      JsonArray jsonArray = method3181(json, memberName);
      method3188(jsonArray.size() == n, memberName + " length mismatch");
      float[] floats = new float[n];

      for (int i = 0; i < n; i++) {
         floats[i] = jsonArray.get(i).getAsFloat();
      }

      return floats;
   }

   private static int[] method3186(JsonObject json, String memberName, int n) {
      JsonArray jsonArray = method3181(json, memberName);
      method3188(jsonArray.size() == n, memberName + " length mismatch");
      int[] ints = new int[n];

      for (int i = 0; i < n; i++) {
         ints[i] = jsonArray.get(i).getAsInt();
      }

      return ints;
   }

   private static float[][] method3187(JsonObject json, String memberName, int n, int n2) {
      JsonArray jsonArray = method3181(json, memberName);
      method3188(jsonArray.size() == n, memberName + " rows mismatch");
      float[][] floatses = new float[n][n2];

      for (int i = 0; i < n; i++) {
         JsonArray jsonArray2 = jsonArray.get(i).getAsJsonArray();
         method3188(jsonArray2.size() == n2, memberName + " columns mismatch");

         for (int j = 0; j < n2; j++) {
            floatses[i][j] = jsonArray2.get(j).getAsFloat();
         }
      }

      return floatses;
   }

   private static void method3188(boolean finite, String str) {
      if (!finite) {
         throw new IllegalArgumentException(str);
      }
   }

   public static final class Entry {
      public final TandemModelData.Entry3 entry3;
      public final int[][] intValues;

      private Entry(TandemModelData.Entry3 entry3, int[][] intValues) {
         this.entry3 = entry3;
         this.intValues = intValues;
      }
   }

   public static final class Entry2 {
      private float[] floatValues;
      private final float[][] floatValues2;

      private Entry2(float[] floatValues, float[][] floatValues2) {
         this.floatValues = floatValues;
         this.floatValues2 = floatValues2;
      }
   }

   public static final class Entry3 {
      public final int intValue;
      public final int intValue2;
      public final boolean flag;
      public final float[] floatValues;
      public final float[] floatValues2;
      public final float[] floatValues3;

      private Entry3(int intValue, int n, boolean flag, float[] floatValues, float[] floatValues2, float[] floatValues3) {
         this.intValue = intValue;
         this.intValue2 = n;
         this.flag = flag;
         this.floatValues = floatValues;
         this.floatValues2 = floatValues2;
         this.floatValues3 = floatValues3;
      }
   }
}
