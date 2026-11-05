package org.zenith.base.figura.mixin.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.List;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.resources.model.cuboid.ItemTransform;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.base.figura.avatar.Avatar;
import org.zenith.base.figura.avatar.AvatarLayer;
import org.zenith.base.figura.avatar.AvatarManager;
import org.zenith.base.figura.ducks.FiguraEntityRenderStateExtension;
import org.zenith.base.figura.lua.api.vanilla_model.VanillaModelPart;
import org.zenith.base.figura.lua.api.world.ItemStackAPI;
import org.zenith.base.figura.math.vector.FiguraVec3;
import org.zenith.base.figura.model.ParentType;
import org.zenith.base.figura.render.FiguraItemTransforms;

@Mixin(ItemInHandLayer.class)
public abstract class HeldItemFeatureRendererMixin {
   @Inject(
      method = "submitArmWithItem(Lnet/minecraft/client/renderer/entity/state/ArmedEntityRenderState;Lnet/minecraft/client/renderer/item/ItemStackRenderState;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/HumanoidArm;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;I)V",
      at = @At("HEAD"),
      cancellable = true
   )
   private void figura$renderHeldItem(
      ArmedEntityRenderState state,
      ItemStackRenderState itemState,
      ItemStack stack,
      HumanoidArm arm,
      PoseStack matrices,
      SubmitNodeCollector queue,
      int light,
      CallbackInfo ci
   ) {
      if (!itemState.isEmpty() && !stack.isEmpty()) {
         FiguraEntityRenderStateExtension extension = (FiguraEntityRenderStateExtension)state;
         List<Avatar> items = AvatarManager.getItemAvatars(extension.figura$getEntityUuid());
         if (!items.isEmpty()) {
            List<Avatar> bodies = AvatarManager.getAvatars(extension.figura$getEntityUuid(), AvatarLayer.BODY);
            boolean left = arm == HumanoidArm.LEFT;
            String mode = left ? "THIRD_PERSON_LEFT_HAND" : "THIRD_PERSON_RIGHT_HAND";
            ItemTransform transform = FiguraItemTransforms.of(itemState);
            FiguraVec3 transformPos = FiguraItemTransforms.pos(transform);
            FiguraVec3 transformRot = FiguraItemTransforms.rot(transform);
            FiguraVec3 transformScale = FiguraItemTransforms.scale(transform);

            for (Avatar avatar : items) {
               if (avatar.luaRuntime != null) {
                  VanillaModelPart itemPart = left ? avatar.luaRuntime.vanilla_model.LEFT_ITEM : avatar.luaRuntime.vanilla_model.RIGHT_ITEM;
                  if (!itemPart.checkVisible()) {
                     ci.cancel();
                     return;
                  }
               }
            }

            ParentType pivot = left ? ParentType.LeftItemPivot : ParentType.RightItemPivot;
            boolean renderedAtPivot = false;

            for (Avatar body : bodies) {
               renderedAtPivot |= body.pivotPartRender(
                  pivot,
                  pivotMatrices -> {
                     pivotMatrices.scale(16.0F, 16.0F, 16.0F);
                     pivotMatrices.rotate(Axis.XP.rotationDegrees(-90.0F));
                     boolean replacedx = false;

                     for (Avatar avatarx : items) {
                        replacedx |= avatarx.itemRenderEvent(
                           ItemStackAPI.verify(stack),
                           mode,
                           transformPos,
                           transformRot,
                           transformScale,
                           left,
                           pivotMatrices,
                           queue,
                           light,
                           OverlayTexture.NO_OVERLAY
                        );
                     }

                     if (!replacedx) {
                        itemState.submit(pivotMatrices, queue, light, OverlayTexture.NO_OVERLAY, state.outlineColor);
                     }
                  }
               );
            }

            if (renderedAtPivot) {
               ci.cancel();
            } else {
               matrices.pushPose();
               ItemInHandLayer<?, ?> renderer = (ItemInHandLayer<?, ?>)(Object)this;
               ArmedModel<ArmedEntityRenderState> model = (ArmedModel<ArmedEntityRenderState>)renderer.getParentModel();
               model.translateToHand(state, arm, matrices);
               matrices.rotate(Axis.XP.rotationDegrees(-90.0F));
               matrices.rotate(Axis.YP.rotationDegrees(180.0F));
               matrices.translate((left ? -1 : 1) / 16.0F, 0.125F, -0.625F);
               boolean replaced = false;

               for (Avatar avatar : items) {
                  replaced |= avatar.itemRenderEvent(
                     ItemStackAPI.verify(stack), mode, transformPos, transformRot, transformScale, left, matrices, queue, light, OverlayTexture.NO_OVERLAY
                  );
               }

               matrices.popPose();
               if (replaced) {
                  ci.cancel();
               }
            }
         }
      }
   }
}
