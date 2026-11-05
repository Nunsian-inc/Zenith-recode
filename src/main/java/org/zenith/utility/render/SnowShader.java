package org.zenith.utility.render;

import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.zenith.RenderState;
import org.zenith.Zenith;
import org.zenith.utility.misc.FrustumHolder;
import org.zenith.utility.misc.KawaseShaderKey;
import org.zenith.utility.misc.ShaderUniform;
import org.zenith.utility.misc.WeatherFlakeChannel;

public final class SnowShader {
   private static KawaseShaderKey kawaseShaderKey;
   private static boolean flag = true;
   private static float[] floatValues = new float[6144];
   private static int[] intValues = new int[2048];
   private static int intValue;
   private static final int CONST_intValue = 16;
   private static final int CONST_intValue2 = 10;
   private static final int CONST_intValue3 = 80;
   private static final SnowShader.Entry[] entrys = new SnowShader.Entry[100];
   private static double doubleValue;
   private static double doubleValue2;
   private static double doubleValue3;
   private static int intValue2;
   private static int[] intValues2 = new int[512];
   private static int[] intValues3 = new int[512];
   private static final int[] CONST_intValues = new int[100];
   private static final int[] CONST_intValues2 = new int[100];
   private static final int[] CONST_intValues3 = new int[100];
   private static double doubleValue4;
   private static double doubleValue5;
   private static double doubleValue6;
   private static boolean flag2;
   private static boolean flag3;
   private static final double CONST_doubleValue = 24.0;
   private static boolean flag4;
   private static double doubleValue7;
   private static double doubleValue8;
   private static double doubleValue9;
   private static ByteBufferBuilder allocator;
   private static final Matrix4f matrix4f = new Matrix4f();
   private static final Matrix4f matrix4f2 = new Matrix4f();
   private static final float[] CONST_floatValues = new float[16];
   private static final List<MeshBuffer> items = new ArrayList<>(100);
   private static boolean flag5;
   private static boolean flag6;
   private static final double CONST_doubleValue2 = 256.0;
   private static double doubleValue10;
   private static double doubleValue11;
   private static double doubleValue12;
   private static float floatValue;
   private static float floatValue2;
   private static float floatValue3;
   private static float floatValue4 = 24.0F;
   private static float floatValue5 = 1.0F;
   private static float floatValue6 = 0.6F;
   private static float floatValue7 = 0.19F;
   private static float floatValue8 = 0.3F;
   private static float floatValue9 = 1.0F;
   private static float[] floatValues2 = new float[]{0.3F, 0.9F, 0.3F};
   private static float[] floatValues3 = new float[]{1.0F, 0.98F, 0.94F};
   private static float[] floatValues4 = new float[]{0.52F, 0.56F, 0.62F};

   private SnowShader() {
   }

   public static void method387() {
      if (kawaseShaderKey == null) {
         kawaseShaderKey = new KawaseShaderKey(Zenith.method1501("snow/data"), com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_COLOR);
      }
   }

   public static boolean isFlag() {
      return flag;
   }

   public static boolean method388() {
      flag = !flag;
      return flag;
   }

   public static String method389() {
      if (kawaseShaderKey == null) {
         return "программа не создана";
      } else if (kawaseShaderKey.text3 != null) {
         return kawaseShaderKey.text3;
      } else {
         try {
            if (!LightUtil.method6712(kawaseShaderKey.getIdentifier())) {
               return "исходники шейдера не прочитаны";
            } else {
               return kawaseShaderKey.method5560("Origin") ? "ок" : "юниформ Origin не найден";
            }
         } catch (RuntimeException e) {
            return "программа не загрузилась: " + e;
         }
      }
   }

   public static boolean method390() {
      if (!flag6) {
         flag5 = method391();
         flag6 = true;
      }

      return flag5;
   }

