package org.zenith.base.figura.lua.api;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.NativeImage.Format;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import net.minecraft.resources.Identifier;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.zenith.base.figura.avatar.Avatar;
import org.zenith.base.figura.lua.LuaNotNil;
import org.zenith.base.figura.lua.LuaWhitelist;
import org.zenith.base.figura.lua.docs.LuaMethodDoc;
import org.zenith.base.figura.lua.docs.LuaMethodOverload;
import org.zenith.base.figura.lua.docs.LuaTypeDoc;
import org.zenith.base.figura.model.rendering.texture.FiguraTexture;
import org.zenith.base.figura.utils.AtlasReader;
import org.zenith.base.figura.utils.ColorUtils;
import org.zenith.base.figura.utils.LuaUtils;

@LuaWhitelist
@LuaTypeDoc(name = "TextureAPI", value = "textures")
public class TextureAPI {
   public static final int TEXTURE_LIMIT = 128;
   private final Avatar owner;

   public TextureAPI(Avatar owner) {
      this.owner = owner;
   }

   private void check() {
      if (this.owner.renderer == null) {
         throw new LuaError("Avatar have no active renderer!");
      }
   }

   public FiguraTexture register(String name, NativeImage image, boolean ignoreSize) {
      return this.owner.registerTexture(name, image, ignoreSize);
   }

   @LuaWhitelist
   @LuaMethodDoc(
      overloads = @LuaMethodOverload(argumentTypes = {String.class, Integer.class, Integer.class}, argumentNames = {"name", "width", "height"}),
      value = "textures.new_texture"
   )
   public FiguraTexture newTexture(@LuaNotNil String name, int width, int height) {
      NativeImage image;
      try {
         image = new NativeImage(width, height, true);
      } catch (Exception e) {
         throw new LuaError(e.getMessage());
      }

      FiguraTexture texture = this.register(name, image, false);
      texture.fill(0, 0, width, height, ColorUtils.Colors.AWESOME_BLUE.vec.augmented(1.0), (Double)null, (Double)null, (Double)null);
      return texture;
   }

   @LuaWhitelist
   @LuaMethodDoc(
      overloads = {
            @LuaMethodOverload(argumentTypes = {String.class, String.class}, argumentNames = {"name", "base64Text"}),
            @LuaMethodOverload(argumentTypes = {String.class, LuaTable.class}, argumentNames = {"name", "byteArray"})
      },
      value = "textures.read"
   )
   public FiguraTexture read(@LuaNotNil String name, @LuaNotNil Object object) {
      byte[] bytes;
      if (object instanceof LuaTable table) {
         bytes = new byte[table.length()];

         for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte)table.get(i + 1).checkint();
         }
      } else {
         if (!(object instanceof String s)) {
            throw new LuaError("Invalid type for read \"" + object.getClass().getSimpleName() + "\"");
         }

         bytes = Base64.getDecoder().decode(s);
      }

      NativeImage image;
      try {
         ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
         image = NativeImage.read(bais);
         bais.close();
      } catch (Exception e) {
         throw new LuaError("Could not read image: " + e.getMessage());
      }

      return this.register(name, image, false);
   }

   @LuaWhitelist
   @LuaMethodDoc(
      overloads = @LuaMethodOverload(argumentTypes = {String.class, FiguraTexture.class}, argumentNames = {"name", "texture"}),
      value = "textures.copy"
   )
   public FiguraTexture copy(@LuaNotNil String name, @LuaNotNil FiguraTexture texture) {
      NativeImage image = texture.copy();
      return this.register(name, image, false);
   }

   @LuaWhitelist
   @LuaMethodDoc(overloads = @LuaMethodOverload(argumentTypes = String.class, argumentNames = "name"), value = "textures.get")
   public FiguraTexture get(@LuaNotNil String name) {
      this.check();
      return this.owner.renderer.customTextures.get(name);
   }

   @LuaWhitelist
   @LuaMethodDoc("textures.get_textures")
   public List<FiguraTexture> getTextures() {
      this.check();
      return new ArrayList<>(this.owner.renderer.textures.values());
   }

   @LuaWhitelist
   @LuaMethodDoc(
      overloads = @LuaMethodOverload(argumentTypes = {String.class, String.class}, argumentNames = {"name", "path"}),
      value = "textures.from_vanilla"
   )
   public FiguraTexture fromVanilla(@LuaNotNil String name, @LuaNotNil String path) {
      this.check();
      Identifier resourceLocation = LuaUtils.parsePath(path);
      return this.register(name, AtlasReader.read(resourceLocation), false);
   }

   @LuaWhitelist
   public FiguraTexture __index(@LuaNotNil String name) {
      this.check();
      return this.owner.renderer.getTexture(name);
   }

   @Override
   public String toString() {
      return "TextureAPI";
   }
}
