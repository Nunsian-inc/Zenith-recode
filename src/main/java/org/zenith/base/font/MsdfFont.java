package org.zenith.base.font;

import com.mojang.renderpearl.api.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.ints.Int2FloatMap;
import it.unimi.dsi.fastutil.ints.Int2FloatOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Generated;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.joml.Matrix4f;
import org.zenith.MinecraftAccess;
import org.zenith.Zenith;
import org.zenith.client.modules.impl.misc.NameProtectModule;
import org.zenith.utility.render.display.base.GlowColor;

public final class MsdfFont implements MinecraftAccess {
   private static final int WIDTH_CACHE_LIMIT = 4096;
   private final String name;
   private final AbstractTexture texture;
   private final FontData.AtlasData atlas;
   private final FontData.MetricsData metrics;
   private final MsdfGlyph[] glyphTable;
   private final int glyphOffset;
   private final Int2ObjectMap<Int2FloatMap> kernings;
   private final Map<String, Float> unitWidthCache = new ConcurrentHashMap<>();
   private final String hintedFace;
   private final float ascent;
   private final float baseline;

   private MsdfFont(
      String name,
      AbstractTexture texture,
      FontData.AtlasData atlas,
      FontData.MetricsData metrics,
      MsdfGlyph[] glyphTable,
      int glyphOffset,
      Int2ObjectMap<Int2FloatMap> kernings,
      String hintedFace,
      float ascent,
      float baseline
   ) {
      this.ascent = ascent;
      this.baseline = baseline;
      this.name = name;
      this.texture = texture;
      this.atlas = atlas;
      this.metrics = metrics;
      this.glyphTable = glyphTable;
      this.glyphOffset = glyphOffset;
      this.kernings = kernings;
      this.hintedFace = hintedFace;
   }

   public float kerning(int charAt, int n) {
      Int2FloatMap kerning = (Int2FloatMap)this.kernings.get(charAt);
      return kerning == null ? 0.0F : kerning.get(n);
   }

   public int getTextureId() {
      return System.identityHashCode(this.texture);
   }

   public GpuTextureView getTextureView() {
      return this.texture.getTextureView();
   }

   private MsdfGlyph glyph(int codePoint) {
      int n = codePoint - this.glyphOffset;
      return n >= 0 && n < this.glyphTable.length ? this.glyphTable[n] : null;
   }

   public boolean hasGlyph(int codePoint) {
      return this.glyph(codePoint) != null;
   }

   public MsdfGlyph getGlyph(int charAt) {
      return this.glyph(charAt);
   }

   public void applyGlyphs(Matrix4f matrix, VertexConsumer consumer, String text, float size, float f, float f2, float floatValue, float f3, float z, int color) {
      int charAt = -1;
      boolean flag = false;

      for (int i = 0; i < text.length(); i++) {
         char codePoint = text.charAt(i);
         if (flag) {
            flag = false;
         } else if (codePoint == 167) {
            flag = true;
         } else {
            MsdfGlyph glyph = this.glyph(codePoint);
            if (glyph == null) {
               char c = normalizeGlyphChar(codePoint);
               glyph = c == codePoint ? null : this.glyph(c);
               if (glyph == null) {
                  continue;
               }

               codePoint = c;
            }

            Int2FloatMap kerning = (Int2FloatMap)this.kernings.get(charAt);
            if (kerning != null) {
               floatValue += kerning.get(codePoint) * size;
            }

            floatValue += glyph.apply(matrix, consumer, size, floatValue, f3, z, color) + f + f2;
            charAt = codePoint;
         }
      }
   }

   public void applyGlyphs(
      Matrix4f matrix4f, VertexConsumer consumer, String text, float size, float f, float f2, float floatValue, float f3, float z, GlowColor glowColor
   ) {
      int charAt = -1;
      boolean flag = false;

      for (int i = 0; i < text.length(); i++) {
         char codePoint = text.charAt(i);
         if (flag) {
            flag = false;
         } else if (codePoint == 167) {
            flag = true;
         } else {
            MsdfGlyph glyph = this.glyph(codePoint);
            if (glyph == null) {
               char c = normalizeGlyphChar(codePoint);
               glyph = c == codePoint ? null : this.glyph(c);
               if (glyph == null) {
                  continue;
               }

               codePoint = c;
            }

            Int2FloatMap kerning = (Int2FloatMap)this.kernings.get(charAt);
            if (kerning != null) {
               floatValue += kerning.get(codePoint) * size;
            }

            floatValue += glyph.apply(matrix4f, consumer, size, floatValue, f3, z, glowColor) + f + f2;
            charAt = codePoint;
         }
      }
   }

