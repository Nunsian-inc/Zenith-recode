package org.zenith.init;

import java.io.File;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import org.zenith.release.ReleaseStartup;

public final class ZenithPreLaunch implements PreLaunchEntrypoint {
   private static final String UNIX_DOMAIN_TEMP_DIRECTORY_PROPERTY = "jdk.net.unixdomain.tmpdir";

   public void onPreLaunch() {
      ReleaseStartup.configure();
      if (FabricLoader.getInstance().isDevelopmentEnvironment() && System.getProperty("jdk.net.unixdomain.tmpdir") == null) {
         File unixDomainTempDirectory = FabricLoader.getInstance().getGameDir().resolve(".nio-tmp").toFile();
         if (unixDomainTempDirectory.isDirectory() || unixDomainTempDirectory.mkdirs()) {
            System.setProperty("jdk.net.unixdomain.tmpdir", unixDomainTempDirectory.getAbsolutePath());
         }
      }
   }
}
