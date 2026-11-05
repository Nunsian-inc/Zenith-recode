package org.zenith.base.bot.world;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BooleanSupplier;
import net.minecraft.client.multiplayer.ClientLevel.ClientLevelData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.core.particles.ExplosionParticleInfo;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.TickRateManager;
import net.minecraft.world.attribute.EnvironmentAttributeSystem;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.EnderDragonPart;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.crafting.RecipeAccess;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.entity.EntityTickList;
import net.minecraft.world.level.entity.LevelCallback;
import net.minecraft.world.level.entity.LevelEntityGetter;
import net.minecraft.world.level.entity.TransientEntitySectionManager;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.storage.LevelData.RespawnData;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.ticks.BlackholeTickAccess;
import net.minecraft.world.ticks.LevelTickAccess;
import org.jetbrains.annotations.Nullable;
import org.zenith.base.bot.net.BotPlayHandler;

public final class BotWorld extends Level implements net.minecraft.client.renderer.block.BlockAndTintGetter {
   public final EntityTickList entityList = new EntityTickList();
   private final TransientEntitySectionManager<Entity> entityManager = new TransientEntitySectionManager<>(Entity.class, new BotWorld.BotEntityHandler());
   private final BotPlayHandler networkHandler;
   private final ClientLevelData clientWorldProperties;
   private final TickRateManager tickManager;
   public final List<Player> players = Lists.newArrayList();
   public final List<EnderDragonPart> enderDragonParts = Lists.newArrayList();
   private final Map<MapId, MapItemSavedData> mapStates = new ConcurrentHashMap<>();
   private final BotChunkManager chunkManager;
   private final EnvironmentAttributeSystem environmentAttributes = EnvironmentAttributeSystem.builder().addDefaultLayers(this).build();
   private final Deque<Runnable> chunkUpdaters = Queues.newArrayDeque();
   private int simulationDistance;
   private final BotPendingUpdateManager pendingUpdateManager = new BotPendingUpdateManager();
   private final int seaLevel;
   @Nullable
   private BotPlayer botPlayer;
   private static final BotWorld.RenderListener[] NO_LISTENERS = new BotWorld.RenderListener[0];
   private volatile BotWorld.RenderListener[] renderListeners = NO_LISTENERS;
   private volatile int entitySetVersion;
   private final WorldBorder zenithWorldBorder = new WorldBorder();

   public BotWorld(
      BotPlayHandler networkHandler, ClientLevelData clientWorldProperties, ResourceKey<Level> dimension, Holder<DimensionType> dimensionType, int chunkLoadDistance, int simulationDistance, boolean debug, long seed, int seaLevel
   ) {
      super(clientWorldProperties, dimension, networkHandler.getRegistryManager(), dimensionType, true, debug, seed, 1000000);
      this.networkHandler = networkHandler;
      this.chunkManager = new BotChunkManager(this, chunkLoadDistance);
      this.tickManager = new TickRateManager();
      this.clientWorldProperties = clientWorldProperties;
      this.seaLevel = seaLevel;
      this.setSpawnPos(new BlockPos(8, 64, 8), 0.0F);
      this.simulationDistance = simulationDistance;
      this.updateSkyBrightness();
   }

   public synchronized void addRenderListener(BotWorld.RenderListener renderListener) {
      BotWorld.RenderListener[] renderListeners = this.renderListeners;

      for (BotWorld.RenderListener renderListener2 : renderListeners) {
         if (renderListener2 == renderListener) {
            return;
         }
      }

      BotWorld.RenderListener[] renderListeners2 = Arrays.copyOf(renderListeners, renderListeners.length + 1);
      renderListeners2[renderListeners.length] = renderListener;
      this.renderListeners = renderListeners2;
   }

   public synchronized void removeRenderListener(BotWorld.RenderListener renderListener) {
      BotWorld.RenderListener[] renderListeners = this.renderListeners;

      for (int i = 0; i < renderListeners.length; i++) {
         if (renderListeners[i] == renderListener) {
            if (renderListeners.length == 1) {
               this.renderListeners = NO_LISTENERS;
            } else {
               BotWorld.RenderListener[] renderListeners2 = new BotWorld.RenderListener[renderListeners.length - 1];
               System.arraycopy(renderListeners, 0, renderListeners2, 0, i);
               System.arraycopy(renderListeners, i + 1, renderListeners2, i, renderListeners.length - i - 1);
               this.renderListeners = renderListeners2;
            }

            return;
         }
      }
   }

   public BotWorld.RenderListener[] getRenderListeners() {
      return this.renderListeners;
   }

   public int getEntitySetVersion() {
      return this.entitySetVersion;
   }

