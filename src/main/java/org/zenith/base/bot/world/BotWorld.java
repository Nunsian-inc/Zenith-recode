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
      BotPlayHandler var1, ClientLevelData var2, ResourceKey<Level> var3, Holder<DimensionType> var4, int var5, int var6, boolean var7, long var8, int var10
   ) {
      super(var2, var3, var1.getRegistryManager(), var4, true, var7, var8, 1000000);
      this.networkHandler = var1;
      this.chunkManager = new BotChunkManager(this, var5);
      this.tickManager = new TickRateManager();
      this.clientWorldProperties = var2;
      this.seaLevel = var10;
      this.setSpawnPos(new BlockPos(8, 64, 8), 0.0F);
      this.simulationDistance = var6;
      this.updateSkyBrightness();
   }

   public synchronized void addRenderListener(BotWorld.RenderListener var1) {
      BotWorld.RenderListener[] var2 = this.renderListeners;

      for (BotWorld.RenderListener var6 : var2) {
         if (var6 == var1) {
            return;
         }
      }

      BotWorld.RenderListener[] var7 = Arrays.copyOf(var2, var2.length + 1);
      var7[var2.length] = var1;
      this.renderListeners = var7;
   }

   public synchronized void removeRenderListener(BotWorld.RenderListener var1) {
      BotWorld.RenderListener[] var2 = this.renderListeners;

      for (int var3 = 0; var3 < var2.length; var3++) {
         if (var2[var3] == var1) {
            if (var2.length == 1) {
               this.renderListeners = NO_LISTENERS;
            } else {
               BotWorld.RenderListener[] var4 = new BotWorld.RenderListener[var2.length - 1];
               System.arraycopy(var2, 0, var4, 0, var3);
               System.arraycopy(var2, var3 + 1, var4, var3, var2.length - var3 - 1);
               this.renderListeners = var4;
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

   public void setBotPlayer(@Nullable BotPlayer var1) {
      this.botPlayer = var1;
   }

   @Nullable
   public BotPlayer getBotPlayer() {
      return this.botPlayer;
   }

   public void handlePlayerActionResponse(int var1) {
      this.pendingUpdateManager.processPendingUpdates(var1, this);
   }

   public void handleBlockUpdate(BlockPos var1, BlockState var2, int var3) {
      if (!this.pendingUpdateManager.hasPendingUpdate(var1, var2)) {
         super.setBlock(var1, var2, var3, 512);
      }
   }

   public void processPendingUpdate(BlockPos var1, BlockState var2, Vec3 var3) {
      BlockState var4 = this.getBlockState(var1);
      if (var4 != var2) {
         this.setBlock(var1, var2, 19);
         BotPlayer var5 = this.botPlayer;
         if (var5 != null && this == var5.level() && var5.isColliding(var1, var2)) {
            var5.absSnapTo(var3.x, var3.y, var3.z);
         }
      }
   }

   public BotPendingUpdateManager getPendingUpdateManager() {
      return this.pendingUpdateManager;
   }

   @Override
   public boolean setBlock(BlockPos pos, BlockState state, int flags, int maxUpdateDepth) {
      if (this.pendingUpdateManager.hasPendingSequence() && this.botPlayer != null) {
         BlockState var5 = this.getBlockState(pos);
         boolean var6 = super.setBlock(pos, state, flags, maxUpdateDepth);
         if (var6) {
            this.pendingUpdateManager.addPendingUpdate(pos, var5, this.botPlayer);
         }

         return var6;
      } else {
         return super.setBlock(pos, state, flags, maxUpdateDepth);
      }
   }

   public void enqueueChunkUpdate(Runnable var1) {
      this.chunkUpdaters.add(var1);
   }

   public void runQueuedChunkUpdates() {
      int var1 = this.chunkUpdaters.size();
      int var2 = var1 < 1000 ? Math.max(10, var1 / 10) : var1;

      for (int var3 = 0; var3 < var2; var3++) {
         Runnable var4 = this.chunkUpdaters.poll();
         if (var4 == null) {
            break;
         }

         var4.run();
      }
   }

   public void tick(BooleanSupplier var1) {
      this.getWorldBorder().tick();
      this.updateSkyBrightness();
      if (this.tickRateManager().runsNormally()) {
         this.tickTime();
      }

      this.chunkManager.tick(var1, true);
   }

   private void tickTime() {
      this.clientWorldProperties.setGameTime(this.clientWorldProperties.getGameTime() + 1L);
      this.clockManager().tick(this.clientWorldProperties.getGameTime());
   }

   // 26.x: time of day lives in world clocks (ClientClockManager on the handler), the packet only sets game time here
   public void setTime(long var1) {
      this.clientWorldProperties.setGameTime(var1);
   }

   @Override
   public net.minecraft.client.ClientClockManager clockManager() {
      return this.networkHandler.clockManager();
   }

   public Iterable<Entity> entitiesForRendering() {
      return this.getEntities().getAll();
   }

   public void tickEntities() {
      this.entityList.forEach(var1 -> {
         if (!var1.isRemoved() && !var1.isPassenger() && !this.tickManager.isEntityFrozen(var1)) {
            this.guardEntityTick(this::tickEntity, var1);
         }
      });
      this.tickBlockEntities();
   }

   public boolean hasEntity(Entity var1) {
      return this.entityList.contains(var1);
   }

   @Override
   public boolean shouldTickDeath(Entity entity) {
      BotPlayer var2 = this.botPlayer;
      return var2 != null && entity.chunkPosition().getChessboardDistance(var2.chunkPosition()) <= this.simulationDistance;
   }

   public void tickEntity(Entity var1) {
      var1.setOldPosAndRot();
      var1.tickCount++;
      var1.tick();

      for (Entity var3 : var1.getPassengers()) {
         this.tickPassenger(var1, var3);
      }
   }

   private void tickPassenger(Entity var1, Entity var2) {
      if (!var2.isRemoved() && var2.getVehicle() == var1) {
         if (var2 instanceof Player || this.entityList.contains(var2)) {
            var2.setOldPosAndRot();
            var2.tickCount++;
            var2.rideTick();

            for (Entity var4 : var2.getPassengers()) {
               this.tickPassenger(var2, var4);
            }
         }
      } else {
         var2.stopRiding();
      }
   }

   public void unloadBlockEntities(LevelChunk var1) {
      var1.clearAllBlockEntities();
      this.chunkManager.getLightEngine().setLightEnabled(var1.getPos(), false);
      this.entityManager.stopTicking(var1.getPos());
   }

   public void resetChunkColor(ChunkPos var1) {
      this.entityManager.startTicking(var1);

      for (BotWorld.RenderListener var5 : this.renderListeners) {
         var5.onChunkChanged(var1.x(), var1.z());
      }
   }

   public void onChunkUnload(long var1) {
      for (BotWorld.RenderListener var6 : this.renderListeners) {
         var6.onSectionChanged(SectionPos.x(var1), SectionPos.y(var1), SectionPos.z(var1));
      }
   }

   @Override
   public boolean hasChunk(int chunkX, int chunkZ) {
      return true;
   }

   public int getRegularEntityCount() {
      return this.entityManager.count();
   }

   public void addEntity(Entity var1) {
      this.removeEntity(var1.getId(), RemovalReason.DISCARDED);
      this.entityManager.addEntity(var1);
   }

   public void removeEntity(int var1, RemovalReason var2) {
      Entity var3 = this.getEntities().get(var1);
      if (var3 != null) {
         var3.setRemoved(var2);
         var3.onClientRemoval();
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

   public void playSoundFromEntity(Entity var1, SoundEvent var2, SoundSource var3, float var4, float var5) {
   }

   public void playSound(double var1, double var3, double var5, SoundEvent var7, SoundSource var8, float var9, float var10, boolean var11) {
   }

   @Override
   public void addParticle(ParticleOptions parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
   }

   @Override
   public void addParticle(
      ParticleOptions parameters, boolean force, boolean canSpawnOnMinimal, double x, double y, double z, double velocityX, double velocityY, double velocityZ
   ) {
   }

   public void addImportantParticle(ParticleOptions var1, double var2, double var4, double var6, double var8, double var10, double var12) {
   }

   public void addImportantParticle(ParticleOptions var1, boolean var2, double var3, double var5, double var7, double var9, double var11, double var13) {
   }

   @Override
   public void sendBlockUpdated(BlockPos pos, BlockState oldState, BlockState newState, int flags) {
      for (BotWorld.RenderListener var8 : this.renderListeners) {
         var8.onBlockChanged(pos);
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

   public void putClientsideMapState(MapId var1, MapItemSavedData var2) {
      this.mapStates.put(var1, var2);
   }

   public void putMapState(MapId var1, MapItemSavedData var2) {
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

   public void setSpawnPos(BlockPos var1, float var2) {
      this.setRespawnData(new RespawnData(GlobalPos.of(this.dimension(), var1), var2, 0.0F));
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

   public void putMapStates(Map<MapId, MapItemSavedData> var1) {
      this.mapStates.putAll(var1);
   }

   @Override
   public LevelEntityGetter<Entity> getEntities() {
      return this.entityManager.getEntityGetter();
   }

   @Override
   public String gatherChunkSourceStats() {
      return "Chunks[Bot] W: " + this.chunkManager.gatherStats() + " E: " + this.entityManager.gatherStats();
   }

   public void setSimulationDistance(int var1) {
      this.simulationDistance = var1;
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
      public void onCreated(Entity var1) {
         BotWorld.this.entitySetVersion++;
      }

      public void onDestroyed(Entity var1) {
         BotWorld.this.entitySetVersion++;
      }

      public void onTickingStart(Entity var1) {
         BotWorld.this.entityList.add(var1);
      }

      public void onTickingEnd(Entity var1) {
         BotWorld.this.entityList.remove(var1);
      }

      public void onTrackingStart(Entity var1) {
         if (var1 instanceof Player var2) {
            BotWorld.this.players.add(var2);
         } else if (var1 instanceof EnderDragon var3) {
            BotWorld.this.enderDragonParts.addAll(Arrays.asList(var3.getSubEntities()));
         }
      }

      public void onTrackingEnd(Entity var1) {
         var1.unRide();
         if (var1 instanceof Player var2) {
            BotWorld.this.players.remove(var2);
         } else if (var1 instanceof EnderDragon var3) {
            BotWorld.this.enderDragonParts.removeAll(Arrays.asList(var3.getSubEntities()));
         }
      }

      public void onSectionChange(Entity var1) {
      }
   }

   public interface RenderListener {
      void onBlockChanged(BlockPos var1);

      void onChunkChanged(int var1, int var2);

      void onSectionChanged(int var1, int var2, int var3);
   }
}
