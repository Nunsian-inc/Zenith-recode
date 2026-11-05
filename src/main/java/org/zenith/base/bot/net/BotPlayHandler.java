package org.zenith.base.bot.net;

import com.google.common.hash.HashCode;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.logging.LogUtils;
import java.time.Instant;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.multiplayer.ChunkBatchSizeCalculator;
import net.minecraft.client.multiplayer.ClientLevel.ClientLevelData;
import net.minecraft.client.multiplayer.ClientRecipeContainer;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.Registry.PendingTags;
import net.minecraft.core.RegistryAccess.Frozen;
import net.minecraft.core.RegistrySynchronization;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.HashedPatchMap.HashGenerator;
import net.minecraft.network.TickablePacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.LastSeenMessages.Update;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.common.ClientboundClearDialogPacket;
import net.minecraft.network.protocol.common.ClientboundShowDialogPacket;
import net.minecraft.network.protocol.common.ClientboundUpdateTagsPacket;
import net.minecraft.network.protocol.configuration.ConfigurationProtocols;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.protocol.game.ClientboundChunksBiomesPacket.ChunkBiomeData;
import net.minecraft.network.protocol.game.ClientboundCommandsPacket.NodeBuilder;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket.Type;
import net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket.AttributeSnapshot;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket.Action;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket.PosRot;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket.Rot;
import net.minecraft.network.protocol.ping.ClientboundPongResponsePacket;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Crypt.SaltSupplier;
import net.minecraft.util.HashOps;
import net.minecraft.util.Mth;
import net.minecraft.util.ProblemReporter.ScopedCollector;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.TickRateManager;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.hurtingprojectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.entity.vehicle.minecart.NewMinecartBehavior;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeAccess;
import net.minecraft.world.item.crafting.SelectableRecipe.SingleInputSet;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.ScoreAccess;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Team.CollisionRule;
import net.minecraft.world.scores.Team.Visibility;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.zenith.base.bot.client.BotClient;
import org.zenith.base.bot.client.BotPhase;
import org.zenith.base.bot.world.BotInteractionManager;
import org.zenith.base.bot.world.BotPlayer;
import org.zenith.base.bot.world.BotRemotePlayer;
import org.zenith.base.bot.world.BotWorld;

public final class BotPlayHandler extends BotCommonHandler implements ClientGamePacketListener, TickablePacketListener {
   private HashGenerator zenith$componentHasher;
   private static final Logger LOGGER = LogUtils.getLogger();
   private final GameProfile profile;
   private final Frozen registries;
   private final FeatureFlagSet enabledFeatures;
   private final ChunkBatchSizeCalculator chunkBatchSizeCalculator = new ChunkBatchSizeCalculator();
   private final RandomSource random = RandomSource.createThreadSafe();
   private final Map<UUID, PlayerInfo> playerListEntries = new ConcurrentHashMap<>();
   private final Scoreboard scoreboard = new Scoreboard();
   private final net.minecraft.client.ClientClockManager clockManager = new net.minecraft.client.ClientClockManager();
   private final BotCommandSource commandSource = new BotCommandSource(this);
   private volatile CommandDispatcher<SharedSuggestionProvider> commandDispatcher = new CommandDispatcher();
   private volatile Set<ResourceKey<Level>> worldKeys = Set.of();
   private ClientRecipeContainer recipeManager = new ClientRecipeContainer(Map.of(), SingleInputSet.empty());
   @Nullable
   private volatile BotWorld world;
   @Nullable
   private volatile BotPlayer player;
   @Nullable
   private volatile BotInteractionManager interactionManager;
   @Nullable
   private ClientLevelData worldProperties;
   @Nullable
   private volatile Component currentScreenTitle;
   private volatile int currentScreenSyncId = -1;
   private int chunkLoadDistance = 3;
   private int simulationDistance = 3;
   private boolean secureChatEnforced;
   private OptionalInt removedPlayerVehicleId = OptionalInt.empty();
   private boolean playerLoadedSent;
   private int loadWaitTicks;
   private boolean initialChunksComing;

   public HashGenerator getComponentHasher() {
      if (this.zenith$componentHasher == null) {
         RegistryOps serializationContext = this.registries.createSerializationContext(HashOps.CRC32C_INSTANCE);
         this.zenith$componentHasher = var1x -> ((HashCode)((com.mojang.serialization.DataResult<HashCode>)(Object)var1x.encodeValue(serializationContext))
               .<IllegalArgumentException>getOrThrow(var1xx -> new IllegalArgumentException("Failed to hash " + var1x + ": " + var1xx)))
            .asInt();
      }

      return this.zenith$componentHasher;
   }

   public boolean isPlayerLoaded() {
      return this.playerLoadedSent;
   }

   public BotPlayHandler(BotClient client, BotConnection connection, BotConnectionState botConnectionState) {
      super(client, connection, botConnectionState);
      this.profile = botConnectionState.profile();
      this.registries = botConnectionState.receivedRegistries();
      this.enabledFeatures = botConnectionState.enabledFeatures();
   }

   public GameProfile getProfile() {
      return this.profile;
   }

   public Frozen getRegistryManager() {
      return this.registries;
   }

   public FeatureFlagSet getEnabledFeatures() {
      return this.enabledFeatures;
   }

   public BotClient getClient() {
      return this.client;
   }

   @Nullable
   public BotWorld getWorld() {
      return this.world;
   }

   @Nullable
   public BotPlayer getPlayer() {
      return this.player;
   }

   public BotInteractionManager getInteractionManager() {
      BotInteractionManager interactionManager = this.interactionManager;
      if (interactionManager == null) {
         throw new IllegalStateException("Bot interaction manager is not initialized (no GameJoin yet)");
      } else {
         return interactionManager;
      }
   }

   @Nullable
   public BotInteractionManager getInteractionManagerOrNull() {
      return this.interactionManager;
   }

   public Scoreboard getScoreboard() {
      return this.scoreboard;
   }

   @Nullable
   public Component getCurrentScreenTitle() {
      BotPlayer player = this.player;
      if (player != null && player.containerMenu != null) {
         return player.containerMenu.containerId == this.currentScreenSyncId ? this.currentScreenTitle : null;
      } else {
         return null;
      }
   }

   public boolean hasOpenScreen() {
      return this.getCurrentScreenTitle() != null;
   }

   public net.minecraft.client.ClientClockManager clockManager() {
      return this.clockManager;
   }

   public RecipeAccess getRecipeManager() {
      return this.recipeManager;
   }

   @Nullable
   public PlayerInfo getPlayerListEntry(UUID uuid) {
      return this.playerListEntries.get(uuid);
   }

   @Nullable
   public PlayerInfo getPlayerListEntry(String anObject) {
      for (PlayerInfo playerInfo : this.playerListEntries.values()) {
         if (playerInfo.getProfile().name().equals(anObject)) {
            return playerInfo;
         }
      }

      return null;
   }

   public Iterable<PlayerInfo> getPlayerList() {
      return this.playerListEntries.values();
   }

   public CommandDispatcher<SharedSuggestionProvider> getCommandDispatcher() {
      return this.commandDispatcher;
   }

   public BotCommandSource getCommandSource() {
      return this.commandSource;
   }

   public Set<ResourceKey<Level>> getWorldKeys() {
      return this.worldKeys;
   }

   @Override
   public void tick() {
      if (this.client.getPhase() == BotPhase.PLAY && this.connection.getPacketListener() == this) {
         BotWorld world = this.world;
         BotPlayer player = this.player;
         BotInteractionManager interactionManager = this.interactionManager;
         if (world != null && player != null && interactionManager != null) {
            world.runQueuedChunkUpdates();
            world.getChunkSource().getLightEngine().runLightUpdates();
            interactionManager.tick();
            this.client.onBotUpdate(world, player);
            world.tick(() -> true);
            world.tickEntities();
            this.tickPlayerLoaded(world, player);
         }

         this.sendPacket(ServerboundClientTickEndPacket.INSTANCE);
      }
   }

