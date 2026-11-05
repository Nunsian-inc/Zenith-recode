package org.zenith.utility.render;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.renderpearl.api.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.BufferBuilder;
import org.zenith.utility.render.Tesselator;
import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;
import java.util.List;
import lombok.Generated;
import org.joml.Matrix4f;
import org.zenith.MatrixUtils;
import org.zenith.MinecraftAccess;
import org.zenith.RenderState;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.MsdfRenderer;
import org.zenith.base.font.ResourceProvider;
import org.zenith.client.events.ScreenRenderEvent;
import org.zenith.client.modules.impl.render.InterfaceModule;
import org.zenith.client.modules.impl.render.MenuModule;
import org.zenith.utility.misc.KawaseShaderKey;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class KawaseBlur implements MinecraftAccess {
   private static final int CONST_intValue = 2;
   private static final float[] CONST_floatValues = new float[]{0.5F, 1.0F, 1.5F, 2.0F, 2.5F, 3.0F};
   private TextureTarget fbo;
   private TextureTarget simpleFramebuffer;
   private TextureTarget tempFbo;
   private KawaseShaderKey kawaseShaderKey;
   private KawaseShaderKey kawaseShaderKey2;
   private KawaseShaderKey kawaseShaderKey3;
   private KawaseShaderKey kawaseShaderKey4;
   private int intValue = 0;
   private int intValue2 = 0;
   private boolean flag = true;

   public KawaseBlur() {
      EventManager.register(this);
   }

   @EventTarget(0)
   public void method7348(ScreenRenderEvent event) {
      if (mc.getShaderManager() != null && (Zenith.getInstance().getNLMenuScreen().getBlurPower() != 0.0F || InterfaceModule.INSTANCE.method6818())) {
         RenderTarget mainRenderTarget = mc.gameRenderer.mainRenderTarget();
         this.method7361(mainRenderTarget);
         if (this.intValue % 3 == 0) {
            float blurPower = Zenith.getInstance().getNLMenuScreen().getBlurPower();
            if (blurPower == 0.0F) {
               return;
            }

            this.method7352(this.fbo, MatrixUtils.method664(event.getMenuDrawContext().pose()), blurPower);
            this.intValue = 0;
         }
      }
   }

   public void method7349(MenuDrawContext menuDrawContext) {
      if (mc.level == null && this.intValue == 0 && this.fbo != null && mc.getShaderManager() != null) {
         float blurPower = Zenith.getInstance().getNLMenuScreen().getBlurPower();
         if (blurPower != 0.0F) {
            this.method7352(this.fbo, MatrixUtils.method664(menuDrawContext.pose()), blurPower);
         }
      }
   }

   public void method7350(MenuDrawContext customDrawContext) {
      if (mc.getShaderManager() != null && Zenith.getInstance().getNLMenuScreen().getBlurPower() != 0.0F && this.flag) {
         RenderTarget mainRenderTarget = mc.gameRenderer.mainRenderTarget();
         this.method7362(mainRenderTarget);
         if (this.intValue2 % (this.method7351() ? 2 : 10) == 0) {
            this.method7352(this.simpleFramebuffer, MatrixUtils.method664(customDrawContext.pose()), 10.0F);
            this.intValue2 = 0;
         }
      }
   }

   private boolean method7351() {
      return MenuModule.INSTANCE.method5344()
         && (Zenith.getInstance().getNLMenuScreen().isClosing() || Zenith.getInstance().getNLMenuScreen().isElementSwapBlurActive());
   }

   private void method7352(RenderTarget fbo, Matrix4f pose, float blurPower) {
      RenderTarget mainRenderTarget = mc.gameRenderer.mainRenderTarget();
      this.method7363(mainRenderTarget);
      if (this.kawaseShaderKey == null) {
         this.kawaseShaderKey = new KawaseShaderKey(
            ResourceProvider.getShaderIdentifier("kawase_blur/data"), com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_COLOR
         );
      }

      if (this.isShaderReady(this.kawaseShaderKey)) {
         float width = 1.0F / fbo.width;
         float height = 1.0F / fbo.height;
         RenderState.Data2 data2 = RenderState.method1904();
         RenderState.method1872();
         RenderState.method1877();

         try {
            this.downsample(mainRenderTarget, fbo);
            Object simpleFramebuffer = fbo;
            Object tempFbo = this.tempFbo;

            for (float f : CONST_floatValues) {
               float batchRange = f * blurPower / 10.0F;
               if (!this.kawasePass(BlitUtils.method6450((RenderTarget)simpleFramebuffer), (RenderTarget)tempFbo, pose, width, height, batchRange)) {
                  return;
               }

               Object obj = simpleFramebuffer;
               simpleFramebuffer = tempFbo;
               tempFbo = obj;
            }
         } finally {
            RenderState.method1893(0);
            RenderState.method1905(data2);
         }
      }
   }

   private void downsample(RenderTarget mainRenderTarget, RenderTarget fbo) {
      BlitUtils.method6455(BlitUtils.method6450(mainRenderTarget), fbo);
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
         BufferBuilder begin = Tesselator.getInstance().begin(PrimitiveTopology.QUADS, com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_COLOR);
         ShapeRenderer.method7161(pose, begin, 0.0F, 0.0F, tempFbo.width, tempFbo.height, -1);
         ImmediateRenderer.method8010(tempFbo.width, tempFbo.height);
         ImmediateRenderer.method8015(begin.buildOrThrow());
         RenderState.method1893(0);
         return true;
      }
   }

   public void method7353(Matrix4f pose, float floatValue, float y, float width, float height, ProxyDotRadiusData headerRadius, ZenithColor zenithColor) {
      this.method7356(this.fbo, pose, floatValue, y, width, height, headerRadius, zenithColor);
   }

   public void method7354(List<KawaseBlur.Data> datas) {
      MsdfRenderer.flushBatch();
      this.method7357(this.fbo, datas);
   }

   public void method7355(Matrix4f pose, float floatValue, float y, float width, float height, ProxyDotRadiusData headerRadius, ZenithColor zenithColor) {
      this.flag = true;
      RenderTarget mainRenderTarget = mc.gameRenderer.mainRenderTarget();
      this.method7362(mainRenderTarget);
      this.method7356(this.simpleFramebuffer, pose, floatValue, y, width, height, headerRadius, zenithColor);
   }

   private void method7356(RenderTarget fbo, Matrix4f pose, float floatValue, float y, float width, float height, ProxyDotRadiusData headerRadius, ZenithColor zenithColor) {
      if (fbo != null) {
         if (this.kawaseShaderKey2 == null) {
            this.kawaseShaderKey2 = new KawaseShaderKey(
               ResourceProvider.getShaderIdentifier("wtf/data"), com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_COLOR
            );
         }

         if (this.isShaderReady(this.kawaseShaderKey2)) {
            RenderState.method1871();
            RenderState.method1873();
            RenderState.method1877();

            try {
               RenderState.method1889(0, BlitUtils.method6450(fbo));
               KawaseShaderKey kawaseShaderKey = this.kawaseShaderKey2.method5165();
               if (kawaseShaderKey != null) {
                  kawaseShaderKey.method5166("Size").method7413(width, height);
                  kawaseShaderKey.method5166("Radius").method7415(headerRadius.getFloatValue(), headerRadius.getFloatValue4(), headerRadius.getFloatValue2(), headerRadius.getFloatValue3());
                  kawaseShaderKey.method5166("Smoothness").method7412(0.01F);
                  BufferBuilder begin = Tesselator.getInstance().begin(PrimitiveTopology.QUADS, com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_COLOR);
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

   private void method7357(RenderTarget fbo, List<KawaseBlur.Data> datas) {
      if (fbo != null && !datas.isEmpty()) {
         if (this.kawaseShaderKey3 == null) {
            this.kawaseShaderKey3 = new KawaseShaderKey(
               ResourceProvider.getShaderIdentifier("batch_wtf/data"), com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_TEX_COLOR
            );
         }

         if (!this.isShaderReady(this.kawaseShaderKey3)) {
            for (KawaseBlur.Data data : datas) {
               this.method7356(
                  fbo, data.matrix4f, data.floatValue, data.floatValue2, data.floatValue3, data.floatValue4, data.proxyDotRadiusData, data.zenithColor
               );
            }
         } else {
            RenderState.method1871();
            RenderState.method1873();
            RenderState.method1877();
            RenderState.method1889(0, BlitUtils.method6450(fbo));

            try {
               int n = 0;

               while (n < datas.size()) {
                  ProxyDotRadiusData proxyDotRadiusData = ((KawaseBlur.Data)datas.get(n)).proxyDotRadiusData;
                  int n2 = n + 1;

                  while (n2 < datas.size() && this.method7360(proxyDotRadiusData, ((KawaseBlur.Data)datas.get(n2)).proxyDotRadiusData)) {
                     n2++;
                  }

                  this.method7358(datas, n, n2, proxyDotRadiusData);
                  n = n2;
               }
            } finally {
               RenderState.method1893(0);
               RenderState.method1876();
               RenderState.method1872();
            }
         }
      }
   }

   private void method7358(List<KawaseBlur.Data> datas, int n, int n2, ProxyDotRadiusData proxyDotRadiusData) {
      KawaseShaderKey kawaseShaderKey = this.kawaseShaderKey3.method5165();
      if (kawaseShaderKey != null) {
         kawaseShaderKey.method5166("Radius").method7415(proxyDotRadiusData.getFloatValue(), proxyDotRadiusData.getFloatValue4(), proxyDotRadiusData.getFloatValue2(), proxyDotRadiusData.getFloatValue3());
         kawaseShaderKey.method5166("Smoothness").method7412(0.01F);
         BufferBuilder begin = Tesselator.getInstance().begin(PrimitiveTopology.QUADS, com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_TEX_COLOR);

         for (int i = n; i < n2; i++) {
            this.method7359(begin, (KawaseBlur.Data)datas.get(i));
         }

         ImmediateRenderer.method8015(begin.buildOrThrow());
      }
   }

   private void method7359(BufferBuilder begin, KawaseBlur.Data data) {
      int color = data.zenithColor.method7287();
      float x = data.floatValue + data.floatValue3;
      float y = data.floatValue2 + data.floatValue4;
      begin.addVertex(data.matrix4f, data.floatValue, data.floatValue2, 0.0F).setUv(data.floatValue3, data.floatValue4).setColor(color);
      begin.addVertex(data.matrix4f, data.floatValue, y, 0.0F).setUv(data.floatValue3, data.floatValue4).setColor(color);
      begin.addVertex(data.matrix4f, x, y, 0.0F).setUv(data.floatValue3, data.floatValue4).setColor(color);
      begin.addVertex(data.matrix4f, x, data.floatValue2, 0.0F).setUv(data.floatValue3, data.floatValue4).setColor(color);
   }

   private boolean method7360(ProxyDotRadiusData proxyDotRadiusData, ProxyDotRadiusData proxyDotRadiusData2) {
      return proxyDotRadiusData.getFloatValue() == proxyDotRadiusData2.getFloatValue()
         && proxyDotRadiusData.getFloatValue4() == proxyDotRadiusData2.getFloatValue4()
         && proxyDotRadiusData.getFloatValue2() == proxyDotRadiusData2.getFloatValue2()
         && proxyDotRadiusData.getFloatValue3() == proxyDotRadiusData2.getFloatValue3();
   }

   private boolean isShaderReady(KawaseShaderKey kawaseShaderKey) {
      return kawaseShaderKey != null && kawaseShaderKey.text3 == null;
   }

   private void method7361(RenderTarget mainRenderTarget) {
      int width = this.method7366(mainRenderTarget.width);
      int height = this.method7366(mainRenderTarget.height);
      if (this.fbo == null) {
         this.fbo = this.method7364(width, height);
      }

      if (this.fbo.width != width || this.fbo.height != height) {
         this.method7365(this.fbo, width, height);
      }
   }

   private void method7362(RenderTarget mainRenderTarget) {
      int width = this.method7366(mainRenderTarget.width);
      int height = this.method7366(mainRenderTarget.height);
      if (this.simpleFramebuffer == null) {
         this.simpleFramebuffer = this.method7364(width, height);
      }

      if (this.simpleFramebuffer.width != width || this.simpleFramebuffer.height != height) {
         this.method7365(this.simpleFramebuffer, width, height);
      }
   }

   private void method7363(RenderTarget mainRenderTarget) {
      int width = this.method7366(mainRenderTarget.width);
      int height = this.method7366(mainRenderTarget.height);
      if (this.tempFbo == null) {
         this.tempFbo = this.method7364(width, height);
      }

      if (this.tempFbo.width != width || this.tempFbo.height != height) {
         this.method7365(this.tempFbo, width, height);
      }
   }

   private TextureTarget method7364(int width, int height) {
      return BlitUtils.method6448("zenith blur", width, height, false);
   }

   private void method7365(TextureTarget fbo, int width, int height) {
      BlitUtils.method6457(fbo, width, height);
   }

   private int method7366(int width) {
      return Math.max(1, width / 2);
   }

   public void method7367() {
      this.intValue = 3;
      this.intValue2 = 10;
   }

   public void method7368() {
      this.intValue++;
      this.intValue2++;
   }

   public void method7369(List<KawaseBlur.Data2> data2s) {
      if (this.fbo != null && !data2s.isEmpty()) {
         MsdfRenderer.flushBatch();
         if (this.kawaseShaderKey4 == null) {
            this.kawaseShaderKey4 = new KawaseShaderKey(
               ResourceProvider.getShaderIdentifier("batch_glass/data"), com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_TEX_COLOR
            );
         }

         if (!this.isShaderReady(this.kawaseShaderKey4)) {
            for (KawaseBlur.Data2 data2 : data2s) {
               RenderShaders.floatValues = data2.getFloatValues();
               RenderShaders.method5264(
                  data2.getMatrix4f(),
                  data2.getFloatValue(),
                  data2.getFloatValue2(),
                  data2.width(),
                  data2.height(),
                  data2.getFloatValue5(),
                  data2.getFloatValue6(),
                  data2.getProxyDotRadiusData(),
                  data2.getZenithColor()
               );
               RenderShaders.floatValues = null;
            }
         } else {
            RenderState.method1871();
            RenderState.method1873();
            RenderState.method1877();
            RenderState.method1889(0, BlitUtils.method6450(this.fbo));

            try {
               int n = 0;

               while (n < data2s.size()) {
                  KawaseBlur.Data2 data2 = (KawaseBlur.Data2)data2s.get(n);
                  int n2 = n + 1;

                  while (n2 < data2s.size() && this.method7371(data2, (KawaseBlur.Data2)data2s.get(n2))) {
                     n2++;
                  }

                  this.method7372(data2s, n, n2, data2);
                  n = n2;
               }
            } finally {
               RenderState.method1893(0);
               RenderState.method1876();
               RenderState.method1872();
            }
         }
      }
   }

   private void method7370(KawaseShaderKey kawaseShaderKey, KawaseBlur.Data2 data2) {
      float[] floatValues = data2.getFloatValues();
      if (floatValues == null) {
         kawaseShaderKey.method5166("Tab").method7415(0.0F, 0.0F, 0.0F, 0.0F);
         kawaseShaderKey.method5166("TabRadius").method7412(0.0F);
         kawaseShaderKey.method5166("MainHeight").method7412(0.0F);
         kawaseShaderKey.method5166("Joint").method7412(0.0F);
      } else {
         float f = data2.method7373();
         float f2 = data2.method7374();
         float f3 = data2.getFloatValue5() / 2.0F;
         kawaseShaderKey.method5166("Tab").method7415(floatValues[0] - f, floatValues[1] - f2, floatValues[2], floatValues[3]);
         kawaseShaderKey.method5166("TabRadius").method7412(floatValues[4] * f3);
         kawaseShaderKey.method5166("MainHeight").method7412(floatValues[5] - f2);
         kawaseShaderKey.method5166("Joint").method7412(floatValues[6]);
      }
   }

   private boolean method7371(KawaseBlur.Data2 data2, KawaseBlur.Data2 data22) {
      return data2.getFloatValues() == null
         && data22.getFloatValues() == null
         && this.method7360(data2.method7377(), data22.method7377())
         && data2.method7378() == data22.method7378()
         && data2.getFloatValue5() == data22.getFloatValue5()
         && data2.getFloatValue6() == data22.getFloatValue6();
   }

   private void method7372(List<KawaseBlur.Data2> data2s, int n, int n2, KawaseBlur.Data2 data2) {
      KawaseShaderKey kawaseShaderKey = this.kawaseShaderKey4.method5165();
      if (kawaseShaderKey != null) {
         ProxyDotRadiusData proxyDotRadiusData = data2.method7377();
         kawaseShaderKey.method5166("Radius").method7415(proxyDotRadiusData.getFloatValue(), proxyDotRadiusData.getFloatValue4(), proxyDotRadiusData.getFloatValue2(), proxyDotRadiusData.getFloatValue3());
         kawaseShaderKey.method5166("Smoothness").method7412(1.0F);
         kawaseShaderKey.method5166("CornerSmoothness").method7412(data2.getFloatValue5());
         kawaseShaderKey.method5166("FresnelPower").method7412(data2.method7378());
         kawaseShaderKey.method5166("FresnelAlpha").method7412(1.0F);
         kawaseShaderKey.method5166("BaseAlpha").method7412(1.0F);
         kawaseShaderKey.method5166("FresnelInvert").set(1);
         kawaseShaderKey.method5166("FresnelMix").method7412(0.0F);
         kawaseShaderKey.method5166("DistortStrength").method7412(data2.getFloatValue6());
         kawaseShaderKey.method5166("Time").method7412((float)(System.currentTimeMillis() % 1000000L) / 1000.0F);
         ZenithColor zenithColor = InterfaceModule.INSTANCE.method6824();
         kawaseShaderKey.method5166("GlareColor").method7414(zenithColor.getIntValue() / 255.0F, zenithColor.getIntValue2() / 255.0F, zenithColor.getIntValue3() / 255.0F);
         kawaseShaderKey.method5166("GlareAlpha").method7412(zenithColor.getIntValue4() / 255.0F);
         kawaseShaderKey.method5166("GlareSpeed").method7412(InterfaceModule.INSTANCE.method6825());
         kawaseShaderKey.method5166("GlareMode").method7412(InterfaceModule.INSTANCE.method6826());
         kawaseShaderKey.method5166("GlareTexture").method7412(InterfaceModule.INSTANCE.method6827());
         this.method7370(kawaseShaderKey, data2);
         BufferBuilder begin = Tesselator.getInstance().begin(PrimitiveTopology.QUADS, com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_TEX_COLOR);

         for (int i = n; i < n2; i++) {
            KawaseBlur.Data2 data22 = (KawaseBlur.Data2)data2s.get(i);
            int color = data22.getZenithColor().method7287();
            float x = data22.method7373();
            float y = data22.method7374();
            float f = data22.method7375();
            float f2 = data22.method7376();
            float x2 = x + f;
            float y2 = y + f2;
            begin.addVertex(data22.getMatrix4f(), x, y, 0.0F).setUv(f, f2).setColor(color);
            begin.addVertex(data22.getMatrix4f(), x, y2, 0.0F).setUv(f, f2).setColor(color);
            begin.addVertex(data22.getMatrix4f(), x2, y2, 0.0F).setUv(f, f2).setColor(color);
            begin.addVertex(data22.getMatrix4f(), x2, y, 0.0F).setUv(f, f2).setColor(color);
         }

         ImmediateRenderer.method8015(begin.buildOrThrow());
      }
   }

   @Generated
   public TextureTarget getFbo() {
      return this.fbo;
   }

   public static final class Data {
      private final Matrix4f matrix4f;
      private final float floatValue;
      private final float floatValue2;
      private final float floatValue3;
      private final float floatValue4;
      private final ProxyDotRadiusData proxyDotRadiusData;
      private final ZenithColor zenithColor;

      public Matrix4f matrix4f() {
         return this.matrix4f;
      }

      public float floatValue() {
         return this.floatValue;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      public float floatValue3() {
         return this.floatValue3;
      }

      public float floatValue4() {
         return this.floatValue4;
      }

      public ProxyDotRadiusData proxyDotRadiusData() {
         return this.proxyDotRadiusData;
      }

      public ZenithColor zenithColor() {
         return this.zenithColor;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.matrix4f, other.matrix4f) && java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.floatValue3, other.floatValue3) && java.util.Objects.equals(this.floatValue4, other.floatValue4) && java.util.Objects.equals(this.proxyDotRadiusData, other.proxyDotRadiusData) && java.util.Objects.equals(this.zenithColor, other.zenithColor);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(matrix4f, floatValue, floatValue2, floatValue3, floatValue4, proxyDotRadiusData, zenithColor);
      }

      @Override
      public String toString() {
         return "Data[" + "matrix4f=" + this.matrix4f + ", " + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "floatValue3=" + this.floatValue3 + ", " + "floatValue4=" + this.floatValue4 + ", " + "proxyDotRadiusData=" + this.proxyDotRadiusData + ", " + "zenithColor=" + this.zenithColor + "]";
      }

      public Data(Matrix4f matrix4f, float floatValue, float f, float width, float height, ProxyDotRadiusData proxyDotRadiusData, ZenithColor zenithColor) {
         this.matrix4f = matrix4f;
         this.floatValue = floatValue;
         this.floatValue2 = f;
         this.floatValue3 = width;
         this.floatValue4 = height;
         this.proxyDotRadiusData = proxyDotRadiusData;
         this.zenithColor = zenithColor;
      }

      public Matrix4f getMatrix4f() {
         return this.matrix4f;
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public float getFloatValue2() {
         return this.floatValue2;
      }

      public float width() {
         return this.floatValue3;
      }

      public float height() {
         return this.floatValue4;
      }

      public ProxyDotRadiusData getProxyDotRadiusData() {
         return this.proxyDotRadiusData;
      }

      public ZenithColor getZenithColor() {
         return this.zenithColor;
      }
   }

   public static final class Data2 {
      private final Matrix4f matrix4f;
      private final float floatValue;
      private final float floatValue2;
      private final float floatValue3;
      private final float floatValue4;
      private final ProxyDotRadiusData proxyDotRadiusData;
      private final ZenithColor zenithColor;
      private final float floatValue5;
      private final float floatValue6;
      private final float[] floatValues;

      public Matrix4f matrix4f() {
         return this.matrix4f;
      }

      public float floatValue() {
         return this.floatValue;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      public float floatValue3() {
         return this.floatValue3;
      }

      public float floatValue4() {
         return this.floatValue4;
      }

      public ProxyDotRadiusData proxyDotRadiusData() {
         return this.proxyDotRadiusData;
      }

      public ZenithColor zenithColor() {
         return this.zenithColor;
      }

      public float floatValue5() {
         return this.floatValue5;
      }

      public float floatValue6() {
         return this.floatValue6;
      }

      public float[] floatValues() {
         return this.floatValues;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data2 other)) return false;
         return java.util.Objects.equals(this.matrix4f, other.matrix4f) && java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.floatValue3, other.floatValue3) && java.util.Objects.equals(this.floatValue4, other.floatValue4) && java.util.Objects.equals(this.proxyDotRadiusData, other.proxyDotRadiusData) && java.util.Objects.equals(this.zenithColor, other.zenithColor) && java.util.Objects.equals(this.floatValue5, other.floatValue5) && java.util.Objects.equals(this.floatValue6, other.floatValue6) && java.util.Objects.equals(this.floatValues, other.floatValues);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(matrix4f, floatValue, floatValue2, floatValue3, floatValue4, proxyDotRadiusData, zenithColor, floatValue5, floatValue6, floatValues);
      }

      @Override
      public String toString() {
         return "Data2[" + "matrix4f=" + this.matrix4f + ", " + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "floatValue3=" + this.floatValue3 + ", " + "floatValue4=" + this.floatValue4 + ", " + "proxyDotRadiusData=" + this.proxyDotRadiusData + ", " + "zenithColor=" + this.zenithColor + ", " + "floatValue5=" + this.floatValue5 + ", " + "floatValue6=" + this.floatValue6 + ", " + "floatValues=" + this.floatValues + "]";
      }
      private static final float CONST_floatValue = 1.5F;

      public Data2(
         Matrix4f matrix4f, float floatValue, float f, float width, float height, ProxyDotRadiusData proxyDotRadiusData, ZenithColor zenithColor, float f2, float f3, float[] floatValues
      ) {
         this.matrix4f = matrix4f;
         this.floatValue = floatValue;
         this.floatValue2 = f;
         this.floatValue3 = width;
         this.floatValue4 = height;
         this.proxyDotRadiusData = proxyDotRadiusData;
         this.zenithColor = zenithColor;
         this.floatValue5 = f2;
         this.floatValue6 = f3;
         this.floatValues = floatValues;
      }

      public float method7373() {
         return this.floatValue - 1.5F;
      }

      public float method7374() {
         return this.floatValue2 - 1.5F;
      }

      public float method7375() {
         return this.floatValue3 + 3.0F;
      }

      public float method7376() {
         return this.floatValue4 + 3.0F;
      }

      public ProxyDotRadiusData method7377() {
         return new ProxyDotRadiusData(
            this.proxyDotRadiusData.getFloatValue() * this.floatValue5 / 2.0F,
            this.proxyDotRadiusData.getFloatValue2() * this.floatValue5 / 2.0F,
            this.proxyDotRadiusData.getFloatValue3() * this.floatValue5 / 2.0F,
            this.proxyDotRadiusData.getFloatValue4() * this.floatValue5 / 2.0F
         );
      }

      public float method7378() {
         return this.floatValue4 == 240.0F ? 100.0F : 50.0F;
      }

      public Matrix4f getMatrix4f() {
         return this.matrix4f;
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public float getFloatValue2() {
         return this.floatValue2;
      }

      public float width() {
         return this.floatValue3;
      }

      public float height() {
         return this.floatValue4;
      }

      public ProxyDotRadiusData getProxyDotRadiusData() {
         return this.proxyDotRadiusData;
      }

      public ZenithColor getZenithColor() {
         return this.zenithColor;
      }

      public float getFloatValue5() {
         return this.floatValue5;
      }

      public float getFloatValue6() {
         return this.floatValue6;
      }

      public float[] getFloatValues() {
         return this.floatValues;
      }
   }
}
