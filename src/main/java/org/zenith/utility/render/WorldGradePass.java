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
import org.zenith.utility.misc.CameraFov;

public final class WorldGradePass {
   private static final Identifier identifier = Identifier.fromNamespaceAndPath("zenith", "world_grade");
   private static final PostEffectManager.Entry entry = new PostEffectManager.Entry();

   private WorldGradePass() {
   }

   public static void method6105(FrameGraphBuilder frame, int width, int height, TargetBundle targets, Camera mainCamera) {
      float floatValue = FrameTimer.method755();
      float[] floats = FrameTimer.method756();
      if (floatValue != 1.0F || floats[0] != 1.0F || floats[1] != 1.0F || floats[2] != 1.0F || floats[3] != 1.0F) {
         PostChain postChain = Minecraft.getInstance().getShaderManager().getPostChain(identifier, LevelTargetBundle.MAIN_TARGETS);
         if (postChain != null) {
            Vector4f vector4f = FogStateCache.getFogColor();
            PostEffectManager.method2819(
               postChain,
               identifier,
               entry.method2828("ZenithSaturation", floatValue)
                  .method2828("NearPlane", 0.05F)
                  .method2828("FarPlane", Minecraft.getInstance().gameRenderer.gameRenderState().levelRenderState.cameraRenderState.depthFar)
                  .method2828("TanHalfFov", CameraFov.getTanHalfFov())
                  .method2828("Aspect", width / Math.max((float)height, 1.0F))
                  .method2828("Pitch", (float)Math.toRadians(mainCamera.xRot()))
                  .method2828("FogEnvironmentalStart", FogStateCache.getEnvironmentalStart())
                  .method2828("FogEnvironmentalEnd", FogStateCache.getEnvironmentalEnd())
                  .method2828("FogRenderDistanceStart", FogStateCache.getRenderDistanceStart())
                  .method2828("FogRenderDistanceEnd", FogStateCache.getRenderDistanceEnd())
                  .method2831("ZenithGrade", floats[0], floats[1], floats[2], floats[3])
                  .method2831("FogColor", vector4f.x, vector4f.y, vector4f.z, vector4f.w)
            );
            postChain.addToFrame(frame, width, height, targets);
         }
      }
   }
}
