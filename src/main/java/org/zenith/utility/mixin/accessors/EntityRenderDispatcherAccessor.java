package org.zenith.utility.mixin.accessors;

import java.util.Map;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({EntityRenderDispatcher.class})
public interface EntityRenderDispatcherAccessor {
   @Accessor("renderers")
   Map<PlayerModelType, EntityRenderer<? extends Player, ?>> zenith$getModelRenderers();
}
