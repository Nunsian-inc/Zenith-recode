package org.zenith.base.figura.model.rendering.texture;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.renderpearl.api.device.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.api.textures.AddressMode;
import com.mojang.renderpearl.api.textures.FilterMode;
import com.mojang.renderpearl.api.GpuFormat;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.MipmapStrategy;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureContents;
import net.minecraft.client.resources.metadata.texture.TextureMetadataSection;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.NotNull;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;
import org.lwjgl.BufferUtils;
import org.zenith.base.figura.FiguraMod;
import org.zenith.base.figura.avatar.Avatar;
import org.zenith.base.figura.lua.LuaNotNil;
import org.zenith.base.figura.lua.LuaWhitelist;
import org.zenith.base.figura.lua.docs.LuaMethodDoc;
import org.zenith.base.figura.lua.docs.LuaMethodOverload;
import org.zenith.base.figura.lua.docs.LuaTypeDoc;
import org.zenith.base.figura.math.matrix.FiguraMat4;
import org.zenith.base.figura.math.vector.FiguraVec2;
import org.zenith.base.figura.math.vector.FiguraVec3;
import org.zenith.base.figura.math.vector.FiguraVec4;
import org.zenith.base.figura.utils.ColorUtils;
import org.zenith.base.figura.utils.FiguraIdentifier;
import org.zenith.base.figura.utils.LuaUtils;

@LuaWhitelist
@LuaTypeDoc(name = "Texture", value = "texture")
public class FiguraTexture extends SimpleTexture {
   private boolean registered = false;
   private boolean dirty = true;
   private boolean modified = false;
   private final String name;
   private final Avatar owner;
   private final NativeImage texture;
   private NativeImage backup;
   private boolean isClosed = false;

   public FiguraTexture(Avatar owner, String name, byte[] data) {
      super(createId(owner, false));

      NativeImage image;
      try {
         ByteBuffer wrapper = BufferUtils.createByteBuffer(data.length);
         wrapper.put(data);
         wrapper.rewind();
         image = NativeImage.read(wrapper);
      } catch (IOException e) {
         FiguraMod.LOGGER.error("", e);
         image = new NativeImage(1, 1, true);
      }

      this.texture = image;
      this.name = name;
      this.owner = owner;
   }

   public FiguraTexture(Avatar owner, String name, int width, int height) {
      super(createId(owner, false));
      this.texture = new NativeImage(width, height, true);
      this.name = name;
      this.owner = owner;
   }

   public FiguraTexture(Avatar owner, String name, NativeImage image) {
      super(createId(owner, true));
      this.texture = image;
      this.name = name;
      this.owner = owner;
   }

   private static Identifier createId(Avatar owner, boolean custom) {
      String prefix = "avatar_tex/" + owner.owner + (custom ? "/custom/" : "/");
      return FiguraIdentifier.of(prefix + UUID.randomUUID());
   }

   @NotNull
   public TextureContents loadContents(ResourceManager resourceManager) throws IOException {
      return new TextureContents(this.texture, new TextureMetadataSection(false, false, MipmapStrategy.AUTO, 0.0F));
   }

   public void apply(TextureContents textureContents) {
      this.uploadDirtyTexture();
   }

   public void close() {
      if (!this.isClosed) {
         this.isClosed = true;
         this.texture.close();
         if (this.backup != null) {
            this.backup.close();
         }

         if (this.registered) {
            this.registered = false;
            Minecraft.getInstance().getTextureManager().release(this.getLocation());
         }

         super.close();
      }
   }

   public void uploadIfDirty() {
      if (!this.registered) {
         Minecraft.getInstance().getTextureManager().registerAndLoad(this.getLocation(), this);
         this.registered = true;
      }

      this.uploadDirtyTexture();
   }

   private void uploadDirtyTexture() {
      if (this.dirty && !this.isClosed) {
         this.dirty = false;
         if (RenderSystem.isOnRenderThread()) {
            this.upload();
         } else {
            Minecraft.getInstance().execute(this::upload);
         }
      }
   }

