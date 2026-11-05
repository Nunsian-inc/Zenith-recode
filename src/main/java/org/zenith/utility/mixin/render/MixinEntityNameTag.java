package org.zenith.utility.mixin.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.client.modules.impl.render.EntityESPModule;
import org.zenith.utility.api.PetManager;

// was AvatarRenderer.submitNameTag in 1.21.11; 26.3 draws every name tag from EntityRenderer.submitNameDisplay
@Mixin({EntityRenderer.class})
public class MixinEntityNameTag {
   @Inject(
      method = {"submitNameDisplay(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;I)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void zenith$hidePlayerNameTag(EntityRenderState entityRenderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState, int n, CallbackInfo callbackInfo) {
      if (entityRenderState instanceof AvatarRenderState avatarRenderState && (EntityESPModule.INSTANCE.method568() || PetManager.method4557(avatarRenderState.id))) {
         callbackInfo.cancel();
      }
   }
}
