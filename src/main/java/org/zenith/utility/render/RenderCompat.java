package org.zenith.utility.render;

import com.mojang.renderpearl.api.GpuFormat;
import java.util.Optional;
import java.util.OptionalInt;
import org.joml.Vector4f;
import org.joml.Vector4fc;

// Small adapters for 1.21.11 → 26.3 render API differences used across Zenith's render code.
public final class RenderCompat {
   // 26.x uses reversed-Z: depth is cleared to 0 (far) and tested with GREATER_THAN_OR_EQUAL.
   public static final double CLEAR_DEPTH = 0.0;

   private RenderCompat() {
   }

   // clear colors were packed ARGB ints, now RGBA floats
   public static Vector4f argb(int argb) {
      return new Vector4f((argb >> 16 & 0xFF) / 255.0F, (argb >> 8 & 0xFF) / 255.0F, (argb & 0xFF) / 255.0F, (argb >>> 24) / 255.0F);
   }

   public static Optional<Vector4fc> clear(OptionalInt argb) {
      return argb.isPresent() ? Optional.of(argb(argb.getAsInt())) : Optional.empty();
   }

   // TextureTarget(name, w, h, useDepth) became TextureTarget(name, w, h, colorFormat, depthFormat)
   public static GpuFormat depthFormat(boolean useDepth) {
      return useDepth ? GpuFormat.D32_FLOAT : null;
   }

   // writeToTexture(dst, image, mip, layer, dstX, dstY, w, h, srcX, srcY) lost its source rectangle in 26.x
   public static void writeRegion(com.mojang.renderpearl.api.textures.GpuTexture dst, com.mojang.blaze3d.platform.NativeImage src, int mip, int layer, int dstX, int dstY, int w, int h, int srcX, int srcY) {
      com.mojang.renderpearl.api.commands.CommandEncoder enc = com.mojang.blaze3d.systems.RenderSystem.getDevice().createCommandEncoder();
      if (srcX == 0 && srcY == 0 && w == src.getWidth() && h == src.getHeight()) {
         enc.writeToTexture(dst, src, mip, layer, dstX, dstY);
         return;
      }

      try (com.mojang.blaze3d.platform.NativeImage sub = new com.mojang.blaze3d.platform.NativeImage(src.format(), w, h, false)) {
         src.copyRect(sub, srcX, srcY, 0, 0, w, h, false, false);
         enc.writeToTexture(dst, sub, mip, layer, dstX, dstY);
      }
   }
}
