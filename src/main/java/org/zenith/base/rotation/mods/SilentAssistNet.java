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

public final class SilentAssistNet {
   public static final String text = "neurotest-assist-silent-v1";
   public static final double CONST_doubleValue = 0.001;
   public static final double CONST_doubleValue2 = 0.002;
   public static final double CONST_doubleValue3 = 0.002;
   public static final double CONST_doubleValue4 = 1.62;
   public static final double CONST_doubleValue5 = 1.27;
   public static final double CONST_doubleValue6 = 0.6;
   public static final double CONST_doubleValue7 = 1.8;
   public static final double CONST_doubleValue8 = 1.5;
   public int intValue;
   public int intValue2;
   public int layers;
   public int intValue3;
   public int intValue4;
   public double doubleValue;
   public double doubleValue2;
   public double doubleValue3;
   public double doubleValue4;
   public boolean flag;
   public boolean flag2;
   public int[] intValues;
   public BcCameraNet bcCameraNet;
   public double[][] doubleValues;
   public double[][] doubleValues2;
   public double[] doubleValues3;
   public double[] doubleValues4;
   public double[][][] doubleValues5;
   public double[][][] doubleValues6;
   public double[][] doubleValues7;
   public double[][] doubleValues8;
   public String text2 = "";
   public String source = "?";

   public SilentAssistNet.Entry method3623() {
      return new SilentAssistNet.Entry();
   }

   public static double method3624(double d, double d2) {
      return Double.isNaN(d) ? d2 : (Double.isNaN(d2) ? d : Math.max(d, d2));
   }

   public static double method3625(double d, double d2) {
      return Double.isNaN(d) ? d2 : (Double.isNaN(d2) ? d : Math.min(d, d2));
   }

   public static double[] method3626(BcCameraNet.Entry4 entry4, double angdeg, double d) {
      boolean flag = entry4.intValue2 >= 0
         && BcCameraNet.method3361(entry4.doubleValue4, entry4.doubleValue5, entry4.doubleValue6)
         && BcCameraNet.method3361(entry4.doubleValue, entry4.doubleValue2, entry4.doubleValue3);
      if (!flag) {
         return new double[4];
      } else {
         boolean flag2 = (entry4.intValue >> 5 & 1) != 0;
         double d2 = entry4.doubleValue2 + (flag2 ? 1.27 : 1.62);
         double d3 = entry4.flag4 ? 1.5 : 1.8;
         double x = entry4.doubleValue4 - entry4.doubleValue;
         double y = entry4.doubleValue6 - entry4.doubleValue3;
         double d4 = Math.max(Math.hypot(x, y), 0.001);
         double d5 = Math.toDegrees(Math.atan2(-x, y));
         double d6 = Double.POSITIVE_INFINITY;
         double d7 = Double.NEGATIVE_INFINITY;

         for (byte i = -1; i <= 1; i += 2) {
            for (byte j = -1; j <= 1; j += 2) {
               double d8 = BcCameraNet.method3357(Math.toDegrees(Math.atan2(-(x + i * 0.6 / 2.0), y + j * 0.6 / 2.0)) - d5);
               d6 = Math.min(d6, d8);
               d7 = Math.max(d7, d8);
            }
         }

         boolean flag3 = Math.abs(x) < 0.3 && Math.abs(y) < 0.3;
         double d8 = d6 + d5;
         double d9 = d7 + d5;
         double d10 = x / d4;
         double d11 = y / d4;
         double d12 = 0.3 / Math.max(Math.max(Math.abs(d10), Math.abs(d11)), 1.0E-6);
         double d13 = d4 - d12;
         double d14 = d4 + d12;
         double d15 = Math.toRadians(angdeg);
         double d16 = -Math.sin(d15);
         double d17 = Math.cos(d15);
         double d18 = (x - 0.3) / d16;
         double d19 = (x + 0.3) / d16;
         double d20 = (y - 0.3) / d17;
         double d21 = (y + 0.3) / d17;
         double d22 = method3624(Math.min(d18, d19), Math.min(d20, d21));
         double d23 = method3625(Math.max(d18, d19), Math.max(d20, d21));
         boolean flag4 = Double.isFinite(d22) && Double.isFinite(d23) && d23 >= Math.max(d22, 0.0);
         double d24 = Math.max(flag4 ? d22 : d13, 0.001);
         double d25 = Math.max(flag4 ? d23 : d14, 0.001);
         double d26 = d2 - (entry4.doubleValue5 + d3);
         double d27 = d2 - entry4.doubleValue5;
         double d28 = Math.toDegrees(Math.atan(d26 / (d26 < 0.0 ? d24 : d25)));
         double d29 = Math.toDegrees(Math.atan(d27 / (d27 > 0.0 ? d24 : d25)));
         return d24 / Math.max(Math.cos(Math.toRadians(Math.abs(d))), 0.001) >= 6.0
            ? new double[]{1000.0, -1000.0, 1000.0, -1000.0}
            : new double[]{
               flag3 ? -1000.0 : BcCameraNet.method3357(d8 - angdeg), flag3 ? 1000.0 : BcCameraNet.method3357(d9 - angdeg), d28 - d, d29 - d
            };
      }
   }

