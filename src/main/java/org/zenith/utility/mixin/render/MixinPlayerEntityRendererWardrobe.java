package org.zenith.utility.mixin.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.utility.render.WardrobeRenderer;

@Mixin({AvatarRenderer.class})
public class MixinPlayerEntityRendererWardrobe {
   @Inject(
      method = {"renderHand(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/resources/Identifier;Lnet/minecraft/client/model/geom/ModelPart;Z)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void zenith$skinArm(PoseStack poseStack, SubmitNodeCollector orderedRenderCommandQueue, int n, Identifier id, ModelPart modelPart, boolean flag, CallbackInfo callbackInfo) {
      if (WardrobeRenderer.method6690((AvatarRenderer)(Object)this, poseStack, orderedRenderCommandQueue, n, modelPart, flag, true)) {
         callbackInfo.cancel();
      }
   }

   @Inject(
      method = {"renderHand(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/resources/Identifier;Lnet/minecraft/client/model/geom/ModelPart;Z)V"},
      at = {@At("TAIL")}
   )
   private void zenith$clothingArm(PoseStack poseStack, SubmitNodeCollector orderedRenderCommandQueue, int n, Identifier id, ModelPart modelPart, boolean flag, CallbackInfo callbackInfo) {
      WardrobeRenderer.method6690((AvatarRenderer)(Object)this, poseStack, orderedRenderCommandQueue, n, modelPart, flag, false);
   }
}
