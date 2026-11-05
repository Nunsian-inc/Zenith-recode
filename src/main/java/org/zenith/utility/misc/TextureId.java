package org.zenith.utility.misc;

import net.minecraft.resources.Identifier;
import org.zenith.Zenith;

public class TextureId {
   public final Identifier identifier;

   public TextureId(String path) {
      this.identifier = Zenith.method1501(this.method7663(path));
   }

   public TextureId(Identifier externalTextureId) {
      this.identifier = Identifier.fromNamespaceAndPath(externalTextureId.getNamespace(), externalTextureId.getPath());
   }

   public String method7663(String path) {
      if (Identifier.isValidPath(path)) {
         return path;
      } else {
         StringBuilder builder = new StringBuilder();

         for (char c : path.toLowerCase().toCharArray()) {
            if (Identifier.validPathChar(c)) {
               builder.append(c);
            }
         }

         return builder.toString();
      }
   }

   public Identifier getIdentifier() {
      return this.identifier;
   }
}
