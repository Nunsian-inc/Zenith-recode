package org.zenith.client.screens.override.main.widget;

import lombok.Generated;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Util;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.client.screens.override.main.MenuIcon;
import org.zenith.client.screens.override.main.MenuSkin;
import org.zenith.client.screens.override.main.MenuSurface;
import org.zenith.client.screens.override.main.MenuTheme;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class MenuTextField extends MenuWidget {
   public static final float HEIGHT = MenuSkin.HEADER_HEIGHT;
   private static final float RADIUS = MenuSkin.radius(HEIGHT);
   private static final float PADDING = MenuTheme.px(16.0F);
   private static final float GAP = MenuTheme.px(8.0F);
   private static final float ICON_SIZE = 12.0F;
   private static final float TEXT_SIZE = 12.0F;
   private final MenuIcon icon;
   private final String placeholder;
   private String value = "";
   private boolean focused;
   private int maxLength = 128;
   private Runnable onEnter;
   private Runnable onChange;
   private long caretResetTime = Util.getMillis();

   public MenuTextField(MenuIcon icon, String placeholder) {
      this.icon = icon;
      this.placeholder = placeholder;
   }

   public void setValue(String randomName) {
      this.value = randomName == null ? "" : randomName;
      if (this.value.length() > this.maxLength) {
         this.value = this.value.substring(0, this.maxLength);
      }

      this.caretResetTime = Util.getMillis();
      if (this.onChange != null) {
         this.onChange.run();
      }
   }

   public void clear() {
      this.setValue("");
   }

   public void setFocused(boolean focused) {
      this.focused = focused;
      this.caretResetTime = Util.getMillis();
   }

   @Override
   public void draw(MenuDrawContext menuDrawContext, float f, float f2) {
      MenuSurface.plate(menuDrawContext, this.x, this.y, this.width, this.height, RADIUS, MenuTheme.plate().method7295(MenuTheme.track(), this.hover() * 0.5F));
      if (this.focused) {
         menuDrawContext.drawRoundedBorder(this.x, this.y, this.width, this.height, -0.1F, ProxyDotRadiusData.method8069(RADIUS), MenuTheme.accent().method7294(0.6F));
      }

      float floatValue = this.x + PADDING;
      if (this.icon != null) {
         Font font = this.icon.font(12.0F);
         ZenithColor accent = this.focused ? MenuTheme.accent() : MenuTheme.textTertiary();
         menuDrawContext.drawText(font, this.icon.glyph(), floatValue, this.y + (this.height - font.height()) / 2.0F, accent);
         floatValue += this.icon.width(12.0F) + GAP;
      }

      Font medium = MenuTheme.medium(12.0F);
      float centeredTextY = this.y + (this.height - medium.height()) / 2.0F;
      float f3 = this.x + this.width - PADDING - floatValue;
      boolean empty = this.value.isEmpty();
      String placeholder = empty && !this.focused ? this.placeholder : this.value;
      ZenithColor textSecondary = empty ? MenuTheme.textSecondary() : MenuTheme.text();

      while (placeholder.length() > 1 && medium.width(placeholder) > f3) {
         placeholder = placeholder.substring(1);
      }

      menuDrawContext.enableScissor(floatValue, this.y, this.x + this.width - PADDING, this.y + this.height);
      menuDrawContext.drawText(medium, placeholder, floatValue, centeredTextY, textSecondary);
      menuDrawContext.disableScissor();
      if (this.focused && (Util.getMillis() - this.caretResetTime) / 500L % 2L == 0L) {
         float floatValue2 = floatValue + medium.width(placeholder) + MenuTheme.px(1.0F);
         menuDrawContext.drawRect(floatValue2, centeredTextY, MenuTheme.px(1.0F), medium.height(), MenuTheme.accent());
      }
   }

   @Override
   public boolean mouseClicked(double lastClickX, double lastClickY, int glfwButton) {
      if (this.visible && this.enabled) {
         boolean contains = this.contains(lastClickX, lastClickY);
         this.setFocused(contains && glfwButton == 0);
         return contains;
      } else {
         return false;
      }
   }

   @Override
   public boolean keyPressed(int n, int n2, int n3) {
      if (this.focused && this.visible && this.enabled) {
         switch (n) {
            case 65:
               if ((n3 & 2) != 0) {
                  return true;
               }
               break;
            case 67:
               if ((n3 & 2) != 0) {
                  Minecraft.getInstance().keyboardHandler.setClipboard(this.value);
                  return true;
               }
               break;
            case 86:
               if ((n3 & 2) != 0) {
                  this.setValue(this.value + Minecraft.getInstance().keyboardHandler.getClipboard());
                  return true;
               }
               break;
            case 256:
               this.setFocused(false);
               return true;
            case 257:
            case 335:
               if (this.onEnter != null) {
                  this.onEnter.run();
               }

               return true;
            case 259:
               if (!this.value.isEmpty()) {
                  this.setValue(this.value.substring(0, this.value.length() - 1));
               }

               return true;
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean charTyped(char c, int n) {
      if (!this.focused || !this.visible || !this.enabled) {
         return false;
      } else if (c >= ' ' && c != 127) {
         if (this.value.length() >= this.maxLength) {
            return true;
         } else {
            this.setValue(this.value + c);
            return true;
         }
      } else {
         return false;
      }
   }

   @Generated
   public String getValue() {
      return this.value;
   }

   @Generated
   public boolean isFocused() {
      return this.focused;
   }

   @Generated
   public void setMaxLength(int maxLength) {
      this.maxLength = maxLength;
   }

   @Generated
   public void setOnEnter(Runnable onEnter) {
      this.onEnter = onEnter;
   }

   @Generated
   public void setOnChange(Runnable onChange) {
      this.onChange = onChange;
   }
}
