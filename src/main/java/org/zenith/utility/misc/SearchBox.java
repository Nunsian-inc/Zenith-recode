package org.zenith.utility.misc;

import com.mojang.blaze3d.platform.InputConstants;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.Generated;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.util.Mth;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.MinecraftAccess;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.utility.render.MathUtils;
import org.zenith.utility.render.display.base.CustomDrawContext;

public class SearchBox implements MinecraftAccess {
   private static final Map<Character, Character> items = Map.ofEntries(
      Map.entry('й', 'q'),
      Map.entry('ц', 'w'),
      Map.entry('у', 'e'),
      Map.entry('к', 'r'),
      Map.entry('е', 't'),
      Map.entry('н', 'y'),
      Map.entry('г', 'u'),
      Map.entry('ш', 'i'),
      Map.entry('щ', 'o'),
      Map.entry('з', 'p'),
      Map.entry('х', '['),
      Map.entry('ъ', ']'),
      Map.entry('ф', 'a'),
      Map.entry('ы', 's'),
      Map.entry('в', 'd'),
      Map.entry('а', 'f'),
      Map.entry('п', 'g'),
      Map.entry('р', 'h'),
      Map.entry('о', 'j'),
      Map.entry('л', 'k'),
      Map.entry('д', 'l'),
      Map.entry('ж', ';'),
      Map.entry('э', '\''),
      Map.entry('я', 'z'),
      Map.entry('ч', 'x'),
      Map.entry('с', 'c'),
      Map.entry('м', 'v'),
      Map.entry('и', 'b'),
      Map.entry('т', 'n'),
      Map.entry('ь', 'm'),
      Map.entry('б', ','),
      Map.entry('ю', '.'),
      Map.entry('ё', '`')
   );
   private String text = "";
   private boolean selected;
   private boolean flag;
   private int cursor;
   private float floatValue;
   private Font font;
   private UVPair vector2f;
   private String text2;
   private float width;
   private float height;
   private boolean multiline;
   private long longValue = System.currentTimeMillis();
   private int maxLength = Integer.MAX_VALUE;
   private SearchBox.Type type = SearchBox.Type.ANY;
   private SearchBox.Type2 type2 = SearchBox.Type2.NO_LIMIT;
   private float scrollOffset = 0.0F;
   private boolean masked;
   private final transient SearchBox.Entry entry = new SearchBox.Entry();
   public Animation animation = new Animation(400L, 0.2F, Easing.INSTANCE13);

   public SearchBox(UVPair vector2f, Font font, String text, float width) {
      this.font = font;
      this.text2 = text;
      this.width = width;
      this.vector2f = vector2f;
   }

   public void method987(CustomDrawContext menuDrawContext, float floatValue, float centeredTextY, ZenithColor zenithColor, ZenithColor zenithColor2) {
      this.vector2f = new UVPair(floatValue, centeredTextY);
      this.cursor = Mth.clamp(this.cursor, 0, this.text.length());
      this.floatValue = floatValue;
      boolean empty = this.isEmpty();
      if (this.multiline) {
         this.method988(menuDrawContext, floatValue, centeredTextY, zenithColor, zenithColor2, empty);
      } else {
         String repeat = this.masked ? "*".repeat(this.text.length()) : this.text;
         float width = this.width;
         SearchBox.Entry entry = this.entry;
         int beginIndex;
         int cursor;
         if (entry.font == this.font && entry.intValue == this.cursor && entry.flag == this.masked && entry.floatValue == width && this.text.equals(entry.text)) {
            beginIndex = entry.intValue2;
            cursor = entry.intValue3;
         } else {
            beginIndex = 0;

            while (this.font.width(repeat.substring(beginIndex, this.cursor)) > width) {
               beginIndex++;
            }

            cursor = this.cursor;

            while (cursor < repeat.length() && this.font.width(repeat.substring(beginIndex, cursor + 1)) <= width) {
               cursor++;
            }

            entry.text = this.text;
            entry.flag = this.masked;
            entry.intValue = this.cursor;
            entry.floatValue = width;
            entry.font = this.font;
            entry.intValue2 = beginIndex;
            entry.intValue3 = cursor;
         }

         String text = repeat.substring(beginIndex, cursor);
         if (this.flag && !text.isEmpty()) {
            menuDrawContext.drawRect(floatValue - 1.0F, centeredTextY - 1.0F, Math.min(this.font.width(text), width) + 2.0F, this.font.height() + 2.0F, zenithColor2.method7294(0.5F));
         }

         if (empty) {
            menuDrawContext.drawText(this.font, this.method994(this.text2), floatValue, centeredTextY, zenithColor2);
         } else {
            menuDrawContext.drawText(this.font, text, floatValue, centeredTextY, zenithColor);
         }

         if (this.selected && System.currentTimeMillis() - this.longValue > 200L) {
            float f = empty ? 0.0F : this.font.width(repeat.substring(beginIndex, this.cursor));
            float floatValue2 = this.floatValue + Math.min(f, this.width);
            this.animation.setDuration(250L);
            menuDrawContext.drawRect(
               floatValue2,
               centeredTextY - 1.0F,
               1.0F,
               this.font.height() + 2.0F,
               zenithColor.method7294(
                  this.animation
                     .method2808(this.animation.getValue() == 0.2F ? 1.0F : (this.animation.getValue() == 1.0F ? 0.2F : this.animation.getFloatValue2()))
               )
            );
         }
      }
   }

