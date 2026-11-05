package org.zenith.base.rotation.mods;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.zenith.Rotation;
import org.zenith.client.modules.impl.combat.AuraModule;
import org.zenith.utility.misc.Util22;

public final class Agent extends Util22 {
   private static final String text = "agent.json";
   private static final String text2 = "models/agent.json";
   private static final List<Path> items = List.of(Path.of("agent.json"), Path.of("models", "agent.json"), Path.of("..", "models", "agent.json"));
   private static final int CONST_intValue = Integer.MIN_VALUE;
   private static final String text3 = "neurotest-agent-v1";
   private static final double CONST_doubleValue = 0.15190947;
   private static final double CONST_doubleValue2 = 1.0E-4;
   private static Agent.Entry entry;
   private static boolean flag;
   private final Random random = new Random();
   private int intValue = Integer.MIN_VALUE;
   private int intValue2 = Integer.MIN_VALUE;
   private int intValue3 = Integer.MIN_VALUE;
   private Rotation rotation;
   private double[] doubleValues;
   private double doubleValue;
   private double doubleValue2;
   private double doubleValue3;
   private double doubleValue4;

   private static synchronized Agent.Entry method3452() {
      if (flag) {
         return entry;
      } else {
         flag = true;

         try (InputStreamReader inputStreamReader = new InputStreamReader(method3453(), StandardCharsets.UTF_8)) {
            JsonReader jsonReader = new JsonReader(inputStreamReader);

            try {
               entry = method3454(jsonReader);
            } catch (Throwable e) {
               try {
                  jsonReader.close();
               } catch (Throwable ex) {
                  e.addSuppressed(ex);
               }

               throw e;
            }

            jsonReader.close();
         } catch (Exception e) {
            System.out.println("агент не загрузился: " + e);
            entry = null;
         }

         return entry;
      }
   }

   private static InputStream method3453() throws IOException {
      for (Path path : items) {
         if (Files.isRegularFile(path)) {
            return Files.newInputStream(path);
         }
      }

      InputStream resourceAsStream = Agent.class.getClassLoader().getResourceAsStream("models/agent.json");
      if (resourceAsStream == null) {
         throw new IOException("agent.json не найден ни рядом с клиентом, ни в jar");
      } else {
         return resourceAsStream;
      }
   }

   private static Agent.Entry method3454(JsonReader jsonReader) throws IOException {
      Agent.Entry entry = new Agent.Entry();
      HashMap map = new HashMap();
      HashMap map2 = new HashMap();
      ArrayList arrayList = new ArrayList();
      ArrayList arrayList2 = new ArrayList();
      String nextString = null;
      jsonReader.beginObject();

      while (jsonReader.hasNext()) {
         String nextName = jsonReader.nextName();
         switch (nextName) {
            case "format":
               nextString = jsonReader.nextString();
               break;
            case "n_obs":
               entry.intValue = jsonReader.nextInt();
               break;
            case "hidden":
               entry.intValue2 = jsonReader.nextInt();
               break;
            case "logsig_min":
               entry.doubleValue = jsonReader.nextDouble();
               break;
            case "logsig_max":
               entry.doubleValue2 = jsonReader.nextDouble();
               break;
            case "weights":
               jsonReader.beginObject();

               while (jsonReader.hasNext()) {
                  String key = jsonReader.nextName();
                  ArrayList arrayList3 = new ArrayList();
                  int[] ints = getInstance(jsonReader, arrayList3);
                  double[] doubles = new double[arrayList3.size()];

                  for (int i = 0; i < doubles.length; i++) {
                     doubles[i] = (Double)arrayList3.get(i);
                  }

                  map.put(key, doubles);
                  map2.put(key, ints);
               }

               jsonReader.endObject();
               break;
            case "ref_inputs":
               method3455(jsonReader, arrayList);
               break;
            case "ref_outputs":
               method3455(jsonReader, arrayList2);
               break;
            default:
               jsonReader.skipValue();
         }
      }

      jsonReader.endObject();
      if (!"neurotest-agent-v1".equals(nextString)) {
         throw new IOException("формат " + nextString + ", ожидался neurotest-agent-v1");
      } else {
         entry.doubleValues = method3457((double[])map.get("gru.weight_ih"), 3 * entry.intValue2, entry.intValue);
         entry.doubleValues2 = method3457((double[])map.get("gru.weight_hh"), 3 * entry.intValue2, entry.intValue2);
         entry.doubleValues3 = (double[])map.get("gru.bias_ih");
         entry.doubleValues4 = (double[])map.get("gru.bias_hh");
         entry.doubleValues5 = method3457((double[])map.get("head.weight"), 4, entry.intValue2);
         entry.doubleValues6 = (double[])map.get("head.bias");
         entry.doubleValues7 = method3457((double[])map.get("gate.weight"), 1, entry.intValue2)[0];
         entry.doubleValue3 = ((double[])map.get("gate.bias"))[0];
         double[] doubleValues = new double[entry.intValue2];
         double d = 0.0;

         for (int i = 0; i < arrayList.size(); i++) {
            doubleValues = entry.method3465((double[])arrayList.get(i), doubleValues);
            double[] doubles = entry.method3466(doubleValues);
            double[] doubles2 = (double[])arrayList2.get(i);

            for (int j = 0; j < doubles2.length; j++) {
               d = Math.max(d, Math.abs(doubles[j] - doubles2[j]));
            }
         }

         if (d > 1.0E-4) {
            throw new IOException("эталонные тики разошлись на " + d + " — раскладка весов не та");
         } else {
            System.out.println("агент загружен: скрытых " + entry.intValue2 + ", эталон сошёлся (" + d + ")");
            return entry;
         }
      }
   }

