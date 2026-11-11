package org.zenith.client.modules.impl.movement;

import com.darkmagician6.eventapi.EventTarget;
import lombok.Generated;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.zenith.ChatLogger;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Rotation;
import org.zenith.StopWatch;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.base.rotation.RotationUtils;
import org.zenith.client.events.PreRotationEvent;
import org.zenith.client.events.WorldRenderEvent;
import org.zenith.client.modules.impl.combat.AuraModule;
import org.zenith.client.modules.impl.combat.AutoTotemModule;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.game.ElytraMath;
import org.zenith.utility.game.PlayerSimulation;
import org.zenith.utility.misc.ServerPositionTracker;
import org.zenith.utility.misc.RotationRequest;
import org.zenith.utility.network.InventoryUtils;
import org.zenith.utility.network.UseItemUtils;
import org.zenith.utility.render.GlowUtil;

@ModuleInfo(
   name = "ElytraTarget",
   description = "module.elytratarget.description",
   category = ModuleCategory.MOVEMENT
)
public final class ElytraTargetModule extends Module {
   private static final double PREDICTION_BOX_HALF_SIZE = 0.35;
   /** Приоритет ротации, пока ElytraTarget сам ведёт полёт к цели. */
   private static final int CHASE_ROTATION_PRIORITY = 7;
   /** Ниже, чем у Aura (1/3): когда у Aura есть цель, наводится и бьёт она, ElytraTarget только держит курс между её ротациями. */
   private static final int UNDER_AURA_ROTATION_PRIORITY = 0;
   public static final ElytraTargetModule INSTANCE = new ElytraTargetModule();
   private final SliderSetting pursuitDistance = new SliderSetting(
      "module.elytraTarget.pursuitDistance", 30.0F, 10.0F, 100.0F, 5.0F, "module.elytraTarget.pursuitDistance.desc", "b"
   );
   private final ModeSetting targetMode = new ModeSetting(
      "module.elytraTarget.targetMode",
      "module.elytraTarget.targetMode.desc",
      "module.elytraTarget.targetMode.elytra",
      "module.elytraTarget.targetMode.all"
   );
   private final BooleanSetting autoFWSetting = new BooleanSetting("module.elytraTarget.autoFWSetting", "module.elytraTarget.autoFWSetting.desc", true);
   private final ModeSetting predictMode = new ModeSetting(
      "module.elytraTarget.predictMode",
      "module.elytraTarget.predictMode.desc",
      "module.elytraTarget.predictMode.simulation",
      "module.elytraTarget.predictMode.velocity",
      "module.elytraTarget.predictMode.rotation"
   );
   private final SliderSetting predictDistance = new SliderSetting(
      "module.elytraTarget.predictDistance", 2.7F, 1.0F, 5.0F, 0.1F, "module.elytraTarget.predictDistance.desc", "", this::isVelocityPrediction, null
   );
   private final SliderSetting predictTicks = new SliderSetting(
      "module.elytraTarget.predictTicks", 3.0F, 0.0F, 6.0F, 1.0F, "module.elytraTarget.predictTicks.desc", "t", this::isSimulationPrediction, null
   );
   private final SliderSetting predictStrength = new SliderSetting(
      "module.elytraTarget.predictStrength", 3.0F, 1.5F, 10.0F, 0.5F, "module.elytraTarget.predictStrength.desc", "b", this::isRotationPrediction, null
   );
   private final SliderSetting overtakeBps = new SliderSetting(
      "module.elytraTarget.overtakeBps", 15.0F, 10.0F, 30.0F, 1.0F, "module.elytraTarget.overtakeBps.desc", "bps", this::isRotationPrediction, null
   );
   private final BooleanSetting instantResponse = new BooleanSetting("module.elytraTarget.instantResponse", "module.elytraTarget.instantResponse.desc", true);
   private final SliderSetting responseSpeed = new SliderSetting(
      "module.elytraTarget.responseSpeed", 1.7F, 1.0F, 3.0F, 0.05F, "module.elytraTarget.responseSpeed.desc", "", this.instantResponse::isEnabled, null
   );
   private final BooleanSetting predictCube = new BooleanSetting("module.elytraTarget.predictCube", "module.elytraTarget.predictCube.desc", true);
   private final SliderSetting predictFillAlpha = new SliderSetting(
      "module.elytraTarget.predictFillAlpha", 40.0F, 0.0F, 255.0F, 1.0F, "module.elytraTarget.predictFillAlpha.desc", "", this.predictCube::isEnabled
   );
   private final StopWatch fireworkTimer = new StopWatch();
   private final StopWatch unusedTimer = new StopWatch();
   private LivingEntity target;
   private AABB predictionBox;

