package org.zenith.base.font;

import com.mojang.renderpearl.api.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;
import java.util.ArrayList;
import java.util.List;
import lombok.Generated;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Font.DisplayMode;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.zenith.RenderState;
import org.zenith.ZenithColor;
import org.zenith.client.modules.impl.misc.NameProtectModule;
import org.zenith.utility.misc.HudBatchState;
import org.zenith.utility.misc.KawaseShaderKey;
import org.zenith.utility.render.ImmediateRenderer;
import org.zenith.utility.render.OutlineVertexProvider;
import org.zenith.utility.render.RoundedRectBatch;
import org.zenith.utility.render.display.base.GlowColor;

public final class MsdfRenderer {
   public static final KawaseShaderKey MSDF_FONT_PROGRAM = new KawaseShaderKey(
      ResourceProvider.getShaderIdentifier("msdf_font/data"), DefaultVertexFormat.POSITION_TEX_COLOR
   );
   private static final int VANILLA_TEXT_LIGHT = 15728880;
   public static final boolean USE_VANILLA_FALLBACK_FOR_TEXT = true;
   private static final int BATCH_INITIAL_BUFFER_SIZE = 65536;
   private static ByteBufferBuilder batchAllocator;
   private static BufferBuilder batchBuilder;
   private static GpuTextureView batchTextureId;
   private static boolean uniformsDirty = true;
   private static float batchRange;
   private static float batchThickness;
   private static float batchSmoothness;
   private static boolean batchFadeout;
   private static float batchFadeoutStart;
   private static float batchFadeoutEnd;
   private static float batchMaxWidth;
   private static float batchTextPosX;
   private static float batchShaderColorR = 1.0F;
   private static float batchShaderColorG = 1.0F;
   private static float batchShaderColorB = 1.0F;
   private static float batchShaderColorA = 1.0F;
   private static float lastUniformRange = Float.NaN;
   private static float lastUniformThickness = Float.NaN;
   private static float lastUniformSmoothness = Float.NaN;
   private static int lastUniformFadeout = -1;
   private static float lastUniformFadeoutStart = Float.NaN;
   private static float lastUniformFadeoutEnd = Float.NaN;
   private static float lastUniformMaxWidth = Float.NaN;
   private static float lastUniformTextPosX = Float.NaN;