   public float getWidth(String text, float size) {
      if (text != null && !text.isEmpty()) {
         text = NameProtectModule.method5149(text);
         Float unitWidth = this.unitWidthCache.get(text);
         if (unitWidth == null) {
            unitWidth = this.computeUnitWidth(text);
            if (this.unitWidthCache.size() >= 4096) {
               this.unitWidthCache.clear();
            }

            this.unitWidthCache.put(text, unitWidth);
         }

         return unitWidth * size;
      } else {
         return 0.0F;
      }
   }

   private float computeUnitWidth(String text) {
      int charAt = -1;
      float f = 0.0F;
      boolean flag = false;

      for (int i = 0; i < text.length(); i++) {
         char codePoint = text.charAt(i);
         if (flag) {
            flag = false;
         } else if (codePoint == 167) {
            flag = true;
         } else {
            MsdfGlyph glyph = this.glyph(codePoint);
            if (glyph == null) {
               char c = normalizeGlyphChar(codePoint);
               glyph = c == codePoint ? null : this.glyph(c);
               if (glyph == null) {
                  continue;
               }

               codePoint = c;
            }

            Int2FloatMap kerning = (Int2FloatMap)this.kernings.get(charAt);
            if (kerning != null) {
               f += kerning.get(codePoint);
            }

            f += glyph.getWidth(1.0F);
            charAt = codePoint;
         }
      }

      return f;
   }

   public int fitPrefix(String name, float size, float f, float width) {
      int charAt = -1;
      float f2 = 0.0F;
      boolean flag = false;
      int n = 0;

      for (int i = 0; i < name.length(); i++) {
         char codePoint = name.charAt(i);
         if (flag) {
            flag = false;
         } else if (codePoint == 167) {
            flag = true;
         } else {
            MsdfGlyph glyph = this.glyph(codePoint);
            if (glyph == null) {
               char c = normalizeGlyphChar(codePoint);
               glyph = c == codePoint ? null : this.glyph(c);
               if (glyph != null) {
                  codePoint = c;
               }
            }

            if (glyph != null) {
               Int2FloatMap kerning = (Int2FloatMap)this.kernings.get(charAt);
               if (kerning != null) {
                  f2 += kerning.get(codePoint);
               }

               f2 += glyph.getWidth(1.0F);
               charAt = codePoint;
            }
         }

         if (!(f2 * size + f > width)) {
            n = i + 1;
         }
      }

      return n;
   }

   public static char normalizeGlyphChar(char codePoint) {
      return switch (codePoint) {
         case '«', '»' -> '"';
         case '·' -> '•';
         case '×' -> 'x';
         case 'ǫ', 'ʠ' -> 'Q';
         case 'ɢ' -> 'G';
         case 'ɪ' -> 'I';
         case 'ɴ' -> 'N';
         case 'ʀ' -> 'R';
         case 'ʏ' -> 'Y';
         case 'ʙ' -> 'B';
         case 'ʜ' -> 'H';
         case 'ʟ' -> 'L';
         case 'ᴀ' -> 'A';
         case 'ᴄ' -> 'C';
         case 'ᴅ' -> 'D';
         case 'ᴇ' -> 'E';
         case 'ᴊ' -> 'J';
         case 'ᴋ' -> 'K';
         case 'ᴍ' -> 'M';
         case 'ᴏ' -> 'O';
         case 'ᴘ' -> 'P';
         case 'ᴛ' -> 'T';
         case 'ᴜ' -> 'U';
         case 'ᴠ' -> 'V';
         case 'ᴡ' -> 'W';
         case 'ᴢ' -> 'Z';
         case '–', '—', '−' -> '-';
         case '…' -> '.';
         case 'ꜰ' -> 'F';
         case 'ꜱ' -> 'S';
         // кириллица, которая выглядит как латиница (укр./бел. ники): рисуется гладким MSDF, а не пиксельным фолбэком
         case 'і' -> 'i';
         case 'І' -> 'I';
         case 'ј' -> 'j';
         case 'Ј' -> 'J';
         case 'ѕ' -> 's';
         case 'Ѕ' -> 'S';
         default -> codePoint;
      };
   }

   public static String normalizeSmallCaps(String text) {
      int length = text.length();

      for (int i = 0; i < length; i++) {
         char charAt = text.charAt(i);
         if (normalizeGlyphChar(charAt) != charAt) {
            StringBuilder builder = new StringBuilder(length);
            builder.append(text, 0, i);

            for (int j = i; j < length; j++) {
               builder.append(normalizeGlyphChar(text.charAt(j)));
            }

            return builder.toString();
         }
      }

      return text;
   }

   public float getTextWidth(Component text, float size) {
      return this.getTextWidthWithVanillaFallback(text, size);
   }

   private float getTextWidthWithVanillaFallback(Component text, float size) {
      Font vanillaFont = mc.font;
      float width = 0.0F;

      for (FormattedTextProcessor.TextSegment segment : FormattedTextProcessor.processText(text, -1)) {
         for (FormattedTextProcessor.TextRun run : FormattedTextProcessor.splitRuns(this, segment)) {
            width += run.msdf() ? this.getWidth(run.text(), size) : vanillaFont.width(run.sequence()) * this.vanillaTextScale(vanillaFont, size);
         }
      }

      return width;
   }

