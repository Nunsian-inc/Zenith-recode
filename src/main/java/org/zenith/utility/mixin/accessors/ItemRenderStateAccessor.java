package org.zenith.utility.mixin.accessors;

import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState.LayerRenderState;
import net.minecraft.world.item.ItemDisplayContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({ItemStackRenderState.class})
public interface ItemRenderStateAccessor {
   @Accessor("layers")
   LayerRenderState[] zenith$getLayers();

   @Accessor("activeLayerCount")
   int zenith$getLayerCount();

   @Accessor("displayContext")
   ItemDisplayContext zenith$getDisplayContext();
}
