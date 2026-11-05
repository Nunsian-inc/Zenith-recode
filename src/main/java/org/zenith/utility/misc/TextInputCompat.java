package org.zenith.utility.misc;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

// 26.3 switched input from GLFW to SDL3: characters (CharacterEvent -> charTyped) arrive only while SDL text input
// is started (TextInputManager#startTextInput). Vanilla EditBox starts it on focus; Zenith's own text fields
// (SearchBox, MenuTextField, FormField, CodeArea, setting fields...) never did, so typing into them did nothing.
// Like GLFW before, Zenith screens keep text input on for the whole time they are open.
public final class TextInputCompat {
   private TextInputCompat() {
   }

   public static boolean needsTextInput(Screen screen) {
      return screen != null && screen.getClass().getName().startsWith("org.zenith.");
   }

   /** After a screen change: turn text input on for a Zenith screen (vanilla already turned it off for the old one). */
   public static void onScreenChanged(Minecraft mc) {
      Screen screen = mc.gui.screen();
      if (needsTextInput(screen)) {
         mc.textInputManager().startTextInput(screen);
      }
   }

   /** A vanilla widget released text input (EditBox lost focus) while a Zenith screen is open: take it back. */
   public static void onFocusReleased(Minecraft mc) {
      onScreenChanged(mc);
   }
}
