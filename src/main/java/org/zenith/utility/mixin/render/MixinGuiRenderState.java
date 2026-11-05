package org.zenith.utility.mixin.render;

import net.minecraft.client.renderer.state.gui.GuiElementRenderState;
import net.minecraft.client.renderer.state.gui.GuiItemRenderState;
import net.minecraft.client.renderer.state.gui.GuiRenderState;
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.utility.render.GuiVertexBatch;
import org.zenith.utility.render.ScissorFlusher;

@Mixin({GuiRenderState.class})
public class MixinGuiRenderState implements ScissorFlusher.Handler {
   @Unique
   private boolean zenith$pending;

   @Inject(
      method = {"addItem(Lnet/minecraft/client/renderer/state/gui/GuiItemRenderState;)V", "addText(Lnet/minecraft/client/renderer/state/gui/GuiTextRenderState;)V", "addPicturesInPictureState(Lnet/minecraft/client/renderer/state/gui/pip/PictureInPictureRenderState;)V", "addGuiElement(Lnet/minecraft/client/renderer/state/gui/GuiElementRenderState;)V", "addBlitToCurrentLayer(Lnet/minecraft/client/renderer/state/gui/BlitRenderState;)V", "addGlyphToCurrentLayer(Lnet/minecraft/client/renderer/state/gui/GuiElementRenderState;)V"},
      at = {@At("HEAD")}
   )
   private void zenith$markPending(CallbackInfo callbackInfo) {
      this.zenith$pending = true;
   }

   @Inject(
      method = {"reset()V"},
      at = {@At("HEAD")}
   )
   private void zenith$clearPending(CallbackInfo callbackInfo) {
      this.zenith$pending = false;
      GuiVertexBatch.clear();
   }

   @ModifyVariable(
      method = {"addGuiElement(Lnet/minecraft/client/renderer/state/gui/GuiElementRenderState;)V"},
      at = @At("HEAD"),
      argsOnly = true
   )
   private GuiElementRenderState zenith$tintSimple(GuiElementRenderState simpleGuiElementRenderState) {
      return GuiVertexBatch.method102(simpleGuiElementRenderState);
   }

   @Inject(
      method = {"addItem(Lnet/minecraft/client/renderer/state/gui/GuiItemRenderState;)V"},
      at = {@At("HEAD")}
   )
   private void zenith$tintItem(GuiItemRenderState guiItemRenderState, CallbackInfo callbackInfo) {
      GuiVertexBatch.method98(guiItemRenderState);
   }

   @Inject(
      method = {"addPicturesInPictureState(Lnet/minecraft/client/renderer/state/gui/pip/PictureInPictureRenderState;)V"},
      at = {@At("HEAD")}
   )
   private void zenith$tintSpecial(PictureInPictureRenderState inPictureRenderState, CallbackInfo callbackInfo) {
      GuiVertexBatch.method98(inPictureRenderState);
   }

   @Override
   public boolean zenith$pending() {
      return this.zenith$pending;
   }
}
