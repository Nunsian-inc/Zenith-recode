package org.zenith.base.bot.world;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData.BlockEntityTagOutput;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.EmptyLevelChunk;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.lighting.LevelLightEngine;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public final class BotChunkManager extends ChunkSource {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final LevelChunk emptyChunk;
   private final LevelLightEngine lightingProvider;
   private volatile BotChunkManager.BotChunkMap chunks;
   private final BotWorld world;

   public BotChunkManager(BotWorld world, int chunkLoadDistance) {
      this.world = world;
      this.emptyChunk = new EmptyLevelChunk(world, new ChunkPos(0, 0), world.registryAccess().lookupOrThrow(Registries.BIOME).getOrThrow(Biomes.PLAINS));
      this.lightingProvider = new LevelLightEngine(this, true, world.dimensionType().hasSkyLight());
      this.chunks = new BotChunkManager.BotChunkMap(getChunkMapRadius(chunkLoadDistance));
   }

   @Override
   public LevelLightEngine getLightEngine() {
      return this.lightingProvider;
   }

   private static boolean positionEquals(@Nullable LevelChunk chunk, int x, int z) {
      if (chunk == null) {
         return false;
      } else {
         ChunkPos pos = chunk.getPos();
         return pos.x() == x && pos.z() == z;
      }
   }

   public void unload(ChunkPos pos) {
      if (this.chunks.isInRadius(pos.x(), pos.z())) {
         int index = this.chunks.getIndex(pos.x(), pos.z());
         LevelChunk chunk = this.chunks.getChunk(index);
         if (positionEquals(chunk, pos.x(), pos.z())) {
            this.chunks.unloadChunk(index, chunk);

            for (BotWorld.RenderListener renderListener : this.world.getRenderListeners()) {
               renderListener.onChunkChanged(pos.x(), pos.z());
            }
         }
      }
   }

   @Nullable
   public LevelChunk getChunk(int x, int z, ChunkStatus targetStatus, boolean loadOrGenerate) {
      if (this.chunks.isInRadius(x, z)) {
         LevelChunk chunk = this.chunks.getChunk(this.chunks.getIndex(x, z));
         if (positionEquals(chunk, x, z)) {
            return chunk;
         }
      }

      return loadOrGenerate ? this.emptyChunk : null;
   }

   @Override
   public BlockGetter getLevel() {
      return this.world;
   }

   public void onChunkBiomeData(int x, int z, FriendlyByteBuf readBuffer) {
      if (!this.chunks.isInRadius(x, z)) {
         LOGGER.warn("Ignoring chunk since it's not in the view range: {}, {}", x, z);
      } else {
         int index = this.chunks.getIndex(x, z);
         LevelChunk chunk = this.chunks.getChunk(index);
         if (!positionEquals(chunk, x, z)) {
            LOGGER.warn("Ignoring chunk since it's not present: {}, {}", x, z);
         } else {
            chunk.replaceBiomes(readBuffer);
         }
      }
   }

   @Nullable
   public LevelChunk loadChunkFromPacket(int x, int z, net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData chunkData) {
      if (!this.chunks.isInRadius(x, z)) {
         LOGGER.warn("Ignoring chunk since it's not in the view range: {}, {}", x, z);
         return null;
      } else {
         int index = this.chunks.getIndex(x, z);
         LevelChunk chunk = this.chunks.getChunk(index);
         ChunkPos chunkPos = new ChunkPos(x, z);
         if (!positionEquals(chunk, x, z)) {
            chunk = new LevelChunk(this.world, chunkPos);
            chunk.replaceWithPacketData(x, z, chunkData);
            this.chunks.set(index, chunk);
         } else {
            chunk.replaceWithPacketData(x, z, chunkData);
            this.chunks.refreshSections(chunk);
         }

         this.world.resetChunkColor(chunkPos);
         return chunk;
      }
   }

   @Override
   public void tick(BooleanSupplier shouldKeepTicking, boolean tickChunks) {
   }

   public void setChunkMapCenter(int centerChunkX, int centerChunkZ) {
      this.chunks.centerChunkX = centerChunkX;
      this.chunks.centerChunkZ = centerChunkZ;
   }

   public void updateLoadDistance(int radius) {
      int n = this.chunks.radius;
      int chunkMapRadius = getChunkMapRadius(radius);
      if (n != chunkMapRadius) {
         BotChunkManager.BotChunkMap botChunkMap = new BotChunkManager.BotChunkMap(chunkMapRadius);
         botChunkMap.centerChunkX = this.chunks.centerChunkX;
         botChunkMap.centerChunkZ = this.chunks.centerChunkZ;

         for (int i = 0; i < this.chunks.chunks.length(); i++) {
            LevelChunk chunk = this.chunks.chunks.get(i);
            if (chunk != null) {
               ChunkPos pos = chunk.getPos();
               if (botChunkMap.isInRadius(pos.x(), pos.z())) {
                  botChunkMap.set(botChunkMap.getIndex(pos.x(), pos.z()), chunk);
               }
            }
         }

         this.chunks = botChunkMap;
      }
   }

   private static int getChunkMapRadius(int chunkLoadDistance) {
      return Math.max(2, chunkLoadDistance) + 3;
   }

   @Override
   public String gatherStats() {
      return this.chunks.chunks.length() + ", " + this.getLoadedChunksCount();
   }

   @Override
   public int getLoadedChunksCount() {
      return this.chunks.loadedChunkCount;
   }

   @Override
   public void onLightUpdate(LightLayer type, SectionPos pos) {
   }

   public LongOpenHashSet getActiveSections() {
      return this.chunks.activeSections;
   }

   @Override
   public void onSectionEmptinessChanged(int x, int sectionY, int z, boolean previouslyEmpty) {
      this.chunks.onSectionStatusChanged(x, sectionY, z, previouslyEmpty);
   }

   private final class BotChunkMap {
      public final AtomicReferenceArray<LevelChunk> chunks;
      public final LongOpenHashSet activeSections = new LongOpenHashSet();
      public final int radius;
      private final int diameter;
      public volatile int centerChunkX;
      public volatile int centerChunkZ;
      public int loadedChunkCount;

      public BotChunkMap(int radius) {
         this.radius = radius;
         this.diameter = radius * 2 + 1;
         this.chunks = new AtomicReferenceArray<>(this.diameter * this.diameter);
      }

      public int getIndex(int x, int z) {
         return Math.floorMod(z, this.diameter) * this.diameter + Math.floorMod(x, this.diameter);
      }

      public void set(int index, @Nullable LevelChunk chunk) {
         LevelChunk andSet = this.chunks.getAndSet(index, chunk);
         if (andSet != null) {
            this.loadedChunkCount--;
            this.unloadChunkSections(andSet);
            BotChunkManager.this.world.unloadBlockEntities(andSet);
         }

         if (chunk != null) {
            this.loadedChunkCount++;
            this.loadChunkSections(chunk);
         }
      }

      public void unloadChunk(int index, LevelChunk chunk) {
         if (this.chunks.compareAndSet(index, chunk, null)) {
            this.loadedChunkCount--;
            this.unloadChunkSections(chunk);
         }

         BotChunkManager.this.world.unloadBlockEntities(chunk);
      }

      public void onSectionStatusChanged(int x, int sectionY, int z, boolean previouslyEmpty) {
         if (this.isInRadius(x, z)) {
            long sectionNode = SectionPos.asLong(x, sectionY, z);
            if (previouslyEmpty) {
               this.activeSections.add(sectionNode);
            } else if (this.activeSections.remove(sectionNode)) {
               BotChunkManager.this.world.onChunkUnload(sectionNode);
            }
         }
      }

      private void unloadChunkSections(LevelChunk andSet) {
         LevelChunkSection[] sections = andSet.getSections();

         for (int i = 0; i < sections.length; i++) {
            ChunkPos pos = andSet.getPos();
            this.activeSections.remove(SectionPos.asLong(pos.x(), andSet.getSectionYFromSectionIndex(i), pos.z()));
         }
      }

      private void loadChunkSections(LevelChunk chunk) {
         LevelChunkSection[] sections = chunk.getSections();

         for (int i = 0; i < sections.length; i++) {
            if (sections[i].hasOnlyAir()) {
               ChunkPos pos = chunk.getPos();
               this.activeSections.add(SectionPos.asLong(pos.x(), chunk.getSectionYFromSectionIndex(i), pos.z()));
            }
         }
      }

      public void refreshSections(LevelChunk chunk) {
         ChunkPos pos = chunk.getPos();
         LevelChunkSection[] sections = chunk.getSections();

         for (int i = 0; i < sections.length; i++) {
            long sectionNode = SectionPos.asLong(pos.x(), chunk.getSectionYFromSectionIndex(i), pos.z());
            if (sections[i].hasOnlyAir()) {
               this.activeSections.add(sectionNode);
            } else if (this.activeSections.remove(sectionNode)) {
               BotChunkManager.this.world.onChunkUnload(sectionNode);
            }
         }
      }

      public boolean isInRadius(int x, int z) {
         return Math.abs(x - this.centerChunkX) <= this.radius && Math.abs(z - this.centerChunkZ) <= this.radius;
      }

      @Nullable
      public LevelChunk getChunk(int index) {
         return this.chunks.get(index);
      }
   }
}
