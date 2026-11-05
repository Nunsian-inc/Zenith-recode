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

   public static Matrix4f method663(GuiGraphicsExtractor var0) {
      return method664(var0.pose());
   }

   public static Matrix4f method664(Matrix3x2fc var0) {
      return method665(var0, new Matrix4f());
   }

   public static Matrix4f method665(Matrix3x2fc var0, Matrix4f var1) {
      var1.identity();
      var1.m00(var0.m00());
      var1.m01(var0.m01());
      var1.m10(var0.m10());
      var1.m11(var0.m11());
      var1.m30(var0.m20());
      var1.m31(var0.m21());
      return var1;
   }

   public static PoseStack method666(Matrix3x2fc var0) {
      PoseStack var1 = new PoseStack();
      var1.mulPose(method664(var0));
      return var1;
   }

   public static PoseStack method667(GuiGraphicsExtractor var0) {
      return method666(var0.pose());
   }

   public static Matrix3x2f method668(Matrix4f var0) {
      return new Matrix3x2f(var0.m00(), var0.m01(), var0.m10(), var0.m11(), var0.m30(), var0.m31());
   }

   public static void method669(Matrix3x2f var0, Matrix4fc var1) {
      var0.set(var1.m00(), var1.m01(), var1.m10(), var1.m11(), var1.m30(), var1.m31());
   }
}
