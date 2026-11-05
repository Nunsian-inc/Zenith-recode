package org.zenith.utility.mixin.scripting;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.multiplayer.ClientLevel.ClientLevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.zenith.utility.misc.ModuleHookDefinitions;

@Mixin(targets = {"net/minecraft/client/ClientClockManager$ClientClockInstance"})
public abstract class MixinScriptWorldTime {
   @ModifyReturnValue(
      method = {"totalTicks()J"},
      at = {@At("RETURN")}
   )
   private long zenith$scriptTime(long returnValue) {
      return !ModuleHookDefinitions.class1212.active() ? returnValue : (long)ModuleHookDefinitions.method6118(ModuleHookDefinitions.class1212, returnValue);
   }
}
