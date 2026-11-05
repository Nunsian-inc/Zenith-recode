package org.zenith.client.screens.override.main.widget;

import lombok.Generated;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.client.screens.override.main.MenuTheme;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class MenuScroll {
   private static final float RAIL_THICKNESS = MenuTheme.px(2.0F);
   private final Animation smooth = new Animation(220L, 0.0F, Easing.INSTANCE12);
   private float target;
   private float viewport;
   private float content;

   public void update(float viewport, float content) {
      this.viewport = viewport;
      this.content = content;
      this.target = Math.min(this.target, Math.max(0.0F, content - viewport));
      this.target = Math.max(0.0F, this.target);
      this.smooth.method2808(this.target);
   }

   public float offset() {
      return this.smooth.getValue();
   }

   public boolean scrollable() {
      return this.content > this.viewport + 0.5F;
   }

   public boolean scroll(double d, float f) {
      if (!this.scrollable()) {
         return false;
      } else {
         this.target = Math.min(Math.max(0.0F, this.content - this.viewport), Math.max(0.0F, this.target - (float)d * f));
         return true;
      }
   }

   public void reset() {
      this.target = 0.0F;
      this.smooth.method2809(0.0F);
   }

   public void renderRail(MenuDrawContext graphics, float floatValue, float y, float height) {
      if (this.scrollable()) {
         ProxyDotRadiusData headerRadius = ProxyDotRadiusData.method8069(RAIL_THICKNESS / 2.0F);
         graphics.drawRoundedRect(floatValue, y, RAIL_THICKNESS, height, headerRadius, MenuTheme.surface());
         float f = Math.min(1.0F, this.viewport / this.content);
         float height2 = Math.max(MenuTheme.px(16.0F), height * f);
         float f2 = height - height2;
         float f3 = this.content - this.viewport <= 0.0F ? 0.0F : this.offset() / (this.content - this.viewport);
         graphics.drawRoundedRect(floatValue, y + f2 * f3, RAIL_THICKNESS, height2, headerRadius, MenuTheme.border());
      }
   }

   public void renderRailHorizontal(MenuDrawContext graphics, float floatValue, float y, float width) {
      if (this.scrollable()) {
         ProxyDotRadiusData headerRadius = ProxyDotRadiusData.method8069(RAIL_THICKNESS / 2.0F);
         graphics.drawRoundedRect(floatValue, y, width, RAIL_THICKNESS, headerRadius, MenuTheme.surface());
         float f = Math.min(1.0F, this.viewport / this.content);
         float width2 = Math.max(MenuTheme.px(16.0F), width * f);
         float f2 = width - width2;
         float f3 = this.content - this.viewport <= 0.0F ? 0.0F : this.offset() / (this.content - this.viewport);
         graphics.drawRoundedRect(floatValue + f2 * f3, y, width2, RAIL_THICKNESS, headerRadius, MenuTheme.border());
      }
   }

   @Generated
   public float getViewport() {
      return this.viewport;
   }

   @Generated
   public float getContent() {
      return this.content;
   }
}
