package org.zenith.client.screens.override.main.widget;

import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.client.screens.override.main.MenuIcon;
import org.zenith.client.screens.override.main.MenuSkin;
import org.zenith.client.screens.override.main.MenuSurface;
import org.zenith.client.screens.override.main.MenuTheme;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class MenuButton extends MenuWidget {
   public static final float HEIGHT = MenuSkin.HEADER_HEIGHT;
   public static final float RADIUS = MenuSkin.radius(HEIGHT);
   public static final float PADDING = MenuTheme.px(20.0F);
   public static final float GAP = MenuTheme.px(8.0F);
   public static final float ICON_SIZE = 12.0F;
   public static final float LABEL_SIZE = 12.0F;
   private MenuIcon icon;
   private String label;
   private MenuButton.Style style;
   private Runnable action = () -> {};

   public MenuButton(MenuIcon icon, String label, MenuButton.Style style) {
      this.icon = icon;
      this.label = label;
      this.style = style;
   }

   public static MenuButton accent(MenuIcon icon, String label) {
      return new MenuButton(icon, label, MenuButton.Style.ACCENT);
   }

   public static MenuButton ghost(MenuIcon icon, String label) {
      return new MenuButton(icon, label, MenuButton.Style.GHOST);
   }

   public static MenuButton iconOnly(MenuIcon icon, MenuButton.Style style) {
      return new MenuButton(icon, null, style);
   }

   public MenuButton onClick(Runnable action) {
      this.action = action;
      return this;
   }

   public MenuButton setLabel(String label) {
      this.label = label;
      return this;
   }

   public MenuButton setIcon(MenuIcon icon) {
      this.icon = icon;
      return this;
   }

   public MenuButton setStyle(MenuButton.Style style) {
      this.style = style;
      return this;
   }

   public MenuButton.Style getStyle() {
      return this.style;
   }

   public float preferredWidth() {
      if (this.label != null && !this.label.isEmpty()) {
         Font medium = MenuTheme.medium(12.0F);
         float width = medium.width(this.label);
         if (this.icon != null) {
            width += this.icon.width(12.0F) + GAP;
         }

         return PADDING * 2.0F + width;
      } else {
         return HEIGHT;
      }
   }

   @Override
   public void draw(MenuDrawContext graphics, float f, float f2) {
      float hover = this.enabled ? this.hover() : 0.0F;
      ProxyDotRadiusData headerRadius = ProxyDotRadiusData.method8069(RADIUS);
      ZenithColor zenithColor;
      ZenithColor zenithColor2;
      ZenithColor zenithColor3;
      switch (this.style) {
         case ACCENT:
            zenithColor = MenuTheme.accent().method7303(hover * 0.12F);
            zenithColor2 = MenuTheme.onAccent();
            zenithColor3 = MenuTheme.onAccent();
            break;
         case DANGER:
            zenithColor = MenuTheme.danger().method7303(hover * 0.12F);
            zenithColor2 = MenuTheme.onAccent();
            zenithColor3 = MenuTheme.onAccent();
            break;
         default:
            zenithColor = MenuTheme.plate().method7295(MenuTheme.track(), hover);
            zenithColor2 = MenuTheme.textTertiary().method7295(MenuTheme.accent(), hover);
            zenithColor3 = MenuTheme.textSecondary().method7295(MenuTheme.text(), hover);
      }

      if (!this.enabled) {
         zenithColor = zenithColor.method7294(0.5F);
         zenithColor2 = zenithColor2.method7294(0.5F);
         zenithColor3 = zenithColor3.method7294(0.5F);
      }

      if (this.style == MenuButton.Style.GHOST) {
         graphics.drawRoundedRect(this.x, this.y, this.width, this.height, headerRadius, zenithColor);
      } else {
         MenuSurface.glow(graphics, this.x, this.y, this.width, this.height, RADIUS, zenithColor, 0.35F + hover * 0.25F);
         graphics.drawRoundedRect(this.x, this.y, this.width, this.height, headerRadius, zenithColor);
      }

      Font medium = MenuTheme.medium(12.0F);
      boolean flag = this.label != null && !this.label.isEmpty();
      float width = this.icon == null ? 0.0F : this.icon.width(12.0F);
      float width2 = flag ? medium.width(this.label) : 0.0F;
      float f3 = width + width2 + (this.icon != null && flag ? GAP : 0.0F);
      float floatValue = this.x + (this.width - f3) / 2.0F;
      if (this.icon != null) {
         Font font = this.icon.font(12.0F);
         graphics.drawText(font, this.icon.glyph(), floatValue, this.y + (this.height - font.height()) / 2.0F, zenithColor2);
         floatValue += width + (flag ? GAP : 0.0F);
      }

      if (flag) {
         graphics.drawText(medium, this.label, floatValue, this.y + (this.height - medium.height()) / 2.0F, zenithColor3);
      }
   }

   @Override
   public boolean mouseClicked(double lastClickX, double lastClickY, int glfwButton) {
      if (this.visible && this.enabled && glfwButton == 0 && this.contains(lastClickX, lastClickY)) {
         this.action.run();
         return true;
      } else {
         return false;
      }
   }

   public static enum Style {
      ACCENT,
      GHOST,
      DANGER;
   }
}
