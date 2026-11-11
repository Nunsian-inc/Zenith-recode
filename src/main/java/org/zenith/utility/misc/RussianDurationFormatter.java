package org.zenith.utility.misc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.StringDecomposer;

/** Меняет только видимый текст длительностей, не затрагивая действия и оформление чата. */
public final class RussianDurationFormatter {
   private static final Pattern SECONDS = Pattern.compile(
      "(?<![\\p{L}\\p{N}_.,+−-])([0-9]+)\\h+(секунд(?:а|ы|у)?|сек\\.?)(?![\\p{L}\\p{N}_])",
      Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE
   );

   private RussianDurationFormatter() {
   }

   public static Component format(Component source) {
      StringBuilder text = new StringBuilder();
      List<Style> styles = new ArrayList<>();
      // Логический порядок нужен для кириллицы рядом с RTL-текстом; §-коды разбирает сам Minecraft.
      source.visit((style, part) -> {
         StringDecomposer.iterateFormatted(part, style, (index, charStyle, codePoint) -> {
            text.appendCodePoint(codePoint);
            for (int i = 0; i < Character.charCount(codePoint); i++) {
               styles.add(charStyle);
            }
            return true;
         });
         return Optional.empty();
      }, Style.EMPTY);

      Matcher matcher = SECONDS.matcher(text);
      MutableComponent result = null;
      int cursor = 0;
      while (matcher.find()) {
         // Не принимаем хвост числа с разрядными пробелами за отдельную длительность.
         int previous = matcher.start() - 1;
         while (previous >= 0 && (Character.isSpaceChar(text.charAt(previous)) || text.charAt(previous) == '\t')) {
            previous--;
         }
         if (previous >= 0 && Character.isDigit(text.charAt(previous))) {
            continue;
         }
         long seconds;
         try {
            seconds = Long.parseLong(matcher.group(1));
         } catch (NumberFormatException ignored) {
            continue;
         }
         if (result == null) {
            result = Component.empty();
         }
         appendRange(result, text, styles, cursor, matcher.start());
         // Новые числа наследуют стиль исходных цифр, названия единиц — стиль слова «секунд».
         appendDuration(result, seconds, styles.get(matcher.start(1)), styles.get(matcher.start(2)));
         cursor = matcher.end();
      }
      if (result == null) {
         return source;
      }
      appendRange(result, text, styles, cursor, text.length());
      return result;
   }

   private static void appendRange(MutableComponent result, CharSequence text, List<Style> styles, int start, int end) {
      while (start < end) {
         int next = start + 1;
         Style style = styles.get(start);
         while (next < end && style.equals(styles.get(next))) {
            next++;
         }
         result.append(Component.literal(text.subSequence(start, next).toString()).setStyle(style));
         start = next;
      }
   }

   private static void appendDuration(MutableComponent result, long seconds, Style numberStyle, Style unitStyle) {
      if (seconds == 0L) {
         appendUnit(result, 0L, "секунд", numberStyle, unitStyle, false);
         return;
      }
      long[] values = {seconds / 86400L, seconds / 3600L % 24L, seconds / 60L % 60L, seconds % 60L};
      String[][] units = {{"день", "дня", "дней"}, {"час", "часа", "часов"},
         {"минута", "минуты", "минут"}, {"секунда", "секунды", "секунд"}};
      boolean separator = false;
      for (int i = 0; i < values.length; i++) {
         long value = values[i];
         if (value == 0L) {
            continue;
         }
         long lastTwo = value % 100L;
         int form = lastTwo >= 11L && lastTwo <= 14L ? 2 : switch ((int)(value % 10L)) {
            case 1 -> 0;
            case 2, 3, 4 -> 1;
            default -> 2;
         };
         appendUnit(result, value, units[i][form], numberStyle, unitStyle, separator);
         separator = true;
      }
   }

   private static void appendUnit(MutableComponent result, long value, String unit, Style numberStyle, Style unitStyle, boolean separator) {
      result.append(Component.literal((separator ? " " : "") + value).setStyle(numberStyle));
      result.append(Component.literal(" " + unit).setStyle(unitStyle));
   }
}
