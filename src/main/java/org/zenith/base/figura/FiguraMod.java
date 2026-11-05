package org.zenith.base.figura;

import java.nio.file.Path;
import java.util.UUID;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zenith.base.figura.utils.IOUtils;
import org.zenith.base.figura.utils.Version;

public final class FiguraMod {
   public static final String MOD_ID = "zenith_figura";
   public static final Version VERSION = new Version("0.1.6");
   public static final Logger LOGGER = LoggerFactory.getLogger("Zenith/Figura");
   public static final float VERTEX_OFFSET = -5.0E-4F;

   private FiguraMod() {
   }

   public static boolean debugModeEnabled() {
      return false;
   }

   public static UUID getLocalPlayerUUID() {
      return Minecraft.getInstance().getUser().getProfileId();
   }

   public static boolean isLocal(UUID other) {
      UUID local = getLocalPlayerUUID();
      return local != null && local.equals(other);
   }

   public static Path getFiguraDirectory() {
      return IOUtils.createDirIfNeeded(FabricLoader.getInstance().getGameDir().resolve("Zenith").resolve("figura-runtime"));
   }

   public static Path getCacheDirectory() {
      return IOUtils.getOrCreateDir(getFiguraDirectory(), "cache");
   }

   public static void debug(String message, Object... args) {
      LOGGER.debug(message, args);
   }

   public static void pushProfiler(String ignored) {
   }

   public static void popPushProfiler(String ignored) {
   }

   public static void popProfiler() {
   }

   public static void popProfiler(int ignored) {
   }

   public static <T> T popReturnProfiler(T value) {
      return value;
   }
}
