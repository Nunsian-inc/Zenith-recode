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
   private void zenith$hideGrimElytraGlidingPose(Avatar var1, AvatarRenderState var2, float var3, CallbackInfo var4) {
      if (var1 == Minecraft.getInstance().player
         && SpeedModule.INSTANCE.isEnabled()
         && SpeedModule.INSTANCE.method6606()
         && (var1.isFallFlying() || var1.getPose() == Pose.FALL_FLYING)) {
         var2.isFallFlying = false;
         var2.fallFlyingTimeInTicks = 0.0F;
         var2.swimAmount = 0.0F;
         var2.pose = Pose.STANDING;
         var2.boundingBoxHeight = var1.getDimensions(Pose.STANDING).height();
         var2.boundingBoxWidth = var1.getDimensions(Pose.STANDING).width();
         var2.eyeHeight = var1.getEyeHeight(Pose.STANDING);
         var2.isCrouching = false;
         var2.isVisuallySwimming = false;
         var2.shouldApplyFlyingYRot = false;
         var2.flyingYRot = 0.0F;
      }
   }

   @Inject(
      method = {"extractRenderState(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;F)V"},
      at = {@At("TAIL")}
   )
   private void zenith$hideCosmeticParts(Avatar var1, AvatarRenderState var2, float var3, CallbackInfo var4) {
      ((Handler4)var2).zenith$setProfileName(var1 instanceof Player var5 ? var5.getGameProfile().name() : null);
      WardrobeFeatureRenderer.method4446(var2);
      var2.nameTag = StreamerModeModule.INSTANCE.method6293(var2.nameTag);
      var2.scoreText = StreamerModeModule.INSTANCE.method6293(var2.scoreText);
   }
}
