package org.zenith.utility.mixin.client_core;

import com.darkmagician6.eventapi.EventManager;
import com.mojang.blaze3d.platform.Window;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.client.events.WindowResizeEvent;

@Mixin({Window.class})
public class MixinWindow {
   @Inject(
      method = {"onResize(II)V"},
      at = {@At("TAIL")}
   )
   private void onWindowSizeChanged(int var3, int var4, CallbackInfo var5) {
      EventManager.call(new WindowResizeEvent());
   }
}
