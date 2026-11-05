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

public final class HandAnchorNet {
   public static final String text = "neurotest-hand-anchor-v1";
   public static final double CONST_doubleValue = 0.002;
   public int intValue;
   public int intValue2;
   public int intValue3;
   public int intValue4;
   public int intValue5;
   public int intValue6;
   public int intValue7;
   public int intValue8;
   public double[] doubleValues;
   public double doubleValue;
   public boolean flag;
   public double doubleValue2;
   public double doubleValue3;
   public double doubleValue4;
   public double doubleValue5;
   public String source = "?";
   public String text2 = "";
   public double[][] doubleValues2;
   public double[][] doubleValues3;
   public double[][] doubleValues4;
   public double[][] doubleValues5;
   public double[][] doubleValues6;
   public double[][] doubleValues7;
   public double[][] doubleValues8;
   public double[][] doubleValues9;
   public double[][] doubleValues10;
   public double[][] doubleValues11;
   public double[][] doubleValues12;
   public double[][] doubleValues13;
   public double[][] doubleValues14;
   public double[][] doubleValues15;
   public double[] doubleValues16;
   public double[] doubleValues17;
   public double[] doubleValues18;
   public double[] doubleValues19;
   public double[] doubleValues20;
   public double[] doubleValues21;
   public double[] doubleValues22;
   public double[] doubleValues23;
   public double[] doubleValues24;
   public double[] doubleValues25;
   public double[] doubleValues26;
   public boolean flag2;
   public double doubleValue6;
   public double doubleValue7;
   public double doubleValue8;
   public double doubleValue9;
   public double doubleValue10;
   public double doubleValue11;
   public double doubleValue12;
   public double doubleValue13;
   public boolean flag3;
   public double doubleValue14;
   public double doubleValue15;

   public HandAnchorNet.Entry method3398() {
      return new HandAnchorNet.Entry();
   }

   public static double method3399(double d) {
      return BcCameraNet.asinh(d / 0.5) / 4.0;
   }

   public double[] method3400(double[] doubles, int n, double[] doubleValues2, double[] doubleValues4, double[] doubles2, double d, double doubleValue) {
      double[] doubles3 = new double[this.intValue4];
      doubles3[0] = method3399(doubles[0]);
      doubles3[1] = method3399(doubles[1]);
      doubles3[2] = BcCameraNet.method3358(doubles[0] / 30.0, -6.0, 6.0);
      doubles3[3] = BcCameraNet.method3358(doubles[1] / 30.0, -6.0, 6.0);
      doubles3[4 + Math.max(0, Math.min(this.intValue - 1, n))] = 1.0;
      int n2 = 4 + this.intValue;
      doubles3[n2] = method3399(doubleValues2[0]);
      doubles3[n2 + 1] = method3399(doubleValues2[1]);

      for (int i = 0; i < this.intValue3; i++) {
         doubles3[n2 + 2 + i] = doubleValues4[i] * doubleValue;
      }

      n2 += 2 + this.intValue3;
      doubles3[n2] = method3399(doubles2[0]);
      doubles3[n2 + 1] = method3399(doubles2[1]);
      doubles3[n2 + 2] = Math.log(d / 0.15);
      doubles3[n2 + 3] = doubleValue;
      if (!(doubleValue > 0.0)) {
         for (int i = 0; i < 4 + this.intValue + 2; i++) {
            doubles3[i] = 0.0;
         }
      }

      return doubles3;
   }

