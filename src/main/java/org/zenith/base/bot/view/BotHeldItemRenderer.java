package org.zenith.base.bot.view;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MapRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.MapRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.PlayerSkin;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.zenith.MinecraftAccess;
import org.zenith.base.bot.net.BotPlayHandler;
import org.zenith.base.bot.world.BotPlayer;

public final class BotHeldItemRenderer implements MinecraftAccess {
   private static final RenderType MAP_BACKGROUND = RenderTypes.text(Identifier.withDefaultNamespace("textures/map/map_background.png"));
   private static final RenderType MAP_BACKGROUND_CHECKERBOARD = RenderTypes.text(Identifier.withDefaultNamespace("textures/map/map_background_checkerboard.png"));
   private final MapRenderState mapRenderState = new MapRenderState();
   private ItemStack mainHand = ItemStack.EMPTY;
   private ItemStack offHand = ItemStack.EMPTY;
   private float equipProgressMainHand;
   private float prevEquipProgressMainHand;
   private float equipProgressOffHand;
   private float prevEquipProgressOffHand;

   public void tick(BotPlayer player) {
      this.prevEquipProgressMainHand = this.equipProgressMainHand;
      this.prevEquipProgressOffHand = this.equipProgressOffHand;
      ItemStack mainHandItem = player.getMainHandItem();
      ItemStack offhandItem = player.getOffhandItem();
      if (this.shouldSkipHandAnimationOnSwap(this.mainHand, mainHandItem)) {
         this.mainHand = mainHandItem;
      }

      if (this.shouldSkipHandAnimationOnSwap(this.offHand, offhandItem)) {
         this.offHand = offhandItem;
      }

      if (player.isRiding()) {
         this.equipProgressMainHand = Mth.clamp(this.equipProgressMainHand - 0.4F, 0.0F, 1.0F);
         this.equipProgressOffHand = Mth.clamp(this.equipProgressOffHand - 0.4F, 0.0F, 1.0F);
      } else {
         float attackStrengthScale = player.getAttackStrengthScale(1.0F);
         float f = this.mainHand != mainHandItem ? 0.0F : attackStrengthScale * attackStrengthScale * attackStrengthScale;
         float f2 = this.offHand != offhandItem ? 0.0F : 1.0F;
         this.equipProgressMainHand = this.equipProgressMainHand + Mth.clamp(f - this.equipProgressMainHand, -0.4F, 0.4F);
         this.equipProgressOffHand = this.equipProgressOffHand + Mth.clamp(f2 - this.equipProgressOffHand, -0.4F, 0.4F);
      }

      if (this.equipProgressMainHand < 0.1F) {
         this.mainHand = mainHandItem;
      }

      if (this.equipProgressOffHand < 0.1F) {
         this.offHand = offhandItem;
      }
   }

   public void snap(BotPlayer player) {
      this.mainHand = player.getMainHandItem();
      this.offHand = player.getOffhandItem();
      this.equipProgressMainHand = this.prevEquipProgressMainHand = 1.0F;
      this.equipProgressOffHand = this.prevEquipProgressOffHand = 1.0F;
   }

   private boolean shouldSkipHandAnimationOnSwap(ItemStack mainHand, ItemStack mainHandItem) {
      return ItemStack.matches(mainHand, mainHandItem) || !mc.getItemModelResolver().shouldPlaySwapAnimation(mainHandItem);
   }

