package org.zenith.utility.mixin.scripting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.player.PlayerSkin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.zenith.utility.game.Util4;

@Mixin({AbstractClientPlayer.class})
public abstract class MixinScriptSkin {
   @Inject(
      method = {"getSkin()Lnet/minecraft/world/entity/player/PlayerSkin;"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private void zenith$scriptSkin(CallbackInfoReturnable<PlayerSkin> var1) {
      if (Util4.method1285()) {
         if ((Object)this == Minecraft.getInstance().player) {
            PlayerSkin var2 = (PlayerSkin)var1.getReturnValue();
            if (var2 != null) {
               var1.setReturnValue(Util4.method1286(var2));
            }
         }
      }
   }
}
