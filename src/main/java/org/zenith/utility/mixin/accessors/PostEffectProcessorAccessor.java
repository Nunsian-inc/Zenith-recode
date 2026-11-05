package org.zenith.utility.mixin.accessors;

import java.util.List;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.PostPass;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({PostChain.class})
public interface PostEffectProcessorAccessor {
   @Accessor("passes")
   List<PostPass> getPasses();
}
