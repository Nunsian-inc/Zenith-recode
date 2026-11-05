package org.zenith.client.screens.nlgui.elements.api;

import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import org.zenith.utility.misc.Enum3;
import org.zenith.utility.render.display.base.MenuDrawContext;

public abstract class InterfaceElement extends Element {
   @Override
   public abstract float getWidth();

   public void renderPriority(MenuDrawContext var1, float var2, float var3, float var4, float var5, float var6) {
   }

   public void render(MenuDrawContext var1, float var2, float var3, float var4, float var5, float var6, int var7) {
   }

   public boolean onMousePriorityClicked(double var1, double var3, Enum3 var5) {
      return false;
   }

   @Override
   public boolean onMouseClicked(double var1, double var3, Enum3 var5) {
      return false;
   }

   public boolean mouseScrolled(double var1, double var3, double var5, double var7) {
      return false;
   }

   @Override
   public void onMouseReleased(double var1, double var3, Enum3 var5) {
      super.onMouseReleased(var1, var3, var5);
   }

   @Override
   public boolean keyPressed(KeyEvent var1) {
      return this.keyPressed(org.zenith.utility.misc.KeyCompat.toGlfw(var1.key()), var1.keycode(), org.zenith.utility.misc.KeyCompat.modsToGlfw(var1.modifiers()));
   }

   @Override
   public boolean keyPressed(int var1, int var2, int var3) {
      return super.keyPressed(new KeyEvent(org.zenith.utility.misc.KeyCompat.toSdl(var1), var2, org.zenith.utility.misc.KeyCompat.modsToSdl(var3)));
   }

   @Override
   public boolean charTyped(CharacterEvent var1) {
      return this.charTyped((char)var1.codepoint(), 0);
   }

   @Override
   public boolean charTyped(char var1, int var2) {
      return super.charTyped(new CharacterEvent(var1));
   }
}
