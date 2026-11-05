package org.zenith.utility.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.zenith.utility.misc.Class15;

public final class BoneMatrices {
   private static final int CONST_intValue = 15728880;
   private static final int CONST_intValue2 = -1;
   private static Matrix4f[] matrix4fs = new Matrix4f[0];
   private static Matrix3f[] matrix3fs = new Matrix3f[0];
   private static final Quaternionf quaternionf = new Quaternionf();
   private static final Vector3f vector3f = new Vector3f();
   private static final Vector3f vector3f2 = new Vector3f();
   private static final int CONST_intValue3 = 24;
   private static final int CONST_intValue4 = 4;
   private static final Vector3f vector3f3 = new Vector3f(-0.35F, 0.78F, 0.52F).normalize();
   private static final Vector3f vector3f4 = new Vector3f(0.55F, 0.35F, 0.76F).normalize();
   private static final Vector3f[] vector3fs = new Vector3f[]{new Vector3f(), new Vector3f(), new Vector3f(), new Vector3f()};
   private static final float[] CONST_floatValues = new float[8];
   private static final Vector3f vector3f5 = new Vector3f();
   private static final Vector3f vector3f6 = new Vector3f();
   private static final Vector3f vector3f7 = new Vector3f();
   private static final Vector3f vector3f8 = new Vector3f();
   private static final Vector3f vector3f9 = new Vector3f();
   private static final Map<Identifier, RenderType> items = new HashMap<>();
   private static final int CONST_intValue5 = 25;
   private static final float[] CONST_floatValues2 = new float[25];
   private static final float[] CONST_floatValues3 = new float[25];
   private static final float[] CONST_floatValues4 = new float[25];
   private static final float[] CONST_floatValues5 = new float[25];
   private static final float[] CONST_floatValues6 = new float[25];
   private static final int[] CONST_intValues = new int[25];

   private BoneMatrices() {
   }

   public static void method4787(
      Class15 key, float[] floatValues, int intValue, PoseStack poseStack, SubmitNodeCollector orderedRenderCommandQueue, int lightCoords, int overlayCoords, boolean flag, boolean flag2, int n
   ) {
      if (key != null && key.intValue3 != 0 && orderedRenderCommandQueue != null && key.identifier != null) {
         if (key.method2033(intValue)) {
            method4788(key, floatValues == null ? null : (float[])floatValues.clone(), intValue, poseStack, orderedRenderCommandQueue, lightCoords, overlayCoords, flag, flag2, n);
         }
      }
   }

   public static void method4788(
      Class15 key, float[] floats, int intValue, PoseStack poseStack, SubmitNodeCollector orderedRenderCommandQueue, int lightCoords, int overlayCoords, boolean flag, boolean flag2, int n
   ) {
      if (key != null && key.intValue3 != 0 && orderedRenderCommandQueue != null && key.identifier != null) {
         if (key.method2033(intValue)) {
            method4796(key, floats, intValue, poseStack, orderedRenderCommandQueue, lightCoords, overlayCoords, flag, BoneMatrices.Type.SOLID, n);
            if (key.method2034(2)) {
               method4796(key, floats, intValue, poseStack, orderedRenderCommandQueue, lightCoords, overlayCoords, flag, BoneMatrices.Type.TRANSLUCENT, n);
            }

            if (flag2 && key.method2034(1)) {
               method4796(key, floats, intValue, poseStack, orderedRenderCommandQueue, lightCoords, overlayCoords, flag, BoneMatrices.Type.GLOW, n);
            }

            if (method4789(key)) {
               method4796(key, floats, intValue, poseStack, orderedRenderCommandQueue, lightCoords, overlayCoords, flag, BoneMatrices.Type.SHINE, n);
            }
         }
      }
   }

   private static boolean method4789(Class15 key) {
      return key.method2034(8) || key.method2034(16);
   }

   public static void method4790(
      Class15 key,
      float[] floats,
      int ordinal,
      PoseStack poseStack,
      SubmitNodeCollector orderedRenderCommandQueue,
      int lightCoords,
      int overlayCoords,
      boolean flag,
      ModelPart modelPart,
      Consumer<PoseStack> consumer
   ) {
      method4791(key, floats, ordinal, poseStack, orderedRenderCommandQueue, lightCoords, overlayCoords, flag, modelPart, consumer, false);
   }

