package org.zenith.base.bot.net;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.multiplayer.ClientRegistryLayer;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.Registry;
import net.minecraft.core.Registry.PendingTags;
import net.minecraft.core.RegistryAccess.Frozen;
import net.minecraft.core.RegistrySynchronization;
import net.minecraft.core.RegistrySynchronization.PackedRegistryEntry;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.RegistryDataLoader.NetworkedRegistryData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraft.tags.TagLoader;
import net.minecraft.tags.TagNetworkSerialization.NetworkPayload;

public final class BotClientRegistries {
   private final Map<ResourceKey<? extends Registry<?>>, List<PackedRegistryEntry>> dynamicRegistries = new HashMap<>();
   private final Map<ResourceKey<? extends Registry<?>>, NetworkPayload> tags = new HashMap<>();

   public void putDynamicRegistry(ResourceKey<? extends Registry<?>> registry, List<PackedRegistryEntry> entries) {
      this.dynamicRegistries.computeIfAbsent(registry, dynamicRegistry -> new ArrayList<>()).addAll(entries);
   }

   public void putTags(Map<ResourceKey<? extends Registry<?>>, NetworkPayload> tags) {
      this.tags.putAll(tags);
   }

   public Frozen createRegistryManager(ResourceProvider knownDataSource, Frozen receivedRegistries) {
      if (this.dynamicRegistries.isEmpty()) {
         return receivedRegistries;
      } else {
         LayeredRegistryAccess registryAccess = ClientRegistryLayer.createRegistryAccess();
         Frozen accessForLoading = registryAccess.getAccessForLoading(ClientRegistryLayer.REMOTE);
         HashMap entries = new HashMap();
         this.dynamicRegistries.forEach((var1x, var2x) -> entries.put(var1x, new NetworkedRegistryData((List<PackedRegistryEntry>)var2x, NetworkPayload.EMPTY)));
         ArrayList tags = new ArrayList();
         this.tags.forEach((var3x, var4x) -> {
            if (!var4x.isEmpty()) {
               if (RegistrySynchronization.isNetworkable((ResourceKey<? extends Registry<?>>)var3x)) {
                  entries.compute(var3x, (var1xx, var2xx) -> new NetworkedRegistryData(var2xx != null ? ((NetworkedRegistryData)var2xx).elements() : List.of(), var4x));
               } else {
                  tags.add(startTagReload(accessForLoading, (ResourceKey<? extends Registry<?>>)var3x, var4x));
               }
            }
         });
         var updatedLookups = TagLoader.buildUpdatedLookups(accessForLoading, tags);
         Frozen layers = (Frozen)RegistryDataLoader.load(entries, knownDataSource, updatedLookups, RegistryDataLoader.SYNCHRONIZED_REGISTRIES, net.minecraft.util.Util.backgroundExecutor()).join();
         return registryAccess.replaceFrom(ClientRegistryLayer.REMOTE, layers).compositeAccess().freeze();
      }
   }

   private static <T> PendingTags<T> startTagReload(Frozen accessForLoading, ResourceKey<? extends Registry<? extends T>> name, NetworkPayload networkPayload) {
      Registry orThrow = accessForLoading.lookupOrThrow(name);
      return orThrow.prepareTagReload(networkPayload.resolve(orThrow));
   }
}
