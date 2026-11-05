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
import org.zenith.utility.misc.Handler3;

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
   private void onMouseButton(long var1, MouseButtonInfo var3, int var4, CallbackInfo var5) {
      int var6 = org.zenith.utility.misc.KeyCompat.mouseToGlfw(var3.button());
      int var7 = org.zenith.utility.misc.KeyCompat.modsToGlfw(var3.modifiers());
      if (var6 != -1 && var1 == MinecraftAccess.mc.getWindow().handle()) {
         EventManager.call(new KeyEvent(var4, var6, var7));
         MouseButtonEvent var8 = new MouseButtonEvent(var6, var4);
         EventManager.call(var8);
         if (var8.isCancelled()) {
            var5.cancel();
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
   public void onMouseScrollHook(long var1, double var3, double var5, CallbackInfo var7) {
      MouseScrollEvent var8 = new MouseScrollEvent(var3, var5);
      EventManager.call(var8);
      if (var8.isCancelled()) {
         var7.cancel();
      }
   }

   @Redirect(
      method = {"handleAccumulatedMovement()V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/MouseHandler;isMouseGrabbed()Z"
      )
   )
   public boolean onIsCursorLocked(MouseHandler var1) {
      return var1.isMouseGrabbed() || this.isAnim();
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
   private boolean modifyMouseRotationInput(LocalPlayer var1, double var2, double var4) {
      MouseRotationEvent var6 = new MouseRotationEvent(var2, var4, this.accumulatedDX, this.accumulatedDY);
      EventManager.call(var6);
      if (var6.isCancelled()) {
         return false;
      } else {
         var1.turn(var6.getDoubleValue(), var6.getDoubleValue2());
         return false;
      }
   }

   @Unique
   private boolean isAnim() {
      return Minecraft.getInstance().gui.screen() instanceof Handler3 var2 && var2.zenith$betterMinecraft$isClosingAnimation();
   }
}
