package org.zenith.client.events;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.zenith.CancellableEvent;

public class BlockBreakingEvent extends CancellableEvent {
   private final BlockPos blockPos;
   private final Direction direction;

   public BlockBreakingEvent(BlockPos blockPos, Direction direction) {
      this.blockPos = blockPos;
      this.direction = direction;
   }

   public BlockPos getBlockPos() {
      return this.blockPos;
   }

   public Direction getDirection() {
      return this.direction;
   }
}
