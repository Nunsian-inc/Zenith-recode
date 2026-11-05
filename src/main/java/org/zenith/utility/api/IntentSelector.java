package org.zenith.utility.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class IntentSelector {
   public static final int CONST_intValue = 3;
   public static final int CONST_intValue2 = 4;
   public static final float CONST_floatValue = -2.5F;
   public static final float CONST_floatValue2 = 0.7F;
   private static final int CONST_intValue3 = 0;
   private static final int CONST_intValue4 = 1;
   private static final int CONST_intValue5 = 2;
   public String[] texts;
   public int[] intValues;
   public float[] floatValues;
   public float[] std;
   public float floatValue;
   public int intValue;
   public int intValue2;
   public int intValue3;
   public int intValue4;
   private float[][] floatValues2;
   private float[] floatValues3;
   private float[] floatValues4;
   private float[] floatValues5;
   private float[][] floatValues6;
   private float[] floatValues7;
   private float[][] floatValues8;
   private float[] floatValues9;
   private float[][] floatValues10;
   private float[] floatValues11;
   private float[][] floatValues12;
   private float[] floatValues13;
   private float[][] floatValues14;
   private float[] floatValues15;
   private static final String text = "/assets/zenith/aimpipe/intent_selector.json";
   private static final Path path = Path.of("intent_selector.json");
   private static final Path[] paths = new Path[]{
      Path.of("..", "scripts", "motor_intents", "out", "intent_selector.json"), Path.of("scripts", "motor_intents", "out", "intent_selector.json")
   };

   public IntentSelector.Entry method7315(float[] floats) {
      float[] floats2 = new float[this.intValue];

      for (int i = 0; i < this.intValue; i++) {
         float sign = floats[i];

         sign = switch (this.intValues[i]) {
            case 1 -> method7316(sign);
            case 2 -> (float)Math.log1p(Math.max(sign, 0.0F));
            default -> sign;
         };
         floats2[i] = (sign - this.floatValues[i]) / this.std[i];
      }

      float[] floats3 = method7317(this.floatValues2, this.floatValues3, floats2);
      method7318(floats3, this.floatValues4, this.floatValues5);
      method7319(floats3);
      float[] floats4 = method7317(this.floatValues6, this.floatValues7, floats3);
      method7319(floats4);
      IntentSelector.Entry entry = new IntentSelector.Entry();
      entry.floatValue = method7317(this.floatValues8, this.floatValues9, floats4)[0];
      float[] floats5 = method7317(this.floatValues10, this.floatValues11, floats4);
      System.arraycopy(floats5, 0, entry.floatValues, 0, floats5.length);
      float[] floats6 = method7317(this.floatValues12, this.floatValues13, floats4);
      byte b = 9;
      entry.floatValues2 = new float[4][this.intValue3][b];
      int n = 0;

      for (int i = 0; i < 4; i++) {
         for (int j = 0; j < this.intValue3; j++) {
            for (int k = 0; k < b; k++) {
               entry.floatValues2[i][j][k] = floats6[n++];
            }
         }
      }

      float[] floats7 = method7317(this.floatValues14, this.floatValues15, floats4);
      entry.floatValues3 = new float[this.intValue4][3];
      n = 0;

      for (int i = 0; i < this.intValue4; i++) {
         for (int j = 0; j < 3; j++) {
            entry.floatValues3[i][j] = floats7[n++];
         }
      }

      return entry;
   }

   public static float method7316(float sign) {
      return (float)Math.copySign(Math.log1p(Math.abs(sign)), (double)sign);
   }

   private static float[] method7317(float[][] floatValues2, float[] floatValues3, float[] floats) {
      float[] floats2 = new float[floatValues2.length];

      for (int i = 0; i < floatValues2.length; i++) {
         float f = floatValues3[i];
         float[] floats3 = floatValues2[i];

         for (int j = 0; j < floats.length; j++) {
            f += floats3[j] * floats[j];
         }

         floats2[i] = f;
      }

      return floats2;
   }

   private static void method7318(float[] floats, float[] floatValues4, float[] floatValues5) {
      double d = 0.0;

      for (float f : floats) {
         d += f;
      }

      d /= floats.length;
      double d2 = 0.0;

      for (float f : floats) {
         double d3 = f - d;
         d2 += d3 * d3;
      }

      d2 /= floats.length;
      double d3 = 1.0 / Math.sqrt(d2 + 1.0E-5);

      for (int i = 0; i < floats.length; i++) {
         floats[i] = (float)((floats[i] - d) * d3 * floatValues4[i] + floatValues5[i]);
      }
   }

   private static void method7319(float[] floats) {
      for (int i = 0; i < floats.length; i++) {
         double d = floats[i];
         floats[i] = (float)(0.5 * d * (1.0 + method7320(d / Math.sqrt(2.0))));
      }
   }

   private static double method7320(double d) {
      double d2 = Math.signum(d);
      double d3 = Math.abs(d);
      double d4 = 1.0 / (1.0 + 0.3275911 * d3);
      double d5 = 1.0
         - ((((1.061405429 * d4 - 1.453152027) * d4 + 1.421413741) * d4 - 0.284496736) * d4 + 0.254829592) * d4 * Math.exp(-d3 * d3);
      return d2 * d5;
   }

   public static IntentSelector method7321() {
      IntentSelector intentSelector = method7322(path);
      if (intentSelector != null) {
         return intentSelector;
      } else {
         for (Path path2 : paths) {
            intentSelector = method7322(path2);
            if (intentSelector != null) {
               return intentSelector;
            }
         }

         return method7323();
      }
   }

   private static IntentSelector method7322(Path path) {
      try {
         return !Files.isRegularFile(path) ? null : method7324(Files.readString(path, StandardCharsets.UTF_8), path.toAbsolutePath().toString());
      } catch (Throwable e) {
         System.err.println("[MotorIntent] failed to load " + path + ": " + e);
         return null;
      }
   }

   private static IntentSelector method7323() {
      try {
         IntentSelector intentSelector;
         try (InputStream resourceAsStream = IntentSelector.class.getResourceAsStream("/assets/zenith/aimpipe/intent_selector.json")) {
            if (resourceAsStream == null) {
               return null;
            }

            intentSelector = method7324(new String(resourceAsStream.readAllBytes(), StandardCharsets.UTF_8), "/assets/zenith/aimpipe/intent_selector.json");
         }

         return intentSelector;
      } catch (Throwable e) {
         System.err.println("[MotorIntent] failed to load /assets/zenith/aimpipe/intent_selector.json: " + e);
         return null;
      }
   }

   private static IntentSelector method7324(String string, String str) {
      JsonObject jsonObject = JsonParser.parseString(string).getAsJsonObject();
      if (!"motor_intent_selector_mlp".equals(jsonObject.get("model").getAsString())) {
         throw new IllegalStateException("unexpected selector model");
      } else if (jsonObject.get("format_version").getAsInt() != 3) {
         throw new IllegalStateException("unsupported selector format_version " + jsonObject.get("format_version").getAsInt() + " (need 3)");
      } else {
         JsonObject json = jsonObject.getAsJsonObject("architecture");
         IntentSelector intentSelector = new IntentSelector();
         intentSelector.intValue = json.get("input_dim").getAsInt();
         intentSelector.intValue2 = json.get("hidden").getAsInt();
         intentSelector.intValue3 = json.get("mixture_components").getAsInt();
         intentSelector.intValue4 = json.get("rest_mixture_components").getAsInt();
         JsonArray jsonArray = json.getAsJsonArray("intents");
         if (jsonArray.size() != 4) {
            throw new IllegalStateException("selector intent taxonomy mismatch");
         } else {
            for (int i = 0; i < jsonArray.size(); i++) {
               if (!IntentLibraryRt.texts[i].equals(jsonArray.get(i).getAsString())) {
                  throw new IllegalStateException("selector intent order mismatch");
               }
            }

            if (json.getAsJsonArray("param_names").size() != 4) {
               throw new IllegalStateException("selector param count mismatch");
            } else {
               intentSelector.floatValue = jsonObject.get("move_threshold").getAsFloat();
               JsonArray jsonArray2 = jsonObject.getAsJsonArray("feature_spec");
               if (jsonArray2.size() != intentSelector.intValue) {
                  throw new IllegalStateException("feature spec size mismatch");
               } else {
                  intentSelector.texts = new String[jsonArray2.size()];
                  intentSelector.intValues = new int[jsonArray2.size()];
                  intentSelector.floatValues = new float[jsonArray2.size()];
                  intentSelector.std = new float[jsonArray2.size()];

                  for (int i = 0; i < jsonArray2.size(); i++) {
                     JsonObject jsonObject2 = jsonArray2.get(i).getAsJsonObject();
                     intentSelector.texts[i] = jsonObject2.get("name").getAsString();
                     String string2 = jsonObject2.get("transform").getAsString();
                     int[] intValues = intentSelector.intValues;

                     intValues[i] = switch (string2) {
                        case "signed_log" -> 1;
                        case "log1p" -> 2;
                        case "linear" -> 0;
                        default -> throw new IllegalStateException("unknown transform " + string2);
                     };
                     intentSelector.floatValues[i] = jsonObject2.get("mean").getAsFloat();
                     intentSelector.std[i] = jsonObject2.get("std").getAsFloat();
                  }

                  JsonObject jsonObject2 = jsonObject.getAsJsonObject("weights");
                  intentSelector.floatValues2 = method7325(jsonObject2, "backbone.0.weight");
                  intentSelector.floatValues3 = method7326(jsonObject2, "backbone.0.bias");
                  intentSelector.floatValues4 = method7326(jsonObject2, "backbone.1.weight");
                  intentSelector.floatValues5 = method7326(jsonObject2, "backbone.1.bias");
                  intentSelector.floatValues6 = method7325(jsonObject2, "backbone.4.weight");
                  intentSelector.floatValues7 = method7326(jsonObject2, "backbone.4.bias");
                  intentSelector.floatValues8 = method7325(jsonObject2, "move_head.weight");
                  intentSelector.floatValues9 = method7326(jsonObject2, "move_head.bias");
                  intentSelector.floatValues10 = method7325(jsonObject2, "intent_head.weight");
                  intentSelector.floatValues11 = method7326(jsonObject2, "intent_head.bias");
                  intentSelector.floatValues12 = method7325(jsonObject2, "params_head.weight");
                  intentSelector.floatValues13 = method7326(jsonObject2, "params_head.bias");
                  intentSelector.floatValues14 = method7325(jsonObject2, "rest_head.weight");
                  intentSelector.floatValues15 = method7326(jsonObject2, "rest_head.bias");
                  int n = 4 * intentSelector.intValue3 * 9;
                  if (intentSelector.floatValues12.length != n) {
                     throw new IllegalStateException("params head size mismatch: " + intentSelector.floatValues12.length + " vs " + n);
                  } else if (intentSelector.floatValues14.length != intentSelector.intValue4 * 3) {
                     throw new IllegalStateException("rest head size mismatch");
                  } else if (intentSelector.floatValues2[0].length == intentSelector.intValue && intentSelector.floatValues2.length == intentSelector.intValue2) {
                     System.out
                        .println(
                           "[MotorIntent] loaded selector "
                              + str
                              + " (in="
                              + intentSelector.intValue
                              + ", hidden="
                              + intentSelector.intValue2
                              + ", mixture="
                              + intentSelector.intValue3
                              + ", threshold="
                              + intentSelector.floatValue
                              + ")"
                        );
                     return intentSelector;
                  } else {
                     throw new IllegalStateException("backbone.0 shape mismatch");
                  }
               }
            }
         }
      }
   }

   private static float[][] method7325(JsonObject json, String memberName) {
      JsonArray jsonArray = json.getAsJsonArray(memberName);
      float[][] floatses = new float[jsonArray.size()][];

      for (int i = 0; i < jsonArray.size(); i++) {
         JsonArray jsonArray2 = jsonArray.get(i).getAsJsonArray();
         floatses[i] = new float[jsonArray2.size()];

         for (int j = 0; j < jsonArray2.size(); j++) {
            floatses[i][j] = jsonArray2.get(j).getAsFloat();
         }
      }

      return floatses;
   }

   private static float[] method7326(JsonObject json, String memberName) {
      JsonArray jsonArray = json.getAsJsonArray(memberName);
      float[] floats = new float[jsonArray.size()];

      for (int i = 0; i < jsonArray.size(); i++) {
         floats[i] = jsonArray.get(i).getAsFloat();
      }

      return floats;
   }

   public static final class Entry {
      public float floatValue;
      public final float[] floatValues = new float[4];
      public float[][][] floatValues2;
      public float[][] floatValues3;
   }
}
