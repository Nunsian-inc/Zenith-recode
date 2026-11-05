package org.zenith.utility.render;

import com.mojang.renderpearl.api.pipeline.BlendFactor;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import org.zenith.utility.render.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;
import java.util.Arrays;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Camera;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.zenith.RenderState;
import org.zenith.Zenith;
import org.zenith.utility.misc.ColorUtils;

public final class Glowboost {
   private static final Identifier identifier = Zenith.method1501("visuals/jumpcircle/glowboost.png");
   private static final float CONST_floatValue = 1.7F;
   private static final boolean CONST_flag = !FabricLoader.getInstance().isModLoaded("sodium");
   private double doubleValue;
   private double doubleValue2;
   private double doubleValue3;
   private double doubleValue4;
   private double doubleValue5;
   private double doubleValue6;
   private double doubleValue7;
   private double doubleValue8;
   private double doubleValue9;
   private double doubleValue10;
   private double doubleValue11;
   private double doubleValue12;
   private float floatValue;
   private boolean flag;
   private float[] floatValues = new float[24576];
   private int[] intValues = new int[4096];
   private int intValue;
   private float[] floatValues2 = new float[12288];
   private int[] intValues2 = new int[2048];
   private int intValue2;
   private float[] floatValues3 = new float[16384];
   private int[] intValues3 = new int[4096];
   private int intValue3;
   private float[] floatValues4 = new float[16384];
   private int[] intValues4 = new int[4096];
   private int intValue4;
   private float[] floatValues5 = new float[12288];
   private int[] intValues5 = new int[1024];
   private int intValue5;
   private float[] floatValues6 = new float[24576];
   private int[] intValues6 = new int[8192];
   private int intValue6;
   private float[] floatValues7 = new float[12288];
   private int[] intValues7 = new int[1024];
   private int intValue7;
   private float[] floatValues8 = new float[20480];
   private int[] intValues8 = new int[4096];
   private int intValue8;
   private float[] floatValues9 = new float[49152];
   private float[] floatValues10 = new float[16384];
   private int[] intValues9 = new int[4096];
   private int[] intValues10 = new int[4096];
   private int intValue9;
   private final Identifier[] identifiers = new Identifier[8];
   private int intValue10;
   private int intValue11;
   private final Vector3f vector3f = new Vector3f();
   private final Vector3f vector3f2 = new Vector3f();

   public void method3232(Camera camera, float f) {
      Vec3 position = camera.position();
      this.doubleValue = position.x;
      this.doubleValue2 = position.y;
      this.doubleValue3 = position.z;
      this.method3234(camera.xRot(), camera.yRot());
      this.floatValue = Math.max(0.0F, f);
      this.flag = false;
      this.reset();
   }

   public void method3233(float xRot, float yRot, float f) {
      this.doubleValue = 0.0;
      this.doubleValue2 = 0.0;
      this.doubleValue3 = 0.0;
      this.method3234(xRot, yRot);
      this.floatValue = Math.max(0.0F, f);
      this.flag = false;
      this.reset();
   }

   private void method3234(float xRot, float yRot) {
      float f = xRot * (float) (Math.PI / 180.0);
      float f2 = -yRot * (float) (Math.PI / 180.0);
      float f3 = Mth.cos(f);
      this.doubleValue10 = Mth.sin(f2) * f3;
      this.doubleValue11 = -Mth.sin(f);
      this.doubleValue12 = Mth.cos(f2) * f3;
      double d = -this.doubleValue12;
      double d2 = this.doubleValue10;
      double d3 = Math.sqrt(d * d + d2 * d2);
      if (d3 < 1.0E-6) {
         d = 1.0;
         d2 = 0.0;
         d3 = 1.0;
      }

      this.doubleValue4 = d / d3;
      this.doubleValue5 = 0.0;
      this.doubleValue6 = d2 / d3;
      this.doubleValue7 = this.doubleValue5 * this.doubleValue12 - this.doubleValue6 * this.doubleValue11;
      this.doubleValue8 = this.doubleValue6 * this.doubleValue10 - this.doubleValue4 * this.doubleValue12;
      this.doubleValue9 = this.doubleValue4 * this.doubleValue11 - this.doubleValue5 * this.doubleValue10;
   }

   private void reset() {
      this.intValue = 0;
      this.intValue2 = 0;
      this.intValue3 = 0;
      this.intValue4 = 0;
      this.intValue5 = 0;
      this.intValue6 = 0;
      this.intValue7 = 0;
      this.intValue8 = 0;
      this.intValue9 = 0;
      this.intValue10 = 0;
      this.intValue11 = 0;
      Arrays.fill(this.identifiers, null);
   }

   public double getDoubleValue4() {
      return this.doubleValue4;
   }

   public double getDoubleValue5() {
      return this.doubleValue5;
   }

   public double getDoubleValue6() {
      return this.doubleValue6;
   }

   public double getDoubleValue7() {
      return this.doubleValue7;
   }

   public double getDoubleValue8() {
      return this.doubleValue8;
   }

   public double getDoubleValue9() {
      return this.doubleValue9;
   }

   public double getDoubleValue10() {
      return this.doubleValue10;
   }

   public double getDoubleValue11() {
      return this.doubleValue11;
   }

   public double getDoubleValue12() {
      return this.doubleValue12;
   }

