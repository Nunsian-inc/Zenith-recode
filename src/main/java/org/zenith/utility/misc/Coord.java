package org.zenith.utility.misc;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class Coord {
   private static final String text = "[z:";
   private static final char CONST_charValue = ']';
   private static final int CONST_intValue = 900;
   public static final String text2 = "coord";
   public static final String text3 = "inv";
   public static final String text4 = "st";
   public static final String text5 = "at";
   private final String text6;
   private final Map<String, String> items;

   private Coord(String lowerCase, Map<String, String> items) {
      this.text6 = lowerCase;
      this.items = items;
   }

   public String kind() {
      return this.text6;
   }

   public String method5169(String str, String str2) {
      String str3 = this.items.get(str);
      return str3 != null && !str3.isEmpty() ? str3 : str2;
   }

   public int method5170(String str, int n) {
      try {
         String str2 = this.items.get(str);
         return str2 != null && !str2.isEmpty() ? Integer.parseInt(str2.trim()) : n;
      } catch (NumberFormatException e) {
         return n;
      }
   }

   public static Coord.Entry method5171(String text) {
      return new Coord.Entry(text);
   }

   public static Coord.Data method5172(String body) {
      if (body == null || body.isEmpty()) {
         return new Coord.Data("", List.of());
      } else if (body.indexOf("[z:") < 0) {
         return new Coord.Data(body, List.of());
      } else {
         ArrayList items = new ArrayList();
         StringBuilder builder = new StringBuilder();
         boolean broken = false;
         int start = 0;

         while (start < body.length()) {
            int indexOf = body.indexOf("[z:", start);
            if (indexOf < 0) {
               builder.append(body, start, body.length());
               break;
            }

            int endIndex = body.indexOf(93, indexOf);
            if (endIndex < 0 || endIndex - indexOf > 900) {
               if (method5173(body, indexOf)) {
                  builder.append(body, start, indexOf);
                  broken = true;
               } else {
                  builder.append(body, start, body.length());
               }
               break;
            }

            Coord coord = method5175(body.substring(indexOf + "[z:".length(), endIndex));
            if (coord == null) {
               builder.append(body, start, endIndex + 1);
            } else {
               builder.append(body, start, indexOf);
               items.add(coord);
            }

            start = endIndex + 1;
         }

         return new Coord.Data(builder.toString().strip(), items, broken);
      }
   }

   private static boolean method5173(String body, int indexOf) {
      int index = indexOf + "[z:".length();

      int n;
      for (n = 0; index < body.length() && body.charAt(index) >= 'a' && body.charAt(index) <= 'z'; n++) {
         index++;
      }

      return n > 0 && index < body.length() && body.charAt(index) == '|';
   }

   public static String method5174(String body, int n) {
      if (body != null && n > 0) {
         if (body.length() <= n) {
            return body;
         } else {
            int n2 = n;
            int fromIndex = 0;

            while (fromIndex < body.length()) {
               int indexOf = body.indexOf("[z:", fromIndex);
               if (indexOf < 0 || indexOf >= n2) {
                  break;
               }

               int indexOf2 = body.indexOf(93, indexOf);
               if (indexOf2 < 0 || indexOf2 >= n2) {
                  n2 = indexOf;
                  break;
               }

               fromIndex = indexOf2 + 1;
            }

            return body.substring(0, Math.max(0, n2)).stripTrailing();
         }
      } else {
         return "";
      }
   }

   private static Coord method5175(String str) {
      if (str.isEmpty()) {
         return null;
      } else {
         String[] strings = str.split("\\|");
         String lowerCase = strings[0].trim().toLowerCase(Locale.ROOT);
         if (!lowerCase.isEmpty() && lowerCase.chars().allMatch(var0x -> var0x >= 97 && var0x <= 122)) {
            LinkedHashMap items = new LinkedHashMap();

            for (int i = 1; i < strings.length; i++) {
               int indexOf = strings[i].indexOf(61);
               if (indexOf > 0) {
                  items.put(strings[i].substring(0, indexOf).trim().toLowerCase(Locale.ROOT), method5176(strings[i].substring(indexOf + 1)));
               }
            }

            return new Coord(lowerCase, items);
         } else {
            return null;
         }
      }
   }

   private static String escape(String str) {
      StringBuilder builder = new StringBuilder(str.length() + 8);

      for (int i = 0; i < str.length(); i++) {
         char charAt = str.charAt(i);
         switch (charAt) {
            case '\t':
            case '\n':
            case '\r':
               builder.append(' ');
               break;
            case '%':
               builder.append("%25");
               break;
            case '[':
               builder.append("%5B");
               break;
            case ']':
               builder.append("%5D");
               break;
            case '|':
               builder.append("%7C");
               break;
            default:
               builder.append(charAt);
         }
      }

      return builder.toString();
   }

   private static String method5176(String str) {
      if (str.indexOf(37) < 0) {
         return str;
      } else {
         StringBuilder builder = new StringBuilder(str.length());

         for (int i = 0; i < str.length(); i++) {
            char charAt = str.charAt(i);
            if (charAt == '%' && i + 2 < str.length()) {
               try {
                  builder.append((char)Integer.parseInt(str.substring(i + 1, i + 3), 16));
                  i += 2;
                  continue;
               } catch (NumberFormatException e) {
               }
            }

            builder.append(charAt);
         }

         return builder.toString();
      }
   }

   public static final class Data {
      private final String text;
      private final List<Coord> items;
      private final boolean broken;

      public String text() {
         return this.text;
      }

      public List<Coord> items() {
         return this.items;
      }

      public boolean broken() {
         return this.broken;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.items, other.items) && java.util.Objects.equals(this.broken, other.broken);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, items, broken);
      }

      @Override
      public String toString() {
         return "Data[" + "text=" + this.text + ", " + "items=" + this.items + ", " + "broken=" + this.broken + "]";
      }

      public Data(String body, List<Coord> items) {
         this(body, items, false);
      }

      public Data(String text, List<Coord> items, boolean broken) {
         this.text = text;
         this.items = items;
         this.broken = broken;
      }

      public Coord method5179(String anObject) {
         for (Coord coord : this.items) {
            if (coord.kind().equals(anObject)) {
               return coord;
            }
         }

         return null;
      }

      public boolean method5180(String anObject) {
         return this.method5179(anObject) != null;
      }

      public List<Coord> getItems() {
         return this.items;
      }

      public boolean isBroken() {
         return this.broken;
      }
   }

   public static final class Entry {
      private final String text;
      private final Map<String, String> items = new LinkedHashMap<>();

      private Entry(String text) {
         this.text = text;
      }

      public Coord.Entry method5177(String str, String dimension) {
         if (dimension != null && !dimension.isBlank()) {
            this.items.put(str, dimension.strip());
         }

         return this;
      }

      public Coord.Entry method5178(String str, int z) {
         this.items.put(str, Integer.toString(z));
         return this;
      }

      @Override
      public String toString() {
         StringBuilder builder = new StringBuilder("[z:").append(this.text);

         for (Map.Entry entry : this.items.entrySet()) {
            builder.append('|').append((String)entry.getKey()).append('=').append(Coord.escape((String)entry.getValue()));
         }

         return builder.append(']').toString();
      }
   }
}
