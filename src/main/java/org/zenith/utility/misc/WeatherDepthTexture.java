package org.zenith.utility.misc;

import com.mojang.renderpearl.api.textures.GpuTextureView;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.Identifier;
import org.zenith.utility.render.WeatherDepthBuffer;

public final class WeatherDepthTexture extends AbstractTexture {
   public static final Identifier identifier = Identifier.fromNamespaceAndPath("zenith", "weather_terrain_depth");
   private static final Identifier identifier2 = identifier.withPath(str -> "textures/effect/" + str + ".png");
   private static final WeatherDepthTexture INSTANCE = new WeatherDepthTexture();
   private static boolean flag;

   private WeatherDepthTexture() {
   }

   public static void method1386() {
      if (!flag) {
         Minecraft minecraft = Minecraft.getInstance();
         if (minecraft != null && minecraft.getTextureManager() != null) {
            minecraft.getTextureManager().register(identifier2, INSTANCE);
            flag = true;
         }
      }
   }

   @Override
   public GpuTextureView getTextureView() {
      GpuTextureView gpuTextureView = WeatherDepthBuffer.method5132();
      return gpuTextureView != null ? gpuTextureView : Minecraft.getInstance().gameRenderer.mainRenderTarget().getDepthTextureView();
   }

   @Override
   public void close() {
   }
}
