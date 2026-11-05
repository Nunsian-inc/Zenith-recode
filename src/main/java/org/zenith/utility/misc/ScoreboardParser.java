package org.zenith.utility.misc;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Generated;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.Nullable;
import org.zenith.client.modules.impl.misc.StreamerModeModule;

public final class ScoreboardParser {
   private static final Pattern pattern = Pattern.compile("(?iu)(?:соло|дуо|трио|клан)?[\\s-]*лайт[\\s#]*\\d*");
   private static final Pattern pattern2 = Pattern.compile("(?i)^(?:§.)*\\s*(XYZ|Block|Chunk|Targeted Block|Targeted Fluid)\\b");
   private static final Pattern pattern3 = Pattern.compile("-?\\d+(?:\\.\\d+)?");
   private static long longValue;
   private static long longValue2;
   private static boolean flag;
   private static final Pattern pattern4 = Pattern.compile("(?i)онлайн:\\s*(\\d+)");
   private static final Pattern pattern5 = Pattern.compile("[\\s#◀▶\\-]+");
   private static final ScoreboardParser.Entry[] entrys = new ScoreboardParser.Entry[]{new ScoreboardParser.Entry(), new ScoreboardParser.Entry()};
   private static int intValue;

   public static boolean isActive() {
      return StreamerModeModule.INSTANCE.isEnabled();
   }

   private static boolean method7066() {
      return StreamerModeModule.INSTANCE.method6289();
   }

   private static void method7067() {
      if (!flag) {
         Random random = new Random();
         longValue = method7068(random);
         longValue2 = method7068(random);
         flag = true;
      }
   }

   private static long method7068(Random random) {
      long l = (50000L + random.nextInt(850000)) / 512L * 512L;
      return random.nextBoolean() ? l : -l;
   }

   public static int method7069(int x) {
      method7067();
      return (int)(x + longValue);
   }

   public static int method7070(int z) {
      method7067();
      return (int)(z + longValue2);
   }

   public static List<String> method7071(List<String> strings) {
      if (isActive() && strings != null) {
         List<String> strings2 = new ArrayList<>(strings.size());

         for (String input : strings) {
            strings2.add(method7072(input));
         }

         return strings2;
      } else {
         return strings;
      }
   }

   private static String method7072(String input) {
      if (input == null) {
         return null;
      } else {
         Matcher matcher = pattern2.matcher(input);
         if (!matcher.find()) {
            return input;
         } else {
            method7067();
            boolean equalsIgnoreCase = matcher.group(1).equalsIgnoreCase("Chunk");
            long l = equalsIgnoreCase ? longValue >> 4 : longValue;
            long l2 = equalsIgnoreCase ? longValue2 >> 4 : longValue2;
            Matcher matcher2 = pattern3.matcher(input);
            StringBuilder sb = new StringBuilder(input.length());

            for (int i = 0; matcher2.find(); i++) {
               String group = matcher2.group();

               long l3 = switch (i) {
                  case 0 -> l;
                  default -> 0L;
                  case 2 -> l2;
                  case 5 -> equalsIgnoreCase ? longValue >> 9 : 0L;
                  case 6 -> equalsIgnoreCase ? longValue2 >> 9 : 0L;
               };
               String str = group;
               if (l3 != 0L) {
                  try {
                     str = group.contains(".")
                        ? String.format(Locale.US, "%." + method7073(group) + "f", Double.parseDouble(group) + l3)
                        : String.valueOf(Long.parseLong(group) + l3);
                  } catch (NumberFormatException e) {
                  }
               }

               matcher2.appendReplacement(sb, Matcher.quoteReplacement(str));
            }

            matcher2.appendTail(sb);
            return sb.toString();
         }
      }
   }

   private static int method7073(String group) {
      return group.length() - group.indexOf(46) - 1;
   }

   @Nullable
   public static Component method7074(@Nullable Component text) {
      if (text != null && isActive()) {
         Language language = Language.getInstance();
         boolean flag = method7066();
         Component component = StreamerModeModule.INSTANCE.getText4();
         int n = StreamerModeModule.INSTANCE.getIntValue32();
         Component component2 = StreamerModeModule.INSTANCE.getText32();
         String str = method7087();

         for (ScoreboardParser.Entry entry : entrys) {
            if (entry.text == text
               && entry.language == language
               && entry.flag == flag
               && entry.text2 == component
               && entry.intValue == n
               && entry.text3 == component2
               && str.equals(entry.text4)) {
               return entry.text5;
            }
         }

         Component component3 = method7075(text);
         ScoreboardParser.Entry entry = entrys[intValue];
         intValue = (intValue + 1) % entrys.length;
         entry.text = text;
         entry.language = language;
         entry.flag = flag;
         entry.text2 = component;
         entry.intValue = n;
         entry.text3 = component2;
         entry.text4 = str;
         entry.text5 = component3;
         return component3;
      } else {
         return text;
      }
   }