   public double getDoubleValue() {
      return this.doubleValue;
   }

   public double getDoubleValue2() {
      return this.doubleValue2;
   }

   public double getDoubleValue3() {
      return this.doubleValue3;
   }

   public float getFloatValue() {
      return this.floatValue;
   }

   public void method3235(float f) {
      this.floatValue = Math.max(0.0F, f);
   }

   public void setFlag(boolean flag) {
      this.flag = flag;
   }

   public boolean isEmpty() {
      return this.intValue == 0
         && this.intValue2 == 0
         && this.intValue3 == 0
         && this.intValue4 == 0
         && this.intValue5 == 0
         && this.intValue7 == 0
         && this.intValue8 == 0
         && this.intValue9 == 0;
   }

   public void method3236(double doubleValue, double d, double d2, double doubleValue2, double d3, double d4, int color) {
      if (color >>> 24 != 0) {
         float f = (float)(doubleValue - this.doubleValue);
         float f2 = (float)(d - this.doubleValue2);
         float f3 = (float)(d2 - this.doubleValue3);
         float f4 = (float)(doubleValue2 - this.doubleValue);
         float f5 = (float)(d3 - this.doubleValue2);
         float f6 = (float)(d4 - this.doubleValue3);
         if (this.floatValue > 0.05F) {
            if ((this.intValue2 + 1) * 6 > this.floatValues2.length) {
               this.floatValues2 = Arrays.copyOf(this.floatValues2, this.floatValues2.length * 2);
            }

            if (this.intValue2 >= this.intValues2.length) {
               this.intValues2 = Arrays.copyOf(this.intValues2, this.intValues2.length * 2);
            }

            int n = this.intValue2 * 6;
            this.floatValues2[n] = f;
            this.floatValues2[n + 1] = f2;
            this.floatValues2[n + 2] = f3;
            this.floatValues2[n + 3] = f4;
            this.floatValues2[n + 4] = f5;
            this.floatValues2[n + 5] = f6;
            this.intValues2[this.intValue2++] = ColorUtils.method6439(color, 0.2F + 0.1F * this.floatValue);
         }

         if ((this.intValue + 1) * 6 > this.floatValues.length) {
            this.floatValues = Arrays.copyOf(this.floatValues, this.floatValues.length * 2);
         }

         if (this.intValue >= this.intValues.length) {
            this.intValues = Arrays.copyOf(this.intValues, this.intValues.length * 2);
         }

         int n = this.intValue * 6;
         this.floatValues[n] = f;
         this.floatValues[n + 1] = f2;
         this.floatValues[n + 2] = f3;
         this.floatValues[n + 3] = f4;
         this.floatValues[n + 4] = f5;
         this.floatValues[n + 5] = f6;
         this.intValues[this.intValue++] = color;
      }
   }

   public void method3237(
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
      int n
   ) {
      if (n >>> 24 != 0) {
         if ((this.intValue5 + 1) * 12 > this.floatValues5.length) {
            this.floatValues5 = Arrays.copyOf(this.floatValues5, this.floatValues5.length * 2);
         }

         if (this.intValue5 >= this.intValues5.length) {
            this.intValues5 = Arrays.copyOf(this.intValues5, this.intValues5.length * 2);
         }

         this.intValue5 = this.method3240(
            this.floatValues5, this.intValues5, this.intValue5, d, d2, d3, d4, d5, d6, d7, d8, d9, d10, d11, d12, n
         );
      }
   }

   public void method3238(
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
      int n
   ) {
      if (n >>> 24 != 0) {
         if ((this.intValue7 + 1) * 12 > this.floatValues7.length) {
            this.floatValues7 = Arrays.copyOf(this.floatValues7, this.floatValues7.length * 2);
         }

         if (this.intValue7 >= this.intValues7.length) {
            this.intValues7 = Arrays.copyOf(this.intValues7, this.intValues7.length * 2);
         }

         this.intValue7 = this.method3240(
            this.floatValues7, this.intValues7, this.intValue7, d, d2, d3, d4, d5, d6, d7, d8, d9, d10, d11, d12, n
         );
      }
   }

   public void method3239(
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
      if ((n | n2 | n3 | n4) >>> 24 != 0) {
         if ((this.intValue6 + 1) * 12 > this.floatValues6.length) {
            this.floatValues6 = Arrays.copyOf(this.floatValues6, this.floatValues6.length * 2);
         }

         if ((this.intValue6 + 1) * 4 > this.intValues6.length) {
            this.intValues6 = Arrays.copyOf(this.intValues6, this.intValues6.length * 2);
         }

         int n5 = this.intValue6 * 12;
         this.floatValues6[n5] = (float)(d - this.doubleValue);
         this.floatValues6[n5 + 1] = (float)(d2 - this.doubleValue2);
         this.floatValues6[n5 + 2] = (float)(d3 - this.doubleValue3);
         this.floatValues6[n5 + 3] = (float)(d4 - this.doubleValue);
         this.floatValues6[n5 + 4] = (float)(d5 - this.doubleValue2);
         this.floatValues6[n5 + 5] = (float)(d6 - this.doubleValue3);
         this.floatValues6[n5 + 6] = (float)(d7 - this.doubleValue);
         this.floatValues6[n5 + 7] = (float)(d8 - this.doubleValue2);
         this.floatValues6[n5 + 8] = (float)(d9 - this.doubleValue3);
         this.floatValues6[n5 + 9] = (float)(d10 - this.doubleValue);
         this.floatValues6[n5 + 10] = (float)(d11 - this.doubleValue2);
         this.floatValues6[n5 + 11] = (float)(d12 - this.doubleValue3);
         int n6 = this.intValue6 * 4;
         this.intValues6[n6] = n;
         this.intValues6[n6 + 1] = n2;
         this.intValues6[n6 + 2] = n3;
         this.intValues6[n6 + 3] = n4;
         this.intValue6++;
      }
   }

