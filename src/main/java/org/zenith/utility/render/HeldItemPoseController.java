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
import org.zenith.utility.misc.Class15;
import org.zenith.utility.misc.HoveredData;
import org.zenith.utility.misc.WardrobeCategory;
import org.zenith.utility.mixin.accessors.ItemRenderStateAccessor;
import org.zenith.utility.mixin.accessors.LayerRenderStateAccessor;

public final class HeldItemPoseController implements MinecraftAccess {
   private static final int CONST_intValue = -1;

   public HeldItemPoseController() {
      EventManager.register(this);
      ClientTickEvents.END_CLIENT_TICK.register((EndTick)var1 -> this.tick());
   }

   public static boolean method6156(
      ArmedEntityRenderState var0, ItemStackRenderState var1, ItemStack var2, PoseStack var3, SubmitNodeCollector var4, int var5
   ) {
      if (var0 instanceof AvatarRenderState var6) {
         HoveredData var7 = method6158(WardrobeFeatureRenderer.method4444(var6), var2);
         if (var7 == null) {
            return false;
         } else {
            float var8 = 0.0F;
            if (FocusSlotEnum.CROSSBOW.matches(var2)) {
               var8 = CrossbowItem.isCharged(var2)
                  ? 1.0F
                  : (var6.isUsingItem && var6.maxCrossbowChargeDuration > 0.0F ? Math.min(0.97F, var6.ticksUsingItem / var6.maxCrossbowChargeDuration) : 0.0F);
            } else if (var6.isUsingItem && FocusSlotEnum.BOW.matches(var2)) {
               var8 = BowItem.getPowerForTime((int)var6.ticksUsingItem);
            }

            return method6159(var7, var6.id, var1, var3, var4, var5, var8, var6.ageInTicks / 20.0F, true);
         }
      } else {
         return false;
      }
   }

   public static boolean method6157(LivingEntity var0, ItemStack var1, ItemStackRenderState var2, PoseStack var3, SubmitNodeCollector var4, int var5) {
      Wardrobe var6 = Wardrobe.method2488();
      if (var6 != null && var0 != null && var0 == mc.player && var6.showSelf.isEnabled()) {
         HoveredData var7 = method6158(var6.method2498(mc.player.getGameProfile().name(), true), var1);
         if (var7 == null) {
            return false;
         } else {
            float var8 = 0.0F;
            if (FocusSlotEnum.CROSSBOW.matches(var1)) {
               var8 = CrossbowItem.isCharged(var1)
                  ? 1.0F
                  : (
                     var0.isUsingItem() && var0.getUseItem() == var1
                        ? Math.min(0.97F, (float)var0.getTicksUsingItem() / CrossbowItem.getChargeDuration(var1, var0))
                        : 0.0F
                  );
            } else if (var0.isUsingItem() && var0.getUseItem() == var1 && FocusSlotEnum.BOW.matches(var1)) {
               var8 = BowItem.getPowerForTime(var0.getTicksUsingItem());
            }

            float var9 = (var0.tickCount + mc.getDeltaTracker().getGameTimeDeltaPartialTick(true)) / 20.0F;
            return method6159(var7, var0.getId(), var2, var3, var4, var5, var8, var9, false);
         }
      } else {
         return false;
      }
   }

   private static HoveredData method6158(List<HoveredData> var0, ItemStack var1) {
      Wardrobe var2 = Wardrobe.method2488();
      FocusSlotEnum var3 = FocusSlotEnum.method545(var1);
      return var2 != null && var3 != null && !var0.isEmpty() ? var2.method2499(var0, var3) : null;
   }

