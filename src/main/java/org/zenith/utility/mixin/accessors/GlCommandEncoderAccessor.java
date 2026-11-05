package org.zenith.utility.mixin.accessors;

import com.mojang.renderpearl.backend.opengl.GlCommandEncoder;
import com.mojang.renderpearl.backend.opengl.GlProgram;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({GlCommandEncoder.class})
public interface GlCommandEncoderAccessor {
   @Accessor("lastProgram")
   void zenith$setCurrentProgram(GlProgram var1);

   @Accessor("lastPipeline")
   void zenith$setCurrentPipeline(com.mojang.renderpearl.backend.opengl.GlRenderPipeline var1);
}
