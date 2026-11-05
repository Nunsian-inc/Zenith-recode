package org.zenith.utility.mixin.render;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.state.gui.BlitRenderState;
import net.minecraft.client.renderer.state.gui.pip.OversizedItemRenderState;
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.zenith.utility.render.GuiVertexBatch;

@Mixin({PictureInPictureRenderer.class})
public class MixinSpecialGuiElementTint {
   @ModifyArg(
      method = {"blitTexture(Lnet/minecraft/client/renderer/state/gui/pip/PictureInPictureRenderState;Lnet/minecraft/client/renderer/state/gui/GuiRenderState;)V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/state/gui/GuiRenderState;addBlitToCurrentLayer(Lnet/minecraft/client/renderer/state/gui/BlitRenderState;)V"
      )
   )
   private BlitRenderState zenith$elementColor(
      BlitRenderState var1, @Local(argsOnly = true) PictureInPictureRenderState var2
   ) {
      Object var3 = var2 instanceof OversizedItemRenderState var4 ? var4.guiItemRenderState() : var2;
      return GuiVertexBatch.method99(var1, var3);
   }
}
