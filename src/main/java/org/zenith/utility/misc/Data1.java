package org.zenith.utility.misc;

import java.util.Locale;
import java.util.Objects;

public final class Data1 {
   private final String text;
   private final String text2;
   private final int intValue;

   public String text() {
      return this.text;
   }

   public String text2() {
      return this.text2;
   }

   public int intValue() {
      return this.intValue;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof Data1 other)) return false;
      return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.intValue, other.intValue);
   }

   @Override
   public int hashCode() {
      return java.util.Objects.hash(text, text2, intValue);
   }

   @Override
   public String toString() {
      return "Data1[" + "text=" + this.text + ", " + "text2=" + this.text2 + ", " + "intValue=" + this.intValue + "]";
   }
   public static final int CONST_intValue = -1;

   public Data1(String text, String name, int intValue) {
      Objects.requireNonNull(text, "id");
      name = name != null && !name.isBlank() ? name.trim().toUpperCase(Locale.ROOT) : "UNKNOWN";
      this.text = text;
      this.text2 = name;
      this.intValue = intValue;
   }

   public static Data1 method823(String text, String name) {
      return new Data1(text, name, -1);
   }

   public String getText() {
      return this.text;
   }

   public String getText2() {
      return this.text2;
   }

   public int getIntValue() {
      return this.intValue;
   }
}
