package org.zenith.client.modules.impl.movement;

import com.darkmagician6.eventapi.EventTarget;
import java.util.Comparator;
import java.util.List;
import lombok.Generated;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket.Action;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.boat.Boat;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.Rotation;
import org.zenith.base.Module;
import org.zenith.client.events.PreRotationEvent;
import org.zenith.client.events.MoveInputEvent;
import org.zenith.client.events.MovementCorrectionEvent;
import org.zenith.client.events.PostMotionEvent;
import org.zenith.client.events.SprintUpdateEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.modules.impl.combat.AuraModule;
import org.zenith.client.modules.impl.combat.ReachV3Module;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.game.PlayerSimulation;
import org.zenith.utility.misc.MovementInputUtils;
import org.zenith.utility.misc.PacketUtils;
import org.zenith.utility.misc.RotationRequest;
import org.zenith.utility.misc.TimerSpeed;
import org.zenith.utility.misc.ViaCompat;
import org.zenith.utility.network.InventoryUtils;

@ModuleInfo(
   name = "Speed",
   description = "module.speed.description",
   category = ModuleCategory.MOVEMENT
)
public final class SpeedModule extends Module {
   public static final SpeedModule INSTANCE = new SpeedModule();
   private static final int ELYTRA_GROUND_TICKS = 16;
   private final ModeSetting mode = new ModeSetting(
      "module.speed.mode",
      "module.speed.mode.desc",
      "module.speed.mode.holyWorld",
      "module.speed.mode.grimOld",
      "module.speed.mode.reallyWorld",
      "module.speed.mode.grimElytra",
      "module.speed.mode.grim72",
      "module.speed.mode.custom"
   );
   private final SliderSetting grim72SlowTimer = new SliderSetting(
      "module.speed.grim72SlowTimer", 0.4F, 0.0F, 2.0F, 0.1F, "module.speed.grim72SlowTimer.desc", "x", this::isGrim72Mode
   );
   private final SliderSetting grim72FastTimer = new SliderSetting(
      "module.speed.grim72FastTimer", 3.4F, 1.0F, 5.0F, 0.1F, "module.speed.grim72FastTimer.desc", "x", this::isGrim72Mode
   );
   private final SliderSetting speed = new SliderSetting("module.speed.speed", 0.1F, 0.0F, 0.5F, 0.01F, "module.speed.speed.desc", "x", this::isCollisionSpeedMode);
   private final SliderSetting collisionRadius = new SliderSetting(
      "module.speed.collisionRadius", 0.3F, 0.0F, 1.0F, 0.01F, "module.speed.collisionRadius.desc", "b", this::isCollisionSpeedMode
   );
   private final BooleanSetting onlyAura = new BooleanSetting("module.speed.onlyAura", "module.speed.onlyAura.desc", true, this::isCollisionSpeedMode);
   // Custom: скорость (блоков за тик) задаётся вручную, отдельно под эффекты Скорости/Замедления.
   private final SliderSetting customBaseSpeed = new SliderSetting(
      "module.speed.customBaseSpeed", 0.36F, 0.1F, 1.0F, 0.01F, "module.speed.customBaseSpeed.desc", "", this::isCustomMode
   );
   private final SliderSetting customSpeedI = new SliderSetting(
      "module.speed.customSpeedI", 0.41F, 0.1F, 1.0F, 0.01F, "module.speed.customSpeedI.desc", "", this::isCustomMode
   );
   private final SliderSetting customSpeedII = new SliderSetting(
      "module.speed.customSpeedII", 0.47F, 0.1F, 1.0F, 0.01F, "module.speed.customSpeedII.desc", "", this::isCustomMode
   );
   private final SliderSetting customSpeedIII = new SliderSetting(
      "module.speed.customSpeedIII", 0.53F, 0.1F, 1.0F, 0.01F, "module.speed.customSpeedIII.desc", "", this::isCustomMode
   );
   private final SliderSetting customSlowI = new SliderSetting(
      "module.speed.customSlowI", 0.85F, 0.1F, 1.0F, 0.01F, "module.speed.customSlowI.desc", "x", this::isCustomMode
   );
   private final SliderSetting customSlowII = new SliderSetting(
      "module.speed.customSlowII", 0.78F, 0.1F, 1.0F, 0.01F, "module.speed.customSlowII.desc", "x", this::isCustomMode
   );
   private final SliderSetting customSlowIII = new SliderSetting(
      "module.speed.customSlowIII", 0.7F, 0.1F, 1.0F, 0.01F, "module.speed.customSlowIII.desc", "x", this::isCustomMode
   );
   private final BooleanSetting customAutoJump = new BooleanSetting(
      "module.speed.customAutoJump", "module.speed.customAutoJump.desc", false, this::isCustomMode
   );
   private final SliderSetting customAirMultiplier = new SliderSetting(
      "module.speed.customAirMultiplier", 1.435F, 1.0F, 2.0F, 0.005F, "module.speed.customAirMultiplier.desc", "x", this::isCustomMode
   );
   private final BooleanSetting customDamageBoost = new BooleanSetting(
      "module.speed.customDamageBoost", "module.speed.customDamageBoost.desc", false, this::isCustomMode
   );
   private final SliderSetting customDamageSpeed = new SliderSetting(
      "module.speed.customDamageSpeed",
      0.5F,
      0.1F,
      2.0F,
      0.01F,
      "module.speed.customDamageSpeed.desc",
      "",
      () -> this.isCustomMode() && this.customDamageBoost.isEnabled()
   );
   private int ticks = 0;
   private int verticalCollisionTicks = 0;
   private double lastGroundY;
   public boolean flag = false;
   public int intValue2 = 0;