   private static boolean method391() {
      if (kawaseShaderKey != null && kawaseShaderKey.text3 == null) {
         try {
            return LightUtil.method6712(kawaseShaderKey.getIdentifier()) && kawaseShaderKey.method5560("Origin");
         } catch (RuntimeException e) {
            return false;
         }
      } else {
         return false;
      }
   }

   public static void method392(Vec3 position, WeatherFlakeChannel weatherFlakeChannel, float f) {
      doubleValue10 = position.x;
      doubleValue11 = position.y;
      doubleValue12 = position.z;
      intValue = 0;
      flag2 = false;
      flag6 = false;
      float f2 = (float)Math.sqrt(
         weatherFlakeChannel.floatValues4[0] * weatherFlakeChannel.floatValues4[0] + weatherFlakeChannel.floatValues4[1] * weatherFlakeChannel.floatValues4[1] + weatherFlakeChannel.floatValues4[2] * weatherFlakeChannel.floatValues4[2]
      );
      if (f2 < 1.0E-5F) {
         f2 = 1.0F;
      }

      floatValues2[0] = weatherFlakeChannel.floatValues4[0] / f2;
      floatValues2[1] = weatherFlakeChannel.floatValues4[1] / f2;
      floatValues2[2] = weatherFlakeChannel.floatValues4[2] / f2;
      System.arraycopy(weatherFlakeChannel.floatValues5, 0, floatValues3, 0, 3);
      floatValues4[0] = (weatherFlakeChannel.floatValues[0] + weatherFlakeChannel.floatValues2[0]) * 0.5F + 0.16F;
      floatValues4[1] = (weatherFlakeChannel.floatValues[1] + weatherFlakeChannel.floatValues2[1]) * 0.5F + 0.16F;
      floatValues4[2] = (weatherFlakeChannel.floatValues[2] + weatherFlakeChannel.floatValues2[2]) * 0.5F + 0.17F;
      floatValue8 = f;
   }

   public static void method393() {
      method394();
      intValue = 0;
      flag2 = true;
      flag3 = false;
   }

   private static void method394() {
      double d = Math.floor(doubleValue10);
      double d2 = Math.floor(doubleValue11);
      double d3 = Math.floor(doubleValue12);
      if (!flag4 || Math.abs(d - doubleValue7) > 24.0 || Math.abs(d2 - doubleValue8) > 24.0 || Math.abs(d3 - doubleValue9) > 24.0) {
         doubleValue7 = d;
         doubleValue8 = d2;
         doubleValue9 = d3;
         flag4 = true;
      }

      doubleValue4 = doubleValue7;
      doubleValue5 = doubleValue8;
      doubleValue6 = doubleValue9;
   }

   public static void method395(
      double d,
      double d2,
      double d3,
      double d4,
      double d5,
      double d6,
      double d7,
      double d8,
      double d9,
      double d10,
      double d11,
      double d12,
      int n,
      int n2,
      int n3,
      int n4
   ) {
      if (!flag2) {
         method394();
         flag2 = true;
      }

      ensure(intValue + 1);
      int n5 = intValue * 12;
      floatValues[n5] = (float)(d - doubleValue4);
      floatValues[n5 + 1] = (float)(d2 - doubleValue5);
      floatValues[n5 + 2] = (float)(d3 - doubleValue6);
      floatValues[n5 + 3] = (float)(d4 - doubleValue4);
      floatValues[n5 + 4] = (float)(d5 - doubleValue5);
      floatValues[n5 + 5] = (float)(d6 - doubleValue6);
      floatValues[n5 + 6] = (float)(d7 - doubleValue4);
      floatValues[n5 + 7] = (float)(d8 - doubleValue5);
      floatValues[n5 + 8] = (float)(d9 - doubleValue6);
      floatValues[n5 + 9] = (float)(d10 - doubleValue4);
      floatValues[n5 + 10] = (float)(d11 - doubleValue5);
      floatValues[n5 + 11] = (float)(d12 - doubleValue6);
      int n6 = intValue * 4;
      intValues[n6] = n;
      intValues[n6 + 1] = n2;
      intValues[n6 + 2] = n3;
      intValues[n6 + 3] = n4;
      intValues2[intValue] = method396(floatValues[n5], floatValues[n5 + 2]);
      intValue++;
   }

