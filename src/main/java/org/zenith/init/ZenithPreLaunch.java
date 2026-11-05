package org.zenith.init;

import java.io.File;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import org.zenith.release.ReleaseStartup;

public final class ZenithPreLaunch implements PreLaunchEntrypoint {
   private static final String PROPERTY = "jdk.net.unixdomain.tmpdir";

   public void onPreLaunch() {
      ReleaseStartup.configure();
      if (FabricLoader.getInstance().isDevelopmentEnvironment() && System.getProperty("jdk.net.unixdomain.tmpdir") == null) {
         File file = FabricLoader.getInstance().getGameDir().resolve(".nio-tmp").toFile();
         if (file.isDirectory() || file.mkdirs()) {
            System.setProperty("jdk.net.unixdomain.tmpdir", file.getAbsolutePath());
         }
      }
   }
}
