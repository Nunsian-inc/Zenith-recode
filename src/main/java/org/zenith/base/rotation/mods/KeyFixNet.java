package org.zenith.base.rotation.mods;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

public final class KeyFixNet {
   public static final String text = "neurotest-keyfix-v1";
   public static final double CONST_doubleValue = 0.001;
   public double[][][] doubleValues;
   public double[][] doubleValues2;
   public double doubleValue;
   public double doubleValue2;
   public int intValue;
   public String source = "?";
   public String text2 = "";

   public static double method3555(double d) {
      double d2 = Math.signum(d);
      double d3 = Math.abs(d);
      double d4 = 1.0 / (1.0 + 0.3275911 * d3);
      double d5 = 1.0
         - ((((1.061405429 * d4 - 1.453152027) * d4 + 1.421413741) * d4 - 0.284496736) * d4 + 0.254829592) * d4 * Math.exp(-d3 * d3);
      return d2 * d5;
   }

   public static double method3556(double d) {
      return 0.5 * d * (1.0 + method3555(d / Math.sqrt(2.0)));
   }

   public double[] method3557(double[] doubleValues, int n) {
      double[] doubles = new double[this.intValue + 8];
      System.arraycopy(doubleValues, 0, doubles, 0, this.intValue);

      for (int i = 0; i < 7; i++) {
         doubles[this.intValue + i] = n >> i & 1;
      }

      doubles[this.intValue + 7] = this.doubleValue;

      for (int i = 0; i < this.doubleValues.length; i++) {
         double[][] doubleValue = this.doubleValues[i];
         double[] doubles2 = new double[doubleValue.length];

         for (int j = 0; j < doubleValue.length; j++) {
            double d = this.doubleValues2[i][j];
            double[] doubles3 = doubleValue[j];

            for (int k = 0; k < doubles3.length; k++) {
               d += doubles3[k] * doubles[k];
            }

            doubles2[j] = i < this.doubleValues.length - 1 ? method3556(d) : d;
         }

         doubles = doubles2;
      }

      return doubles;
   }

   public static int method3558(int n, int n2) {
      int n3 = n & 1;
      int n4 = n >> 1 & 1;
      int n5 = n >> 2;
      int n6 = n5 % 3 - 1;
      int n7 = n5 / 3 - 1;
      return (n7 > 0 ? 1 : 0) | (n7 < 0 ? 2 : 0) | (n6 > 0 ? 4 : 0) | (n6 < 0 ? 8 : 0) | n4 << 4 | n2 & 32 | n3 << 6;
   }

   public boolean method3559(double[] doubleValues) {
      return doubleValues[21] > 0.5 && Math.exp(doubleValues[29] * 2.0) < this.doubleValue2;
   }

   public int method3560(double[] doubleValues, int n, Random random) {
      double[] doubles = this.method3557(doubleValues, n);
      double d = Double.NEGATIVE_INFINITY;

      for (double d2 : doubles) {
         d = Math.max(d, d2);
      }

      double d2 = 0.0;
      double[] doubles2 = new double[doubles.length];

      for (int i = 0; i < doubles.length; i++) {
         doubles2[i] = Math.exp(doubles[i] - d);
         d2 += doubles2[i];
      }

      double d3 = random.nextDouble() * d2;
      double d4 = 0.0;

      for (int i = 0; i < doubles2.length; i++) {
         d4 += doubles2[i];
         if (d3 < d4) {
            return method3558(i, n);
         }
      }

      return method3558(doubles2.length - 1, n);
   }

   public static double[] method3561(JsonArray jsonArray) {
      double[] doubles = new double[jsonArray.size()];

      for (int i = 0; i < doubles.length; i++) {
         doubles[i] = jsonArray.get(i).getAsDouble();
      }

      return doubles;
   }

   public static KeyFixNet method3562(InputStream inputStream) throws IOException {
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
      if (!"neurotest-keyfix-v1".equals(jsonObject.get("format").getAsString())) {
         throw new IOException("формат " + jsonObject.get("format").getAsString() + ", ждали neurotest-keyfix-v1");
      } else {
         KeyFixNet keyFixNet = new KeyFixNet();
         keyFixNet.doubleValue = jsonObject.get("q").getAsDouble();
         keyFixNet.doubleValue2 = jsonObject.get("max_dist").getAsDouble();
         keyFixNet.intValue = jsonObject.get("nf").getAsInt();
         keyFixNet.source = jsonObject.get("source").getAsString();
         JsonArray jsonArray = jsonObject.getAsJsonArray("layers");
         keyFixNet.doubleValues = new double[jsonArray.size()][][];
         keyFixNet.doubleValues2 = new double[jsonArray.size()][];

         for (int i = 0; i < jsonArray.size(); i++) {
            JsonObject json = jsonArray.get(i).getAsJsonObject();
            JsonArray jsonArray2 = json.getAsJsonArray("w");
            keyFixNet.doubleValues[i] = new double[jsonArray2.size()][];

            for (int j = 0; j < jsonArray2.size(); j++) {
               keyFixNet.doubleValues[i][j] = method3561(jsonArray2.get(j).getAsJsonArray());
            }

            keyFixNet.doubleValues2[i] = method3561(json.getAsJsonArray("b"));
         }

         JsonObject json = jsonObject.getAsJsonObject("ref");
         JsonArray jsonArray2 = json.getAsJsonArray("x");
         JsonArray jsonArray3 = json.getAsJsonArray("u");
         JsonArray jsonArray4 = json.getAsJsonArray("logits");
         double d = 0.0;

         for (int i = 0; i < jsonArray2.size(); i++) {
            double[] doubles = keyFixNet.method3557(method3561(jsonArray2.get(i).getAsJsonArray()), jsonArray3.get(i).getAsInt());
            double[] doubles2 = method3561(jsonArray4.get(i).getAsJsonArray());

            for (int j = 0; j < doubles.length; j++) {
               d = Math.max(d, Math.abs(doubles[j] - doubles2[j]));
            }
         }

         if (!(d < 0.001)) {
            throw new IOException("эталон поправщика не сошёлся: max |Δлогит| " + d);
         } else {
            keyFixNet.text2 = String.format(
               "поправщик клавиш %s: эталон %d строк, max |Δлогит| %.1e, q %.2f, ближе %.1f блока",
               keyFixNet.source,
               jsonArray2.size(),
               d,
               keyFixNet.doubleValue,
               keyFixNet.doubleValue2
            );
            return keyFixNet;
         }
      }
   }

   public static void main(String[] strings) throws IOException {
      try (InputStream inputStream = Files.newInputStream(Path.of(strings[0]))) {
         System.out.println(method3562(inputStream).text2);
      }
   }
}
