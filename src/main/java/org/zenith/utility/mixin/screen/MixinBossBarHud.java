package org.zenith.utility.mixin.screen;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.BossHealthOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.client.hud.WatermarkIslandHud;

@Mixin({BossHealthOverlay.class})
public class MixinBossBarHud {
   @Inject(
      method = {"extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void zenith$islandTakesOver(GuiGraphicsExtractor guiGraphicsExtractor, CallbackInfo callbackInfo) {
      if (WatermarkIslandHud.method5669()) {
         callbackInfo.cancel();
      }
   }
}
