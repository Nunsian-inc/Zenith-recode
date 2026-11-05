package org.zenith.utility.api;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Random;

public final class WorldStylePlanModel {
   public static final String text = "neuraim_java_world_style_plan_v1";
   public static final String text2 = "neuraim_aim_world_v1.json";
   private final int intValue;
   private final int intValue2;
   private final int intValue3;
   private final int intValue4;
   private final int intValue5;
   private final int intValue6;
   private final int intValue7;
   private final int intValue8;
   private final int intValue9;
   private final int intValue10;
   private final int intValue11;
   private final int intValue12;
   private final String text3;
   private final String text4;
   private final float floatValue;
   private final float floatValue2;
   private final float floatValue3;
   private final float floatValue4;
   private final float floatValue5;
   private final float floatValue6;
   private final float floatValue7;
   private final float floatValue8;
   private final float floatValue9;
   private final int intValue13;
   private final float[] floatValues;
   private final float[] floatValues2;
   private final boolean[] flags;
   private final float[][][] floatValues3;
   private final float[][][] floatValues4;
   private final float[][] floatValues5;
   private final float[][] floatValues6;
   private final float[] floatValues7;
   private final float[] floatValues8;
   private final float[][][][][] floatValues9;
   private final float[][][][][] floatValues10;
   private final float[] floatValues11;
   private final float[] floatValues12;
   private final float[][] floatValues13;
   private final float[][] floatValues14;
   private final float[][] floatValues15;
   private final float[][] floatValues16;
   private final float[] floatValues17;
   private final float[] floatValues18;
   private final float[] floatValues19;
   private final float[] floatValues20;
   private final float[] floatValues21;
   private final float[] floatValues22;
   private final float[] floatValues23;
   private final float[] floatValues24;
   private final float[] floatValues25;
   private final float[] floatValues26;

