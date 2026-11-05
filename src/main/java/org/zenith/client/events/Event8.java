package org.zenith.client.events;

import lombok.Generated;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import org.zenith.CancellableEvent;

public class Event8 extends CancellableEvent {
   private final Block block;
   private final BlockPos blockPos;

   @Generated
   public Event8(Block block, BlockPos blockPos) {
      this.block = block;
      this.blockPos = blockPos;
   }

   @Generated
   public Block getBlock() {
      return this.block;
   }

   @Generated
   public BlockPos getBlockPos2() {
      return this.blockPos;
   }
}
