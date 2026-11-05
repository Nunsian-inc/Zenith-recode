package org.zenith.client.screens.override.main.component;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.client.screens.override.main.MenuSkin;
import org.zenith.client.screens.override.main.MenuSurface;
import org.zenith.client.screens.override.main.MenuTheme;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public final class VanillaOptionStyle {
   private static final float PADDING = MenuTheme.px(14.0F);
   private static final float TRACK_HEIGHT = MenuTheme.px(4.0F);
   private static final float KNOB_SIZE = MenuTheme.px(6.0F);
   private static final float LABEL_SIZE = 12.0F;
   private static boolean active;

   private VanillaOptionStyle() {
   }

   public static boolean isActive() {
      return active;
   }

   public static void begin() {
      active = true;
   }

   public static void end() {
      active = false;
   }

   public static void drawButton(AbstractWidget abstractWidget, GuiGraphicsExtractor context, int mouseX, int mouseY, float deltaTicks) {
      MenuDrawContext graphics = MenuDrawContext.method8080(context, mouseX, mouseY, deltaTicks);
      float x = abstractWidget.getX();
      float y = abstractWidget.getY();
      float width = abstractWidget.getWidth();
      float height = abstractWidget.getHeight();
      boolean flag = abstractWidget.isHovered() || abstractWidget.isFocused();
      float radius = MenuSkin.radius(height);
      ProxyDotRadiusData headerRadius = ProxyDotRadiusData.method8069(radius);
      ZenithColor accent = MenuTheme.accent();
      ZenithColor plate = flag ? MenuTheme.plate().method7295(accent, 0.75F) : MenuTheme.plate();
      if (flag) {
         MenuSurface.glow(graphics, x, y, width, height, radius, accent, 0.3F);
      }

      graphics.drawRoundedRect(x, y, width, height, headerRadius, plate);
      Font medium = MenuTheme.medium(12.0F);
      String string = abstractWidget.getMessage().getString();
      ZenithColor textSecondary = abstractWidget.active ? (flag ? MenuTheme.onAccent() : MenuTheme.textSecondary()) : MenuTheme.textTertiary();
      drawClipped(graphics, medium, string, x, y, width, height, textSecondary);
   }

   public static void drawSlider(AbstractWidget abstractWidget, double d, GuiGraphicsExtractor context, int mouseX, int mouseY, float deltaTicks) {
      MenuDrawContext menuDrawContext = MenuDrawContext.method8080(context, mouseX, mouseY, deltaTicks);
      float x = abstractWidget.getX();
      float y = abstractWidget.getY();
      float width = abstractWidget.getWidth();
      float height = abstractWidget.getHeight();
      boolean flag = abstractWidget.isHovered() || abstractWidget.isFocused();
      ProxyDotRadiusData headerRadius = ProxyDotRadiusData.method8069(MenuSkin.radius(height));
      menuDrawContext.drawRoundedRect(x, y, width, height, headerRadius, MenuTheme.plate().method7295(MenuTheme.track(), flag ? 0.6F : 0.0F));
      float floatValue = x + PADDING;
      float f = Math.max(0.0F, width - PADDING * 2.0F);
      float y2 = y + height - TRACK_HEIGHT - MenuTheme.px(6.0F);
      ProxyDotRadiusData proxyDotRadiusData = ProxyDotRadiusData.method8069(TRACK_HEIGHT / 2.0F);
      menuDrawContext.drawRoundedRect(floatValue, y2, f, TRACK_HEIGHT, proxyDotRadiusData, MenuTheme.track());
      float width2 = (float)(f * Math.min(1.0, Math.max(0.0, d)));
      if (width2 > 0.0F) {
         menuDrawContext.drawRoundedRect(floatValue, y2, width2, TRACK_HEIGHT, proxyDotRadiusData, MenuTheme.accent());
      }

      menuDrawContext.drawRoundedRect(
         floatValue + width2 - KNOB_SIZE / 2.0F,
         y2 + (TRACK_HEIGHT - KNOB_SIZE) / 2.0F,
         KNOB_SIZE,
         KNOB_SIZE,
         ProxyDotRadiusData.method8069(KNOB_SIZE / 2.0F),
         ZenithColor.INSTANCE
      );
      Font medium = MenuTheme.medium(12.0F);
      String string = abstractWidget.getMessage().getString();
      menuDrawContext.enableScissor(x + PADDING, y, x + width - PADDING, y + height);
      menuDrawContext.drawText(medium, string, x + PADDING, y + MenuTheme.px(6.0F), MenuTheme.text());
      menuDrawContext.disableScissor();
   }

   private static void drawClipped(MenuDrawContext graphics, Font medium, String string, float x, float y, float width, float height, ZenithColor textSecondary) {
      float f = medium.width(string);
      float floatValue = f > width - PADDING * 2.0F ? x + PADDING : x + (width - f) / 2.0F;
      graphics.enableScissor(x + MenuTheme.px(4.0F), y, x + width - MenuTheme.px(4.0F), y + height);
      graphics.drawText(medium, string, floatValue, y + (height - medium.height()) / 2.0F, textSecondary);
      graphics.disableScissor();
   }
}
