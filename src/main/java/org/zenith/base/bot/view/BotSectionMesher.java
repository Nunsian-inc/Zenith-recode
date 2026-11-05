package org.zenith.base.bot.view;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockModelLighter;
import net.minecraft.client.renderer.block.BlockQuadOutput;
import net.minecraft.client.renderer.block.BlockStateModelSet;
import net.minecraft.client.renderer.block.FluidRenderer;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.material.FluidState;
import org.zenith.base.bot.world.BotWorld;

public final class BotSectionMesher {
   private static final int INITIAL_LAYER_BUFFER_SIZE = 65536;

   private BotSectionMesher() {
   }

   public static BotSectionMesher.MeshResult build(BotWorld world, long pos) {
      int n = SectionPos.x(pos);
      int sectionY = SectionPos.y(pos);
      int z2 = SectionPos.z(pos);
      LevelChunk chunk = world.getChunkSource().getChunk(n, z2, null, false);
      if (chunk == null) {
         return new BotSectionMesher.MeshResult(pos, List.of(), List.of(), true);
      } else {
         int sectionIndexFromSectionY = world.getSectionIndexFromSectionY(sectionY);
         if (sectionIndexFromSectionY >= 0 && sectionIndexFromSectionY < chunk.getSections().length && !chunk.getSections()[sectionIndexFromSectionY].hasOnlyAir()) {
            // 26.3 tesselation pieces, wired like vanilla SectionCompiler
            Minecraft minecraft = Minecraft.getInstance();
            ModelBlockRenderer modelBlockRenderer = new ModelBlockRenderer(minecraft.options.ambientOcclusion().get(), true, minecraft.getBlockColors());
            FluidRenderer fluidRenderer = new FluidRenderer(minecraft.getModelManager().getFluidStateModelSet());
            BlockStateModelSet blockStateModelSet = minecraft.getModelManager().getBlockStateModelSet();
            boolean cutoutLeave = minecraft.options.cutoutLeaves().get();
            BlockPos pos2 = new BlockPos(n << 4, sectionY << 4, z2 << 4);
            BlockPos offset = pos2.offset(15, 15, 15);
            HashMap map = new HashMap();
            HashMap map2 = new HashMap();
            ArrayList blockEntities = new ArrayList();
            BlockQuadOutput blockQuadOutput = (x, y, z, quad, instance) -> beginLayer(map, map2, quad.materialInfo().layer()).putBlockBakedQuad(x, y, z, quad, instance);
            BlockQuadOutput blockQuadOutput2 = (x, y, z, quad, instance) -> beginLayer(map, map2, ChunkSectionLayer.SOLID).putBlockBakedQuad(x, y, z, quad, instance);
            BlockModelLighter.enableCaching();

            try {
               for (BlockPos pos3 : BlockPos.betweenClosed(pos2, offset)) {
                  BlockState blockState = world.getBlockState(pos3);
                  if (!blockState.isAir()) {
                     if (blockState.hasBlockEntity()) {
                        BlockEntity blockEntity = world.getBlockEntity(pos3);
                        if (blockEntity != null) {
                           blockEntities.add(blockEntity);
                        }
                     }

                     FluidState fluidState = blockState.getFluidState();
                     if (!fluidState.isEmpty()) {
                        fluidRenderer.tesselate(world, pos3, layer -> beginLayer(map, map2, layer), blockState, fluidState);
                     }

                     if (blockState.getRenderShape() == RenderShape.MODEL) {
                        modelBlockRenderer.tesselateBlock(
                           ModelBlockRenderer.forceOpaque(cutoutLeave, blockState) ? blockQuadOutput2 : blockQuadOutput,
                           SectionPos.sectionRelative(pos3.getX()),
                           SectionPos.sectionRelative(pos3.getY()),
                           SectionPos.sectionRelative(pos3.getZ()),
                           world,
                           pos3,
                           blockState,
                           blockStateModelSet.get(blockState),
                           blockState.getSeed(pos3)
                        );
                     }
                  }
               }
            } catch (Throwable e) {
               for (Object obj : (Iterable<?>)(Object)(map2.values())) {
                  ByteBufferBuilder byteBufferBuilder = (ByteBufferBuilder)obj;
                  byteBufferBuilder.close();
               }

               throw e;
            } finally {
               BlockModelLighter.clearCache();
            }

            List<LayerMesh> layers = new ArrayList<>(map.size());

            for (Object obj : (Iterable<?>)(Object)(map.entrySet())) {
               Entry entry = (Entry)obj;
               MeshData buffer = ((BufferBuilder)entry.getValue()).build();
               ByteBufferBuilder allocator = (ByteBufferBuilder)map2.get(entry.getKey());
               if (buffer != null) {
                  layers.add(new BotSectionMesher.LayerMesh((ChunkSectionLayer)entry.getKey(), buffer, allocator));
               } else {
                  allocator.close();
               }
            }

            return new BotSectionMesher.MeshResult(pos, layers, blockEntities, false);
         } else {
            return new BotSectionMesher.MeshResult(pos, List.of(), List.of(), false);
         }
      }
   }

