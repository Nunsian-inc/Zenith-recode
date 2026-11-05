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
   public int color(BlockState var1) {
      return frost(this.src.color(var1), var1);
   }

   @Override
   public int colorInWorld(BlockState var1, BlockAndTintGetter var2, BlockPos var3) {
      return frost(this.src.colorInWorld(var1, var2, var3), var1);
   }

   @Override
   public int colorAsTerrainParticle(BlockState var1, BlockAndTintGetter var2, BlockPos var3) {
      return frost(this.src.colorAsTerrainParticle(var1, var2, var3), var1);
   }

   @Override
   public Set<Property<?>> relevantProperties() {
      return this.src.relevantProperties();
   }
   static final int FROZEN_GRASS = frosted(8434839);
   static final int FROZEN_FOLIAGE = frosted(6332795);

   private static int frosted(int var0) {
      int var1 = var0 >> 16 & 0xFF;
      int var2 = var0 >> 8 & 0xFF;
      int var3 = var0 & 0xFF;
      var1 += (255 - var1) * 88 / 100;
      var2 += (255 - var2) * 88 / 100;
      var3 += (255 - var3) * 92 / 100;
      return var1 << 16 | var2 << 8 | var3;
   }
   private static int blend(int var0, int var1, float var2) {
      return Math.clamp((long)((int)(var0 + (var1 - var0) * var2)), 0, 255);
   }

   static int frost(int var1, BlockState var2) {
      float var6 = WeatherModule.INSTANCE.method3101();
      if (var6 <= 0.001F) {
         return var1;
      } else {
         int var7 = var2.is(BlockTags.LEAVES) ? FROZEN_FOLIAGE : FROZEN_GRASS;
         int var8 = blend(var1 >> 16 & 0xFF, var7 >> 16 & 0xFF, var6);
         int var9 = blend(var1 >> 8 & 0xFF, var7 >> 8 & 0xFF, var6);
         int var10 = blend(var1 & 0xFF, var7 & 0xFF, var6);
         return var1 & 0xFF000000 | var8 << 16 | var9 << 8 | var10;
      }
   }
}
