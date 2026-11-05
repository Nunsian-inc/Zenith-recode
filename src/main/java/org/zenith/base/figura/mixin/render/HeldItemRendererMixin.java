package org.zenith.base.figura.mixin.render;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.FirstPersonHandsAndItemsRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.FirstPersonHandsAndItemsRenderState;
import net.minecraft.client.renderer.state.level.PlayerRenderState;
import net.minecraft.client.resources.model.cuboid.ItemTransform;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.base.figura.avatar.Avatar;
import org.zenith.base.figura.avatar.AvatarLayer;
import org.zenith.base.figura.avatar.AvatarManager;
import org.zenith.base.figura.lua.api.world.ItemStackAPI;
import org.zenith.base.figura.math.matrix.FiguraMat4;
import org.zenith.base.figura.model.rendering.EntityRenderMode;
import org.zenith.base.figura.render.FiguraItemTransforms;

// 26.3: ItemInHandRenderer became FirstPersonHandsAndItemsRenderer and works from render states. First person is
// always the local player, held items are pre-resolved ItemStackRenderStates (the old renderItem hook wraps their submit).
@Mixin(FirstPersonHandsAndItemsRenderer.class)
public abstract class HeldItemRendererMixin {
   @Unique
   private static final String HANDS = "submitHandsWithItems(FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/PlayerRenderState;Lnet/minecraft/client/renderer/state/level/FirstPersonHandsAndItemsRenderState;)V";
   @Unique
   private static final String ARM = "submitArmWithItem(Lnet/minecraft/client/renderer/state/level/PlayerRenderState;Lnet/minecraft/client/renderer/state/level/FirstPersonHandsAndItemsRenderState;FFLnet/minecraft/world/InteractionHand;FLnet/minecraft/world/item/ItemStack;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;I)V";
   @Unique
   private final Deque<List<Avatar>> figura$firstPersonFrames = new ArrayDeque<>();
   @Unique
   private final ItemStackRenderState figura$itemState = new ItemStackRenderState();

   @Shadow
   protected abstract void renderPlayerArm(PoseStack matrices, SubmitNodeCollector queue, int light, float equipProgress, float swingProgress, HumanoidArm arm, PlayerRenderState playerState);

   @Inject(method = HANDS, at = @At("HEAD"))
   private void figura$beginFirstPerson(
      float tickDelta, PoseStack matrices, SubmitNodeCollector queue, PlayerRenderState playerState, FirstPersonHandsAndItemsRenderState state, CallbackInfo ci
   ) {
      LocalPlayer player = Minecraft.getInstance().player;
      List<Avatar> avatars = player == null ? List.of() : AvatarManager.getLayers(player.getUUID());
      this.figura$firstPersonFrames.push(avatars);
      FiguraMat4 pose = new FiguraMat4().set(matrices.last().pose());

      for (Avatar avatar : avatars) {
         avatar.renderMode = EntityRenderMode.FIRST_PERSON;
         avatar.renderEvent(tickDelta, pose);
      }
   }

   @Inject(method = HANDS, at = @At("RETURN"))
   private void figura$endFirstPerson(
      float tickDelta, PoseStack matrices, SubmitNodeCollector queue, PlayerRenderState playerState, FirstPersonHandsAndItemsRenderState state, CallbackInfo ci
   ) {
      List<Avatar> avatars = this.figura$firstPersonFrames.poll();
      if (avatars != null) {
         FiguraMat4 pose = new FiguraMat4().set(matrices.last().pose());

         for (Avatar avatar : avatars) {
            avatar.postRenderEvent(tickDelta, pose);
         }
      }
   }

