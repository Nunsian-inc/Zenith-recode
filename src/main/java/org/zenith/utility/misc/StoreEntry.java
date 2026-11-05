package org.zenith.utility.misc;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class StoreEntry {
   private final String text;
   private final String text2;
   private final String text3;
   private final int intValue;
   private final long longValue;
   private final List<Data1> items;

   public String text() {
      return this.text;
   }

   public String text2() {
      return this.text2;
   }

   public String text3() {
      return this.text3;
   }

   public int intValue() {
      return this.intValue;
   }

   public long longValue() {
      return this.longValue;
   }

   public List<Data1> items() {
      return this.items;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof StoreEntry other)) return false;
      return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.text3, other.text3) && java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.items, other.items);
   }

   @Override
   public int hashCode() {
      return java.util.Objects.hash(text, text2, text3, intValue, longValue, items);
   }

   @Override
   public String toString() {
      return "StoreEntry[" + "text=" + this.text + ", " + "text2=" + this.text2 + ", " + "text3=" + this.text3 + ", " + "intValue=" + this.intValue + ", " + "longValue=" + this.longValue + ", " + "items=" + this.items + "]";
   }
   public static final String text4 = "PUBLIC";
   public static final String text5 = "FRIENDS";
   public static final String text6 = "PRIVATE";

   public StoreEntry(String text, String str, String str2, int intValue, long longValue, List<Data1> items) {
      Objects.requireNonNull(text, "userId");
      str2 = str2 != null && !str2.isBlank() ? str2 : "PUBLIC";
      items = items == null ? List.of() : List.copyOf(items);
      this.text = text;
      this.text2 = str;
      this.text3 = str2;
      this.intValue = intValue;
      this.longValue = longValue;
      this.items = items;
   }

   public List<String> method1680() {
      List<String> strings = new ArrayList<>(this.items.size());

      for (Data1 data1 : this.items) {
         strings.add(data1.getText());
      }

      return strings;
   }

   public boolean isEmpty() {
      return this.items.isEmpty();
   }

   public boolean method1681(StoreEntry storeEntry) {
      if (storeEntry == null) {
         return true;
      } else {
         return this.intValue != storeEntry.intValue ? this.intValue > storeEntry.intValue : this.longValue > storeEntry.longValue;
      }
   }

   public String userId() {
      return this.text;
   }

   public String name() {
      return this.text2;
   }

   public String getText3() {
      return this.text3;
   }

   public int version() {
      return this.intValue;
   }

   public long getLongValue() {
      return this.longValue;
   }

   public List<Data1> getItems() {
      return this.items;
   }
}
