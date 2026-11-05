package org.zenith.base.figura.mixin.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.ThrownTridentRenderer;
import net.minecraft.client.renderer.entity.state.ThrownTridentRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.arrow.ThrownTrident;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.base.figura.avatar.Avatar;
import org.zenith.base.figura.avatar.AvatarManager;
import org.zenith.base.figura.ducks.FiguraEntityRenderStateExtension;
import org.zenith.base.figura.lua.api.entity.EntityAPI;

@Mixin(ThrownTridentRenderer.class)
public abstract class TridentEntityRendererMixin {
   @Inject(
      method = "submit(Lnet/minecraft/client/renderer/entity/state/ThrownTridentRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
      at = @At(
         value = "FIELD",
         target = "Lnet/minecraft/client/renderer/entity/state/ThrownTridentRenderState;isFoil:Z"
      ),
      cancellable = true
   )
   private void figura$renderTrident(ThrownTridentRenderState state, PoseStack matrices, SubmitNodeCollector queue, CameraRenderState camera, CallbackInfo ci) {
      Minecraft client = Minecraft.getInstance();
      if (client.level != null) {
         int entityId = ((FiguraEntityRenderStateExtension)state).figura$getEntityId();
         if (client.level.getEntity(entityId) instanceof ThrownTrident trident) {
            Entity owner = trident.getOwner();
            if (owner != null) {
               boolean replaced = false;
               float tickDelta = ((FiguraEntityRenderStateExtension)state).figura$getTickDelta();

               for (Avatar avatar : AvatarManager.getAvatars(owner.getUUID())) {
                  avatar.prepareEntity(owner);
                  replaced |= avatar.tridentRenderEvent(tickDelta, EntityAPI.wrap(trident));
                  replaced |= avatar.renderTrident(matrices, queue, tickDelta, state.lightCoords);
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
