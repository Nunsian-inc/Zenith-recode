package org.zenith.utility.render;

import com.mojang.renderpearl.api.pipeline.BlendFactor;
import com.mojang.renderpearl.api.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.BufferBuilder;
import org.zenith.utility.render.Tesselator;
import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;
import lombok.Generated;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec2;
import org.joml.Matrix4f;
import org.zenith.RenderState;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.MsdfRenderer;
import org.zenith.client.modules.impl.render.InterfaceModule;
import org.zenith.utility.misc.WindowAccess;
import org.zenith.utility.misc.KawaseShaderKey;
import org.zenith.utility.misc.ShadowShaderKey;
import org.zenith.utility.render.display.base.CategoryIcon;
import org.zenith.utility.render.display.base.GlowColor;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public final class RenderShaders implements WindowAccess {
   public static final float CONST_floatValue = 0.8F;
   public static KawaseShaderKey kawaseShaderKey;
   public static KawaseShaderKey kawaseShaderKey2;
   public static KawaseShaderKey kawaseShaderKey3;
   public static KawaseShaderKey kawaseShaderKey4;
   private static ShadowShaderKey shadowShaderKey;
   private static KawaseShaderKey kawaseShaderKey5;
   private static KawaseShaderKey kawaseShaderKey6;
   private static KawaseShaderKey kawaseShaderKey7;
   private static KawaseShaderKey kawaseShaderKey8;
   private static KawaseShaderKey kawaseShaderKey9;
   private static KawaseShaderKey kawaseShaderKey10;
   private static KawaseShaderKey kawaseShaderKey11;
   private static KawaseShaderKey kawaseShaderKey12;
   private static KawaseShaderKey kawaseShaderKey13;
   private static KawaseShaderKey kawaseShaderKey14;
   private static KawaseShaderKey kawaseShaderKey15;
   public static GpuTextureView gpuTextureView;
   public static boolean flag = true;
   public static float[] floatValues;

   public static void method5255() {
      kawaseShaderKey = new KawaseShaderKey(Zenith.method1501("rectangle/data"), com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_COLOR);
      kawaseShaderKey2 = new KawaseShaderKey(Zenith.method1501("batch_rectangle/data"), com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_TEX_COLOR);
      kawaseShaderKey3 = new KawaseShaderKey(Zenith.method1501("batch_shape/data"), VertexFormats.vertexFormat);
      kawaseShaderKey4 = new KawaseShaderKey(Zenith.method1501("batch_noise/data"), VertexFormats.vertexFormat);
      shadowShaderKey = new ShadowShaderKey(Zenith.method1501("drop_shadow/data"));
      kawaseShaderKey5 = new KawaseShaderKey(Zenith.method1501("squircle/data"), com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_COLOR);
      kawaseShaderKey7 = new KawaseShaderKey(Zenith.method1501("squircle_texture/data"), com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_TEX_COLOR);
      kawaseShaderKey6 = new KawaseShaderKey(Zenith.method1501("texture/data"), com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_TEX_COLOR);
      kawaseShaderKey8 = new KawaseShaderKey(Zenith.method1501("border/data"), com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_COLOR);
      kawaseShaderKey9 = new KawaseShaderKey(Zenith.method1501("corner/data"), com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_COLOR);
      kawaseShaderKey10 = new KawaseShaderKey(Zenith.method1501("arc_border/data"), com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_COLOR);
      kawaseShaderKey12 = new KawaseShaderKey(Zenith.method1501("loading/data"), com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_COLOR);
      kawaseShaderKey13 = new KawaseShaderKey(Zenith.method1501("menu_background/data"), com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_COLOR);
      kawaseShaderKey14 = new KawaseShaderKey(Zenith.method1501("media_background/data"), com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_COLOR);
      kawaseShaderKey15 = new KawaseShaderKey(Zenith.method1501("gradient_rectangle/data"), com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_COLOR);
      kawaseShaderKey11 = new KawaseShaderKey(Zenith.method1501("liquidglass/data"), com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_TEX_COLOR);
      SnowShader.method387();
   }

   private static void method5256(float x, float y, float f, float f2, boolean flag) {
      BufferBuilder begin = Tesselator.getInstance().begin(PrimitiveTopology.QUADS, com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_TEX_COLOR);
      byte b = -1;
      float f3 = flag ? 0.0F : 1.0F;
      float f4 = flag ? 1.0F : 0.0F;
      begin.addVertex(x, y, 0.0F).setUv(0.0F, f4).setColor(-1);
      begin.addVertex(x, y + f2, 0.0F).setUv(0.0F, f3).setColor(-1);
      begin.addVertex(x + f, y + f2, 0.0F).setUv(1.0F, f3).setColor(-1);
      begin.addVertex(x + f, y, 0.0F).setUv(1.0F, f4).setColor(-1);
      ImmediateRenderer.method8015(begin.buildOrThrow());
   }

   public static void method5257(Matrix4f pose, Vec2 vec, Vec2 vec2, ZenithColor zenithColor) {
      MsdfRenderer.flushBatch();

      try {
         RenderState.method1871();
         RenderState.method1873();
         RenderState.method1898("core/position_color");
         RenderState.method1902(1.0F);
         method5284();
         BufferBuilder begin = Tesselator.getInstance().begin(PrimitiveTopology.DEBUG_LINE_STRIP, com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_COLOR);
         begin.addVertex(pose, vec.x, vec.y, 0.0F).setColor(zenithColor.method7287());
         begin.addVertex(pose, vec2.x, vec2.y, 0.0F).setColor(zenithColor.method7287());
         ImmediateRenderer.method8015(begin.buildOrThrow());
         method5285();
      } finally {
         RenderState.method1872();
         RenderState.method1902(1.0F);
      }
   }

   public static void method5258(Matrix4f matrix, Vec2 vec, Vec2 vec2, Vec2 vec3, Vec2 vec4, ZenithColor zenithColor, int n) {
      MsdfRenderer.flushBatch();

      try {
         Matrix4f pose = matrix;
         RenderState.method1871();
         RenderState.method1873();
         RenderState.method1898("core/position_color");
         RenderState.method1902(1.0F);
         method5284();
         BufferBuilder begin = Tesselator.getInstance().begin(PrimitiveTopology.DEBUG_LINE_STRIP, com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_COLOR);

         for (int i = 0; i <= n; i++) {
            float f = (float)i / n;
            float x = (float)MathUtils.method2644(f, vec.x, vec2.x, vec3.x, vec4.x);
            float y = (float)MathUtils.method2644(f, vec.y, vec2.y, vec3.y, vec4.y);
            begin.addVertex(pose, x, y, 0.0F).setColor(zenithColor.method7287());
         }

         ImmediateRenderer.method8015(begin.buildOrThrow());
         method5285();
      } finally {
         RenderState.method1872();
         RenderState.method1902(1.0F);
      }
   }

   private static float method5259(float f, float f2, float f3, float f4, float f5) {
      float f6 = 1.0F - f;
      float f7 = f * f;
      float f8 = f6 * f6;
      return f8 * f6 * f2 + 3.0F * f8 * f * f3 + 3.0F * f6 * f7 * f4 + f7 * f * f5;
   }

   public static void method5260(Matrix4f pose, float floatValue, float y, float width, float height, ZenithColor zenithColor) {
      MsdfRenderer.flushBatch();
      RenderState.method1898("core/position_color");
      method5284();
      BufferBuilder begin = Tesselator.getInstance().begin(PrimitiveTopology.QUADS, com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_COLOR);
      begin.addVertex(pose, floatValue, y + height, 0.0F).setColor(zenithColor.method7287());
      begin.addVertex(pose, floatValue + width, y + height, 0.0F).setColor(zenithColor.method7287());
      begin.addVertex(pose, floatValue + width, y, 0.0F).setColor(zenithColor.method7287());
      begin.addVertex(pose, floatValue, y, 0.0F).setColor(zenithColor.method7287());
      ImmediateRenderer.method8015(begin.buildOrThrow());
      method5285();
   }

   public static void method5261(Matrix4f pose, float guiScaledWidth, float guiScaledHeight, ZenithColor zenithColor, ZenithColor zenithColor2, float batchRange) {
      MsdfRenderer.flushBatch();
      kawaseShaderKey13.method5165();
      kawaseShaderKey13.method5166("Resolution").method7413(guiScaledWidth, guiScaledHeight);
      kawaseShaderKey13.method5166("Time").method7412((float)(System.currentTimeMillis() % 3600000L) / 1000.0F);
      kawaseShaderKey13.method5166("Accent").method7414(zenithColor.getIntValue() / 255.0F, zenithColor.getIntValue2() / 255.0F, zenithColor.getIntValue3() / 255.0F);
      kawaseShaderKey13.method5166("Secondary").method7414(zenithColor2.getIntValue() / 255.0F, zenithColor2.getIntValue2() / 255.0F, zenithColor2.getIntValue3() / 255.0F);
      kawaseShaderKey13.method5166("Intensity").method7412(batchRange);
      method5284();
      BufferBuilder begin = Tesselator.getInstance().begin(PrimitiveTopology.QUADS, com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_COLOR);
      int color = ZenithColor.INSTANCE.method7287();
      begin.addVertex(pose, 0.0F, 0.0F, 0.0F).setColor(color);
      begin.addVertex(pose, 0.0F, guiScaledHeight, 0.0F).setColor(color);
      begin.addVertex(pose, guiScaledWidth, guiScaledHeight, 0.0F).setColor(color);
      begin.addVertex(pose, guiScaledWidth, 0.0F, 0.0F).setColor(color);
      ImmediateRenderer.method8015(begin.buildOrThrow());
      method5285();
   }

   public static void method5262(Matrix4f pose, float width, float height, ZenithColor[] zenithColors, float batchRange, float energy, float visibility) {
      MsdfRenderer.flushBatch();
      kawaseShaderKey14.method5165();
      kawaseShaderKey14.method5166("Resolution").method7413(width, height);
      kawaseShaderKey14.method5166("Time").method7412(batchRange);
      String[] strings = new String[]{"ColorA", "ColorB", "ColorC", "ColorD"};

      for (int i = 0; i < strings.length; i++) {
         ZenithColor zenithColor = zenithColors[Math.min(i, zenithColors.length - 1)];
         kawaseShaderKey14.method5166(strings[i]).method7414(zenithColor.getIntValue() / 255.0F, zenithColor.getIntValue2() / 255.0F, zenithColor.getIntValue3() / 255.0F);
      }

      kawaseShaderKey14.method5166("Energy").method7412(energy);
      method5284();
      BufferBuilder begin = Tesselator.getInstance().begin(PrimitiveTopology.QUADS, com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_COLOR);
      int color = ZenithColor.INSTANCE.method7292(visibility).method7287();
      begin.addVertex(pose, 0.0F, 0.0F, 0.0F).setColor(color);
      begin.addVertex(pose, 0.0F, height, 0.0F).setColor(color);
      begin.addVertex(pose, width, height, 0.0F).setColor(color);
      begin.addVertex(pose, width, 0.0F, 0.0F).setColor(color);
      ImmediateRenderer.method8015(begin.buildOrThrow());
      method5285();
   }

   public static void method5263(
      Matrix4f pose,
      float x,
      float y,
      float width,
      float height,
      ProxyDotRadiusData headerRadius,
      ZenithColor zenithColor,
      float batchRange,
      float f,
      ZenithColor zenithColor2,
      float batchRange2,
      boolean flag,
      float batchRange3,
      float batchRange4,
      float batchRange5
   ) {
      MsdfRenderer.flushBatch();
      method5284();
      RenderState.method1877();
      RenderState.method1889(0, gpuTextureView != null ? gpuTextureView : Zenith.getInstance().getKawaseBlur().getFbo().getColorTextureView());
      kawaseShaderKey11.method5165();
      kawaseShaderKey11.method5166("GlobalAlpha").method7412(batchRange);
      kawaseShaderKey11.method5166("Size").method7413(width, height);
      kawaseShaderKey11.method5166("Radius").method7415(headerRadius.getFloatValue(), headerRadius.getFloatValue4(), headerRadius.getFloatValue2(), headerRadius.getFloatValue3());
      kawaseShaderKey11.method5166("Smoothness").method7412(1.0F);
      kawaseShaderKey11.method5166("FresnelPower").method7412(f);
      kawaseShaderKey11.method5166("FresnelColor").method7414(zenithColor2.getIntValue() / 255.0F, zenithColor2.getIntValue2() / 255.0F, zenithColor2.getIntValue3() / 255.0F);
      kawaseShaderKey11.method5166("FresnelAlpha").method7412(zenithColor2.getIntValue4() / 255.0F);
      kawaseShaderKey11.method5166("BaseAlpha").method7412(batchRange2);
      kawaseShaderKey11.method5166("FresnelInvert").set(flag ? 1 : 0);
      kawaseShaderKey11.method5166("FresnelMix").method7412(batchRange3);
      kawaseShaderKey11.method5166("DistortStrength").method7412(batchRange4);
      kawaseShaderKey11.method5166("Time").method7412((float)(System.currentTimeMillis() % 1000000L) / 1000.0F);
      ZenithColor zenithColor3 = InterfaceModule.INSTANCE.method6824();
      kawaseShaderKey11.method5166("GlareColor").method7414(zenithColor3.getIntValue() / 255.0F, zenithColor3.getIntValue2() / 255.0F, zenithColor3.getIntValue3() / 255.0F);
      kawaseShaderKey11.method5166("GlareAlpha").method7412(zenithColor3.getIntValue4() / 255.0F);
      kawaseShaderKey11.method5166("GlareSpeed").method7412(InterfaceModule.INSTANCE.method6825());
      kawaseShaderKey11.method5166("GlareMode").method7412(InterfaceModule.INSTANCE.method6826());
      kawaseShaderKey11.method5166("GlareTexture").method7412(InterfaceModule.INSTANCE.method6827());
      kawaseShaderKey11.method5166("CornerSmoothness").method7412(batchRange5);
      method5283(floatValues, kawaseShaderKey11, x, y, batchRange5 / 2.0F);
      int guiScaledWidth = window.getGuiScaledWidth();
      int guiScaledHeight = window.getGuiScaledHeight();
      float f2 = x / guiScaledWidth;
      float f3 = (guiScaledHeight - y - height) / guiScaledHeight;
      float f4 = width / guiScaledWidth;
      float f5 = height / guiScaledHeight;
      BufferBuilder begin = Tesselator.getInstance().begin(PrimitiveTopology.QUADS, com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_TEX_COLOR);
      begin.addVertex(pose, x, y, 0.0F).setUv(f2, f3 + f5).setColor(zenithColor.method7287());
      begin.addVertex(pose, x, y + height, 0.0F).setUv(f2, f3).setColor(zenithColor.method7287());
      begin.addVertex(pose, x + width, y + height, 0.0F).setUv(f2 + f4, f3).setColor(zenithColor.method7287());
      begin.addVertex(pose, x + width, y, 0.0F).setUv(f2 + f4, f3 + f5).setColor(zenithColor.method7287());
      ImmediateRenderer.method8015(begin.buildOrThrow());
      RenderState.method1892(0, 0);
      RenderState.method1876();
      method5285();
   }

   public static void method5264(
      Matrix4f pose, float floatValue, float y, float width, float height, float f, float f2, ProxyDotRadiusData headerRadius, ZenithColor zenithColor
   ) {
      headerRadius = new ProxyDotRadiusData(
         headerRadius.getFloatValue() * f / 2.0F, headerRadius.getFloatValue2() * f / 2.0F, headerRadius.getFloatValue3() * f / 2.0F, headerRadius.getFloatValue4() * f / 2.0F
      );
      float f3 = 1.5F;
      method5263(
         pose,
         floatValue - f3,
         y - f3,
         width + f3 * 2.0F,
         height + f3 * 2.0F,
         headerRadius,
         zenithColor,
         zenithColor.getIntValue4() / 255.0F,
         height == 240.0F ? 100.0F : 50.0F,
         zenithColor.method7293(255),
         1.0F,
         true,
         0.0F,
         f2,
         f
      );
   }

   public static void method5265(
      Matrix4f pose, float x, float y, float width, float height, float f, ProxyDotRadiusData headerRadius, ZenithColor zenithColor, boolean flag
   ) {
      headerRadius = new ProxyDotRadiusData(
         headerRadius.getFloatValue() * f / 2.0F, headerRadius.getFloatValue2() * f / 2.0F, headerRadius.getFloatValue3() * f / 2.0F, headerRadius.getFloatValue4() * f / 2.0F
      );
      method5263(
         pose,
         x,
         y,
         width,
         height,
         headerRadius,
         zenithColor,
         zenithColor.getIntValue4() / 255.0F,
         height == 240.0F ? 100.0F : 50.0F,
         zenithColor.method7293(255),
         1.0F,
         true,
         0.0F,
         0.08F,
         f
      );
   }

   public static void method5266(Matrix4f pose, float floatValue, float y, float width, float height, float batchRange, ProxyDotRadiusData proxyDotRadiusData, ZenithColor zenithColor) {
      MsdfRenderer.flushBatch();
      float f = 0.8F;
      kawaseShaderKey5.method5165();
      kawaseShaderKey5.method5166("Size").method7413(width, height);
      kawaseShaderKey5.method5166("Radius")
         .method7415(
            proxyDotRadiusData.getFloatValue() * batchRange / 2.0F, proxyDotRadiusData.getFloatValue4() * batchRange / 2.0F, proxyDotRadiusData.getFloatValue2() * batchRange / 2.0F, proxyDotRadiusData.getFloatValue3() * batchRange / 2.0F
         );
      kawaseShaderKey5.method5166("Smoothness").method7412(f);
      kawaseShaderKey5.method5166("CornerSmoothness").method7412(batchRange);
      method5284();
      BufferBuilder begin = Tesselator.getInstance().begin(PrimitiveTopology.QUADS, com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_COLOR);
      begin.addVertex(pose, floatValue, y, 0.0F).setColor(zenithColor.method7287());
      begin.addVertex(pose, floatValue, y + height, 0.0F).setColor(zenithColor.method7287());
      begin.addVertex(pose, floatValue + width, y + height, 0.0F).setColor(zenithColor.method7287());
      begin.addVertex(pose, floatValue + width, y, 0.0F).setColor(zenithColor.method7287());
      ImmediateRenderer.method8015(begin.buildOrThrow());
      method5285();
   }

   public static void method5267(Matrix4f pose, float x, float y, float width, float height, float batchRange, ProxyDotRadiusData proxyDotRadiusData, ZenithColor zenithColor) {
      MsdfRenderer.flushBatch();
      float f = 0.8F;
      kawaseShaderKey12.method5165();
      kawaseShaderKey12.method5166("Size").method7413(width, height);
      kawaseShaderKey12.method5166("Radius").method7415(proxyDotRadiusData.getFloatValue(), proxyDotRadiusData.getFloatValue4(), proxyDotRadiusData.getFloatValue2(), proxyDotRadiusData.getFloatValue3());
      kawaseShaderKey12.method5166("Smoothness").method7412(f);
      kawaseShaderKey12.method5166("Progress").method7412(batchRange);
      kawaseShaderKey12.method5166("StripeWidth").method7412(0.0F);
      kawaseShaderKey12.method5166("Fade").method7412(0.5F);
      method5284();
      BufferBuilder begin = Tesselator.getInstance().begin(PrimitiveTopology.QUADS, com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_COLOR);
      begin.addVertex(pose, x, y, 0.0F).setColor(zenithColor.method7287());
      begin.addVertex(pose, x, y + height, 0.0F).setColor(zenithColor.method7287());
      begin.addVertex(pose, x + width, y + height, 0.0F).setColor(zenithColor.method7287());
      begin.addVertex(pose, x + width, y, 0.0F).setColor(zenithColor.method7287());
      ImmediateRenderer.method8015(begin.buildOrThrow());
      method5285();
   }

   public static void method5268(Matrix4f matrix, float floatValue, float y, float width, float height, ProxyDotRadiusData proxyDotRadiusData, ZenithColor zenithColor) {
      MsdfRenderer.flushBatch();
      float batchRange = 0.8F;
      kawaseShaderKey.method5165();
      kawaseShaderKey.method5166("Size").method7413(width, height);
      kawaseShaderKey.method5166("Radius").method7415(proxyDotRadiusData.getFloatValue(), proxyDotRadiusData.getFloatValue4(), proxyDotRadiusData.getFloatValue2(), proxyDotRadiusData.getFloatValue3());
      kawaseShaderKey.method5166("Smoothness").method7412(batchRange);
      method5283(floatValues, kawaseShaderKey, floatValue, y, 1.0F);
      method5284();
      BufferBuilder begin = Tesselator.getInstance().begin(PrimitiveTopology.QUADS, com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_COLOR);
      begin.addVertex(matrix, floatValue, y, 0.0F).setColor(zenithColor.method7287());
      begin.addVertex(matrix, floatValue, y + height, 0.0F).setColor(zenithColor.method7287());
      begin.addVertex(matrix, floatValue + width, y + height, 0.0F).setColor(zenithColor.method7287());
      begin.addVertex(matrix, floatValue + width, y, 0.0F).setColor(zenithColor.method7287());
      ImmediateRenderer.method8015(begin.buildOrThrow());
      method5285();
   }

   public static void method5269(
      Matrix4f pose,
      float floatValue,
      float y,
      float width,
      float height,
      ProxyDotRadiusData proxyDotRadiusData,
      ZenithColor zenithColor,
      ZenithColor zenithColor2,
      ZenithColor zenithColor4,
      ZenithColor zenithColor3
   ) {
      MsdfRenderer.flushBatch();
      float batchRange = 0.8F;
      kawaseShaderKey15.method5165();
      kawaseShaderKey15.method5166("Size").method7413(width, height);
      kawaseShaderKey15.method5166("Radius").method7415(proxyDotRadiusData.getFloatValue(), proxyDotRadiusData.getFloatValue4(), proxyDotRadiusData.getFloatValue2(), proxyDotRadiusData.getFloatValue3());
      kawaseShaderKey15.method5166("Smoothness").method7412(batchRange);
      kawaseShaderKey15.method5166("TopLeftColor")
         .method7415(zenithColor.getIntValue() / 255.0F, zenithColor.getIntValue2() / 255.0F, zenithColor.getIntValue3() / 255.0F, zenithColor.getIntValue4() / 255.0F);
      kawaseShaderKey15.method5166("BottomLeftColor")
         .method7415(zenithColor2.getIntValue() / 255.0F, zenithColor2.getIntValue2() / 255.0F, zenithColor2.getIntValue3() / 255.0F, zenithColor2.getIntValue4() / 255.0F);
      kawaseShaderKey15.method5166("BottomRightColor")
         .method7415(zenithColor4.getIntValue() / 255.0F, zenithColor4.getIntValue2() / 255.0F, zenithColor4.getIntValue3() / 255.0F, zenithColor4.getIntValue4() / 255.0F);
      kawaseShaderKey15.method5166("TopRightColor")
         .method7415(zenithColor3.getIntValue() / 255.0F, zenithColor3.getIntValue2() / 255.0F, zenithColor3.getIntValue3() / 255.0F, zenithColor3.getIntValue4() / 255.0F);
      method5284();
      BufferBuilder begin = Tesselator.getInstance().begin(PrimitiveTopology.QUADS, com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_COLOR);
      begin.addVertex(pose, floatValue, y, 0.0F).setColor(zenithColor.method7287());
      begin.addVertex(pose, floatValue, y + height, 0.0F).setColor(zenithColor2.method7287());
      begin.addVertex(pose, floatValue + width, y + height, 0.0F).setColor(zenithColor4.method7287());
      begin.addVertex(pose, floatValue + width, y, 0.0F).setColor(zenithColor3.method7287());
      ImmediateRenderer.method8015(begin.buildOrThrow());
      method5285();
   }

   public static void method5270(Matrix4f pose, float floatValue, float y, float width, float height, ProxyDotRadiusData proxyDotRadiusData, GlowColor glowColor) {
      method5269(pose, floatValue, y, width, height, proxyDotRadiusData, glowColor.getZenithColor(), glowColor.getZenithColor2(), glowColor.getZenithColor4(), glowColor.getZenithColor3());
   }

   public static void method5271(Matrix4f pose, float floatValue, float lastY, float width, float height, float batchRange, ProxyDotRadiusData proxyDotRadiusData, GlowColor glowColor) {
      method5273(pose, floatValue, lastY, width, height, batchRange, proxyDotRadiusData, glowColor.getZenithColor(), glowColor.getZenithColor2(), glowColor.getZenithColor4(), glowColor.getZenithColor3());
   }

   public static void method5272(Matrix4f pose, float floatValue, float y, float width, float height, float batchRange, ProxyDotRadiusData headerRadius, ZenithColor zenithColor) {
      method5273(pose, floatValue, y, width, height, batchRange, headerRadius, zenithColor, zenithColor, zenithColor, zenithColor);
   }

   public static void method5273(
      Matrix4f pose,
      float floatValue,
      float lastY,
      float width,
      float height,
      float batchRange,
      ProxyDotRadiusData proxyDotRadiusData,
      ZenithColor zenithColor,
      ZenithColor zenithColor2,
      ZenithColor zenithColor4,
      ZenithColor zenithColor3
   ) {
      MsdfRenderer.flushBatch();
      float f = 0.4F;
      float height2 = 0.4F;
      if (batchRange >= 0.15) {
         f = 0.6F;
         height2 = f;
      }

      kawaseShaderKey8.method5165();
      kawaseShaderKey8.method5166("Size").method7413(width, height);
      kawaseShaderKey8.method5166("Radius").method7415(proxyDotRadiusData.getFloatValue(), proxyDotRadiusData.getFloatValue4(), proxyDotRadiusData.getFloatValue2(), proxyDotRadiusData.getFloatValue3());
      kawaseShaderKey8.method5166("Smoothness").method7413(f, height2);
      kawaseShaderKey8.method5166("Thickness").method7412(batchRange);
      method5284();
      BufferBuilder begin = Tesselator.getInstance().begin(PrimitiveTopology.QUADS, com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_COLOR);
      begin.addVertex(pose, floatValue, lastY, 0.0F).setColor(zenithColor.method7287());
      begin.addVertex(pose, floatValue, lastY + height, 0.0F).setColor(zenithColor2.method7287());
      begin.addVertex(pose, floatValue + width, lastY + height, 0.0F).setColor(zenithColor4.method7287());
      begin.addVertex(pose, floatValue + width, lastY, 0.0F).setColor(zenithColor3.method7287());
      ImmediateRenderer.method8015(begin.buildOrThrow());
      method5285();
   }

   public static void method5274(Matrix4f pose, float x, float y, float width, float height, float batchRange, float f, float f2, GlowColor glowColor) {
      method5276(pose, x, y, width, height, batchRange, f, f2, glowColor.getZenithColor(), glowColor.getZenithColor2(), glowColor.getZenithColor4(), glowColor.getZenithColor3());
   }

   public static void method5275(Matrix4f pose, float floatValue, float y, float width, float height, float batchRange, float f, float f2, ZenithColor zenithColor) {
      method5276(pose, floatValue, y, width, height, batchRange, f, f2, zenithColor, zenithColor, zenithColor, zenithColor);
   }

   public static void method5276(
      Matrix4f pose,
      float x,
      float y,
      float width,
      float height,
      float batchRange,
      float f,
      float f2,
      ZenithColor zenithColor,
      ZenithColor zenithColor2,
      ZenithColor zenithColor22,
      ZenithColor zenithColor3
   ) {
      MsdfRenderer.flushBatch();
      float width2 = 0.4F;
      float height2 = 0.4F;
      float batchRange2 = Math.max(0.0F, Math.min(360.0F, f));
      float batchRange3 = Math.max(0.0F, Math.min(1.0F, f2));
      kawaseShaderKey10.method5165();
      kawaseShaderKey10.method5166("Size").method7413(width, height);
      kawaseShaderKey10.method5166("Smoothness").method7413(width2, height2);
      kawaseShaderKey10.method5166("Thickness").method7412(batchRange);
      kawaseShaderKey10.method5166("ArcDegrees").method7412(batchRange2);
      kawaseShaderKey10.method5166("CapRoundness").method7412(batchRange3);
      method5284();
      BufferBuilder begin = Tesselator.getInstance().begin(PrimitiveTopology.QUADS, com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_COLOR);
      begin.addVertex(pose, x, y, 0.0F).setColor(zenithColor.method7287());
      begin.addVertex(pose, x, y + height, 0.0F).setColor(zenithColor2.method7287());
      begin.addVertex(pose, x + width, y + height, 0.0F).setColor(zenithColor22.method7287());
      begin.addVertex(pose, x + width, y, 0.0F).setColor(zenithColor3.method7287());
      ImmediateRenderer.method8015(begin.buildOrThrow());
      method5285();
   }

   public static void method5277(
      Matrix4f pose, float floatValue, float y, float width, float height, float batchRange, float f, ZenithColor zenithColor, ProxyDotRadiusData proxyDotRadiusData
   ) {
      floatValue -= 0.3F;
      y -= 0.3F;
      width += 0.6F;
      height += 0.6F;
      method5279(pose, floatValue, y, f, f, batchRange, proxyDotRadiusData, zenithColor, 0.0F);
      method5279(pose, floatValue + width - f, y, f, f, batchRange, proxyDotRadiusData, zenithColor, 1.0F);
      method5279(pose, floatValue, y + height - f, f, f, batchRange, proxyDotRadiusData, zenithColor, 2.0F);
      method5279(pose, floatValue + width - f, y + height - f, f, f, batchRange, proxyDotRadiusData, zenithColor, 3.0F);
   }

   public static void method5278(
      Matrix4f pose, float floatValue, float y, float f, float f2, float batchRange, float width, ZenithColor zenithColor, ProxyDotRadiusData proxyDotRadiusData
   ) {
      if (InterfaceModule.INSTANCE.method6822()) {
         floatValue -= 0.3F;
         y -= 0.3F;
         f += 0.6F;
         f2 += 0.6F;
         method5279(pose, floatValue, y, width, width, batchRange, proxyDotRadiusData, zenithColor, 0.0F);
         method5279(pose, floatValue + f - width, y, width, width, batchRange, proxyDotRadiusData, zenithColor, 1.0F);
         method5279(pose, floatValue, y + f2 - width, width, width, batchRange, proxyDotRadiusData, zenithColor, 2.0F);
         method5279(pose, floatValue + f - width, y + f2 - width, width, width, batchRange, proxyDotRadiusData, zenithColor, 3.0F);
      }
   }

   public static void method5279(
      Matrix4f pose, float floatValue, float y, float width, float height, float batchRange, ProxyDotRadiusData proxyDotRadiusData, ZenithColor zenithColor, float f
   ) {
      MsdfRenderer.flushBatch();
      float width2 = 0.8F;
      float height2 = 1.0F;
      kawaseShaderKey9.method5165();
      kawaseShaderKey9.method5166("Size").method7413(width, height);
      kawaseShaderKey9.method5166("Radius").method7415(proxyDotRadiusData.getFloatValue(), proxyDotRadiusData.getFloatValue4(), proxyDotRadiusData.getFloatValue2(), proxyDotRadiusData.getFloatValue3());
      kawaseShaderKey9.method5166("Smoothness").method7413(width2, height2);
      kawaseShaderKey9.method5166("Thickness").method7412(batchRange);
      kawaseShaderKey9.method5166("CornerIndex").method7412(f);
      method5284();
      BufferBuilder begin = Tesselator.getInstance().begin(PrimitiveTopology.QUADS, com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_COLOR);
      begin.addVertex(pose, floatValue, y, 0.0F).setColor(zenithColor.method7287());
      begin.addVertex(pose, floatValue, y + height, 0.0F).setColor(zenithColor.method7287());
      begin.addVertex(pose, floatValue + width, y + height, 0.0F).setColor(zenithColor.method7287());
      begin.addVertex(pose, floatValue + width, y, 0.0F).setColor(zenithColor.method7287());
      ImmediateRenderer.method8015(begin.buildOrThrow());
      method5285();
   }

   public static void method5280(Matrix4f pose, Identifier identifier, float x, float y, float guiScaledWidth, float guiScaledHeight, ZenithColor zenithColor) {
      MsdfRenderer.flushBatch();
      RenderState.method1898("core/position_tex_color");
      RenderState.method1888(0, TextureFilterUtils.method7269(identifier), TextureFilterUtils.method7268(identifier));
      method5284();
      BufferBuilder begin = Tesselator.getInstance().begin(PrimitiveTopology.QUADS, com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_TEX_COLOR);
      begin.addVertex(pose, x, y, 0.0F).setUv(0.0F, 0.0F).setColor(zenithColor.method7287());
      begin.addVertex(pose, x, y + guiScaledHeight, 0.0F).setUv(0.0F, 1.0F).setColor(zenithColor.method7287());
      begin.addVertex(pose, x + guiScaledWidth, y + guiScaledHeight, 0.0F).setUv(1.0F, 1.0F).setColor(zenithColor.method7287());
      begin.addVertex(pose, x + guiScaledWidth, y, 0.0F).setUv(1.0F, 0.0F).setColor(zenithColor.method7287());
      ImmediateRenderer.method8015(begin.buildOrThrow());
      method5285();
      RenderState.method1887(0, (GpuTextureView)null);
   }

   public static void method5281(Matrix4f pose, Identifier identifier, float floatValue, float y, float width, float height, GlowColor glowColor) {
      MsdfRenderer.flushBatch();
      RenderState.method1898("core/position_tex_color");
      RenderState.method1888(0, TextureFilterUtils.method7269(identifier), TextureFilterUtils.method7268(identifier));
      method5284();
      BufferBuilder begin = Tesselator.getInstance().begin(PrimitiveTopology.QUADS, com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_TEX_COLOR);
      begin.addVertex(pose, floatValue, y, 0.0F).setUv(0.0F, 0.0F).setColor(glowColor.getZenithColor().method7287());
      begin.addVertex(pose, floatValue, y + height, 0.0F).setUv(0.0F, 1.0F).setColor(glowColor.getZenithColor2().method7287());
      begin.addVertex(pose, floatValue + width, y + height, 0.0F).setUv(1.0F, 1.0F).setColor(glowColor.getZenithColor4().method7287());
      begin.addVertex(pose, floatValue + width, y, 0.0F).setUv(1.0F, 0.0F).setColor(glowColor.getZenithColor3().method7287());
      ImmediateRenderer.method8015(begin.buildOrThrow());
      method5285();
      RenderState.method1887(0, (GpuTextureView)null);
   }

   public static void method5282(
      Matrix4f pose, Identifier identifier, float floatValue, float y, float f, float f2, float f3, float f4, float f5, float f6, ZenithColor zenithColor
   ) {
      MsdfRenderer.flushBatch();
      RenderState.method1871();
      RenderState.method1873();
      int color = zenithColor.method7287();
      float x = floatValue + f;
      float y2 = y + f2;
      RenderState.method1898("core/position_tex_color");
      RenderState.method1888(0, TextureFilterUtils.method7269(identifier), TextureFilterUtils.method7268(identifier));
      BufferBuilder begin = Tesselator.getInstance().begin(PrimitiveTopology.QUADS, com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_TEX_COLOR);
      begin.addVertex(pose, floatValue, y, 0.0F).setUv(f3, f5).setColor(color);
      begin.addVertex(pose, floatValue, y2, 0.0F).setUv(f3, f6).setColor(color);
      begin.addVertex(pose, x, y2, 0.0F).setUv(f4, f6).setColor(color);
      begin.addVertex(pose, x, y, 0.0F).setUv(f4, f5).setColor(color);
      ImmediateRenderer.method8015(begin.buildOrThrow());
      method5285();
      RenderState.method1887(0, (GpuTextureView)null);
      RenderState.method1872();
   }

   public static void method5283(float[] floatValues, KawaseShaderKey kawaseShaderKey11, float x, float y, float f) {
      if (floatValues == null) {
         kawaseShaderKey11.method5166("Tab").method7415(0.0F, 0.0F, 0.0F, 0.0F);
         kawaseShaderKey11.method5166("TabRadius").method7412(0.0F);
         kawaseShaderKey11.method5166("MainHeight").method7412(0.0F);
         kawaseShaderKey11.method5166("Joint").method7412(0.0F);
      } else {
         kawaseShaderKey11.method5166("Tab").method7415(floatValues[0] - x, floatValues[1] - y, floatValues[2], floatValues[3]);
         kawaseShaderKey11.method5166("TabRadius").method7412(floatValues[4] * f);
         kawaseShaderKey11.method5166("MainHeight").method7412(floatValues[5] - y);
         kawaseShaderKey11.method5166("Joint").method7412(floatValues[6]);
      }
   }

   public static void method5284() {
      RenderState.method1871();
      RenderState.method1873();
   }

   public static void method5285() {
      RenderState.method1872();
   }

   public static void method5286(Matrix4f pose, float floatValue, float y, float width, float height, float blurPower, ProxyDotRadiusData headerRadius, ZenithColor zenithColor) {
      method5287(
         pose,
         floatValue,
         y,
         width,
         height,
         blurPower,
         headerRadius,
         zenithColor,
         (InterfaceModule.INSTANCE.method6817() || InterfaceModule.INSTANCE.method6818()) && flag,
         InterfaceModule.INSTANCE.method6819()
      );
   }

   public static void method5287(
      Matrix4f pose, float floatValue, float y, float panelWidth, float height, float blurPower, ProxyDotRadiusData headerRadius, ZenithColor zenithColor, boolean flag, boolean flag2
   ) {
      if (flag2 && InterfaceModule.INSTANCE.method6821()) {
         method5290(pose, floatValue, y, panelWidth, height, InterfaceModule.INSTANCE.method6823(), ProxyDotRadiusData.method8069(InterfaceModule.method6801()));
      }

      if (flag) {
         method5301(pose, floatValue, y, panelWidth, height, headerRadius, zenithColor, InterfaceModule.INSTANCE.method6818());
      }

      if (flag2 && !InterfaceModule.INSTANCE.method6821()) {
         method5290(pose, floatValue, y, panelWidth, height, InterfaceModule.INSTANCE.method6823(), ProxyDotRadiusData.method8069(InterfaceModule.method6801()));
      }
   }

   public static void method5288(
      Matrix4f pose, float floatValue, float y, float height, float f, float f2, float f3, float f4, float f5, ProxyDotRadiusData panelRadius, ZenithColor zenithColor
   ) {
      float height2 = height;
      float f6 = f;
      if (!(height <= 0.0F) && !(f <= 0.0F) && zenithColor.getIntValue4() != 0) {
         MsdfRenderer.flushBatch();

         try {
            float batchRange = (float)Math.ceil(Math.max(0.0F, f4) * 1.5F + Math.max(0.0F, f5)) + 1.0F;
            float f7 = floatValue + f2;
            float f8 = y + f3;
            float x = f7 - batchRange;
            float y2 = f8 - batchRange;
            float width = height2 + batchRange * 2.0F;
            float height3 = f6 + batchRange * 2.0F;
            shadowShaderKey.method5165();
            shadowShaderKey.method5497(width, height3, height2, f6, batchRange, Math.max(0.0F, f4), f5, panelRadius);
            method5284();
            BufferBuilder begin = Tesselator.getInstance().begin(PrimitiveTopology.QUADS, com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_COLOR);
            begin.addVertex(pose, x, y2, 0.0F).setColor(zenithColor.method7287());
            begin.addVertex(pose, x, y2 + height3, 0.0F).setColor(zenithColor.method7287());
            begin.addVertex(pose, x + width, y2 + height3, 0.0F).setColor(zenithColor.method7287());
            begin.addVertex(pose, x + width, y2, 0.0F).setColor(zenithColor.method7287());
            ImmediateRenderer.method8015(begin.buildOrThrow());
         } finally {
            method5285();
         }
      }
   }

   public static void method5289(Matrix4f pose, float floatValue, float y, float width, float heightPanel, float blurPower, ProxyDotRadiusData headerRadius, ZenithColor zenithColor) {
      MsdfRenderer.flushBatch();
      Zenith.getInstance().getKawaseBlur().method7355(pose, floatValue, y, width, heightPanel, headerRadius, zenithColor);
   }

   public static void method5290(Matrix4f pose, float floatValue, float y, float panelWidth, float height, int intValue, ProxyDotRadiusData proxyDotRadiusData2) {
      ShapeRenderer.method7167(pose, floatValue, y, panelWidth, height, intValue, proxyDotRadiusData2, Zenith.getInstance().getStyleManager().getGlowColor());
   }

   public static void method5291(Matrix4f pose, Identifier texturePath, float floatValue, float y, float width, ProxyDotRadiusData headerRadius, ZenithColor zenithColor) {
      method5296(pose, texturePath, floatValue, y, width, width, headerRadius, zenithColor, 0.125F, 0.125F, 0.25F, 0.25F);
   }

   public static void method5292(Matrix4f pose, Identifier prewarm, float floatValue, float y, float width, float height, ProxyDotRadiusData avatarRadius) {
      method5294(pose, prewarm, floatValue, y, width, height, avatarRadius, ZenithColor.INSTANCE);
   }

   public static void method5293(
      Matrix4f pose,
      Identifier prewarm,
      float x,
      float y,
      float width,
      float height,
      ProxyDotRadiusData avatarRadius,
      ZenithColor zenithColor,
      ZenithColor zenithColor2,
      ZenithColor zenithColor3,
      ZenithColor zenithColor4
   ) {
      MsdfRenderer.flushBatch();
      float batchRange = 0.8F;
      kawaseShaderKey6.method5165();
      RenderState.method1888(0, TextureFilterUtils.method7269(prewarm), TextureFilterUtils.method7268(prewarm));
      kawaseShaderKey6.method5166("Size").method7413(width, height);
      kawaseShaderKey6.method5166("Radius")
         .method7415(avatarRadius.getFloatValue() * 2.0F, avatarRadius.getFloatValue4() * 2.0F, avatarRadius.getFloatValue2() * 2.0F, avatarRadius.getFloatValue3() * 2.0F);
      kawaseShaderKey6.method5166("Smoothness").method7412(batchRange);
      method5284();
      BufferBuilder begin = Tesselator.getInstance().begin(PrimitiveTopology.QUADS, com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_TEX_COLOR);
      begin.addVertex(pose, x, y, 0.0F).setUv(0.0F, 0.0F).setColor(zenithColor.method7287());
      begin.addVertex(pose, x, y + height, 0.0F).setUv(0.0F, 1.0F).setColor(zenithColor2.method7287());
      begin.addVertex(pose, x + width, y + height, 0.0F).setUv(1.0F, 1.0F).setColor(zenithColor3.method7287());
      begin.addVertex(pose, x + width, y, 0.0F).setUv(1.0F, 0.0F).setColor(zenithColor4.method7287());
      ImmediateRenderer.method8015(begin.buildOrThrow());
      method5285();
      RenderState.method1887(0, (GpuTextureView)null);
   }

   public static void method5294(Matrix4f pose, Identifier prewarm, float x, float y, float width, float height, ProxyDotRadiusData avatarRadius, ZenithColor zenithColor) {
      method5293(pose, prewarm, x, y, width, height, avatarRadius, zenithColor, zenithColor, zenithColor, zenithColor);
   }

   public static void method5295(Matrix4f pose, Identifier prewarm, float x, float y, float width, float height, ProxyDotRadiusData avatarRadius, GlowColor glowColor) {
      method5293(pose, prewarm, x, y, width, height, avatarRadius, glowColor.getZenithColor(), glowColor.getZenithColor2(), glowColor.getZenithColor4(), glowColor.getZenithColor3());
   }

   public static void method5296(
      Matrix4f pose,
      Identifier texturePath,
      float floatValue,
      float y,
      float width,
      float height,
      ProxyDotRadiusData headerRadius,
      ZenithColor zenithColor,
      float f,
      float f2,
      float f3,
      float f4
   ) {
      method5297(pose, texturePath, floatValue, y, width, height, headerRadius, zenithColor, f, f2, f3, f4, false);
   }

   private static void method5297(
      Matrix4f pose,
      Identifier texturePath,
      float floatValue,
      float y,
      float width,
      float height,
      ProxyDotRadiusData headerRadius,
      ZenithColor zenithColor,
      float f,
      float f2,
      float f3,
      float f4,
      boolean flag
   ) {
      MsdfRenderer.flushBatch();
      float batchRange = 0.8F;
      kawaseShaderKey6.method5165();
      RenderState.method1888(0, TextureFilterUtils.method7269(texturePath), TextureFilterUtils.method7268(texturePath));
      kawaseShaderKey6.method5166("Size").method7413(width, height);
      kawaseShaderKey6.method5166("Radius").method7415(headerRadius.getFloatValue(), headerRadius.getFloatValue4(), headerRadius.getFloatValue2(), headerRadius.getFloatValue3());
      kawaseShaderKey6.method5166("Smoothness").method7412(batchRange);
      method5284();
      if (flag) {
         RenderState.method1875(BlendFactor.SRC_ALPHA, BlendFactor.ONE, BlendFactor.ZERO, BlendFactor.ONE);
      }

      BufferBuilder begin = Tesselator.getInstance().begin(PrimitiveTopology.QUADS, com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_TEX_COLOR);
      begin.addVertex(pose, floatValue, y, 0.0F).setUv(f, f2).setColor(zenithColor.method7287());
      begin.addVertex(pose, floatValue, y + height, 0.0F).setUv(f, f4).setColor(zenithColor.method7287());
      begin.addVertex(pose, floatValue + width, y + height, 0.0F).setUv(f3, f4).setColor(zenithColor.method7287());
      begin.addVertex(pose, floatValue + width, y, 0.0F).setUv(f3, f2).setColor(zenithColor.method7287());
      ImmediateRenderer.method8015(begin.buildOrThrow());
      if (flag) {
         RenderState.method1873();
      }

      method5285();
      RenderState.method1887(0, (GpuTextureView)null);
   }

   public static void method5298(
      Matrix4f pose,
      GpuTextureView colorAttachment,
      float floatValue,
      float y,
      float width,
      float height,
      ProxyDotRadiusData headerRadius,
      ZenithColor zenithColor,
      float f,
      float f2,
      float f3,
      float f4
   ) {
      MsdfRenderer.flushBatch();
      float batchRange = 0.8F;
      kawaseShaderKey6.method5165();
      RenderState.method1887(0, colorAttachment);
      kawaseShaderKey6.method5166("Size").method7413(width, height);
      kawaseShaderKey6.method5166("Radius").method7415(headerRadius.getFloatValue(), headerRadius.getFloatValue4(), headerRadius.getFloatValue2(), headerRadius.getFloatValue3());
      kawaseShaderKey6.method5166("Smoothness").method7412(batchRange);
      method5284();
      BufferBuilder begin = Tesselator.getInstance().begin(PrimitiveTopology.QUADS, com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_TEX_COLOR);
      begin.addVertex(pose, floatValue, y, 0.0F).setUv(f, f2).setColor(zenithColor.method7287());
      begin.addVertex(pose, floatValue, y + height, 0.0F).setUv(f, f4).setColor(zenithColor.method7287());
      begin.addVertex(pose, floatValue + width, y + height, 0.0F).setUv(f3, f4).setColor(zenithColor.method7287());
      begin.addVertex(pose, floatValue + width, y, 0.0F).setUv(f3, f2).setColor(zenithColor.method7287());
      ImmediateRenderer.method8015(begin.buildOrThrow());
      method5285();
      RenderState.method1887(0, (GpuTextureView)null);
   }

   public static void method5299(Matrix4f pose, float moduleMenuX, float f, float width, float height, float batchRange, ProxyDotRadiusData moduleMenuRadius, ZenithColor zenithColor) {
      MsdfRenderer.flushBatch();
      kawaseShaderKey.method5165();
      kawaseShaderKey.method5166("Size").method7413(width, height);
      kawaseShaderKey.method5166("Radius")
         .method7415(moduleMenuRadius.getFloatValue() * 3.0F, moduleMenuRadius.getFloatValue4() * 3.0F, moduleMenuRadius.getFloatValue2() * 3.0F, moduleMenuRadius.getFloatValue3() * 3.0F);
      kawaseShaderKey.method5166("Smoothness").method7412(batchRange);
      method5284();
      float f2 = -batchRange / 2.0F + batchRange * 2.0F;
      float f3 = batchRange / 2.0F + batchRange;
      float x = moduleMenuX - f2 / 2.0F;
      float y = f - f3 / 2.0F;
      float f4 = width + f2;
      float f5 = height + f3;
      BufferBuilder begin = Tesselator.getInstance().begin(PrimitiveTopology.QUADS, com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_COLOR);
      begin.addVertex(pose, x, y, 0.0F).setColor(zenithColor.method7287());
      begin.addVertex(pose, x, y + f5, 0.0F).setColor(zenithColor.method7287());
      begin.addVertex(pose, x + f4, y + f5, 0.0F).setColor(zenithColor.method7287());
      begin.addVertex(pose, x + f4, y, 0.0F).setColor(zenithColor.method7287());
      ImmediateRenderer.method8015(begin.buildOrThrow());
      method5285();
   }

   public static void method5300(Matrix4f pose, CategoryIcon categoryIcon, float floatValue, float y, float f, float f2, ZenithColor zenithColor) {
      method5282(pose, categoryIcon.getIdentifier(), floatValue, y, f, f2, 0.0F, 1.0F, 0.0F, 1.0F, zenithColor);
   }

   public static void method5301(Matrix4f pose, float floatValue, float y, float panelWidth, float height, ProxyDotRadiusData headerRadius, ZenithColor zenithColor, boolean flag) {
      if (Zenith.getInstance().getNLMenuScreen().getBlurPower() != 0.0F) {
         MsdfRenderer.flushBatch();
         if (flag) {
            method5264(pose, floatValue, y, panelWidth, height, 10.0F, 0.08F, headerRadius, zenithColor);
         } else {
            Zenith.getInstance().getKawaseBlur().method7353(pose, floatValue, y, panelWidth, height, headerRadius, zenithColor);
         }
      }
   }

   @Generated
   private RenderShaders() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
}
