package org.zenith.utility.mixin.scripting;

import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine.PlayResult;
import net.minecraft.client.sounds.SoundManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.zenith.utility.misc.ModuleHookDefinitions;

@Mixin({SoundManager.class})
public abstract class MixinScriptSound {
   @Inject(
      method = {"play(Lnet/minecraft/client/resources/sounds/SoundInstance;)Lnet/minecraft/client/sounds/SoundEngine$PlayResult;"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void zenith$scriptSound(SoundInstance soundInstance, CallbackInfoReturnable<PlayResult> callbackInfoReturnable) {
      if (ModuleHookDefinitions.class1215.active() && soundInstance != null) {
         ModuleHookDefinitions.Entry2 entry = new ModuleHookDefinitions.Entry2(
            String.valueOf(soundInstance.getIdentifier()), soundInstance.getSource().getName(), soundInstance.getX(), soundInstance.getY(), soundInstance.getZ(), 1.0F, 1.0F
         );
         if (ModuleHookDefinitions.class1215.dispatch(entry).cancelled) {
            callbackInfoReturnable.setReturnValue(PlayResult.NOT_STARTED);
         }
      }
   }
}
