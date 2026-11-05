package org.zenith.release;

import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.net.URI;
import java.util.concurrent.atomic.AtomicBoolean;

public final class ReleaseStartup {
   private static final AtomicBoolean opened = new AtomicBoolean();

   private ReleaseStartup() {
   }

   public static void configure() {
      if (System.getProperty("DevMode") == null) {
         System.setProperty("DevMode", "true");
      }

      if (System.getProperty("DevName") == null) {
         System.setProperty("DevName", "GlebVeaneqzhe");
      }
   }

   public static void openLinks() {
      if (opened.compareAndSet(false, true)) {
         Thread thread = new Thread(() -> {
            if (Desktop.isDesktopSupported()) {
               Desktop var0x = Desktop.getDesktop();
               if (var0x.isSupported(Action.BROWSE)) {
                  for (String str : new String[]{"Для югейма - GlebVeaneqzhe", "Для югейма - GlebVeaneqzhe"}) {
                     try {
                        var0x.browse(URI.create(str));
                     } catch (Exception e) {
                        System.err.println("[Zenith] Не могу открыть: " + e.getClass().getSimpleName());
                     }
                  }
               }
            }
         }, "zenith-startup-links");
         thread.setDaemon(true);
         thread.start();
      }
   }
}
