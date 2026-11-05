package org.zenith.utility.mixin.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.WingsLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.utility.render.WardrobeRenderer;

@Mixin({WingsLayer.class})
public class MixinElytraFeatureRenderer {
   @Inject(
      method = {"submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/HumanoidRenderState;FF)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void zenith$hideWingsUnderWardrobe(
      PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int n, HumanoidRenderState humanoidRenderState, float f, float f2, CallbackInfo callbackInfo
   ) {
      if (humanoidRenderState instanceof AvatarRenderState state && WardrobeRenderer.method6689(state)) {
         callbackInfo.cancel();
      }
   }
}