   public void setBotPlayer(@Nullable BotPlayer botPlayer) {
      this.botPlayer = botPlayer;
   }

   @Nullable
   public BotPlayer getBotPlayer() {
      return this.botPlayer;
   }

   public void handlePlayerActionResponse(int sequence) {
      this.pendingUpdateManager.processPendingUpdates(sequence, this);
   }

   public void handleBlockUpdate(BlockPos pos, BlockState blockState, int updateFlags) {
      if (!this.pendingUpdateManager.hasPendingUpdate(pos, blockState)) {
         super.setBlock(pos, blockState, updateFlags, 512);
      }
   }

   public void processPendingUpdate(BlockPos pos, BlockState blockState, Vec3 playerPos) {
      BlockState blockState2 = this.getBlockState(pos);
      if (blockState2 != blockState) {
         this.setBlock(pos, blockState, 19);
         BotPlayer botPlayer = this.botPlayer;
         if (botPlayer != null && this == botPlayer.level() && botPlayer.isColliding(pos, blockState)) {
            botPlayer.absSnapTo(playerPos.x, playerPos.y, playerPos.z);
         }
      }
   }

   public BotPendingUpdateManager getPendingUpdateManager() {
      return this.pendingUpdateManager;
   }

   @Override
   public boolean setBlock(BlockPos pos, BlockState state, int flags, int maxUpdateDepth) {
      if (this.pendingUpdateManager.hasPendingSequence() && this.botPlayer != null) {
         BlockState blockState = this.getBlockState(pos);
         boolean setBlock = super.setBlock(pos, state, flags, maxUpdateDepth);
         if (setBlock) {
            this.pendingUpdateManager.addPendingUpdate(pos, blockState, this.botPlayer);
         }

         return setBlock;
      } else {
         return super.setBlock(pos, state, flags, maxUpdateDepth);
      }
   }

   public void enqueueChunkUpdate(Runnable runnable) {
      this.chunkUpdaters.add(runnable);
   }

   public void runQueuedChunkUpdates() {
      int size = this.chunkUpdaters.size();
      int n = size < 1000 ? Math.max(10, size / 10) : size;

      for (int i = 0; i < n; i++) {
         Runnable chunkUpdater = this.chunkUpdaters.poll();
         if (chunkUpdater == null) {
            break;
         }

         chunkUpdater.run();
      }
   }

   public void tick(BooleanSupplier shouldKeepTicking) {
      this.getWorldBorder().tick();
      this.updateSkyBrightness();
      if (this.tickRateManager().runsNormally()) {
         this.tickTime();
      }

      this.chunkManager.tick(shouldKeepTicking, true);
   }

   private void tickTime() {
      this.clientWorldProperties.setGameTime(this.clientWorldProperties.getGameTime() + 1L);
      this.clockManager().tick(this.clientWorldProperties.getGameTime());
   }

   // 26.x: time of day lives in world clocks (ClientClockManager on the handler), the packet only sets game time here
   public void setTime(long time) {
      this.clientWorldProperties.setGameTime(time);
   }

   @Override
   public net.minecraft.client.ClientClockManager clockManager() {
      return this.networkHandler.clockManager();
   }

   public Iterable<Entity> entitiesForRendering() {
      return this.getEntities().getAll();
   }

   public void tickEntities() {
      this.entityList.forEach(entity -> {
         if (!entity.isRemoved() && !entity.isPassenger() && !this.tickManager.isEntityFrozen(entity)) {
            this.guardEntityTick(this::tickEntity, entity);
         }
      });
      this.tickBlockEntities();
   }

   public boolean hasEntity(Entity entity) {
      return this.entityList.contains(entity);
   }

   @Override
   public boolean shouldTickDeath(Entity entity) {
      BotPlayer botPlayer = this.botPlayer;
      return botPlayer != null && entity.chunkPosition().getChessboardDistance(botPlayer.chunkPosition()) <= this.simulationDistance;
   }

   public void tickEntity(Entity passenger) {
      passenger.setOldPosAndRot();
      passenger.tickCount++;
      passenger.tick();

      for (Entity entity : passenger.getPassengers()) {
         this.tickPassenger(passenger, entity);
      }
   }

   private void tickPassenger(Entity passenger, Entity entity) {
      if (!entity.isRemoved() && entity.getVehicle() == passenger) {
         if (entity instanceof Player || this.entityList.contains(entity)) {
            entity.setOldPosAndRot();
            entity.tickCount++;
            entity.rideTick();

            for (Entity passenger2 : entity.getPassengers()) {
               this.tickPassenger(entity, passenger2);
            }
         }
      } else {
         entity.stopRiding();
      }
   }