   private static VertexConsumer beginBatch(MsdfFont var0, float var1, float var2, boolean var3, float var4, float var5, float var6, float var7) {
      if (!var3) {
         var4 = 0.0F;
         var5 = 0.0F;
         var6 = 0.0F;
         var7 = 0.0F;
      }

      if (HintedText.pending()) {
         HintedText.flush();
      }

      GpuTextureView var8 = var0.getTextureView();
      float var9 = var0.getAtlas().range();
      float[] var10 = RenderState.method1885();
      float var11 = var10[0];
      float var12 = var10[1];
      float var13 = var10[2];
      float var14 = var10[3];
      if (batchBuilder != null
         && (
            batchTextureId != var8
               || batchRange != var9
               || batchThickness != var1
               || batchSmoothness != var2
               || batchFadeout != var3
               || batchFadeoutStart != var4
               || batchFadeoutEnd != var5
               || batchMaxWidth != var6
               || batchTextPosX != var7
               || batchShaderColorR != var11
               || batchShaderColorG != var12
               || batchShaderColorB != var13
               || batchShaderColorA != var14
         )) {
         flushBatch();
      }

      if (batchBuilder == null) {
         if (batchAllocator == null) {
            batchAllocator = new ByteBufferBuilder(65536);
         }

         batchBuilder = new BufferBuilder(batchAllocator, PrimitiveTopology.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
         batchTextureId = var8;
         batchRange = var9;
         batchThickness = var1;
         batchSmoothness = var2;
         batchFadeout = var3;
         batchFadeoutStart = var4;
         batchFadeoutEnd = var5;
         batchMaxWidth = var6;
         batchTextPosX = var7;
         batchShaderColorR = var11;
         batchShaderColorG = var12;
         batchShaderColorB = var13;
         batchShaderColorA = var14;
      }

      return batchBuilder;
   }

   public static void flushBatch() {
      RoundedRectBatch.method2935();
      HudBatchState.method7759();
      HintedText.flush();
      flushMsdfPending();
   }

   public static void flushMsdfPending() {
      if (batchBuilder != null) {
         BufferBuilder var0 = batchBuilder;
         batchBuilder = null;
         MeshData var1 = var0.build();
         if (var1 == null) {
            batchAllocator.clear();
         } else {
            float[] var2 = RenderState.method1885();
            float var3 = var2[0];
            float var4 = var2[1];
            float var5 = var2[2];
            float var6 = var2[3];
            boolean var7 = var3 != batchShaderColorR || var4 != batchShaderColorG || var5 != batchShaderColorB || var6 != batchShaderColorA;
            RenderState.method1871();
            RenderState.method1873();
            RenderState.method1877();
            if (var7) {
               RenderState.method1883(batchShaderColorR, batchShaderColorG, batchShaderColorB, batchShaderColorA);
            }

            RenderState.method1889(0, batchTextureId);
            KawaseShaderKey var8 = MSDF_FONT_PROGRAM.method5165();
            if (uniformsDirty) {
               uniformsDirty = false;
               lastUniformRange = Float.NaN;
               lastUniformThickness = Float.NaN;
               lastUniformSmoothness = Float.NaN;
               lastUniformFadeout = -1;
               lastUniformFadeoutStart = Float.NaN;
               lastUniformFadeoutEnd = Float.NaN;
               lastUniformMaxWidth = Float.NaN;
               lastUniformTextPosX = Float.NaN;
            }

            if (lastUniformRange != batchRange) {
               lastUniformRange = batchRange;
               var8.method5166("Range").method7412(batchRange);
            }

            if (lastUniformThickness != batchThickness) {
               lastUniformThickness = batchThickness;
               var8.method5166("Thickness").method7412(batchThickness);
            }

            if (lastUniformSmoothness != batchSmoothness) {
               lastUniformSmoothness = batchSmoothness;
               var8.method5166("Smoothness").method7412(batchSmoothness);
            }

            int var9 = batchFadeout ? 1 : 0;
            if (lastUniformFadeout != var9) {
               lastUniformFadeout = var9;
               var8.method5166("EnableFadeout").set(var9);
            }

            if (batchFadeout) {
               if (lastUniformFadeoutStart != batchFadeoutStart) {
                  lastUniformFadeoutStart = batchFadeoutStart;
                  var8.method5166("FadeoutStart").method7412(batchFadeoutStart);
               }

               if (lastUniformFadeoutEnd != batchFadeoutEnd) {
                  lastUniformFadeoutEnd = batchFadeoutEnd;
                  var8.method5166("FadeoutEnd").method7412(batchFadeoutEnd);
               }

               if (lastUniformMaxWidth != batchMaxWidth) {
                  lastUniformMaxWidth = batchMaxWidth;
                  var8.method5166("MaxWidth").method7412(batchMaxWidth);
               }

               if (lastUniformTextPosX != batchTextPosX) {
                  lastUniformTextPosX = batchTextPosX;
                  var8.method5166("TextPosX").method7412(batchTextPosX);
               }
            }

            try {
               ImmediateRenderer.method8015(var1);
            } finally {
               RenderState.method1893(0);
               if (var7) {
                  RenderState.method1883(var3, var4, var5, var6);
               }

               RenderState.method1876();
               RenderState.method1872();
               batchAllocator.clear();
            }
         }
      }
   }

   public static void renderText(MsdfFont var0, String var1, float var2, int var3, Matrix4f var4, float var5, float var6, float var7) {
      renderText(var0, var1, var2, var3, var4, var5, var6, var7, false, 0.0F, 1.0F, 0.0F);
   }

   public static void renderText(
      MsdfFont var0, String var1, float var2, int var3, Matrix4f var4, float var5, float var6, float var7, boolean var8, float var9, float var10, float var11
   ) {
      float var12 = 0.0F;
      float var13 = 0.45F;
      float var14 = 0.0F;
      var1 = NameProtectModule.method5149(var1);
      if (var8 || !HintedText.render(var0, var1, var2, var3, var4, var5, var6 + textLineOffset(var0, var2), var7)) {
         float var15 = var5;
         float var16 = var11;
         if (var8 && var11 > 0.0F) {
            Vector4f var17 = new Vector4f(var5, 0.0F, 0.0F, 1.0F);
            Vector4f var18 = new Vector4f(var5 + var11, 0.0F, 0.0F, 1.0F);
            var4.transform(var17);
            var4.transform(var18);
            var15 = var17.x;
            var16 = var18.x - var17.x;
         }

         VertexConsumer var20 = beginBatch(var0, var12, var13, var8, var9, var10, var16, var15);
         float var21 = var6 + textLineOffset(var0, var2);
         var0.applyGlyphs(var4, var20, var1, var2, var12 * 0.5F * var2, var14, var5, var21, var7, var3);
      }
   }

   public static void renderText(
      MsdfFont var0, String var1, float var2, int var3, Matrix4f var4, float var5, float var6, float var7, boolean var8, float var9, float var10
   ) {
      float var11 = var0.getWidth(var1, var2) * 2.0F;
      renderText(var0, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
   }

   public static void renderText(MsdfFont var0, Component var1, float var2, Matrix4f var3, float var4, float var5, float var6) {
      renderTextWithVanillaFallback(var0, var1, var2, var3, var4, var5, var6);
   }

   public static void renderText(
      MsdfFont var0, Component var1, float var2, Matrix4f var3, float var4, float var5, float var6, boolean var7, float var8, float var9, float var10
   ) {
      renderTextWithVanillaFallback(var0, var1, var2, var3, var4, var5, var6);
   }

   public static void renderText(
      MsdfFont var0, Component var1, float var2, Matrix4f var3, float var4, float var5, float var6, boolean var7, float var8, float var9, float var10, int var11
   ) {
      renderTextWithVanillaFallback(var0, var1, var2, var3, var4, var5, var6, var11);
   }

   public static void renderTextWithVanillaFallback(MsdfFont var0, Component var1, float var2, Matrix4f var3, float var4, float var5, float var6) {
      renderTextWithVanillaFallback(var0, var1, var2, var3, var4, var5, var6, ZenithColor.INSTANCE.method7287());
   }

   public static void renderTextWithVanillaFallback(MsdfFont var0, Component var1, float var2, Matrix4f var3, float var4, float var5, float var6, int var7) {
      Minecraft var8 = Minecraft.getInstance();
      renderTextWithVanillaFallback(var0, var1, var2, var8.font, null, var3, var4, var5, var6, var7);
   }

   public static void renderTextWithVanillaFallback(
      MsdfFont font, Component text, float size, Font vanillaFont, Object unused, Matrix4f matrix, float x, float y, float z, int color
   ) {
      float cursor = x;
      List<MsdfRenderer.VanillaRun> vanillaRuns = null;

      for (FormattedTextProcessor.TextSegment segment : FormattedTextProcessor.processText(text, color)) {
         for (FormattedTextProcessor.TextRun run : FormattedTextProcessor.splitRuns(font, segment)) {
            if (run.msdf()) {
               renderText(font, run.text(), size, segment.color(), matrix, cursor, y, z);
               cursor += font.getWidth(run.text(), size);
            } else {
               FormattedCharSequence sequence = run.sequence();
               if (vanillaRuns == null) {
                  vanillaRuns = new ArrayList<>();
               }

               vanillaRuns.add(new MsdfRenderer.VanillaRun(sequence, cursor, segment.color()));
               cursor += vanillaFont.width(sequence) * vanillaTextScale(vanillaFont, size);
            }
         }
      }

      if (vanillaRuns != null) {
         renderVanillaRuns(font, vanillaRuns, size, matrix, y, z);
      }
   }

   public static void renderText(MsdfFont var0, Component var1, float var2, Matrix4f var3, float var4, float var5, float var6, boolean var7, float var8, float var9) {
      renderTextWithVanillaFallback(var0, var1, var2, var3, var4, var5, var6);
   }

   public static void renderText(MsdfFont var0, String var1, float var2, GlowColor var3, Matrix4f var4, float var5, float var6, float var7) {
      renderText(var0, var1, var2, var3, var4, var5, var6, var7, false, 0.0F, 1.0F, 0.0F);
   }

   public static void renderText(
      MsdfFont var0,
      String var1,
      float var2,
      GlowColor var3,
      Matrix4f var4,
      float var5,
      float var6,
      float var7,
      boolean var8,
      float var9,
      float var10,
      float var11
   ) {
      var1 = var1.replace("і", "i").replace("І", "I");
      float var12 = 0.05F;
      float var13 = 0.5F;
      float var14 = 0.0F;
      var1 = NameProtectModule.method5149(var1);
      float var15 = var5;
      float var16 = var11;
      if (var8 && var11 > 0.0F) {
         Vector4f var17 = new Vector4f(var5, 0.0F, 0.0F, 1.0F);
         Vector4f var18 = new Vector4f(var5 + var11, 0.0F, 0.0F, 1.0F);
         var4.transform(var17);
         var4.transform(var18);
         var15 = var17.x;
         var16 = var18.x - var17.x;
      }

      VertexConsumer var21 = beginBatch(var0, var12, var13, var8, var9, var10, var16, var15);
      float var22 = var6 + textLineOffset(var0, var2);
      var0.applyGlyphs(var4, var21, var1, var2, var12 * 0.5F * var2, var14, var5, var22, var7, var3);
   }

   public static float textLineOffset(MsdfFont var0, float var1) {
      return var0.getBaseline() * var1;
   }

   /**
    * Все ванильные куски одного текста рисуются одним проходом рендера (раньше — отдельный проход на каждый кусок,
    * а в нике вида «★ Ник ❤» их несколько). Фон и MSDF-часть сбрасываются перед этим, поэтому порядок слоёв прежний.
    */
   private static void renderVanillaRuns(MsdfFont font, List<MsdfRenderer.VanillaRun> runs, float size, Matrix4f matrix, float y, float z) {
      flushBatch();
      float scale = vanillaTextScale(null, size);
      float top = y + textLineOffset(font, size) - (9 - 2) * scale;
      // 26.2 removed Font#drawInBatch(MultiBufferSource); vanilla glyphs are submitted as a text feature instead
      DisplayMode mode = ImmediateRenderer.method8005() ? DisplayMode.SEE_THROUGH : DisplayMode.NORMAL;
      OutlineVertexProvider.method1542(collector -> {
         for (MsdfRenderer.VanillaRun run : runs) {
            PoseStack pose = new PoseStack();
            pose.last().pose().set(new Matrix4f(matrix).translate(run.x(), top, z).scale(scale, scale, 1.0F));
            collector.submitText(pose, 0.0F, 0.0F, run.text(), false, mode, VANILLA_TEXT_LIGHT, run.color(), 0, 0);
         }
      });
   }

   private static float vanillaTextScale(Font var0, float var1) {
      return var1 / Math.max(1.0F, 9.0F);
   }

   public static void renderText(
      MsdfFont var0, String var1, float var2, GlowColor var3, Matrix4f var4, float var5, float var6, float var7, boolean var8, float var9, float var10
   ) {
      float var11 = var0.getWidth(var1, var2) * 2.0F;
      renderText(var0, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
   }

   private record VanillaRun(FormattedCharSequence text, float x, int color) {
   }

   @Generated
   private MsdfRenderer() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
}
