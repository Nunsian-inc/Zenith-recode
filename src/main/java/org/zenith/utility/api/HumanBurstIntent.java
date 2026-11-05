package org.zenith.utility.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class HumanBurstIntent {
   public static final int CONST_intValue = 59;
   public static final int CONST_intValue2 = 0;
   public static final int CONST_intValue3 = 1;
   public static final int CONST_intValue4 = 2;
   private static final String text = "/assets/zenith/pointclick/human_burst_intent.json";
   private static final Path path = Path.of("human_burst_intent.json");
   private final int intValue;
   private final int intValue2;
   private final int intValue3;
   private final int intValue4;
   private final HumanBurstIntent.Data[] datas;
   private final HumanBurstIntent.Data5[] data5s;
   private final HumanBurstIntent.Data2[] data2s;
   private final float[][] floatValues;
   private final float[] floatValues2;
   private final float[][] floatValues3;
   private final float[] floatValues4;
   private final float[][] floatValues5;
   private final float[] floatValues6;
   private final float[] floatValues7;
   private final float[] floatValues8;
   private final float floatValue;
   private final float[][] floatValues9;
   private final float[] floatValues10;
   private final float[][] floatValues11;
   private final float[] floatValues12;
   private final float[][] floatValues13;
   private final float[] floatValues14;
   private final float[][] floatValues15;
   private final float[] floatValues16;
   private final float[][] floatValues17;
   private final float[] floatValues18;
   private final float[][] floatValues19;
   private final float[] floatValues20;
   private final float[][] floatValues21;
   private final float[] floatValues22;
   private final float[][] floatValues23;
   private int intValue5;

   private HumanBurstIntent(
      int intValue,
      int n,
      int n2,
      int n3,
      HumanBurstIntent.Data[] datas,
      HumanBurstIntent.Data5[] data5s,
      HumanBurstIntent.Data2[] data2s,
      float[][] floatValues,
      float[] floatValues2,
      float[][] floatValues3,
      float[] floatValues4,
      float[][] floatValues5,
      float[] floatValues6,
      float[] floatValues7,
      float[] floatValues8,
      float floatValue,
      float[][] floatValues9,
      float[] floatValues10,
      float[][] floatValues11,
      float[] floatValues12,
      float[][] floatValues13,
      float[] floatValues14,
      float[][] floatValues15,
      float[] floatValues16,
      float[][] floatValues17,
      float[] floatValues18,
      float[][] floatValues19,
      float[] floatValues20,
      float[][] floatValues21,
      float[] floatValues22
   ) {
      this.intValue = intValue;
      this.intValue2 = n;
      this.intValue3 = n2;
      this.intValue4 = n3;
      this.datas = datas;
      this.data5s = data5s;
      this.data2s = data2s;
      this.floatValues = floatValues;
      this.floatValues2 = floatValues2;
      this.floatValues3 = floatValues3;
      this.floatValues4 = floatValues4;
      this.floatValues5 = floatValues5;
      this.floatValues6 = floatValues6;
      this.floatValues7 = floatValues7;
      this.floatValues8 = floatValues8;
      this.floatValue = floatValue;
      this.floatValues9 = floatValues9;
      this.floatValues10 = floatValues10;
      this.floatValues11 = floatValues11;
      this.floatValues12 = floatValues12;
      this.floatValues13 = floatValues13;
      this.floatValues14 = floatValues14;
      this.floatValues15 = floatValues15;
      this.floatValues16 = floatValues16;
      this.floatValues17 = floatValues17;
      this.floatValues18 = floatValues18;
      this.floatValues19 = floatValues19;
      this.floatValues20 = floatValues20;
      this.floatValues21 = floatValues21;
      this.floatValues22 = floatValues22;
      this.floatValues23 = new float[n3][intValue];
   }

   public static HumanBurstIntent method6372() {
      HumanBurstIntent humanBurstIntent = method6373(path);
      if (humanBurstIntent != null) {
         return humanBurstIntent;
      } else {
         try {
            HumanBurstIntent humanBurstIntent2;
            try (InputStream resourceAsStream = HumanBurstIntent.class.getResourceAsStream("/assets/zenith/pointclick/human_burst_intent.json")) {
               if (resourceAsStream == null) {
                  return null;
               }

               humanBurstIntent2 = method6374(new String(resourceAsStream.readAllBytes(), StandardCharsets.UTF_8), "/assets/zenith/pointclick/human_burst_intent.json");
            }

            return humanBurstIntent2;
         } catch (Throwable e) {
            System.err.println("[HumanBurst] failed to load /assets/zenith/pointclick/human_burst_intent.json: " + e);
            return null;
         }
      }
   }

   private static HumanBurstIntent method6373(Path path) {
      try {
         return !Files.isRegularFile(path) ? null : method6374(Files.readString(path, StandardCharsets.UTF_8), path.toAbsolutePath().toString());
      } catch (Throwable e) {
         System.err.println("[HumanBurst] failed to load " + path + ": " + e);
         return null;
      }
   }

   private static HumanBurstIntent method6374(String str, String string) {
      JsonObject jsonObject = JsonParser.parseString(str).getAsJsonObject();
      method6399(jsonObject.get("format_version").getAsInt() == 2, "format version");
      method6399("human_burst_target_relative_gru_bump".equals(jsonObject.get("model").getAsString()), "model name");
      JsonObject json = jsonObject.getAsJsonObject("architecture");
      int intValue = json.get("input_size").getAsInt();
      int n = json.get("hidden_size").getAsInt();
      int n2 = json.get("layers").getAsInt();
      int n3 = json.get("max_horizon").getAsInt();
      int n4 = jsonObject.get("history_ticks").getAsInt();
      method6399(intValue == 61, "input size");
      method6399(n > 0 && n2 > 0 && n3 >= 2 && n4 > 0, "architecture");
      JsonArray jsonArray = jsonObject.getAsJsonArray("feature_spec");
      method6399(jsonArray.size() == intValue, "feature count");
      HumanBurstIntent.Data[] datas = new HumanBurstIntent.Data[intValue];

      for (int i = 0; i < intValue; i++) {
         JsonObject jsonObject2 = jsonArray.get(i).getAsJsonObject();
         datas[i] = new HumanBurstIntent.Data(
            jsonObject2.get("name").getAsString(),
            jsonObject2.get("transform").getAsString(),
            jsonObject2.get("scale").getAsFloat(),
            jsonObject2.get("mean").getAsFloat(),
            jsonObject2.get("std").getAsFloat()
         );
         method6399(Float.isFinite(datas[i].getFloatValue3()) && datas[i].getFloatValue3() > 0.0F, "feature std " + i);
      }

      method6399("target_yaw_error_sin".equals(datas[intValue - 2].name()), "yaw sine feature");
      method6399("target_yaw_error_cos".equals(datas[intValue - 1].name()), "yaw cosine feature");
      JsonArray jsonArray2 = jsonObject.getAsJsonArray("target_spec");
      method6399(jsonArray2.size() == 6, "target count");
      HumanBurstIntent.Data5[] data5s = new HumanBurstIntent.Data5[6];

      for (int i = 0; i < data5s.length; i++) {
         JsonObject jsonObject2 = jsonArray2.get(i).getAsJsonObject();
         data5s[i] = new HumanBurstIntent.Data5(
            jsonObject2.get("transform").getAsString(), jsonObject2.get("scale").getAsFloat(), jsonObject2.get("mean").getAsFloat(), jsonObject2.get("std").getAsFloat()
         );
         method6399(Float.isFinite(data5s[i].getFloatValue3()) && data5s[i].getFloatValue3() > 0.0F, "target std " + i);
      }

      JsonObject jsonObject2 = jsonObject.getAsJsonObject("weights");
      JsonArray jsonArray3 = jsonObject2.getAsJsonArray("gru_layers");
      method6399(jsonArray3.size() == n2, "GRU layer count");
      HumanBurstIntent.Data2[] data2s = new HumanBurstIntent.Data2[n2];
      int intValue2 = intValue;

      for (int i = 0; i < n2; i++) {
         JsonObject jsonObject3 = jsonArray3.get(i).getAsJsonObject();
         data2s[i] = new HumanBurstIntent.Data2(
            method6398(jsonObject3.getAsJsonArray("weight_ih")),
            method6398(jsonObject3.getAsJsonArray("weight_hh")),
            method6397(jsonObject3.getAsJsonArray("bias_ih")),
            method6397(jsonObject3.getAsJsonArray("bias_hh"))
         );
         method6384(data2s[i], intValue2, n, "GRU layer " + i);
         intValue2 = n;
      }

      HumanBurstIntent humanBurstIntent = new HumanBurstIntent(
         intValue,
         n,
         n3,
         n4,
         datas,
         data5s,
         data2s,
         method6398(jsonObject2.getAsJsonArray("attention_projection_weight")),
         method6397(jsonObject2.getAsJsonArray("attention_projection_bias")),
         method6398(jsonObject2.getAsJsonArray("attention_score_weight")),
         method6397(jsonObject2.getAsJsonArray("attention_score_bias")),
         method6398(jsonObject2.getAsJsonArray("trunk_weight")),
         method6397(jsonObject2.getAsJsonArray("trunk_bias")),
         method6397(jsonObject2.getAsJsonArray("layer_norm_weight")),
         method6397(jsonObject2.getAsJsonArray("layer_norm_bias")),
         jsonObject2.get("layer_norm_epsilon").getAsFloat(),
         method6398(jsonObject2.getAsJsonArray("mode_weight")),
         method6397(jsonObject2.getAsJsonArray("mode_bias")),
         method6398(jsonObject2.getAsJsonArray("horizon_weight")),
         method6397(jsonObject2.getAsJsonArray("horizon_bias")),
         method6398(jsonObject2.getAsJsonArray("motion_weight")),
         method6397(jsonObject2.getAsJsonArray("motion_bias")),
         method6398(jsonObject2.getAsJsonArray("aim_weight")),
         method6397(jsonObject2.getAsJsonArray("aim_bias")),
         method6398(jsonObject2.getAsJsonArray("terminal_weight")),
         method6397(jsonObject2.getAsJsonArray("terminal_bias")),
         method6398(jsonObject2.getAsJsonArray("residual_weight")),
         method6397(jsonObject2.getAsJsonArray("residual_bias")),
         method6398(jsonObject2.getAsJsonArray("commit_weight")),
         method6397(jsonObject2.getAsJsonArray("commit_bias"))
      );
      humanBurstIntent.method6383();
      method6386(humanBurstIntent, jsonObject.getAsJsonArray("parity"));
      System.out.println("[HumanBurst] loaded " + string + " (GRU" + n + "x" + n2 + ", attention, history=" + n4 + ")");
      return humanBurstIntent;
   }

   public void reset() {
      this.intValue5 = 0;
   }

   public void method6375(float[] floats, float f) {
      float[] floats2 = this.method6378(floats, f);
      if (this.intValue5 < this.intValue4) {
         this.floatValues23[this.intValue5++] = floats2;
      } else {
         System.arraycopy(this.floatValues23, 1, this.floatValues23, 0, this.intValue4 - 1);
         this.floatValues23[this.intValue4 - 1] = floats2;
      }
   }

   public HumanBurstIntent.Data4 method6376() {
      return this.intValue5 == 0 ? method6377() : this.method6382(this.method6379(this.floatValues23, this.intValue5));
   }

   private static HumanBurstIntent.Data4 method6377() {
      return new HumanBurstIntent.Data4(0, 0, 0.0F, 0.0F, 0.5F, 0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
   }

   private float[] method6378(float[] floats, float f) {
      if (floats != null && floats.length == 59) {
         method6399(Float.isFinite(f) && f > 0.0F, "mouse GCD");
         float[] floats2 = new float[this.intValue];

         for (int i = 0; i < 59; i++) {
            float f2 = i == 26 ? method6387(floats[i], f) : floats[i];
            floats2[i] = method6390(method6388(f2, this.datas[i]), this.datas[i], "feature " + i);
         }

         double d = Math.toRadians(floats[0] * f);
         floats2[59] = method6390((float)Math.sin(d), this.datas[59], "yaw sine");
         floats2[60] = method6390((float)Math.cos(d), this.datas[60], "yaw cosine");
         return floats2;
      } else {
         throw new IllegalArgumentException("expected 59 raw features");
      }
   }

   private HumanBurstIntent.Data3 method6379(float[][] floatValues23, int length) {
      method6399(length > 0 && length <= floatValues23.length, "history length");
      float[][] floatses = new float[this.data2s.length][this.intValue2];
      float[][] floatses2 = new float[length][this.intValue2];

      for (int i = 0; i < length; i++) {
         float[] floats = floatValues23[i];

         for (int j = 0; j < this.data2s.length; j++) {
            floatses[j] = this.method6380(floats, floatses[j], this.data2s[j]);
            floats = floatses[j];
         }

         floatses2[i] = (float[])floats.clone();
      }

      float[] floatValues2 = new float[length];

      for (int i = 0; i < length; i++) {
         float[] floats = method6392(floatses2[i], this.floatValues, this.floatValues2);

         for (int j = 0; j < floats.length; j++) {
            floats[j] = (float)Math.tanh(floats[j]);
         }

         floatValues2[i] = method6392(floats, this.floatValues3, this.floatValues4)[0];
      }

      float[] floats = method6394(floatValues2);
      float[] floats2 = new float[this.intValue2];

      for (int i = 0; i < length; i++) {
         for (int j = 0; j < this.intValue2; j++) {
            floats2[j] += floats[i] * floatses2[i][j];
         }
      }

      float[] floats3 = new float[this.intValue2 * 2];
      System.arraycopy(floatses2[length - 1], 0, floats3, 0, this.intValue2);
      System.arraycopy(floats2, 0, floats3, this.intValue2, this.intValue2);
      float[] floats4 = method6392(floats3, this.floatValues5, this.floatValues6);

      for (int i = 0; i < floats4.length; i++) {
         floats4[i] *= method6393(floats4[i]);
      }

      floats4 = this.method6381(floats4);
      return new HumanBurstIntent.Data3(
         method6392(floats4, this.floatValues9, this.floatValues10),
         method6392(floats4, this.floatValues11, this.floatValues12),
         method6392(floats4, this.floatValues13, this.floatValues14),
         method6392(floats4, this.floatValues15, this.floatValues16),
         method6392(floats4, this.floatValues17, this.floatValues18),
         method6392(floats4, this.floatValues19, this.floatValues20),
         method6392(floats4, this.floatValues21, this.floatValues22)[0]
      );
   }

   private float[] method6380(float[] floats, float[] floats2, HumanBurstIntent.Data2 data2) {
      int n = this.intValue2;
      float[] floats3 = method6391(floats, data2.getFloatValues(), data2.getFloatValues3());
      float[] floats4 = method6391(floats2, data2.getFloatValues2(), data2.getFloatValues4());
      float[] floats5 = new float[n];

      for (int i = 0; i < n; i++) {
         float f = method6393(floats3[i] + floats4[i]);
         float f2 = method6393(floats3[n + i] + floats4[n + i]);
         float tanh = (float)Math.tanh(floats3[2 * n + i] + f * floats4[2 * n + i]);
         floats5[i] = (1.0F - f2) * tanh + f2 * floats2[i];
      }

      return floats5;
   }

   private float[] method6381(float[] floats) {
      double d = 0.0;

      for (float f : floats) {
         d += f;
      }

      d /= floats.length;
      double d2 = 0.0;

      for (float f : floats) {
         double d3 = f - d;
         d2 += d3 * d3;
      }

      d2 /= floats.length;
      double d3 = 1.0 / Math.sqrt(d2 + this.floatValue);
      float[] floats2 = new float[floats.length];

      for (int i = 0; i < floats2.length; i++) {
         floats2[i] = (float)((floats[i] - d) * d3) * this.floatValues7[i] + this.floatValues8[i];
      }

      return floats2;
   }

   private HumanBurstIntent.Data4 method6382(HumanBurstIntent.Data3 data3) {
      int intValue = method6395(data3.getFloatValues());
      float[] floats = method6394(data3.getFloatValues2());
      double d = 0.0;

      for (int i = 0; i < floats.length; i++) {
         d += floats[i] * (i + 2);
      }

      int n = Math.max(2, Math.min(this.intValue3, (int)Math.round(d)));
      if (intValue == 0) {
         n = 0;
      } else if (intValue == 1) {
         n = 1;
      }

      float[] floats2 = new float[]{
         data3.getFloatValues3()[0],
         data3.getFloatValues3()[1],
         data3.getFloatValues5()[0],
         data3.getFloatValues5()[1],
         data3.getFloatValues6()[0],
         data3.getFloatValues6()[1]
      };
      float[] floats3 = new float[this.data5s.length];

      for (int i = 0; i < floats3.length; i++) {
         float x = floats2[i] * this.data5s[i].getFloatValue3() + this.data5s[i].getFloatValue2();
         floats3[i] = method6389(x, this.data5s[i]);
      }

      return new HumanBurstIntent.Data4(
         intValue,
         n,
         floats3[0],
         floats3[1],
         method6393(data3.getFloatValues4()[0]),
         method6393(data3.getFloatValues4()[1]),
         floats3[2],
         floats3[3],
         floats3[4],
         floats3[5],
         method6393(data3.getFloatValue())
      );
   }

   private void method6383() {
      method6385(this.floatValues, this.floatValues2, this.intValue2, this.intValue2, "attention projection");
      method6385(this.floatValues3, this.floatValues4, this.intValue2, 1, "attention score");
      method6385(this.floatValues5, this.floatValues6, this.intValue2 * 2, this.intValue2, "trunk");
      method6399(this.floatValues7.length == this.intValue2 && this.floatValues8.length == this.intValue2, "layer norm");
      method6385(this.floatValues9, this.floatValues10, this.intValue2, 3, "mode head");
      method6385(this.floatValues11, this.floatValues12, this.intValue2, this.intValue3 - 1, "horizon head");
      method6385(this.floatValues13, this.floatValues14, this.intValue2, 2, "motion head");
      method6385(this.floatValues15, this.floatValues16, this.intValue2, 2, "aim head");
      method6385(this.floatValues17, this.floatValues18, this.intValue2, 2, "terminal head");
      method6385(this.floatValues19, this.floatValues20, this.intValue2, 2, "residual head");
      method6385(this.floatValues21, this.floatValues22, this.intValue2, 1, "commit head");
      method6399(Float.isFinite(this.floatValue) && this.floatValue > 0.0F, "layer norm epsilon");
   }

   private static void method6384(HumanBurstIntent.Data2 data2, int n, int n2, String str) {
      method6385(data2.getFloatValues(), data2.getFloatValues3(), n, n2 * 3, str + " input");
      method6385(data2.getFloatValues2(), data2.getFloatValues4(), n2, n2 * 3, str + " hidden");
   }

   private static void method6385(float[][] floatValues, float[] floatValues4, int n, int n2, String str) {
      method6399(floatValues.length == n2 && floatValues4.length == n2, str + " output size");

      for (float[] floatValue : floatValues) {
         method6399(floatValue.length == n, str + " input size");
      }
   }

   private static void method6386(HumanBurstIntent humanBurstIntent, JsonArray jsonArray) {
      for (int i = 0; i < jsonArray.size(); i++) {
         JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
         JsonArray jsonArray2 = jsonObject.getAsJsonArray("raw_history");
         float[][] floatValues23 = new float[jsonArray2.size()][];
         float f = jsonObject.get("mouse_gcd").getAsFloat();

         for (int j = 0; j < jsonArray2.size(); j++) {
            floatValues23[j] = humanBurstIntent.method6378(method6397(jsonArray2.get(j).getAsJsonArray()), f);
         }

         HumanBurstIntent.Data3 data3 = humanBurstIntent.method6379(floatValues23, floatValues23.length);
         float f2 = 0.0F;
         f2 = Math.max(f2, method6396(data3.getFloatValues(), method6397(jsonObject.getAsJsonArray("mode_logits"))));
         f2 = Math.max(f2, method6396(data3.getFloatValues2(), method6397(jsonObject.getAsJsonArray("horizon_logits"))));
         f2 = Math.max(f2, method6396(data3.getFloatValues3(), method6397(jsonObject.getAsJsonArray("motion"))));
         f2 = Math.max(f2, method6396(data3.getFloatValues4(), method6397(jsonObject.getAsJsonArray("aim_logits"))));
         f2 = Math.max(f2, method6396(data3.getFloatValues5(), method6397(jsonObject.getAsJsonArray("terminal"))));
         f2 = Math.max(f2, method6396(data3.getFloatValues6(), method6397(jsonObject.getAsJsonArray("residual"))));
         f2 = Math.max(f2, Math.abs(data3.getFloatValue() - jsonObject.get("commit_logit").getAsFloat()));
         method6399(f2 <= 0.005F, "parity case " + i + ", error=" + f2);
         HumanBurstIntent.Data4 data4 = humanBurstIntent.method6382(data3);
         JsonObject json = jsonObject.getAsJsonObject("intent");
         method6399(data4.getIntValue() == json.get("mode").getAsInt(), "parity mode " + i);
         method6399(data4.getIntValue2() == json.get("horizon_ticks").getAsInt(), "parity horizon " + i);
      }
   }

   private static float method6387(float f, float f2) {
      float f3 = 360.0F / f2;
      return f - f3 * (float)Math.floor((f + f3 * 0.5F) / f3);
   }

   private static float method6388(float f, HumanBurstIntent.Data data) {
      method6399(Float.isFinite(f), "non-finite " + data.name());
      String str = data.getText2();

      return switch (str) {
         case "signed_mu" -> (float)(Math.signum(f) * Math.log1p(Math.abs(f) / data.getFloatValue()));
         case "log1p" -> (float)Math.log1p(Math.max(f, 0.0F) / data.getFloatValue());
         case "linear" -> f;
         default -> throw new IllegalStateException("unsupported feature transform " + data.getText2());
      };
   }

   private static float method6389(float x, HumanBurstIntent.Data5 data5) {
      String text = data5.getText();

      return switch (text) {
         case "signed_mu" -> (float)(Math.signum(x) * Math.expm1(Math.abs(x)) * data5.getFloatValue());
         case "log1p" -> (float)Math.max(Math.expm1(x) * data5.getFloatValue(), 0.0);
         case "linear" -> x;
         default -> throw new IllegalStateException("unsupported target transform " + data5.getText());
      };
   }

   private static float method6390(float f, HumanBurstIntent.Data data, String str) {
      float f2 = (f - data.getFloatValue2()) / data.getFloatValue3();
      method6399(Float.isFinite(f2), "non-finite normalized " + str);
      return f2;
   }

   private static float[] method6391(float[] floats, float[][] floatValues, float[] floatValues3) {
      float[] floats2 = new float[floatValues3.length];

      for (int i = 0; i < floats2.length; i++) {
         double d = floatValues3[i];

         for (int j = 0; j < floats.length; j++) {
            d += floatValues[i][j] * floats[j];
         }

         floats2[i] = (float)d;
      }

      return floats2;
   }

   private static float[] method6392(float[] floats, float[][] floatValues, float[] floatValues2) {
      return method6391(floats, floatValues, floatValues2);
   }

   private static float method6393(float floatValue) {
      if (floatValue >= 0.0F) {
         double exp = Math.exp(-floatValue);
         return (float)(1.0 / (1.0 + exp));
      } else {
         double exp = Math.exp(floatValue);
         return (float)(exp / (1.0 + exp));
      }
   }

   private static float[] method6394(float[] floatValues2) {
      float f = floatValues2[0];

      for (int i = 1; i < floatValues2.length; i++) {
         f = Math.max(f, floatValues2[i]);
      }

      float[] floats = new float[floatValues2.length];
      double d = 0.0;

      for (int i = 0; i < floats.length; i++) {
         floats[i] = (float)Math.exp(floatValues2[i] - f);
         d += floats[i];
      }

      for (int i = 0; i < floats.length; i++) {
         floats[i] /= (float)d;
      }

      return floats;
   }

   private static int method6395(float[] floatValues) {
      int n = 0;

      for (int i = 1; i < floatValues.length; i++) {
         if (floatValues[i] > floatValues[n]) {
            n = i;
         }
      }

      return n;
   }

   private static float method6396(float[] floatValues, float[] floats) {
      method6399(floatValues.length == floats.length, "parity vector length");
      float f = 0.0F;

      for (int i = 0; i < floatValues.length; i++) {
         f = Math.max(f, Math.abs(floatValues[i] - floats[i]));
      }

      return f;
   }

   private static float[] method6397(JsonArray jsonArray) {
      float[] floats = new float[jsonArray.size()];

      for (int i = 0; i < floats.length; i++) {
         floats[i] = jsonArray.get(i).getAsFloat();
      }

      return floats;
   }

   private static float[][] method6398(JsonArray jsonArray) {
      float[][] floatses = new float[jsonArray.size()][];

      for (int i = 0; i < floatses.length; i++) {
         floatses[i] = method6397(jsonArray.get(i).getAsJsonArray());
      }

      return floatses;
   }

   private static void method6399(boolean finite, String str) {
      if (!finite) {
         throw new IllegalStateException("invalid human-burst artifact: " + str);
      }
   }

   private static final class Data {
      private final String text;
      private final String text2;
      private final float floatValue;
      private final float floatValue2;
      private final float floatValue3;

      public String text() {
         return this.text;
      }

      public String text2() {
         return this.text2;
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
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.floatValue3, other.floatValue3);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, text2, floatValue, floatValue2, floatValue3);
      }

      @Override
      public String toString() {
         return "Data[" + "text=" + this.text + ", " + "text2=" + this.text2 + ", " + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "floatValue3=" + this.floatValue3 + "]";
      }

      private Data(String text, String string, float floatValue, float f, float f2) {
         this.text = text;
         this.text2 = string;
         this.floatValue = floatValue;
         this.floatValue2 = f;
         this.floatValue3 = f2;
      }

      public String name() {
         return this.text;
      }

      public String getText2() {
         return this.text2;
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

   private static final class Data2 {
      private final float[][] floatValues;
      private final float[][] floatValues2;
      private final float[] floatValues3;
      private final float[] floatValues4;

      public float[][] floatValues() {
         return this.floatValues;
      }

      public float[][] floatValues2() {
         return this.floatValues2;
      }

      public float[] floatValues3() {
         return this.floatValues3;
      }

      public float[] floatValues4() {
         return this.floatValues4;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data2 other)) return false;
         return java.util.Objects.equals(this.floatValues, other.floatValues) && java.util.Objects.equals(this.floatValues2, other.floatValues2) && java.util.Objects.equals(this.floatValues3, other.floatValues3) && java.util.Objects.equals(this.floatValues4, other.floatValues4);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(floatValues, floatValues2, floatValues3, floatValues4);
      }

      @Override
      public String toString() {
         return "Data2[" + "floatValues=" + this.floatValues + ", " + "floatValues2=" + this.floatValues2 + ", " + "floatValues3=" + this.floatValues3 + ", " + "floatValues4=" + this.floatValues4 + "]";
      }

      private Data2(float[][] floatValues, float[][] floatValues2, float[] floatValues3, float[] floatValues4) {
         this.floatValues = floatValues;
         this.floatValues2 = floatValues2;
         this.floatValues3 = floatValues3;
         this.floatValues4 = floatValues4;
      }

      public float[][] getFloatValues() {
         return this.floatValues;
      }

      public float[][] getFloatValues2() {
         return this.floatValues2;
      }

      public float[] getFloatValues3() {
         return this.floatValues3;
      }

      public float[] getFloatValues4() {
         return this.floatValues4;
      }
   }

   private static final class Data3 {
      private final float[] floatValues;
      private final float[] floatValues2;
      private final float[] floatValues3;
      private final float[] floatValues4;
      private final float[] floatValues5;
      private final float[] floatValues6;
      private final float floatValue;

      public float[] floatValues() {
         return this.floatValues;
      }

      public float[] floatValues2() {
         return this.floatValues2;
      }

      public float[] floatValues3() {
         return this.floatValues3;
      }

      public float[] floatValues4() {
         return this.floatValues4;
      }

      public float[] floatValues5() {
         return this.floatValues5;
      }

      public float[] floatValues6() {
         return this.floatValues6;
      }

      public float floatValue() {
         return this.floatValue;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data3 other)) return false;
         return java.util.Objects.equals(this.floatValues, other.floatValues) && java.util.Objects.equals(this.floatValues2, other.floatValues2) && java.util.Objects.equals(this.floatValues3, other.floatValues3) && java.util.Objects.equals(this.floatValues4, other.floatValues4) && java.util.Objects.equals(this.floatValues5, other.floatValues5) && java.util.Objects.equals(this.floatValues6, other.floatValues6) && java.util.Objects.equals(this.floatValue, other.floatValue);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(floatValues, floatValues2, floatValues3, floatValues4, floatValues5, floatValues6, floatValue);
      }

      @Override
      public String toString() {
         return "Data3[" + "floatValues=" + this.floatValues + ", " + "floatValues2=" + this.floatValues2 + ", " + "floatValues3=" + this.floatValues3 + ", " + "floatValues4=" + this.floatValues4 + ", " + "floatValues5=" + this.floatValues5 + ", " + "floatValues6=" + this.floatValues6 + ", " + "floatValue=" + this.floatValue + "]";
      }

      private Data3(float[] floatValues, float[] floatValues2, float[] floatValues3, float[] floatValues4, float[] floatValues5, float[] floatValues6, float floatValue) {
         this.floatValues = floatValues;
         this.floatValues2 = floatValues2;
         this.floatValues3 = floatValues3;
         this.floatValues4 = floatValues4;
         this.floatValues5 = floatValues5;
         this.floatValues6 = floatValues6;
         this.floatValue = floatValue;
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

      public float[] getFloatValues4() {
         return this.floatValues4;
      }

      public float[] getFloatValues5() {
         return this.floatValues5;
      }

      public float[] getFloatValues6() {
         return this.floatValues6;
      }

      public float getFloatValue() {
         return this.floatValue;
      }
   }

   public static final class Data4 {
      private final int intValue;
      private final int intValue2;
      private final float floatValue;
      private final float floatValue2;
      private final float floatValue3;
      private final float floatValue4;
      private final float floatValue5;
      private final float floatValue6;
      private final float floatValue7;
      private final float floatValue8;
      private final float floatValue9;

      public int intValue() {
         return this.intValue;
      }

      public int intValue2() {
         return this.intValue2;
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

      public float floatValue4() {
         return this.floatValue4;
      }

      public float floatValue5() {
         return this.floatValue5;
      }

      public float floatValue6() {
         return this.floatValue6;
      }

      public float floatValue7() {
         return this.floatValue7;
      }

      public float floatValue8() {
         return this.floatValue8;
      }

      public float floatValue9() {
         return this.floatValue9;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data4 other)) return false;
         return java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.intValue2, other.intValue2) && java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.floatValue3, other.floatValue3) && java.util.Objects.equals(this.floatValue4, other.floatValue4) && java.util.Objects.equals(this.floatValue5, other.floatValue5) && java.util.Objects.equals(this.floatValue6, other.floatValue6) && java.util.Objects.equals(this.floatValue7, other.floatValue7) && java.util.Objects.equals(this.floatValue8, other.floatValue8) && java.util.Objects.equals(this.floatValue9, other.floatValue9);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(intValue, intValue2, floatValue, floatValue2, floatValue3, floatValue4, floatValue5, floatValue6, floatValue7, floatValue8, floatValue9);
      }

      @Override
      public String toString() {
         return "Data4[" + "intValue=" + this.intValue + ", " + "intValue2=" + this.intValue2 + ", " + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "floatValue3=" + this.floatValue3 + ", " + "floatValue4=" + this.floatValue4 + ", " + "floatValue5=" + this.floatValue5 + ", " + "floatValue6=" + this.floatValue6 + ", " + "floatValue7=" + this.floatValue7 + ", " + "floatValue8=" + this.floatValue8 + ", " + "floatValue9=" + this.floatValue9 + "]";
      }

      public Data4(int intValue, int n, float floatValue, float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8) {
         this.intValue = intValue;
         this.intValue2 = n;
         this.floatValue = floatValue;
         this.floatValue2 = f;
         this.floatValue3 = f2;
         this.floatValue4 = f3;
         this.floatValue5 = f4;
         this.floatValue6 = f5;
         this.floatValue7 = f6;
         this.floatValue8 = f7;
         this.floatValue9 = f8;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public int getIntValue2() {
         return this.intValue2;
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

      public float getFloatValue4() {
         return this.floatValue4;
      }

      public float getFloatValue5() {
         return this.floatValue5;
      }

      public float getFloatValue6() {
         return this.floatValue6;
      }

      public float getFloatValue7() {
         return this.floatValue7;
      }

      public float getFloatValue8() {
         return this.floatValue8;
      }

      public float getFloatValue9() {
         return this.floatValue9;
      }
   }

   private static final class Data5 {
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
         if (!(o instanceof Data5 other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.floatValue3, other.floatValue3);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, floatValue, floatValue2, floatValue3);
      }

      @Override
      public String toString() {
         return "Data5[" + "text=" + this.text + ", " + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "floatValue3=" + this.floatValue3 + "]";
      }

      private Data5(String text, float floatValue, float f, float f2) {
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
