package org.zenith.base.rotation.mods;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public final class MotorNet {
   public static final String text = "neurotest-motor-v1";
   public static final double CONST_doubleValue = 1.0E-7;
   public int intValue;
   public int intValue2;
   public int intValue3;
   public int intValue4;
   public int intValue5;
   public int intValue6;
   public int intValue7;
   public int intValue8;
   public double doubleValue;
   public double doubleValue2;
   public double doubleValue3;
   public double doubleValue4;
   public double doubleValue5;
   public double doubleValue6;
   public double doubleValue7;
   public double[][] doubleValues;
   public double[][] doubleValues2;
   public double[][] doubleValues3;
   public double[][] doubleValues4;
   public double[][] doubleValues5;
   public double[] doubleValues6;
   public double[] doubleValues7;
   public double[] doubleValues8;
   public double[] doubleValues9;
   public double[] doubleValues10;
   public double[] doubleValues11;
   public double doubleValue8;
   public String text2 = "";

   public static double method3597(double d) {
      return d > 20.0 ? d : Math.log1p(Math.exp(d));
   }

   public static double method3598(double d) {
      return 1.0 / (1.0 + Math.exp(-d));
   }

   public static double method3599(double x) {
      return x > 0.0 ? x : Math.expm1(x);
   }

   public static double[] method3600(double[] doubles) {
      double d = Double.NEGATIVE_INFINITY;

      for (double d2 : doubles) {
         d = Math.max(d, d2);
      }

      double d2 = 0.0;

      for (double d3 : doubles) {
         d2 += Math.exp(d3 - d);
      }

      double d3 = d + Math.log(d2);
      double[] doubles2 = new double[doubles.length];

      for (int i = 0; i < doubles.length; i++) {
         doubles2[i] = doubles[i] - d3;
      }

      return doubles2;
   }

   public static int method3601(double[] doubles, double d) {
      double[] doubles2 = method3600(doubles);
      double d2 = 0.0;

      int n;
      for (n = 0; n < doubles2.length; n++) {
         d2 += Math.exp(doubles2[n]);
         if (d2 > d) {
            break;
         }
      }

      return Math.min(n, doubles2.length - 1);
   }

   public static double[] method3602(double[][] doubleValues3, double[] doubles, double[] doubleValues8) {
      double[] doubles2 = new double[doubleValues3.length];

      for (int i = 0; i < doubleValues3.length; i++) {
         double d = doubleValues8[i];
         double[] doubles3 = doubleValues3[i];

         for (int j = 0; j < doubles3.length; j++) {
            d += doubles3[j] * doubles[j];
         }

         doubles2[i] = d;
      }

      return doubles2;
   }

   public static double[] method3603(double[] doubles, double[] original) {
      double[] doubles2 = new double[doubles.length + original.length];
      System.arraycopy(doubles, 0, doubles2, 0, doubles.length);
      System.arraycopy(original, 0, doubles2, doubles.length, original.length);
      return doubles2;
   }

   public double[] method3604(double[] doubles, double[] doubles2) {
      double[] doubles3 = method3602(this.doubleValues3, doubles, this.doubleValues8);
      double[] doubles4 = method3602(this.doubleValues4, doubles2, this.doubleValues9);
      double[] doubles5 = new double[this.intValue7];

      for (int i = 0; i < this.intValue7; i++) {
         double d = method3598(doubles3[i] + doubles4[i]);
         double d2 = method3598(doubles3[this.intValue7 + i] + doubles4[this.intValue7 + i]);
         double tanh = Math.tanh(doubles3[2 * this.intValue7 + i] + d * doubles4[2 * this.intValue7 + i]);
         doubles5[i] = (1.0 - d2) * tanh + d2 * doubles2[i];
      }

      return doubles5;
   }

   public double method3605(double d) {
      return Math.signum(d) * Math.log1p(Math.abs(d) / this.doubleValue5);
   }

   public double[] method3606(double[][] doubleses) {
      int length = doubleses.length;
      double[] doubles = new double[length];

      for (int i = 0; i < length; i++) {
         doubles[i] = Math.abs(doubleses[i][0]) + Math.abs(doubleses[i][1]) == 0.0 ? 1.0 : 0.0;
      }

      double[] doubles2 = new double[this.intValue2];
      int n = 0;

      for (int n2 : new int[]{3, 10, this.intValue}) {
         double d = 0.0;
         double d2 = 0.0;
         double d3 = 0.0;

         for (int i = length - n2; i < length; i++) {
            d += Math.abs(doubleses[i][0]);
            d2 += Math.abs(doubleses[i][1]);
            d3 += doubles[i];
         }

         doubles2[n++] = d / n2;
         doubles2[n++] = d2 / n2;
         doubles2[n++] = d3 / n2;
      }

      double d = 0.0;
      double d2 = 0.0;
      int n2 = 0;
      double d3 = 0.0;

      for (int i = 0; i < length; i++) {
         double d4 = 1.0 - doubles[i];
         d3 += d4;
         d = Math.max(d, d4 * i);
         double d5 = d4 * (i + 1.0);
         if (d5 > d2) {
            d2 = d5;
            n2 = i;
         }
      }

      doubles2[n++] = (length - 1.0 - d) / this.intValue;
      doubles2[n] = Math.abs(doubleses[n2][0]) * (d3 > 0.0 ? 1.0 : 0.0);
      return doubles2;
   }

   public static double method3607(double doubleValue) {
      return Math.signum(doubleValue) * Math.log1p(Math.abs(doubleValue)) / 5.0;
   }

   public static double[] method3608(double doubleValue, double d, int n, Double doubleValue2, double d2, double d3) {
      double x = Math.sqrt(doubleValue * doubleValue + d * d + 1.0E-9);
      return new double[]{
         method3607(doubleValue),
         method3607(d),
         Math.log1p(x) / 5.0,
         doubleValue2 == null ? 0.0 : method3607(doubleValue2),
         n / 2.0,
         0.0,
         Math.signum(doubleValue) * d2,
         Math.signum(d) * d3
      };
   }

   public double method3609(double[] doubles, double[] doubles2, double[] doubles3, double[] doubles4, double d, double d2, double d3) {
      int n = method3601(doubles, d);
      double[] doubles5 = new double[this.intValue6];
      double[] doubles6 = new double[this.intValue6];
      double[] doubles7 = method3600(doubles2);

      for (int i = 0; i < this.intValue6; i++) {
         doubles5[i] = Math.exp(Math.max(-7.0, Math.min(7.0, doubles4[i])));
         doubles6[i] = doubles7[i] - method3597((0.5 - doubles3[i]) / doubles5[i]);
      }

      int n2 = method3601(doubles6, d2);
      double d4 = method3598((0.5 - doubles3[n2]) / doubles5[n2]);
      double d5 = d4 + (1.0 - d4) * d3;
      d5 = Math.min(Math.max(d5, 1.0E-6), 0.999999);
      double d6 = doubles3[n2] + doubles5[n2] * (Math.log(d5) - Math.log1p(-d5));
      double d7 = Math.min(Math.max(Math.rint(d6), 1.0), this.doubleValue) + this.intValue5;
      return n < this.intValue5 ? n + 1.0 : d7;
   }

   public double method3610(double[] doubles, double doubleValue, double d, boolean flag, double[] doubles2, int n, double[] doubleValues) {
      double[] doubles3 = method3602(this.doubleValues5, doubles, this.doubleValues10);
      System.arraycopy(doubles3, 0, doubleValues, 0, doubles3.length);
      double d2 = doubles3[0];
      double d3 = doubles3[1];
      double[] doubles4 = new double[this.intValue5 + 1];
      System.arraycopy(doubles3, 2, doubles4, 0, this.intValue5 + 1);
      int n2 = 2 + this.intValue5 + 1;
      double[] doubles5 = new double[this.intValue6];
      double[] doubles6 = new double[this.intValue6];
      double[] doubles7 = new double[this.intValue6];
      boolean flag2 = doubles2[n + 1] < method3598(d3);
      double d4 = flag2 ? -d : d;
      boolean flag3 = d4 == Math.signum(doubleValue) && doubleValue != 0.0;
      double d5 = flag3 ? Math.abs(doubleValue) : 0.0;

      for (int i = 0; i < this.intValue6; i++) {
         doubles5[i] = doubles3[n2 + i];
         double d6 = this.doubleValue3 * method3598(doubles3[n2 + 2 * this.intValue6 + i]);
         double d7 = this.doubleValue2 * method3598(doubles3[n2 + 3 * this.intValue6 + i]);
         doubles6[i] = d6 * d5 + d7;
         doubles7[i] = Math.log(method3597(doubles3[n2 + this.intValue6 + i]) + this.doubleValue4 + this.doubleValue7 * doubles6[i]);
      }

      boolean flag4 = doubles2[n] < method3598(d2);
      double d6 = this.method3609(doubles4, doubles5, doubles6, doubles7, doubles2[n + 2], doubles2[n + 3], doubles2[n + 4]);
      double d7 = flag4 ? 0.0 : d4 * d6;
      return flag ? 0.0 : d7;
   }

   public MotorNet.Entry method3611(double[][] doubleValues, double[] original, double[] doubleValues2, double[] doubles) {
      double[][] doubleses = new double[this.intValue][2];
      double[] doubles2 = new double[this.intValue * 2];

      for (int i = 0; i < this.intValue; i++) {
         doubleses[i][0] = this.method3605(doubleValues[i][0]);
         doubleses[i][1] = this.method3605(doubleValues[i][1]);
         doubles2[2 * i] = doubleses[i][0];
         doubles2[2 * i + 1] = doubleses[i][1];
      }

      double[] doubles3 = method3603(method3603(doubles2, this.method3606(doubleses)), original);
      double[] doubles4 = method3602(this.doubleValues, doubles3, this.doubleValues6);

      for (int i = 0; i < doubles4.length; i++) {
         doubles4[i] = method3599(doubles4[i]);
      }

      double[] original2 = method3602(this.doubleValues2, doubles4, this.doubleValues7);

      for (int i = 0; i < original2.length; i++) {
         original2[i] = Math.tanh(original2[i]);
      }

      double d = original[0] >= 0.0 ? 1.0 : -1.0;
      double d2 = original[1] >= 0.0 ? 1.0 : -1.0;
      double doubleValue = doubleValues2[0];
      double doubleValue2 = doubleValues2[1];
      MotorNet.Entry entry = new MotorNet.Entry();
      double[] doubles5 = this.method3604(method3603(method3608(doubleValue, doubleValue2, 0, null, d, d2), original2), original2);
      double d3 = this.doubleValue8;

      for (int i = 0; i < this.intValue7; i++) {
         d3 += this.doubleValues11[i] * doubles5[i];
      }

      entry.doubleValue3 = d3;
      boolean flag = doubles[0] < method3598(d3);
      doubles5 = this.method3604(method3603(method3608(doubleValue, doubleValue2, 1, null, d, d2), original2), doubles5);
      entry.doubleValues = new double[this.intValue4];
      entry.doubleValue = this.method3610(doubles5, doubleValue, d, flag, doubles, 1, entry.doubleValues);
      doubles5 = this.method3604(method3603(method3608(doubleValue, doubleValue2, 2, entry.doubleValue, d, d2), original2), doubles5);
      entry.doubleValues2 = new double[this.intValue4];
      entry.doubleValue2 = this.method3610(doubles5, doubleValue2, d2, flag, doubles, 6, entry.doubleValues2);
      return entry;
   }

   public static double[] method3612(double floatValue, double d, double[] doubles, double[] doubles2, double[] doubles3, double d2, double d3, double attackStrengthScale) {
      double d4 = doubles2[3] - doubles2[0];
      double d5 = (doubles2[0] + doubles2[3]) / 2.0;
      double d6 = (doubles2[1] + doubles2[4]) / 2.0;
      double d7 = (doubles2[2] + doubles2[5]) / 2.0;
      double d8 = d5 - doubles[0];
      double d9 = d6 - doubles[1];
      double x = d7 - doubles[2];
      double x2 = Math.sqrt(d8 * d8 + x * x + 1.0E-12);
      double d10 = Math.toDegrees(Math.atan2(-d8, x));
      double d11 = Math.toDegrees(Math.atan2(-d9, x2));
      double x3 = Math.toDegrees(Math.atan2(d4 * 0.5, x2));
      double d12 = Math.toDegrees(Math.atan2(-(doubles2[4] - doubles[1]), x2));
      double d13 = Math.toDegrees(Math.atan2(-(doubles2[1] - doubles[1]), x2));
      double x4 = 0.5 * Math.abs(d13 - d12);
      double d14 = method3614(d10 - floatValue + 180.0, 360.0) - 180.0;
      double d15 = d11 - d;
      double x5 = Math.sqrt(d14 * d14 + d15 * d15 + 1.0E-12);
      return new double[]{
         method3615(d14 / Math.max(x3, 0.001), 12.0),
         method3615(d15 / Math.max(x4, 0.001), 12.0),
         Math.log1p(x5) / 5.0,
         d14 / x5,
         d15 / x5,
         Math.log1p(x3) / 3.0,
         Math.log1p(x4) / 3.0,
         doubles3[0],
         doubles3[1],
         doubles3[2],
         doubles3[3],
         doubles3[4],
         d2,
         d3,
         attackStrengthScale
      };
   }

   public double[] method3613(double[] original, double floatValue, double[] doubles) {
      if (this.intValue3 == original.length) {
         return original;
      } else if (this.intValue3 != original.length + 3) {
         throw new IllegalStateException("раскладка геометрии: модель ждёт " + this.intValue3);
      } else {
         double d = Math.toRadians(floatValue);
         double d2 = -doubles[0] * Math.sin(d) + doubles[2] * Math.cos(d);
         double d3 = -doubles[0] * Math.cos(d) - doubles[2] * Math.sin(d);
         double[] doubles2 = Arrays.copyOf(original, this.intValue3);
         doubles2[original.length] = Math.tanh(d2 / 0.3);
         doubles2[original.length + 1] = Math.tanh(d3 / 0.3);
         doubles2[original.length + 2] = Math.tanh(doubles[1] / 0.3);
         return doubles2;
      }
   }

   public static double method3614(double d, double d2) {
      double d3 = d % d2;
      return d3 < 0.0 ? d3 + d2 : d3;
   }

   public static double method3615(double d, double d2) {
      return Math.max(-d2, Math.min(d2, d));
   }

   public static MotorNet method3616(InputStream inputStream) throws IOException {
      InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);

      JsonObject jsonObject;
      try {
         jsonObject = JsonParser.parseReader(inputStreamReader).getAsJsonObject();
      } catch (Throwable e) {
         try {
            inputStreamReader.close();
         } catch (Throwable ex) {
            e.addSuppressed(ex);
         }

         throw e;
      }

      inputStreamReader.close();
      String string = jsonObject.get("format").getAsString();
      if (!"neurotest-motor-v1".equals(string)) {
         throw new IOException("формат " + string + ", ожидался neurotest-motor-v1");
      } else {
         MotorNet motorNet = new MotorNet();
         JsonObject json = jsonObject.getAsJsonObject("const");
         motorNet.intValue = json.get("p_ctx").getAsInt();
         motorNet.intValue2 = json.get("n_sum").getAsInt();
         motorNet.intValue3 = json.get("n_geom").getAsInt();
         motorNet.intValue4 = json.get("n_out").getAsInt();
         motorNet.intValue5 = json.get("m_core").getAsInt();
         motorNet.intValue6 = json.get("k_mix").getAsInt();
         motorNet.intValue7 = json.get("hid").getAsInt();
         motorNet.intValue8 = json.get("n_u").getAsInt();
         motorNet.doubleValue = json.get("c_max").getAsDouble();
         motorNet.doubleValue2 = json.get("b_max").getAsDouble();
         motorNet.doubleValue3 = json.get("k_max").getAsDouble();
         motorNet.doubleValue4 = json.get("s_min").getAsDouble();
         motorNet.doubleValue7 = json.has("s_rel") ? json.get("s_rel").getAsDouble() : 0.0;
         motorNet.doubleValue5 = json.get("scale").getAsDouble();
         motorNet.doubleValue6 = json.get("mdpc_train").getAsDouble();
         JsonObject jsonObject2 = jsonObject.getAsJsonObject("weights");
         motorNet.doubleValues = method3619(jsonObject2, "cond.0.weight");
         motorNet.doubleValues6 = method3618(jsonObject2, "cond.0.bias");
         motorNet.doubleValues2 = method3619(jsonObject2, "cond.2.weight");
         motorNet.doubleValues7 = method3618(jsonObject2, "cond.2.bias");
         motorNet.doubleValues3 = method3619(jsonObject2, "cell.weight_ih");
         motorNet.doubleValues4 = method3619(jsonObject2, "cell.weight_hh");
         motorNet.doubleValues8 = method3618(jsonObject2, "cell.bias_ih");
         motorNet.doubleValues9 = method3618(jsonObject2, "cell.bias_hh");
         motorNet.doubleValues5 = method3619(jsonObject2, "head.weight");
         motorNet.doubleValues10 = method3618(jsonObject2, "head.bias");
         motorNet.doubleValues11 = method3618(jsonObject2, "rest.weight");
         motorNet.doubleValue8 = method3618(jsonObject2, "rest.bias")[0];
         method3617(motorNet.doubleValues[0].length == motorNet.intValue * 2 + motorNet.intValue2 + motorNet.intValue3, "вход cond");
         method3617(motorNet.doubleValues3.length == 3 * motorNet.intValue7 && motorNet.doubleValues3[0].length == 8 + motorNet.intValue7, "вход GRU");
         method3617(motorNet.doubleValues5.length == motorNet.intValue4, "голова");
         motorNet.method3621(jsonObject);
         return motorNet;
      }
   }

   public static void method3617(boolean flag, String str) throws IOException {
      if (!flag) {
         throw new IOException("не тот размер: " + str);
      }
   }

   public static double[] method3618(JsonObject json, String memberName) throws IOException {
      JsonElement jsonElement = json.get(memberName);
      if (jsonElement == null) {
         throw new IOException("нет весов " + memberName);
      } else {
         return method3620(jsonElement.getAsJsonArray());
      }
   }

   public static double[][] method3619(JsonObject json, String memberName) throws IOException {
      JsonElement jsonElement = json.get(memberName);
      if (jsonElement == null) {
         throw new IOException("нет весов " + memberName);
      } else {
         JsonArray jsonArray = jsonElement.getAsJsonArray();
         double[][] doubleses = new double[jsonArray.size()][];

         for (int i = 0; i < jsonArray.size(); i++) {
            doubleses[i] = method3620(jsonArray.get(i).getAsJsonArray());
         }

         return doubleses;
      }
   }

   public static double[] method3620(JsonArray jsonArray) {
      double[] doubles = new double[jsonArray.size()];

      for (int i = 0; i < jsonArray.size(); i++) {
         doubles[i] = jsonArray.get(i).getAsDouble();
      }

      return doubles;
   }

   public void method3621(JsonObject jsonObject) throws IOException {
      double d = 0.0;

      for (JsonElement json : jsonObject.getAsJsonArray("ref_feats")) {
         JsonObject jsonObject2 = json.getAsJsonObject();
         double[] doubles = method3620(jsonObject2.getAsJsonArray("cam"));
         double[] original = method3612(
            doubles[0],
            doubles[1],
            method3620(jsonObject2.getAsJsonArray("eye")),
            method3620(jsonObject2.getAsJsonArray("box")),
            method3620(jsonObject2.getAsJsonArray("keys")),
            jsonObject2.get("ground").getAsDouble(),
            jsonObject2.get("sprint").getAsDouble(),
            jsonObject2.get("cool").getAsDouble()
         );
         if (jsonObject2.has("vel")) {
            original = this.method3613(original, doubles[0], method3620(jsonObject2.getAsJsonArray("vel")));
         }

         double[] doubles2 = method3620(jsonObject2.getAsJsonArray("g"));
         method3617(original.length == doubles2.length, "длина геометрии в эталоне");

         for (int i = 0; i < doubles2.length; i++) {
            d = Math.max(d, Math.abs(original[i] - doubles2[i]));
         }
      }

      double d2 = 0.0;
      int n = 0;
      int n2 = 0;

      for (JsonElement json : jsonObject.getAsJsonArray("ref_ticks")) {
         JsonObject jsonObject2 = json.getAsJsonObject();
         JsonArray jsonArray = jsonObject2.getAsJsonArray("hist");
         double[][] doubleValues = new double[jsonArray.size()][];

         for (int i = 0; i < jsonArray.size(); i++) {
            doubleValues[i] = method3620(jsonArray.get(i).getAsJsonArray());
         }

         MotorNet.Entry entry = this.method3611(
            doubleValues, method3620(jsonObject2.getAsJsonArray("g")), method3620(jsonObject2.getAsJsonArray("v")), method3620(jsonObject2.getAsJsonArray("u"))
         );
         d2 = Math.max(d2, Math.abs(entry.doubleValue3 - jsonObject2.get("rest").getAsDouble()));
         double[] doubles = method3620(jsonObject2.getAsJsonArray("oy"));
         double[] doubles2 = method3620(jsonObject2.getAsJsonArray("op"));

         for (int i = 0; i < doubles.length; i++) {
            d2 = Math.max(d2, Math.abs(entry.doubleValues[i] - doubles[i]));
            d2 = Math.max(d2, Math.abs(entry.doubleValues2[i] - doubles2[i]));
         }

         if (entry.doubleValue != jsonObject2.get("dy").getAsDouble() || entry.doubleValue2 != jsonObject2.get("dp").getAsDouble()) {
            n++;
         }

         n2++;
      }

      this.text2 = String.format("мотор: признаки сошлись до %.2e, тики до %.2e, счета совпали в %d из %d", d, d2, n2 - n, n2);
      if (d > 1.0E-7 || d2 > 1.0E-7 || n > 0) {
         throw new IOException("ЭТАЛОН РАЗОШЁЛСЯ — " + this.text2);
      }
   }

   public static MotorNet method3622(Path path) throws IOException {
      MotorNet motorNet;
      try (InputStream inputStream = Files.newInputStream(path)) {
         motorNet = method3616(inputStream);
      }

      return motorNet;
   }

   public static void main(String[] strings) throws Exception {
      MotorNet motorNet = method3622(Path.of(strings[0]));
      System.out.println(motorNet.text2);
      System.out.println("ок: скрытых " + motorNet.intValue7 + ", сенса обучения " + motorNet.doubleValue6);
   }

   public static final class Entry {
      public double doubleValue;
      public double doubleValue2;
      public double doubleValue3;
      public double[] doubleValues;
      public double[] doubleValues2;
   }
}