   private void upload() {
      if (!this.isClosed) {
         this.sampler = RenderSystem.getSamplerCache().getSampler(AddressMode.REPEAT, AddressMode.REPEAT, FilterMode.NEAREST, FilterMode.NEAREST, false);
         GpuDevice device = RenderSystem.getDevice();
         int width = this.texture.getWidth();
         int height = this.texture.getHeight();
         if (super.texture == null || super.texture.getWidth(0) != width || super.texture.getHeight(0) != height) {
            super.close();
            super.texture = device.createTexture(this.resourceId()::toString, 5, GpuFormat.RGBA8_UNORM, width, height, 1, 1);
            this.textureView = device.createTextureView(super.texture);
         }

         device.createCommandEncoder().writeToTexture(super.texture, this.texture);
      }
   }

   public void writeTexture(Path dest) throws IOException {
      this.texture.writeToFile(dest);
   }

   private void backupImage() {
      this.modified = true;
      if (this.backup == null) {
         this.backup = this.copy();
      }
   }

   public NativeImage copy() {
      NativeImage image = new NativeImage(this.texture.format(), this.texture.getWidth(), this.texture.getHeight(), true);
      image.copyFrom(this.texture);
      return image;
   }

   public int getWidth() {
      return this.texture.getWidth();
   }

   public int getHeight() {
      return this.texture.getHeight();
   }

   public Identifier getLocation() {
      return this.resourceId();
   }

   private FiguraVec4 parseColor(String method, Object r, Double g, Double b, Double a) {
      return LuaUtils.parseVec4(method, r, g, b, a, 0.0, 0.0, 0.0, 1.0);
   }

   @LuaWhitelist
   @LuaMethodDoc("texture.get_name")
   public String getName() {
      return this.name;
   }

   @LuaWhitelist
   @LuaMethodDoc("texture.get_path")
   public String getPath() {
      return this.getLocation().toString();
   }

   @LuaWhitelist
   @LuaMethodDoc("texture.get_dimensions")
   public FiguraVec2 getDimensions() {
      return FiguraVec2.of(this.getWidth(), this.getHeight());
   }

   @LuaWhitelist
   @LuaMethodDoc(overloads = @LuaMethodOverload(argumentTypes = {Integer.class, Integer.class}, argumentNames = {"x", "y"}), value = "texture.get_pixel")
   public FiguraVec4 getPixel(int x, int y) {
      try {
         int argb = this.texture.getPixel(x, y);
         return FiguraVec4.of((argb >> 16 & 0xFF) / 255.0, (argb >> 8 & 0xFF) / 255.0, (argb & 0xFF) / 255.0, (argb >>> 24) / 255.0);
      } catch (Exception e) {
         throw new LuaError(e.getMessage());
      }
   }

   @LuaWhitelist
   @LuaMethodDoc(
      overloads = {
            @LuaMethodOverload(argumentTypes = {Integer.class, Integer.class, FiguraVec3.class}, argumentNames = {"x", "y", "rgb"}),
            @LuaMethodOverload(argumentTypes = {Integer.class, Integer.class, FiguraVec4.class}, argumentNames = {"x", "y", "rgba"}),
            @LuaMethodOverload(
               argumentTypes = {Integer.class, Integer.class, Double.class, Double.class, Double.class, Double.class},
               argumentNames = {"x", "y", "r", "g", "b", "a"}
            )
      },
      aliases = "pixel",
      value = "texture.set_pixel"
   )
   public FiguraTexture setPixel(int x, int y, Object r, Double g, Double b, Double a) {
      try {
         this.backupImage();
         this.texture.setPixel(x, y, ColorUtils.rgbaToIntARGB(this.parseColor("setPixel", r, g, b, a)));
         return this;
      } catch (Exception e) {
         throw new LuaError(e.getMessage());
      }
   }

   @LuaWhitelist
   public FiguraTexture pixel(int x, int y, Object r, Double g, Double b, Double a) {
      return this.setPixel(x, y, r, g, b, a);
   }

