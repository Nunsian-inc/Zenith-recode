package org.zenith.utility.mixin.render;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.FirstPersonHandsAndItemsRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.zenith.utility.render.HeldItemPoseController;

@Mixin({FirstPersonHandsAndItemsRenderer.class})
public class MixinHeldItemRendererWardrobe {
   @WrapOperation(
      method = {"submitArmWithItem(Lnet/minecraft/client/renderer/state/level/PlayerRenderState;Lnet/minecraft/client/renderer/state/level/FirstPersonHandsAndItemsRenderState;FFLnet/minecraft/world/InteractionHand;FLnet/minecraft/world/item/ItemStack;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;I)V"},
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
      @Local(argsOnly = true) ItemStack mainHandItem
   ) {
      // 26.3 first person is always the local player
      LivingEntity player = net.minecraft.client.Minecraft.getInstance().player;
      if (!HeldItemPoseController.method6157(player, mainHandItem, itemStackRenderState, poseStack, orderedRenderCommandQueue, lightCoords)) {
         operation.call(new Object[]{itemStackRenderState, poseStack, orderedRenderCommandQueue, lightCoords, n, n2});
      }
   }
}