   private SpeedModule() {
   }

   @Override
   public void onEnable() {
      this.intValue2 = 0;
      this.flag = false;
      this.ticks = 0;
      this.verticalCollisionTicks = 0;
      TimerSpeed.setSpeedMultiplier(1.0F);
      if (this.method6606() && mc.player != null && !mc.player.getItemBySlot(EquipmentSlot.CHEST).is(Items.ELYTRA)) {
         this.swapChestEquipment();
      }

      super.onEnable();
   }

   @Override
   public void onDisable() {
      TimerSpeed.setSpeedMultiplier(1.0F);
      if (this.method6606() && mc.player != null && mc.player.getItemBySlot(EquipmentSlot.CHEST).is(Items.ELYTRA)) {
         this.swapChestEquipment();
      }

      super.onDisable();
   }

   @EventTarget
   public void onSprintUpdate(SprintUpdateEvent event) {
      if (this.flag) {
         event.setFlag2(false);
         this.flag = false;
      }
   }

   @EventTarget(4)
   public void onCollisionSpeedUpdate(UpdateEvent event) {
      if (this.intValue2 > 0) {
         this.intValue2--;
      }

      if (this.isCollisionSpeedMode()) {
         if (AuraModule.INSTANCE.isEnabled() || !this.onlyAura.isEnabled()) {
            this.applyCollisionSpeed(null);
         }
      }
   }

   @EventTarget
   public void onReallyWorldMovementCorrection(MovementCorrectionEvent event) {
      if (this.isReallyWorldMode() && mc.player != null) {
         TimerSpeed.setSpeedMultiplier(1.7F);
         if (this.ticks > 3) {
            double pushSpeed = 0.03;
            if (this.ticks % 2 == 0) {
               mc.player.push(0.0, 0.03F, 0.0);
               pushSpeed = mc.player.onGround() ? 0.085 : 0.03;
            }

            if (this.hasMovementInput()) {
               double movementYawRadians = MovementInputUtils.movementYawRadians(mc.player.getYRot(), ViaCompat.method4846(mc.player.input), ViaCompat.method4847(mc.player.input));
               mc.player.push(-Math.sin(movementYawRadians) * pushSpeed, 0.0, Math.cos(movementYawRadians) * pushSpeed);
            }
         }

         this.ticks++;
      }
   }

   @EventTarget
   public void onMoveInput(MoveInputEvent event) {
      if (this.isReallyWorldMode() && mc.player != null) {
         if (mc.player.verticalCollision) {
            this.verticalCollisionTicks++;
         } else {
            this.verticalCollisionTicks = 0;
         }

         if (this.verticalCollisionTicks >= 1) {
            mc.player.jumpFromGround();
         }
      }
   }

