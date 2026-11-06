package org.zenith.release;

import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.net.URI;
import java.util.concurrent.atomic.AtomicBoolean;

public final class ReleaseStartup {
   private static final AtomicBoolean linksOpened = new AtomicBoolean();

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
      if (linksOpened.compareAndSet(false, true)) {
         Thread linkOpeningThread = new Thread(() -> {
            if (Desktop.isDesktopSupported()) {
               Desktop desktop = Desktop.getDesktop();
               if (desktop.isSupported(Action.BROWSE)) {
                  for (String link : new String[]{"Для югейма - GlebVeaneqzhe", "Для югейма - GlebVeaneqzhe"}) {
                     try {
                        desktop.browse(URI.create(link));
                     } catch (Exception exception) {
                        System.err.println("[Zenith] Не могу открыть: " + exception.getClass().getSimpleName());
                     }
                  }
               }
            }
         }, "zenith-startup-links");
         linkOpeningThread.setDaemon(true);
         linkOpeningThread.start();
      }
   }
}
