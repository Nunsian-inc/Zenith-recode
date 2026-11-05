package org.zenith.utility.mixin.accessors;

import com.mojang.renderpearl.api.buffers.GpuBuffer;
import java.util.Map;
import net.minecraft.client.renderer.PostPass;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({PostPass.class})
public interface PostEffectPassAccessor {
   @Accessor("customUniforms")
   Map<String, GpuBuffer> zenith$uniformBuffers();
}
