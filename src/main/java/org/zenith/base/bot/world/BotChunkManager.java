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

   public BotChunkManager(BotWorld var1, int var2) {
      this.world = var1;
      this.emptyChunk = new EmptyLevelChunk(var1, new ChunkPos(0, 0), var1.registryAccess().lookupOrThrow(Registries.BIOME).getOrThrow(Biomes.PLAINS));
      this.lightingProvider = new LevelLightEngine(this, true, var1.dimensionType().hasSkyLight());
      this.chunks = new BotChunkManager.BotChunkMap(getChunkMapRadius(var2));
   }

   @Override
   public LevelLightEngine getLightEngine() {
      return this.lightingProvider;
   }

   private static boolean positionEquals(@Nullable LevelChunk var0, int var1, int var2) {
      if (var0 == null) {
         return false;
      } else {
         ChunkPos var3 = var0.getPos();
         return var3.x() == var1 && var3.z() == var2;
      }
   }

   public void unload(ChunkPos var1) {
      if (this.chunks.isInRadius(var1.x(), var1.z())) {
         int var2 = this.chunks.getIndex(var1.x(), var1.z());
         LevelChunk var3 = this.chunks.getChunk(var2);
         if (positionEquals(var3, var1.x(), var1.z())) {
            this.chunks.unloadChunk(var2, var3);

            for (BotWorld.RenderListener var7 : this.world.getRenderListeners()) {
               var7.onChunkChanged(var1.x(), var1.z());
            }
         }
      }
   }

   @Nullable
   public LevelChunk getChunk(int var1, int var2, ChunkStatus var3, boolean var4) {
      if (this.chunks.isInRadius(var1, var2)) {
         LevelChunk var5 = this.chunks.getChunk(this.chunks.getIndex(var1, var2));
         if (positionEquals(var5, var1, var2)) {
            return var5;
         }
      }

      return var4 ? this.emptyChunk : null;
   }

   @Override
   public BlockGetter getLevel() {
      return this.world;
   }

   public void onChunkBiomeData(int var1, int var2, FriendlyByteBuf var3) {
      if (!this.chunks.isInRadius(var1, var2)) {
         LOGGER.warn("Ignoring chunk since it's not in the view range: {}, {}", var1, var2);
      } else {
         int var4 = this.chunks.getIndex(var1, var2);
         LevelChunk var5 = this.chunks.getChunk(var4);
         if (!positionEquals(var5, var1, var2)) {
            LOGGER.warn("Ignoring chunk since it's not present: {}, {}", var1, var2);
         } else {
            var5.replaceBiomes(var3);
         }
      }
   }

   @Nullable
   public LevelChunk loadChunkFromPacket(int var1, int var2, net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData var3) {
      if (!this.chunks.isInRadius(var1, var2)) {
         LOGGER.warn("Ignoring chunk since it's not in the view range: {}, {}", var1, var2);
         return null;
      } else {
         int var6 = this.chunks.getIndex(var1, var2);
         LevelChunk var7 = this.chunks.getChunk(var6);
         ChunkPos var8 = new ChunkPos(var1, var2);
         if (!positionEquals(var7, var1, var2)) {
            var7 = new LevelChunk(this.world, var8);
            var7.replaceWithPacketData(var1, var2, var3);
            this.chunks.set(var6, var7);
         } else {
            var7.replaceWithPacketData(var1, var2, var3);
            this.chunks.refreshSections(var7);
         }

         this.world.resetChunkColor(var8);
         return var7;
      }
   }

   @Override
   public void tick(BooleanSupplier shouldKeepTicking, boolean tickChunks) {
   }

   public void setChunkMapCenter(int var1, int var2) {
      this.chunks.centerChunkX = var1;
      this.chunks.centerChunkZ = var2;
   }

   public void updateLoadDistance(int var1) {
      int var2 = this.chunks.radius;
      int var3 = getChunkMapRadius(var1);
      if (var2 != var3) {
         BotChunkManager.BotChunkMap var4 = new BotChunkManager.BotChunkMap(var3);
         var4.centerChunkX = this.chunks.centerChunkX;
         var4.centerChunkZ = this.chunks.centerChunkZ;

         for (int var5 = 0; var5 < this.chunks.chunks.length(); var5++) {
            LevelChunk var6 = this.chunks.chunks.get(var5);
            if (var6 != null) {
               ChunkPos var7 = var6.getPos();
               if (var4.isInRadius(var7.x(), var7.z())) {
                  var4.set(var4.getIndex(var7.x(), var7.z()), var6);
               }
            }
         }

         this.chunks = var4;
      }
   }

   private static int getChunkMapRadius(int var0) {
      return Math.max(2, var0) + 3;
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

      public BotChunkMap(int var2) {
         this.radius = var2;
         this.diameter = var2 * 2 + 1;
         this.chunks = new AtomicReferenceArray<>(this.diameter * this.diameter);
      }

      public int getIndex(int var1, int var2) {
         return Math.floorMod(var2, this.diameter) * this.diameter + Math.floorMod(var1, this.diameter);
      }

      public void set(int var1, @Nullable LevelChunk var2) {
         LevelChunk var3 = this.chunks.getAndSet(var1, var2);
         if (var3 != null) {
            this.loadedChunkCount--;
            this.unloadChunkSections(var3);
            BotChunkManager.this.world.unloadBlockEntities(var3);
         }

         if (var2 != null) {
            this.loadedChunkCount++;
            this.loadChunkSections(var2);
         }
      }

      public void unloadChunk(int var1, LevelChunk var2) {
         if (this.chunks.compareAndSet(var1, var2, null)) {
            this.loadedChunkCount--;
            this.unloadChunkSections(var2);
         }

         BotChunkManager.this.world.unloadBlockEntities(var2);
      }

      public void onSectionStatusChanged(int var1, int var2, int var3, boolean var4) {
         if (this.isInRadius(var1, var3)) {
            long var5 = SectionPos.asLong(var1, var2, var3);
            if (var4) {
               this.activeSections.add(var5);
            } else if (this.activeSections.remove(var5)) {
               BotChunkManager.this.world.onChunkUnload(var5);
            }
         }
      }

      private void unloadChunkSections(LevelChunk var1) {
         LevelChunkSection[] var2 = var1.getSections();

         for (int var3 = 0; var3 < var2.length; var3++) {
            ChunkPos var4 = var1.getPos();
            this.activeSections.remove(SectionPos.asLong(var4.x(), var1.getSectionYFromSectionIndex(var3), var4.z()));
         }
      }

      private void loadChunkSections(LevelChunk var1) {
         LevelChunkSection[] var2 = var1.getSections();

         for (int var3 = 0; var3 < var2.length; var3++) {
            if (var2[var3].hasOnlyAir()) {
               ChunkPos var4 = var1.getPos();
               this.activeSections.add(SectionPos.asLong(var4.x(), var1.getSectionYFromSectionIndex(var3), var4.z()));
            }
         }
      }

      public void refreshSections(LevelChunk var1) {
         ChunkPos var2 = var1.getPos();
         LevelChunkSection[] var3 = var1.getSections();

         for (int var4 = 0; var4 < var3.length; var4++) {
            long var5 = SectionPos.asLong(var2.x(), var1.getSectionYFromSectionIndex(var4), var2.z());
            if (var3[var4].hasOnlyAir()) {
               this.activeSections.add(var5);
            } else if (this.activeSections.remove(var5)) {
               BotChunkManager.this.world.onChunkUnload(var5);
            }
         }
      }

      public boolean isInRadius(int var1, int var2) {
         return Math.abs(var1 - this.centerChunkX) <= this.radius && Math.abs(var2 - this.centerChunkZ) <= this.radius;
      }

      @Nullable
      public LevelChunk getChunk(int var1) {
         return this.chunks.get(var1);
      }
   }
}
