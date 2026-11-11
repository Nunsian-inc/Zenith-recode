package org.zenith.utility.mixin.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.client.screens.override.main.component.VanillaOptionStyle;

@Mixin({AbstractButton.class})
public abstract class MixinPressableWidget {
   @Inject(
      method = {"extractWidgetRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void zenith$style(GuiGraphicsExtractor context, int mouseX, int mouseY, float deltaTicks, CallbackInfo callbackInfo) {
      AbstractButton button = (AbstractButton)(Object)this;
      boolean pauseMenu = org.zenith.client.screens.override.main.component.PauseMenuScope.isDescendant(
         net.minecraft.client.Minecraft.getInstance().gui.screen());
      if (VanillaOptionStyle.isActive() && (!pauseMenu || button.getWidth() > button.getHeight()
         && (button.getClass() == net.minecraft.client.gui.components.Button.Plain.class
            || button instanceof net.minecraft.client.gui.components.CycleButton<?>))) {
         VanillaOptionStyle.drawButton((AbstractButton)(Object)this, context, mouseX, mouseY, deltaTicks);
         callbackInfo.cancel();
      }
   }
}
