package org.zenith.client.scripting;

import com.mojang.blaze3d.platform.NativeImage;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.TargetDataLine;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.material.MapColor;
import javax.sound.sampled.AudioFileFormat.Type;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.zenith.utility.misc.HudHolder;
import org.zenith.utility.misc.MediaSessionManager;
import org.zenith.utility.misc.QrCode;
import org.zenith.utility.misc.ScriptPermissions;

public final class ScriptMediaApi {
   private static final AtomicBoolean atomicBoolean = new AtomicBoolean();
   private static final AtomicBoolean atomicBoolean2 = new AtomicBoolean();
   private static int[] intValues;

   private ScriptMediaApi() {
   }

   public static Scriptable method1736(Scriptable scope) {
      ScriptValueUtils.Entry2 entry2 = ScriptValueUtils.method6247(scope);
      entry2.method6274(
         "current",
         var1x -> {
            MediaSessionManager mediaSessionManager = MediaSessionManager.getInstance();
            return !mediaSessionManager.active()
               ? null
               : ScriptValueUtils.method6247(scope)
                  .method6277("title", mediaSessionManager.title())
                  .method6277("artist", mediaSessionManager.method6006())
                  .method6277("playing", mediaSessionManager.method6007())
                  .method6277("position", mediaSessionManager.method6009())
                  .method6277("duration", mediaSessionManager.method6008())
                  .getScriptableObject();
         }
      );
      entry2.method6274("playing", var0x -> MediaSessionManager.getInstance().active() && MediaSessionManager.getInstance().method6007());
      entry2.method6274("toggle", var0x -> {
         MediaSessionManager.getInstance().playPause();
         return null;
      });
      entry2.method6274("next", var0x -> {
         MediaSessionManager.getInstance().next();
         return null;
      });
      entry2.method6274("previous", var0x -> {
         MediaSessionManager.getInstance().previous();
         return null;
      });
      entry2.method6274("seek", var0x -> {
         MediaSessionManager.getInstance().method6019(Math.max(0.0, ScriptValueUtils.method6256(var0x, 0, 0.0)));
         return null;
      });
      return entry2.getScriptableObject();
   }

   private static Scriptable method1737(Script workspace, Storage storage, Scriptable scope, BufferedImage bufferedImage) {
      int width = Math.min(2048, bufferedImage.getWidth());
      int height = Math.min(2048, bufferedImage.getHeight());
      NativeImage nativeImage = new NativeImage(width, height, false);
      int[] rgbArray = new int[width];

      for (int i = 0; i < height; i++) {
         bufferedImage.getRGB(0, i, width, 1, rgbArray, 0, width);

         for (int j = 0; j < width; j++) {
            nativeImage.setPixel(j, i, rgbArray[j]);
         }
      }

      return RemoteUtil.method1836(scope, storage, RemoteUtil.method1829(workspace, List.of(nativeImage), List.of(0)));
   }

