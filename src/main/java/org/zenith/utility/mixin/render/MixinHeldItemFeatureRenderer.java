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
      ItemStackRenderState var1,
      PoseStack var2,
      SubmitNodeCollector var3,
      int var4,
      int var5,
      int var6,
      Operation<Void> var7,
      @Local(argsOnly = true) ArmedEntityRenderState var8,
      @Local(argsOnly = true) ItemStack var9
   ) {
      if (!HeldItemPoseController.method6156(var8, var1, var9, var2, var3, var4)) {
         var7.call(new Object[]{var1, var2, var3, var4, var5, var6});
      }
   }
}
