package org.zenith.utility.mixin.scripting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.player.PlayerSkin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.zenith.utility.game.ScriptRenderOverrides;

@Mixin({AbstractClientPlayer.class})
public abstract class MixinScriptSkin {
   @Inject(
      method = {"getSkin()Lnet/minecraft/world/entity/player/PlayerSkin;"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private void zenith$scriptSkin(CallbackInfoReturnable<PlayerSkin> callbackInfoReturnable) {
      if (ScriptRenderOverrides.hasSkinOverride()) {
         if ((Object)this == Minecraft.getInstance().player) {
            PlayerSkin returnValue = (PlayerSkin)callbackInfoReturnable.getReturnValue();
            if (returnValue != null) {
               callbackInfoReturnable.setReturnValue(ScriptRenderOverrides.applySkinOverride(returnValue));
            }
         }
      }
   }
}
