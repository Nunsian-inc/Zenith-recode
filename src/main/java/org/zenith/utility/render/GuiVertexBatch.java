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

   public static void method98(Object obj) {
      float[] floats = method97();
      if (floats != null) {
         items.put(obj, floats);
      }
   }

   public static BlitRenderState method99(BlitRenderState blitRenderState, @Nullable Object guiItemRenderState) {
      float[] floatValues = guiItemRenderState == null ? null : items.get(guiItemRenderState);
      return floatValues == null
         ? blitRenderState
         : new BlitRenderState(
            blitRenderState.pipeline(),
            blitRenderState.textureSetup(),
            blitRenderState.pose(),
            blitRenderState.x0(),
            blitRenderState.y0(),
            blitRenderState.x1(),
            blitRenderState.y1(),
            blitRenderState.u0(),
            blitRenderState.u1(),
            blitRenderState.v0(),
            blitRenderState.v1(),
            method100(blitRenderState.color(), floatValues, blitRenderState.pipeline() == RenderPipelines.GUI_TEXTURED_PREMULTIPLIED_ALPHA),
            blitRenderState.scissorArea(),
            blitRenderState.bounds()
         );
   }

   public static void clear() {
      items.clear();
   }

   public static int method100(int color, float[] floatValues, boolean flag) {
      float f = floatValues[3];
      float f2 = flag ? f : 1.0F;
      int n = Math.round((color >>> 24 & 0xFF) * f);
      int n2 = Math.round((color >> 16 & 0xFF) * floatValues[0] * f2);
      int n3 = Math.round((color >> 8 & 0xFF) * floatValues[1] * f2);
      int n4 = Math.round((color & 0xFF) * floatValues[2] * f2);
      return method101(n) << 24 | method101(n2) << 16 | method101(n3) << 8 | method101(n4);
   }

   private static int method101(int n) {
      return Math.max(0, Math.min(255, n));
   }

   public static GuiElementRenderState method102(GuiElementRenderState simpleGuiElementRenderState) {
      float[] floats = method97();
      return (GuiElementRenderState)(floats != null && !(simpleGuiElementRenderState instanceof GuiVertexBatch.Data) ? new GuiVertexBatch.Data(simpleGuiElementRenderState, (float[])floats.clone()) : simpleGuiElementRenderState);
   }

   public static void method103() {
      intValue++;
   }

   public static void method104() {
      if (intValue > 0) {
         intValue--;
      }
   }

   public static Vector4fc method105(Vector4fc vec) {
      return (Vector4fc)(intValue <= 0 && !RenderState.method1886()
         ? new Vector4f(vec).mul(RenderState.getFloatValue(), RenderState.getFloatValue2(), RenderState.getFloatValue3(), RenderState.getAlpha())
         : vec);
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

      private Data(GuiElementRenderState simpleGuiElementRenderState, float[] floatValues) {
         this.simpleGuiElementRenderState = simpleGuiElementRenderState;
         this.floatValues = floatValues;
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

      private Data2(VertexConsumer vertexConsumer, float[] floatValues, boolean flag) {
         this.vertexConsumer = vertexConsumer;
         this.floatValues = floatValues;
         this.flag = flag;
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
