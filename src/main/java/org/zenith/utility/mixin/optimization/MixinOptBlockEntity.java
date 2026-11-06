package org.zenith.utility.mixin.optimization;

import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.zenith.utility.misc.BlockEntityOptimizationState;

@Mixin({BlockEntity.class})
public class MixinOptBlockEntity implements BlockEntityOptimizationState.Handler {
   @Unique
   private BlockEntityOptimizationState.Entry zenith$optData;

   @Override
   public BlockEntityOptimizationState.Entry zenith$optData() {
      BlockEntityOptimizationState.Entry zenith$optData = this.zenith$optData;
      if (zenith$optData == null) {
         zenith$optData = new BlockEntityOptimizationState.Entry();
         this.zenith$optData = zenith$optData;
      }

      return zenith$optData;
   }
}