   public static double method3627(double[] doubles, double d, double d2) {
      return Math.min(Math.min(d - doubles[0], doubles[1] - d), Math.min(d2 - doubles[2], doubles[3] - d2));
   }

   public static BcCameraNet.Entry4 method3628(BcCameraNet.Entry4 entry4) {
      BcCameraNet.Entry4 entry42 = new BcCameraNet.Entry4();
      entry42.doubleValue = entry4.doubleValue;
      entry42.doubleValue2 = entry4.doubleValue2;
      entry42.doubleValue3 = entry4.doubleValue3;
      entry42.intValue = entry4.intValue;
      entry42.flag = entry4.flag;
      entry42.flag2 = entry4.flag2;
      entry42.flag3 = entry4.flag3;
      entry42.doubleValue4 = entry4.doubleValue4;
      entry42.doubleValue5 = entry4.doubleValue5;
      entry42.doubleValue6 = entry4.doubleValue6;
      entry42.intValue2 = entry4.intValue2;
      entry42.intValue3 = entry4.intValue3;
      entry42.flag4 = entry4.flag4;
      entry42.doubleValue7 = entry4.doubleValue7;
      entry42.doubleValue8 = entry4.doubleValue8;
      entry42.doubleValue9 = entry4.doubleValue9;
      entry42.doubleValue10 = entry4.doubleValue10;
      entry42.flag5 = entry4.flag5;
      return entry42;
   }

   public void method3629(SilentAssistNet.Entry entry, BcCameraNet.Entry4 entry4) {
      entry.entry2.method3394(entry4);
      BcCameraNet.Entry4 entry42 = method3628(entry4);
      if (entry.flag) {
         entry42.doubleValue7 = entry.doubleValue;
         entry42.doubleValue8 = entry.doubleValue2;
         entry42.doubleValue9 = entry.doubleValue7;
         entry42.doubleValue10 = entry.doubleValue8;
         entry42.flag5 = entry.flag2;
      }

      entry.entry22.method3394(entry42);
   }

