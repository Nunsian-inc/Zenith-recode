package org.zenith.utility.render;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import org.zenith.utility.render.Tesselator;
import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.joml.Matrix4f;
import org.zenith.RenderState;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.MsdfFont;
import org.zenith.base.font.MsdfRenderer;
import org.zenith.utility.misc.KawaseShaderKey;
import org.zenith.utility.render.display.base.GlowColor;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public final class RoundedRectBatch {
   public static boolean enabled = true;
   private static final RoundedRectBatch INSTANCE = new RoundedRectBatch();
   private static RoundedRectBatch INSTANCE2;
   private static final Comparator<RoundedRectBatch.Entry3> comparator = Comparator.comparingInt(entry3 -> entry3.intValue2);
   private final List<RoundedRectBatch.Entry2> items = new ArrayList<>(256);
   private final List<RoundedRectBatch.Entry3> items2 = new ArrayList<>(256);
   private final RoundedRectBatch.Entry<RoundedRectBatch.Entry2> entry = new RoundedRectBatch.Entry<>(
      RoundedRectBatch.Entry2::new, RoundedRectBatch.Entry2::method2957
   );
   private final RoundedRectBatch.Entry<RoundedRectBatch.Entry3> entry2 = new RoundedRectBatch.Entry<>(
      RoundedRectBatch.Entry3::new, RoundedRectBatch.Entry3::method2958
   );
   private final PoseStack matrixStack = new PoseStack();
   private boolean replaying;

   private RoundedRectBatch() {
   }

   public static void begin() {
      if (!enabled) {
         INSTANCE2 = null;
      } else {
         INSTANCE.method2955();
         INSTANCE.replaying = false;
         INSTANCE2 = INSTANCE;
      }
   }

   public static void method2933() {
      RoundedRectBatch roundedRectBatch = INSTANCE2;
      INSTANCE2 = null;
      if (roundedRectBatch != null) {
         try {
            roundedRectBatch.method2945();
         } finally {
            roundedRectBatch.method2955();
         }
      }
   }

   public static boolean method2934() {
      RoundedRectBatch roundedRectBatch = INSTANCE2;
      return roundedRectBatch != null && !roundedRectBatch.replaying;
   }

   public static void method2935() {
      RoundedRectBatch roundedRectBatch = INSTANCE2;
      if (roundedRectBatch != null && !roundedRectBatch.replaying && (!roundedRectBatch.items.isEmpty() || !roundedRectBatch.items2.isEmpty())) {
         roundedRectBatch.method2945();
      }
   }

   public static void method2936(Matrix4f matrix, float floatValue, float y, float width, float height, ZenithColor zenithColor) {
      RoundedRectBatch.Entry2 entry2 = INSTANCE2.entry.method2956();
      entry2.type = RoundedRectBatch.Type.RECT;
      entry2.matrix4f.set(matrix);
      entry2.floatValue = floatValue;
      entry2.floatValue2 = y;
      entry2.width = width;
      entry2.height = height;
      entry2.zenithColor = zenithColor;
      INSTANCE2.items.add(entry2);
   }

   public static void method2937(Matrix4f matrix, float floatValue, float y, float width, float height, ProxyDotRadiusData proxyDotRadiusData, ZenithColor zenithColor) {
      RoundedRectBatch.Entry2 entry2 = INSTANCE2.entry.method2956();
      entry2.type = RoundedRectBatch.Type.BATCH_ROUNDED;
      entry2.matrix4f.set(matrix);
      entry2.floatValue = floatValue;
      entry2.floatValue2 = y;
      entry2.width = width;
      entry2.height = height;
      entry2.floatValue3 = proxyDotRadiusData.getFloatValue();
      entry2.floatValue4 = proxyDotRadiusData.getFloatValue4();
      entry2.floatValue5 = proxyDotRadiusData.getFloatValue2();
      entry2.floatValue6 = proxyDotRadiusData.getFloatValue3();
      entry2.intValue = zenithColor.method7287();
      INSTANCE2.items.add(entry2);
   }

   public static void method2938(Matrix4f matrix, float floatValue, float y, float width, float height, ProxyDotRadiusData proxyDotRadiusData, ZenithColor zenithColor) {
      if (proxyDotRadiusData.getFloatValue() == proxyDotRadiusData.getFloatValue4() && proxyDotRadiusData.getFloatValue() == proxyDotRadiusData.getFloatValue2() && proxyDotRadiusData.getFloatValue() == proxyDotRadiusData.getFloatValue3()) {
         method2937(matrix, floatValue, y, width, height, proxyDotRadiusData, zenithColor);
      } else {
         RoundedRectBatch.Entry2 entry2 = INSTANCE2.entry.method2956();
         entry2.type = RoundedRectBatch.Type.SINGLE_ROUNDED;
         entry2.matrix4f.set(matrix);
         entry2.floatValue = floatValue;
         entry2.floatValue2 = y;
         entry2.width = width;
         entry2.height = height;
         entry2.proxyDotRadiusData = proxyDotRadiusData;
         entry2.zenithColor = zenithColor;
         INSTANCE2.items.add(entry2);
      }
   }

   public static void method2939(Matrix4f matrix, float floatValue, float y, float width, float height, ProxyDotRadiusData proxyDotRadiusData, GlowColor glowColor) {
      RoundedRectBatch.Entry2 entry2 = INSTANCE2.entry.method2956();
      entry2.type = RoundedRectBatch.Type.SINGLE_GRADIENT;
      entry2.matrix4f.set(matrix);
      entry2.floatValue = floatValue;
      entry2.floatValue2 = y;
      entry2.width = width;
      entry2.height = height;
      entry2.proxyDotRadiusData = proxyDotRadiusData;
      entry2.zenithColor = glowColor.getZenithColor();
      entry2.zenithColor2 = glowColor.getZenithColor2();
      entry2.zenithColor3 = glowColor.getZenithColor4();
      entry2.zenithColor4 = glowColor.getZenithColor3();
      INSTANCE2.items.add(entry2);
   }

   public static void method2940(
      Matrix4f matrix,
      float floatValue,
      float lastY,
      float width,
      float height,
      float batchRange,
      ProxyDotRadiusData proxyDotRadiusData,
      ZenithColor zenithColor,
      ZenithColor zenithColor2,
      ZenithColor zenithColor3,
      ZenithColor zenithColor4
   ) {
      RoundedRectBatch.Entry2 entry2 = INSTANCE2.entry.method2956();
      entry2.type = RoundedRectBatch.Type.BORDER;
      entry2.matrix4f.set(matrix);
      entry2.floatValue = floatValue;
      entry2.floatValue2 = lastY;
      entry2.width = width;
      entry2.height = height;
      entry2.floatValue7 = batchRange;
      entry2.proxyDotRadiusData = proxyDotRadiusData;
      entry2.zenithColor = zenithColor;
      entry2.zenithColor2 = zenithColor2;
      entry2.zenithColor3 = zenithColor3;
      entry2.zenithColor4 = zenithColor4;
      INSTANCE2.items.add(entry2);
   }

   public static void method2941(Font font, String text, Matrix4f matrix, float floatValue, float centeredTextY, int intValue) {
      if (text != null && !text.isEmpty()) {
         RoundedRectBatch.Entry3 entry3 = method2944(font, text, matrix, floatValue, centeredTextY);
         entry3.type2 = RoundedRectBatch.Type2.PLAIN;
         entry3.intValue = intValue;
         INSTANCE2.items2.add(entry3);
      }
   }

   public static void method2942(Font font, String name, Matrix4f matrix, float floatValue, float y, int intValue, boolean flag, float f, float f2, float maxWidth) {
      if (name != null && !name.isEmpty()) {
         RoundedRectBatch.Entry3 entry3 = method2944(font, name, matrix, floatValue, y);
         entry3.type2 = RoundedRectBatch.Type2.FADEOUT;
         entry3.intValue = intValue;
         entry3.flag = flag;
         entry3.floatValue3 = f;
         entry3.floatValue4 = f2;
         entry3.maxWidth = maxWidth;
         INSTANCE2.items2.add(entry3);
      }
   }

   public static void method2943(Font font, String text, Matrix4f matrix, float floatValue, float centeredTextY, GlowColor glowColor) {
      if (text != null && !text.isEmpty()) {
         RoundedRectBatch.Entry3 entry3 = method2944(font, text, matrix, floatValue, centeredTextY);
         entry3.type2 = RoundedRectBatch.Type2.GRADIENT;
         entry3.glowColor = glowColor;
         INSTANCE2.items2.add(entry3);
      }
   }

   private static RoundedRectBatch.Entry3 method2944(Font font, String text, Matrix4f matrix, float floatValue, float centeredTextY) {
      RoundedRectBatch.Entry3 entry3 = INSTANCE2.entry2.method2956();
      entry3.font = font.getFont();
      entry3.size = font.getSize();
      entry3.text = text;
      entry3.matrix4f.set(matrix);
      entry3.floatValue = floatValue;
      entry3.floatValue2 = centeredTextY;
      entry3.intValue2 = entry3.font.getTextureId();
      return entry3;
   }

   private void method2945() {
      if (!this.items.isEmpty() || !this.items2.isEmpty()) {
         this.replaying = true;

         try {
            this.method2946();
            this.method2954();
         } finally {
            this.replaying = false;
            this.method2955();
         }
      }
   }

   private void method2946() {
      if (!this.items.isEmpty()) {
         BufferBuilder bufferBuilder = null;
         BufferBuilder bufferBuilder2 = null;
         float f = 0.0F;
         float f2 = 0.0F;
         float f3 = 0.0F;
         float f4 = 0.0F;

         for (int i = 0; i < this.items.size(); i++) {
            RoundedRectBatch.Entry2 entry2 = this.items.get(i);
            if (entry2.type == RoundedRectBatch.Type.RECT) {
               bufferBuilder = this.method2951(bufferBuilder);
               if (bufferBuilder2 == null) {
                  bufferBuilder2 = this.method2947();
               }

               this.method2949(bufferBuilder2, entry2);
            } else {
               bufferBuilder2 = this.method2948(bufferBuilder2);
               if (entry2.type == RoundedRectBatch.Type.BATCH_ROUNDED) {
                  if (bufferBuilder == null || entry2.floatValue3 != f || entry2.floatValue4 != f2 || entry2.floatValue5 != f3 || entry2.floatValue6 != f4) {
                     bufferBuilder = this.method2951(bufferBuilder);
                     bufferBuilder = this.method2950(entry2);
                     f = entry2.floatValue3;
                     f2 = entry2.floatValue4;
                     f3 = entry2.floatValue5;
                     f4 = entry2.floatValue6;
                  }

                  this.method2952(bufferBuilder, entry2);
               } else {
                  bufferBuilder = this.method2951(bufferBuilder);
                  this.method2953(entry2);
               }
            }
         }

         this.method2951(bufferBuilder);
         this.method2948(bufferBuilder2);
      }
   }

   private BufferBuilder method2947() {
      RenderState.method1898("core/position_color");
      RenderState.method1871();
      RenderState.method1873();
      return Tesselator.getInstance().begin(PrimitiveTopology.QUADS, com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_COLOR);
   }

   private BufferBuilder method2948(BufferBuilder bufferBuilder) {
      if (bufferBuilder != null) {
         ImmediateRenderer.method8015(bufferBuilder.buildOrThrow());
         RenderState.method1872();
      }

      return null;
   }

   private void method2949(BufferBuilder bufferBuilder, RoundedRectBatch.Entry2 entry2) {
      int color = entry2.zenithColor.method7287();
      float floatValue = entry2.floatValue;
      float y = entry2.floatValue2;
      float width = entry2.width;
      float height = entry2.height;
      bufferBuilder.addVertex(entry2.matrix4f, floatValue, y + height, 0.0F).setColor(color);
      bufferBuilder.addVertex(entry2.matrix4f, floatValue + width, y + height, 0.0F).setColor(color);
      bufferBuilder.addVertex(entry2.matrix4f, floatValue + width, y, 0.0F).setColor(color);
      bufferBuilder.addVertex(entry2.matrix4f, floatValue, y, 0.0F).setColor(color);
   }

   private BufferBuilder method2950(RoundedRectBatch.Entry2 entry2) {
      KawaseShaderKey kawaseShaderKey2 = RenderShaders.kawaseShaderKey2;
      kawaseShaderKey2.method5165();
      kawaseShaderKey2.method5166("Radius").method7415(entry2.floatValue3, entry2.floatValue4, entry2.floatValue5, entry2.floatValue6);
      kawaseShaderKey2.method5166("Smoothness").method7412(0.8F);
      RenderState.method1871();
      RenderState.method1873();
      return Tesselator.getInstance().begin(PrimitiveTopology.QUADS, com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_TEX_COLOR);
   }

   private BufferBuilder method2951(BufferBuilder bufferBuilder) {
      if (bufferBuilder != null) {
         ImmediateRenderer.method8015(bufferBuilder.buildOrThrow());
         RenderState.method1872();
      }

      return null;
   }

   private void method2952(BufferBuilder bufferBuilder, RoundedRectBatch.Entry2 entry2) {
      float floatValue = entry2.floatValue;
      float y = entry2.floatValue2;
      float width = entry2.width;
      float height = entry2.height;
      int intValue = entry2.intValue;
      bufferBuilder.addVertex(entry2.matrix4f, floatValue, y, 0.0F).setUv(entry2.width, entry2.height).setColor(intValue);
      bufferBuilder.addVertex(entry2.matrix4f, floatValue, y + height, 0.0F).setUv(entry2.width, entry2.height).setColor(intValue);
      bufferBuilder.addVertex(entry2.matrix4f, floatValue + width, y + height, 0.0F).setUv(entry2.width, entry2.height).setColor(intValue);
      bufferBuilder.addVertex(entry2.matrix4f, floatValue + width, y, 0.0F).setUv(entry2.width, entry2.height).setColor(intValue);
   }

   private void method2953(RoundedRectBatch.Entry2 entry2) {
      this.matrixStack.last().pose().set(entry2.matrix4f);
      switch (entry2.type) {
         case RECT:
            RenderShaders.method5260(this.matrixStack.last().pose(), entry2.floatValue, entry2.floatValue2, entry2.width, entry2.height, entry2.zenithColor);
         case BATCH_ROUNDED:
         default:
            break;
         case SINGLE_ROUNDED:
            RenderShaders.method5268(
               this.matrixStack.last().pose(),
               entry2.floatValue,
               entry2.floatValue2,
               entry2.width,
               entry2.height,
               entry2.proxyDotRadiusData,
               entry2.zenithColor
            );
            break;
         case SINGLE_GRADIENT:
            RenderShaders.method5269(
               this.matrixStack.last().pose(),
               entry2.floatValue,
               entry2.floatValue2,
               entry2.width,
               entry2.height,
               entry2.proxyDotRadiusData,
               entry2.zenithColor,
               entry2.zenithColor2,
               entry2.zenithColor3,
               entry2.zenithColor4
            );
            break;
         case BORDER:
            RenderShaders.method5273(
               this.matrixStack.last().pose(),
               entry2.floatValue,
               entry2.floatValue2,
               entry2.width,
               entry2.height,
               entry2.floatValue7,
               entry2.proxyDotRadiusData,
               entry2.zenithColor,
               entry2.zenithColor2,
               entry2.zenithColor3,
               entry2.zenithColor4
            );
      }
   }

   private void method2954() {
      if (!this.items2.isEmpty()) {
         this.items2.sort(comparator);

         for (int i = 0; i < this.items2.size(); i++) {
            RoundedRectBatch.Entry3 entry3 = this.items2.get(i);
            switch (entry3.type2) {
               case PLAIN:
                  MsdfRenderer.renderText(entry3.font, entry3.text, entry3.size, entry3.intValue, entry3.matrix4f, entry3.floatValue, entry3.floatValue2, 0.0F);
                  break;
               case FADEOUT:
                  MsdfRenderer.renderText(
                     entry3.font,
                     entry3.text,
                     entry3.size,
                     entry3.intValue,
                     entry3.matrix4f,
                     entry3.floatValue,
                     entry3.floatValue2,
                     0.0F,
                     entry3.flag,
                     entry3.floatValue3,
                     entry3.floatValue4,
                     entry3.maxWidth
                  );
                  break;
               case GRADIENT:
                  MsdfRenderer.renderText(entry3.font, entry3.text, entry3.size, entry3.glowColor, entry3.matrix4f, entry3.floatValue, entry3.floatValue2, 0.0F);
            }
         }
      }

      MsdfRenderer.flushBatch();
   }

   private void method2955() {
      this.items.clear();
      this.items2.clear();
      this.entry.reset();
      this.entry2.reset();
   }

   private static final class Entry<T> {
      private final ArrayList<T> arrayList = new ArrayList<>();
      private final Supplier<T> supplier;
      private final Consumer<T> consumer;
      private int intValue;

      public Entry(Supplier<T> supplier, Consumer<T> consumer) {
         this.supplier = supplier;
         this.consumer = consumer;
      }

      public T method2956() {
         if (this.intValue < this.arrayList.size()) {
            return this.arrayList.get(this.intValue++);
         } else {
            Object obj = this.supplier.get();
            this.arrayList.add((T)obj);
            this.intValue++;
            return (T)obj;
         }
      }

      public void reset() {
         for (int i = 0; i < this.intValue; i++) {
            this.consumer.accept(this.arrayList.get(i));
         }

         this.intValue = 0;
      }
   }

   private static final class Entry2 {
      public final Matrix4f matrix4f = new Matrix4f();
      public RoundedRectBatch.Type type;
      public float floatValue;
      public float floatValue2;
      public float width;
      public float height;
      public float floatValue3;
      public float floatValue4;
      public float floatValue5;
      public float floatValue6;
      public float floatValue7;
      public int intValue;
      public ProxyDotRadiusData proxyDotRadiusData;
      public ZenithColor zenithColor;
      public ZenithColor zenithColor2;
      public ZenithColor zenithColor3;
      public ZenithColor zenithColor4;

      public void method2957() {
         this.proxyDotRadiusData = null;
         this.zenithColor = null;
         this.zenithColor2 = null;
         this.zenithColor3 = null;
         this.zenithColor4 = null;
      }
   }

   private static final class Entry3 {
      public final Matrix4f matrix4f = new Matrix4f();
      public RoundedRectBatch.Type2 type2;
      public MsdfFont font;
      public String text;
      public float size;
      public float floatValue;
      public float floatValue2;
      public int intValue;
      public int intValue2;
      public boolean flag;
      public float floatValue3;
      public float floatValue4;
      public float maxWidth;
      public GlowColor glowColor;

      public void method2958() {
         this.font = null;
         this.text = null;
         this.glowColor = null;
      }
   }

   private static enum Type {
      RECT,
      BATCH_ROUNDED,
      SINGLE_ROUNDED,
      SINGLE_GRADIENT,
      BORDER;
   }

   private static enum Type2 {
      PLAIN,
      FADEOUT,
      GRADIENT;
   }
}
