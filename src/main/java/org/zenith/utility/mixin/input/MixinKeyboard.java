package org.zenith.utility.mixin.input;

import com.darkmagician6.eventapi.EventManager;
import net.minecraft.client.KeyboardHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.KeyEvent;

@Mixin({KeyboardHandler.class})
public class MixinKeyboard {
   @Inject(
      method = {"keyPress(JILnet/minecraft/client/input/KeyEvent;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void triggerKeyEvent(long l, int intValue, net.minecraft.client.input.KeyEvent event, CallbackInfo callbackInfo) {
      if (org.zenith.utility.misc.KeyCompat.toGlfw(event.key()) != -1) {
         EventManager.call(new KeyEvent(intValue, org.zenith.utility.misc.KeyCompat.toGlfw(event.key()), org.zenith.utility.misc.KeyCompat.modsToGlfw(event.modifiers())));
      }
   }
}
