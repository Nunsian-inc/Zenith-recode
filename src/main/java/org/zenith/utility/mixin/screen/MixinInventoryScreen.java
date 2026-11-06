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
import org.zenith.utility.misc.ContainerScreenAnimationAccess;

@Mixin({InventoryScreen.class})
public abstract class MixinInventoryScreen extends AbstractRecipeBookScreen<InventoryMenu> {
   public MixinInventoryScreen(InventoryMenu menu, RecipeBookComponent<?> recipeBookComponent, Inventory inventory, Component title) {
      super(menu, recipeBookComponent, inventory, title);
   }

   @Inject(
      method = {"extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V"},
      at = {@At("RETURN")}
   )
   private void zenith$popInventoryScaleAnimation(GuiGraphicsExtractor guiGraphicsExtractor, int n, int n2, float f, CallbackInfo callbackInfo) {
      ContainerScreenAnimationAccess handler3 = (ContainerScreenAnimationAccess)(Object)this;
      handler3.zenith$betterMinecraft$popScaleIfNeeded(guiGraphicsExtractor);
      handler3.zenith$betterMinecraft$finishClosingAnimation();
   }
}
