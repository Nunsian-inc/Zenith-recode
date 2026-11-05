package org.zenith.base.figura.mixin.render;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.resources.model.cuboid.ItemTransform;
import net.minecraft.client.renderer.entity.layers.PlayerItemInHandLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.base.figura.avatar.Avatar;
import org.zenith.base.figura.avatar.AvatarLayer;
import org.zenith.base.figura.avatar.AvatarManager;
import org.zenith.base.figura.ducks.FiguraEntityRenderStateExtension;
import org.zenith.base.figura.lua.api.world.ItemStackAPI;
import org.zenith.base.figura.math.vector.FiguraVec3;
import org.zenith.base.figura.model.ParentType;
import org.zenith.base.figura.render.FiguraItemTransforms;

@Mixin(PlayerItemInHandLayer.class)
public abstract class PlayerHeldItemFeatureRendererMixin {
   @Inject(
      method = "renderItemHeldToEye(Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;Lnet/minecraft/world/entity/HumanoidArm;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;I)V",
      at = @At("HEAD"),
      cancellable = true
   )
   private void figura$renderSpyglassPivot(AvatarRenderState state, HumanoidArm arm, PoseStack matrices, SubmitNodeCollector queue, int light, CallbackInfo ci) {
      ItemStackRenderState itemState = state.heldOnHead;
      if (!itemState.isEmpty()) {
         FiguraEntityRenderStateExtension extension = (FiguraEntityRenderStateExtension)state;
         List<Avatar> items = AvatarManager.getItemAvatars(extension.figura$getEntityUuid());
         if (!items.isEmpty()) {
            List<Avatar> bodies = AvatarManager.getAvatars(extension.figura$getEntityUuid(), AvatarLayer.BODY);
            Minecraft client = Minecraft.getInstance();
            Player player = client.level != null && client.level.getEntity(extension.figura$getEntityId()) instanceof Player value ? value : null;
            if (player != null) {
               boolean left = arm == HumanoidArm.LEFT;

               for (Avatar avatar : items) {
                  if (avatar.luaRuntime != null
                     && !(left ? avatar.luaRuntime.vanilla_model.LEFT_ITEM : avatar.luaRuntime.vanilla_model.RIGHT_ITEM).checkVisible()) {
                     ci.cancel();
                     return;
                  }
               }

               ItemStack stack = player.getUseItem();
               if (stack.isEmpty()) {
                  stack = arm == player.getMainArm() ? player.getMainHandItem() : player.getOffhandItem();
               }

               if (!stack.isEmpty()) {
                  ItemStack renderedStack = stack;
                  ParentType pivot = left ? ParentType.LeftSpyglassPivot : ParentType.RightSpyglassPivot;
                  ItemTransform transform = FiguraItemTransforms.of(itemState);
                  FiguraVec3 pos = FiguraItemTransforms.pos(transform);
                  FiguraVec3 rot = FiguraItemTransforms.rot(transform);
                  FiguraVec3 scale = FiguraItemTransforms.scale(transform);
                  boolean rendered = false;

                  for (Avatar body : bodies) {
                     rendered |= body.pivotPartRender(
                        pivot,
                        pivotStack -> {
                           pivotStack.scale(10.0F, 10.0F, 10.0F);
                           pivotStack.translate(0.0F, 0.0F, 0.4375F);
                           boolean replaced = false;

                           for (Avatar avatar : items) {
                              replaced |= avatar.itemRenderEvent(
                                 ItemStackAPI.verify(renderedStack),
                                 ItemDisplayContext.HEAD.name(),
                                 pos,
                                 rot,
                                 scale,
                                 false,
                                 pivotStack,
                                 queue,
                                 light,
                                 OverlayTexture.NO_OVERLAY
                              );
                           }

                           if (!replaced) {
                              itemState.submit(pivotStack, queue, light, OverlayTexture.NO_OVERLAY, state.outlineColor);
                           }
                        }
                     );
                  }

                  if (rendered) {
                     ci.cancel();
                  }
               }
            }
         }
      }
   }
}
