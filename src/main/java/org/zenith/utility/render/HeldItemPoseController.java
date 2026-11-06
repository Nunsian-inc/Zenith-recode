package org.zenith.utility.render;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.List;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.EndTick;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.resources.model.cuboid.ItemTransform;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.zenith.MinecraftAccess;
import org.zenith.client.events.AttackEvent;
import org.zenith.utility.api.Wardrobe;
import org.zenith.utility.game.FocusSlotEnum;
import org.zenith.utility.misc.AnimationCache;
import org.zenith.utility.misc.WardrobeModel;
import org.zenith.utility.misc.HoveredData;
import org.zenith.utility.misc.WardrobeCategory;
import org.zenith.utility.mixin.accessors.ItemRenderStateAccessor;
import org.zenith.utility.mixin.accessors.LayerRenderStateAccessor;

public final class HeldItemPoseController implements MinecraftAccess {
   private static final int CONST_intValue = -1;

   public HeldItemPoseController() {
      EventManager.register(this);
      ClientTickEvents.END_CLIENT_TICK.register((EndTick)minecraft -> this.tick());
   }

   public static boolean method6156(
      ArmedEntityRenderState armedEntityRenderState, ItemStackRenderState itemStackRenderState, ItemStack mainHandItem, PoseStack poseStack, SubmitNodeCollector orderedRenderCommandQueue, int lightCoords
   ) {
      if (armedEntityRenderState instanceof AvatarRenderState state) {
         HoveredData hoveredData = method6158(WardrobeFeatureRenderer.method4444(state), mainHandItem);
         if (hoveredData == null) {
            return false;
         } else {
            float powerForTime = 0.0F;
            if (FocusSlotEnum.CROSSBOW.matches(mainHandItem)) {
               powerForTime = CrossbowItem.isCharged(mainHandItem)
                  ? 1.0F
                  : (state.isUsingItem && state.maxCrossbowChargeDuration > 0.0F ? Math.min(0.97F, state.ticksUsingItem / state.maxCrossbowChargeDuration) : 0.0F);
            } else if (state.isUsingItem && FocusSlotEnum.BOW.matches(mainHandItem)) {
               powerForTime = BowItem.getPowerForTime((int)state.ticksUsingItem);
            }

            return method6159(hoveredData, state.id, itemStackRenderState, poseStack, orderedRenderCommandQueue, lightCoords, powerForTime, state.ageInTicks / 20.0F, true);
         }
      } else {
         return false;
      }
   }

   public static boolean method6157(LivingEntity player, ItemStack mainHandItem, ItemStackRenderState itemStackRenderState, PoseStack poseStack, SubmitNodeCollector orderedRenderCommandQueue, int lightCoords) {
      Wardrobe wardrobe = Wardrobe.method2488();
      if (wardrobe != null && player != null && player == mc.player && wardrobe.showSelf.isEnabled()) {
         HoveredData hoveredData = method6158(wardrobe.method2498(mc.player.getGameProfile().name(), true), mainHandItem);
         if (hoveredData == null) {
            return false;
         } else {
            float powerForTime = 0.0F;
            if (FocusSlotEnum.CROSSBOW.matches(mainHandItem)) {
               powerForTime = CrossbowItem.isCharged(mainHandItem)
                  ? 1.0F
                  : (
                     player.isUsingItem() && player.getUseItem() == mainHandItem
                        ? Math.min(0.97F, (float)player.getTicksUsingItem() / CrossbowItem.getChargeDuration(mainHandItem, player))
                        : 0.0F
                  );
            } else if (player.isUsingItem() && player.getUseItem() == mainHandItem && FocusSlotEnum.BOW.matches(mainHandItem)) {
               powerForTime = BowItem.getPowerForTime(player.getTicksUsingItem());
            }

            float floatValue = (player.tickCount + mc.getDeltaTracker().getGameTimeDeltaPartialTick(true)) / 20.0F;
            return method6159(hoveredData, player.getId(), itemStackRenderState, poseStack, orderedRenderCommandQueue, lightCoords, powerForTime, floatValue, false);
         }
      } else {
         return false;
      }
   }

