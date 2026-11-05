package org.zenith.utility.mixin.render;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.renderer.state.gui.BlitRenderState;
import net.minecraft.client.renderer.state.gui.GuiItemRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.utility.render.GuiVertexBatch;
import org.zenith.utility.render.ScissorFlusher;

@Mixin({GuiRenderer.class})
public class MixinGuiRendererTint {
   @Inject(
      method = {"render()V"},
      at = {@At("HEAD")}
   )
   private void zenith$enter(CallbackInfo var1) {
      GuiVertexBatch.method103();
   }

   @Inject(
      method = {"render()V"},
      at = {@At("RETURN")}
   )
   private void zenith$exit(CallbackInfo var1) {
      GuiVertexBatch.method104();
   }

   @Inject(
      method = {"clearUnusedOversizedItemRenderers()V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void zenith$keepOversizedMidFrame(CallbackInfo var1) {
      if (ScissorFlusher.method6646()) {
         var1.cancel();
      }
   }

   @ModifyArg(
      method = {"submitBlitFromItemAtlas(Lnet/minecraft/client/renderer/state/gui/GuiItemRenderState;Lnet/minecraft/client/gui/render/GuiItemAtlas$SlotView;)V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/state/gui/GuiRenderState;addBlitToCurrentLayer(Lnet/minecraft/client/renderer/state/gui/BlitRenderState;)V"
      )
   )
   private BlitRenderState zenith$itemColor(BlitRenderState var1, @Local(argsOnly = true) GuiItemRenderState var2) {
      return GuiVertexBatch.method99(var1, var2);
   }
}