   private WorldStylePlanModel(JsonObject json) {
      method6195("neuraim_java_world_style_plan_v1".equals(method6189(json, "format")), "unexpected model format");
      JsonObject json2 = method6187(json, "architecture");
      this.intValue = method6190(json2, "input_dim");
      this.intValue2 = method6190(json2, "hidden_dim");
      this.intValue3 = method6190(json2, "num_layers");
      this.intValue4 = method6190(json2, "horizons");
      this.intValue5 = method6190(json2, "style_dim");
      this.intValue6 = method6190(json2, "physics_input_dim");
      this.intValue10 = method6190(json2, "physics_embedding_dim");
      this.intValue7 = method6190(json2, "wall_channels");
      this.intValue8 = method6190(json2, "wall_size");
      this.intValue9 = method6190(json2, "wall_embedding_dim");
      this.floatValue = method6191(json2, "count_scale");
      this.floatValue2 = method6191(json2, "min_log_scale");
      this.floatValue3 = method6191(json2, "max_log_scale");
      this.floatValue4 = method6191(json2, "max_correlation");
      method6195(this.intValue3 > 0, "num_layers must be positive");
      method6195(this.intValue8 == 8 && this.intValue7 == 3, "only a 3x8x8x8 cube is supported");
      JsonObject json3 = method6187(json, "runtime");
      this.intValue11 = method6190(json3, "history_ticks");
      this.intValue12 = method6190(json3, "style_refresh_ticks");
      this.floatValue5 = method6191(json3, "variance_temperature");
      this.text3 = method6189(json3, "action_mode");
      this.text4 = method6189(json3, "style_scope");
      method6195(this.text3.equals("sampled") || this.text3.equals("means"), "unsupported action mode");
      method6195(this.text4.equals("target_session"), "unsupported style scope");
      this.flags = new boolean[this.intValue];
      JsonArray jsonArray = json3.getAsJsonArray("masked_history_feature_indices");
      if (jsonArray != null) {
         for (JsonElement jsonElement : jsonArray) {
            int n = jsonElement.getAsInt();
            method6195(n >= 0 && n < this.intValue, "masked feature index out of range");
            this.flags[n] = true;
         }
      }

      JsonObject json4 = method6187(json, "collision_cube");
      method6195(method6190(json4, "size") == this.intValue8, "collision cube size mismatch");
      this.floatValue6 = method6191(json4, "horizontal_radius");
      this.floatValue7 = method6191(json4, "min_y_offset");
      this.floatValue8 = method6191(json4, "max_y_offset");
      this.intValue13 = method6190(json4, "occupancy_samples_per_axis");
      this.floatValue9 = method6191(json4, "distance_cap");
      JsonObject json5 = method6187(json, "standardizer");
      method6195(method6188(json5, "columns").size() == this.intValue, "standardizer column count mismatch");
      this.floatValues = method6192(json5, "mean", this.intValue);
      this.floatValues2 = method6192(json5, "scale", this.intValue);

      for (float f : this.floatValues2) {
         method6195(Float.isFinite(f) && f > 0.0F, "invalid feature scale");
      }

      JsonObject json6 = method6187(json, "weights");
      this.floatValues3 = new float[this.intValue3][][];
      this.floatValues4 = new float[this.intValue3][][];
      this.floatValues5 = new float[this.intValue3][];
      this.floatValues6 = new float[this.intValue3][];

      for (int i = 0; i < this.intValue3; i++) {
         int intValue = i == 0 ? this.intValue : this.intValue2;
         this.floatValues3[i] = method6193(json6, "encoder.weight_ih_l" + i, 3 * this.intValue2, intValue);
         this.floatValues4[i] = method6193(json6, "encoder.weight_hh_l" + i, 3 * this.intValue2, this.intValue2);
         this.floatValues5[i] = method6192(json6, "encoder.bias_ih_l" + i, 3 * this.intValue2);
         this.floatValues6[i] = method6192(json6, "encoder.bias_hh_l" + i, 3 * this.intValue2);
      }

      this.floatValues7 = method6192(json6, "history_norm.weight", this.intValue2);
      this.floatValues8 = method6192(json6, "history_norm.bias", this.intValue2);
      this.floatValues9 = method6194(json6, "wall_encoder.0.weight", 16, this.intValue7, 3, 3, 3);
      this.floatValues11 = method6192(json6, "wall_encoder.0.bias", 16);
      this.floatValues10 = method6194(json6, "wall_encoder.3.weight", 32, 16, 3, 3, 3);
      this.floatValues12 = method6192(json6, "wall_encoder.3.bias", 32);
      this.floatValues13 = method6193(json6, "wall_encoder.7.weight", this.intValue9, 256);
      this.floatValues17 = method6192(json6, "wall_encoder.7.bias", this.intValue9);
      this.floatValues18 = method6192(json6, "wall_encoder.8.weight", this.intValue9);
      this.floatValues19 = method6192(json6, "wall_encoder.8.bias", this.intValue9);
      this.floatValues14 = method6193(json6, "physics_encoder.0.weight", this.intValue10, this.intValue6);
      this.floatValues20 = method6192(json6, "physics_encoder.0.bias", this.intValue10);
      this.floatValues21 = method6192(json6, "physics_encoder.1.weight", this.intValue10);
      this.floatValues22 = method6192(json6, "physics_encoder.1.bias", this.intValue10);
      int intValue = this.intValue2 + this.intValue5 + this.intValue9 + this.intValue10;
      this.floatValues15 = method6193(json6, "style_fusion.0.weight", this.intValue2, intValue);
      this.floatValues23 = method6192(json6, "style_fusion.0.bias", this.intValue2);
      this.floatValues24 = method6192(json6, "style_fusion.1.weight", this.intValue2);
      this.floatValues25 = method6192(json6, "style_fusion.1.bias", this.intValue2);
      this.floatValues16 = method6193(json6, "plan_head.weight", this.intValue4 * 5, this.intValue2);
      this.floatValues26 = method6192(json6, "plan_head.bias", this.intValue4 * 5);
   }

   public static WorldStylePlanModel method6162(Path path) throws IOException {
      WorldStylePlanModel worldStylePlanModel;
      try (BufferedReader bufferedReader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
         worldStylePlanModel = method6164(bufferedReader);
      }

      return worldStylePlanModel;
   }

   public static WorldStylePlanModel method6163(InputStream resourceAsStream) throws IOException {
      WorldStylePlanModel worldStylePlanModel;
      try (InputStreamReader inputStreamReader = new InputStreamReader(resourceAsStream, StandardCharsets.UTF_8)) {
         worldStylePlanModel = method6164(inputStreamReader);
      }

      return worldStylePlanModel;
   }

   private static WorldStylePlanModel method6164(Reader bufferedReader) throws IOException {
      try {
         JsonObject json = (JsonObject)new Gson().fromJson(bufferedReader, JsonObject.class);
         method6195(json != null, "empty model JSON");
         return new WorldStylePlanModel(json);
      } catch (RuntimeException e) {
         throw new IOException("invalid Neuraim model JSON: " + e.getMessage(), e);
      }
   }

