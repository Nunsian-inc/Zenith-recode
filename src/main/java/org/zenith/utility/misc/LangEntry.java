package org.zenith.utility.misc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import lombok.Generated;

public class LangEntry {
   private final String text;
   private final Map<String, String> items = new HashMap<>();

   public LangEntry(String text, InputStream resourceAsStream) throws IOException {
      this.text = text;
      this.method3303(resourceAsStream);
   }

   private void method3303(InputStream resourceAsStream) throws IOException {
      String line;
      try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(resourceAsStream, StandardCharsets.UTF_8))) {
         while ((line = bufferedReader.readLine()) != null) {
            line = line.trim();
            if (!line.isEmpty() && !line.startsWith("#")) {
               String[] strings = line.split("=", 2);
               if (strings.length == 2) {
                  this.items.put(strings[0].trim(), strings[1].trim());
               }
            }
         }
      }
   }

   public String method3304(String text) {
      String str = this.items.get(text);
      if (str != null) {
         return str;
      } else {
         int lastIndexOf = text.lastIndexOf(46);
         if (lastIndexOf >= 0 && lastIndexOf + 1 < text.length()) {
            String str2 = this.items.get(text.substring(0, lastIndexOf + 1) + "*");
            if (str2 != null) {
               return str2.replace("{value}", text.substring(lastIndexOf + 1));
            }
         }

         return text;
      }
   }

   @Generated
   public String getName() {
      return this.text;
   }
}