   private void tickPlayerLoaded(BotWorld world, BotPlayer player) {
      if (!this.playerLoadedSent) {
         boolean flag = world.getChunkSource().getChunk(player.chunkPosition().x(), player.chunkPosition().z(), null, false) != null;
         if (flag || ++this.loadWaitTicks >= 60) {
            this.playerLoadedSent = true;
            this.loadWaitTicks = 0;
            this.sendPacket(new ServerboundPlayerLoadedPacket());
         }
      }
   }

   public void sendChatMessage(String text) {
      this.sendPacket(new ServerboundChatPacket(text, Instant.now(), SaltSupplier.getLong(), null, new Update(0, new BitSet(), (byte)0)));
   }

   public void sendCommand(String command) {
      this.sendPacket(new ServerboundChatCommandPacket(command));
   }

   @Override
   public void handleLogin(ClientboundLoginPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      CommonPlayerSpawnInfo commonPlayerSpawnInfo = packet.commonPlayerSpawnInfo();
      this.worldKeys = new LinkedHashSet<>(packet.levels());
      this.chunkLoadDistance = packet.chunkRadius();
      this.simulationDistance = packet.simulationDistance();
      this.secureChatEnforced = packet.enforcesSecureChat();
      BotInteractionManager botInteractionManager = new BotInteractionManager(this);
      this.interactionManager = botInteractionManager;
      ClientLevelData clientLevelData = new ClientLevelData(Difficulty.NORMAL, packet.hardcore(), commonPlayerSpawnInfo.isFlat());
      this.worldProperties = clientLevelData;
      BotWorld botWorld = new BotWorld(
         this, clientLevelData, commonPlayerSpawnInfo.dimension(), commonPlayerSpawnInfo.dimensionType(), this.chunkLoadDistance, this.simulationDistance, commonPlayerSpawnInfo.isDebug(), commonPlayerSpawnInfo.seed(), commonPlayerSpawnInfo.seaLevel()
      );
      this.world = botWorld;
      BotPlayer botPlayer = new BotPlayer(botWorld, this, false, false);
      this.player = botPlayer;
      botPlayer.init();
      botPlayer.setId(packet.playerId());
      botPlayer.setYRot(-180.0F);
      botWorld.setBotPlayer(botPlayer);
      botWorld.addEntity(botPlayer);
      botInteractionManager.copyAbilities(botPlayer);
      botPlayer.setReducedDebugInfo(packet.reducedDebugInfo());
      botPlayer.setLastDeathLocation(commonPlayerSpawnInfo.lastDeathLocation());
      botPlayer.setPortalCooldown(commonPlayerSpawnInfo.portalCooldown());
      botInteractionManager.setGameModes(commonPlayerSpawnInfo.gameType(), commonPlayerSpawnInfo.previousGameType().orElse(null));
      this.playerLoadedSent = false;
      this.loadWaitTicks = 0;
      this.initialChunksComing = false;
      LOGGER.debug("Bot {} joined dimension={} entityId={}", new Object[]{this.client.name(), commonPlayerSpawnInfo.dimension().identifier(), packet.playerId()});
      this.client.onGameJoin(botWorld, botPlayer);
   }

   @Override
   public void handleRespawn(ClientboundRespawnPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotPlayer player = this.player;
      BotWorld world = this.world;
      BotInteractionManager interactionManager = this.interactionManager;
      if (player != null && world != null && interactionManager != null && this.worldProperties != null) {
         CommonPlayerSpawnInfo commonPlayerSpawnInfo = packet.commonPlayerSpawnInfo();
         ResourceKey dimension = commonPlayerSpawnInfo.dimension();
         Holder dimensionType = commonPlayerSpawnInfo.dimensionType();
         boolean flag = dimension != player.level().dimension();
         BotWorld botWorld = world;
         if (flag) {
            var mapStates = world.getMapStates();
            ClientLevelData clientLevelData = new ClientLevelData(this.worldProperties.getDifficulty(), this.worldProperties.isHardcore(), commonPlayerSpawnInfo.isFlat());
            this.worldProperties = clientLevelData;
            botWorld = new BotWorld(this, clientLevelData, dimension, dimensionType, this.chunkLoadDistance, this.simulationDistance, commonPlayerSpawnInfo.isDebug(), commonPlayerSpawnInfo.seed(), commonPlayerSpawnInfo.seaLevel());
            botWorld.putMapStates(mapStates);
            this.world = botWorld;
         }

         if (player.hasContainerOpen()) {
            player.closeContainer();
         }

         boolean shouldKeep = packet.shouldKeep((byte)2);
         BotPlayer botPlayer = new BotPlayer(botWorld, this, shouldKeep && player.isShiftKeyDown(), shouldKeep && player.isSprinting());
         botPlayer.setId(player.getId());
         this.player = botPlayer;
         if (shouldKeep) {
            var nonDefaultValues = player.getEntityData().getNonDefaultValues();
            if (nonDefaultValues != null) {
               botPlayer.getEntityData().assignValues(nonDefaultValues);
            }

            botPlayer.setDeltaMovement(player.getDeltaMovement());
            botPlayer.setYRot(player.getYRot());
            botPlayer.setXRot(player.getXRot());
         } else {
            botPlayer.init();
            botPlayer.setYRot(-180.0F);
         }

         if (packet.shouldKeep((byte)1)) {
            botPlayer.getAttributes().assignAllValues(player.getAttributes());
         } else {
            botPlayer.getAttributes().assignBaseValues(player.getAttributes());
         }

         botWorld.setBotPlayer(botPlayer);
         botWorld.addEntity(botPlayer);
         interactionManager.copyAbilities(botPlayer);
         botPlayer.setReducedDebugInfo(player.isReducedDebugInfo());
         botPlayer.setLastDeathLocation(commonPlayerSpawnInfo.lastDeathLocation());
         botPlayer.setPortalCooldown(commonPlayerSpawnInfo.portalCooldown());
         interactionManager.setGameModes(commonPlayerSpawnInfo.gameType(), commonPlayerSpawnInfo.previousGameType().orElse(null));
         this.playerLoadedSent = false;
         this.loadWaitTicks = 0;
         if (flag) {
            this.client.onGameJoin(botWorld, botPlayer);
         } else {
            this.client.onRespawn(botWorld, botPlayer);
         }
      }
   }

   @Override
   public void handleConfigurationStart(ClientboundStartConfigurationPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      this.world = null;
      this.player = null;
      this.interactionManager = null;
      this.playerLoadedSent = false;
      this.loadWaitTicks = 0;
      this.client.setPhase(BotPhase.CONFIGURATION);
      this.client.onWorldUnload();
      BotConnectionState botConnectionState = new BotConnectionState(
         this.profile, this.registries, this.enabledFeatures, this.brand, this.serverCookies, this.customReportDetails, this.serverLinks
      );
      BotConfigHandler botConfigHandler = new BotConfigHandler(this.client, this.connection, botConnectionState);
      this.connection.transitionInbound(ConfigurationProtocols.CLIENTBOUND, botConfigHandler);
      this.connection.send(ServerboundConfigurationAcknowledgedPacket.INSTANCE);
      this.connection.transitionOutbound(ConfigurationProtocols.SERVERBOUND);
   }

   @Override
   public void handleBundlePacket(ClientboundBundlePacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());

