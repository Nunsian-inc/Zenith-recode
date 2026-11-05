package org.zenith.utility.render;

import java.util.Set;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.state.properties.Property;
import org.zenith.client.modules.impl.render.WeatherModule;

// WeatherModule frost: wraps vanilla tint sources (see MixinBlockColors); lives outside the mixin package so it can load.
public record FrostTintSource(BlockTintSource src) implements BlockTintSource {
   @Override
   public int color(BlockState state) {
      return frost(this.src.color(state), state);
   }

   @Override
   public int colorInWorld(BlockState state, BlockAndTintGetter level, BlockPos pos) {
      return frost(this.src.colorInWorld(state, level, pos), state);
   }

   @Override
   public int colorAsTerrainParticle(BlockState state, BlockAndTintGetter level, BlockPos pos) {
      return frost(this.src.colorAsTerrainParticle(state, level, pos), state);
   }

   @Override
   public Set<Property<?>> relevantProperties() {
      return this.src.relevantProperties();
   }
   static final int FROZEN_GRASS = frosted(8434839);
   static final int FROZEN_FOLIAGE = frosted(6332795);

   private static int frosted(int n) {
      int n2 = n >> 16 & 0xFF;
      int n3 = n >> 8 & 0xFF;
      int n4 = n & 0xFF;
      n2 += (255 - n2) * 88 / 100;
      n3 += (255 - n3) * 88 / 100;
      n4 += (255 - n4) * 92 / 100;
      return n2 << 16 | n3 << 8 | n4;
   }
   private static int blend(int n, int n2, float f) {
      return Math.clamp((long)((int)(n + (n2 - n) * f)), 0, 255);
   }

   static int frost(int color, BlockState state) {
      float f = WeatherModule.INSTANCE.method3101();
      if (f <= 0.001F) {
         return color;
      } else {
         int n = state.is(BlockTags.LEAVES) ? FROZEN_FOLIAGE : FROZEN_GRASS;
         int blend2 = blend(color >> 16 & 0xFF, n >> 16 & 0xFF, f);
         int blend3 = blend(color >> 8 & 0xFF, n >> 8 & 0xFF, f);
         int blend4 = blend(color & 0xFF, n & 0xFF, f);
         return color & 0xFF000000 | blend2 << 16 | blend3 << 8 | blend4;
      }
   }
}
