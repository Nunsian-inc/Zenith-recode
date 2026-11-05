package org.zenith.utility.render.display.base;

import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix3x2fStack;
import org.joml.Matrix4f;
import org.zenith.MatrixUtils;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.utility.render.Jebriksheet;

public final class QueuedCustomDrawContext extends CustomDrawContext {
   private boolean replaying;
   private static final Matrix4f QUEUE_MATRIX = new Matrix4f();

   private QueuedCustomDrawContext(CustomDrawContext customDrawContext) {
      super(customDrawContext);
   }

   public static QueuedCustomDrawContext of(CustomDrawContext customDrawContext) {
      return customDrawContext instanceof QueuedCustomDrawContext customDrawContext2 ? customDrawContext2 : new QueuedCustomDrawContext(customDrawContext);
   }

   private Matrix4f queueMatrix() {
      return MatrixUtils.method665(this.pose(), QUEUE_MATRIX);
   }

   private void queueContent(Runnable runnable) {
      Matrix4f matrix4f = MatrixUtils.method664(this.pose());
      Jebriksheet.method2349(() -> this.runWithMatrix(matrix4f, runnable));
   }

   private void queueBackground(Runnable runnable) {
      Matrix4f matrix4f = MatrixUtils.method664(this.pose());
      Jebriksheet.method2353(() -> this.runWithMatrix(matrix4f, runnable));
   }

   private void runWithMatrix(Matrix4f matrix4f, Runnable runnable) {
      Matrix3x2fStack pose = this.pose();
      boolean replaying = this.replaying;
      pose.pushMatrix();
      MatrixUtils.method669(pose, matrix4f);
      this.replaying = true;

      try {
         runnable.run();
      } finally {
         this.replaying = replaying;
         pose.popMatrix();
      }
   }

   @Override
   public void drawText(Font font, String text, float floatValue, float centeredTextY, ZenithColor zenithColor) {
      if (this.replaying) {
         super.drawText(font, text, floatValue, centeredTextY, zenithColor);
      } else {
         this.queueContent(() -> super.drawText(font, text, floatValue, centeredTextY, zenithColor));
      }
   }

   @Override
   public void drawText(Font font, String name, float floatValue, float centeredTextY, ZenithColor zenithColor, boolean flag, float f, float f2, float maxWidth) {
      if (this.replaying) {
         super.drawText(font, name, floatValue, centeredTextY, zenithColor, flag, f, f2, maxWidth);
      } else {
         this.queueContent(() -> super.drawText(font, name, floatValue, centeredTextY, zenithColor, flag, f, f2, maxWidth));
      }
   }

   @Override
   public void drawText(Font font, String name, float floatValue, float y, int intValue, boolean flag, float f, float f2, float maxWidth) {
      if (this.replaying) {
         super.drawText(font, name, floatValue, y, intValue, flag, f, f2, maxWidth);
      } else {
         this.queueContent(() -> super.drawText(font, name, floatValue, y, intValue, flag, f, f2, maxWidth));
      }
   }

   @Override
   public void drawText(Font font, String text, float floatValue, float centeredTextY, GlowColor glowColor) {
      if (this.replaying) {
         super.drawText(font, text, floatValue, centeredTextY, glowColor);
      } else {
         this.queueContent(() -> super.drawText(font, text, floatValue, centeredTextY, glowColor));
      }
   }

   @Override
   public void drawText(Font font, Component text, float x1, float y, int color) {
      if (this.replaying) {
         super.drawText(font, text, x1, y, color);
      } else {
         this.queueContent(() -> super.drawText(font, text, x1, y, color));
      }
   }

   @Override
   public void drawRoundedRect(float floatValue, float y, float width, float height, ProxyDotRadiusData headerRadius, ZenithColor zenithColor) {
      if (this.replaying) {
         super.drawRoundedRect(floatValue, y, width, height, headerRadius, zenithColor);
      } else {
         Jebriksheet.method2364(this.queueMatrix(), floatValue, y, width, height, headerRadius, zenithColor);
      }
   }

   @Override
   public void drawRect(float floatValue, float y, float width, float height, ZenithColor zenithColor) {
      if (this.replaying) {
         super.drawRect(floatValue, y, width, height, zenithColor);
      } else {
         Jebriksheet.method2365(this.queueMatrix(), floatValue, y, width, height, zenithColor);
      }
   }

   @Override
   public void drawBlurHudBooleanCheck(
      float floatValue, float y, float width, float panelHeight, float blurPower, ProxyDotRadiusData moduleMenuRadius, ZenithColor zenithColor, boolean flag, boolean flag2
   ) {
      if (this.replaying) {
         super.drawBlurHudBooleanCheck(floatValue, y, width, panelHeight, blurPower, moduleMenuRadius, zenithColor, flag, flag2);
      } else {
         Jebriksheet.method2363(this.queueMatrix(), floatValue, y, width, panelHeight, blurPower, moduleMenuRadius, zenithColor, flag, flag2);
      }
   }

   @Override
   public void drawPlayerHeadWithRoundedShader(Identifier texturePath, float floatValue, float y, float width, ProxyDotRadiusData headerRadius, ZenithColor zenithColor) {
      if (this.replaying) {
         super.drawPlayerHeadWithRoundedShader(texturePath, floatValue, y, width, headerRadius, zenithColor);
      } else {
         this.queueContent(() -> super.drawPlayerHeadWithRoundedShader(texturePath, floatValue, y, width, headerRadius, zenithColor));
      }
   }

   @Override
   public void item(ItemStack item, int x, int y) {
      if (this.replaying) {
         super.item(item, x, y);
      } else {
         this.queueContent(() -> super.item(item, x, y));
      }
   }

   @Override
   public void item(ItemStack stack, int x, int y, int seed) {
      if (this.replaying) {
         super.item(stack, x, y, seed);
      } else {
         this.queueContent(() -> super.item(stack, x, y, seed));
      }
   }

   @Override
   public void fill(int x1, int y1, int x2, int y2, int color) {
      if (this.replaying) {
         super.fill(x1, y1, x2, y2, color);
      } else {
         this.queueRect(x1, y1, x2, y2, color);
      }
   }

   @Override
   public void fill(RenderPipeline pipeline, int x1, int y1, int x2, int y2, int color) {
      if (this.replaying) {
         super.fill(pipeline, x1, y1, x2, y2, color);
      } else {
         this.queueRect(x1, y1, x2, y2, color);
      }
   }

   private void queueRect(int x1, int y1, int x2, int y2, int color) {
      int floatValue = Math.min(x1, x2);
      int y = Math.min(y1, y2);
      int n = Math.max(x1, x2);
      int n2 = Math.max(y1, y2);
      Jebriksheet.method2365(this.queueMatrix(), floatValue, y, n - floatValue, n2 - y, ZenithColor.method7291(color));
   }

   @Override
   public void enableScissor(int x1, int y1, int x2, int y2) {
      if (this.replaying) {
         super.enableScissor(x1, y1, x2, y2);
      } else {
         this.queueBackground(() -> super.enableScissor(x1, y1, x2, y2));
         this.queueContent(() -> super.enableScissor(x1, y1, x2, y2));
      }
   }

   @Override
   public void disableScissor() {
      if (this.replaying) {
         super.disableScissor();
      } else {
         this.queueBackground(() -> super.disableScissor());
         this.queueContent(() -> super.disableScissor());
      }
   }
}
