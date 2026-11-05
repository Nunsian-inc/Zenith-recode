package org.zenith.utility.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.zenith.utility.api.CosmeticAnimation;
import org.zenith.utility.misc.CosmeticSkeleton;

public final class BoneTransforms {
   private static Matrix4f[] matrix4fs = new Matrix4f[0];
   private static Matrix3f[] matrix3fs = new Matrix3f[0];
   private static final float[] CONST_floatValues = new float[9];
   private static final Quaternionf quaternionf = new Quaternionf();
   private static final Vector3f vector3f = new Vector3f();
   private static final Vector3f vector3f2 = new Vector3f();

   private BoneTransforms() {
   }

   public static void method547(
      CosmeticSkeleton cosmeticSkeleton,
      Identifier[] ids,
      PoseStack poseStack,
      SubmitNodeCollector submitNodeCollector,
      int lightCoords,
      int overlayCoords,
      int color,
      float f,
      CosmeticAnimation cosmeticAnimation,
      boolean flag
   ) {
      if (cosmeticSkeleton != null && ids != null && ids.length != 0 && ids[0] != null && submitNodeCollector != null && cosmeticSkeleton.method7138() != 0) {
         CosmeticSkeleton.Data[] datas = cosmeticSkeleton.getDatas();
         if (datas.length != 0) {
            boolean flag2 = color >>> 24 < 255;
            int n = cosmeticSkeleton.method7138();
            int n2 = Integer.MIN_VALUE;

            for (int i = 0; i < n; i++) {
               int n3 = cosmeticSkeleton.method7140(i);
               if (n3 != n2) {
                  n2 = n3;
                  Identifier texture = ids[n3 >= 0 && n3 < ids.length ? n3 : 0];
                  if (texture == null) {
                     texture = ids[0];
                  }

                  RenderType entityTranslucent = flag2 ? RenderTypes.entityTranslucent(texture) : RenderTypes.entityCutout(texture);
                  submitNodeCollector.submitCustomGeometry(poseStack, entityTranslucent, (var9x, var10x) -> method548(cosmeticSkeleton, datas, var9x, var10x, n3, lightCoords, overlayCoords, color, f, cosmeticAnimation, flag));
               }
            }
         }
      }
   }

   private static void method548(
      CosmeticSkeleton cosmeticSkeleton,
      CosmeticSkeleton.Data[] datas,
      Pose pose,
      VertexConsumer consumer,
      int n,
      int lightCoords,
      int overlayCoords,
      int color,
      float f,
      CosmeticAnimation cosmeticAnimation,
      boolean flag
   ) {
      method550(datas.length);
      method549(cosmeticSkeleton, datas, pose, f, cosmeticAnimation, flag);
      int n2 = -1;
      Matrix4f matrix4f = matrix4fs[0];
      Matrix3f matrix3f = matrix3fs[0];
      int n3 = cosmeticSkeleton.method7138();

      for (int i = 0; i < n3; i++) {
         if (cosmeticSkeleton.method7140(i) == n) {
            int n4 = cosmeticSkeleton.method7139(i);
            if (n4 != n2) {
               n2 = n4;
               int n5 = n4 >= 0 && n4 < datas.length ? n4 : 0;
               matrix4f = matrix4fs[n5];
               matrix3f = matrix3fs[n5];
            }

            vector3f2.set(cosmeticSkeleton.method7144(i, 0), cosmeticSkeleton.method7144(i, 1), cosmeticSkeleton.method7144(i, 2));
            matrix3f.transform(vector3f2);
            if (vector3f2.lengthSquared() > 1.0E-8F) {
               vector3f2.normalize();
            } else {
               vector3f2.set(0.0F, 1.0F, 0.0F);
            }

            for (int j = 0; j < 4; j++) {
               vector3f.set(cosmeticSkeleton.method7141(i, j, 0), cosmeticSkeleton.method7141(i, j, 1), cosmeticSkeleton.method7141(i, j, 2));
               matrix4f.transformPosition(vector3f);
               consumer.addVertex(
                  vector3f.x,
                  vector3f.y,
                  vector3f.z,
                  color,
                  cosmeticSkeleton.method7142(i, j),
                  cosmeticSkeleton.method7143(i, j),
                  overlayCoords,
                  lightCoords,
                  vector3f2.x,
                  vector3f2.y,
                  vector3f2.z
               );
            }
         }
      }
   }

   private static void method549(CosmeticSkeleton cosmeticSkeleton, CosmeticSkeleton.Data[] datas, Pose pose, float f, CosmeticAnimation cosmeticAnimation, boolean flag) {
      for (int i = 0; i < datas.length; i++) {
         CosmeticSkeleton.Data data = datas[i];
         Matrix4f matrix4f = matrix4fs[i];
         Matrix3f matrix3f = matrix3fs[i];
         int intValue = data.getIntValue();
         if (intValue >= 0 && intValue < i) {
            matrix4f.set(matrix4fs[intValue]);
            matrix3f.set(matrix3fs[intValue]);
         } else {
            matrix4f.set(pose.pose());
            matrix3f.set(pose.normal());
         }

         float rotationX = data.getRotationX();
         float rotationY = data.getRotationY();
         float f2 = data.getFloatValue4();
         float f3 = 0.0F;
         float f4 = 0.0F;
         float f5 = 0.0F;
         float x = 1.0F;
         float y = 1.0F;
         float z = 1.0F;
         if (cosmeticAnimation != null && cosmeticAnimation.method7810(data.name(), f, flag, CONST_floatValues)) {
            rotationX += CONST_floatValues[0];
            rotationY += CONST_floatValues[1];
            f2 += CONST_floatValues[2];
            f3 = CONST_floatValues[3];
            f4 = CONST_floatValues[4];
            f5 = CONST_floatValues[5];
            x = CONST_floatValues[6];
            y = CONST_floatValues[7];
            z = CONST_floatValues[8];
         }

         matrix4f.translate(data.getFloatValue() + f3, data.getFloatValue2() + f4, data.getFloatValue3() + f5);
         if (rotationX != 0.0F || rotationY != 0.0F || f2 != 0.0F) {
            quaternionf.rotationXYZ((float)Math.toRadians(-rotationX), (float)Math.toRadians(rotationY), (float)Math.toRadians(-f2));
            matrix4f.rotate(quaternionf);
            matrix3f.rotate(quaternionf);
         }

         if (x != 1.0F || y != 1.0F || z != 1.0F) {
            matrix4f.scale(x, y, z);
         }

         matrix4f.translate(-data.getFloatValue(), -data.getFloatValue2(), -data.getFloatValue3());
      }
   }

   private static void method550(int length) {
      if (matrix4fs.length < length) {
         Matrix4f[] matrixes = new Matrix4f[length];
         Matrix3f[] matrix3fs2 = new Matrix3f[length];
         System.arraycopy(matrix4fs, 0, matrixes, 0, matrix4fs.length);
         System.arraycopy(matrix3fs, 0, matrix3fs2, 0, matrix3fs.length);

         for (int i = matrix4fs.length; i < length; i++) {
            matrixes[i] = new Matrix4f();
            matrix3fs2[i] = new Matrix3f();
         }

         matrix4fs = matrixes;
         matrix3fs = matrix3fs2;
      }
   }
}
