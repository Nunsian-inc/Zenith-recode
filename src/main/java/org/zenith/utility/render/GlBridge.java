package org.zenith.utility.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.api.textures.GpuTextureView;
import com.mojang.renderpearl.backend.opengl.GlCommandEncoder;
import com.mojang.renderpearl.backend.opengl.GlDevice;
import com.mojang.renderpearl.backend.opengl.GlTextureView;
import com.mojang.renderpearl.frontend.FrontendCommandEncoder;
import com.mojang.renderpearl.frontend.FrontendGpuDevice;
import java.util.Collections;
import org.jetbrains.annotations.Nullable;

// Zenith's raw-OpenGL effects (ShaderProgram/FramebufferUtils: ESP glow, hand shaders, weather preview)
// predate renderpearl. Since 26.3 RenderSystem hands out frontend wrappers; this reaches the OpenGL backend
// behind them (classes widened in zenith.accesswidener).
// ponytail: only works with the OpenGL backend; under Vulkan these effects are skipped (isOpenGl() == false).
public final class GlBridge {
   private GlBridge() {
   }

   public static boolean isOpenGl() {
      return RenderSystem.getDevice() instanceof FrontendGpuDevice var0 && var0.backend instanceof GlDevice;
   }

   public static GlDevice device() {
      return (GlDevice)((FrontendGpuDevice)RenderSystem.getDevice()).backend;
   }

   public static GlCommandEncoder encoder() {
      return (GlCommandEncoder)((FrontendCommandEncoder)RenderSystem.getDevice().createCommandEncoder()).backend();
   }

   public static int fbo(GpuTextureView var0, @Nullable GpuTextureView var1) {
      GlDevice var2 = device();
      return var2.frameBufferCache().getFbo(var2.directStateAccess(), Collections.singletonList((GlTextureView)var0), (GlTextureView)var1);
   }
}