   public static void method1738(Script workspace, Storage storage, Scriptable scope, ScriptableObject scriptableObject) {
      ScriptValueUtils.Entry2 entry2 = ScriptValueUtils.method6246(scope, scriptableObject);
      entry2.method6274("qr", var3x -> {
         String var4x = ScriptValueUtils.method6254(var3x, 0, "");
         if (var4x.isEmpty()) {
            throw ScriptValueUtils.method6251("image.qr: пустой текст");
         } else {
            Scriptable scriptable = ScriptValueUtils.method6262(var3x, 1);
            int n = scriptable == null ? 4 : (int)Math.max(1.0, Math.min(16.0, ScriptValueUtils.method6264(scriptable, "scale", 4.0)));
            int n2 = scriptable == null ? 4 : (int)Math.max(0.0, Math.min(8.0, ScriptValueUtils.method6264(scriptable, "margin", 4.0)));
            int n3 = scriptable == null ? -16777216 : (int)((long)ScriptValueUtils.method6264(scriptable, "color", 4.27819E9F));
            int n4 = scriptable == null ? -1 : (int)((long)ScriptValueUtils.method6264(scriptable, "background", 4.294967295E9));

            boolean[][] flagses;
            try {
               flagses = QrCode.method3020(var4x.getBytes(StandardCharsets.UTF_8));
            } catch (IllegalArgumentException e) {
               throw ScriptValueUtils.method6251(e.getMessage());
            }

            int width = (flagses.length + n2 * 2) * n;
            NativeImage nativeImage = new NativeImage(width, width, false);

            for (int i = 0; i < width; i++) {
               for (int j = 0; j < width; j++) {
                  int n5 = j / n - n2;
                  int n6 = i / n - n2;
                  boolean flag = n5 >= 0 && n6 >= 0 && n5 < flagses.length && n6 < flagses.length && flagses[n6][n5];
                  nativeImage.setPixel(j, i, flag ? n3 : n4);
               }
            }

            return RemoteUtil.method1836(scope, storage, RemoteUtil.method1829(workspace, List.of(nativeImage), List.of(0)));
         }
      });
      entry2.method6274("text", var3x -> {
         String var4x = ScriptValueUtils.method6254(var3x, 0, "");
         if (!var4x.isEmpty() && var4x.length() <= 2000) {
            Scriptable scriptable = ScriptValueUtils.method6262(var3x, 1);
            int size = scriptable == null ? 32 : (int)Math.max(6.0, Math.min(160.0, ScriptValueUtils.method6264(scriptable, "size", 32.0)));
            int rgba = scriptable == null ? -1 : (int)((long)ScriptValueUtils.method6264(scriptable, "color", 4.294967295E9));
            int n = scriptable == null ? 0 : (int)((long)ScriptValueUtils.method6264(scriptable, "background", 0.0));
            int n2 = scriptable == null ? 4 : (int)Math.max(0.0, Math.min(64.0, ScriptValueUtils.method6264(scriptable, "padding", 4.0)));
            boolean flag = scriptable != null && ScriptValueUtils.method6265(scriptable, "bold", false);
            String str;
            if (scriptable == null) {
               str = "SansSerif";
            } else {
               String lowerCase = ScriptValueUtils.method6266(scriptable, "font", "sans").toLowerCase(Locale.ROOT);
               switch (lowerCase) {
                  case "serif":
                     str = "Serif";
                     break;
                  case "mono":
                  case "monospace":
                     str = "Monospaced";
                     break;
                  default:
                     str = "SansSerif";
               }
            }

            String name = str;
            Font font = new Font(name, flag ? 1 : 0, size);
            BufferedImage bufferedImage = new BufferedImage(1, 1, 2);
            Graphics2D graphics = bufferedImage.createGraphics();
            graphics.setFont(font);
            FontMetrics fontMetrics = graphics.getFontMetrics();
            String[] strings = var4x.split("\n");
            int width = 1;

            for (String str2 : strings) {
               width = Math.max(width, fontMetrics.stringWidth(str2));
            }

            int height = fontMetrics.getHeight() * strings.length;
            graphics.dispose();
            width = Math.min(2048, width + n2 * 2);
            height = Math.min(2048, height + n2 * 2);
            BufferedImage bufferedImage2 = new BufferedImage(width, height, 2);
            Graphics2D graphics2D = bufferedImage2.createGraphics();
            graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (n >>> 24 != 0) {
               graphics2D.setColor(new Color(n, true));
               graphics2D.fillRect(0, 0, width, height);
            }

            graphics2D.setFont(font);
            graphics2D.setColor(new Color(rgba, true));

            for (int i = 0; i < strings.length; i++) {
               graphics2D.drawString(strings[i], n2, n2 + fontMetrics.getAscent() + i * fontMetrics.getHeight());
            }

            graphics2D.dispose();
            return method1737(workspace, storage, scope, bufferedImage2);
         } else {
            throw ScriptValueUtils.method6251("image.text: от 1 до 2000 символов");
         }
      });
      entry2.method6274("mapArt", var3x -> {
         RemoteUtil.Entry var4x = RemoteUtil.method1827(ScriptValueUtils.arg(var3x, 0));
         NativeImage nativeImage = var4x.method1848();
         int[] ints = method1739();
         NativeImage nativeImage2 = new NativeImage(128, 128, false);

         for (int i = 0; i < 128; i++) {
            for (int j = 0; j < 128; j++) {
               int x = Math.min(nativeImage.getWidth() - 1, j * nativeImage.getWidth() / 128);
               int y = Math.min(nativeImage.getHeight() - 1, i * nativeImage.getHeight() / 128);
               int pixel = nativeImage.getPixel(x, y);
               if (pixel >>> 24 < 128) {
                  nativeImage2.setPixel(j, i, 0);
               } else {
                  int n = ints[0];
                  long l = Long.MAX_VALUE;

                  for (int n2 : ints) {
                     int n3 = (pixel >> 16 & 0xFF) - (n2 >> 16 & 0xFF);
                     int n4 = (pixel >> 8 & 0xFF) - (n2 >> 8 & 0xFF);
                     int n5 = (pixel & 0xFF) - (n2 & 0xFF);
                     long l2 = 3L * n3 * n3 + 4L * n4 * n4 + 2L * n5 * n5;
                     if (l2 < l) {
                        l = l2;
                        n = n2;
                     }
                  }

                  nativeImage2.setPixel(j, i, n | 0xFF000000);
               }
            }
         }

         return RemoteUtil.method1836(scope, storage, RemoteUtil.method1829(workspace, List.of(nativeImage2), List.of(0)));
      });
   }