   @EventTarget
   public void onReallyWorldPostMotion(PostMotionEvent event) {
      if (this.isReallyWorldMode() && mc.player != null && mc.getConnection() != null) {
         if (this.ticks % 2 == 0) {
            TimerSpeed.setSpeedMultiplier(0.3F);
            PacketUtils.method3150(new ServerboundPlayerCommandPacket(mc.player, Action.START_FALL_FLYING));
         }
      }
   }

   @EventTarget
   public void onPacket(PacketEvent event) {
      if (this.isReallyWorldMode()) {
         if (event.getPacket() instanceof ClientboundPlayerPositionPacket) {
            mc.execute(() -> {
               if (this.ticks % 2 == 1) {
                  this.ticks++;
               }

               TimerSpeed.setSpeedMultiplier(1.0F);
            });
         }
      }
   }

   @EventTarget
   public void onGrim72PostMotion(PostMotionEvent event) {
      if (this.isGrim72Mode()) {
         if (this.shouldResetGrim72()) {
            this.resetGrim72Timer();
         } else {
            if (this.ticks % 2 == 0) {
               TimerSpeed.setSpeedMultiplier(this.grim72SlowTimer.getValue3());
               PacketUtils.method3150(new ServerboundPlayerCommandPacket(mc.player, Action.START_FALL_FLYING));
               PacketUtils.method3150(new ServerboundPlayerCommandPacket(mc.player, Action.START_FALL_FLYING));
            }
         }
      }
   }

   @EventTarget
   public void onGrim72MovementCorrection(MovementCorrectionEvent event) {
      if (this.isGrim72Mode()) {
         if (this.shouldResetGrim72()) {
            this.resetGrim72Timer();
         } else {
            TimerSpeed.setSpeedMultiplier(this.grim72FastTimer.getValue3());
            if (this.ticks > 3) {
               double pushSpeed = 0.03;
               if (this.ticks % 2 == 0) {
                  mc.player.push(0.0, 0.03, 0.0);
                  pushSpeed = mc.player.onGround() ? 0.085 : 0.03;
               }

               double movementYawRadians = MovementInputUtils.movementYawRadians(mc.player.getYRot(), ViaCompat.method4846(mc.player.input), ViaCompat.method4847(mc.player.input));
               mc.player.push(-Math.sin(movementYawRadians) * pushSpeed, 0.0, Math.cos(movementYawRadians) * pushSpeed);
            }

            this.ticks++;
         }
      }
   }

   @EventTarget
   public void onGrim72Packet(PacketEvent event) {
      if (this.isGrim72Mode() && event.isIncoming() && event.getPacket() instanceof ClientboundPlayerPositionPacket) {
         mc.execute(() -> {
            if (!this.shouldResetGrim72()) {
               if (this.ticks % 2 == 1) {
                  this.ticks++;
               }

               TimerSpeed.setSpeedMultiplier(1.0F);
            }
         });
      }
   }

   private boolean shouldResetGrim72() {
      return mc.player == null
         || mc.level == null
         || mc.getConnection() == null
         || !this.hasMovementInput()
         || mc.player.isPassenger()
         || mc.player.getAbilities().flying;
   }

   private void resetGrim72Timer() {
      this.ticks = 0;
      TimerSpeed.setSpeedMultiplier(1.0F);
   }

   public boolean isGrim72Mode() {
      return this.mode.isSelected(4);
   }

