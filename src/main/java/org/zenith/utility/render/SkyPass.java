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

   public static boolean method7267(FrameGraphBuilder var0, int var1, int var2, TargetBundle var3, Camera var4) {
      WeatherModule var5 = WeatherModule.INSTANCE;
      if (!var5.method3099()) {
         return false;
      } else {
         PostChain var6 = Minecraft.getInstance().getShaderManager().getPostChain(identifier, LevelTargetBundle.MAIN_TARGETS);
         if (var6 == null) {
            return false;
         } else {
            float var7 = var1 / Math.max((float)var2, 1.0F);
            float var8 = Util15.method2704();
            float var9 = (float)Math.toRadians(var4.xRot());
            float var10 = (float)(Math.PI - Math.toRadians(var4.yRot()));
            Vec3 var11 = var4.position();
            PostEffectManager.Entry var12 = entry;
            WeatherUniforms.method799(
               data, var5.method3085(), var5.method3098(), var5.method3104(), var5.method3096(), var11.x, var11.y, var11.z, var9, var10, var8, var7
            );
            PostEffectManager.method2819(var6, identifier, var12);
            var6.addToFrame(var0, var1, var2, var3);
            return true;
         }
      }
   }
}