   private static Component method7075(Component text) {
      var datas = method7093(text);
      List<List<ScoreboardParser.Data>> datases = method7094(datas);
      List<List<Data>> datases2 = new ArrayList<>(datases.size());
      boolean flag = false;

      for (List list : datases) {
         String input = method7098(list);
         String str = input.trim();
         if (method7090(str)) {
            if (method7066()) {
               Component header = method7088(list, str);
               if (header != null) {
                  datases2.add(method7093(header));
                  flag = true;
                  continue;
               }
            }

            flag = true;
         } else {
            var list2 = method7092(list, input, method7087());
            if (!list2.equals(list)) {
               flag = true;
            }

            datases2.add(list2);
         }
      }

      return flag ? method7095(datases2) : text;
   }

   @Nullable
   public static Component method7076(@Nullable Component header) {
      if (isActive() && header != null) {
         String string = header.getString();
         if (method7080(string)) {
            if (method7066()) {
               Component component = StreamerModeModule.INSTANCE.getText3();
               if (component != null) {
                  return component.copy();
               } else {
                  var datas = method7093(header);
                  return method7095(List.of(method7092(datas, method7098(datas), method7087())));
               }
            } else {
               return null;
            }
         } else if (!method7081(string)) {
            return header;
         } else if (method7066()) {
            Component component = StreamerModeModule.INSTANCE.getText22();
            return (Component)(component != null ? component.copy() : method7089(header, StreamerModeModule.INSTANCE.getIntValue32()));
         } else {
            return null;
         }
      } else {
         return header;
      }
   }

   public static boolean method7077(@Nullable Component header) {
      return header == null ? false : method7076(header) == null;
   }

   public static boolean method7078(@Nullable Component formatNameForTeam) {
      return formatNameForTeam == null ? false : method7080(formatNameForTeam.getString());
   }

   public static boolean method7079(@Nullable Component formatNameForTeam) {
      return formatNameForTeam == null ? false : method7081(formatNameForTeam.getString());
   }

   private static boolean method7080(String string) {
      return string.contains("-◆-") && pattern.matcher(string).find();
   }

   private static boolean method7081(String string) {
      return string.toLowerCase(Locale.ROOT).startsWith("онлайн:");
   }

   @Nullable
   public static Component method7082(@Nullable Component header) {
      if (header == null) {
         return null;
      } else {
         for (List list : method7094(method7093(header))) {
            String input = method7098(list).trim();
            if (method7091(input)) {
               return method7095(List.of(list));
            }
         }

         return null;
      }
   }

   @Nullable
   public static String method7083(@Nullable Component text) {
      if (text == null) {
         return null;
      } else {
         Matcher matcher = pattern.matcher(text.getString());
         if (!matcher.find()) {
            return null;
         } else {
            String str = matcher.group().trim();
            return str.isEmpty() ? null : str;
         }
      }
   }

   @Nullable
   public static Component method7084(@Nullable Component header) {
      if (header == null) {
         return null;
      } else {
         for (List list : method7094(method7093(header))) {
            String str = method7098(list).trim();
            if (str.toLowerCase(Locale.ROOT).startsWith("онлайн:")) {
               return method7095(List.of(list));
            }
         }

         return null;
      }
   }

   public static int method7085(@Nullable Component formatNameForTeam) {
      if (formatNameForTeam == null) {
         return -1;
      } else {
         Matcher matcher = pattern4.matcher(formatNameForTeam.getString());
         return matcher.find() ? Integer.parseInt(matcher.group(1)) : -1;
      }
   }

   public static Component method7086(Component header) {
      var datas = method7093(header);
      return method7095(List.of(method7092(datas, method7098(datas), "лайт")));
   }

   private static String method7087() {
      if (method7066()) {
         String str = StreamerModeModule.INSTANCE.getText5();
         if (str != null && !str.isEmpty()) {
            return str;
         }
      }

      return "лайт";
   }

   @Nullable
   private static Component method7088(List<ScoreboardParser.Data> datas, String input) {
      String lowerCase = input.toLowerCase(Locale.ROOT);
      if (lowerCase.equals("анархия")) {
         return method7095(List.of(datas));
      } else if (lowerCase.startsWith("онлайн:")) {
         Component component = StreamerModeModule.INSTANCE.getText4();
         return (Component)(component != null ? component.copy() : method7089(method7095(List.of(datas)), StreamerModeModule.INSTANCE.getIntValue32()));
      } else if (method7091(input)) {
         Component component = StreamerModeModule.INSTANCE.getText32();
         return (Component)(component != null ? component.copy() : method7095(List.of(method7092(datas, method7098(datas), method7087()))));
      } else {
         return null;
      }
   }

   private static Component method7089(Component header, int n) {
      if (n <= 0) {
         return header.copy();
      } else {
         String string = header.getString();
         Matcher matcher = pattern4.matcher(string);
         if (!matcher.find()) {
            return header.copy();
         } else {
            var datas = method7093(header);
            int start = matcher.start(1);
            int end = matcher.end(1);
            String str = String.valueOf(n);
            List<ScoreboardParser.Data> datas2 = new ArrayList<>(datas.size() - (end - start) + str.length());

            for (int i = 0; i < start && i < datas.size(); i++) {
               datas2.add((ScoreboardParser.Data)datas.get(i));
            }

            Style style = start < datas.size() ? ((ScoreboardParser.Data)datas.get(start)).style() : Style.EMPTY;

            for (int i = 0; i < str.length(); i++) {
               datas2.add(new ScoreboardParser.Data(str.charAt(i), style));
            }

            for (int i = end; i < datas.size(); i++) {
               datas2.add((ScoreboardParser.Data)datas.get(i));
            }

            return method7095(List.of(datas2));
         }
      }
   }

