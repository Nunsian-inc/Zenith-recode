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

      GlCommandEncoderAccessor var0 = (GlCommandEncoderAccessor)(Object)GlBridge.encoder();
      var0.zenith$setCurrentProgram(null);
      var0.zenith$setCurrentPipeline(null);
   }

   public static void method5654(int var0, @Nullable RenderTarget var1) {
      method5655(var0, var1, -1.0F, -1.0F, 1.0F, 1.0F);
   }

   public static void method5655(int var0, @Nullable RenderTarget var1, float var2, float var3, float var4, float var5) {
      RenderSystem.assertOnRenderThread();
      if (!GlBridge.isOpenGl()) {
         return;
      }

      RenderTarget var6 = var1 != null ? var1 : Minecraft.getInstance().gameRenderer.mainRenderTarget();
      GpuTextureView var7 = var6.getColorTextureView();
      if (var7 != null) {
         GpuTextureView var8 = var6.hasDepth() ? var6.getDepthTextureView() : null;
         int var10 = GlBridge.fbo(var7, var8);
         GlStateManager._glBindFramebuffer(36160, var10);
         GlStateManager._viewport(0, 0, var6.width, var6.height);
         GlStateManager._glUseProgram(var0);
         method5656(RenderState.method1904());
         method5657();
         method5658();
         method5659();
         GlStateManager._glBindVertexArray(intValue);
         GlStateManager._glBindBuffer(34962, intValue2);
         MemoryStack var11 = MemoryStack.stackPush();

         try {
            FloatBuffer var12 = var11.floats(new float[]{var2, var3, 0.0F, var4, var3, 0.0F, var2, var5, 0.0F, var4, var5, 0.0F});
            GL15.glBufferSubData(34962, 0L, var12);
         } catch (Throwable var15) {
            if (var11 != null) {
               try {
                  var11.close();
               } catch (Throwable var14) {
                  var15.addSuppressed(var14);
               }
            }

            throw var15;
         }

         if (var11 != null) {
            var11.close();
         }

         GL11.glDrawArrays(5, 0, 4);
         GlStateManager._glBindBuffer(34962, 0);
         GlStateManager._glBindVertexArray(0);
         GlStateManager._glBindFramebuffer(36160, 0);
         method5653();
      }
   }

   private static void method5656(RenderState.Data2 var0) {
      if (var0.isFlag()) {
         GlStateManager._enableBlend(0);
         GlStateManager._blendFuncSeparate(
            GlConst.toGl(var0.getSourceFactor()),
            GlConst.toGl(var0.getDestFactor()),
            GlConst.toGl(var0.getSourceFactor2()),
            GlConst.toGl(var0.getDestFactor2())
         );
      } else {
         GlStateManager._disableBlend(0);
      }

      if (var0.isFlag2()) {
         GlStateManager._enableCull();
      } else {
         GlStateManager._disableCull();
      }

      if (var0.getDepthTestFunction() != CompareOp.ALWAYS_PASS) {
         GlStateManager._enableDepthTest();
         GlStateManager._depthFunc(GlConst.toGl(var0.getDepthTestFunction()));
      } else {
         GlStateManager._disableDepthTest();
      }

      GlStateManager._depthMask(var0.isFlag3());
      boolean var1 = RenderState.isFlag5();
      GlStateManager._colorMask((var1 ? 7 : 0) | (RenderState.isFlag6() ? 8 : 0));
      GlStateManager._disablePolygonOffset();
      GlStateManager._disableColorLogicOp();
   }

   private static void method5657() {
      ScissorState var0 = RenderSystem.getScissorStateForRenderTypeDraws();
      if (var0.enabled()) {
         GlStateManager._enableScissorTest();
         GlStateManager._scissorBox(var0.x(), var0.y(), var0.width(), var0.height());
      } else {
         GlStateManager._disableScissorTest();
      }
   }

   private static void method5658() {
      for (int var0 = 0; var0 < 8; var0++) {
         GpuTextureView var1 = RenderState.method1894(var0);
         if (var1 != null) {
            GlStateManager._activeTexture(33984 + var0);
            GlStateManager._bindTexture(((GlTexture)var1.texture()).glId());
            GlSampler var2 = (GlSampler)RenderState.method1896(var0);
            GL33.glBindSampler(var0, var2.getId());
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
