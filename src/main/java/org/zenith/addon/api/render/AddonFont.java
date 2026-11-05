package org.zenith.addon.api.render;

import java.util.Objects;
import net.minecraft.resources.Identifier;

public record AddonFont(Identifier data, Identifier atlas) {
   public AddonFont {
      Objects.requireNonNull(data, "data");
      Objects.requireNonNull(atlas, "atlas");
   }

   public static AddonFont msdf(String namespace, String basePath) {
      String path = normalizeBasePath(basePath);
      return new AddonFont(Identifier.fromNamespaceAndPath(namespace, path + ".json"), Identifier.fromNamespaceAndPath(namespace, path + ".png"));
   }

   public static AddonFont msdf(Identifier data, Identifier atlas) {
      return new AddonFont(data, atlas);
   }

   private static String normalizeBasePath(String basePath) {
      String path = Objects.requireNonNull(basePath, "basePath").trim().replace('\\', '/');
      if (path.endsWith(".json") || path.endsWith(".png")) {
         path = path.substring(0, path.length() - 5);
      }

      if (path.isEmpty()) {
         throw new IllegalArgumentException("Font basePath must not be empty");
      } else {
         return path;
      }
   }
}
