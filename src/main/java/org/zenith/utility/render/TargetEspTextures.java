package org.zenith.utility.render;

import com.mojang.renderpearl.api.pipeline.BlendFactor;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;
import org.zenith.utility.render.Tesselator;
import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;
import java.util.Arrays;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.zenith.RenderState;
import org.zenith.Zenith;
import org.zenith.utility.misc.ColorUtils;

public final class TargetEspTextures {
   public static final int CONST_intValue = 0;
   public static final int CONST_intValue2 = 1;
   public static final int CONST_intValue3 = 2;
   public static final int CONST_intValue4 = 3;
   public static final int CONST_intValue5 = 4;
   private static final Identifier identifier = method5053("soft_64");
   private static final Identifier identifier2 = method5053("heart_atlas_128");
   private static final Identifier identifier3 = method5053("heart_atlas_48");
   private static final Identifier identifier4 = method5053("star_128");
   private static final Identifier identifier5 = method5053("star_48");
   private static final Identifier identifier6 = method5053("dot_64");
   private static final Identifier identifier7 = method5053("dot_24");
   private static final float CONST_floatValue = (float) (Math.PI * 2);
   private static final float CONST_floatValue2 = (float) Math.PI;
   private static final int CONST_intValue6 = -1;
   private static final int CONST_intValue7 = 12;
   private static final int CONST_intValue8 = 4;
   private static final int CONST_intValue9 = 6;
   private static final int CONST_intValue10 = 40;
   private static final int CONST_intValue11 = 72;
   private static final int CONST_intValue12 = 48;
   private static final int CONST_intValue13 = 64;
   private static final int CONST_intValue14 = 24;
   private static final int CONST_intValue15 = 3;
   private final TargetEspTextures.Entry entry = new TargetEspTextures.Entry();
   private final TargetEspTextures.Entry entry2 = new TargetEspTextures.Entry();
   private final TargetEspTextures.Entry entry3 = new TargetEspTextures.Entry();
   private final TargetEspTextures.Entry entry4 = new TargetEspTextures.Entry();
   private final Matrix4f matrix4f = new Matrix4f();
   private final Vector3f vector3f = new Vector3f();
   private Integer[] integers = new Integer[64];
   private float[] floatValues = new float[64];
   private float floatValue;

   private static Identifier method5053(String str) {
      Identifier location = Zenith.method1501("visuals/targetesp/" + str + ".png");
      TextureFilterUtils.smooth(location);
      return location;
   }

   public void method5054(
      int n,
      Matrix4f pose,
      double d,
      double d2,
      double d3,
      float f,
      float f2,
      float floatValue,
      int[] ints,
      float alpha,
      float f3,
      float f4,
      float floatValue2,
      float ang,
      boolean flag,
      float xScale
   ) {
      if (!(alpha <= 0.01F)) {
         this.entry.count = 0;
         this.entry2.count = 0;
         this.entry3.count = 0;
         this.entry4.count = 0;
         this.floatValue = floatValue2;
         float easeOut2 = easeOut(alpha);
         float f5 = f4 * (0.7F + 0.3F * easeOut2);
         float f6 = (f + 0.1F) * f5;
         switch (n) {
            case 0:
               this.method5055(d, d2, d3, f6, f2, floatValue, ints, alpha, f3, f5);
               break;
            case 1:
               this.method5058(d, d2, d3, f6, f2, floatValue, ints, alpha, f3, f5);
               break;
            case 2:
               this.method5059(d, d2, d3, f6, f2, floatValue, ints, alpha, f3, f5);
               break;
            case 3:
               this.method5060(d, d2, d3, f6, f2, floatValue, ints, alpha, f3, f5);
               break;
            default:
               this.method5061(d, d2, d3, f6, f2, floatValue, ints, alpha, f3, f5);
         }

         boolean flag2 = xScale * 0.3F * f4 > 40.0F;
         this.method5067(pose, floatValue2, ang, flag, flag2);
      }
   }

