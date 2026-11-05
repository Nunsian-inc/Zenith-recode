package org.zenith.utility.misc;

import lombok.Generated;

public enum Enum3 {
   LEFT(0),
   RIGHT(1),
   MIDDLE(2),
   BUTTON_4(3),
   BUTTON_5(4),
   BUTTON_6(5),
   BUTTON_7(6);

   private final int intValue;

   public static Enum3 method6094(int mouseToGlfw) {
      for (Enum3 enum2 : values()) {
         if (enum2.getIntValue() == mouseToGlfw) {
            return enum2;
         }
      }

      return LEFT;
   }

   @Generated
   private Enum3(int intValue) {
      this.intValue = intValue;
   }

   @Generated
   public int getIntValue() {
      return this.intValue;
   }
}
