package org.zenith.utility.mixin.screen;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.utility.misc.Handler3;

@Mixin({InventoryScreen.class})
public abstract class MixinInventoryScreen extends AbstractRecipeBookScreen<InventoryMenu> {
   public MixinInventoryScreen(InventoryMenu var1, RecipeBookComponent<?> var2, Inventory var3, Component var4) {
      super(var1, var2, var3, var4);
   }

   @Inject(
      method = {"extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V"},
      at = {@At("RETURN")}
   )
   private void zenith$popInventoryScaleAnimation(GuiGraphicsExtractor var1, int var2, int var3, float var4, CallbackInfo var5) {
      Handler3 var6 = (Handler3)(Object)this;
      var6.zenith$betterMinecraft$popScaleIfNeeded(var1);
      var6.zenith$betterMinecraft$finishClosingAnimation();
   }
}
