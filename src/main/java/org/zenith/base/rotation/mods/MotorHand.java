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

public final class MotorHand {
   public static final String text = "neurotest-motor-hand-v1";
   public int w;
   public int intValue;
   public double doubleValue;
   public double doubleValue2;
   public double doubleValue3;
   public double doubleValue4;
   public double[] doubleValues;
   public double[][][] doubleValues2;
   public double[][] doubleValues3;
   public String text2 = "";

   public MotorHand.Entry method3588() {
      return new MotorHand.Entry();
   }

   public static double method3589(double d, double d2) {
      double d3 = d / d2;
      return Math.signum(d3) * Math.log(Math.abs(d3) + Math.sqrt(d3 * d3 + 1.0));
   }

   public static double method3590(double d) {
      double d2 = (d + 180.0) % 360.0;
      if (d2 < 0.0) {
         d2 += 360.0;
      }

      return d2 - 180.0;
   }

   public int method3591(MotorHand.Entry entry, double[] doubles, double nextDouble) {
      double[] doubles2 = new double[]{
         method3589(doubles[0], this.doubleValue4) * this.doubleValues[0],
         method3589(doubles[1], this.doubleValue4) * this.doubleValues[1],
         method3589(entry.doubleValues2[0], this.doubleValue4) * this.doubleValues[2],
         method3589(entry.doubleValues2[1], this.doubleValue4) * this.doubleValues[3]
      };
      int intValue = this.intValue;
      int[] ints = new int[intValue];
      double[] doubles3 = new double[intValue];
      int n = 0;

      for (int i = 0; i < this.doubleValues3.length; i++) {
         double[] doubles4 = this.doubleValues3[i];
         double d = 0.0;

         for (int j = 0; j < 4; j++) {
            double d2 = doubles4[j] - doubles2[j];
            d += d2 * d2;
         }

         if (n < intValue || d < doubles3[n - 1]) {
            int n2;
            for (n2 = n < intValue ? n : intValue - 1; n2 > 0 && doubles3[n2 - 1] > d; n2--) {
               if (n2 < intValue) {
                  doubles3[n2] = doubles3[n2 - 1];
                  ints[n2] = ints[n2 - 1];
               }
            }

            doubles3[n2] = d;
            ints[n2] = i;
            if (n < intValue) {
               n++;
            }
         }
      }

      return ints[Math.min(intValue - 1, (int)(nextDouble * intValue))];
   }

   public double[] method3592(MotorHand.Entry entry, double[] doubles, double[] doubles2, double d, double nextDouble) {
      double d2 = method3590(doubles[0] - doubles2[0]);
      double d3 = doubles[1] - doubles2[1];
      double d4 = 0.0;
      double d5 = 0.0;
      if (entry.doubleValues4 != null) {
         d4 = method3590(doubles[0] - entry.doubleValues4[0]);
         d5 = doubles[1] - entry.doubleValues4[1];
      }

      entry.doubleValues4 = new double[]{doubles[0], doubles[1]};
      int n = -1;
      if (entry.doubleValues == null || entry.pos >= this.w) {
         double[] doubles3 = new double[]{this.doubleValue * d2 + d4 * this.w, this.doubleValue * d3 + d5 * this.w};
         n = this.method3591(entry, doubles3, nextDouble);
         double[][] doubleses = this.doubleValues2[n];
         double[][] doubleValues = new double[this.w][2];

         for (int i = 0; i < 2; i++) {
            double d6 = 0.0;

            for (int j = 0; j < this.w; j++) {
               d6 += doubleses[j][i];
            }

            double d7 = 1.0;
            if (Math.abs(d6) > 1.0E-9 && Math.abs(doubles3[i]) > 1.0E-9 && Math.signum(d6) == Math.signum(doubles3[i])) {
               d7 = Math.min(Math.max(doubles3[i] / d6, this.doubleValue2), this.doubleValue3);
            }

            for (int j = 0; j < this.w; j++) {
               doubleValues[j][i] = doubleses[j][i] * d7;
            }
         }

         entry.doubleValues = doubleValues;
         entry.pos = 0;
      }

      double[] doubleValue = entry.doubleValues[entry.pos++];
      entry.doubleValues2[0] = doubleValue[0];
      entry.doubleValues2[1] = doubleValue[1];
      double[] doubles3 = new double[3];

      for (int i = 0; i < 2; i++) {
         double d6 = doubleValue[i] + entry.doubleValues3[i];
         doubles3[i] = Math.rint(d6 / d);
         entry.doubleValues3[i] = d6 - doubles3[i] * d;
      }

      doubles3[2] = n;
      return doubles3;
   }

