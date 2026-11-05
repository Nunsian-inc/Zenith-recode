package org.zenith.utility.render;

import com.mojang.renderpearl.api.pipeline.BlendFactor;
import com.mojang.blaze3d.vertex.BufferBuilder;
import org.zenith.utility.render.Tesselator;
import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.resources.Identifier;
import org.joml.Matrix4f;
import org.zenith.RenderState;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.MsdfRenderer;
import org.zenith.client.modules.impl.render.InterfaceModule;
import org.zenith.utility.misc.HudBatchState;
import org.zenith.utility.misc.HudQueuedDraw;
import org.zenith.utility.misc.KawaseShaderKey;
import org.zenith.utility.render.display.base.HudQueuedContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public final class Jebriksheet {
   public static boolean flag = true;
   private static final Comparator<Jebriksheet.Handler2> comparator = Comparator.comparingInt(Jebriksheet::method2384);
   private static Jebriksheet INSTANCE;
   private static final ZenithColor zenithColor = new ZenithColor(255, 255, 255, 18);
   private static final Identifier identifier = Zenith.method1501("menu/jebriksheet.png");
   private static final float CONST_floatValue = 1024.0F;
   private final List<Jebriksheet.Entry3> items = new ArrayList<>(64);
   private final List<Jebriksheet.Handler> items2 = new ArrayList<>(192);
   private final List<Jebriksheet.Handler2> items3 = new ArrayList<>(256);
   private final List<Jebriksheet.Handler2> items4 = new ArrayList<>(128);
   private final List<Jebriksheet.Handler2> items5 = new ArrayList<>(128);
   private final List<Jebriksheet.Handler2> items6 = new ArrayList<>(64);
   private final List<KawaseBlur.Data> items7 = new ArrayList<>(64);
   private final List<KawaseBlur.Data2> items8 = new ArrayList<>(64);
   private static final float CONST_floatValue2 = 10.0F;
   private static final float CONST_floatValue3 = 0.08F;
   private final RoundedRectRenderer roundedRectRenderer = new RoundedRectRenderer();
   private final RoundedRectBatch2 roundedRectBatch2 = new RoundedRectBatch2();
   private final RectBatch rectBatch = new RectBatch();
   private final Matrix4f matrix4f = new Matrix4f();
   private boolean flag2;
   private final Jebriksheet.Entry<HudQueuedDraw> entry = new Jebriksheet.Entry<>(HudQueuedDraw::new, HudQueuedDraw::method2487);
   private final Jebriksheet.Entry<Jebriksheet.Entry4> entry2 = new Jebriksheet.Entry<>(Jebriksheet.Entry4::new, Jebriksheet.Entry4::method2388);
   private final Jebriksheet.Entry<Jebriksheet.Entry5> entry3 = new Jebriksheet.Entry<>(Jebriksheet.Entry5::new, Jebriksheet.Entry5::method2389);
   private final Jebriksheet.Entry<Jebriksheet.Entry3> entry4 = new Jebriksheet.Entry<>(Jebriksheet.Entry3::new, Jebriksheet.Entry3::method2387);
   private final Jebriksheet.Entry<Jebriksheet.Entry2> entry5 = new Jebriksheet.Entry<>(Jebriksheet.Entry2::new, Jebriksheet.Entry2::method2386);
   private final List<Jebriksheet.Entry2> items9 = new ArrayList<>(8);
   private final List<Jebriksheet.Entry2> items10 = new ArrayList<>(8);
   private final List<Jebriksheet.Entry2> items11 = new ArrayList<>(8);
   private final List<Jebriksheet.Entry2> items12 = new ArrayList<>(8);
   private final List<Jebriksheet.Entry2> items13 = new ArrayList<>(8);
   private float floatValue = Float.NaN;
   private ProxyDotRadiusData proxyDotRadiusData = ProxyDotRadiusData.INSTANCE;

   public static void method2346(Jebriksheet jebriksheet) {
      jebriksheet.clear();
      INSTANCE = jebriksheet;
   }

   public static void method2347(Jebriksheet jebriksheet) {
      if (INSTANCE == jebriksheet) {
         INSTANCE = null;
         jebriksheet.clear();
      }
   }

   public static boolean method2348() {
      return INSTANCE != null && !INSTANCE.flag2;
   }

   public static void method2349(Runnable runnable) {
      Jebriksheet jebriksheet = method2366();
      jebriksheet.method2368(jebriksheet.items3, jebriksheet.items11);
      jebriksheet.items3.add(new Jebriksheet.Data(runnable));
   }

   public static void method2350(Matrix4f mat, Consumer<Matrix4f> consumer) {
      Matrix4f matrix = new Matrix4f(mat);
      Jebriksheet jebriksheet = method2366();
      jebriksheet.method2368(jebriksheet.items3, jebriksheet.items11);
      jebriksheet.items3.add(() -> consumer.accept(new Matrix4f(matrix)));
   }

   public static void method2351(Runnable runnable) {
      Jebriksheet jebriksheet = method2366();
      jebriksheet.method2368(jebriksheet.items4, jebriksheet.items12);
      jebriksheet.items4.add(new Jebriksheet.Data(runnable));
   }

   public static void method2352(Runnable runnable) {
      Jebriksheet jebriksheet = method2366();
      jebriksheet.method2368(jebriksheet.items5, jebriksheet.items13);
      jebriksheet.items5.add(new Jebriksheet.Data(runnable));
   }

   public static void method2353(Runnable runnable) {
      Jebriksheet jebriksheet = method2366();
      jebriksheet.method2368(jebriksheet.items2, jebriksheet.items10);
      jebriksheet.items2.add(new Jebriksheet.Data(runnable));
   }

   public static void method2354(Matrix4f mat, Consumer<Matrix4f> consumer) {
      Matrix4f matrix = new Matrix4f(mat);
      Jebriksheet jebriksheet = method2366();
      jebriksheet.method2368(jebriksheet.items2, jebriksheet.items10);
      jebriksheet.items2.add(new Jebriksheet.Data(() -> consumer.accept(new Matrix4f(matrix))));
   }

   public static HudQueuedDraw method2355(HudQueuedContext hudQueuedContext) {
      HudQueuedDraw hudQueuedDraw = method2366().entry.method2385();
      hudQueuedDraw.hudQueuedContext = hudQueuedContext;
      return hudQueuedDraw;
   }

   public static void method2356(HudQueuedDraw queueCommand) {
      Jebriksheet jebriksheet = method2366();
      jebriksheet.method2368(jebriksheet.items2, jebriksheet.items10);
      jebriksheet.items2.add(queueCommand);
   }

   public static void method2357(HudQueuedDraw queueCommand) {
      Jebriksheet jebriksheet = method2366();
      jebriksheet.method2368(jebriksheet.items3, jebriksheet.items11);
      jebriksheet.items3.add(queueCommand);
   }

   public static void method2358(HudQueuedDraw queueCommand) {
      Jebriksheet jebriksheet = method2366();
      jebriksheet.method2368(jebriksheet.items4, jebriksheet.items12);
      jebriksheet.items4.add(queueCommand);
   }

   public static void method2359(HudQueuedDraw queueCommand) {
      Jebriksheet jebriksheet = method2366();
      jebriksheet.method2368(jebriksheet.items5, jebriksheet.items13);
      queueCommand.intValue10 = method2360(queueCommand);
      jebriksheet.items5.add(queueCommand);
   }

   private static int method2360(HudQueuedDraw queueCommand) {
      if (queueCommand.obj instanceof Font font) {
         return switch (queueCommand.type) {
            case TEXT_FONT_STRING_RGBA, TEXT_FONT_STRING_RGBA_FADEOUT, TEXT_FONT_STRING_INT_FADEOUT, TEXT_FONT_STRING_GRADIENT, TEXT_FONT_TEXT_INT, TEXT_FONT_TEXT -> font.getFont()
               .getTextureId();
            default -> -1;
         };
      } else {
         return -1;
      }
   }

   public static void method2361(HudQueuedContext hudQueuedContext, Matrix4f queueMatrix, int intValue, int y1, int x2, int y2) {
      Jebriksheet jebriksheet = method2366();
      Jebriksheet.Entry2 entry2 = jebriksheet.entry5.method2385();
      entry2.hudQueuedContext = hudQueuedContext;
      entry2.matrix4f.set(queueMatrix);
      entry2.intValue = intValue;
      entry2.intValue2 = y1;
      entry2.intValue3 = x2;
      entry2.intValue4 = y2;
      jebriksheet.items9.add(entry2);
   }

   public static void method2362() {
      Jebriksheet jebriksheet = method2366();
      if (!jebriksheet.items9.isEmpty()) {
         jebriksheet.items9.removeLast();
      }
   }

   public static void method2363(
      Matrix4f queueMatrix, float floatValue, float y, float width, float height, float blurRadius, ProxyDotRadiusData proxyDotRadiusData, ZenithColor zenithColor, boolean flag, boolean flag2
   ) {
      Jebriksheet jebriksheet = method2366();
      Jebriksheet.Entry3 entry3 = jebriksheet.entry4.method2385();
      entry3.matrix4f.set(queueMatrix);
      entry3.floatValue = floatValue;
      entry3.floatValue2 = y;
      entry3.width = width;
      entry3.height = height;
      entry3.blurRadius = blurRadius;
      entry3.proxyDotRadiusData = proxyDotRadiusData;
      entry3.zenithColor = zenithColor;
      entry3.flag = flag;
      entry3.flag2 = flag2;
      entry3.flag3 = InterfaceModule.INSTANCE.method6820();
      entry3.flag4 = InterfaceModule.INSTANCE.method6818();
      entry3.flag5 = InterfaceModule.INSTANCE.method6821();
      entry3.intValue = InterfaceModule.INSTANCE.method6823();
      entry3.proxyDotRadiusData2 = jebriksheet.method2367();
      entry3.floatValues = RenderShaders.floatValues;
      jebriksheet.items.add(entry3);
   }

   public static void method2364(Matrix4f queueMatrix, float floatValue, float y, float width, float height, ProxyDotRadiusData proxyDotRadiusData, ZenithColor zenithColor) {
      Jebriksheet jebriksheet = method2366();
      jebriksheet.method2368(jebriksheet.items2, jebriksheet.items10);
      Jebriksheet.Entry4 entry4 = jebriksheet.entry2.method2385();
      entry4.matrix4f.set(queueMatrix);
      entry4.floatValue = floatValue;
      entry4.floatValue2 = y;
      entry4.width = width;
      entry4.height = height;
      entry4.proxyDotRadiusData = proxyDotRadiusData;
      entry4.zenithColor = zenithColor;
      entry4.floatValues = RenderShaders.floatValues;
      jebriksheet.items2.add(entry4);
   }

   public static void method2365(Matrix4f queueMatrix, float floatValue, float y, float width, float height, ZenithColor zenithColor) {
      Jebriksheet jebriksheet = method2366();
      jebriksheet.method2368(jebriksheet.items2, jebriksheet.items10);
      Jebriksheet.Entry5 entry5 = jebriksheet.entry3.method2385();
      entry5.matrix4f.set(queueMatrix);
      entry5.floatValue = floatValue;
      entry5.floatValue2 = y;
      entry5.width = width;
      entry5.height = height;
      entry5.zenithColor = zenithColor;
      jebriksheet.items2.add(entry5);
   }

   private static Jebriksheet method2366() {
      if (!method2348()) {
         throw new IllegalStateException("HudRenderQueue is not recording");
      } else {
         return INSTANCE;
      }
   }

   private ProxyDotRadiusData method2367() {
      float floatValue = InterfaceModule.method6801();
      if (floatValue != this.floatValue) {
         this.floatValue = floatValue;
         this.proxyDotRadiusData = ProxyDotRadiusData.method8069(floatValue);
      }

      return this.proxyDotRadiusData;
   }

   private void method2368(List<? super HudQueuedDraw> list, List<Jebriksheet.Entry2> entry2s) {
      int size = entry2s.size();
      int n = this.items9.size();
      if (size != 0 || n != 0) {
         int n2 = 0;

         while (n2 < size && n2 < n && entry2s.get(n2) == this.items9.get(n2)) {
            n2++;
         }

         for (int i = size - 1; i >= n2; i--) {
            list.add(this.method2370(((Jebriksheet.Entry2)entry2s.get(i)).hudQueuedContext));
            entry2s.remove(i);
         }

         for (int i = n2; i < n; i++) {
            Jebriksheet.Entry2 entry2 = this.items9.get(i);
            list.add(this.method2369(entry2));
            entry2s.add(entry2);
         }
      }
   }

   private HudQueuedDraw method2369(Jebriksheet.Entry2 entry2) {
      HudQueuedDraw hudQueuedDraw = this.entry.method2385();
      hudQueuedDraw.hudQueuedContext = entry2.hudQueuedContext;
      hudQueuedDraw.type = HudQueuedDraw.Type.SCISSOR_ENABLE;
      hudQueuedDraw.matrix4f.set(entry2.matrix4f);
      hudQueuedDraw.intValue = entry2.intValue;
      hudQueuedDraw.intValue2 = entry2.intValue2;
      hudQueuedDraw.intValue3 = entry2.intValue3;
      hudQueuedDraw.intValue4 = entry2.intValue4;
      return hudQueuedDraw;
   }

   private HudQueuedDraw method2370(HudQueuedContext hudQueuedContext) {
      HudQueuedDraw hudQueuedDraw = this.entry.method2385();
      hudQueuedDraw.hudQueuedContext = hudQueuedContext;
      hudQueuedDraw.type = HudQueuedDraw.Type.SCISSOR_DISABLE;
      hudQueuedDraw.matrix4f.identity();
      return hudQueuedDraw;
   }

   private void method2371() {
      this.method2372(this.items2, this.items10);
      this.method2372(this.items3, this.items11);
      this.method2372(this.items4, this.items12);
      this.method2372(this.items5, this.items13);
   }

   private void method2372(List<? super HudQueuedDraw> list, List<Jebriksheet.Entry2> entry2s) {
      for (int i = entry2s.size() - 1; i >= 0; i--) {
         list.add(this.method2370(((Jebriksheet.Entry2)entry2s.get(i)).hudQueuedContext));
      }

      entry2s.clear();
   }

   public void flush() {
      if (!this.flag2) {
         this.method2371();
         this.flag2 = true;
         HudBatchState.begin();

         try {
            this.method2374(true);
            this.method2375();
            this.method2374(false);
            this.method2376();
            this.method2373();
            this.method2377();
            this.method2378();
            ScissorFlusher.flush();
            this.method2381();
         } finally {
            HudBatchState.end();
            this.flag2 = false;
            this.clear();
         }
      }
   }

   private void method2373() {
      boolean flag = false;
      BufferBuilder begin = null;

      for (int i = 0; i < this.items.size(); i++) {
         Jebriksheet.Entry3 entry3 = this.items.get(i);
         if (entry3.flag3) {
            if (!flag) {
               MsdfRenderer.flushBatch();
               KawaseShaderKey kawaseShaderKey4 = RenderShaders.kawaseShaderKey4;
               kawaseShaderKey4.method5165();
               kawaseShaderKey4.method5166("Smoothness").method7413(0.8F, 1.0F);
               kawaseShaderKey4.method5166("TextureSize").method7412(1024.0F);
               RenderState.method1890(0, identifier);
               RenderState.method1871();
               RenderState.method1875(BlendFactor.SRC_ALPHA, BlendFactor.ONE, BlendFactor.ZERO, BlendFactor.ONE);
               begin = Tesselator.getInstance().begin(PrimitiveTopology.QUADS, VertexFormats.vertexFormat);
               flag = true;
            }

            RoundedRectRenderer.method4785(
               begin, entry3.matrix4f, entry3.floatValue, entry3.floatValue2, entry3.width, entry3.height, entry3.proxyDotRadiusData, zenithColor, 0.0F, 0.0F
            );
         }
      }

      if (flag) {
         ImmediateRenderer.method8015(begin.buildOrThrow());
         RenderState.method1873();
         RenderState.method1872();
         RenderState.method1892(0, 0);
      }
   }

   private void clear() {
      this.items.clear();
      this.items2.clear();
      this.items3.clear();
      this.items4.clear();
      this.items5.clear();
      this.items6.clear();
      this.items7.clear();
      this.items9.clear();
      this.items10.clear();
      this.items11.clear();
      this.items12.clear();
      this.items13.clear();
      this.entry.reset();
      this.entry2.reset();
      this.entry3.reset();
      this.entry4.reset();
      this.entry5.reset();
   }

   private void method2374(boolean flag) {
      for (int i = 0; i < this.items.size(); i++) {
         Jebriksheet.Entry3 entry3 = this.items.get(i);
         if (entry3.flag2 && entry3.flag5 == flag) {
            RenderShaders.method5290(entry3.matrix4f, entry3.floatValue, entry3.floatValue2, entry3.width, entry3.height, entry3.intValue, entry3.proxyDotRadiusData2);
         }
      }
   }

   private void method2375() {
      this.items7.clear();
      this.items8.clear();

      for (int i = 0; i < this.items.size(); i++) {
         Jebriksheet.Entry3 entry3 = this.items.get(i);
         if (entry3.flag) {
            if (entry3.flag4) {
               this.items8
                  .add(
                     new KawaseBlur.Data2(
                        entry3.matrix4f,
                        entry3.floatValue,
                        entry3.floatValue2,
                        entry3.width,
                        entry3.height,
                        entry3.proxyDotRadiusData,
                        entry3.zenithColor,
                        10.0F,
                        0.08F,
                        entry3.floatValues
                     )
                  );
            } else if (entry3.floatValues != null) {
               float[] floatValues = entry3.floatValues;
               this.items7
                  .add(
                     new KawaseBlur.Data(
                        entry3.matrix4f, entry3.floatValue, entry3.floatValue2, entry3.width, floatValues[5] - entry3.floatValue2, entry3.proxyDotRadiusData, entry3.zenithColor
                     )
                  );
               this.items7
                  .add(new KawaseBlur.Data(entry3.matrix4f, floatValues[0], floatValues[1], floatValues[2], floatValues[3], ProxyDotRadiusData.method8069(floatValues[4]), entry3.zenithColor));
            } else {
               this.items7
                  .add(
                     new KawaseBlur.Data(entry3.matrix4f, entry3.floatValue, entry3.floatValue2, entry3.width, entry3.height, entry3.proxyDotRadiusData, entry3.zenithColor)
                  );
            }
         }
      }

      if (!this.items7.isEmpty()) {
         Zenith.getInstance().getKawaseBlur().method7354(this.items7);
      }

      if (!this.items8.isEmpty()) {
         Zenith.getInstance().getKawaseBlur().method7369(this.items8);
      }
   }

   private void method2376() {
      for (int i = 0; i < this.items2.size(); i++) {
         Jebriksheet.Handler handler = this.items2.get(i);
         if (handler instanceof Jebriksheet.Entry4 entry4 && entry4.floatValues != null) {
            this.roundedRectRenderer.flush();
            RenderShaders.floatValues = entry4.floatValues;

            try {
               RenderShaders.method5268(
                  new Matrix4f(entry4.matrix4f), entry4.floatValue, entry4.floatValue2, entry4.width, entry4.height, entry4.proxyDotRadiusData, entry4.zenithColor
               );
            } finally {
               RenderShaders.floatValues = null;
            }
         } else if (handler instanceof Jebriksheet.Entry4 entry42) {
            this.roundedRectRenderer.begin();
            this.roundedRectRenderer
               .method4779(entry42.matrix4f, entry42.floatValue, entry42.floatValue2, entry42.width, entry42.height, entry42.proxyDotRadiusData, entry42.zenithColor);
         } else if (handler instanceof Jebriksheet.Entry5 entry5) {
            this.roundedRectRenderer.begin();
            this.roundedRectRenderer.method4780(entry5.matrix4f, entry5.floatValue, entry5.floatValue2, entry5.width, entry5.height, entry5.zenithColor);
         } else {
            this.roundedRectRenderer.flush();
            if (handler instanceof Jebriksheet.Handler2 handler2) {
               handler2.render();
            }
         }
      }

      this.roundedRectRenderer.flush();
   }

   private void method2377() {
      this.method2379(this.items3);
   }

   private void method2378() {
      this.method2379(this.items4);
   }

   private void method2379(List<Jebriksheet.Handler2> handler2s) {
      for (int i = 0; i < handler2s.size(); i++) {
         Jebriksheet.Handler2 handler2 = (Jebriksheet.Handler2)handler2s.get(i);
         if (!(handler2 instanceof HudQueuedDraw hudQueuedDraw && this.method2380(hudQueuedDraw))) {
            this.roundedRectRenderer.flush();
            handler2.render();
         }
      }

      this.roundedRectRenderer.flush();
   }

   private boolean method2380(HudQueuedDraw hudQueuedDraw) {
      if (hudQueuedDraw.type != HudQueuedDraw.Type.ROUNDED_BORDER_RGBA
         && hudQueuedDraw.type != HudQueuedDraw.Type.ROUNDED_CORNER_ONLY
         && hudQueuedDraw.type != HudQueuedDraw.Type.ARC_BORDER) {
         return false;
      } else if (hudQueuedDraw.type == HudQueuedDraw.Type.ARC_BORDER) {
         this.roundedRectRenderer.begin();
         this.roundedRectRenderer
            .method4783(
               hudQueuedDraw.matrix4f,
               hudQueuedDraw.floatValue,
               hudQueuedDraw.floatValue2,
               hudQueuedDraw.floatValue3,
               hudQueuedDraw.floatValue4,
               hudQueuedDraw.floatValue5,
               hudQueuedDraw.floatValue6,
               hudQueuedDraw.floatValue7,
               (ZenithColor)hudQueuedDraw.obj
            );
         return true;
      } else {
         Matrix4f matrix4f = hudQueuedDraw.matrix4f;
         ProxyDotRadiusData proxyDotRadiusData = (ProxyDotRadiusData)hudQueuedDraw.obj;
         ZenithColor zenithColor = (ZenithColor)hudQueuedDraw.obj2;
         this.roundedRectRenderer.begin();
         if (hudQueuedDraw.type == HudQueuedDraw.Type.ROUNDED_BORDER_RGBA) {
            this.roundedRectRenderer.method4781(matrix4f, hudQueuedDraw.floatValue, hudQueuedDraw.floatValue2, hudQueuedDraw.floatValue3, hudQueuedDraw.floatValue4, hudQueuedDraw.floatValue5, proxyDotRadiusData, zenithColor);
         } else {
            this.roundedRectRenderer
               .method4782(matrix4f, hudQueuedDraw.floatValue, hudQueuedDraw.floatValue2, hudQueuedDraw.floatValue3, hudQueuedDraw.floatValue4, hudQueuedDraw.floatValue5, proxyDotRadiusData, zenithColor, hudQueuedDraw.floatValue6);
         }

         return true;
      }
   }

   private void method2381() {
      for (int i = 0; i < this.items5.size(); i++) {
         Jebriksheet.Handler2 handler2 = this.items5.get(i);
         if (flag && method2383(handler2)) {
            this.items6.add(handler2);
         } else {
            this.method2382();
            handler2.render();
         }
      }

      this.method2382();
   }

   private void method2382() {
      int size = this.items6.size();
      if (size != 0) {
         if (size > 1) {
            this.items6.sort(comparator);
         }

         for (int i = 0; i < size; i++) {
            this.items6.get(i).render();
         }

         this.items6.clear();
      }
   }

   private static boolean method2383(Jebriksheet.Handler2 handler2) {
      return handler2 instanceof HudQueuedDraw hudQueuedDraw && hudQueuedDraw.intValue10 >= 0;
   }

   private static int method2384(Jebriksheet.Handler2 handler2) {
      return ((HudQueuedDraw)handler2).intValue10;
   }

   private static final class Data implements Jebriksheet.Handler2 {
      private final Runnable runnable;

      public Runnable runnable() {
         return this.runnable;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.runnable, other.runnable);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(runnable);
      }

      @Override
      public String toString() {
         return "Data[" + "runnable=" + this.runnable + "]";
      }

      private Data(Runnable runnable) {
         this.runnable = runnable;
      }

      @Override
      public void render() {
         this.runnable.run();
      }

      public Runnable getRunnable() {
         return this.runnable;
      }
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

      public T method2385() {
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

   public static final class Entry2 {
      public final Matrix4f matrix4f = new Matrix4f();
      public int intValue;
      public int intValue2;
      public int intValue3;
      public int intValue4;
      public HudQueuedContext hudQueuedContext;

      public void method2386() {
         this.hudQueuedContext = null;
      }
   }

   private static final class Entry3 {
      public final Matrix4f matrix4f = new Matrix4f();
      public float floatValue;
      public float floatValue2;
      public float width;
      public float height;
      public float blurRadius;
      public ProxyDotRadiusData proxyDotRadiusData;
      public ZenithColor zenithColor;
      public boolean flag;
      public boolean flag2;
      public boolean flag3;
      public boolean flag4;
      public boolean flag5;
      public int intValue;
      public ProxyDotRadiusData proxyDotRadiusData2;
      public float[] floatValues;

      public void method2387() {
         this.proxyDotRadiusData = null;
         this.zenithColor = null;
         this.proxyDotRadiusData2 = null;
         this.floatValues = null;
      }
   }

   private static final class Entry4 implements Jebriksheet.Handler {
      public final Matrix4f matrix4f = new Matrix4f();
      public float floatValue;
      public float floatValue2;
      public float width;
      public float height;
      public ProxyDotRadiusData proxyDotRadiusData;
      public ZenithColor zenithColor;
      public float[] floatValues;

      public void method2388() {
         this.proxyDotRadiusData = null;
         this.zenithColor = null;
         this.floatValues = null;
      }
   }

   private static final class Entry5 implements Jebriksheet.Handler {
      public final Matrix4f matrix4f = new Matrix4f();
      public float floatValue;
      public float floatValue2;
      public float width;
      public float height;
      public ZenithColor zenithColor;

      public void method2389() {
         this.zenithColor = null;
      }
   }

   public interface Handler {
   }

   public interface Handler2 extends Jebriksheet.Handler {
      void render();
   }
}