   public double[] method3630(
      SilentAssistNet.Entry entry, BcCameraNet.Entry4 entry4, double d, double d2, boolean flag, double d3, double yRot, double xRot, double[] doubles
   ) {
      double[] doubles2 = this.bcCameraNet.method3365(entry.entry2, d3);
      double[] doubles3 = this.bcCameraNet.method3365(entry.entry22, d3);
      double[] doubles4 = method3626(entry4, entry4.doubleValue7, entry4.doubleValue8);
      double sign = method3627(doubles4, 0.0, 0.0);
      double sign2 = method3627(doubles4, entry.doubleValue3, entry.doubleValue4);
      double d4 = doubles2[21];
      double[] doubleValue = new double[this.intValue2];
      System.arraycopy(doubles2, 0, doubleValue, 0, this.intValue3);
      int n = this.intValue3;

      for (int i = 0; i < this.intValues.length; i++) {
         doubleValue[n++] = doubles3[this.intValues[i]];
      }

      doubleValue[n - 1] = d4 * BcCameraNet.method3371(sign2 / this.doubleValue4);
      doubleValue[n++] = BcCameraNet.method3358(d / 10.0, -6.0, 6.0);
      doubleValue[n++] = BcCameraNet.method3358(d2 / 10.0, -6.0, 6.0);
      doubleValue[n++] = BcCameraNet.asinh(d / 0.5) / 4.0;
      doubleValue[n++] = BcCameraNet.asinh(d2 / 0.5) / 4.0;
      doubleValue[n++] = flag ? 1.0 : 0.0;
      doubleValue[n++] = BcCameraNet.asinh(entry.doubleValue3) / 2.0;
      doubleValue[n++] = BcCameraNet.asinh(entry.doubleValue4) / 2.0;
      doubleValue[n++] = entry.doubleValue3 / 8.0;
      doubleValue[n++] = entry.doubleValue4 / 8.0;
      doubleValue[n++] = d4 * BcCameraNet.asinh(sign) / 3.0;
      doubleValue[n++] = d4 * BcCameraNet.asinh(sign2) / 3.0;
      if (doubles != null) {
         System.arraycopy(doubleValue, 0, doubles, 0, this.intValue2);
      }

      double[] doubles5 = this.method3633(entry, doubleValue);
      double expm1;
      double expm12;
      if (this.flag) {
         expm1 = Math.expm1(this.doubleValue * Math.tanh(doubles5[0]));
         expm12 = Math.expm1(this.doubleValue * Math.tanh(doubles5[1]));
      } else {
         expm1 = this.doubleValue * Math.tanh(doubles5[0]);
         expm12 = this.doubleValue * Math.tanh(doubles5[1]);
      }

      double d5 = expm1 * d + this.doubleValue2 * Math.tanh(doubles5[2]);
      double d6 = expm12 * d2 + this.doubleValue2 * Math.tanh(doubles5[3]);
      double d7 = d5;
      double d8 = d6;
      entry.doubleValue9 = expm1;
      entry.doubleValue12 = sign2;
      entry.intValue++;
      if (entry.intValue <= this.intValue4) {
         entry.doubleValue10 = 0.0;
         entry.doubleValue11 = 0.0;
         return new double[]{yRot, xRot, d5, d6};
      } else {
         if (!entry.flag) {
            entry.flag = true;
            entry.doubleValue = entry4.doubleValue7;
            entry.doubleValue2 = entry4.doubleValue8;
            entry.doubleValue3 = 0.0;
            entry.doubleValue4 = 0.0;
            entry.doubleValue5 = 0.0;
            entry.doubleValue6 = 0.0;
         }

         if (!flag) {
            d5 = 0.0;
            d6 = 0.0;
         }

         d5 = BcCameraNet.method3358(entry.doubleValue3 + d5, -this.doubleValue3, this.doubleValue3) - entry.doubleValue3;
         d6 = BcCameraNet.method3358(entry.doubleValue4 + d6, -this.doubleValue3, this.doubleValue3) - entry.doubleValue4;
         double d9 = entry.doubleValue5 + d + d5;
         double d10 = entry.doubleValue6 + d2 + d6;
         entry.doubleValue13 = d9;
         entry.doubleValue14 = d10;
         double d11 = Math.rint(d9 / d3) * d3;
         double d12 = Math.rint(d10 / d3) * d3;
         if (this.flag2) {
            d11 = method3632(d11, d, d3);
            d12 = method3632(d12, d2, d3);
         }

         double d13;
         double d14;
         if (flag) {
            entry.doubleValue5 = d9 - d11;
            entry.doubleValue6 = d10 - d12;
            d13 = d11;
            d14 = d12;
         } else {
            d13 = BcCameraNet.method3357(yRot - entry4.doubleValue7);
            d14 = xRot - entry4.doubleValue8;
         }

         double doubleValue2 = entry.doubleValue + d13;
         double d15 = BcCameraNet.method3358(entry.doubleValue2 + d14, -90.0, 90.0);
         entry.doubleValue7 = doubleValue2 - entry.doubleValue;
         entry.doubleValue8 = d15 - entry.doubleValue2;
         entry.flag2 = flag;
         entry.doubleValue = doubleValue2;
         entry.doubleValue2 = d15;
         entry.doubleValue3 = BcCameraNet.method3357(doubleValue2 - yRot);
         entry.doubleValue4 = d15 - xRot;
         entry.doubleValue10 = d5;
         entry.doubleValue11 = d6;
         return new double[]{doubleValue2, d15, d7, d8};
      }
   }

