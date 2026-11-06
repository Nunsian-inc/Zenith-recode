package org.zenith.utility.misc;

import net.minecraft.client.gui.GuiGraphicsExtractor;

public interface ContainerScreenAnimationAccess {
   boolean zenith$betterMinecraft$isClosingAnimation();

   void zenith$betterMinecraft$popScaleIfNeeded(GuiGraphicsExtractor guiGraphicsExtractor);

   void zenith$betterMinecraft$finishClosingAnimation();
}
