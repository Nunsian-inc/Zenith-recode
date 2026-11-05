package org.zenith.utility.mixin.render;

import com.darkmagician6.eventapi.EventManager;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.FirstPersonHandsAndItemsRenderer;
import net.minecraft.client.renderer.state.level.FirstPersonHandsAndItemsRenderState;
import net.minecraft.client.renderer.state.level.PlayerRenderState;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.RenderState;
import org.zenith.client.events.HeldItemRenderEvent;
import org.zenith.client.modules.impl.render.HandFireModule;
import org.zenith.client.modules.impl.render.SwingAnimationModule;
import org.zenith.client.modules.impl.render.ViewModelModule;
import org.zenith.utility.render.HeldItemAnimator;

@Mixin({FirstPersonHandsAndItemsRenderer.class})
public abstract class MixinHeldItemRenderer {
   @Shadow
   public abstract void swingArm(float f, PoseStack poseStack, int n, HumanoidArm humanoidArm);

   @Inject(
      method = {"submitHandsWithItems(FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/PlayerRenderState;Lnet/minecraft/client/renderer/state/level/FirstPersonHandsAndItemsRenderState;)V"},
      at = {@At("HEAD")}
   )
   private void handFireBeginFrame(CallbackInfo callbackInfo) {
      HandFireModule handFireModule = HandFireModule.INSTANCE;
      if (handFireModule.method6612()) {
         handFireModule.method6617();
      }
   }

   @Inject(
      method = {"submitHandsWithItems(FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/PlayerRenderState;Lnet/minecraft/client/renderer/state/level/FirstPersonHandsAndItemsRenderState;)V"},
      at = {@At("TAIL")}
   )
   private void handFireEndFrame(CallbackInfo callbackInfo) {
      HandFireModule handFireModule = HandFireModule.INSTANCE;
      if (handFireModule.method6612()) {
         handFireModule.method6618();
      }
   }

