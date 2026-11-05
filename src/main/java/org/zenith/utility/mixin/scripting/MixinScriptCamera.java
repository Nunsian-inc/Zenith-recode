package org.zenith.utility.mixin.scripting;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.utility.game.Util4;
import org.zenith.utility.misc.ModuleHookDefinitions;

@Mixin({GameRenderer.class})
public abstract class MixinScriptCamera {
   @Inject(
      method = {"bobHurt(Lnet/minecraft/client/renderer/state/level/CameraRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void zenith$hurtCam(net.minecraft.client.renderer.state.level.CameraRenderState var2, PoseStack var1, CallbackInfo var3) {
      if (ModuleHookDefinitions.method6119(ModuleHookDefinitions.class129)) {
         var3.cancel();
      }
   }

   @Inject(
      method = {"bobView(Lnet/minecraft/client/renderer/state/level/CameraRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void zenith$viewBob(net.minecraft.client.renderer.state.level.CameraRenderState var2, PoseStack var1, CallbackInfo var3) {
      if (ModuleHookDefinitions.method6119(ModuleHookDefinitions.class1210)) {
         var3.cancel();
      }
   }

   // script zoom moved to MixinCamera.calculateFov (26.3 computes fov in Camera)
}
