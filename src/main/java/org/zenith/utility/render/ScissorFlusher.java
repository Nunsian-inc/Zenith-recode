package org.zenith.utility.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.ScissorState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.fog.FogRenderer.FogMode;
import org.zenith.RenderState;
import org.zenith.base.font.MsdfRenderer;
import org.zenith.utility.mixin.accessors.GameRendererAccessor;

public final class ScissorFlusher {
   private static int intValue;

   private ScissorFlusher() {
   }

   public static boolean method6646() {
      return intValue > 0;
   }

   public static void flush() {
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft != null && minecraft.gameRenderer != null) {
         MsdfRenderer.flushBatch();
         GameRendererAccessor gameRenderer = (GameRendererAccessor)(Object)minecraft.gameRenderer;
         if (((ScissorFlusher.Handler)minecraft.gameRenderer.gameRenderState().guiRenderState).zenith$pending()) {
            ScissorState scissorStateForRenderTypeDraws = RenderSystem.getScissorStateForRenderTypeDraws();
            boolean enabled = scissorStateForRenderTypeDraws.enabled();
            int x = scissorStateForRenderTypeDraws.x();
            int y = scissorStateForRenderTypeDraws.y();
            int width = scissorStateForRenderTypeDraws.width();
            int height = scissorStateForRenderTypeDraws.height();
            RenderState.method1901();
            RenderState.method1881();
            intValue++;

            try {
               gameRenderer.zenith$guiRenderer().render();
            } finally {
               intValue--;
               if (enabled) {
                  RenderState.method1900(x, y, width, height);
               }
            }
         }
      }
   }

   public interface Handler {
      boolean zenith$pending();
   }
}
