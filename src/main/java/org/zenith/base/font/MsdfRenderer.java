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

   private static VertexConsumer beginBatch(MsdfFont font, float f, float batchSmoothness2, boolean flag, float batchFadeoutStart2, float batchFadeoutEnd2, float batchMaxWidth2, float batchTextPosX2) {
      if (!flag) {
         batchFadeoutStart2 = 0.0F;
         batchFadeoutEnd2 = 0.0F;
         batchMaxWidth2 = 0.0F;
         batchTextPosX2 = 0.0F;
      }

      if (HintedText.pending()) {
         HintedText.flush();
      }

      GpuTextureView textureView = font.getTextureView();
      float range = font.getAtlas().range();
      float[] floats = RenderState.method1885();
      float batchShaderColorR2 = floats[0];
      float batchShaderColorG2 = floats[1];
      float batchShaderColorB2 = floats[2];
      float batchShaderColorA2 = floats[3];
      if (batchBuilder != null
         && (
            batchTextureId != textureView
               || batchRange != range
               || batchThickness != f
               || batchSmoothness != batchSmoothness2
               || batchFadeout != flag
               || batchFadeoutStart != batchFadeoutStart2
               || batchFadeoutEnd != batchFadeoutEnd2
               || batchMaxWidth != batchMaxWidth2
               || batchTextPosX != batchTextPosX2
               || batchShaderColorR != batchShaderColorR2
               || batchShaderColorG != batchShaderColorG2
               || batchShaderColorB != batchShaderColorB2
               || batchShaderColorA != batchShaderColorA2
         )) {
         flushBatch();
      }

      if (batchBuilder == null) {
         if (batchAllocator == null) {
            batchAllocator = new ByteBufferBuilder(65536);
         }

         batchBuilder = new BufferBuilder(batchAllocator, PrimitiveTopology.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
         batchTextureId = textureView;
         batchRange = range;
         batchThickness = f;
         batchSmoothness = batchSmoothness2;
         batchFadeout = flag;
         batchFadeoutStart = batchFadeoutStart2;
         batchFadeoutEnd = batchFadeoutEnd2;
         batchMaxWidth = batchMaxWidth2;
         batchTextPosX = batchTextPosX2;
         batchShaderColorR = batchShaderColorR2;
         batchShaderColorG = batchShaderColorG2;
         batchShaderColorB = batchShaderColorB2;
         batchShaderColorA = batchShaderColorA2;
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
         BufferBuilder bufferBuilder = batchBuilder;
         batchBuilder = null;
         MeshData orThrow = bufferBuilder.build();
         if (orThrow == null) {
            batchAllocator.clear();
         } else {
            float[] floats = RenderState.method1885();
            float f = floats[0];
            float f2 = floats[1];
            float f3 = floats[2];
            float previewAlpha = floats[3];
            boolean flag = f != batchShaderColorR || f2 != batchShaderColorG || f3 != batchShaderColorB || previewAlpha != batchShaderColorA;
            RenderState.method1871();
            RenderState.method1873();
            RenderState.method1877();
            if (flag) {
               RenderState.method1883(batchShaderColorR, batchShaderColorG, batchShaderColorB, batchShaderColorA);
            }

            RenderState.method1889(0, batchTextureId);
            KawaseShaderKey kawaseShaderKey = MSDF_FONT_PROGRAM.method5165();
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
               kawaseShaderKey.method5166("Range").method7412(batchRange);
            }

            if (lastUniformThickness != batchThickness) {
               lastUniformThickness = batchThickness;
               kawaseShaderKey.method5166("Thickness").method7412(batchThickness);
            }

            if (lastUniformSmoothness != batchSmoothness) {
               lastUniformSmoothness = batchSmoothness;
               kawaseShaderKey.method5166("Smoothness").method7412(batchSmoothness);
            }

            int n = batchFadeout ? 1 : 0;
            if (lastUniformFadeout != n) {
               lastUniformFadeout = n;
               kawaseShaderKey.method5166("EnableFadeout").set(n);
            }

            if (batchFadeout) {
               if (lastUniformFadeoutStart != batchFadeoutStart) {
                  lastUniformFadeoutStart = batchFadeoutStart;
                  kawaseShaderKey.method5166("FadeoutStart").method7412(batchFadeoutStart);
               }

               if (lastUniformFadeoutEnd != batchFadeoutEnd) {
                  lastUniformFadeoutEnd = batchFadeoutEnd;
                  kawaseShaderKey.method5166("FadeoutEnd").method7412(batchFadeoutEnd);
               }

               if (lastUniformMaxWidth != batchMaxWidth) {
                  lastUniformMaxWidth = batchMaxWidth;
                  kawaseShaderKey.method5166("MaxWidth").method7412(batchMaxWidth);
               }

               if (lastUniformTextPosX != batchTextPosX) {
                  lastUniformTextPosX = batchTextPosX;
                  kawaseShaderKey.method5166("TextPosX").method7412(batchTextPosX);
               }
            }

            try {
               ImmediateRenderer.method8015(orThrow);
            } finally {
               RenderState.method1893(0);
               if (flag) {
                  RenderState.method1883(f, f2, f3, previewAlpha);
               }

               RenderState.method1876();
               RenderState.method1872();
               batchAllocator.clear();
            }
         }
      }
   }

   public static void renderText(MsdfFont font, String text, float size, int color, Matrix4f matrix, float floatValue, float y, float z) {
      renderText(font, text, size, color, matrix, floatValue, y, z, false, 0.0F, 1.0F, 0.0F);
   }

   public static void renderText(
      MsdfFont font, String text, float size, int color, Matrix4f matrix, float floatValue, float y, float z, boolean flag, float f, float f2, float maxWidth
   ) {
      float f3 = 0.0F;
      float f4 = 0.45F;
      float f5 = 0.0F;
      text = NameProtectModule.method5149(text);
      if (flag || !HintedText.render(font, text, size, color, matrix, floatValue, y + textLineOffset(font, size), z)) {
         float floatValue2 = floatValue;
         float maxWidth2 = maxWidth;
         if (flag && maxWidth > 0.0F) {
            Vector4f vec = new Vector4f(floatValue, 0.0F, 0.0F, 1.0F);
            Vector4f vec2 = new Vector4f(floatValue + maxWidth, 0.0F, 0.0F, 1.0F);
            matrix.transform(vec);
            matrix.transform(vec2);
            floatValue2 = vec.x;
            maxWidth2 = vec2.x - vec.x;
         }

         VertexConsumer consumer = beginBatch(font, f3, f4, flag, f, f2, maxWidth2, floatValue2);
         float f6 = y + textLineOffset(font, size);
         font.applyGlyphs(matrix, consumer, text, size, f3 * 0.5F * size, f5, floatValue, f6, z, color);
      }
   }

   public static void renderText(
      MsdfFont font, String text, float size, int color, Matrix4f matrix, float floatValue, float y, float z, boolean flag, float f, float f2
   ) {
      float maxWidth = font.getWidth(text, size) * 2.0F;
      renderText(font, text, size, color, matrix, floatValue, y, z, flag, f, f2, maxWidth);
   }

   public static void renderText(MsdfFont font, Component text, float size, Matrix4f matrix, float floatValue, float y, float z) {
      renderTextWithVanillaFallback(font, text, size, matrix, floatValue, y, z);
   }

   public static void renderText(
      MsdfFont font, Component text, float size, Matrix4f matrix, float floatValue, float y, float z, boolean flag, float f, float f2, float f3
   ) {
      renderTextWithVanillaFallback(font, text, size, matrix, floatValue, y, z);
   }

   public static void renderText(
      MsdfFont font, Component text, float size, Matrix4f matrix, float x1, float y, float z, boolean flag, float f, float f2, float f3, int color
   ) {
      renderTextWithVanillaFallback(font, text, size, matrix, x1, y, z, color);
   }

   public static void renderTextWithVanillaFallback(MsdfFont font, Component text, float size, Matrix4f matrix, float floatValue, float y, float z) {
      renderTextWithVanillaFallback(font, text, size, matrix, floatValue, y, z, ZenithColor.INSTANCE.method7287());
   }

   public static void renderTextWithVanillaFallback(MsdfFont font, Component text, float size, Matrix4f matrix, float x1, float y, float z, int color) {
      Minecraft minecraft = Minecraft.getInstance();
      renderTextWithVanillaFallback(font, text, size, minecraft.font, null, matrix, x1, y, z, color);
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

   public static void renderText(MsdfFont font, Component text, float size, Matrix4f matrix, float floatValue, float y, float z, boolean flag, float f, float f2) {
      renderTextWithVanillaFallback(font, text, size, matrix, floatValue, y, z);
   }

   public static void renderText(MsdfFont font, String text, float size, GlowColor glowColor, Matrix4f matrix4f, float floatValue, float f, float f2) {
      renderText(font, text, size, glowColor, matrix4f, floatValue, f, f2, false, 0.0F, 1.0F, 0.0F);
   }

   public static void renderText(
      MsdfFont font,
      String text,
      float size,
      GlowColor glowColor,
      Matrix4f matrix4f,
      float floatValue,
      float f,
      float z,
      boolean flag,
      float f2,
      float f3,
      float f4
   ) {
      text = text.replace("і", "i").replace("І", "I");
      float f5 = 0.05F;
      float f6 = 0.5F;
      float f7 = 0.0F;
      text = NameProtectModule.method5149(text);
      float floatValue2 = floatValue;
      float f8 = f4;
      if (flag && f4 > 0.0F) {
         Vector4f vec = new Vector4f(floatValue, 0.0F, 0.0F, 1.0F);
         Vector4f vec2 = new Vector4f(floatValue + f4, 0.0F, 0.0F, 1.0F);
         matrix4f.transform(vec);
         matrix4f.transform(vec2);
         floatValue2 = vec.x;
         f8 = vec2.x - vec.x;
      }

      VertexConsumer consumer = beginBatch(font, f5, f6, flag, f2, f3, f8, floatValue2);
      float f9 = f + textLineOffset(font, size);
      font.applyGlyphs(matrix4f, consumer, text, size, f5 * 0.5F * size, f7, floatValue, f9, z, glowColor);
   }

   public static float textLineOffset(MsdfFont font, float size) {
      return font.getBaseline() * size;
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

   private static float vanillaTextScale(Font vanillaFont, float size) {
      return size / Math.max(1.0F, 9.0F);
   }

   public static void renderText(
      MsdfFont font, String text, float size, GlowColor glowColor, Matrix4f matrix4f, float floatValue, float f, float z, boolean flag, float f2, float f3
   ) {
      float f4 = font.getWidth(text, size) * 2.0F;
      renderText(font, text, size, glowColor, matrix4f, floatValue, f, z, flag, f2, f3, f4);
   }

   private record VanillaRun(FormattedCharSequence text, float x, int color) {
   }

   @Generated
   private MsdfRenderer() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
}