   public double[] method3401(HandAnchorNet.Entry entry, double[] doubleValue) {
      double[] doubles = BcCameraNet.method3372(this.doubleValues2, this.doubleValues16, doubleValue);

      for (int i = 0; i < doubles.length; i++) {
         doubles[i] = BcCameraNet.method3369(doubles[i]);
      }

      double[] doubleValues = entry.doubleValues;
      double[] doubles2 = BcCameraNet.method3372(this.doubleValues3, this.doubleValues17, doubles);
      double[] doubles3 = BcCameraNet.method3372(this.doubleValues4, this.doubleValues18, doubleValues);
      double[] doubleValues2 = new double[this.intValue5];

      for (int i = 0; i < this.intValue5; i++) {
         double d = BcCameraNet.method3371(doubles2[i] + doubles3[i]);
         double d2 = BcCameraNet.method3371(doubles2[this.intValue5 + i] + doubles3[this.intValue5 + i]);
         double tanh = Math.tanh(doubles2[2 * this.intValue5 + i] + d * doubles3[2 * this.intValue5 + i]);
         doubleValues2[i] = (1.0 - d2) * tanh + d2 * doubleValues[i];
      }

      entry.doubleValues = doubleValues2;
      return doubleValues2;
   }

   public static double[] method3402(double[][] doubleValues5, double[] doubleValues19, double[][] doubleValues6, double[] doubleValues20, double[] doubleValue) {
      double[] doubles = BcCameraNet.method3372(doubleValues5, doubleValues19, doubleValue);

      for (int i = 0; i < doubles.length; i++) {
         doubles[i] = BcCameraNet.method3369(doubles[i]);
      }

      return BcCameraNet.method3372(doubleValues6, doubleValues20, doubles);
   }

   public static double[] method3403(double[]... doubleses) {
      int n = 0;

      for (double[] doubles : doubleses) {
         n += doubles.length;
      }

      double[] doubles = new double[n];
      int n2 = 0;

      for (double[] doubles2 : doubleses) {
         System.arraycopy(doubles2, 0, doubles, n2, doubles2.length);
         n2 += doubles2.length;
      }

      return doubles;
   }

   public double[] method3404(double[] doubleValue) {
      return method3402(this.doubleValues5, this.doubleValues19, this.doubleValues6, this.doubleValues20, doubleValue);
   }

   public double[] method3405(double[] doubleValue, int n) {
      return method3402(this.doubleValues8, this.doubleValues21, this.doubleValues9, this.doubleValues22, method3403(doubleValue, this.doubleValues7[n]));
   }

   public double[] method3406(double[] doubleValue, int n, int n2) {
      return method3402(
         this.doubleValues11,
         this.doubleValues23,
         this.doubleValues12,
         this.doubleValues24,
         method3403(doubleValue, this.doubleValues7[n], this.doubleValues10[n2])
      );
   }

   public double[] method3407(double[] doubleValue, int n, int n2, int n3) {
      return method3402(
         this.doubleValues14,
         this.doubleValues25,
         this.doubleValues15,
         this.doubleValues26,
         method3403(doubleValue, this.doubleValues7[n], this.doubleValues10[n2], this.doubleValues13[n3])
      );
   }

