package org.zenith.utility.mixin.accessors;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.decoration.ItemFrame;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({ItemFrame.class})
public interface ItemFrameEntityAccessor {
   @Accessor("DATA_ROTATION")
   static EntityDataAccessor<Integer> getRotationData() {
      throw new AssertionError();
   }
}
