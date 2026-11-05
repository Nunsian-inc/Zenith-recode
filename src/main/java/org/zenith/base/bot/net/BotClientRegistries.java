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

   public void putDynamicRegistry(ResourceKey<? extends Registry<?>> var1, List<PackedRegistryEntry> var2) {
      this.dynamicRegistries.computeIfAbsent(var1, var0 -> new ArrayList<>()).addAll(var2);
   }

   public void putTags(Map<ResourceKey<? extends Registry<?>>, NetworkPayload> var1) {
      this.tags.putAll(var1);
   }

   public Frozen createRegistryManager(ResourceProvider var1, Frozen var2) {
      if (this.dynamicRegistries.isEmpty()) {
         return var2;
      } else {
         LayeredRegistryAccess var3 = ClientRegistryLayer.createRegistryAccess();
         Frozen var4 = var3.getAccessForLoading(ClientRegistryLayer.REMOTE);
         HashMap var5 = new HashMap();
         this.dynamicRegistries.forEach((var1x, var2x) -> var5.put(var1x, new NetworkedRegistryData((List<PackedRegistryEntry>)var2x, NetworkPayload.EMPTY)));
         ArrayList var6 = new ArrayList();
         this.tags.forEach((var3x, var4x) -> {
            if (!var4x.isEmpty()) {
               if (RegistrySynchronization.isNetworkable((ResourceKey<? extends Registry<?>>)var3x)) {
                  var5.compute(var3x, (var1xx, var2xx) -> new NetworkedRegistryData(var2xx != null ? ((NetworkedRegistryData)var2xx).elements() : List.of(), var4x));
               } else {
                  var6.add(startTagReload(var4, (ResourceKey<? extends Registry<?>>)var3x, var4x));
               }
            }
         });
         var var7 = TagLoader.buildUpdatedLookups(var4, var6);
         Frozen var8 = (Frozen)RegistryDataLoader.load(var5, var1, var7, RegistryDataLoader.SYNCHRONIZED_REGISTRIES, net.minecraft.util.Util.backgroundExecutor()).join();
         return var3.replaceFrom(ClientRegistryLayer.REMOTE, var8).compositeAccess().freeze();
      }
   }

   private static <T> PendingTags<T> startTagReload(Frozen var0, ResourceKey<? extends Registry<? extends T>> var1, NetworkPayload var2) {
      Registry var3 = var0.lookupOrThrow(var1);
      return var3.prepareTagReload(var2.resolve(var3));
   }
}
