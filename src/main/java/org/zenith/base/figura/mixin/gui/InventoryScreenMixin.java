package org.zenith.base.figura.mixin.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.base.figura.model.rendering.EntityRenderMode;
import org.zenith.base.figura.render.FiguraGuiRenderContext;

@Mixin(value = InventoryScreen.class, priority = 999)
public abstract class InventoryScreenMixin {
   @Inject(
      method = "extractEntityInInventoryFollowsMouse(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIIIIFFFLnet/minecraft/world/entity/LivingEntity;)V",
      at = @At("HEAD")
   )
   private static void figura$markInventoryRender(
      GuiGraphicsExtractor var0, int var1, int var2, int var3, int var4, int var5, float var6, float var7, float var8, LivingEntity var9, CallbackInfo var10
   ) {
      FiguraGuiRenderContext.mark(var9, EntityRenderMode.MINECRAFT_GUI);
   }
}