   private void method988(CustomDrawContext menuDrawContext, float floatValue, float centeredTextY, ZenithColor zenithColor, ZenithColor zenithColor2, boolean empty) {
      String repeat = this.masked ? "*".repeat(this.text.length()) : this.text;
      if (empty) {
         var strings = this.method989(1, this.text2 == null ? "" : this.text2);

         for (int i = 0; i < strings.size() && (i + 1) * this.font.height() <= this.height; i++) {
            menuDrawContext.drawText(this.font, (String)strings.get(i), floatValue, centeredTextY + i * this.font.height(), zenithColor2);
         }
      } else {
         var strings = this.method989(0, repeat);
         float height = this.font.height();

         for (int i = 0; i < strings.size() && (i + 1) * height <= this.height; i++) {
            menuDrawContext.drawText(this.font, (String)strings.get(i), floatValue, centeredTextY + i * height, zenithColor);
         }
      }

      float height = this.font.height();
      if (this.selected && System.currentTimeMillis() - this.longValue > 200L) {
         var strings = this.method989(2, repeat.substring(0, this.cursor));
         int n = Math.max(0, strings.size() - 1);
         if ((n + 1) * height <= this.height) {
            String text = strings.isEmpty() ? "" : (String)strings.get(n);
            this.animation.setDuration(250L);
            menuDrawContext.drawRect(
               floatValue + this.font.width(text),
               centeredTextY + n * height - 1.0F,
               1.0F,
               height + 2.0F,
               zenithColor.method7294(
                  this.animation
                     .method2808(this.animation.getValue() == 0.2F ? 1.0F : (this.animation.getValue() == 1.0F ? 0.2F : this.animation.getFloatValue2()))
               )
            );
         }
      }
   }

   private List<String> method989(int n, String repeat) {
      SearchBox.Entry entry = this.entry;
      if (entry.fonts[n] == this.font && entry.floatValues[n] == this.width && repeat.equals(entry.texts[n])) {
         return entry.itemss[n];
      } else {
         var unmodifiableList = Collections.unmodifiableList(this.method990(repeat));
         entry.texts[n] = repeat;
         entry.floatValues[n] = this.width;
         entry.fonts[n] = this.font;
         entry.itemss[n] = unmodifiableList;
         return unmodifiableList;
      }
   }

   private List<String> method990(String repeat) {
      ArrayList<String> strings = new ArrayList<>();
      StringBuilder builder = new StringBuilder();

      for (int i = 0; i < repeat.length(); i++) {
         char charAt = repeat.charAt(i);
         if (charAt == '\n') {
            strings.add(builder.toString());
            builder.setLength(0);
         } else {
            if (!builder.isEmpty() && this.font.width(builder.toString() + charAt) > this.width) {
               strings.add(builder.toString());
               builder.setLength(0);
            }

            builder.append(charAt);
         }
      }

      strings.add(builder.toString());
      return strings;
   }

