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

   public static void method1750(@Nullable ScreenRectangle var0) {
      MsdfRenderer.flushBatch();
      if (var0 == null) {
         RenderSystem.disableScissorForRenderTypeDraws();
      } else {
         Window var1 = Minecraft.getInstance().getWindow();
         int var2 = var1.getGuiScale();
         RenderSystem.enableScissorForRenderTypeDraws(
            var0.left() * var2, var1.getHeight() - var0.bottom() * var2, Math.max(0, var0.width() * var2), Math.max(0, var0.height() * var2)
         );
      }
   }

   public static void clear() {
      MsdfRenderer.flushBatch();
      RenderSystem.disableScissorForRenderTypeDraws();
   }
}
