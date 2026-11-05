package org.zenith.base.figura.mixin;

import net.minecraft.world.food.FoodData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(FoodData.class)
public interface HungerManagerAccessor {
   @Accessor("exhaustionLevel")
   float figura$getExhaustion();
}
