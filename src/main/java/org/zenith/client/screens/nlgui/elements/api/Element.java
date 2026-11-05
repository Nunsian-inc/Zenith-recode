package org.zenith.client.screens.nlgui.elements.api;

import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import org.zenith.MinecraftAccess;
import org.zenith.utility.misc.Enum3;

public abstract class Element implements MinecraftAccess {
   public abstract String getName();

   public boolean isVisible() {
      return true;
   }

   public abstract float getHeight();

   public abstract float getWidth();

   public abstract boolean onMouseClicked(double lastClickX, double lastClickY, Enum3 enum2);

   public boolean keyPressed(KeyEvent event) {
      return this.keyPressed(org.zenith.utility.misc.KeyCompat.toGlfw(event.key()), event.keycode(), org.zenith.utility.misc.KeyCompat.modsToGlfw(event.modifiers()));
   }

   public boolean keyPressed(int keyCode, int keycode, int modifiers) {
      return false;
   }

   public void onMouseReleased(double d, double d2, Enum3 enum2) {
   }

   public boolean charTyped(CharacterEvent event) {
      return this.charTyped((char)event.codepoint(), 0);
   }

   public boolean charTyped(char codepoint, int n) {
      return false;
   }
}
