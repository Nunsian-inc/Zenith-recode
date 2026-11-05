package org.zenith.utility.misc;

import org.zenith.client.modules.impl.misc.AutoCraftModule;

public class CraftPresetData {
   private final String text;
   private String text2 = "";
   private String text3 = "";
   private final String[] texts = new String[9];
   private final String[] texts2 = new String[9];
   private int intValue = 0;

   public CraftPresetData(String str) {
      this.text = str == null ? "" : str;

      for (int i = 0; i < 9; i++) {
         this.texts[i] = "";
         this.texts2[i] = "";
      }
   }

   public static CraftPresetData method6198() {
      return new CraftPresetData("");
   }

   public boolean method6199() {
      return this.text.isBlank();
   }

   public String getText() {
      return this.text;
   }

   public String getText2() {
      return this.text2;
   }

   public String getText3() {
      return this.text3;
   }

   public int getIntValue() {
      return this.intValue;
   }

   public String method6200(int n) {
      return this.texts[n];
   }

   public String method6201(int n) {
      return this.texts2[n];
   }

   public boolean method6202() {
      if (this.text2 != null && !this.text2.isBlank()) {
         int n = 0;

         for (int i = 0; i < 9; i++) {
            if (this.texts[i] != null && !this.texts[i].isBlank()) {
               n++;
            }
         }

         return n >= 2;
      } else {
         return false;
      }
   }

   public void method6203(String identifier, String anotherString, String[] strings, String[] strings2, int n) {
      this.text2 = identifier == null ? "" : identifier;
      this.text3 = anotherString == null ? "" : anotherString;
      this.intValue = Math.max(0, n);

      for (int i = 0; i < 9; i++) {
         this.texts[i] = strings != null && i < strings.length && strings[i] != null ? strings[i] : "";
         this.texts2[i] = strings2 != null && i < strings2.length && strings2[i] != null ? strings2[i] : "";
      }
   }

   public void method6204() {
      this.text2 = "";
      this.text3 = "";
      this.intValue = 0;

      for (int i = 0; i < 9; i++) {
         this.texts[i] = "";
         this.texts2[i] = "";
      }
   }

   public String method6205(AutoCraftModule autoCraftModule) {
      return !this.method6202()
         ? "AutoCraft: craft item in crafting table to add preset"
         : "AutoCraft: take crafted item to save " + autoCraftModule.method1107(this.text2, this.text3);
   }
}