   @LuaWhitelist
   @LuaMethodDoc(
      overloads = {
            @LuaMethodOverload(
               argumentTypes = {Integer.class, Integer.class, Integer.class, Integer.class, FiguraVec3.class},
               argumentNames = {"x", "y", "width", "height", "rgb"}
            ),
            @LuaMethodOverload(
               argumentTypes = {Integer.class, Integer.class, Integer.class, Integer.class, FiguraVec4.class},
               argumentNames = {"x", "y", "width", "height", "rgba"}
            ),
            @LuaMethodOverload(
               argumentTypes = {Integer.class, Integer.class, Integer.class, Integer.class, Double.class, Double.class, Double.class, Double.class},
               argumentNames = {"x", "y", "width", "height", "r", "g", "b", "a"}
            )
      },
      value = "texture.fill"
   )
   public FiguraTexture fill(int x, int y, int width, int height, Object r, Double g, Double b, Double a) {
      try {
         this.backupImage();
         this.texture.fillRect(x, y, width, height, ColorUtils.rgbaToIntABGR(this.parseColor("fill", r, g, b, a)));
         return this;
      } catch (Exception e) {
         throw new LuaError(e.getMessage());
      }
   }

   @LuaWhitelist
   @LuaMethodDoc("texture.update")
   public FiguraTexture update() {
      this.dirty = true;
      return this;
   }

   @LuaWhitelist
   @LuaMethodDoc("texture.restore")
   public FiguraTexture restore() {
      if (this.modified) {
         this.texture.copyFrom(this.backup);
         this.modified = false;
      }

      return this;
   }

   @LuaWhitelist
   @LuaMethodDoc("texture.save")
   public String save() {
      Path temporary = null;

      String e;
      try {
         temporary = Files.createTempFile("zenith-figura-texture-", ".png");
         this.texture.writeToFile(temporary);
         e = Base64.getEncoder().encodeToString(Files.readAllBytes(temporary));
      } catch (Exception ex) {
         throw new LuaError(ex.getMessage());
      } finally {
         if (temporary != null) {
            try {
               Files.deleteIfExists(temporary);
            } catch (IOException ex) {
            }
         }
      }

      return e;
   }

   @LuaWhitelist
   @LuaMethodDoc(
      overloads = @LuaMethodOverload(
         argumentTypes = {Integer.class, Integer.class, Integer.class, Integer.class, LuaFunction.class},
         argumentNames = {"x", "y", "width", "height", "func"}
      ),
      value = "texture.apply_func"
   )
   public FiguraTexture applyFunc(int x, int y, int width, int height, @LuaNotNil LuaFunction function) {
      for (int i = y; i < y + height; i++) {
         for (int j = x; j < x + width; j++) {
            FiguraVec4 color = this.getPixel(j, i);
            LuaValue result = function.call(this.owner.luaRuntime.typeManager.javaToLua(color).arg1(), LuaValue.valueOf(j), LuaValue.valueOf(i));
            if (!result.isnil() && result.isuserdata(FiguraVec4.class)) {
               this.setPixel(j, i, result.checkuserdata(FiguraVec4.class), (Double)null, (Double)null, (Double)null);
            }
         }
      }

      return this;
   }

   @LuaWhitelist
   @LuaMethodDoc(
      overloads = @LuaMethodOverload(
         argumentTypes = {Integer.class, Integer.class, Integer.class, Integer.class, FiguraMat4.class, Boolean.class},
         argumentNames = {"x", "y", "width", "height", "matrix", "clip"}
      ),
      value = "texture.apply_matrix"
   )
   public FiguraTexture applyMatrix(int x, int y, int width, int height, @LuaNotNil FiguraMat4 matrix, boolean clip) {
      for (int i = y; i < y + height; i++) {
         for (int j = x; j < x + width; j++) {
            FiguraVec4 color = this.getPixel(j, i);
            color.transform(matrix);
            if (clip) {
               color.x = Math.max(0.0, Math.min(color.x, 1.0));
               color.y = Math.max(0.0, Math.min(color.y, 1.0));
               color.z = Math.max(0.0, Math.min(color.z, 1.0));
               color.w = Math.max(0.0, Math.min(color.w, 1.0));
            }

            this.setPixel(j, i, color, (Double)null, (Double)null, (Double)null);
         }
      }

      return this;
   }

   @LuaWhitelist
   public Object __index(String arg) {
      return "name".equals(arg) ? this.name : null;
   }

   public String toString() {
      String name = this.name;
      return (name + " (" + this.getWidth() + "x" + this.getHeight() + ") (Texture)");
   }
}
