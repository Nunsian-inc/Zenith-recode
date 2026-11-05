package org.zenith.utility.misc;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import org.jetbrains.annotations.Nullable;
import org.zenith.base.font.MsdfRenderer;

public final class ScissorStack {
   private ScissorStack() {
   }

   public static void method1750(@Nullable ScreenRectangle screenRectangle) {
      MsdfRenderer.flushBatch();
      if (screenRectangle == null) {
         RenderSystem.disableScissorForRenderTypeDraws();
      } else {
         Window window = Minecraft.getInstance().getWindow();
         int guiScale = window.getGuiScale();
         RenderSystem.enableScissorForRenderTypeDraws(
            screenRectangle.left() * guiScale, window.getHeight() - screenRectangle.bottom() * guiScale, Math.max(0, screenRectangle.width() * guiScale), Math.max(0, screenRectangle.height() * guiScale)
         );
      }
   }

   public static void clear() {
      MsdfRenderer.flushBatch();
      RenderSystem.disableScissorForRenderTypeDraws();
   }
}
