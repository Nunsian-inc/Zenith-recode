package org.zenith.utility.render;

import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderSetup.RenderSetupBuilder;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

public final class ChamsRenderLayer {
   private ChamsRenderLayer() {
   }

   @Nullable
   public static RenderType method3202(String var0, @Nullable RenderPipeline var1, @Nullable Identifier var2, boolean var3, boolean var4, boolean var5) {
      if (var1 == null) {
         return null;
      } else {
         RenderSetupBuilder var6 = RenderSetup.builder(var1);
         if (var2 != null) {
            var6.withTexture("Sampler0", var2);
         }

         if (var3) {
            var6.useLightmap();
         }

         if (var4) {
            var6.useOverlay();
         }

         if (var5) {
            var6.sortOnUpload();
         }

         return RenderType.create(var0, var6.createRenderSetup());
      }
   }

   public static RenderPipeline method3203(RenderType var0) {
      return var0.state.pipeline;
   }
}