   private int method3240(
      float[] floatValues5,
      int[] intValues5,
      int n,
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
      int n2
   ) {
      int n3 = n * 12;
      floatValues5[n3] = (float)(d - this.doubleValue);
      floatValues5[n3 + 1] = (float)(d2 - this.doubleValue2);
      floatValues5[n3 + 2] = (float)(d3 - this.doubleValue3);
      floatValues5[n3 + 3] = (float)(d4 - this.doubleValue);
      floatValues5[n3 + 4] = (float)(d5 - this.doubleValue2);
      floatValues5[n3 + 5] = (float)(d6 - this.doubleValue3);
      floatValues5[n3 + 6] = (float)(d7 - this.doubleValue);
      floatValues5[n3 + 7] = (float)(d8 - this.doubleValue2);
      floatValues5[n3 + 8] = (float)(d9 - this.doubleValue3);
      floatValues5[n3 + 9] = (float)(d10 - this.doubleValue);
      floatValues5[n3 + 10] = (float)(d11 - this.doubleValue2);
      floatValues5[n3 + 11] = (float)(d12 - this.doubleValue3);
      intValues5[n] = n2;
      return n + 1;
   }

   public void method3241(double d, double d2, double d3, double d4, int n, long longValue, float f, int n2, boolean flag) {
      if (n2 >>> 24 != 0 && !(d4 <= 5.0E-4)) {
         int n3 = Math.max(5, n);
         double d5 = 0.0;
         double d6 = 0.0;

         for (int i = 0; i <= n3; i++) {
            int n4 = i % n3;
            double d7 = n4 * (Math.PI * 2) / n3;
            double d8 = 1.0 - f * 0.5 + f * method3268(longValue, n4, 11);
            double d9 = d + Math.cos(d7) * d4 * d8;
            double d10 = d3 + Math.sin(d7) * d4 * d8;
            if (i > 0) {
               if (flag) {
                  this.method3238(d, d2, d3, d5, d2, d6, d9, d2, d10, d, d2, d3, n2);
               } else {
                  this.method3237(d, d2, d3, d5, d2, d6, d9, d2, d10, d, d2, d3, n2);
               }
            }

            d5 = d9;
            d6 = d10;
         }
      }
   }

   public void method3242(double d, double d2, double d3, double d4, double d5, int n, int n2, boolean flag) {
      if (n2 >>> 24 != 0 && !(d4 <= 5.0E-4)) {
         int n3 = Math.max(6, n);
         double d6 = Math.max(0.0, d4 - d5 * 0.5);
         double d7 = d4 + d5 * 0.5;
         double d8 = 1.0;
         double d9 = 0.0;

         for (int i = 1; i <= n3; i++) {
            double d10 = i * (Math.PI * 2) / n3;
            double d11 = Math.cos(d10);
            double d12 = Math.sin(d10);
            if (flag) {
               this.method3238(
                  d + d8 * d6,
                  d2,
                  d3 + d9 * d6,
                  d + d11 * d6,
                  d2,
                  d3 + d12 * d6,
                  d + d11 * d7,
                  d2,
                  d3 + d12 * d7,
                  d + d8 * d7,
                  d2,
                  d3 + d9 * d7,
                  n2
               );
            } else {
               this.method3237(
                  d + d8 * d6,
                  d2,
                  d3 + d9 * d6,
                  d + d11 * d6,
                  d2,
                  d3 + d12 * d6,
                  d + d11 * d7,
                  d2,
                  d3 + d12 * d7,
                  d + d8 * d7,
                  d2,
                  d3 + d9 * d7,
                  n2
               );
            }

            d8 = d11;
            d9 = d12;
         }
      }
   }

   public void method3243(double doubleValue, double d, double d2, float f, float f2, int n) {
      if (n >>> 24 != 0 && !(f <= 0.0F)) {
         if ((this.intValue8 + 1) * 5 > this.floatValues8.length) {
            this.floatValues8 = Arrays.copyOf(this.floatValues8, this.floatValues8.length * 2);
         }

         if (this.intValue8 >= this.intValues8.length) {
            this.intValues8 = Arrays.copyOf(this.intValues8, this.intValues8.length * 2);
         }

         int n2 = this.intValue8 * 5;
         this.floatValues8[n2] = (float)(doubleValue - this.doubleValue);
         this.floatValues8[n2 + 1] = (float)(d - this.doubleValue2);
         this.floatValues8[n2 + 2] = (float)(d2 - this.doubleValue3);
         this.floatValues8[n2 + 3] = f;
         this.floatValues8[n2 + 4] = f2;
         this.intValues8[this.intValue8++] = n;
      }
   }

