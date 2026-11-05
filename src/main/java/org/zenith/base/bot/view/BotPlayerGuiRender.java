package org.zenith.base.bot.view;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.zenith.utility.render.OutlineVertexProvider;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.zenith.MatrixUtils;
import org.zenith.base.bot.net.BotPlayHandler;
import org.zenith.base.bot.world.BotPlayer;
import org.zenith.utility.render.ScissorFlusher;

public final class BotPlayerGuiRender {
   private static final BotPlayerRenderer RENDERER = new BotPlayerRenderer();

   private BotPlayerGuiRender() {
   }

   public static void drawEntity(
      GuiGraphicsExtractor var0, int var1, int var2, int var3, int var4, int var5, float var6, float var7, float var8, BotPlayer var9, @Nullable BotPlayHandler var10
   ) {
      float var11 = (var1 + var3) / 2.0F;
      float var12 = (var2 + var4) / 2.0F;
      var0.enableScissor(var1, var2, var3, var4);
      float var13 = (float)Math.atan((var11 - var7) / 40.0F);
      float var14 = (float)Math.atan((var12 - var8) / 40.0F);
      Quaternionf var15 = new Quaternionf().rotateZ((float) Math.PI);
      var15.mul(new Quaternionf().rotateX(var14 * 20.0F * (float) (Math.PI / 180.0)));
      float var16 = var9.getScale();
      Vector3f var17 = new Vector3f(0.0F, var9.getBbHeight() / 2.0F + var6 * var16, 0.0F);
      float var18 = var5 / var16;
      PoseStack var19 = MatrixUtils.method666(var0.pose());
      var19.translate(var11, var12, 50.0F);
      var19.scale(var18, var18, -var18);
      var19.translate(var17.x, var17.y, var17.z);
      var19.rotate(var15);
      ScissorFlusher.flush();
      Minecraft var20 = Minecraft.getInstance();
      synchronized (var9.networkHandler.getClient().getRenderStateLock()) {
         float var23 = var9.yBodyRot;
         float var24 = var9.getYRot();
         float var25 = var9.getXRot();
         float var26 = var9.yHeadRotO;
         float var27 = var9.yHeadRot;
         var9.yBodyRot = 180.0F + var13 * 20.0F;
         var9.setYRot(180.0F + var13 * 40.0F);
         var9.setXRot(-var14 * 20.0F);
         var9.yHeadRot = var9.getYRot();
         var9.yHeadRotO = var9.getYRot();

         try {
            OutlineVertexProvider.method1542(var21 -> RENDERER.render(var9, var10, var9.getX(), var9.getY(), var9.getZ(), 1.0F, var19, var21, 15728880, false));
         } finally {
            var9.yBodyRot = var23;
            var9.setYRot(var24);
            var9.setXRot(var25);
            var9.yHeadRotO = var26;
            var9.yHeadRot = var27;
         }
      }

      var0.disableScissor();
   }
}
