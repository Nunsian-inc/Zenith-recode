package org.zenith.base.bot.net;

import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.client.multiplayer.KnownPacksManager;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess.Frozen;
import net.minecraft.core.RegistrySynchronization.PackedRegistryEntry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.TickablePacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.common.ClientboundClearDialogPacket;
import net.minecraft.network.protocol.common.ClientboundShowDialogPacket;
import net.minecraft.network.protocol.common.ClientboundUpdateTagsPacket;
import net.minecraft.network.protocol.configuration.ClientConfigurationPacketListener;
import net.minecraft.network.protocol.configuration.ClientboundCodeOfConductPacket;
import net.minecraft.network.protocol.configuration.ClientboundFinishConfigurationPacket;
import net.minecraft.network.protocol.configuration.ClientboundRegistryDataPacket;
import net.minecraft.network.protocol.configuration.ClientboundResetChatPacket;
import net.minecraft.network.protocol.configuration.ClientboundSelectKnownPacks;
import net.minecraft.network.protocol.configuration.ClientboundUpdateEnabledFeaturesPacket;
import net.minecraft.network.protocol.configuration.ServerboundFinishConfigurationPacket;
import net.minecraft.network.protocol.configuration.ServerboundSelectKnownPacks;
import net.minecraft.network.protocol.game.GameProtocols;
import net.minecraft.network.protocol.game.GameProtocols.Context;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.repository.BuiltInPackSource;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraft.world.entity.animal.wolf.WolfVariants;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.zenith.base.bot.client.BotClient;

public final class BotConfigHandler extends BotCommonHandler implements TickablePacketListener, ClientConfigurationPacketListener {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final GameProfile profile;
   private final Frozen receivedRegistries;
   private final BotClientRegistries clientRegistries = new BotClientRegistries();
   private final Map<ResourceKey<? extends Registry<?>>, Integer> receivedRegistryEntries = new HashMap<>();
   private FeatureFlagSet enabledFeatures;
   @Nullable
   private KnownPacksManager dataPackManager;

   public BotConfigHandler(BotClient var1, BotConnection var2, BotConnectionState var3) {
      super(var1, var2, var3);
      this.profile = var3.profile();
      this.receivedRegistries = var3.receivedRegistries();
      this.enabledFeatures = var3.enabledFeatures();
   }

   @Override
   public void handleRegistryData(ClientboundRegistryDataPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      this.clientRegistries.putDynamicRegistry(packet.registry(), packet.entries());
      this.receivedRegistryEntries.merge(packet.registry(), packet.entries().size(), Integer::sum);
   }

   @Override
   public void handleUpdateTags(ClientboundUpdateTagsPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      this.clientRegistries.putTags(packet.tags());
   }

   @Override
   public void handleEnabledFeatures(ClientboundUpdateEnabledFeaturesPacket packet) {
      this.enabledFeatures = FeatureFlags.REGISTRY.fromNames(packet.features());
   }

   @Override
   public void handleSelectKnownPacks(ClientboundSelectKnownPacks packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      if (this.dataPackManager == null) {
         this.dataPackManager = new KnownPacksManager();
      }

      this.sendPacket(new ServerboundSelectKnownPacks(this.dataPackManager.trySelectingPacks(packet.knownPacks())));
   }

   @Override
   public void handleResetChat(ClientboundResetChatPacket packet) {
   }

   @Override
   public void handleConfigurationFinished(ClientboundFinishConfigurationPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      if (this.connection.isOpen() && this.connection.getPacketListener() == this) {
         this.installMissingWolfVariantFallback();

         Frozen var2;
         try {
            var2 = this.openClientDataPack(var1 -> this.clientRegistries.createRegistryManager(var1, this.receivedRegistries));
         } catch (RuntimeException var5) {
            LOGGER.error("Bot {} registry loading failed", this.client.name(), var5);
            this.connection.disconnect(Component.literal("registry loading failed"));
            return;
         }

         BotConnectionState var3 = new BotConnectionState(
            this.profile, var2, this.enabledFeatures, this.brand, this.serverCookies, this.customReportDetails, this.serverLinks
         );
         BotPlayHandler var4 = new BotPlayHandler(this.client, this.connection, var3);
         this.client.setPlayHandler(var4);
         this.connection.transitionInbound(GameProtocols.CLIENTBOUND_TEMPLATE.bind(RegistryFriendlyByteBuf.decorator(var2)), var4);
         this.connection.send(ServerboundFinishConfigurationPacket.INSTANCE);
         this.connection.transitionOutbound(GameProtocols.SERVERBOUND_TEMPLATE.bind(RegistryFriendlyByteBuf.decorator(var2), new Context() {
            @Override
            public boolean hasInfiniteMaterials() {
               return false;
            }

            @Override
            public boolean canUseCommandBlocks() {
               return false;
            }
         }));
      }
   }

   private <T> T openClientDataPack(Function<ResourceProvider, T> var1) {
      if (this.dataPackManager == null) {
         return (T)var1.apply(ResourceProvider.EMPTY);
      } else {
         Object var3;
         try (CloseableResourceManager var2 = this.dataPackManager.createResourceManager()) {
            var3 = var1.apply(var2);
         }

         return (T)var3;
      }
   }

   private void installMissingWolfVariantFallback() {
      if (this.receivedRegistryEntries.getOrDefault(Registries.WOLF_VARIANT, 0) <= 0) {
         var var1 = List.of(
            WolfVariants.PALE,
            WolfVariants.SPOTTED,
            WolfVariants.SNOWY,
            WolfVariants.BLACK,
            WolfVariants.ASHEN,
            WolfVariants.RUSTY,
            WolfVariants.WOODS,
            WolfVariants.CHESTNUT,
            WolfVariants.STRIPED
         );
         var var2 = var1.stream().map(var0 -> new PackedRegistryEntry(var0.identifier(), Optional.empty())).toList();
         this.clientRegistries.putDynamicRegistry(Registries.WOLF_VARIANT, var2);
         if (this.dataPackManager == null) {
            this.dataPackManager = new KnownPacksManager();
            this.dataPackManager.trySelectingPacks(List.of(BuiltInPackSource.CORE_PACK_INFO));
         }

         LOGGER.debug("Bot {} installed wolf_variant registry fallback ({} entries)", this.client.name(), var2.size());
      }
   }

   @Override
   public void tick() {
   }

   @Override
   public void handleShowDialog(ClientboundShowDialogPacket packet) {
   }

   @Override
   public void handleClearDialog(ClientboundClearDialogPacket packet) {
   }

   @Override
   public void handleCodeOfConduct(ClientboundCodeOfConductPacket packet) {
   }
}