   public static void method4791(
      Class15 key,
      float[] floats,
      int ordinal,
      PoseStack poseStack,
      SubmitNodeCollector orderedRenderCommandQueue,
      int lightCoords,
      int overlayCoords,
      boolean flag,
      ModelPart modelPart,
      Consumer<PoseStack> consumer,
      boolean flag2
   ) {
      if (key != null && key.intValue3 != 0 && orderedRenderCommandQueue != null && key.identifier != null && key.method2033(ordinal)) {
         float[] floats2 = floats == null ? null : (float[])floats.clone();
         method4792(key, floats2, ordinal, poseStack, orderedRenderCommandQueue, lightCoords, overlayCoords, BoneMatrices.Type.SOLID, modelPart, consumer, flag2);
         if (key.method2034(2)) {
            method4792(key, floats2, ordinal, poseStack, orderedRenderCommandQueue, lightCoords, overlayCoords, BoneMatrices.Type.TRANSLUCENT, modelPart, consumer, flag2);
         }

         if (flag && key.method2034(1)) {
            method4792(key, floats2, ordinal, poseStack, orderedRenderCommandQueue, lightCoords, overlayCoords, BoneMatrices.Type.GLOW, modelPart, consumer, flag2);
         }

         if (method4789(key)) {
            method4792(key, floats2, ordinal, poseStack, orderedRenderCommandQueue, lightCoords, overlayCoords, BoneMatrices.Type.SHINE, modelPart, consumer, flag2);
         }
      }
   }

   private static void method4792(
      Class15 key,
      float[] floats,
      int ordinal,
      PoseStack poseStack,
      SubmitNodeCollector orderedRenderCommandQueue,
      int lightCoords,
      int overlayCoords,
      BoneMatrices.Type type,
      ModelPart modelPart,
      Consumer<PoseStack> consumer,
      boolean flag
   ) {
      method4794(orderedRenderCommandQueue, type).submitCustomGeometry(poseStack, method4795(key, type, -1), (var9x, var10x) -> {
         PoseStack poseStack2 = new PoseStack();
         poseStack2.last().pose().set(var9x.pose());
         poseStack2.last().normal().set(var9x.normal());
         modelPart.translateAndRotate(poseStack2);
         if (consumer != null) {
            consumer.accept(poseStack2);
         }

         method4798(key, floats, ordinal, poseStack2.last(), var10x, lightCoords, overlayCoords, true, type, -1, flag);
      });
   }

   private static OrderedSubmitNodeCollector method4794(SubmitNodeCollector orderedRenderCommandQueue, BoneMatrices.Type type) {
      return (OrderedSubmitNodeCollector)(switch (type) {
         case TRANSLUCENT -> orderedRenderCommandQueue.order(1);
         case SHINE -> orderedRenderCommandQueue.order(2);
         default -> orderedRenderCommandQueue;
      });
   }

   private static RenderType method4795(Class15 key, BoneMatrices.Type type, int n) {
      return switch (type) {
         case SOLID -> n >>> 24 < 255 ? RenderTypes.entityTranslucent(key.identifier) : RenderTypes.entityCutout(key.identifier);
         case TRANSLUCENT -> RenderTypes.entityTranslucent(key.identifier);
         case GLOW -> RenderTypes.eyes(key.identifier);
         case SHINE -> (RenderType)items.computeIfAbsent(key.identifier, var0x -> RenderTypes.energySwirl(var0x, 0.0F, 0.0F));
      };
   }

   private static void method4796(
      Class15 key,
      float[] floats,
      int intValue,
      PoseStack poseStack,
      SubmitNodeCollector orderedRenderCommandQueue,
      int lightCoords,
      int overlayCoords,
      boolean flag,
      BoneMatrices.Type type,
      int n
   ) {
      RenderType renderType = method4795(key, type, n);
      method4794(orderedRenderCommandQueue, type).submitCustomGeometry(poseStack, renderType, (var8x, var9x) -> method4797(key, floats, intValue, var8x, var9x, lightCoords, overlayCoords, flag, type, n));
   }

