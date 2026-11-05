package org.zenith.utility.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import org.zenith.MatrixUtils;
import org.zenith.utility.render.display.base.MenuDrawContext;

public final class MatrixStackUtils {
   public static final float CONST_floatValue = 18.0F;

   private MatrixStackUtils() {
   }

   public static PoseStack method5467(MenuDrawContext var0, float var1, float var2, float var3) {
      PoseStack var4 = MatrixUtils.method666(var0.pose());
      var4.translate(var1, var2, 50.0F);
      var4.scale(var3, -var3, var3 * 0.4F);
      var4.rotate(Axis.XP.rotationDegrees(18.0F));
      return var4;
   }

   public static float method5468() {
      return -18.0F;
   }

   public static float method5469() {
      return 0.0F;
   }
}