   public static boolean method3631(double d, double d2, double d3) {
      if (Math.abs(d) <= 1.0E-6) {
         return true;
      } else {
         double d4 = d2 / d3 - Math.floor(d2 / d3);
         return Math.abs(Math.abs(d) - d3) <= 1.0E-6 && Math.abs(d4 - 0.5) < 0.001;
      }
   }

   public static double method3632(double d, double d2, double d3) {
      if (d2 == 0.0) {
         return 0.0;
      } else {
         return d != 0.0 && Math.signum(d) == Math.signum(d2) ? d : Math.signum(d2) * d3;
      }
   }

   public double[] method3633(SilentAssistNet.Entry entry, double[] doubleValue) {
      double[] doubles = BcCameraNet.method3372(this.doubleValues, this.doubleValues3, doubleValue);

      for (int i = 0; i < doubles.length; i++) {
         doubles[i] = BcCameraNet.method3369(doubles[i]);
      }

      for (int i = 0; i < this.layers; i++) {
         double[] doubleValue2 = entry.doubleValues[i];
         double[] doubles2 = BcCameraNet.method3372(this.doubleValues5[i], this.doubleValues7[i], doubles);
         double[] doubles3 = BcCameraNet.method3372(this.doubleValues6[i], this.doubleValues8[i], doubleValue2);
         double[] doubles4 = new double[this.intValue];

         for (int j = 0; j < this.intValue; j++) {
            double d = BcCameraNet.method3371(doubles2[j] + doubles3[j]);
            double d2 = BcCameraNet.method3371(doubles2[this.intValue + j] + doubles3[this.intValue + j]);
            double tanh = Math.tanh(doubles2[2 * this.intValue + j] + d * doubles3[2 * this.intValue + j]);
            doubles4[j] = (1.0 - d2) * tanh + d2 * doubleValue2[j];
         }

         entry.doubleValues[i] = doubles4;
         doubles = doubles4;
      }

      return BcCameraNet.method3372(this.doubleValues2, this.doubleValues4, doubles);
   }

