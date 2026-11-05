package org.zenith.client.screens.nlgui.elements.api;

import lombok.Generated;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.MatrixUtils;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.base.font.MsdfRenderer;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.settings.Setting;
import org.zenith.utility.misc.Enum3;
import org.zenith.utility.render.MathUtils;
import org.zenith.utility.render.display.base.MenuDrawContext;

public abstract class GuiSetting<T extends Setting> extends Element {
   private static final float MIN_DESC_SCROLL_SPEED = 0.028F;
   private static final float MAX_DESC_SCROLL_SPEED = 0.065F;
   private static final float TARGET_DESC_CHARS_PER_SECOND = 12.0F;
   public final float width;
   public final T setting;
   public final Animation animationVisible;
   private float scrollOffset = 0.0F;
   private int scrollDirection = 1;
   private long hoverStartTime = -1L;
   private long lastUpdateTime = -1L;
   private long pauseUntilTime = -1L;
   private boolean continueScroll = false;

   public GuiSetting(float width, T setting) {
      this.width = width;
      this.setting = (T)setting;
      this.animationVisible = new Animation(200L, setting.isVisible() ? 1.0F : 0.0F, Easing.INSTANCE10);
   }

   @Override
   public boolean isVisible() {
      return this.animationVisible.getValue() != 0.0F || this.setting.isVisible();
   }

   @Override
   public float getHeight() {
      return this.rowHeight();
   }

   public float rowHeight() {
      return this.isShort() ? 7.0F : 14.0F;
   }

   public float getVisibleProgress() {
      return this.animationVisible.getValue();
   }

   public float getAnimHeight() {
      return this.getHeight() * this.animationVisible.getValue();
   }

   public boolean isShort() {
      return this.setting.getDescription().isEmpty() || Zenith.getInstance().getNLMenuScreen().isShortMode();
   }

   public boolean onMousePriorityScroll(double d, double d2, double d3, double d4) {
      return false;
   }

   public boolean onMousePriorityClicked(double lastClickX, double lastClickY, Enum3 enum2) {
      return false;
   }

   public void renderPriority(MenuDrawContext menuDrawContext, float x, float y, float f, float f2, float previewAlpha, float f3) {
   }

   public void render(MenuDrawContext menuDrawContext, float lastClickX, float lastClickY, float f, float f2, float previewAlpha) {
   }

   public void drawDefault(
      MenuDrawContext menuDrawContext,
      float x,
      float y,
      String icon,
      String name,
      String description,
      Font font,
      Font font2,
      float floatValue,
      float centeredTextY,
      float maxWidth,
      ZenithColor zenithColor,
      ZenithColor zenithColor2,
      ZenithColor zenithColor3
   ) {
      float px = font.getSize() - 0.5F;
      if (px == 5.0F) {
         px = 5.01F;
      }

      Font font3 = Fonts.NEW_ICONS.getFont(px);
      boolean renderIcon = Zenith.getInstance().getNLMenuScreen().isRenderIcon();
      float rowHeight = this.rowHeight();
      float f = renderIcon ? font3.width(icon) + GuiStyle.PADDING.intValue() / 2.0F : 0.0F;
      String clip = font.clip(name, maxWidth - f);
      if (this.isShort()) {
         if (renderIcon) {
            menuDrawContext.drawText(font3, icon, floatValue, centeredTextY + (rowHeight - font3.height()) / 2.0F, zenithColor3);
         }

         menuDrawContext.drawText(font, clip, floatValue + f, centeredTextY + (rowHeight - font.height()) / 2.0F, zenithColor);
      } else {
         if (renderIcon) {
            menuDrawContext.drawText(font3, icon, floatValue, centeredTextY + (font3.height() / 2.0F - (icon.equals("u") ? 0.0F : 0.2F)), zenithColor3);
         }

         menuDrawContext.drawText(font, clip, floatValue + f, centeredTextY + 1.1F, zenithColor);
         float y2 = centeredTextY + rowHeight - font2.height() - 1.0F;
         float width = font2.width(description);
         float f2 = width - maxWidth;
         boolean flag = MathUtils.method2647(x, y, floatValue, y2 - 3.0F, maxWidth + 1.0F, font2.height() + 10.0F);
         if (f2 <= 1.0F) {
            menuDrawContext.drawText(font2, description, floatValue, y2, zenithColor2);
            this.resetDescScroll();
         } else {
            menuDrawContext.enableScissor(floatValue - 0.5F, y2, floatValue + maxWidth + 2.0F, y2 + font2.height() + 4.0F);
            long now = System.currentTimeMillis();
            if (this.lastUpdateTime == -1L) {
               this.lastUpdateTime = now;
            }

            float f3 = (float)(now - this.lastUpdateTime);
            this.lastUpdateTime = now;
            if (flag) {
               if (this.hoverStartTime == -1L) {
                  this.hoverStartTime = now;
                  this.pauseUntilTime = now + 300L;
               }

               this.continueScroll = true;
            } else if (this.continueScroll && this.scrollOffset <= 0.0F && this.scrollDirection == 1) {
               this.continueScroll = false;
            }

            if (!flag && !this.continueScroll) {
               this.hoverStartTime = -1L;
               this.pauseUntilTime = -1L;
            } else if (this.pauseUntilTime == -1L || now >= this.pauseUntilTime) {
               this.pauseUntilTime = -1L;
               float scrollOffset = f2 + 10.0F;
               float descScrollSpeed = this.getDescScrollSpeed(width, description);
               this.scrollOffset = this.scrollOffset + this.scrollDirection * descScrollSpeed * f3;
               if (this.scrollOffset >= scrollOffset) {
                  this.scrollOffset = scrollOffset;
                  this.scrollDirection = -1;
                  this.pauseUntilTime = now + 900L;
               } else if (this.scrollOffset <= 0.0F) {
                  this.scrollOffset = 0.0F;
                  this.scrollDirection = 1;
                  this.pauseUntilTime = now + 300L;
               }
            }

            float f4 = 10.0F;
            float maxWidth2 = maxWidth + this.scrollOffset;
            float f5 = maxWidth2 > 0.0F ? (maxWidth - f4 + this.scrollOffset) / maxWidth2 : 1.0F;
            MsdfRenderer.renderText(
               font2.getFont(),
               description,
               font2.getSize(),
               zenithColor2.method7287(),
               MatrixUtils.method664(menuDrawContext.pose()),
               floatValue - this.scrollOffset,
               y2,
               0.0F,
               true,
               f5,
               1.0F,
               maxWidth2
            );
            menuDrawContext.disableScissor();
         }
      }
   }

   private float getDescScrollSpeed(float width, String description) {
      float f = width / Math.max(description.length(), 1);
      float f2 = f * 12.0F / 1000.0F;
      return Math.max(0.028F, Math.min(f2, 0.065F));
   }

   private void resetDescScroll() {
      this.scrollOffset = 0.0F;
      this.scrollDirection = 1;
      this.hoverStartTime = -1L;
      this.lastUpdateTime = -1L;
      this.pauseUntilTime = -1L;
      this.continueScroll = false;
   }

   @Generated
   @Override
   public float getWidth() {
      return this.width;
   }

   @Generated
   public T getSetting() {
      return this.setting;
   }
}
