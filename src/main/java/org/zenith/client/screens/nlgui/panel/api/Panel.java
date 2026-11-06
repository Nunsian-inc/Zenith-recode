package org.zenith.client.screens.nlgui.panel.api;

import org.zenith.MinecraftAccess;
import org.zenith.utility.misc.MouseButton;
import org.zenith.utility.render.display.base.MenuDrawContext;

public abstract class Panel implements MinecraftAccess {
   public abstract void render(MenuDrawContext customDrawContext, int mouseX, int mouseY, float f, float floatValue, float f2);

   public abstract boolean onMouseClicked(double x, double y, MouseButton enum2);

   public boolean keyPressed(int keyCode, int keycode, int modifiers) {
      return false;
   }

   public boolean onMouseReleased(double x, double y, MouseButton enum2) {
      return false;
   }

   public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      return false;
   }

   public boolean charTyped(char codepoint, int n) {
      return false;
   }
}
