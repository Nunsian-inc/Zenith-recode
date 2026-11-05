package org.zenith.utility.misc;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import lombok.Generated;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import org.zenith.MinecraftAccess;

public final class TextUtils implements MinecraftAccess {
   private static final String text = "ёйцукенгшщзхъфывапролджэячсмитьбю";
   private static final String text2 = "`qwertyuiop[]asdfghjkl;'zxcvbnm,.";

   public static String method2104(double d) {
      return String.format(Locale.US, "%.1f", d);
   }

   public static Component method2105(Component motd) {
      FormattedCharSequence visualOrderText = motd.getVisualOrderText();
      MutableComponent empty = Component.empty();
      StringBuilder builder = new StringBuilder();
      Style[] styles = new Style[]{Style.EMPTY};
      boolean[] flags = new boolean[]{true};
      visualOrderText.accept((var4x, var5x, codePoint) -> {
         if (flags[0] && Character.isWhitespace(codePoint)) {
            return true;
         } else {
            flags[0] = false;
            if (!var5x.equals(styles[0])) {
               if (builder.length() > 0) {
                  empty.append(Component.literal(builder.toString()).setStyle(styles[0]));
                  builder.setLength(0);
               }

               styles[0] = var5x;
            }

            builder.appendCodePoint(codePoint);
            return true;
         }
      });
      if (builder.length() > 0) {
         empty.append(Component.literal(builder.toString()).setStyle(styles[0]));
      }

      return empty;
   }

   public static MutableComponent method2106(Component component, Component component2, Component component3) {
      String string = component.getString();
      String str = component2.getString();
      if (str != null && !str.isEmpty()) {
         int indexOf = string.indexOf(str);
         if (indexOf == -1) {
            return component.copy();
         } else {
            int endIndex = indexOf + str.length();
            int codePointCount = string.codePointCount(0, indexOf);
            int n = string.codePointCount(0, endIndex);
            FormattedCharSequence visualOrderText = component.getVisualOrderText();
            ArrayList arrayList = new ArrayList();
            visualOrderText.accept((var1x, var2x, var3x) -> {
               arrayList.add(new TextUtils.Data(new String(Character.toChars(var3x)), var2x));
               return true;
            });
            MutableComponent empty = Component.empty();

            for (int i = 0; i < arrayList.size(); i++) {
               if (i == codePointCount) {
                  empty.append(component3.copy());
               }

               if (i < codePointCount || i >= n) {
                  TextUtils.Data data = (TextUtils.Data)arrayList.get(i);
                  empty.append(Component.literal(data.text()).setStyle(data.style()));
               }
            }

            return empty;
         }
      } else {
         return component.copy();
      }
   }

   public static MutableComponent method2107(Component decorateName, String scoreboardName, Component component) {
      if (decorateName != null && scoreboardName != null && !scoreboardName.isEmpty()) {
         FormattedCharSequence visualOrderText = decorateName.getVisualOrderText();
         ArrayList arrayList = new ArrayList();
         StringBuilder builder = new StringBuilder();
         visualOrderText.accept((var2x, var3x, var4x) -> {
            String var5x = new String(Character.toChars(var4x));
            arrayList.add(new TextUtils.Data(var5x, var3x));
            builder.append(var5x);
            return true;
         });
         String string = builder.toString();
         int indexOf = string.indexOf(scoreboardName);
         if (indexOf == -1) {
            return decorateName.copy();
         } else {
            int endIndex = indexOf + scoreboardName.length();
            int codePointCount = string.codePointCount(0, endIndex);
            MutableComponent empty = Component.empty();
            empty.append(component.copy());

            for (int i = codePointCount; i < arrayList.size(); i++) {
               TextUtils.Data data = (TextUtils.Data)arrayList.get(i);
               empty.append(Component.literal(data.text()).setStyle(data.style()));
            }

            return empty;
         }
      } else {
         return decorateName == null ? Component.empty() : decorateName.copy();
      }
   }

