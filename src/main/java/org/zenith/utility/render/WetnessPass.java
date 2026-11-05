package org.zenith.utility.render;

import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelTargetBundle;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.PostChain.TargetBundle;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector4f;
import org.zenith.client.modules.impl.render.WeatherModule;
import org.zenith.utility.misc.ColorUtils;
import org.zenith.utility.misc.FlakeUtil;
import org.zenith.utility.misc.FrameTimer;
import org.zenith.utility.misc.Util15;
import org.zenith.utility.misc.WeatherDepthTexture;
import org.zenith.utility.misc.WeatherFlakeChannel;
import org.zenith.utility.misc.WeatherUniforms;

public final class WetnessPass {
   private static final Identifier identifier = Identifier.fromNamespaceAndPath("zenith", "weather_gloss");
   private static final float CONST_floatValue = 0.05F;
   private static final PostEffectManager.Entry entry = new PostEffectManager.Entry();
   private static final WeatherUniforms.Data data = new WeatherUniforms.Data(entry);
   private static boolean flag;

   private WetnessPass() {
   }

   public static boolean method3122(Camera mainCamera) {
      return flag && mainCamera.getFluidInCamera() == FogType.NONE;
   }

   public static boolean method3123(FrameGraphBuilder frame, int width, int height, TargetBundle targets, Camera mainCamera) {
      flag = false;
      WeatherModule weatherModule = WeatherModule.INSTANCE;
      if (!weatherModule.method3103()) {
         return false;
      } else if (mainCamera.getFluidInCamera() != FogType.NONE) {
         return false;
      } else {
         WeatherFlakeChannel weatherFlakeChannel = weatherModule.method3085();
         float f = weatherModule.method3100();
         float depthFar = f * weatherFlakeChannel.floatValue23;
         if (depthFar <= 0.005F) {
            return false;
         } else {
            Minecraft minecraft = Minecraft.getInstance();
            WeatherDepthTexture.method1386();
            PostChain postChain = minecraft.getShaderManager().getPostChain(identifier, LevelTargetBundle.MAIN_TARGETS);
            if (postChain == null) {
               return false;
            } else {
               float f2 = width / Math.max((float)height, 1.0F);
               float f3 = Util15.method2704();
               float f4 = (float)Math.toRadians(mainCamera.xRot());
               float f5 = (float)(Math.PI - Math.toRadians(mainCamera.yRot()));
               float depthFar2 = minecraft.gameRenderer.gameRenderState().levelRenderState.cameraRenderState.depthFar;
               float depthFar3 = weatherFlakeChannel.flag
                  ? Math.min(depthFar2, Math.max(weatherFlakeChannel.floatValue14 * 1.6F, 192.0F))
                  : Math.clamp(weatherFlakeChannel.floatValue14 * 1.1F, 24.0F, Math.min(96.0F, depthFar2));
               Vec3 position = mainCamera.position();
               PostEffectManager.Entry entry2 = entry;
               float f6 = weatherModule.method3104();
               WeatherUniforms.method799(data, weatherFlakeChannel, weatherModule.method3098(), f6, weatherModule.method3096(), position.x, position.y, position.z, f4, f5, f3, f2);
               method3125(entry2, "NearPlane", 0.05F);
               method3125(entry2, "FarPlane", depthFar2);
               method3125(entry2, "WetAmount", depthFar);
               method3125(entry2, "DarkAmount", ColorUtils.method6427(weatherFlakeChannel.intValue4));
               method3127(entry2, "WetTint", weatherFlakeChannel.intValue5);
               method3125(entry2, "ReflectAmount", weatherFlakeChannel.floatValue24);
               method3125(entry2, "PuddleAmount", weatherFlakeChannel.floatValue26);
               method3125(entry2, "GlintAmount", weatherFlakeChannel.floatValue25);
               method3125(entry2, "Frost", weatherFlakeChannel.flag ? 1.0F : 0.0F);
               method3125(entry2, "TerrainValid", WeatherDepthBuffer.method5133() ? 1.0F : 0.0F);
               method3125(entry2, "MistAmount", weatherFlakeChannel.floatValue27 * weatherModule.method3098());
               method3127(entry2, "MistTint", weatherFlakeChannel.intValue8);
               method3125(entry2, "MistBase", (float)(position.y - 1.6));
               method3125(entry2, "MistHeight", 3.2F);
               method3124(entry2, "MistWind", weatherFlakeChannel.floatValue20, weatherFlakeChannel.floatValue21);
               float depthFar4 = 0.0F;
               if (weatherFlakeChannel.flag) {
                  depthFar4 = SnowShader.isFlag() ? FlakeUtil.method7506() : 1000000.0F;
               }

               method3125(entry2, "CoverRadius", depthFar4);
               method3125(entry2, "FadeEnd", depthFar3);
               method3125(entry2, "RainAmount", weatherFlakeChannel.intValue2 == 1 ? weatherFlakeChannel.floatValue18 : 0.0F);
               float[] floats = FrameTimer.method756();
               Vector4f vector4f = Util26.getVector4f();
               method3125(entry2, "ZenithSaturation", FrameTimer.method755());
               method3125(entry2, "FogEnvironmentalStart", Util26.getFloatValue());
               method3125(entry2, "FogEnvironmentalEnd", Util26.getFloatValue2());
               method3125(entry2, "FogRenderDistanceStart", Util26.getFloatValue3());
               method3125(entry2, "FogRenderDistanceEnd", Util26.getFloatValue4());
               entry2.method2831("ZenithGrade", floats[0], floats[1], floats[2], floats[3]);
               entry2.method2831("FogColor", vector4f.x, vector4f.y, vector4f.z, vector4f.w);
               PostEffectManager.method2819(postChain, identifier, entry2);
               postChain.addToFrame(frame, width, height, targets);
               flag = true;
               return true;
            }
         }
      }
   }

   private static void method3124(PostEffectManager.Entry entry, String str, float f, float f2) {
      entry.method2829(str, f, f2);
   }

   private static void method3125(PostEffectManager.Entry entry, String str, float depthFar) {
      entry.method2828(str, depthFar);
   }

   private static void method3126(PostEffectManager.Entry entry, String str, float f, float f2, float f3) {
      entry.method2830(str, f, f2, f3);
   }

   private static void method3127(PostEffectManager.Entry entry, String str, int intValue) {
      method3126(entry, str, ColorUtils.method6424(intValue), ColorUtils.method6425(intValue), ColorUtils.method6426(intValue));
   }
}
