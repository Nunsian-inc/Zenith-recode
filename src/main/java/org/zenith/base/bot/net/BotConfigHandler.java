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

   public BotConfigHandler(BotClient client, BotConnection connection, BotConnectionState botConnectionState) {
      super(client, connection, botConnectionState);
      this.profile = botConnectionState.profile();
      this.receivedRegistries = botConnectionState.receivedRegistries();
      this.enabledFeatures = botConnectionState.enabledFeatures();
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

         Frozen openClientDataPack;
         try {
            openClientDataPack = this.openClientDataPack(knownDataSource -> this.clientRegistries.createRegistryManager(knownDataSource, this.receivedRegistries));
         } catch (RuntimeException e) {
            LOGGER.error("Bot {} registry loading failed", this.client.name(), e);
            this.connection.disconnect(Component.literal("registry loading failed"));
            return;
         }

         BotConnectionState botConnectionState = new BotConnectionState(
            this.profile, openClientDataPack, this.enabledFeatures, this.brand, this.serverCookies, this.customReportDetails, this.serverLinks
         );
         BotPlayHandler botPlayHandler = new BotPlayHandler(this.client, this.connection, botConnectionState);
         this.client.setPlayHandler(botPlayHandler);
         this.connection.transitionInbound(GameProtocols.CLIENTBOUND_TEMPLATE.bind(RegistryFriendlyByteBuf.decorator(openClientDataPack)), botPlayHandler);
         this.connection.send(ServerboundFinishConfigurationPacket.INSTANCE);
         this.connection.transitionOutbound(GameProtocols.SERVERBOUND_TEMPLATE.bind(RegistryFriendlyByteBuf.decorator(openClientDataPack), new Context() {
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

   private <T> T openClientDataPack(Function<ResourceProvider, T> function) {
      if (this.dataPackManager == null) {
         return (T)function.apply(ResourceProvider.EMPTY);
      } else {
         Object obj;
         try (CloseableResourceManager resourceManager = this.dataPackManager.createResourceManager()) {
            obj = function.apply(resourceManager);
         }

         return (T)obj;
      }
   }

   private void installMissingWolfVariantFallback() {
      if (this.receivedRegistryEntries.getOrDefault(Registries.WOLF_VARIANT, 0) <= 0) {
         var resourceKeys = List.of(
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
         var entries = resourceKeys.stream().map(resourceKey -> new PackedRegistryEntry(resourceKey.identifier(), Optional.empty())).toList();
         this.clientRegistries.putDynamicRegistry(Registries.WOLF_VARIANT, entries);
         if (this.dataPackManager == null) {
            this.dataPackManager = new KnownPacksManager();
            this.dataPackManager.trySelectingPacks(List.of(BuiltInPackSource.CORE_PACK_INFO));
         }

         LOGGER.debug("Bot {} installed wolf_variant registry fallback ({} entries)", this.client.name(), entries.size());
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
