package org.zenith.utility.mixin.client_core;

import com.darkmagician6.eventapi.EventManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.zenith.Zenith;
import org.zenith.client.events.BlockBreakingEvent;
import org.zenith.client.events.ClickSlotEvent;
import org.zenith.client.events.InteractBlockEvent;
import org.zenith.client.events.InteractItemEvent;

@Mixin({MultiPlayerGameMode.class})
public class MixinClientPlayerInteractionManager {
   @Unique
   public float safeYaw;
   @Unique
   public float safePitch;

   @Inject(
      method = {"handleContainerInput(IIILnet/minecraft/world/inventory/ContainerInput;Lnet/minecraft/world/entity/player/Player;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void clickSlotHook(int intValue, int n, int n2, ContainerInput slotActionType, Player player, CallbackInfo callbackInfo) {
      ClickSlotEvent event = new ClickSlotEvent(intValue, n, n2, slotActionType);
      EventManager.call(event);
      if (event.isCancelled()) {
         callbackInfo.cancel();
      }
   }

   @Inject(
      method = {"continueDestroyBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)Z"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void injectBlockBreaking(BlockPos blockPos, Direction direction, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
      BlockBreakingEvent event = new BlockBreakingEvent(blockPos, direction);
      EventManager.call(event);
      if (event.isCancelled()) {
         callbackInfoReturnable.setReturnValue(false);
      }
   }

   @Inject(
      method = {"useItem(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;"},
      at = {@At("HEAD")}
   )
   private void interact(Player player, InteractionHand interactionHand, CallbackInfoReturnable<InteractionResult> callbackInfoReturnable) {
      if (Zenith.getInstance().getRotationManager().getCurrentRotation() != null && Minecraft.getInstance().player != null) {
         this.safeYaw = Minecraft.getInstance().player.getYRot();
         this.safePitch = Minecraft.getInstance().player.getXRot();
         Minecraft.getInstance().player.setYRot(Zenith.getInstance().getRotationManager().getCurrentRotation().getFloatValue());
         Minecraft.getInstance().player.setXRot(Zenith.getInstance().getRotationManager().getCurrentRotation().getFloatValue2());
      }

      EventManager.call(new InteractItemEvent());
   }

   @Inject(
      method = {"useItem(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;"},
      at = {@At("RETURN")}
   )
   private void interact2(Player player, InteractionHand interactionHand, CallbackInfoReturnable<InteractionResult> callbackInfoReturnable) {
      if (Zenith.getInstance().getRotationManager().getCurrentRotation() != null && Minecraft.getInstance().player != null) {
         Minecraft.getInstance().player.setYRot(this.safeYaw);
         Minecraft.getInstance().player.setXRot(this.safePitch);
      }
   }

   @Inject(
      method = {"useItemOn(Lnet/minecraft/client/player/LocalPlayer;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/BlockHitResult;)Lnet/minecraft/world/InteractionResult;"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void interactBlock(LocalPlayer player, InteractionHand interactionHand, BlockHitResult blockHitResult, CallbackInfoReturnable<InteractionResult> callbackInfoReturnable) {
      InteractBlockEvent event = new InteractBlockEvent();
      EventManager.call(event);
      if (event.isCancelled()) {
         callbackInfoReturnable.setReturnValue(InteractionResult.FAIL);
      }
   }
}