   private static synchronized int[] method1739() {
      if (intValues != null) {
         return intValues;
      } else {
         ArrayList arrayList = new ArrayList();

         for (int i = 1; i < 64; i++) {
            MapColor mapColor = MapColor.byId(i);
            if (mapColor != null && mapColor.col != 0 && mapColor.id == i) {
               for (int j = 0; j < 4; j++) {
                  arrayList.add(MapColor.getColorFromPackedId(i * 4 + j));
               }
            }
         }

         intValues = arrayList.stream().mapToInt(x -> (Integer)x).toArray();
         return intValues;
      }
   }

   public static void method1740(Script workspace, Storage storage, Scriptable scope, ScriptableObject scriptableObject) {
      ScriptValueUtils.Entry2 entry2 = ScriptValueUtils.method6246(scope, scriptableObject);
      entry2.method6274(
         "tts",
         var2x -> {
            ScriptPermissions.method4(workspace, "tts");
            String var3x = ScriptValueUtils.method6254(var2x, 0, "").replaceAll("[\\p{Cntrl}]", " ").strip();
            if (var3x.isEmpty() || var3x.length() > 500) {
               throw ScriptValueUtils.method6251("sound.tts: от 1 до 500 символов");
            } else if (!System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("win")) {
               throw ScriptValueUtils.method6251("озвучка работает только на Windows");
            } else if (!atomicBoolean.compareAndSet(false, true)) {
               return false;
            } else {
               Scriptable var4x = ScriptValueUtils.method6262(var2x, 1);
               int n = var4x == null ? 0 : (int)Math.max(-10.0, Math.min(10.0, ScriptValueUtils.method6264(var4x, "rate", 0.0)));
               int n2 = var4x == null ? 100 : (int)Math.max(0.0, Math.min(100.0, ScriptValueUtils.method6264(var4x, "volume", 100.0)));
               var function = var4x == null ? null : (ScriptValueUtils.method6263(var4x, "onDone") instanceof Function function2 ? function2 : null);
               Thread.ofPlatform()
                  .daemon(true)
                  .name("zenith-script-tts")
                  .start(
                     () -> {
                        try {
                           ProcessBuilder var6x = new ProcessBuilder(
                              "powershell",
                              "-NoProfile",
                              "-NonInteractive",
                              "-ExecutionPolicy",
                              "Bypass",
                              "-Command",
                              "Add-Type -AssemblyName System.Speech; $s = New-Object System.Speech.Synthesis.SpeechSynthesizer; $s.Rate = "
                                 + n
                                 + "; $s.Volume = "
                                 + n2
                                 + "; $s.Speak($env:ZENITH_TTS_TEXT)"
                           );
                           var6x.environment().put("ZENITH_TTS_TEXT", var3x);
                           var6x.redirectErrorStream(true);
                           Process var7x = var6x.start();
                           var7x.getInputStream().readAllBytes();
                           if (!var7x.waitFor(60L, TimeUnit.SECONDS)) {
                              var7x.destroyForcibly();
                           }
                        } catch (Exception e) {
                        } finally {
                           atomicBoolean.set(false);
                           if (function != null) {
                              RemoteUtil.method1847(storage, workspace, function);
                           }
                        }
                     }
                  );
               return true;
            }
         }
      );
      entry2.method6274("speaking", var0x -> atomicBoolean.get());
      entry2.method6274("record", var2x -> {
         ScriptPermissions.method4(workspace, "microphone");
         if (workspace.getPath() == null) {
            throw ScriptValueUtils.method6251("запись доступна только скриптам-папкам");
         } else {
            long var3x = (long)Math.max(200.0, Math.min(30000.0, ScriptValueUtils.method6256(var2x, 0, 3000.0)));
            var function = ScriptValueUtils.method6260(var2x, 1);
            if (!atomicBoolean2.compareAndSet(false, true)) {
               throw ScriptValueUtils.method6251("уже идёт запись");
            } else {
               HudHolder.method6969().method6973("d", Component.literal(workspace.method1403() + ": запись микрофона " + var3x / 1000.0 + " с"), Math.max(1500L, var3x));
               Thread.ofPlatform().daemon(true).name("zenith-script-record").start(() -> {
                  String var5x = null;
                  String str = null;
                  AudioFormat audioFormat = new AudioFormat(44100.0F, 16, 1, true, false);

                  try (TargetDataLine targetDataLine = AudioSystem.getTargetDataLine(audioFormat)) {
                     targetDataLine.open(audioFormat);
                     targetDataLine.start();
                     ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                     byte[] bytes = new byte[4096];
                     long l = System.currentTimeMillis() + var3x;

                     while (System.currentTimeMillis() < l && workspace.isRunning()) {
                        int len = targetDataLine.read(bytes, 0, bytes.length);
                        if (len > 0) {
                           byteArrayOutputStream.write(bytes, 0, len);
                        }
                     }

                     targetDataLine.stop();
                     byte[] byteArray = byteArrayOutputStream.toByteArray();
                     Path dir = workspace.getPath().resolve("sounds");
                     Files.createDirectories(dir);
                     String str2 = "rec-" + System.currentTimeMillis() + ".wav";

                     try (AudioInputStream audioInputStream = new AudioInputStream(new ByteArrayInputStream(byteArray), audioFormat, byteArray.length / audioFormat.getFrameSize())) {
                        AudioSystem.write(audioInputStream, Type.WAVE, dir.resolve(str2).toFile());
                     }

                     str = "sounds/" + str2;
                  } catch (Exception e) {
                     var5x = e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage();
                  } finally {
                     atomicBoolean2.set(false);
                  }

                  String str2 = str;
                  String str3 = var5x;
                  Minecraft.getInstance().execute(() -> {
                     if (workspace.isRunning()) {
                        storage.method1321(workspace, function, str2, str3);
                     }
                  });
               });
               return null;
            }
         }
      });
      entry2.method6274("recording", var0x -> atomicBoolean2.get());
   }
}