   private void method5055(double d, double d2, double d3, float f, float f2, float floatValue, int[] ints, float alpha, float f3, float f4) {
      float f5 = f * 1.05F;
      float f6 = 1.0F + 0.3F * f3;

      for (int i = 0; i < 3; i++) {
         int color = ints[i % ints.length];
         float f7 = method5056(floatValue, i);

         for (int j = 12; j >= 1; j--) {
            float f8 = f7 - j * 0.05F;
            float f9 = 1.0F - j / 13.0F;
            this.entry
               .method5077(
                  d + Mth.cos(f8) * f5,
                  method5057(d2, f2, floatValue - j * 0.04F, i),
                  d3 + Mth.sin(f8) * f5,
                  (0.05F + 0.12F * f9) * f4,
                  0.0F,
                  0.0F,
                  method5075(color, alpha * f9 * f9 * 0.3F)
               );
         }

         for (int j = 0; j < 4; j++) {
            float f8 = method5072(floatValue * 0.55F + j / 4.0F + i * 0.13F);
            float floatValue2 = floatValue - f8 / 0.55F;
            float f9 = method5056(floatValue2, i);
            float f10 = 1.0F + f8 * 0.25F;
            double d4 = d + Mth.cos(f9) * f5 * f10;
            double d5 = method5057(d2, f2, floatValue2, i) + f8 * 0.5F * f4;
            double d6 = d3 + Mth.sin(f9) * f5 * f10;
            float f11 = f8 < 0.15F ? method5074(f8 / 0.15F) : 1.0F - 0.35F * (f8 - 0.15F) / 0.85F;
            float f12 = 1.0F - f8;
            this.method5065(
               d4,
               d5,
               d6,
               0.11F * f4 * f11,
               Mth.sin(f8 * 7.0F + j * 2.0F) * 0.3F,
               this.method5066(f9),
               color,
               alpha * f12 * f12 * 0.95F
            );
         }

         double d4 = d + Mth.cos(f7) * f5;
         double d5 = method5057(d2, f2, floatValue, i);
         double d6 = d3 + Mth.sin(f7) * f5;
         float f8 = 0.3F * f4 * method5070(floatValue / 1.1F + i * 0.31F) * f6;
         this.entry.method5077(d4, d5, d6, f8 * 1.6F, 0.0F, 0.0F, method5075(color, alpha * (0.2F + 0.25F * f3)));
         this.method5065(d4, d5, d6, f8, Mth.sin(floatValue * 1.3F + i * 2.0F) * 0.12F, this.method5066(f7), color, alpha);
      }

      if (f3 > 0.01F) {
         float easeOut2 = easeOut(1.0F - f3);
         float f7 = f * (0.35F + easeOut2 * 1.2F);
         double d4 = d2 + f2 * 0.62F + easeOut2 * 0.35F * f4;

         for (int i = 0; i < 6; i++) {
            float f8 = i / 6.0F * (float) (Math.PI * 2) + 0.4F;
            this.method5065(
               d + Mth.cos(f8) * f7,
               d4,
               d3 + Mth.sin(f8) * f7,
               0.14F * f4 * (1.0F - 0.35F * easeOut2),
               (i - 2.5F) * 0.1F,
               this.method5066(f8),
               ints[1],
               f3 * alpha
            );
         }
      }
   }

   private static float method5056(float floatValue, int n) {
      return floatValue * 1.25F + n * (float) (Math.PI * 2) / 3.0F;
   }

   private static double method5057(double d, float f, float floatValue, int n) {
      return d + f * 0.56F + Mth.sin(floatValue * 1.4F + n * 2.1F) * f * 0.1F;
   }

