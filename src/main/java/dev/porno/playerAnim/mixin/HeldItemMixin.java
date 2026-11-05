package dev.porno.playerAnim.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.porno.playerAnim.api.PartKey;
import dev.porno.playerAnim.api.TransformType;
import dev.porno.playerAnim.api.firstPerson.FirstPersonConfiguration;
import dev.porno.playerAnim.core.impl.AnimationProcessor;
import dev.porno.playerAnim.core.util.Vec3f;
import dev.porno.playerAnim.impl.IPlayerAnimationState;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandLayer.class)
public class HeldItemMixin {
   @Inject(
      method = "submitArmWithItem(Lnet/minecraft/client/renderer/entity/state/ArmedEntityRenderState;Lnet/minecraft/client/renderer/item/ItemStackRenderState;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/HumanoidArm;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;I)V",
      at = @At("HEAD"),
      cancellable = true
   )
   private void hideFirstPersonItem(
      ArmedEntityRenderState renderState,
      ItemStackRenderState itemStackRenderState,
      ItemStack itemStack,
      HumanoidArm arm,
      PoseStack matrices,
      SubmitNodeCollector submitNodeCollector,
      int i,
      CallbackInfo ci
   ) {
      if (renderState instanceof IPlayerAnimationState state && state.playerAnimator$isFirstPersonPass()) {
         FirstPersonConfiguration config = state.playerAnimator$getAnimationApplier().getFirstPersonConfiguration();
         if (arm == HumanoidArm.LEFT ? !config.isShowLeftItem() : !config.isShowRightItem()) {
            ci.cancel();
         }
      }
   }

   @Inject(
      method = "submitArmWithItem(Lnet/minecraft/client/renderer/entity/state/ArmedEntityRenderState;Lnet/minecraft/client/renderer/item/ItemStackRenderState;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/HumanoidArm;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;I)V",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/item/ItemStackRenderState;submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;III)V"
      )
   )
   private void changeItemLocation(
      ArmedEntityRenderState renderState,
      ItemStackRenderState itemStackRenderState,
      ItemStack itemStack,
      HumanoidArm arm,
      PoseStack matrices,
      SubmitNodeCollector submitNodeCollector,
      int i,
      CallbackInfo ci
   ) {
      if (renderState instanceof IPlayerAnimationState state && state.playerAnimator$getAnimationApplier().isActive()) {
         AnimationProcessor anim = state.playerAnimator$getAnimationApplier();
         Vec3f scale = anim.get3DTransform(arm == HumanoidArm.LEFT ? PartKey.LEFT_ITEM : PartKey.RIGHT_ITEM, TransformType.SCALE, new Vec3f(1.0F, 1.0F, 1.0F));
         Vec3f rot = anim.get3DTransform(arm == HumanoidArm.LEFT ? PartKey.LEFT_ITEM : PartKey.RIGHT_ITEM, TransformType.ROTATION, Vec3f.ZERO);
         Vec3f pos = anim.get3DTransform(arm == HumanoidArm.LEFT ? PartKey.LEFT_ITEM : PartKey.RIGHT_ITEM, TransformType.POSITION, Vec3f.ZERO).scale(0.0625F);
         matrices.scale(scale.getX(), scale.getY(), scale.getZ());
         matrices.translate(pos.getX(), pos.getY(), pos.getZ());
         matrices.rotate(Axis.ZP.rotation(rot.getZ()));
         matrices.rotate(Axis.YP.rotation(rot.getY()));
         matrices.rotate(Axis.XP.rotation(rot.getX()));
      }
   }
}