   public static int method3408(double[] doubles, Random random) {
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
            return i;
         }
      }

      return doubles2.length - 1;
   }

   public double method3409(int n, int n2, double d, Random random) {
      if (n == 0) {
         return 0.0;
      } else {
         boolean flag = n <= this.intValue6;
         int n3 = Math.max(0, Math.min(this.intValue6 - 1, flag ? n - 1 : n - 1 - this.intValue6));
         double log = Math.log(this.doubleValues[n3]);
         double log2 = Math.log(this.doubleValues[n3 + 1]);
         double d2 = log + (log2 - log) * n2 / this.intValue7;
         double d3 = log + (log2 - log) * (n2 + 1) / this.intValue7;
         double exp = Math.exp(d2 + (d3 - d2) * random.nextDouble());
         double d4 = Math.ceil(Math.exp(d2) / d);
         double d5 = Math.ceil(Math.exp(d3) / d) - 1.0;
         double nextDouble = random.nextDouble();
         double d6 = d5 >= d4 ? (d4 + Math.floor(nextDouble * (d5 - d4 + 1.0))) * d : exp;
         return flag ? d6 : -d6;
      }
   }

   public double[] method3410(double[] doubles, double[] doubles2, double[] doubles3, double[] doubles4, boolean flag, boolean flag2, boolean flag3, double floatValue, double d) {
      double[] doubles5 = new double[3];
      double[] doubles6 = new double[3];

      for (int i = 0; i < 3; i++) {
         double d2 = doubles2 == null ? 0.0 : doubles[i] - doubles2[i];
         double d3 = flag && doubles4 != null ? doubles3[i] - doubles4[i] : 0.0;
         doubles5[i] = Double.isFinite(d2) ? d2 : 0.0;
         doubles6[i] = Double.isFinite(d3) ? d3 : 0.0;
      }

      if (!this.flag) {
         doubles5[1] = 0.0;
         doubles6[1] = 0.0;
      }

      double d2 = doubles[0] + doubles5[0] * this.doubleValue;
      double d3 = doubles[1] + doubles5[1] * this.doubleValue + (flag3 ? this.doubleValue3 : this.doubleValue2);
      double d4 = doubles[2] + doubles5[2] * this.doubleValue;
      double d5 = flag2 ? this.doubleValue5 : this.doubleValue4;
      double d6 = doubles3[0] + doubles6[0] * this.doubleValue;
      double d7 = doubles3[1] + doubles6[1] * this.doubleValue + d5 / 2.0;
      double d8 = doubles3[2] + doubles6[2] * this.doubleValue;
      double[] doubles7 = BcCameraNet.method3362(d6 - d2, d7 - d3, d8 - d4);
      return new double[]{BcCameraNet.method3357(doubles7[0] - floatValue), BcCameraNet.method3358(doubles7[1], -90.0, 90.0) - d};
   }

   public void method3411(HandAnchorNet.Entry entry, double[] doubles) {
      entry.doubleValues5[0] = doubles[0];
      entry.doubleValues5[1] = doubles[1];
      entry.doubleValue4 = entry.doubleValue4 + (Math.abs(doubles[0]) - entry.doubleValue4) / this.doubleValue13;
   }

   public double[] method3412(HandAnchorNet.Entry entry, double d, double d2) {
      d = BcCameraNet.method3358(d, entry.doubleValues5[0] - this.doubleValue6, entry.doubleValues5[0] + this.doubleValue6);
      d2 = BcCameraNet.method3358(d2, entry.doubleValues5[1] - this.doubleValue7, entry.doubleValues5[1] + this.doubleValue7);
      double d3 = BcCameraNet.method3358((entry.doubleValue4 - this.doubleValue10) / (this.doubleValue11 - this.doubleValue10), 0.0, 1.0);
      double d4 = this.doubleValue8 + (this.doubleValue12 - this.doubleValue8) * d3;
      return new double[]{BcCameraNet.method3358(d, -d4, d4), BcCameraNet.method3358(d2, -this.doubleValue9, this.doubleValue9)};
   }

   public double[] method3413(HandAnchorNet.Entry entry, double d, double d2, boolean flag) {
      double d3 = entry.doubleValues6[0] + entry.doubleValues7[0] * this.intValue;
      double d4 = entry.doubleValues6[1] + entry.doubleValues7[1] * this.intValue;
      double d5 = BcCameraNet.method3357(d - d3);
      double d6 = d2 - d4;
      boolean flag2 = !entry.flag;
      double d7 = flag2 ? d : d3 + this.doubleValue14 * d5;
      double d8 = flag2 ? d2 : d4 + this.doubleValue14 * d6;
      double d9 = flag2 ? 0.0 : entry.doubleValues7[0] + this.doubleValue15 * d5 / this.intValue;
      double d10 = flag2 ? 0.0 : entry.doubleValues7[1] + this.doubleValue15 * d6 / this.intValue;
      entry.doubleValues6 = new double[]{d7, d8};
      entry.doubleValues7 = new double[]{d9, d10};
      entry.flag = flag;
      return new double[]{d7, d8};
   }

   public double[] method3414(HandAnchorNet.Entry entry, double[] doubles, double[] doubles2, double d, double d2, double d3, Random random) {
      if (entry.ticks > 0) {
         entry.doubleValues3[0] = entry.doubleValues3[0] + doubles[0];
         entry.doubleValues3[1] = entry.doubleValues3[1] + doubles[1];
      }

      if (this.flag2) {
         this.method3411(entry, doubles);
      }

      if (entry.ticks % this.intValue == 0) {
         if (this.flag3) {
            if (doubles2 != null) {
               double[] doubles3 = this.method3413(entry, d + doubles2[0], d2 + doubles2[1], true);
               doubles2 = new double[]{BcCameraNet.method3357(doubles3[0] - d), doubles3[1] - d2};
            } else {
               entry.flag = false;
            }
         }

         entry.doubleValue = doubles2 != null ? 1.0 : 0.0;
         entry.doubleValues2 = doubles2 != null ? new double[]{doubles2[0], doubles2[1]} : new double[2];

         for (int i = 0; i < this.intValue3; i++) {
            entry.doubleValues4[i] = doubles2 != null ? random.nextGaussian() : 0.0;
         }

         entry.doubleValues3[0] = 0.0;
         entry.doubleValues3[1] = 0.0;
         entry.j = 0;
      }

      double[] doubles3 = new double[]{entry.doubleValues2[0] - entry.doubleValues3[0], entry.doubleValues2[1] - entry.doubleValues3[1]};
      double[] doubleValue = this.method3401(entry, this.method3400(doubles3, entry.j, entry.doubleValues2, entry.doubleValues4, doubles, d3, entry.doubleValue));
      int n = method3408(this.method3404(doubleValue), random);
      int n2 = method3408(this.method3405(doubleValue, n), random);
      int n3 = method3408(this.method3406(doubleValue, n, n2), random);
      int n4 = method3408(this.method3407(doubleValue, n, n2, n3), random);
      entry.j++;
      entry.ticks++;
      double[] doubles4 = new double[]{this.method3409(n, n2, d3, random), this.method3409(n3, n4, d3, random)};
      return this.flag2 ? this.method3412(entry, doubles4[0], doubles4[1]) : doubles4;
   }

   public static double[] method3415(HandAnchorNet.Entry entry, double d, double d2, double d3, double d4) {
      entry.doubleValue2 += d;
      entry.doubleValue3 += d2;
      double d5 = Math.rint(entry.doubleValue2 / d3) * d3;
      double d6 = Math.rint(entry.doubleValue3 / d3) * d3;
      entry.doubleValue2 -= d5;
      entry.doubleValue3 -= d6;
      double d7 = Math.max(-90.0, Math.min(90.0, d4 + d6));
      return new double[]{d5, d7 - d4};
   }

   public static double[][] method3416(JsonArray jsonArray) {
      double[][] doubleses = new double[jsonArray.size()][];

      for (int i = 0; i < doubleses.length; i++) {
         doubleses[i] = BcCameraNet.method3388(jsonArray.get(i).getAsJsonArray());
      }

      return doubleses;
   }

   public static HandAnchorNet method3417(InputStream inputStream) throws IOException {
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
      if (!"neurotest-hand-anchor-v1".equals(jsonObject.get("format").getAsString())) {
         throw new IOException("формат " + jsonObject.get("format").getAsString() + ", ожидался neurotest-hand-anchor-v1");
      } else {
         HandAnchorNet handAnchorNet = new HandAnchorNet();
         handAnchorNet.intValue = jsonObject.get("K").getAsInt();
         handAnchorNet.intValue2 = jsonObject.get("H").getAsInt();
         handAnchorNet.intValue3 = jsonObject.get("DZ").getAsInt();
         handAnchorNet.intValue4 = jsonObject.get("nin").getAsInt();
         handAnchorNet.intValue5 = jsonObject.get("h").getAsInt();
         handAnchorNet.intValue6 = jsonObject.get("nb").getAsInt();
         handAnchorNet.intValue7 = jsonObject.get("nsub").getAsInt();
         handAnchorNet.intValue8 = jsonObject.get("ncls").getAsInt();
         handAnchorNet.doubleValues = BcCameraNet.method3388(jsonObject.getAsJsonArray("edges"));
         handAnchorNet.doubleValue = jsonObject.get("ex").getAsDouble();
         handAnchorNet.flag = jsonObject.get("vert").getAsBoolean();
         handAnchorNet.doubleValue2 = jsonObject.get("eye").getAsDouble();
         handAnchorNet.doubleValue3 = jsonObject.get("eye_sneak").getAsDouble();
         handAnchorNet.doubleValue4 = jsonObject.get("tgt_h").getAsDouble();
         handAnchorNet.doubleValue5 = jsonObject.get("tgt_h_sneak").getAsDouble();
         if (4 + handAnchorNet.intValue + 2 + handAnchorNet.intValue3 + 4 != handAnchorNet.intValue4) {
            throw new IOException("вход руки " + handAnchorNet.intValue4 + " не сходится с K " + handAnchorNet.intValue + ", DZ " + handAnchorNet.intValue3);
         } else {
            JsonObject json = jsonObject.getAsJsonObject("weights");
            handAnchorNet.doubleValues2 = BcCameraNet.method3386(json, "inp.0.weight");
            handAnchorNet.doubleValues16 = BcCameraNet.method3385(json, "inp.0.bias");
            handAnchorNet.doubleValues3 = BcCameraNet.method3386(json, "gru.weight_ih_l0");
            handAnchorNet.doubleValues4 = BcCameraNet.method3386(json, "gru.weight_hh_l0");
            handAnchorNet.doubleValues17 = BcCameraNet.method3385(json, "gru.bias_ih_l0");
            handAnchorNet.doubleValues18 = BcCameraNet.method3385(json, "gru.bias_hh_l0");
            handAnchorNet.doubleValues5 = BcCameraNet.method3386(json, "yaw.0.weight");
            handAnchorNet.doubleValues19 = BcCameraNet.method3385(json, "yaw.0.bias");
            handAnchorNet.doubleValues6 = BcCameraNet.method3386(json, "yaw.2.weight");
            handAnchorNet.doubleValues20 = BcCameraNet.method3385(json, "yaw.2.bias");
            handAnchorNet.doubleValues7 = BcCameraNet.method3386(json, "yemb.weight");
            handAnchorNet.doubleValues8 = BcCameraNet.method3386(json, "ysub.0.weight");
            handAnchorNet.doubleValues21 = BcCameraNet.method3385(json, "ysub.0.bias");
            handAnchorNet.doubleValues9 = BcCameraNet.method3386(json, "ysub.2.weight");
            handAnchorNet.doubleValues22 = BcCameraNet.method3385(json, "ysub.2.bias");
            handAnchorNet.doubleValues10 = BcCameraNet.method3386(json, "ysemb.weight");
            handAnchorNet.doubleValues11 = BcCameraNet.method3386(json, "pitch.0.weight");
            handAnchorNet.doubleValues23 = BcCameraNet.method3385(json, "pitch.0.bias");
            handAnchorNet.doubleValues12 = BcCameraNet.method3386(json, "pitch.2.weight");
            handAnchorNet.doubleValues24 = BcCameraNet.method3385(json, "pitch.2.bias");
            handAnchorNet.doubleValues13 = BcCameraNet.method3386(json, "pcemb.weight");
            handAnchorNet.doubleValues14 = BcCameraNet.method3386(json, "psub.0.weight");
            handAnchorNet.doubleValues25 = BcCameraNet.method3385(json, "psub.0.bias");
            handAnchorNet.doubleValues15 = BcCameraNet.method3386(json, "psub.2.weight");
            handAnchorNet.doubleValues26 = BcCameraNet.method3385(json, "psub.2.bias");
            String str = "";
            if (jsonObject.has("governor") && jsonObject.get("governor").isJsonObject()) {
               JsonObject jsonObject2 = jsonObject.getAsJsonObject("governor");
               handAnchorNet.flag2 = true;
               handAnchorNet.doubleValue6 = jsonObject2.get("ay").getAsDouble();
               handAnchorNet.doubleValue7 = jsonObject2.get("ap").getAsDouble();
               handAnchorNet.doubleValue8 = jsonObject2.get("vy").getAsDouble();
               handAnchorNet.doubleValue9 = jsonObject2.get("vp").getAsDouble();
               handAnchorNet.doubleValue10 = jsonObject2.get("e0").getAsDouble();
               handAnchorNet.doubleValue11 = jsonObject2.get("e1").getAsDouble();
               handAnchorNet.doubleValue12 = jsonObject2.get("vmin").getAsDouble();
               handAnchorNet.doubleValue13 = jsonObject2.get("tau").getAsDouble();
               JsonObject jsonObject3 = jsonObject2.getAsJsonObject("ref");
               double[][] doubleses = method3416(jsonObject3.getAsJsonArray("real"));
               double[][] doubleses2 = method3416(jsonObject3.getAsJsonArray("want"));
               double[][] doubleses3 = method3416(jsonObject3.getAsJsonArray("out"));
               HandAnchorNet.Entry entry = handAnchorNet.method3398();
               double d = 0.0;

               for (int i = 0; i < doubleses.length; i++) {
                  handAnchorNet.method3411(entry, doubleses[i]);
                  double[] doubles = handAnchorNet.method3412(entry, doubleses2[i][0], doubleses2[i][1]);
                  d = Math.max(d, Math.max(Math.abs(doubles[0] - doubleses3[i][0]), Math.abs(doubles[1] - doubleses3[i][1])));
               }

               if (!(d < 1.0E-9)) {
                  throw new IOException("эталон мышц не сошёлся: " + d);
               }

               str = str
                  + String.format(
                     "; мышцы: ускорение %.0f/%.0f°, скорость %.0f/%.0f°, усталость %.0f->%.0f °/тик до %.0f° (эталон %.1e)",
                     handAnchorNet.doubleValue6,
                     handAnchorNet.doubleValue7,
                     handAnchorNet.doubleValue8,
                     handAnchorNet.doubleValue9,
                     handAnchorNet.doubleValue10,
                     handAnchorNet.doubleValue11,
                     handAnchorNet.doubleValue12,
                     d
                  );
            }

            if (jsonObject.has("tracker") && jsonObject.get("tracker").isJsonObject()) {
               JsonObject jsonObject2 = jsonObject.getAsJsonObject("tracker");
               handAnchorNet.flag3 = true;
               handAnchorNet.doubleValue14 = jsonObject2.get("alpha").getAsDouble();
               handAnchorNet.doubleValue15 = handAnchorNet.doubleValue14 * handAnchorNet.doubleValue14 / (2.0 - handAnchorNet.doubleValue14);
               JsonObject jsonObject3 = jsonObject2.getAsJsonObject("ref");
               double[] doubles = BcCameraNet.method3388(jsonObject3.getAsJsonArray("wy"));
               double[] doubles2 = BcCameraNet.method3388(jsonObject3.getAsJsonArray("wp"));
               double[] doubles3 = BcCameraNet.method3388(jsonObject3.getAsJsonArray("has"));
               double[][] doubleses = method3416(jsonObject3.getAsJsonArray("out"));
               HandAnchorNet.Entry entry = handAnchorNet.method3398();
               double d = 0.0;

               for (int i = 0; i < doubles.length; i++) {
                  double[] doubles4 = handAnchorNet.method3413(entry, doubles[i], doubles2[i], doubles3[i] > 0.0);
                  d = Math.max(d, Math.max(Math.abs(doubles4[0] - doubleses[i][0]), Math.abs(doubles4[1] - doubleses[i][1])));
               }

               if (!(d < 1.0E-6)) {
                  throw new IOException("эталон трекера не сошёлся: " + d);
               }

               str = str + String.format("; трекер намерения alpha %.2f (эталон %.1e)", handAnchorNet.doubleValue14, d);
            }

            handAnchorNet.source = jsonObject.get("source").getAsString().replace(',', ';');
            handAnchorNet.text2 = handAnchorNet.method3418(jsonObject.getAsJsonObject("ref_anchor"), jsonObject.getAsJsonObject("ref_net")) + str;
            return handAnchorNet;
         }
      }
   }

   public String method3418(JsonObject jsonObject, JsonObject json) throws IOException {
      double[][] doubleses = method3416(jsonObject.getAsJsonArray("p1"));
      double[][] doubleses2 = method3416(jsonObject.getAsJsonArray("p2"));
      double[][] doubleses3 = method3416(jsonObject.getAsJsonArray("g1"));
      double[][] doubleses4 = method3416(jsonObject.getAsJsonArray("g2"));
      double[][] doubleses5 = method3416(jsonObject.getAsJsonArray("out"));
      double[] doubles = BcCameraNet.method3388(jsonObject.getAsJsonArray("same"));
      double[] doubles2 = BcCameraNet.method3388(jsonObject.getAsJsonArray("tsneak"));
      double[] doubles3 = BcCameraNet.method3388(jsonObject.getAsJsonArray("sneak"));
      double[] doubles4 = BcCameraNet.method3388(jsonObject.getAsJsonArray("cy"));
      double[] doubles5 = BcCameraNet.method3388(jsonObject.getAsJsonArray("cp"));
      double d = 0.0;

      for (int i = 0; i < doubleses.length; i++) {
         double[] doubles6 = this.method3410(
            doubleses[i], doubleses2[i], doubleses3[i], doubleses4[i], doubles[i] > 0.0, doubles2[i] > 0.0, doubles3[i] > 0.0, doubles4[i], doubles5[i]
         );
         d = Math.max(d, Math.max(Math.abs(doubles6[0] - doubleses5[i][0]), Math.abs(doubles6[1] - doubleses5[i][1])));
      }

      double[][] doubleses6 = method3416(json.getAsJsonArray("r"));
      double[][] doubleses7 = method3416(json.getAsJsonArray("D"));
      double[][] doubleses8 = method3416(json.getAsJsonArray("z"));
      double[][] doubleses9 = method3416(json.getAsJsonArray("prev"));
      double[][] doubleses10 = method3416(json.getAsJsonArray("f"));
      double[][] doubleses11 = method3416(json.getAsJsonArray("ly"));
      double[][] doubleses12 = method3416(json.getAsJsonArray("lys"));
      double[][] doubleses13 = method3416(json.getAsJsonArray("lp"));
      double[][] doubleses14 = method3416(json.getAsJsonArray("lps"));
      double[] doubles6 = BcCameraNet.method3388(json.getAsJsonArray("j"));
      double[] doubles7 = BcCameraNet.method3388(json.getAsJsonArray("grid"));
      double[] doubles8 = BcCameraNet.method3388(json.getAsJsonArray("has"));
      double[] doubles9 = BcCameraNet.method3388(json.getAsJsonArray("yc"));
      double[] doubles10 = BcCameraNet.method3388(json.getAsJsonArray("ys"));
      double[] doubles11 = BcCameraNet.method3388(json.getAsJsonArray("pc"));
      HandAnchorNet.Entry entry = this.method3398();
      double d2 = 0.0;
      double d3 = 0.0;

      for (int i = 0; i < doubleses6.length; i++) {
         double[] doubleValue = this.method3400(doubleses6[i], (int)doubles6[i], doubleses7[i], doubleses8[i], doubleses9[i], doubles7[i], doubles8[i]);

         for (int j = 0; j < this.intValue4; j++) {
            d2 = Math.max(d2, Math.abs(doubleValue[j] - doubleses10[i][j]));
         }

         double[] doubleValue2 = this.method3401(entry, doubleValue);
         int n = (int)doubles9[i];
         int n2 = (int)doubles10[i];
         int n3 = (int)doubles11[i];
         double[][] doubleses15 = new double[][]{
            this.method3404(doubleValue2), this.method3405(doubleValue2, n), this.method3406(doubleValue2, n, n2), this.method3407(doubleValue2, n, n2, n3)
         };
         double[][] doubleses16 = new double[][]{doubleses11[i], doubleses12[i], doubleses13[i], doubleses14[i]};

         for (int j = 0; j < 4; j++) {
            for (int k = 0; k < doubleses15[j].length; k++) {
               d3 = Math.max(d3, Math.abs(doubleses15[j][k] - doubleses16[j][k]));
            }
         }
      }

      String str = String.format(
         "эталон: якорь %d (%.1e), признаки %.1e, логиты %d тиков %.1e; K %d H %d, продление %.0f тиков%s",
         doubleses.length,
         d,
         d2,
         doubleses6.length,
         d3,
         this.intValue,
         this.intValue2,
         this.doubleValue,
         this.flag ? " с вертикалью" : ""
      );
      if (d < 1.0E-6 && d2 < 1.0E-5 && d3 <= 0.002) {
         return str;
      } else {
         throw new IOException("ЭТАЛОН РУКИ НЕ СОШЁЛСЯ — " + str);
      }
   }

   public static void main(String[] strings) throws Exception {
      try (InputStream inputStream = Files.newInputStream(Path.of(strings[0]))) {
         HandAnchorNet handAnchorNet = method3417(inputStream);
         System.out.println("OK: " + handAnchorNet.text2 + "; модель " + handAnchorNet.source);
         HandAnchorNet.Entry entry = handAnchorNet.method3398();
         Random random = new Random(0L);
         long nanoTime = System.nanoTime();

         for (int i = 0; i < 2000; i++) {
            handAnchorNet.method3414(entry, new double[]{0.3, -0.1}, new double[]{12.0, 3.0}, 0.0, 0.0, 0.15, random);
         }

         System.out.printf("шаг руки: %.3f мс%n", (System.nanoTime() - nanoTime) / 2000L / 1000000.0);

         for (int i = 0; i < 3; i++) {
            entry = handAnchorNet.method3398();
            double d = 0.0;
            double d2 = 0.0;
            double[] doubles = new double[]{0.0, 0.0};
            StringBuilder x = new StringBuilder("контур:");

            for (int j = 0; j < 24; j++) {
               double[] doubles2 = handAnchorNet.method3414(entry, doubles, new double[]{30.0 - d, 10.0 - d2}, d, d2, 0.15, random);
               doubles = method3415(entry, doubles2[0], doubles2[1], 0.15, d2);
               d += doubles[0];
               d2 += doubles[1];
               x.append(String.format(" %.1f/%.1f", d, d2));
            }

            System.out.println(x);
         }
      }
   }

   public final class Entry {
      public double[] doubleValues = new double[HandAnchorNet.this.intValue5];
      public double[] doubleValues2 = new double[2];
      public double[] doubleValues3 = new double[2];
      public double[] doubleValues4 = new double[HandAnchorNet.this.intValue3];
      public double doubleValue;
      public int j;
      public int ticks;
      public double doubleValue2;
      public double doubleValue3;
      public double doubleValue4;
      public double[] doubleValues5 = new double[2];
      public double[] doubleValues6 = new double[2];
      public double[] doubleValues7 = new double[2];
      public boolean flag;

      public void method3419() {
         this.doubleValue2 = 0.0;
         this.doubleValue3 = 0.0;
      }
   }
}
