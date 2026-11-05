package org.zenith.utility.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.ProcessBuilder.Redirect;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.client.Minecraft;
import org.zenith.Zenith;
import org.zenith.utility.misc.MediaPlayerState;
import org.zenith.utility.misc.MediaSessionManager;

public final class MediaHelperProcess {
   private static final MediaHelperProcess INSTANCE = new MediaHelperProcess();
   private static final boolean CONST_flag = System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("win");
   private final ExecutorService executorService = Executors.newSingleThreadExecutor(task -> {
      Thread thread = new Thread(task, "zenith-media-helper");
      thread.setDaemon(true);
      return thread;
   });
   private Process process;
   private BufferedWriter bufferedWriter;
   private long longValue;
   private volatile boolean flag;
   private volatile int intValue;
   private volatile long longValue2;
   private volatile boolean flag2;
   private volatile boolean flag3;
   private final AtomicReference<String> atomicReference = new AtomicReference<>();

   private MediaHelperProcess() {
   }

   public static MediaHelperProcess getInstance() {
      return INSTANCE;
   }

   public boolean method6859() {
      return CONST_flag;
   }

   public boolean isFlag2() {
      return this.flag2;
   }

   public boolean isFlag3() {
      return this.flag3;
   }

   public boolean isFlag() {
      return this.flag;
   }

   public int getIntValue() {
      return this.intValue;
   }

   public long getLongValue2() {
      return this.longValue2;
   }

   public void method6860(String owner, double doubleValue) {
      this.method6869("seek|" + clean(owner) + "|" + String.format(Locale.ROOT, "%.3f", doubleValue));
   }

   public void method6861(String owner, float floatValue) {
      String newValue = "vol|" + clean(owner) + "|" + String.format(Locale.ROOT, "%.3f", floatValue);
      if (this.atomicReference.getAndSet(newValue) == null && CONST_flag) {
         this.executorService.execute(() -> {
            String var1x = this.atomicReference.getAndSet(null);
            if (var1x != null) {
               this.write(var1x);
            }
         });
      }
   }

   public void method6862(String owner, float f, int n) {
      this.method6869("fade|" + clean(owner) + "|" + String.format(Locale.ROOT, "%.3f", f) + "|" + n);
   }

   public void method6863(String owner) {
      this.method6869("getvol|" + clean(owner));
   }

   public void method6864(String owner) {
      this.method6869("shuffle|" + clean(owner));
   }

   public void method6865(String owner) {
      this.method6869("repeat|" + clean(owner));
   }

   public void method6866(String owner) {
      this.method6869("time|" + clean(owner));
   }

   public void method6867(String owner) {
      this.method6869("meter|" + clean(owner));
   }

   public void method6868(String owner) {
      this.method6869("state|" + clean(owner));
   }

   private static String clean(String owner) {
      return owner == null ? "" : owner.replace('|', ' ').replace('\n', ' ');
   }

   private void method6869(String str) {
      if (CONST_flag) {
         this.executorService.execute(() -> this.write(str));
      }
   }

   private void write(String str) {
      try {
         if (!this.method6870()) {
            return;
         }

         this.bufferedWriter.write(str);
         this.bufferedWriter.write(10);
         this.bufferedWriter.flush();
      } catch (Throwable e) {
         this.stop();
      }
   }

   private boolean method6870() {
      if (this.process != null && this.process.isAlive()) {
         return true;
      } else if (System.currentTimeMillis() - this.longValue < 30000L) {
         return false;
      } else {
         try {
            Path dir = Zenith.file.toPath().resolve("media");
            Files.createDirectories(dir);
            Path target = dir.resolve("helper.ps1");

            try (InputStream resourceAsStream = MediaHelperProcess.class.getResourceAsStream("/assets/zenith/media/helper.ps1")) {
               if (resourceAsStream == null) {
                  this.longValue = System.currentTimeMillis();
                  return false;
               }

               Files.copy(resourceAsStream, target, StandardCopyOption.REPLACE_EXISTING);
            }

            ProcessBuilder processBuilder = new ProcessBuilder(
               "powershell.exe", "-NoLogo", "-NoProfile", "-NonInteractive", "-ExecutionPolicy", "Bypass", "-WindowStyle", "Hidden", "-File", target.toString()
            );
            processBuilder.redirectErrorStream(false);
            processBuilder.redirectError(Redirect.DISCARD);
            this.process = processBuilder.start();
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(this.process.getOutputStream(), StandardCharsets.UTF_8));
            Process process = this.process;
            Thread thread = new Thread(() -> this.method6871(process), "zenith-media-reader");
            thread.setDaemon(true);
            thread.start();
            return true;
         } catch (Throwable e) {
            this.longValue = System.currentTimeMillis();
            this.process = null;
            return false;
         }
      }
   }

   private void method6871(Process process) {
      String line;
      try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
         while ((line = bufferedReader.readLine()) != null) {
            String str = line.trim();
            if (str.startsWith("peak ")) {
               MediaPlayerState.getInstance().method1671(method6873(str.substring(5)));
            } else if (str.startsWith("time ")) {
               String[] strings = str.split(" ");
               if (strings.length >= 5) {
                  double d = method6873(strings[1]);
                  double d2 = method6873(strings[2]);
                  long l = method6872(strings[3]);
                  boolean flag = strings[4].trim().equals("4");
                  Minecraft.getInstance().execute(() -> MediaSessionManager.getInstance().method6002(d, d2, l, flag));
               }
            } else if (str.startsWith("vol ")) {
               float floatValue = method6873(str.substring(4));
               Minecraft.getInstance().execute(() -> MediaPlayerState.getInstance().method1670(floatValue));
            } else if (str.startsWith("state ")) {
               String[] strings = str.split(" ");
               if (strings.length >= 3) {
                  this.flag = strings[1].equalsIgnoreCase("true");
                  this.intValue = (int)method6873(strings[2]);
                  if (strings.length >= 5) {
                     this.flag2 = strings[3].equalsIgnoreCase("true");
                     this.flag3 = strings[4].equalsIgnoreCase("true");
                  }

                  this.longValue2 = System.currentTimeMillis();
               }
            }
         }
      } catch (Throwable e) {
      }
   }

   private static long method6872(String str) {
      try {
         return Long.parseLong(str.trim());
      } catch (NumberFormatException e) {
         return 0L;
      }
   }

   private static float method6873(String str) {
      try {
         return Float.parseFloat(str.trim().replace(',', '.'));
      } catch (NumberFormatException e) {
         return -1.0F;
      }
   }

   public void stop() {
      if (this.process != null) {
         this.process.destroy();
         this.process = null;
      }
   }
}