   public static Component method2108(Component tabListDisplayName, boolean flag) {
      FormattedCharSequence visualOrderText = tabListDisplayName.getVisualOrderText();
      MutableComponent empty = Component.empty();
      StringBuilder builder = new StringBuilder();
      Style[] styles = new Style[]{Style.EMPTY};
      int[] ints = new int[]{0};
      boolean[] flags = new boolean[]{false};
      visualOrderText.accept((var5x, var6x, var7x) -> {
         if (ints[0] > 2) {
            flags[0] = true;
            return false;
         } else {
            if (!var6x.equals(styles[0])) {
               if (!builder.isEmpty()) {
                  empty.append(Component.literal(builder.toString()).setStyle(styles[0]));
                  builder.setLength(0);
               }

               styles[0] = var6x;
            }

            builder.appendCodePoint(var7x);
            if (Character.isWhitespace(var7x)) {
               ints[0]++;
            }

            return true;
         }
      });
      if (builder.length() > 0) {
         empty.append(Component.literal(builder.toString()).setStyle(styles[0]));
      }

      if (flag && flags[0]) {
         Style style = styles[0] != null ? styles[0] : tabListDisplayName.getStyle();
         empty.append(Component.literal("…").setStyle(style));
      }

      return empty;
   }

   public static Component method2109(Component component, int n, boolean flag) {
      if (n <= 0) {
         return Component.empty();
      } else {
         FormattedCharSequence visualOrderText = component.getVisualOrderText();
         MutableComponent empty = Component.empty();
         StringBuilder builder = new StringBuilder();
         Style[] styles = new Style[]{Style.EMPTY};
         int[] ints = new int[]{0};
         boolean[] flags = new boolean[]{false};
         visualOrderText.accept((var6x, var7x, var8x) -> {
            if (ints[0] >= n) {
               flags[0] = true;
               return false;
            } else {
               if (!var7x.equals(styles[0])) {
                  if (builder.length() > 0) {
                     empty.append(Component.literal(builder.toString()).setStyle(styles[0]));
                     builder.setLength(0);
                  }

                  styles[0] = var7x;
               }

               builder.appendCodePoint(var8x);
               ints[0]++;
               return true;
            }
         });
         if (builder.length() > 0) {
            empty.append(Component.literal(builder.toString()).setStyle(styles[0]));
         }

         if (flag && flags[0]) {
            Style style = styles[0] != null ? styles[0] : component.getStyle();
            return empty.append(Component.literal("…").setStyle(style));
         } else {
            return empty;
         }
      }
   }

   public static MutableComponent method2110(Component name, String scoreboardName, String zenith$protectedRowsName) {
      return method2112(name, scoreboardName, var1x -> {
         Style var2x = var1x.get(0).style() != null ? var1x.get(0).style() : Style.EMPTY;
         return Component.literal(zenith$protectedRowsName).setStyle(var2x);
      });
   }

   public static MutableComponent method2111(Component decorateName, String scoreboardName, Component component) {
      return method2112(decorateName, scoreboardName, var1x -> component.copy());
   }

   private static MutableComponent method2112(Component name, String scoreboardName, Function<List<TextUtils.Data>, Component> function) {
      FormattedCharSequence visualOrderText = name.getVisualOrderText();
      MutableComponent empty = Component.empty();
      ArrayList<TextUtils.Data> datas = new ArrayList();
      Runnable runnable = () -> {
         if (!datas.isEmpty()) {
            StringBuilder var4x = new StringBuilder();

            for (TextUtils.Data var6x : datas) {
               var4x.append(var6x.text());
            }

            String string = var4x.toString();
            if (string.equals(scoreboardName)) {
               empty.append((Component)function.apply(datas));
            } else {
               for (TextUtils.Data data : datas) {
                  empty.append(Component.literal(data.text()).setStyle(data.style()));
               }
            }

            datas.clear();
         }
      };
      visualOrderText.accept((var3x, var4x, var5x) -> {
         String var6x = new String(Character.toChars(var5x));
         boolean flag = Character.isLetterOrDigit(var5x) || var5x == 95;
         if (flag) {
            datas.add(new TextUtils.Data(var6x, var4x));
         } else {
            runnable.run();
            empty.append(Component.literal(var6x).setStyle(var4x));
         }

         return true;
      });
      runnable.run();
      return empty;
   }