   public static WorldStylePlanModel method6165() {
      for (Path path : List.of(
         Path.of("models", "neuraim_aim_world_v1.json"),
         Path.of("run", "models", "neuraim_aim_world_v1.json"),
         Path.of("neuraim_aim_world_v1.json"),
         Path.of("..", "models", "neuraim_aim_world_v1.json")
      )) {
         if (Files.isRegularFile(path)) {
            try {
               WorldStylePlanModel worldStylePlanModel = method6162(path);
               System.out.println("[Neuraim] Loaded " + path.toAbsolutePath());
               return worldStylePlanModel;
            } catch (IOException e) {
               System.err.println("[Neuraim] Failed to load " + path + ": " + e.getMessage());
            }
         }
      }

      InputStream resourceAsStream = WorldStylePlanModel.class.getClassLoader().getResourceAsStream("models/neuraim_aim_world_v1.json");
      if (resourceAsStream != null) {
         try {
            return method6163(resourceAsStream);
         } catch (IOException e) {
            System.err.println("[Neuraim] Bundled model is invalid: " + e.getMessage());
         }
      }

      System.err.println("[Neuraim] neuraim_aim_world_v1.json not found; TestRotationModeV3 disabled");
      return null;
   }

   public WorldStylePlanModel.Data method6166(float[][] floatses, float[] floats, float[][][][] floatseseses, float[] floatValues4) {
      method6195(floatses != null && floatses.length == this.intValue11, "history tick count mismatch");
      method6195(floats != null && floats.length == this.intValue5, "style width mismatch");
      method6195(floatValues4 != null && floatValues4.length == this.intValue6, "physics width mismatch");
      method6186(floatseseses, this.intValue7, this.intValue8);
      float[][] floatses2 = new float[this.intValue11][this.intValue];

      for (int i = 0; i < this.intValue11; i++) {
         method6195(floatses[i] != null && floatses[i].length == this.intValue, "history feature width mismatch");

         for (int j = 0; j < this.intValue; j++) {
            floatses2[i][j] = this.flags[j] ? 0.0F : (floatses[i][j] - this.floatValues[j]) / this.floatValues2[j];
            method6195(Float.isFinite(floatses2[i][j]), "non-finite history feature");
         }
      }

      float[] floatValues = this.method6171(floatses2);
      float[] floatValues2 = this.method6173(floatseseses);
      float[] floatValues3 = method6179(method6178(method6183(this.floatValues14, this.floatValues20, floatValues4), this.floatValues21, this.floatValues22));
      float[] floatValues42 = method6179(
         method6178(method6183(this.floatValues15, this.floatValues23, method6177(floatValues, floats, floatValues2, floatValues3)), this.floatValues24, this.floatValues25)
      );
      float[] floats2 = method6183(this.floatValues16, this.floatValues26, floatValues42);
      float[][] floatValues5 = new float[this.intValue4][2];
      float[][] floatValues6 = new float[this.intValue4][2];
      float[] floatValues7 = new float[this.intValue4];
      float log = (float)Math.log(this.floatValue);

      for (int i = 0; i < this.intValue4; i++) {
         int n = i * 5;
         floatValues5[i][0] = floats2[n] * this.floatValue;
         floatValues5[i][1] = floats2[n + 1] * this.floatValue;
         floatValues6[i][0] = clamp(floats2[n + 2], this.floatValue2, this.floatValue3) + log;
         floatValues6[i][1] = clamp(floats2[n + 3], this.floatValue2, this.floatValue3) + log;
         floatValues7[i] = (float)Math.tanh(floats2[n + 4]) * this.floatValue4;
      }

      return new WorldStylePlanModel.Data(floatValues, floatValues2, floatValues3, floatValues42, floatValues5, floatValues6, floatValues7);
   }

