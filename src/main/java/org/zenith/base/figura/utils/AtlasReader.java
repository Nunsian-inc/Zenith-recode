package org.zenith.base.figura.utils;

import com.mojang.renderpearl.api.buffers.GpuBuffer;
import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.renderpearl.api.commands.CommandEncoder;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.api.textures.GpuTexture;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import org.luaj.vm2.LuaError;

public final class AtlasReader {
   private AtlasReader() {
   }

   public static NativeImage read(Identifier id) {
      AbstractTexture texture = Minecraft.getInstance().getTextureManager().getTexture(id);
      if (texture instanceof TextureAtlas) {
         GpuTexture gpu = texture.getTexture();
         int width = gpu.getWidth(0);
         int height = gpu.getHeight(0);
         int pixel = gpu.getFormat().blockSize();
         NativeImage image = new NativeImage(width, height, false);
         CommandEncoder encoder = RenderSystem.getDevice().createCommandEncoder();
         GpuBuffer buffer = RenderSystem.getDevice().createBuffer(() -> "Figura atlas read", 9, (long)width * height * pixel);
         encoder.copyTextureToBuffer(gpu, buffer, 0L, () -> {
            GpuBufferSlice.MappedView view = buffer.slice().map(true, false);

            try {
               for (int y = 0; y < height; y++) {
                  for (int x = 0; x < width; x++) {
                     image.setPixelABGR(x, height - y - 1, view.data().getInt((x + y * width) * pixel) | 0xFF000000);
                  }
               }
            } catch (Throwable var10x) {
               if (view != null) {
                  try {
                     view.close();
                  } catch (Throwable var9x) {
                     var10x.addSuppressed(var9x);
                  }
               }

               throw var10x;
            }

            if (view != null) {
               view.close();
            }

            buffer.close();
         }, 0);
         return image;
      }

      try {
         Optional<Resource> resource = Minecraft.getInstance().getResourceManager().getResource(id);
         return resource.isPresent() ? NativeImage.read(resource.get().open()) : MissingTextureAtlasSprite.generateMissingImage();
      } catch (Exception e) {
         throw new LuaError(e.getMessage());
      }
   }
}
