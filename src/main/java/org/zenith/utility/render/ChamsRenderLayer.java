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
   public static RenderType method3202(String name, @Nullable RenderPipeline pipeline, @Nullable Identifier texture, boolean flag, boolean flag2, boolean flag3) {
      if (pipeline == null) {
         return null;
      } else {
         RenderSetupBuilder builder = RenderSetup.builder(pipeline);
         if (texture != null) {
            builder.withTexture("Sampler0", texture);
         }

         if (flag) {
            builder.useLightmap();
         }

         if (flag2) {
            builder.useOverlay();
         }

         if (flag3) {
            builder.sortOnUpload();
         }

         return RenderType.create(name, builder.createRenderSetup());
      }
   }

   public static RenderPipeline method3203(RenderType renderType) {
      return renderType.state.pipeline;
   }
}