   private void method5058(double d, double d2, double d3, float f, float f2, float floatValue, int[] ints, float alpha, float f3, float f4) {
      double d4 = d2 + f2 * 0.55F;
      float f5 = f * 1.05F * (1.0F + 0.12F * f3);
      float f6 = 1.05F;
      float f7 = Mth.cos(f6);
      float f8 = Mth.sin(f6);

      for (int i = 0; i < 3; i++) {
         float f9 = i * (float) (Math.PI * 2) / 3.0F + floatValue * 0.25F;
         float f10 = Mth.cos(f9);
         float f11 = Mth.sin(f9);
         double d5 = -f11 * f7;
         double d6 = f8;
         double d7 = f10 * f7;
         int color = ints[i % ints.length];

         for (int j = 0; j < 72; j++) {
            float f12 = j / 72.0F * (float) (Math.PI * 2);
            float f13 = Mth.cos(f12) * f5;
            float f14 = Mth.sin(f12) * f5;
            this.entry
               .method5077(
                  d + f10 * f13 + d5 * f14,
                  d4 + d6 * f14,
                  d3 + f11 * f13 + d7 * f14,
                  0.05F * f4,
                  0.0F,
                  0.0F,
                  method5075(color, alpha * 0.07F)
               );
         }

         float f12 = floatValue * 2.2F + i * 2.1F;

         for (int j = 40; j >= 0; j--) {
            float f13 = f12 - j * 0.04F;
            float f14 = Mth.cos(f13) * f5;
            float f15 = Mth.sin(f13) * f5;
            double d8 = d + f10 * f14 + d5 * f15;
            double d9 = d4 + d6 * f15;
            double d10 = d3 + f11 * f14 + d7 * f15;
            if (j == 0) {
               this.entry.method5077(d8, d9, d10, 0.4F * f4, 0.0F, 0.0F, method5075(color, alpha * 0.3F * (1.0F + 0.8F * f3)));
               this.entry3.method5077(d8, d9, d10, 0.1F * f4 * (1.0F + 0.4F * f3), 0.0F, 0.0F, method5075(method5076(color, -1, 0.35F), alpha));
            } else {
               float alpha1 = 1.0F - j / 41.0F;
               this.entry
                  .method5077(d8, d9, d10, Mth.lerp(alpha1, 0.03F, 0.2F) * f4, 0.0F, 0.0F, method5075(color, alpha * alpha1 * alpha1 * 0.6F));
            }
         }
      }
   }

   private void method5059(double d, double d2, double d3, float f, float f2, float floatValue, int[] ints, float alpha, float f3, float f4) {
      float f5 = f * 0.9F;
      float f6 = alpha * 1.2F;

      for (int i = 0; i <= 48; i++) {
         float f7 = i / 48.0F;
         float f8 = Mth.clamp((f6 - f7) * 8.0F, 0.0F, 1.0F);
         if (f8 <= 0.0F) {
            break;
         }

         float f9 = Math.min(1.0F, Math.min(f7, 1.0F - f7) * 6.0F + 0.2F) * f8;
         float f10 = 0.5F + 0.5F * Mth.sin(f7 * (float) (Math.PI * 2) * 1.5F - floatValue * 2.6F);
         float f11 = method5073(Mth.clamp((f10 - 0.7F) / 0.3F, 0.0F, 1.0F));
         float f12 = f7 * (float) (Math.PI * 2) * 1.5F + floatValue * 1.4F;
         float f13 = f5 * (1.0F + 0.12F * f3 * Mth.sin(f7 * (float) Math.PI));
         double d4 = d2 + f7 * f2 * 1.05F - 0.02F;
         double d5 = 0.0;
         double d6 = 0.0;
         double d7 = 0.0;
         double d8 = 0.0;

         for (int j = 0; j < 2; j++) {
            float f14 = f12 + j * (float) Math.PI;
            double d9 = d + Mth.cos(f14) * f13;
            double d10 = d3 + Mth.sin(f14) * f13;
            int color = ints[j * 2 % ints.length];
            this.entry.method5077(d9, d4, d10, (0.07F + 0.05F * f10) * f4, 0.0F, 0.0F, method5075(color, alpha * f9 * (0.22F + 0.4F * f10)));
            if (f11 > 0.01F) {
               this.entry3.method5077(d9, d4, d10, 0.065F * f4 * f11, 0.0F, 0.0F, method5075(method5076(color, -1, 0.3F), alpha * f9 * f11));
            }

            if (j == 0) {
               d5 = d9;
               d6 = d10;
            } else {
               d7 = d9;
               d8 = d10;
            }
         }

         if (i % 8 == 4) {
            for (int j = 1; j <= 5; j++) {
               float alpha1 = j / 6.0F;
               this.entry
                  .method5077(
                     d5 + (d7 - d5) * alpha1,
                     d4,
                     d6 + (d8 - d6) * alpha1,
                     0.04F * f4,
                     0.0F,
                     0.0F,
                     method5075(method5076(ints[0], ints[2 % ints.length], alpha1), alpha * f9 * 0.18F)
                  );
            }
         }
      }
   }

