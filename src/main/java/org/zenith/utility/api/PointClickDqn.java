package org.zenith.utility.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class PointClickDqn {
   public static final int CONST_intValue = 11;
   public static final int CONST_intValue2 = 50;
   private static final String text = "/assets/zenith/pointclick/point_click_dqn.json";
   private static final Path path = Path.of("point_click_dqn.json");
   private final float[][] floatValues;
   private final float[] floatValues2;
   private final float[][] floatValues3;
   private final float[] floatValues4;

   private PointClickDqn(float[][] floatValues, float[] floatValues2, float[][] floatValues3, float[] floatValues4) {
      this.floatValues = floatValues;
      this.floatValues2 = floatValues2;
      this.floatValues3 = floatValues3;
      this.floatValues4 = floatValues4;
   }

   public static PointClickDqn method3223() {
      PointClickDqn pointClickDqn = method3224(path);
      if (pointClickDqn != null) {
         return pointClickDqn;
      } else {
         try {
            PointClickDqn pointClickDqn2;
            try (InputStream resourceAsStream = PointClickDqn.class.getResourceAsStream("/assets/zenith/pointclick/point_click_dqn.json")) {
               if (resourceAsStream == null) {
                  return null;
               }

               pointClickDqn2 = method3225(new String(resourceAsStream.readAllBytes(), StandardCharsets.UTF_8), "/assets/zenith/pointclick/point_click_dqn.json");
            }

            return pointClickDqn2;
         } catch (Throwable e) {
            System.err.println("[PointClick] failed to load /assets/zenith/pointclick/point_click_dqn.json: " + e);
            return null;
         }
      }
   }

   private static PointClickDqn method3224(Path path) {
      try {
         return !Files.isRegularFile(path) ? null : method3225(Files.readString(path, StandardCharsets.UTF_8), path.toAbsolutePath().toString());
      } catch (Throwable e) {
         System.err.println("[PointClick] failed to load " + path + ": " + e);
         return null;
      }
   }

   private static PointClickDqn method3225(String str, String string) {
      JsonObject jsonObject = JsonParser.parseString(str).getAsJsonObject();
      if (jsonObject.get("format_version").getAsInt() == 1 && "point_and_click_dqn".equals(jsonObject.get("model").getAsString())) {
         JsonObject json = jsonObject.getAsJsonObject("architecture");
         method3231(json.get("input").getAsInt() == 11, "input size");
         method3231(json.get("output").getAsInt() == 50, "output size");
         int n = json.get("hidden").getAsInt();
         JsonObject jsonObject2 = jsonObject.getAsJsonObject("weights");
         PointClickDqn pointClickDqn = new PointClickDqn(
            method3230(jsonObject2.getAsJsonArray("hidden_kernel"), 11, n),
            method3229(jsonObject2.getAsJsonArray("hidden_bias"), n),
            method3230(jsonObject2.getAsJsonArray("output_kernel"), n, 50),
            method3229(jsonObject2.getAsJsonArray("output_bias"), 50)
         );
         method3228(pointClickDqn, jsonObject.getAsJsonArray("parity"));
         System.out.println("[PointClick] loaded DQN " + string + " (11x" + n + "x50)");
         return pointClickDqn;
      } else {
         throw new IllegalStateException("unsupported point-click policy artifact");
      }
   }

   public int method3226(float[] floats) {
      float[] floats2 = this.method3227(floats);
      int n = 0;

      for (int i = 1; i < floats2.length; i++) {
         if (floats2[i] > floats2[n]) {
            n = i;
         }
      }

      return n;
   }

   public float[] method3227(float[] floats) {
      if (floats.length != 11) {
         throw new IllegalArgumentException("expected 11 inputs");
      } else {
         float[] floats2 = new float[this.floatValues2.length];

         for (int i = 0; i < floats2.length; i++) {
            float f = this.floatValues2[i];

            for (int j = 0; j < floats.length; j++) {
               f += floats[j] * this.floatValues[j][i];
            }

            floats2[i] = Math.max(f, 0.0F);
         }

         float[] floats3 = (float[])this.floatValues4.clone();

         for (int i = 0; i < floats3.length; i++) {
            for (int j = 0; j < floats2.length; j++) {
               floats3[i] += floats2[j] * this.floatValues3[j][i];
            }
         }

         return floats3;
      }
   }

   private static void method3228(PointClickDqn pointClickDqn, JsonArray jsonArray) {
      for (int i = 0; i < jsonArray.size(); i++) {
         JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
         float[] floats = method3229(jsonObject.getAsJsonArray("input"), 11);
         float[] floats2 = method3229(jsonObject.getAsJsonArray("q_values"), 50);
         float[] floats3 = pointClickDqn.method3227(floats);
         float f = 0.0F;

         for (int j = 0; j < floats3.length; j++) {
            f = Math.max(f, Math.abs(floats3[j] - floats2[j]));
         }

         method3231(f <= 0.002F, "parity case " + i + ", error=" + f);
         method3231(pointClickDqn.method3226(floats) == jsonObject.get("action").getAsInt(), "parity action " + i);
      }
   }

   private static float[] method3229(JsonArray jsonArray, int n) {
      method3231(jsonArray.size() == n, "vector length");
      float[] floats = new float[n];

      for (int i = 0; i < n; i++) {
         floats[i] = jsonArray.get(i).getAsFloat();
      }

      return floats;
   }

   private static float[][] method3230(JsonArray jsonArray, int n, int n2) {
      method3231(jsonArray.size() == n, "matrix rows");
      float[][] floatses = new float[n][n2];

      for (int i = 0; i < n; i++) {
         floatses[i] = method3229(jsonArray.get(i).getAsJsonArray(), n2);
      }

      return floatses;
   }

   private static void method3231(boolean flag, String str) {
      if (!flag) {
         throw new IllegalStateException("invalid point-click artifact: " + str);
      }
   }
}
