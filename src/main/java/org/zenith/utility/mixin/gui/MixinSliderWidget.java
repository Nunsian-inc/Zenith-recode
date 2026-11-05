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
   private void zenith$style(GuiGraphicsExtractor var1, int var2, int var3, float var4, CallbackInfo var5) {
      if (VanillaOptionStyle.isActive()) {
         VanillaOptionStyle.drawSlider((AbstractSliderButton)(Object)this, this.value, var1, var2, var3, var4);
         var5.cancel();
      }
   }
}