   private ElytraTargetModule() {
   }

   @Override
   public void onEnable() {
      this.resetState();
      if (!AuraModule.INSTANCE.isEnabled()) {
         ChatLogger.method2019("ElytraTarget работает вместе с Aura: сам не бьёт, а ведёт полёт к цели Aura. Включи Aura.");
      }

      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.resetState();
      super.onDisable();
   }

   @EventTarget
   public void onWorldRender(WorldRenderEvent event) {
      if (this.predictCube.isEnabled() && this.predictionBox != null) {
         int clientColor = Zenith.getInstance().getStyleManager().getClientColor(0).method7287();
         int color = Math.round(this.predictFillAlpha.getValue3()) << 24 | clientColor & (255 << 16 | (char)-1);
         GlowUtil.method2232(this.predictionBox, color, 1.5F, true, true, false);
      }
   }

   @EventTarget
   public void onRotation(PreRotationEvent event) {
      LocalPlayer player = mc.player;
      this.target = AuraModule.INSTANCE.isEnabled() ? this.findTarget() : null;
      if (player != null && this.canPursueTarget(this.target)) {
         Vec3 predictedPosition = this.predictTargetPosition(this.target);
         this.predictionBox = this.createPredictionBox(predictedPosition);
         Vec3 eyePosition = player.getEyePosition();
         Rotation rotation = RotationUtils.method6912(predictedPosition.subtract(eyePosition));
         int priority = AuraModule.INSTANCE.method6358() != null ? UNDER_AURA_ROTATION_PRIORITY : CHASE_ROTATION_PRIORITY;
         rotationManager.requestRotation(
            new RotationRequest(rotation, () -> bcCameraTestnerv.method1851(AuraModule.INSTANCE.method6320(), rotation), AuraModule.INSTANCE.method6320()),
            priority,
            this
         );
         if (this.autoFWSetting.isEnabled()
            && !ElytraMotionModule.INSTANCE.isMovementStopped()
            && !AutoTotemModule.INSTANCE.method725()
            && this.fireworkTimer.hasElapsed(400L)) {
            InventoryUtils.useFireworkInFlight();
            this.fireworkTimer.reset();
         }
      } else {
         this.clearPredictionBox();
      }
   }

   public boolean canPursueTarget(LivingEntity target) {
      return this.isEnabled() && this.isValidTarget(target);
   }

   private int getPredictionTicks() {
      return (int)this.predictTicks.getValue3();
   }

   private boolean isSimulationPrediction() {
      return this.predictMode.method2101("module.elytraTarget.predictMode.simulation");
   }

   private boolean isRotationPrediction() {
      return this.predictMode.method2101("module.elytraTarget.predictMode.rotation");
   }

   private boolean isVelocityPrediction() {
      return this.predictMode.method2101("module.elytraTarget.predictMode.velocity");
   }

   private Vec3 predictTargetPosition(LivingEntity target) {
      if (this.isSimulationPrediction()) {
         int predictionTicks = this.getPredictionTicks();
         return target instanceof Player player
            ? PlayerSimulation.method6032(player, predictionTicks).box.getCenter()
            : target.getBoundingBox().getCenter().add(target.getDeltaMovement().scale(predictionTicks));
      } else if (this.isVelocityPrediction()) {
         Vec3 serverPosition = target.getPositionCodec().getBase();
         return target.dimensions.makeBoundingBox(serverPosition).getCenter().add(((ServerPositionTracker)target).zenithDLC$getServerDelta().scale(this.predictDistance.getValue3()));
      } else if (this.isRotationPrediction()) {
         Vec3 center = target.getBoundingBox().getCenter();
         return getSpeedBlocksPerSecond(target) <= this.overtakeBps.getValue3() ? center : center.add(target.getLookAngle().scale(this.predictStrength.getValue3()));
      } else {
         return target.getPositionCodec().getBase();
      }
   }

