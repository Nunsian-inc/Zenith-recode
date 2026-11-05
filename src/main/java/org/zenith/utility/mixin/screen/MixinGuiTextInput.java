package org.zenith.utility.mixin.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.utility.misc.TextInputCompat;

// See TextInputCompat: Gui.setScreen stops SDL text input, Zenith screens turn it back on.
@Mixin({Gui.class})
public class MixinGuiTextInput {
   @Shadow
   @Final
   private Minecraft minecraft;

   @Inject(method = {"setScreen(Lnet/minecraft/client/gui/screens/Screen;)V"}, at = {@At("TAIL")})
   private void zenith$startTextInputForZenithScreens(CallbackInfo ci) {
      TextInputCompat.onScreenChanged(this.minecraft);
   }
}
