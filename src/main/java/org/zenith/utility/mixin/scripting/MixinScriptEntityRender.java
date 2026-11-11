package org.zenith.utility.mixin.scripting;

import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.zenith.utility.game.ScriptRenderOverrides;

@Mixin({EntityRenderDispatcher.class})
public abstract class MixinScriptEntityRender {
   @Inject(
      method = {"shouldRender(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/client/renderer/culling/Frustum;DDDF)Z"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private <E extends Entity> void zenith$scriptHidden(E value2, Frustum frustum, double d, double d2, double d3, float f, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
      if (ScriptRenderOverrides.isEntityHidden(value2.getId())) {
         callbackInfoReturnable.setReturnValue(false);
      }
   }
}
