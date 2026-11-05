package org.zenith.base.figura.parsers;

import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.nbt.ByteArrayTag;
import org.luaj.vm2.ast.Chunk;
import org.luaj.vm2.ast.NameResolver;
import org.luaj.vm2.parser.LuaParser;
import org.luaj.vm2.parser.ParseException;
import org.zenith.base.figura.FiguraMod;
import org.zenith.base.figura.runtime.RuntimeDefaults;

public class LuaScriptParser {
   private static final Pattern string = Pattern.compile(
      "([\"'])(?:\\\\(?:z\\s*|\\d{1,3}|x[a-fA-F\\d]{2}|[\\\\\"'\\n\\rabfnrtv])|(?:(?!\\1|[\\\\\\n\\r]).)*+)*?\\1", 8
   );
   private static final Pattern multilineString = Pattern.compile("(?s:\\[(=*)\\[.*?]\\1])", 8);
   private static final Pattern comments = Pattern.compile("--[^\n]*$", 8);
   private static final Pattern multilineComment = Pattern.compile("--(?s:\\[(?<s>=*)\\[.*?](\\k<s>)])", 8);
   private static final Pattern newlines = Pattern.compile("^[\t ]*((\n|\n\r|\r\n|\r)[\t ]*)?");
   private static final Pattern words = Pattern.compile("[a-zA-Z_]\\w*");
   private static final Pattern trailingNewlines = Pattern.compile("\n*$");
   private static final Pattern sheBangs = Pattern.compile("^#![^\n]*");
   private static final Pattern allStrings;
   private static final Pattern whitespacePlus = Pattern.compile("[ \n]+");
   private static final Pattern nameOops = Pattern.compile("\\w{2}");
   private static boolean error;

   public static ByteArrayTag parseScript(String name, String script) {
      error = true;

      String var10000 = switch (RuntimeDefaults.FORMAT_SCRIPT.value) {
         case 0 -> noMinifier(script);
         case 1 -> regexMinify(name, script);
         case 2 -> aggressiveMinify(name, script);
         case 3 -> ASTMinify(name, script);
         default -> throw new IllegalStateException(
            "FORMAT_SCRIPT should not be %d, expecting 0 to %d"
               .formatted(RuntimeDefaults.FORMAT_SCRIPT.value, RuntimeDefaults.FORMAT_SCRIPT.enumList.size() - 1)
         );
      };
      ByteArrayTag out;
      if (error) {
         FiguraMod.LOGGER.warn("Failed to minify the script, likely to be syntax error");
         out = new ByteArrayTag(script.getBytes(StandardCharsets.UTF_8));
      } else {
         out = new ByteArrayTag(var10000.getBytes(StandardCharsets.UTF_8));
      }

      return out;
   }

   private static String noMinifier(String script) {
      error = false;
      return script;
   }

   private static String regexMinify(String name, String script) {
      StringBuilder builder = new StringBuilder(script);
      int ogLen = script.length();

      for (int i = 0; i < builder.length(); i++) {
         if (builder.length() > ogLen) {
            throw new RuntimeException("Script minifier stopped due to a possible infinite loop when parsing \"" + name + "\"");
         }

         switch (builder.charAt(i)) {
            case '\t':
            case '\n':
            case '\r':
            case ' ':
               Matcher newline = newlines.matcher(builder.substring(i));
               if (newline.find()) {
                  if (newline.start() == 0) {
                     builder.delete(i, i + newline.end()).insert(i, Optional.ofNullable(newline.group(1)).map(t -> "\n").orElse(" "));
                  } else {
                     FiguraMod.LOGGER.warn("script TODO appears to have invalid new line configuration");
                  }
               }
               break;
            case '"':
            case '\'':
               Matcher stringMatcher = string.matcher(builder);
               if (!stringMatcher.find(i) || stringMatcher.start() != i) {
                  return builder.toString();
               }

               i = stringMatcher.end() - 1;
               break;
            case '#':
               if (i <= 0) {
                  Matcher sheBangMatcher = sheBangs.matcher(builder);
                  if (sheBangMatcher.find()) {
                     builder.delete(0, sheBangMatcher.end());
                  }
               }
               break;
            case '-':
               if (i == builder.length() - 1) {
                  return builder.toString();
               }

               Matcher multiline = multilineComment.matcher(builder);
               if (multiline.find(i) && multiline.start() == i) {
                  int breaks = builder.substring(i, multiline.end()).split("\n").length - 1;
                  builder.delete(i, multiline.end()).insert(i, "\n".repeat(breaks));
                  i--;
               } else {
                  Matcher comment = comments.matcher(builder);
                  if (comment.find(i) && comment.start() == i) {
                     builder.delete(comment.start(), comment.end());
                     i--;
                  }
               }
               break;
            case '[':
               Matcher multilineStringMatcher = multilineString.matcher(builder);
               if (multilineStringMatcher.find(i) && multilineStringMatcher.start() == i) {
                  i = multilineStringMatcher.end() - 1;
               }
               break;
            default:
               Matcher word = words.matcher(builder);
               if (word.find(i) && word.start() == i) {
                  i = word.end() - 1;
               }
         }
      }

      Matcher trailingNewline = trailingNewlines.matcher(builder);
      if (trailingNewline.find()) {
         builder.replace(trailingNewline.start(), trailingNewline.end(), "\n");
      }

      FiguraMod.debug("Script \"{}\" minified from {} chars to {} chars using LIGHT mode", name, script.length(), builder.length());
      error = false;
      return builder.toString();
   }

   private static String aggressiveMinify(String name, String script) {
      String start = regexMinify(name, script);
      if (error) {
         return start;
      }

      StringBuilder builder = new StringBuilder(start);

      for (int i = 0; i < builder.length(); i++) {
         switch (builder.charAt(i)) {
            case '\n':
            case ' ':
               Matcher whitespaceMatcher = whitespacePlus.matcher(builder);
               int matchStart;
               if (whitespaceMatcher.find(i) && (matchStart = whitespaceMatcher.start()) == i) {
                  builder.delete(i, whitespaceMatcher.end());
                  if (matchStart > 0 && matchStart + 1 < builder.length()) {
                     if (nameOops.matcher(builder.substring(matchStart - 1, matchStart + 1)).matches()) {
                        builder.insert(i, " ");
                     } else {
                        i--;
                     }
                  }
               }
               break;
            case '"':
            case '\'':
            case '[':
               Matcher allStringsMatcher = allStrings.matcher(builder);
               if (allStringsMatcher.find(i) && allStringsMatcher.start() == i) {
                  i = allStringsMatcher.end() - 1;
               }
               break;
            default:
               Matcher word = words.matcher(builder);
               if (word.find(i) && word.start() == i) {
                  i = word.end() - 1;
               }
         }
      }

      FiguraMod.debug("Script \"{}\" minified from {} chars to {} chars using HEAVY mode", name, script.length(), builder.length());
      return builder.toString();
   }

   private static String ASTMinify(String name, String script) {
      try {
         Chunk chunk = new LuaParser(new StringReader(script)).Chunk();
         chunk.accept(new NameResolver());
         LuaScriptBuilderVisitor visitor = new LuaScriptBuilderVisitor();
         chunk.accept(visitor);
         error = false;
         FiguraMod.debug("Script \"{}\" minified from {} chars to {} chars using AST mode", name, script.length(), visitor.length());
         return visitor.getString();
      } catch (ParseException var41) {
         return script;
      }
   }

   static {
      String var10000 = string.pattern();
      allStrings = Pattern.compile(var10000 + "|" + multilineString.pattern().replace('1', '2'), 8);
   }
}
