package org.zenith.utility.mixin.world;

import java.util.Optional;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.zenith.client.modules.impl.misc.ShulkerPreviewModule;

@Mixin({ItemStack.class})
public abstract class MixinShulkerLookItemStack {
   @Inject(
      method = {"getTooltipImage()Ljava/util/Optional;"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void injectShulkerTooltipData(CallbackInfoReturnable<Optional<TooltipComponent>> callbackInfoReturnable) {
      var returnValue = ShulkerPreviewModule.method1387((ItemStack)(Object)this);
      if (returnValue.isPresent()) {
         callbackInfoReturnable.setReturnValue(returnValue);
      }
   }
}
