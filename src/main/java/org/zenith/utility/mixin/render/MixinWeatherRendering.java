package org.zenith.utility.mixin.render;

import net.minecraft.client.renderer.WeatherEffectRenderer;
import net.minecraft.client.renderer.state.level.WeatherRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome.Precipitation;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.zenith.client.modules.impl.render.WeatherModule;

@Mixin({WeatherEffectRenderer.class})
public class MixinWeatherRendering {
   // 26.3 asks ClientLevel.getPrecipitationAt per column instead of a renderer helper
   @org.spongepowered.asm.mixin.injection.Redirect(
      method = {"extractRenderState(Lnet/minecraft/client/multiplayer/ClientLevel;FLnet/minecraft/world/phys/Vec3;Lnet/minecraft/client/renderer/state/level/WeatherRenderState;)V"},
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;getPrecipitationAt(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/biome/Biome$Precipitation;")
   )
   private Precipitation zenith$forcePrecipitation(net.minecraft.client.multiplayer.ClientLevel level, BlockPos pos) {
      int n = WeatherModule.INSTANCE.method3112();
      if (n >= 0) {
         return switch (n) {
            case 1 -> Precipitation.RAIN;
            case 2 -> Precipitation.SNOW;
            default -> Precipitation.NONE;
         };
      }

      return level.getPrecipitationAt(pos);
   }

   @Inject(
      method = {"extractRenderState(Lnet/minecraft/client/multiplayer/ClientLevel;FLnet/minecraft/world/phys/Vec3;Lnet/minecraft/client/renderer/state/level/WeatherRenderState;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void zenith$skipHiddenPieces(net.minecraft.client.multiplayer.ClientLevel level, float f, Vec3 vec, WeatherRenderState weatherRenderState, CallbackInfo callbackInfo) {
      if (WeatherModule.INSTANCE.method3113()) {
         weatherRenderState.reset();
         callbackInfo.cancel();
      }
   }
}
