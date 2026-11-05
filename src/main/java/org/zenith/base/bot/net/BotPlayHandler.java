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
         RegistryOps var1 = this.registries.createSerializationContext(HashOps.CRC32C_INSTANCE);
         this.zenith$componentHasher = var1x -> ((HashCode)((com.mojang.serialization.DataResult<HashCode>)(Object)var1x.encodeValue(var1))
               .<IllegalArgumentException>getOrThrow(var1xx -> new IllegalArgumentException("Failed to hash " + var1x + ": " + var1xx)))
            .asInt();
      }

      return this.zenith$componentHasher;
   }

   public boolean isPlayerLoaded() {
      return this.playerLoadedSent;
   }

   public BotPlayHandler(BotClient var1, BotConnection var2, BotConnectionState var3) {
      super(var1, var2, var3);
      this.profile = var3.profile();
      this.registries = var3.receivedRegistries();
      this.enabledFeatures = var3.enabledFeatures();
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
      BotInteractionManager var1 = this.interactionManager;
      if (var1 == null) {
         throw new IllegalStateException("Bot interaction manager is not initialized (no GameJoin yet)");
      } else {
         return var1;
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
      BotPlayer var1 = this.player;
      if (var1 != null && var1.containerMenu != null) {
         return var1.containerMenu.containerId == this.currentScreenSyncId ? this.currentScreenTitle : null;
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
   public PlayerInfo getPlayerListEntry(UUID var1) {
      return this.playerListEntries.get(var1);
   }

   @Nullable
   public PlayerInfo getPlayerListEntry(String var1) {
      for (PlayerInfo var3 : this.playerListEntries.values()) {
         if (var3.getProfile().name().equals(var1)) {
            return var3;
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
         BotWorld var1 = this.world;
         BotPlayer var2 = this.player;
         BotInteractionManager var3 = this.interactionManager;
         if (var1 != null && var2 != null && var3 != null) {
            var1.runQueuedChunkUpdates();
            var1.getChunkSource().getLightEngine().runLightUpdates();
            var3.tick();
            this.client.onBotUpdate(var1, var2);
            var1.tick(() -> true);
            var1.tickEntities();
            this.tickPlayerLoaded(var1, var2);
         }

         this.sendPacket(ServerboundClientTickEndPacket.INSTANCE);
      }
   }

   private void tickPlayerLoaded(BotWorld var1, BotPlayer var2) {
      if (!this.playerLoadedSent) {
         boolean var3 = var1.getChunkSource().getChunk(var2.chunkPosition().x(), var2.chunkPosition().z(), null, false) != null;
         if (var3 || ++this.loadWaitTicks >= 60) {
            this.playerLoadedSent = true;
            this.loadWaitTicks = 0;
            this.sendPacket(new ServerboundPlayerLoadedPacket());
         }
      }
   }

   public void sendChatMessage(String var1) {
      this.sendPacket(new ServerboundChatPacket(var1, Instant.now(), SaltSupplier.getLong(), null, new Update(0, new BitSet(), (byte)0)));
   }

   public void sendCommand(String var1) {
      this.sendPacket(new ServerboundChatCommandPacket(var1));
   }

   @Override
   public void handleLogin(ClientboundLoginPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      CommonPlayerSpawnInfo var2 = packet.commonPlayerSpawnInfo();
      this.worldKeys = new LinkedHashSet<>(packet.levels());
      this.chunkLoadDistance = packet.chunkRadius();
      this.simulationDistance = packet.simulationDistance();
      this.secureChatEnforced = packet.enforcesSecureChat();
      BotInteractionManager var3 = new BotInteractionManager(this);
      this.interactionManager = var3;
      ClientLevelData var4 = new ClientLevelData(Difficulty.NORMAL, packet.hardcore(), var2.isFlat());
      this.worldProperties = var4;
      BotWorld var5 = new BotWorld(
         this, var4, var2.dimension(), var2.dimensionType(), this.chunkLoadDistance, this.simulationDistance, var2.isDebug(), var2.seed(), var2.seaLevel()
      );
      this.world = var5;
      BotPlayer var6 = new BotPlayer(var5, this, false, false);
      this.player = var6;
      var6.init();
      var6.setId(packet.playerId());
      var6.setYRot(-180.0F);
      var5.setBotPlayer(var6);
      var5.addEntity(var6);
      var3.copyAbilities(var6);
      var6.setReducedDebugInfo(packet.reducedDebugInfo());
      var6.setLastDeathLocation(var2.lastDeathLocation());
      var6.setPortalCooldown(var2.portalCooldown());
      var3.setGameModes(var2.gameType(), var2.previousGameType().orElse(null));
      this.playerLoadedSent = false;
      this.loadWaitTicks = 0;
      this.initialChunksComing = false;
      LOGGER.debug("Bot {} joined dimension={} entityId={}", new Object[]{this.client.name(), var2.dimension().identifier(), packet.playerId()});
      this.client.onGameJoin(var5, var6);
   }

   @Override
   public void handleRespawn(ClientboundRespawnPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotPlayer var2 = this.player;
      BotWorld var3 = this.world;
      BotInteractionManager var4 = this.interactionManager;
      if (var2 != null && var3 != null && var4 != null && this.worldProperties != null) {
         CommonPlayerSpawnInfo var5 = packet.commonPlayerSpawnInfo();
         ResourceKey var6 = var5.dimension();
         Holder var7 = var5.dimensionType();
         boolean var8 = var6 != var2.level().dimension();
         BotWorld var9 = var3;
         if (var8) {
            var var10 = var3.getMapStates();
            ClientLevelData var11 = new ClientLevelData(this.worldProperties.getDifficulty(), this.worldProperties.isHardcore(), var5.isFlat());
            this.worldProperties = var11;
            var9 = new BotWorld(this, var11, var6, var7, this.chunkLoadDistance, this.simulationDistance, var5.isDebug(), var5.seed(), var5.seaLevel());
            var9.putMapStates(var10);
            this.world = var9;
         }

         if (var2.hasContainerOpen()) {
            var2.closeContainer();
         }

         boolean var13 = packet.shouldKeep((byte)2);
         BotPlayer var14 = new BotPlayer(var9, this, var13 && var2.isShiftKeyDown(), var13 && var2.isSprinting());
         var14.setId(var2.getId());
         this.player = var14;
         if (var13) {
            var var12 = var2.getEntityData().getNonDefaultValues();
            if (var12 != null) {
               var14.getEntityData().assignValues(var12);
            }

            var14.setDeltaMovement(var2.getDeltaMovement());
            var14.setYRot(var2.getYRot());
            var14.setXRot(var2.getXRot());
         } else {
            var14.init();
            var14.setYRot(-180.0F);
         }

         if (packet.shouldKeep((byte)1)) {
            var14.getAttributes().assignAllValues(var2.getAttributes());
         } else {
            var14.getAttributes().assignBaseValues(var2.getAttributes());
         }

         var9.setBotPlayer(var14);
         var9.addEntity(var14);
         var4.copyAbilities(var14);
         var14.setReducedDebugInfo(var2.isReducedDebugInfo());
         var14.setLastDeathLocation(var5.lastDeathLocation());
         var14.setPortalCooldown(var5.portalCooldown());
         var4.setGameModes(var5.gameType(), var5.previousGameType().orElse(null));
         this.playerLoadedSent = false;
         this.loadWaitTicks = 0;
         if (var8) {
            this.client.onGameJoin(var9, var14);
         } else {
            this.client.onRespawn(var9, var14);
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
      BotConnectionState var2 = new BotConnectionState(
         this.profile, this.registries, this.enabledFeatures, this.brand, this.serverCookies, this.customReportDetails, this.serverLinks
      );
      BotConfigHandler var3 = new BotConfigHandler(this.client, this.connection, var2);
      this.connection.transitionInbound(ConfigurationProtocols.CLIENTBOUND, var3);
      this.connection.send(ServerboundConfigurationAcknowledgedPacket.INSTANCE);
      this.connection.transitionOutbound(ConfigurationProtocols.SERVERBOUND);
   }

   @Override
   public void handleBundlePacket(ClientboundBundlePacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());

      for (Packet var3 : packet.subPackets()) {
         var3.handle(this);
      }
   }

   @Override
   public void handleMovePlayer(ClientboundPlayerPositionPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotPlayer var2 = this.player;
      if (var2 == null) {
         Vec3 var3 = packet.change().position();
         this.sendPacket(new ServerboundAcceptTeleportationPacket(packet.id(), var3.x, var3.y, var3.z, packet.change().yRot(), packet.change().xRot()));
      } else {
         if (!var2.isPassenger()) {
            setPosition(packet.change(), packet.relatives(), var2, false);
         }

         this.sendPacket(new ServerboundAcceptTeleportationPacket(packet.id(), var2.getX(), var2.getY(), var2.getZ(), var2.getYRot(), var2.getXRot()));
         this.sendPacket(new PosRot(var2.getX(), var2.getY(), var2.getZ(), var2.getYRot(), var2.getXRot(), false, false));
      }
   }

   private static boolean setPosition(PositionMoveRotation var0, Set<Relative> var1, Entity var2, boolean var3) {
      PositionMoveRotation var4 = PositionMoveRotation.of(var2);
      PositionMoveRotation var5 = PositionMoveRotation.calculateAbsolute(var4, var0, var1);
      boolean var6 = var4.position().distanceToSqr(var5.position()) > 4096.0;
      if (var3 && !var6) {
         var2.moveOrInterpolateTo(var5.position(), var5.yRot(), var5.xRot());
         var2.setDeltaMovement(var5.deltaMovement());
         return true;
      } else {
         var2.setPos(var5.position());
         var2.setDeltaMovement(var5.deltaMovement());
         var2.setYRot(var5.yRot());
         var2.setXRot(var5.xRot());
         PositionMoveRotation var7 = new PositionMoveRotation(var2.oldPosition(), Vec3.ZERO, var2.yRotO, var2.xRotO);
         PositionMoveRotation var8 = PositionMoveRotation.calculateAbsolute(var7, var0, var1);
         var2.setOldPosAndRot(var8.position(), var8.yRot(), var8.xRot());
         return false;
      }
   }

   @Override
   public void handleRotatePlayer(ClientboundPlayerRotationPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotPlayer var2 = this.player;
      if (var2 != null) {
         var var3 = Relative.rotation(packet.relativeY(), packet.relativeX());
         PositionMoveRotation var4 = PositionMoveRotation.of(var2);
         PositionMoveRotation var5 = PositionMoveRotation.calculateAbsolute(var4, var4.withRotation(packet.yRot(), packet.xRot()), var3);
         var2.setYRot(var5.yRot());
         var2.setXRot(var5.xRot());
         var2.setOldRot();
         this.sendPacket(new Rot(var2.getYRot(), var2.getXRot(), false, false));
      } else {
         this.sendPacket(new Rot(packet.yRot(), packet.xRot(), false, false));
      }
   }

   @Override
   public void handleMoveVehicle(ClientboundMoveVehiclePacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotPlayer var2 = this.player;
      if (var2 != null) {
         Entity var3 = var2.getRootVehicle();
         if (var3 != var2 && var3.isLocalInstanceAuthoritative()) {
            net.minecraft.core.PositionAndRotation var6 = packet.movingTo();
            Vec3 var4 = var6.position();
            Vec3 var5 = var3.getClientPosition();
            if (var4.distanceTo(var5) > 1.0E-5F) {
               if (var3.isInterpolating()) {
                  var3.getInterpolation().cancel();
               }

               var3.absSnapTo(var4.x(), var4.y(), var4.z(), var6.yRot(), var6.xRot());
            }

            this.connection.send(ServerboundMoveVehiclePacket.fromEntity(var3));
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
      BotWorld var2 = this.world;
      if (var2 != null) {
         int var3 = packet.x();
         int var4 = packet.z();
         ClientboundLevelChunkPacketData var5 = packet.chunkData();
         var2.getChunkSource().loadChunkFromPacket(var3, var4, var5);
         ClientboundLightUpdatePacketData var6 = packet.lightData();
         var2.enqueueChunkUpdate(() -> {
            this.readLightData(var3, var4, var6);
            LevelChunk var5x = var2.getChunkSource().getChunk(var3, var4, false);
            if (var5x != null) {
               this.markChunkSectionsReady(var2, var5x);
            }
         });
      }
   }

   private void markChunkSectionsReady(BotWorld var1, LevelChunk var2) {
      LevelLightEngine var3 = var1.getChunkSource().getLightEngine();
      LevelChunkSection[] var4 = var2.getSections();
      ChunkPos var5 = var2.getPos();

      for (int var6 = 0; var6 < var4.length; var6++) {
         LevelChunkSection var7 = var4[var6];
         int var8 = var1.getSectionYFromSectionIndex(var6);
         var3.updateSectionStatus(SectionPos.of(var5, var8), var7.hasOnlyAir());
      }
   }

   @Override
   public void handleChunksBiomes(ClientboundChunksBiomesPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld var2 = this.world;
      if (var2 != null) {
         for (ChunkBiomeData var4 : packet.chunkBiomeData()) {
            var2.getChunkSource().onChunkBiomeData(var4.pos().x(), var4.pos().z(), var4.getReadBuffer());
         }

         for (ChunkBiomeData var6 : packet.chunkBiomeData()) {
            var2.resetChunkColor(new ChunkPos(var6.pos().x(), var6.pos().z()));
         }
      }
   }

   @Override
   public void handleForgetLevelChunk(ClientboundForgetLevelChunkPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld var2 = this.world;
      if (var2 != null) {
         var2.getChunkSource().unload(packet.pos());
         ChunkPos var3 = packet.pos();
         var2.enqueueChunkUpdate(() -> {
            LevelLightEngine var2x = var2.getLightEngine();
            var2x.setLightEnabled(var3, false);

            for (int var3x = var2x.getMinLightSection(); var3x < var2x.getMaxLightSection(); var3x++) {
               SectionPos var4 = SectionPos.of(var3, var3x);
               var2x.queueSectionData(LightLayer.BLOCK, var4, null);
               var2x.queueSectionData(LightLayer.SKY, var4, null);
            }

            for (int var5 = var2.getMinSectionY(); var5 <= var2.getMaxSectionY(); var5++) {
               var2x.updateSectionStatus(SectionPos.of(var3, var5), true);
            }
         });
      }
   }

   @Override
   public void handleLightUpdatePacket(ClientboundLightUpdatePacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld var2 = this.world;
      if (var2 != null) {
         int var3 = packet.x();
         int var4 = packet.z();
         ClientboundLightUpdatePacketData var5 = packet.lightData();
         var2.enqueueChunkUpdate(() -> this.readLightData(var3, var4, var5));
      }
   }

   private void readLightData(int var1, int var2, ClientboundLightUpdatePacketData var3) {
      BotWorld var4 = this.world;
      if (var4 != null) {
         LevelLightEngine var5 = var4.getChunkSource().getLightEngine();
         BitSet var6 = var3.skyYMask();
         BitSet var7 = var3.emptySkyYMask();
         var var8 = var3.skyUpdates().iterator();
         this.updateLighting(var1, var2, var5, LightLayer.SKY, var6, var7, var8);
         BitSet var9 = var3.blockYMask();
         BitSet var10 = var3.emptyBlockYMask();
         var var11 = var3.blockUpdates().iterator();
         this.updateLighting(var1, var2, var5, LightLayer.BLOCK, var9, var10, var11);
         var5.setLightEnabled(new ChunkPos(var1, var2), true);
      }
   }

   private void updateLighting(int var1, int var2, LevelLightEngine var3, LightLayer var4, BitSet var5, BitSet var6, Iterator<byte[]> var7) {
      for (int var8 = 0; var8 < var3.getLightSectionCount(); var8++) {
         int var9 = var3.getMinLightSection() + var8;
         boolean var10 = var5.get(var8);
         boolean var11 = var6.get(var8);
         if (var10 || var11) {
            var3.queueSectionData(
               var4, SectionPos.of(var1, var9, var2), var10 ? new DataLayer((byte[])((byte[])var7.next()).clone()) : new DataLayer()
            );
         }
      }
   }

   @Override
   public void handleChunkBlocksUpdate(ClientboundSectionBlocksUpdatePacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld var2 = this.world;
      if (var2 != null) {
         packet.runUpdates((var1, var2x) -> var2.handleBlockUpdate(var1, var2x, 19));
      }
   }

   @Override
   public void handleBlockUpdate(ClientboundBlockUpdatePacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld var2 = this.world;
      if (var2 != null) {
         var2.handleBlockUpdate(packet.getPos(), packet.getBlockState(), 19);
      }
   }

   @Override
   public void handleSetChunkCacheRadius(ClientboundSetChunkCacheRadiusPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      this.chunkLoadDistance = packet.getRadius();
      BotWorld var2 = this.world;
      if (var2 != null) {
         var2.getChunkSource().updateLoadDistance(packet.getRadius());
      }
   }

   @Override
   public void handleSetSimulationDistance(ClientboundSetSimulationDistancePacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      this.simulationDistance = packet.simulationDistance();
      BotWorld var2 = this.world;
      if (var2 != null) {
         var2.setSimulationDistance(this.simulationDistance);
      }
   }

   @Override
   public void handleSetChunkCacheCenter(ClientboundSetChunkCacheCenterPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld var2 = this.world;
      if (var2 != null) {
         var2.getChunkSource().setChunkMapCenter(packet.getX(), packet.getZ());
      }
   }

   @Override
   public void handleBlockChangedAck(ClientboundBlockChangedAckPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld var2 = this.world;
      if (var2 != null) {
         var2.handlePlayerActionResponse(packet.sequence());
      }
   }

   @Override
   public void handleBlockEntityData(ClientboundBlockEntityDataPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld var2 = this.world;
      if (var2 != null) {
         var2.getBlockEntity(packet.getPos(), packet.getType()).ifPresent(var2x -> {
            CompoundTag var3 = packet.getTag();
            if (!var3.isEmpty()) {
               try (ScopedCollector var4 = new ScopedCollector(var2x.problemPath(), LOGGER)) {
                  var2x.loadWithComponents(TagValueInput.create(var4, this.registries, var3));
               }
            }
         });
      }
   }

   @Override
   public void handleBlockEvent(ClientboundBlockEventPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld var2 = this.world;
      if (var2 != null) {
         var2.blockEvent(packet.getPos(), packet.getBlock(), packet.getB0(), packet.getB1());
      }
   }

   @Override
   public void handleBlockDestruction(ClientboundBlockDestructionPacket packet) {
   }

   @Override
   public void handleLevelEvent(ClientboundLevelEventPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld var2 = this.world;
      if (var2 != null) {
         if (packet.isGlobalEvent()) {
            var2.globalLevelEvent(packet.getType(), packet.getPos(), packet.getData());
         } else {
            var2.levelEvent(packet.getType(), packet.getPos(), packet.getData());
         }
      }
   }

   @Override
   public void handleAddEntity(ClientboundAddEntityPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld var2 = this.world;
      if (var2 != null) {
         if (this.removedPlayerVehicleId.isPresent() && this.removedPlayerVehicleId.getAsInt() == packet.getId()) {
            this.removedPlayerVehicleId = OptionalInt.empty();
         }

         Entity var3 = this.createEntity(var2, packet);
         if (var3 != null) {
            var3.recreateFromPacket(packet);
            var2.addEntity(var3);
         } else {
            LOGGER.warn("Bot {}: skipping entity with id {}", this.client.name(), packet.getType());
         }
      }
   }

   @Nullable
   private Entity createEntity(BotWorld var1, ClientboundAddEntityPacket var2) {
      EntityType var3 = var2.getType();
      if (var3 == net.minecraft.world.entity.EntityTypes.PLAYER) {
         PlayerInfo var4 = this.getPlayerListEntry(var2.getUUID());
         if (var4 == null) {
            LOGGER.warn("Bot {}: server added player prior to sending player info (id {})", this.client.name(), var2.getUUID());
            return null;
         } else {
            return new BotRemotePlayer(var1, this, var4.getProfile());
         }
      } else {
         return var3.create(var1, EntitySpawnReason.LOAD);
      }
   }

   @Override
   public void handleSetEntityMotion(ClientboundSetEntityMotionPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld var2 = this.world;
      if (var2 != null) {
         Entity var3 = var2.getEntity(packet.id());
         if (var3 != null) {
            var3.lerpMotion(packet.movement());
         }
      }
   }

   @Override
   public void handleSetEntityData(ClientboundSetEntityDataPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld var2 = this.world;
      if (var2 != null) {
         Entity var3 = var2.getEntity(packet.id());
         if (var3 != null) {
            var3.getEntityData().assignValues(packet.packedItems());
         }
      }
   }

   @Override
   public void handleEntityPositionSync(ClientboundEntityPositionSyncPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld var2 = this.world;
      BotPlayer var3 = this.player;
      if (var2 != null && var3 != null) {
         Entity var4 = var2.getEntity(packet.id());
         if (var4 != null) {
            net.minecraft.world.entity.PositionPath var9 = packet.position();
            Vec3 var5 = var9.endPosition();
            var4.getPositionCodec().setBase(var5);
            if (!var4.isLocalInstanceAuthoritative()) {
               float var6 = packet.yRot();
               float var7 = packet.xRot();
               boolean var8 = var4.position().distanceToSqr(var5) > 4096.0;
               if (var2.hasEntity(var4) && !var8) {
                  var4.moveOrInterpolateTo(var9, var6, var7);
               } else {
                  var4.snapTo(var5.x, var5.y, var5.z, var6, var7);
                  if (var4.hasIndirectPassenger(var3)) {
                     var4.positionRider(var3);
                     var3.setOldPosAndRot();
                  }
               }

               var4.setOnGround(packet.onGround());
            }
         }
      }
   }

   @Override
   public void handleTeleportEntity(ClientboundTeleportEntityPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld var2 = this.world;
      BotPlayer var3 = this.player;
      if (var2 != null && var3 != null) {
         Entity var4 = var2.getEntity(packet.id());
         if (var4 == null) {
            if (this.removedPlayerVehicleId.isPresent() && this.removedPlayerVehicleId.getAsInt() == packet.id()) {
               LOGGER.debug("Bot {}: teleport for removed vehicle {}, applying to player", this.client.name(), packet.id());
               setPosition(packet.change(), packet.relatives(), var3, false);
               this.connection.send(new PosRot(var3.getX(), var3.getY(), var3.getZ(), var3.getYRot(), var3.getXRot(), false, false));
            }
         } else {
            boolean var5 = packet.relatives().contains(Relative.X)
               || packet.relatives().contains(Relative.Y)
               || packet.relatives().contains(Relative.Z);
            boolean var6 = var2.hasEntity(var4) || !var4.isLocalInstanceAuthoritative() || var5;
            boolean var7 = setPosition(packet.change(), packet.relatives(), var4, var6);
            var4.setOnGround(packet.onGround());
            if (!var7 && var4.hasIndirectPassenger(var3)) {
               var4.positionRider(var3);
               var3.setOldPosAndRot();
               if (var4.isLocalInstanceAuthoritative()) {
                  this.connection.send(ServerboundMoveVehiclePacket.fromEntity(var4));
               }
            }
         }
      }
   }

   @Override
   public void handleMoveEntity(ClientboundMoveEntityPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld var2 = this.world;
      if (var2 != null) {
         Entity var3 = packet.getEntity(var2);
         if (var3 != null) {
            if (var3.isLocalInstanceAuthoritative()) {
               VecDeltaCodec var4 = var3.getPositionCodec();
               if (packet.hasPosition()) {
                  var4.setBase(packet.getPositionDelta().decode(var4).endPosition());
               }
            } else {
               if (packet.hasPosition()) {
                  VecDeltaCodec var6 = var3.getPositionCodec();
                  net.minecraft.world.entity.PositionPath var7 = packet.getPositionDelta().decode(var6);
                  var6.setBase(var7.endPosition());
                  if (packet.hasRotation()) {
                     var3.moveOrInterpolateTo(var7, packet.getYRot(), packet.getXRot());
                  } else {
                     var3.moveOrInterpolateTo(var7);
                  }
               } else if (packet.hasRotation()) {
                  var3.moveOrInterpolateTo(packet.getYRot(), packet.getXRot());
               }

               var3.setOnGround(packet.isOnGround());
            }
         }
      }
   }

   @Override
   public void handleRotateMob(ClientboundRotateHeadPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld var2 = this.world;
      if (var2 != null) {
         Entity var3 = packet.getEntity(var2);
         if (var3 != null) {
            var3.lerpHeadTo(packet.getYHeadRot(), 3);
         }
      }
   }

   @Override
   public void handleRemoveEntities(ClientboundRemoveEntitiesPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld var2 = this.world;
      BotPlayer var3 = this.player;
      if (var2 != null) {
         packet.entityIds().forEach(var3x -> {
            Entity var4 = var2.getEntity(var3x);
            if (var4 != null) {
               if (var3 != null && var4.hasIndirectPassenger(var3)) {
                  this.removedPlayerVehicleId = OptionalInt.of(var3x);
               }

               var2.removeEntity(var3x, RemovalReason.DISCARDED);
            }
         });
      }
   }

   @Override
   public void handleAnimate(ClientboundAnimatePacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld var2 = this.world;
      if (var2 != null) {
         Entity var3 = var2.getEntity(packet.getId());
         if (var3 != null) {
            if (packet.getAction() == ClientboundAnimatePacket.WAKE_UP && var3 instanceof Player var4) {
               var4.stopSleepInBed(false, false);
            }
         }
      }
   }

   @Override
   public void handleSwingAnimation(net.minecraft.network.protocol.game.ClientboundSwingAnimationPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld var2 = this.world;
      if (var2 != null && var2.getEntity(packet.entityId()) instanceof LivingEntity var3) {
         var3.swing(packet.hand(), packet.animation(), false);
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
      BotWorld var2 = this.world;
      if (var2 != null) {
         Entity var3 = packet.getEntity(var2);
         if (var3 != null) {
            switch (packet.getEventId()) {
               case 21:
               case 35:
               case 63:
                  break;
               default:
                  var3.handleEntityEvent(packet.getEventId());
            }
         }
      }
   }

   @Override
   public void handleEntityLinkPacket(ClientboundSetEntityLinkPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld var2 = this.world;
      if (var2 != null && var2.getEntity(packet.getSourceId()) instanceof Leashable var3) {
         var3.setDelayedLeashHolderId(packet.getDestId());
      }
   }

   @Override
   public void handleSetEntityPassengersPacket(ClientboundSetPassengersPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld var2 = this.world;
      BotPlayer var3 = this.player;
      if (var2 != null) {
         Entity var4 = var2.getEntity(packet.getVehicle());
         if (var4 == null) {
            LOGGER.warn("Bot {}: received passengers for unknown entity", this.client.name());
         } else {
            var4.ejectPassengers();

            for (int var8 : packet.getPassengers()) {
               Entity var9 = var2.getEntity(var8);
               if (var9 != null) {
                  var9.startRiding(var4, true, false);
                  if (var9 == var3) {
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
      BotWorld var2 = this.world;
      if (var2 != null) {
         Entity var3 = var2.getEntity(packet.getEntityId());
         if (var3 != null) {
            if (!(var3 instanceof LivingEntity var4)) {
               throw new IllegalStateException("Server tried to update attributes of a non-living entity (actually: " + var3 + ")");
            } else {
               AttributeMap var5 = var4.getAttributes();

               for (AttributeSnapshot var7 : packet.getValues()) {
                  AttributeInstance var8 = var5.getInstance(var7.attribute());
                  if (var8 == null) {
                     LOGGER.warn("Bot {}: entity {} does not have attribute {}", new Object[]{this.client.name(), var3, var7.attribute().getRegisteredName()});
                  } else {
                     var8.setBaseValue(var7.base());
                     var8.removeModifiers();

                     for (AttributeModifier var10 : var7.modifiers()) {
                        var8.addTransientModifier(var10);
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
      BotWorld var2 = this.world;
      if (var2 != null) {
         if (var2.getEntity(packet.getEntityId()) instanceof LivingEntity var4) {
            Holder var5 = packet.getEffect();
            MobEffectInstance var6 = new MobEffectInstance(
               var5, packet.getEffectDurationTicks(), packet.getEffectAmplifier(), packet.isEffectAmbient(), packet.isEffectVisible(), packet.effectShowsIcon(), null
            );
            if (!packet.shouldBlend()) {
               var6.skipBlending();
            }

            var4.forceAddEffect(var6, null);
         }
      }
   }

   @Override
   public void handleRemoveMobEffect(ClientboundRemoveMobEffectPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld var2 = this.world;
      if (var2 != null && packet.getEntity(var2) instanceof LivingEntity var3) {
         var3.removeEffectNoUpdate(packet.effect());
      }
   }

   @Override
   public void handleSetEquipment(ClientboundSetEquipmentPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld var2 = this.world;
      if (var2 != null && var2.getEntity(packet.getEntity()) instanceof LivingEntity var3) {
         packet.getSlots().forEach(var1 -> var3.setItemSlot((EquipmentSlot)var1.getFirst(), (ItemStack)var1.getSecond()));
      }
   }

   @Override
   public void handleDamageEvent(ClientboundDamageEventPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld var2 = this.world;
      if (var2 != null) {
         Entity var3 = var2.getEntity(packet.entityId());
         if (var3 != null) {
            var3.handleDamageEvent(packet.getSource(var2));
         }
      }
   }

   @Override
   public void handleHurtAnimation(ClientboundHurtAnimationPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld var2 = this.world;
      if (var2 != null) {
         Entity var3 = var2.getEntity(packet.id());
         if (var3 != null) {
            var3.animateHurt(packet.yaw());
         }
      }
   }

   @Override
   public void handleMinecartAlongTrack(ClientboundMoveMinecartPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld var2 = this.world;
      if (var2 != null) {
         Entity var3 = packet.getEntity(var2);
         if (var3 instanceof AbstractMinecart var4
            && !var3.isLocalInstanceAuthoritative()
            && var4.getBehavior() instanceof NewMinecartBehavior var5) {
            var5.lerpSteps.addAll(packet.lerpSteps());
         }
      }
   }

   @Override
   public void handleProjectilePowerPacket(ClientboundProjectilePowerPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld var2 = this.world;
      if (var2 != null && var2.getEntity(packet.getId()) instanceof AbstractHurtingProjectile var3) {
         var3.accelerationPower = packet.getAccelerationPower();
      }
   }

   @Override
   public void handleTakeItemEntity(ClientboundTakeItemEntityPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld var2 = this.world;
      if (var2 != null) {
         Entity var3 = var2.getEntity(packet.getItemId());
         if (var3 != null) {
            if (var3 instanceof ItemEntity var4) {
               ItemStack var5 = var4.getItem();
               if (!var5.isEmpty()) {
                  var5.shrink(packet.getAmount());
               }

               if (var5.isEmpty()) {
                  var2.removeEntity(packet.getItemId(), RemovalReason.DISCARDED);
               }
            } else if (!(var3 instanceof ExperienceOrb)) {
               var2.removeEntity(packet.getItemId(), RemovalReason.DISCARDED);
            }
         }
      }
   }

   @Override
   public void handleExplosion(ClientboundExplodePacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotPlayer var2 = this.player;
      if (var2 != null) {
         packet.playerKnockback().ifPresent(var2::addDeltaMovement);
      }
   }

   @Override
   public void handleLookAt(ClientboundPlayerLookAtPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld var2 = this.world;
      BotPlayer var3 = this.player;
      if (var2 != null && var3 != null) {
         Vec3 var4 = packet.getPosition(var2);
         if (var4 != null) {
            var3.lookAt(packet.getFromAnchor(), var4);
         }
      }
   }

   @Override
   public void handleSetHealth(ClientboundSetHealthPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotPlayer var2 = this.player;
      if (var2 != null) {
         var2.updateHealth(packet.getHealth());
         var2.getFoodData().setFoodLevel(packet.getFood());
         var2.getFoodData().setSaturation(packet.getSaturation());
      }
   }

   @Override
   public void handleSetExperience(ClientboundSetExperiencePacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotPlayer var2 = this.player;
      if (var2 != null) {
         var2.setExperience(packet.getExperienceProgress(), packet.getTotalExperience(), packet.getExperienceLevel());
      }
   }

   @Override
   public void handleSetHeldSlot(ClientboundSetHeldSlotPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotPlayer var2 = this.player;
      if (var2 != null && Inventory.isHotbarSlot(packet.slot())) {
         var2.getInventory().setSelectedSlot(packet.slot());
      }
   }

   @Override
   public void handleContainerSetSlot(ClientboundContainerSetSlotPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotPlayer var2 = this.player;
      if (var2 != null) {
         ItemStack var3 = packet.getItem();
         int var4 = packet.getSlot();
         if (packet.getContainerId() == 0) {
            var2.inventoryMenu.setItem(var4, packet.getStateId(), var3);
         } else if (packet.getContainerId() == var2.containerMenu.containerId) {
            var2.containerMenu.setItem(var4, packet.getStateId(), var3);
         }
      }
   }

   @Override
   public void handleSetCursorItem(ClientboundSetCursorItemPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotPlayer var2 = this.player;
      if (var2 != null) {
         var2.containerMenu.setCarried(packet.contents());
      }
   }

   @Override
   public void handleSetPlayerInventory(ClientboundSetPlayerInventoryPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotPlayer var2 = this.player;
      if (var2 != null) {
         var2.getInventory().setItem(packet.slot(), packet.contents());
      }
   }

   @Override
   public void handleContainerContent(ClientboundContainerSetContentPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotPlayer var2 = this.player;
      if (var2 != null) {
         if (packet.containerId() == 0) {
            var2.inventoryMenu.initializeContents(packet.stateId(), packet.items(), packet.carriedItem());
         } else if (packet.containerId() == var2.containerMenu.containerId) {
            var2.containerMenu.initializeContents(packet.stateId(), packet.items(), packet.carriedItem());
         }
      }
   }

   @Override
   public void handleContainerSetData(ClientboundContainerSetDataPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotPlayer var2 = this.player;
      if (var2 != null && var2.containerMenu.containerId == packet.getContainerId()) {
         var2.containerMenu.setData(packet.getId(), packet.getValue());
      }
   }

   @Override
   public void handleOpenScreen(ClientboundOpenScreenPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotPlayer var2 = this.player;
      if (var2 != null) {
         AbstractContainerMenu var3 = packet.getType().create(packet.getContainerId(), var2.getInventory());
         var2.containerMenu = var3;
         this.currentScreenTitle = packet.getTitle();
         this.currentScreenSyncId = packet.getContainerId();
      }
   }

   @Override
   public void handleContainerClose(ClientboundContainerClosePacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotPlayer var2 = this.player;
      if (var2 != null) {
         var2.closeScreen();
      }
   }

   @Override
   public void handleMerchantOffers(ClientboundMerchantOffersPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotPlayer var2 = this.player;
      if (var2 != null) {
         AbstractContainerMenu var3 = var2.containerMenu;
         if (packet.getContainerId() == var3.containerId && var3 instanceof MerchantMenu var4) {
            var4.setOffers(packet.getOffers());
            var4.setXp(packet.getVillagerXp());
            var4.setMerchantLevel(packet.getVillagerLevel());
            var4.setShowProgressBar(packet.showProgress());
            var4.setCanRestock(packet.canRestock());
         }
      }
   }

   @Override
   public void handleItemCooldown(ClientboundCooldownPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotPlayer var2 = this.player;
      if (var2 != null) {
         if (packet.duration() == 0) {
            var2.getCooldowns().removeCooldown(packet.cooldownGroup());
         } else {
            var2.getCooldowns().addCooldown(packet.cooldownGroup(), packet.duration());
         }
      }
   }

   @Override
   public void handlePlayerAbilities(ClientboundPlayerAbilitiesPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotPlayer var2 = this.player;
      if (var2 != null) {
         var2.getAbilities().flying = packet.isFlying();
         var2.getAbilities().instabuild = packet.canInstabuild();
         var2.getAbilities().invulnerable = packet.isInvulnerable();
         var2.getAbilities().mayfly = packet.canFly();
         var2.getAbilities().setFlyingSpeed(packet.getFlyingSpeed());
         var2.getAbilities().setWalkingSpeed(packet.getWalkingSpeed());
      }
   }

   @Override
   public void handlePlayerCombatKill(ClientboundPlayerCombatKillPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld var2 = this.world;
      BotPlayer var3 = this.player;
      if (var2 != null && var3 != null) {
         Entity var4 = var2.getEntity(packet.playerId());
         if (var4 == var3) {
            var3.requestRespawn();
         }
      }
   }

   @Override
   public void handleGameEvent(ClientboundGameEventPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld var2 = this.world;
      Type var3 = packet.getEvent();
      float var4 = packet.getParam();
      int var5 = Mth.floor(var4 + 0.5F);
      if (var3 == ClientboundGameEventPacket.START_RAINING && var2 != null) {
         var2.setRainLevel(0.0F);
      } else if (var3 == ClientboundGameEventPacket.STOP_RAINING && var2 != null) {
         var2.setRainLevel(1.0F);
      } else if (var3 == ClientboundGameEventPacket.CHANGE_GAME_MODE) {
         BotInteractionManager var6 = this.interactionManager;
         if (var6 != null) {
            var6.setGameMode(GameType.byId(var5));
         }
      } else if (var3 == ClientboundGameEventPacket.WIN_GAME) {
         this.sendPacket(new ServerboundClientCommandPacket(Action.PERFORM_RESPAWN));
      } else if (var3 == ClientboundGameEventPacket.RAIN_LEVEL_CHANGE && var2 != null) {
         var2.setRainLevel(var4);
      } else if (var3 == ClientboundGameEventPacket.THUNDER_LEVEL_CHANGE && var2 != null) {
         var2.setThunderLevel(var4);
      } else if (var3 == ClientboundGameEventPacket.LEVEL_CHUNKS_LOAD_START) {
         this.initialChunksComing = true;
      }
   }

   @Override
   public void handleLowDiskSpaceWarning(net.minecraft.network.protocol.game.ClientboundLowDiskSpaceWarningPacket packet) {
   }

   @Override
   public void handleSetTime(ClientboundSetTimePacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld var2 = this.world;
      if (var2 != null) {
         var2.setTime(packet.gameTime());
         this.clockManager.handleUpdates(packet.gameTime(), packet.clockUpdates());
      }
   }

   @Override
   public void handleSetSpawn(ClientboundSetDefaultSpawnPositionPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld var2 = this.world;
      if (var2 != null) {
         var2.setRespawnData(packet.respawnData());
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
      BotWorld var2 = this.world;
      if (var2 != null) {
         WorldBorder var3 = var2.getWorldBorder();
         var3.setCenter(packet.getNewCenterX(), packet.getNewCenterZ());
         long var4 = packet.getLerpTime();
         if (var4 > 0L) {
            var3.lerpSizeBetween(packet.getOldSize(), packet.getNewSize(), var4, 0L);
         } else {
            var3.setSize(packet.getNewSize());
         }

         var3.setAbsoluteMaxSize(packet.getNewAbsoluteMaxSize());
         var3.setWarningBlocks(packet.getWarningBlocks());
         var3.setWarningTime(packet.getWarningTime());
      }
   }

   @Override
   public void handleSetBorderCenter(ClientboundSetBorderCenterPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld var2 = this.world;
      if (var2 != null) {
         var2.getWorldBorder().setCenter(packet.getNewCenterX(), packet.getNewCenterZ());
      }
   }

   @Override
   public void handleSetBorderLerpSize(ClientboundSetBorderLerpSizePacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld var2 = this.world;
      if (var2 != null) {
         var2.getWorldBorder().lerpSizeBetween(packet.getOldSize(), packet.getNewSize(), packet.getLerpTime(), 0L);
      }
   }

   @Override
   public void handleSetBorderSize(ClientboundSetBorderSizePacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld var2 = this.world;
      if (var2 != null) {
         var2.getWorldBorder().setSize(packet.getSize());
      }
   }

   @Override
   public void handleSetBorderWarningDistance(ClientboundSetBorderWarningDistancePacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld var2 = this.world;
      if (var2 != null) {
         var2.getWorldBorder().setWarningBlocks(packet.getWarningBlocks());
      }
   }

   @Override
   public void handleSetBorderWarningDelay(ClientboundSetBorderWarningDelayPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld var2 = this.world;
      if (var2 != null) {
         var2.getWorldBorder().setWarningTime(packet.getWarningDelay());
      }
   }

   @Override
   public void handleMapItemData(ClientboundMapItemDataPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld var2 = this.world;
      if (var2 != null) {
         MapId var3 = packet.mapId();
         MapItemSavedData var4 = var2.getMapData(var3);
         if (var4 == null) {
            var4 = MapItemSavedData.createForClient(packet.scale(), packet.locked(), var2.dimension());
            var2.putClientsideMapState(var3, var4);
         }

         packet.applyToMap(var4);
      }
   }

   @Override
   public void handleTickingState(ClientboundTickingStatePacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld var2 = this.world;
      if (var2 != null) {
         TickRateManager var3 = var2.tickRateManager();
         var3.setTickRate(packet.tickRate());
         var3.setFrozen(packet.isFrozen());
      }
   }

   @Override
   public void handleTickingStep(ClientboundTickingStepPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld var2 = this.world;
      if (var2 != null) {
         var2.tickRateManager().setFrozenTicksToRun(packet.tickSteps());
      }
   }

   @Override
   public void handlePlayerInfoUpdate(ClientboundPlayerInfoUpdatePacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());

      for (net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket.Entry var3 : packet.newEntries()) {
         BotPlayerListEntry var4 = new BotPlayerListEntry(Objects.requireNonNull(var3.profile()), false);
         this.playerListEntries.putIfAbsent(var3.profileId(), var4);
      }

      for (net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket.Entry var9 : packet.entries()) {
         PlayerInfo var10 = this.playerListEntries.get(var9.profileId());
         if (var10 instanceof BotPlayerListEntry var5) {
            for (net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket.Action var7 : packet.actions()) {
               this.handlePlayerListAction(var7, var9, var5);
            }
         } else {
            LOGGER.warn(
               "Bot {}: ignoring player info update for unknown player {} ({})", new Object[]{this.client.name(), var9.profileId(), packet.actions()}
            );
         }
      }
   }

   private void handlePlayerListAction(net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket.Action var1, net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket.Entry var2, BotPlayerListEntry var3) {
      BotPlayer var4 = this.player;
      switch (var1) {
         case INITIALIZE_CHAT:
            var3.resetBotSession(false);
            break;
         case UPDATE_GAME_MODE:
            if (var3.getGameMode() != var2.gameMode() && var4 != null && var4.getUUID().equals(var2.profileId())) {
               var4.onGameModeChanged(var2.gameMode());
            }

            var3.setBotGameMode(var2.gameMode());
            break;
         case UPDATE_LATENCY:
            var3.setBotLatency(var2.latency());
            break;
         case UPDATE_DISPLAY_NAME:
            var3.setTabListDisplayName(var2.displayName());
            break;
         case UPDATE_HAT:
            var3.setShowHat(var2.showHat());
            break;
         case UPDATE_LIST_ORDER:
            var3.setTabListOrder(var2.listOrder());
         case UPDATE_LISTED:
      }
   }

   @Override
   public void handlePlayerInfoRemove(ClientboundPlayerInfoRemovePacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());

      for (UUID var3 : packet.profileIds()) {
         this.playerListEntries.remove(var3);
      }
   }

   @Override
   public void handleAddObjective(ClientboundSetObjectivePacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      String var2 = packet.getObjectiveName();
      synchronized (this.scoreboard) {
         if (packet.getMethod() == 0) {
            this.scoreboard
               .addObjective(var2, ObjectiveCriteria.DUMMY, packet.getDisplayName(), packet.getRenderType(), false, packet.getNumberFormat().orElse(null));
         } else {
            Objective var4 = this.scoreboard.getObjective(var2);
            if (var4 != null) {
               if (packet.getMethod() == 1) {
                  this.scoreboard.removeObjective(var4);
               } else if (packet.getMethod() == 2) {
                  var4.setRenderType(packet.getRenderType());
                  var4.setDisplayName(packet.getDisplayName());
                  var4.setNumberFormat(packet.getNumberFormat().orElse(null));
               }
            }
         }
      }
   }

   @Override
   public void handleSetScore(ClientboundSetScorePacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      String var2 = packet.objectiveName();
      ScoreHolder var3 = ScoreHolder.forNameOnly(packet.owner());
      synchronized (this.scoreboard) {
         Objective var5 = this.scoreboard.getObjective(var2);
         if (var5 != null) {
            ScoreAccess var6 = this.scoreboard.getOrCreatePlayerScore(var3, var5, true);
            var6.set(packet.score());
            var6.display(packet.display().orElse(null));
            var6.numberFormatOverride(packet.numberFormat().orElse(null));
            return;
         }
      }

      LOGGER.warn("Bot {}: received packet for unknown scoreboard objective: {}", this.client.name(), var2);
   }

   @Override
   public void handleResetScore(ClientboundResetScorePacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      String var2 = packet.objectiveName();
      ScoreHolder var3 = ScoreHolder.forNameOnly(packet.owner());
      synchronized (this.scoreboard) {
         if (var2 == null) {
            this.scoreboard.resetAllPlayerScores(var3);
         } else {
            Objective var5 = this.scoreboard.getObjective(var2);
            if (var5 != null) {
               this.scoreboard.resetSinglePlayerScore(var3, var5);
            }
         }
      }
   }

   @Override
   public void handleSetDisplayObjective(ClientboundSetDisplayObjectivePacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      String var2 = packet.getObjectiveName();
      synchronized (this.scoreboard) {
         Objective var4 = var2 == null ? null : this.scoreboard.getObjective(var2);
         this.scoreboard.setDisplayObjective(packet.getSlot(), var4);
      }
   }

   @Override
   public void handleSetPlayerTeamPacket(ClientboundSetPlayerTeamPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      synchronized (this.scoreboard) {
         this.applyTeam(packet);
      }
   }

   private void applyTeam(ClientboundSetPlayerTeamPacket var1) {
      net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket.Action var2 = var1.getTeamAction();
      PlayerTeam var3;
      if (var2 == net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket.Action.ADD) {
         var3 = this.scoreboard.addPlayerTeam(var1.getName());
      } else {
         var3 = this.scoreboard.getPlayerTeam(var1.getName());
         if (var3 == null) {
            LOGGER.warn("Bot {}: received packet for unknown team {}", this.client.name(), var1.getName());
            return;
         }
      }

      var1.getParameters().ifPresent(var1x -> {
         var3.setDisplayName(var1x.displayName());
         var3.setColor(var1x.color());
         var3.unpackOptions(var1x.options());
         var3.setNameTagVisibility(var1x.nameTagVisibility());
         var3.setCollisionRule(var1x.collisionRule());
         var3.setPlayerPrefix(var1x.playerPrefix());
         var3.setPlayerSuffix(var1x.playerSuffix());
      });
      net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket.Action var4 = var1.getPlayerAction();
      if (var4 == net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket.Action.ADD) {
         for (String var6 : var1.getPlayers()) {
            this.scoreboard.addPlayerToTeam(var6, var3);
         }
      } else if (var4 == net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket.Action.REMOVE) {
         for (String var8 : var1.getPlayers()) {
            this.scoreboard.removePlayerFromTeam(var8, var3);
         }
      }

      if (var2 == net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket.Action.REMOVE) {
         this.scoreboard.removePlayerTeam(var3);
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
      var var2 = new ArrayList<>(packet.tags().size());
      packet.tags().forEach((var2x, var3) -> {
         if (RegistrySynchronization.isNetworkable((ResourceKey<? extends Registry<?>>)var2x)) {
            var2.add(this.startTagReload((ResourceKey<? extends Registry<?>>)var2x, var3));
         }
      });
      var2.forEach(x -> ((PendingTags)x).apply());
   }

   private <T> PendingTags<T> startTagReload(
      ResourceKey<? extends Registry<? extends T>> var1, net.minecraft.tags.TagNetworkSerialization.NetworkPayload var2
   ) {
      Registry var3 = this.registries.lookupOrThrow(var1);
      return var3.prepareTagReload(var2.resolve(var3));
   }

   @Override
   public void handleSystemChat(ClientboundSystemChatPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      this.client.onChat(packet.content());
   }

   @Override
   public void handlePlayerChat(ClientboundPlayerChatPacket packet) {
      PacketUtils.ensureRunningOnSameThread(packet, this, this.client.getPacketApplyBatcher());
      Object var2 = packet.unsignedContent() != null ? packet.unsignedContent() : Component.literal(packet.body().content());
      this.client.onChat(packet.chatType().decorate((Component)var2));
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