   public void method3244(Identifier identifier) {
      for (int i = 0; i < this.intValue10; i++) {
         if (this.identifiers[i] == identifier) {
            this.intValue11 = i;
            return;
         }
      }

      if (this.intValue10 == this.identifiers.length) {
         this.intValue11 = this.intValue10 - 1;
         this.identifiers[this.intValue11] = identifier;
      } else {
         this.identifiers[this.intValue10] = identifier;
         this.intValue11 = this.intValue10++;
      }
   }

   public void method3245(
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
      float f,
      float f2,
      float f3,
      float f4,
      int n
   ) {
      if (n >>> 24 != 0) {
         this.method3247();
         int n2 = this.intValue9 * 12;
         this.floatValues9[n2] = (float)(d - this.doubleValue);
         this.floatValues9[n2 + 1] = (float)(d2 - this.doubleValue2);
         this.floatValues9[n2 + 2] = (float)(d3 - this.doubleValue3);
         this.floatValues9[n2 + 3] = (float)(d4 - this.doubleValue);
         this.floatValues9[n2 + 4] = (float)(d5 - this.doubleValue2);
         this.floatValues9[n2 + 5] = (float)(d6 - this.doubleValue3);
         this.floatValues9[n2 + 6] = (float)(d7 - this.doubleValue);
         this.floatValues9[n2 + 7] = (float)(d8 - this.doubleValue2);
         this.floatValues9[n2 + 8] = (float)(d9 - this.doubleValue3);
         this.floatValues9[n2 + 9] = (float)(d10 - this.doubleValue);
         this.floatValues9[n2 + 10] = (float)(d11 - this.doubleValue2);
         this.floatValues9[n2 + 11] = (float)(d12 - this.doubleValue3);
         this.method3248(f, f2, f3, f4, n);
      }
   }

   public void method3246(
      double doubleValue,
      double d,
      double d2,
      double d3,
      double d4,
      double d5,
      double d6,
      float f,
      float f2,
      float f3,
      float f4,
      int n
   ) {
      double d7 = d3 * d6;
      double d8 = d4 * d6;
      double d9 = -d4 * d5;
      double d10 = d3 * d5;
      this.method3245(
         doubleValue - d9 + d7,
         d,
         d2 - d10 + d8,
         doubleValue + d9 + d7,
         d,
         d2 + d10 + d8,
         doubleValue + d9 - d7,
         d,
         d2 + d10 - d8,
         doubleValue - d9 - d7,
         d,
         d2 - d10 - d8,
         f,
         f2,
         f3,
         f4,
         n
      );
   }

   private void method3247() {
      if ((this.intValue9 + 1) * 12 > this.floatValues9.length) {
         this.floatValues9 = Arrays.copyOf(this.floatValues9, this.floatValues9.length * 2);
         this.floatValues10 = Arrays.copyOf(this.floatValues10, this.floatValues10.length * 2);
         this.intValues9 = Arrays.copyOf(this.intValues9, this.intValues9.length * 2);
         this.intValues10 = Arrays.copyOf(this.intValues10, this.intValues10.length * 2);
      }
   }

   private void method3248(float f, float f2, float f3, float f4, int n) {
      int n2 = this.intValue9 * 4;
      this.floatValues10[n2] = f;
      this.floatValues10[n2 + 1] = f2;
      this.floatValues10[n2 + 2] = f3;
      this.floatValues10[n2 + 3] = f4;
      this.intValues10[this.intValue9] = this.intValue11;
      this.intValues9[this.intValue9++] = n;
   }

   public void method3249(
      double doubleValue,
      double d,
      double d2,
      double d3,
      double d4,
      double d5,
      float f,
      float f2,
      float f3,
      float f4,
      float f5,
      float f6,
      int n
   ) {
      if (n >>> 24 != 0 && !(f <= 0.0F) && !(f2 <= 0.0F)) {
         double d6 = Math.sqrt(d3 * d3 + d4 * d4 + d5 * d5);
         if (!(d6 < 1.0E-6)) {
            double d7 = d3 / d6 * f;
            double d8 = d4 / d6 * f;
            double d9 = d5 / d6 * f;
            double d10 = d4 / d6 * this.doubleValue12 - d5 / d6 * this.doubleValue11;
            double d11 = d5 / d6 * this.doubleValue10 - d3 / d6 * this.doubleValue12;
            double d12 = d3 / d6 * this.doubleValue11 - d4 / d6 * this.doubleValue10;
            double d13 = Math.sqrt(d10 * d10 + d11 * d11 + d12 * d12);
            if (!(d13 < 1.0E-4)) {
               double d14 = d10 / d13 * f2;
               double d15 = d11 / d13 * f2;
               double d16 = d12 / d13 * f2;
               this.method3247();
               int n2 = this.intValue9 * 12;
               float f7 = (float)(doubleValue - this.doubleValue);
               float f8 = (float)(d - this.doubleValue2);
               float f9 = (float)(d2 - this.doubleValue3);
               this.floatValues9[n2] = (float)(f7 - d14 + d7);
               this.floatValues9[n2 + 1] = (float)(f8 - d15 + d8);
               this.floatValues9[n2 + 2] = (float)(f9 - d16 + d9);
               this.floatValues9[n2 + 3] = (float)(f7 + d14 + d7);
               this.floatValues9[n2 + 4] = (float)(f8 + d15 + d8);
               this.floatValues9[n2 + 5] = (float)(f9 + d16 + d9);
               this.floatValues9[n2 + 6] = (float)(f7 + d14 - d7);
               this.floatValues9[n2 + 7] = (float)(f8 + d15 - d8);
               this.floatValues9[n2 + 8] = (float)(f9 + d16 - d9);
               this.floatValues9[n2 + 9] = (float)(f7 - d14 - d7);
               this.floatValues9[n2 + 10] = (float)(f8 - d15 - d8);
               this.floatValues9[n2 + 11] = (float)(f9 - d16 - d9);
               this.method3248(f3, f4, f5, f6, n);
            }
         }
      }
   }

