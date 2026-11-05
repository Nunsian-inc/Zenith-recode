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

   public static boolean method2636(FrameGraphBuilder var0, int var1, int var2, TargetBundle var3) {
      ShaderESPModule var4 = ShaderESPModule.INSTANCE;
      if (var4 != null && var4.isEnabled()) {
         PostChain var5 = Minecraft.getInstance().getShaderManager().getPostChain(identifier, LevelTargetBundle.OUTLINE_TARGETS);
         if (var5 == null) {
            return false;
         } else {
            PostEffectManager.method2819(
               var5,
               identifier,
               new PostEffectManager.Entry()
                  .method2828("OutlineWidth", var4.method4590())
                  .method2828("GlowStrength", var4.method4591())
                  .method2828("FillOpacity", var4.method4592())
                  .method2828("Pulse", var4.method4594())
                  .method2828("Radius", var4.method4593())
            );
            var5.addToFrame(var0, var1, var2, var3);
            return true;
         }
      } else {
         return false;
      }
   }
}
