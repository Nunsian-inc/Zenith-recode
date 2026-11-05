package org.zenith.utility.mixin.client_core;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.events.GuiEventListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.utility.misc.TextInputCompat;

// See TextInputCompat: a vanilla EditBox losing focus inside a Zenith screen must not leave Zenith fields without text input.
@Mixin({Minecraft.class})
public class MixinMinecraftTextInput {
   @Inject(method = {"onTextInputFocusChange(Lnet/minecraft/client/gui/components/events/GuiEventListener;Z)V"}, at = {@At("TAIL")})
   private void zenith$keepTextInputForZenithScreens(GuiEventListener listener, boolean focused, CallbackInfo ci) {
      if (!focused) {
         TextInputCompat.onFocusReleased((Minecraft)(Object)this);
      }
   }
}
