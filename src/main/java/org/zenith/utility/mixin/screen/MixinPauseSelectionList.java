package org.zenith.utility.mixin.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractSelectionList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.client.screens.override.main.component.PauseMenuScope;

@Mixin(AbstractSelectionList.class)
public abstract class MixinPauseSelectionList {
   @Inject(method = {"extractListBackground", "extractListSeparators"}, at = @At("HEAD"), cancellable = true)
   private void zenith$transparentPauseList(GuiGraphicsExtractor graphics, CallbackInfo info) {
      if (PauseMenuScope.isDescendant(Minecraft.getInstance().gui.screen())) {
         info.cancel();
      }
   }
}