   public static SilentAssistNet method3634(InputStream inputStream) throws IOException {
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
      if (!"neurotest-assist-silent-v1".equals(jsonObject.get("format").getAsString())) {
         throw new IOException("формат " + jsonObject.get("format").getAsString() + ", ожидался neurotest-assist-silent-v1");
      } else {
         SilentAssistNet silentAssistNet = new SilentAssistNet();
         silentAssistNet.intValue = jsonObject.get("h").getAsInt();
         silentAssistNet.intValue2 = jsonObject.get("nin").getAsInt();
         silentAssistNet.layers = jsonObject.get("layers").getAsInt();
         silentAssistNet.intValue3 = jsonObject.get("nf").getAsInt();
         silentAssistNet.intValue4 = jsonObject.get("warm").getAsInt();
         silentAssistNet.doubleValue = jsonObject.get("alpha").getAsDouble();
         silentAssistNet.doubleValue2 = jsonObject.get("beta").getAsDouble();
         silentAssistNet.doubleValue3 = jsonObject.get("omax").getAsDouble();
         silentAssistNet.doubleValue4 = jsonObject.get("tau_in").getAsDouble();
         silentAssistNet.flag = "log".equals(jsonObject.get("mode").getAsString());
         silentAssistNet.flag2 = jsonObject.get("keep").getAsBoolean();
         double[] doubles = BcCameraNet.method3388(jsonObject.getAsJsonArray("sent_idx"));
         silentAssistNet.intValues = new int[doubles.length];

         for (int i = 0; i < doubles.length; i++) {
            silentAssistNet.intValues[i] = (int)doubles[i];
         }

         if (silentAssistNet.intValue2 != silentAssistNet.intValue3 + silentAssistNet.intValues.length + 11) {
            throw new IOException("вход сети " + silentAssistNet.intValue2 + " не совпадает с раскладкой");
         } else {
            silentAssistNet.bcCameraNet = BcCameraNet.method3356(silentAssistNet.intValue3, jsonObject.get("clip").getAsInt(), jsonObject.get("window").getAsInt());
            JsonObject json = jsonObject.getAsJsonObject("weights");
            silentAssistNet.doubleValues = BcCameraNet.method3386(json, "inp.weight");
            silentAssistNet.doubleValues3 = BcCameraNet.method3385(json, "inp.bias");
            silentAssistNet.doubleValues5 = new double[silentAssistNet.layers][][];
            silentAssistNet.doubleValues6 = new double[silentAssistNet.layers][][];
            silentAssistNet.doubleValues7 = new double[silentAssistNet.layers][];
            silentAssistNet.doubleValues8 = new double[silentAssistNet.layers][];

            for (int i = 0; i < silentAssistNet.layers; i++) {
               silentAssistNet.doubleValues5[i] = BcCameraNet.method3386(json, "rnn.weight_ih_l" + i);
               silentAssistNet.doubleValues6[i] = BcCameraNet.method3386(json, "rnn.weight_hh_l" + i);
               silentAssistNet.doubleValues7[i] = BcCameraNet.method3385(json, "rnn.bias_ih_l" + i);
               silentAssistNet.doubleValues8[i] = BcCameraNet.method3385(json, "rnn.bias_hh_l" + i);
            }

            silentAssistNet.doubleValues2 = BcCameraNet.method3386(json, "head.weight");
            silentAssistNet.doubleValues4 = BcCameraNet.method3385(json, "head.bias");
            silentAssistNet.source = jsonObject.get("source").getAsString().replace(',', ';');
            silentAssistNet.text2 = silentAssistNet.method3635(jsonObject.getAsJsonArray("ref")) + "; модель " + jsonObject.get("source").getAsString();
            return silentAssistNet;
         }
      }
   }

