package org.zenith.utility.render;

import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelTargetBundle;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.PostChain.TargetBundle;
import net.minecraft.resources.Identifier;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.client.modules.impl.render.PredictionsModule;

public final class FirePostPass {
   private static long lastFrameNanos = -1L;
   private static float floatValue;

   private FirePostPass() {
   }

   public static boolean method5079(FrameGraphBuilder frame, int screenWidth, int screenHeight, TargetBundle providedTargets, boolean flag) {
      Identifier id = PredictionsModule.INSTANCE.method2910();
      boolean flag2 = PredictionsModule.INSTANCE.method2913();
      if (PredictionsModule.INSTANCE.method2909() && id != null && flag2) {
         PostChain postChain = Minecraft.getInstance().getShaderManager().getPostChain(id, LevelTargetBundle.OUTLINE_TARGETS);
         if (postChain == null) {
            return false;
         } else {
            method5080(postChain, id, method5082(), flag);
            postChain.addToFrame(frame, screenWidth, screenHeight, providedTargets);
            return true;
         }
      } else {
         lastFrameNanos = -1L;
         return false;
      }
   }

   private static void method5080(PostChain postChain, Identifier id, float floatValue, boolean flag) {
      ZenithColor clientColor = Zenith.getInstance().getStyleManager().getClientColor(0);
      ZenithColor zenithColor = Zenith.getInstance().getStyleManager().getClientColor(45);
      float[] floats = method5081(clientColor);
      float[] floats2 = method5081(zenithColor);
      PostEffectManager.method2819(
         postChain,
         id,
         new PostEffectManager.Entry()
            .method2831("FireColor", floats[0], floats[1], floats[2], floats[3])
            .method2831("EmberColor", floats2[0], floats2[1], floats2[2], floats2[3])
            .method2828("Time", floatValue)
            .method2828("KeepForeign", flag ? 1.0F : 0.0F)
      );
   }

   private static float[] method5081(ZenithColor clientColor) {
      return new float[]{clientColor.getIntValue() / 255.0F, clientColor.getIntValue2() / 255.0F, clientColor.getIntValue3() / 255.0F, clientColor.getIntValue4() / 255.0F};
   }

   private static float method5082() {
      long nanoTime = System.nanoTime();
      if (lastFrameNanos < 0L) {
         lastFrameNanos = nanoTime;
         return floatValue;
      } else {
         float f = Math.min((float)(nanoTime - lastFrameNanos) / 1.0E9F, 0.1F);
         lastFrameNanos = nanoTime;
         floatValue = (floatValue + f) % 100000.0F;
         return floatValue;
      }
   }
}
