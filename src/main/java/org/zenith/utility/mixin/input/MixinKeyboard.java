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
   public void triggerKeyEvent(long var1, int var3, net.minecraft.client.input.KeyEvent var4, CallbackInfo var5) {
      if (org.zenith.utility.misc.KeyCompat.toGlfw(var4.key()) != -1) {
         EventManager.call(new KeyEvent(var3, org.zenith.utility.misc.KeyCompat.toGlfw(var4.key()), org.zenith.utility.misc.KeyCompat.modsToGlfw(var4.modifiers())));
      }
   }
}
