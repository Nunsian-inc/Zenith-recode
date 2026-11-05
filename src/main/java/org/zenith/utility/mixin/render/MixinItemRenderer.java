package org.zenith.utility.mixin.render;

import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.item.ItemStackRenderState.FoilType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.zenith.utility.misc.HandRenderState;

// 26.3 removed ItemRenderer#getFoilBuffer; glint is now a FoilType carried by each item submit.
@Mixin({SubmitNodeCollection.class})
public class MixinItemRenderer {
   @ModifyVariable(method = "submitItem", at = @At("HEAD"), argsOnly = true)
   private FoilType noItemGlintInHandFirePass(FoilType var1) {
      return HandRenderState.isActive() ? FoilType.NONE : var1;
   }
}
