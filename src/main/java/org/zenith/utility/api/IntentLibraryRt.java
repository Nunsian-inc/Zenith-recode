package org.zenith.utility.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Random;

public final class IntentLibraryRt {
   public static final int CONST_intValue = 0;
   public static final int CONST_intValue2 = 1;
   public static final int CONST_intValue3 = 2;
   public static final int CONST_intValue4 = 3;
   public static final int CONST_intValue5 = 4;
   public static final String[] texts = new String[]{"flick", "correction", "tracking", "settle"};
   public static final int CONST_intValue6 = 8;
   private static final float[] CONST_floatValues = new float[]{1.0F, 1.0F, 0.5F, 0.5F, 0.0F, 0.0F, 0.6F, 0.6F};
   private static final String text = "/assets/zenith/aimpipe/intent_library_rt.json";
   private static final Path path = Path.of("intent_library_rt.json");
   private static final Path[] paths = new Path[]{
      Path.of("..", "scripts", "motor_intents", "out", "intent_library_rt.json"), Path.of("scripts", "motor_intents", "out", "intent_library_rt.json")
   };
   private final IntentLibraryRt.Entry3[] entry3s;
   private final IntentLibraryRt.Entry[] entrys = new IntentLibraryRt.Entry[4];

   public IntentLibraryRt.Entry3 method1378(int n) {
      return this.entry3s[n];
   }

   public int size() {
      return this.entry3s.length;
   }

   public IntentLibraryRt.Entry3 method1379(
      int n,
      double[] doubles,
      IntentLibraryRt.Entry2 entry2,
      Random random,
      int n2,
      double d,
      int n3,
      double d2,
      double d3,
      double d4,
      double d5,
      double d6,
      double d7
   ) {
      IntentLibraryRt.Entry entry = this.entrys[n];
      int length = entry.intValues.length;
      double[] doubles2 = new double[length];

      for (int i = 0; i < length; i++) {
         double d8 = 0.0;
         float[] floatValue = entry.floatValues[i];

         for (int j = 0; j < 8; j++) {
            if (CONST_floatValues[j] != 0.0F) {
               double d9 = (doubles[j] - entry.floatValues2[j]) / entry.std[j];
               double d10 = (floatValue[j] - d9) * CONST_floatValues[j];
               d8 += d10 * d10;
            }
         }

         doubles2[i] = Math.sqrt(d8);
         if (entry.intValues[i] == n3 && length > 1) {
            doubles2[i] = Double.POSITIVE_INFINITY;
         }
      }

      int n4 = Math.min(n2 * 4, length);
      int[] ints = method1380(doubles2, n4);
      double hypot = Math.hypot(entry2.doubleValue, entry2.doubleValue2);
      boolean flag = d4 > 0.0 && (entry2.intValue != 0 || entry2.intValue2 != 0);
      double hypot2 = Math.hypot(entry2.intValue, entry2.intValue2);
      double[] doubles3 = new double[n4];

      for (int i = 0; i < n4; i++) {
         int n5 = ints[i];
         double d8 = doubles2[n5];
         if (d2 > 0.0) {
            double d9 = entry2.doubleValue - entry.floatValues3[n5] + entry2.doubleValue3 * entry.floatValues5[n5];
            double d10 = entry2.doubleValue2 - entry.floatValues4[n5] + entry2.doubleValue4 * entry.floatValues5[n5];
            double d11 = Math.max(entry2.doubleValue5 * d3, 1.0);
            double d12 = Math.max(entry2.doubleValue6 * d3, 1.0);
            double x = Math.max(Math.abs(d9) - d11, 0.0);
            double y = Math.max(Math.abs(d10) - d12, 0.0);
            double hypot3 = Math.hypot(x, y);
            d8 += d2 * Math.log1p(hypot3);
         }

         if (flag) {
            double x = entry.floatValues6[n5] - entry2.intValue;
            double y = entry.floatValues7[n5] - entry2.intValue2;
            double hypot3 = Math.hypot(x, y);
            double hypot4 = Math.hypot(entry.floatValues6[n5], entry.floatValues7[n5]);
            d8 += d4 * (hypot3 / (hypot2 + hypot4 + 1.0));
         }

         if (d5 > 0.0 && hypot > d7) {
            double x = entry2.doubleValue - entry.floatValues8[n5] + entry2.doubleValue3 * entry.floatValues10[n5];
            double y = entry2.doubleValue2 - entry.floatValues9[n5] + entry2.doubleValue4 * entry.floatValues10[n5];
            double d9 = Math.hypot(x, y) / hypot;
            d8 += d5 * Math.abs(d9 - d6);
         }

         doubles3[i] = d8;
      }

      int n5 = Math.min(n2, n4);
      int[] ints2 = method1380(doubles3, n5);
      if (d <= 1.0E-9) {
         return this.entry3s[entry.intValues[ints[ints2[0]]]];
      } else {
         double[] doubles4 = new double[n5];

         for (int i = 0; i < n5; i++) {
            doubles4[i] = doubles3[ints2[i]];
         }

         double d8 = Math.max(method1381(doubles4), 1.0E-6);
         double[] doubles5 = new double[n5];
         double d9 = Double.NEGATIVE_INFINITY;

         for (int i = 0; i < n5; i++) {
            doubles5[i] = -doubles4[i] / (d8 * d);
            d9 = Math.max(d9, doubles5[i]);
         }

         double d10 = 0.0;

         for (int i = 0; i < n5; i++) {
            doubles5[i] = Math.exp(doubles5[i] - d9);
            d10 += doubles5[i];
         }

         double d11 = random.nextDouble() * d10;
         int n6 = n5 - 1;
         double d12 = 0.0;

         for (int i = 0; i < n5; i++) {
            d12 += doubles5[i];
            if (d11 <= d12) {
               n6 = i;
               break;
            }
         }

         return this.entry3s[entry.intValues[ints[ints2[n6]]]];
      }
   }

