package org.zenith.utility.config;

import java.nio.file.Path;
import java.util.Locale;
import net.fabricmc.loader.api.FabricLoader;

public final class BuiltinCosmeticsPath {
   private static final String text = "zenith";
   private static final String text2 = "assets/zenith/cosmetics";
   public static final String text3 = "builtin/";
   private static volatile Path path;
   private static volatile boolean flag;

   private BuiltinCosmeticsPath() {
   }

   public static Path method4447() {
      if (!flag) {
         synchronized (BuiltinCosmeticsPath.class) {
            if (!flag) {
               path = method4448();
               flag = true;
            }
         }
      }

      return path;
   }

   private static Path method4448() {
      try {
         return FabricLoader.getInstance()
            .getModContainer("zenith")
            .flatMap(modContainer -> modContainer.findPath("assets/zenith/cosmetics"))
            .map(path -> path.toAbsolutePath().normalize())
            .orElse(null);
      } catch (Exception e) {
         return null;
      }
   }

   public static boolean method4449(Path path) {
      Path path2 = method4447();
      return (boolean)(path2 != null && path != null ? path.toAbsolutePath().normalize().startsWith(path2) : 0);
   }

   public static String method4450(Path path) {
      Path path2 = method4447();
      if (path2 != null && path != null) {
         Path path3 = path.toAbsolutePath().normalize();
         return !path3.startsWith(path2) ? "" : "builtin/" + path2.relativize(path3).toString().replace('\\', '/');
      } else {
         return "";
      }
   }

   public static Path method4451(String string) {
      Path path = method4447();
      if (path != null && string != null) {
         if (!string.toLowerCase(Locale.ROOT).startsWith("builtin/")) {
            return null;
         } else {
            String str = string.substring("builtin/".length());
            if (str.isBlank()) {
               return null;
            } else {
               try {
                  Path path2 = path.resolve(str).normalize();
                  return path2.startsWith(path) ? path2 : null;
               } catch (Exception e) {
                  return null;
               }
            }
         }
      } else {
         return null;
      }
   }
}