   public void render(BotPlayer player, @Nullable BotPlayHandler playHandler, float tickDelta, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords) {
      float swingAnimation = player.getSwingAnimation(tickDelta);
      InteractionHand hand = player.getCurrentSwing() != null ? player.getCurrentSwing().hand() : InteractionHand.MAIN_HAND;
      float xRot = player.getXRot(tickDelta);
      BotHeldItemRenderer.HandRenderType handRenderType = getHandRenderType(player);
      if (handRenderType.renderMainHand) {
         float x = hand == InteractionHand.MAIN_HAND ? swingAnimation : 0.0F;
         float f = 1.0F - Mth.lerp(tickDelta, this.prevEquipProgressMainHand, this.equipProgressMainHand);
         this.renderFirstPersonItem(player, playHandler, tickDelta, xRot, InteractionHand.MAIN_HAND, x, this.mainHand, f, poseStack, submitNodeCollector, lightCoords);
      }

      if (handRenderType.renderOffHand) {
         float x = hand == InteractionHand.OFF_HAND ? swingAnimation : 0.0F;
         float f = 1.0F - Mth.lerp(tickDelta, this.prevEquipProgressOffHand, this.equipProgressOffHand);
         this.renderFirstPersonItem(player, playHandler, tickDelta, xRot, InteractionHand.OFF_HAND, x, this.offHand, f, poseStack, submitNodeCollector, lightCoords);
      }
   }

   private static BotHeldItemRenderer.HandRenderType getHandRenderType(BotPlayer player) {
      ItemStack mainHandItem = player.getMainHandItem();
      ItemStack offhandItem = player.getOffhandItem();
      boolean flag = mainHandItem.is(Items.BOW) || offhandItem.is(Items.BOW);
      boolean flag2 = mainHandItem.is(Items.CROSSBOW) || offhandItem.is(Items.CROSSBOW);
      if (!flag && !flag2) {
         return BotHeldItemRenderer.HandRenderType.RENDER_BOTH_HANDS;
      } else if (player.isUsingItem()) {
         return getUsingItemHandRenderType(player);
      } else {
         return isChargedCrossbow(mainHandItem) ? BotHeldItemRenderer.HandRenderType.RENDER_MAIN_HAND_ONLY : BotHeldItemRenderer.HandRenderType.RENDER_BOTH_HANDS;
      }
   }

   private static BotHeldItemRenderer.HandRenderType getUsingItemHandRenderType(BotPlayer player) {
      ItemStack useItem = player.getUseItem();
      InteractionHand usedItemHand = player.getUsedItemHand();
      if (!useItem.is(Items.BOW) && !useItem.is(Items.CROSSBOW)) {
         return usedItemHand == InteractionHand.MAIN_HAND && isChargedCrossbow(player.getOffhandItem())
            ? BotHeldItemRenderer.HandRenderType.RENDER_MAIN_HAND_ONLY
            : BotHeldItemRenderer.HandRenderType.RENDER_BOTH_HANDS;
      } else {
         return BotHeldItemRenderer.HandRenderType.shouldOnlyRender(usedItemHand);
      }
   }

   private static boolean isChargedCrossbow(ItemStack mainHandItem) {
      return mainHandItem.is(Items.CROSSBOW) && CrossbowItem.isCharged(mainHandItem);
   }

