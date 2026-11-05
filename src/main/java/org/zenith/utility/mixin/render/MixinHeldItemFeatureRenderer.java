package org.zenith.utility.mixin.render;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.zenith.utility.render.HeldItemPoseController;

@Mixin({ItemInHandLayer.class})
public class MixinHeldItemFeatureRenderer {
   @WrapOperation(
      method = {"submitArmWithItem(Lnet/minecraft/client/renderer/entity/state/ArmedEntityRenderState;Lnet/minecraft/client/renderer/item/ItemStackRenderState;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/HumanoidArm;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;I)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/item/ItemStackRenderState;submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;III)V"
      )}
   )
   private void zenith$weaponSkin(
      ItemStackRenderState itemStackRenderState,
      PoseStack poseStack,
      SubmitNodeCollector orderedRenderCommandQueue,
      int lightCoords,
      int n,
      int n2,
      Operation<Void> operation,
      @Local(argsOnly = true) ArmedEntityRenderState armedEntityRenderState,
      @Local(argsOnly = true) ItemStack mainHandItem
   ) {
      if (!HeldItemPoseController.method6156(armedEntityRenderState, itemStackRenderState, mainHandItem, poseStack, orderedRenderCommandQueue, lightCoords)) {
         operation.call(new Object[]{itemStackRenderState, poseStack, orderedRenderCommandQueue, lightCoords, n, n2});
      }
   }
}
