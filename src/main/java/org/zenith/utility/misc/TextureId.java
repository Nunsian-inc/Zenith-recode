package org.zenith.utility.misc;

import net.minecraft.resources.Identifier;
import org.zenith.Zenith;

public class TextureId {
   public final Identifier identifier;

   public TextureId(String var1) {
      this.identifier = Zenith.method1501(this.method7663(var1));
   }

   public TextureId(Identifier var1) {
      this.identifier = Identifier.fromNamespaceAndPath(var1.getNamespace(), var1.getPath());
   }

   public String method7663(String var1) {
      if (Identifier.isValidPath(var1)) {
         return var1;
      } else {
         StringBuilder var2 = new StringBuilder();

         for (char var6 : var1.toLowerCase().toCharArray()) {
            if (Identifier.validPathChar(var6)) {
               var2.append(var6);
            }
         }

         return var2.toString();
      }
   }

   public Identifier getIdentifier() {
      return this.identifier;
   }
}