   private void renderFirstPersonItem(
      BotPlayer player,
      @Nullable BotPlayHandler playHandler,
      float tickDelta,
      float xRot,
      InteractionHand interactionHand,
      float x,
      ItemStack mainHand,
      float f,
      PoseStack poseStack,
      SubmitNodeCollector submitNodeCollector,
      int lightCoords
   ) {
      if (!player.isScoping()) {
         boolean flag = interactionHand == InteractionHand.MAIN_HAND;
         HumanoidArm mainArm = flag ? player.getMainArm() : player.getMainArm().getOpposite();
         poseStack.pushPose();
         if (mainHand.isEmpty()) {
            if (flag && !player.isInvisible()) {
               this.renderArmHoldingItem(player, playHandler, poseStack, submitNodeCollector, lightCoords, f, x, mainArm);
            }
         } else if (mainHand.has(DataComponents.MAP_ID)) {
            if (flag && this.offHand.isEmpty()) {
               this.renderMapInBothHands(player, playHandler, poseStack, submitNodeCollector, lightCoords, xRot, f, x);
            } else {
               this.renderMapInOneHand(player, playHandler, poseStack, submitNodeCollector, lightCoords, f, mainArm, x, mainHand);
            }
         } else if (mainHand.is(Items.CROSSBOW)) {
            boolean charged = CrossbowItem.isCharged(mainHand);
            boolean flag2 = mainArm == HumanoidArm.RIGHT;
            int n = flag2 ? 1 : -1;
            if (player.isUsingItem() && player.getUseItemRemainingTicks() > 0 && player.getUsedItemHand() == interactionHand) {
               this.applyEquipOffset(poseStack, mainArm, f);
               poseStack.translate(n * -0.4785682F, -0.094387F, 0.05731531F);
               poseStack.rotate(Axis.XP.rotationDegrees(-11.935F));
               poseStack.rotate(Axis.YP.rotationDegrees(n * 65.3F));
               poseStack.rotate(Axis.ZP.rotationDegrees(n * -9.785F));
               float f2 = mainHand.getUseDuration(player) - (player.getUseItemRemainingTicks() - tickDelta + 1.0F);
               float f3 = f2 / CrossbowItem.getChargeDuration(mainHand, player);
               if (f3 > 1.0F) {
                  f3 = 1.0F;
               }

               if (f3 > 0.1F) {
                  float f4 = Mth.sin((f2 - 0.1F) * 1.3F) * (f3 - 0.1F);
                  poseStack.translate(0.0F, f4 * 0.004F, 0.0F);
               }

               poseStack.translate(0.0F, 0.0F, f3 * 0.04F);
               poseStack.scale(1.0F, 1.0F, 1.0F + f3 * 0.2F);
               poseStack.rotate(Axis.YN.rotationDegrees(n * 45.0F));
            } else {
               this.swingArm(x, f, poseStack, n, mainArm);
               if (charged && x < 0.001F && flag) {
                  poseStack.translate(n * -0.641864F, 0.0F, 0.0F);
                  poseStack.rotate(Axis.YP.rotationDegrees(n * 10.0F));
               }
            }

            this.renderItem(
               player, mainHand, flag2 ? ItemDisplayContext.FIRST_PERSON_RIGHT_HAND : ItemDisplayContext.FIRST_PERSON_LEFT_HAND, !flag2, poseStack, submitNodeCollector, lightCoords
            );
         } else {
            boolean flag2 = mainArm == HumanoidArm.RIGHT;
            int n = flag2 ? 1 : -1;
            if (player.isUsingItem() && player.getUseItemRemainingTicks() > 0 && player.getUsedItemHand() == interactionHand) {
               switch (mainHand.getUseAnimation()) {
                  case NONE:
                     this.applyEquipOffset(poseStack, mainArm, f);
                     break;
                  case EAT:
                  case DRINK:
                     this.applyEatOrDrinkTransformation(poseStack, tickDelta, mainArm, mainHand, player);
                     this.applyEquipOffset(poseStack, mainArm, f);
                     break;
                  case BLOCK:
                     this.applyEquipOffset(poseStack, mainArm, f);
                     if (!(mainHand.getItem() instanceof ShieldItem)) {
                        poseStack.translate(n * -0.14142136F, 0.08F, 0.14142136F);
                        poseStack.rotate(Axis.XP.rotationDegrees(-102.25F));
                        poseStack.rotate(Axis.YP.rotationDegrees(n * 13.365F));
                        poseStack.rotate(Axis.ZP.rotationDegrees(n * 78.05F));
                     }
                     break;
                  case BOW:
                     this.applyEquipOffset(poseStack, mainArm, f);
                     poseStack.translate(n * -0.2785682F, 0.18344387F, 0.15731531F);
                     poseStack.rotate(Axis.XP.rotationDegrees(-13.935F));
                     poseStack.rotate(Axis.YP.rotationDegrees(n * 35.3F));
                     poseStack.rotate(Axis.ZP.rotationDegrees(n * -9.785F));
                     float f2 = mainHand.getUseDuration(player) - (player.getUseItemRemainingTicks() - tickDelta + 1.0F);
                     float f3 = f2 / 20.0F;
                     f3 = (f3 * f3 + f3 * 2.0F) / 3.0F;
                     if (f3 > 1.0F) {
                        f3 = 1.0F;
                     }

                     if (f3 > 0.1F) {
                        float f4 = Mth.sin((f2 - 0.1F) * 1.3F) * (f3 - 0.1F);
                        poseStack.translate(0.0F, f4 * 0.004F, 0.0F);
                     }

                     poseStack.translate(0.0F, 0.0F, f3 * 0.04F);
                     poseStack.scale(1.0F, 1.0F, 1.0F + f3 * 0.2F);
                     poseStack.rotate(Axis.YN.rotationDegrees(n * 45.0F));
                     break;
                  case SPEAR:
                     this.applyEquipOffset(poseStack, mainArm, f);
                     poseStack.translate(n * -0.5F, 0.7F, 0.1F);
                     poseStack.rotate(Axis.XP.rotationDegrees(-55.0F));
                     poseStack.rotate(Axis.YP.rotationDegrees(n * 35.3F));
                     poseStack.rotate(Axis.ZP.rotationDegrees(n * -9.785F));
                     float f4 = mainHand.getUseDuration(player) - (player.getUseItemRemainingTicks() - tickDelta + 1.0F);
                     float f5 = Math.min(f4 / 10.0F, 1.0F);
                     if (f5 > 0.1F) {
                        float f6 = Mth.sin((f4 - 0.1F) * 1.3F) * (f5 - 0.1F);
                        poseStack.translate(0.0F, f6 * 0.004F, 0.0F);
                     }

                     poseStack.translate(0.0F, 0.0F, f5 * 0.2F);
                     poseStack.scale(1.0F, 1.0F, 1.0F + f5 * 0.2F);
                     poseStack.rotate(Axis.YN.rotationDegrees(n * 45.0F));
                     break;
                  case BRUSH:
                     this.applyBrushTransformation(poseStack, tickDelta, mainArm, player, f);
                     break;
                  case BUNDLE:
                  default:
                     this.swingArm(x, f, poseStack, n, mainArm);
               }
            } else if (player.isAutoSpinAttack()) {
               this.applyEquipOffset(poseStack, mainArm, f);
               poseStack.translate(n * -0.4F, 0.8F, 0.3F);
               poseStack.rotate(Axis.YP.rotationDegrees(n * 65.0F));
               poseStack.rotate(Axis.ZP.rotationDegrees(n * -85.0F));
            } else {
               this.swingArm(x, f, poseStack, n, mainArm);
            }

            this.renderItem(
               player, mainHand, flag2 ? ItemDisplayContext.FIRST_PERSON_RIGHT_HAND : ItemDisplayContext.FIRST_PERSON_LEFT_HAND, !flag2, poseStack, submitNodeCollector, lightCoords
            );
         }

         poseStack.popPose();
      }
   }

