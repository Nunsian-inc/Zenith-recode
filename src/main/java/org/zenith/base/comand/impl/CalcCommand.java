package org.zenith.base.comand.impl;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;
import net.minecraft.commands.SharedSuggestionProvider;
import org.zenith.ChatLogger;
import org.zenith.base.comand.api.CommandAbstract;

public class CalcCommand extends CommandAbstract {
   public CalcCommand() {
      super("calc");
   }

   @Override
   public void execute(LiteralArgumentBuilder<SharedSuggestionProvider> literalArgumentBuilder) {
      literalArgumentBuilder.executes(commandContext -> {
         ChatLogger.method2020(ChatLogger.Type.INFO, "Использование: .calc <выражение>");
         ChatLogger.method2020(ChatLogger.Type.INFO, "Пример: .calc (2 + 2) * 2 или .calc 64 * 3.5кк, подробнее: .calc help");
         return 1;
      });
      literalArgumentBuilder.then(literal("help").executes(commandContext -> {
         ChatLogger.method2020(ChatLogger.Type.INFO, "Операторы: + - * / ^ и скобки, умножение можно писать как x или х");
         ChatLogger.method2020(ChatLogger.Type.INFO, "Числа: 1.5 или 1,5; суффиксы к/кк/ккк: 1кк = 1 000 000");
         ChatLogger.method2020(ChatLogger.Type.INFO, "Процент от числа: .calc 250 + 10% = 275, .calc 1кк - 15% = 850 000");
         ChatLogger.method2020(ChatLogger.Type.INFO, "Функции: sqrt, cbrt, abs, round, floor, ceil, sin, cos, tan, ln, log; константы pi, e");
         ChatLogger.method2020(ChatLogger.Type.INFO, "Пример: .calc sqrt(16) + 2 ^ 10 или .calc (27 * 1.8кк) - 10%");
         return 1;
      }));
      literalArgumentBuilder.then(arg("expression", StringArgumentType.greedyString()).executes(var1x -> {
         String str = ((String)var1x.getArgument("expression", String.class)).trim();

         try {
            double d = new CalcCommand.ExpressionParser(str).parse();
            ChatLogger.method2020(ChatLogger.Type.INFO, str + " = " + this.format(d));
         } catch (CalcCommand.CalcException e) {
            ChatLogger.method2020(ChatLogger.Type.ERROR, e.getMessage());
         }

         return 1;
      }));
   }

   private String format(double d) {
      BigDecimal stripTrailingZeros = BigDecimal.valueOf(d).setScale(10, RoundingMode.HALF_UP).stripTrailingZeros();
      if (stripTrailingZeros.compareTo(BigDecimal.ZERO) == 0) {
         stripTrailingZeros = BigDecimal.ZERO;
      }

      String plainString = stripTrailingZeros.toPlainString();
      boolean startsWith = plainString.startsWith("-");
      if (startsWith) {
         plainString = plainString.substring(1);
      }

      int indexOf = plainString.indexOf(46);
      String str = indexOf == -1 ? plainString : plainString.substring(0, indexOf);
      String str2 = indexOf == -1 ? "" : plainString.substring(indexOf);
      StringBuilder builder = new StringBuilder();

      for (int i = 0; i < str.length(); i++) {
         if (i > 0 && (str.length() - i) % 3 == 0) {
            builder.append(' ');
         }

         builder.append(str.charAt(i));
      }

      return (startsWith ? "-" : "") + builder + str2;
   }

   private static final class CalcException extends RuntimeException {
      public CalcException(String message) {
         super(message);
      }
   }

   private static final class ExpressionParser {
      private final String src;
      private int pos;

      public ExpressionParser(String str) {
         this.src = str.toLowerCase(Locale.ROOT)
            .replace(',', '.')
            .replace('x', '*')
            .replace('х', '*')
            .replace('×', '*')
            .replace('÷', '/')
            .replace(':', '/')
            .replace('−', '-');
      }

      public double parse() {
         double expression = this.expression();
         this.skipWs();
         if (this.pos < this.src.length()) {
            throw this.err("Лишний символ '" + this.src.charAt(this.pos) + "'", this.pos);
         } else if (Double.isNaN(expression)) {
            throw new CalcCommand.CalcException("Результат не определён");
         } else if (Double.isInfinite(expression)) {
            throw new CalcCommand.CalcException("Результат слишком большой");
         } else {
            return expression;
         }
      }

      private double expression() {
         double d = resolve(this.term());

         while (true) {
            this.skipWs();
            char c = this.peek();
            if (c != '+' && c != '-') {
               return d;
            }

            this.pos++;
            CalcCommand.Val term = this.term();
            double d2 = term.pct() ? d * term.v() / 100.0 : term.v();
            d = c == '+' ? d + d2 : d - d2;
         }
      }

      private CalcCommand.Val term() {
         CalcCommand.Val unary = this.unary();

         while (true) {
            this.skipWs();
            char c = this.peek();
            if (c != '*' && c != '/') {
               if (c != '(') {
                  return unary;
               }

               unary = this.multiply(unary, this.unary(), false);
            } else {
               this.pos++;
               unary = this.multiply(unary, this.unary(), c == '/');
            }
         }
      }

      private CalcCommand.Val multiply(CalcCommand.Val unary, CalcCommand.Val val, boolean flag) {
         double d = resolve(unary);
         double d2 = resolve(val);
         if (flag) {
            if (d2 == 0.0) {
               throw new CalcCommand.CalcException("Деление на ноль");
            } else {
               return new CalcCommand.Val(d / d2, false);
            }
         } else {
            return new CalcCommand.Val(d * d2, false);
         }
      }

