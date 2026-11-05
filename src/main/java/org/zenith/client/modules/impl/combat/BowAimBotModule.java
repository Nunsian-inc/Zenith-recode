package org.zenith.client.modules.impl.combat;

import com.darkmagician6.eventapi.EventTarget;
import java.util.List;
import java.util.Optional;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Rotation;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.base.rotation.RotationUtils;
import org.zenith.client.events.Event5;
import org.zenith.client.modules.impl.render.PredictionsModule;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.game.PlayerSimulation;
import org.zenith.utility.game.TargetUtils;
import org.zenith.utility.misc.RotationRequest;
import org.zenith.utility.network.UseItemUtils;

@ModuleInfo(
   name = "BowAimBot",
   description = "module.bowaimbot.description",
   category = ModuleCategory.COMBAT,
   method896 = true
)
public final class BowAimBotModule extends Module {
   public static final BowAimBotModule INSTANCE = new BowAimBotModule();
   private static final int CONST_intValue = 1;
   private static final int CONST_intValue2 = 90;
   private static final int CONST_intValue3 = 50;
   private int intValue;
   private boolean flag;
   private final SliderSetting predictSetting = new SliderSetting(
      "module.bowAimBot.predictSetting", 3.0F, 0.0F, 6.0F, 1.0F, "module.bowAimBot.predictSetting.desc", "t"
   );
   private final BooleanSetting autoShoot = new BooleanSetting("module.bowAimBot.autoShoot", true);
   private final SliderSetting autoShootDelay = new SliderSetting(
      "module.bowAimBot.autoShootDelay", 0.0F, 0.0F, 20.0F, 1.0F, "module.bowAimBot.autoShootDelay.desc", "t", this.autoShoot::isEnabled
   );

   @EventTarget
   public void method5721(Event5 event) {
      if (this.flag) {
         this.flag = false;
         this.method5722();
      }

      if (mc.player.isUsingItem() && mc.player.getUseItem().getItem() == Items.BOW) {
         this.method5722();
         Rotation rotation = this.method5723();
         if (rotation == null) {
            this.intValue = 0;
            return;
         }

         Rotation rotation2 = bcCameraTestnerv.method1851(bcCameraTestnerv.getRotationConfig(), rotation);
         Zenith.getInstance().getRotationManager().method7849(new RotationRequest(rotation2, () -> rotation2, bcCameraTestnerv.getRotationConfig3()), 20, this);
      } else {
         this.intValue = 0;
      }
   }

   private void method5722() {
      mc.options.keyUse.setDown(UseItemUtils.method6520(KeyMappingHelper.getBoundKeyOf(mc.options.keyUse)));
   }

   private Rotation method5723() {
      Rotation rotation = new Rotation(mc.player.getYRot(), mc.player.getXRot());

      try {
         ItemStack useItem = mc.player.getUseItem();
         PlayerSimulation playerSimulation = PlayerSimulation.method6031(1);
         Vec3 eyePosition = this.method5729(playerSimulation);
         LivingEntity livingEntity = this.method5726(playerSimulation);
         if (livingEntity == null) {
            return null;
         } else {
            AABB aabb = this.method5731(livingEntity);
            Rotation rotation2 = Rotation.method3128(this.method5727(aabb), eyePosition);
            return this.autoShoot.isEnabled() && this.method5733(useItem, playerSimulation, aabb) ? null : this.method5732(useItem, playerSimulation, rotation2, aabb);
         }
      } catch (Exception e) {
         e.printStackTrace();
         return rotation;
      }
   }

   public Vec3 method5724(LivingEntity livingEntity) {
      return this.method5727(this.method5731(livingEntity));
   }

   public LivingEntity method5725() {
      return this.method5726(PlayerSimulation.method6031(1));
   }

   private LivingEntity method5726(PlayerSimulation playerSimulation) {
      var hitResults = this.method5728(mc.player.getUseItem(), RotationUtils.method6911(), playerSimulation);
      if (hitResults != null && (!hitResults.isEmpty())) {
         HitResult hitResult = (HitResult)hitResults.getFirst();
         if (hitResult == null) {
            return null;
         } else if (hitResult instanceof EntityHitResult entityHitResult
            && entityHitResult.getEntity() instanceof LivingEntity player
            && !Zenith.getInstance().getFriends().method7626(player)) {
            return player;
         } else {
            Player player2 = null;
            double d = Double.MAX_VALUE;

            for (Player player3 : mc.level.players()) {
               double d2;
               if (mc.player != player3
                  && !Zenith.getInstance().getFriends().method7626(player3)
                  && (d2 = this.method5724(player3).distanceToSqr(hitResult.getLocation())) < d) {
                  d = d2;
                  player2 = player3;
               }
            }

            return player2;
         }
      } else {
         return null;
      }
   }

