package dev.porno.playerAnim.fabric.client;

import dev.porno.playerAnim.minecraftApi.PlayerAnimationRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Internal
public class FabricClientInitializer implements ClientModInitializer {
   public static final Logger LOGGER = LoggerFactory.getLogger("player-animator");

   public void onInitializeClient() {
      ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
         public Identifier getFabricId() {
            return PlayerAnimationRegistry.KEY;
         }

         public void onResourceManagerReload(@NotNull ResourceManager manager) {
            PlayerAnimationRegistry.resourceLoaderCallback(manager);
         }
      });
   }
}