   private static HoveredData method6158(List<HoveredData> hoveredDatas, ItemStack mainHandItem) {
      Wardrobe wardrobe = Wardrobe.method2488();
      FocusSlotEnum focusSlotEnum = FocusSlotEnum.method545(mainHandItem);
      return wardrobe != null && focusSlotEnum != null && !hoveredDatas.isEmpty() ? wardrobe.method2499(hoveredDatas, focusSlotEnum) : null;
   }

   private static boolean method6159(
      HoveredData hoveredData, int id, ItemStackRenderState itemStackRenderState, PoseStack poseStack, SubmitNodeCollector orderedRenderCommandQueue, int lightCoords, float powerForTime, float floatValue, boolean flag
   ) {
      Wardrobe wardrobe = Wardrobe.method2488();
      WardrobeModel key = wardrobe == null ? null : wardrobe.getRegistry().method1427(hoveredData);
      if (key != null && key.data2 != null) {
         ItemRenderStateAccessor renderStateAccessor = (ItemRenderStateAccessor)(Object)itemStackRenderState;
         if (renderStateAccessor.zenith$getLayerCount() > 0 && renderStateAccessor.zenith$getDisplayContext() != null) {
            ItemTransform zenith$getTransform = ((LayerRenderStateAccessor)(Object)renderStateAccessor.zenith$getLayers()[0]).zenith$getTransform();
            WardrobeModel.Data2 data2 = key.data2;
            AnimationCache.Entry entry = AnimationCache.method7621(id, hoveredData, wardrobe.isFlag());
            entry.animationState.method6539(key, "idle", floatValue);
            float[] floatValues = entry.class16.method2139(key, entry.animationState, floatValue, null);
            entry.class16.method2141(key, entry.animationState, "pull", powerForTime, powerForTime > 0.0F ? 1.0F : 0.0F);
            if (data2.getIntValue() >= 0) {
               float f = powerForTime > (hoveredData.getFocusSlotEnum() == FocusSlotEnum.CROSSBOW ? 0.99F : 0.05F) ? 1.0F : 0.0F;
               floatValues[data2.getIntValue() * 9 + 6] = f;
               floatValues[data2.getIntValue() * 9 + 7] = f;
               floatValues[data2.getIntValue() * 9 + 8] = f;
            }

            poseStack.pushPose();

            try {
               zenith$getTransform.apply(renderStateAccessor.zenith$getDisplayContext().leftHand(), poseStack.last());
               poseStack.translate(data2.getFloatValue() / 16.0F, data2.getFloatValue2() / 16.0F, data2.getFloatValue3() / 16.0F);
               boolean flag2 = flag && hoveredData.method1546() == WardrobeCategory.ITEM && hoveredData.getFocusSlotEnum() != FocusSlotEnum.ANCHOR;
               if (flag2) {
                  poseStack.translate(0.0F, 0.0F, 0.22F);
               }

               if (data2.getFloatValue4() != 0.0F) {
                  poseStack.rotate(Axis.ZP.rotationDegrees(data2.getFloatValue4()));
               }

               poseStack.rotate(Axis.XP.rotationDegrees(180.0F));
               if (data2.getFloatValue5() != 0.0F) {
                  poseStack.rotate(Axis.XP.rotationDegrees(data2.getFloatValue5()));
               }

               if (flag2) {
                  poseStack.rotate(Axis.XP.rotationDegrees(90.0F));
                  poseStack.rotate(Axis.YP.rotationDegrees(180.0F));
               }

               if (data2.getFloatValue6() != 1.0F) {
                  poseStack.scale(data2.getFloatValue6(), data2.getFloatValue6(), data2.getFloatValue6());
               }

               BoneMatrices.method4787(key, floatValues, 6, poseStack, orderedRenderCommandQueue, lightCoords, OverlayTexture.NO_OVERLAY, true, hoveredData.getRarity().isFlag(), -1);
            } finally {
               poseStack.popPose();
            }

            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private static WardrobeModel method6160(ItemStack itemInHand) {
      Wardrobe wardrobe = Wardrobe.method2488();
      if (wardrobe != null && mc.player != null && wardrobe.showSelf.isEnabled()) {
         HoveredData hoveredData = method6158(wardrobe.method2498(mc.player.getGameProfile().name(), true), itemInHand);
         return hoveredData == null ? null : wardrobe.getRegistry().method1427(hoveredData);
      } else {
         return null;
      }
   }

   @EventTarget
   public void method6161(AttackEvent event) {
      if (event.getType4() == AttackEvent.Type.PRE && mc.player != null && WardrobeParticleEmitter.enabled()) {
         WardrobeModel class2 = method6160(mc.player.getItemInHand(InteractionHand.MAIN_HAND));
         Entity entity = event.getEntity();
         if (class2 != null && class2.data2 != null && entity != null) {
            Vec3 center = entity.getBoundingBox().getCenter();
            if (class2.data2.getParticleEffect2() != null) {
               for (int i = 0; i < 10; i++) {
                  WardrobeParticleEmitter.spawnParticle(
                     class2.data2.getParticleEffect2(),
                     center.x + (Math.random() - 0.5) * entity.getBbWidth(),
                     center.y + (Math.random() - 0.5) * entity.getBbHeight() * 0.8,
                     center.z + (Math.random() - 0.5) * entity.getBbWidth(),
                     (Math.random() - 0.5) * 0.2,
                     Math.random() * 0.15,
                     (Math.random() - 0.5) * 0.2
                  );
               }
            }
         }
      }
   }

   private void tick() {
      if (mc.player != null && mc.level != null && WardrobeParticleEmitter.enabled()) {
         WardrobeModel class2 = method6160(mc.player.getItemInHand(InteractionHand.MAIN_HAND));
         if (class2 != null && class2.data2 != null && class2.data2.getParticleEffect() != null && mc.player.isSwinging()) {
            float swingAnimation = mc.player.getSwingAnimation(1.0F);
            float f = (float)Math.toRadians(mc.player.getYRot());
            Vec3 eyePosition = mc.player.getEyePosition();
            Vec3 vec = new Vec3(-Math.sin(f), 0.0, Math.cos(f));
            Vec3 vec2 = new Vec3(-Math.cos(f), 0.0, -Math.sin(f));

            for (int i = 0; i < 3; i++) {
               float f2 = Math.min(1.0F, swingAnimation + i * 0.08F);
               double scale = 0.7 - 1.4 * f2;
               double y = 0.35 - 0.7 * f2;
               Vec3 vec3 = eyePosition.add(vec.scale(0.95)).add(vec2.scale(scale)).add(0.0, y, 0.0);
               WardrobeParticleEmitter.spawnParticle(class2.data2.getParticleEffect(), vec3.x, vec3.y, vec3.z, 0.0, 0.0, 0.0);
            }
         }

         WardrobeModel class3 = null;
         Wardrobe wardrobe = Wardrobe.method2488();
         if (wardrobe != null) {
            FocusSlotEnum key = !FocusSlotEnum.CROSSBOW.matches(mc.player.getMainHandItem()) && !FocusSlotEnum.CROSSBOW.matches(mc.player.getOffhandItem())
               ? FocusSlotEnum.BOW
               : FocusSlotEnum.CROSSBOW;
            HoveredData hoveredData = wardrobe.method2491(key);
            class3 = hoveredData == null ? null : wardrobe.getRegistry().method1427(hoveredData);
         }

         if (class3 != null && class3.data2 != null && class3.data2.getParticleEffect() != null) {
            for (Entity entity : mc.level.entitiesForRendering()) {
               if (entity instanceof AbstractArrow abstractArrow
                  && abstractArrow.getOwner() == mc.player
                  && abstractArrow.getDeltaMovement().lengthSqr() > 0.01
                  && abstractArrow.tickCount < 200) {
                  WardrobeParticleEmitter.spawnParticle(class3.data2.getParticleEffect(), abstractArrow.getX(), abstractArrow.getY(), abstractArrow.getZ(), 0.0, 0.0, 0.0);
               }
            }
         }
      }
   }
}
