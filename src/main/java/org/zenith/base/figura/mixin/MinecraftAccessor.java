package org.zenith.base.figura.mixin;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Minecraft.class)
public interface MinecraftAccessor {
   @Invoker("getTickTargetMillis")
   float figura$getTargetMillisPerTick(float var1);
}
