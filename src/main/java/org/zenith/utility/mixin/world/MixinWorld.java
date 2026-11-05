package org.zenith.utility.mixin.world;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.zenith.client.modules.impl.render.WeatherModule;

@Mixin({Level.class})
public class MixinWorld {
   @Inject(
      method = {"getRainLevel(F)F"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void getRainGradient(float f, CallbackInfoReturnable<Float> callbackInfoReturnable) {
      if ((Object)this instanceof ClientLevel) {
         float returnValue = WeatherModule.INSTANCE.method3110();
         if (returnValue >= 0.0F) {
            callbackInfoReturnable.setReturnValue(returnValue);
         }
      }
   }

   @Inject(
      method = {"getThunderLevel(F)F"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void getThunderGradient(float f, CallbackInfoReturnable<Float> callbackInfoReturnable) {
      if ((Object)this instanceof ClientLevel) {
         float returnValue = WeatherModule.INSTANCE.method3111();
         if (returnValue >= 0.0F) {
            callbackInfoReturnable.setReturnValue(returnValue);
         }
      }
   }

   @Inject(
      method = {"isRaining()Z"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void isRaining(CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
      if ((Object)this instanceof ClientLevel) {
         float f = WeatherModule.INSTANCE.method3110();
         if (f >= 0.0F) {
            callbackInfoReturnable.setReturnValue(f > 0.2F);
         }
      }
   }

   @Inject(
      method = {"isThundering()Z"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void isThundering(CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
      if ((Object)this instanceof ClientLevel) {
         float f = WeatherModule.INSTANCE.method3111();
         if (f >= 0.0F) {
            callbackInfoReturnable.setReturnValue(f > 0.2F);
         }
      }
   }
}
