package org.zenith.utility.misc;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Data2 {
   private final String text;
   private final boolean flag;
   private final int intValue;

   public String text() {
      return this.text;
   }

   public boolean flag() {
      return this.flag;
   }

   public int intValue() {
      return this.intValue;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof Data2 other)) return false;
      return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.flag, other.flag) && java.util.Objects.equals(this.intValue, other.intValue);
   }

   @Override
   public int hashCode() {
      return java.util.Objects.hash(text, flag, intValue);
   }

   @Override
   public String toString() {
      return "Data2[" + "text=" + this.text + ", " + "flag=" + this.flag + ", " + "intValue=" + this.intValue + "]";
   }
   private static final int CONST_intValue = 322;
   private static final Pattern pattern = Pattern.compile("§[0-9a-fk-orx]", 322);
   private static final Pattern pattern2 = Pattern.compile(
      method1517("(?<![\\p{L}\\p{N}])(?:pvp|пвп|бой|бою|боевой\\s+режим|режим\\s+боя|combat)(?![\\p{L}\\p{N}])"), 322
   );
   private static final Pattern pattern3 = Pattern.compile(
      method1517(
         "(?<![\\p{L}\\p{N}])(?:выключ[её]н[аоы]?|отключ[её]н[аоы]?|заверш[её]н[аоы]?|закончен[аоы]?|неактивен|не\\s+активен|вне\\s+боя|не\\s+в\\s+бою|вышли\\s+из|покинули|disabled|ended|expired|not\\s+in\\s+combat|off)(?![\\p{L}\\p{N}])"
      ),
      322
   );
   private static final Pattern pattern4 = Pattern.compile(
      method1517("(?:будет\\s+(?:выключ|отключ|заверш|закончен)|(?:выключ|отключ|заверш|закончен)\\p{L}*\\s+через)"), 322
   );
   private static final Pattern pattern5 = Pattern.compile("(?<![\\d:.])(\\d{1,3}):([0-5]\\d)(?![\\d:.])");
   private static final Pattern pattern6 = Pattern.compile(
      "(?<![\\d.,:])(\\d{1,4})(?![\\d.,:])\\s*(?:[сc][еe][кk](?:[уy][нnh][дd](?:ы|а|у)?)?\\.?|seconds?|secs?\\.?|[sсc])(?=$|[^\\p{L}\\p{N}])", 322
   );
   private static final Pattern pattern7 = Pattern.compile("\\d+", 322);
   private static final Pattern pattern8 = Pattern.compile("\\d\\s*:\\s*\\d");
   private static final Pattern pattern9 = Pattern.compile("(?<![\\p{L}])(?:мин(?:ут[аыу]?)?|minutes?|mins?)\\.?(?![\\p{L}])", 322);

   public Data2(String text, boolean flag, int intValue) {
      this.text = text;
      this.flag = flag;
      this.intValue = intValue;
   }

   public static Data2 method1515(String string) {
      String text = method1518(string);
      String lowerCase = text.toLowerCase(Locale.ROOT);
      String input = method1517(lowerCase);
      boolean flag = pattern2.matcher(input).find();
      if (flag && pattern3.matcher(input).find() && !pattern4.matcher(input).find()) {
         return new Data2(text, false, -1);
      } else {
         int intValue = method1516(lowerCase, flag);
         return new Data2(text, flag && intValue != 0, intValue);
      }
   }

   private static int method1516(String lowerCase, boolean flag) {
      Matcher matcher = pattern5.matcher(lowerCase);
      if (matcher.find()) {
         return Integer.parseInt(matcher.group(1)) * 60 + Integer.parseInt(matcher.group(2));
      } else if (!pattern9.matcher(lowerCase).find() && !pattern8.matcher(lowerCase).find()) {
         Matcher matcher2 = pattern6.matcher(lowerCase);
         if (matcher2.find()) {
            int n = Integer.parseInt(matcher2.group(1));
            return matcher2.find() ? -1 : n;
         } else if (flag && lowerCase.indexOf(37) < 0 && lowerCase.indexOf(65285) < 0) {
            Matcher matcher3 = pattern7.matcher(lowerCase);
            if (!matcher3.find()) {
               return -1;
            } else {
               String group = matcher3.group();
               if (group.length() > 4) {
                  return -1;
               } else {
                  int n = Integer.parseInt(group);
                  return matcher3.find() ? -1 : n;
               }
            }
         } else {
            return -1;
         }
      } else {
         return -1;
      }
   }

   private static String method1517(String lowerCase) {
      StringBuilder builder = new StringBuilder(lowerCase.length());
      String str = "асеорхукнвтм";
      String str2 = "aceopxykhbtm";

      for (int i = 0; i < lowerCase.length(); i++) {
         char charAt = lowerCase.charAt(i);
         int indexOf = str.indexOf(charAt);
         builder.append(indexOf < 0 ? charAt : str2.charAt(indexOf));
      }

      return builder.toString();
   }

   private static String method1518(String string) {
      if (string != null && !string.isEmpty()) {
         String str = pattern.matcher(string).replaceAll("");
         StringBuilder builder = new StringBuilder(str.length());
         boolean flag = false;
         int index = 0;

         while (index < str.length()) {
            int codePointAt = str.codePointAt(index);
            index += Character.charCount(codePointAt);
            int type = Character.getType(codePointAt);
            if (type != 18 && type != 16) {
               if (!Character.isWhitespace(codePointAt) && !Character.isSpaceChar(codePointAt)) {
                  if (flag) {
                     builder.append(' ');
                     flag = false;
                  }

                  builder.appendCodePoint(codePointAt);
               } else {
                  flag = !builder.isEmpty();
               }
            }
         }

         return builder.toString();
      } else {
         return "";
      }
   }

   public String value() {
      if (this.intValue < 0) {
         return this.flag ? "" : null;
      } else {
         return this.intValue >= 60 ? this.intValue / 60 + ":" + (this.intValue % 60 < 10 ? "0" : "") + this.intValue % 60 : this.intValue + "s";
      }
   }

   public String title() {
      return this.text;
   }

   public boolean isFlag() {
      return this.flag;
   }

   public int getIntValue() {
      return this.intValue;
   }
}
