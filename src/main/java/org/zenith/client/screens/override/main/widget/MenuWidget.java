package org.zenith.client.screens.override.main.widget;

import lombok.Generated;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.client.screens.override.main.MenuTheme;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;

public abstract class MenuWidget {
   public float x;
   public float y;
   public float width;
   public float height;
   public final Animation hoverAnimation = new Animation(160L, Easing.INSTANCE12);
   public boolean hovered;
   public boolean visible = true;
   public boolean enabled = true;

   public void setBounds(float floatValue, float f, float preferredWidth, float f2) {
      this.x = MenuTheme.snap(floatValue);
      this.y = MenuTheme.snap(f);
      this.width = MenuTheme.snap(floatValue + preferredWidth) - this.x;
      this.height = MenuTheme.snap(f + f2) - this.y;
   }

   public BoundsData bounds() {
      return new BoundsData(this.x, this.y, this.width, this.height);
   }

   public boolean contains(double lastClickX, double lastClickY) {
      return lastClickX >= this.x && lastClickX <= this.x + this.width && lastClickY >= this.y && lastClickY <= this.y + this.height;
   }

   public MenuWidget setVisible(boolean visible) {
      this.visible = visible;
      return this;
   }

   public MenuWidget setEnabled(boolean enabled) {
      this.enabled = enabled;
      return this;
   }

   public final void render(MenuDrawContext graphics, float mouseX, float mouseY) {
      if (this.visible) {
         this.hovered = this.enabled && this.contains(mouseX, mouseY);
         this.hoverAnimation.method2807(this.hovered);
         this.draw(graphics, mouseX, mouseY);
      }
   }

   public abstract void draw(MenuDrawContext graphics, float mouseX, float mouseY);

   public boolean mouseClicked(double d, double d2, int n) {
      return false;
   }

   public boolean mouseReleased(double d, double d2, int n) {
      return false;
   }

   public boolean mouseDragged(double d, double d2, int n) {
      return false;
   }

   public boolean mouseScrolled(double d, double d2, double d3) {
      return false;
   }

   public boolean keyPressed(int n, int n2, int n3) {
      return false;
   }

   public boolean charTyped(char c, int n) {
      return false;
   }

   public float hover() {
      return this.hoverAnimation.getValue();
   }

   @Generated
   public float getX() {
      return this.x;
   }

   @Generated
   public float getY() {
      return this.y;
   }

   @Generated
   public float getWidth() {
      return this.width;
   }

   @Generated
   public float getHeight() {
      return this.height;
   }

   @Generated
   public boolean isHovered() {
      return this.hovered;
   }

   @Generated
   public boolean isVisible() {
      return this.visible;
   }

   @Generated
   public boolean isEnabled() {
      return this.enabled;
   }
}