   public boolean onMouseClicked(double x, double y, Enum3 enum2) {
      UVPair vector2f = this.getVector2f();
      float height = this.multiline ? Math.max(this.font.height(), this.height) : this.font.height();
      this.selected = enum2.getIntValue() == 0 && MathUtils.method2647(x, y, vector2f.u(), vector2f.v() - 3.0F, this.width, height + 6.0F);
      if (this.selected) {
         this.flag = false;
         if (!this.multiline) {
            this.method991(x);
         }
      }

      return this.selected;
   }

   private void method991(double x) {
      String repeat = this.masked ? "*".repeat(this.text.length()) : this.text;
      this.cursor = Mth.clamp(this.cursor, 0, this.text.length());
      int beginIndex = 0;

      while (beginIndex < this.cursor && this.font.width(repeat.substring(beginIndex, this.cursor)) > this.width) {
         beginIndex++;
      }

      float f = Mth.clamp((float)x - this.vector2f.u(), 0.0F, this.width);
      int cursor = beginIndex;

      for (float width = 0.0F; cursor < repeat.length(); cursor++) {
         float width2 = this.font.width(repeat.substring(beginIndex, cursor + 1));
         if (width2 > this.width || f < (width + width2) / 2.0F) {
            break;
         }

         width = width2;
      }

      this.cursor = cursor;
      this.longValue = System.currentTimeMillis();
   }

   public boolean keyPressed(int keyCode, int keycode, int modifiers) {
      if (!this.selected) {
         return false;
      } else if (keyCode == 256) {
         this.selected = false;
         return true;
      } else if (keyCode == 257) {
         if (this.multiline) {
            this.method992("\n", this.cursor);
            this.cursor++;
            this.longValue = System.currentTimeMillis();
         } else {
            this.selected = false;
         }

         return true;
      } else {
         this.longValue = System.currentTimeMillis();
         this.cursor = Mth.clamp(this.cursor, 0, this.text.length());
         if (org.zenith.utility.misc.KeyCompat.isKeyDown(341)) {
            if (keyCode == 86) {
               String clipboard = mc.keyboardHandler.getClipboard();
               if (this.flag) {
                  this.text = "";
                  this.cursor = 0;
                  this.flag = false;
               }

               this.cursor = this.cursor + this.method992(clipboard, this.cursor);
               this.flag = false;
            } else if (keyCode == 65) {
               this.flag = true;
               this.cursor = this.text.length();
            } else if (keyCode == 67 && this.selected && this.flag) {
               mc.keyboardHandler.setClipboard(this.text);
            }
         } else if (keyCode == 261 && !this.text.isEmpty()) {
            this.method993(this.cursor + 1);
            this.flag = false;
         } else if (keyCode == 259 && !this.text.isEmpty()) {
            if (this.flag) {
               this.text = "";
               this.cursor = 0;
               this.flag = false;
            } else {
               this.method993(this.cursor);
               this.cursor--;
               if (org.zenith.utility.misc.KeyCompat.isKeyDown(341)) {
                  while (!this.text.isEmpty() && this.cursor > 0) {
                     this.method993(this.cursor);
                     this.cursor--;
                  }
               }
            }
         } else if (keyCode == 262) {
            this.cursor++;
            if (org.zenith.utility.misc.KeyCompat.isKeyDown(341)) {
               this.cursor = this.text.length();
            }

            this.flag = false;
         } else if (keyCode == 263) {
            this.cursor--;
            if (org.zenith.utility.misc.KeyCompat.isKeyDown(341)) {
               this.cursor = 0;
            }

            this.flag = false;
         } else if (keyCode == 269) {
            this.cursor = this.text.length();
            this.flag = false;
         } else if (keyCode == 268) {
            this.cursor = 0;
            this.flag = false;
         }

         this.cursor = Mth.clamp(this.cursor, 0, this.text.length());
         return true;
      }
   }

   public boolean charTyped(char codepoint, int n) {
      if (!this.selected) {
         return false;
      } else {
         this.longValue = System.currentTimeMillis();
         this.cursor = Mth.clamp(this.cursor, 0, this.text.length());
         if (this.flag) {
            this.text = "";
            this.cursor = 0;
            this.flag = false;
         }

         this.cursor = this.cursor + this.method992(Character.toString(codepoint), this.cursor);
         this.cursor = Mth.clamp(this.cursor, 0, this.text.length());
         return true;
      }
   }

