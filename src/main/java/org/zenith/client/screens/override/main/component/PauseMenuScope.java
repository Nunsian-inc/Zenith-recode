package org.zenith.client.screens.override.main.component;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

public final class PauseMenuScope {
   private static final Set<Screen> descendants = Collections.newSetFromMap(new WeakHashMap<>());

   private PauseMenuScope() {
   }

   public static boolean isDescendant(Screen screen) {
      return Minecraft.getInstance().level != null && screen != null
         && (screen instanceof PauseScreen pause && pause.showsPauseMenu() || descendants.contains(screen));
   }

   public static void onScreenChange(Screen previous, Screen next) {
      if (Minecraft.getInstance().level == null || next == null || next instanceof ChatScreen
         || next instanceof AbstractContainerScreen<?> || next instanceof TitleScreen) {
         descendants.clear();
      } else if (next instanceof PauseScreen) {
         descendants.clear();
      } else if (isDescendant(previous)) {
         // Следим за переходами, а не классами: сюда входят и экраны настроек модов.
         descendants.add(next);
      }
   }
}
