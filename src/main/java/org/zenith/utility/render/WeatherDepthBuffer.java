package org.zenith.utility.render;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.api.textures.GpuTexture;
import com.mojang.renderpearl.api.textures.GpuTextureView;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.Nullable;

public final class WeatherDepthBuffer {
   public static final int CONST_intValue = 8;
   private static TextureTarget simpleFramebuffer;
   private static boolean flag;

   private WeatherDepthBuffer() {
   }

   public static void method5130() {
      flag = false;
      RenderTarget mainRenderTarget = Minecraft.getInstance().gameRenderer.mainRenderTarget();
      if (mainRenderTarget != null && mainRenderTarget.hasDepth()) {
         int width = mainRenderTarget.width;
         int height = mainRenderTarget.height;
         if (width > 0 && height > 0) {
            if (simpleFramebuffer == null) {
               simpleFramebuffer = BlitUtils.method6448("zenith weather depth", width, height, true);
            } else if (simpleFramebuffer.width != width || simpleFramebuffer.height != height) {
               simpleFramebuffer.resize(width, height);
            }

            GpuTexture depthTexture = mainRenderTarget.getDepthTexture();
            GpuTexture destination = simpleFramebuffer.getDepthTexture();
            if (depthTexture != null && destination != null) {
               RenderSystem.getDevice().createCommandEncoder().copyTextureToTexture(depthTexture, destination, 0, 0, 0, 0, 0, width, height);
               flag = true;
            }
         }
      }
   }

   public static void method5131() {
   }

   @Nullable
   public static GpuTextureView method5132() {
      return flag && simpleFramebuffer != null ? simpleFramebuffer.getDepthTextureView() : null;
   }

   public static boolean method5133() {
      return flag && simpleFramebuffer != null;
   }
}
