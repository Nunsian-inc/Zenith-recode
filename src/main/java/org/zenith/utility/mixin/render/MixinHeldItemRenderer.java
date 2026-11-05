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
   public abstract void swingArm(float var1, PoseStack var2, int var3, HumanoidArm var4);

   @Inject(
      method = {"submitHandsWithItems(FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/PlayerRenderState;Lnet/minecraft/client/renderer/state/level/FirstPersonHandsAndItemsRenderState;)V"},
      at = {@At("HEAD")}
   )
   private void handFireBeginFrame(CallbackInfo var6) {
      HandFireModule var7 = HandFireModule.INSTANCE;
      if (var7.method6612()) {
         var7.method6617();
      }
   }

   @Inject(
      method = {"submitHandsWithItems(FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/PlayerRenderState;Lnet/minecraft/client/renderer/state/level/FirstPersonHandsAndItemsRenderState;)V"},
      at = {@At("TAIL")}
   )
   private void handFireEndFrame(CallbackInfo var6) {
      HandFireModule var7 = HandFireModule.INSTANCE;
      if (var7.method6612()) {
         var7.method6618();
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
      PlayerRenderState var0,
      FirstPersonHandsAndItemsRenderState var00,
      float var2,
      float var3,
      InteractionHand var4,
      float var5,
      ItemStack var6,
      float var7,
      PoseStack var8,
      SubmitNodeCollector var9,
      int var10,
      CallbackInfo var11
   ) {
      ViewModelModule var12 = ViewModelModule.INSTANCE;
      if (var12.isEnabled()) {
         boolean var13 = var4 == InteractionHand.MAIN_HAND;
         HumanoidArm var14 = var13 ? Minecraft.getInstance().player.getMainArm() : Minecraft.getInstance().player.getMainArm().getOpposite();
         var12.method235(var8, var14);
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
      PlayerRenderState var0,
      FirstPersonHandsAndItemsRenderState var00,
      float var2,
      float var3,
      InteractionHand var4,
      float var5,
      ItemStack var6,
      float var7,
      PoseStack var8,
      SubmitNodeCollector var9,
      int var10,
      CallbackInfo var11
   ) {
      ViewModelModule var12 = ViewModelModule.INSTANCE;
      if (var12.isEnabled()) {
         boolean var13 = var4 == InteractionHand.MAIN_HAND;
         HumanoidArm var14 = var13 ? Minecraft.getInstance().player.getMainArm() : Minecraft.getInstance().player.getMainArm().getOpposite();
         var12.method235(var8, var14);
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
      PlayerRenderState var0,
      FirstPersonHandsAndItemsRenderState var00,
      float var2,
      float var3,
      InteractionHand var4,
      float var5,
      ItemStack var6,
      float var7,
      PoseStack var8,
      SubmitNodeCollector var9,
      int var10,
      CallbackInfo var11
   ) {
      ViewModelModule var12 = ViewModelModule.INSTANCE;
      if (var12.isEnabled() && !var6.isEmpty() && !var6.has(DataComponents.MAP_ID)) {
         boolean var13 = var4 == InteractionHand.MAIN_HAND;
         HumanoidArm var14 = var13 ? Minecraft.getInstance().player.getMainArm() : Minecraft.getInstance().player.getMainArm().getOpposite();
         var12.method236(var8, var14);
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
      FirstPersonHandsAndItemsRenderer var1, float var2, PoseStack var3, int var4, HumanoidArm var5, @Local(argsOnly = true,ordinal = 3) float var6
   ) {
      SwingAnimationModule var7 = SwingAnimationModule.INSTANCE;
      LocalPlayer var8 = Minecraft.getInstance().player;
      if (var7.active() && var8 != null && var5 == var8.getMainArm()) {
         var7.method3270(var3, var2, var6, var5);
      } else {
         this.swingArm(var2, var3, var4, var5);
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
      FirstPersonHandsAndItemsRenderer var1,
      PlayerRenderState var0,
      FirstPersonHandsAndItemsRenderState var00,
      float var3,
      float var4,
      InteractionHand var5,
      float var6,
      ItemStack var7,
      float var8,
      PoseStack var9,
      SubmitNodeCollector var10,
      int var11,
      Operation<Void> var12
   ) {
      // 26.3 renders hands from render state; first person is always the local player
      AbstractClientPlayer var2 = Minecraft.getInstance().player;
      HeldItemRenderEvent var13 = new HeldItemRenderEvent(var2, var7, var5);
      EventManager.call(var13);
      HandFireModule var14 = HandFireModule.INSTANCE;
      if (var14.method6612()) {
         var14.method6619(
            var13.getAbstractClientPlayerEntity(),
            var3,
            var4,
            var13.getHand(),
            var6,
            var13.getItemStack(),
            var8,
            var9,
            var10,
            var11,
            var9x -> {
               if (!HeldItemAnimator.method4775(var1, var13.getAbstractClientPlayerEntity(), var13.getHand(), var9, var9x, var11)) {
                  var12.call(
                     new Object[]{var1, var0, var00, var3, var4, var13.getHand(), var6, var13.getItemStack(), var8, var9, var9x, var11}
                  );
               }
            }
         );
      } else if (!HeldItemAnimator.method4775(var1, var13.getAbstractClientPlayerEntity(), var13.getHand(), var9, var10, var11)) {
         var12.call(
            new Object[]{var1, var0, var00, var3, var4, var13.getHand(), var6, var13.getItemStack(), var8, var9, var10, var11}
         );
      }
   }

   // deviation: 26.2+ submits hands to the feature dispatcher; there is no BufferSource#endBatch in
   // submitHandsWithItems to fade vanilla hands for HandFire (handFireVanillaFlush), so that fade is not applied.
}