   private void renderItem(BotPlayer player, ItemStack mainHand, ItemDisplayContext displayContext, boolean flag, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords) {
      if (!mainHand.isEmpty()) {
         ItemStackRenderState itemStackRenderState = new ItemStackRenderState();
         mc.getItemModelResolver().updateForTopItem(itemStackRenderState, mainHand, displayContext, player.level(), player, player.getId() + displayContext.ordinal());
         itemStackRenderState.submit(poseStack, submitNodeCollector, lightCoords, OverlayTexture.NO_OVERLAY, 0);
      }
   }

   private void renderArmHoldingItem(
      BotPlayer player, @Nullable BotPlayHandler playHandler, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, float f, float x, HumanoidArm mainArm
   ) {
      boolean flag = mainArm != HumanoidArm.LEFT;
      float f2 = flag ? 1.0F : -1.0F;
      float f3 = Mth.sqrt(x);
      float f4 = -0.3F * Mth.sin(f3 * (float) Math.PI);
      float f5 = 0.4F * Mth.sin(f3 * (float) (Math.PI * 2));
      float f6 = -0.4F * Mth.sin(x * (float) Math.PI);
      poseStack.translate(f2 * (f4 + 0.64000005F), f5 + -0.6F + f * -0.6F, f6 + -0.71999997F);
      poseStack.rotate(Axis.YP.rotationDegrees(f2 * 45.0F));
      float f7 = Mth.sin(x * x * (float) Math.PI);
      float f8 = Mth.sin(f3 * (float) Math.PI);
      poseStack.rotate(Axis.YP.rotationDegrees(f2 * f8 * 70.0F));
      poseStack.rotate(Axis.ZP.rotationDegrees(f2 * f7 * -20.0F));
      poseStack.translate(f2 * -1.0F, 3.6F, 3.5F);
      poseStack.rotate(Axis.ZP.rotationDegrees(f2 * 120.0F));
      poseStack.rotate(Axis.XP.rotationDegrees(200.0F));
      poseStack.rotate(Axis.YP.rotationDegrees(f2 * -135.0F));
      poseStack.translate(f2 * 5.6F, 0.0F, 0.0F);
      this.renderArm(player, playHandler, poseStack, submitNodeCollector, lightCoords, mainArm);
   }

