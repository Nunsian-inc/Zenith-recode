package org.zenith.utility.render;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.renderpearl.api.pipeline.ShaderType;
import com.mojang.renderpearl.api.commands.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.api.textures.FilterMode;
import com.mojang.renderpearl.api.textures.GpuTexture;
import com.mojang.renderpearl.api.textures.GpuTextureView;
import java.util.OptionalInt;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

public final class BlitUtils {
   private BlitUtils() {
   }

   public static TextureTarget method6448(String var0, int var1, int var2, boolean var3) {
      TextureTarget var4 = new TextureTarget(var0, Math.max(1, var1), Math.max(1, var2), com.mojang.renderpearl.api.GpuFormat.RGBA8_UNORM, org.zenith.utility.render.RenderCompat.depthFormat(var3));
      method6456(var4, 0);
      return var4;
   }

   public static void method6449(@Nullable RenderTarget var0) {
      if (var0 != null) {
         var0.destroyBuffers();
      }
   }

   @Nullable
   public static GpuTextureView method6450(@Nullable RenderTarget var0) {
      return var0 == null ? null : var0.getColorTextureView();
   }

   @Nullable
   public static GpuTextureView method6451(@Nullable RenderTarget var0) {
      return var0 != null && var0.hasDepth() ? var0.getDepthTextureView() : null;
   }

   public static RenderTarget method6452() {
      return Minecraft.getInstance().gameRenderer.mainRenderTarget();
   }

   public static void method6453(@Nullable RenderTarget var0, @Nullable RenderTarget var1) {
      if (var0 != null && var1 != null) {
         GpuTexture var2 = var0.getColorTexture();
         GpuTexture var3 = var1.getColorTexture();
         if (var2 != null && var3 != null) {
            if ((var0.width != var1.width || var0.height != var1.height) && method6454()) {
               method6455(var0.getColorTextureView(), var1);
            } else {
               RenderSystem.getDevice()
                  .createCommandEncoder()
                  .copyTextureToTexture(
                     var2, var3, 0, 0, 0, 0, 0, Math.min(var0.width, var1.width), Math.min(var0.height, var1.height)
                  );
            }
         }
      }
   }

   public static boolean method6454() {
      Minecraft var0 = Minecraft.getInstance();
      return var0 != null && var0.getShaderManager() != null
         ? LightUtil.shaderSource(Identifier.withDefaultNamespace("core/screenquad"), ShaderType.VERTEX) != null
            && LightUtil.shaderSource(Identifier.withDefaultNamespace("core/blit_screen"), ShaderType.FRAGMENT) != null
         : false;
   }

   public static void method6455(@Nullable GpuTextureView var0, @Nullable RenderTarget var1) {
      if (var0 != null && var1 != null && var1.getColorTextureView() != null && method6454()) {
         try (RenderPass var2 = RenderSystem.getDevice()
               .createCommandEncoder()
               .createRenderPass(() -> "Zenith blit", var1.getColorTextureView(), java.util.Optional.empty())) {
            var2.setPipeline(com.mojang.blaze3d.systems.RenderSystem.getCompiledPipeline(RenderPipelines.TRACY_BLIT));
            RenderSystem.bindDefaultUniforms(var2);
            var2.setUniform("InSampler", var0, RenderSystem.getSamplerCache().getClampToEdge(FilterMode.LINEAR));
            var2.draw(3, 1, 0, 0);
         }
      }
   }

   public static void method6456(@Nullable RenderTarget var0, int var1) {
      if (var0 != null && var0.getColorTexture() != null) {
         if (var0.hasDepth() && var0.getDepthTexture() != null) {
            RenderSystem.getDevice().createCommandEncoder().clearColorAndDepthTextures(var0.getColorTexture(), org.zenith.utility.render.RenderCompat.argb(var1), var0.getDepthTexture(), org.zenith.utility.render.RenderCompat.CLEAR_DEPTH);
         } else {
            RenderSystem.getDevice().createCommandEncoder().clearColorTexture(var0.getColorTexture(), org.zenith.utility.render.RenderCompat.argb(var1));
         }
      }
   }

   public static void method6457(@Nullable RenderTarget var0, int var1, int var2) {
      if (var0 != null) {
         var0.resize(Math.max(1, var1), Math.max(1, var2));
         method6456(var0, 0);
      }
   }
}
