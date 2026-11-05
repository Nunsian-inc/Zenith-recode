package org.zenith.utility.misc;

import com.mojang.blaze3d.Blaze3D;
import java.io.File;
import java.net.URI;

// 26.3 removed Util.OS#openFile/openUri; Blaze3D opens through SDL now.
public final class OsOpen {
   private OsOpen() {
   }

   public static void file(File file) {
      Blaze3D.openPath(file.toPath());
   }

   public static void uri(String uri) {
      Blaze3D.openUri(URI.create(uri));
   }

   public static void uri(URI uri) {
      Blaze3D.openUri(uri);
   }
}
