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

   public abstract boolean onMouseClicked(double var1, double var3, Enum3 var5);

   public boolean keyPressed(KeyEvent var1) {
      return this.keyPressed(org.zenith.utility.misc.KeyCompat.toGlfw(var1.key()), var1.keycode(), org.zenith.utility.misc.KeyCompat.modsToGlfw(var1.modifiers()));
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      return false;
   }

   public void onMouseReleased(double var1, double var3, Enum3 var5) {
   }

   public boolean charTyped(CharacterEvent var1) {
      return this.charTyped((char)var1.codepoint(), 0);
   }

   public boolean charTyped(char var1, int var2) {
      return false;
   }
}
