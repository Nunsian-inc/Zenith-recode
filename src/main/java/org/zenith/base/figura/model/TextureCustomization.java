package org.zenith.base.figura.model;

import net.minecraft.resources.Identifier;
import org.zenith.base.figura.avatar.Avatar;
import org.zenith.base.figura.model.rendering.texture.FiguraTexture;
import org.zenith.base.figura.model.rendering.texture.FiguraTextureSet;
import org.zenith.base.figura.utils.AtlasReader;

public class TextureCustomization {
   private final FiguraTextureSet.OverrideType first;
   private final Object second;

   public TextureCustomization(FiguraTextureSet.OverrideType first, Object second) {
      this.first = first;
      this.second = second;
   }

   public FiguraTextureSet.OverrideType getOverrideType() {
      return this.first;
   }

   public Object getValue() {
      return this.second;
   }

   public FiguraTexture getTexture(Avatar avatar, FiguraTextureSet textureSet) {
      if (avatar.render == null) {
         return null;
      }

      Identifier resourceLocation = textureSet.getOverrideTexture(avatar.owner, this);
      String name = resourceLocation.toString();
      return avatar.renderer.customTextures.containsKey(name)
         ? avatar.renderer.customTextures.get(name)
         : avatar.registerTexture(name, AtlasReader.read(resourceLocation), false);
   }
}
