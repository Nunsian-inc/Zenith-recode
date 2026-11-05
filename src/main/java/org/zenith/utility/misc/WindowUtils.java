package org.zenith.utility.misc;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Util;
import net.minecraft.util.Util.OS;

public final class WindowUtils {
   private WindowUtils() {
   }

   private static boolean method5474(int glfwKey, int n) {
      Window window = Minecraft.getInstance().getWindow();
      return org.zenith.utility.misc.KeyCompat.isKeyDown(glfwKey) || org.zenith.utility.misc.KeyCompat.isKeyDown(n);
   }

   public static boolean method5475() {
      return Util.getPlatform() == OS.OSX ? method5474(343, 347) : method5474(341, 345);
   }

   public static boolean method5476() {
      return method5474(340, 344);
   }

   public static boolean method5477() {
      return method5474(342, 346);
   }
}
