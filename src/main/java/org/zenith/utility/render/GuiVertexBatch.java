package org.zenith.utility.render;

import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.IdentityHashMap;
import java.util.Map;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.state.gui.BlitRenderState;
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;
import net.minecraft.client.renderer.RenderPipelines;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector4f;
import org.joml.Vector4fc;
import org.zenith.RenderState;

public final class GuiVertexBatch {
   private static final Map<Object, float[]> items = new IdentityHashMap<>();
   private static int intValue;

   private GuiVertexBatch() {
   }

   @Nullable
   public static float[] method97() {
      return RenderState.method1886() ? null : RenderState.method1885();
   }

   public static void method98(Object var0) {
      float[] var1 = method97();
      if (var1 != null) {
         items.put(var0, var1);
      }
   }

   public static BlitRenderState method99(BlitRenderState var0, @Nullable Object var1) {
      float[] var2 = var1 == null ? null : items.get(var1);
      return var2 == null
         ? var0
         : new BlitRenderState(
            var0.pipeline(),
            var0.textureSetup(),
            var0.pose(),
            var0.x0(),
            var0.y0(),
            var0.x1(),
            var0.y1(),
            var0.u0(),
            var0.u1(),
            var0.v0(),
            var0.v1(),
            method100(var0.color(), var2, var0.pipeline() == RenderPipelines.GUI_TEXTURED_PREMULTIPLIED_ALPHA),
            var0.scissorArea(),
            var0.bounds()
         );
   }

   public static void clear() {
      items.clear();
   }

   public static int method100(int var0, float[] var1, boolean var2) {
      float var3 = var1[3];
      float var4 = var2 ? var3 : 1.0F;
      int var5 = Math.round((var0 >>> 24 & 0xFF) * var3);
      int var6 = Math.round((var0 >> 16 & 0xFF) * var1[0] * var4);
      int var7 = Math.round((var0 >> 8 & 0xFF) * var1[1] * var4);
      int var8 = Math.round((var0 & 0xFF) * var1[2] * var4);
      return method101(var5) << 24 | method101(var6) << 16 | method101(var7) << 8 | method101(var8);
   }

   private static int method101(int var0) {
      return Math.max(0, Math.min(255, var0));
   }

   public static GuiElementRenderState method102(GuiElementRenderState var0) {
      float[] var1 = method97();
      return (GuiElementRenderState)(var1 != null && !(var0 instanceof GuiVertexBatch.Data) ? new GuiVertexBatch.Data(var0, (float[])var1.clone()) : var0);
   }

   public static void method103() {
      intValue++;
   }

   public static void method104() {
      if (intValue > 0) {
         intValue--;
      }
   }

   public static Vector4fc method105(Vector4fc var0) {
      return (Vector4fc)(intValue <= 0 && !RenderState.method1886()
         ? new Vector4f(var0).mul(RenderState.getFloatValue(), RenderState.getFloatValue2(), RenderState.getFloatValue3(), RenderState.getAlpha())
         : var0);
   }

   private static final class Data implements GuiElementRenderState {
      private final GuiElementRenderState simpleGuiElementRenderState;
      private final float[] floatValues;

      public GuiElementRenderState simpleGuiElementRenderState() {
         return this.simpleGuiElementRenderState;
      }

      public float[] floatValues() {
         return this.floatValues;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.simpleGuiElementRenderState, other.simpleGuiElementRenderState) && java.util.Objects.equals(this.floatValues, other.floatValues);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(simpleGuiElementRenderState, floatValues);
      }

      @Override
      public String toString() {
         return "Data[" + "simpleGuiElementRenderState=" + this.simpleGuiElementRenderState + ", " + "floatValues=" + this.floatValues + "]";
      }

      private Data(GuiElementRenderState var1, float[] var2) {
         this.simpleGuiElementRenderState = var1;
         this.floatValues = var2;
      }

      @Override
      public void buildVertices(VertexConsumer vertices) {
         this.simpleGuiElementRenderState
            .buildVertices(
               new GuiVertexBatch.Data2(
                  vertices, this.floatValues, this.simpleGuiElementRenderState.pipeline() == RenderPipelines.GUI_TEXTURED_PREMULTIPLIED_ALPHA
               )
            );
      }

      @Override
      public RenderPipeline pipeline() {
         return this.simpleGuiElementRenderState.pipeline();
      }

      @Override
      public TextureSetup textureSetup() {
         return this.simpleGuiElementRenderState.textureSetup();
      }

      @Nullable
      @Override
      public ScreenRectangle scissorArea() {
         return this.simpleGuiElementRenderState.scissorArea();
      }

      @Nullable
      @Override
      public ScreenRectangle bounds() {
         return this.simpleGuiElementRenderState.bounds();
      }

      public GuiElementRenderState getSimpleGuiElementRenderState() {
         return this.simpleGuiElementRenderState;
      }

      public float[] getFloatValues() {
         return this.floatValues;
      }
   }

   private static final class Data2 implements VertexConsumer {
      private final VertexConsumer vertexConsumer;
      private final float[] floatValues;
      private final boolean flag;

      public VertexConsumer vertexConsumer() {
         return this.vertexConsumer;
      }

      public float[] floatValues() {
         return this.floatValues;
      }

      public boolean flag() {
         return this.flag;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data2 other)) return false;
         return java.util.Objects.equals(this.vertexConsumer, other.vertexConsumer) && java.util.Objects.equals(this.floatValues, other.floatValues) && java.util.Objects.equals(this.flag, other.flag);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(vertexConsumer, floatValues, flag);
      }

      @Override
      public String toString() {
         return "Data2[" + "vertexConsumer=" + this.vertexConsumer + ", " + "floatValues=" + this.floatValues + ", " + "flag=" + this.flag + "]";
      }

      private Data2(VertexConsumer var1, float[] var2, boolean var3) {
         this.vertexConsumer = var1;
         this.floatValues = var2;
         this.flag = var3;
      }

      @Override
      public VertexConsumer addVertex(float x, float y, float z) {
         this.vertexConsumer.addVertex(x, y, z);
         return this;
      }

      @Override
      public VertexConsumer setColor(int red, int green, int blue, int alpha) {
         return this.setColor(alpha << 24 | red << 16 | green << 8 | blue);
      }

      @Override
      public VertexConsumer setColor(int argb) {
         this.vertexConsumer.setColor(GuiVertexBatch.method100(argb, this.floatValues, this.flag));
         return this;
      }

      @Override
      public VertexConsumer setUv(float u, float v) {
         this.vertexConsumer.setUv(u, v);
         return this;
      }

      @Override
      public VertexConsumer setUv1(int u, int v) {
         this.vertexConsumer.setUv1(u, v);
         return this;
      }

      @Override
      public VertexConsumer setUv2(int u, int v) {
         this.vertexConsumer.setUv2(u, v);
         return this;
      }

      @Override
      public VertexConsumer setUv3(float u, float v) {
         this.vertexConsumer.setUv3(u, v);
         return this;
      }

      @Override
      public VertexConsumer setNormal(float x, float y, float z) {
         this.vertexConsumer.setNormal(x, y, z);
         return this;
      }

      @Override
      public VertexConsumer setLineWidth(float width) {
         this.vertexConsumer.setLineWidth(width);
         return this;
      }

      public VertexConsumer getVertexConsumer() {
         return this.vertexConsumer;
      }

      public float[] getFloatValues() {
         return this.floatValues;
      }

      public boolean isFlag() {
         return this.flag;
      }
   }
}
