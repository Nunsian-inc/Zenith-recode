package org.zenith.utility.render;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.api.textures.GpuTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.material.MapColor;

public final class MapAtlasPage {
   public static final int CONST_intValue = 128;
   public static final int CONST_intValue2 = 2048;
   public static final int CONST_intValue3 = 16;
   public static final int CONST_intValue4 = 256;
   private static int intValue;
   private final Identifier identifier;
   private final DynamicTexture nativeImageBackedTexture;
   private int intValue2;
   private boolean closed;

   public MapAtlasPage(TextureManager zenith$getTextureManager) {
      int n = intValue++;
      this.identifier = Identifier.fromNamespaceAndPath("zenith", "map_atlas/" + n);
      this.nativeImageBackedTexture = new DynamicTexture(() -> "Zenith map atlas #" + n, 2048, 2048, true);
      zenith$getTextureManager.register(this.identifier, this.nativeImageBackedTexture);
   }

   public Identifier getIdentifier() {
      return this.identifier;
   }

   public boolean isFull() {
      return this.intValue2 >= 256;
   }

   public int method5416() {
      return this.intValue2 >= 256 ? -1 : this.intValue2++;
   }

   public void method5417(int intValue, int dstY, byte[] colors) {
      if (!this.closed) {
         NativeImage pixels = this.nativeImageBackedTexture.getPixels();
         GpuTexture texture = this.nativeImageBackedTexture.getTexture();
         if (pixels != null && texture != null && !texture.isClosed()) {
            for (int i = 0; i < 128; i++) {
               for (int j = 0; j < 128; j++) {
                  pixels.setPixel(intValue + j, dstY + i, MapColor.getColorFromPackedId(colors[j + i * 128]));
               }
            }

            org.zenith.utility.render.RenderCompat.writeRegion(texture, pixels, 0, 0, intValue, dstY, 128, 128, intValue, dstY);
         }
      }
   }

   public void method5418(TextureManager textureManager) {
      if (!this.closed) {
         this.closed = true;
         textureManager.release(this.identifier);
      }
   }
}
