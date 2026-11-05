package org.zenith;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fc;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

public final class MatrixUtils {
   private MatrixUtils() {
   }

   public static Matrix4f method663(GuiGraphicsExtractor customDrawContext) {
      return method664(customDrawContext.pose());
   }

   public static Matrix4f method664(Matrix3x2fc pose) {
      return method665(pose, new Matrix4f());
   }

   public static Matrix4f method665(Matrix3x2fc pose, Matrix4f matrix) {
      matrix.identity();
      matrix.m00(pose.m00());
      matrix.m01(pose.m01());
      matrix.m10(pose.m10());
      matrix.m11(pose.m11());
      matrix.m30(pose.m20());
      matrix.m31(pose.m21());
      return matrix;
   }

   public static PoseStack method666(Matrix3x2fc pose) {
      PoseStack poseStack = new PoseStack();
      poseStack.mulPose(method664(pose));
      return poseStack;
   }

   public static PoseStack method667(GuiGraphicsExtractor guiGraphicsExtractor) {
      return method666(guiGraphicsExtractor.pose());
   }

   public static Matrix3x2f method668(Matrix4f matrix) {
      return new Matrix3x2f(matrix.m00(), matrix.m01(), matrix.m10(), matrix.m11(), matrix.m30(), matrix.m31());
   }

   public static void method669(Matrix3x2f pose, Matrix4fc matrix4f) {
      pose.set(matrix4f.m00(), matrix4f.m01(), matrix4f.m10(), matrix4f.m11(), matrix4f.m30(), matrix4f.m31());
   }
}
