package org.zenith.utility.api;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import org.zenith.ChatLogger;
import org.zenith.Zenith;

public class JointCodebook {
   private static final String[] texts = new String[]{
      "rotation_model_v22_2_joint_codebook.json", "rotation_model_v22_1_joint_codebook.json", "rotation_model_v22_joint_codebook.json"
   };
   private static final int CONST_intValue = 22;
   private static final String text = "joint_nonuniform_codebook_residual_rotation_policy_v22";
   private static final String text2 = "joint_nonuniform_codebook_residual_rotation_policy_v22_1";
   private static final String text3 = "joint_nonuniform_codebook_class_conditioned_residual_rotation_policy_v22_2";
   private static final String text4 = "joint_codebook_plus_residual_count_policy";
   private static final String text5 = "joint_codebook_plus_class_conditioned_residual_count_policy";
   private static final float CONST_floatValue = 0.5F;
   private boolean loaded = false;
   private JointCodebook.Entry9 entry9;
   private int intValue = 24;
   private final Deque<float[]> deque = new ArrayDeque<>();

   private boolean method2524() {
      try {
         return Zenith.getInstance().getEntry().getUsername().equals("Bogdan");
      } catch (Throwable e) {
         return false;
      }
   }

   private void method2525(String absolutePath) {
      if (this.method2524()) {
         ChatLogger.method2019(absolutePath);
      }
   }

   private void method2526(String x) {
      if (this.method2524()) {
         System.out.println(x);
      }
   }

   private void method2527(String x) {
      if (this.method2524()) {
         System.err.println(x);
      }
   }

   private void method2528(Exception e) {
      if (this.method2524()) {
         e.printStackTrace();
      }
   }

   public void method2529() {
      this.loaded = false;
      this.entry9 = null;
      this.method2536();

      try {
         for (File neuroFile : this.method2530()) {
            if (this.method2531(neuroFile)) {
               this.method2525(neuroFile.getAbsolutePath());
               return;
            }
         }

         for (String text : texts) {
            if (this.method2532("models/" + text)) {
               this.method2525("jar:models/" + text);
               return;
            }
         }

         this.method2527("[DeepLearning] No V22 joint-codebook rotation model found. Checked only V22 filenames; no v17 fallback is attempted.");
      } catch (Exception e) {
         this.method2527("[DeepLearning] Failed to load V22 rotation model: " + e.getMessage());
         this.method2528(e);
      }
   }

   private File[] method2530() {
      String[] strings = new String[]{"models", "run/models", "scripts", "../models", "../scripts", ""};
      ArrayList arrayList = new ArrayList();

      for (String parent : strings) {
         for (String text : texts) {
            arrayList.add(parent.isEmpty() ? new File(text) : new File(parent, text));
         }
      }

      return (File[])arrayList.toArray(new File[0]);
   }

   private boolean method2531(File neuroFile) {
      if (neuroFile != null && neuroFile.exists() && neuroFile.isFile()) {
         try {
            this.method2533(neuroFile);
            return true;
         } catch (RuntimeException | IOException e) {
            this.method2527("[DeepLearning] Failed V22 candidate " + neuroFile.getPath() + ": " + e.getMessage());
            return false;
         }
      } else {
         return false;
      }
   }

   private boolean method2532(String name) {
      InputStream resourceAsStream = JointCodebook.class.getClassLoader().getResourceAsStream(name);
      if (resourceAsStream == null) {
         return false;
      } else {
         try {
            InputStream inputStream = resourceAsStream;

            boolean flag;
            try {
               this.method2526("[DeepLearning] Loading V22 rotation model from jar: " + name);
               this.method2534(resourceAsStream);
               flag = true;
            } catch (Throwable e) {
               if (resourceAsStream != null) {
                  try {
                     inputStream.close();
                  } catch (Throwable ex) {
                     e.addSuppressed(ex);
                  }
               }

               throw e;
            }

            if (resourceAsStream != null) {
               resourceAsStream.close();
            }

            return flag;
         } catch (RuntimeException | IOException e) {
            this.method2527("[DeepLearning] Failed V22 resource " + name + ": " + e.getMessage());
            return false;
         }
      }
   }

   public void method2533(File neuroFile) throws IOException {
      if (neuroFile != null && neuroFile.exists() && neuroFile.isFile()) {
         try (FileInputStream fileInputStream = new FileInputStream(neuroFile)) {
            this.method2526("[DeepLearning] Loading V22 rotation model from: " + neuroFile.getPath());
            this.method2534(fileInputStream);
         }
      } else {
         throw new IOException("Model file not found");
      }
   }

   public void method2534(InputStream resourceAsStream) throws IOException {
      String str = new String(resourceAsStream.readAllBytes(), StandardCharsets.UTF_8);
      if (!this.method2591(str)) {
         throw new IOException("Unsupported V22 rotation model payload format");
      } else {
         this.method2535(str);
      }
   }

   private void method2535(String str) {
      JsonObject jsonObject = (JsonObject)new Gson().fromJson(str, JsonObject.class);
      JointCodebook.Entry9 entry9 = this.method2560(jsonObject);
      this.entry9 = entry9;
      this.intValue = Math.max(1, entry9.intValue2);
      this.loaded = true;
      this.method2536();
      this.method2526("[DeepLearning] V22 model loaded successfully: " + entry9.text);
      this.method2526("[DeepLearning] Input: " + this.method2537() + ", sequence: " + this.intValue + ", horizon: " + entry9.intValue3);
      this.method2526(
         "[DeepLearning] Hidden: " + entry9.intValue5 + ", FC: " + entry9.intValue7 + ", layers: " + entry9.intValue6 + ", joint codes: " + entry9.intValue8
      );
      this.method2526("[DeepLearning] Decode: " + entry9.text3 + ", topk=" + entry9.intValue9 + ", class-conditioned residual=" + entry9.flag);
   }

   public void method2536() {
      this.deque.clear();
   }