   private static void method3455(JsonReader jsonReader, List<double[]> doubleses) throws IOException {
      jsonReader.beginArray();

      while (jsonReader.hasNext()) {
         ArrayList arrayList = new ArrayList();
         jsonReader.beginArray();

         while (jsonReader.hasNext()) {
            arrayList.add(jsonReader.nextDouble());
         }

         jsonReader.endArray();
         double[] doubles = new double[arrayList.size()];

         for (int i = 0; i < doubles.length; i++) {
            doubles[i] = (Double)arrayList.get(i);
         }

         doubleses.add(doubles);
      }

      jsonReader.endArray();
   }

   private static int[] getInstance(JsonReader jsonReader, List<Double> doubles) throws IOException {
      ArrayList arrayList = new ArrayList();
      method3456(jsonReader, doubles, arrayList, 0);
      int[] ints = new int[arrayList.size()];

      for (int i = 0; i < ints.length; i++) {
         ints[i] = (Integer)arrayList.get(i);
      }

      return ints;
   }

   private static void method3456(JsonReader jsonReader, List<Double> doubles, List<Integer> integers, int n) throws IOException {
      jsonReader.beginArray();

      int n2;
      for (n2 = 0; jsonReader.hasNext(); n2++) {
         if (jsonReader.peek() == JsonToken.BEGIN_ARRAY) {
            method3456(jsonReader, doubles, integers, n + 1);
         } else {
            doubles.add(jsonReader.nextDouble());
         }
      }

      jsonReader.endArray();

      while (integers.size() <= n) {
         integers.add(0);
      }

      integers.set(n, n2);
   }

   private static double[][] method3457(double[] doubles, int n, int intValue) throws IOException {
      if (doubles != null && doubles.length == n * intValue) {
         double[][] doubleses = new double[n][intValue];

         for (int i = 0; i < n; i++) {
            System.arraycopy(doubles, i * intValue, doubleses[i], 0, intValue);
         }

         return doubleses;
      } else {
         throw new IOException("не тот размер весов: ждали " + n + "x" + intValue + ", пришло " + (doubles == null ? 0 : doubles.length));
      }
   }

   private static double method3458(double d) {
      double d2 = (d + 180.0) % 360.0;
      if (d2 < 0.0) {
         d2 += 360.0;
      }

      return d2 - 180.0;
   }

   private void method3459(int intValue) {
      this.doubleValues = null;
      this.doubleValue = 0.0;
      this.doubleValue2 = 0.0;
      this.doubleValue3 = 0.0;
      this.doubleValue4 = 0.0;
      this.intValue = intValue;
      this.intValue2 = Integer.MIN_VALUE;
      this.intValue3 = Integer.MIN_VALUE;
      this.rotation = null;
   }

