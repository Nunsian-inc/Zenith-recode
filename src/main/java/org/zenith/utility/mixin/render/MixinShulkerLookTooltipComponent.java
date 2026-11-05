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
   private static void replaceTooltipFactory(TooltipComponent var0, CallbackInfoReturnable<ClientTooltipComponent> var1) {
      ClientTooltipComponent var2 = ShulkerPreviewModule.method1388(var0);
      if (var2 != null) {
         var1.setReturnValue(var2);
      }
   }
}
