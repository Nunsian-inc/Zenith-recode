package org.zenith.base.font;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.StringDecomposer;
import org.zenith.client.modules.impl.misc.NameProtectModule;
import org.zenith.utility.misc.ColorUtils;

public class FormattedTextProcessor {
   public static List<FormattedTextProcessor.TextSegment> processText(Component var0, int var1) {
      ArrayList<FormattedTextProcessor.TextSegment> var2 = new ArrayList<>();
      var0.visit((var2x, var3) -> {
         if (!var3.isEmpty()) {
            appendSegments(var2, NameProtectModule.method5149(var3), var2x, var1);
         }

         return Optional.empty();
      }, Style.EMPTY);
      return var2;
   }

   /**
    * §-коды внутри literal-текста (серверы часто так шлют ники и префиксы) применяются как в ванилле:
    * цвет, жирный, §k, §r. Раньше они просто вырезались, и всё рисовалось цветом родителя.
    */
   private static void appendSegments(List<FormattedTextProcessor.TextSegment> segments, String text, Style style, int baseColor) {
      if (text.indexOf('§') < 0) {
         segments.add(segment(text, style, baseColor));
         return;
      }

      StringBuilder runText = new StringBuilder();
      Style[] runStyle = new Style[1];
      StringDecomposer.iterateFormatted(text, style, (position, charStyle, codePoint) -> {
         if (runStyle[0] != null && !runStyle[0].equals(charStyle)) {
            segments.add(segment(runText.toString(), runStyle[0], baseColor));
            runText.setLength(0);
         }

         runStyle[0] = charStyle;
         runText.appendCodePoint(codePoint);
         return true;
      });
      if (!runText.isEmpty()) {
         segments.add(segment(runText.toString(), runStyle[0], baseColor));
      }
   }

   private static FormattedTextProcessor.TextSegment segment(String text, Style style, int baseColor) {
      return new FormattedTextProcessor.TextSegment(
         text,
         extractColor(style, baseColor),
         style.isBold(),
         style.isItalic(),
         style.isUnderlined(),
         style.isStrikethrough(),
         style.withColor((TextColor)null)
      );
   }

   /**
    * Делит сегмент на куски для отрисовки: символы из MSDF-атласа рисуются им, всё остальное (любой Unicode:
    * иероглифы, эмодзи, ★❤⚔, PUA-иконки ресурспаков) — ванильным шрифтом. Сегменты со своим шрифтом в стиле
    * (иконки серверных ресурспаков, спрайты и головы из object-компонентов) и §k целиком уходят в ванильный шрифт.
    * Один и тот же разбор используется и для ширины, и для отрисовки, поэтому они всегда совпадают.
    */
   public static List<FormattedTextProcessor.TextRun> splitRuns(MsdfFont font, FormattedTextProcessor.TextSegment segment) {
      Style style = segment.style();
      if (!FontDescription.DEFAULT.equals(style.getFont()) || style.isObfuscated()) {
         return List.of(new FormattedTextProcessor.TextRun(segment.text(), false, style));
      }

      String text = MsdfFont.normalizeSmallCaps(segment.text());
      List<FormattedTextProcessor.TextRun> runs = new ArrayList<>(1);
      int runStart = 0;
      boolean runMsdf = false;
      int index = 0;

      while (index < text.length()) {
         int codePoint = text.codePointAt(index);
         boolean msdf = codePoint <= 65535 && font.hasGlyph(codePoint);
         if (index == runStart) {
            runMsdf = msdf;
         } else if (msdf != runMsdf) {
            runs.add(new FormattedTextProcessor.TextRun(text.substring(runStart, index), runMsdf, Style.EMPTY));
            runStart = index;
            runMsdf = msdf;
         }

         index += Character.charCount(codePoint);
      }

      if (runStart < text.length()) {
         runs.add(new FormattedTextProcessor.TextRun(text.substring(runStart), runMsdf, Style.EMPTY));
      }

      return runs;
   }

   private static int extractColor(Style var0, int var1) {
      TextColor var2 = var0.getColor();
      if (var2 != null) {
         int var3 = var2.getValue() | 0xFF000000;
         return var3 != -1 ? ColorUtils.method6439(var3, ColorUtils.method6427(var1)) : var1;
      } else {
         return var1;
      }
   }

   /**
    * Кусок текста одного вида: msdf = рисуется MSDF-атласом, иначе ванильным шрифтом со стилем style
    * (стиль без цвета — цвет с учётом прозрачности берётся из сегмента).
    */
   public record TextRun(String text, boolean msdf, Style style) {
      public FormattedCharSequence sequence() {
         return FormattedCharSequence.forward(this.text, this.style);
      }
   }

   public static final class TextSegment {
      private final String text;
      private final int color;
      private final boolean bold;
      private final boolean italic;
      private final boolean underlined;
      private final boolean strikethrough;
      private final Style style;

      public String text() {
         return this.text;
      }

      public int color() {
         return this.color;
      }

      public boolean bold() {
         return this.bold;
      }

      public boolean italic() {
         return this.italic;
      }

      public boolean underlined() {
         return this.underlined;
      }

      public boolean strikethrough() {
         return this.strikethrough;
      }

      public Style style() {
         return this.style;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof TextSegment other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.color, other.color) && java.util.Objects.equals(this.bold, other.bold) && java.util.Objects.equals(this.italic, other.italic) && java.util.Objects.equals(this.underlined, other.underlined) && java.util.Objects.equals(this.strikethrough, other.strikethrough) && java.util.Objects.equals(this.style, other.style);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, color, bold, italic, underlined, strikethrough, style);
      }

      @Override
      public String toString() {
         return "TextSegment[" + "text=" + this.text + ", " + "color=" + this.color + ", " + "bold=" + this.bold + ", " + "italic=" + this.italic + ", " + "underlined=" + this.underlined + ", " + "strikethrough=" + this.strikethrough + ", " + "style=" + this.style + "]";
      }

      public TextSegment(String var1, int var2, boolean var3, boolean var4, boolean var5, boolean var6) {
         this(var1, var2, var3, var4, var5, var6, Style.EMPTY);
      }

      public TextSegment(String text, int color, boolean bold, boolean italic, boolean underlined, boolean strikethrough, Style style) {
         this.text = text;
         this.color = color;
         this.bold = bold;
         this.italic = italic;
         this.underlined = underlined;
         this.strikethrough = strikethrough;
         this.style = style;
      }
   }
}
