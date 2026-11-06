package org.zenith.utility.mixin.input;

import com.darkmagician6.eventapi.EventManager;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.KeyEvent;
import org.zenith.MinecraftAccess;
import org.zenith.client.events.MouseButtonEvent;
import org.zenith.client.events.MouseRotationEvent;
import org.zenith.client.events.MouseScrollEvent;
import org.zenith.utility.misc.ContainerScreenAnimationAccess;

@Mixin({MouseHandler.class})
public class MixinMouse {
   @Shadow
   private MouseButtonInfo activeButton;
   @Shadow
   private double accumulatedDX;
   @Shadow
   private double accumulatedDY;

   @Inject(
      method = {"onButton(JLnet/minecraft/client/input/MouseButtonInfo;I)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onMouseButton(long l, MouseButtonInfo mouseButtonInfo, int intValue, CallbackInfo callbackInfo) {
      int mouseToGlfw = org.zenith.utility.misc.KeyCompat.mouseToGlfw(mouseButtonInfo.button());
      int modifiers = org.zenith.utility.misc.KeyCompat.modsToGlfw(mouseButtonInfo.modifiers());
      if (mouseToGlfw != -1 && l == MinecraftAccess.mc.getWindow().handle()) {
         EventManager.call(new KeyEvent(intValue, mouseToGlfw, modifiers));
         MouseButtonEvent event = new MouseButtonEvent(mouseToGlfw, intValue);
         EventManager.call(event);
         if (event.isCancelled()) {
            callbackInfo.cancel();
         }
      }
   }

   @Inject(
      method = {"onScroll(JDD)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/player/LocalPlayer;getInventory()Lnet/minecraft/world/entity/player/Inventory;"
      )},
      cancellable = true
   )
   public void onMouseScrollHook(long l, double doubleValue, double d, CallbackInfo callbackInfo) {
      MouseScrollEvent event = new MouseScrollEvent(doubleValue, d);
      EventManager.call(event);
      if (event.isCancelled()) {
         callbackInfo.cancel();
      }
   }

   @Redirect(
      method = {"handleAccumulatedMovement()V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/MouseHandler;isMouseGrabbed()Z"
      )
   )
   public boolean onIsCursorLocked(MouseHandler mouseHandler) {
      return mouseHandler.isMouseGrabbed() || this.isAnim();
   }

   @WrapWithCondition(
      method = {"turnPlayer(D)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/player/LocalPlayer;turn(DD)V"
      )},
      require = 1,
      allow = 1
   )
   private boolean modifyMouseRotationInput(LocalPlayer player, double doubleValue, double d) {
      MouseRotationEvent event = new MouseRotationEvent(doubleValue, d, this.accumulatedDX, this.accumulatedDY);
      EventManager.call(event);
      if (event.isCancelled()) {
         return false;
      } else {
         player.turn(event.getDoubleValue(), event.getDoubleValue2());
         return false;
      }
   }

   @Unique
   private boolean isAnim() {
      return Minecraft.getInstance().gui.screen() instanceof ContainerScreenAnimationAccess handler3 && handler3.zenith$betterMinecraft$isClosingAnimation();
   }
}
