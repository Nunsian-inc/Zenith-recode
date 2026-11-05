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
      float var4 = WorldModule.INSTANCE.method5647();
      if (var4 == -1.0F) {
         var4 = WeatherModule.INSTANCE.method3109();
      }

      if (var4 != -1.0F) {
         float var5 = (var4 % 24.0F + 24.0F) % 24.0F;
         return (long)((var5 + 18.0F) % 24.0F * 1000.0F);
      } else {
         return -1L;
      }
   }

   @com.llamalad7.mixinextras.injector.ModifyReturnValue(method = {"totalTicks()J"}, at = {@At("RETURN")})
   private long setTimeOfDayHook(long var1) {
      long var3 = zenith$override();
      return var3 >= 0L ? var3 : var1;
   }

   @com.llamalad7.mixinextras.injector.ModifyReturnValue(method = {"partialTick()F"}, at = {@At("RETURN")})
   private float zenith$freezePartial(float var1) {
      return zenith$override() >= 0L ? 0.0F : var1;
   }
}
