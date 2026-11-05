package org.zenith.base.figura.mixin.render;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.base.figura.ducks.FiguraEntityRenderStateExtension;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity, S extends EntityRenderState> {
   @Inject(
      method = "extractRenderState(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/client/renderer/entity/state/EntityRenderState;F)V",
      at = @At("HEAD")
   )
   private void figura$captureEntity(T entity, S state, float tickDelta, CallbackInfo ci) {
      ((FiguraEntityRenderStateExtension)state).figura$setEntity(entity.getUUID(), entity.getId(), tickDelta);
   }
}
