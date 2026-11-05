package org.zenith.utility.mixin.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.client.modules.impl.misc.StreamerModeModule;
import org.zenith.client.modules.impl.movement.SpeedModule;
import org.zenith.client.modules.impl.render.EntityESPModule;
import org.zenith.utility.api.PetManager;
import org.zenith.utility.misc.Handler4;
import org.zenith.utility.render.WardrobeFeatureRenderer;

@Mixin({AvatarRenderer.class})
public class MixinPlayerEntityRenderer {
   @Inject(
      method = {"extractRenderState(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;F)V"},
      at = {@At("RETURN")}
   )
   private void zenith$hideGrimElytraGlidingPose(Avatar avatar, AvatarRenderState avatarRenderState, float f, CallbackInfo callbackInfo) {
      if (avatar == Minecraft.getInstance().player
         && SpeedModule.INSTANCE.isEnabled()
         && SpeedModule.INSTANCE.method6606()
         && (avatar.isFallFlying() || avatar.getPose() == Pose.FALL_FLYING)) {
         avatarRenderState.isFallFlying = false;
         avatarRenderState.fallFlyingTimeInTicks = 0.0F;
         avatarRenderState.swimAmount = 0.0F;
         avatarRenderState.pose = Pose.STANDING;
         avatarRenderState.boundingBoxHeight = avatar.getDimensions(Pose.STANDING).height();
         avatarRenderState.boundingBoxWidth = avatar.getDimensions(Pose.STANDING).width();
         avatarRenderState.eyeHeight = avatar.getEyeHeight(Pose.STANDING);
         avatarRenderState.isCrouching = false;
         avatarRenderState.isVisuallySwimming = false;
         avatarRenderState.shouldApplyFlyingYRot = false;
         avatarRenderState.flyingYRot = 0.0F;
      }
   }

   @Inject(
      method = {"extractRenderState(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;F)V"},
      at = {@At("TAIL")}
   )
   private void zenith$hideCosmeticParts(Avatar avatar, AvatarRenderState state, float f, CallbackInfo callbackInfo) {
      ((Handler4)state).zenith$setProfileName(avatar instanceof Player player ? player.getGameProfile().name() : null);
      WardrobeFeatureRenderer.method4446(state);
      state.nameTag = StreamerModeModule.INSTANCE.method6293(state.nameTag);
      state.scoreText = StreamerModeModule.INSTANCE.method6293(state.scoreText);
   }
}