   public void unloadBlockEntities(LevelChunk andSet) {
      andSet.clearAllBlockEntities();
      this.chunkManager.getLightEngine().setLightEnabled(andSet.getPos(), false);
      this.entityManager.stopTicking(andSet.getPos());
   }

   public void resetChunkColor(ChunkPos chunkPos) {
      this.entityManager.startTicking(chunkPos);

      for (BotWorld.RenderListener renderListener : this.renderListeners) {
         renderListener.onChunkChanged(chunkPos.x(), chunkPos.z());
      }
   }

   public void onChunkUnload(long sectionNode) {
      for (BotWorld.RenderListener renderListener : this.renderListeners) {
         renderListener.onSectionChanged(SectionPos.x(sectionNode), SectionPos.y(sectionNode), SectionPos.z(sectionNode));
      }
   }

   @Override
   public boolean hasChunk(int chunkX, int chunkZ) {
      return true;
   }

   public int getRegularEntityCount() {
      return this.entityManager.count();
   }

   public void addEntity(Entity botPlayer) {
      this.removeEntity(botPlayer.getId(), RemovalReason.DISCARDED);
      this.entityManager.addEntity(botPlayer);
   }

   public void removeEntity(int itemId, RemovalReason reason) {
      Entity entity = this.getEntities().get(itemId);
      if (entity != null) {
         entity.setRemoved(reason);
         entity.onClientRemoval();
      }
   }

   @Nullable
   @Override
   public Entity getEntity(int id) {
      return this.getEntities().get(id);
   }

   public void disconnect() {
      this.networkHandler.getConnection().disconnect(Component.translatable("multiplayer.status.quitting"));
   }

   @Override
   public void playSeededSound(
      @Nullable Entity source, double x, double y, double z, Holder<SoundEvent> sound, SoundSource category, float volume, float pitch, long seed
   ) {
   }

   @Override
   public void playSeededSound(
      @Nullable Entity source, Entity entity, Holder<SoundEvent> sound, SoundSource category, float volume, float pitch, long seed
   ) {
   }

   public void playSoundFromEntity(Entity entity, SoundEvent event, SoundSource soundSource, float f, float f2) {
   }

   public void playSound(double d, double d2, double d3, SoundEvent event, SoundSource soundSource, float f, float f2, boolean flag) {
   }

   @Override
   public void addParticle(ParticleOptions parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
   }

   @Override
   public void addParticle(
      ParticleOptions parameters, boolean force, boolean canSpawnOnMinimal, double x, double y, double z, double velocityX, double velocityY, double velocityZ
   ) {
   }

   public void addImportantParticle(ParticleOptions particleOptions, double d, double d2, double d3, double d4, double d5, double d6) {
   }

   public void addImportantParticle(ParticleOptions particleOptions, boolean flag, double d, double d2, double d3, double d4, double d5, double d6) {
   }

   @Override
   public void sendBlockUpdated(BlockPos pos, BlockState oldState, BlockState newState, int flags) {
      for (BotWorld.RenderListener renderListener : this.renderListeners) {
         renderListener.onBlockChanged(pos);
      }
   }

   @Override
   public void destroyBlockProgress(int entityId, BlockPos pos, int progress) {
   }

   @Override
   public void globalLevelEvent(int eventId, BlockPos pos, int data) {
   }

   @Override
   public void levelEvent(@Nullable Entity source, int eventId, BlockPos pos, int data) {
   }

   @Override
   public void addDestroyBlockEffect(BlockPos pos, BlockState state) {
   }

   @Override
   public void gameEvent(Holder<GameEvent> event, Vec3 emitterPos, Context emitter) {
   }

   @Override
   public void explode(
      @Nullable Entity entity,
      @Nullable DamageSource damageSource,
      @Nullable ExplosionDamageCalculator behavior,
      double x,
      double y,
      double z,
      float power,
      boolean createFire,
      ExplosionInteraction explosionSourceType,
      ParticleOptions smallParticle,
      ParticleOptions largeParticle,
      WeightedList<ExplosionParticleInfo> blockParticles,
      Holder<SoundEvent> soundEvent
   ) {
   }

   @Override
   public void sendPacketToServer(Packet<?> packet) {
      this.networkHandler.sendPacket(packet);
   }

   @Override
   public RecipeAccess recipeAccess() {
      return this.networkHandler.getRecipeManager();
   }

   @Override
   public TickRateManager tickRateManager() {
      return this.tickManager;
   }

   @Override
   public LevelTickAccess<Block> getBlockTicks() {
      return BlackholeTickAccess.emptyLevelList();
   }

