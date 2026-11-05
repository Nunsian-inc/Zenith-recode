package org.zenith.base.font;

import com.mojang.blaze3d.vertex.VertexConsumer;
import org.joml.Matrix4f;
import org.zenith.utility.render.display.base.GlowColor;

public final class MsdfGlyph {
   private final int code;
   private final float minU;
   private final float maxU;
   private final float minV;
   private final float maxV;
   private final float advance;
   private final float topPosition;
   private final float width;
   private final float height;

   public MsdfGlyph(FontData.GlyphData glyph, float width, float height) {
      this.code = glyph.unicode();
      this.advance = glyph.advance();
      FontData.BoundsData atlasBounds = glyph.atlasBounds();
      if (atlasBounds != null) {
         this.minU = atlasBounds.left() / width;
         this.maxU = atlasBounds.right() / width;
         this.minV = 1.0F - atlasBounds.top() / height;
         this.maxV = 1.0F - atlasBounds.bottom() / height;
      } else {
         this.minU = this.maxU = this.minV = this.maxV = 0.0F;
      }

      FontData.BoundsData planeBounds = glyph.planeBounds();
      if (planeBounds != null) {
         this.width = planeBounds.right() - planeBounds.left();
         this.height = planeBounds.top() - planeBounds.bottom();
         this.topPosition = planeBounds.top();
      } else {
         this.width = this.height = this.topPosition = 0.0F;
      }
   }

   private static void vertex(VertexConsumer consumer, Matrix4f matrix, float floatValue, float f, float z, float minU, float minV, int color) {
      float x = matrix.m00() * floatValue + matrix.m10() * f + matrix.m20() * z + matrix.m30();
      float y = matrix.m01() * floatValue + matrix.m11() * f + matrix.m21() * z + matrix.m31();
      float z2 = matrix.m02() * floatValue + matrix.m12() * f + matrix.m22() * z + matrix.m32();
      consumer.addVertex(x, y, z2).setUv(minU, minV).setColor(color);
   }

   public float apply(Matrix4f matrix, VertexConsumer consumer, float size, float floatValue, float f, float z, int color) {
      f -= this.topPosition * size;
      float floatValue2 = floatValue + this.width * size;
      float f2 = f + this.height * size;
      vertex(consumer, matrix, floatValue, f, z, this.minU, this.minV, color);
      vertex(consumer, matrix, floatValue, f2, z, this.minU, this.maxV, color);
      vertex(consumer, matrix, floatValue2, f2, z, this.maxU, this.maxV, color);
      vertex(consumer, matrix, floatValue2, f, z, this.maxU, this.minV, color);
      return this.advance * size;
   }

   public float apply(Matrix4f matrix4f, VertexConsumer consumer, float size, float floatValue, float f, float z, GlowColor glowColor) {
      f -= this.topPosition * size;
      float floatValue2 = floatValue + this.width * size;
      float f2 = f + this.height * size;
      vertex(consumer, matrix4f, floatValue, f, z, this.minU, this.minV, glowColor.getZenithColor().method7287());
      vertex(consumer, matrix4f, floatValue, f2, z, this.minU, this.maxV, glowColor.getZenithColor2().method7287());
      vertex(consumer, matrix4f, floatValue2, f2, z, this.maxU, this.maxV, glowColor.getZenithColor4().method7287());
      vertex(consumer, matrix4f, floatValue2, f, z, this.maxU, this.minV, glowColor.getZenithColor3().method7287());
      return this.advance * size;
   }

   public float getWidth(float size) {
      return this.advance * size;
   }

   public float getInkWidth(float size) {
      return this.width * size;
   }

   public float getInkHeight(float size) {
      return this.height * size;
   }

   public float getInkTop(float size) {
      return this.topPosition * size;
   }

   public int getCharCode() {
      return this.code;
   }
}
