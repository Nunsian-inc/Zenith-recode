package org.zenith.utility.api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Locale;
import org.zenith.Zenith;
import org.zenith.utility.misc.AesGcmCrypto;

public class PlayTimeTracker {
   private static final String text = "protection.zenith";
   private static final String text2 = "playTimeZnth";
   private final File file;
   private final long longValue;
   private long longValue2;
   private long longValue3;
   private String text3;

   public PlayTimeTracker() {
      this.file = new File(Zenith.file, "protection.zenith");
      this.longValue = System.currentTimeMillis();
      this.text3 = this.method1398();
      this.load();
   }

   private String method1398() {
      LocalDate now = LocalDate.now();
      WeekFields weekFields = WeekFields.of(Locale.getDefault());
      int n = now.get(weekFields.weekOfWeekBasedYear());
      int n2 = now.get(weekFields.weekBasedYear());
      return n2 + "-W" + n;
   }

   private void load() {
      if (!this.file.exists()) {
         this.longValue2 = 0L;
         this.longValue3 = 0L;
      } else {
         try {
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(this.file))) {
               String line = bufferedReader.readLine();
               if (line == null || line.isEmpty()) {
                  this.longValue2 = 0L;
                  this.longValue3 = 0L;
                  return;
               }

               String json = AesGcmCrypto.method5332(line, "playTimeZnth");
               Gson gson = new Gson();
               JsonObject json2 = (JsonObject)gson.fromJson(json, JsonObject.class);
               if (json2 != null) {
                  this.longValue2 = json2.has("totalSeconds") ? json2.get("totalSeconds").getAsLong() : 0L;
                  if (json2.has("weeklyData")) {
                     JsonObject jsonObject = json2.getAsJsonObject("weeklyData");
                     this.longValue3 = jsonObject.has(this.text3) ? jsonObject.get(this.text3).getAsLong() : 0L;
                  }

                  return;
               }

               this.longValue2 = 0L;
               this.longValue3 = 0L;
            }
         } catch (Exception e) {
            this.longValue2 = 0L;
            this.longValue3 = 0L;
         }
      }
   }

   public void save() {
      long l = this.method1399();
      long l2 = this.longValue2 + l;
      String property = this.method1398();
      long l3;
      if (property.equals(this.text3)) {
         l3 = this.longValue3 + l;
      } else {
         l3 = l;
      }

      JsonObject json = new JsonObject();
      json.addProperty("totalSeconds", l2);
      JsonObject json2 = new JsonObject();
      json2.addProperty(property, l3);
      json.add("weeklyData", json2);

      try {
         String str = new Gson().toJson(json);
         String str2 = AesGcmCrypto.method5331(str, "playTimeZnth");

         try (FileWriter fileWriter = new FileWriter(this.file)) {
            fileWriter.write(str2);
         }
      } catch (Exception e) {
      }
   }

   public long method1399() {
      return (System.currentTimeMillis() - this.longValue) / 1000L;
   }

   public long method1400() {
      String str = this.method1398();
      return str.equals(this.text3) ? this.longValue3 + this.method1399() : this.method1399();
   }

   public long method1401() {
      return this.longValue2 + this.method1399();
   }

   public static String method1402(long l) {
      long l2 = l / 3600L;
      long l3 = l % 3600L / 60L;
      return l2 > 0L ? l2 + "h " + l3 + "m" : l3 + "m";
   }
}
