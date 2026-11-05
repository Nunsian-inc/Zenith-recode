package org.zenith.base.figura.utils;

import org.zenith.base.figura.avatar.Avatar;

public final class RenderUtils {
   private RenderUtils() {
   }

   public static boolean vanillaModelAndScript(Avatar avatar) {
      return avatar != null && avatar.luaRuntime != null && !avatar.scriptError;
   }
}
