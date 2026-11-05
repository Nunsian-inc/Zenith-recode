package org.zenith.utility.misc;

import com.mojang.renderpearl.api.textures.GpuTextureView;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.Identifier;
import org.zenith.utility.render.WeatherDepthBuffer;

public final class WeatherDepthTexture extends AbstractTexture {
   public static final Identifier identifier = Identifier.fromNamespaceAndPath("zenith", "weather_terrain_depth");
   private static final Identifier identifier2 = identifier.withPath(var0 -> "textures/effect/" + var0 + ".png");
   private static final WeatherDepthTexture INSTANCE = new WeatherDepthTexture();
   private static boolean flag;

   private WeatherDepthTexture() {
   }

   public static void method1386() {
      if (!flag) {
         Minecraft var0 = Minecraft.getInstance();
         if (var0 != null && var0.getTextureManager() != null) {
            var0.getTextureManager().register(identifier2, INSTANCE);
            flag = true;
         }
      }
   }

   @Override
   public GpuTextureView getTextureView() {
      GpuTextureView var1 = WeatherDepthBuffer.method5132();
      return var1 != null ? var1 : Minecraft.getInstance().gameRenderer.mainRenderTarget().getDepthTextureView();
   }

   @Override
   public void close() {
   }
}