   private void renderArm(BotPlayer player, @Nullable BotPlayHandler playHandler, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, HumanoidArm mainArm) {
      PlayerSkin skin = BotPlayerRenderer.resolveSkin(player, playHandler);
      AvatarRenderer rendererFor = BotPlayerRenderer.rendererFor(skin.model());
      if (rendererFor != null) {
         Identifier texturePath = skin.body().texturePath();
         if (mainArm == HumanoidArm.RIGHT) {
            rendererFor.renderRightHand(poseStack, submitNodeCollector, lightCoords, texturePath, true);
         } else {
            rendererFor.renderLeftHand(poseStack, submitNodeCollector, lightCoords, texturePath, true);
         }
      }
   }

   private void renderMapInOneHand(
      BotPlayer player,
      @Nullable BotPlayHandler playHandler,
      PoseStack poseStack,
      SubmitNodeCollector submitNodeCollector,
      int lightCoords,
      float f,
      HumanoidArm mainArm,
      float x,
      ItemStack mainHand
   ) {
      float f2 = mainArm == HumanoidArm.RIGHT ? 1.0F : -1.0F;
      poseStack.translate(f2 * 0.125F, -0.125F, 0.0F);
      if (!player.isInvisible()) {
         poseStack.pushPose();
         poseStack.rotate(Axis.ZP.rotationDegrees(f2 * 10.0F));
         this.renderArmHoldingItem(player, playHandler, poseStack, submitNodeCollector, lightCoords, f, x, mainArm);
         poseStack.popPose();
      }

      poseStack.pushPose();
      poseStack.translate(f2 * 0.51F, -0.08F + f * -1.2F, -0.75F);
      float f3 = Mth.sqrt(x);
      float f4 = Mth.sin(f3 * (float) Math.PI);
      float f5 = -0.5F * f4;
      float f6 = 0.4F * Mth.sin(f3 * (float) (Math.PI * 2));
      float zo = -0.3F * Mth.sin(x * (float) Math.PI);
      poseStack.translate(f2 * f5, f6 - 0.3F * f4, zo);
      poseStack.rotate(Axis.XP.rotationDegrees(f4 * -45.0F));
      poseStack.rotate(Axis.YP.rotationDegrees(f2 * f4 * -30.0F));
      this.renderFirstPersonMap(player, poseStack, submitNodeCollector, lightCoords, mainHand);
      poseStack.popPose();
   }

   private void renderMapInBothHands(
      BotPlayer player, @Nullable BotPlayHandler playHandler, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, float xRot, float f, float x
   ) {
      float f2 = Mth.sqrt(x);
      float f3 = -0.2F * Mth.sin(x * (float) Math.PI);
      float zo = -0.4F * Mth.sin(f2 * (float) Math.PI);
      poseStack.translate(0.0F, -f3 / 2.0F, zo);
      float mapAngle = this.getMapAngle(xRot);
      poseStack.translate(0.0F, 0.04F + f * -1.2F + mapAngle * -0.5F, -0.72F);
      poseStack.rotate(Axis.XP.rotationDegrees(mapAngle * -85.0F));
      if (!player.isInvisible()) {
         poseStack.pushPose();
         poseStack.rotate(Axis.YP.rotationDegrees(90.0F));
         this.renderPlainArm(player, playHandler, poseStack, submitNodeCollector, lightCoords, HumanoidArm.RIGHT);
         this.renderPlainArm(player, playHandler, poseStack, submitNodeCollector, lightCoords, HumanoidArm.LEFT);
         poseStack.popPose();
      }

      float f4 = Mth.sin(f2 * (float) Math.PI);
      poseStack.rotate(Axis.XP.rotationDegrees(f4 * 20.0F));
      poseStack.scale(2.0F, 2.0F, 2.0F);
      this.renderFirstPersonMap(player, poseStack, submitNodeCollector, lightCoords, this.mainHand);
   }

