package org.zenith.client.screens.override.main;

import org.zenith.base.font.Font;
import org.zenith.base.font.SvgIcon;

public final class MenuIcon {
   private final String glyph;
   private final boolean legacy;
   private final boolean vector;

   public String glyph() {
      return this.glyph;
   }

   public boolean legacy() {
      return this.legacy;
   }

   public boolean vector() {
      return this.vector;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof MenuIcon other)) return false;
      return java.util.Objects.equals(this.glyph, other.glyph) && java.util.Objects.equals(this.legacy, other.legacy) && java.util.Objects.equals(this.vector, other.vector);
   }

   @Override
   public int hashCode() {
      return java.util.Objects.hash(glyph, legacy, vector);
   }

   @Override
   public String toString() {
      return "MenuIcon[" + "glyph=" + this.glyph + ", " + "legacy=" + this.legacy + ", " + "vector=" + this.vector + "]";
   }

   public MenuIcon(String glyph, boolean legacy) {
      this(glyph, legacy, false);
   }

   public MenuIcon(String glyph, boolean legacy, boolean vector) {
      this.glyph = glyph;
      this.legacy = legacy;
      this.vector = vector;
   }

   public static MenuIcon of(char c) {
      return new MenuIcon(String.valueOf(c), false);
   }

   public static MenuIcon legacy(char c) {
      return new MenuIcon(String.valueOf(c), true);
   }

   public static MenuIcon svg(SvgIcon svgIcon) {
      return new MenuIcon(svgIcon.glyph(), false, true);
   }

   public Font font(float f) {
      if (this.vector) {
         return SvgIcon.font(MenuTheme.px(f));
      } else {
         return this.legacy ? MenuTheme.legacyIcon(f) : MenuTheme.icon(f);
      }
   }

   public float width(float f) {
      return this.font(f).width(this.glyph);
   }

   public float height(float f) {
      return this.font(f).height();
   }
}
