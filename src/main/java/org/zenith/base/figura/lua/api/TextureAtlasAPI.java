package org.zenith.base.figura.lua.api;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.Identifier;
import org.zenith.base.figura.lua.LuaNotNil;
import org.zenith.base.figura.lua.LuaWhitelist;
import org.zenith.base.figura.lua.docs.LuaMethodDoc;
import org.zenith.base.figura.lua.docs.LuaMethodOverload;
import org.zenith.base.figura.lua.docs.LuaTypeDoc;
import org.zenith.base.figura.math.vector.FiguraVec4;
import org.zenith.base.figura.mixin.render.TextureAtlasAccessor;
import org.zenith.base.figura.utils.LuaUtils;

@LuaWhitelist
@LuaTypeDoc(name = "TextureAtlas", value = "texture_atlas")
public class TextureAtlasAPI {
   private final TextureAtlas atlas;

   public TextureAtlasAPI(TextureAtlas atlas) {
      this.atlas = atlas;
   }

   @LuaWhitelist
   @LuaMethodDoc("texture_atlas.list_sprites")
   public List<String> listSprites() {
      List<String> list = new ArrayList<>();

      for (Identifier res : ((TextureAtlasAccessor)this.atlas).figura$getTexturesByName().keySet()) {
         list.add(res.toString());
      }

      return list;
   }

   @LuaWhitelist
   @LuaMethodDoc(overloads = @LuaMethodOverload(argumentTypes = String.class, argumentNames = "path"), value = "texture_atlas.get_sprite_uv")
   public FiguraVec4 getSpriteUV(@LuaNotNil String sprite) {
      Identifier spriteLocation = LuaUtils.parsePath(sprite);
      TextureAtlasSprite s = this.atlas.getSprite(spriteLocation);
      return FiguraVec4.of(s.getU0(), s.getV0(), s.getU1(), s.getV1());
   }

   @LuaWhitelist
   @LuaMethodDoc("texture_atlas.get_width")
   public int getWidth() {
      return this.atlas.getTexture().getWidth(0);
   }

   @LuaWhitelist
   @LuaMethodDoc("texture_atlas.get_height")
   public int getHeight() {
      return this.atlas.getTexture().getHeight(0);
   }

   @Override
   public String toString() {
      return "TextureAtlas (" + this.atlas.location() + ")";
   }
}
