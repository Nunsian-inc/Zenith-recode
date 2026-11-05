package org.zenith.base.figura.mixin.particle;

import net.minecraft.client.particle.SingleQuadParticle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(SingleQuadParticle.class)
public interface BillboardParticleAccessor {
   @Accessor("quadSize")
   void figura$setScale(float f);

   @Accessor("rCol")
   float figura$getRed();

   @Accessor("gCol")
   float figura$getGreen();

   @Accessor("bCol")
   float figura$getBlue();

   @Accessor("alpha")
   float figura$getAlpha();

   @Invoker("setAlpha")
   void figura$setAlpha(float f);
}