   private void renderPlainArm(BotPlayer player, @Nullable BotPlayHandler playHandler, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, HumanoidArm mainArm) {
      poseStack.pushPose();
      float f = mainArm == HumanoidArm.RIGHT ? 1.0F : -1.0F;
      poseStack.rotate(Axis.YP.rotationDegrees(92.0F));
      poseStack.rotate(Axis.XP.rotationDegrees(45.0F));
      poseStack.rotate(Axis.ZP.rotationDegrees(f * -41.0F));
      poseStack.translate(f * 0.3F, -1.1F, 0.45F);
      this.renderArm(player, playHandler, poseStack, submitNodeCollector, lightCoords, mainArm);
      poseStack.popPose();
   }

   private void renderFirstPersonMap(BotPlayer player, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, ItemStack mainHand) {
      poseStack.rotate(Axis.YP.rotationDegrees(180.0F));
      poseStack.rotate(Axis.ZP.rotationDegrees(180.0F));
      poseStack.scale(0.38F, 0.38F, 0.38F);
      poseStack.translate(-0.5F, -0.5F, 0.0F);
      poseStack.scale(0.0078125F, 0.0078125F, 0.0078125F);
      MapId id = mainHand.get(DataComponents.MAP_ID);
      MapItemSavedData savedData = MapItem.getSavedData(id, player.level());
      submitNodeCollector.submitCustomGeometry(poseStack, savedData == null ? MAP_BACKGROUND : MAP_BACKGROUND_CHECKERBOARD, (pose, consumer) -> {
         consumer.addVertex(pose, -7.0F, 135.0F, 0.0F).setColor(-1).setUv(0.0F, 1.0F).setLight(lightCoords);
         consumer.addVertex(pose, 135.0F, 135.0F, 0.0F).setColor(-1).setUv(1.0F, 1.0F).setLight(lightCoords);
         consumer.addVertex(pose, 135.0F, -7.0F, 0.0F).setColor(-1).setUv(1.0F, 0.0F).setLight(lightCoords);
         consumer.addVertex(pose, -7.0F, -7.0F, 0.0F).setColor(-1).setUv(0.0F, 0.0F).setLight(lightCoords);
      });
      if (savedData != null) {
         MapRenderer mapRenderer = mc.getMapRenderer();
         mapRenderer.extractRenderState(id, savedData, this.mapRenderState);
         mapRenderer.render(this.mapRenderState, poseStack, submitNodeCollector, false, lightCoords);
      }
   }

   private float getMapAngle(float xRot) {
      float f = 1.0F - xRot / 45.0F + 0.1F;
      f = Mth.clamp(f, 0.0F, 1.0F);
      return -Mth.cos(f * (float) Math.PI) * 0.5F + 0.5F;
   }

   private void applyEatOrDrinkTransformation(PoseStack poseStack, float tickDelta, HumanoidArm mainArm, ItemStack mainHand, BotPlayer player) {
      float f = player.getUseItemRemainingTicks() - tickDelta + 1.0F;
      float f2 = f / mainHand.getUseDuration(player);
      if (f2 < 0.8F) {
         float yo = Mth.abs(Mth.cos(f / 4.0F * (float) Math.PI) * 0.1F);
         poseStack.translate(0.0F, yo, 0.0F);
      }

      float f3 = 1.0F - (float)Math.pow(f2, 27.0);
      int n = mainArm == HumanoidArm.RIGHT ? 1 : -1;
      poseStack.translate(f3 * 0.6F * n, f3 * -0.5F, 0.0F);
      poseStack.rotate(Axis.YP.rotationDegrees(n * f3 * 90.0F));
      poseStack.rotate(Axis.XP.rotationDegrees(f3 * 10.0F));
      poseStack.rotate(Axis.ZP.rotationDegrees(n * f3 * 30.0F));
   }

