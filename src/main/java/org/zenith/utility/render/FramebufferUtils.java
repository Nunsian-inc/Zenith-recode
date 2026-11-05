package org.zenith.utility.render;

import com.mojang.renderpearl.backend.opengl.GlConst;
import com.mojang.renderpearl.backend.opengl.GlSampler;
import com.mojang.renderpearl.backend.opengl.GlStateManager;
import com.mojang.renderpearl.backend.opengl.GlTexture;
import com.mojang.renderpearl.backend.opengl.GlTextureView;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.renderpearl.api.pipeline.CompareOp;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.ScissorState;
import com.mojang.renderpearl.api.textures.GpuTexture;
import com.mojang.renderpearl.api.textures.GpuTextureView;
import java.nio.FloatBuffer;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryStack;
import org.zenith.RenderState;
import org.zenith.utility.mixin.accessors.GlCommandEncoderAccessor;

public final class FramebufferUtils {
   private static final int CONST_intValue = 8;
   private static int intValue;
   private static int intValue2;

   private FramebufferUtils() {
   }

   public static void method5653() {
      if (!GlBridge.isOpenGl()) {
         return;
      }

      GlCommandEncoderAccessor encoder = (GlCommandEncoderAccessor)(Object)GlBridge.encoder();
      encoder.zenith$setCurrentProgram(null);
      encoder.zenith$setCurrentPipeline(null);
   }

   public static void method5654(int current, @Nullable RenderTarget renderTarget) {
      method5655(current, renderTarget, -1.0F, -1.0F, 1.0F, 1.0F);
   }

   public static void method5655(int current, @Nullable RenderTarget renderTarget, float f, float f2, float f3, float f4) {
      RenderSystem.assertOnRenderThread();
      if (!GlBridge.isOpenGl()) {
         return;
      }

      RenderTarget mainRenderTarget = renderTarget != null ? renderTarget : Minecraft.getInstance().gameRenderer.mainRenderTarget();
      GpuTextureView colorTextureView = mainRenderTarget.getColorTextureView();
      if (colorTextureView != null) {
         GpuTextureView depthTextureView = mainRenderTarget.hasDepth() ? mainRenderTarget.getDepthTextureView() : null;
         int fbo = GlBridge.fbo(colorTextureView, depthTextureView);
         GlStateManager._glBindFramebuffer(36160, fbo);
         GlStateManager._viewport(0, 0, mainRenderTarget.width, mainRenderTarget.height);
         GlStateManager._glUseProgram(current);
         method5656(RenderState.method1904());
         method5657();
         method5658();
         method5659();
         GlStateManager._glBindVertexArray(intValue);
         GlStateManager._glBindBuffer(34962, intValue2);
         MemoryStack stackPush = MemoryStack.stackPush();

         try {
            FloatBuffer floats = stackPush.floats(new float[]{f, f2, 0.0F, f3, f2, 0.0F, f, f4, 0.0F, f3, f4, 0.0F});
            GL15.glBufferSubData(34962, 0L, floats);
         } catch (Throwable e) {
            if (stackPush != null) {
               try {
                  stackPush.close();
               } catch (Throwable ex) {
                  e.addSuppressed(ex);
               }
            }

            throw e;
         }

         if (stackPush != null) {
            stackPush.close();
         }

         GL11.glDrawArrays(5, 0, 4);
         GlStateManager._glBindBuffer(34962, 0);
         GlStateManager._glBindVertexArray(0);
         GlStateManager._glBindFramebuffer(36160, 0);
         method5653();
      }
   }

   private static void method5656(RenderState.Data2 data2) {
      if (data2.isFlag()) {
         GlStateManager._enableBlend(0);
         GlStateManager._blendFuncSeparate(
            GlConst.toGl(data2.getSourceFactor()),
            GlConst.toGl(data2.getDestFactor()),
            GlConst.toGl(data2.getSourceFactor2()),
            GlConst.toGl(data2.getDestFactor2())
         );
      } else {
         GlStateManager._disableBlend(0);
      }

      if (data2.isFlag2()) {
         GlStateManager._enableCull();
      } else {
         GlStateManager._disableCull();
      }

      if (data2.getDepthTestFunction() != CompareOp.ALWAYS_PASS) {
         GlStateManager._enableDepthTest();
         GlStateManager._depthFunc(GlConst.toGl(data2.getDepthTestFunction()));
      } else {
         GlStateManager._disableDepthTest();
      }

      GlStateManager._depthMask(data2.isFlag3());
      boolean flag = RenderState.isFlag5();
      GlStateManager._colorMask((flag ? 7 : 0) | (RenderState.isFlag6() ? 8 : 0));
      GlStateManager._disablePolygonOffset();
      GlStateManager._disableColorLogicOp();
   }

   private static void method5657() {
      ScissorState scissorStateForRenderTypeDraws = RenderSystem.getScissorStateForRenderTypeDraws();
      if (scissorStateForRenderTypeDraws.enabled()) {
         GlStateManager._enableScissorTest();
         GlStateManager._scissorBox(scissorStateForRenderTypeDraws.x(), scissorStateForRenderTypeDraws.y(), scissorStateForRenderTypeDraws.width(), scissorStateForRenderTypeDraws.height());
      } else {
         GlStateManager._disableScissorTest();
      }
   }

   private static void method5658() {
      for (int i = 0; i < 8; i++) {
         GpuTextureView gpuTextureView = RenderState.method1894(i);
         if (gpuTextureView != null) {
            GlStateManager._activeTexture(33984 + i);
            GlStateManager._bindTexture(((GlTexture)gpuTextureView.texture()).glId());
            GlSampler glSampler = (GlSampler)RenderState.method1896(i);
            GL33.glBindSampler(i, glSampler.getId());
         }
      }

      GlStateManager._activeTexture(33984);
   }

   private static void method5659() {
      if (intValue == 0) {
         intValue = GlStateManager._glGenVertexArrays();
         intValue2 = GlStateManager._glGenBuffers();
         GlStateManager._glBindVertexArray(intValue);
         GlStateManager._glBindBuffer(34962, intValue2);
         GL15.glBufferData(34962, 48L, 35048);
         GL20.glEnableVertexAttribArray(0);
         GL20.glVertexAttribPointer(0, 3, 5126, false, 12, 0L);
         GlStateManager._glBindVertexArray(0);
         GlStateManager._glBindBuffer(34962, 0);
      }
   }
}