   private void method3460(int tickCount, Rotation rotation) {
      this.intValue3 = tickCount;
      this.rotation = rotation;
   }

   public void reset() {
      this.method3459(Integer.MIN_VALUE);
   }

   public boolean method3461() {
      return method3452() != null;
   }

   public Rotation method3462() {
      Rotation rotation = rotationManager.method7844();
      Agent.Entry entry = method3452();
      if (entry == null) {
         this.reset();
         return mc.player == null ? rotation : new Rotation(mc.player.getYRot(), mc.player.getXRot(), true);
      } else {
         LivingEntity livingEntity = AuraModule.INSTANCE.method6358();
         if (mc.player != null && livingEntity != null && livingEntity.isAlive()) {
            int tickCount = mc.player.tickCount;
            if (tickCount == this.intValue3 && this.rotation != null) {
               return this.rotation;
            } else {
               if (this.intValue2 != Integer.MIN_VALUE && tickCount != this.intValue2 + 1) {
                  this.method3459(livingEntity.getId());
               } else if (livingEntity.getId() != this.intValue) {
                  this.method3459(livingEntity.getId());
               }

               this.intValue2 = tickCount;
               this.intValue = livingEntity.getId();
               if (this.doubleValues == null || this.doubleValues.length != entry.intValue2) {
                  this.doubleValues = new double[entry.intValue2];
               }

               AABB boundingBox = livingEntity.getBoundingBox();
               Vec3 eyePosition = mc.player.getEyePosition();
               Vec3 center = boundingBox.getCenter();
               double d = center.x - eyePosition.x;
               double d2 = center.y - eyePosition.y;
               double x = center.z - eyePosition.z;
               double x2 = Math.sqrt(d * d + x * x);
               double d3 = Math.toDegrees(Math.atan2(-d, x));
               double d4 = Math.toDegrees(Math.atan2(-d2, x2));
               double x3 = 0.001;
               double x4 = 0.001;

               for (int i = 0; i < 8; i++) {
                  double d5 = ((i & 1) == 0 ? boundingBox.minX : boundingBox.maxX) - eyePosition.x;
                  double d6 = ((i & 2) == 0 ? boundingBox.minY : boundingBox.maxY) - eyePosition.y;
                  double x5 = ((i & 4) == 0 ? boundingBox.minZ : boundingBox.maxZ) - eyePosition.z;
                  double x6 = Math.sqrt(d5 * d5 + x5 * x5);
                  x3 = Math.max(x3, Math.abs(method3458(Math.toDegrees(Math.atan2(-d5, x5)) - d3)));
                  x4 = Math.max(x4, Math.abs(Math.toDegrees(Math.atan2(-d6, x6)) - d4));
               }

               double x5 = method3458(d3 - rotation.getFloatValue());
               double y = d4 - rotation.getFloatValue2();
               double x6 = Math.max(Math.hypot(x5, y), 1.0E-6);
               Input keyPresses = mc.player.input.keyPresses;
               double[] doubles = new double[entry.intValue];
               doubles[0] = method3463(x5 / x3, -6.0, 6.0);
               doubles[1] = method3463(y / x4, -6.0, 6.0);
               doubles[2] = Math.log1p(x6) / 5.0;
               doubles[3] = x5 / x6;
               doubles[4] = y / x6;
               doubles[5] = Math.log1p(x3) / 3.0;
               doubles[6] = Math.log1p(x4) / 3.0;
               doubles[7] = keyPresses.forward() ? 1.0 : 0.0;
               doubles[8] = keyPresses.backward() ? 1.0 : 0.0;
               doubles[9] = keyPresses.left() ? 1.0 : 0.0;
               doubles[10] = keyPresses.right() ? 1.0 : 0.0;
               doubles[11] = keyPresses.jump() ? 1.0 : 0.0;
               doubles[12] = mc.player.onGround() ? 1.0 : 0.0;
               doubles[13] = mc.player.isSprinting() ? 1.0 : 0.0;
               doubles[14] = mc.player.getAttackStrengthScale(0.0F);
               doubles[15] = method3463(this.doubleValue3 / 10.0, -6.0, 6.0);
               doubles[16] = method3463(this.doubleValue4 / 10.0, -6.0, 6.0);
               doubles[17] = Math.min(Math.hypot(this.doubleValue3, this.doubleValue4), 60.0) / 10.0;
               this.doubleValues = entry.method3465(doubles, this.doubleValues);
               double[] doubles2 = entry.method3466(this.doubleValues);
               double d5 = 1.0 / (1.0 + Math.exp(-doubles2[4]));
               double d6 = 0.0;
               double d7 = 0.0;
               if (this.random.nextDouble() >= d5) {
                  d6 = doubles2[0] + Math.exp(doubles2[2]) * this.random.nextGaussian();
                  d7 = doubles2[1] + Math.exp(doubles2[3]) * this.random.nextGaussian();
               }

               double d8 = d6 / 0.15190947 + this.doubleValue;
               double d9 = d7 / 0.15190947 + this.doubleValue2;
               long l = (long)d8;
               long l2 = (long)d9;
               this.doubleValue = d8 - l;
               this.doubleValue2 = d9 - l2;
               this.doubleValue3 = l * 0.15190947;
               this.doubleValue4 = l2 * 0.15190947;
               Rotation rotation2 = rotation.method3140((float)this.doubleValue3, (float)this.doubleValue4);
               this.method3460(tickCount, rotation2);
               return rotation2;
            }
         } else {
            this.reset();
            if (mc.player != null) {
               mc.player.setYRot(rotation.getFloatValue());
               mc.player.setXRot(rotation.getFloatValue2());
            }

            return rotation;
         }
      }
   }

