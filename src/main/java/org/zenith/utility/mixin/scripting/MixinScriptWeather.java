package org.zenith.utility.mixin.scripting;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.zenith.utility.misc.ModuleHookDefinitions;

@Mixin({Level.class})
public abstract class MixinScriptWeather {
   @ModifyReturnValue(
      method = {"getRainLevel(F)F"},
      at = {@At("RETURN")}
   )
   private float zenith$scriptRain(float returnValue) {
      return ModuleHookDefinitions.class1213.active() && (Object)this instanceof ClientLevel
         ? (float)Math.max(0.0, Math.min(1.0, ModuleHookDefinitions.method6118(ModuleHookDefinitions.class1213, returnValue)))
         : returnValue;
   }

   @ModifyReturnValue(
      method = {"getThunderLevel(F)F"},
      at = {@At("RETURN")}
   )
   private float zenith$scriptThunder(float returnValue) {
      return ModuleHookDefinitions.class1214.active() && (Object)this instanceof ClientLevel
         ? (float)Math.max(0.0, Math.min(1.0, ModuleHookDefinitions.method6118(ModuleHookDefinitions.class1214, returnValue)))
         : returnValue;
   }
}