   public static MutableComponent method2113(Component nameForDisplay, String str) {
      if (str != null && !str.isEmpty()) {
         FormattedCharSequence visualOrderText = nameForDisplay.getVisualOrderText();
         ArrayList arrayList = new ArrayList();
         StringBuilder builder = new StringBuilder();
         visualOrderText.accept((var2x, var3x, var4x) -> {
            String var5x = new String(Character.toChars(var4x));
            arrayList.add(new TextUtils.Data(var5x, var3x));
            builder.append(var5x);
            return true;
         });
         String string = builder.toString();
         int indexOf = string.indexOf(str);
         if (indexOf == -1) {
            return Component.empty();
         } else {
            MutableComponent empty = Component.empty();

            for (int i = 0; i < indexOf; i++) {
               TextUtils.Data data = (TextUtils.Data)arrayList.get(i);
               empty.append(Component.literal(data.text()).setStyle(data.style()));
            }

            return empty;
         }
      } else {
         return Component.empty();
      }
   }

   public static MutableComponent method2114(Component name, String str, Component text) {
      String string = name.getString();
      int indexOf = string.indexOf(str);
      if (indexOf == -1) {
         return name.copy();
      } else {
         FormattedCharSequence visualOrderText = name.getVisualOrderText();
         ArrayList arrayList = new ArrayList();
         StringBuilder builder = new StringBuilder();
         visualOrderText.accept((var2x, var3x, var4x) -> {
            String var5x = new String(Character.toChars(var4x));
            arrayList.add(new TextUtils.Data(var5x, var3x));
            builder.append(var5x);
            return true;
         });
         string = builder.toString();
         int index = indexOf + str.length();

         while (index < string.length()) {
            int codePointAt = string.codePointAt(index);
            if (!Character.isWhitespace(codePointAt)) {
               break;
            }

            index += Character.charCount(codePointAt);
         }

         if (index >= string.length()) {
            return name.copy();
         } else {
            int n = index;

            while (n < string.length()) {
               int codePointAt = string.codePointAt(n);
               if (Character.isWhitespace(codePointAt)) {
                  break;
               }

               n += Character.charCount(codePointAt);
            }

            MutableComponent component = method2115(text, 1);
            if (component.getString().isEmpty()) {
               component = method2115(text, 0);
            }

            int codePointCount = string.codePointCount(0, index);
            int codePointCount2 = string.codePointCount(0, n);
            MutableComponent empty = Component.empty();

            for (int i = 0; i < codePointCount; i++) {
               TextUtils.Data data = (TextUtils.Data)arrayList.get(i);
               empty.append(Component.literal(data.text()).setStyle(data.style()));
            }

            empty.append(component.copy());

            for (int i = codePointCount2; i < arrayList.size(); i++) {
               TextUtils.Data data = (TextUtils.Data)arrayList.get(i);
               empty.append(Component.literal(data.text()).setStyle(data.style()));
            }

            return empty;
         }
      }
   }

   private static MutableComponent method2115(Component text, int n) {
      FormattedCharSequence visualOrderText = text.getVisualOrderText();
      ArrayList arrayList = new ArrayList();
      StringBuilder builder = new StringBuilder();
      visualOrderText.accept((var2x, var3x, var4x) -> {
         String var5x = new String(Character.toChars(var4x));
         arrayList.add(new TextUtils.Data(var5x, var3x));
         builder.append(var5x);
         return true;
      });
      String string = builder.toString();
      int n2 = 0;

      for (int i = 0; i < string.length(); n2++) {
         while (i < string.length()) {
            int codePointAt = string.codePointAt(i);
            if (!Character.isWhitespace(codePointAt)) {
               break;
            }

            i += Character.charCount(codePointAt);
         }

         if (i >= string.length()) {
            break;
         }

         int endIndex = i;

         while (true) {
            if (i < string.length()) {
               int codePointAt = string.codePointAt(i);
               if (!Character.isWhitespace(codePointAt)) {
                  i += Character.charCount(codePointAt);
                  continue;
               }
            }

            if (n2 == n) {
               int codePointCount = string.codePointCount(0, endIndex);
               int codePointCount2 = string.codePointCount(0, i);
               MutableComponent empty = Component.empty();

               for (int j = codePointCount; j < codePointCount2; j++) {
                  TextUtils.Data data = (TextUtils.Data)arrayList.get(j);
                  empty.append(Component.literal(data.text()).setStyle(data.style()));
               }

               return empty;
            }
            break;
         }
      }

      return Component.empty();
   }