   public String method3635(JsonArray jsonArray) throws IOException {
      double d = 0.0;
      double d2 = 0.0;
      double d3 = 0.0;
      double d4 = 0.0;
      int n = 0;
      int n2 = -1;
      int n3 = 0;
      String str = "";

      for (JsonElement json : jsonArray) {
         n2++;
         JsonObject jsonObject = json.getAsJsonObject();
         double d5 = jsonObject.get("grid").getAsDouble();
         double[] doubles = BcCameraNet.method3388(jsonObject.getAsJsonArray("yaw"));
         double[] doubles2 = BcCameraNet.method3388(jsonObject.getAsJsonArray("pitch"));
         double[] doubles3 = BcCameraNet.method3388(jsonObject.getAsJsonArray("my"));
         double[] doubles4 = BcCameraNet.method3388(jsonObject.getAsJsonArray("mp"));
         double[] doubles5 = BcCameraNet.method3388(jsonObject.getAsJsonArray("ok"));
         double[] doubles6 = BcCameraNet.method3388(jsonObject.getAsJsonArray("s_yaw"));
         double[] doubles7 = BcCameraNet.method3388(jsonObject.getAsJsonArray("s_pitch"));
         JsonObject jsonObject2 = jsonObject.getAsJsonObject("rec");
         JsonArray jsonArray2 = jsonObject2.getAsJsonArray("pos");
         JsonArray jsonArray3 = jsonObject2.getAsJsonArray("tgt");
         double[] doubles8 = BcCameraNet.method3388(jsonObject2.getAsJsonArray("keys"));
         double[] doubles9 = BcCameraNet.method3388(jsonObject2.getAsJsonArray("attack"));
         double[] doubles10 = BcCameraNet.method3388(jsonObject2.getAsJsonArray("tgt_id"));
         double[] doubles11 = BcCameraNet.method3388(jsonObject2.getAsJsonArray("n_cand"));
         double[] doubles12 = BcCameraNet.method3388(jsonObject2.getAsJsonArray("tgt_sneak"));
         double[] doubles13 = BcCameraNet.method3388(jsonObject2.getAsJsonArray("on_ground"));
         double[] doubles14 = BcCameraNet.method3388(jsonObject2.getAsJsonArray("hcoll"));
         JsonArray jsonArray4 = jsonObject.getAsJsonArray("x");
         JsonArray jsonArray5 = jsonObject.getAsJsonArray("d");
         int n4 = jsonObject.get("start").getAsInt();
         SilentAssistNet.Entry entry = this.method3623();
         double[] doubles15 = new double[this.intValue2];

         for (int i = 1; i < doubles.length; i++) {
            int n5 = i - 1;
            BcCameraNet.Entry4 entry4 = new BcCameraNet.Entry4();
            JsonArray jsonArray6 = jsonArray2.get(n5).getAsJsonArray();
            JsonArray jsonArray7 = jsonArray3.get(n5).getAsJsonArray();
            entry4.doubleValue = BcCameraNet.method3389(jsonArray6.get(0).getAsDouble());
            entry4.doubleValue2 = BcCameraNet.method3389(jsonArray6.get(1).getAsDouble());
            entry4.doubleValue3 = BcCameraNet.method3389(jsonArray6.get(2).getAsDouble());
            entry4.doubleValue4 = BcCameraNet.method3389(jsonArray7.get(0).getAsDouble());
            entry4.doubleValue5 = BcCameraNet.method3389(jsonArray7.get(1).getAsDouble());
            entry4.doubleValue6 = BcCameraNet.method3389(jsonArray7.get(2).getAsDouble());
            entry4.intValue = (int)doubles8[n5];
            entry4.flag3 = doubles9[n5] > 0.0;
            entry4.intValue2 = (int)doubles10[n5];
            entry4.intValue3 = (int)doubles11[n5];
            entry4.flag4 = doubles12[n5] > 0.0;
            entry4.flag = doubles13[n5] > 0.0;
            entry4.flag2 = doubles14[n5] > 0.0;
            entry4.doubleValue7 = doubles[n5];
            entry4.doubleValue8 = doubles2[n5];
            entry4.doubleValue9 = doubles3[n5];
            entry4.doubleValue10 = doubles4[n5];
            entry4.flag5 = doubles5[n5] > 0.0;
            this.method3629(entry, entry4);
            if (i >= n4) {
               double[] doubles16 = this.method3630(entry, entry4, doubles3[i], doubles4[i], doubles5[i] > 0.0, d5, doubles[i], doubles2[i], doubles15);
               double[] doubles17 = BcCameraNet.method3388(jsonArray4.get(i - n4).getAsJsonArray());

               for (int j = 0; j < this.intValue2; j++) {
                  d = Math.max(d, Math.abs(doubles15[j] - doubles17[j]));
               }

               double[] doubles18 = BcCameraNet.method3388(jsonArray5.get(i - n4).getAsJsonArray());
               d2 = Math.max(d2, Math.max(Math.abs(doubles16[2] - doubles18[0]), Math.abs(doubles16[3] - doubles18[1])));
               double d6 = BcCameraNet.method3357(doubles16[0] - doubles6[i]);
               double d7 = doubles16[1] - doubles7[i];
               double d8 = Math.max(Math.abs(d6), Math.abs(d7));
               if (d8 > 1.0E-6 && method3631(d6, entry.doubleValue13, d5) && method3631(d7, entry.doubleValue14, d5)) {
                  n3++;
                  entry.doubleValue5 += d6;
                  entry.doubleValue6 += d7;
                  entry.doubleValue -= d6;
                  entry.doubleValue2 -= d7;
                  entry.doubleValue7 -= d6;
                  entry.doubleValue8 -= d7;
                  entry.doubleValue3 = BcCameraNet.method3357(entry.doubleValue - doubles[i]);
                  entry.doubleValue4 = entry.doubleValue2 - doubles2[i];
                  d8 = 0.0;
               }

               if (d8 > 1.0E-6 && str.isEmpty()) {
                  str = String.format(
                     "; первое расхождение камеры: кусок %d, тик %d (ход человека %.4f %.4f, решётка %.4f, выход %.5f %.5f / эталон %.5f %.5f)",
                     n2,
                     i,
                     doubles3[i],
                     doubles4[i],
                     d5,
                     doubles16[2],
                     doubles16[3],
                     doubles18[0],
                     doubles18[1]
                  );
               }

               d3 = Math.max(d3, d8);
               d4 = Math.max(d4, Math.max(Math.abs(entry.doubleValue3), Math.abs(entry.doubleValue4)));
               n++;
            }
         }
      }

      String str2 = String.format(
            "эталон: %d тиков, вход макс. расхождение %.2e, выход %.2e, отправленная камера %.2e°, перескоков у границы округления %d, |o| макс %.2f°",
            n,
            d,
            d2,
            d3,
            n3,
            d4
         )
         + str;
      if (d <= 0.001 && d2 <= 0.002 && d3 <= 0.002 && n3 <= Math.max(3, n / 100)) {
         return str2;
      } else {
         throw new IOException("ЭТАЛОН НЕ СОШЁЛСЯ — " + str2);
      }
   }