   private static boolean method6159(
      HoveredData var0, int var1, ItemStackRenderState var2, PoseStack var3, SubmitNodeCollector var4, int var5, float var6, float var7, boolean var8
   ) {
      Wardrobe var9 = Wardrobe.method2488();
      Class15 var10 = var9 == null ? null : var9.getRegistry().method1427(var0);
      if (var10 != null && var10.data2 != null) {
         ItemRenderStateAccessor var11 = (ItemRenderStateAccessor)(Object)var2;
         if (var11.zenith$getLayerCount() > 0 && var11.zenith$getDisplayContext() != null) {
            ItemTransform var12 = ((LayerRenderStateAccessor)(Object)var11.zenith$getLayers()[0]).zenith$getTransform();
            Class15.Data2 var13 = var10.data2;
            AnimationCache.Entry var14 = AnimationCache.method7621(var1, var0, var9.isFlag());
            var14.animationState.method6539(var10, "idle", var7);
            float[] var15 = var14.class16.method2139(var10, var14.animationState, var7, null);
            var14.class16.method2141(var10, var14.animationState, "pull", var6, var6 > 0.0F ? 1.0F : 0.0F);
            if (var13.getIntValue() >= 0) {
               float var16 = var6 > (var0.getFocusSlotEnum() == FocusSlotEnum.CROSSBOW ? 0.99F : 0.05F) ? 1.0F : 0.0F;
               var15[var13.getIntValue() * 9 + 6] = var16;
               var15[var13.getIntValue() * 9 + 7] = var16;
               var15[var13.getIntValue() * 9 + 8] = var16;
            }

            var3.pushPose();

            try {
               var12.apply(var11.zenith$getDisplayContext().leftHand(), var3.last());
               var3.translate(var13.getFloatValue() / 16.0F, var13.getFloatValue2() / 16.0F, var13.getFloatValue3() / 16.0F);
               boolean var20 = var8 && var0.method1546() == WardrobeCategory.ITEM && var0.getFocusSlotEnum() != FocusSlotEnum.ANCHOR;
               if (var20) {
                  var3.translate(0.0F, 0.0F, 0.22F);
               }

               if (var13.getFloatValue4() != 0.0F) {
                  var3.rotate(Axis.ZP.rotationDegrees(var13.getFloatValue4()));
               }

               var3.rotate(Axis.XP.rotationDegrees(180.0F));
               if (var13.getFloatValue5() != 0.0F) {
                  var3.rotate(Axis.XP.rotationDegrees(var13.getFloatValue5()));
               }

               if (var20) {
                  var3.rotate(Axis.XP.rotationDegrees(90.0F));
                  var3.rotate(Axis.YP.rotationDegrees(180.0F));
               }

               if (var13.getFloatValue6() != 1.0F) {
                  var3.scale(var13.getFloatValue6(), var13.getFloatValue6(), var13.getFloatValue6());
               }

               BoneMatrices.method4787(var10, var15, 6, var3, var4, var5, OverlayTexture.NO_OVERLAY, true, var0.getRarity().isFlag(), -1);
            } finally {
               var3.popPose();
            }

            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private static Class15 method6160(ItemStack var0) {
      Wardrobe var1 = Wardrobe.method2488();
      if (var1 != null && mc.player != null && var1.showSelf.isEnabled()) {
         HoveredData var2 = method6158(var1.method2498(mc.player.getGameProfile().name(), true), var0);
         return var2 == null ? null : var1.getRegistry().method1427(var2);
      } else {
         return null;
      }
   }

   @EventTarget
   public void method6161(AttackEvent var1) {
      if (var1.getType4() == AttackEvent.Type.PRE && mc.player != null && Util19.enabled()) {
         Class15 var2 = method6160(mc.player.getItemInHand(InteractionHand.MAIN_HAND));
         Entity var3 = var1.getEntity();
         if (var2 != null && var2.data2 != null && var3 != null) {
            Vec3 var4 = var3.getBoundingBox().getCenter();
            if (var2.data2.getParticleEffect2() != null) {
               for (int var5 = 0; var5 < 10; var5++) {
                  Util19.method5043(
                     var2.data2.getParticleEffect2(),
                     var4.x + (Math.random() - 0.5) * var3.getBbWidth(),
                     var4.y + (Math.random() - 0.5) * var3.getBbHeight() * 0.8,
                     var4.z + (Math.random() - 0.5) * var3.getBbWidth(),
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
      if (mc.player != null && mc.level != null && Util19.enabled()) {
         Class15 var1 = method6160(mc.player.getItemInHand(InteractionHand.MAIN_HAND));
         if (var1 != null && var1.data2 != null && var1.data2.getParticleEffect() != null && mc.player.isSwinging()) {
            float var2 = mc.player.getSwingAnimation(1.0F);
            float var3 = (float)Math.toRadians(mc.player.getYRot());
            Vec3 var4 = mc.player.getEyePosition();
            Vec3 var5 = new Vec3(-Math.sin(var3), 0.0, Math.cos(var3));
            Vec3 var6 = new Vec3(-Math.cos(var3), 0.0, -Math.sin(var3));

            for (int var7 = 0; var7 < 3; var7++) {
               float var8 = Math.min(1.0F, var2 + var7 * 0.08F);
               double var9 = 0.7 - 1.4 * var8;
               double var11 = 0.35 - 0.7 * var8;
               Vec3 var13 = var4.add(var5.scale(0.95)).add(var6.scale(var9)).add(0.0, var11, 0.0);
               Util19.method5043(var1.data2.getParticleEffect(), var13.x, var13.y, var13.z, 0.0, 0.0, 0.0);
            }
         }

         Class15 var14 = null;
         Wardrobe var15 = Wardrobe.method2488();
         if (var15 != null) {
            FocusSlotEnum var16 = !FocusSlotEnum.CROSSBOW.matches(mc.player.getMainHandItem()) && !FocusSlotEnum.CROSSBOW.matches(mc.player.getOffhandItem())
               ? FocusSlotEnum.BOW
               : FocusSlotEnum.CROSSBOW;
            HoveredData var18 = var15.method2491(var16);
            var14 = var18 == null ? null : var15.getRegistry().method1427(var18);
         }

         if (var14 != null && var14.data2 != null && var14.data2.getParticleEffect() != null) {
            for (Entity var19 : mc.level.entitiesForRendering()) {
               if (var19 instanceof AbstractArrow var20
                  && var20.getOwner() == mc.player
                  && var20.getDeltaMovement().lengthSqr() > 0.01
                  && var20.tickCount < 200) {
                  Util19.method5043(var14.data2.getParticleEffect(), var20.getX(), var20.getY(), var20.getZ(), 0.0, 0.0, 0.0);
               }
            }
         }
      }
   }
}