   @Inject(method = ARM, at = @At("HEAD"), cancellable = true)
   private void figura$applyFirstPersonVisibility(
      PlayerRenderState playerState,
      FirstPersonHandsAndItemsRenderState state,
      float tickDelta,
      float pitch,
      InteractionHand hand,
      float swingProgress,
      ItemStack item,
      float equipProgress,
      PoseStack matrices,
      SubmitNodeCollector queue,
      int light,
      CallbackInfo ci
   ) {
      LocalPlayer player = Minecraft.getInstance().player;
      AvatarRenderState avatarState = playerState.avatarRenderState;
      if (player != null && avatarState != null && !state.isScoping) {
         List<Avatar> bodies = AvatarManager.getAvatars(player.getUUID(), AvatarLayer.BODY);
         List<Avatar> items = AvatarManager.getItemAvatars(player.getUUID());
         if (!bodies.isEmpty() || !items.isEmpty()) {
            boolean main = hand == InteractionHand.MAIN_HAND;
            HumanoidArm arm = main ? avatarState.mainArm : avatarState.mainArm.getOpposite();
            boolean willRenderItem = !item.isEmpty();
            boolean willRenderArm = !willRenderItem && main
               || item.has(DataComponents.MAP_ID)
               || !willRenderItem && state.mainHandItem.has(DataComponents.MAP_ID);
            boolean hideArm = false;
            boolean forceArm = false;

            for (Avatar avatar : bodies) {
               if (avatar.luaRuntime != null) {
                  Boolean armVisible = arm == HumanoidArm.LEFT ? avatar.luaRuntime.renderer.renderLeftArm : avatar.luaRuntime.renderer.renderRightArm;
                  hideArm |= Boolean.FALSE.equals(armVisible);
                  forceArm |= Boolean.TRUE.equals(armVisible);
               }
            }

            boolean hideItem = false;

            for (Avatar avatar : items) {
               if (avatar.luaRuntime != null) {
                  hideItem |= !(arm == HumanoidArm.LEFT ? avatar.luaRuntime.vanilla_model.LEFT_ITEM : avatar.luaRuntime.vanilla_model.RIGHT_ITEM)
                     .checkVisible();
               }
            }

            if (willRenderArm && !willRenderItem && hideArm) {
               ci.cancel();
            } else {
               if (!willRenderArm && !avatarState.isInvisible && forceArm) {
                  matrices.pushPose();
                  this.renderPlayerArm(matrices, queue, light, equipProgress, swingProgress, arm, playerState);
                  matrices.popPose();
               }

               if (willRenderItem && hideItem) {
                  ci.cancel();
               }
            }
         }
      }
   }

   @WrapOperation(
      method = ARM,
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/item/ItemStackRenderState;submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;III)V")
   )
   private void figura$replaceItem(
      ItemStackRenderState renderState,
      PoseStack matrices,
      SubmitNodeCollector queue,
      int light,
      int overlay,
      int outline,
      Operation<Void> original,
      @Local(argsOnly = true) PlayerRenderState playerState,
      @Local(argsOnly = true) InteractionHand hand,
      @Local(argsOnly = true) ItemStack stack
   ) {
      LocalPlayer player = Minecraft.getInstance().player;
      AvatarRenderState avatarState = playerState.avatarRenderState;
      if (player != null && avatarState != null && !stack.isEmpty()) {
         List<Avatar> avatars = AvatarManager.getItemAvatars(player.getUUID());
         if (!avatars.isEmpty()) {
            HumanoidArm arm = hand == InteractionHand.MAIN_HAND ? avatarState.mainArm : avatarState.mainArm.getOpposite();
            ItemDisplayContext mode = arm == HumanoidArm.RIGHT ? ItemDisplayContext.FIRST_PERSON_RIGHT_HAND : ItemDisplayContext.FIRST_PERSON_LEFT_HAND;
            Minecraft.getInstance().getItemModelResolver().updateForTopItem(this.figura$itemState, stack, mode, player.level(), player, player.getId() + mode.ordinal());
            ItemTransform transform = FiguraItemTransforms.of(this.figura$itemState);
            boolean replaced = false;

            for (Avatar avatar : avatars) {
               replaced |= avatar.itemRenderEvent(
                  ItemStackAPI.verify(stack),
                  mode.name(),
                  FiguraItemTransforms.pos(transform),
                  FiguraItemTransforms.rot(transform),
                  FiguraItemTransforms.scale(transform),
                  mode.leftHand(),
                  matrices,
                  queue,
                  light,
                  overlay
               );
            }

            if (replaced) {
               return;
            }
         }
      }

      original.call(renderState, matrices, queue, light, overlay, outline);
   }
}
