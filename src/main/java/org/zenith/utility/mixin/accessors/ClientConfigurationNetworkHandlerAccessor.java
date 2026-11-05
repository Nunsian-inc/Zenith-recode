package org.zenith.utility.mixin.accessors;

import com.mojang.authlib.GameProfile;
import java.util.function.Function;
import net.minecraft.client.multiplayer.ClientConfigurationPacketListenerImpl;
import net.minecraft.client.multiplayer.KnownPacksManager;
import net.minecraft.client.multiplayer.RegistryDataCollector;
import net.minecraft.core.RegistryAccess.Frozen;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraft.world.flag.FeatureFlagSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({ClientConfigurationPacketListenerImpl.class})
public interface ClientConfigurationNetworkHandlerAccessor {
   @Accessor("localGameProfile")
   GameProfile zenith$getProfile();

   @Accessor("enabledFeatures")
   FeatureFlagSet zenith$getEnabledFeatures();

   @Accessor("registryDataCollector")
   RegistryDataCollector zenith$getClientRegistries();

   @Accessor("receivedRegistries")
   Frozen zenith$getRegistryManager();

   @Accessor("knownPacks")
   KnownPacksManager zenith$getDataPackManager();

   @Accessor("knownPacks")
   void zenith$setDataPackManager(KnownPacksManager knownPacksManager);

   @Invoker("runWithResources")
   <T> T zenith$openClientDataPack(Function<ResourceProvider, T> function);
}
