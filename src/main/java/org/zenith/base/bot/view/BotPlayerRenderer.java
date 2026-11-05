package org.zenith.base.bot.view;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel.ArmPose;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityAttachment;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.entity.player.PlayerSkin;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.zenith.base.bot.net.BotPlayHandler;
import org.zenith.utility.misc.Handler4;
import org.zenith.utility.mixin.accessors.EntityRenderDispatcherAccessor;
import org.zenith.utility.render.OutlineVertexProvider;

public final class BotPlayerRenderer {
   private final AvatarRenderState state = new AvatarRenderState();

   @Nullable
   public static AvatarRenderer rendererFor(PlayerModelType model) {
      EntityRenderDispatcherAccessor entityRenderDispatcher = (EntityRenderDispatcherAccessor)(Object)Minecraft.getInstance().getEntityRenderDispatcher();
      EntityRenderer zenith$getModelRenderer = entityRenderDispatcher.zenith$getModelRenderers().get(model);
      if (zenith$getModelRenderer == null) {
         zenith$getModelRenderer = entityRenderDispatcher.zenith$getModelRenderers().get(PlayerModelType.WIDE);
      }

      return zenith$getModelRenderer instanceof AvatarRenderer avatarRenderer ? avatarRenderer : null;
   }

   public void render(
      Player player,
      @Nullable BotPlayHandler playHandler,
      double x,
      double y,
      double z,
      float tickDelta,
      PoseStack poseStack,
      SubmitNodeCollector submitNodeCollector,
      int lightCoords,
      boolean flag
   ) {
      PlayerSkin skin = resolveSkin(player, playHandler);
      AvatarRenderer avatarRenderer = rendererFor(skin.model());
      if (avatarRenderer != null) {
         this.fillState(player, skin, tickDelta, x, y, z, flag);
         double d = Mth.lerp((double)tickDelta, player.xOld, player.getX());
         double d2 = Mth.lerp((double)tickDelta, player.yOld, player.getY());
         double d3 = Mth.lerp((double)tickDelta, player.zOld, player.getZ());
         poseStack.pushPose();
         poseStack.translate(d - x, d2 - y, d3 - z);
         this.state.lightCoords = lightCoords;
         // 26.3: callers wrap this in OutlineVertexProvider.method1542 and pass its collector
         avatarRenderer.submit(this.state, poseStack, submitNodeCollector, OutlineVertexProvider.method1541(new Vec3(x, y, z)));

         poseStack.popPose();
      }
   }

   public static PlayerSkin resolveSkin(Player player, @Nullable BotPlayHandler playHandler) {
      if (playHandler != null) {
         PlayerInfo playerListEntry = playHandler.getPlayerListEntry(player.getUUID());
         if (playerListEntry != null) {
            try {
               return playerListEntry.getSkin();
            } catch (Exception e) {
            }
         }
      }

      return DefaultPlayerSkin.get(player.getUUID());
   }

