package org.zenith.utility.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zenith.Zenith;
import org.zenith.utility.config.SiteSession;

public final class SiteLicenseService {
   private static final Logger logger = LoggerFactory.getLogger("Zenith/Site");
   public static final int CONST_intValue = 1;
   public static final int CONST_intValue2 = 2;
   public static final int CONST_intValue3 = 3;
   public static final int CONST_intValue4 = 4;
   public static final int CONST_intValue5 = 9;
   private static final long CONST_longValue = 1800000L;
   private static final long CONST_longValue2 = 300000L;
   private static final long CONST_longValue3 = 60L;
   private static final AtomicBoolean atomicBoolean = new AtomicBoolean();
   private static final AtomicBoolean atomicBoolean2 = new AtomicBoolean();
   private static volatile long longValue;
   private static volatile long longValue2;
   private static volatile List<SiteLicenseService.Data> items = List.of();
   private static volatile String text = "";

   private SiteLicenseService() {
   }

   public static void start() {
      if (atomicBoolean.compareAndSet(false, true)) {
         ScheduledExecutorService singleThreadScheduledExecutor = Executors.newSingleThreadScheduledExecutor(var0x -> {
            Thread thread = new Thread(var0x, "zenith-site-licenses");
            thread.setDaemon(true);
            return thread;
         });
         singleThreadScheduledExecutor.scheduleWithFixedDelay(SiteLicenseService::tick, 0L, 60L, TimeUnit.SECONDS);
      }
   }

   public static List<SiteLicenseService.Data> getItems() {
      return items;
   }

   public static void method4604() {
      longValue2 = 0L;
      longValue = 0L;
      tick();
   }

   private static void tick() {
      try {
         long now = System.currentTimeMillis();
         String str = SiteSession.method2289();
         if (atomicBoolean2.get() || str.isEmpty()) {
            return;
         }

         if (!str.equals(text)) {
            longValue = 0L;
            longValue2 = 0L;
         }

         if (longValue != 0L && now - longValue < 1800000L) {
            return;
         }

         if (longValue2 != 0L && now - longValue2 < 300000L) {
            return;
         }

         if (!atomicBoolean2.compareAndSet(false, true)) {
            return;
         }

         text = str;
         method4605().whenComplete((var0x, throwable) -> atomicBoolean2.set(false));
      } catch (Throwable e) {
         atomicBoolean2.set(false);
         logger.debug("Лицензии сайта: {}", e.toString());
      }
   }

   private static CompletableFuture<Void> method4605() {
      var thenApply = SiteHttpClient.method2411("/license/get_all_working", new JsonObject()).thenApply(SiteLicenseService::method4606);
      var exceptionally = SiteHttpClient.method2411("/user/login_token", new JsonObject())
         .thenApply(var0x -> "admin".equalsIgnoreCase(SiteHttpClient.method2417(var0x, "role", "").trim()))
         .exceptionally(var0x -> false);
      return thenApply.<Boolean, Void>thenCombine(exceptionally, (var0x, var1x) -> {
         method4607((List<SiteLicenseService.Data>)var0x, var1x);
         return (Void)null;
      }).whenComplete((var0x, var1x) -> {
         if (var1x != null) {
            longValue2 = System.currentTimeMillis();
            logger.info("Лицензии сайта не пришли: {}", SiteHttpClient.method2424(var1x));
         }
      });
   }

   private static List<SiteLicenseService.Data> method4606(JsonObject json) {
      JsonElement jsonElement = json == null ? null : json.get("current_licenses");
      if (jsonElement != null && jsonElement.isJsonArray()) {
         JsonArray jsonArray = jsonElement.getAsJsonArray();
         List<Data> coll = new ArrayList<>(jsonArray.size());

         for (JsonElement json2 : jsonArray) {
            if (json2.isJsonObject()) {
               JsonObject jsonObject = json2.getAsJsonObject();
               int intValue = SiteHttpClient.method2421(jsonObject, "id", 0);
               if (intValue > 0) {
                  coll.add(
                     new SiteLicenseService.Data(intValue, SiteHttpClient.method2417(jsonObject, "name", ""), Math.max(0, SiteHttpClient.method2421(jsonObject, "days", 0)))
                  );
               }
            }
         }

         return List.copyOf(coll);
      } else {
         return List.of();
      }
   }

   private static void method4607(List<SiteLicenseService.Data> datas, boolean flag) {
      items = datas;
      longValue = System.currentTimeMillis();
      longValue2 = 0L;
      SiteLicenseService.Data data = null;

      for (SiteLicenseService.Data data2 : datas) {
         int n = method4608(data2.getIntValue());
         if (n > 0
            && (data == null || n > method4608(data.getIntValue()) || n == method4608(data.getIntValue()) && data2.getIntValue2() > data.getIntValue2())) {
            data = data2;
         }
      }

      Zenith.Entry.Type type = data == null ? null : method4609(data.getIntValue());
      long l = data == null ? -1L : data.getIntValue2();
      Zenith zenith = Zenith.getInstance();
      if (zenith != null && zenith.getEntry() != null) {
         zenith.getEntry().method1505(type, l, flag);
      }

      logger.info(
         "Лицензии сайта: {} → {}{}", new Object[]{method4610(datas), type == null ? "нет лицензии этой сборки" : type.getName(), flag ? " (админ)" : ""}
      );
   }

   private static int method4608(int intValue) {
      return switch (intValue) {
         case 1, 2, 3 -> 1;
         case 4 -> 3;
         default -> 0;
         case 9 -> 2;
      };
   }

   private static Zenith.Entry.Type method4609(int intValue) {
      return switch (intValue) {
         case 4 -> Zenith.Entry.Type.PRIVATE;
         case 9 -> Zenith.Entry.Type.OPUS;
         default -> Zenith.Entry.Type.method1514(null);
      };
   }

   private static String method4610(List<SiteLicenseService.Data> datas) {
      if (datas.isEmpty()) {
         return "[]";
      } else {
         StringBuilder builder = new StringBuilder("[");

         for (SiteLicenseService.Data data : datas) {
            if (builder.length() > 1) {
               builder.append(", ");
            }

            builder.append(data.getIntValue()).append(':').append(data.name().toLowerCase(Locale.ROOT)).append('/').append(data.getIntValue2()).append('d');
         }

         return builder.append(']').toString();
      }
   }

   public static final class Data {
      private final int intValue;
      private final String text;
      private final int intValue2;

      public int intValue() {
         return this.intValue;
      }

      public String text() {
         return this.text;
      }

      public int intValue2() {
         return this.intValue2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.intValue2, other.intValue2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(intValue, text, intValue2);
      }

      @Override
      public String toString() {
         return "Data[" + "intValue=" + this.intValue + ", " + "text=" + this.text + ", " + "intValue2=" + this.intValue2 + "]";
      }

      public Data(int intValue, String text, int n) {
         this.intValue = intValue;
         this.text = text;
         this.intValue2 = n;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public String name() {
         return this.text;
      }

      public int getIntValue2() {
         return this.intValue2;
      }
   }
}