   public int[][] method6167(float[][] floatses, float[] floats, float[][][][] floatseseses, float[] floatValues4, Random random) {
      method6195(random != null, "random must not be null");
      WorldStylePlanModel.Data data = this.method6166(floatses, floats, floatseseses, floatValues4);
      int[][] intses = new int[this.intValue4][2];
      double d = Math.sqrt(this.floatValue5);

      for (int i = 0; i < this.intValue4; i++) {
         double nextGaussian = random.nextGaussian();
         double nextGaussian2 = random.nextGaussian();
         double d2 = data.floatValues7[i];
         double d3 = d2 * nextGaussian + Math.sqrt(Math.max(1.0E-6, 1.0 - d2 * d2)) * nextGaussian2;
         intses[i][0] = method6185(data.floatValues5[i][0] + Math.exp(data.floatValues6[i][0]) * d * nextGaussian);
         intses[i][1] = method6185(data.floatValues5[i][1] + Math.exp(data.floatValues6[i][1]) * d * d3);
      }

      return intses;
   }

   public int[][] method6168(float[][] floatses, float[] floats, float[][][][] floatseseses, float[] floatValues4) {
      WorldStylePlanModel.Data data = this.method6166(floatses, floats, floatseseses, floatValues4);
      int[][] intses = new int[this.intValue4][2];

      for (int i = 0; i < this.intValue4; i++) {
         intses[i][0] = method6185(data.floatValues5[i][0]);
         intses[i][1] = method6185(data.floatValues5[i][1]);
      }

      return intses;
   }

   public float[] method6169(Random random) {
      float[] floats = new float[this.intValue5];

      for (int i = 0; i < floats.length; i++) {
         floats[i] = (float)random.nextGaussian();
      }

      return floats;
   }

   public float[] method6170() {
      return new float[this.intValue5];
   }

   private float[] method6171(float[][] floatses) {
      float[][] floatses2 = new float[this.intValue3][this.intValue2];

      for (float[] floats : floatses) {
         float[] floatValues4 = floats;

         for (int i = 0; i < this.intValue3; i++) {
            floatses2[i] = this.method6172(floatValues4, floatses2[i], i);
            floatValues4 = floatses2[i];
         }
      }

      return method6178(floatses2[this.intValue3 - 1], this.floatValues7, this.floatValues8);
   }

   private float[] method6172(float[] floatValues4, float[] floats, int n) {
      float[] floats2 = method6183(this.floatValues3[n], this.floatValues5[n], floatValues4);
      float[] floats3 = method6183(this.floatValues4[n], this.floatValues6[n], floats);
      float[] floats4 = new float[this.intValue2];

      for (int i = 0; i < this.intValue2; i++) {
         float f = method6184(floats2[i] + floats3[i]);
         float f2 = method6184(floats2[this.intValue2 + i] + floats3[this.intValue2 + i]);
         float tanh = (float)Math.tanh(floats2[2 * this.intValue2 + i] + f * floats3[2 * this.intValue2 + i]);
         floats4[i] = (1.0F - f2) * tanh + f2 * floats[i];
      }

      return floats4;
   }

   private float[] method6173(float[][][][] floatseseses) {
      float[][][][] floatseseses2 = method6175(method6180(method6174(floatseseses, this.floatValues9, this.floatValues11)));
      float[] floatValues4 = method6176(method6175(method6180(method6174(floatseseses2, this.floatValues10, this.floatValues12))));
      method6195(floatValues4.length == 256, "wall flatten mismatch");
      return method6179(method6178(method6183(this.floatValues13, this.floatValues17, floatValues4), this.floatValues18, this.floatValues19));
   }

   private static float[][][][] method6174(float[][][][] floatseseses, float[][][][][] floatValues9, float[] floatValues11) {
      int length = floatValues9.length;
      int n = floatseseses.length;
      int length2 = floatseseses[0].length;
      int length3 = floatseseses[0][0].length;
      int length4 = floatseseses[0][0][0].length;
      float[][][][] floatseseses2 = new float[length][length2][length3][length4];

      for (int i = 0; i < length; i++) {
         for (int j = 0; j < length2; j++) {
            for (int k = 0; k < length3; k++) {
               for (int m = 0; m < length4; m++) {
                  float f = floatValues11[i];

                  for (int i2 = 0; i2 < n; i2++) {
                     for (int i3 = 0; i3 < 3; i3++) {
                        for (int i4 = 0; i4 < 3; i4++) {
                           for (int i5 = 0; i5 < 3; i5++) {
                              int n2 = j + i3 - 1;
                              int n3 = k + i4 - 1;
                              int n4 = m + i5 - 1;
                              if (n2 >= 0 && n2 < length2 && n3 >= 0 && n3 < length3 && n4 >= 0 && n4 < length4) {
                                 f += floatValues9[i][i2][i3][i4][i5] * floatseseses[i2][n2][n3][n4];
                              }
                           }
                        }
                     }
                  }

                  floatseseses2[i][j][k][m] = f;
               }
            }
         }
      }

      return floatseseses2;
   }