   private static void method4797(
      Class15 key, float[] floats, int intValue, Pose pose, VertexConsumer consumer, int lightCoords, int overlayCoords, boolean flag, BoneMatrices.Type type, int n
   ) {
      method4798(key, floats, intValue, pose, consumer, lightCoords, overlayCoords, flag, type, n, false);
   }

   private static void method4798(
      Class15 key, float[] floats, int ordinal, Pose pose, VertexConsumer consumer, int lightCoords, int overlayCoords, boolean flag, BoneMatrices.Type type, int n, boolean flag2
   ) {
      int n2 = key.method2030();
      method4809(n2);

      for (int i = 0; i < n2; i++) {
         if (key.intValues3[i] == ordinal) {
            Matrix4f matrix4f = matrix4fs[i];
            Matrix3f matrix3f = matrix3fs[i];
            int intValue = key.intValues[i];
            if (intValue >= 0) {
               matrix4f.set(matrix4fs[intValue]);
               matrix3f.set(matrix3fs[intValue]);
            } else {
               matrix4f.set(pose.pose());
               matrix3f.set(pose.normal());
            }

            int n3 = i * 9;
            float angleX = floats == null ? key.floatValues2[i * 3] : floats[n3];
            float angleY = floats == null ? key.floatValues2[i * 3 + 1] : floats[n3 + 1];
            float angleZ = floats == null ? key.floatValues2[i * 3 + 2] : floats[n3 + 2];
            float f = floats == null ? 0.0F : floats[n3 + 3];
            float f2 = floats == null ? 0.0F : floats[n3 + 4];
            float f3 = floats == null ? 0.0F : floats[n3 + 5];
            float x = floats == null ? 1.0F : floats[n3 + 6];
            float y = floats == null ? 1.0F : floats[n3 + 7];
            float z = floats == null ? 1.0F : floats[n3 + 8];
            boolean flag3 = intValue < 0 && key.intValues2[i] >= 0;
            float floatValue = flag3 ? 0.0F : key.floatValues[i * 3];
            float floatValue2 = flag3 ? 0.0F : key.floatValues[i * 3 + 1];
            float floatValue3 = flag3 ? 0.0F : key.floatValues[i * 3 + 2];
            matrix4f.translate((floatValue + f) / 16.0F, (floatValue2 + f2) / 16.0F, (floatValue3 + f3) / 16.0F);
            if (angleX != 0.0F || angleY != 0.0F || angleZ != 0.0F) {
               quaternionf.rotationZYX(angleZ, angleY, angleX);
               matrix4f.rotate(quaternionf);
               matrix3f.rotate(quaternionf);
            }

            if (x != 1.0F || y != 1.0F || z != 1.0F) {
               matrix4f.scale(x, y, z);
            }
         }
      }

      int n3 = key.method2028(ordinal);

      for (int i = key.method2029(ordinal); n3 < i; n3++) {
         int n4 = key.intValues4[n3];
         if (key.intValues3[n4] == ordinal && (!flag2 || key.intValues[n4] < 0)) {
            int n5 = key.intValues5[n3];
            if (flag || (n5 & 4) == 0) {
               boolean flag3 = (n5 & 2) != 0;
               boolean flag4 = (n5 & 1) != 0;
               switch (type) {
                  case SOLID:
                     if (flag3) {
                        continue;
                     }
                     break;
                  case TRANSLUCENT:
                     if (!flag3) {
                        continue;
                     }
                     break;
                  case GLOW:
                     if (!flag4) {
                        continue;
                     }
                     break;
                  case SHINE:
                     if ((n5 & 24) == 0) {
                        continue;
                     }
               }

               if (floats == null || floats[n4 * 9 + 6] != 0.0F || floats[n4 * 9 + 7] != 0.0F) {
                  Matrix4f matrix4f = matrix4fs[n4];
                  Matrix3f matrix3f = matrix3fs[n4];
                  vector3f2.set(key.floatValues6[n3 * 3], key.floatValues6[n3 * 3 + 1], key.floatValues6[n3 * 3 + 2]);
                  matrix3f.transform(vector3f2);
                  if (vector3f2.lengthSquared() > 1.0E-8F) {
                     vector3f2.normalize();
                  } else {
                     vector3f2.set(0.0F, 1.0F, 0.0F);
                  }

                  int lightCoords2 = !flag4 && type != BoneMatrices.Type.GLOW ? lightCoords : 15728880;
                  int color = type == BoneMatrices.Type.GLOW ? -1 : n;
                  boolean flag5 = (n5 & 8) != 0;
                  if (type == BoneMatrices.Type.SHINE || type == BoneMatrices.Type.TRANSLUCENT && flag5) {
                     for (int j = 0; j < 4; j++) {
                        int n6 = n3 * 12 + j * 3;
                        vector3fs[j].set(key.floatValues4[n6], key.floatValues4[n6 + 1], key.floatValues4[n6 + 2]);
                        matrix4f.transformPosition(vector3fs[j]);
                        CONST_floatValues[j * 2] = key.floatValues5[n3 * 8 + j * 2];
                        CONST_floatValues[j * 2 + 1] = key.floatValues5[n3 * 8 + j * 2 + 1];
                     }

                     if (type == BoneMatrices.Type.SHINE && !flag5) {
                        method4805(consumer, n3, n, overlayCoords, lightCoords);
                     } else {
                        method4804(consumer, type == BoneMatrices.Type.SHINE, overlayCoords, lightCoords2, n);
                     }
                  } else {
                     for (int j = 0; j < 4; j++) {
                        int n6 = n3 * 12 + j * 3;
                        vector3f.set(key.floatValues4[n6], key.floatValues4[n6 + 1], key.floatValues4[n6 + 2]);
                        matrix4f.transformPosition(vector3f);
                        consumer.addVertex(
                           vector3f.x,
                           vector3f.y,
                           vector3f.z,
                           color,
                           key.floatValues5[n3 * 8 + j * 2],
                           key.floatValues5[n3 * 8 + j * 2 + 1],
                           overlayCoords,
                           lightCoords2,
                           vector3f2.x,
                           vector3f2.y,
                           vector3f2.z
                        );
                     }
                  }
               }
            }
         }
      }
   }