   private static BufferBuilder beginLayer(Map<ChunkSectionLayer, BufferBuilder> map, Map<ChunkSectionLayer, ByteBufferBuilder> map2, ChunkSectionLayer layer) {
      BufferBuilder bufferBuilder = (BufferBuilder)map.get(layer);
      if (bufferBuilder == null) {
         ByteBufferBuilder buffer = new ByteBufferBuilder(Math.min(layer.bufferSize(), 65536));
         map2.put(layer, buffer);
         bufferBuilder = new BufferBuilder(buffer, PrimitiveTopology.QUADS, layer.vertexFormat());
         map.put(layer, bufferBuilder);
      }

      return bufferBuilder;
   }

   public static final class LayerMesh {
      private final ChunkSectionLayer layer;
      private final MeshData buffer;
      private final ByteBufferBuilder allocator;

      public ChunkSectionLayer layer() {
         return this.layer;
      }

      public MeshData buffer() {
         return this.buffer;
      }

      public ByteBufferBuilder allocator() {
         return this.allocator;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof LayerMesh other)) return false;
         return java.util.Objects.equals(this.layer, other.layer) && java.util.Objects.equals(this.buffer, other.buffer) && java.util.Objects.equals(this.allocator, other.allocator);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(layer, buffer, allocator);
      }

      @Override
      public String toString() {
         return "LayerMesh[" + "layer=" + this.layer + ", " + "buffer=" + this.buffer + ", " + "allocator=" + this.allocator + "]";
      }

      public LayerMesh(ChunkSectionLayer layer, MeshData buffer, ByteBufferBuilder allocator) {
         this.layer = layer;
         this.buffer = buffer;
         this.allocator = allocator;
      }
   }

   public static final class MeshResult {
      private final long sectionPos;
      private final List<BotSectionMesher.LayerMesh> layers;
      private final List<BlockEntity> blockEntities;
      private final boolean chunkMissing;

      public long sectionPos() {
         return this.sectionPos;
      }

      public List<BotSectionMesher.LayerMesh> layers() {
         return this.layers;
      }

      public List<BlockEntity> blockEntities() {
         return this.blockEntities;
      }

      public boolean chunkMissing() {
         return this.chunkMissing;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof MeshResult other)) return false;
         return java.util.Objects.equals(this.sectionPos, other.sectionPos) && java.util.Objects.equals(this.layers, other.layers) && java.util.Objects.equals(this.blockEntities, other.blockEntities) && java.util.Objects.equals(this.chunkMissing, other.chunkMissing);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(sectionPos, layers, blockEntities, chunkMissing);
      }

      @Override
      public String toString() {
         return "MeshResult[" + "sectionPos=" + this.sectionPos + ", " + "layers=" + this.layers + ", " + "blockEntities=" + this.blockEntities + ", " + "chunkMissing=" + this.chunkMissing + "]";
      }

      public MeshResult(long sectionPos, List<BotSectionMesher.LayerMesh> layers, List<BlockEntity> blockEntities, boolean chunkMissing) {
         this.sectionPos = sectionPos;
         this.layers = layers;
         this.blockEntities = blockEntities;
         this.chunkMissing = chunkMissing;
      }
   }
}