   private int method992(String clipboard, int cursor) {
      String str = this.method995(clipboard);
      str = this.method996(str);
      StringBuilder builder = new StringBuilder();

      for (char ch : str.toCharArray()) {
         if (this.type.method999(ch)) {
            builder.append(ch);
         }
      }

      String string = builder.toString();
      if (this.text.length() + string.length() > this.maxLength) {
         int n = this.maxLength - this.text.length();
         if (n <= 0) {
            return 0;
         }

         string = string.substring(0, Math.min(n, string.length()));
      }

      StringBuilder builder2 = new StringBuilder();
      boolean flag = false;

      for (int i = 0; i < this.text.length(); i++) {
         if (i == cursor) {
            flag = true;
            builder2.append(string);
         }

         builder2.append(this.text.charAt(i));
      }

      if (!flag) {
         builder2.append(string);
      }

      this.text = builder2.toString();
      return string.length();
   }

   private void method993(int cursor) {
      StringBuilder builder = new StringBuilder();

      for (int i = 0; i < this.text.length(); i++) {
         if (i != cursor - 1) {
            builder.append(this.text.charAt(i));
         }
      }

      this.text = builder.toString();
   }

   public boolean isEmpty() {
      return this.text.isEmpty();
   }

   private String method994(String text) {
      if (text != null && !text.isEmpty() && !(this.width <= 0.0F) && !(this.font.width(text) <= this.width)) {
         int length = text.length();

         while (length > 0 && this.font.width(text.substring(0, length)) > this.width) {
            length--;
         }

         return text.substring(0, length);
      } else {
         return text == null ? "" : text;
      }
   }

   private String method995(String clipboard) {
      if (clipboard != null && !clipboard.isEmpty()) {
         return switch (this.type) {
            case ENGLISH, ENGLISH_NUMBERS, ENGLISH_NAME, SERVER_ADDRESS -> method997(clipboard, items);
            default -> clipboard;
         };
      } else {
         return clipboard;
      }
   }

   private String method996(String str) {
      if (str != null && !str.isEmpty() && this.type2 != SearchBox.Type2.NO_LIMIT) {
         StringBuilder builder = new StringBuilder(str.length());

         for (int i = 0; i < str.length(); i++) {
            char charAt = str.charAt(i);
            if (!Character.isWhitespace(charAt)) {
               builder.append(charAt);
            }
         }

         return builder.toString();
      } else {
         return str;
      }
   }

   private static String method997(String clipboard, Map<Character, Character> items) {
      StringBuilder builder = new StringBuilder(clipboard.length());

      for (int i = 0; i < clipboard.length(); i++) {
         char charAt = clipboard.charAt(i);
         char lowerCase = Character.toLowerCase(charAt);
         Character ch = (Character)items.get(lowerCase);
         if (ch == null) {
            builder.append(charAt);
         } else {
            char upperCase = Character.isUpperCase(charAt) ? Character.toUpperCase(ch) : ch;
            builder.append(upperCase);
         }
      }

      return builder.toString();
   }

   @Generated
   public String getText() {
      return this.text;
   }

   @Generated
   public boolean isSelected() {
      return this.selected;
   }

   @Generated
   public boolean isFlag() {
      return this.flag;
   }

   @Generated
   public int getCursor() {
      return this.cursor;
   }

   @Generated
   public float getFloatValue() {
      return this.floatValue;
   }

   @Generated
   public Font getFont() {
      return this.font;
   }

   @Generated
   public UVPair getVector2f() {
      return this.vector2f;
   }

   @Generated
   public String getText2() {
      return this.text2;
   }

   @Generated
   public float getWidth() {
      return this.width;
   }

   @Generated
   public float getHeight() {
      return this.height;
   }

   @Generated
   public boolean isMultiline() {
      return this.multiline;
   }

   @Generated
   public long getLongValue() {
      return this.longValue;
   }

   @Generated
   public int getMaxLength() {
      return this.maxLength;
   }

   @Generated
   public SearchBox.Type getType() {
      return this.type;
   }

   @Generated
   public SearchBox.Type2 getType2() {
      return this.type2;
   }

   @Generated
   public float getScrollOffset() {
      return this.scrollOffset;
   }

   @Generated
   public boolean isMasked() {
      return this.masked;
   }

   @Generated
   public Animation getAnimation() {
      return this.animation;
   }

