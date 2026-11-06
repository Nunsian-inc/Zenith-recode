package org.zenith.client.modules.impl.combat;

import com.darkmagician6.eventapi.EventTarget;
import java.util.List;
import java.util.Optional;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.ThrownTrident;
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
import org.zenith.client.events.PreRotationEvent;
import org.zenith.client.modules.impl.render.PredictionsModule;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.game.PlayerSimulation;
import org.zenith.utility.game.TargetUtils;
import org.zenith.utility.misc.RotationRequest;

@ModuleInfo(
   name = "TridentAimbot",
   description = "module.tridentaimbot.description",
   category = ModuleCategory.COMBAT,
   method896 = true
)
public final class TridentAimbotModule extends Module {
   public static final TridentAimbotModule INSTANCE = new TridentAimbotModule();
   private static final int CONST_intValue = 1;
   private static final double CONST_doubleValue = 2.5;
   private static final int CONST_intValue2 = 90;
   private static final int CONST_intValue3 = 50;
   private final SliderSetting predictSetting = new SliderSetting(
      "module.elytraTarget.predictSetting", 3.0F, 0.0F, 6.0F, 1.0F, "module.itemUseController.predictSetting.desc", "t"
   );

   @EventTarget
   public void method2788(PreRotationEvent event) {
      if (mc.player.isUsingItem() && mc.player.getUseItem().getItem() == Items.TRIDENT) {
         Rotation rotation = this.method2789();
         if (rotation == null) {
            return;
         }

         Rotation rotation2 = bcCameraTestnerv.method1851(bcCameraTestnerv.getRotationConfig(), rotation);
         Zenith.getInstance().getRotationManager().requestRotation(new RotationRequest(rotation2, () -> rotation2, bcCameraTestnerv.getRotationConfig()), 20, this);
      }
   }

   private Rotation method2789() {
      Rotation rotation = new Rotation(mc.player.getYRot(), mc.player.getXRot());

      try {
         ItemStack useItem = mc.player.getUseItem();
         PlayerSimulation playerSimulation = PlayerSimulation.method6031(1);
         Vec3 eyePosition = this.method2795(playerSimulation);
         LivingEntity livingEntity = this.method2792(playerSimulation, useItem);
         if (livingEntity == null) {
            return null;
         } else {
            AABB aabb = this.method2797(livingEntity);
            Rotation rotation2 = Rotation.lookingAt(this.method2793(aabb), eyePosition);
            return this.method2798(useItem, playerSimulation, rotation2, aabb);
         }
      } catch (Exception e) {
         e.printStackTrace();
         return rotation;
      }
   }

   public Vec3 method2790(LivingEntity livingEntity) {
      return this.method2793(this.method2797(livingEntity));
   }

   public LivingEntity method2791() {
      return mc.player.isUsingItem() && mc.player.getUseItem().getItem() == Items.TRIDENT
         ? this.method2792(PlayerSimulation.method6031(1), mc.player.getUseItem())
         : null;
   }

