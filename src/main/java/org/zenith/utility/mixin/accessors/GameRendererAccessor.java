package org.zenith.utility.mixin.accessors;

import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.fog.FogRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({GameRenderer.class})
public interface GameRendererAccessor {
   @Accessor("screenEffectRenderer")
   ScreenEffectRenderer zenith$overlayRenderer();

   @Accessor("guiRenderer")
   GuiRenderer zenith$guiRenderer();

   @Accessor("fogRenderer")
   FogRenderer zenith$fogRenderer();

   @Accessor("featureRenderDispatcher")
   FeatureRenderDispatcher zenith$entityRenderDispatcher();
}
