package org.zenith.base.figura.utils;

import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.minecraft.client.gui.Font;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;

public final class TextUtils {
   private TextUtils() {
   }

   public static Component tryParseJson(String value) {
      if (value == null) {
         return Component.empty();
      }

      try {
         Component parsed = (Component)ComponentSerialization.CODEC
            .parse(RegistryAccess.EMPTY.createSerializationContext(JsonOps.INSTANCE), JsonParser.parseString(value))
            .getOrThrow();
         return (Component)(parsed == null ? Component.literal(value) : parsed);
      } catch (Exception e) {
         return Component.literal(value);
      }
   }

   public static List<Component> formatInBounds(FormattedText text, Font font, int maxWidth, boolean wrap) {
      if (maxWidth <= 0) {
         return splitText(text, "\\n");
      }

      if (wrap) {
         List<Component> result = new ArrayList<>();

         for (Component line : splitText(text, "\\n")) {
            for (FormattedCharSequence ordered : font.split(line, maxWidth)) {
               result.add(fromOrderedText(ordered));
            }
         }

         return result;
      } else {
         List<Component> result = new ArrayList<>();

         for (Component line : splitText(text, "\\n")) {
            result.add(fromVisitable(font.substrByWidth(line, maxWidth)));
         }

         return result;
      }
   }

   public static int getWidth(List<?> lines, Font font) {
      int width = 0;

      for (Object line : lines) {
         if (line instanceof Component text) {
            width = Math.max(width, font.width(text));
         } else if (line instanceof FormattedCharSequence ordered) {
            width = Math.max(width, font.width(ordered));
         } else if (line instanceof String string) {
            width = Math.max(width, font.width(string));
         }
      }

      return width;
   }

   public static int getHeight(List<?> lines, Font font) {
      int count = lines.size();
      return count * 9 + Math.max(count - 1, 0);
   }

   public static List<Component> splitText(FormattedText text, String regex) {
      List<Component> result = new ArrayList<>();
      MutableComponent[] current = new MutableComponent[]{Component.empty()};
      text.visit((style, string) -> {
         String[] pieces = string.split(regex, -1);

         for (int i = 0; i < pieces.length; i++) {
            if (i > 0) {
               result.add(current[0]);
               current[0] = Component.empty();
            }

            current[0].append(Component.literal(pieces[i]).setStyle(style));
         }

         return Optional.empty();
      }, Style.EMPTY);
      result.add(current[0]);
      return result;
   }

   private static Component fromVisitable(FormattedText source) {
      MutableComponent result = Component.empty();
      source.visit((style, string) -> {
         result.append(Component.literal(string).setStyle(style));
         return Optional.empty();
      }, Style.EMPTY);
      return result;
   }

   private static Component fromOrderedText(FormattedCharSequence source) {
      MutableComponent result = Component.empty();
      source.accept((index, style, codePoint) -> {
         result.append(Component.literal(Character.toString(codePoint)).setStyle(style));
         return true;
      });
      return result;
   }

   public enum Alignment {
      LEFT((font, text) -> 0, width -> 0),
      RIGHT(Font::width, width -> width),
      CENTER((font, text) -> font.width(text) / 2, width -> width / 2);

      private final BiFunction<Font, FormattedText, Integer> textFunction;
      private final Function<Integer, Integer> widthFunction;

      Alignment(BiFunction<Font, FormattedText, Integer> textFunction, Function<Integer, Integer> widthFunction) {
         this.textFunction = textFunction;
         this.widthFunction = widthFunction;
      }

      public int apply(Font font, FormattedText text) {
         return this.textFunction.apply(font, text);
      }

      public int apply(int width) {
         return this.widthFunction.apply(width);
      }
   }
}