   private static float[][][][] method6175(float[][][][] floatseseses) {
      int length = floatseseses.length;
      int n = floatseseses[0].length / 2;
      int n2 = floatseseses[0][0].length / 2;
      int n3 = floatseseses[0][0][0].length / 2;
      float[][][][] floatseseses2 = new float[length][n][n2][n3];

      for (int i = 0; i < length; i++) {
         for (int j = 0; j < n; j++) {
            for (int k = 0; k < n2; k++) {
               for (int m = 0; m < n3; m++) {
                  float f = -Float.MAX_VALUE;

                  for (int i2 = 0; i2 < 2; i2++) {
                     for (int i3 = 0; i3 < 2; i3++) {
                        for (int i4 = 0; i4 < 2; i4++) {
                           f = Math.max(f, floatseseses[i][2 * j + i2][2 * k + i3][2 * m + i4]);
                        }
                     }
                  }

                  floatseseses2[i][j][k][m] = f;
               }
            }
         }
      }

      return floatseseses2;
   }

   private static float[] method6176(float[][][][] floatseseses) {
      float[] floats = new float[floatseseses.length * floatseseses[0].length * floatseseses[0][0].length * floatseseses[0][0][0].length];
      int n = 0;

      for (float[][][] floatseses : floatseseses) {
         for (float[][] floatses : floatseses) {
            for (float[] floats2 : floatses) {
               for (float f : floats2) {
                  floats[n++] = f;
               }
            }
         }
      }

      return floats;
   }

   private static float[] method6177(float[]... floatses) {
      int n = 0;

      for (float[] floats : floatses) {
         n += floats.length;
      }

      float[] floats = new float[n];
      int n2 = 0;

      for (float[] floats2 : floatses) {
         System.arraycopy(floats2, 0, floats, n2, floats2.length);
         n2 += floats2.length;
      }

      return floats;
   }

   private static float[] method6178(float[] floats, float[] floatValues21, float[] floatValues22) {
      float f = 0.0F;

      for (float f2 : floats) {
         f += f2;
      }

      f /= floats.length;
      float f2 = 0.0F;

      for (float f3 : floats) {
         float f4 = f3 - f;
         f2 += f4 * f4;
      }

      f2 /= floats.length;
      float f3 = (float)(1.0 / Math.sqrt(f2 + 1.0E-5F));
      float[] floats2 = new float[floats.length];

      for (int i = 0; i < floats.length; i++) {
         floats2[i] = (floats[i] - f) * f3 * floatValues21[i] + floatValues22[i];
      }

      return floats2;
   }

   private static float[] method6179(float[] floats) {
      float[] floats2 = new float[floats.length];

      for (int i = 0; i < floats.length; i++) {
         floats2[i] = method6181(floats[i]);
      }

      return floats2;
   }

   private static float[][][][] method6180(float[][][][] floatseseses) {
      for (int i = 0; i < floatseseses.length; i++) {
         for (int j = 0; j < floatseseses[i].length; j++) {
            for (int k = 0; k < floatseseses[i][j].length; k++) {
               for (int m = 0; m < floatseseses[i][j][k].length; m++) {
                  floatseseses[i][j][k][m] = method6181(floatseseses[i][j][k][m]);
               }
            }
         }
      }

      return floatseseses;
   }

   private static float method6181(float f) {
      return 0.5F * f * (1.0F + method6182(f * 0.70710677F));
   }

   private static float method6182(float f) {
      float f2 = f < 0.0F ? -1.0F : 1.0F;
      double d = Math.abs((double)f);
      double d2 = 1.0 / (1.0 + 0.5 * d);
      double d3 = d2
         * Math.exp(
            -d * d
               - 1.26551223
               + d2
                  * (
                     1.00002368
                        + d2
                           * (
                              0.37409196
                                 + d2
                                    * (
                                       0.09678418
                                          + d2
                                             * (
                                                -0.18628806
                                                   + d2
                                                      * (0.27886807 + d2 * (-1.13520398 + d2 * (1.48851587 + d2 * (-0.82215223 + d2 * 0.17087277))))
                                             )
                                    )
                           )
                  )
         );
      return (float)(f2 * (1.0 - d3));
   }

