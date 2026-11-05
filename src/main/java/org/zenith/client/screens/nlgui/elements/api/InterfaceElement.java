package org.zenith.client.screens.nlgui.elements.api;

import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import org.zenith.utility.misc.Enum3;
import org.zenith.utility.render.display.base.MenuDrawContext;

public abstract class InterfaceElement extends Element {
   @Override
   public abstract float getWidth();

   public void renderPriority(MenuDrawContext menuDrawContext, float f, float f2, float f3, float f4, float f5) {
   }

   public void render(MenuDrawContext menuDrawContext, float f, float f2, float f3, float f4, float f5, int n) {
   }

   public boolean onMousePriorityClicked(double d, double d2, Enum3 enum2) {
      return false;
   }

   @Override
   public boolean onMouseClicked(double d, double d2, Enum3 enum2) {
      return false;
   }

   public boolean mouseScrolled(double d, double d2, double d3, double d4) {
      return false;
   }

   @Override
   public void onMouseReleased(double d, double d2, Enum3 enum2) {
      super.onMouseReleased(d, d2, enum2);
   }

   @Override
   public boolean keyPressed(KeyEvent event) {
      return this.keyPressed(org.zenith.utility.misc.KeyCompat.toGlfw(event.key()), event.keycode(), org.zenith.utility.misc.KeyCompat.modsToGlfw(event.modifiers()));
   }

   @Override
   public boolean keyPressed(int keyCode, int keycode, int modifiers) {
      return super.keyPressed(new KeyEvent(org.zenith.utility.misc.KeyCompat.toSdl(keyCode), keycode, org.zenith.utility.misc.KeyCompat.modsToSdl(modifiers)));
   }

   @Override
   public boolean charTyped(CharacterEvent event) {
      return this.charTyped((char)event.codepoint(), 0);
   }

   @Override
   public boolean charTyped(char codepoint, int n) {
      return super.charTyped(new CharacterEvent(codepoint));
   }
}
