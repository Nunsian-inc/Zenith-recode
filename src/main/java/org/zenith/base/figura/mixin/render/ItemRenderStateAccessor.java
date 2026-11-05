package org.zenith.base.figura.mixin.render;

import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState.LayerRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemStackRenderState.class)
public interface ItemRenderStateAccessor {
   @Accessor("activeLayerCount")
   int figura$getLayerCount();

   @Accessor("layers")
   LayerRenderState[] figura$getLayers();
}