   private void applyBrushTransformation(PoseStack poseStack, float tickDelta, HumanoidArm mainArm, BotPlayer player, float f) {
      this.applyEquipOffset(poseStack, mainArm, f);
      float f2 = player.getUseItemRemainingTicks() % 10;
      float f3 = f2 - tickDelta + 1.0F;
      float f4 = 1.0F - f3 / 10.0F;
      float angle = -15.0F + 75.0F * Mth.cos(f4 * 2.0F * (float) Math.PI);
      if (mainArm != HumanoidArm.RIGHT) {
         poseStack.translate(0.1, 0.83, 0.35);
         poseStack.rotate(Axis.XP.rotationDegrees(-80.0F));
         poseStack.rotate(Axis.YP.rotationDegrees(-90.0F));
         poseStack.rotate(Axis.XP.rotationDegrees(angle));
         poseStack.translate(-0.3, 0.22, 0.35);
      } else {
         poseStack.translate(-0.25, 0.22, 0.35);
         poseStack.rotate(Axis.XP.rotationDegrees(-80.0F));
         poseStack.rotate(Axis.YP.rotationDegrees(90.0F));
         poseStack.rotate(Axis.XP.rotationDegrees(angle));
      }
   }

   private void applySwingOffset(PoseStack poseStack, HumanoidArm mainArm, float x) {
      int n = mainArm == HumanoidArm.RIGHT ? 1 : -1;
      float f = Mth.sin(x * x * (float) Math.PI);
      poseStack.rotate(Axis.YP.rotationDegrees(n * (45.0F + f * -20.0F)));
      float f2 = Mth.sin(Mth.sqrt(x) * (float) Math.PI);
      poseStack.rotate(Axis.ZP.rotationDegrees(n * f2 * -20.0F));
      poseStack.rotate(Axis.XP.rotationDegrees(f2 * -80.0F));
      poseStack.rotate(Axis.YP.rotationDegrees(n * -45.0F));
   }

   private void applyEquipOffset(PoseStack poseStack, HumanoidArm mainArm, float f) {
      int n = mainArm == HumanoidArm.RIGHT ? 1 : -1;
      poseStack.translate(n * 0.56F, -0.52F + f * -0.6F, -0.72F);
   }

   private void swingArm(float x, float f, PoseStack poseStack, int n, HumanoidArm mainArm) {
      float f2 = -0.4F * Mth.sin(Mth.sqrt(x) * (float) Math.PI);
      float yo = 0.2F * Mth.sin(Mth.sqrt(x) * (float) (Math.PI * 2));
      float zo = -0.2F * Mth.sin(x * (float) Math.PI);
      poseStack.translate(n * f2, yo, zo);
      this.applyEquipOffset(poseStack, mainArm, f);
      this.applySwingOffset(poseStack, mainArm, x);
   }

   private static enum HandRenderType {
      RENDER_BOTH_HANDS(true, true),
      RENDER_MAIN_HAND_ONLY(true, false),
      RENDER_OFF_HAND_ONLY(false, true);

      public final boolean renderMainHand;
      public final boolean renderOffHand;

      private HandRenderType(boolean renderMainHand, boolean renderOffHand) {
         this.renderMainHand = renderMainHand;
         this.renderOffHand = renderOffHand;
      }

      public static BotHeldItemRenderer.HandRenderType shouldOnlyRender(InteractionHand usedItemHand) {
         return usedItemHand == InteractionHand.MAIN_HAND ? RENDER_MAIN_HAND_ONLY : RENDER_OFF_HAND_ONLY;
      }
   }
}
