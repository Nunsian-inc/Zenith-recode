package org.zenith.utility.render;

import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelTargetBundle;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.PostChain.TargetBundle;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import org.zenith.client.modules.impl.render.WeatherModule;
import org.zenith.utility.misc.Util15;
import org.zenith.utility.misc.WeatherUniforms;

public final class SkyPass {
   private static final Identifier identifier = Identifier.fromNamespaceAndPath("zenith", "weather_sky");
   private static final PostEffectManager.Entry entry = new PostEffectManager.Entry();
   private static final WeatherUniforms.Data data = new WeatherUniforms.Data(entry);

   private SkyPass() {
   }

   public static boolean method7267(FrameGraphBuilder frame, int width, int height, TargetBundle targets, Camera mainCamera) {
      WeatherModule weatherModule = WeatherModule.INSTANCE;
      if (!weatherModule.method3099()) {
         return false;
      } else {
         PostChain postChain = Minecraft.getInstance().getShaderManager().getPostChain(identifier, LevelTargetBundle.MAIN_TARGETS);
         if (postChain == null) {
            return false;
         } else {
            float f = width / Math.max((float)height, 1.0F);
            float f2 = Util15.method2704();
            float f3 = (float)Math.toRadians(mainCamera.xRot());
            float f4 = (float)(Math.PI - Math.toRadians(mainCamera.yRot()));
            Vec3 position = mainCamera.position();
            PostEffectManager.Entry entry2 = entry;
            WeatherUniforms.method799(
               data, weatherModule.method3085(), weatherModule.method3098(), weatherModule.method3104(), weatherModule.method3096(), position.x, position.y, position.z, f3, f4, f2, f
            );
            PostEffectManager.method2819(postChain, identifier, entry2);
            postChain.addToFrame(frame, width, height, targets);
            return true;
         }
      }
   }
}