   public static void main(String[] strings) throws Exception {
      try (InputStream inputStream = Files.newInputStream(Path.of(strings[0]))) {
         SilentAssistNet silentAssistNet = method3634(inputStream);
         System.out.println("OK: " + silentAssistNet.text2);
         SilentAssistNet.Entry entry = silentAssistNet.method3623();
         BcCameraNet.Entry4 entry4 = new BcCameraNet.Entry4();
         long nanoTime = System.nanoTime();

         for (int i = 0; i < 2000; i++) {
            silentAssistNet.method3629(entry, entry4);
            silentAssistNet.method3630(entry, entry4, 0.3, 0.0, true, 0.15, 0.0, 0.0, null);
         }

         System.out.printf("шаг: %.3f мс%n", (System.nanoTime() - nanoTime) / 2000L / 1000000.0);
      }
   }

   public final class Entry {
      public final BcCameraNet.Entry2 entry2 = SilentAssistNet.this.bcCameraNet.method3355();
      public final BcCameraNet.Entry2 entry22 = SilentAssistNet.this.bcCameraNet.method3355();
      public double[][] doubleValues;
      public int intValue;
      public boolean flag;
      public double doubleValue;
      public double doubleValue2;
      public double doubleValue3;
      public double doubleValue4;
      public double doubleValue5;
      public double doubleValue6;
      public double doubleValue7;
      public double doubleValue8;
      public boolean flag2;
      public double doubleValue9;
      public double doubleValue10;
      public double doubleValue11;
      public double doubleValue12;
      public double doubleValue13;
      public double doubleValue14;

      public Entry() {
         this.doubleValues = new double[SilentAssistNet.this.layers][SilentAssistNet.this.intValue];
         this.doubleValue12 = Double.NaN;
      }

      public double getDoubleValue3() {
         return this.doubleValue3;
      }

      public double getDoubleValue4() {
         return this.doubleValue4;
      }

      public boolean isFlag() {
         return this.flag;
      }
   }
}
