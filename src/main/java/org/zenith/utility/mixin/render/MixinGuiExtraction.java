package org.zenith.utility.mixin.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.state.gui.GuiRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.base.font.MsdfRenderer;
import org.zenith.utility.misc.ScissorStack;
import org.zenith.utility.render.ImmediateRenderer;

// 26.3 extracts the whole GUI (Gui.extractRenderState) before the world is drawn, then GameRenderer.render() clears the
// frame. Zenith draws its HUD/menus immediately while the GUI is being extracted, so that would be wiped. Keep the
// 1.21.11 order instead: skip the call in extract() and run it in render() right before the vanilla GUI pass, after the
// world. Gui state that the world pass reads (isHudHidden) just lags one frame.
@Mixin({GameRenderer.class})
public class MixinGuiExtraction {
   @Unique
   private static DeltaTracker zenith$tracker;
   @Unique
   private static boolean zenith$renderLevel;
   @Unique
   private static boolean zenith$loaded;
   @Unique
   private static boolean zenith$pending;

   @Redirect(
      method = {"extract(Lnet/minecraft/client/DeltaTracker;Z)V"},
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;extractRenderState(Lnet/minecraft/client/DeltaTracker;ZZ)V")
   )
   private void zenith$deferGui(Gui gui, DeltaTracker deltaTracker, boolean flag, boolean zenith$loaded2) {
      zenith$tracker = deltaTracker;
      zenith$renderLevel = flag;
      zenith$loaded = zenith$loaded2;
      zenith$pending = true;
   }

   @Inject(method = {"render()V"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/gui/render/GuiRenderer;render()V")})
   private void zenith$extractGui(CallbackInfo callbackInfo) {
      if (zenith$pending) {
         zenith$pending = false;
         Minecraft minecraft = Minecraft.getInstance();
         ImmediateRenderer.method8003();

         try {
            minecraft.gui.extractRenderState(zenith$tracker, zenith$renderLevel, zenith$loaded);
            // the loading overlay asks for a full-frame clear colour; render() already cleared before it was set
            GuiRenderState guiRenderState = minecraft.gameRenderer.gameRenderState().guiRenderState;
            if (!zenith$renderLevel && guiRenderState.clearColorOverride.w() > 0.0F) {
               RenderSystem.getDevice().createCommandEncoder().clearColorTexture(minecraft.gameRenderer.mainRenderTarget().getColorTexture(), guiRenderState.clearColorOverride);
            }
         } finally {
            MsdfRenderer.flushBatch();
            ImmediateRenderer.method8004();
            ScissorStack.clear();
         }
      }
   }
}
