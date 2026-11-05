package org.zenith.utility.mixin.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractSliderButton;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.client.screens.override.main.component.VanillaOptionStyle;

@Mixin({AbstractSliderButton.class})
public abstract class MixinSliderWidget {
   @Shadow
   public double value;

   @Inject(
      method = {"extractWidgetRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void zenith$style(GuiGraphicsExtractor context, int mouseX, int mouseY, float deltaTicks, CallbackInfo callbackInfo) {
      if (VanillaOptionStyle.isActive()) {
         VanillaOptionStyle.drawSlider((AbstractSliderButton)(Object)this, this.value, context, mouseX, mouseY, deltaTicks);
         callbackInfo.cancel();
      }
   }
}
