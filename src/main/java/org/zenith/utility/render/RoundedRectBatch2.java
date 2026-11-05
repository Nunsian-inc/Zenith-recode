package org.zenith.utility.render;

import com.mojang.blaze3d.vertex.BufferBuilder;
import org.zenith.utility.render.Tesselator;
import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;
import lombok.Generated;
import org.joml.Matrix4f;
import org.zenith.RenderState;
import org.zenith.ZenithColor;
import org.zenith.base.font.MsdfRenderer;
import org.zenith.utility.misc.KawaseShaderKey;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class RoundedRectBatch2 {
   private BufferBuilder builder;
   private float floatValue;
   private float floatValue2;
   private float floatValue3;
   private float floatValue4;
   private float batchSmoothness;
   private boolean flag;

   public void method1534(ProxyDotRadiusData proxyDotRadiusData) {
      this.method1535(proxyDotRadiusData, 0.8F);
   }

   public void method1535(ProxyDotRadiusData proxyDotRadiusData, float batchSmoothness) {
      MsdfRenderer.flushBatch();
      float floatValue = proxyDotRadiusData.getFloatValue();
      float f = proxyDotRadiusData.getFloatValue4();
      float f2 = proxyDotRadiusData.getFloatValue2();
      float f3 = proxyDotRadiusData.getFloatValue3();
      if (this.flag) {
         if (floatValue == this.floatValue && f == this.floatValue2 && f2 == this.floatValue3 && f3 == this.floatValue4 && batchSmoothness == this.batchSmoothness) {
            return;
         }

         this.flush();
      }

      this.floatValue = floatValue;
      this.floatValue2 = f;
      this.floatValue3 = f2;
      this.floatValue4 = f3;
      this.batchSmoothness = batchSmoothness;
      KawaseShaderKey kawaseShaderKey2 = RenderShaders.kawaseShaderKey2;
      kawaseShaderKey2.method5165();
      kawaseShaderKey2.method5166("Radius").method7415(floatValue, f, f2, f3);
      kawaseShaderKey2.method5166("Smoothness").method7412(batchSmoothness);
      RenderState.method1871();
      RenderState.method1873();
      this.builder = Tesselator.getInstance().begin(PrimitiveTopology.QUADS, com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_TEX_COLOR);
      this.flag = true;
   }

   public void method1536(Matrix4f pose, float floatValue, float animationPosY, float width, float height, ZenithColor zenithColor) {
      int color = zenithColor.method7287();
      this.builder.addVertex(pose, floatValue, animationPosY, 0.0F).setUv(width, height).setColor(color);
      this.builder.addVertex(pose, floatValue, animationPosY + height, 0.0F).setUv(width, height).setColor(color);
      this.builder.addVertex(pose, floatValue + width, animationPosY + height, 0.0F).setUv(width, height).setColor(color);
      this.builder.addVertex(pose, floatValue + width, animationPosY, 0.0F).setUv(width, height).setColor(color);
   }

   public void flush() {
      if (this.flag) {
         ImmediateRenderer.method8015(this.builder.buildOrThrow());
         RenderState.method1872();
         this.builder = null;
         this.flag = false;
      }
   }

   @Generated
   public boolean isFlag() {
      return this.flag;
   }
}
