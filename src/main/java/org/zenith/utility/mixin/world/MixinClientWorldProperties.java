package org.zenith.utility.mixin.world;

import net.minecraft.client.multiplayer.ClientLevel.ClientLevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.client.modules.impl.render.WeatherModule;
import org.zenith.client.modules.impl.render.WorldModule;

// 26.x: time of day lives in world clocks, the client reads it through ClientClockInstance (sky, environment
// attributes, getDefaultClockTime). Same override as the old ClientLevelData.setDayTime hook: a fixed time of day.
@Mixin(targets = {"net/minecraft/client/ClientClockManager$ClientClockInstance"})
public abstract class MixinClientWorldProperties {
   @Unique
   private static long zenith$override() {
      float f = WorldModule.INSTANCE.method5647();
      if (f == -1.0F) {
         f = WeatherModule.INSTANCE.method3109();
      }

      if (f != -1.0F) {
         float f2 = (f % 24.0F + 24.0F) % 24.0F;
         return (long)((f2 + 18.0F) % 24.0F * 1000.0F);
      } else {
         return -1L;
      }
   }

   @com.llamalad7.mixinextras.injector.ModifyReturnValue(method = {"totalTicks()J"}, at = {@At("RETURN")})
   private long setTimeOfDayHook(long timeOfDayHook) {
      long l = zenith$override();
      return l >= 0L ? l : timeOfDayHook;
   }

   @com.llamalad7.mixinextras.injector.ModifyReturnValue(method = {"partialTick()F"}, at = {@At("RETURN")})
   private float zenith$freezePartial(float f) {
      return zenith$override() >= 0L ? 0.0F : f;
   }
}
