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
   public static AvatarRenderer rendererFor(PlayerModelType var0) {
      EntityRenderDispatcherAccessor var1 = (EntityRenderDispatcherAccessor)(Object)Minecraft.getInstance().getEntityRenderDispatcher();
      EntityRenderer var2 = var1.zenith$getModelRenderers().get(var0);
      if (var2 == null) {
         var2 = var1.zenith$getModelRenderers().get(PlayerModelType.WIDE);
      }

      return var2 instanceof AvatarRenderer var3 ? var3 : null;
   }

   public void render(
      Player var1,
      @Nullable BotPlayHandler var2,
      double var3,
      double var5,
      double var7,
      float var9,
      PoseStack var10,
      SubmitNodeCollector var11,
      int var12,
      boolean var13
   ) {
      PlayerSkin var14 = resolveSkin(var1, var2);
      AvatarRenderer var15 = rendererFor(var14.model());
      if (var15 != null) {
         this.fillState(var1, var14, var9, var3, var5, var7, var13);
         double var16 = Mth.lerp((double)var9, var1.xOld, var1.getX());
         double var18 = Mth.lerp((double)var9, var1.yOld, var1.getY());
         double var20 = Mth.lerp((double)var9, var1.zOld, var1.getZ());
         var10.pushPose();
         var10.translate(var16 - var3, var18 - var5, var20 - var7);
         this.state.lightCoords = var12;
         // 26.3: callers wrap this in OutlineVertexProvider.method1542 and pass its collector
         var15.submit(this.state, var10, var11, OutlineVertexProvider.method1541(new Vec3(var3, var5, var7)));

         var10.popPose();
      }
   }

   public static PlayerSkin resolveSkin(Player var0, @Nullable BotPlayHandler var1) {
      if (var1 != null) {
         PlayerInfo var2 = var1.getPlayerListEntry(var0.getUUID());
         if (var2 != null) {
            try {
               return var2.getSkin();
            } catch (Exception var4) {
            }
         }
      }

      return DefaultPlayerSkin.get(var0.getUUID());
   }

   private void fillState(Player var1, PlayerSkin var2, float var3, double var4, double var6, double var8, boolean var10) {
      this.state.x = Mth.lerp((double)var3, var1.xOld, var1.getX());
      this.state.y = Mth.lerp((double)var3, var1.yOld, var1.getY());
      this.state.z = Mth.lerp((double)var3, var1.zOld, var1.getZ());
      this.state.isInvisible = var1.isInvisible();
      this.state.ageInTicks = var1.tickCount + var3;
      this.state.boundingBoxWidth = var1.getBbWidth();
      this.state.boundingBoxHeight = var1.getBbHeight();
      this.state.eyeHeight = var1.getEyeHeight();
      this.state.passengerOffset = null;
      this.state.distanceToCameraSq = Mth.lengthSquared(this.state.x - var4, this.state.y - var6, this.state.z - var8);
      this.state.isDiscrete = var1.isDiscrete();
      this.state.displayFireAnimation = var1.displayFireAnimation();
      this.state.leashStates = List.of();
      if (var10 && this.state.distanceToCameraSq < 4096.0) {
         this.state.nameTag = var1.getDisplayName();
         this.state.nameTagAttachment = var1.getAttachments().getNullable(EntityAttachment.NAME_TAG, 0, var1.getYRot(var3));
      } else {
         this.state.nameTag = null;
         this.state.nameTagAttachment = null;
      }

      float var11 = Mth.rotLerp(var3, var1.yHeadRotO, var1.yHeadRot);
      this.state.bodyRot = Mth.rotLerp(var3, var1.yBodyRotO, var1.yBodyRot);
      this.state.yRot = Mth.wrapDegrees(var11 - this.state.bodyRot);
      this.state.xRot = var1.getXRot(var3);
      this.state.isUpsideDown = false;
      if (!var1.isPassenger() && var1.isAlive()) {
         this.state.walkAnimationPos = var1.walkAnimation.position(var3);
         this.state.walkAnimationSpeed = var1.walkAnimation.speed(var3);
      } else {
         this.state.walkAnimationPos = 0.0F;
         this.state.walkAnimationSpeed = 0.0F;
      }

      this.state.wornHeadAnimationPos = this.state.walkAnimationPos;
      this.state.scale = var1.getScale();
      this.state.ageScale = var1.getAgeScale();
      this.state.pose = var1.getPose();
      this.state.bedOrientation = var1.getBedOrientation();
      if (this.state.bedOrientation != null) {
         this.state.eyeHeight = var1.getEyeHeight(Pose.STANDING);
      }

      this.state.isFullyFrozen = var1.isFullyFrozen();
      this.state.isBaby = var1.isBaby();
      this.state.isInWater = var1.isInWater();
      this.state.isAutoSpinAttack = var1.isAutoSpinAttack();
      this.state.hasRedOverlay = var1.hurtTime > 0 || var1.deathTime > 0;
      this.state.wornHeadType = null;
      this.state.wornHeadProfile = null;
      this.state.headItem.clear();
      this.state.deathTime = var1.deathTime > 0 ? var1.deathTime + var3 : 0.0F;
      this.state.isInvisibleToPlayer = false;
      this.state.outlineColor = 0;
      HumanoidMobRenderer.extractHumanoidRenderState(var1, this.state, var3, Minecraft.getInstance().getItemModelResolver());
      this.state.leftArmPose = getArmPose(var1, HumanoidArm.LEFT);
      this.state.rightArmPose = getArmPose(var1, HumanoidArm.RIGHT);
      this.state.skin = var2;
      this.state.arrowCount = var1.getArrowCount();
      this.state.stingerCount = var1.getStingerCount();
      this.state.isSpectator = var1.isSpectator();
      this.state.showHat = var1.isModelPartShown(PlayerModelPart.HAT);
      this.state.showJacket = var1.isModelPartShown(PlayerModelPart.JACKET);
      this.state.showLeftPants = var1.isModelPartShown(PlayerModelPart.LEFT_PANTS_LEG);
      this.state.showRightPants = var1.isModelPartShown(PlayerModelPart.RIGHT_PANTS_LEG);
      this.state.showLeftSleeve = var1.isModelPartShown(PlayerModelPart.LEFT_SLEEVE);
      this.state.showRightSleeve = var1.isModelPartShown(PlayerModelPart.RIGHT_SLEEVE);
      this.state.showCape = false;
      this.state.fallFlyingTimeInTicks = 0.0F;
      this.state.shouldApplyFlyingYRot = false;
      this.state.flyingYRot = 0.0F;
      this.state.scoreText = null;
      this.state.parrotOnLeftShoulder = null;
      this.state.parrotOnRightShoulder = null;
      this.state.id = var1.getId();
      ((Handler4)this.state).zenith$setProfileName(var1.getGameProfile().name());
      this.state.heldOnHead.clear();
   }

   private static ArmPose getArmPose(Player var0, HumanoidArm var1) {
      ArmPose var2 = getArmPose(var0, var0.getItemInHand(InteractionHand.MAIN_HAND), InteractionHand.MAIN_HAND);
      ArmPose var3 = getArmPose(var0, var0.getItemInHand(InteractionHand.OFF_HAND), InteractionHand.OFF_HAND);
      if (var2.isTwoHanded()) {
         var3 = var0.getItemInHand(InteractionHand.OFF_HAND).isEmpty() ? ArmPose.EMPTY : ArmPose.ITEM;
      }

      return var0.getMainArm() == var1 ? var2 : var3;
   }

   private static ArmPose getArmPose(Player var0, ItemStack var1, InteractionHand var2) {
      if (var1.isEmpty()) {
         return ArmPose.EMPTY;
      } else {
         if (var0.getUsedItemHand() == var2 && var0.getUseItemRemainingTicks() > 0) {
            ItemUseAnimation var3 = var1.getUseAnimation();
            switch (var3) {
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
         } else if (!var0.isSwinging() && var1.is(Items.CROSSBOW) && CrossbowItem.isCharged(var1)) {
            return ArmPose.CROSSBOW_HOLD;
         }

         return ArmPose.ITEM;
      }
   }
}
