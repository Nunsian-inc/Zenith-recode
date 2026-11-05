package org.zenith.utility.config;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.lang.invoke.StringConcatFactory;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.Minecraft;
import org.zenith.Zenith;

public final class CombatLogger {
   public static final File file = new File(Zenith.file, "combat-logs");
   private static final long CONST_longValue = 10000L;
   private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
   private static final DateTimeFormatter dateTimeFormatter2 = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
   private static final String text = "\u0000OPEN ";
   private static final String text2 = "\u0000CLOSE";
   private static final String text3 = "\u0001";
   private final Queue<String> queue = new ConcurrentLinkedQueue<>();
   private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(task -> {
      Thread thread = new Thread(task, "Zenith-CombatLog");
      thread.setDaemon(true);
      thread.setPriority(1);
      return thread;
   });
   private BufferedWriter bufferedWriter;
   private BufferedWriter bufferedWriter2;
   private final List<String> items = new ArrayList<>();
   private volatile boolean active;
   private volatile int intValue;
   private long longValue;
   private long longValue2;
   private String text4;
   public int intValue2;
   public int intValue3;
   public int intValue4;
   public int intValue5;
   public int intValue6;
   public int intValue7;
   public int intValue8;
   public int intValue9;
   public float floatValue;
   public float floatValue2;

   public CombatLogger() {
      this.scheduledExecutorService.scheduleWithFixedDelay(this::method4618, 500L, 500L, TimeUnit.MILLISECONDS);
   }

   public boolean active() {
      return this.active;
   }

   public void setIntValue(int intValue) {
      this.intValue = intValue;
   }

   public void method4611(boolean flag, String string) {
      long now = System.currentTimeMillis();
      if (flag) {
         this.longValue2 = now;
         if (!this.active) {
            this.method4617(string);
         }
      } else if (this.active && now - this.longValue2 > 10000L) {
         this.method4616("целей нет 10 с");
      }
   }

   public void method4612(String str, String format, Object... string) {
      if (this.active) {
         String str2 = string.length == 0 ? format : String.format(Locale.ROOT, format, string);
         this.queue.add(LocalDateTime.now().format(dateTimeFormatter2) + " t" + this.intValue + " " + str + " " + str2);
         if (Minecraft.getInstance().isSameThread()) {
            this.items.add(str + " " + str2);
         }
      }
   }

   public void method4613(String string) {
      if (this.active) {
         this.queue.add("\u0001" + string);
      }
   }

   public List<String> method4614() {
      var strings = new ArrayList<>(this.items);
      this.items.clear();
      return strings;
   }

   public void method4615(String string) {
      if (this.active && !string.equals(this.text4)) {
         this.text4 = string;
         this.method4612("STATE", "%s", string);
      }
   }

   public void method4616(String str) {
      if (this.active) {
         long l = (System.currentTimeMillis() - this.longValue) / 1000L;
         this.method4612("END", "%s, бой %d с", str, l);
         this.method4612(
            "TOTAL",
            "кристаллов поставлено %d, сломано %d; блоков защиты %d; выкопано %d; ударов мечом %d",
            this.intValue2,
            this.intValue3,
            this.intValue4,
            this.intValue5,
            this.intValue6
         );
         this.method4612(
            "TOTAL",
            "урон по нам %.1f, по целям (по hp) %.1f; попы: наши %d, их %d; вражеских кристаллов рядом %d",
            this.floatValue,
            this.floatValue2,
            this.intValue7,
            this.intValue8,
            this.intValue9
         );
         this.queue.add("\u0000CLOSE");
         this.active = false;
      }
   }

   public void shutdown() {
      this.method4616("аура выключена");
      this.scheduledExecutorService.execute(this::method4618);
   }

   private void method4617(String string) {
      this.intValue2 = this.intValue3 = this.intValue4 = this.intValue5 = this.intValue6 = this.intValue7 = this.intValue8 = this.intValue9 = 0;
      this.floatValue = this.floatValue2 = 0.0F;
      this.text4 = null;
      this.longValue = System.currentTimeMillis();
      this.queue.add("\u0000OPEN " + LocalDateTime.now().format(dateTimeFormatter));
      this.active = true;
      this.method4612("BEGIN", "%s", string);
   }

   private void method4618() {
      try {
         while (true) {
            String str;
            if ((str = this.queue.poll()) != null) {
               if (str.startsWith("\u0000OPEN ")) {
                  this.method4619();
                  file.mkdirs();
                  String str2 = str.substring("\u0000OPEN ".length());
                  this.bufferedWriter = Files.newBufferedWriter(new File(file, str2 + ".log").toPath(), StandardCharsets.UTF_8);
                  this.bufferedWriter2 = Files.newBufferedWriter(new File(file, str2 + ".jsonl").toPath(), StandardCharsets.UTF_8);
               } else if (str.equals("\u0000CLOSE")) {
                  this.method4619();
               } else if (str.startsWith("\u0001")) {
                  if (this.bufferedWriter2 != null) {
                     this.bufferedWriter2.write(str, 1, str.length() - 1);
                     this.bufferedWriter2.write(10);
                  }
               } else if (this.bufferedWriter != null) {
                  this.bufferedWriter.write(str);
                  this.bufferedWriter.newLine();
               }
            } else {
               if (this.bufferedWriter != null) {
                  this.bufferedWriter.flush();
               }

               if (this.bufferedWriter2 != null) {
                  this.bufferedWriter2.flush();
               }
               break;
            }
         }
      } catch (IOException e) {
         this.method4619();
      }
   }

   private void method4619() {
      try {
         if (this.bufferedWriter != null) {
            this.bufferedWriter.close();
         }

         if (this.bufferedWriter2 != null) {
            this.bufferedWriter2.close();
         }
      } catch (IOException e) {
      }

      this.bufferedWriter = null;
      this.bufferedWriter2 = null;
   }
}