   private void method5060(double d, double d2, double d3, float f, float f2, float floatValue, int[] ints, float alpha, float f3, float f4) {
      double d4 = d2 + f2 * 0.58F + Mth.sin(floatValue * 1.2F) * 0.04F * f4;
      float f5 = floatValue * 0.3F;
      float f6 = Mth.cos(f5);
      float f7 = Mth.sin(f5);

      for (int i = 0; i < 2; i++) {
         float f8 = f * (1.15F + i * 0.18F) * (1.0F + 0.18F * f3);
         float f9 = i == 0 ? 0.38F : -0.38F;
         float f10 = Mth.cos(f9);
         float f11 = Mth.sin(f9);
         float f12 = floatValue * (i == 0 ? 1.1F : -0.9F);
         int color = ints[i * 2 % ints.length];

         for (int j = 0; j <= 64; j++) {
            boolean flag = j == 64;
            float f13 = flag ? f12 : j / 64.0F * (float) (Math.PI * 2);
            float f14 = Mth.cos(f13) * f8;
            float f15 = Mth.sin(f13) * f8;
            float f16;
            float f17;
            float f18;
            if (i == 0) {
               f16 = f14;
               f17 = -f15 * f11;
               f18 = f15 * f10;
            } else {
               f16 = f14 * f10;
               f17 = f14 * f11;
               f18 = f15;
            }

            double d5 = d + f16 * f6 - f18 * f7;
            double d6 = d3 + f16 * f7 + f18 * f6;
            if (flag) {
               this.entry.method5077(d5, d4 + f17, d6, 0.34F * f4, 0.0F, 0.0F, method5075(color, alpha * 0.3F * (1.0F + 0.6F * f3)));
               this.entry3.method5077(d5, d4 + f17, d6, 0.08F * f4, 0.0F, 0.0F, method5075(method5076(color, -1, 0.35F), alpha));
            } else {
               float f19 = method5071(f13, f12);
               this.entry
                  .method5077(
                     d5,
                     d4 + f17,
                     d6,
                     (0.05F + 0.1F * f19) * f4,
                     0.0F,
                     0.0F,
                     method5075(method5076(color, -1, f19 * 0.25F), alpha * (0.16F + 0.6F * f19))
                  );
            }
         }
      }
   }

   private void method5061(double d, double d2, double d3, float f, float f2, float floatValue, int[] ints, float alpha, float f3, float f4) {
      float f5 = f * 1.1F;

      for (int i = 0; i < 4; i++) {
         int color = ints[i % ints.length];

         for (int j = 24; j >= 1; j--) {
            float f6 = j * 0.045F;
            float f7 = 1.0F - j / 25.0F;
            this.entry
               .method5077(
                  method5062(d, f5, floatValue - f6, i),
                  method5063(d2, f2, floatValue - f6, i) - f6 * f6 * 0.9F,
                  method5064(d3, f5, floatValue - f6, i),
                  (0.03F + 0.1F * f7) * f4,
                  0.0F,
                  0.0F,
                  method5075(color, alpha * f7 * f7 * 0.5F)
               );
         }

         for (int j = 1; j <= 3; j++) {
            float f6 = j * 0.25F;
            float f7 = 1.0F - f6;
            float f8 = 0.75F + 0.25F * Mth.sin(floatValue * 3.0F + i + f6 * 5.0F);
            this.entry2
               .method5077(
                  method5062(d, f5, floatValue - f6, i),
                  method5063(d2, f2, floatValue - f6, i) - f6 * f6 * 0.9F,
                  method5064(d3, f5, floatValue - f6, i),
                  0.1F * f4 * f7,
                  f6 * 3.0F + i,
                  0.0F,
                  method5075(color, alpha * f7 * 0.7F * f8)
               );
         }

         double d4 = method5062(d, f5, floatValue, i);
         double d5 = method5063(d2, f2, floatValue, i);
         double d6 = method5064(d3, f5, floatValue, i);
         float f6 = 0.34F * f4 * (0.9F + 0.1F * Mth.sin(floatValue * 3.0F + i * 2.0F)) * (1.0F + 0.4F * f3);
         this.entry.method5077(d4, d5, d6, f6 * 1.5F, 0.0F, 0.0F, method5075(color, alpha * 0.3F));
         this.entry2.method5077(d4, d5, d6, f6, floatValue * 0.6F + i, 0.0F, method5075(method5076(color, -1, 0.2F + 0.4F * f3), alpha));
         this.entry3.method5077(d4, d5, d6, f6 * 0.2F, 0.0F, 0.0F, method5075(method5076(color, -1, 0.5F), alpha));
      }
   }

