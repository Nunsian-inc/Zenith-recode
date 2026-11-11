package org.zenith.utility.api;

import java.net.http.HttpResponse;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public final class ServerClock {
   private static final long HTTP_OFFSET_TOLERANCE_MILLIS = 2000L;
   private static final long MAX_OFFSET_MILLIS = 86400000L;
   private static volatile long offsetMillis;
   private static volatile boolean serverTimeReceived;

   private ServerClock() {
   }

   public static long currentTimeMillis() {
      return System.currentTimeMillis() + offsetMillis;
   }

   public static long getOffsetMillis() {
      return offsetMillis;
   }

   public static void syncFromServerTime(long l) {
      if (l > 0L) {
         long l2 = l - System.currentTimeMillis();
         if (Math.abs(l2) <= 86400000L) {
            offsetMillis = Math.abs(l2) < 500L ? 0L : l2;
            serverTimeReceived = true;
         }
      }
   }

   public static void syncFromHttpResponse(HttpResponse<?> httpResponse) {
      if (httpResponse != null && !serverTimeReceived) {
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

            offsetMillis = Math.abs(l2) < 2000L ? 0L : l2;
         } catch (RuntimeException e) {
         }
      }
   }
}