      private CalcCommand.Val unary() {
         this.skipWs();
         char c = this.peek();
         if (c == '+') {
            this.pos++;
            return this.unary();
         } else if (c == '-') {
            this.pos++;
            CalcCommand.Val unary = this.unary();
            return new CalcCommand.Val(-unary.v(), unary.pct());
         } else {
            return this.power();
         }
      }

      private CalcCommand.Val power() {
         CalcCommand.Val postfix = this.postfix();
         this.skipWs();
         if (this.peek() != '^') {
            return postfix;
         } else {
            this.pos++;
            CalcCommand.Val unary = this.unary();
            return new CalcCommand.Val(Math.pow(resolve(postfix), resolve(unary)), false);
         }
      }

      private CalcCommand.Val postfix() {
         double primary = this.primary();
         this.skipWs();
         if (this.peek() == '%') {
            this.pos++;
            return new CalcCommand.Val(primary, true);
         } else {
            return new CalcCommand.Val(primary, false);
         }
      }

      private double primary() {
         this.skipWs();
         if (this.pos >= this.src.length()) {
            throw this.err("Ожидалось число", this.pos);
         } else {
            char charAt = this.src.charAt(this.pos);
            if (charAt == '(') {
               int pos = this.pos++;
               double expression = this.expression();
               this.skipWs();
               if (this.peek() != ')') {
                  throw this.err("Не закрыта скобка", pos);
               } else {
                  this.pos++;
                  return expression;
               }
            } else if (Character.isDigit(charAt) || charAt == '.') {
               return this.number();
            } else if (Character.isLetter(charAt)) {
               return this.identifier();
            } else {
               throw this.err("Непонятный символ '" + charAt + "'", this.pos);
            }
         }
      }

      private double number() {
         int pos = this.pos;
         boolean flag = false;

         while (this.pos < this.src.length()) {
            char charAt = this.src.charAt(this.pos);
            if (Character.isDigit(charAt)) {
               this.pos++;
            } else {
               if (charAt != '.' || flag) {
                  break;
               }

               flag = true;
               this.pos++;
            }
         }

         String str = this.src.substring(pos, this.pos);
         if (str.equals(".")) {
            throw this.err("Непонятное число", pos);
         } else {
            double d = Double.parseDouble(str);

            int n;
            for (n = 0; this.pos < this.src.length() && (this.src.charAt(this.pos) == 1082 || this.src.charAt(this.pos) == 'k'); this.pos++) {
               n++;
            }

            if (n > 4) {
               throw this.err("Слишком длинный суффикс из 'к'", this.pos - n);
            } else {
               if (n > 0) {
                  d *= Math.pow(1000.0, n);
               }

               return d;
            }
         }
      }

      private double identifier() {
         int pos = this.pos;

         while (this.pos < this.src.length() && Character.isLetter(this.src.charAt(this.pos))) {
            this.pos++;
         }

         String str = this.src.substring(pos, this.pos);
         switch (str) {
            case "pi":
            case "пи":
               return Math.PI;
            case "e":
               return Math.E;
            default:
               this.skipWs();
               if (this.peek() != '(') {
                  throw this.err("Неизвестная функция или константа '" + str + "'", pos);
               } else {
                  int n = this.pos++;
                  double expression = this.expression();
                  this.skipWs();
                  if (this.peek() != ')') {
                     throw this.err("Не закрыта скобка", n);
                  } else {
                     this.pos++;
                     return this.apply(str, expression, pos);
                  }
               }
         }
      }

      private double apply(String str, double expression, int pos) {
         return switch (str) {
            case "sqrt" -> Math.sqrt(expression);
            case "cbrt" -> Math.cbrt(expression);
            case "abs" -> Math.abs(expression);
            case "round" -> Math.round(expression);
            case "floor" -> Math.floor(expression);
            case "ceil" -> Math.ceil(expression);
            case "sin" -> Math.sin(expression);
            case "cos" -> Math.cos(expression);
            case "tan" -> Math.tan(expression);
            case "ln" -> Math.log(expression);
            case "log" -> Math.log10(expression);
            default -> throw this.err("Неизвестная функция '" + str + "'", pos);
         };
      }

      private static double resolve(CalcCommand.Val unary) {
         return unary.pct() ? unary.v() / 100.0 : unary.v();
      }

      private char peek() {
         return this.pos < this.src.length() ? this.src.charAt(this.pos) : '\u0000';
      }

      private void skipWs() {
         while (this.pos < this.src.length() && Character.isWhitespace(this.src.charAt(this.pos))) {
            this.pos++;
         }
      }

      private CalcCommand.CalcException err(String str, int pos) {
         return new CalcCommand.CalcException(str + " (позиция " + (pos + 1) + ")");
      }
   }

   private static final class Val {
      private final double v;
      private final boolean pct;

      public double v() {
         return this.v;
      }

      public boolean pct() {
         return this.pct;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Val other)) return false;
         return java.util.Objects.equals(this.v, other.v) && java.util.Objects.equals(this.pct, other.pct);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(v, pct);
      }

      @Override
      public String toString() {
         return "Val[" + "v=" + this.v + ", " + "pct=" + this.pct + "]";
      }

      private Val(double primary, boolean pct) {
         this.v = primary;
         this.pct = pct;
      }
   }
}