   private static int[] method1380(double[] doubles, int n) {
      Integer[] integers = new Integer[doubles.length];

      for (int i = 0; i < doubles.length; i++) {
         integers[i] = i;
      }

      Arrays.sort(integers, (var1x, var2x) -> Double.compare(doubles[var1x], doubles[var2x]));
      int[] ints = new int[n];

      for (int i = 0; i < n; i++) {
         ints[i] = integers[i];
      }

      return ints;
   }

   private static double method1381(double[] doubles) {
      double[] doubles2 = (double[])doubles.clone();
      Arrays.sort(doubles2);
      int n = doubles2.length / 2;
      return doubles2.length % 2 == 1 ? doubles2[n] : 0.5 * (doubles2[n - 1] + doubles2[n]);
   }

   private IntentLibraryRt(IntentLibraryRt.Entry3[] entry3s, float[][] floatses) {
      this.entry3s = entry3s;
      int[] ints = new int[4];

      for (IntentLibraryRt.Entry3 entry3 : entry3s) {
         ints[entry3.intValue2]++;
      }

      for (int i = 0; i < 4; i++) {
         IntentLibraryRt.Entry entry = new IntentLibraryRt.Entry();
         int n = ints[i];
         entry.intValues = new int[n];
         entry.floatValues = new float[n][8];
         entry.floatValues3 = new float[n];
         entry.floatValues4 = new float[n];
         entry.floatValues5 = new float[n];
         entry.floatValues6 = new float[n];
         entry.floatValues7 = new float[n];
         entry.floatValues8 = new float[n];
         entry.floatValues9 = new float[n];
         entry.floatValues10 = new float[n];
         this.entrys[i] = entry;
      }

      int[] ints2 = new int[4];

      for (IntentLibraryRt.Entry3 entry3 : entry3s) {
         IntentLibraryRt.Entry entry = this.entrys[entry3.intValue2];
         int n = ints2[entry3.intValue2]++;
         entry.intValues[n] = entry3.intValue;
         System.arraycopy(floatses[entry3.intValue], 0, entry.floatValues[n], 0, 8);
         entry.floatValues3[n] = entry3.intValue3;
         entry.floatValues4[n] = entry3.intValue4;
         entry.floatValues5[n] = entry3.length();
         entry.floatValues6[n] = entry3.intValues[0];
         entry.floatValues7[n] = entry3.intValues2[0];
         int n2 = Math.min(entry3.length(), 4);
         int n3 = 0;
         int n4 = 0;

         for (int i = 0; i < n2; i++) {
            n3 += entry3.intValues[i];
            n4 += entry3.intValues2[i];
         }

         entry.floatValues8[n] = n3;
         entry.floatValues9[n] = n4;
         entry.floatValues10[n] = n2;
      }

      for (int i = 0; i < 4; i++) {
         IntentLibraryRt.Entry entry = this.entrys[i];
         int length = entry.intValues.length;
         if (length == 0) {
            throw new IllegalStateException("library has no primitives of intent " + texts[i]);
         }

         for (int j = 0; j < 8; j++) {
            double d = 0.0;

            for (int k = 0; k < length; k++) {
               d += entry.floatValues[k][j];
            }

            d /= length;
            double d2 = 0.0;

            for (int k = 0; k < length; k++) {
               double d3 = entry.floatValues[k][j] - d;
               d2 += d3 * d3;
            }

            double d3 = Math.max(Math.sqrt(d2 / length), 1.0E-6);
            entry.floatValues2[j] = (float)d;
            entry.std[j] = (float)d3;

            for (int k = 0; k < length; k++) {
               entry.floatValues[k][j] = (float)((entry.floatValues[k][j] - d) / d3);
            }
         }
      }
   }

   public static IntentLibraryRt method1382() {
      IntentLibraryRt intentLibraryRt = method1383(path);
      if (intentLibraryRt != null) {
         return intentLibraryRt;
      } else {
         for (Path path2 : paths) {
            intentLibraryRt = method1383(path2);
            if (intentLibraryRt != null) {
               return intentLibraryRt;
            }
         }

         return method1384();
      }
   }

   private static IntentLibraryRt method1383(Path path) {
      try {
         return !Files.isRegularFile(path) ? null : method1385(Files.readString(path, StandardCharsets.UTF_8), path.toAbsolutePath().toString());
      } catch (Throwable e) {
         System.err.println("[MotorIntent] failed to load " + path + ": " + e);
         return null;
      }
   }