   public void method3250(double doubleValue, double d, double d2, float f, int color, float f2) {
      if (!(this.floatValue <= 0.02F) && !(f < 0.001F)) {
         float f3 = f2 * Math.min(1.5F, this.floatValue);
         if (!(f3 < 0.01F)) {
            this.method3252(doubleValue, d, d2, f * 2.1F, ColorUtils.method6439(color, Math.min(1.0F, f3 * 0.22F)));
            this.method3252(doubleValue, d, d2, f, ColorUtils.method6439(color, Math.min(1.0F, f3 * 0.55F)));
         }
      }
   }

   public void method3251(double d, double d2, double d3, float f, int color, float f2) {
      if (!(f < 0.001F) && !(f2 < 0.002F)) {
         int n = ColorUtils.method6439(color, Math.min(1.0F, f2));
         if (n >>> 24 != 0) {
            if ((this.intValue4 + 1) * 4 > this.floatValues4.length) {
               this.floatValues4 = Arrays.copyOf(this.floatValues4, this.floatValues4.length * 2);
            }

            if (this.intValue4 >= this.intValues4.length) {
               this.intValues4 = Arrays.copyOf(this.intValues4, this.intValues4.length * 2);
            }

            int n2 = this.intValue4 * 4;
            this.floatValues4[n2] = (float)(d - this.doubleValue);
            this.floatValues4[n2 + 1] = (float)(d2 - this.doubleValue2);
            this.floatValues4[n2 + 2] = (float)(d3 - this.doubleValue3);
            this.floatValues4[n2 + 3] = f;
            this.intValues4[this.intValue4++] = n;
         }
      }
   }

   private void method3252(double doubleValue, double d, double d2, float f, int n) {
      if (n >>> 24 != 0) {
         if ((this.intValue3 + 1) * 4 > this.floatValues3.length) {
            this.floatValues3 = Arrays.copyOf(this.floatValues3, this.floatValues3.length * 2);
         }

         if (this.intValue3 >= this.intValues3.length) {
            this.intValues3 = Arrays.copyOf(this.intValues3, this.intValues3.length * 2);
         }

         int n2 = this.intValue3 * 4;
         this.floatValues3[n2] = (float)(doubleValue - this.doubleValue);
         this.floatValues3[n2 + 1] = (float)(d - this.doubleValue2);
         this.floatValues3[n2 + 2] = (float)(d2 - this.doubleValue3);
         this.floatValues3[n2 + 3] = f;
         this.intValues3[this.intValue3++] = n;
      }
   }

   public void method3253(PoseStack matrixStack) {
      if (!this.isEmpty()) {
         Pose pose = matrixStack.last();
         GL11.glDepthRange(0.0, this.flag ? 0.01 : 1.0);

         try {
            this.method3254(pose);
            this.method3256(pose);
            this.method3258(pose);
            this.method3260(pose);
            this.method3261(pose);
            this.method3262(pose);
            this.method3263(pose);
         } finally {
            GL11.glDepthRange(0.0, 1.0);
            this.reset();
            RenderState.method1881();
         }
      }
   }

   private void method3254(Pose pose) {
      if (this.intValue5 != 0 || this.intValue6 != 0) {
         RenderState.method1871();
         RenderState.method1873();
         RenderState.method1877();
         RenderState.method1878();
         RenderState.method1882(false);
         RenderState.method1898("core/position_color");
         if (this.intValue5 > 0) {
            this.method3257(pose, this.floatValues5, this.intValues5, this.intValue5);
         }

         if (this.intValue6 > 0) {
            RenderState.method1882(true);
            this.method3255(pose);
            RenderState.method1882(false);
         }

         RenderState.method1882(true);
         RenderState.method1876();
         RenderState.method1872();
      }
   }

   private void method3255(Pose pose) {
      BufferBuilder begin = Tesselator.getInstance().begin(PrimitiveTopology.QUADS, com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_COLOR);
      Matrix4f matrix = pose.pose();

      for (int i = 0; i < this.intValue6; i++) {
         int n = i * 12;
         int n2 = i * 4;
         this.method3265(begin, matrix, this.floatValues6[n], this.floatValues6[n + 1], this.floatValues6[n + 2]).setColor(this.intValues6[n2]);
         this.method3265(begin, matrix, this.floatValues6[n + 3], this.floatValues6[n + 4], this.floatValues6[n + 5]).setColor(this.intValues6[n2 + 1]);
         this.method3265(begin, matrix, this.floatValues6[n + 6], this.floatValues6[n + 7], this.floatValues6[n + 8]).setColor(this.intValues6[n2 + 2]);
         this.method3265(begin, matrix, this.floatValues6[n + 9], this.floatValues6[n + 10], this.floatValues6[n + 11]).setColor(this.intValues6[n2 + 3]);
      }

      MeshData orThrow = begin.build();
      if (orThrow != null) {
         ImmediateRenderer.method8015(orThrow);
      }
   }

