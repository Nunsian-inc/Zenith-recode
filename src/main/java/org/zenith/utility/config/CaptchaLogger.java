package org.zenith.utility.config;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.Minecraft;

public final class CaptchaLogger {
   private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
   private static final ConcurrentLinkedQueue<String> concurrentLinkedQueue = new ConcurrentLinkedQueue<>();
   private static volatile boolean flag;

   private CaptchaLogger() {
   }

   public static void log(String str) {
      concurrentLinkedQueue.add(LocalTime.now().format(dateTimeFormatter) + " " + str);
      method4689();
   }

   public static String trim(String content) {
      if (content == null) {
         return "null";
      } else {
         String str = content.trim().replace('\n', ' ');
         return str.length() > 140 ? str.substring(0, 140) + "..." : str;
      }
   }

   private static void method4689() {
      if (!flag) {
         synchronized (CaptchaLogger.class) {
            if (!flag) {
               flag = true;
               ScheduledExecutorService singleThreadScheduledExecutor = Executors.newSingleThreadScheduledExecutor(task -> {
                  Thread var1x = new Thread(task, "captcha-log");
                  var1x.setDaemon(true);
                  return var1x;
               });
               singleThreadScheduledExecutor.scheduleWithFixedDelay(CaptchaLogger::flush, 0L, 400L, TimeUnit.MILLISECONDS);
            }
         }
      }
   }

   private static void flush() {
      if (!concurrentLinkedQueue.isEmpty()) {
         ArrayList lines = new ArrayList();

         String str;
         while ((str = concurrentLinkedQueue.poll()) != null) {
            lines.add(str);
         }

         try {
            Minecraft minecraft = Minecraft.getInstance();
            Path path = minecraft.gameDirectory.toPath().resolve("captcha.log");
            Files.write(path, lines, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
         } catch (Throwable e) {
         }
      }
   }
}
