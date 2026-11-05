package org.zenith.utility.render.display.base;

import lombok.Generated;
import net.minecraft.resources.Identifier;
import org.zenith.Zenith;

public class CategoryIcon {
   private final Identifier identifier;

   public CategoryIcon(String identifier) {
      if (identifier.contains(":")) {
         this.identifier = Identifier.parse(identifier);
      } else if (identifier.contains("/")) {
         this.identifier = Zenith.method1501(identifier);
      } else {
         this.identifier = Zenith.method1501("icons/category/" + identifier);
      }
   }

   @Generated
   public Identifier getIdentifier() {
      return this.identifier;
   }
}