   private static int method396(float floatValue, float f) {
      int n = Math.clamp((long)((int)Math.floor((floatValue + 80.0F) / 16.0F)), 0, 9);
      int n2 = Math.clamp((long)((int)Math.floor((f + 80.0F) / 16.0F)), 0, 9);
      return n2 * 10 + n;
   }

   public static boolean isEmpty() {
      return intValue == 0;
   }

   public static void method397(PoseStack matrixStack) {
      if (flag2) {
         long l = WorldProfiler.method782();
         method398();
         WorldProfiler.method783(15, l);
         WorldProfiler.method784(2);
      }

      if (intValue2 != 0 && method390()) {
         KawaseShaderKey kawaseShaderKey2 = kawaseShaderKey.method5165();
         if (kawaseShaderKey2 == null) {
            intValue = 0;
         } else {
            floatValue = (float)method402(doubleValue10);
            floatValue2 = (float)method402(doubleValue11);
            floatValue3 = (float)method402(doubleValue12);
            method407("Origin", floatValue, floatValue2, floatValue3);
            method407("SunDir", floatValues2[0], floatValues2[1], floatValues2[2]);
            method407("SunColor", floatValues3[0], floatValues3[1], floatValues3[2]);
            method407("SkyColor", floatValues4[0], floatValues4[1], floatValues4[2]);
            method405("GrainScale", floatValue4);
            method405("GrainStrength", floatValue5);
            method405("CrustAmount", floatValue6);
            method405("SparkleAmount", floatValue7);
            method405("Wetness", floatValue8);
            method405("Sheen", floatValue9);
            method405("WrapPeriod", (float)(256.0 * floatValue4));
            RenderState.method1871();
            RenderState.method1873();
            RenderState.method1877();
            RenderState.method1878();
            RenderState.method1882(true);
            float x = (float)(doubleValue - doubleValue10);
            float y = (float)(doubleValue2 - doubleValue11);
            float z = (float)(doubleValue3 - doubleValue12);
            Matrix4f modelView = matrix4f.set(matrixStack.last().pose()).translate(x, y, z);
            method406("InverseView", matrix4f2.translation(x, y, z));
            Frustum frustum = FrustumHolder.getFrustum();
            var meshBuffers = items;
            meshBuffers.clear();

            for (SnowShader.Entry entry : entrys) {
               if (entry != null
                  && entry.intValue != 0
                  && entry.meshBuffer != null
                  && !entry.meshBuffer.isEmpty()
                  && (
                     frustum == null
                        || frustum.isVisible(
                           new AABB(
                              doubleValue + entry.floatValue,
                              doubleValue2 + entry.floatValue2,
                              doubleValue3 + entry.floatValue3,
                              doubleValue + entry.floatValue4,
                              doubleValue2 + entry.floatValue5,
                              doubleValue3 + entry.floatValue6
                           )
                        )
                  )) {
                  meshBuffers.add(entry.meshBuffer);
               }
            }

            if (!meshBuffers.isEmpty()) {
               RenderPipeline pipeline = kawaseShaderKey.method5561(
                  com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_COLOR, PrimitiveTopology.QUADS, RenderState.method1904()
               );
               if (pipeline != null) {
                  MeshBuffer.method6589(meshBuffers, pipeline, modelView, null, kawaseShaderKey.method5562());
               }

               meshBuffers.clear();
            }

            ImmediateRenderer.method8012(null);
            RenderState.method1876();
            RenderState.method1872();
            intValue = 0;
         }
      } else {
         intValue = 0;
      }
   }

