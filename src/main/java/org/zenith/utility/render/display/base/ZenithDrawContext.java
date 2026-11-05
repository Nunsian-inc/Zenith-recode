package org.zenith.utility.render.display.base;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.zenith.MatrixUtils;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.MsdfRenderer;
import org.zenith.utility.render.RoundedRectBatch;

public class ZenithDrawContext extends MenuDrawContext {
   public ZenithDrawContext(MenuDrawContext var1) {
      super(var1, var1.getMouseX(), var1.getMouseY(), var1.getDelta());
   }

   public static MenuDrawContext method8095(MenuDrawContext var0) {
      return (MenuDrawContext)(RoundedRectBatch.enabled && !(var0 instanceof ZenithDrawContext) ? new ZenithDrawContext(var0) : var0);
   }

   @Override
   public void drawText(Font var1, String var2, float var3, float var4, ZenithColor var5) {
      if (RoundedRectBatch.method2934()) {
         RoundedRectBatch.method2941(var1, var2, MatrixUtils.method664(this.pose()), var3, var4, var5.method7287());
      } else {
         super.drawText(var1, var2, var3, var4, var5);
      }
   }

   @Override
   public void drawText(Font var1, String var2, float var3, float var4, int var5, boolean var6, float var7, float var8, float var9) {
      if (RoundedRectBatch.method2934()) {
         RoundedRectBatch.method2942(var1, var2, MatrixUtils.method664(this.pose()), var3, var4, var5, var6, var7, var8, var9);
      } else {
         super.drawText(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      }
   }

   @Override
   public void drawText(Font var1, String var2, float var3, float var4, GlowColor var5) {
      if (RoundedRectBatch.method2934()) {
         RoundedRectBatch.method2943(var1, var2, MatrixUtils.method664(this.pose()), var3, var4, var5);
      } else {
         super.drawText(var1, var2, var3, var4, var5);
      }
   }

   @Override
   public void drawRoundedRect(float var1, float var2, float var3, float var4, ProxyDotRadiusData var5, ZenithColor var6) {
      if (RoundedRectBatch.method2934()) {
         RoundedRectBatch.method2938(MatrixUtils.method664(this.pose()), var1, var2, var3, var4, var5, var6);
      } else {
         super.drawRoundedRect(var1, var2, var3, var4, var5, var6);
      }
   }

   @Override
   public void drawRect(float var1, float var2, float var3, float var4, ZenithColor var5) {
      if (RoundedRectBatch.method2934()) {
         RoundedRectBatch.method2936(MatrixUtils.method664(this.pose()), var1, var2, var3, var4, var5);
      } else {
         super.drawRect(var1, var2, var3, var4, var5);
      }
   }

   @Override
   public void drawRoundedRect(float var1, float var2, float var3, float var4, ProxyDotRadiusData var5, GlowColor var6) {
      if (RoundedRectBatch.method2934()) {
         RoundedRectBatch.method2939(MatrixUtils.method664(this.pose()), var1, var2, var3, var4, var5, var6);
      } else {
         super.drawRoundedRect(var1, var2, var3, var4, var5, var6);
      }
   }

   @Override
   public void drawRoundedRectBatched(float var1, float var2, float var3, float var4, ProxyDotRadiusData var5, ZenithColor var6) {
      if (RoundedRectBatch.method2934()) {
         RoundedRectBatch.method2937(MatrixUtils.method664(this.pose()), var1, var2, var3, var4, var5, var6);
      } else {
         super.drawRoundedRectBatched(var1, var2, var3, var4, var5, var6);
      }
   }

   @Override
   public void beginRoundedRectBatch(ProxyDotRadiusData var1) {
      if (!RoundedRectBatch.method2934()) {
         super.beginRoundedRectBatch(var1);
      }
   }

   @Override
   public void flushRoundedRects() {
      if (!RoundedRectBatch.method2934()) {
         super.flushRoundedRects();
      }
   }

   @Override
   public void drawRoundedBorder(float var1, float var2, float var3, float var4, float var5, ProxyDotRadiusData var6, GlowColor var7) {
      if (RoundedRectBatch.method2934()) {
         RoundedRectBatch.method2940(
            MatrixUtils.method664(this.pose()),
            var1,
            var2,
            var3,
            var4,
            var5,
            var6,
            var7.getZenithColor(),
            var7.getZenithColor2(),
            var7.getZenithColor4(),
            var7.getZenithColor3()
         );
      } else {
         super.drawRoundedBorder(var1, var2, var3, var4, var5, var6, var7);
      }
   }

   @Override
   public void drawRoundedBorder(float var1, float var2, float var3, float var4, float var5, ProxyDotRadiusData var6, ZenithColor var7) {
      if (RoundedRectBatch.method2934()) {
         RoundedRectBatch.method2940(MatrixUtils.method664(this.pose()), var1, var2, var3, var4, var5, var6, var7, var7, var7, var7);
      } else {
         super.drawRoundedBorder(var1, var2, var3, var4, var5, var6, var7);
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

   public void drawItem(ItemStack var1, int var2, int var3, int var4, int var5) {
      MsdfRenderer.flushBatch();
      super.item(var1, var2, var3, var4);
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
