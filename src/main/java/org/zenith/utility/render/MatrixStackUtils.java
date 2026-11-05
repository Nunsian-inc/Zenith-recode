package org.zenith.utility.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import org.zenith.MatrixUtils;
import org.zenith.utility.render.display.base.MenuDrawContext;

public final class MatrixStackUtils {
   public static final float CONST_floatValue = 18.0F;

   private MatrixStackUtils() {
   }

   public static PoseStack method5467(MenuDrawContext customDrawContext, float xo, float yo, float xScale) {
      PoseStack poseStack = MatrixUtils.method666(customDrawContext.pose());
      poseStack.translate(xo, yo, 50.0F);
      poseStack.scale(xScale, -xScale, xScale * 0.4F);
      poseStack.rotate(Axis.XP.rotationDegrees(18.0F));
      return poseStack;
   }

   public static float method5468() {
      return -18.0F;
   }

   public static float method5469() {
      return 0.0F;
   }
}
