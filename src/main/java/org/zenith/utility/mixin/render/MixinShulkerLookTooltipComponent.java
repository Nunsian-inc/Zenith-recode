package org.zenith.utility.mixin.render;

import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.zenith.client.modules.impl.misc.ShulkerPreviewModule;

@Mixin({ClientTooltipComponent.class})
public interface MixinShulkerLookTooltipComponent {
   @Inject(
      method = {"create(Lnet/minecraft/world/inventory/tooltip/TooltipComponent;)Lnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipComponent;"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void replaceTooltipFactory(TooltipComponent tooltipComponent, CallbackInfoReturnable<ClientTooltipComponent> callbackInfoReturnable) {
      ClientTooltipComponent returnValue = ShulkerPreviewModule.method1388(tooltipComponent);
      if (returnValue != null) {
         callbackInfoReturnable.setReturnValue(returnValue);
      }
   }
}
