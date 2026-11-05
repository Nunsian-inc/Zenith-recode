package org.zenith.utility.render.display.base;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.zenith.MatrixUtils;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.MsdfRenderer;
import org.zenith.utility.render.RoundedRectBatch;

public class ZenithDrawContext extends MenuDrawContext {
   public ZenithDrawContext(MenuDrawContext menuDrawContext) {
      super(menuDrawContext, menuDrawContext.getMouseX(), menuDrawContext.getMouseY(), menuDrawContext.getDelta());
   }

   public static MenuDrawContext method8095(MenuDrawContext menuDrawContext) {
      return (MenuDrawContext)(RoundedRectBatch.enabled && !(menuDrawContext instanceof ZenithDrawContext) ? new ZenithDrawContext(menuDrawContext) : menuDrawContext);
   }

   @Override
   public void drawText(Font font, String text, float floatValue, float centeredTextY, ZenithColor zenithColor) {
      if (RoundedRectBatch.method2934()) {
         RoundedRectBatch.method2941(font, text, MatrixUtils.method664(this.pose()), floatValue, centeredTextY, zenithColor.method7287());
      } else {
         super.drawText(font, text, floatValue, centeredTextY, zenithColor);
      }
   }

   @Override
   public void drawText(Font font, String name, float floatValue, float y, int intValue, boolean flag, float f, float f2, float maxWidth) {
      if (RoundedRectBatch.method2934()) {
         RoundedRectBatch.method2942(font, name, MatrixUtils.method664(this.pose()), floatValue, y, intValue, flag, f, f2, maxWidth);
      } else {
         super.drawText(font, name, floatValue, y, intValue, flag, f, f2, maxWidth);
      }
   }

   @Override
   public void drawText(Font font, String text, float floatValue, float centeredTextY, GlowColor glowColor) {
      if (RoundedRectBatch.method2934()) {
         RoundedRectBatch.method2943(font, text, MatrixUtils.method664(this.pose()), floatValue, centeredTextY, glowColor);
      } else {
         super.drawText(font, text, floatValue, centeredTextY, glowColor);
      }
   }

   @Override
   public void drawRoundedRect(float floatValue, float y, float width, float height, ProxyDotRadiusData proxyDotRadiusData, ZenithColor zenithColor) {
      if (RoundedRectBatch.method2934()) {
         RoundedRectBatch.method2938(MatrixUtils.method664(this.pose()), floatValue, y, width, height, proxyDotRadiusData, zenithColor);
      } else {
         super.drawRoundedRect(floatValue, y, width, height, proxyDotRadiusData, zenithColor);
      }
   }

   @Override
   public void drawRect(float floatValue, float y, float width, float height, ZenithColor zenithColor) {
      if (RoundedRectBatch.method2934()) {
         RoundedRectBatch.method2936(MatrixUtils.method664(this.pose()), floatValue, y, width, height, zenithColor);
      } else {
         super.drawRect(floatValue, y, width, height, zenithColor);
      }
   }

   @Override
   public void drawRoundedRect(float floatValue, float y, float width, float height, ProxyDotRadiusData proxyDotRadiusData, GlowColor glowColor) {
      if (RoundedRectBatch.method2934()) {
         RoundedRectBatch.method2939(MatrixUtils.method664(this.pose()), floatValue, y, width, height, proxyDotRadiusData, glowColor);
      } else {
         super.drawRoundedRect(floatValue, y, width, height, proxyDotRadiusData, glowColor);
      }
   }

   @Override
   public void drawRoundedRectBatched(float floatValue, float y, float width, float height, ProxyDotRadiusData proxyDotRadiusData, ZenithColor zenithColor) {
      if (RoundedRectBatch.method2934()) {
         RoundedRectBatch.method2937(MatrixUtils.method664(this.pose()), floatValue, y, width, height, proxyDotRadiusData, zenithColor);
      } else {
         super.drawRoundedRectBatched(floatValue, y, width, height, proxyDotRadiusData, zenithColor);
      }
   }

   @Override
   public void beginRoundedRectBatch(ProxyDotRadiusData proxyDotRadiusData) {
      if (!RoundedRectBatch.method2934()) {
         super.beginRoundedRectBatch(proxyDotRadiusData);
      }
   }

   @Override
   public void flushRoundedRects() {
      if (!RoundedRectBatch.method2934()) {
         super.flushRoundedRects();
      }
   }

   @Override
   public void drawRoundedBorder(float floatValue, float lastY, float width, float height, float batchRange, ProxyDotRadiusData proxyDotRadiusData, GlowColor glowColor) {
      if (RoundedRectBatch.method2934()) {
         RoundedRectBatch.method2940(
            MatrixUtils.method664(this.pose()),
            floatValue,
            lastY,
            width,
            height,
            batchRange,
            proxyDotRadiusData,
            glowColor.getZenithColor(),
            glowColor.getZenithColor2(),
            glowColor.getZenithColor4(),
            glowColor.getZenithColor3()
         );
      } else {
         super.drawRoundedBorder(floatValue, lastY, width, height, batchRange, proxyDotRadiusData, glowColor);
      }
   }

   @Override
   public void drawRoundedBorder(float floatValue, float lastY, float width, float height, float batchRange, ProxyDotRadiusData proxyDotRadiusData, ZenithColor zenithColor) {
      if (RoundedRectBatch.method2934()) {
         RoundedRectBatch.method2940(MatrixUtils.method664(this.pose()), floatValue, lastY, width, height, batchRange, proxyDotRadiusData, zenithColor, zenithColor, zenithColor, zenithColor);
      } else {
         super.drawRoundedBorder(floatValue, lastY, width, height, batchRange, proxyDotRadiusData, zenithColor);
      }
   }

   @Override
   public void enableScissor(int x1, int y1, int x2, int y2) {
      MsdfRenderer.flushBatch();
      super.enableScissor(x1, y1, x2, y2);
   }

   @Override
   public void disableScissor() {
      MsdfRenderer.flushBatch();
      super.disableScissor();
   }

   @Override
   public void item(ItemStack item, int x, int y) {
      MsdfRenderer.flushBatch();
      super.item(item, x, y);
   }

   @Override
   public void item(ItemStack stack, int x, int y, int seed) {
      MsdfRenderer.flushBatch();
      super.item(stack, x, y, seed);
   }

   public void drawItem(ItemStack itemStack, int x, int y, int seed, int n) {
      MsdfRenderer.flushBatch();
      super.item(itemStack, x, y, seed);
   }

   @Override
   public void item(LivingEntity entity, ItemStack stack, int x, int y, int seed) {
      MsdfRenderer.flushBatch();
      super.item(entity, stack, x, y, seed);
   }

   @Override
   public void fakeItem(ItemStack stack, int x, int y) {
      MsdfRenderer.flushBatch();
      super.fakeItem(stack, x, y);
   }

   @Override
   public void fakeItem(ItemStack stack, int x, int y, int seed) {
      MsdfRenderer.flushBatch();
      super.fakeItem(stack, x, y, seed);
   }

   @Override
   public void itemDecorations(net.minecraft.client.gui.Font textRenderer, ItemStack stack, int x, int y) {
      MsdfRenderer.flushBatch();
      super.itemDecorations(textRenderer, stack, x, y);
   }

   @Override
   public void itemDecorations(net.minecraft.client.gui.Font textRenderer, ItemStack stack, int x, int y, String stackCountText) {
      MsdfRenderer.flushBatch();
      super.itemDecorations(textRenderer, stack, x, y, stackCountText);
   }
}
