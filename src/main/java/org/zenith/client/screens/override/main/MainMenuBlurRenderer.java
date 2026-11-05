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

   public void capture(MenuDrawContext var1, float var2) {
      if (mc.getShaderManager() != null && !(var2 <= 0.0F)) {
         MsdfRenderer.flushBatch();
         RenderTarget var3 = mc.gameRenderer.mainRenderTarget();
         this.ensureFramebuffers(var3);
         this.blurCurrentFramebuffer(var3, MatrixUtils.method664(var1.pose()), var2);
      }
   }

   public GpuTextureView texture() {
      return this.backgroundFbo == null ? null : this.backgroundFbo.getColorTextureView();
   }

   public void render(Matrix4f var1, float var2, float var3, float var4, float var5, ProxyDotRadiusData var6, ZenithColor var7) {
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
               KawaseShaderKey var8 = this.maskShaderKey.method5165();
               if (var8 != null) {
                  var8.method5166("Size").method7413(var4, var5);
                  var8.method5166("Radius").method7415(var6.getFloatValue(), var6.getFloatValue4(), var6.getFloatValue2(), var6.getFloatValue3());
                  var8.method5166("Smoothness").method7412(0.01F);
                  BufferBuilder var9 = Tesselator.getInstance().begin(PrimitiveTopology.QUADS, DefaultVertexFormat.POSITION_COLOR);
                  var9.addVertex(var1, var2, var3, 0.0F).setColor(var7.method7287());
                  var9.addVertex(var1, var2, var3 + var5, 0.0F).setColor(var7.method7287());
                  var9.addVertex(var1, var2 + var4, var3 + var5, 0.0F).setColor(var7.method7287());
                  var9.addVertex(var1, var2 + var4, var3, 0.0F).setColor(var7.method7287());
                  ImmediateRenderer.method8015(var9.buildOrThrow());
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

   private void blurCurrentFramebuffer(RenderTarget var1, Matrix4f var2, float var3) {
      if (this.kawaseShaderKey == null) {
         this.kawaseShaderKey = new KawaseShaderKey(ResourceProvider.getShaderIdentifier("kawase_blur/data"), DefaultVertexFormat.POSITION_COLOR);
      }

      if (this.isShaderReady(this.kawaseShaderKey)) {
         float var4 = 1.0F / this.backgroundFbo.width;
         float var5 = 1.0F / this.backgroundFbo.height;
         RenderState.Data2 var6 = RenderState.method1904();
         RenderState.method1872();
         RenderState.method1877();

         try {
            this.downsample(var1, this.backgroundFbo);
            TextureTarget var7 = this.backgroundFbo;
            TextureTarget var8 = this.tempFbo;

            for (float var12 : KAWASE_OFFSETS) {
               float var13 = var12 * var3 / 10.0F;
               if (!this.kawasePass(BlitUtils.method6450(var7), var8, var2, var4, var5, var13)) {
                  return;
               }

               TextureTarget var14 = var7;
               var7 = var8;
               var8 = var14;
            }
         } finally {
            RenderState.method1893(0);
            RenderState.method1905(var6);
         }
      }
   }

   private void downsample(RenderTarget var1, RenderTarget var2) {
      BlitUtils.method6455(BlitUtils.method6450(var1), var2);
   }

   private boolean kawasePass(GpuTextureView var1, RenderTarget var2, Matrix4f var3, float var4, float var5, float var6) {
      ImmediateRenderer.method8013(var2);
      RenderState.method1889(0, var1);
      KawaseShaderKey var7 = this.kawaseShaderKey.method5165();
      if (var7 == null) {
         ImmediateRenderer.method8013(null);
         RenderState.method1893(0);
         return false;
      } else {
         var7.method5166("Resolution").method7413(var4, var5);
         var7.method5166("Offset").method7412(var6);
         BufferBuilder var8 = Tesselator.getInstance().begin(PrimitiveTopology.QUADS, DefaultVertexFormat.POSITION_COLOR);
         ShapeRenderer.method7161(var3, var8, 0.0F, 0.0F, var2.width, var2.height, -1);
         ImmediateRenderer.method8010(var2.width, var2.height);
         ImmediateRenderer.method8015(var8.buildOrThrow());
         RenderState.method1893(0);
         return true;
      }
   }

   private void ensureFramebuffers(RenderTarget var1) {
      int var2 = Math.max(1, var1.width / 2);
      int var3 = Math.max(1, var1.height / 2);
      this.backgroundFbo = this.ensureFramebuffer(this.backgroundFbo, var2, var3);
      this.tempFbo = this.ensureFramebuffer(this.tempFbo, var2, var3);
   }

   private TextureTarget ensureFramebuffer(TextureTarget var1, int var2, int var3) {
      if (var1 == null) {
         var1 = BlitUtils.method6448("zenith menu blur", var2, var3, false);
      } else if (var1.width != var2 || var1.height != var3) {
         BlitUtils.method6457(var1, var2, var3);
      }

      return var1;
   }

   private boolean isShaderReady(KawaseShaderKey var1) {
      ShaderManager var2 = mc.getShaderManager();
      return var2 == null ? false : org.zenith.utility.render.LightUtil.shaderSource(var1.getIdentifier(), ShaderType.FRAGMENT) != null;
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
