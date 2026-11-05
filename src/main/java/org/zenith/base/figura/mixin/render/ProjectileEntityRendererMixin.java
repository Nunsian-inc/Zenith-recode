package org.zenith.base.figura.mixin.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.state.ArrowRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.base.figura.avatar.Avatar;
import org.zenith.base.figura.avatar.AvatarManager;
import org.zenith.base.figura.ducks.FiguraEntityRenderStateExtension;
import org.zenith.base.figura.lua.api.entity.EntityAPI;

@Mixin(ArrowRenderer.class)
public abstract class ProjectileEntityRendererMixin {
   @Inject(
      method = "submit(Lnet/minecraft/client/renderer/entity/state/ArrowRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/SubmitNodeCollector;submitModel(Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/resources/Identifier;III)V"
      ),
      cancellable = true
   )
   private void figura$renderArrow(ArrowRenderState state, PoseStack matrices, SubmitNodeCollector queue, CameraRenderState camera, CallbackInfo ci) {
      Minecraft client = Minecraft.getInstance();
      if (client.level != null) {
         int entityId = ((FiguraEntityRenderStateExtension)state).figura$getEntityId();
         if (client.level.getEntity(entityId) instanceof AbstractArrow projectile) {
            Entity owner = projectile.getOwner();
            if (owner != null) {
               boolean replaced = false;
               float tickDelta = ((FiguraEntityRenderStateExtension)state).figura$getTickDelta();

               for (Avatar avatar : AvatarManager.getAvatars(owner.getUUID())) {
                  avatar.prepareEntity(owner);
                  replaced |= avatar.arrowRenderEvent(tickDelta, EntityAPI.wrap(projectile));
                  replaced |= avatar.renderArrow(matrices, queue, tickDelta, state.lightCoords);
               }

               if (replaced) {
                  matrices.popPose();
                  ci.cancel();
               }
            }
         }
      }
   }
}