   public static MutableComponent method2116(Component text, String str) {
      FormattedCharSequence visualOrderText = text.getVisualOrderText();
      int[] ints = new int[]{0};
      visualOrderText.accept((var1x, var2x, var3x) -> {
         ints[0]++;
         return true;
      });
      if (ints[0] == 0) {
         return text.copy();
      } else {
         int n = ints[0] - 1;
         MutableComponent empty = Component.empty();
         StringBuilder builder = new StringBuilder();
         Style[] styles = new Style[]{null};
         int[] ints2 = new int[]{0};
         visualOrderText.accept((var6x, var7x, var8x) -> {
            boolean flag = ints2[0] == n;
            if (styles[0] == null || !styles[0].equals(var7x)) {
               method2117(empty, builder, styles[0]);
               styles[0] = var7x;
            }

            if (flag) {
            }

            builder.appendCodePoint(var8x);
            ints2[0]++;
            return true;
         });
         method2117(empty, builder, styles[0]);
         return empty;
      }
   }

   private static void method2117(MutableComponent empty, StringBuilder builder, Style style) {
      if (builder.length() != 0) {
         MutableComponent literal = Component.literal(builder.toString());
         if (style != null) {
            literal.setStyle(style);
         }

         empty.append(literal);
         builder.setLength(0);
      }
   }

   public static Component method2118(Component tabListDisplayName, String name, boolean flag) {
      if (name != null && !name.isEmpty()) {
         FormattedCharSequence visualOrderText = tabListDisplayName.getVisualOrderText();
         MutableComponent empty = Component.empty();
         StringBuilder builder = new StringBuilder();
         Style[] styles = new Style[]{Style.EMPTY};
         StringBuilder builder2 = new StringBuilder();
         boolean[] flags = new boolean[]{false};
         visualOrderText.accept((var6x, var7x, var8x) -> {
            String var9x = new String(Character.toChars(var8x));
            if (builder2.toString().contains(name)) {
               flags[0] = true;
               return false;
            } else {
               builder2.append(var9x);
               if (!var7x.equals(styles[0])) {
                  if (builder.length() > 0) {
                     empty.append(Component.literal(builder.toString()).setStyle(styles[0]));
                     builder.setLength(0);
                  }

                  styles[0] = var7x;
               }

               builder.append(var9x);
               return true;
            }
         });
         if (builder.length() > 0) {
            empty.append(Component.literal(builder.toString()).setStyle(styles[0]));
         }

         if (flag && flags[0]) {
            Style style = styles[0] != null ? styles[0] : tabListDisplayName.getStyle();
            empty.append(Component.literal("…").setStyle(style));
         }

         return empty;
      } else {
         return tabListDisplayName;
      }
   }

   public static String method2119(String str) {
      if (str.isEmpty()) {
         return str;
      } else {
         char[] chars = new char[str.length()];

         for (int i = 0; i < str.length(); i++) {
            char charAt = str.charAt(i);
            boolean flag = charAt >= 1040 && charAt <= 1071 || charAt == 1025;
            char lowerCase = Character.toLowerCase(charAt);
            int indexOf = "ёйцукенгшщзхъфывапролджэячсмитьбю".indexOf(lowerCase);
            if (indexOf != -1) {
               char ch = "`qwertyuiop[]asdfghjkl;'zxcvbnm,.".charAt(indexOf);
               chars[i] = flag ? Character.toUpperCase(ch) : ch;
            } else {
               chars[i] = charAt;
            }
         }

         return new String(chars);
      }
   }

   @Generated
   private TextUtils() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }

   public static final class Data {
      private final String text;
      private final Style style;

      public String text() {
         return this.text;
      }

      public Style style() {
         return this.style;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.style, other.style);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, style);
      }

      @Override
      public String toString() {
         return "Data[" + "text=" + this.text + ", " + "style=" + this.style + "]";
      }

      public Data(String text, Style style) {
         this.text = text;
         this.style = style;
      }
   }
}
