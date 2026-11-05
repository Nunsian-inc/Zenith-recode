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

   public static BotSectionMesher.MeshResult build(BotWorld var0, long var1) {
      int var3 = SectionPos.x(var1);
      int var4 = SectionPos.y(var1);
      int var5 = SectionPos.z(var1);
      LevelChunk var6 = var0.getChunkSource().getChunk(var3, var5, null, false);
      if (var6 == null) {
         return new BotSectionMesher.MeshResult(var1, List.of(), List.of(), true);
      } else {
         int var7 = var0.getSectionIndexFromSectionY(var4);
         if (var7 >= 0 && var7 < var6.getSections().length && !var6.getSections()[var7].hasOnlyAir()) {
            // 26.3 tesselation pieces, wired like vanilla SectionCompiler
            Minecraft var8 = Minecraft.getInstance();
            ModelBlockRenderer var17 = new ModelBlockRenderer(var8.options.ambientOcclusion().get(), true, var8.getBlockColors());
            FluidRenderer var18 = new FluidRenderer(var8.getModelManager().getFluidStateModelSet());
            BlockStateModelSet var24 = var8.getModelManager().getBlockStateModelSet();
            boolean var23 = var8.options.cutoutLeaves().get();
            BlockPos var9 = new BlockPos(var3 << 4, var4 << 4, var5 << 4);
            BlockPos var10 = var9.offset(15, 15, 15);
            HashMap var11 = new HashMap();
            HashMap var12 = new HashMap();
            ArrayList var13 = new ArrayList();
            BlockQuadOutput var14 = (x, y, z, quad, instance) -> beginLayer(var11, var12, quad.materialInfo().layer()).putBlockBakedQuad(x, y, z, quad, instance);
            BlockQuadOutput var15 = (x, y, z, quad, instance) -> beginLayer(var11, var12, ChunkSectionLayer.SOLID).putBlockBakedQuad(x, y, z, quad, instance);
            BlockModelLighter.enableCaching();

            try {
               for (BlockPos var29 : BlockPos.betweenClosed(var9, var10)) {
                  BlockState var31 = var0.getBlockState(var29);
                  if (!var31.isAir()) {
                     if (var31.hasBlockEntity()) {
                        BlockEntity var20 = var0.getBlockEntity(var29);
                        if (var20 != null) {
                           var13.add(var20);
                        }
                     }

                     FluidState var33 = var31.getFluidState();
                     if (!var33.isEmpty()) {
                        var18.tesselate(var0, var29, var21 -> beginLayer(var11, var12, var21), var31, var33);
                     }

                     if (var31.getRenderShape() == RenderShape.MODEL) {
                        var17.tesselateBlock(
                           ModelBlockRenderer.forceOpaque(var23, var31) ? var15 : var14,
                           SectionPos.sectionRelative(var29.getX()),
                           SectionPos.sectionRelative(var29.getY()),
                           SectionPos.sectionRelative(var29.getZ()),
                           var0,
                           var29,
                           var31,
                           var24.get(var31),
                           var31.getSeed(var29)
                        );
                     }
                  }
               }
            } catch (Throwable var26) {
               for (Object var19_o : (Iterable<?>)(Object)(var12.values())) {
                  ByteBufferBuilder var19 = (ByteBufferBuilder)var19_o;
                  var19.close();
               }

               throw var26;
            } finally {
               BlockModelLighter.clearCache();
            }

            List<LayerMesh> var28 = new ArrayList<>(var11.size());

            for (Object var32_o : (Iterable<?>)(Object)(var11.entrySet())) {
               Entry var32 = (Entry)var32_o;
               MeshData var34 = ((BufferBuilder)var32.getValue()).build();
               ByteBufferBuilder var36 = (ByteBufferBuilder)var12.get(var32.getKey());
               if (var34 != null) {
                  var28.add(new BotSectionMesher.LayerMesh((ChunkSectionLayer)var32.getKey(), var34, var36));
               } else {
                  var36.close();
               }
            }

            return new BotSectionMesher.MeshResult(var1, var28, var13, false);
         } else {
            return new BotSectionMesher.MeshResult(var1, List.of(), List.of(), false);
         }
      }
   }

   private static BufferBuilder beginLayer(Map<ChunkSectionLayer, BufferBuilder> var0, Map<ChunkSectionLayer, ByteBufferBuilder> var1, ChunkSectionLayer var2) {
      BufferBuilder var3 = (BufferBuilder)var0.get(var2);
      if (var3 == null) {
         ByteBufferBuilder var4 = new ByteBufferBuilder(Math.min(var2.bufferSize(), 65536));
         var1.put(var2, var4);
         var3 = new BufferBuilder(var4, PrimitiveTopology.QUADS, var2.vertexFormat());
         var0.put(var2, var3);
      }

      return var3;
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

      public LayerMesh(ChunkSectionLayer var1, MeshData var2, ByteBufferBuilder var3) {
         this.layer = var1;
         this.buffer = var2;
         this.allocator = var3;
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

      public MeshResult(long var1, List<BotSectionMesher.LayerMesh> var3, List<BlockEntity> var4, boolean var5) {
         this.sectionPos = var1;
         this.layers = var3;
         this.blockEntities = var4;
         this.chunkMissing = var5;
      }
   }
}
