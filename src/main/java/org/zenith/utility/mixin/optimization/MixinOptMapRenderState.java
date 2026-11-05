package org.zenith.utility.mixin.optimization;

import net.minecraft.client.renderer.state.MapRenderState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.zenith.utility.misc.MapAtlas;
import org.zenith.utility.misc.Util30;

@Mixin({MapRenderState.class})
public abstract class MixinOptMapRenderState implements Util30.Handler3 {
   @Unique
   private MapAtlas.Data zenith$atlasSlot;

   @Override
   public void zenith$setAtlasSlot(@Nullable MapAtlas.Data zenith$atlasSlot) {
      this.zenith$atlasSlot = zenith$atlasSlot;
   }

   @Nullable
   @Override
   public MapAtlas.Data zenith$getAtlasSlot() {
      return this.zenith$atlasSlot;
   }
}
