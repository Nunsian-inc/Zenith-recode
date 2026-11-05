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

   public void tick(BotPlayer var1) {
      this.prevEquipProgressMainHand = this.equipProgressMainHand;
      this.prevEquipProgressOffHand = this.equipProgressOffHand;
      ItemStack var2 = var1.getMainHandItem();
      ItemStack var3 = var1.getOffhandItem();
      if (this.shouldSkipHandAnimationOnSwap(this.mainHand, var2)) {
         this.mainHand = var2;
      }

      if (this.shouldSkipHandAnimationOnSwap(this.offHand, var3)) {
         this.offHand = var3;
      }

      if (var1.isRiding()) {
         this.equipProgressMainHand = Mth.clamp(this.equipProgressMainHand - 0.4F, 0.0F, 1.0F);
         this.equipProgressOffHand = Mth.clamp(this.equipProgressOffHand - 0.4F, 0.0F, 1.0F);
      } else {
         float var4 = var1.getAttackStrengthScale(1.0F);
         float var5 = this.mainHand != var2 ? 0.0F : var4 * var4 * var4;
         float var6 = this.offHand != var3 ? 0.0F : 1.0F;
         this.equipProgressMainHand = this.equipProgressMainHand + Mth.clamp(var5 - this.equipProgressMainHand, -0.4F, 0.4F);
         this.equipProgressOffHand = this.equipProgressOffHand + Mth.clamp(var6 - this.equipProgressOffHand, -0.4F, 0.4F);
      }

      if (this.equipProgressMainHand < 0.1F) {
         this.mainHand = var2;
      }

      if (this.equipProgressOffHand < 0.1F) {
         this.offHand = var3;
      }
   }

   public void snap(BotPlayer var1) {
      this.mainHand = var1.getMainHandItem();
      this.offHand = var1.getOffhandItem();
      this.equipProgressMainHand = this.prevEquipProgressMainHand = 1.0F;
      this.equipProgressOffHand = this.prevEquipProgressOffHand = 1.0F;
   }

   private boolean shouldSkipHandAnimationOnSwap(ItemStack var1, ItemStack var2) {
      return ItemStack.matches(var1, var2) || !mc.getItemModelResolver().shouldPlaySwapAnimation(var2);
   }

   public void render(BotPlayer var1, @Nullable BotPlayHandler var2, float var3, PoseStack var4, SubmitNodeCollector var5, int var6) {
      float var7 = var1.getSwingAnimation(var3);
      InteractionHand var8 = var1.getCurrentSwing() != null ? var1.getCurrentSwing().hand() : InteractionHand.MAIN_HAND;
      float var9 = var1.getXRot(var3);
      BotHeldItemRenderer.HandRenderType var10 = getHandRenderType(var1);
      if (var10.renderMainHand) {
         float var11 = var8 == InteractionHand.MAIN_HAND ? var7 : 0.0F;
         float var12 = 1.0F - Mth.lerp(var3, this.prevEquipProgressMainHand, this.equipProgressMainHand);
         this.renderFirstPersonItem(var1, var2, var3, var9, InteractionHand.MAIN_HAND, var11, this.mainHand, var12, var4, var5, var6);
      }

      if (var10.renderOffHand) {
         float var13 = var8 == InteractionHand.OFF_HAND ? var7 : 0.0F;
         float var14 = 1.0F - Mth.lerp(var3, this.prevEquipProgressOffHand, this.equipProgressOffHand);
         this.renderFirstPersonItem(var1, var2, var3, var9, InteractionHand.OFF_HAND, var13, this.offHand, var14, var4, var5, var6);
      }
   }

   private static BotHeldItemRenderer.HandRenderType getHandRenderType(BotPlayer var0) {
      ItemStack var1 = var0.getMainHandItem();
      ItemStack var2 = var0.getOffhandItem();
      boolean var3 = var1.is(Items.BOW) || var2.is(Items.BOW);
      boolean var4 = var1.is(Items.CROSSBOW) || var2.is(Items.CROSSBOW);
      if (!var3 && !var4) {
         return BotHeldItemRenderer.HandRenderType.RENDER_BOTH_HANDS;
      } else if (var0.isUsingItem()) {
         return getUsingItemHandRenderType(var0);
      } else {
         return isChargedCrossbow(var1) ? BotHeldItemRenderer.HandRenderType.RENDER_MAIN_HAND_ONLY : BotHeldItemRenderer.HandRenderType.RENDER_BOTH_HANDS;
      }
   }

   private static BotHeldItemRenderer.HandRenderType getUsingItemHandRenderType(BotPlayer var0) {
      ItemStack var1 = var0.getUseItem();
      InteractionHand var2 = var0.getUsedItemHand();
      if (!var1.is(Items.BOW) && !var1.is(Items.CROSSBOW)) {
         return var2 == InteractionHand.MAIN_HAND && isChargedCrossbow(var0.getOffhandItem())
            ? BotHeldItemRenderer.HandRenderType.RENDER_MAIN_HAND_ONLY
            : BotHeldItemRenderer.HandRenderType.RENDER_BOTH_HANDS;
      } else {
         return BotHeldItemRenderer.HandRenderType.shouldOnlyRender(var2);
      }
   }

   private static boolean isChargedCrossbow(ItemStack var0) {
      return var0.is(Items.CROSSBOW) && CrossbowItem.isCharged(var0);
   }

   private void renderFirstPersonItem(
      BotPlayer var1,
      @Nullable BotPlayHandler var2,
      float var3,
      float var4,
      InteractionHand var5,
      float var6,
      ItemStack var7,
      float var8,
      PoseStack var9,
      SubmitNodeCollector var10,
      int var11
   ) {
      if (!var1.isScoping()) {
         boolean var12 = var5 == InteractionHand.MAIN_HAND;
         HumanoidArm var13 = var12 ? var1.getMainArm() : var1.getMainArm().getOpposite();
         var9.pushPose();
         if (var7.isEmpty()) {
            if (var12 && !var1.isInvisible()) {
               this.renderArmHoldingItem(var1, var2, var9, var10, var11, var8, var6, var13);
            }
         } else if (var7.has(DataComponents.MAP_ID)) {
            if (var12 && this.offHand.isEmpty()) {
               this.renderMapInBothHands(var1, var2, var9, var10, var11, var4, var8, var6);
            } else {
               this.renderMapInOneHand(var1, var2, var9, var10, var11, var8, var13, var6, var7);
            }
         } else if (var7.is(Items.CROSSBOW)) {
            boolean var14 = CrossbowItem.isCharged(var7);
            boolean var15 = var13 == HumanoidArm.RIGHT;
            int var16 = var15 ? 1 : -1;
            if (var1.isUsingItem() && var1.getUseItemRemainingTicks() > 0 && var1.getUsedItemHand() == var5) {
               this.applyEquipOffset(var9, var13, var8);
               var9.translate(var16 * -0.4785682F, -0.094387F, 0.05731531F);
               var9.rotate(Axis.XP.rotationDegrees(-11.935F));
               var9.rotate(Axis.YP.rotationDegrees(var16 * 65.3F));
               var9.rotate(Axis.ZP.rotationDegrees(var16 * -9.785F));
               float var17 = var7.getUseDuration(var1) - (var1.getUseItemRemainingTicks() - var3 + 1.0F);
               float var18 = var17 / CrossbowItem.getChargeDuration(var7, var1);
               if (var18 > 1.0F) {
                  var18 = 1.0F;
               }

               if (var18 > 0.1F) {
                  float var19 = Mth.sin((var17 - 0.1F) * 1.3F) * (var18 - 0.1F);
                  var9.translate(0.0F, var19 * 0.004F, 0.0F);
               }

               var9.translate(0.0F, 0.0F, var18 * 0.04F);
               var9.scale(1.0F, 1.0F, 1.0F + var18 * 0.2F);
               var9.rotate(Axis.YN.rotationDegrees(var16 * 45.0F));
            } else {
               this.swingArm(var6, var8, var9, var16, var13);
               if (var14 && var6 < 0.001F && var12) {
                  var9.translate(var16 * -0.641864F, 0.0F, 0.0F);
                  var9.rotate(Axis.YP.rotationDegrees(var16 * 10.0F));
               }
            }

            this.renderItem(
               var1, var7, var15 ? ItemDisplayContext.FIRST_PERSON_RIGHT_HAND : ItemDisplayContext.FIRST_PERSON_LEFT_HAND, !var15, var9, var10, var11
            );
         } else {
            boolean var20 = var13 == HumanoidArm.RIGHT;
            int var21 = var20 ? 1 : -1;
            if (var1.isUsingItem() && var1.getUseItemRemainingTicks() > 0 && var1.getUsedItemHand() == var5) {
               switch (var7.getUseAnimation()) {
                  case NONE:
                     this.applyEquipOffset(var9, var13, var8);
                     break;
                  case EAT:
                  case DRINK:
                     this.applyEatOrDrinkTransformation(var9, var3, var13, var7, var1);
                     this.applyEquipOffset(var9, var13, var8);
                     break;
                  case BLOCK:
                     this.applyEquipOffset(var9, var13, var8);
                     if (!(var7.getItem() instanceof ShieldItem)) {
                        var9.translate(var21 * -0.14142136F, 0.08F, 0.14142136F);
                        var9.rotate(Axis.XP.rotationDegrees(-102.25F));
                        var9.rotate(Axis.YP.rotationDegrees(var21 * 13.365F));
                        var9.rotate(Axis.ZP.rotationDegrees(var21 * 78.05F));
                     }
                     break;
                  case BOW:
                     this.applyEquipOffset(var9, var13, var8);
                     var9.translate(var21 * -0.2785682F, 0.18344387F, 0.15731531F);
                     var9.rotate(Axis.XP.rotationDegrees(-13.935F));
                     var9.rotate(Axis.YP.rotationDegrees(var21 * 35.3F));
                     var9.rotate(Axis.ZP.rotationDegrees(var21 * -9.785F));
                     float var23 = var7.getUseDuration(var1) - (var1.getUseItemRemainingTicks() - var3 + 1.0F);
                     float var25 = var23 / 20.0F;
                     var25 = (var25 * var25 + var25 * 2.0F) / 3.0F;
                     if (var25 > 1.0F) {
                        var25 = 1.0F;
                     }

                     if (var25 > 0.1F) {
                        float var28 = Mth.sin((var23 - 0.1F) * 1.3F) * (var25 - 0.1F);
                        var9.translate(0.0F, var28 * 0.004F, 0.0F);
                     }

                     var9.translate(0.0F, 0.0F, var25 * 0.04F);
                     var9.scale(1.0F, 1.0F, 1.0F + var25 * 0.2F);
                     var9.rotate(Axis.YN.rotationDegrees(var21 * 45.0F));
                     break;
                  case SPEAR:
                     this.applyEquipOffset(var9, var13, var8);
                     var9.translate(var21 * -0.5F, 0.7F, 0.1F);
                     var9.rotate(Axis.XP.rotationDegrees(-55.0F));
                     var9.rotate(Axis.YP.rotationDegrees(var21 * 35.3F));
                     var9.rotate(Axis.ZP.rotationDegrees(var21 * -9.785F));
                     float var22 = var7.getUseDuration(var1) - (var1.getUseItemRemainingTicks() - var3 + 1.0F);
                     float var24 = Math.min(var22 / 10.0F, 1.0F);
                     if (var24 > 0.1F) {
                        float var27 = Mth.sin((var22 - 0.1F) * 1.3F) * (var24 - 0.1F);
                        var9.translate(0.0F, var27 * 0.004F, 0.0F);
                     }

                     var9.translate(0.0F, 0.0F, var24 * 0.2F);
                     var9.scale(1.0F, 1.0F, 1.0F + var24 * 0.2F);
                     var9.rotate(Axis.YN.rotationDegrees(var21 * 45.0F));
                     break;
                  case BRUSH:
                     this.applyBrushTransformation(var9, var3, var13, var1, var8);
                     break;
                  case BUNDLE:
                  default:
                     this.swingArm(var6, var8, var9, var21, var13);
               }
            } else if (var1.isAutoSpinAttack()) {
               this.applyEquipOffset(var9, var13, var8);
               var9.translate(var21 * -0.4F, 0.8F, 0.3F);
               var9.rotate(Axis.YP.rotationDegrees(var21 * 65.0F));
               var9.rotate(Axis.ZP.rotationDegrees(var21 * -85.0F));
            } else {
               this.swingArm(var6, var8, var9, var21, var13);
            }

            this.renderItem(
               var1, var7, var20 ? ItemDisplayContext.FIRST_PERSON_RIGHT_HAND : ItemDisplayContext.FIRST_PERSON_LEFT_HAND, !var20, var9, var10, var11
            );
         }

         var9.popPose();
      }
   }

   private void renderItem(BotPlayer var1, ItemStack var2, ItemDisplayContext var3, boolean var4, PoseStack var5, SubmitNodeCollector var6, int var7) {
      if (!var2.isEmpty()) {
         ItemStackRenderState var8 = new ItemStackRenderState();
         mc.getItemModelResolver().updateForTopItem(var8, var2, var3, var1.level(), var1, var1.getId() + var3.ordinal());
         var8.submit(var5, var6, var7, OverlayTexture.NO_OVERLAY, 0);
      }
   }

   private void renderArmHoldingItem(
      BotPlayer var1, @Nullable BotPlayHandler var2, PoseStack var3, SubmitNodeCollector var4, int var5, float var6, float var7, HumanoidArm var8
   ) {
      boolean var9 = var8 != HumanoidArm.LEFT;
      float var10 = var9 ? 1.0F : -1.0F;
      float var11 = Mth.sqrt(var7);
      float var12 = -0.3F * Mth.sin(var11 * (float) Math.PI);
      float var13 = 0.4F * Mth.sin(var11 * (float) (Math.PI * 2));
      float var14 = -0.4F * Mth.sin(var7 * (float) Math.PI);
      var3.translate(var10 * (var12 + 0.64000005F), var13 + -0.6F + var6 * -0.6F, var14 + -0.71999997F);
      var3.rotate(Axis.YP.rotationDegrees(var10 * 45.0F));
      float var15 = Mth.sin(var7 * var7 * (float) Math.PI);
      float var16 = Mth.sin(var11 * (float) Math.PI);
      var3.rotate(Axis.YP.rotationDegrees(var10 * var16 * 70.0F));
      var3.rotate(Axis.ZP.rotationDegrees(var10 * var15 * -20.0F));
      var3.translate(var10 * -1.0F, 3.6F, 3.5F);
      var3.rotate(Axis.ZP.rotationDegrees(var10 * 120.0F));
      var3.rotate(Axis.XP.rotationDegrees(200.0F));
      var3.rotate(Axis.YP.rotationDegrees(var10 * -135.0F));
      var3.translate(var10 * 5.6F, 0.0F, 0.0F);
      this.renderArm(var1, var2, var3, var4, var5, var8);
   }

   private void renderArm(BotPlayer var1, @Nullable BotPlayHandler var2, PoseStack var3, SubmitNodeCollector var4, int var5, HumanoidArm var6) {
      PlayerSkin var7 = BotPlayerRenderer.resolveSkin(var1, var2);
      AvatarRenderer var8 = BotPlayerRenderer.rendererFor(var7.model());
      if (var8 != null) {
         Identifier var9 = var7.body().texturePath();
         if (var6 == HumanoidArm.RIGHT) {
            var8.renderRightHand(var3, var4, var5, var9, true);
         } else {
            var8.renderLeftHand(var3, var4, var5, var9, true);
         }
      }
   }

   private void renderMapInOneHand(
      BotPlayer var1,
      @Nullable BotPlayHandler var2,
      PoseStack var3,
      SubmitNodeCollector var4,
      int var5,
      float var6,
      HumanoidArm var7,
      float var8,
      ItemStack var9
   ) {
      float var10 = var7 == HumanoidArm.RIGHT ? 1.0F : -1.0F;
      var3.translate(var10 * 0.125F, -0.125F, 0.0F);
      if (!var1.isInvisible()) {
         var3.pushPose();
         var3.rotate(Axis.ZP.rotationDegrees(var10 * 10.0F));
         this.renderArmHoldingItem(var1, var2, var3, var4, var5, var6, var8, var7);
         var3.popPose();
      }

      var3.pushPose();
      var3.translate(var10 * 0.51F, -0.08F + var6 * -1.2F, -0.75F);
      float var11 = Mth.sqrt(var8);
      float var12 = Mth.sin(var11 * (float) Math.PI);
      float var13 = -0.5F * var12;
      float var14 = 0.4F * Mth.sin(var11 * (float) (Math.PI * 2));
      float var15 = -0.3F * Mth.sin(var8 * (float) Math.PI);
      var3.translate(var10 * var13, var14 - 0.3F * var12, var15);
      var3.rotate(Axis.XP.rotationDegrees(var12 * -45.0F));
      var3.rotate(Axis.YP.rotationDegrees(var10 * var12 * -30.0F));
      this.renderFirstPersonMap(var1, var3, var4, var5, var9);
      var3.popPose();
   }

   private void renderMapInBothHands(
      BotPlayer var1, @Nullable BotPlayHandler var2, PoseStack var3, SubmitNodeCollector var4, int var5, float var6, float var7, float var8
   ) {
      float var9 = Mth.sqrt(var8);
      float var10 = -0.2F * Mth.sin(var8 * (float) Math.PI);
      float var11 = -0.4F * Mth.sin(var9 * (float) Math.PI);
      var3.translate(0.0F, -var10 / 2.0F, var11);
      float var12 = this.getMapAngle(var6);
      var3.translate(0.0F, 0.04F + var7 * -1.2F + var12 * -0.5F, -0.72F);
      var3.rotate(Axis.XP.rotationDegrees(var12 * -85.0F));
      if (!var1.isInvisible()) {
         var3.pushPose();
         var3.rotate(Axis.YP.rotationDegrees(90.0F));
         this.renderPlainArm(var1, var2, var3, var4, var5, HumanoidArm.RIGHT);
         this.renderPlainArm(var1, var2, var3, var4, var5, HumanoidArm.LEFT);
         var3.popPose();
      }

      float var13 = Mth.sin(var9 * (float) Math.PI);
      var3.rotate(Axis.XP.rotationDegrees(var13 * 20.0F));
      var3.scale(2.0F, 2.0F, 2.0F);
      this.renderFirstPersonMap(var1, var3, var4, var5, this.mainHand);
   }

   private void renderPlainArm(BotPlayer var1, @Nullable BotPlayHandler var2, PoseStack var3, SubmitNodeCollector var4, int var5, HumanoidArm var6) {
      var3.pushPose();
      float var7 = var6 == HumanoidArm.RIGHT ? 1.0F : -1.0F;
      var3.rotate(Axis.YP.rotationDegrees(92.0F));
      var3.rotate(Axis.XP.rotationDegrees(45.0F));
      var3.rotate(Axis.ZP.rotationDegrees(var7 * -41.0F));
      var3.translate(var7 * 0.3F, -1.1F, 0.45F);
      this.renderArm(var1, var2, var3, var4, var5, var6);
      var3.popPose();
   }

   private void renderFirstPersonMap(BotPlayer var1, PoseStack var2, SubmitNodeCollector var3, int var4, ItemStack var5) {
      var2.rotate(Axis.YP.rotationDegrees(180.0F));
      var2.rotate(Axis.ZP.rotationDegrees(180.0F));
      var2.scale(0.38F, 0.38F, 0.38F);
      var2.translate(-0.5F, -0.5F, 0.0F);
      var2.scale(0.0078125F, 0.0078125F, 0.0078125F);
      MapId var7 = var5.get(DataComponents.MAP_ID);
      MapItemSavedData var8 = MapItem.getSavedData(var7, var1.level());
      var3.submitCustomGeometry(var2, var8 == null ? MAP_BACKGROUND : MAP_BACKGROUND_CHECKERBOARD, (var10, var9) -> {
         var9.addVertex(var10, -7.0F, 135.0F, 0.0F).setColor(-1).setUv(0.0F, 1.0F).setLight(var4);
         var9.addVertex(var10, 135.0F, 135.0F, 0.0F).setColor(-1).setUv(1.0F, 1.0F).setLight(var4);
         var9.addVertex(var10, 135.0F, -7.0F, 0.0F).setColor(-1).setUv(1.0F, 0.0F).setLight(var4);
         var9.addVertex(var10, -7.0F, -7.0F, 0.0F).setColor(-1).setUv(0.0F, 0.0F).setLight(var4);
      });
      if (var8 != null) {
         MapRenderer var11 = mc.getMapRenderer();
         var11.extractRenderState(var7, var8, this.mapRenderState);
         var11.render(this.mapRenderState, var2, var3, false, var4);
      }
   }

   private float getMapAngle(float var1) {
      float var2 = 1.0F - var1 / 45.0F + 0.1F;
      var2 = Mth.clamp(var2, 0.0F, 1.0F);
      return -Mth.cos(var2 * (float) Math.PI) * 0.5F + 0.5F;
   }

   private void applyEatOrDrinkTransformation(PoseStack var1, float var2, HumanoidArm var3, ItemStack var4, BotPlayer var5) {
      float var6 = var5.getUseItemRemainingTicks() - var2 + 1.0F;
      float var7 = var6 / var4.getUseDuration(var5);
      if (var7 < 0.8F) {
         float var8 = Mth.abs(Mth.cos(var6 / 4.0F * (float) Math.PI) * 0.1F);
         var1.translate(0.0F, var8, 0.0F);
      }

      float var10 = 1.0F - (float)Math.pow(var7, 27.0);
      int var9 = var3 == HumanoidArm.RIGHT ? 1 : -1;
      var1.translate(var10 * 0.6F * var9, var10 * -0.5F, 0.0F);
      var1.rotate(Axis.YP.rotationDegrees(var9 * var10 * 90.0F));
      var1.rotate(Axis.XP.rotationDegrees(var10 * 10.0F));
      var1.rotate(Axis.ZP.rotationDegrees(var9 * var10 * 30.0F));
   }

   private void applyBrushTransformation(PoseStack var1, float var2, HumanoidArm var3, BotPlayer var4, float var5) {
      this.applyEquipOffset(var1, var3, var5);
      float var6 = var4.getUseItemRemainingTicks() % 10;
      float var7 = var6 - var2 + 1.0F;
      float var8 = 1.0F - var7 / 10.0F;
      float var9 = -15.0F + 75.0F * Mth.cos(var8 * 2.0F * (float) Math.PI);
      if (var3 != HumanoidArm.RIGHT) {
         var1.translate(0.1, 0.83, 0.35);
         var1.rotate(Axis.XP.rotationDegrees(-80.0F));
         var1.rotate(Axis.YP.rotationDegrees(-90.0F));
         var1.rotate(Axis.XP.rotationDegrees(var9));
         var1.translate(-0.3, 0.22, 0.35);
      } else {
         var1.translate(-0.25, 0.22, 0.35);
         var1.rotate(Axis.XP.rotationDegrees(-80.0F));
         var1.rotate(Axis.YP.rotationDegrees(90.0F));
         var1.rotate(Axis.XP.rotationDegrees(var9));
      }
   }

   private void applySwingOffset(PoseStack var1, HumanoidArm var2, float var3) {
      int var4 = var2 == HumanoidArm.RIGHT ? 1 : -1;
      float var5 = Mth.sin(var3 * var3 * (float) Math.PI);
      var1.rotate(Axis.YP.rotationDegrees(var4 * (45.0F + var5 * -20.0F)));
      float var6 = Mth.sin(Mth.sqrt(var3) * (float) Math.PI);
      var1.rotate(Axis.ZP.rotationDegrees(var4 * var6 * -20.0F));
      var1.rotate(Axis.XP.rotationDegrees(var6 * -80.0F));
      var1.rotate(Axis.YP.rotationDegrees(var4 * -45.0F));
   }

   private void applyEquipOffset(PoseStack var1, HumanoidArm var2, float var3) {
      int var4 = var2 == HumanoidArm.RIGHT ? 1 : -1;
      var1.translate(var4 * 0.56F, -0.52F + var3 * -0.6F, -0.72F);
   }

   private void swingArm(float var1, float var2, PoseStack var3, int var4, HumanoidArm var5) {
      float var6 = -0.4F * Mth.sin(Mth.sqrt(var1) * (float) Math.PI);
      float var7 = 0.2F * Mth.sin(Mth.sqrt(var1) * (float) (Math.PI * 2));
      float var8 = -0.2F * Mth.sin(var1 * (float) Math.PI);
      var3.translate(var4 * var6, var7, var8);
      this.applyEquipOffset(var3, var5, var2);
      this.applySwingOffset(var3, var5, var1);
   }

   private static enum HandRenderType {
      RENDER_BOTH_HANDS(true, true),
      RENDER_MAIN_HAND_ONLY(true, false),
      RENDER_OFF_HAND_ONLY(false, true);

      public final boolean renderMainHand;
      public final boolean renderOffHand;

      private HandRenderType(boolean var3, boolean var4) {
         this.renderMainHand = var3;
         this.renderOffHand = var4;
      }

      public static BotHeldItemRenderer.HandRenderType shouldOnlyRender(InteractionHand var0) {
         return var0 == InteractionHand.MAIN_HAND ? RENDER_MAIN_HAND_ONLY : RENDER_OFF_HAND_ONLY;
      }
   }
}