   private void method3256(Pose pose) {
      if (this.intValue7 != 0) {
         RenderState.method1871();
         RenderState.method1875(BlendFactor.SRC_ALPHA, BlendFactor.ONE, BlendFactor.ZERO, BlendFactor.ONE);
         RenderState.method1877();
         RenderState.method1878();
         RenderState.method1882(false);
         RenderState.method1898("core/position_color");
         this.method3257(pose, this.floatValues7, this.intValues7, this.intValue7);
         RenderState.method1882(true);
         RenderState.method1876();
         RenderState.method1872();
         RenderState.method1873();
      }
   }

   private void method3257(Pose pose, float[] floatValues5, int[] intValues5, int n) {
      BufferBuilder begin = Tesselator.getInstance().begin(PrimitiveTopology.QUADS, com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_COLOR);
      Matrix4f matrix = pose.pose();

      for (int i = 0; i < n; i++) {
         int n2 = i * 12;
         int color = intValues5[i];
         this.method3265(begin, matrix, floatValues5[n2], floatValues5[n2 + 1], floatValues5[n2 + 2]).setColor(color);
         this.method3265(begin, matrix, floatValues5[n2 + 3], floatValues5[n2 + 4], floatValues5[n2 + 5]).setColor(color);
         this.method3265(begin, matrix, floatValues5[n2 + 6], floatValues5[n2 + 7], floatValues5[n2 + 8]).setColor(color);
         this.method3265(begin, matrix, floatValues5[n2 + 9], floatValues5[n2 + 10], floatValues5[n2 + 11]).setColor(color);
      }

      MeshData orThrow = begin.build();
      if (orThrow != null) {
         ImmediateRenderer.method8015(orThrow);
      }
   }

   private void method3258(Pose pose) {
      if (this.intValue9 != 0) {
         RenderState.method1871();
         RenderState.method1873();
         RenderState.method1877();
         RenderState.method1878();
         RenderState.method1882(false);
         int n = 0;

         while (n < this.intValue9) {
            int n2 = this.intValues10[n];
            int n3 = n + 1;

            while (n3 < this.intValue9 && this.intValues10[n3] == n2) {
               n3++;
            }

            this.method3259(pose, n, n3, this.identifiers[n2]);
            n = n3;
         }

         RenderState.method1882(true);
         RenderState.method1876();
         RenderState.method1872();
      }
   }

   private void method3259(Pose pose, int n, int n2, Identifier identifier) {
      boolean flag = identifier != null;
      if (flag) {
         RenderState.method1898("core/position_tex_color");
         RenderState.method1890(0, identifier);
      } else {
         RenderState.method1898("core/position_color");
      }

      BufferBuilder begin = Tesselator.getInstance()
         .begin(
            PrimitiveTopology.QUADS, flag ? com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_TEX_COLOR : com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_COLOR
         );
      Matrix4f matrix = pose.pose();

      for (int i = n; i < n2; i++) {
         int n3 = i * 12;
         int color = this.intValues9[i];
         if (flag) {
            int n4 = i * 4;
            float f = this.floatValues10[n4];
            float f2 = this.floatValues10[n4 + 1];
            float f3 = this.floatValues10[n4 + 2];
            float f4 = this.floatValues10[n4 + 3];
            this.method3265(begin, matrix, this.floatValues9[n3], this.floatValues9[n3 + 1], this.floatValues9[n3 + 2]).setUv(f, f2).setColor(color);
            this.method3265(begin, matrix, this.floatValues9[n3 + 3], this.floatValues9[n3 + 4], this.floatValues9[n3 + 5])
               .setUv(f3, f2)
               .setColor(color);
            this.method3265(begin, matrix, this.floatValues9[n3 + 6], this.floatValues9[n3 + 7], this.floatValues9[n3 + 8])
               .setUv(f3, f4)
               .setColor(color);
            this.method3265(begin, matrix, this.floatValues9[n3 + 9], this.floatValues9[n3 + 10], this.floatValues9[n3 + 11])
               .setUv(f, f4)
               .setColor(color);
         } else {
            this.method3265(begin, matrix, this.floatValues9[n3], this.floatValues9[n3 + 1], this.floatValues9[n3 + 2]).setColor(color);
            this.method3265(begin, matrix, this.floatValues9[n3 + 3], this.floatValues9[n3 + 4], this.floatValues9[n3 + 5]).setColor(color);
            this.method3265(begin, matrix, this.floatValues9[n3 + 6], this.floatValues9[n3 + 7], this.floatValues9[n3 + 8]).setColor(color);
            this.method3265(begin, matrix, this.floatValues9[n3 + 9], this.floatValues9[n3 + 10], this.floatValues9[n3 + 11]).setColor(color);
         }
      }

      MeshData orThrow = begin.build();
      if (orThrow != null) {
         ImmediateRenderer.method8015(orThrow);
      }
   }

