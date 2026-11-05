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
      GuiGraphicsExtractor guiGraphicsExtractor, int x0, int y0, int x1, int y1, int n, float f, float xo, float yo, BotPlayer player, @Nullable BotPlayHandler playHandler
   ) {
      float xo2 = (x0 + x1) / 2.0F;
      float yo2 = (y0 + y1) / 2.0F;
      guiGraphicsExtractor.enableScissor(x0, y0, x1, y1);
      float f2 = (float)Math.atan((xo2 - xo) / 40.0F);
      float f3 = (float)Math.atan((yo2 - yo) / 40.0F);
      Quaternionf rotateZ = new Quaternionf().rotateZ((float) Math.PI);
      rotateZ.mul(new Quaternionf().rotateX(f3 * 20.0F * (float) (Math.PI / 180.0)));
      float scale = player.getScale();
      Vector3f vec = new Vector3f(0.0F, player.getBbHeight() / 2.0F + f * scale, 0.0F);
      float xScale = n / scale;
      PoseStack poseStack = MatrixUtils.method666(guiGraphicsExtractor.pose());
      poseStack.translate(xo2, yo2, 50.0F);
      poseStack.scale(xScale, xScale, -xScale);
      poseStack.translate(vec.x, vec.y, vec.z);
      poseStack.rotate(rotateZ);
      ScissorFlusher.flush();
      Minecraft minecraft = Minecraft.getInstance();
      synchronized (player.networkHandler.getClient().getRenderStateLock()) {
         float yBodyRot = player.yBodyRot;
         float yRot = player.getYRot();
         float xRot = player.getXRot();
         float yHeadRotO = player.yHeadRotO;
         float yHeadRot = player.yHeadRot;
         player.yBodyRot = 180.0F + f2 * 20.0F;
         player.setYRot(180.0F + f2 * 40.0F);
         player.setXRot(-f3 * 20.0F);
         player.yHeadRot = player.getYRot();
         player.yHeadRotO = player.getYRot();

         try {
            OutlineVertexProvider.method1542(submitNodeCollector -> RENDERER.render(player, playHandler, player.getX(), player.getY(), player.getZ(), 1.0F, poseStack, submitNodeCollector, 15728880, false));
         } finally {
            player.yBodyRot = yBodyRot;
            player.setYRot(yRot);
            player.setXRot(xRot);
            player.yHeadRotO = yHeadRotO;
            player.yHeadRot = yHeadRot;
         }
      }

      guiGraphicsExtractor.disableScissor();
   }
}
