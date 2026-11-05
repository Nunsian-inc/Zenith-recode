package org.zenith.base.bot.view;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.renderpearl.api.textures.GpuTextureView;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.attribute.EnvironmentAttributes;
import org.zenith.base.bot.world.BotWorld;

public final class BotLightmap implements AutoCloseable {
   private final NativeImage image = new NativeImage(16, 16, false);
   private final DynamicTexture texture = new DynamicTexture(() -> "zenith", this.image);
   private boolean uploaded;

   public GpuTextureView getGlTextureView() {
      return this.texture.getTextureView();
   }

   public void update(BotWorld world) {
      if (!this.uploaded) {
         this.uploaded = true;
         int color = ARGB.color(255, 255, 255, 255);

         for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
               this.image.setPixel(j, i, color);
            }
         }

         this.texture.upload();
      }
   }

   public static float getSkyBrightness(BotWorld world) {
      float dimensionValue = world.environmentAttributes().getDimensionValue(EnvironmentAttributes.SUN_ANGLE);
      float f = 1.0F - (Mth.cos(dimensionValue * (float) (Math.PI * 2)) * 2.0F + 0.2F);
      f = Mth.clamp(f, 0.0F, 1.0F);
      f = 1.0F - f;
      f *= 1.0F - world.getRainLevel(1.0F) * 5.0F / 16.0F;
      f *= 1.0F - world.getThunderLevel(1.0F) * 5.0F / 16.0F;
      return f * 0.8F + 0.2F;
   }

   @Override
   public void close() {
      this.texture.close();
   }
}