   @Generated
   public void setText(String text) {
      this.text = text;
   }

   @Generated
   public void setSelected(boolean selected) {
      this.selected = selected;
   }

   @Generated
   public void setFlag(boolean flag) {
      this.flag = flag;
   }

   @Generated
   public void setCursor(int cursor) {
      this.cursor = cursor;
   }

   @Generated
   public void setFloatValue(float floatValue) {
      this.floatValue = floatValue;
   }

   @Generated
   public void setFont(Font font) {
      this.font = font;
   }

   @Generated
   public void setVector2f(UVPair vector2f) {
      this.vector2f = vector2f;
   }

   @Generated
   public void setText2(String tr) {
      this.text2 = tr;
   }

   @Generated
   public void setWidth(float width) {
      this.width = width;
   }

   @Generated
   public void setHeight(float height) {
      this.height = height;
   }

   @Generated
   public void setMultiline(boolean multiline) {
      this.multiline = multiline;
   }

   @Generated
   public void setLongValue(long longValue) {
      this.longValue = longValue;
   }

   @Generated
   public void setMaxLength(int maxLength) {
      this.maxLength = maxLength;
   }

   @Generated
   public void setType(SearchBox.Type type) {
      this.type = type;
   }

   @Generated
   public void setType2(SearchBox.Type2 type2) {
      this.type2 = type2;
   }

   @Generated
   public void setScrollOffset(float scrollOffset) {
      this.scrollOffset = scrollOffset;
   }

   @Generated
   public void setMasked(boolean masked) {
      this.masked = masked;
   }

   @Generated
   public void setAnimation(Animation animation) {
      this.animation = animation;
   }