   private void fillState(Player player, PlayerSkin skin, float tickDelta, double x, double y, double z, boolean flag) {
      this.state.x = Mth.lerp((double)tickDelta, player.xOld, player.getX());
      this.state.y = Mth.lerp((double)tickDelta, player.yOld, player.getY());
      this.state.z = Mth.lerp((double)tickDelta, player.zOld, player.getZ());
      this.state.isInvisible = player.isInvisible();
      this.state.ageInTicks = player.tickCount + tickDelta;
      this.state.boundingBoxWidth = player.getBbWidth();
      this.state.boundingBoxHeight = player.getBbHeight();
      this.state.eyeHeight = player.getEyeHeight();
      this.state.passengerOffset = null;
      this.state.distanceToCameraSq = Mth.lengthSquared(this.state.x - x, this.state.y - y, this.state.z - z);
      this.state.isDiscrete = player.isDiscrete();
      this.state.displayFireAnimation = player.displayFireAnimation();
      this.state.leashStates = List.of();
      if (flag && this.state.distanceToCameraSq < 4096.0) {
         this.state.nameTag = player.getDisplayName();
         this.state.nameTagAttachment = player.getAttachments().getNullable(EntityAttachment.NAME_TAG, 0, player.getYRot(tickDelta));
      } else {
         this.state.nameTag = null;
         this.state.nameTagAttachment = null;
      }

      float rotLerp = Mth.rotLerp(tickDelta, player.yHeadRotO, player.yHeadRot);
      this.state.bodyRot = Mth.rotLerp(tickDelta, player.yBodyRotO, player.yBodyRot);
      this.state.yRot = Mth.wrapDegrees(rotLerp - this.state.bodyRot);
      this.state.xRot = player.getXRot(tickDelta);
      this.state.isUpsideDown = false;
      if (!player.isPassenger() && player.isAlive()) {
         this.state.walkAnimationPos = player.walkAnimation.position(tickDelta);
         this.state.walkAnimationSpeed = player.walkAnimation.speed(tickDelta);
      } else {
         this.state.walkAnimationPos = 0.0F;
         this.state.walkAnimationSpeed = 0.0F;
      }

      this.state.wornHeadAnimationPos = this.state.walkAnimationPos;
      this.state.scale = player.getScale();
      this.state.ageScale = player.getAgeScale();
      this.state.pose = player.getPose();
      this.state.bedOrientation = player.getBedOrientation();
      if (this.state.bedOrientation != null) {
         this.state.eyeHeight = player.getEyeHeight(Pose.STANDING);
      }

      this.state.isFullyFrozen = player.isFullyFrozen();
      this.state.isBaby = player.isBaby();
      this.state.isInWater = player.isInWater();
      this.state.isAutoSpinAttack = player.isAutoSpinAttack();
      this.state.hasRedOverlay = player.hurtTime > 0 || player.deathTime > 0;
      this.state.wornHeadType = null;
      this.state.wornHeadProfile = null;
      this.state.headItem.clear();
      this.state.deathTime = player.deathTime > 0 ? player.deathTime + tickDelta : 0.0F;
      this.state.isInvisibleToPlayer = false;
      this.state.outlineColor = 0;
      HumanoidMobRenderer.extractHumanoidRenderState(player, this.state, tickDelta, Minecraft.getInstance().getItemModelResolver());
      this.state.leftArmPose = getArmPose(player, HumanoidArm.LEFT);
      this.state.rightArmPose = getArmPose(player, HumanoidArm.RIGHT);
      this.state.skin = skin;
      this.state.arrowCount = player.getArrowCount();
      this.state.stingerCount = player.getStingerCount();
      this.state.isSpectator = player.isSpectator();
      this.state.showHat = player.isModelPartShown(PlayerModelPart.HAT);
      this.state.showJacket = player.isModelPartShown(PlayerModelPart.JACKET);
      this.state.showLeftPants = player.isModelPartShown(PlayerModelPart.LEFT_PANTS_LEG);
      this.state.showRightPants = player.isModelPartShown(PlayerModelPart.RIGHT_PANTS_LEG);
      this.state.showLeftSleeve = player.isModelPartShown(PlayerModelPart.LEFT_SLEEVE);
      this.state.showRightSleeve = player.isModelPartShown(PlayerModelPart.RIGHT_SLEEVE);
      this.state.showCape = false;
      this.state.fallFlyingTimeInTicks = 0.0F;
      this.state.shouldApplyFlyingYRot = false;
      this.state.flyingYRot = 0.0F;
      this.state.scoreText = null;
      this.state.parrotOnLeftShoulder = null;
      this.state.parrotOnRightShoulder = null;
      this.state.id = player.getId();
      ((Handler4)this.state).zenith$setProfileName(player.getGameProfile().name());
      this.state.heldOnHead.clear();
   }

   private static ArmPose getArmPose(Player player, HumanoidArm humanoidArm) {
      ArmPose armPose = getArmPose(player, player.getItemInHand(InteractionHand.MAIN_HAND), InteractionHand.MAIN_HAND);
      ArmPose armPose2 = getArmPose(player, player.getItemInHand(InteractionHand.OFF_HAND), InteractionHand.OFF_HAND);
      if (armPose.isTwoHanded()) {
         armPose2 = player.getItemInHand(InteractionHand.OFF_HAND).isEmpty() ? ArmPose.EMPTY : ArmPose.ITEM;
      }

      return player.getMainArm() == humanoidArm ? armPose : armPose2;
   }

   private static ArmPose getArmPose(Player player, ItemStack itemInHand, InteractionHand interactionHand) {
      if (itemInHand.isEmpty()) {
         return ArmPose.EMPTY;
      } else {
         if (player.getUsedItemHand() == interactionHand && player.getUseItemRemainingTicks() > 0) {
            ItemUseAnimation useAnimation = itemInHand.getUseAnimation();
            switch (useAnimation) {
               case BLOCK:
                  return ArmPose.BLOCK;
               case BOW:
                  return ArmPose.BOW_AND_ARROW;
               case TRIDENT:
                  return ArmPose.THROW_TRIDENT;
               case SPEAR:
                  return ArmPose.SPEAR;
               case CROSSBOW:
                  return ArmPose.CROSSBOW_CHARGE;
               case SPYGLASS:
                  return ArmPose.SPYGLASS;
               case TOOT_HORN:
                  return ArmPose.TOOT_HORN;
               case BRUSH:
                  return ArmPose.BRUSH;
            }
         } else if (!player.isSwinging() && itemInHand.is(Items.CROSSBOW) && CrossbowItem.isCharged(itemInHand)) {
            return ArmPose.CROSSBOW_HOLD;
         }

         return ArmPose.ITEM;
      }
   }
}
