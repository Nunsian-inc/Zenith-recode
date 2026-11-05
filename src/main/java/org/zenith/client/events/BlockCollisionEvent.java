package org.zenith.client.events;

import com.darkmagician6.eventapi.events.callables.EventCancellable;
import lombok.Generated;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;

public class BlockCollisionEvent extends EventCancellable {
   private final Block block;
   private final BlockPos blockPos;

   @Generated
   public Block getBlock() {
      return this.block;
   }

   @Generated
   public BlockPos getBlockPos() {
      return this.blockPos;
   }

   @Generated
   public BlockCollisionEvent(Block block, BlockPos blockPos) {
      this.block = block;
      this.blockPos = blockPos;
   }
}