   private float vanillaTextScale(Font vanillaFont, float size) {
      return size / Math.max(1.0F, 9.0F);
   }

   public org.zenith.base.font.Font getFont(float px) {
      return new org.zenith.base.font.Font(this, px);
   }

   public static MsdfFont.Builder builder() {
      return new MsdfFont.Builder();
   }

   @Generated
   public String getName() {
      return this.name;
   }

   @Generated
   public FontData.AtlasData getAtlas() {
      return this.atlas;
   }

   @Generated
   public FontData.MetricsData getMetrics() {
      return this.metrics;
   }

   @Generated
   public String getHintedFace() {
      return this.hintedFace;
   }

   @Generated
   public float getAscent() {
      return this.ascent;
   }

   @Generated
   public float getBaseline() {
      return this.baseline;
   }

   public static class Builder {
      private String name = "?";
      private Identifier dataIdentifer;
      private Identifier atlasIdentifier;
      private String hintedFace;
      private boolean capCentered;

      private Builder() {
      }

      public MsdfFont.Builder hinted(String hintedFace) {
         this.hintedFace = hintedFace;
         return this;
      }

      public MsdfFont.Builder capCentered() {
         this.capCentered = true;
         return this;
      }

      public MsdfFont.Builder name(String name) {
         this.name = name;
         return this;
      }

      public MsdfFont.Builder data(String str) {
         this.dataIdentifer = Zenith.method1501("fonts/msdf/" + str + ".json");
         return this;
      }

      public MsdfFont.Builder data(Identifier dataIdentifer) {
         this.dataIdentifer = dataIdentifer;
         return this;
      }

      public MsdfFont.Builder atlas(String str) {
         this.atlasIdentifier = Zenith.method1501("fonts/msdf/" + str + ".png");
         return this;
      }

      public MsdfFont.Builder atlas(Identifier atlasIdentifier) {
         this.atlasIdentifier = atlasIdentifier;
         return this;
      }

      public MsdfFont build() {
         FontData fontData = ResourceProvider.fromJsonToInstance(this.dataIdentifer, FontData.class);
         AbstractTexture texture = MinecraftAccess.mc.getTextureManager().getTexture(this.atlasIdentifier);
         if (fontData == null) {
            throw new RuntimeException(
               "Failed to read font data file: "
                  + this.dataIdentifer.toString()
                  + "; Are you sure this is json file? Try to check the correctness of its syntax."
            );
         } else {
            float width = fontData.atlas().width();
            float height = fontData.atlas().height();
            int unicode = Integer.MAX_VALUE;
            int n = Integer.MIN_VALUE;

            for (FontData.GlyphData glyph : fontData.glyphs()) {
               int unicode2 = glyph.unicode();
               if (unicode2 < unicode) {
                  unicode = unicode2;
               }

               if (unicode2 > n) {
                  n = unicode2;
               }
            }

            MsdfGlyph[] glyphTable;
            int glyphOffset;
            if (unicode > n) {
               glyphTable = new MsdfGlyph[0];
               glyphOffset = 0;
            } else {
               glyphOffset = unicode;
               glyphTable = new MsdfGlyph[n - unicode + 1];

               for (FontData.GlyphData glyph : fontData.glyphs()) {
                  glyphTable[glyph.unicode() - glyphOffset] = new MsdfGlyph(glyph, width, height);
               }
            }

            Int2ObjectOpenHashMap int2ObjectOpenHashMap = new Int2ObjectOpenHashMap();
            fontData.kernings().forEach(var1x -> {
               Int2FloatMap var2x = (Int2FloatMap)int2ObjectOpenHashMap.computeIfAbsent(var1x.leftChar(), var0x -> new Int2FloatOpenHashMap());
               var2x.put(var1x.rightChar(), var1x.advance());
            });
            float ascender = fontData.metrics().ascender() > 0.0F ? fontData.metrics().ascender() : 0.7F;
            float baseline = ascender;
            if (this.capCentered) {
               for (FontData.GlyphData glyph : fontData.glyphs()) {
                  if (glyph.unicode() == 72 && glyph.planeBounds() != null) {
                     float f = glyph.planeBounds().top() + glyph.planeBounds().bottom();
                     if (f > 0.0F && f < ascender) {
                        baseline = (ascender + f) / 2.0F;
                     }
                     break;
                  }
               }
            }

            return new MsdfFont(this.name, texture, fontData.atlas(), fontData.metrics(), glyphTable, glyphOffset, int2ObjectOpenHashMap, this.hintedFace, ascender, baseline);
         }
      }
   }
}
