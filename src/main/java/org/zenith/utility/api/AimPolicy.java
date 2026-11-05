package org.zenith.utility.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class AimPolicy {
   public AimPolicy.Entry[] entrys;
   public int[][] intValues;
   public int[][] intValues2;
   public float floatValue;
   public float floatValue2;
   public int intValue;
   public int intValue2;
   public int intValue3;
   public int intValue4;
   public int intValue5;
   public int intValue6;
   public int intValue7;
   public int intValue8;
   public int intValue9;
   public int intValue10;
   public int intValue11;
   public float[][] floatValues;
   public float[][] floatValues2;
   public float[] floatValues3;
   public float[] floatValues4;
   public float[][] floatValues5;
   public float[] floatValues6;
   public float[][] floatValues7;
   public float[][] floatValues8;
   public float[][] floatValues9;
   public float[][] floatValues10;
   public float[] floatValues11;
   public float[] floatValues12;
   public float[][] floatValues13;
   public float[] floatValues14;
   public float[][] floatValues15;
   public float[] floatValues16;
   public float[][] floatValues17;
   public float[] floatValues18;
   public float[][] floatValues19;
   public float[] floatValues20;
   public float[][] floatValues21;
   public float[] floatValues22;
   private static final String text = "/assets/zenith/aimpipe/aim_policy.json";
   private static final Path path = Path.of("aim_policy.json");
   private static final Path[] paths = new Path[]{
      Path.of("..", "scripts", "test", "aimpipe", "out", "policy", "aim_policy.json"),
      Path.of("scripts", "test", "aimpipe", "out", "policy", "aim_policy.json")
   };

   public static AimPolicy method5432() {
      AimPolicy aimPolicy = method5433(path);
      if (aimPolicy != null) {
         return aimPolicy;
      } else {
         aimPolicy = method5434();
         if (aimPolicy != null) {
            return aimPolicy;
         } else {
            for (Path path2 : paths) {
               aimPolicy = method5433(path2);
               if (aimPolicy != null) {
                  return aimPolicy;
               }
            }

            return null;
         }
      }
   }

   private static AimPolicy method5433(Path path) {
      try {
         return !Files.isRegularFile(path) ? null : method5435(Files.readString(path, StandardCharsets.UTF_8), path.toAbsolutePath().toString());
      } catch (Throwable e) {
         System.err.println("[AimPolicy] failed to load " + path + ": " + e);
         return null;
      }
   }

   private static AimPolicy method5434() {
      try {
         AimPolicy aimPolicy;
         try (InputStream resourceAsStream = AimPolicy.class.getResourceAsStream("/assets/zenith/aimpipe/aim_policy.json")) {
            if (resourceAsStream == null) {
               return null;
            }

            aimPolicy = method5435(new String(resourceAsStream.readAllBytes(), StandardCharsets.UTF_8), "/assets/zenith/aimpipe/aim_policy.json");
         }

         return aimPolicy;
      } catch (Throwable e) {
         System.err.println("[AimPolicy] failed to load /assets/zenith/aimpipe/aim_policy.json: " + e);
         return null;
      }
   }

   private static AimPolicy method5435(String string, String str) {
      AimPolicy aimPolicy = method5436(JsonParser.parseString(string).getAsJsonObject());
      System.out
         .println(
            "[AimPolicy] loaded "
               + str
               + " (planner="
               + aimPolicy.intValue3
               + ", motor="
               + aimPolicy.intValue4
               + ", bins="
               + aimPolicy.intValue5
               + "/"
               + aimPolicy.intValue6
               + ", cadence="
               + aimPolicy.intValue
               + ", z="
               + aimPolicy.intValue10
               + ")"
         );
      return aimPolicy;
   }

   private static AimPolicy method5436(JsonObject jsonObject) {
      int n = jsonObject.has("format_version") ? jsonObject.get("format_version").getAsInt() : 1;
      if (n != 5) {
         throw new IllegalStateException("unsupported artifact format_version " + n + " (expected 5; retrain and re-run policy/export.py)");
      } else {
         AimPolicy aimPolicy = new AimPolicy();
         JsonArray jsonArray = jsonObject.getAsJsonArray("feature_spec");
         aimPolicy.entrys = new AimPolicy.Entry[jsonArray.size()];

         for (int i = 0; i < jsonArray.size(); i++) {
            JsonObject json = jsonArray.get(i).getAsJsonObject();
            aimPolicy.entrys[i] = new AimPolicy.Entry(
               json.get("index").getAsInt(),
               "mu".equals(json.get("transform").getAsString()),
               json.get("scale").getAsFloat(),
               json.get("mean").getAsFloat(),
               json.get("std").getAsFloat()
            );
         }

         aimPolicy.intValues = new int[2][];
         aimPolicy.intValues2 = new int[2][];
         String[] strings = new String[]{"bins_yaw", "bins_pitch"};

         for (int i = 0; i < 2; i++) {
            JsonArray jsonArray2 = jsonObject.getAsJsonArray(strings[i]);
            aimPolicy.intValues[i] = new int[jsonArray2.size()];
            aimPolicy.intValues2[i] = new int[jsonArray2.size()];

            for (int j = 0; j < jsonArray2.size(); j++) {
               JsonObject json = jsonArray2.get(j).getAsJsonObject();
               aimPolicy.intValues[i][j] = json.get("lo").getAsInt();
               aimPolicy.intValues2[i][j] = json.get("hi").getAsInt();
            }
         }

         JsonObject json = jsonObject.getAsJsonObject("sampling");
         aimPolicy.floatValue = json.get("temperature").getAsFloat();
         aimPolicy.floatValue2 = json.get("top_p").getAsFloat();
         aimPolicy.intValue = jsonObject.getAsJsonObject("planner").get("ticks").getAsInt();
         JsonObject jsonObject2 = jsonObject.getAsJsonObject("model");
         aimPolicy.intValue2 = jsonObject2.get("in_dim").getAsInt();
         aimPolicy.intValue3 = jsonObject2.get("hidden").getAsInt();
         aimPolicy.intValue4 = jsonObject2.get("motor_hidden").getAsInt();
         aimPolicy.intValue5 = jsonObject2.get("n_bins_yaw").getAsInt();
         aimPolicy.intValue6 = jsonObject2.get("n_bins_pitch").getAsInt();
         aimPolicy.intValue7 = jsonObject2.get("zero_bin_yaw").getAsInt();
         aimPolicy.intValue8 = jsonObject2.get("zero_bin_pitch").getAsInt();
         aimPolicy.intValue9 = jsonObject2.get("bin_embed").getAsInt();
         aimPolicy.intValue10 = jsonObject2.get("z_dim").getAsInt();
         aimPolicy.intValue11 = jsonObject2.get("prior_hidden").getAsInt();
         JsonObject jsonObject3 = jsonObject.getAsJsonObject("weights");
         aimPolicy.floatValues = method5439(jsonObject3.getAsJsonArray("planner_weight_ih"));
         aimPolicy.floatValues2 = method5439(jsonObject3.getAsJsonArray("planner_weight_hh"));
         aimPolicy.floatValues3 = method5438(jsonObject3.getAsJsonArray("planner_bias_ih"));
         aimPolicy.floatValues4 = method5438(jsonObject3.getAsJsonArray("planner_bias_hh"));
         aimPolicy.floatValues5 = method5439(jsonObject3.getAsJsonArray("intent_weight"));
         aimPolicy.floatValues6 = method5438(jsonObject3.getAsJsonArray("intent_bias"));
         aimPolicy.floatValues7 = method5439(jsonObject3.getAsJsonArray("yaw_embedding"));
         aimPolicy.floatValues8 = method5439(jsonObject3.getAsJsonArray("pitch_embedding"));
         aimPolicy.floatValues9 = method5439(jsonObject3.getAsJsonArray("motor_weight_ih"));
         aimPolicy.floatValues10 = method5439(jsonObject3.getAsJsonArray("motor_weight_hh"));
         aimPolicy.floatValues11 = method5438(jsonObject3.getAsJsonArray("motor_bias_ih"));
         aimPolicy.floatValues12 = method5438(jsonObject3.getAsJsonArray("motor_bias_hh"));
         aimPolicy.floatValues13 = method5439(jsonObject3.getAsJsonArray("yaw_head_weight"));
         aimPolicy.floatValues14 = method5438(jsonObject3.getAsJsonArray("yaw_head_bias"));
         aimPolicy.floatValues15 = method5439(jsonObject3.getAsJsonArray("pitch_context_weight"));
         aimPolicy.floatValues16 = method5438(jsonObject3.getAsJsonArray("pitch_context_bias"));
         aimPolicy.floatValues17 = method5439(jsonObject3.getAsJsonArray("pitch_head_weight"));
         aimPolicy.floatValues18 = method5438(jsonObject3.getAsJsonArray("pitch_head_bias"));
         aimPolicy.floatValues19 = method5439(jsonObject3.getAsJsonArray("prior_weight_0"));
         aimPolicy.floatValues20 = method5438(jsonObject3.getAsJsonArray("prior_bias_0"));
         aimPolicy.floatValues21 = method5439(jsonObject3.getAsJsonArray("prior_weight_1"));
         aimPolicy.floatValues22 = method5438(jsonObject3.getAsJsonArray("prior_bias_1"));
         aimPolicy.method5437();
         return aimPolicy;
      }
   }

   private void method5437() {
      if (this.entrys.length == this.intValue2 && this.intValue >= 1) {
         if (this.intValues[0].length != this.intValue5 || this.intValues[1].length != this.intValue6) {
            throw new IllegalStateException("bin table size mismatch");
         } else if (this.intValue7 < 0 || this.intValue7 >= this.intValue5 || this.intValue8 < 0 || this.intValue8 >= this.intValue6) {
            throw new IllegalStateException("zero-bin index out of range");
         } else if (this.floatValues.length == 4 * this.intValue3 && this.floatValues9.length == 3 * this.intValue4) {
            if (this.intValue10 < 1 || this.floatValues9[0].length != this.intValue4 + 2 * this.intValue9 + this.intValue10) {
               throw new IllegalStateException("motor input width must include the gesture latent");
            } else if (this.floatValues19.length != this.intValue11
               || this.floatValues19[0].length != this.intValue4 + 2 * this.intValue9
               || this.floatValues21.length != 2 * this.intValue10
               || this.floatValues21[0].length != this.intValue11) {
               throw new IllegalStateException("gesture prior weight shape mismatch");
            }
         } else {
            throw new IllegalStateException("recurrent weight shape mismatch");
         }
      } else {
         throw new IllegalStateException("feature/planner configuration mismatch");
      }
   }

   private static float[] method5438(JsonArray jsonArray) {
      float[] floats = new float[jsonArray.size()];

      for (int i = 0; i < floats.length; i++) {
         floats[i] = jsonArray.get(i).getAsFloat();
      }

      return floats;
   }

   private static float[][] method5439(JsonArray jsonArray) {
      float[][] floatses = new float[jsonArray.size()][];

      for (int i = 0; i < floatses.length; i++) {
         floatses[i] = method5438(jsonArray.get(i).getAsJsonArray());
      }

      return floatses;
   }

   public static final class Entry {
      public final int intValue;
      public final boolean flag;
      public final float floatValue;
      public final float floatValue2;
      public final float floatValue3;

      public Entry(int intValue, boolean flag, float floatValue, float f, float f2) {
         this.intValue = intValue;
         this.flag = flag;
         this.floatValue = floatValue;
         this.floatValue2 = f;
         this.floatValue3 = f2;
      }
   }
}
