package org.zenith.utility.render.display.base;

import java.util.Objects;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.zenith.MatrixUtils;
import org.zenith.MinecraftAccess;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.MsdfRenderer;
import org.zenith.utility.mixin.accessors.DrawContextAccessor;
import org.zenith.utility.render.RectBatch;
import org.zenith.utility.render.RenderShaders;
import org.zenith.utility.render.RoundedRectBatch2;
import org.zenith.utility.render.ScissorFlusher;

public class CustomDrawContext extends GuiGraphicsExtractor implements MinecraftAccess {
   private final RoundedRectBatch2 roundedRectBatch = new RoundedRectBatch2();
   private final RectBatch rectBatch = new RectBatch();

   public CustomDrawContext(GuiGraphicsExtractor context) {
      super(mc, ((DrawContextAccessor)(Object)context).zenith$state(), ((DrawContextAccessor)(Object)context).zenith$mouseX(), ((DrawContextAccessor)(Object)context).zenith$mouseY());
      this.pose = context.pose;
      this.scissorStack = context.scissorStack;
   }

   public static CustomDrawContext of(GuiGraphicsExtractor context) {
      return new CustomDrawContext(context);
   }

   public void draw() {
      ScissorFlusher.flush();
   }

   public void drawText(Font font, String text, float floatValue, float centeredTextY, ZenithColor zenithColor) {
      MsdfRenderer.renderText(font.getFont(), text, font.getSize(), zenithColor.method7287(), MatrixUtils.method664(this.pose()), floatValue, centeredTextY, 0.0F);
   }

   public void drawText(Font font, String name, float floatValue, float centeredTextY, ZenithColor zenithColor, boolean flag, float f, float f2, float maxWidth) {
      this.drawText(font, name, floatValue, centeredTextY, zenithColor.method7287(), flag, f, f2, maxWidth);
   }

   public void drawText(Font font, String name, float floatValue, float y, int intValue, boolean flag, float f, float f2, float maxWidth) {
      MsdfRenderer.renderText(font.getFont(), name, font.getSize(), intValue, MatrixUtils.method664(this.pose()), floatValue, y, 0.0F, flag, f, f2, maxWidth);
   }

   public void enableScissor(float floatValue, float y, float guiScaledWidth, float guiScaledHeight) {
      this.enableScissor((int)Math.floor(floatValue), (int)Math.floor(y), (int)Math.ceil(guiScaledWidth), (int)Math.ceil(guiScaledHeight));
   }

   @Override
   public void enableScissor(int x1, int y1, int x2, int y2) {
      super.enableScissor(x1, y1, x2, y2);
   }

   @Override
   public void disableScissor() {
      super.disableScissor();
   }

   public void drawText(Font font, String text, float floatValue, float centeredTextY, GlowColor glowColor) {
      MsdfRenderer.renderText(font.getFont(), text, font.getSize(), glowColor, MatrixUtils.method664(this.pose()), floatValue, centeredTextY, 0.0F);
   }

   public void drawText(Font font, Component text, float x1, float y, int color) {
      MsdfRenderer.renderText(font.getFont(), text, font.getSize(), MatrixUtils.method664(this.pose()), x1, y, 0.0F, false, 0.0F, 1.0F, 0.0F, color);
   }

   public void drawText(Font font, Component text, float floatValue, float y) {
      MsdfRenderer.renderText(font.getFont(), text, font.getSize(), MatrixUtils.method664(this.pose()), floatValue, y, 0.0F);
   }

   public void drawSquircle(float floatValue, float y, float width, float height, float batchRange, ProxyDotRadiusData proxyDotRadiusData, ZenithColor zenithColor) {
      RenderShaders.method5266(MatrixUtils.method664(this.pose()), floatValue, y, width, height, batchRange, proxyDotRadiusData, zenithColor);
   }

   public void drawRoundedRect(float floatValue, float y, float width, float height, ProxyDotRadiusData headerRadius, ZenithColor zenithColor) {
      RenderShaders.method5268(MatrixUtils.method664(this.pose()), floatValue, y, width, height, headerRadius, zenithColor);
   }

