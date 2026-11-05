package org.zenith.utility.render;

import com.mojang.blaze3d.vertex.BufferBuilder;
import org.zenith.utility.render.Tesselator;
import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;
import org.joml.Matrix4f;
import org.zenith.RenderState;
import org.zenith.ZenithColor;
import org.zenith.base.font.MsdfRenderer;

public class RectBatch {
   private BufferBuilder builder;
   private boolean flag;

   public boolean isFlag() {
      return this.flag;
   }

   public void begin() {
      MsdfRenderer.flushBatch();
      if (!this.flag) {
         RenderState.method1898("core/position_color");
         RenderState.method1871();
         RenderState.method1873();
         this.builder = Tesselator.getInstance().begin(PrimitiveTopology.QUADS, com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_COLOR);
         this.flag = true;
      }
   }

   public void method1756(Matrix4f var1, float var2, float var3, float var4, float var5, ZenithColor var6) {
      int var7 = var6.method7287();
      this.builder.addVertex(var1, var2, var3 + var5, 0.0F).setColor(var7);
      this.builder.addVertex(var1, var2 + var4, var3 + var5, 0.0F).setColor(var7);
      this.builder.addVertex(var1, var2 + var4, var3, 0.0F).setColor(var7);
      this.builder.addVertex(var1, var2, var3, 0.0F).setColor(var7);
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
