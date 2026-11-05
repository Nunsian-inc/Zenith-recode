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

   public MapAtlasPage(TextureManager var1) {
      int var2 = intValue++;
      this.identifier = Identifier.fromNamespaceAndPath("zenith", "map_atlas/" + var2);
      this.nativeImageBackedTexture = new DynamicTexture(() -> "Zenith map atlas #" + var2, 2048, 2048, true);
      var1.register(this.identifier, this.nativeImageBackedTexture);
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

   public void method5417(int var1, int var2, byte[] var3) {
      if (!this.closed) {
         NativeImage var4 = this.nativeImageBackedTexture.getPixels();
         GpuTexture var5 = this.nativeImageBackedTexture.getTexture();
         if (var4 != null && var5 != null && !var5.isClosed()) {
            for (int var6 = 0; var6 < 128; var6++) {
               for (int var7 = 0; var7 < 128; var7++) {
                  var4.setPixel(var1 + var7, var2 + var6, MapColor.getColorFromPackedId(var3[var7 + var6 * 128]));
               }
            }

            org.zenith.utility.render.RenderCompat.writeRegion(var5, var4, 0, 0, var1, var2, 128, 128, var1, var2);
         }
      }
   }

   public void method5418(TextureManager var1) {
      if (!this.closed) {
         this.closed = true;
         var1.release(this.identifier);
      }
   }
}