   private static double method5062(double d, float f, float floatValue, int n) {
      return d + Mth.cos(floatValue * 1.1F + n * (float) (Math.PI * 2) / 4.0F) * f;
   }

   private static double method5063(double d, float f, float floatValue, int n) {
      return d + f * 0.55F + Mth.sin(floatValue * 1.5F + n * 1.7F) * f * 0.16F;
   }

   private static double method5064(double d, float f, float floatValue, int n) {
      return d + Mth.sin(floatValue * 1.1F + n * (float) (Math.PI * 2) / 4.0F) * f;
   }

   private void method5065(double d, double d2, double d3, float f, float f2, float f3, int color, float alpha) {
      if (!(alpha < 0.01F) && !(f < 0.002F)) {
         this.entry4.method5078(d, d2, d3, f, f2, f3, method5075(color, alpha), 0.0F, 0.5F);
         this.entry4.method5078(d, d2, d3, f, f2, f3, method5075(-1, alpha * 0.9F), 0.5F, 1.0F);
      }
   }

   private float method5066(float f) {
      float f2 = (float)Math.atan2(Mth.cos(f), Mth.sin(f)) + this.floatValue;
      return 0.35F * Mth.sin(2.0F * f2);
   }

   private void method5067(Matrix4f pose, float f, float ang, boolean flag, boolean flag2) {
      RenderState.method1871();
      if (flag) {
         RenderState.method1878();
      } else {
         RenderState.method1879();
      }

      RenderState.method1882(false);
      RenderState.method1877();
      RenderState.method1898("core/position_tex_color");

      try {
         RenderState.method1875(BlendFactor.SRC_ALPHA, BlendFactor.ONE, BlendFactor.ZERO, BlendFactor.ONE);
         this.method5068(this.entry, identifier, pose, f, ang, false);
         this.method5068(this.entry2, flag2 ? identifier4 : identifier5, pose, f, ang, false);
         RenderState.method1875(BlendFactor.SRC_ALPHA, BlendFactor.ONE_MINUS_SRC_ALPHA, BlendFactor.ZERO, BlendFactor.ONE);
         this.method5068(this.entry3, flag2 ? identifier6 : identifier7, pose, f, ang, false);
         this.method5068(this.entry4, flag2 ? identifier2 : identifier3, pose, f, ang, true);
      } finally {
         RenderState.method1882(true);
         RenderState.method1878();
         RenderState.method1876();
         RenderState.method1872();
         RenderState.method1873();
      }
   }

   private void method5068(TargetEspTextures.Entry entry, Identifier identifier, Matrix4f pose, float f, float ang, boolean flag) {
      int count = entry.count;
      if (count != 0) {
         if (flag) {
            this.method5069(entry, pose);
         }

         RenderState.method1890(0, identifier);
         BufferBuilder begin = Tesselator.getInstance().begin(PrimitiveTopology.QUADS, com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_TEX_COLOR);

         for (int i = 0; i < count; i++) {
            int integer = flag ? this.integers[i] : i;
            int n = integer * 8;
            float[] floatValues = entry.floatValues;
            float y = floatValues[n + 3] * 0.5F;
            float f2 = floatValues[n + 6];
            float f3 = floatValues[n + 7];
            int intValue = entry.intValues[integer];
            this.matrix4f
               .set(pose)
               .translate(floatValues[n], floatValues[n + 1], floatValues[n + 2])
               .rotateY(-f + floatValues[n + 5])
               .rotateX(ang)
               .rotateZ(floatValues[n + 4]);
            begin.addVertex(this.matrix4f, -y, y, 0.0F).setUv(f2, 0.0F).setColor(intValue);
            begin.addVertex(this.matrix4f, -y, -y, 0.0F).setUv(f2, 1.0F).setColor(intValue);
            begin.addVertex(this.matrix4f, y, -y, 0.0F).setUv(f3, 1.0F).setColor(intValue);
            begin.addVertex(this.matrix4f, y, y, 0.0F).setUv(f3, 0.0F).setColor(intValue);
         }

         MeshData orThrow = begin.build();
         if (orThrow != null) {
            ImmediateRenderer.method8015(orThrow);
         }
      }
   }

