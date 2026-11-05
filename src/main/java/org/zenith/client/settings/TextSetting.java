package org.zenith.client.settings;

import com.google.gson.JsonObject;
import java.util.function.Predicate;
import java.util.function.Supplier;
import lombok.Generated;

public class TextSetting extends Setting {
   private final TextSetting.Entry entry;
   private String value;
   private final String text;
   private boolean flag;
   private boolean flag2;

   public TextSetting method2120() {
      this.flag = true;
      return this;
   }

   public TextSetting method2121() {
      this.flag2 = true;
      return this;
   }

   public TextSetting(String key, String str, String text) {
      this(key, "", str, text);
   }

   public TextSetting(String key, String description, String str, String text) {
      super(key, description);
      this.value = str;
      this.text = text;
      this.entry = TextSetting.Entry.method2124();
   }

   public TextSetting(String key, String str, String text, TextSetting.Entry entry) {
      this(key, "", str, text, entry);
   }

   public TextSetting(String key, String description, String str, String text, TextSetting.Entry entry) {
      super(key, description);
      this.value = str;
      this.text = text;
      this.entry = entry == null ? TextSetting.Entry.method2124() : entry;
   }

   public TextSetting(String key, String str, String text, Supplier<Boolean> visible) {
      this(key, "", str, text, visible);
   }

   public TextSetting(String key, String description, String str, String text, Supplier<Boolean> visible) {
      super(key, description);
      this.value = str;
      this.text = text;
      this.entry = TextSetting.Entry.method2124();
      this.setVisible(visible);
   }

   public TextSetting(String key, String str, String text, Supplier<Boolean> visible, TextSetting.Entry entry) {
      this(key, "", str, text, visible, entry);
   }

   public TextSetting(String key, String description, String str, String text, Supplier<Boolean> visible, TextSetting.Entry entry) {
      super(key, description);
      this.value = str;
      this.text = text;
      this.entry = entry == null ? TextSetting.Entry.method2124() : entry;
      this.setVisible(visible);
   }

   public boolean method2122(String text) {
      if (this.entry.method2123(text)) {
         this.value = text;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public void safe(JsonObject json) {
      json.addProperty(String.valueOf(this.key), this.value);
   }

   @Override
   public void load(JsonObject json) {
      if (json.has(String.valueOf(this.key))) {
         String string = json.get(String.valueOf(this.key)).getAsString();
         this.method2122(string);
      }
   }

   @Generated
   public TextSetting.Entry getEntry() {
      return this.entry;
   }

   @Generated
   public String getValue() {
      return this.value;
   }

   @Generated
   public String getText() {
      return this.text;
   }

   @Generated
   public boolean isFlag() {
      return this.flag;
   }

   @Generated
   public boolean isLive() {
      return this.flag2;
   }

   @Generated
   public void setValue(String string) {
      this.value = string;
   }

   public abstract static class Entry {
      private final int maxLength;

      public Entry() {
         this.maxLength = Integer.MAX_VALUE;
      }

      public abstract boolean method2123(String text);

      public static TextSetting.Entry method2124() {
         return new TextSetting.Entry(Integer.MAX_VALUE) {
            @Override
            public boolean method2123(String str) {
               return str != null;
            }
         };
      }

      public static TextSetting.Entry method2125(final int n) {
         return new TextSetting.Entry(n) {
            @Override
            public boolean method2123(String str) {
               return str != null && str.length() <= n;
            }
         };
      }

      public static TextSetting.Entry method2126(int n, final Predicate<String> predicate) {
         return new TextSetting.Entry(n) {
            @Override
            public boolean method2123(String var1x) {
               return var1x != null && predicate.test(var1x);
            }
         };
      }

      @Generated
      public Entry(int maxLength) {
         this.maxLength = maxLength;
      }

      @Generated
      public int getMaxLength() {
         return this.maxLength;
      }
   }
}
