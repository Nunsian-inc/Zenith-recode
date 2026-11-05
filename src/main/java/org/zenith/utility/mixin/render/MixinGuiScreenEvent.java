package org.zenith.utility.mixin.render;

import com.darkmagician6.eventapi.EventManager;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.MinecraftAccess;
import org.zenith.RenderState;
import org.zenith.client.events.ScreenRenderEvent;
import org.zenith.utility.render.RenderCompat;
import org.zenith.utility.render.ScissorFlusher;
import org.zenith.utility.render.display.base.MenuDrawContext;

// Zenith HUD layer: between the vanilla HUD and the overlay/screen, like the 1.21.11 GameRenderer.render getOverlay() hook.
// Runs inside the deferred GUI extraction (MixinGuiExtraction), so the world is already drawn.
@Mixin({Gui.class})
public class MixinGuiScreenEvent {
   @Inject(
      method = {"extractRenderState(Lnet/minecraft/client/DeltaTracker;ZZ)V"},
      at = {@At(value = "FIELD", target = "Lnet/minecraft/client/gui/Gui;overlay:Lnet/minecraft/client/gui/screens/Overlay;", ordinal = 0)}
   )
   private void renderScreenHook(
      DeltaTracker deltaTracker, boolean flag, boolean flag2, CallbackInfo callbackInfo, @Local(ordinal = 0) int mouseX, @Local(ordinal = 1) int mouseY, @Local GuiGraphicsExtractor context
   ) {
      ScissorFlusher.flush();
      context.pose().pushMatrix();

      try {
         EventManager.call(new ScreenRenderEvent(MenuDrawContext.method8080(context, mouseX, mouseY, MinecraftAccess.mc.getDeltaTracker().getGameTimeDeltaPartialTick(false))));
         ScissorFlusher.flush();
      } catch (Exception e) {
      }

      context.pose().popMatrix();
      RenderTarget mainRenderTarget = MinecraftAccess.mc.gameRenderer.mainRenderTarget();
      if (mainRenderTarget.getDepthTexture() != null) {
         RenderSystem.getDevice().createCommandEncoder().clearDepthTexture(mainRenderTarget.getDepthTexture(), RenderCompat.CLEAR_DEPTH);
      }

      RenderState.method1881();
   }

   // moved from MixinMinecraftClient: 26.3 Minecraft.setScreen delegates to Gui.setScreen
   @org.spongepowered.asm.mixin.injection.ModifyVariable(
      method = {"setScreen(Lnet/minecraft/client/gui/screens/Screen;)V"},
      at = @At("HEAD"),
      argsOnly = true
   )
   private net.minecraft.client.gui.screens.Screen mixin$modifySetScreenArg(net.minecraft.client.gui.screens.Screen screen) {
      org.zenith.client.events.SetScreenEvent event = new org.zenith.client.events.SetScreenEvent(screen);
      EventManager.call(event);
      return event.getScreen();
   }
}
