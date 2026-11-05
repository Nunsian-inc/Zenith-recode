package org.zenith.utility.api;

import java.net.http.HttpResponse;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public final class Util18 {
   private static final long CONST_longValue = 2000L;
   private static final long CONST_longValue2 = 86400000L;
   private static volatile long longValue;
   private static volatile boolean flag;

   private Util18() {
   }

   public static long method4489() {
      return System.currentTimeMillis() + longValue;
   }

   public static long getLongValue() {
      return longValue;
   }

   public static void method4490(long l) {
      if (l > 0L) {
         long l2 = l - System.currentTimeMillis();
         if (Math.abs(l2) <= 86400000L) {
            longValue = Math.abs(l2) < 500L ? 0L : l2;
            flag = true;
         }
      }
   }

   public static void method4491(HttpResponse<?> httpResponse) {
      if (httpResponse != null && !flag) {
         try {
            String text = httpResponse.headers().firstValue("Date").orElse(null);
            if (text == null || text.isBlank()) {
               return;
            }

            long l = ZonedDateTime.parse(text, DateTimeFormatter.RFC_1123_DATE_TIME).toInstant().toEpochMilli() + 500L;
            String str = httpResponse.headers().firstValue("Age").orElse(null);
            if (str != null && !str.isBlank()) {
               long l2 = Long.parseLong(str.strip());
               if (l2 > 0L && l2 <= 86400L) {
                  l += l2 * 1000L;
               }
            }

            long l2 = l - System.currentTimeMillis();
            if (Math.abs(l2) > 86400000L) {
               return;
            }

            longValue = Math.abs(l2) < 2000L ? 0L : l2;
         } catch (RuntimeException e) {
         }
      }
   }
}