   private static double getSpeedBlocksPerSecond(LivingEntity target) {
      double deltaX = target.getX() - target.xo;
      double deltaY = target.getY() - target.yo;
      double deltaZ = target.getZ() - target.zo;
      return Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) * 20.0;
   }

   public Vec3 method5249(Vec3 travelInput, Vec3 fallFlyingVelocity, Vec3 lookAngle) {
      LocalPlayer player = mc.player;
      if (this.isEnabled() && this.instantResponse.isEnabled() && player != null && mc.level != null && player.isFallFlying() && UseItemUtils.method6517() != null) {
         LivingEntity target = this.target;
         if (target != null && !target.isFallFlying() && player.getEyePosition().distanceTo(closestPointOnBox(player.getEyePosition(), target.getBoundingBox())) < 2.6) {
            return fallFlyingVelocity;
         } else {
            double responseScale = this.responseSpeed.getValue3();
            if (target != null && player.getEyePosition().distanceTo(target.getEyePosition().add(target.getDeltaMovement())) < 2.0) {
               responseScale = target.getDeltaMovement().length();
            }

            return ElytraMath.method5973(player, lookAngle.scale(responseScale), lookAngle, 1.0);
         }
      } else {
         return fallFlyingVelocity;
      }
   }

   private static Vec3 closestPointOnBox(Vec3 eyePosition, AABB boundingBox) {
      return new Vec3(
         Mth.clamp(eyePosition.x, boundingBox.minX, boundingBox.maxX), Mth.clamp(eyePosition.y, boundingBox.minY, boundingBox.maxY), Mth.clamp(eyePosition.z, boundingBox.minZ, boundingBox.maxZ)
      );
   }

   /** Цель Aura, если она подходит; иначе ближайшая сущность, которую Aura согласна бить (её фильтр «Цели», друзья, боты). */
   private LivingEntity findTarget() {
      LivingEntity target = AuraModule.INSTANCE.method6358();
      if (this.isValidTarget(target)) {
         return target;
      } else {
         LocalPlayer player = mc.player;
         if (player != null && mc.level != null) {
            LivingEntity nearest = null;
            double nearestDistance = Double.MAX_VALUE;

            for (Entity entity : mc.level.entitiesForRendering()) {
               if (entity instanceof LivingEntity living && this.isValidTarget(living) && AuraModule.INSTANCE.method6331(living)) {
                  double distance = player.distanceToSqr(living);
                  if (distance < nearestDistance) {
                     nearestDistance = distance;
                     nearest = living;
                  }
               }
            }

            return nearest;
         } else {
            return null;
         }
      }
   }

   private boolean isValidTarget(LivingEntity target) {
      LocalPlayer player = mc.player;
      return player != null
         && target != null
         && target != player
         && target.isAlive()
         && player.isFallFlying()
         && (target.isFallFlying() || this.targetMode.method2101("module.elytraTarget.targetMode.all"))
         && player.distanceTo(target) <= this.pursuitDistance.getValue3();
   }

   private AABB createPredictionBox(Vec3 predictedPosition) {
      return new AABB(predictedPosition.x - 0.35, predictedPosition.y - 0.35, predictedPosition.z - 0.35, predictedPosition.x + 0.35, predictedPosition.y + 0.35, predictedPosition.z + 0.35);
   }

   private void clearPredictionBox() {
      this.predictionBox = null;
   }

   private void resetState() {
      this.target = null;
      this.predictionBox = null;
      this.fireworkTimer.reset();
      this.unusedTimer.reset();
   }

   @Generated
   public AABB getBox() {
      return this.predictionBox;
   }
}
