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
      DeltaTracker var1, boolean var2, boolean var3, CallbackInfo var4, @Local(ordinal = 0) int var5, @Local(ordinal = 1) int var6, @Local GuiGraphicsExtractor var7
   ) {
      ScissorFlusher.flush();
      var7.pose().pushMatrix();

      try {
         EventManager.call(new ScreenRenderEvent(MenuDrawContext.method8080(var7, var5, var6, MinecraftAccess.mc.getDeltaTracker().getGameTimeDeltaPartialTick(false))));
         ScissorFlusher.flush();
      } catch (Exception var9) {
      }

      var7.pose().popMatrix();
      RenderTarget var8 = MinecraftAccess.mc.gameRenderer.mainRenderTarget();
      if (var8.getDepthTexture() != null) {
         RenderSystem.getDevice().createCommandEncoder().clearDepthTexture(var8.getDepthTexture(), RenderCompat.CLEAR_DEPTH);
      }

      RenderState.method1881();
   }

   // moved from MixinMinecraftClient: 26.3 Minecraft.setScreen delegates to Gui.setScreen
   @org.spongepowered.asm.mixin.injection.ModifyVariable(
      method = {"setScreen(Lnet/minecraft/client/gui/screens/Screen;)V"},
      at = @At("HEAD"),
      argsOnly = true
   )
   private net.minecraft.client.gui.screens.Screen mixin$modifySetScreenArg(net.minecraft.client.gui.screens.Screen var1) {
      org.zenith.client.events.SetScreenEvent var2 = new org.zenith.client.events.SetScreenEvent(var1);
      EventManager.call(var2);
      return var2.getScreen();
   }
}
