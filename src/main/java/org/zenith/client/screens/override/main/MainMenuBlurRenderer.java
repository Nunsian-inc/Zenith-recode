package org.zenith.client.screens.override.main;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.renderpearl.api.pipeline.ShaderType;
import com.mojang.renderpearl.api.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import org.zenith.utility.render.Tesselator;
import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;
import net.minecraft.client.renderer.ShaderManager;
import org.joml.Matrix4f;
import org.zenith.MatrixUtils;
import org.zenith.MinecraftAccess;
import org.zenith.RenderState;
import org.zenith.ZenithColor;
import org.zenith.base.font.MsdfRenderer;
import org.zenith.base.font.ResourceProvider;
import org.zenith.utility.misc.KawaseShaderKey;
import org.zenith.utility.render.BlitUtils;
import org.zenith.utility.render.ImmediateRenderer;
import org.zenith.utility.render.ShapeRenderer;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public final class MainMenuBlurRenderer implements MinecraftAccess {
   private static final int DOWNSAMPLE_FACTOR = 2;
   private static final float[] KAWASE_OFFSETS = new float[]{0.5F, 1.0F, 1.5F, 2.0F, 2.5F, 3.0F};
   private TextureTarget backgroundFbo;
   private TextureTarget tempFbo;
   private KawaseShaderKey kawaseShaderKey;
   private KawaseShaderKey maskShaderKey;

   public void capture(MenuDrawContext graphics, float px) {
      if (mc.getShaderManager() != null && !(px <= 0.0F)) {
         MsdfRenderer.flushBatch();
         RenderTarget mainRenderTarget = mc.gameRenderer.mainRenderTarget();
         this.ensureFramebuffers(mainRenderTarget);
         this.blurCurrentFramebuffer(mainRenderTarget, MatrixUtils.method664(graphics.pose()), px);
      }
   }

   public GpuTextureView texture() {
      return this.backgroundFbo == null ? null : this.backgroundFbo.getColorTextureView();
   }

   public void render(Matrix4f pose, float floatValue, float y, float width, float height, ProxyDotRadiusData headerRadius, ZenithColor zenithColor) {
      if (this.backgroundFbo != null && mc.getShaderManager() != null) {
         if (this.maskShaderKey == null) {
            this.maskShaderKey = new KawaseShaderKey(ResourceProvider.getShaderIdentifier("wtf/data"), DefaultVertexFormat.POSITION_COLOR);
         }

         if (this.isShaderReady(this.maskShaderKey)) {
            MsdfRenderer.flushBatch();
            RenderState.method1871();
            RenderState.method1873();
            RenderState.method1877();

            try {
               RenderState.method1889(0, this.backgroundFbo.getColorTextureView());
               KawaseShaderKey kawaseShaderKey = this.maskShaderKey.method5165();
               if (kawaseShaderKey != null) {
                  kawaseShaderKey.method5166("Size").method7413(width, height);
                  kawaseShaderKey.method5166("Radius").method7415(headerRadius.getFloatValue(), headerRadius.getFloatValue4(), headerRadius.getFloatValue2(), headerRadius.getFloatValue3());
                  kawaseShaderKey.method5166("Smoothness").method7412(0.01F);
                  BufferBuilder begin = Tesselator.getInstance().begin(PrimitiveTopology.QUADS, DefaultVertexFormat.POSITION_COLOR);
                  begin.addVertex(pose, floatValue, y, 0.0F).setColor(zenithColor.method7287());
                  begin.addVertex(pose, floatValue, y + height, 0.0F).setColor(zenithColor.method7287());
                  begin.addVertex(pose, floatValue + width, y + height, 0.0F).setColor(zenithColor.method7287());
                  begin.addVertex(pose, floatValue + width, y, 0.0F).setColor(zenithColor.method7287());
                  ImmediateRenderer.method8015(begin.buildOrThrow());
                  return;
               }
            } finally {
               RenderState.method1893(0);
               RenderState.method1876();
               RenderState.method1872();
            }
         }
      }
   }

   private void blurCurrentFramebuffer(RenderTarget mainRenderTarget, Matrix4f pose, float px) {
      if (this.kawaseShaderKey == null) {
         this.kawaseShaderKey = new KawaseShaderKey(ResourceProvider.getShaderIdentifier("kawase_blur/data"), DefaultVertexFormat.POSITION_COLOR);
      }

      if (this.isShaderReady(this.kawaseShaderKey)) {
         float width = 1.0F / this.backgroundFbo.width;
         float height = 1.0F / this.backgroundFbo.height;
         RenderState.Data2 data2 = RenderState.method1904();
         RenderState.method1872();
         RenderState.method1877();

         try {
            this.downsample(mainRenderTarget, this.backgroundFbo);
            TextureTarget backgroundFbo = this.backgroundFbo;
            TextureTarget tempFbo = this.tempFbo;

            for (float f : KAWASE_OFFSETS) {
               float batchRange = f * px / 10.0F;
               if (!this.kawasePass(BlitUtils.method6450(backgroundFbo), tempFbo, pose, width, height, batchRange)) {
                  return;
               }

               TextureTarget textureTarget = backgroundFbo;
               backgroundFbo = tempFbo;
               tempFbo = textureTarget;
            }
         } finally {
            RenderState.method1893(0);
            RenderState.method1905(data2);
         }
      }
   }

   private void downsample(RenderTarget mainRenderTarget, RenderTarget backgroundFbo) {
      BlitUtils.method6455(BlitUtils.method6450(mainRenderTarget), backgroundFbo);
   }

   private boolean kawasePass(GpuTextureView colorTextureView, RenderTarget tempFbo, Matrix4f pose, float width, float height, float batchRange) {
      ImmediateRenderer.method8013(tempFbo);
      RenderState.method1889(0, colorTextureView);
      KawaseShaderKey kawaseShaderKey = this.kawaseShaderKey.method5165();
      if (kawaseShaderKey == null) {
         ImmediateRenderer.method8013(null);
         RenderState.method1893(0);
         return false;
      } else {
         kawaseShaderKey.method5166("Resolution").method7413(width, height);
         kawaseShaderKey.method5166("Offset").method7412(batchRange);
         BufferBuilder begin = Tesselator.getInstance().begin(PrimitiveTopology.QUADS, DefaultVertexFormat.POSITION_COLOR);
         ShapeRenderer.method7161(pose, begin, 0.0F, 0.0F, tempFbo.width, tempFbo.height, -1);
         ImmediateRenderer.method8010(tempFbo.width, tempFbo.height);
         ImmediateRenderer.method8015(begin.buildOrThrow());
         RenderState.method1893(0);
         return true;
      }
   }

   private void ensureFramebuffers(RenderTarget mainRenderTarget) {
      int width = Math.max(1, mainRenderTarget.width / 2);
      int height = Math.max(1, mainRenderTarget.height / 2);
      this.backgroundFbo = this.ensureFramebuffer(this.backgroundFbo, width, height);
      this.tempFbo = this.ensureFramebuffer(this.tempFbo, width, height);
   }

   private TextureTarget ensureFramebuffer(TextureTarget backgroundFbo, int width, int height) {
      if (backgroundFbo == null) {
         backgroundFbo = BlitUtils.method6448("zenith menu blur", width, height, false);
      } else if (backgroundFbo.width != width || backgroundFbo.height != height) {
         BlitUtils.method6457(backgroundFbo, width, height);
      }

      return backgroundFbo;
   }

   private boolean isShaderReady(KawaseShaderKey maskShaderKey) {
      ShaderManager shaderManager = mc.getShaderManager();
      return shaderManager == null ? false : org.zenith.utility.render.LightUtil.shaderSource(maskShaderKey.getIdentifier(), ShaderType.FRAGMENT) != null;
   }

   public void close() {
      if (this.backgroundFbo != null) {
         this.backgroundFbo.destroyBuffers();
         this.backgroundFbo = null;
      }

      if (this.tempFbo != null) {
         this.tempFbo.destroyBuffers();
         this.tempFbo = null;
      }
   }
}
