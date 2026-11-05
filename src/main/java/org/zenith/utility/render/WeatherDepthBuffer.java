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
      RenderTarget var0 = Minecraft.getInstance().gameRenderer.mainRenderTarget();
      if (var0 != null && var0.hasDepth()) {
         int var1 = var0.width;
         int var2 = var0.height;
         if (var1 > 0 && var2 > 0) {
            if (simpleFramebuffer == null) {
               simpleFramebuffer = BlitUtils.method6448("zenith weather depth", var1, var2, true);
            } else if (simpleFramebuffer.width != var1 || simpleFramebuffer.height != var2) {
               simpleFramebuffer.resize(var1, var2);
            }

            GpuTexture var3 = var0.getDepthTexture();
            GpuTexture var4 = simpleFramebuffer.getDepthTexture();
            if (var3 != null && var4 != null) {
               RenderSystem.getDevice().createCommandEncoder().copyTextureToTexture(var3, var4, 0, 0, 0, 0, 0, var1, var2);
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
