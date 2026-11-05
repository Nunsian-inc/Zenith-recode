package dev.porno.playerAnim.impl.fabric;

import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public class HelperImpl {
   public static boolean isBendyLibPresent() {
      return FabricLoader.getInstance().isModLoaded("bendy-lib");
   }
}