   @Inject(
      method = {"submitArmWithItem(Lnet/minecraft/client/renderer/state/level/PlayerRenderState;Lnet/minecraft/client/renderer/state/level/FirstPersonHandsAndItemsRenderState;FFLnet/minecraft/world/InteractionHand;FLnet/minecraft/world/item/ItemStack;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;I)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/item/ItemStackRenderState;submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;III)V",
         ordinal = 0
      )}
   )
   public void injectBeforeRenderCrossBowItem(
      PlayerRenderState playerRenderState,
      FirstPersonHandsAndItemsRenderState andItemsRenderState,
      float f,
      float f2,
      InteractionHand interactionHand,
      float f3,
      ItemStack stack,
      float f4,
      PoseStack poseStack,
      SubmitNodeCollector submitNodeCollector,
      int n,
      CallbackInfo callbackInfo
   ) {
      ViewModelModule viewModelModule = ViewModelModule.INSTANCE;
      if (viewModelModule.isEnabled()) {
         boolean flag = interactionHand == InteractionHand.MAIN_HAND;
         HumanoidArm mainArm = flag ? Minecraft.getInstance().player.getMainArm() : Minecraft.getInstance().player.getMainArm().getOpposite();
         viewModelModule.method235(poseStack, mainArm);
      }
   }

   @Inject(
      method = {"submitArmWithItem(Lnet/minecraft/client/renderer/state/level/PlayerRenderState;Lnet/minecraft/client/renderer/state/level/FirstPersonHandsAndItemsRenderState;FFLnet/minecraft/world/InteractionHand;FLnet/minecraft/world/item/ItemStack;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;I)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/item/ItemStackRenderState;submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;III)V",
         ordinal = 1
      )}
   )
   public void injectBeforeRenderItem(
      PlayerRenderState playerRenderState,
      FirstPersonHandsAndItemsRenderState andItemsRenderState,
      float f,
      float f2,
      InteractionHand interactionHand,
      float f3,
      ItemStack stack,
      float f4,
      PoseStack poseStack,
      SubmitNodeCollector submitNodeCollector,
      int n,
      CallbackInfo callbackInfo
   ) {
      ViewModelModule viewModelModule = ViewModelModule.INSTANCE;
      if (viewModelModule.isEnabled()) {
         boolean flag = interactionHand == InteractionHand.MAIN_HAND;
         HumanoidArm mainArm = flag ? Minecraft.getInstance().player.getMainArm() : Minecraft.getInstance().player.getMainArm().getOpposite();
         viewModelModule.method235(poseStack, mainArm);
      }
   }

   @Inject(
      method = {"submitArmWithItem(Lnet/minecraft/client/renderer/state/level/PlayerRenderState;Lnet/minecraft/client/renderer/state/level/FirstPersonHandsAndItemsRenderState;FFLnet/minecraft/world/InteractionHand;FLnet/minecraft/world/item/ItemStack;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;I)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V",
         shift = Shift.AFTER,
         ordinal = 0
      )}
   )
   public void injectAfterMatrixPushHandPosition(
      PlayerRenderState playerRenderState,
      FirstPersonHandsAndItemsRenderState andItemsRenderState,
      float f,
      float f2,
      InteractionHand interactionHand,
      float f3,
      ItemStack stack,
      float f4,
      PoseStack poseStack,
      SubmitNodeCollector submitNodeCollector,
      int n,
      CallbackInfo callbackInfo
   ) {
      ViewModelModule viewModelModule = ViewModelModule.INSTANCE;
      if (viewModelModule.isEnabled() && !stack.isEmpty() && !stack.has(DataComponents.MAP_ID)) {
         boolean flag = interactionHand == InteractionHand.MAIN_HAND;
         HumanoidArm mainArm = flag ? Minecraft.getInstance().player.getMainArm() : Minecraft.getInstance().player.getMainArm().getOpposite();
         viewModelModule.method236(poseStack, mainArm);
      }
   }

   @Redirect(
      method = {"submitArmWithItem(Lnet/minecraft/client/renderer/state/level/PlayerRenderState;Lnet/minecraft/client/renderer/state/level/FirstPersonHandsAndItemsRenderState;FFLnet/minecraft/world/InteractionHand;FLnet/minecraft/world/item/ItemStack;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;I)V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/FirstPersonHandsAndItemsRenderer;swingArm(FLcom/mojang/blaze3d/vertex/PoseStack;ILnet/minecraft/world/entity/HumanoidArm;)V",
         ordinal = 2
      )
   )
   public void redirectSwingArmForCustomAnim(
      FirstPersonHandsAndItemsRenderer handsAndItemsRenderer, float f, PoseStack poseStack, int n, HumanoidArm humanoidArm, @Local(argsOnly = true,ordinal = 3) float f2
   ) {
      SwingAnimationModule swingAnimationModule = SwingAnimationModule.INSTANCE;
      LocalPlayer player = Minecraft.getInstance().player;
      if (swingAnimationModule.active() && player != null && humanoidArm == player.getMainArm()) {
         swingAnimationModule.method3270(poseStack, f, f2, humanoidArm);
      } else {
         this.swingArm(f, poseStack, n, humanoidArm);
      }
   }

   @WrapOperation(
      method = {"submitHandsWithItems(FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/PlayerRenderState;Lnet/minecraft/client/renderer/state/level/FirstPersonHandsAndItemsRenderState;)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/FirstPersonHandsAndItemsRenderer;submitArmWithItem(Lnet/minecraft/client/renderer/state/level/PlayerRenderState;Lnet/minecraft/client/renderer/state/level/FirstPersonHandsAndItemsRenderState;FFLnet/minecraft/world/InteractionHand;FLnet/minecraft/world/item/ItemStack;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;I)V"
      )}
   )
   private void itemRenderHook(
      FirstPersonHandsAndItemsRenderer handsAndItemsRenderer,
      PlayerRenderState playerRenderState,
      FirstPersonHandsAndItemsRenderState andItemsRenderState,
      float f,
      float f2,
      InteractionHand hand,
      float f3,
      ItemStack itemStack,
      float f4,
      PoseStack poseStack,
      SubmitNodeCollector submitNodeCollector,
      int n,
      Operation<Void> operation
   ) {
      // 26.3 renders hands from render state; first person is always the local player
      AbstractClientPlayer player = Minecraft.getInstance().player;
      HeldItemRenderEvent event = new HeldItemRenderEvent(player, itemStack, hand);
      EventManager.call(event);
      HandFireModule handFireModule = HandFireModule.INSTANCE;
      if (handFireModule.method6612()) {
         handFireModule.method6619(
            event.getAbstractClientPlayerEntity(),
            f,
            f2,
            event.getHand(),
            f3,
            event.getItemStack(),
            f4,
            poseStack,
            submitNodeCollector,
            n,
            var9x -> {
               if (!HeldItemAnimator.method4775(handsAndItemsRenderer, event.getAbstractClientPlayerEntity(), event.getHand(), poseStack, var9x, n)) {
                  operation.call(
                     new Object[]{handsAndItemsRenderer, playerRenderState, andItemsRenderState, f, f2, event.getHand(), f3, event.getItemStack(), f4, poseStack, var9x, n}
                  );
               }
            }
         );
      } else if (!HeldItemAnimator.method4775(handsAndItemsRenderer, event.getAbstractClientPlayerEntity(), event.getHand(), poseStack, submitNodeCollector, n)) {
         operation.call(
            new Object[]{handsAndItemsRenderer, playerRenderState, andItemsRenderState, f, f2, event.getHand(), f3, event.getItemStack(), f4, poseStack, submitNodeCollector, n}
         );
      }
   }

   // deviation: 26.2+ submits hands to the feature dispatcher; there is no BufferSource#endBatch in
   // submitHandsWithItems to fade vanilla hands for HandFire (handFireVanillaFlush), so that fade is not applied.
}