   private static IntentLibraryRt method1384() {
      try {
         IntentLibraryRt intentLibraryRt;
         try (InputStream resourceAsStream = IntentLibraryRt.class.getResourceAsStream("/assets/zenith/aimpipe/intent_library_rt.json")) {
            if (resourceAsStream == null) {
               return null;
            }

            intentLibraryRt = method1385(new String(resourceAsStream.readAllBytes(), StandardCharsets.UTF_8), "/assets/zenith/aimpipe/intent_library_rt.json");
         }

         return intentLibraryRt;
      } catch (Throwable e) {
         System.err.println("[MotorIntent] failed to load /assets/zenith/aimpipe/intent_library_rt.json: " + e);
         return null;
      }
   }

   private static IntentLibraryRt method1385(String string, String str) {
      JsonObject jsonObject = JsonParser.parseString(string).getAsJsonObject();
      if (jsonObject.get("format_version").getAsInt() == 1 && "motor_intent_runtime_library".equals(jsonObject.get("artifact").getAsString())) {
         JsonArray jsonArray = jsonObject.getAsJsonArray("intent_types");
         if (jsonArray.size() != 4) {
            throw new IllegalStateException("intent taxonomy mismatch");
         } else {
            for (int i = 0; i < 4; i++) {
               if (!texts[i].equals(jsonArray.get(i).getAsString())) {
                  throw new IllegalStateException("intent taxonomy mismatch at " + i);
               }
            }

            JsonArray jsonArray2 = jsonObject.getAsJsonArray("primitives");
            IntentLibraryRt.Entry3[] entry3s = new IntentLibraryRt.Entry3[jsonArray2.size()];
            float[][] floatses = new float[jsonArray2.size()][8];

            for (int i = 0; i < jsonArray2.size(); i++) {
               JsonArray jsonArray3 = jsonArray2.get(i).getAsJsonArray();
               int n = jsonArray3.get(0).getAsInt();
               if (n < 0 || n >= 4) {
                  throw new IllegalStateException("bad intent index " + n);
               }

               JsonArray jsonArray4 = jsonArray3.get(1).getAsJsonArray();
               if (jsonArray4.size() < 1) {
                  throw new IllegalStateException("empty primitive " + i);
               }

               int[] intValues = new int[jsonArray4.size()];
               int[] intValues2 = new int[jsonArray4.size()];

               for (int j = 0; j < jsonArray4.size(); j++) {
                  JsonArray jsonArray5 = jsonArray4.get(j).getAsJsonArray();
                  intValues[j] = jsonArray5.get(0).getAsInt();
                  intValues2[j] = jsonArray5.get(1).getAsInt();
               }

               JsonArray jsonArray5 = jsonArray3.get(2).getAsJsonArray();
               if (jsonArray5.size() != 8) {
                  throw new IllegalStateException("bad descriptor size for primitive " + i);
               }

               for (int j = 0; j < 8; j++) {
                  floatses[i][j] = jsonArray5.get(j).getAsFloat();
               }

               entry3s[i] = new IntentLibraryRt.Entry3(i, n, intValues, intValues2);
            }

            if (jsonObject.has("count") && jsonObject.get("count").getAsInt() != entry3s.length) {
               throw new IllegalStateException("library count mismatch");
            } else {
               IntentLibraryRt intentLibraryRt = new IntentLibraryRt(entry3s, floatses);
               System.out.println("[MotorIntent] loaded library " + str + " (" + entry3s.length + " primitives)");
               return intentLibraryRt;
            }
         }
      } else {
         throw new IllegalStateException("unsupported motor-intent library artifact");
      }
   }

   private static final class Entry {
      public int[] intValues;
      public float[][] floatValues;
      public float[] floatValues2 = new float[8];
      public float[] std = new float[8];
      public float[] floatValues3;
      public float[] floatValues4;
      public float[] floatValues5;
      public float[] floatValues6;
      public float[] floatValues7;
      public float[] floatValues8;
      public float[] floatValues9;
      public float[] floatValues10;
   }

   public static final class Entry2 {
      public double doubleValue;
      public double doubleValue2;
      public double doubleValue3;
      public double doubleValue4;
      public double doubleValue5;
      public double doubleValue6;
      public int intValue;
      public int intValue2;
   }

   public static final class Entry3 {
      public final int intValue;
      public final int intValue2;
      public final int[] intValues;
      public final int[] intValues2;
      public final int intValue3;
      public final int intValue4;

      private Entry3(int intValue, int n, int[] intValues, int[] intValues2) {
         this.intValue = intValue;
         this.intValue2 = n;
         this.intValues = intValues;
         this.intValues2 = intValues2;
         int n2 = 0;
         int n3 = 0;

         for (int i = 0; i < intValues.length; i++) {
            n2 += intValues[i];
            n3 += intValues2[i];
         }

         this.intValue3 = n2;
         this.intValue4 = n3;
      }

      public int length() {
         return this.intValues.length;
      }
   }
}