   private void method3260(Pose pose) {
      if (this.intValue8 != 0) {
         RenderState.method1871();
         RenderState.method1873();
         RenderState.method1877();
         RenderState.method1878();
         RenderState.method1882(false);
         RenderState.method1898("core/position_color");
         BufferBuilder begin = Tesselator.getInstance().begin(PrimitiveTopology.QUADS, com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_COLOR);
         Matrix4f matrix = pose.pose();

         for (int i = 0; i < this.intValue8; i++) {
            int n = i * 5;
            float f = this.floatValues8[n];
            float f2 = this.floatValues8[n + 1];
            float f3 = this.floatValues8[n + 2];
            float f4 = this.floatValues8[n + 3];
            float f5 = this.floatValues8[n + 4];
            int color = this.intValues8[i];
            float f6 = Mth.cos(f5) * f4;
            float f7 = Mth.sin(f5) * f4;
            float f8 = (float)(this.doubleValue4 * f6 + this.doubleValue7 * f7);
            float f9 = (float)(this.doubleValue5 * f6 + this.doubleValue8 * f7);
            float f10 = (float)(this.doubleValue6 * f6 + this.doubleValue9 * f7);
            float f11 = (float)(this.doubleValue7 * f6 - this.doubleValue4 * f7);
            float f12 = (float)(this.doubleValue8 * f6 - this.doubleValue5 * f7);
            float f13 = (float)(this.doubleValue9 * f6 - this.doubleValue6 * f7);
            this.method3265(begin, matrix, f + f8, f2 + f9, f3 + f10).setColor(color);
            this.method3265(begin, matrix, f + f11, f2 + f12, f3 + f13).setColor(color);
            this.method3265(begin, matrix, f - f8, f2 - f9, f3 - f10).setColor(color);
            this.method3265(begin, matrix, f - f11, f2 - f12, f3 - f13).setColor(color);
         }

         MeshData orThrow = begin.build();
         if (orThrow != null) {
            ImmediateRenderer.method8015(orThrow);
         }

         RenderState.method1882(true);
         RenderState.method1876();
         RenderState.method1872();
      }
   }

   private void method3261(Pose pose) {
      if (this.intValue4 != 0) {
         RenderState.method1871();
         RenderState.method1873();
         RenderState.method1877();
         RenderState.method1878();
         RenderState.method1882(false);
         RenderState.method1898("core/position_tex_color");
         RenderState.method1890(0, identifier);
         BufferBuilder begin = Tesselator.getInstance().begin(PrimitiveTopology.QUADS, com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_TEX_COLOR);
         Matrix4f matrix = pose.pose();

         for (int i = 0; i < this.intValue4; i++) {
            int n = i * 4;
            float f = this.floatValues4[n];
            float f2 = this.floatValues4[n + 1];
            float f3 = this.floatValues4[n + 2];
            float f4 = this.floatValues4[n + 3];
            int color = this.intValues4[i];
            float f5 = (float)(this.doubleValue4 * f4);
            float f6 = (float)(this.doubleValue5 * f4);
            float f7 = (float)(this.doubleValue6 * f4);
            float f8 = (float)(this.doubleValue7 * f4);
            float f9 = (float)(this.doubleValue8 * f4);
            float f10 = (float)(this.doubleValue9 * f4);
            this.method3265(begin, matrix, f + f5 - f8, f2 + f6 - f9, f3 + f7 - f10).setUv(0.0F, 1.0F).setColor(color);
            this.method3265(begin, matrix, f - f5 - f8, f2 - f6 - f9, f3 - f7 - f10).setUv(1.0F, 1.0F).setColor(color);
            this.method3265(begin, matrix, f - f5 + f8, f2 - f6 + f9, f3 - f7 + f10).setUv(1.0F, 0.0F).setColor(color);
            this.method3265(begin, matrix, f + f5 + f8, f2 + f6 + f9, f3 + f7 + f10).setUv(0.0F, 0.0F).setColor(color);
         }

         MeshData orThrow = begin.build();
         if (orThrow != null) {
            ImmediateRenderer.method8015(orThrow);
         }

         RenderState.method1882(true);
         RenderState.method1876();
         RenderState.method1872();
      }
   }

   private void method3262(Pose pose) {
      if (this.intValue3 != 0) {
         RenderState.method1871();
         RenderState.method1877();
         RenderState.method1875(BlendFactor.SRC_ALPHA, BlendFactor.ONE, BlendFactor.ZERO, BlendFactor.ONE);
         RenderState.method1878();
         RenderState.method1882(false);
         RenderState.method1898("core/position_tex_color");
         RenderState.method1890(0, identifier);
         BufferBuilder begin = Tesselator.getInstance().begin(PrimitiveTopology.QUADS, com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_TEX_COLOR);
         Matrix4f matrix = pose.pose();

         for (int i = 0; i < this.intValue3; i++) {
            int n = i * 4;
            float f = this.floatValues3[n];
            float f2 = this.floatValues3[n + 1];
            float f3 = this.floatValues3[n + 2];
            float f4 = this.floatValues3[n + 3];
            int color = this.intValues3[i];
            float f5 = (float)(this.doubleValue4 * f4);
            float f6 = (float)(this.doubleValue5 * f4);
            float f7 = (float)(this.doubleValue6 * f4);
            float f8 = (float)(this.doubleValue7 * f4);
            float f9 = (float)(this.doubleValue8 * f4);
            float f10 = (float)(this.doubleValue9 * f4);
            this.method3265(begin, matrix, f + f5 - f8, f2 + f6 - f9, f3 + f7 - f10).setUv(0.0F, 1.0F).setColor(color);
            this.method3265(begin, matrix, f - f5 - f8, f2 - f6 - f9, f3 - f7 - f10).setUv(1.0F, 1.0F).setColor(color);
            this.method3265(begin, matrix, f - f5 + f8, f2 - f6 + f9, f3 - f7 + f10).setUv(1.0F, 0.0F).setColor(color);
            this.method3265(begin, matrix, f + f5 + f8, f2 + f6 + f9, f3 + f7 + f10).setUv(0.0F, 0.0F).setColor(color);
         }

         MeshData orThrow = begin.build();
         if (orThrow != null) {
            ImmediateRenderer.method8015(orThrow);
         }

         RenderState.method1882(true);
         RenderState.method1876();
         RenderState.method1872();
         RenderState.method1873();
      }
   }