   private static void method398() {
      flag2 = false;
      flag3 = true;
      if (intValue == 0) {
         method400();
      } else {
         for (SnowShader.Entry entry : entrys) {
            if (entry != null) {
               entry.intValue = 0;
            }
         }

         int[] ints = CONST_intValues3;
         Arrays.fill(ints, 0);

         for (int i = 0; i < intValue; i++) {
            ints[intValues2[i]]++;
         }

         if (intValues3.length < intValue) {
            intValues3 = new int[Math.max(intValue, intValues3.length * 2)];
         }

         int n = 0;

         for (int i = 0; i < ints.length; i++) {
            CONST_intValues[i] = n;
            n += ints[i];
         }

         System.arraycopy(CONST_intValues, 0, CONST_intValues2, 0, ints.length);
         int n2 = 0;

         while (n2 < intValue) {
            intValues3[CONST_intValues2[intValues2[n2]]++] = n2++;
         }

         for (int i = 0; i < ints.length; i++) {
            if (ints[i] > 0) {
               method399(i, CONST_intValues[i], ints[i]);
            }
         }

         doubleValue = doubleValue4;
         doubleValue2 = doubleValue5;
         doubleValue3 = doubleValue6;
         intValue2 = intValue;
      }
   }

   private static void method399(int n, int n2, int intValue) {
      SnowShader.Entry entry = entrys[n];
      if (entry == null) {
         entry = new SnowShader.Entry();
         entrys[n] = entry;
      }

      if (entry.meshBuffer == null) {
         entry.meshBuffer = new MeshBuffer("zenith snow tile " + n);
      }

      if (entry.method408(intValues3, n2, intValue) && !entry.meshBuffer.isEmpty()) {
         entry.intValue = intValue;
      } else {
         entry.intValue2 = 0;
         entry.reset();
         if (allocator == null) {
            allocator = new ByteBufferBuilder(262144);
         }

         allocator.discard();
         BufferBuilder bufferBuilder = new BufferBuilder(allocator, PrimitiveTopology.QUADS, com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_COLOR);

         for (int i = n2; i < n2 + intValue; i++) {
            int n3 = intValues3[i];
            int n4 = n3 * 12;
            int n5 = n3 * 4;

            for (int j = 0; j < 4; j++) {
               float floatValue = floatValues[n4 + j * 3];
               float y = floatValues[n4 + j * 3 + 1];
               float z = floatValues[n4 + j * 3 + 2];
               entry.method410(floatValue, y, z);
               bufferBuilder.addVertex(floatValue, y, z).setColor(intValues[n5 + j]);
            }
         }

         MeshData buffer = bufferBuilder.build();
         if (buffer != null) {
            entry.meshBuffer.method6583(buffer);
            entry.intValue = intValue;
            entry.method409(intValues3, n2, intValue);
         }
      }
   }

   private static void method400() {
      intValue2 = 0;

      for (SnowShader.Entry entry : entrys) {
         if (entry != null) {
            entry.intValue = 0;
         }
      }
   }

   public static void method401() {
      intValue2 = 0;
      flag2 = false;
      intValue = 0;
      flag3 = false;
   }

   public static boolean isFlag3() {
      return flag3;
   }

   private static double method402(double d) {
      return d - Math.floor(d / 256.0) * 256.0;
   }

   public static void method403(float f) {
      floatValue9 = Math.clamp(f, 0.0F, 2.5F);
   }

   public static void method404(float f, float f2, float f3, float f4) {
      floatValue4 = Math.max(1.0F, (float)Math.round(f));
      floatValue5 = f2;
      floatValue6 = f3;
      floatValue7 = f4;
   }

   private static void method405(String key, float batchRange) {
      ShaderUniform shaderUniform = kawaseShaderKey.method5166(key);
      if (shaderUniform != null) {
         shaderUniform.method7412(batchRange);
      }
   }

   private static void method406(String key, Matrix4f translation) {
      ShaderUniform shaderUniform = kawaseShaderKey.method5166(key);
      if (shaderUniform != null) {
         translation.get(CONST_floatValues);
         shaderUniform.method7417(CONST_floatValues);
      }
   }