   private static double method3463(double d, double d2, double d3) {
      return Math.max(d2, Math.min(d3, d));
   }

   public static String method3464() {
      Agent.Entry entry = method3452();
      return entry == null ? "МОДЕЛЬ НЕ ЗАГРУЖЕНА" : "загружено: признаков " + entry.intValue + ", скрытых " + entry.intValue2 + ", эталонные тики сошлись";
   }

   private static final class Entry {
      public int intValue;
      public int intValue2;
      public double doubleValue;
      public double doubleValue2;
      public double[][] doubleValues;
      public double[][] doubleValues2;
      public double[] doubleValues3;
      public double[] doubleValues4;
      public double[][] doubleValues5;
      public double[] doubleValues6;
      public double[] doubleValues7;
      public double doubleValue3;

      public double[] method3465(double[] doubles, double[] doubleValues) {
         double[] doubles2 = new double[3 * this.intValue2];
         double[] doubles3 = new double[3 * this.intValue2];

         for (int i = 0; i < 3 * this.intValue2; i++) {
            double d = this.doubleValues3[i];

            for (int j = 0; j < this.intValue; j++) {
               d += this.doubleValues[i][j] * doubles[j];
            }

            doubles2[i] = d;
            double d2 = this.doubleValues4[i];

            for (int j = 0; j < this.intValue2; j++) {
               d2 += this.doubleValues2[i][j] * doubleValues[j];
            }

            doubles3[i] = d2;
         }

         double[] doubles4 = new double[this.intValue2];

         for (int i = 0; i < this.intValue2; i++) {
            double d = 1.0 / (1.0 + Math.exp(-(doubles2[i] + doubles3[i])));
            double d2 = 1.0 / (1.0 + Math.exp(-(doubles2[this.intValue2 + i] + doubles3[this.intValue2 + i])));
            double tanh = Math.tanh(doubles2[2 * this.intValue2 + i] + d * doubles3[2 * this.intValue2 + i]);
            doubles4[i] = (1.0 - d2) * tanh + d2 * doubleValues[i];
         }

         return doubles4;
      }

      public double[] method3466(double[] doubleValues) {
         double[] doubles = new double[5];

         for (int i = 0; i < 4; i++) {
            double d = this.doubleValues6[i];

            for (int j = 0; j < this.intValue2; j++) {
               d += this.doubleValues5[i][j] * doubleValues[j];
            }

            doubles[i] = i < 2 ? d : Math.max(this.doubleValue, Math.min(this.doubleValue2, d));
         }

         double d = this.doubleValue3;

         for (int i = 0; i < this.intValue2; i++) {
            d += this.doubleValues7[i] * doubleValues[i];
         }

         doubles[4] = d;
         return doubles;
      }
   }
}