   private static boolean method7090(String input) {
      if (input.isEmpty()) {
         return false;
      } else {
         String lowerCase = input.toLowerCase(Locale.ROOT);
         return lowerCase.equals("анархия") || lowerCase.startsWith("онлайн:") || method7091(input);
      }
   }

   private static boolean method7091(String input) {
      if (input.isEmpty()) {
         return false;
      } else {
         String str = pattern5.matcher(input).replaceAll("");
         return pattern.matcher(input).matches() || pattern.matcher(str).matches();
      }
   }

   private static List<ScoreboardParser.Data> method7092(List<ScoreboardParser.Data> datas, String input, String str) {
      Matcher matcher = pattern.matcher(input);
      if (!matcher.find()) {
         return datas;
      } else {
         List<Data> datas2 = new ArrayList<>(datas.size());
         matcher.reset();

         int end;
         for (end = 0; matcher.find(); end = matcher.end()) {
            datas2.addAll(datas.subList(end, matcher.start()));
            Style style = ((ScoreboardParser.Data)datas.get(matcher.start())).style();

            for (int i = 0; i < str.length(); i++) {
               datas2.add(new ScoreboardParser.Data(str.charAt(i), style));
            }
         }

         datas2.addAll(datas.subList(end, datas.size()));
         return datas2;
      }
   }

   private static List<ScoreboardParser.Data> method7093(Component header) {
      ArrayList<ScoreboardParser.Data> datas = new ArrayList<>();
      FormattedCharSequence visualOrderText = header.getVisualOrderText();
      visualOrderText.accept((var1x, var2x, codePoint) -> {
         String str = new String(Character.toChars(codePoint));

         for (int i = 0; i < str.length(); i++) {
            datas.add(new ScoreboardParser.Data(str.charAt(i), var2x));
         }

         return true;
      });
      return datas;
   }

   private static List<List<ScoreboardParser.Data>> method7094(List<ScoreboardParser.Data> datas) {
      ArrayList<List<ScoreboardParser.Data>> datases = new ArrayList<>();
      ArrayList arrayList = new ArrayList();

      for (ScoreboardParser.Data data : datas) {
         if (data.getCharValue() == '\n') {
            datases.add(arrayList);
            arrayList = new ArrayList();
         } else {
            arrayList.add(data);
         }
      }

      datases.add(arrayList);
      return datases;
   }

   private static Component method7095(List<List<ScoreboardParser.Data>> datases) {
      MutableComponent empty = Component.empty();
      Style style = Style.EMPTY;

      for (int i = 0; i < datases.size(); i++) {
         if (i > 0) {
            empty.append(Component.literal("\n").setStyle(style));
         }

         List list = (List)datases.get(i);
         if (!list.isEmpty()) {
            empty.append(method7096(list));
            style = ((ScoreboardParser.Data)list.getLast()).style();
         }
      }

      return empty;
   }

   private static MutableComponent method7096(List<ScoreboardParser.Data> datas) {
      MutableComponent empty = Component.empty();
      Style style = null;
      StringBuilder builder = new StringBuilder();

      for (ScoreboardParser.Data data : datas) {
         if (style == null || !data.style().equals(style)) {
            method7097(empty, builder, style);
            style = data.style();
         }

         builder.append(data.getCharValue());
      }

      method7097(empty, builder, style);
      return empty;
   }

   private static void method7097(MutableComponent empty, StringBuilder builder, Style style) {
      if (!builder.isEmpty()) {
         empty.append(Component.literal(builder.toString()).setStyle(style == null ? Style.EMPTY : style));
         builder.setLength(0);
      }
   }

   private static String method7098(List<ScoreboardParser.Data> datas) {
      StringBuilder builder = new StringBuilder(datas.size());

      for (ScoreboardParser.Data data : datas) {
         builder.append(data.getCharValue());
      }

      return builder.toString();
   }

   @Generated
   private ScoreboardParser() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }

   private static final class Data {
      private final char charValue;
      private final Style style;

      public char charValue() {
         return this.charValue;
      }

      public Style style() {
         return this.style;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.charValue, other.charValue) && java.util.Objects.equals(this.style, other.style);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(charValue, style);
      }

      @Override
      public String toString() {
         return "Data[" + "charValue=" + this.charValue + ", " + "style=" + this.style + "]";
      }

      private Data(char charValue, Style style) {
         this.charValue = charValue;
         this.style = style;
      }

      public char getCharValue() {
         return this.charValue;
      }
   }

   private static final class Entry {
      public Component text;
      public Language language;
      public boolean flag;
      public Component text2;
      public int intValue;
      public Component text3;
      public String text4;
      public Component text5;
   }
}