   public void drawRoundedRect(float floatValue, float y, float width, float height, ProxyDotRadiusData proxyDotRadiusData, GlowColor glowColor) {
      RenderShaders.method5270(MatrixUtils.method664(this.pose()), floatValue, y, width, height, proxyDotRadiusData, glowColor);
   }

   public void drawRect(float floatValue, float y, float width, float height, ZenithColor zenithColor) {
      RenderShaders.method5260(MatrixUtils.method664(this.pose()), floatValue, y, width, height, zenithColor);
   }

   public void beginRectBatch() {
      this.rectBatch.begin();
   }

   public void drawRectBatched(float x, float y, float cell, float f, ZenithColor zenithColor) {
      this.rectBatch.method1756(MatrixUtils.method664(this.pose()), x, y, cell, f, zenithColor);
   }

   public void flushRects() {
      MsdfRenderer.flushBatch();
      this.rectBatch.flush();
   }

   public void beginRoundedRectBatch(ProxyDotRadiusData proxyDotRadiusData) {
      this.roundedRectBatch.method1534(proxyDotRadiusData);
   }

   public void drawRoundedRectBatched(float floatValue, float animationPosY, float width, float height, ProxyDotRadiusData proxyDotRadiusData, ZenithColor zenithColor) {
      this.roundedRectBatch.method1534(proxyDotRadiusData);
      this.roundedRectBatch.method1536(MatrixUtils.method664(this.pose()), floatValue, animationPosY, width, height, zenithColor);
   }

   public void flushRoundedRects() {
      MsdfRenderer.flushBatch();
      this.roundedRectBatch.flush();
   }

   public boolean isRoundedRectBatchActive() {
      return this.roundedRectBatch.isFlag();
   }

   public int drawTextWithBackground(net.minecraft.client.gui.Font font, Component str, int x, int y, int n, ProxyDotRadiusData headerRadius, ZenithColor zenithColor, ZenithColor zenithColor2) {
      int floatValue = x - 3;
      int y2 = y - 2;
      int width = n + 6;
      Objects.requireNonNull(font);
      this.drawRoundedRect((float)floatValue, (float)y2, (float)width, 13.0F, headerRadius, zenithColor2);
      this.text(font, str, x, y, zenithColor.method7287(), true);
      return font.width(str);
   }

   public void drawSprite(CategoryIcon categoryIcon, float floatValue, float y, float f, float f2, ZenithColor zenithColor) {
      RenderShaders.method5300(MatrixUtils.method664(this.pose()), categoryIcon, floatValue, y, f, f2, zenithColor);
   }

   public void drawRoundedCorner(float floatValue, float y, float width, float height, float batchRange, float f, ZenithColor zenithColor, ProxyDotRadiusData headerRadius) {
      width = Math.round(width);
      height = Math.round(height);
      this.enableScissor((int)Math.ceil(floatValue - 10.0F), (int)(y - 10.0F), (int)(floatValue + f), (int)(y + f));
      this.drawRoundedBorder(floatValue, y, width, height, batchRange, headerRadius, zenithColor);
      this.disableScissor();
      this.enableScissor((int)(floatValue + width - f), (int)(y - 10.0F), (int)(floatValue + width + 10.0F), (int)(y + f));
      this.drawRoundedBorder(floatValue, y, width, height, batchRange, headerRadius, zenithColor);
      this.disableScissor();
      this.enableScissor((int)(floatValue - 10.0F), (int)(y + height - f), (int)(floatValue + f), (int)(y + height + 10.0F));
      this.drawRoundedBorder(floatValue, y, width, height, batchRange, headerRadius, zenithColor);
      this.disableScissor();
      this.enableScissor((int)(floatValue + width - f), (int)(y + height - f), (int)(floatValue + width + 10.0F), (int)(y + height + 10.0F));
      this.drawRoundedBorder(floatValue, y, width, height, batchRange, headerRadius, zenithColor);
      this.disableScissor();
   }

   public void drawRoundedCornerOnly(float floatValue, float y, float width, float height, float batchRange, ProxyDotRadiusData proxyDotRadiusData, ZenithColor zenithColor, float f) {
      RenderShaders.method5279(MatrixUtils.method664(this.pose()), floatValue, y, width, height, batchRange, proxyDotRadiusData, zenithColor, f);
   }

