package org.zenith.utility.render;

import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelTargetBundle;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.PostChain.TargetBundle;
import net.minecraft.resources.Identifier;
import org.zenith.client.modules.impl.render.ShaderESPModule;

public final class EspGlowPass {
   private static final Identifier identifier = Identifier.fromNamespaceAndPath("zenith", "esp_glow");

   private EspGlowPass() {
   }

   public static boolean method2636(FrameGraphBuilder frame, int screenWidth, int screenHeight, TargetBundle providedTargets) {
      ShaderESPModule shaderESPModule = ShaderESPModule.INSTANCE;
      if (shaderESPModule != null && shaderESPModule.isEnabled()) {
         PostChain postChain = Minecraft.getInstance().getShaderManager().getPostChain(identifier, LevelTargetBundle.OUTLINE_TARGETS);
         if (postChain == null) {
            return false;
         } else {
            PostEffectManager.method2819(
               postChain,
               identifier,
               new PostEffectManager.Entry()
                  .method2828("OutlineWidth", shaderESPModule.method4590())
                  .method2828("GlowStrength", shaderESPModule.method4591())
                  .method2828("FillOpacity", shaderESPModule.method4592())
                  .method2828("Pulse", shaderESPModule.method4594())
                  .method2828("Radius", shaderESPModule.method4593())
            );
            postChain.addToFrame(frame, screenWidth, screenHeight, providedTargets);
            return true;
         }
      } else {
         return false;
      }
   }
}