   public Vec3 method5727(AABB aabb) {
      return new Vec3(Mth.lerp(0.5, aabb.minX, aabb.maxX), Mth.lerp(0.8, aabb.minY, aabb.maxY), Mth.lerp(0.5, aabb.minZ, aabb.maxZ));
   }

   private List<HitResult> method5728(ItemStack useItem, Rotation rotation, PlayerSimulation playerSimulation) {
      return PredictionsModule.INSTANCE.method2916(useItem, rotation, this.method5729(playerSimulation), playerSimulation.vec3d2, 1);
   }

   private Vec3 method5729(PlayerSimulation playerSimulation) {
      return playerSimulation.vec3d.add(0.0, mc.player.getEyeHeight(mc.player.getPose()), 0.0);
   }

   private int method5730() {
      return 1 + (int)this.predictSetting.getValue3();
   }

   private AABB method5731(LivingEntity livingEntity) {
      if (livingEntity instanceof Player player) {
         return PlayerSimulation.method6032(player, this.method5730()).box;
      } else {
         Vec3 vec = livingEntity.position().subtract(livingEntity.xo, livingEntity.yo, livingEntity.zo);
         return livingEntity.getBoundingBox().move(vec.scale(1.0));
      }
   }

   private Rotation method5732(ItemStack useItem, PlayerSimulation playerSimulation, Rotation rotation, AABB aabb) {
      Arrow arrow = new Arrow(mc.level, mc.player, useItem, useItem);
      Vec3 vec = this.method5729(playerSimulation);
      Vec3 vec3d2 = playerSimulation.vec3d2;
      double d = this.method5739();
      if (this.method5738(rotation, vec, vec3d2, aabb, arrow, d)) {
         return rotation;
      } else {
         int yRot = Math.round(rotation.getFloatValue());
         int n = Math.round(rotation.getFloatValue2());
         Integer xRot = this.method5735(yRot, n, vec, vec3d2, aabb, arrow, d);
         if (xRot == null) {
            return null;
         } else {
            Rotation rotation2 = new Rotation(yRot, xRot.intValue());
            return this.method5738(rotation2, vec, vec3d2, aabb, arrow, d) ? rotation2 : this.method5736(yRot, xRot, vec, vec3d2, aabb, arrow, d);
         }
      }
   }

   private boolean method5733(ItemStack useItem, PlayerSimulation playerSimulation, AABB aabb) {
      if (mc.gameMode == null) {
         return false;
      } else if (!this.method5734(aabb)) {
         return false;
      } else {
         mc.options.keyUse.setDown(false);
         this.flag = true;
         return true;
      }
   }

   private boolean method5734(AABB aabb) {
      double d;
      int n = ((d = mc.player.distanceToSqr(aabb.getCenter())) < 100.0 ? (d < 16.0 ? 2 : 5) : 7) + (int)this.autoShootDelay.getValue3();
      if (this.intValue++ <= n) {
         return false;
      } else {
         this.intValue = 0;
         return true;
      }
   }

   private Integer method5735(int yRot, int n, Vec3 vec, Vec3 vec3d2, AABB aabb, Arrow arrow, double d) {
      for (int i = 0; i <= 90; i++) {
         int xRot = n + i;
         if (this.method5737(yRot, xRot, vec, vec3d2, aabb, arrow, d)) {
            return xRot;
         }

         if (i != 0) {
            int xRot2 = n - i;
            if (this.method5737(yRot, xRot2, vec, vec3d2, aabb, arrow, d)) {
               return xRot2;
            }
         }
      }

      return null;
   }

   private Rotation method5736(int yRot, int xRot, Vec3 vec, Vec3 vec3d2, AABB aabb, Arrow arrow, double d) {
      for (int i = 1; i <= 50; i++) {
         Rotation rotation = new Rotation(yRot + i, xRot);
         if (this.method5738(rotation, vec, vec3d2, aabb, arrow, d)) {
            return rotation.method3140(3.0F, 0.0F);
         }

         Rotation rotation2 = new Rotation(yRot - i, xRot);
         if (this.method5738(rotation2, vec, vec3d2, aabb, arrow, d)) {
            return rotation2.method3140(-3.0F, 0.0F);
         }
      }

      return null;
   }

