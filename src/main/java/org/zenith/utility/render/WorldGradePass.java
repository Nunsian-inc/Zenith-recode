package org.zenith.utility.render;

import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelTargetBundle;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.PostChain.TargetBundle;
import net.minecraft.resources.Identifier;
import org.joml.Vector4f;
import org.zenith.utility.misc.FrameTimer;
import org.zenith.utility.misc.Util15;

public final class WorldGradePass {
   private static final Identifier identifier = Identifier.fromNamespaceAndPath("zenith", "world_grade");
   private static final PostEffectManager.Entry entry = new PostEffectManager.Entry();

   private WorldGradePass() {
   }

   public static void method6105(FrameGraphBuilder var0, int var1, int var2, TargetBundle var3, Camera var4) {
      float var5 = FrameTimer.method755();
      float[] var6 = FrameTimer.method756();
      if (var5 != 1.0F || var6[0] != 1.0F || var6[1] != 1.0F || var6[2] != 1.0F || var6[3] != 1.0F) {
         PostChain var7 = Minecraft.getInstance().getShaderManager().getPostChain(identifier, LevelTargetBundle.MAIN_TARGETS);
         if (var7 != null) {
            Vector4f var8 = Util26.getVector4f();
            PostEffectManager.method2819(
               var7,
               identifier,
               entry.method2828("ZenithSaturation", var5)
                  .method2828("NearPlane", 0.05F)
                  .method2828("FarPlane", Minecraft.getInstance().gameRenderer.gameRenderState().levelRenderState.cameraRenderState.depthFar)
                  .method2828("TanHalfFov", Util15.method2704())
                  .method2828("Aspect", var1 / Math.max((float)var2, 1.0F))
                  .method2828("Pitch", (float)Math.toRadians(var4.xRot()))
                  .method2828("FogEnvironmentalStart", Util26.getFloatValue())
                  .method2828("FogEnvironmentalEnd", Util26.getFloatValue2())
                  .method2828("FogRenderDistanceStart", Util26.getFloatValue3())
                  .method2828("FogRenderDistanceEnd", Util26.getFloatValue4())
                  .method2831("ZenithGrade", var6[0], var6[1], var6[2], var6[3])
                  .method2831("FogColor", var8.x, var8.y, var8.z, var8.w)
            );
            var7.addToFrame(var0, var1, var2, var3);
         }
      }
   }
}