   private static float[] method6183(float[][] floatValues14, float[] floatValues20, float[] floatValues4) {
      float[] floats = new float[floatValues14.length];

      for (int i = 0; i < floatValues14.length; i++) {
         float f = floatValues20[i];

         for (int j = 0; j < floatValues4.length; j++) {
            f += floatValues14[i][j] * floatValues4[j];
         }

         floats[i] = f;
      }

      return floats;
   }

   private static float method6184(float f) {
      return (float)(1.0 / (1.0 + Math.exp(-f)));
   }

   private static float clamp(float f, float f2, float f3) {
      return Math.max(f2, Math.min(f3, f));
   }

   private static int method6185(double d) {
      if (!Double.isFinite(d)) {
         return 0;
      } else if (d >= 2.147483647E9) {
         return Integer.MAX_VALUE;
      } else {
         return d <= -2.1474836E9F ? Integer.MIN_VALUE : (int)Math.round(d);
      }
   }

   private static void method6186(float[][][][] floatseseses, int n, int n2) {
      method6195(floatseseses != null && floatseseses.length == n, "wall channel mismatch");

      for (float[][][] floatseses : floatseseses) {
         method6195(floatseses.length == n2, "wall depth mismatch");

         for (float[][] floatses : floatseses) {
            method6195(floatses.length == n2, "wall height mismatch");

            for (float[] floats : floatses) {
               method6195(floats.length == n2, "wall width mismatch");
            }
         }
      }
   }

   private static JsonObject method6187(JsonObject json, String memberName) {
      JsonElement jsonElement = json.get(memberName);
      method6195(jsonElement != null && jsonElement.isJsonObject(), "missing object " + memberName);
      return jsonElement.getAsJsonObject();
   }

   private static JsonArray method6188(JsonObject json, String memberName) {
      JsonElement jsonElement = json.get(memberName);
      method6195(jsonElement != null && jsonElement.isJsonArray(), "missing array " + memberName);
      return jsonElement.getAsJsonArray();
   }

   private static String method6189(JsonObject json, String memberName) {
      JsonElement jsonElement = json.get(memberName);
      method6195(jsonElement != null && jsonElement.isJsonPrimitive(), "missing string " + memberName);
      return jsonElement.getAsString();
   }

   private static int method6190(JsonObject json, String memberName) {
      return Math.round(method6191(json, memberName));
   }

   private static float method6191(JsonObject json, String memberName) {
      JsonElement jsonElement = json.get(memberName);
      method6195(jsonElement != null && jsonElement.isJsonPrimitive(), "missing number " + memberName);
      float f = jsonElement.getAsFloat();
      method6195(Float.isFinite(f), "non-finite " + memberName);
      return f;
   }

   private static float[] method6192(JsonObject json, String memberName, int intValue) {
      JsonArray jsonArray = method6188(json, memberName);
      method6195(jsonArray.size() == intValue, memberName + " length mismatch");
      float[] floats = new float[intValue];

      for (int i = 0; i < intValue; i++) {
         floats[i] = jsonArray.get(i).getAsFloat();
         method6195(Float.isFinite(floats[i]), "non-finite " + memberName);
      }

      return floats;
   }

   private static float[][] method6193(JsonObject json, String memberName, int n, int intValue) {
      JsonArray jsonArray = method6188(json, memberName);
      method6195(jsonArray.size() == n, memberName + " rows mismatch");
      float[][] floatses = new float[n][intValue];

      for (int i = 0; i < n; i++) {
         JsonArray jsonArray2 = jsonArray.get(i).getAsJsonArray();
         method6195(jsonArray2.size() == intValue, memberName + " cols mismatch");

         for (int j = 0; j < intValue; j++) {
            floatses[i][j] = jsonArray2.get(j).getAsFloat();
         }
      }

      return floatses;
   }

