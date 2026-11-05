package org.zenith.utility.mixin.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.client.modules.impl.render.ShaderHandModule;
import org.zenith.utility.misc.ModuleHookDefinitions;

@Mixin({ScreenEffectRenderer.class})
public class MixinGameOverlayRenderer {
   @Inject(
      method = {"submit"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void deferOverlaysForHandShader(CallbackInfo callbackInfo) {
      if (ShaderHandModule.INSTANCE.method1241()) {
         callbackInfo.cancel();
      }
   }

   @Inject(
      method = {"submitFire"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void removeFireOverlay(CallbackInfo callbackInfo) {
      if (ModuleHookDefinitions.method6119(ModuleHookDefinitions.class125)) {
         callbackInfo.cancel();
      }
   }

   @Inject(
      method = {"submitBlockSprite"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void renderInWallOverlayHook(CallbackInfo callbackInfo) {
      if (ModuleHookDefinitions.method6119(ModuleHookDefinitions.class126)) {
         callbackInfo.cancel();
      }
   }
}