   private static void method4799() {
      vector3f5.set(vector3f2);
   }

   private static float method4800(Vector3f vector3f6, Vector3f vector3f4, Vector3f vector3f7, float f) {
      vector3f8.set(vector3f4).add(vector3f7);
      if (vector3f8.lengthSquared() < 1.0E-8F) {
         return 0.0F;
      } else {
         vector3f8.normalize();
         return method4801(Math.max(0.0F, vector3f6.dot(vector3f8)), (int)f);
      }
   }

   private static float method4801(float f, int n) {
      float f2 = 1.0F;
      float f3 = f;

      while (n > 0) {
         if ((n & 1) != 0) {
            f2 *= f3;
         }

         f3 *= f3;
         n >>= 1;
      }

      return f2;
   }

   private static void method4802(Vector3f vector3f9, Vector3f vector3f7) {
      vector3f7.set(vector3f9).negate();
      if (vector3f7.lengthSquared() > 1.0E-8F) {
         vector3f7.normalize();
      } else {
         vector3f7.set(0.0F, 0.0F, 1.0F);
      }
   }

   private static float method4803(int lightCoords) {
      if (lightCoords == 15728880) {
         return 1.0F;
      } else {
         float f = (lightCoords >> 4 & 15) / 15.0F;
         float f2 = (lightCoords >> 20 & 15) / 15.0F;
         ClientLevel level = Minecraft.getInstance().level;
         if (level != null) {
            f2 *= 1.0F - level.getSkyDarken() / 11.0F * 0.75F;
         }

         return Math.max(0.15F, Math.min(1.0F, Math.max(f, f2)));
      }
   }