   private static void method407(String key, float floatValue, float f, float f2) {
      ShaderUniform shaderUniform = kawaseShaderKey.method5166(key);
      if (shaderUniform != null) {
         shaderUniform.method7414(floatValue, f, f2);
      }
   }

   private static void ensure(int n) {
      if (n * 12 > floatValues.length) {
         float[] floats = new float[Math.max(floatValues.length * 2, n * 12)];
         int[] ints = new int[Math.max(intValues.length * 2, n * 4)];
         int[] intValues22 = new int[Math.max(intValues2.length * 2, n)];
         System.arraycopy(floatValues, 0, floats, 0, intValue * 12);
         System.arraycopy(intValues, 0, ints, 0, intValue * 4);
         System.arraycopy(intValues2, 0, intValues22, 0, intValue);
         floatValues = floats;
         intValues = ints;
         intValues2 = intValues22;
      }
   }

   private static final class Entry {
      private MeshBuffer meshBuffer;
      private int intValue;
      private float floatValue;
      private float floatValue2;
      private float floatValue3;
      private float floatValue4;
      private float floatValue5;
      private float floatValue6;
      private int intValue2;
      private int[] intValues = new int[0];
      private int[] intValues2 = new int[0];

      private boolean method408(int[] intValues3, int n, int intValue) {
         if (this.intValue2 != intValue) {
            return false;
         } else {
            int[] intValues = this.intValues;
            int[] intValues2 = this.intValues2;

            for (int i = 0; i < intValue; i++) {
               int n2 = intValues3[n + i];
               int n3 = n2 * 12;
               int n4 = n2 * 4;
               int n5 = i * 12;
               int n6 = i * 4;

               for (int j = 0; j < 12; j++) {
                  if (Float.floatToRawIntBits(SnowShader.floatValues[n3 + j]) != intValues[n5 + j]) {
                     return false;
                  }
               }

               for (int j = 0; j < 4; j++) {
                  if (SnowShader.intValues[n4 + j] != intValues2[n6 + j]) {
                     return false;
                  }
               }
            }

            return true;
         }
      }

      private void method409(int[] intValues3, int n, int intValue) {
         if (this.intValues.length < intValue * 12) {
            this.intValues = new int[Math.max(intValue * 12, this.intValues.length * 2)];
            this.intValues2 = new int[Math.max(intValue * 4, this.intValues2.length * 2)];
         }

         for (int i = 0; i < intValue; i++) {
            int n2 = intValues3[n + i];
            int n3 = n2 * 12;
            int n4 = n2 * 4;

            for (int j = 0; j < 12; j++) {
               this.intValues[i * 12 + j] = Float.floatToRawIntBits(SnowShader.floatValues[n3 + j]);
            }

            System.arraycopy(SnowShader.intValues, n4, this.intValues2, i * 4, 4);
         }

         this.intValue2 = intValue;
      }

      private void reset() {
         this.floatValue = Float.MAX_VALUE;
         this.floatValue2 = Float.MAX_VALUE;
         this.floatValue3 = Float.MAX_VALUE;
         this.floatValue4 = -Float.MAX_VALUE;
         this.floatValue5 = -Float.MAX_VALUE;
         this.floatValue6 = -Float.MAX_VALUE;
      }

      private void method410(float floatValue, float y, float z) {
         if (floatValue < this.floatValue) {
            this.floatValue = floatValue;
         }

         if (y < this.floatValue2) {
            this.floatValue2 = y;
         }

         if (z < this.floatValue3) {
            this.floatValue3 = z;
         }

         if (floatValue > this.floatValue4) {
            this.floatValue4 = floatValue;
         }

         if (y > this.floatValue5) {
            this.floatValue5 = y;
         }

         if (z > this.floatValue6) {
            this.floatValue6 = z;
         }
      }
   }
}
