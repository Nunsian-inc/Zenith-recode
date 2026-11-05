package org.zenith.utility.misc;

import java.util.Locale;

public enum Rarity {
   COMMON(-6642771, false, 0),
   RARE(-11557889, false, 1),
   EPIC(-5018625, true, 2),
   LEGENDARY(45885, true, 3),
   MYTHIC(-45189, true, 4);

   private final int intValue;
   private final boolean flag;
   private final int intValue2;

   private Rarity(int intValue, boolean flag, int n) {
      this.intValue = intValue;
      this.flag = flag;
      this.intValue2 = n;
   }

   public int getIntValue() {
      return this.intValue;
   }

   public boolean isFlag() {
      return this.flag;
   }

   public int getIntValue2() {
      return this.intValue2;
   }

   public String key() {
      return "wardrobe.rarity." + this.name().toLowerCase(Locale.ROOT);
   }

   public static Rarity method4709(String str) {
      if (str == null) {
         return COMMON;
      } else {
         try {
            return valueOf(str.strip().toUpperCase(Locale.ROOT));
         } catch (IllegalArgumentException e) {
            return COMMON;
         }
      }
   }
}