   @Generated
   @Override
   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof SearchBox searchBox)) {
         return false;
      } else if (!searchBox.method998(this)) {
         return false;
      } else if (this.isSelected() != searchBox.isSelected()) {
         return false;
      } else if (this.isFlag() != searchBox.isFlag()) {
         return false;
      } else if (this.getCursor() != searchBox.getCursor()) {
         return false;
      } else if (Float.compare(this.getFloatValue(), searchBox.getFloatValue()) != 0) {
         return false;
      } else if (Float.compare(this.getWidth(), searchBox.getWidth()) != 0) {
         return false;
      } else if (Float.compare(this.getHeight(), searchBox.getHeight()) != 0) {
         return false;
      } else if (this.isMultiline() != searchBox.isMultiline()) {
         return false;
      } else if (this.getLongValue() != searchBox.getLongValue()) {
         return false;
      } else if (this.getMaxLength() != searchBox.getMaxLength()) {
         return false;
      } else if (Float.compare(this.getScrollOffset(), searchBox.getScrollOffset()) != 0) {
         return false;
      } else if (this.isMasked() != searchBox.isMasked()) {
         return false;
      } else {
         String text = this.getText();
         String anObject = searchBox.getText();
         if (text == null ? anObject == null : text.equals(anObject)) {
            Font font = this.getFont();
            Font font2 = searchBox.getFont();
            if (font == null ? font2 == null : font.equals(font2)) {
               UVPair vector2f = this.getVector2f();
               UVPair uvPair = searchBox.getVector2f();
               if (vector2f == null ? uvPair == null : vector2f.equals(uvPair)) {
                  String str = this.getText2();
                  String anObject2 = searchBox.getText2();
                  if (str == null ? anObject2 == null : str.equals(anObject2)) {
                     SearchBox.Type type = this.getType();
                     SearchBox.Type type2 = searchBox.getType();
                     if (type == null ? type2 == null : type.equals(type2)) {
                        SearchBox.Type2 type22 = this.getType2();
                        SearchBox.Type2 type23 = searchBox.getType2();
                        if (type22 == null ? type23 == null : type22.equals(type23)) {
                           Animation animation = this.getAnimation();
                           Animation animation2 = searchBox.getAnimation();
                           return animation == null ? animation2 == null : animation.equals(animation2);
                        } else {
                           return false;
                        }
                     } else {
                        return false;
                     }
                  } else {
                     return false;
                  }
               } else {
                  return false;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   @Generated
   public boolean method998(Object obj) {
      return obj instanceof SearchBox;
   }

   @Generated
   @Override
   public int hashCode() {
      byte b = 59;
      int n = 1;
      n = n * 59 + (this.isSelected() ? 79 : 97);
      n = n * 59 + (this.isFlag() ? 79 : 97);
      n = n * 59 + this.getCursor();
      n = n * 59 + Float.floatToIntBits(this.getFloatValue());
      n = n * 59 + Float.floatToIntBits(this.getWidth());
      n = n * 59 + Float.floatToIntBits(this.getHeight());
      n = n * 59 + (this.isMultiline() ? 79 : 97);
      long longValue = this.getLongValue();
      n = n * 59 + (int)(longValue >>> 32 ^ longValue);
      n = n * 59 + this.getMaxLength();
      n = n * 59 + Float.floatToIntBits(this.getScrollOffset());
      n = n * 59 + (this.isMasked() ? 79 : 97);
      String text = this.getText();
      n = n * 59 + (text == null ? 43 : text.hashCode());
      Font font = this.getFont();
      n = n * 59 + (font == null ? 43 : font.hashCode());
      UVPair vector2f = this.getVector2f();
      n = n * 59 + (vector2f == null ? 43 : vector2f.hashCode());
      String str = this.getText2();
      n = n * 59 + (str == null ? 43 : str.hashCode());
      SearchBox.Type type = this.getType();
      n = n * 59 + (type == null ? 43 : type.hashCode());
      SearchBox.Type2 type2 = this.getType2();
      n = n * 59 + (type2 == null ? 43 : type2.hashCode());
      Animation animation = this.getAnimation();
      return n * 59 + (animation == null ? 43 : animation.hashCode());
   }

   @Generated
   @Override
   public String toString() {
      return "TextBox(text="
         + this.getText()
         + ", selected="
         + this.isSelected()
         + ", selectAll="
         + this.isFlag()
         + ", cursor="
         + this.getCursor()
         + ", posX="
         + this.getFloatValue()
         + ", font="
         + this.getFont()
         + ", position="
         + this.getVector2f()
         + ", emptyText="
         + this.getText2()
         + ", width="
         + this.getWidth()
         + ", height="
         + this.getHeight()
         + ", multiline="
         + this.isMultiline()
         + ", lastInputTime="
         + this.getLongValue()
         + ", maxLength="
         + this.getMaxLength()
         + ", charFilter="
         + this.getType()
         + ", wordLimit="
         + this.getType2()
         + ", scrollOffset="
         + this.getScrollOffset()
         + ", masked="
         + this.isMasked()
         + ", animation="
         + this.getAnimation()
         + ")";
   }

   private static final class Entry {
      private static final int CONST_intValue = 0;
      private static final int CONST_intValue2 = 1;
      private static final int CONST_intValue3 = 2;
      private String text;
      private boolean flag;
      private int intValue = -1;
      private float floatValue;
      private Font font;
      private int intValue2;
      private int intValue3;
      private final String[] texts = new String[3];
      private final float[] floatValues = new float[3];
      private final Font[] fonts = new Font[3];
      private final List<String>[] itemss = new List[3];
   }

   public static enum Type {
      ANY,
      ENGLISH,
      ENGLISH_NUMBERS,
      ENGLISH_NAME,
      SERVER_ADDRESS,
      CYRILLIC,
      NUMBERS_ONLY,
      DECIMAL_NUMBER;

      public boolean method999(char ch) {
         return switch (this) {
            case ANY -> true;
            case ENGLISH -> Character.isLetter(ch) && ch <= 127 && Character.isAlphabetic(ch);
            case ENGLISH_NUMBERS -> Character.isLetterOrDigit(ch) && ch <= 127;
            case ENGLISH_NAME -> Character.isLetterOrDigit(ch) && ch <= 127 || ch == '_';
            case SERVER_ADDRESS -> Character.isLetterOrDigit(ch) && ch <= 127 || ch == '.' || ch == ':' || ch == '-' || ch == '_';
            case CYRILLIC -> String.valueOf(ch).matches("[А-Яа-яЁё]");
            case NUMBERS_ONLY -> Character.isDigit(ch);
            case DECIMAL_NUMBER -> Character.isDigit(ch) || ch == '-' || ch == '.' || ch == ',';
         };
      }
   }

   public static enum Type2 {
      NO_LIMIT,
      ONLY_ONE_WORLD;
   }
}
