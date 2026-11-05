package org.zenith.utility.render;

import com.mojang.blaze3d.vertex.BufferBuilder;
import org.zenith.utility.render.Tesselator;
import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryUtil;
import org.zenith.RenderState;
import org.zenith.ZenithColor;
import org.zenith.base.font.MsdfRenderer;
import org.zenith.utility.misc.KawaseShaderKey;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class RoundedRectRenderer {
   public static final float CONST_floatValue = 0.0F;
   public static final float CONST_floatValue2 = 1.0F;
   public static final float CONST_floatValue3 = 2.0F;
   public static final float CONST_floatValue4 = 3.0F;
   public static final float CONST_floatValue5 = 7.0F;
   private BufferBuilder builder;
   private boolean flag;

   public boolean isFlag() {
      return this.flag;
   }

   public void begin() {
      MsdfRenderer.flushBatch();
      if (!this.flag) {
         KawaseShaderKey kawaseShaderKey3 = RenderShaders.kawaseShaderKey3;
         kawaseShaderKey3.method5165();
         kawaseShaderKey3.method5166("Smoothness").method7413(0.8F, 1.0F);
         RenderState.method1871();
         RenderState.method1873();
         this.builder = Tesselator.getInstance().begin(PrimitiveTopology.QUADS, VertexFormats.vertexFormat);
         this.flag = true;
      }
   }

   public void method4779(Matrix4f matrix4f, float floatValue, float y, float width, float height, ProxyDotRadiusData proxyDotRadiusData, ZenithColor zenithColor) {
      this.method4784(matrix4f, floatValue, y, width, height, proxyDotRadiusData, zenithColor, 0.0F, 0.0F);
   }

   public void method4780(Matrix4f matrix4f, float floatValue, float y, float width, float height, ZenithColor zenithColor) {
      this.method4784(matrix4f, floatValue, y, width, height, ProxyDotRadiusData.INSTANCE, zenithColor, 0.0F, 1.0F);
   }

   public void method4781(Matrix4f matrix4f, float floatValue, float y, float width, float height, float f, ProxyDotRadiusData proxyDotRadiusData, ZenithColor zenithColor) {
      this.method4784(matrix4f, floatValue, y, width, height, proxyDotRadiusData, zenithColor, f, 2.0F);
   }

   public void method4782(Matrix4f matrix4f, float floatValue, float y, float width, float height, float f, ProxyDotRadiusData proxyDotRadiusData, ZenithColor zenithColor, float f2) {
      this.method4784(matrix4f, floatValue, y, width, height, proxyDotRadiusData, zenithColor, f, 3.0F + f2);
   }

   public void method4783(Matrix4f matrix4f, float floatValue, float y, float width, float height, float f, float floatValue2, float f2, ZenithColor zenithColor) {
      ProxyDotRadiusData proxyDotRadiusData = new ProxyDotRadiusData(floatValue2, 0.0F, 0.0F, f2);
      method4785(this.builder, matrix4f, floatValue, y, width, height, proxyDotRadiusData, zenithColor, f, 7.0F);
   }

   private void method4784(Matrix4f matrix4f, float floatValue, float y, float width, float height, ProxyDotRadiusData proxyDotRadiusData, ZenithColor zenithColor, float f, float f2) {
      method4785(this.builder, matrix4f, floatValue, y, width, height, proxyDotRadiusData, zenithColor, f, f2);
   }

   public static void method4785(
      BufferBuilder builder, Matrix4f matrix4f, float floatValue, float y, float width, float height, ProxyDotRadiusData proxyDotRadiusData, ZenithColor zenithColor, float f, float f2
   ) {
      int color = zenithColor.method7287();
      float floatValue2 = floatValue + width;
      float y2 = y + height;
      method4786(builder, matrix4f, floatValue, y, width, height, color, proxyDotRadiusData, f, f2);
      method4786(builder, matrix4f, floatValue, y2, width, height, color, proxyDotRadiusData, f, f2);
      method4786(builder, matrix4f, floatValue2, y2, width, height, color, proxyDotRadiusData, f, f2);
      method4786(builder, matrix4f, floatValue2, y, width, height, color, proxyDotRadiusData, f, f2);
   }

   private static void method4786(
      BufferBuilder builder, Matrix4f matrix4f, float floatValue, float y, float width, float height, int color, ProxyDotRadiusData proxyDotRadiusData, float f, float f2
   ) {
      builder.addVertex(matrix4f, floatValue, y, 0.0F).setColor(color).setUv(width, height);
      long pointer = VertexFormats.pointer(builder, VertexFormats.vertexFormatElement);
      MemoryUtil.memPutFloat(pointer, proxyDotRadiusData.getFloatValue());
      MemoryUtil.memPutFloat(pointer + 4L, proxyDotRadiusData.getFloatValue4());
      MemoryUtil.memPutFloat(pointer + 8L, proxyDotRadiusData.getFloatValue2());
      MemoryUtil.memPutFloat(pointer + 12L, proxyDotRadiusData.getFloatValue3());
      long ptr = VertexFormats.pointer(builder, VertexFormats.vertexFormatElement2);
      MemoryUtil.memPutFloat(ptr, f);
      MemoryUtil.memPutFloat(ptr + 4L, f2);
   }

   public void flush() {
      if (this.flag) {
         ImmediateRenderer.method8015(this.builder.buildOrThrow());
         RenderState.method1872();
         this.builder = null;
         this.flag = false;
      }
   }
}
