package org.zenith.utility.mixin.render;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.zenith.client.modules.impl.render.WeatherModule;

@Mixin({BlockColors.class})
public class MixinBlockColors {
   // 26.3: tints are BlockTintSource objects (some cached at model bake), so wrap them and frost at colour time
   @ModifyReturnValue(method = {"getTintSources(Lnet/minecraft/world/level/block/state/BlockState;)Ljava/util/List;"}, at = {@At("RETURN")})
   private java.util.List<net.minecraft.client.color.block.BlockTintSource> zenith$frostSources(java.util.List<net.minecraft.client.color.block.BlockTintSource> var1) {
      return var1.isEmpty() || org.zenith.client.modules.impl.render.WeatherModule.INSTANCE.method3101() <= 0.001F ? var1 : var1.stream().<net.minecraft.client.color.block.BlockTintSource>map(org.zenith.utility.render.FrostTintSource::new).toList();
   }

   @ModifyReturnValue(method = {"getTintSource(Lnet/minecraft/world/level/block/state/BlockState;I)Lnet/minecraft/client/color/block/BlockTintSource;"}, at = {@At("RETURN")})
   private net.minecraft.client.color.block.BlockTintSource zenith$frostSource(net.minecraft.client.color.block.BlockTintSource var1) {
      return var1 == null || org.zenith.client.modules.impl.render.WeatherModule.INSTANCE.method3101() <= 0.001F ? var1 : new org.zenith.utility.render.FrostTintSource(var1);
   }

}
