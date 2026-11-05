package org.zenith.utility.mixin.render;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EndCrystalRenderer;
import net.minecraft.client.renderer.entity.state.EndCrystalRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.rendertype.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.zenith.utility.render.CrystalAnimation;

@Mixin({EndCrystalRenderer.class})
public class MixinEndCrystalEntityRenderer {
   @WrapOperation(
      method = {"submit(Lnet/minecraft/client/renderer/entity/state/EndCrystalRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/SubmitNodeCollector;submitModel(Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/resources/Identifier;III)V"
      )}
   )
   private void zenith$crystalSkin(
      SubmitNodeCollector orderedRenderCommandQueue,
      Model<?> model,
      Object obj,
      PoseStack poseStack,
      net.minecraft.resources.Identifier id,
      int n,
      int n2,
      int n3,
      Operation<Void> operation
   ) {
      if (!(obj instanceof EndCrystalRenderState endCrystalRenderState && CrystalAnimation.method4463(endCrystalRenderState, poseStack, orderedRenderCommandQueue))) {
         operation.call(new Object[]{orderedRenderCommandQueue, model, obj, poseStack, id, n, n2, n3});
      }
   }
}
