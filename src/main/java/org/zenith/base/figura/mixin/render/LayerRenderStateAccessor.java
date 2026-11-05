package org.zenith.base.figura.mixin.render;

import net.minecraft.client.resources.model.cuboid.ItemTransform;
import net.minecraft.client.renderer.item.ItemStackRenderState.LayerRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LayerRenderState.class)
public interface LayerRenderStateAccessor {
   @Accessor("itemTransform")
   ItemTransform figura$getTransform();
}