   @Override
   public LevelTickAccess<Fluid> getFluidTicks() {
      return BlackholeTickAccess.emptyLevelList();
   }

   public BotChunkManager getChunkSource() {
      return this.chunkManager;
   }

   @Nullable
   @Override
   public MapItemSavedData getMapData(MapId id) {
      return this.mapStates.get(id);
   }

   public void putClientsideMapState(MapId mapId, MapItemSavedData mapData) {
      this.mapStates.put(mapId, mapData);
   }

   public void putMapState(MapId mapId, MapItemSavedData mapItemSavedData) {
   }

   public MapId increaseAndGetMapId() {
      return new MapId(0);
   }

   @Override
   public Scoreboard getScoreboard() {
      return this.networkHandler.getScoreboard();
   }

   @Override
   public List<Player> players() {
      return this.players;
   }

   public List<EnderDragonPart> dragonParts() {
      return this.enderDragonParts;
   }

   @Override
   public Holder<Biome> getUncachedNoiseBiome(int biomeX, int biomeY, int biomeZ) {
      return this.registryAccess().lookupOrThrow(Registries.BIOME).getOrThrow(Biomes.PLAINS);
   }

   // 26.x replaced getShade with per-dimension cardinal lighting
   @Override
   public net.minecraft.world.level.CardinalLighting cardinalLighting() {
      return this.dimensionType().cardinalLightType().get();
   }

   @Override
   public int getBlockTint(BlockPos pos, ColorResolver colorResolver) {
      return colorResolver.getColor(this.getBiome(pos).value(), pos.getX(), pos.getZ());
   }

   public void setSpawnPos(BlockPos pos, float yaw) {
      this.setRespawnData(new RespawnData(GlobalPos.of(this.dimension(), pos), yaw, 0.0F));
   }

   @Override
   public void setRespawnData(RespawnData spawnPoint) {
      this.levelData.setSpawn(spawnPoint);
   }

   @Override
   public RespawnData getRespawnData() {
      return this.levelData.getRespawnData();
   }

   @Override
   public EnvironmentAttributeSystem environmentAttributes() {
      return this.environmentAttributes;
   }

   @Override
   public String toString() {
      return "BotLevel";
   }

   public ClientLevelData getLevelData() {
      return this.clientWorldProperties;
   }

   public Map<MapId, MapItemSavedData> getMapStates() {
      return Map.copyOf(this.mapStates);
   }

   public void putMapStates(Map<MapId, MapItemSavedData> mapStates) {
      this.mapStates.putAll(mapStates);
   }

   @Override
   public LevelEntityGetter<Entity> getEntities() {
      return this.entityManager.getEntityGetter();
   }

   @Override
   public String gatherChunkSourceStats() {
      return "Chunks[Bot] W: " + this.chunkManager.gatherStats() + " E: " + this.entityManager.gatherStats();
   }

   public void setSimulationDistance(int simulationDistance) {
      this.simulationDistance = simulationDistance;
   }

   public int getSimulationDistance() {
      return this.simulationDistance;
   }

   @Override
   public FeatureFlagSet enabledFeatures() {
      return this.networkHandler.getEnabledFeatures();
   }

   @Override
   public int getSeaLevel() {
      return this.seaLevel;
   }

   @Override
   public WorldBorder getWorldBorder() {
      return this.zenithWorldBorder;
   }

   private final class BotEntityHandler implements LevelCallback<Entity> {
      public void onCreated(Entity entity) {
         BotWorld.this.entitySetVersion++;
      }

      public void onDestroyed(Entity entity) {
         BotWorld.this.entitySetVersion++;
      }

      public void onTickingStart(Entity entity) {
         BotWorld.this.entityList.add(entity);
      }

      public void onTickingEnd(Entity entity) {
         BotWorld.this.entityList.remove(entity);
      }

      public void onTrackingStart(Entity entity) {
         if (entity instanceof Player player) {
            BotWorld.this.players.add(player);
         } else if (entity instanceof EnderDragon enderDragon) {
            BotWorld.this.enderDragonParts.addAll(Arrays.asList(enderDragon.getSubEntities()));
         }
      }

      public void onTrackingEnd(Entity entity) {
         entity.unRide();
         if (entity instanceof Player player) {
            BotWorld.this.players.remove(player);
         } else if (entity instanceof EnderDragon enderDragon) {
            BotWorld.this.enderDragonParts.removeAll(Arrays.asList(enderDragon.getSubEntities()));
         }
      }

      public void onSectionChange(Entity entity) {
      }
   }

   public interface RenderListener {
      void onBlockChanged(BlockPos pos);

      void onChunkChanged(int x, int z);

      void onSectionChanged(int x, int y, int z);
   }
}
