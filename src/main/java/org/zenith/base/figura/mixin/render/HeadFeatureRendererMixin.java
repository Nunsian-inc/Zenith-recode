package org.zenith.base.figura.mixin.render;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.object.skull.SkullModelBase;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.resources.model.cuboid.ItemTransform;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.SkullBlock.Type;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.base.figura.avatar.Avatar;
import org.zenith.base.figura.avatar.AvatarManager;
import org.zenith.base.figura.ducks.FiguraEntityRenderStateExtension;
import org.zenith.base.figura.lua.api.world.ItemStackAPI;
import org.zenith.base.figura.model.ParentType;
import org.zenith.base.figura.render.FiguraItemTransforms;
import org.zenith.base.figura.render.FiguraRenderFrames;

@Mixin(CustomHeadLayer.class)
public abstract class HeadFeatureRendererMixin {
   @Unique
   private static final String FIGURA_RENDER_METHOD = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;ILnet/minecraft/client/render/entity/state/LivingEntityRenderState;FF)V";
   @Shadow
   @Final
   private Function<Type, SkullModelBase> skullModels;
   @Unique
   private FiguraRenderFrames.Head figura$headFrame;

   @Shadow
   private RenderType resolveSkullRenderType(LivingEntityRenderState state, Type type) {
      throw new AssertionError();
   }

   @Inject(
      method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;FF)V",
      at = @At("HEAD"),
      cancellable = true
   )
   private void figura$renderAtHelmetPivot(
      PoseStack matrices, SubmitNodeCollector queue, int light, LivingEntityRenderState state, float headYaw, float headPitch, CallbackInfo ci
   ) {
      this.figura$headFrame = null;
      if (!state.headItem.isEmpty() || state.wornHeadType != null) {
         FiguraEntityRenderStateExtension extension = (FiguraEntityRenderStateExtension)state;
         if (figura$getEntity(extension.figura$getEntityId()) instanceof LivingEntity living) {
            List<Avatar> avatars = List.copyOf(AvatarManager.getAvatars(extension.figura$getEntityUuid()));
            if (!avatars.isEmpty()) {
               ItemStack stack = living.getItemBySlot(EquipmentSlot.HEAD);
               boolean hidden = false;
               boolean replaced = false;

               for (Avatar avatar : avatars) {
                  if (avatar.luaRuntime != null) {
                     hidden |= !avatar.luaRuntime.vanilla_model.HELMET_ITEM.checkVisible();
                  }

                  if (state.wornHeadType != null) {
                     replaced |= avatar.pivotPartRender(ParentType.HelmetItemPivot, pivot -> this.figura$renderSkullAtPivot(pivot, queue, light, state));
                  } else {
                     replaced |= avatar.pivotPartRender(
                        ParentType.HelmetItemPivot, pivot -> this.figura$renderItemAtPivot(avatar, pivot, queue, light, state.headItem, stack)
                     );
                  }
               }

               if (hidden || replaced) {
                  ci.cancel();
               } else if (state.wornHeadType == null && !stack.isEmpty()) {
                  this.figura$headFrame = new FiguraRenderFrames.Head(avatars, stack);
               }
            }
         }
      }
   }

   @WrapOperation(
      method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;FF)V",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/item/ItemStackRenderState;submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;III)V"
      )
   )
   private void figura$itemRenderEvent(
      ItemStackRenderState state, PoseStack matrices, SubmitNodeCollector queue, int light, int overlay, int outline, Operation<Void> original
   ) {
      FiguraRenderFrames.Head frame = this.figura$headFrame;
      boolean replaced = false;
      if (frame != null) {
         ItemTransform transform = FiguraItemTransforms.of(state);

         for (Avatar avatar : frame.avatars()) {
            replaced |= avatar.itemRenderEvent(
               ItemStackAPI.verify(frame.stack()),
               ItemDisplayContext.HEAD.name(),
               FiguraItemTransforms.pos(transform),
               FiguraItemTransforms.rot(transform),
               FiguraItemTransforms.scale(transform),
               false,
               matrices,
               queue,
               light,
               overlay
            );
         }
      }

      if (!replaced) {
         original.call(new Object[]{state, matrices, queue, light, overlay, outline});
      }
   }

   @Inject(
      method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;FF)V",
      at = @At("RETURN")
   )
   private void figura$clearHeadFrame(
      PoseStack matrices, SubmitNodeCollector queue, int light, LivingEntityRenderState state, float headYaw, float headPitch, CallbackInfo ci
   ) {
      this.figura$headFrame = null;
   }

   @Unique
   private void figura$renderItemAtPivot(Avatar avatar, PoseStack matrices, SubmitNodeCollector queue, int light, ItemStackRenderState state, ItemStack stack) {
      matrices.translate(0.0, 4.0, 0.0);
      matrices.scale(10.0F, 10.0F, 10.0F);
      ItemTransform transform = FiguraItemTransforms.of(state);
      boolean replaced = avatar.itemRenderEvent(
         ItemStackAPI.verify(stack),
         ItemDisplayContext.HEAD.name(),
         FiguraItemTransforms.pos(transform),
         FiguraItemTransforms.rot(transform),
         FiguraItemTransforms.scale(transform),
         false,
         matrices,
         queue,
         light,
         OverlayTexture.NO_OVERLAY
      );
      if (!replaced) {
         state.submit(matrices, queue, light, OverlayTexture.NO_OVERLAY, 0);
      }
   }

   @Unique
   private void figura$renderSkullAtPivot(PoseStack matrices, SubmitNodeCollector queue, int light, LivingEntityRenderState state) {
      matrices.scale(19.0F, 19.0F, 19.0F);
      // 26.3 submitSkull no longer re-centres (+0.5) or flips the skull, so the old -0.5 offset is gone too
      Type type = state.wornHeadType;
      SkullModelBase model = this.skullModels.apply(type);
      RenderType layer = this.resolveSkullRenderType(state, type);
      SkullBlockRenderer.submitSkull(state.wornHeadAnimationPos, matrices, queue, light, model, layer, state.outlineColor, null);
   }

   @Unique
   private static Entity figura$getEntity(int id) {
      return Minecraft.getInstance().level != null && id != Integer.MIN_VALUE ? Minecraft.getInstance().level.getEntity(id) : null;
   }
}
