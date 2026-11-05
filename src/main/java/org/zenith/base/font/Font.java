package org.zenith.base.font;

import lombok.Generated;
import net.minecraft.network.chat.Component;

public class Font {
   private MsdfFont font;
   private float size;

   public float height() {
      return this.font.getAscent() * this.size;
   }

   public float baseline() {
      return MsdfRenderer.textLineOffset(this.font, this.size);
   }

   public float getStringHeight(String str) {
      float height = 0.0F;
      float f = 0.0F;

      for (char c : (str.isEmpty() ? " " : str).toCharArray()) {
         if (c == '\n') {
            height = height == 0.0F ? this.height() : height;
            f += height;
            height = 0.0F;
         } else {
            height = Math.max(this.height(), height);
         }
      }

      return height + f;
   }

   public float width(String text) {
      return this.font.getWidth(text, this.size);
   }

   public float[] inkBounds(String text) {
      if (text != null && !text.isEmpty()) {
         MsdfGlyph glyph = this.font.getGlyph(text.charAt(0));
         return glyph == null ? null : new float[]{glyph.getInkWidth(this.size), glyph.getInkHeight(this.size), glyph.getInkTop(this.size)};
      } else {
         return null;
      }
   }

   public float width(Component text) {
      return this.font.getTextWidth(text, this.size);
   }

   public String clip(String name, float width) {
      if (name == null || name.isEmpty()) {
         return name;
      } else if (width <= 0.0F) {
         return "";
      } else if (this.width(name) <= width) {
         return name;
      } else {
         float f = this.width("…");
         int fitPrefix = this.font.fitPrefix(name, this.size, f, width);
         return fitPrefix == 0 ? "" : name.substring(0, fitPrefix) + "…";
      }
   }

   @Generated
   public MsdfFont getFont() {
      return this.font;
   }

   @Generated
   public float getSize() {
      return this.size;
   }

   @Generated
   public Font(MsdfFont font, float size) {
      this.font = font;
      this.size = size;
   }
}
