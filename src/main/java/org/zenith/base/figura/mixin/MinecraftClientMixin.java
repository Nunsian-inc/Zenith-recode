package org.zenith.base.figura.mixin;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.base.figura.avatar.AvatarManager;
import org.zenith.base.figura.render.FiguraModelEdits;

@Mixin(Minecraft.class)
public abstract class MinecraftClientMixin {
   @Inject(method = "runTick(Z)V", at = @At("HEAD"))
   private void figura$applyAnimations(boolean tick, CallbackInfo ci) {
      AvatarManager.applyAnimations();
   }

   @Inject(method = "runTick(Z)V", at = @At("RETURN"))
   private void figura$clearAnimations(boolean tick, CallbackInfo ci) {
      AvatarManager.clearAnimations();
      FiguraModelEdits.endFrame();
   }
}
