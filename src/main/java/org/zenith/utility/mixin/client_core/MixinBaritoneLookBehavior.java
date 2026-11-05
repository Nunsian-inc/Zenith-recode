package org.zenith.utility.mixin.client_core;

import baritone.api.utils.Rotation;
import baritone.behavior.LookBehavior;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.base.rotation.RotationLock;

@Mixin(
   value = {LookBehavior.class},
   remap = false
)
public abstract class MixinBaritoneLookBehavior {
   @Inject(
      method = {"updateTarget(Lbaritone/api/utils/Rotation;Z)V"},
      at = {@At("HEAD")},
      cancellable = true,
      require = 0
   )
   private void zenith$interceptLook(Rotation rotation, boolean flag, CallbackInfo callbackInfo) {
      if (rotation != null && RotationLock.method2706()) {
         RotationLock.method2707(rotation.getYaw(), rotation.getPitch());
         callbackInfo.cancel();
      }
   }
}
