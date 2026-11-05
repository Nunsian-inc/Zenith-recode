package org.zenith.client.screens.override.button;

import net.minecraft.client.input.KeyEvent;
import org.zenith.utility.misc.Enum3;
import org.zenith.utility.render.display.base.Bounds;
import org.zenith.utility.render.display.base.MenuDrawContext;

public abstract class ButtonScreen {
   public Bounds bounds;

   public ButtonScreen(float width, float height) {
      this.bounds = new Bounds(0.0F, 0.0F, width, height);
   }

   public void render(MenuDrawContext customDrawContext, float x, float y, float floatValue, float f) {
      this.bounds.setX(floatValue);
      this.bounds.setY(f);
   }

   public float getWidth() {
      return this.bounds.getWidth();
   }

   public float getHeight() {
      return this.bounds.getHeight();
   }

   public void onMouseClicked(double x, double y, Enum3 enum2) {
      if (this.bounds.contains(x, y)) {
         this.onClick(x, y, enum2);
      }
   }

   public abstract void onClick(double x, double y, Enum3 enum2);

   public void onMouseReleased(double d, double d2, Enum3 enum2) {
   }

   public void onMouseDragged(double d, double d2, Enum3 enum2, double d3, double d4) {
   }

   public boolean keyPressed(KeyEvent event) {
      return this.keyPressed(org.zenith.utility.misc.KeyCompat.toGlfw(event.key()), event.keycode(), org.zenith.utility.misc.KeyCompat.modsToGlfw(event.modifiers()));
   }

   public boolean keyPressed(int keyCode, int keycode, int modifiers) {
      return false;
   }

   public boolean mouseScrolled(double d, double d2, double d3, double d4) {
      return false;
   }
}