   public static MotorHand method3593(InputStream inputStream) throws IOException {
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
      if (!"neurotest-motor-hand-v1".equals(string)) {
         throw new IOException("формат " + string + ", ожидался neurotest-motor-hand-v1");
      } else {
         MotorHand motorHand = new MotorHand();
         JsonObject json = jsonObject.getAsJsonObject("params");
         motorHand.w = json.get("w").getAsInt();
         motorHand.intValue = json.get("knn").getAsInt();
         motorHand.doubleValue = json.get("alpha").getAsDouble();
         motorHand.doubleValue2 = json.get("slo").getAsDouble();
         motorHand.doubleValue3 = json.get("shi").getAsDouble();
         motorHand.doubleValue4 = json.get("comp_c").getAsDouble();
         motorHand.doubleValues = method3594(json.getAsJsonArray("kw"));
         JsonArray jsonArray = jsonObject.getAsJsonArray("seqs");
         JsonArray jsonArray2 = jsonObject.getAsJsonArray("prev");
         int size = jsonArray.size();
         motorHand.doubleValues2 = new double[size][][];
         motorHand.doubleValues3 = new double[size][4];

         for (int i = 0; i < size; i++) {
            JsonArray jsonArray3 = jsonArray.get(i).getAsJsonArray();
            double[][] doubleses = new double[jsonArray3.size()][];
            double d = 0.0;
            double d2 = 0.0;

            for (int j = 0; j < doubleses.length; j++) {
               doubleses[j] = method3594(jsonArray3.get(j).getAsJsonArray());
               d += doubleses[j][0];
               d2 += doubleses[j][1];
            }

            if (doubleses.length != motorHand.w) {
               throw new IOException("отрезок " + i + " длиной " + doubleses.length + ", ждём " + motorHand.w);
            }

            motorHand.doubleValues2[i] = doubleses;
            double[] doubles = method3594(jsonArray2.get(i).getAsJsonArray());
            motorHand.doubleValues3[i][0] = method3589(d, motorHand.doubleValue4) * motorHand.doubleValues[0];
            motorHand.doubleValues3[i][1] = method3589(d2, motorHand.doubleValue4) * motorHand.doubleValues[1];
            motorHand.doubleValues3[i][2] = method3589(doubles[0], motorHand.doubleValue4) * motorHand.doubleValues[2];
            motorHand.doubleValues3[i][3] = method3589(doubles[1], motorHand.doubleValue4) * motorHand.doubleValues[3];
         }

         motorHand.method3595(jsonObject.getAsJsonObject("ref"));
         return motorHand;
      }
   }

   public static double[] method3594(JsonArray jsonArray) {
      double[] doubles = new double[jsonArray.size()];

      for (int i = 0; i < jsonArray.size(); i++) {
         doubles[i] = jsonArray.get(i).getAsDouble();
      }

      return doubles;
   }

   public void method3595(JsonObject jsonObject) throws IOException {
      double d = jsonObject.get("g").getAsDouble();
      MotorHand.Entry entry = this.method3588();
      int n = 0;
      int n2 = 0;
      int n3 = 0;

      for (JsonElement json : jsonObject.getAsJsonArray("ticks")) {
         JsonObject jsonObject2 = json.getAsJsonObject();
         double[] doubles = this.method3592(
            entry, method3594(jsonObject2.getAsJsonArray("target")), method3594(jsonObject2.getAsJsonArray("cam")), d, jsonObject2.get("u").getAsDouble()
         );
         double[] doubles2 = method3594(jsonObject2.getAsJsonArray("c"));
         int n4 = jsonObject2.get("idx").getAsInt();
         if (doubles[0] != doubles2[0] || doubles[1] != doubles2[1] || (int)doubles[2] != n4) {
            n++;
         }

         if (n4 >= 0) {
            n3++;
         }

         n2++;
      }

      this.text2 = String.format(
         "рука: отрезков %d по %d тиков; эталон совпал в %d из %d тиков (%d выборов)", this.doubleValues2.length, this.w, n2 - n, n2, n3
      );
      if (n > 0) {
         throw new IOException("ЭТАЛОН РАЗОШЁЛСЯ — " + this.text2);
      }
   }

   public static MotorHand method3596(Path path) throws IOException {
      MotorHand motorHand;
      try (InputStream inputStream = Files.newInputStream(path)) {
         motorHand = method3593(inputStream);
      }

      return motorHand;
   }

   public static void main(String[] strings) throws Exception {
      MotorHand motorHand = method3596(Path.of(strings[0]));
      System.out.println(motorHand.text2);
   }

   public final class Entry {
      public double[][] doubleValues;
      public int pos;
      public final double[] doubleValues2 = new double[2];
      public final double[] doubleValues3 = new double[2];
      public double[] doubleValues4;
   }
}
