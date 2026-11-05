package org.zenith.utility.config;

import java.io.File;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zenith.Zenith;

public final class SiteSession {
   private static final String text = "zenith.site.token";
   private static final String text2 = "ZENITH_SITE_TOKEN";
   private static final int CONST_intValue = 8;
   private static final int CONST_intValue2 = 4096;
   private static final long CONST_longValue = 10000L;
   private static final Logger logger = LoggerFactory.getLogger("Zenith/Site");
   private static volatile String text3 = "";
   private static volatile long longValue;
   private static volatile Path path;
   private static volatile long longValue2;
   private static final Set<String> items = ConcurrentHashMap.newKeySet();
   private static volatile Method[] methods;

   private SiteSession() {
   }

   public static String method2289() {
      String str = text3;
      long now = System.currentTimeMillis();
      if (!str.isEmpty()) {
         if (path != null && now - longValue >= 10000L) {
            synchronized (SiteSession.class) {
               if (System.currentTimeMillis() - longValue < 10000L) {
                  return text3;
               } else {
                  longValue = System.currentTimeMillis();
                  Path path2 = path;
                  if (path2 != null && method2290(path2) != longValue2) {
                     text3 = method2293();
                  }

                  return text3;
               }
            }
         } else {
            return str;
         }
      } else if (now - longValue < 10000L) {
         return "";
      } else {
         synchronized (SiteSession.class) {
            if (!text3.isEmpty()) {
               return text3;
            } else {
               longValue = System.currentTimeMillis();
               text3 = method2293();
               return text3;
            }
         }
      }
   }

   private static long method2290(Path path) {
      try {
         return Files.isRegularFile(path) ? Files.getLastModifiedTime(path).toMillis() : -1L;
      } catch (Exception e) {
         return -1L;
      }
   }

   public static boolean method2291() {
      return method2289().length() >= 8;
   }

   public static void method2292() {
      synchronized (SiteSession.class) {
         text3 = "";
         longValue = 0L;
         path = null;
         longValue2 = 0L;
      }
   }

   private static String method2293() {
      path = null;
      longValue2 = 0L;
      String str = method2300(System.getProperty("zenith.site.token", ""), "property");
      if (!str.isEmpty()) {
         return str;
      } else {
         str = method2300(System.getenv("ZENITH_SITE_TOKEN"), "environment");
         if (!str.isEmpty()) {
            return str;
         } else {
            str = method2295();
            if (!str.isEmpty()) {
               return str;
            } else {
               str = method2297();
               return !str.isEmpty() ? str : method2294();
            }
         }
      }
   }

   private static String method2294() {
      try {
         File file = Zenith.file;
         return file == null ? "" : method2298(file.toPath().resolve("site-token.txt"));
      } catch (Throwable e) {
         return "";
      }
   }

   private static String method2295() {
      for (Method method : method2296()) {
         try {
            Object obj = method.invoke(null);
            String str = method2300(obj == null ? "" : obj.toString(), "protection");
            if (!str.isEmpty()) {
               return str;
            }
         } catch (RuntimeException | ReflectiveOperationException e) {
         }
      }

      return "";
   }

   private static Method[] method2296() {
      Method[] methods2 = methods;
      if (methods2 != null) {
         return methods2;
      } else {
         ArrayList arrayList = new ArrayList();

         try {
            Class clazz = Class.forName("ru.session.Session");

            for (String name : new String[]{"getToken", "getSessionToken", "token"}) {
               try {
                  arrayList.add(clazz.getMethod(name));
               } catch (RuntimeException | ReflectiveOperationException e) {
               }
            }
         } catch (RuntimeException | ReflectiveOperationException e) {
         }

         methods2 = (Method[])arrayList.toArray(new Method[0]);
         methods = methods2;
         return methods2;
      }
   }

   private static String method2297() {
      Path path = method2299();
      return path == null ? "" : method2298(path.resolve("Zenith-Launcher").resolve("session.token"));
   }

   private static String method2298(Path path2) {
      try {
         if (!Files.isReadable(path2)) {
            return "";
         } else if (Files.size(path2) > 4160L) {
            method2301(path2.getFileName().toString(), "файл " + Files.size(path2) + " байт");
            return "";
         } else {
            long millis = Files.getLastModifiedTime(path2).toMillis();
            String str = method2300(new String(Files.readAllBytes(path2), StandardCharsets.UTF_8), path2.getFileName().toString());
            if (!str.isEmpty()) {
               path = path2;
               longValue2 = millis;
            }

            return str;
         }
      } catch (Exception e) {
         return "";
      }
   }

   private static Path method2299() {
      String lowerCase = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);

      try {
         if (lowerCase.contains("win")) {
            String getenv = System.getenv("LOCALAPPDATA");
            return getenv != null && !getenv.isBlank() ? Path.of(getenv) : null;
         } else {
            String property = System.getProperty("user.home", "");
            if (property.isBlank()) {
               return null;
            } else if (!lowerCase.contains("mac") && !lowerCase.contains("darwin")) {
               String getenv = System.getenv("XDG_DATA_HOME");
               return getenv != null && !getenv.isBlank() ? Path.of(getenv) : Path.of(property, ".local", "share");
            } else {
               return Path.of(property, "Library", "Application Support");
            }
         }
      } catch (RuntimeException e) {
         return null;
      }
   }

   private static String method2300(String property, String string) {
      if (property == null) {
         return "";
      } else {
         String str = property.strip();
         if (str.isEmpty()) {
            return "";
         } else if (str.length() >= 8 && str.length() <= 4096) {
            for (int i = 0; i < str.length(); i++) {
               char charAt = str.charAt(i);
               if (charAt <= ' ' || charAt > '~') {
                  method2301(string, "непечатаемый символ, длина " + str.length());
                  return "";
               }
            }

            return str;
         } else {
            method2301(string, "длина " + str.length());
            return "";
         }
      }
   }

   private static void method2301(String string, String str) {
      if (items.add(string)) {
         logger.warn("Сайт: токен сессии из {} отвергнут ({})", string, str);
      }
   }
}