   private static void method4804(VertexConsumer consumer, boolean flag, int overlayCoords, int lightCoords, int n) {
      method4799();
      float f = Math.max(vector3fs[0].distance(vector3fs[1]), vector3fs[1].distance(vector3fs[2]));
      int n2 = Math.max(1, Math.min(4, (int)Math.ceil(f / 0.06F)));
      float f2 = (n >>> 24 & 0xFF) / 255.0F;
      float f3 = method4803(lightCoords);
      int n3 = n2 + 1;

      for (int i = 0; i <= n2; i++) {
         for (int j = 0; j <= n2; j++) {
            float f4 = (float)j / n2;
            float f5 = (float)i / n2;
            method4806(f4, f5, vector3f9);
            method4802(vector3f9, vector3f7);
            float f6 = Math.abs(vector3f5.dot(vector3f7));
            float f7 = method4801(1.0F - f6, 5);
            int n4 = i * n3 + j;
            CONST_floatValues2[n4] = vector3f9.x;
            CONST_floatValues3[n4] = vector3f9.y;
            CONST_floatValues4[n4] = vector3f9.z;
            CONST_floatValues5[n4] = method4807(CONST_floatValues, 0, f4, f5);
            CONST_floatValues6[n4] = method4807(CONST_floatValues, 1, f4, f5);
            if (flag) {
               vector3f8.set(vector3f3).add(vector3f7);
               float f8;
               if (vector3f8.lengthSquared() < 1.0E-8F) {
                  f8 = 0.0F;
               } else {
                  vector3f8.normalize();
                  float f9 = Math.max(0.0F, vector3f5.dot(vector3f8));
                  f8 = method4801(f9, 60) * 0.85F + method4801(f9, 10) * 0.12F;
               }

               float f9 = method4800(vector3f5, vector3f4, vector3f7, 40.0F) * 0.35F;
               float f10 = Math.min(1.0F, (f8 + f9 + f7 * 0.15F) * f2 * f3);
               int n5 = (int)(f10 * 255.0F);
               CONST_intValues[n4] = 0xFF000000 | n5 << 16 | n5 << 8 | n5;
            } else {
               float f8 = Math.min(1.0F, 0.05F + f7 * 0.2F) * f2;
               CONST_intValues[n4] = (int)(f8 * 255.0F) << 24 | 16777215;
            }
         }
      }

      int lightCoords2 = flag ? 15728880 : lightCoords;

      for (int i = 0; i < n2; i++) {
         for (int j = 0; j < n2; j++) {
            for (int k = 0; k < 4; k++) {
               int n4 = (i + (k >= 2 ? 1 : 0)) * n3 + j + (k != 1 && k != 2 ? 0 : 1);
               consumer.addVertex(
                  CONST_floatValues2[n4],
                  CONST_floatValues3[n4],
                  CONST_floatValues4[n4],
                  CONST_intValues[n4],
                  CONST_floatValues5[n4],
                  CONST_floatValues6[n4],
                  overlayCoords,
                  lightCoords2,
                  vector3f2.x,
                  vector3f2.y,
                  vector3f2.z
               );
            }
         }
      }
   }

   private static void method4805(VertexConsumer consumer, int n, int n2, int overlayCoords, int lightCoords) {
      method4799();
      int n3 = n * -1640531535;
      n3 ^= n3 >>> 15;
      n3 *= -2048144777;
      n3 ^= n3 >>> 13;
      float x = ((n3 & 0xFF) / 255.0F - 0.5F) * 0.9F;
      float y = ((n3 >>> 8 & 0xFF) / 255.0F - 0.5F) * 0.9F;
      float z = ((n3 >>> 16 & 0xFF) / 255.0F - 0.5F) * 0.9F;
      vector3f6.set(vector3f5).add(x, y, z);
      if (!(vector3f6.lengthSquared() < 1.0E-8F)) {
         vector3f6.normalize();
         vector3f9.set(vector3fs[0]).add(vector3fs[1]).add(vector3fs[2]).add(vector3fs[3]).mul(0.25F);
         method4802(vector3f9, vector3f7);
         float f = method4800(vector3f6, vector3f3, vector3f7, 16.0F) * 0.9F + method4800(vector3f6, vector3f4, vector3f7, 16.0F) * 0.45F;
         float f2 = (n2 >>> 24 & 0xFF) / 255.0F;
         float f3 = Math.min(1.0F, f * f2 * method4803(lightCoords));
         if (!(f3 < 0.02F)) {
            int n4 = (int)(f3 * 255.0F);
            int color = 0xFF000000 | n4 << 16 | n4 << 8 | n4;
            float f4 = 0.0012F;

            for (int i = 0; i < 4; i++) {
               Vector3f vector3f = vector3fs[i];
               float x2 = vector3f9.x + (vector3f.x - vector3f9.x) * 1.12F + vector3f2.x * f4;
               float y2 = vector3f9.y + (vector3f.y - vector3f9.y) * 1.12F + vector3f2.y * f4;
               float z2 = vector3f9.z + (vector3f.z - vector3f9.z) * 1.12F + vector3f2.z * f4;
               consumer.addVertex(
                  x2,
                  y2,
                  z2,
                  color,
                  CONST_floatValues[i * 2],
                  CONST_floatValues[i * 2 + 1],
                  overlayCoords,
                  15728880,
                  vector3f2.x,
                  vector3f2.y,
                  vector3f2.z
               );
            }
         }
      }
   }