   private void method3263(Pose pose) {
      if (this.intValue != 0 || this.intValue2 != 0) {
         GL11.glEnable(2848);
         GL11.glHint(3154, 4354);
         GL11.glEnable(2881);

         try {
            RenderState.method1871();
            RenderState.method1877();
            RenderState.method1878();
            RenderState.method1882(false);
            RenderState.method1874(BlendFactor.SRC_ALPHA, BlendFactor.ONE_MINUS_CONSTANT_ALPHA);
            RenderState.method1898("core/rendertype_lines");
            if (this.intValue2 > 0) {
               RenderState.method1902(5.44F);
               this.method3264(pose, this.floatValues2, this.intValues2, this.intValue2);
            }

            if (this.intValue > 0) {
               RenderState.method1902(1.7F);
               this.method3264(pose, this.floatValues, this.intValues, this.intValue);
            }
         } finally {
            RenderState.method1882(true);
            RenderState.method1876();
            RenderState.method1872();
            RenderState.method1873();
            RenderState.method1902(1.0F);
            GL11.glDisable(2881);
            GL11.glDisable(2848);
         }
      }
   }

   private void method3264(Pose pose, float[] floatValues2, int[] intValues2, int intValue) {
      BufferBuilder begin = Tesselator.getInstance().begin(PrimitiveTopology.LINES, com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_COLOR_NORMAL_LINE_WIDTH);
      float width = RenderState.getFloatValue4();
      Matrix4f matrix = pose.pose();

      for (int i = 0; i < intValue; i++) {
         int n = i * 6;
         float x = floatValues2[n];
         float y = floatValues2[n + 1];
         float z = floatValues2[n + 2];
         float f = floatValues2[n + 3];
         float y2 = floatValues2[n + 4];
         float z2 = floatValues2[n + 5];
         int color = intValues2[i];
         float f2 = f - x;
         float f3 = y2 - y;
         float f4 = z2 - z;
         float f5 = f2 * f2 + f3 * f3 + f4 * f4;
         float f6 = f5 > 1.0E-12F ? 1.0F / (float)Math.sqrt(f5) : 1.0F;
         float x2 = f2 * f6;
         float y3 = f3 * f6;
         float z3 = f4 * f6;
         if (CONST_flag) {
            pose.transformNormal(x2, y3, z3, this.vector3f2);
            float x3 = this.vector3f2.x;
            float y4 = this.vector3f2.y;
            float z4 = this.vector3f2.z;
            this.method3265(begin, matrix, x, y, z).setColor(color).setNormal(x3, y4, z4).setLineWidth(width);
            this.method3265(begin, matrix, f, y2, z2).setColor(color).setNormal(x3, y4, z4).setLineWidth(width);
         } else {
            begin.addVertex(matrix, x, y, z).setColor(color).setNormal(pose, x2, y3, z3).setLineWidth(width);
            begin.addVertex(matrix, f, y2, z2).setColor(color).setNormal(pose, x2, y3, z3).setLineWidth(width);
         }
      }

      MeshData orThrow = begin.build();
      if (orThrow != null) {
         ImmediateRenderer.method8015(orThrow);
      }
   }

   private VertexConsumer method3265(BufferBuilder begin, Matrix4f pose, float x, float y, float z) {
      if (CONST_flag) {
         pose.transformPosition(x, y, z, this.vector3f);
         return begin.addVertex(this.vector3f.x, this.vector3f.y, this.vector3f.z);
      } else {
         return begin.addVertex(pose, x, y, z);
      }
   }

   public static float easeOut(float f) {
      float f2 = 1.0F - f;
      return 1.0F - f2 * f2 * f2;
   }

   public static float method3266(float f) {
      return method3267(f, 0.1F, 0.45F);
   }

   public static float method3267(float f, float f2, float f3) {
      float f4 = f < f2 ? f / f2 : 1.0F;
      float f5 = f > f3 ? (1.0F - f) / (1.0F - f3) : 1.0F;
      return f4 * f5 * f5;
   }

   public static float method3268(long longValue, int n, int n2) {
      long l = longValue * -7046029254386353131L + n * -4658895280553007687L + n2 * -7723592293110705685L;
      l ^= l >>> 30;
      l *= -4658895280553007687L;
      l ^= l >>> 27;
      l *= -7723592293110705685L;
      l ^= l >>> 31;
      return (float)(l >>> 40) / 1.6777216E7F;
   }
}
