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

   public static TextureTarget method6448(String label, int width, int height, boolean useDepth) {
      TextureTarget textureTarget = new TextureTarget(label, Math.max(1, width), Math.max(1, height), com.mojang.renderpearl.api.GpuFormat.RGBA8_UNORM, org.zenith.utility.render.RenderCompat.depthFormat(useDepth));
      method6456(textureTarget, 0);
      return textureTarget;
   }

   public static void method6449(@Nullable RenderTarget renderTarget) {
      if (renderTarget != null) {
         renderTarget.destroyBuffers();
      }
   }

   @Nullable
   public static GpuTextureView method6450(@Nullable RenderTarget simpleFramebuffer) {
      return simpleFramebuffer == null ? null : simpleFramebuffer.getColorTextureView();
   }

   @Nullable
   public static GpuTextureView method6451(@Nullable RenderTarget renderTarget) {
      return renderTarget != null && renderTarget.hasDepth() ? renderTarget.getDepthTextureView() : null;
   }

   public static RenderTarget method6452() {
      return Minecraft.getInstance().gameRenderer.mainRenderTarget();
   }

   public static void method6453(@Nullable RenderTarget mainRenderTarget, @Nullable RenderTarget simpleFramebuffer) {
      if (mainRenderTarget != null && simpleFramebuffer != null) {
         GpuTexture colorTexture = mainRenderTarget.getColorTexture();
         GpuTexture destination = simpleFramebuffer.getColorTexture();
         if (colorTexture != null && destination != null) {
            if ((mainRenderTarget.width != simpleFramebuffer.width || mainRenderTarget.height != simpleFramebuffer.height) && method6454()) {
               method6455(mainRenderTarget.getColorTextureView(), simpleFramebuffer);
            } else {
               RenderSystem.getDevice()
                  .createCommandEncoder()
                  .copyTextureToTexture(
                     colorTexture, destination, 0, 0, 0, 0, 0, Math.min(mainRenderTarget.width, simpleFramebuffer.width), Math.min(mainRenderTarget.height, simpleFramebuffer.height)
                  );
            }
         }
      }
   }

   public static boolean method6454() {
      Minecraft minecraft = Minecraft.getInstance();
      return minecraft != null && minecraft.getShaderManager() != null
         ? LightUtil.shaderSource(Identifier.withDefaultNamespace("core/screenquad"), ShaderType.VERTEX) != null
            && LightUtil.shaderSource(Identifier.withDefaultNamespace("core/blit_screen"), ShaderType.FRAGMENT) != null
         : false;
   }

   public static void method6455(@Nullable GpuTextureView colorTextureView, @Nullable RenderTarget backgroundFbo) {
      if (colorTextureView != null && backgroundFbo != null && backgroundFbo.getColorTextureView() != null && method6454()) {
         try (RenderPass renderPass = RenderSystem.getDevice()
               .createCommandEncoder()
               .createRenderPass(() -> "Zenith blit", backgroundFbo.getColorTextureView(), java.util.Optional.empty())) {
            renderPass.setPipeline(com.mojang.blaze3d.systems.RenderSystem.getCompiledPipeline(RenderPipelines.TRACY_BLIT));
            RenderSystem.bindDefaultUniforms(renderPass);
            renderPass.setUniform("InSampler", colorTextureView, RenderSystem.getSamplerCache().getClampToEdge(FilterMode.LINEAR));
            renderPass.draw(3, 1, 0, 0);
         }
      }
   }

   public static void method6456(@Nullable RenderTarget simpleFramebuffer, int colorFromFloat) {
      if (simpleFramebuffer != null && simpleFramebuffer.getColorTexture() != null) {
         if (simpleFramebuffer.hasDepth() && simpleFramebuffer.getDepthTexture() != null) {
            RenderSystem.getDevice().createCommandEncoder().clearColorAndDepthTextures(simpleFramebuffer.getColorTexture(), org.zenith.utility.render.RenderCompat.argb(colorFromFloat), simpleFramebuffer.getDepthTexture(), org.zenith.utility.render.RenderCompat.CLEAR_DEPTH);
         } else {
            RenderSystem.getDevice().createCommandEncoder().clearColorTexture(simpleFramebuffer.getColorTexture(), org.zenith.utility.render.RenderCompat.argb(colorFromFloat));
         }
      }
   }

   public static void method6457(@Nullable RenderTarget backgroundFbo, int width, int height) {
      if (backgroundFbo != null) {
         backgroundFbo.resize(Math.max(1, width), Math.max(1, height));
         method6456(backgroundFbo, 0);
      }
   }
}
