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
      Minecraft var0 = Minecraft.getInstance();
      if (var0 != null && var0.gameRenderer != null) {
         MsdfRenderer.flushBatch();
         GameRendererAccessor var1 = (GameRendererAccessor)(Object)var0.gameRenderer;
         if (((ScissorFlusher.Handler)var0.gameRenderer.gameRenderState().guiRenderState).zenith$pending()) {
            ScissorState var2 = RenderSystem.getScissorStateForRenderTypeDraws();
            boolean var3 = var2.enabled();
            int var4 = var2.x();
            int var5 = var2.y();
            int var6 = var2.width();
            int var7 = var2.height();
            RenderState.method1901();
            RenderState.method1881();
            intValue++;

            try {
               var1.zenith$guiRenderer().render();
            } finally {
               intValue--;
               if (var3) {
                  RenderState.method1900(var4, var5, var6, var7);
               }
            }
         }
      }
   }

   public interface Handler {
      boolean zenith$pending();
   }
}
