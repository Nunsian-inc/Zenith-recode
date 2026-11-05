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

public final class MotorClaudeNet {
   public static final String text = "neurotest-motor-claude-v1";
   public static final double CONST_doubleValue = 1.0E-7;
   public int intValue;
   public int intValue2;
   public int intValue3;
   public int intValue4;
   public int intValue5;
   public int intValue6;
   public int intValue7;
   public int intValue8;
   public int intValue9;
   public double doubleValue;
   public double doubleValue2;
   public double doubleValue3;
   public double doubleValue4;
   public double doubleValue5;
   public double doubleValue6;
   public double doubleValue7;
   public double doubleValue8;
   public double doubleValue9;
   public double doubleValue10;
   public double doubleValue11;
   public double doubleValue12;
   public double doubleValue13;
   public double doubleValue14;
   public double doubleValue15;
   public double doubleValue16;
   public double[][] doubleValues;
   public double[][] doubleValues2;
   public double[][] doubleValues3;
   public double[][] doubleValues4;
   public double[][] doubleValues5;
   public double[][] doubleValues6;
   public double[][] doubleValues7;
   public double[] doubleValues8;
   public double[] doubleValues9;
   public double[] doubleValues10;
   public double[] doubleValues11;
   public double[] doubleValues12;
   public double[] doubleValues13;
   public double[] doubleValues14;
   public String text2 = "";

   public MotorClaudeNet.Entry method3563() {
      return new MotorClaudeNet.Entry();
   }

   public static double method3564(double d) {
      return 1.0 / (1.0 + Math.exp(-d));
   }

   public static double method3565(double d) {
      return d >= 0.0 ? -Math.log1p(Math.exp(-d)) : d - Math.log1p(Math.exp(d));
   }

   public static double method3566(double d) {
      return d > 0.0 ? d + Math.log1p(Math.exp(-d)) : Math.log1p(Math.exp(d));
   }

   public static double method3567(double x) {
      return x > 0.0 ? x : Math.expm1(x);
   }

