package org.zenith.utility.game;

import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

public final class NoRenderBlocks {
   public static volatile boolean enabled = false;
   private static final String[] texts = new String[]{
      "short_grass", "tall_grass", "fern", "large_fern", "dead_bush", "seagrass", "tall_seagrass", "short_dry_grass", "tall_dry_grass", "bush"
   };

   private NoRenderBlocks() {
   }

   public static boolean method5237(BlockState blockState, BlockPos pos) {
      if (!enabled || blockState == null || pos == null) {
         return false;
      } else if (!method5238(blockState)) {
         return false;
      } else {
         int y = pos.getY();
         if (blockState.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF) && blockState.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER) {
            y--;
         }

         return method5239(pos.getX(), y, pos.getZ()) < 0.5F;
      }
   }

   private static boolean method5238(BlockState blockState) {
      return NoRenderBlocks.Entry.items.contains(blockState.getBlock());
   }

   private static float method5239(int x, int y, int z) {
      long l = x * -7046029254386353131L ^ y * -4417276706812531889L ^ z * 1609587929392839161L;
      l ^= l >>> 31;
      l *= -4658895280553007687L;
      l ^= l >>> 29;
      return (float)(l >>> 40) / 1.6777216E7F;
   }

   private static final class Entry {
      private static final Set<Block> items = build();

      private static Set<Block> build() {
         ReferenceOpenHashSet referenceOpenHashSet = new ReferenceOpenHashSet();

         for (String text : NoRenderBlocks.texts) {
            BuiltInRegistries.BLOCK.getOptional(Identifier.withDefaultNamespace(text)).ifPresent(referenceOpenHashSet::add);
         }

         return referenceOpenHashSet;
      }
   }
}