   private void applyCollisionSpeed(MoveInputEvent event) {
      boolean wasSprinting;
      if (wasSprinting = mc.player.isSprinting()) {
         mc.player.setSprinting(false);
      }

      PlayerSimulation playerSimulation = PlayerSimulation.method6031(1);
      if (wasSprinting) {
         mc.player.setSprinting(true);
      }

      AABB collisionBox = playerSimulation.box.inflate(this.collisionRadius.getValue3(), this.collisionRadius.getValue3(), this.collisionRadius.getValue3());
      Object collisionTarget = null;
      if (!this.onlyAura.isEnabled()) {
         if (AuraModule.INSTANCE.method6358() == null) {
            for (Entity entity : mc.level.entitiesForRendering()) {
               if (entity != mc.player && (entity instanceof LivingEntity || entity instanceof Boat) && collisionBox.intersects(entity.getBoundingBox())) {
                  collisionTarget = entity;
                  break;
               }
            }
         }
      } else {
         if (AuraModule.INSTANCE.method6358() == null) {
            return;
         }

         if (collisionBox.intersects(AuraModule.INSTANCE.method6358().getBoundingBox())) {
            collisionTarget = AuraModule.INSTANCE.method6358();
         }
      }

      if (collisionTarget instanceof Player) {
         Vec3 simulatedPosition = playerSimulation.vec3d;
         Vec3 targetCenter = (ReachV3Module.INSTANCE.isEnabled() && ReachV3Module.INSTANCE.getVec3d2() != null
               ? ((Entity)collisionTarget).dimensions.makeBoundingBox(ReachV3Module.INSTANCE.getVec3d2())
               : (collisionTarget instanceof Player ? PlayerSimulation.method6032((Player)collisionTarget, 2).box : ((Entity)collisionTarget).getBoundingBox()))
            .getCenter();
         double deltaX = targetCenter.x - simulatedPosition.x;
         double deltaZ = targetCenter.z - simulatedPosition.z;
         double horizontalDistance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
         double configuredSpeed = this.speed.getValue3();
         double minimumDistance = 0.0;
         double remainingDistance = Math.max(0.0, horizontalDistance - minimumDistance);
         double pushScale = Math.min(configuredSpeed, remainingDistance);
         double[] horizontalPush = new double[]{deltaX * pushScale, deltaZ * pushScale};
         mc.player.push(horizontalPush[0], 0.0, horizontalPush[1]);
         if (this.mode.isSelected(0)) {
            if (mc.player.wasSprinting) {
               mc.player.push(-horizontalPush[0], 0.0, -horizontalPush[1]);
            }

            mc.player.setSprinting(false);
            mc.options.keySprint.setDown(false);
            this.flag = true;
         }

         if (mc.player.getBoundingBox().intersects(((Entity)collisionTarget).getBoundingBox())) {
            this.intValue2 = 3;
            return;
         }
      } else {
         this.intValue2 = 0;
      }
   }

   private boolean isReallyWorldMode() {
      return this.mode.isSelected(2);
   }

   @EventTarget
   public void onGrimElytraUpdate(UpdateEvent event) {
      if (this.method6606() && mc.player != null && mc.level != null) {
         if (this.hasMovementInput()) {
            mc.player.setSprinting(true);
         }

         if (!mc.player.onGround() && !mc.player.isFallFlying() && mc.player.getItemBySlot(EquipmentSlot.CHEST).is(Items.ELYTRA)) {
            PacketUtils.method3150(new ServerboundPlayerCommandPacket(mc.player, Action.START_FALL_FLYING));
            mc.player.startFallFlying();
         }

         if (mc.player.onGround()) {
            this.ticks = 16;
            this.lastGroundY = mc.player.getY();
         }
      }
   }

   @EventTarget
   public void onGrimElytraRotation(PreRotationEvent event) {
      if (this.method6606()
         && mc.player != null
         && mc.level != null
         && this.hasMovementInput()
         && !mc.player.onGround()
         && mc.player.getItemBySlot(EquipmentSlot.CHEST).is(Items.ELYTRA)) {
         float movementYaw = (float)Math.toDegrees(
            MovementInputUtils.movementYawRadians(mc.player.getYRot(), ViaCompat.method4846(mc.player.input), ViaCompat.method4847(mc.player.input))
         );
         if (this.ticks > 0) {
            double heightDifference = Math.abs(mc.player.getY() - this.lastGroundY);
            Math.max(0.0, 1.0 - heightDifference);
            float glidePitch = 75.0F;
            Rotation rotation = new Rotation(movementYaw, glidePitch);
            zenith.getRotationManager().requestRotation(new RotationRequest(rotation, () -> rotation, bcCameraTestnerv.getRotationConfig()), 2, this);
         }
      }
   }