   public static double[] method3568(double[] doubles) {
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

   public static int method3569(double[] doubles, double d) {
      double[] doubles2 = method3568(doubles);
      double d2 = 0.0;

      for (int i = 0; i < doubles2.length; i++) {
         d2 += Math.exp(doubles2[i]);
         if (d2 > d) {
            return i;
         }
      }

      return doubles2.length - 1;
   }

   public static double[] method3570(double[][] doubleValues2, double[] doubleValues9, double[] doubleValues) {
      double[] doubles = new double[doubleValues2.length];

      for (int i = 0; i < doubleValues2.length; i++) {
         double d = 0.0;
         double[] doubles2 = doubleValues2[i];

         for (int j = 0; j < doubles2.length; j++) {
            d += doubles2[j] * doubleValues[j];
         }

         doubles[i] = d + doubleValues9[i];
      }

      return doubles;
   }

   public static double[] method3571(double[] doubles) {
      double[] doubles2 = new double[doubles.length];

      for (int i = 0; i < doubles.length; i++) {
         doubles2[i] = method3567(doubles[i]);
      }

      return doubles2;
   }

   public double method3572(double d) {
      return Math.log1p(Math.abs(d) / this.doubleValue);
   }

   public double method3573(double d) {
      return Math.signum(d) * this.method3572(d) / 3.0;
   }

   public double[] method3574(MotorClaudeNet.Entry entry, double[] doubles, double[] doubles2, double log, double d, double log2) {
      double[] doubles3 = new double[this.intValue2];
      int n = 0;
      double[][] doubleValues = entry.doubleValues;
      double[] doubles4 = doubleValues[this.intValue4 - 1];
      double[] doubles5 = doubleValues[this.intValue4 - 2];
      double[] doubles6 = doubleValues[this.intValue4 - 3];

      for (int i = 0; i < 2; i++) {
         doubles3[n++] = this.method3573(doubles4[i]);
      }

      for (int i = 0; i < 2; i++) {
         doubles3[n++] = this.method3573(doubles5[i]);
      }

      for (int i = 0; i < 2; i++) {
         doubles3[n++] = this.method3573(doubles6[i]);
      }

      for (int i = 0; i < 2; i++) {
         doubles3[n++] = this.method3573(doubles4[i] - 2.0 * doubles5[i] + doubles6[i]);
      }

      for (int i = 0; i < 2; i++) {
         doubles3[n++] = Math.log1p(entry.doubleValues2[i]) / 3.0;
      }

      for (int i = 0; i < 2; i++) {
         doubles3[n++] = Math.log1p(entry.doubleValues3[i]) / 3.0;
      }

      for (int i = 0; i < 2; i++) {
         double d2 = Double.NEGATIVE_INFINITY;

         for (int j = this.intValue4 - this.intValue5; j < this.intValue4; j++) {
            d2 = Math.max(d2, this.method3572(doubleValues[j][i]));
         }

         doubles3[n++] = (this.method3572(doubleValues[this.intValue4 - 1][i]) - d2) / 3.0;
      }

      for (int i = 0; i < 2; i++) {
         doubles3[n++] = this.method3572(entry.doubleValues5[i]) / 3.0;
      }

      for (int n2 : new int[]{3, 10, 30}) {
         for (int i = 0; i < 2; i++) {
            double d2 = 0.0;

            for (int j = this.intValue4 - n2; j < this.intValue4; j++) {
               d2 += this.method3572(doubleValues[j][i]);
            }

            doubles3[n++] = d2 / n2 / 3.0;
         }
      }

      for (int n2 : new int[]{3, 10, 30}) {
         double d2 = 0.0;

         for (int i = this.intValue4 - n2; i < this.intValue4; i++) {
            d2 += doubleValues[i][0] == 0.0 && doubleValues[i][1] == 0.0 ? 1.0 : 0.0;
         }

         doubles3[n++] = d2 / n2;
      }

      doubles3[n++] = Math.min(entry.doubleValue, this.doubleValue2) / this.doubleValue2;
      doubles3[n++] = Math.tanh(entry.doubleValue / this.doubleValue3);
      doubles3[n++] = Math.min(entry.doubleValue2, this.doubleValue2) / this.doubleValue2;
      doubles3[n++] = Math.tanh(entry.doubleValue2 / this.doubleValue3);
      double[] doubles7 = new double[]{Math.abs(doubles[0]) < this.doubleValue4 ? 0.0 : doubles[0], Math.abs(doubles[1]) < this.doubleValue4 ? 0.0 : doubles[1]};
      double d2 = Math.sqrt(doubles7[0] * doubles7[0] + doubles7[1] * doubles7[1]);
      doubles3[n++] = this.method3573(doubles7[0]);
      doubles3[n++] = this.method3573(doubles7[1]);
      doubles3[n++] = doubles7[0] / (d2 + this.doubleValue5);
      doubles3[n++] = doubles7[1] / (d2 + this.doubleValue5);
      doubles3[n++] = asinh(d2) / 3.0;
      doubles3[n++] = Math.signum(doubles4[0]) * Math.signum(doubles7[0]);
      doubles3[n++] = Math.signum(doubles4[1]) * Math.signum(doubles7[1]);
      doubles3[n++] = this.method3573(doubles2[0]);
      doubles3[n++] = this.method3573(doubles2[1]);
      doubles3[n++] = log - this.doubleValue6;
      doubles3[n++] = d / 90.0;
      doubles3[n++] = log2 - this.doubleValue7;
      if (n != this.intValue2) {
         throw new IllegalStateException("признаков " + n + ", ждём " + this.intValue2);
      } else {
         return doubles3;
      }
   }

   public static double asinh(double d) {
      return Math.log(d + Math.sqrt(d * d + 1.0));
   }

   public void method3575(MotorClaudeNet.Entry entry, double[] doubles) {
      boolean flag = true;

      for (int i = 0; i < 2; i++) {
         boolean flag2 = doubles[i] == 0.0;
         double d = Math.signum(doubles[i]);
         boolean flag3 = !flag2 && entry.doubleValues4[i] != 0.0 && d != entry.doubleValues4[i];
         entry.doubleValues2[i] = flag2 ? entry.doubleValues2[i] + 1.0 : 0.0;
         entry.doubleValues3[i] = flag3 ? 0.0 : entry.doubleValues3[i] + 1.0;
         if (!flag2) {
            entry.doubleValues4[i] = d;
            entry.doubleValues5[i] = Math.abs(doubles[i]);
         }

         flag &= flag2;
      }

      entry.doubleValue = flag ? 0.0 : entry.doubleValue + 1.0;
      entry.doubleValue2 = flag ? entry.doubleValue2 + 1.0 : 0.0;

      for (int i = 0; i + 1 < this.intValue4; i++) {
         entry.doubleValues[i][0] = entry.doubleValues[i + 1][0];
         entry.doubleValues[i][1] = entry.doubleValues[i + 1][1];
      }

      entry.doubleValues[this.intValue4 - 1][0] = doubles[0];
      entry.doubleValues[this.intValue4 - 1][1] = doubles[1];
   }

   public double[] method3576(double[] doubleValues, double[] doubleValues7) {
      double[] doubles = method3570(this.doubleValues2, this.doubleValues9, doubleValues);
      double[] doubles2 = method3570(this.doubleValues3, this.doubleValues10, doubleValues7);
      double[] doubles3 = new double[this.intValue];

      for (int i = 0; i < this.intValue; i++) {
         double d = method3564(doubles[i] + doubles2[i]);
         double d2 = method3564(doubles[this.intValue + i] + doubles2[this.intValue + i]);
         double tanh = Math.tanh(doubles[2 * this.intValue + i] + d * doubles2[2 * this.intValue + i]);
         doubles3[i] = (1.0 - d2) * tanh + d2 * doubleValues7[i];
      }

      return doubles3;
   }

   public double[] method3577(double[][] doubleValues4, double[] doubleValues11, double[][] doubleValues5, double[] doubleValues12, double[] doubleValues7) {
      double[] doubles = method3570(doubleValues5, doubleValues12, method3571(method3570(doubleValues4, doubleValues11, doubleValues7)));
      if (this.doubleValue15 != 1.0) {
         int n = 2 + this.intValue6 + 1;

         for (int i = 2; i < n + this.intValue7; i++) {
            doubles[i] /= this.doubleValue15;
         }
      }

      return doubles;
   }

   public double method3578(double[] doubleValues2, double d, double d2, double[] doubles, int n) {
      boolean flag = doubles[n] < method3564(doubleValues2[0]);
      boolean flag2 = doubles[n + 1] < method3564(doubleValues2[1]);
      double d3 = flag2 ? -d2 : d2;
      double[] doubles2 = new double[this.intValue6 + 1];
      System.arraycopy(doubleValues2, 2, doubles2, 0, this.intValue6 + 1);
      int n2 = method3569(doubles2, doubles[n + 2]);
      double d4;
      if (n2 < this.intValue6) {
         d4 = n2 + 1.0;
      } else {
         double d5 = d != 0.0 && Math.signum(d) == d3 ? Math.abs(d) : 0.0;
         int n3 = 2 + this.intValue6 + 1;
         double[] doubles3 = new double[this.intValue7];
         double[] doubles4 = new double[this.intValue7];
         double[] doubles5 = new double[this.intValue7];
         double[] doubles6 = new double[this.intValue7];

         for (int i = 0; i < this.intValue7; i++) {
            doubles3[i] = doubleValues2[n3 + i];
         }

         double[] doubles7 = method3568(doubles3);
         double d6 = this.intValue6 + 0.5;

         for (int i = 0; i < this.intValue7; i++) {
            double d7 = this.doubleValue8 * method3564(doubleValues2[n3 + 2 * this.intValue7 + i]);
            double exp = Math.exp(Math.max(this.doubleValue9, Math.min(this.doubleValue10, doubleValues2[n3 + 3 * this.intValue7 + i])));
            doubles4[i] = d7 * d5 + exp;
            doubles5[i] = (method3566(doubleValues2[n3 + this.intValue7 + i]) + this.doubleValue11 + this.doubleValue12 * doubles4[i]) * this.doubleValue14;
            doubles6[i] = doubles7[i] + method3565(-(d6 - doubles4[i]) / doubles5[i]);
         }

         int n4 = method3569(doubles6, doubles[n + 3]);
         double d7 = method3564((d6 - doubles4[n4]) / doubles5[n4]);
         double d8 = Math.min(Math.max(d7 + (1.0 - d7) * doubles[n + 4], 1.0E-6), 0.999999);
         double d9 = doubles4[n4] + doubles5[n4] * (Math.log(d8) - Math.log1p(-d8));
         d4 = Math.min(Math.max(Math.rint(d9), this.intValue6 + 1.0), this.doubleValue13);
      }

      return flag ? 0.0 : d3 * d4;
   }

   public MotorClaudeNet.Entry2 method3579(
      MotorClaudeNet.Entry entry, double[] doubles, double[] doubles2, double d, double d2, double log, double[] doubles3, double[] doubles4
   ) {
      MotorClaudeNet.Entry2 entry2 = new MotorClaudeNet.Entry2();
      entry2.doubleValues = this.method3574(entry, doubles, doubles2, log, d, Math.log(d2));
      entry.doubleValues7 = this.method3576(method3571(method3570(this.doubleValues, this.doubleValues8, entry2.doubleValues)), entry.doubleValues7);
      double[] doubles5 = new double[]{entry.doubleValues4[0] == 0.0 ? 1.0 : entry.doubleValues4[0], entry.doubleValues4[1] == 0.0 ? 1.0 : entry.doubleValues4[1]};
      entry2.doubleValues2 = this.method3577(this.doubleValues4, this.doubleValues11, this.doubleValues5, this.doubleValues12, entry.doubleValues7);
      entry2.doubleValue = doubles4 != null ? doubles4[0] : this.method3578(entry2.doubleValues2, entry.doubleValues6[0], doubles5[0], doubles3, 0);
      double[] doubleValues7 = new double[this.intValue + 2];
      System.arraycopy(entry.doubleValues7, 0, doubleValues7, 0, this.intValue);
      doubleValues7[this.intValue] = this.method3573(entry2.doubleValue * d2);
      doubleValues7[this.intValue + 1] = entry2.doubleValue == 0.0 ? 1.0 : 0.0;
      entry2.doubleValues3 = this.method3577(this.doubleValues6, this.doubleValues13, this.doubleValues7, this.doubleValues14, doubleValues7);
      entry2.doubleValue2 = doubles4 != null ? doubles4[1] : this.method3578(entry2.doubleValues3, entry.doubleValues6[1], doubles5[1], doubles3, 5);
      this.method3575(entry, new double[]{entry2.doubleValue * d2, entry2.doubleValue2 * d2});
      entry.doubleValues6[0] = entry2.doubleValue;
      entry.doubleValues6[1] = entry2.doubleValue2;
      return entry2;
   }

   public double getDoubleValue16() {
      return this.doubleValue16;
   }

   public int getIntValue8() {
      return this.intValue8;
   }

   public int getIntValue9() {
      return this.intValue9;
   }

   public static MotorClaudeNet method3580(InputStream inputStream) throws IOException {
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
      if (!"neurotest-motor-claude-v1".equals(string)) {
         throw new IOException("формат " + string + ", ожидался neurotest-motor-claude-v1");
      } else {
         MotorClaudeNet motorClaudeNet = new MotorClaudeNet();
         JsonObject json = jsonObject.getAsJsonObject("const");
         motorClaudeNet.intValue = json.get("hid").getAsInt();
         motorClaudeNet.intValue2 = json.get("n_feat").getAsInt();
         motorClaudeNet.intValue3 = json.get("n_out").getAsInt();
         motorClaudeNet.intValue4 = json.get("hist").getAsInt();
         motorClaudeNet.intValue5 = json.get("peak").getAsInt();
         motorClaudeNet.intValue6 = json.get("m_core").getAsInt();
         motorClaudeNet.intValue7 = json.get("k_mix").getAsInt();
         motorClaudeNet.intValue8 = json.get("warm").getAsInt();
         motorClaudeNet.intValue9 = json.get("n_u").getAsInt();
         motorClaudeNet.doubleValue = json.get("sc").getAsDouble();
         motorClaudeNet.doubleValue2 = json.get("fine").getAsDouble();
         motorClaudeNet.doubleValue3 = json.get("coarse").getAsDouble();
         motorClaudeNet.doubleValue4 = json.get("r_eps").getAsDouble();
         motorClaudeNet.doubleValue5 = json.get("dir_eps").getAsDouble();
         motorClaudeNet.doubleValue6 = json.get("log_k0").getAsDouble();
         motorClaudeNet.doubleValue7 = json.get("log_g0").getAsDouble();
         motorClaudeNet.doubleValue8 = json.get("k_max").getAsDouble();
         motorClaudeNet.doubleValue9 = json.get("b_log_lo").getAsDouble();
         motorClaudeNet.doubleValue10 = json.get("b_log_hi").getAsDouble();
         motorClaudeNet.doubleValue11 = json.get("s_min").getAsDouble();
         motorClaudeNet.doubleValue12 = json.get("s_rel").getAsDouble();
         motorClaudeNet.doubleValue13 = json.get("c_max").getAsDouble();
         motorClaudeNet.doubleValue14 = json.get("tau").getAsDouble();
         motorClaudeNet.doubleValue15 = json.get("temp_d").getAsDouble();
         motorClaudeNet.doubleValue16 = json.get("k_default").getAsDouble();
         JsonObject jsonObject2 = jsonObject.getAsJsonObject("weights");
         motorClaudeNet.doubleValues = method3583(jsonObject2, "inp.0.weight");
         motorClaudeNet.doubleValues8 = method3582(jsonObject2, "inp.0.bias");
         motorClaudeNet.doubleValues2 = method3583(jsonObject2, "gru.weight_ih_l0");
         motorClaudeNet.doubleValues3 = method3583(jsonObject2, "gru.weight_hh_l0");
         motorClaudeNet.doubleValues9 = method3582(jsonObject2, "gru.bias_ih_l0");
         motorClaudeNet.doubleValues10 = method3582(jsonObject2, "gru.bias_hh_l0");
         motorClaudeNet.doubleValues4 = method3583(jsonObject2, "hy.0.weight");
         motorClaudeNet.doubleValues11 = method3582(jsonObject2, "hy.0.bias");
         motorClaudeNet.doubleValues5 = method3583(jsonObject2, "hy.2.weight");
         motorClaudeNet.doubleValues12 = method3582(jsonObject2, "hy.2.bias");
         motorClaudeNet.doubleValues6 = method3583(jsonObject2, "hp.0.weight");
         motorClaudeNet.doubleValues13 = method3582(jsonObject2, "hp.0.bias");
         motorClaudeNet.doubleValues7 = method3583(jsonObject2, "hp.2.weight");
         motorClaudeNet.doubleValues14 = method3582(jsonObject2, "hp.2.bias");
         method3581(motorClaudeNet.doubleValues[0].length == motorClaudeNet.intValue2 && motorClaudeNet.doubleValues.length == motorClaudeNet.intValue, "вход");
         method3581(motorClaudeNet.doubleValues2.length == 3 * motorClaudeNet.intValue && motorClaudeNet.doubleValues2[0].length == motorClaudeNet.intValue, "GRU вход");
         method3581(motorClaudeNet.doubleValues3.length == 3 * motorClaudeNet.intValue && motorClaudeNet.doubleValues3[0].length == motorClaudeNet.intValue, "GRU состояние");
         method3581(motorClaudeNet.doubleValues5.length == motorClaudeNet.intValue3 && motorClaudeNet.doubleValues7.length == motorClaudeNet.intValue3, "головы");
         method3581(motorClaudeNet.doubleValues6[0].length == motorClaudeNet.intValue + 2, "голова pitch");
         motorClaudeNet.method3585(jsonObject.getAsJsonObject("ref"));
         return motorClaudeNet;
      }
   }

   public static void method3581(boolean flag, String str) throws IOException {
      if (!flag) {
         throw new IOException("не тот размер: " + str);
      }
   }

   public static double[] method3582(JsonObject json, String memberName) throws IOException {
      JsonElement jsonElement = json.get(memberName);
      if (jsonElement == null) {
         throw new IOException("нет весов " + memberName);
      } else {
         return method3584(jsonElement.getAsJsonArray());
      }
   }

   public static double[][] method3583(JsonObject json, String memberName) throws IOException {
      JsonElement jsonElement = json.get(memberName);
      if (jsonElement == null) {
         throw new IOException("нет весов " + memberName);
      } else {
         JsonArray jsonArray = jsonElement.getAsJsonArray();
         double[][] doubleses = new double[jsonArray.size()][];

         for (int i = 0; i < jsonArray.size(); i++) {
            doubleses[i] = method3584(jsonArray.get(i).getAsJsonArray());
         }

         return doubleses;
      }
   }

   public static double[] method3584(JsonArray jsonArray) {
      double[] doubles = new double[jsonArray.size()];

      for (int i = 0; i < jsonArray.size(); i++) {
         doubles[i] = jsonArray.get(i).getAsDouble();
      }

      return doubles;
   }

   public void method3585(JsonObject jsonObject) throws IOException {
      double d = jsonObject.get("g").getAsDouble();
      double log = jsonObject.get("log_k").getAsDouble();
      MotorClaudeNet.Entry entry = this.method3563();
      double d2 = 0.0;
      double d3 = 0.0;
      int n = 0;
      int n2 = 0;

      for (JsonElement json : jsonObject.getAsJsonArray("ticks")) {
         JsonObject jsonObject2 = json.getAsJsonObject();
         double[] doubles = jsonObject2.get("force").isJsonNull() ? null : method3584(jsonObject2.getAsJsonArray("force"));
         MotorClaudeNet.Entry2 entry2 = this.method3579(
            entry,
            method3584(jsonObject2.getAsJsonArray("r")),
            method3584(jsonObject2.getAsJsonArray("bvel")),
            jsonObject2.get("pitch").getAsDouble(),
            d,
            log,
            method3584(jsonObject2.getAsJsonArray("u")),
            doubles
         );
         d2 = Math.max(d2, method3586(entry2.doubleValues, method3584(jsonObject2.getAsJsonArray("x"))));
         d3 = Math.max(d3, method3586(entry2.doubleValues2, method3584(jsonObject2.getAsJsonArray("oy"))));
         d3 = Math.max(d3, method3586(entry2.doubleValues3, method3584(jsonObject2.getAsJsonArray("op"))));
         double[] doubles2 = method3584(jsonObject2.getAsJsonArray("c"));
         if (entry2.doubleValue != doubles2[0] || entry2.doubleValue2 != doubles2[1]) {
            n++;
         }

         n2++;
      }

      this.text2 = String.format("мотор Claude: признаки сошлись до %.2e, выходы голов до %.2e, счета совпали в %d из %d", d2, d3, n2 - n, n2);
      if (d2 > 1.0E-7 || d3 > 1.0E-7 || n > 0) {
         throw new IOException("ЭТАЛОН РАЗОШЁЛСЯ — " + this.text2);
      }
   }

   public static double method3586(double[] doubleValues, double[] doubles) {
      if (doubleValues.length != doubles.length) {
         return Double.POSITIVE_INFINITY;
      } else {
         double d = 0.0;

         for (int i = 0; i < doubleValues.length; i++) {
            d = Math.max(d, Math.abs(doubleValues[i] - doubles[i]));
         }

         return d;
      }
   }

   public static MotorClaudeNet method3587(Path path) throws IOException {
      MotorClaudeNet motorClaudeNet;
      try (InputStream inputStream = Files.newInputStream(path)) {
         motorClaudeNet = method3580(inputStream);
      }

      return motorClaudeNet;
   }

   public static void main(String[] strings) throws Exception {
      MotorClaudeNet motorClaudeNet = method3587(Path.of(strings[0]));
      System.out.println(motorClaudeNet.text2);
      System.out.println("ок: скрытых " + motorClaudeNet.intValue + ", признаков " + motorClaudeNet.intValue2 + ", K по умолчанию " + motorClaudeNet.doubleValue16);
   }

   public final class Entry {
      public final double[][] doubleValues = new double[MotorClaudeNet.this.intValue4][2];
      public final double[] doubleValues2 = new double[2];
      public final double[] doubleValues3 = new double[2];
      public final double[] doubleValues4 = new double[2];
      public final double[] doubleValues5 = new double[2];
      public final double[] doubleValues6 = new double[2];
      public double doubleValue;
      public double doubleValue2;
      public double[] doubleValues7 = new double[MotorClaudeNet.this.intValue];
   }

   public static final class Entry2 {
      public double doubleValue;
      public double doubleValue2;
      public double[] doubleValues;
      public double[] doubleValues2;
      public double[] doubleValues3;
   }
}