   private boolean method5737(int yRot, int xRot, Vec3 vec, Vec3 vec3d2, AABB aabb, Arrow arrow, double d) {
      Rotation rotation = new Rotation(yRot, xRot);
      Vec3 vec2 = rotation.method3135();
      double length;
      if ((length = vec2.length()) <= 1.0E-6) {
         return false;
      } else {
         Vec3 vec3 = this.method5727(aabb);
         Vec3 vec4 = new Vec3(vec3.x - vec.x, 0.0, vec3.z - vec.z);
         double horizontalDistance;
         if ((horizontalDistance = vec4.horizontalDistance()) <= 1.0E-6) {
            return true;
         } else {
            vec4 = vec4.normalize();
            Vec3 to = vec;
            Vec3 vec5 = vec2.scale(d / length).add(vec3d2);

            for (int i = 0; i < 300; i++) {
               Vec3 from = to;
               to = to.add(vec5);
               Vec3 vec6 = PredictionsModule.INSTANCE.method2921(arrow, from, vec5);
               BlockHitResult blockHitResult = TargetUtils.method3208(from, to, Block.COLLIDER, arrow);
               double d2 = this.method5741(from, to, blockHitResult);
               double d3 = this.method5740(from, vec, vec4);
               double d4;
               if ((d4 = this.method5740(to, vec, vec4)) >= horizontalDistance) {
                  double d5;
                  double alpha1;
                  if (!((alpha1 = (d5 = d4 - d3) <= 1.0E-6 ? 0.0 : Mth.clamp((horizontalDistance - d3) / d5, 0.0, 1.0)) <= d2)) {
                     return false;
                  }

                  double d6;
                  return (d6 = Mth.lerp(alpha1, from.y, to.y)) >= aabb.minY && d6 <= aabb.maxY;
               }

               if (d2 < 1.0) {
                  return false;
               }

               if (to.y < -128.0) {
                  return false;
               }

               if (to.y < aabb.minY && vec6.y <= 0.0) {
                  return false;
               }

               vec5 = vec6;
            }

            return false;
         }
      }
   }

   private boolean method5738(Rotation rotation, Vec3 vec, Vec3 vec3d2, AABB aabb, Arrow arrow, double d) {
      Vec3 vec2 = rotation.method3135();
      double length;
      if ((length = vec2.length()) <= 1.0E-6) {
         return false;
      } else {
         Vec3 to = vec;
         Vec3 vec3 = vec2.scale(d / length).add(vec3d2);

         for (int i = 0; i < 300; i++) {
            Vec3 from = to;
            to = to.add(vec3);
            Vec3 vec4 = PredictionsModule.INSTANCE.method2921(arrow, from, vec3);
            BlockHitResult blockHitResult = TargetUtils.method3208(from, to, Block.COLLIDER, arrow);
            double d2 = blockHitResult.getType() == Type.MISS ? Double.POSITIVE_INFINITY : from.distanceToSqr(blockHitResult.getLocation());
            var clip = aabb.clip(from, to);
            if (clip.isPresent() && from.distanceToSqr((Vec3)clip.get()) <= d2) {
               return true;
            }

            if (blockHitResult.getType() != Type.MISS || to.y < -128.0) {
               return false;
            }

            if (to.y < aabb.minY - 1.0E-6 && vec4.y <= 0.0) {
               return false;
            }

            vec3 = vec4;
         }

         return false;
      }
   }

   private double method5739() {
      float f = mc.player.getTicksUsingItem() + mc.getDeltaTracker().getGameTimeDeltaPartialTick(false) + 1.0F;
      return 3.0F * Mth.clamp(f / 20.0F, 0.0F, 1.0F);
   }

   private double method5740(Vec3 from, Vec3 vec, Vec3 vec2) {
      Vec3 vec3 = from.subtract(vec);
      return vec3.x * vec2.x + vec3.z * vec2.z;
   }

   private double method5741(Vec3 from, Vec3 to, BlockHitResult blockHitResult) {
      if (blockHitResult.getType() == Type.MISS) {
         return Double.POSITIVE_INFINITY;
      } else {
         double d;
         return (d = from.distanceTo(to)) <= 1.0E-6 ? 0.0 : from.distanceTo(blockHitResult.getLocation()) / d;
      }
   }
}
