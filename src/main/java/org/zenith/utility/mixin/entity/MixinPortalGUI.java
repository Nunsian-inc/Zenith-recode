package org.zenith.utility.mixin.entity;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.zenith.client.modules.impl.misc.PortalGUIModule;

@Mixin(LocalPlayer.class)
public class MixinPortalGUI {
   // Меняем только разрешение экрана, не останавливая таймер перехода и эффекты портала.
   @ModifyExpressionValue(
      method = "handlePortalTransitionEffect(Z)V",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;isAllowedInPortal()Z"),
      require = 1
   )
   private boolean allowScreenInPortal(boolean allowed) {
      return allowed || PortalGUIModule.INSTANCE.isEnabled();
   }
}
