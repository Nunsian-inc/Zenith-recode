package org.zenith.utility.misc;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Generated;

public class Localization {
   private final Map<String, LangEntry> items = new HashMap<>();
   private final List<String> items2 = new ArrayList<>();
   private LangEntry langEntry;
   private String text;

   public Localization() {
      this.method1727("ru");
      this.method1727("en");
      this.method1727("pl");
      this.method1727("tr");
   }

   public void method1727(String text) {
      if (!this.items2.contains(text)) {
         this.items2.add(text);
      }

      if (this.text == null) {
         this.method1728(text);
      }
   }

   public void method1728(String text) {
      if (this.items2.contains(text)) {
         if (this.langEntry == null || !text.equals(this.text)) {
            LangEntry langEntry = this.method1733(text);
            if (langEntry != null) {
               this.items.clear();
               this.items.put(text, langEntry);
               this.langEntry = langEntry;
               this.text = text;
            } else if (this.text == null) {
               this.text = text;
            }
         }
      }
   }

   public void method1729() {
      this.method1730(true);
   }

   public void method1730(boolean flag) {
      if (!this.items2.isEmpty()) {
         int indexOf;
         if ((indexOf = this.items2.indexOf(this.text)) < 0) {
            indexOf = 0;
         }

         int n;
         if (flag) {
            n = (indexOf + 1) % this.items2.size();
         } else {
            n = (indexOf - 1 + this.items2.size()) % this.items2.size();
         }

         this.method1728(this.items2.get(n));
      }
   }

   public void method1731() {
      this.method1730(false);
   }

   public List<String> method1732() {
      return Collections.unmodifiableList(this.items2);
   }

   public String getLanguageName() {
      return this.text == null ? "ru" : this.text;
   }

   public String translate(String text) {
      if (this.langEntry == null && this.text != null) {
         this.langEntry = this.method1733(this.text);
      }

      return this.langEntry == null ? text : this.langEntry.method3304(text);
   }

   private LangEntry method1733(String text) {
      LangEntry langEntry = this.items.get(text);
      if (langEntry != null) {
         return langEntry;
      } else {
         try {
            LangEntry langEntry2;
            try (InputStream resourceAsStream = Localization.class.getResourceAsStream("/assets/zenith/languages/" + text + ".language")) {
               if (resourceAsStream == null) {
                  return null;
               }

               LangEntry langEntry3 = new LangEntry(text, resourceAsStream);
               this.items.put(text, langEntry3);
               langEntry2 = langEntry3;
            }

            return langEntry2;
         } catch (IOException e) {
            e.printStackTrace();
            return null;
         }
      }
   }

   @Generated
   public String getText() {
      return this.text;
   }
}
