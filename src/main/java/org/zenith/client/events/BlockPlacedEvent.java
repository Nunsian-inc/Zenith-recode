package org.zenith.client.events;

import com.darkmagician6.eventapi.events.Event;
import lombok.Generated;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class BlockPlacedEvent implements Event {
   private final BlockPos blockPos;
   private final BlockState blockState;

   @Generated
   public BlockPlacedEvent(BlockPos blockPos, BlockState blockState) {
      this.blockPos = blockPos;
      this.blockState = blockState;
   }

   @Generated
   public BlockPos getBlockPos() {
      return this.blockPos;
   }

   @Generated
   public BlockState getBlockState() {
      return this.blockState;
   }
}
