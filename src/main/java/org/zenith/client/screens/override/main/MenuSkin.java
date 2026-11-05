package org.zenith.client.screens.override.main;

import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.client.modules.impl.render.InterfaceModule;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public final class MenuSkin {
   public static final float HEADER_HEIGHT = MenuTheme.px(42.0F);
   public static final float ROW_HEIGHT = MenuTheme.px(31.0F);
   public static final float TILE_INSET = MenuTheme.px(7.0F);
   public static final float HEADER_TILE = HEADER_HEIGHT - TILE_INSET * 2.0F;
   public static final float ROW_TILE = MenuTheme.px(21.0F);
   public static final float EDGE = MenuTheme.px(12.0F);
   public static final float GAP = MenuTheme.px(6.0F);
   public static final float LIST_PAD = MenuTheme.px(10.0F);

   private MenuSkin() {
   }

   public static float radius(float height) {
      float f = InterfaceModule.method6801() * 2.0F;
      return Math.max(MenuTheme.px(4.0F), Math.min(f, height / 2.0F));
   }

   public static float tileRadius(float width) {
      return width * 0.3F;
   }

   public static float dividerWidth() {
      return GAP * 2.0F + 1.0F;
   }

   public static void panel(MenuDrawContext graphics, float floatValue, float snap, float width, float height) {
      panel(graphics, floatValue, snap, width, height, radius(height));
   }

   public static void panel(MenuDrawContext graphics, float floatValue, float snap, float width, float height, float radius) {
      ProxyDotRadiusData headerRadius = ProxyDotRadiusData.method8069(radius);
      MenuSurface.blur(graphics, floatValue, snap, width, height, headerRadius, 1.0F);
      graphics.drawRoundedRect(floatValue, snap, width, height, headerRadius, MenuTheme.panel());
   }

   public static void plate(MenuDrawContext graphics, float floatValue, float snap, float width, float height, ZenithColor plate) {
      graphics.drawRoundedRect(floatValue, snap, width, height, ProxyDotRadiusData.method8069(radius(height)), plate);
   }

   public static float divider(MenuDrawContext graphics, float divider, float snap, float height) {
      float f = height * 0.45F;
      graphics.drawRect(divider + GAP, snap + (height - f) / 2.0F, 1.0F, f, MenuTheme.textTertiary().method7294(0.45F));
      return divider + dividerWidth();
   }

   public static void accentTile(MenuDrawContext graphics, float floatValue, float y, float width, MenuIcon menuIcon) {
      graphics.drawRoundedRect(floatValue, y, width, width, ProxyDotRadiusData.method8069(tileRadius(width)), MenuTheme.accent());
      iconCentered(graphics, menuIcon, menuIcon.vector() ? width * 1.25F : width * 0.5F, floatValue, y, width, MenuTheme.onAccent());
   }

   public static void mutedTile(MenuDrawContext graphics, float floatValue, float y, float width, MenuIcon menuIcon, ZenithColor textTertiary) {
      graphics.drawRoundedRect(floatValue, y, width, width, ProxyDotRadiusData.method8069(tileRadius(width)), MenuTheme.plate());
      if (menuIcon != null) {
         iconCentered(graphics, menuIcon, menuIcon.vector() ? width * 1.25F : width * 0.5F, floatValue, y, width, textTertiary);
      }
   }

   public static void iconCentered(MenuDrawContext graphics, MenuIcon menuIcon, float f, float floatValue, float y, float width, ZenithColor textTertiary) {
      Font font = menuIcon.font(f);
      String glyph = menuIcon.glyph();
      float[] inkBounds = font.inkBounds(glyph);
      if (inkBounds == null) {
         graphics.drawText(font, glyph, floatValue + (width - font.width(glyph)) / 2.0F, y + (width - font.height()) / 2.0F, textTertiary);
      } else {
         graphics.drawText(font, glyph, floatValue + (width - inkBounds[0]) / 2.0F, y + (width - inkBounds[1]) / 2.0F - font.baseline() + inkBounds[2], textTertiary);
      }
   }

   public static float header(MenuDrawContext graphics, float floatValue, float snap, MenuIcon menuIcon, String text) {
      float y = snap + TILE_INSET;
      accentTile(graphics, floatValue + TILE_INSET, y, HEADER_TILE, menuIcon);
      float f = divider(graphics, floatValue + TILE_INSET + HEADER_TILE, snap, HEADER_HEIGHT);
      Font medium = MenuTheme.medium(12.0F);
      graphics.drawText(medium, text, f, snap + (HEADER_HEIGHT - medium.height()) / 2.0F, MenuTheme.text());
      return f + medium.width(text);
   }

   public static float headerWidth(String text) {
      return TILE_INSET + HEADER_TILE + dividerWidth() + MenuTheme.medium(12.0F).width(text) + EDGE;
   }

   public static BoundsData listArea(MenuDrawContext graphics, BoundsData boundsData, float height) {
      panel(graphics, boundsData.getFloatValue(), boundsData.getFloatValue2(), boundsData.width(), height);
      return new BoundsData(boundsData.getFloatValue() + LIST_PAD, boundsData.getFloatValue2() + LIST_PAD, boundsData.width() - LIST_PAD * 2.0F, height - LIST_PAD * 2.0F);
   }

   public static float stagger(float f, int n) {
      float f2 = Math.min(0.55F, n * 0.07F);
      return Math.clamp((f - f2) / Math.max(0.05F, 1.0F - f2), 0.0F, 1.0F);
   }

   public static float enterOffset(float f, int n) {
      float stagger2 = stagger(f, n);
      return (1.0F - stagger2) * MenuTheme.px(14.0F);
   }

   public static float valuePillWidth(String playerCount) {
      return Math.max(ROW_TILE, MenuTheme.semibold(10.0F).width(playerCount) + MenuTheme.px(10.0F));
   }

   public static float valuePill(MenuDrawContext graphics, float f, float snap, float f2, String playerCount) {
      Font semibold = MenuTheme.semibold(10.0F);
      float width = valuePillWidth(playerCount);
      float height = ROW_TILE;
      float floatValue = f - width;
      float y = snap + (f2 - height) / 2.0F;
      graphics.drawRoundedRect(floatValue, y, width, height, ProxyDotRadiusData.method8069(tileRadius(height)), MenuTheme.plate());
      graphics.drawText(semibold, playerCount, floatValue + (width - semibold.width(playerCount)) / 2.0F, y + (height - semibold.height()) / 2.0F, MenuTheme.text());
      return floatValue;
   }
}
