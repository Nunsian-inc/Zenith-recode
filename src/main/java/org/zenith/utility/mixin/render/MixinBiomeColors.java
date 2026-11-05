package org.zenith.utility.mixin.render;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.zenith.client.modules.impl.render.WeatherModule;

@Mixin({BiomeColors.class})
public class MixinBiomeColors {
   private static final int FROZEN_GRASS = 14215388;
   private static final int FROZEN_FOLIAGE = 13821144;

   @ModifyReturnValue(
      method = {"getAverageGrassColor(Lnet/minecraft/client/renderer/block/BlockAndTintGetter;Lnet/minecraft/core/BlockPos;)I"},
      at = {@At("RETURN")}
   )
   private static int zenith$frostGrass(int n, BlockAndTintGetter blockAndTintGetter, BlockPos pos) {
      float f = WeatherModule.INSTANCE.method3101();
      return f > 0.001F ? blend(n, 14215388, f) : n;
   }

   @ModifyReturnValue(
      method = {"getAverageFoliageColor(Lnet/minecraft/client/renderer/block/BlockAndTintGetter;Lnet/minecraft/core/BlockPos;)I"},
      at = {@At("RETURN")}
   )
   private static int zenith$frostFoliage(int n, BlockAndTintGetter blockAndTintGetter, BlockPos pos) {
      float f = WeatherModule.INSTANCE.method3101();
      return f > 0.001F ? blend(n, 13821144, f) : n;
   }

   private static int blend(int n, int n2, float f) {
      int channel2 = channel(n >> 16, n2 >> 16, f);
      int channel3 = channel(n >> 8, n2 >> 8, f);
      int channel4 = channel(n, n2, f);
      return channel2 << 16 | channel3 << 8 | channel4;
   }

   private static int channel(int n, int n2, float f) {
      int n3 = n & 0xFF;
      int n4 = n2 & 0xFF;
      return Math.clamp((long)((int)(n3 + (n4 - n3) * f + 0.5F)), 0, 255);
   }
}