      for (Packet subPacket : packet.subPackets()) {
         subPacket.handle(this);
      }
   }

   @Override
   public void handleMovePlayer(ClientboundPlayerPositionPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotPlayer player = this.player;
      if (player == null) {
         Vec3 position = packet.change().position();
         this.sendPacket(new ServerboundAcceptTeleportationPacket(packet.id(), position.x, position.y, position.z, packet.change().yRot(), packet.change().xRot()));
      } else {
         if (!player.isPassenger()) {
            setPosition(packet.change(), packet.relatives(), player, false);
         }

         this.sendPacket(new ServerboundAcceptTeleportationPacket(packet.id(), player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot()));
         this.sendPacket(new PosRot(player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot(), false, false));
      }
   }

   private static boolean setPosition(PositionMoveRotation change, Set<Relative> relatives, Entity player, boolean flag) {
      PositionMoveRotation source = PositionMoveRotation.of(player);
      PositionMoveRotation absolute = PositionMoveRotation.calculateAbsolute(source, change, relatives);
      boolean flag2 = source.position().distanceToSqr(absolute.position()) > 4096.0;
      if (flag && !flag2) {
         player.moveOrInterpolateTo(absolute.position(), absolute.yRot(), absolute.xRot());
         player.setDeltaMovement(absolute.deltaMovement());
         return true;
      } else {
         player.setPos(absolute.position());
         player.setDeltaMovement(absolute.deltaMovement());
         player.setYRot(absolute.yRot());
         player.setXRot(absolute.xRot());
         PositionMoveRotation positionMoveRotation = new PositionMoveRotation(player.oldPosition(), Vec3.ZERO, player.yRotO, player.xRotO);
         PositionMoveRotation absolute2 = PositionMoveRotation.calculateAbsolute(positionMoveRotation, change, relatives);
         player.setOldPosAndRot(absolute2.position(), absolute2.yRot(), absolute2.xRot());
         return false;
      }
   }

   @Override
   public void handleRotatePlayer(ClientboundPlayerRotationPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotPlayer player = this.player;
      if (player != null) {
         var rotation = Relative.rotation(packet.relativeY(), packet.relativeX());
         PositionMoveRotation source = PositionMoveRotation.of(player);
         PositionMoveRotation absolute = PositionMoveRotation.calculateAbsolute(source, source.withRotation(packet.yRot(), packet.xRot()), rotation);
         player.setYRot(absolute.yRot());
         player.setXRot(absolute.xRot());
         player.setOldRot();
         this.sendPacket(new Rot(player.getYRot(), player.getXRot(), false, false));
      } else {
         this.sendPacket(new Rot(packet.yRot(), packet.xRot(), false, false));
      }
   }

   @Override
   public void handleMoveVehicle(ClientboundMoveVehiclePacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotPlayer player = this.player;
      if (player != null) {
         Entity rootVehicle = player.getRootVehicle();
         if (rootVehicle != player && rootVehicle.isLocalInstanceAuthoritative()) {
            net.minecraft.core.PositionAndRotation movingTo = packet.movingTo();
            Vec3 position = movingTo.position();
            Vec3 clientPosition = rootVehicle.getClientPosition();
            if (position.distanceTo(clientPosition) > 1.0E-5F) {
               if (rootVehicle.isInterpolating()) {
                  rootVehicle.getInterpolation().cancel();
               }

               rootVehicle.absSnapTo(position.x(), position.y(), position.z(), movingTo.yRot(), movingTo.xRot());
            }

            this.connection.send(ServerboundMoveVehiclePacket.fromEntity(rootVehicle));
         }
      }
   }

   @Override
   public void handleChunkBatchStart(ClientboundChunkBatchStartPacket packet) {
      this.chunkBatchSizeCalculator.onBatchStart();
   }

   @Override
   public void handleChunkBatchFinished(ClientboundChunkBatchFinishedPacket packet) {
      this.chunkBatchSizeCalculator.onBatchFinished(packet.batchSize());
      this.sendPacket(new ServerboundChunkBatchReceivedPacket(this.chunkBatchSizeCalculator.getDesiredChunksPerTick()));
   }

   @Override
   public void handleLevelChunkWithLight(ClientboundLevelChunkWithLightPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld world = this.world;
      if (world != null) {
         int x = packet.x();
         int z = packet.z();
         ClientboundLevelChunkPacketData chunkData = packet.chunkData();
         world.getChunkSource().loadChunkFromPacket(x, z, chunkData);
         ClientboundLightUpdatePacketData lightData = packet.lightData();
         world.enqueueChunkUpdate(() -> {
            this.readLightData(x, z, lightData);
            LevelChunk var5x = world.getChunkSource().getChunk(x, z, false);
            if (var5x != null) {
               this.markChunkSectionsReady(world, var5x);
            }
         });
      }
   }

   private void markChunkSectionsReady(BotWorld world, LevelChunk levelChunk) {
      LevelLightEngine lightEngine = world.getChunkSource().getLightEngine();
      LevelChunkSection[] sections = levelChunk.getSections();
      ChunkPos pos = levelChunk.getPos();

      for (int i = 0; i < sections.length; i++) {
         LevelChunkSection levelChunkSection = sections[i];
         int sectionYFromSectionIndex = world.getSectionYFromSectionIndex(i);
         lightEngine.updateSectionStatus(SectionPos.of(pos, sectionYFromSectionIndex), levelChunkSection.hasOnlyAir());
      }
   }

   @Override
   public void handleChunksBiomes(ClientboundChunksBiomesPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld world = this.world;
      if (world != null) {
         for (ChunkBiomeData chunkBiomeData : packet.chunkBiomeData()) {
            world.getChunkSource().onChunkBiomeData(chunkBiomeData.pos().x(), chunkBiomeData.pos().z(), chunkBiomeData.getReadBuffer());
         }

         for (ChunkBiomeData chunkBiomeData : packet.chunkBiomeData()) {
            world.resetChunkColor(new ChunkPos(chunkBiomeData.pos().x(), chunkBiomeData.pos().z()));
         }
      }
   }

   @Override
   public void handleForgetLevelChunk(ClientboundForgetLevelChunkPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld world = this.world;
      if (world != null) {
         world.getChunkSource().unload(packet.pos());
         ChunkPos pos = packet.pos();
         world.enqueueChunkUpdate(() -> {
            LevelLightEngine var2x = world.getLightEngine();
            var2x.setLightEnabled(pos, false);

            for (int var3x = var2x.getMinLightSection(); var3x < var2x.getMaxLightSection(); var3x++) {
               SectionPos sectionPos = SectionPos.of(pos, var3x);
               var2x.queueSectionData(LightLayer.BLOCK, sectionPos, null);
               var2x.queueSectionData(LightLayer.SKY, sectionPos, null);
            }

            for (int i = world.getMinSectionY(); i <= world.getMaxSectionY(); i++) {
               var2x.updateSectionStatus(SectionPos.of(pos, i), true);
            }
         });
      }
   }

   @Override
   public void handleLightUpdatePacket(ClientboundLightUpdatePacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld world = this.world;
      if (world != null) {
         int x = packet.x();
         int z = packet.z();
         ClientboundLightUpdatePacketData lightData = packet.lightData();
         world.enqueueChunkUpdate(() -> this.readLightData(x, z, lightData));
      }
   }

   private void readLightData(int x, int z, ClientboundLightUpdatePacketData lightData) {
      BotWorld world = this.world;
      if (world != null) {
         LevelLightEngine lightEngine = world.getChunkSource().getLightEngine();
         BitSet skyYMask = lightData.skyYMask();
         BitSet emptySkyYMask = lightData.emptySkyYMask();
         var iterator = lightData.skyUpdates().iterator();
         this.updateLighting(x, z, lightEngine, LightLayer.SKY, skyYMask, emptySkyYMask, iterator);
         BitSet blockYMask = lightData.blockYMask();
         BitSet emptyBlockYMask = lightData.emptyBlockYMask();
         var iterator2 = lightData.blockUpdates().iterator();
         this.updateLighting(x, z, lightEngine, LightLayer.BLOCK, blockYMask, emptyBlockYMask, iterator2);
         lightEngine.setLightEnabled(new ChunkPos(x, z), true);
      }
   }

   private void updateLighting(int x, int z, LevelLightEngine lightEngine, LightLayer layer, BitSet skyYMask, BitSet emptySkyYMask, Iterator<byte[]> iterator) {
      for (int i = 0; i < lightEngine.getLightSectionCount(); i++) {
         int y = lightEngine.getMinLightSection() + i;
         boolean flag = skyYMask.get(i);
         boolean flag2 = emptySkyYMask.get(i);
         if (flag || flag2) {
            lightEngine.queueSectionData(
               layer, SectionPos.of(x, y, z), flag ? new DataLayer((byte[])((byte[])iterator.next()).clone()) : new DataLayer()
            );
         }
      }
   }

   @Override
   public void handleChunkBlocksUpdate(ClientboundSectionBlocksUpdatePacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld world = this.world;
      if (world != null) {
         packet.runUpdates((pos, var2x) -> world.handleBlockUpdate(pos, var2x, 19));
      }
   }

   @Override
   public void handleBlockUpdate(ClientboundBlockUpdatePacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld world = this.world;
      if (world != null) {
         world.handleBlockUpdate(packet.getPos(), packet.getBlockState(), 19);
      }
   }

   @Override
   public void handleSetChunkCacheRadius(ClientboundSetChunkCacheRadiusPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      this.chunkLoadDistance = packet.getRadius();
      BotWorld world = this.world;
      if (world != null) {
         world.getChunkSource().updateLoadDistance(packet.getRadius());
      }
   }

   @Override
   public void handleSetSimulationDistance(ClientboundSetSimulationDistancePacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      this.simulationDistance = packet.simulationDistance();
      BotWorld world = this.world;
      if (world != null) {
         world.setSimulationDistance(this.simulationDistance);
      }
   }

   @Override
   public void handleSetChunkCacheCenter(ClientboundSetChunkCacheCenterPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld world = this.world;
      if (world != null) {
         world.getChunkSource().setChunkMapCenter(packet.getX(), packet.getZ());
      }
   }

   @Override
   public void handleBlockChangedAck(ClientboundBlockChangedAckPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld world = this.world;
      if (world != null) {
         world.handlePlayerActionResponse(packet.sequence());
      }
   }

   @Override
   public void handleBlockEntityData(ClientboundBlockEntityDataPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld world = this.world;
      if (world != null) {
         world.getBlockEntity(packet.getPos(), packet.getType()).ifPresent(var2x -> {
            CompoundTag tag = packet.getTag();
            if (!tag.isEmpty()) {
               try (ScopedCollector scopedCollector = new ScopedCollector(var2x.problemPath(), LOGGER)) {
                  var2x.loadWithComponents(TagValueInput.create(scopedCollector, this.registries, tag));
               }
            }
         });
      }
   }

   @Override
   public void handleBlockEvent(ClientboundBlockEventPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld world = this.world;
      if (world != null) {
         world.blockEvent(packet.getPos(), packet.getBlock(), packet.getB0(), packet.getB1());
      }
   }

   @Override
   public void handleBlockDestruction(ClientboundBlockDestructionPacket packet) {
   }

   @Override
   public void handleLevelEvent(ClientboundLevelEventPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld world = this.world;
      if (world != null) {
         if (packet.isGlobalEvent()) {
            world.globalLevelEvent(packet.getType(), packet.getPos(), packet.getData());
         } else {
            world.levelEvent(packet.getType(), packet.getPos(), packet.getData());
         }
      }
   }

   @Override
   public void handleAddEntity(ClientboundAddEntityPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld world = this.world;
      if (world != null) {
         if (this.removedPlayerVehicleId.isPresent() && this.removedPlayerVehicleId.getAsInt() == packet.getId()) {
            this.removedPlayerVehicleId = OptionalInt.empty();
         }

         Entity entity = this.createEntity(world, packet);
         if (entity != null) {
            entity.recreateFromPacket(packet);
            world.addEntity(entity);
         } else {
            LOGGER.warn("Bot {}: skipping entity with id {}", this.client.name(), packet.getType());
         }
      }
   }

   @Nullable
   private Entity createEntity(BotWorld world, ClientboundAddEntityPacket packet) {
      EntityType type = packet.getType();
      if (type == net.minecraft.world.entity.EntityTypes.PLAYER) {
         PlayerInfo playerListEntry = this.getPlayerListEntry(packet.getUUID());
         if (playerListEntry == null) {
            LOGGER.warn("Bot {}: server added player prior to sending player info (id {})", this.client.name(), packet.getUUID());
            return null;
         } else {
            return new BotRemotePlayer(world, this, playerListEntry.getProfile());
         }
      } else {
         return type.create(world, EntitySpawnReason.LOAD);
      }
   }

   @Override
   public void handleSetEntityMotion(ClientboundSetEntityMotionPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld world = this.world;
      if (world != null) {
         Entity entity = world.getEntity(packet.id());
         if (entity != null) {
            entity.lerpMotion(packet.movement());
         }
      }
   }

   @Override
   public void handleSetEntityData(ClientboundSetEntityDataPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld world = this.world;
      if (world != null) {
         Entity entity = world.getEntity(packet.id());
         if (entity != null) {
            entity.getEntityData().assignValues(packet.packedItems());
         }
      }
   }

   @Override
   public void handleEntityPositionSync(ClientboundEntityPositionSyncPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld world = this.world;
      BotPlayer player = this.player;
      if (world != null && player != null) {
         Entity entity = world.getEntity(packet.id());
         if (entity != null) {
            net.minecraft.world.entity.PositionPath position = packet.position();
            Vec3 endPosition = position.endPosition();
            entity.getPositionCodec().setBase(endPosition);
            if (!entity.isLocalInstanceAuthoritative()) {
               float yRot = packet.yRot();
               float xRot = packet.xRot();
               boolean flag = entity.position().distanceToSqr(endPosition) > 4096.0;
               if (world.hasEntity(entity) && !flag) {
                  entity.moveOrInterpolateTo(position, yRot, xRot);
               } else {
                  entity.snapTo(endPosition.x, endPosition.y, endPosition.z, yRot, xRot);
                  if (entity.hasIndirectPassenger(player)) {
                     entity.positionRider(player);
                     player.setOldPosAndRot();
                  }
               }

               entity.setOnGround(packet.onGround());
            }
         }
      }
   }

   @Override
   public void handleTeleportEntity(ClientboundTeleportEntityPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld world = this.world;
      BotPlayer player = this.player;
      if (world != null && player != null) {
         Entity entity = world.getEntity(packet.id());
         if (entity == null) {
            if (this.removedPlayerVehicleId.isPresent() && this.removedPlayerVehicleId.getAsInt() == packet.id()) {
               LOGGER.debug("Bot {}: teleport for removed vehicle {}, applying to player", this.client.name(), packet.id());
               setPosition(packet.change(), packet.relatives(), player, false);
               this.connection.send(new PosRot(player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot(), false, false));
            }
         } else {
            boolean flag = packet.relatives().contains(Relative.X)
               || packet.relatives().contains(Relative.Y)
               || packet.relatives().contains(Relative.Z);
            boolean flag2 = world.hasEntity(entity) || !entity.isLocalInstanceAuthoritative() || flag;
            boolean setPosition2 = setPosition(packet.change(), packet.relatives(), entity, flag2);
            entity.setOnGround(packet.onGround());
            if (!setPosition2 && entity.hasIndirectPassenger(player)) {
               entity.positionRider(player);
               player.setOldPosAndRot();
               if (entity.isLocalInstanceAuthoritative()) {
                  this.connection.send(ServerboundMoveVehiclePacket.fromEntity(entity));
               }
            }
         }
      }
   }

   @Override
   public void handleMoveEntity(ClientboundMoveEntityPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld world = this.world;
      if (world != null) {
         Entity entity = packet.getEntity(world);
         if (entity != null) {
            if (entity.isLocalInstanceAuthoritative()) {
               VecDeltaCodec positionCodec = entity.getPositionCodec();
               if (packet.hasPosition()) {
                  positionCodec.setBase(packet.getPositionDelta().decode(positionCodec).endPosition());
               }
            } else {
               if (packet.hasPosition()) {
                  VecDeltaCodec positionCodec = entity.getPositionCodec();
                  net.minecraft.world.entity.PositionPath position = packet.getPositionDelta().decode(positionCodec);
                  positionCodec.setBase(position.endPosition());
                  if (packet.hasRotation()) {
                     entity.moveOrInterpolateTo(position, packet.getYRot(), packet.getXRot());
                  } else {
                     entity.moveOrInterpolateTo(position);
                  }
               } else if (packet.hasRotation()) {
                  entity.moveOrInterpolateTo(packet.getYRot(), packet.getXRot());
               }

               entity.setOnGround(packet.isOnGround());
            }
         }
      }
   }

   @Override
   public void handleRotateMob(ClientboundRotateHeadPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld world = this.world;
      if (world != null) {
         Entity entity = packet.getEntity(world);
         if (entity != null) {
            entity.lerpHeadTo(packet.getYHeadRot(), 3);
         }
      }
   }

   @Override
   public void handleRemoveEntities(ClientboundRemoveEntitiesPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld world = this.world;
      BotPlayer player = this.player;
      if (world != null) {
         packet.entityIds().forEach(var3x -> {
            Entity entity = world.getEntity(var3x);
            if (entity != null) {
               if (player != null && entity.hasIndirectPassenger(player)) {
                  this.removedPlayerVehicleId = OptionalInt.of(var3x);
               }

               world.removeEntity(var3x, RemovalReason.DISCARDED);
            }
         });
      }
   }

   @Override
   public void handleAnimate(ClientboundAnimatePacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld world = this.world;
      if (world != null) {
         Entity entity = world.getEntity(packet.getId());
         if (entity != null) {
            if (packet.getAction() == ClientboundAnimatePacket.WAKE_UP && entity instanceof Player player) {
               player.stopSleepInBed(false, false);
            }
         }
      }
   }

   @Override
   public void handleSwingAnimation(net.minecraft.network.protocol.game.ClientboundSwingAnimationPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld world = this.world;
      if (world != null && world.getEntity(packet.entityId()) instanceof LivingEntity livingEntity) {
         livingEntity.swing(packet.hand(), packet.animation(), false);
      }
   }

   // transient (client-predicted) blocks are only a visual effect
   @Override
   public void handleAddTransientBlockPacket(net.minecraft.network.protocol.game.ClientboundAddTransientBlockPacket packet) {
   }

   @Override
   public void handleGameRuleValues(net.minecraft.network.protocol.game.ClientboundGameRuleValuesPacket packet) {
   }

   @Override
   public void handleEntityEvent(ClientboundEntityEventPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld world = this.world;
      if (world != null) {
         Entity entity = packet.getEntity(world);
         if (entity != null) {
            switch (packet.getEventId()) {
               case 21:
               case 35:
               case 63:
                  break;
               default:
                  entity.handleEntityEvent(packet.getEventId());
            }
         }
      }
   }

   @Override
   public void handleEntityLinkPacket(ClientboundSetEntityLinkPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld world = this.world;
      if (world != null && world.getEntity(packet.getSourceId()) instanceof Leashable leashable) {
         leashable.setDelayedLeashHolderId(packet.getDestId());
      }
   }

   @Override
   public void handleSetEntityPassengersPacket(ClientboundSetPassengersPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld world = this.world;
      BotPlayer player = this.player;
      if (world != null) {
         Entity entity = world.getEntity(packet.getVehicle());
         if (entity == null) {
            LOGGER.warn("Bot {}: received passengers for unknown entity", this.client.name());
         } else {
            entity.ejectPassengers();

            for (int passenger : packet.getPassengers()) {
               Entity entity2 = world.getEntity(passenger);
               if (entity2 != null) {
                  entity2.startRiding(entity, true, false);
                  if (entity2 == player) {
                     this.removedPlayerVehicleId = OptionalInt.empty();
                  }
               }
            }
         }
      }
   }

   @Override
   public void handleUpdateAttributes(ClientboundUpdateAttributesPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld world = this.world;
      if (world != null) {
         Entity entity = world.getEntity(packet.getEntityId());
         if (entity != null) {
            if (!(entity instanceof LivingEntity livingEntity)) {
               throw new IllegalStateException("Server tried to update attributes of a non-living entity (actually: " + entity + ")");
            } else {
               AttributeMap attributes = livingEntity.getAttributes();

               for (AttributeSnapshot attributeSnapshot : packet.getValues()) {
                  AttributeInstance attributeInstance = attributes.getInstance(attributeSnapshot.attribute());
                  if (attributeInstance == null) {
                     LOGGER.warn("Bot {}: entity {} does not have attribute {}", new Object[]{this.client.name(), entity, attributeSnapshot.attribute().getRegisteredName()});
                  } else {
                     attributeInstance.setBaseValue(attributeSnapshot.base());
                     attributeInstance.removeModifiers();

                     for (AttributeModifier modifier : attributeSnapshot.modifiers()) {
                        attributeInstance.addTransientModifier(modifier);
                     }
                  }
               }
            }
         }
      }
   }

   @Override
   public void handleUpdateMobEffect(ClientboundUpdateMobEffectPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld world = this.world;
      if (world != null) {
         if (world.getEntity(packet.getEntityId()) instanceof LivingEntity livingEntity) {
            Holder effect = packet.getEffect();
            MobEffectInstance mobEffectInstance = new MobEffectInstance(
               effect, packet.getEffectDurationTicks(), packet.getEffectAmplifier(), packet.isEffectAmbient(), packet.isEffectVisible(), packet.effectShowsIcon(), null
            );
            if (!packet.shouldBlend()) {
               mobEffectInstance.skipBlending();
            }

            livingEntity.forceAddEffect(mobEffectInstance, null);
         }
      }
   }

   @Override
   public void handleRemoveMobEffect(ClientboundRemoveMobEffectPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld world = this.world;
      if (world != null && packet.getEntity(world) instanceof LivingEntity livingEntity) {
         livingEntity.removeEffectNoUpdate(packet.effect());
      }
   }

   @Override
   public void handleSetEquipment(ClientboundSetEquipmentPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld world = this.world;
      if (world != null && world.getEntity(packet.getEntity()) instanceof LivingEntity livingEntity) {
         packet.getSlots().forEach(slot -> livingEntity.setItemSlot((EquipmentSlot)slot.getFirst(), (ItemStack)slot.getSecond()));
      }
   }

   @Override
   public void handleDamageEvent(ClientboundDamageEventPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld world = this.world;
      if (world != null) {
         Entity entity = world.getEntity(packet.entityId());
         if (entity != null) {
            entity.handleDamageEvent(packet.getSource(world));
         }
      }
   }

   @Override
   public void handleHurtAnimation(ClientboundHurtAnimationPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld world = this.world;
      if (world != null) {
         Entity entity = world.getEntity(packet.id());
         if (entity != null) {
            entity.animateHurt(packet.yaw());
         }
      }
   }

   @Override
   public void handleMinecartAlongTrack(ClientboundMoveMinecartPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld world = this.world;
      if (world != null) {
         Entity entity = packet.getEntity(world);
         if (entity instanceof AbstractMinecart abstractMinecart
            && !entity.isLocalInstanceAuthoritative()
            && abstractMinecart.getBehavior() instanceof NewMinecartBehavior newMinecartBehavior) {
            newMinecartBehavior.lerpSteps.addAll(packet.lerpSteps());
         }
      }
   }

   @Override
   public void handleProjectilePowerPacket(ClientboundProjectilePowerPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld world = this.world;
      if (world != null && world.getEntity(packet.getId()) instanceof AbstractHurtingProjectile hurtingProjectile) {
         hurtingProjectile.accelerationPower = packet.getAccelerationPower();
      }
   }

   @Override
   public void handleTakeItemEntity(ClientboundTakeItemEntityPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld world = this.world;
      if (world != null) {
         Entity entity = world.getEntity(packet.getItemId());
         if (entity != null) {
            if (entity instanceof ItemEntity itemEntity) {
               ItemStack stack = itemEntity.getItem();
               if (!stack.isEmpty()) {
                  stack.shrink(packet.getAmount());
               }

               if (stack.isEmpty()) {
                  world.removeEntity(packet.getItemId(), RemovalReason.DISCARDED);
               }
            } else if (!(entity instanceof ExperienceOrb)) {
               world.removeEntity(packet.getItemId(), RemovalReason.DISCARDED);
            }
         }
      }
   }

   @Override
   public void handleExplosion(ClientboundExplodePacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotPlayer player = this.player;
      if (player != null) {
         packet.playerKnockback().ifPresent(player::addDeltaMovement);
      }
   }

   @Override
   public void handleLookAt(ClientboundPlayerLookAtPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld world = this.world;
      BotPlayer player = this.player;
      if (world != null && player != null) {
         Vec3 position = packet.getPosition(world);
         if (position != null) {
            player.lookAt(packet.getFromAnchor(), position);
         }
      }
   }

   @Override
   public void handleSetHealth(ClientboundSetHealthPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotPlayer player = this.player;
      if (player != null) {
         player.updateHealth(packet.getHealth());
         player.getFoodData().setFoodLevel(packet.getFood());
         player.getFoodData().setSaturation(packet.getSaturation());
      }
   }

   @Override
   public void handleSetExperience(ClientboundSetExperiencePacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotPlayer player = this.player;
      if (player != null) {
         player.setExperience(packet.getExperienceProgress(), packet.getTotalExperience(), packet.getExperienceLevel());
      }
   }

   @Override
   public void handleSetHeldSlot(ClientboundSetHeldSlotPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotPlayer player = this.player;
      if (player != null && Inventory.isHotbarSlot(packet.slot())) {
         player.getInventory().setSelectedSlot(packet.slot());
      }
   }

   @Override
   public void handleContainerSetSlot(ClientboundContainerSetSlotPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotPlayer player = this.player;
      if (player != null) {
         ItemStack itemStack = packet.getItem();
         int slot = packet.getSlot();
         if (packet.getContainerId() == 0) {
            player.inventoryMenu.setItem(slot, packet.getStateId(), itemStack);
         } else if (packet.getContainerId() == player.containerMenu.containerId) {
            player.containerMenu.setItem(slot, packet.getStateId(), itemStack);
         }
      }
   }

   @Override
   public void handleSetCursorItem(ClientboundSetCursorItemPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotPlayer player = this.player;
      if (player != null) {
         player.containerMenu.setCarried(packet.contents());
      }
   }

   @Override
   public void handleSetPlayerInventory(ClientboundSetPlayerInventoryPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotPlayer player = this.player;
      if (player != null) {
         player.getInventory().setItem(packet.slot(), packet.contents());
      }
   }

   @Override
   public void handleContainerContent(ClientboundContainerSetContentPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotPlayer player = this.player;
      if (player != null) {
         if (packet.containerId() == 0) {
            player.inventoryMenu.initializeContents(packet.stateId(), packet.items(), packet.carriedItem());
         } else if (packet.containerId() == player.containerMenu.containerId) {
            player.containerMenu.initializeContents(packet.stateId(), packet.items(), packet.carriedItem());
         }
      }
   }

   @Override
   public void handleContainerSetData(ClientboundContainerSetDataPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotPlayer player = this.player;
      if (player != null && player.containerMenu.containerId == packet.getContainerId()) {
         player.containerMenu.setData(packet.getId(), packet.getValue());
      }
   }

   @Override
   public void handleOpenScreen(ClientboundOpenScreenPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotPlayer player = this.player;
      if (player != null) {
         AbstractContainerMenu containerMenu = packet.getType().create(packet.getContainerId(), player.getInventory());
         player.containerMenu = containerMenu;
         this.currentScreenTitle = packet.getTitle();
         this.currentScreenSyncId = packet.getContainerId();
      }
   }

   @Override
   public void handleContainerClose(ClientboundContainerClosePacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotPlayer player = this.player;
      if (player != null) {
         player.closeScreen();
      }
   }

   @Override
   public void handleMerchantOffers(ClientboundMerchantOffersPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotPlayer player = this.player;
      if (player != null) {
         AbstractContainerMenu containerMenu = player.containerMenu;
         if (packet.getContainerId() == containerMenu.containerId && containerMenu instanceof MerchantMenu merchantMenu) {
            merchantMenu.setOffers(packet.getOffers());
            merchantMenu.setXp(packet.getVillagerXp());
            merchantMenu.setMerchantLevel(packet.getVillagerLevel());
            merchantMenu.setShowProgressBar(packet.showProgress());
            merchantMenu.setCanRestock(packet.canRestock());
         }
      }
   }

   @Override
   public void handleItemCooldown(ClientboundCooldownPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotPlayer player = this.player;
      if (player != null) {
         if (packet.duration() == 0) {
            player.getCooldowns().removeCooldown(packet.cooldownGroup());
         } else {
            player.getCooldowns().addCooldown(packet.cooldownGroup(), packet.duration());
         }
      }
   }

   @Override
   public void handlePlayerAbilities(ClientboundPlayerAbilitiesPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotPlayer player = this.player;
      if (player != null) {
         player.getAbilities().flying = packet.isFlying();
         player.getAbilities().instabuild = packet.canInstabuild();
         player.getAbilities().invulnerable = packet.isInvulnerable();
         player.getAbilities().mayfly = packet.canFly();
         player.getAbilities().setFlyingSpeed(packet.getFlyingSpeed());
         player.getAbilities().setWalkingSpeed(packet.getWalkingSpeed());
      }
   }

   @Override
   public void handlePlayerCombatKill(ClientboundPlayerCombatKillPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld world = this.world;
      BotPlayer player = this.player;
      if (world != null && player != null) {
         Entity entity = world.getEntity(packet.playerId());
         if (entity == player) {
            player.requestRespawn();
         }
      }
   }

   @Override
   public void handleGameEvent(ClientboundGameEventPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld world = this.world;
      Type event = packet.getEvent();
      float rainLevel = packet.getParam();
      int id = Mth.floor(rainLevel + 0.5F);
      if (event == ClientboundGameEventPacket.START_RAINING && world != null) {
         world.setRainLevel(0.0F);
      } else if (event == ClientboundGameEventPacket.STOP_RAINING && world != null) {
         world.setRainLevel(1.0F);
      } else if (event == ClientboundGameEventPacket.CHANGE_GAME_MODE) {
         BotInteractionManager interactionManager = this.interactionManager;
         if (interactionManager != null) {
            interactionManager.setGameMode(GameType.byId(id));
         }
      } else if (event == ClientboundGameEventPacket.WIN_GAME) {
         this.sendPacket(new ServerboundClientCommandPacket(Action.PERFORM_RESPAWN));
      } else if (event == ClientboundGameEventPacket.RAIN_LEVEL_CHANGE && world != null) {
         world.setRainLevel(rainLevel);
      } else if (event == ClientboundGameEventPacket.THUNDER_LEVEL_CHANGE && world != null) {
         world.setThunderLevel(rainLevel);
      } else if (event == ClientboundGameEventPacket.LEVEL_CHUNKS_LOAD_START) {
         this.initialChunksComing = true;
      }
   }

   @Override
   public void handleLowDiskSpaceWarning(net.minecraft.network.protocol.game.ClientboundLowDiskSpaceWarningPacket packet) {
   }

   @Override
   public void handleSetTime(ClientboundSetTimePacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld world = this.world;
      if (world != null) {
         world.setTime(packet.gameTime());
         this.clockManager.handleUpdates(packet.gameTime(), packet.clockUpdates());
      }
   }

   @Override
   public void handleSetSpawn(ClientboundSetDefaultSpawnPositionPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld world = this.world;
      if (world != null) {
         world.setRespawnData(packet.respawnData());
      }
   }

   @Override
   public void handleChangeDifficulty(ClientboundChangeDifficultyPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      if (this.worldProperties != null) {
         this.worldProperties.setDifficulty(packet.difficulty());
         this.worldProperties.setDifficultyLocked(packet.locked());
      }
   }

   @Override
   public void handleInitializeBorder(ClientboundInitializeBorderPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld world = this.world;
      if (world != null) {
         WorldBorder worldBorder = world.getWorldBorder();
         worldBorder.setCenter(packet.getNewCenterX(), packet.getNewCenterZ());
         long lerpTime = packet.getLerpTime();
         if (lerpTime > 0L) {
            worldBorder.lerpSizeBetween(packet.getOldSize(), packet.getNewSize(), lerpTime, 0L);
         } else {
            worldBorder.setSize(packet.getNewSize());
         }

         worldBorder.setAbsoluteMaxSize(packet.getNewAbsoluteMaxSize());
         worldBorder.setWarningBlocks(packet.getWarningBlocks());
         worldBorder.setWarningTime(packet.getWarningTime());
      }
   }

   @Override
   public void handleSetBorderCenter(ClientboundSetBorderCenterPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld world = this.world;
      if (world != null) {
         world.getWorldBorder().setCenter(packet.getNewCenterX(), packet.getNewCenterZ());
      }
   }

   @Override
   public void handleSetBorderLerpSize(ClientboundSetBorderLerpSizePacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld world = this.world;
      if (world != null) {
         world.getWorldBorder().lerpSizeBetween(packet.getOldSize(), packet.getNewSize(), packet.getLerpTime(), 0L);
      }
   }

   @Override
   public void handleSetBorderSize(ClientboundSetBorderSizePacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld world = this.world;
      if (world != null) {
         world.getWorldBorder().setSize(packet.getSize());
      }
   }

   @Override
   public void handleSetBorderWarningDistance(ClientboundSetBorderWarningDistancePacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld world = this.world;
      if (world != null) {
         world.getWorldBorder().setWarningBlocks(packet.getWarningBlocks());
      }
   }

   @Override
   public void handleSetBorderWarningDelay(ClientboundSetBorderWarningDelayPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld world = this.world;
      if (world != null) {
         world.getWorldBorder().setWarningTime(packet.getWarningDelay());
      }
   }

   @Override
   public void handleMapItemData(ClientboundMapItemDataPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld world = this.world;
      if (world != null) {
         MapId mapId = packet.mapId();
         MapItemSavedData mapData = world.getMapData(mapId);
         if (mapData == null) {
            mapData = MapItemSavedData.createForClient(packet.scale(), packet.locked(), world.dimension());
            world.putClientsideMapState(mapId, mapData);
         }

         packet.applyToMap(mapData);
      }
   }

   @Override
   public void handleTickingState(ClientboundTickingStatePacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld world = this.world;
      if (world != null) {
         TickRateManager tickRateManager = world.tickRateManager();
         tickRateManager.setTickRate(packet.tickRate());
         tickRateManager.setFrozen(packet.isFrozen());
      }
   }

   @Override
   public void handleTickingStep(ClientboundTickingStepPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld world = this.world;
      if (world != null) {
         world.tickRateManager().setFrozenTicksToRun(packet.tickSteps());
      }
   }

   @Override
   public void handlePlayerInfoUpdate(ClientboundPlayerInfoUpdatePacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());

      for (net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket.Entry entry : packet.newEntries()) {
         BotPlayerListEntry botPlayerListEntry = new BotPlayerListEntry(Objects.requireNonNull(entry.profile()), false);
         this.playerListEntries.putIfAbsent(entry.profileId(), botPlayerListEntry);
      }

      for (net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket.Entry entry : packet.entries()) {
         PlayerInfo playerListEntry = this.playerListEntries.get(entry.profileId());
         if (playerListEntry instanceof BotPlayerListEntry botPlayerListEntry2) {
            for (net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket.Action action : packet.actions()) {
               this.handlePlayerListAction(action, entry, botPlayerListEntry2);
            }
         } else {
            LOGGER.warn(
               "Bot {}: ignoring player info update for unknown player {} ({})", new Object[]{this.client.name(), entry.profileId(), packet.actions()}
            );
         }
      }
   }

   private void handlePlayerListAction(net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket.Action action, net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket.Entry entry, BotPlayerListEntry botPlayerListEntry) {
      BotPlayer player = this.player;
      switch (action) {
         case INITIALIZE_CHAT:
            botPlayerListEntry.resetBotSession(false);
            break;
         case UPDATE_GAME_MODE:
            if (botPlayerListEntry.getGameMode() != entry.gameMode() && player != null && player.getUUID().equals(entry.profileId())) {
               player.onGameModeChanged(entry.gameMode());
            }

            botPlayerListEntry.setBotGameMode(entry.gameMode());
            break;
         case UPDATE_LATENCY:
            botPlayerListEntry.setBotLatency(entry.latency());
            break;
         case UPDATE_DISPLAY_NAME:
            botPlayerListEntry.setTabListDisplayName(entry.displayName());
            break;
         case UPDATE_HAT:
            botPlayerListEntry.setShowHat(entry.showHat());
            break;
         case UPDATE_LIST_ORDER:
            botPlayerListEntry.setTabListOrder(entry.listOrder());
         case UPDATE_LISTED:
      }
   }

   @Override
   public void handlePlayerInfoRemove(ClientboundPlayerInfoRemovePacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());

      for (UUID profileId : packet.profileIds()) {
         this.playerListEntries.remove(profileId);
      }
   }

   @Override
   public void handleAddObjective(ClientboundSetObjectivePacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      String objectiveName = packet.getObjectiveName();
      synchronized (this.scoreboard) {
         if (packet.getMethod() == 0) {
            this.scoreboard
               .addObjective(objectiveName, ObjectiveCriteria.DUMMY, packet.getDisplayName(), packet.getRenderType(), false, packet.getNumberFormat().orElse(null));
         } else {
            Objective objective = this.scoreboard.getObjective(objectiveName);
            if (objective != null) {
               if (packet.getMethod() == 1) {
                  this.scoreboard.removeObjective(objective);
               } else if (packet.getMethod() == 2) {
                  objective.setRenderType(packet.getRenderType());
                  objective.setDisplayName(packet.getDisplayName());
                  objective.setNumberFormat(packet.getNumberFormat().orElse(null));
               }
            }
         }
      }
   }

   @Override
   public void handleSetScore(ClientboundSetScorePacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      String objectiveName = packet.objectiveName();
      ScoreHolder scoreHolder = ScoreHolder.forNameOnly(packet.owner());
      synchronized (this.scoreboard) {
         Objective objective = this.scoreboard.getObjective(objectiveName);
         if (objective != null) {
            ScoreAccess orCreatePlayerScore = this.scoreboard.getOrCreatePlayerScore(scoreHolder, objective, true);
            orCreatePlayerScore.set(packet.score());
            orCreatePlayerScore.display(packet.display().orElse(null));
            orCreatePlayerScore.numberFormatOverride(packet.numberFormat().orElse(null));
            return;
         }
      }

      LOGGER.warn("Bot {}: received packet for unknown scoreboard objective: {}", this.client.name(), objectiveName);
   }

   @Override
   public void handleResetScore(ClientboundResetScorePacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      String objectiveName = packet.objectiveName();
      ScoreHolder player = ScoreHolder.forNameOnly(packet.owner());
      synchronized (this.scoreboard) {
         if (objectiveName == null) {
            this.scoreboard.resetAllPlayerScores(player);
         } else {
            Objective objective = this.scoreboard.getObjective(objectiveName);
            if (objective != null) {
               this.scoreboard.resetSinglePlayerScore(player, objective);
            }
         }
      }
   }

   @Override
   public void handleSetDisplayObjective(ClientboundSetDisplayObjectivePacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      String objectiveName = packet.getObjectiveName();
      synchronized (this.scoreboard) {
         Objective objective = objectiveName == null ? null : this.scoreboard.getObjective(objectiveName);
         this.scoreboard.setDisplayObjective(packet.getSlot(), objective);
      }
   }

   @Override
   public void handleSetPlayerTeamPacket(ClientboundSetPlayerTeamPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      synchronized (this.scoreboard) {
         this.applyTeam(packet);
      }
   }

   private void applyTeam(ClientboundSetPlayerTeamPacket packet) {
      net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket.Action teamAction = packet.getTeamAction();
      PlayerTeam addPlayerTeam;
      if (teamAction == net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket.Action.ADD) {
         addPlayerTeam = this.scoreboard.addPlayerTeam(packet.getName());
      } else {
         addPlayerTeam = this.scoreboard.getPlayerTeam(packet.getName());
         if (addPlayerTeam == null) {
            LOGGER.warn("Bot {}: received packet for unknown team {}", this.client.name(), packet.getName());
            return;
         }
      }

      packet.getParameters().ifPresent(var1x -> {
         addPlayerTeam.setDisplayName(var1x.displayName());
         addPlayerTeam.setColor(var1x.color());
         addPlayerTeam.unpackOptions(var1x.options());
         addPlayerTeam.setNameTagVisibility(var1x.nameTagVisibility());
         addPlayerTeam.setCollisionRule(var1x.collisionRule());
         addPlayerTeam.setPlayerPrefix(var1x.playerPrefix());
         addPlayerTeam.setPlayerSuffix(var1x.playerSuffix());
      });
      net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket.Action playerAction = packet.getPlayerAction();
      if (playerAction == net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket.Action.ADD) {
         for (String player : packet.getPlayers()) {
            this.scoreboard.addPlayerToTeam(player, addPlayerTeam);
         }
      } else if (playerAction == net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket.Action.REMOVE) {
         for (String player : packet.getPlayers()) {
            this.scoreboard.removePlayerFromTeam(player, addPlayerTeam);
         }
      }

      if (teamAction == net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket.Action.REMOVE) {
         this.scoreboard.removePlayerTeam(addPlayerTeam);
      }
   }

   @Override
   public void handleUpdateRecipes(ClientboundUpdateRecipesPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      this.recipeManager = new ClientRecipeContainer(packet.itemSets(), packet.stonecutterRecipes());
   }

   @Override
   public void handleUpdateTags(ClientboundUpdateTagsPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      var arrayList = new ArrayList<>(packet.tags().size());
      packet.tags().forEach((var2x, tag) -> {
         if (RegistrySynchronization.isNetworkable((ResourceKey<? extends Registry<?>>)var2x)) {
            arrayList.add(this.startTagReload((ResourceKey<? extends Registry<?>>)var2x, tag));
         }
      });
      arrayList.forEach(x -> ((PendingTags)x).apply());
   }

   private <T> PendingTags<T> startTagReload(
      ResourceKey<? extends Registry<? extends T>> name, net.minecraft.tags.TagNetworkSerialization.NetworkPayload tag
   ) {
      Registry orThrow = this.registries.lookupOrThrow(name);
      return orThrow.prepareTagReload(tag.resolve(orThrow));
   }

   @Override
   public void handleSystemChat(ClientboundSystemChatPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      this.client.onChat(packet.content());
   }

   @Override
   public void handlePlayerChat(ClientboundPlayerChatPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      Object unsignedContent = packet.unsignedContent() != null ? packet.unsignedContent() : Component.literal(packet.body().content());
      this.client.onChat(packet.chatType().decorate((Component)unsignedContent));
   }

   @Override
   public void handleDisguisedChat(ClientboundDisguisedChatPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      this.client.onChat(packet.chatType().decorate(packet.message()));
   }

   @Override
   public void handleDeleteChat(ClientboundDeleteChatPacket packet) {
   }

   @Override
   public void handleParticleEvent(ClientboundLevelParticlesPacket packet) {
   }

   @Override
   public void handleSoundEvent(ClientboundSoundPacket packet) {
   }

   @Override
   public void handleSoundEntityEvent(ClientboundSoundEntityPacket packet) {
   }

   @Override
   public void handleStopSoundEvent(ClientboundStopSoundPacket packet) {
   }

   @Override
   public void handleOpenSignEditor(ClientboundOpenSignEditorPacket packet) {
   }

   @Override
   public void handleOpenBook(ClientboundOpenBookPacket packet) {
   }

   @Override
   public void handlePlaceRecipe(ClientboundPlaceGhostRecipePacket packet) {
   }

   @Override
   public void handleAwardStats(ClientboundAwardStatsPacket packet) {
   }

   @Override
   public void handleRecipeBookAdd(ClientboundRecipeBookAddPacket packet) {
   }

   @Override
   public void handleRecipeBookRemove(ClientboundRecipeBookRemovePacket packet) {
   }

   @Override
   public void handleRecipeBookSettings(ClientboundRecipeBookSettingsPacket packet) {
   }

   @Override
   public void handleUpdateAdvancementsPacket(ClientboundUpdateAdvancementsPacket packet) {
   }

   @Override
   public void handleSelectAdvancementsTab(ClientboundSelectAdvancementsTabPacket packet) {
   }

   @Override
   public void handleCommands(ClientboundCommandsPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      this.commandDispatcher = new CommandDispatcher(
         packet.getRoot(CommandBuildContext.simple(this.registries, this.enabledFeatures), new NodeBuilder<SharedSuggestionProvider>() {
            @Override
            public ArgumentBuilder<SharedSuggestionProvider, ?> createLiteral(String name) {
               return LiteralArgumentBuilder.literal(name);
            }

            @Override
            public ArgumentBuilder<SharedSuggestionProvider, ?> createArgument(String name, ArgumentType<?> type, @Nullable Identifier suggestionProviderId) {
               return RequiredArgumentBuilder.argument(name, type);
            }

            @Override
            public ArgumentBuilder<SharedSuggestionProvider, ?> configure(ArgumentBuilder<SharedSuggestionProvider, ?> arg, boolean disableExecution, boolean requireTrusted) {
               return arg;
            }
         })
      );
   }

   @Override
   public void handleCommandSuggestions(ClientboundCommandSuggestionsPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      this.commandSource.onCommandSuggestions(packet.id(), packet.toSuggestions());
   }

   @Override
   public void handleCustomChatCompletions(ClientboundCustomChatCompletionsPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      this.commandSource.onChatSuggestions(packet.action(), packet.entries());
   }

   @Override
   public void handleTagQueryPacket(ClientboundTagQueryPacket packet) {
   }

   @Override
   public void handlePlayerCombatEnd(ClientboundPlayerCombatEndPacket packet) {
   }

   @Override
   public void handlePlayerCombatEnter(ClientboundPlayerCombatEnterPacket packet) {
   }

   @Override
   public void handleSetCamera(ClientboundSetCameraPacket packet) {
   }

   @Override
   public void setTitleText(ClientboundSetTitleTextPacket packet) {
   }

   @Override
   public void setSubtitleText(ClientboundSetSubtitleTextPacket packet) {
   }

   @Override
   public void setActionBarText(ClientboundSetActionBarTextPacket packet) {
   }

   @Override
   public void setTitlesAnimation(ClientboundSetTitlesAnimationPacket packet) {
   }

   @Override
   public void handleTitlesClear(ClientboundClearTitlesPacket packet) {
   }

   @Override
   public void handleServerData(ClientboundServerDataPacket packet) {
   }

   @Override
   public void handleTabListCustomisation(ClientboundTabListPacket packet) {
   }

   @Override
   public void handleBossUpdate(ClientboundBossEventPacket packet) {
   }

   @Override
   public void handleDebugSample(ClientboundDebugSamplePacket packet) {
   }

   @Override
   public void handlePongResponse(ClientboundPongResponsePacket packet) {
   }

   @Override
   public void handleMountScreenOpen(ClientboundMountScreenOpenPacket packet) {
   }

   @Override
   public void handleTestInstanceBlockStatus(ClientboundTestInstanceBlockStatus packet) {
   }

   @Override
   public void handleWaypoint(ClientboundTrackedWaypointPacket packet) {
   }

   @Override
   public void handleDebugChunkValue(ClientboundDebugChunkValuePacket packet) {
   }

   @Override
   public void handleDebugBlockValue(ClientboundDebugBlockValuePacket packet) {
   }

   @Override
   public void handleDebugEntityValue(ClientboundDebugEntityValuePacket packet) {
   }

   @Override
   public void handleDebugEvent(ClientboundDebugEventPacket packet) {
   }

   @Override
   public void handleGameTestHighlightPos(ClientboundGameTestHighlightPosPacket packet) {
   }

   @Override
   public void handleShowDialog(ClientboundShowDialogPacket packet) {
   }

   @Override
   public void handleClearDialog(ClientboundClearDialogPacket packet) {
   }
}