   public void drawRoundedBorder(float floatValue, float lastY, float width, float height, float batchRange, ProxyDotRadiusData proxyDotRadiusData, GlowColor glowColor) {
      RenderShaders.method5271(MatrixUtils.method664(this.pose()), floatValue, lastY, width, height, batchRange, proxyDotRadiusData, glowColor);
   }

   public void drawRoundedBorder(float floatValue, float y, float width, float height, float batchRange, ProxyDotRadiusData headerRadius, ZenithColor zenithColor) {
      RenderShaders.method5272(MatrixUtils.method664(this.pose()), floatValue, y, width, height, batchRange, headerRadius, zenithColor);
   }

   public void drawTexture(Identifier identifier, float x, float y, float guiScaledWidth, float guiScaledHeight, ZenithColor zenithColor) {
      RenderShaders.method5280(MatrixUtils.method664(this.pose()), identifier, x, y, guiScaledWidth, guiScaledHeight, zenithColor);
   }

   public void drawBlurHud(float floatValue, float y, float width, float height, float blurPower, ProxyDotRadiusData headerRadius, ZenithColor zenithColor) {
      RenderShaders.method5286(MatrixUtils.method664(this.pose()), floatValue, y, width, height, blurPower, headerRadius, zenithColor);
   }

   public void drawBlurHudBooleanCheck(
      float floatValue, float y, float width, float panelHeight, float blurPower, ProxyDotRadiusData moduleMenuRadius, ZenithColor zenithColor, boolean flag, boolean flag2
   ) {
      RenderShaders.method5287(MatrixUtils.method664(this.pose()), floatValue, y, width, panelHeight, blurPower, moduleMenuRadius, zenithColor, flag, flag2);
   }

   public void drawArcBorder(float floatValue, float y, float width, float height, float batchRange, float f, float f2, ZenithColor zenithColor) {
      RenderShaders.method5275(MatrixUtils.method664(this.pose()), floatValue, y, width, height, batchRange, f, f2, zenithColor);
   }

   public void drawRoundedTexture(Identifier prewarm, float floatValue, float y, float width, float height, ProxyDotRadiusData avatarRadius) {
      RenderShaders.method5292(MatrixUtils.method664(this.pose()), prewarm, floatValue, y, width, height, avatarRadius);
   }

   public void drawRoundedTexture(Identifier texture, float floatValue, float cardAvatarY, float size, float height, ProxyDotRadiusData headerRadius, ZenithColor zenithColor) {
      RenderShaders.method5294(MatrixUtils.method664(this.pose()), texture, floatValue, cardAvatarY, size, height, headerRadius, zenithColor);
   }

   public void drawRoundedTexture(
      Identifier texture,
      float floatValue,
      float cardAvatarY,
      float width,
      float height,
      ProxyDotRadiusData headerRadius,
      ZenithColor zenithColor,
      float f,
      float f2,
      float f3,
      float f4
   ) {
      RenderShaders.method5296(MatrixUtils.method664(this.pose()), texture, floatValue, cardAvatarY, width, height, headerRadius, zenithColor, f, f2, f3, f4);
   }

   public void drawPlayerHeadWithRoundedShader(Identifier texturePath, float floatValue, float y, float width, ProxyDotRadiusData headerRadius, ZenithColor zenithColor) {
      RenderShaders.method5291(MatrixUtils.method664(this.pose()), texturePath, floatValue, y, width, headerRadius, zenithColor);
   }

   public void renderItemBar(ItemStack itemStack, int intValue, int n) {
      ((DrawContextAccessor)(Object)this).callDrawItemBar(itemStack, intValue, n);
   }

   public void renderItemCooldown(ItemStack itemStack, int intValue, int n) {
      ((DrawContextAccessor)(Object)this).callDrawCooldownProgress(itemStack, intValue, n);
   }

   public void pushMatrix() {
      this.pose().pushMatrix();
   }

   public void popMatrix() {
      this.pose().popMatrix();
   }
}