   public boolean isLoaded() {
      return this.loaded;
   }

   public int method2537() {
      return this.entry9 == null ? -1 : this.entry9.intValue4;
   }

   public int method2538() {
      return this.method2537();
   }

   public String[] method2539() {
      return this.entry9 != null && this.entry9.texts != null ? (String[])this.entry9.texts.clone() : new String[0];
   }

   public float method2540() {
      return this.entry9 == null ? 0.0F : this.entry9.floatValue5;
   }

   public float method2541() {
      return this.entry9 != null && this.entry9.entry != null ? this.entry9.entry.floatValue : 850.0F;
   }

   public float method2542() {
      return this.entry9 != null && this.entry9.entry != null ? this.entry9.entry.floatValue2 : 650.0F;
   }

   public float method2543() {
      return this.entry9 != null && this.entry9.entry != null ? this.entry9.entry.floatValue3 : 250.0F;
   }

   public float method2544() {
      return this.entry9 != null && this.entry9.entry != null ? this.entry9.entry.floatValue4 : 0.6F;
   }

   public boolean method2545() {
      return this.loaded && this.entry9 != null && this.method2568(this.entry9.text);
   }

   public JointCodebook.Entry7 method2546(float[] floatValues, float floatValue, float f) {
      if (this.method2545() && floatValues != null) {
         if (floatValues.length != this.entry9.intValue4) {
            this.method2527("[DeepLearning] Invalid V22 feature count: " + floatValues.length + ", expected " + this.entry9.intValue4);
            return null;
         } else if (this.method2588(floatValues) && this.isFinite(floatValue) && this.isFinite(f)) {
            this.deque.addLast((float[])floatValues.clone());
            this.method2557();
            float[] floats = this.method2554(this.method2556());
            return floats != null && this.method2588(floats) ? this.method2547(floats, floatValue, f) : null;
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   private JointCodebook.Entry7 method2547(float[] floats, float floatValue, float f) {
      float[] floatValues = this.method2559(floats, this.entry9.entry83.floatValues, this.entry9.entry83.floatValues2);
      if (floatValues.length >= this.entry9.intValue8 && this.method2588(floatValues)) {
         float[] floats2 = new float[this.entry9.intValue8];
         System.arraycopy(floatValues, 0, floats2, 0, this.entry9.intValue8);
         float[] floatValues2 = this.method2559(floats, this.entry9.entry84.floatValues, this.entry9.entry84.floatValues2);
         if (!this.method2588(floatValues2)) {
            return null;
         } else {
            JointCodebook.Entry2 entry2 = this.entry9.flag ? this.method2549(floats2, floatValues2) : this.method2548(floats2, floatValues2);
            if (entry2 != null && this.method2588(entry2.floatValues) && this.method2588(entry2.floatValues2) && this.method2588(entry2.floatValues3)) {
               float[] floats3 = (float[])entry2.floatValues.clone();
               this.method2553(floats3);
               float floatValue2 = floats3[0] * this.entry9.floatValue5;
               float floatValue3 = floats3[1] * this.entry9.floatValue5;
               if (this.isFinite(floatValue2) && this.isFinite(floatValue3)) {
                  float f2 = this.method2589((Math.abs(floats3[0]) - this.entry9.floatValue6) * 3.0F);
                  float f3 = this.method2589((Math.abs(floats3[1]) - this.entry9.floatValue6) * 3.0F);
                  float f4 = this.method2589((Math.max(Math.abs(floats3[0]), Math.abs(floats3[1])) - this.entry9.floatValue6) * 3.0F);
                  return new JointCodebook.Entry7(
                     floatValue2,
                     floatValue3,
                     floatValue2 - floatValue,
                     floatValue3 - f,
                     f2,
                     f3,
                     f4,
                     entry2.floatValues2[0],
                     entry2.floatValues2[1],
                     entry2.floatValues3[0],
                     entry2.floatValues3[1],
                     floats3[0],
                     floats3[1]
                  );
               } else {
                  return null;
               }
            } else {
               return null;
            }
         }
      } else {
         return null;
      }
   }

   private JointCodebook.Entry2 method2548(float[] floats, float[] floats2) {
      if (floats2.length < 2) {
         return null;
      } else {
         JointCodebook.Entry3 entry3 = this.method2551(floats);
         if (entry3 == null) {
            return null;
         } else {
            float[] floats3 = this.method2552(entry3.floatValues2);
            float[] floatValues3 = new float[]{this.tanh(floats2[0]) * floats3[0], this.tanh(floats2[1]) * floats3[1]};
            return new JointCodebook.Entry2(new float[]{entry3.floatValues[0] + floatValues3[0], entry3.floatValues[1] + floatValues3[1]}, entry3.floatValues, floatValues3, entry3.floatValue);
         }
      }
   }

   private JointCodebook.Entry2 method2549(float[] floats, float[] floats2) {
      if (floats2.length < this.entry9.intValue8 * 2) {
         return null;
      } else {
         float[] floats3 = this.method2585(floats, this.entry9.floatValue);
         if (floats3 == null) {
            return null;
         } else {
            String anObject = this.entry9.text3;
            if ("argmax".equals(anObject) || "sample".equals(anObject)) {
               int n = this.method2586(floats);
               float[] floatValues3 = this.method2550(floats2, n);
               float[] floatValues2 = (float[])this.entry9.floatValues[n].clone();
               return new JointCodebook.Entry2(new float[]{floatValues2[0] + floatValues3[0], floatValues2[1] + floatValues3[1]}, floatValues2, floatValues3, floats3[n]);
            } else if ("topk".equals(anObject)) {
               int n = Math.min(Math.max(1, this.entry9.intValue9), this.entry9.intValue8);
               int[] ints = this.method2587(floats, n);
               float[] floats4 = new float[ints.length];

               for (int i = 0; i < ints.length; i++) {
                  floats4[i] = floats[ints[i]];
               }

               float[] floats5 = this.method2585(floats4, this.entry9.floatValue);
               if (floats5 == null) {
                  return null;
               } else {
                  float[] floatValues = new float[2];
                  float[] floatValues2 = new float[2];
                  float floatValue = 0.0F;

                  for (int i = 0; i < ints.length; i++) {
                     int n2 = ints[i];
                     float[] floats6 = this.method2550(floats2, n2);
                     float f = floats5[i];
                     floatValues2[0] += this.entry9.floatValues[n2][0] * f;
                     floatValues2[1] += this.entry9.floatValues[n2][1] * f;
                     floatValues[0] += (this.entry9.floatValues[n2][0] + floats6[0]) * f;
                     floatValues[1] += (this.entry9.floatValues[n2][1] + floats6[1]) * f;
                     floatValue = Math.max(floatValue, floats3[n2]);
                  }

                  return new JointCodebook.Entry2(floatValues, floatValues2, new float[]{floatValues[0] - floatValues2[0], floatValues[1] - floatValues2[1]}, floatValue);
               }
            } else {
               float[] floatValues = new float[2];
               float[] floatValues2 = new float[2];
               float floatValue = 0.0F;

               for (int i = 0; i < this.entry9.intValue8; i++) {
                  float f = floats3[i];
                  float[] floats4 = this.method2550(floats2, i);
                  floatValues2[0] += this.entry9.floatValues[i][0] * f;
                  floatValues2[1] += this.entry9.floatValues[i][1] * f;
                  floatValues[0] += (this.entry9.floatValues[i][0] + floats4[0]) * f;
                  floatValues[1] += (this.entry9.floatValues[i][1] + floats4[1]) * f;
                  floatValue = Math.max(floatValue, f);
               }

               return new JointCodebook.Entry2(floatValues, floatValues2, new float[]{floatValues[0] - floatValues2[0], floatValues[1] - floatValues2[1]}, floatValue);
            }
         }
      }
   }

   private float[] method2550(float[] floats, int n) {
      float[] floats2 = this.method2552(this.entry9.floatValues2[n]);
      int n2 = n * 2;
      return new float[]{this.tanh(floats[n2]) * floats2[0], this.tanh(floats[n2 + 1]) * floats2[1]};
   }

   private JointCodebook.Entry3 method2551(float[] floats) {
      float[] floats2 = this.method2585(floats, this.entry9.floatValue);
      if (floats2 == null) {
         return null;
      } else {
         String anObject = this.entry9.text3;
         if ("argmax".equals(anObject) || "sample".equals(anObject)) {
            int n = this.method2586(floats);
            return new JointCodebook.Entry3((float[])this.entry9.floatValues[n].clone(), (float[])this.entry9.floatValues2[n].clone(), floats2[n]);
         } else if ("topk".equals(anObject)) {
            int n = Math.min(Math.max(1, this.entry9.intValue9), this.entry9.intValue8);
            int[] ints = this.method2587(floats, n);
            float[] floats3 = new float[ints.length];

            for (int i = 0; i < ints.length; i++) {
               floats3[i] = floats[ints[i]];
            }

            float[] floats4 = this.method2585(floats3, this.entry9.floatValue);
            if (floats4 == null) {
               return null;
            } else {
               float[] floatValues = new float[2];
               float[] floatValues2 = new float[2];
               float floatValue = 0.0F;

               for (int i = 0; i < ints.length; i++) {
                  int n2 = ints[i];
                  float f = floats4[i];
                  floatValues[0] += this.entry9.floatValues[n2][0] * f;
                  floatValues[1] += this.entry9.floatValues[n2][1] * f;
                  floatValues2[0] += this.entry9.floatValues2[n2][0] * f;
                  floatValues2[1] += this.entry9.floatValues2[n2][1] * f;
                  floatValue = Math.max(floatValue, floats2[n2]);
               }

               return new JointCodebook.Entry3(floatValues, floatValues2, floatValue);
            }
         } else {
            float[] floatValues = new float[2];
            float[] floatValues2 = new float[2];
            float floatValue = 0.0F;

            for (int i = 0; i < this.entry9.intValue8; i++) {
               float f = floats2[i];
               floatValues[0] += this.entry9.floatValues[i][0] * f;
               floatValues[1] += this.entry9.floatValues[i][1] * f;
               floatValues2[0] += this.entry9.floatValues2[i][0] * f;
               floatValues2[1] += this.entry9.floatValues2[i][1] * f;
               floatValue = Math.max(floatValue, f);
            }

            return new JointCodebook.Entry3(floatValues, floatValues2, floatValue);
         }
      }
   }

   private float[] method2552(float[] floatValues2) {
      return new float[]{
         this.clamp(floatValues2[0] * this.entry9.floatValue2, this.entry9.floatValue3, this.entry9.floatValue4),
         this.clamp(floatValues2[1] * this.entry9.floatValue2, this.entry9.floatValue3, this.entry9.floatValue4)
      };
   }

   private void method2553(float[] floats) {
      if (floats != null && floats.length >= 2 && this.entry9.entry5 != null && this.entry9.entry5.enabled) {
         floats[0] = this.clamp(floats[0], -this.entry9.entry5.floatValues[0], this.entry9.entry5.floatValues[0]);
         floats[1] = this.clamp(floats[1], -this.entry9.entry5.floatValues[1], this.entry9.entry5.floatValues[1]);
      }
   }

   private float[] method2554(float[][] floatses) {
      if (floatses != null && this.entry9 != null && this.entry9.entry6 != null) {
         float[][] floatses2 = new float[floatses.length][];

         for (int i = 0; i < floatses.length; i++) {
            floatses2[i] = this.method2555(floatses[i], this.entry9.entry6);
         }

         for (int i = 0; i < this.entry9.intValue6; i++) {
            JointCodebook.Entry4 entry4 = this.entry9.entry4s[i];
            float[] floats = new float[this.entry9.intValue5];
            float[][] floatses3 = new float[floatses.length][this.entry9.intValue5];

            for (int j = 0; j < floatses.length; j++) {
               floats = this.method2558(floatses2[j], floats, entry4, this.entry9.intValue5);
               floatses3[j] = (float[])floats.clone();
            }

            floatses2 = floatses3;
         }

         float[] floats = floatses2[floatses2.length - 1];
         float[] floats2 = this.method2590(this.method2559(floats, this.entry9.entry8.floatValues, this.entry9.entry8.floatValues2));
         return this.method2590(this.method2559(floats2, this.entry9.entry82.floatValues, this.entry9.entry82.floatValues2));
      } else {
         return null;
      }
   }

   private float[] method2555(float[] floats, JointCodebook.Entry6 entry6) {
      float[] floats2 = new float[floats.length];

      for (int i = 0; i < floats.length; i++) {
         float f = Math.max(Math.abs(entry6.floatValues2[i]), 1.0E-6F);
         floats2[i] = (floats[i] - entry6.floatValues[i]) / f;
      }

      return floats2;
   }

   private float[][] method2556() {
      float[][] floatses = new float[this.intValue][this.entry9.intValue4];
      if (this.deque.isEmpty()) {
         return floatses;
      } else {
         float[] peekFirst = this.deque.peekFirst();
         int n = this.intValue - this.deque.size();

         for (int i = 0; i < n; i++) {
            floatses[i] = (float[])peekFirst.clone();
         }

         int n2 = Math.max(0, n);

         for (float[] floats : this.deque) {
            if (n2 >= floatses.length) {
               break;
            }

            floatses[n2++] = (float[])floats.clone();
         }

         return floatses;
      }
   }

   private void method2557() {
      while (this.deque.size() > this.intValue) {
         this.deque.removeFirst();
      }
   }

   private float[] method2558(float[] floats, float[] floats2, JointCodebook.Entry4 entry4, int n) {
      float[] floats3 = this.method2559(floats, entry4.floatValues, entry4.floatValues3);
      float[] floats4 = this.method2559(floats2, entry4.floatValues2, entry4.floatValues4);
      float[] floats5 = new float[n];

      for (int i = 0; i < n; i++) {
         float f = this.method2589(floats3[i] + floats4[i]);
         float f2 = this.method2589(floats3[n + i] + floats4[n + i]);
         float tanh = (float)Math.tanh(floats3[n * 2 + i] + f * floats4[n * 2 + i]);
         floats5[i] = (1.0F - f2) * tanh + f2 * floats2[i];
      }

      return floats5;
   }

   private float[] method2559(float[] floats, float[][] floatValues, float[] floatValues2) {
      int length = floatValues.length;
      float[] floats2 = new float[length];

      for (int i = 0; i < length; i++) {
         float f = floatValues2 != null && i < floatValues2.length ? floatValues2[i] : 0.0F;
         float[] floats3 = floatValues[i];
         int n = Math.min(floats.length, floats3.length);

         for (int j = 0; j < n; j++) {
            f += floats3[j] * floats[j];
         }

         floats2[i] = f;
      }

      return floats2;
   }

   private JointCodebook.Entry9 method2560(JsonObject jsonObject) {
      if (jsonObject == null) {
         throw new IllegalArgumentException("Model JSON is empty");
      } else {
         String text = this.method2575(jsonObject, "model_type");
         if (!this.method2568(text)) {
            throw new IllegalArgumentException("Unsupported V22 model_type: " + text);
         } else {
            int intValue = this.method2577(jsonObject, "schema_version");
            if (intValue != 22) {
               throw new IllegalArgumentException("Unsupported V22 model schema_version: " + intValue);
            } else {
               String anObject = this.method2575(jsonObject, "output_mode");
               if (!"joint_codebook_plus_residual_count_policy".equals(anObject) && !"joint_codebook_plus_class_conditioned_residual_count_policy".equals(anObject)) {
                  throw new IllegalArgumentException("Unsupported V22 output_mode: " + anObject);
               } else {
                  JsonObject json = this.method2570(jsonObject, "normalization");
                  JsonObject jsonObject2 = this.method2570(jsonObject, "architecture");
                  JsonObject jsonObject3 = this.method2570(jsonObject, "policy_config");
                  JsonObject jsonObject4 = this.method2570(jsonObject, "state_dict");
                  int n = this.method2577(jsonObject3, "schema_version");
                  if (n != 22) {
                     throw new IllegalArgumentException("Unsupported policy_config schema_version: " + n);
                  } else {
                     JointCodebook.Entry9 entry9 = new JointCodebook.Entry9();
                     entry9.text = text;
                     entry9.text2 = anObject;
                     entry9.intValue = intValue;
                     entry9.intValue2 = this.method2577(jsonObject, "window");
                     entry9.intValue3 = this.method2577(jsonObject, "future_horizon");
                     entry9.intValue4 = this.method2577(jsonObject2, "input_size");
                     entry9.intValue5 = this.method2577(jsonObject2, "hidden");
                     entry9.intValue6 = this.method2577(jsonObject2, "layers");
                     entry9.intValue7 = this.method2577(jsonObject2, "fc");
                     entry9.intValue8 = this.method2577(jsonObject2, "joint_codes");
                     entry9.floatValue = Math.max(0.05F, this.method2580(jsonObject2, "joint_temperature", 1.0F));
                     entry9.text3 = this.method2569(this.method2576(jsonObject2, "joint_inference_mode", "topk"));
                     entry9.intValue9 = Math.max(1, this.method2578(jsonObject2, "joint_inference_topk", 5));
                     entry9.floatValue2 = Math.max(0.0F, this.method2580(jsonObject2, "residual_width_multiplier", 1.0F));
                     entry9.floatValue3 = Math.max(0.0F, this.method2580(jsonObject2, "residual_min_count", 0.15F));
                     entry9.floatValue4 = Math.max(entry9.floatValue3, this.method2580(jsonObject2, "residual_max_count", 24.0F));
                     entry9.texts = this.method2582(this.method2572(jsonObject, "feature_columns"));
                     entry9.texts2 = this.method2582(this.method2572(jsonObject, "target_columns"));
                     entry9.floatValue5 = this.method2579(jsonObject, "mouse_gcd");
                     entry9.floatValue6 = this.method2580(jsonObject3, "move_threshold", 0.5F);
                     entry9.flag = "joint_codebook_plus_class_conditioned_residual_count_policy".equals(anObject)
                        || "joint_nonuniform_codebook_class_conditioned_residual_rotation_policy_v22_2".equals(text);
                     if (this.isFinite(entry9.floatValue5) && !(entry9.floatValue5 <= 0.0F)) {
                        if (entry9.texts.length != entry9.intValue4) {
                           throw new IllegalArgumentException("Model feature_columns size mismatch: " + entry9.texts.length + " != " + entry9.intValue4);
                        } else if (entry9.intValue8 <= 0) {
                           throw new IllegalArgumentException("Model architecture has invalid joint_codes");
                        } else {
                           entry9.entry6 = this.method2564(json);
                           if (entry9.entry6.floatValues.length == entry9.intValue4 && entry9.entry6.floatValues2.length == entry9.intValue4) {
                              JsonObject jsonObject5 = this.method2570(jsonObject3, "joint_codebook");
                              entry9.floatValues = this.method2584(this.method2572(jsonObject5, "centers"));
                              entry9.floatValues2 = this.method2584(this.method2572(jsonObject5, "residual_limits"));
                              entry9.floatValues3 = this.method2583(this.method2573(jsonObject5, "scales"));
                              if (entry9.floatValues.length == entry9.intValue8 && entry9.floatValues2.length == entry9.intValue8) {
                                 for (int i = 0; i < entry9.intValue8; i++) {
                                    if (entry9.floatValues[i].length != 2 || entry9.floatValues2[i].length != 2) {
                                       throw new IllegalArgumentException("joint_codebook centers/residual_limits must be [K][2]");
                                    }
                                 }

                                 entry9.entry4s = this.method2562(this.method2572(jsonObject4, "gru_layers"), entry9.intValue6);
                                 entry9.entry8 = this.method2563(this.method2570(jsonObject4, "fc1"));
                                 entry9.entry82 = this.method2563(this.method2570(jsonObject4, "fc2"));
                                 entry9.entry83 = this.method2563(this.method2570(jsonObject4, "joint_code_head"));
                                 entry9.entry84 = this.method2563(this.method2570(jsonObject4, "count_residual_head"));
                                 int n2 = entry9.flag ? entry9.intValue3 * entry9.intValue8 * 2 : entry9.intValue3 * 2;
                                 if (entry9.entry84.floatValues2.length == entry9.intValue3 * entry9.intValue8 * 2) {
                                    entry9.flag = true;
                                    n2 = entry9.intValue3 * entry9.intValue8 * 2;
                                 }

                                 if (entry9.entry83.floatValues2.length < entry9.intValue3 * entry9.intValue8) {
                                    throw new IllegalArgumentException("joint_code_head output is too small");
                                 } else if (entry9.entry84.floatValues2.length < n2) {
                                    throw new IllegalArgumentException("count_residual_head output is too small for this V22 model");
                                 } else {
                                    entry9.entry5 = this.method2565(this.method2571(jsonObject3, "runtime_safety"));
                                    entry9.entry = this.method2566(this.method2571(jsonObject3, "runtime_feature_builder"));
                                    this.method2561(entry9);
                                    return entry9;
                                 }
                              } else {
                                 throw new IllegalArgumentException("joint_codebook count mismatch");
                              }
                           } else {
                              throw new IllegalArgumentException("V22 normalization size mismatch");
                           }
                        }
                     } else {
                        throw new IllegalArgumentException("Model config has invalid mouse_gcd");
                     }
                  }
               }
            }
         }
      }
   }

   private void method2561(JointCodebook.Entry9 entry9) {
      if (entry9.entry8.floatValues.length != entry9.intValue7 || entry9.entry82.floatValues.length != entry9.intValue7) {
         throw new IllegalArgumentException("FC layer output size mismatch");
      } else if (entry9.entry83.floatValues.length < entry9.intValue3 * entry9.intValue8) {
         throw new IllegalArgumentException("joint_code_head weight output size mismatch");
      } else if (entry9.intValue5 <= 0 || entry9.intValue7 <= 0 || entry9.intValue6 <= 0) {
         throw new IllegalArgumentException("Invalid V22 architecture sizes");
      }
   }

   private JointCodebook.Entry4[] method2562(JsonArray jsonArray, int n) {
      if (jsonArray.size() != n) {
         throw new IllegalArgumentException("gru_layers count mismatch");
      } else {
         JointCodebook.Entry4[] entry4s = new JointCodebook.Entry4[n];

         for (int i = 0; i < n; i++) {
            JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
            JointCodebook.Entry4 entry4 = new JointCodebook.Entry4();
            entry4.floatValues = this.method2584(this.method2572(jsonObject, "weight_ih"));
            entry4.floatValues2 = this.method2584(this.method2572(jsonObject, "weight_hh"));
            entry4.floatValues3 = this.method2583(this.method2572(jsonObject, "bias_ih"));
            entry4.floatValues4 = this.method2583(this.method2572(jsonObject, "bias_hh"));
            entry4s[i] = entry4;
         }

         return entry4s;
      }
   }

   private JointCodebook.Entry8 method2563(JsonObject jsonObject) {
      float[][] floatValues = this.method2584(this.method2572(jsonObject, "weight"));
      float[] floatValues2 = this.method2583(this.method2572(jsonObject, "bias"));
      if (floatValues.length != floatValues2.length) {
         throw new IllegalArgumentException("Linear layer weight/bias size mismatch");
      } else {
         return new JointCodebook.Entry8(floatValues, floatValues2);
      }
   }

   private JointCodebook.Entry6 method2564(JsonObject jsonObject) {
      return new JointCodebook.Entry6(this.method2583(this.method2572(jsonObject, "mean")), this.method2583(this.method2572(jsonObject, "std")));
   }

   private JointCodebook.Entry5 method2565(JsonObject json) {
      JointCodebook.Entry5 entry5 = new JointCodebook.Entry5();
      entry5.enabled = false;
      entry5.floatValues = new float[]{720.0F, 360.0F};
      if (json == null) {
         return entry5;
      } else {
         entry5.enabled = this.method2581(json, "enabled", true);
         float[] floats = this.method2583(this.method2573(json, "max_abs_count"));
         if (floats.length >= 2 && this.isFinite(floats[0]) && this.isFinite(floats[1]) && floats[0] > 0.0F && floats[1] > 0.0F) {
            entry5.floatValues = new float[]{Math.abs(floats[0]), Math.abs(floats[1])};
         }

         return entry5;
      }
   }

   private JointCodebook.Entry method2566(JsonObject json) {
      JointCodebook.Entry entry = new JointCodebook.Entry();
      entry.floatValue = 850.0F;
      entry.floatValue2 = 650.0F;
      entry.floatValue3 = 250.0F;
      entry.floatValue4 = 0.6F;
      if (json == null) {
         return entry;
      } else {
         entry.floatValue = this.method2567(this.method2580(json, "max_stable_width_yaw_count", entry.floatValue), entry.floatValue);
         entry.floatValue2 = this.method2567(this.method2580(json, "max_stable_height_pitch_count", entry.floatValue2), entry.floatValue2);
         entry.floatValue3 = this.method2567(this.method2580(json, "max_stable_inside_center_count", entry.floatValue3), entry.floatValue3);
         entry.floatValue4 = Math.max(0.05F, this.method2567(this.method2580(json, "close_xz_threshold", entry.floatValue4), entry.floatValue4));
         return entry;
      }
   }

   private float method2567(float floatValue, float f) {
      return this.isFinite(floatValue) && floatValue > 0.0F ? floatValue : f;
   }

   private boolean method2568(String text) {
      return "joint_nonuniform_codebook_residual_rotation_policy_v22".equals(text)
         || "joint_nonuniform_codebook_residual_rotation_policy_v22_1".equals(text)
         || "joint_nonuniform_codebook_class_conditioned_residual_rotation_policy_v22_2".equals(text);
   }

   private String method2569(String str) {
      if (str == null) {
         return "topk";
      } else {
         String lowerCase = str.toLowerCase();
         return !"soft".equals(lowerCase) && !"argmax".equals(lowerCase) && !"topk".equals(lowerCase) && !"sample".equals(lowerCase) ? "topk" : lowerCase;
      }
   }

   private JsonObject method2570(JsonObject jsonObject, String memberName) {
      JsonElement jsonElement = this.method2574(jsonObject, memberName);
      if (!jsonElement.isJsonObject()) {
         throw new IllegalArgumentException("Model JSON field is not an object: " + memberName);
      } else {
         return jsonElement.getAsJsonObject();
      }
   }

   private JsonObject method2571(JsonObject json, String memberName) {
      if (json != null && json.has(memberName) && !json.get(memberName).isJsonNull()) {
         JsonElement jsonElement = json.get(memberName);
         return jsonElement.isJsonObject() ? jsonElement.getAsJsonObject() : null;
      } else {
         return null;
      }
   }

   private JsonArray method2572(JsonObject jsonObject, String memberName) {
      JsonElement jsonElement = this.method2574(jsonObject, memberName);
      if (!jsonElement.isJsonArray()) {
         throw new IllegalArgumentException("Model JSON field is not an array: " + memberName);
      } else {
         return jsonElement.getAsJsonArray();
      }
   }

   private JsonArray method2573(JsonObject json, String memberName) {
      if (json != null && json.has(memberName) && !json.get(memberName).isJsonNull()) {
         JsonElement jsonElement = json.get(memberName);
         return jsonElement.isJsonArray() ? jsonElement.getAsJsonArray() : new JsonArray();
      } else {
         return new JsonArray();
      }
   }

   private JsonElement method2574(JsonObject jsonObject, String memberName) {
      if (jsonObject != null && jsonObject.has(memberName) && !jsonObject.get(memberName).isJsonNull()) {
         return jsonObject.get(memberName);
      } else {
         throw new IllegalArgumentException("Model JSON missing field: " + memberName);
      }
   }

   private String method2575(JsonObject jsonObject, String memberName) {
      return this.method2574(jsonObject, memberName).getAsString();
   }

   private String method2576(JsonObject json, String memberName, String str) {
      return json != null && json.has(memberName) && !json.get(memberName).isJsonNull() ? json.get(memberName).getAsString() : str;
   }

   private int method2577(JsonObject jsonObject, String memberName) {
      return this.method2574(jsonObject, memberName).getAsInt();
   }

   private int method2578(JsonObject json, String memberName, int n) {
      return json != null && json.has(memberName) && !json.get(memberName).isJsonNull() ? json.get(memberName).getAsInt() : n;
   }

   private float method2579(JsonObject jsonObject, String memberName) {
      return this.method2574(jsonObject, memberName).getAsFloat();
   }

   private float method2580(JsonObject json, String memberName, float floatValue) {
      return json != null && json.has(memberName) && !json.get(memberName).isJsonNull() ? json.get(memberName).getAsFloat() : floatValue;
   }

   private boolean method2581(JsonObject json, String memberName, boolean flag) {
      return json != null && json.has(memberName) && !json.get(memberName).isJsonNull() ? json.get(memberName).getAsBoolean() : flag;
   }

   private String[] method2582(JsonArray jsonArray) {
      String[] strings = new String[jsonArray.size()];

      for (int i = 0; i < jsonArray.size(); i++) {
         strings[i] = jsonArray.get(i).getAsString();
      }

      return strings;
   }

   private float[] method2583(JsonArray jsonArray) {
      if (jsonArray == null) {
         return new float[0];
      } else {
         float[] floats = new float[jsonArray.size()];

         for (int i = 0; i < jsonArray.size(); i++) {
            floats[i] = jsonArray.get(i).getAsFloat();
         }

         return floats;
      }
   }

   private float[][] method2584(JsonArray jsonArray) {
      if (jsonArray == null) {
         return new float[0][0];
      } else {
         float[][] floatses = new float[jsonArray.size()][];

         for (int i = 0; i < jsonArray.size(); i++) {
            floatses[i] = this.method2583(jsonArray.get(i).getAsJsonArray());
         }

         return floatses;
      }
   }

   private float[] method2585(float[] floats, float floatValue) {
      if (floats != null && floats.length != 0) {
         float f = Math.max(0.05F, floatValue);
         float floatValue2 = -Float.MAX_VALUE;

         for (float f2 : floats) {
            float floatValue3 = f2 / f;
            if (this.isFinite(floatValue3)) {
               floatValue2 = Math.max(floatValue2, floatValue3);
            }
         }

         if (!this.isFinite(floatValue2)) {
            return null;
         } else {
            float floatValue3 = 0.0F;
            float[] floats2 = new float[floats.length];

            for (int i = 0; i < floats.length; i++) {
               float exp = (float)Math.exp(floats[i] / f - floatValue2);
               floats2[i] = exp;
               floatValue3 += exp;
            }

            if (this.isFinite(floatValue3) && !(floatValue3 <= 0.0F)) {
               for (int i = 0; i < floats2.length; i++) {
                  floats2[i] /= floatValue3;
               }

               return floats2;
            } else {
               return null;
            }
         }
      } else {
         return null;
      }
   }

   private int method2586(float[] floats) {
      int n = 0;
      float f = -Float.MAX_VALUE;

      for (int i = 0; i < floats.length; i++) {
         if (floats[i] > f) {
            f = floats[i];
            n = i;
         }
      }

      return n;
   }

   private int[] method2587(float[] floats, int n) {
      int n2 = Math.min(Math.max(1, n), floats.length);
      int[] ints = new int[n2];
      float[] floats2 = new float[n2];

      for (int i = 0; i < n2; i++) {
         ints[i] = -1;
         floats2[i] = -Float.MAX_VALUE;
      }

      for (int i = 0; i < floats.length; i++) {
         float f = floats[i];
         if (!(f <= floats2[n2 - 1])) {
            int n3;
            for (n3 = n2 - 1; n3 > 0 && f > floats2[n3 - 1]; n3--) {
               floats2[n3] = floats2[n3 - 1];
               ints[n3] = ints[n3 - 1];
            }

            floats2[n3] = f;
            ints[n3] = i;
         }
      }

      for (int i = 0; i < n2; i++) {
         if (ints[i] < 0) {
            ints[i] = 0;
         }
      }

      return ints;
   }

   private boolean method2588(float[] floatValues) {
      if (floatValues == null) {
         return false;
      } else {
         for (float floatValue : floatValues) {
            if (!this.isFinite(floatValue)) {
               return false;
            }
         }

         return true;
      }
   }

   private boolean isFinite(float floatValue) {
      return !Float.isNaN(floatValue) && !Float.isInfinite(floatValue);
   }

   private float clamp(float f, float f2, float floatValue) {
      if (f2 > floatValue) {
         float f3 = f2;
         f2 = floatValue;
         floatValue = f3;
      }

      return Math.max(f2, Math.min(floatValue, f));
   }

   private float method2589(float f) {
      if (f >= 0.0F) {
         float exp = (float)Math.exp(-f);
         return 1.0F / (1.0F + exp);
      } else {
         float exp = (float)Math.exp(f);
         return exp / (1.0F + exp);
      }
   }

   private float tanh(float x) {
      return (float)Math.tanh(x);
   }

   private float[] method2590(float[] floats) {
      float[] floats2 = new float[floats.length];

      for (int i = 0; i < floats.length; i++) {
         float f = floats[i];
         floats2[i] = 0.5F * f * (1.0F + (float)Math.tanh(Math.sqrt(0.6366197723675814) * (f + 0.044715F * f * f * f)));
      }

      return floats2;
   }

   private boolean method2591(String str) {
      if (str == null) {
         return false;
      } else {
         for (int i = 0; i < str.length(); i++) {
            char charAt = str.charAt(i);
            if (charAt != '\ufeff' && !Character.isWhitespace(charAt)) {
               return charAt == '{';
            }
         }

         return false;
      }
   }

   private static final class Entry {
      public float floatValue;
      public float floatValue2;
      public float floatValue3;
      public float floatValue4;
   }

   private static final class Entry2 {
      public final float[] floatValues;
      public final float[] floatValues2;
      public final float[] floatValues3;
      public final float floatValue;

      private Entry2(float[] floatValues, float[] floatValues2, float[] floatValues3, float floatValue) {
         this.floatValues = floatValues;
         this.floatValues2 = floatValues2;
         this.floatValues3 = floatValues3;
         this.floatValue = floatValue;
      }
   }

   private static final class Entry3 {
      public final float[] floatValues;
      public final float[] floatValues2;
      public final float floatValue;

      private Entry3(float[] floatValues, float[] floatValues2, float floatValue) {
         this.floatValues = floatValues;
         this.floatValues2 = floatValues2;
         this.floatValue = floatValue;
      }
   }

   private static final class Entry4 {
      public float[][] floatValues;
      public float[][] floatValues2;
      public float[] floatValues3;
      public float[] floatValues4;
   }

   private static final class Entry5 {
      public boolean enabled;
      public float[] floatValues;
   }

   private static final class Entry6 {
      public final float[] floatValues;
      public final float[] floatValues2;

      private Entry6(float[] floatValues, float[] floatValues2) {
         this.floatValues = floatValues;
         this.floatValues2 = floatValues2;
      }
   }

   public static final class Entry7 {
      private final float floatValue;
      private final float floatValue2;
      private final float floatValue3;
      private final float floatValue4;
      private final float floatValue5;
      private final float floatValue6;
      private final float floatValue7;
      private final float floatValue8;
      private final float floatValue9;
      private final float floatValue10;
      private final float floatValue11;
      private final float floatValue12;
      private final float floatValue13;
      private final float floatValue14;
      private final float floatValue15;
      private final float floatValue16;
      private final float floatValue17;
      private final float floatValue18;
      private final float floatValue19;

      private Entry7(
         float floatValue,
         float f,
         float f2,
         float f3,
         float f4,
         float f5,
         float f6,
         float f7,
         float f8,
         float f9,
         float f10,
         float f11,
         float f12
      ) {
         this(floatValue, f, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12, f11, f12, f11, f12, 0.0F, 0.0F);
      }

      private Entry7(
         float floatValue,
         float f,
         float f2,
         float f3,
         float f4,
         float f5,
         float f6,
         float f7,
         float f8,
         float f9,
         float f10,
         float f11,
         float f12,
         float f13,
         float f14,
         float f15,
         float f16,
         float f17,
         float f18
      ) {
         this.floatValue = floatValue;
         this.floatValue2 = f;
         this.floatValue3 = f2;
         this.floatValue4 = f3;
         this.floatValue5 = f4;
         this.floatValue6 = f5;
         this.floatValue7 = f6;
         this.floatValue8 = f7;
         this.floatValue9 = f8;
         this.floatValue10 = f9;
         this.floatValue11 = f10;
         this.floatValue12 = f11;
         this.floatValue13 = f12;
         this.floatValue14 = f13;
         this.floatValue15 = f14;
         this.floatValue16 = f15;
         this.floatValue17 = f16;
         this.floatValue18 = f17;
         this.floatValue19 = f18;
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public float getFloatValue2() {
         return this.floatValue2;
      }

      public JointCodebook.Entry7 method2592(float floatValue, float f, float f2, float f3) {
         return new JointCodebook.Entry7(
            floatValue,
            f,
            floatValue - f2,
            f - f3,
            this.floatValue5,
            this.floatValue6,
            this.floatValue7,
            this.floatValue8,
            this.floatValue9,
            this.floatValue10,
            this.floatValue11,
            this.floatValue12,
            this.floatValue13,
            this.floatValue14,
            this.floatValue15,
            this.floatValue16,
            this.floatValue17,
            this.floatValue18,
            this.floatValue19
         );
      }

      public JointCodebook.Entry7 method2593(
         float floatValue, float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, float f9
      ) {
         return new JointCodebook.Entry7(
            floatValue,
            f,
            floatValue - f2,
            f - f3,
            this.floatValue5,
            this.floatValue6,
            this.floatValue7,
            this.floatValue8,
            this.floatValue9,
            this.floatValue10,
            this.floatValue11,
            this.floatValue12,
            this.floatValue13,
            f4,
            f5,
            f6,
            f7,
            f8,
            f9
         );
      }

      public float[] method2594() {
         return new float[]{this.floatValue, this.floatValue2};
      }

      public float getFloatValue3() {
         return this.floatValue3;
      }

      public float getFloatValue4() {
         return this.floatValue4;
      }

      public float getFloatValue5() {
         return this.floatValue5;
      }

      public float getFloatValue6() {
         return this.floatValue6;
      }

      public float getFloatValue7() {
         return this.floatValue7;
      }

      public float method2595() {
         return Math.abs(this.floatValue);
      }

      public float method2596() {
         return Math.abs(this.floatValue2);
      }

      public float getFloatValue8() {
         return this.floatValue8;
      }

      public float getFloatValue9() {
         return this.floatValue9;
      }

      public float getFloatValue10() {
         return this.floatValue10;
      }

      public float getFloatValue11() {
         return this.floatValue11;
      }

      public float getFloatValue12() {
         return this.floatValue12;
      }

      public float getFloatValue13() {
         return this.floatValue13;
      }

      public float getFloatValue14() {
         return this.floatValue14;
      }

      public float getFloatValue15() {
         return this.floatValue15;
      }

      public float getFloatValue16() {
         return this.floatValue16;
      }

      public float getFloatValue17() {
         return this.floatValue17;
      }

      public float getFloatValue18() {
         return this.floatValue18;
      }

      public float getFloatValue19() {
         return this.floatValue19;
      }
   }

   private static final class Entry8 {
      public final float[][] floatValues;
      public final float[] floatValues2;

      private Entry8(float[][] floatValues, float[] floatValues2) {
         this.floatValues = floatValues;
         this.floatValues2 = floatValues2;
      }
   }

   private static final class Entry9 {
      public String text;
      public String text2;
      public int intValue;
      public int intValue2;
      public int intValue3;
      public int intValue4;
      public int intValue5;
      public int intValue6;
      public int intValue7;
      public int intValue8;
      public float floatValue;
      public String text3;
      public int intValue9;
      public float floatValue2;
      public float floatValue3;
      public float floatValue4;
      public boolean flag;
      public String[] texts;
      public String[] texts2;
      public float floatValue5;
      public float floatValue6;
      public JointCodebook.Entry6 entry6;
      public JointCodebook.Entry4[] entry4s;
      public JointCodebook.Entry8 entry8;
      public JointCodebook.Entry8 entry82;
      public JointCodebook.Entry8 entry83;
      public JointCodebook.Entry8 entry84;
      public float[][] floatValues;
      public float[][] floatValues2;
      public float[] floatValues3;
      public JointCodebook.Entry5 entry5;
      public JointCodebook.Entry entry;
   }
}