   private static void method4806(float f, float f2, Vector3f vector3f9) {
      float f3 = vector3fs[0].x + (vector3fs[1].x - vector3fs[0].x) * f;
      float f4 = vector3fs[0].y + (vector3fs[1].y - vector3fs[0].y) * f;
      float f5 = vector3fs[0].z + (vector3fs[1].z - vector3fs[0].z) * f;
      float f6 = vector3fs[3].x + (vector3fs[2].x - vector3fs[3].x) * f;
      float f7 = vector3fs[3].y + (vector3fs[2].y - vector3fs[3].y) * f;
      float f8 = vector3fs[3].z + (vector3fs[2].z - vector3fs[3].z) * f;
      vector3f9.set(f3 + (f6 - f3) * f2, f4 + (f7 - f4) * f2, f5 + (f8 - f5) * f2);
   }

   private static float method4807(float[] floats, int n, float f, float f2) {
      float f3 = floats[n] + (floats[2 + n] - floats[n]) * f;
      float f4 = floats[6 + n] + (floats[4 + n] - floats[6 + n]) * f;
      return f3 + (f4 - f3) * f2;
   }

   public static void method4808(Class15 key, float[] floats, int intValue, Matrix4f matrix4f) {
      int[] ints = new int[key.method2030()];
      int n = 0;

      for (int i = intValue; i >= 0 && n < ints.length; i = key.intValues[i]) {
         ints[n++] = i;
      }

      for (int i = n - 1; i >= 0; i--) {
         int n2 = ints[i];
         int n3 = n2 * 9;
         boolean flag = key.intValues[n2] < 0 && key.intValues2[n2] >= 0;
         float floatValue = flag ? 0.0F : key.floatValues[n2 * 3];
         float f = flag ? 0.0F : key.floatValues[n2 * 3 + 1];
         float floatValue2 = flag ? 0.0F : key.floatValues[n2 * 3 + 2];
         float f2 = floats == null ? 0.0F : floats[n3 + 3];
         float f3 = floats == null ? 0.0F : floats[n3 + 4];
         float f4 = floats == null ? 0.0F : floats[n3 + 5];
         matrix4f.translate((floatValue + f2) / 16.0F, (f + f3) / 16.0F, (floatValue2 + f4) / 16.0F);
         float angleX = floats == null ? key.floatValues2[n2 * 3] : floats[n3];
         float angleY = floats == null ? key.floatValues2[n2 * 3 + 1] : floats[n3 + 1];
         float angleZ = floats == null ? key.floatValues2[n2 * 3 + 2] : floats[n3 + 2];
         matrix4f.rotateZYX(angleZ, angleY, angleX);
         if (floats != null) {
            matrix4f.scale(floats[n3 + 6], floats[n3 + 7], floats[n3 + 8]);
         }
      }
   }

   private static void method4809(int n) {
      if (matrix4fs.length < n) {
         Matrix4f[] matrixes = new Matrix4f[n];
         Matrix3f[] matrix3fs2 = new Matrix3f[n];
         System.arraycopy(matrix4fs, 0, matrixes, 0, matrix4fs.length);
         System.arraycopy(matrix3fs, 0, matrix3fs2, 0, matrix3fs.length);

         for (int i = matrix4fs.length; i < n; i++) {
            matrixes[i] = new Matrix4f();
            matrix3fs2[i] = new Matrix3f();
         }

         matrix4fs = matrixes;
         matrix3fs = matrix3fs2;
      }
   }

   private static enum Type {
      SOLID,
      TRANSLUCENT,
      GLOW,
      SHINE;
   }
}