   private LivingEntity method2792(PlayerSimulation playerSimulation, ItemStack useItem) {
      var hitResults = this.method2794(useItem, RotationUtils.method6911(), playerSimulation);
      if (hitResults != null && !hitResults.isEmpty()) {
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
               if (mc.player != player3 && !Zenith.getInstance().getFriends().method7626(player3)) {
                  double d2 = this.method2790(player3).distanceToSqr(hitResult.getLocation());
                  if (d2 < d) {
                     d = d2;
                     player2 = player3;
                  }
               }
            }

            return player2;
         }
      } else {
         return null;
      }
   }

   public Vec3 method2793(AABB aabb) {
      return new Vec3(Mth.lerp(0.5, aabb.minX, aabb.maxX), Mth.lerp(0.8, aabb.minY, aabb.maxY), Mth.lerp(0.5, aabb.minZ, aabb.maxZ));
   }

   private List<HitResult> method2794(ItemStack useItem, Rotation rotation, PlayerSimulation playerSimulation) {
      HitResult hitResult = PredictionsModule.INSTANCE
         .method2918(this.method2795(playerSimulation), playerSimulation.vec3d2, rotation.toDirection(), new ThrownTrident(mc.level, mc.player, useItem), this.method2804());
      return hitResult == null ? List.of() : List.of(hitResult);
   }

   private Vec3 method2795(PlayerSimulation playerSimulation) {
      return playerSimulation.vec3d.add(0.0, mc.player.getEyeHeight(mc.player.getPose()), 0.0);
   }

   private int method2796() {
      return 1 + (int)this.predictSetting.getValue3();
   }

   private AABB method2797(LivingEntity livingEntity) {
      if (livingEntity instanceof Player player) {
         return PlayerSimulation.method6032(player, this.method2796()).box;
      } else {
         Vec3 vec = livingEntity.position().subtract(livingEntity.xo, livingEntity.yo, livingEntity.zo);
         return livingEntity.getBoundingBox().move(vec.scale(1.0));
      }
   }

   private Rotation method2798(ItemStack useItem, PlayerSimulation playerSimulation, Rotation rotation, AABB aabb) {
      ThrownTrident thrownTrident = new ThrownTrident(mc.level, mc.player, useItem);
      Vec3 vec = this.method2795(playerSimulation);
      Vec3 vec3d2 = playerSimulation.vec3d2;
      double d = this.method2804();
      if (this.method2802(rotation, vec, vec3d2, aabb, thrownTrident, d)) {
         return rotation;
      } else {
         int yRot = Math.round(rotation.getFloatValue());
         int n = Math.round(rotation.getFloatValue2());
         Integer xRot = this.method2799(yRot, n, vec, vec3d2, aabb, thrownTrident, d);
         if (xRot == null) {
            return null;
         } else {
            Rotation rotation2 = new Rotation(yRot, xRot.intValue());
            return this.method2802(rotation2, vec, vec3d2, aabb, thrownTrident, d) ? rotation2 : this.method2800(yRot, xRot, vec, vec3d2, aabb, thrownTrident, d);
         }
      }
   }

   private Integer method2799(int yRot, int n, Vec3 vec, Vec3 vec3d2, AABB aabb, ThrownTrident thrownTrident, double d) {
      for (int i = 0; i <= 90; i++) {
         int xRot = n + i;
         if (this.method2801(yRot, xRot, vec, vec3d2, aabb, thrownTrident, d)) {
            return xRot;
         }

         if (i != 0) {
            int xRot2 = n - i;
            if (this.method2801(yRot, xRot2, vec, vec3d2, aabb, thrownTrident, d)) {
               return xRot2;
            }
         }
      }

      return null;
   }

   private Rotation method2800(int yRot, int xRot, Vec3 vec, Vec3 vec3d2, AABB aabb, ThrownTrident thrownTrident, double d) {
      for (int i = 1; i <= 50; i++) {
         Rotation rotation = new Rotation(yRot + i, xRot);
         if (this.method2802(rotation, vec, vec3d2, aabb, thrownTrident, d)) {
            return rotation.add(3.0F, 0.0F);
         }

         Rotation rotation2 = new Rotation(yRot - i, xRot);
         if (this.method2802(rotation2, vec, vec3d2, aabb, thrownTrident, d)) {
            return rotation2.add(-3.0F, 0.0F);
         }
      }

      return null;
   }

   private boolean method2801(int yRot, int xRot, Vec3 vec, Vec3 vec3d2, AABB aabb, ThrownTrident thrownTrident, double d) {
      Rotation rotation = new Rotation(yRot, xRot);
      Vec3 vec2 = rotation.toDirection();
      double length = vec2.length();
      if (length <= 1.0E-6) {
         return false;
      } else {
         Vec3 vec3 = this.method2793(aabb);
         Vec3 vec4 = new Vec3(vec3.x - vec.x, 0.0, vec3.z - vec.z);
         double horizontalDistance = vec4.horizontalDistance();
         if (horizontalDistance <= 1.0E-6) {
            return true;
         } else {
            vec4 = vec4.normalize();
            Vec3 to = vec;
            Vec3 vec5 = vec2.scale(d / length).add(vec3d2);

            for (int i = 0; i < 300; i++) {
               Vec3 from = to;
               to = to.add(vec5);
               Vec3 vec6 = PredictionsModule.INSTANCE.method2921(thrownTrident, from, vec5);
               BlockHitResult blockHitResult = TargetUtils.method3208(from, to, Block.COLLIDER, thrownTrident);
               double d2 = this.method2806(from, to, blockHitResult);
               double d3 = this.method2805(from, vec, vec4);
               double d4 = this.method2805(to, vec, vec4);
               if (d4 >= horizontalDistance) {
                  double d5 = d4 - d3;
                  double alpha1 = d5 <= 1.0E-6 ? 0.0 : Mth.clamp((horizontalDistance - d3) / d5, 0.0, 1.0);
                  if (!(alpha1 <= d2)) {
                     return false;
                  }

                  double d6 = Mth.lerp(alpha1, from.y, to.y);
                  return d6 >= aabb.minY && d6 <= aabb.maxY;
               }

               if (d2 < 1.0 || to.y < -128.0) {
                  return false;
               }

               if (this.method2803(to, vec6, aabb)) {
                  return false;
               }

               vec5 = vec6;
            }

            return false;
         }
      }
   }

   private boolean method2802(Rotation rotation, Vec3 vec, Vec3 vec3d2, AABB aabb, ThrownTrident thrownTrident, double d) {
      Vec3 vec2 = rotation.toDirection();
      double length = vec2.length();
      if (length <= 1.0E-6) {
         return false;
      } else {
         Vec3 to = vec;
         Vec3 vec3 = vec2.scale(d / length).add(vec3d2);

         for (int i = 0; i < 300; i++) {
            Vec3 from = to;
            to = to.add(vec3);
            Vec3 vec4 = PredictionsModule.INSTANCE.method2921(thrownTrident, from, vec3);
            BlockHitResult blockHitResult = TargetUtils.method3208(from, to, Block.COLLIDER, thrownTrident);
            double d2 = blockHitResult.getType() == Type.MISS ? Double.POSITIVE_INFINITY : from.distanceToSqr(blockHitResult.getLocation());
            var clip = aabb.clip(from, to);
            if (clip.isPresent() && from.distanceToSqr((Vec3)clip.get()) <= d2) {
               return true;
            }

            if (blockHitResult.getType() != Type.MISS || to.y < -128.0) {
               return false;
            }

            if (this.method2803(to, vec4, aabb)) {
               return false;
            }

            vec3 = vec4;
         }

         return false;
      }
   }

   private boolean method2803(Vec3 to, Vec3 vec, AABB aabb) {
      return vec.y <= 0.0 && to.y < aabb.minY - 1.0E-6;
   }

   private double method2804() {
      return 2.5;
   }

   private double method2805(Vec3 from, Vec3 vec, Vec3 vec2) {
      Vec3 vec3 = from.subtract(vec);
      return vec3.x * vec2.x + vec3.z * vec2.z;
   }

   private double method2806(Vec3 from, Vec3 to, BlockHitResult blockHitResult) {
      if (blockHitResult.getType() == Type.MISS) {
         return Double.POSITIVE_INFINITY;
      } else {
         double d = from.distanceTo(to);
         return d <= 1.0E-6 ? 0.0 : from.distanceTo(blockHitResult.getLocation()) / d;
      }
   }
}