   public boolean method6606() {
      return this.mode.isSelected(3);
   }

   private boolean isCollisionSpeedMode() {
      return this.mode.isSelected(0) || this.mode.isSelected(1);
   }

   private void swapChestEquipment() {
      if (mc.player != null) {
         Slot slot;
         if (mc.player.getItemBySlot(EquipmentSlot.CHEST).is(Items.ELYTRA)) {
            slot = InventoryUtils.method916(
               List.of(
                  Items.NETHERITE_CHESTPLATE,
                  Items.DIAMOND_CHESTPLATE,
                  Items.CHAINMAIL_CHESTPLATE,
                  Items.IRON_CHESTPLATE,
                  Items.GOLDEN_CHESTPLATE,
                  Items.LEATHER_CHESTPLATE
               ),
               Comparator.comparingInt(slot2 -> slot2.index)
            );
         } else {
            slot = InventoryUtils.method912(mc.player.inventoryMenu, Items.ELYTRA, Comparator.comparingInt(slot2 -> slot2.index), slot2 -> true);
         }

         if (slot != null) {
            InventoryUtils.method934(slot, 6, true, false);
         }
      }
   }

   private boolean isCustomMode() {
      return this.mode.isSelected(5);
   }

   @EventTarget
   public void onCustomUpdate(UpdateEvent event) {
      if (this.isCustomMode() && mc.player != null && mc.level != null) {
         this.handleCustomMove(mc.player);
      }
   }

   /** Custom: каждый тик ставит горизонтальную скорость по вводу; в воздухе — с множителем, без ввода — стоп. */
   private void handleCustomMove(LocalPlayer player) {
      float horizontalSpeed = this.customBaseSpeed.getValue3();
      MobEffectInstance speedEffect = player.getEffect(MobEffects.SPEED);
      if (speedEffect != null) {
         horizontalSpeed = switch (speedEffect.getAmplifier()) {
            case 0 -> this.customSpeedI.getValue3();
            case 1 -> this.customSpeedII.getValue3();
            default -> this.customSpeedIII.getValue3();
         };
      }

      MobEffectInstance slownessEffect = player.getEffect(MobEffects.SLOWNESS);
      if (slownessEffect != null) {
         horizontalSpeed *= switch (slownessEffect.getAmplifier()) {
            case 0 -> this.customSlowI.getValue3();
            case 1 -> this.customSlowII.getValue3();
            default -> this.customSlowIII.getValue3();
         };
      }

      if (this.customDamageBoost.isEnabled() && player.hurtTime > 0) {
         horizontalSpeed += this.customDamageSpeed.getValue3();
      }

      if (this.customAutoJump.isEnabled() && this.hasMovementInput() && player.onGround() && !mc.options.keyJump.isDown()) {
         player.jumpFromGround();
      }

      if (!player.isInWater() && !player.onGround() && !player.isFallFlying()) {
         this.setHorizontalSpeed(player, horizontalSpeed * this.customAirMultiplier.getValue3());
      }

      if (player.onGround()) {
         this.setHorizontalSpeed(player, horizontalSpeed);
      }
   }

   private void setHorizontalSpeed(LocalPlayer player, double horizontalSpeed) {
      Vec3 deltaMovement = player.getDeltaMovement();
      if (!this.hasMovementInput()) {
         player.setDeltaMovement(0.0, deltaMovement.y, 0.0);
      } else {
         double movementYawRadians = MovementInputUtils.movementYawRadians(player.getYRot(), ViaCompat.method4846(player.input), ViaCompat.method4847(player.input));
         player.setDeltaMovement(-Math.sin(movementYawRadians) * horizontalSpeed, deltaMovement.y, Math.cos(movementYawRadians) * horizontalSpeed);
      }
   }

   private boolean hasMovementInput() {
      return ViaCompat.method4846(mc.player.input) != 0.0F || ViaCompat.method4847(mc.player.input) != 0.0F;
   }

   @Generated
   public int getIntValue23() {
      return this.intValue2;
   }
}