   private void method5069(TargetEspTextures.Entry entry, Matrix4f pose) {
      int count = entry.count;
      if (this.integers.length < count) {
         this.integers = new Integer[count * 2];
         this.floatValues = new float[count * 2];
      }

      for (int i = 0; i < count; i++) {
         int n = i * 8;
         pose.transformPosition(entry.floatValues[n], entry.floatValues[n + 1], entry.floatValues[n + 2], this.vector3f);
         this.floatValues[i] = this.vector3f.z + (i & 1) * 1.0E-4F;
         this.integers[i] = i;
      }

      float[] floatValues = this.floatValues;
      Arrays.sort(this.integers, 0, count, (var1x, var2x) -> Float.compare(floatValues[var1x], floatValues[var2x]));
   }

   private static float method5070(float f) {
      float f2 = f - (float)Math.floor(f);
      float f3 = (f2 - 0.1F) / 0.09F;
      float f4 = (f2 - 0.33F) / 0.1F;
      return 1.0F + 0.09F * (float)Math.exp(-f3 * f3) + 0.05F * (float)Math.exp(-f4 * f4);
   }

   private static float method5071(float f, float f2) {
      float f3 = f - f2;
      f3 -= (float) (Math.PI * 2) * (float)Math.floor(f3 / (float) (Math.PI * 2) + 0.5F);
      return (float)Math.exp(-f3 * f3 * 2.0F);
   }

   private static float method5072(float f) {
      return f - (float)Math.floor(f);
   }

   private static float method5073(float f) {
      return f * f * (3.0F - 2.0F * f);
   }

   private static float easeOut(float alpha) {
      float f = 1.0F - alpha;
      return 1.0F - f * f * f;
   }

   private static float method5074(float f) {
      if (f <= 0.0F) {
         return 0.0F;
      } else {
         float f2 = f - 1.0F;
         return 1.0F + 2.70158F * f2 * f2 * f2 + 1.70158F * f2 * f2;
      }
   }

   private static int method5075(int color, float alpha) {
      return ColorUtils.method6439(color, Mth.clamp(alpha, 0.0F, 1.0F));
   }

   private static int method5076(int color, int n, float alpha1) {
      int lerpInt = Mth.lerpInt(alpha1, color >> 16 & 0xFF, n >> 16 & 0xFF);
      int lerpInt2 = Mth.lerpInt(alpha1, color >> 8 & 0xFF, n >> 8 & 0xFF);
      int lerpInt3 = Mth.lerpInt(alpha1, color & 0xFF, n & 0xFF);
      return color & 0xFF000000 | lerpInt << 16 | lerpInt2 << 8 | lerpInt3;
   }

   private static final class Entry {
      public static final int CONST_intValue = 8;
      public float[] floatValues = new float[4096];
      public int[] intValues = new int[512];
      public int count;

      public void method5077(double d, double d2, double d3, float f, float f2, float f3, int n) {
         this.method5078(d, d2, d3, f, f2, f3, n, 0.0F, 1.0F);
      }

      public void method5078(double d, double d2, double d3, float f, float f2, float f3, int n, float f4, float f5) {
         if (n >>> 24 != 0 && !(f <= 0.001F)) {
            if (this.count == this.intValues.length) {
               this.floatValues = Arrays.copyOf(this.floatValues, this.floatValues.length * 2);
               this.intValues = Arrays.copyOf(this.intValues, this.intValues.length * 2);
            }

            int n2 = this.count * 8;
            this.floatValues[n2] = (float)d;
            this.floatValues[n2 + 1] = (float)d2;
            this.floatValues[n2 + 2] = (float)d3;
            this.floatValues[n2 + 3] = f;
            this.floatValues[n2 + 4] = f2;
            this.floatValues[n2 + 5] = f3;
            this.floatValues[n2 + 6] = f4;
            this.floatValues[n2 + 7] = f5;
            this.intValues[this.count++] = n;
         }
      }
   }
}
