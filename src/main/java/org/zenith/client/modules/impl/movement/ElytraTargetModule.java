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
import org.zenith.client.events.Event5;
import org.zenith.client.events.WorldRenderEvent;
import org.zenith.client.modules.impl.combat.AuraModule;
import org.zenith.client.modules.impl.combat.AutoTotemModule;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.game.ElytraMath;
import org.zenith.utility.game.PlayerSimulation;
import org.zenith.utility.misc.Handler6;
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
   private static final double CONST_doubleValue = 0.35;
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
      "module.elytraTarget.predictDistance", 2.7F, 1.0F, 5.0F, 0.1F, "module.elytraTarget.predictDistance.desc", "", this::method5246, null
   );
   private final SliderSetting predictTicks = new SliderSetting(
      "module.elytraTarget.predictTicks", 3.0F, 0.0F, 6.0F, 1.0F, "module.elytraTarget.predictTicks.desc", "t", this::method5244, null
   );
   private final SliderSetting predictStrength = new SliderSetting(
      "module.elytraTarget.predictStrength", 3.0F, 1.5F, 10.0F, 0.5F, "module.elytraTarget.predictStrength.desc", "b", this::method5245, null
   );
   private final SliderSetting overtakeBps = new SliderSetting(
      "module.elytraTarget.overtakeBps", 15.0F, 10.0F, 30.0F, 1.0F, "module.elytraTarget.overtakeBps.desc", "bps", this::method5245, null
   );
   private final BooleanSetting instantResponse = new BooleanSetting("module.elytraTarget.instantResponse", "module.elytraTarget.instantResponse.desc", true);
   private final SliderSetting responseSpeed = new SliderSetting(
      "module.elytraTarget.responseSpeed", 1.7F, 1.0F, 3.0F, 0.05F, "module.elytraTarget.responseSpeed.desc", "", this.instantResponse::isEnabled, null
   );
   private final BooleanSetting predictCube = new BooleanSetting("module.elytraTarget.predictCube", "module.elytraTarget.predictCube.desc", true);
   private final SliderSetting predictFillAlpha = new SliderSetting(
      "module.elytraTarget.predictFillAlpha", 40.0F, 0.0F, 255.0F, 1.0F, "module.elytraTarget.predictFillAlpha.desc", "", this.predictCube::isEnabled
   );
   private final StopWatch stopWatch = new StopWatch();
   private final StopWatch stopWatch2 = new StopWatch();
   private LivingEntity livingEntity;
   private AABB box;

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
   public void method5240(WorldRenderEvent event) {
      if (this.predictCube.isEnabled() && this.box != null) {
         int n = Zenith.getInstance().getStyleManager().getClientColor(0).method7287();
         int color = Math.round(this.predictFillAlpha.getValue3()) << 24 | n & (255 << 16 | (char)-1);
         GlowUtil.method2232(this.box, color, 1.5F, true, true, false);
      }
   }

   @EventTarget
   public void method5241(Event5 event) {
      LocalPlayer player = mc.player;
      this.livingEntity = AuraModule.INSTANCE.isEnabled() ? this.method5251() : null;
      if (player != null && this.method5242(this.livingEntity)) {
         Vec3 vec = this.method5247(this.livingEntity);
         this.box = this.method5253(vec);
         Vec3 eyePosition = player.getEyePosition();
         Rotation rotation = RotationUtils.method6912(vec.subtract(eyePosition));
         int priority = AuraModule.INSTANCE.method6358() != null ? UNDER_AURA_ROTATION_PRIORITY : CHASE_ROTATION_PRIORITY;
         rotationManager.method7849(
            new RotationRequest(rotation, () -> bcCameraTestnerv.method1851(AuraModule.INSTANCE.method6320(), rotation), AuraModule.INSTANCE.method6320()),
            priority,
            this
         );
         if (this.autoFWSetting.isEnabled()
            && !ElytraMotionModule.INSTANCE.isFlag10()
            && !AutoTotemModule.INSTANCE.method725()
            && this.stopWatch.method7546(400L)) {
            InventoryUtils.useFireworkInFlight();
            this.stopWatch.reset();
         }
      } else {
         this.method5254();
      }
   }

   public boolean method5242(LivingEntity livingEntity) {
      return this.isEnabled() && this.method5252(livingEntity);
   }

   private int method5243() {
      return (int)this.predictTicks.getValue3();
   }

   private boolean method5244() {
      return this.predictMode.method2101("module.elytraTarget.predictMode.simulation");
   }

   private boolean method5245() {
      return this.predictMode.method2101("module.elytraTarget.predictMode.rotation");
   }

   private boolean method5246() {
      return this.predictMode.method2101("module.elytraTarget.predictMode.velocity");
   }

   private Vec3 method5247(LivingEntity livingEntity) {
      if (this.method5244()) {
         int scale = this.method5243();
         return livingEntity instanceof Player player
            ? PlayerSimulation.method6032(player, scale).box.getCenter()
            : livingEntity.getBoundingBox().getCenter().add(livingEntity.getDeltaMovement().scale(scale));
      } else if (this.method5246()) {
         Vec3 base = livingEntity.getPositionCodec().getBase();
         return livingEntity.dimensions.makeBoundingBox(base).getCenter().add(((Handler6)livingEntity).zenithDLC$getServerDelta().scale(this.predictDistance.getValue3()));
      } else if (this.method5245()) {
         Vec3 center = livingEntity.getBoundingBox().getCenter();
         return method5248(livingEntity) <= this.overtakeBps.getValue3() ? center : center.add(livingEntity.getLookAngle().scale(this.predictStrength.getValue3()));
      } else {
         return livingEntity.getPositionCodec().getBase();
      }
   }

   private static double method5248(LivingEntity livingEntity) {
      double d = livingEntity.getX() - livingEntity.xo;
      double d2 = livingEntity.getY() - livingEntity.yo;
      double d3 = livingEntity.getZ() - livingEntity.zo;
      return Math.sqrt(d * d + d2 * d2 + d3 * d3) * 20.0;
   }

   public Vec3 method5249(Vec3 vec, Vec3 returnValue, Vec3 lookAngle) {
      LocalPlayer player = mc.player;
      if (this.isEnabled() && this.instantResponse.isEnabled() && player != null && mc.level != null && player.isFallFlying() && UseItemUtils.method6517() != null) {
         LivingEntity livingEntity = this.livingEntity;
         if (livingEntity != null && !livingEntity.isFallFlying() && player.getEyePosition().distanceTo(method5250(player.getEyePosition(), livingEntity.getBoundingBox())) < 2.6) {
            return returnValue;
         } else {
            double scale = this.responseSpeed.getValue3();
            if (livingEntity != null && player.getEyePosition().distanceTo(livingEntity.getEyePosition().add(livingEntity.getDeltaMovement())) < 2.0) {
               scale = livingEntity.getDeltaMovement().length();
            }

            return ElytraMath.method5973(player, lookAngle.scale(scale), lookAngle, 1.0);
         }
      } else {
         return returnValue;
      }
   }

   private static Vec3 method5250(Vec3 eyePosition, AABB boundingBox) {
      return new Vec3(
         Mth.clamp(eyePosition.x, boundingBox.minX, boundingBox.maxX), Mth.clamp(eyePosition.y, boundingBox.minY, boundingBox.maxY), Mth.clamp(eyePosition.z, boundingBox.minZ, boundingBox.maxZ)
      );
   }

   /** Цель Aura, если она подходит; иначе ближайшая сущность, которую Aura согласна бить (её фильтр «Цели», друзья, боты). */
   private LivingEntity method5251() {
      LivingEntity livingEntity = AuraModule.INSTANCE.method6358();
      if (this.method5252(livingEntity)) {
         return livingEntity;
      } else {
         LocalPlayer player = mc.player;
         if (player != null && mc.level != null) {
            LivingEntity nearest = null;
            double nearestDistance = Double.MAX_VALUE;

            for (Entity entity : mc.level.entitiesForRendering()) {
               if (entity instanceof LivingEntity living && this.method5252(living) && AuraModule.INSTANCE.method6331(living)) {
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

   private boolean method5252(LivingEntity livingEntity) {
      LocalPlayer player = mc.player;
      return player != null
         && livingEntity != null
         && livingEntity != player
         && livingEntity.isAlive()
         && player.isFallFlying()
         && (livingEntity.isFallFlying() || this.targetMode.method2101("module.elytraTarget.targetMode.all"))
         && player.distanceTo(livingEntity) <= this.pursuitDistance.getValue3();
   }

   private AABB method5253(Vec3 vec) {
      return new AABB(vec.x - 0.35, vec.y - 0.35, vec.z - 0.35, vec.x + 0.35, vec.y + 0.35, vec.z + 0.35);
   }

   private void method5254() {
      this.box = null;
   }

   private void resetState() {
      this.livingEntity = null;
      this.box = null;
      this.stopWatch.reset();
      this.stopWatch2.reset();
   }

   @Generated
   public AABB getBox() {
      return this.box;
   }
}
