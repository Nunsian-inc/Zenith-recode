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
      ItemStackRenderState var1,
      PoseStack var2,
      SubmitNodeCollector var3,
      int var4,
      int var5,
      int var6,
      Operation<Void> var7,
      @Local(argsOnly = true) ItemStack var9
   ) {
      // 26.3 first person is always the local player
      LivingEntity var8 = net.minecraft.client.Minecraft.getInstance().player;
      if (!HeldItemPoseController.method6157(var8, var9, var1, var2, var3, var4)) {
         var7.call(new Object[]{var1, var2, var3, var4, var5, var6});
      }
   }
}