   private static float[][][][][] method6194(JsonObject json, String memberName, int n, int n2, int n3, int n4, int n5) {
      JsonArray jsonArray = method6188(json, memberName);
      method6195(jsonArray.size() == n, memberName + " dim0");
      float[][][][][] floatseseseses = new float[n][n2][n3][n4][n5];

      for (int i = 0; i < n; i++) {
         JsonArray jsonArray2 = jsonArray.get(i).getAsJsonArray();
         method6195(jsonArray2.size() == n2, memberName + " dim1");

         for (int j = 0; j < n2; j++) {
            JsonArray jsonArray3 = jsonArray2.get(j).getAsJsonArray();
            method6195(jsonArray3.size() == n3, memberName + " dim2");

            for (int k = 0; k < n3; k++) {
               JsonArray jsonArray4 = jsonArray3.get(k).getAsJsonArray();
               method6195(jsonArray4.size() == n4, memberName + " dim3");

               for (int m = 0; m < n4; m++) {
                  JsonArray jsonArray5 = jsonArray4.get(m).getAsJsonArray();
                  method6195(jsonArray5.size() == n5, memberName + " dim4");

                  for (int i2 = 0; i2 < n5; i2++) {
                     floatseseseses[i][j][k][m][i2] = jsonArray5.get(i2).getAsFloat();
                  }
               }
            }
         }
      }

      return floatseseseses;
   }

   private static void method6195(boolean finite, String str) {
      if (!finite) {
         throw new IllegalArgumentException(str);
      }
   }

   public int getIntValue() {
      return this.intValue;
   }

   public int getIntValue11() {
      return this.intValue11;
   }

   public int getIntValue4() {
      return this.intValue4;
   }

   public int getIntValue12() {
      return this.intValue12;
   }

   public String getText3() {
      return this.text3;
   }

   public String getText4() {
      return this.text4;
   }

   public int getIntValue6() {
      return this.intValue6;
   }

   public int getIntValue8() {
      return this.intValue8;
   }

   public float getFloatValue6() {
      return this.floatValue6;
   }

   public float getFloatValue7() {
      return this.floatValue7;
   }

   public float getFloatValue8() {
      return this.floatValue8;
   }

   public int getIntValue13() {
      return this.intValue13;
   }

   public float getFloatValue9() {
      return this.floatValue9;
   }

   public static final class Data {
      private final float[] floatValues;
      private final float[] floatValues2;
      private final float[] floatValues3;
      private final float[] floatValues4;
      private final float[][] floatValues5;
      private final float[][] floatValues6;
      private final float[] floatValues7;

      public float[] floatValues() {
         return this.floatValues;
      }

      public float[] floatValues2() {
         return this.floatValues2;
      }

      public float[] floatValues3() {
         return this.floatValues3;
      }

      public float[] floatValues4() {
         return this.floatValues4;
      }

      public float[][] floatValues5() {
         return this.floatValues5;
      }

      public float[][] floatValues6() {
         return this.floatValues6;
      }

      public float[] floatValues7() {
         return this.floatValues7;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.floatValues, other.floatValues) && java.util.Objects.equals(this.floatValues2, other.floatValues2) && java.util.Objects.equals(this.floatValues3, other.floatValues3) && java.util.Objects.equals(this.floatValues4, other.floatValues4) && java.util.Objects.equals(this.floatValues5, other.floatValues5) && java.util.Objects.equals(this.floatValues6, other.floatValues6) && java.util.Objects.equals(this.floatValues7, other.floatValues7);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(floatValues, floatValues2, floatValues3, floatValues4, floatValues5, floatValues6, floatValues7);
      }

      @Override
      public String toString() {
         return "Data[" + "floatValues=" + this.floatValues + ", " + "floatValues2=" + this.floatValues2 + ", " + "floatValues3=" + this.floatValues3 + ", " + "floatValues4=" + this.floatValues4 + ", " + "floatValues5=" + this.floatValues5 + ", " + "floatValues6=" + this.floatValues6 + ", " + "floatValues7=" + this.floatValues7 + "]";
      }

      public Data(float[] floatValues, float[] floatValues2, float[] floatValues3, float[] floatValues4, float[][] floatValues5, float[][] floatValues6, float[] floatValues7) {
         this.floatValues = floatValues;
         this.floatValues2 = floatValues2;
         this.floatValues3 = floatValues3;
         this.floatValues4 = floatValues4;
         this.floatValues5 = floatValues5;
         this.floatValues6 = floatValues6;
         this.floatValues7 = floatValues7;
      }

      public float[] getFloatValues() {
         return this.floatValues;
      }

      public float[] getFloatValues2() {
         return this.floatValues2;
      }

      public float[] getFloatValues3() {
         return this.floatValues3;
      }

      public float[] getFloatValues4() {
         return this.floatValues4;
      }

      public float[][] getFloatValues5() {
         return this.floatValues5;
      }

      public float[][] getFloatValues6() {
         return this.floatValues6;
      }

      public float[] getFloatValues7() {
         return this.floatValues7;
      }
   }
}
