package org.zenith.utility.mixin.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.utility.render.WardrobeRenderer;

@Mixin({HumanoidArmorLayer.class})
public class MixinArmorFeatureRenderer {
   @Inject(
      method = {"submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/HumanoidRenderState;FF)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void zenith$hideArmorOnSkins(
      PoseStack var1, SubmitNodeCollector var2, int var3, HumanoidRenderState var4, float var5, float var6, CallbackInfo var7
   ) {
      if (var4 instanceof AvatarRenderState var8 && WardrobeRenderer.method6688(var8)) {
         var7.cancel();
      }
   }
}
