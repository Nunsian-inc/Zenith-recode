package org.zenith.client.modules.impl.movement;

import com.darkmagician6.eventapi.EventTarget;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.network.protocol.game.ClientboundMoveVehiclePacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import net.minecraft.world.phys.Vec3;
import org.zenith.ChatLogger;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.base.Module;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.SliderSetting;

@ModuleInfo(
   name = "BoatFly",
   category = ModuleCategory.MOVEMENT,
   description = "module.boatfly.description"
)
public final class BoatFlyModule extends Module {
   public static final BoatFlyModule INSTANCE = new BoatFlyModule();
   private static final int ANTI_KICK_INTERVAL_TICKS = 40;
   private static final double ANTI_KICK_VERTICAL_OFFSET = 0.04;
   private static final int REMOUNT_WINDOW_TICKS = 20;
   private static final double MAX_REMOUNT_DISTANCE_SQUARED = 20.25;
   private final ModeSetting mode = new ModeSetting(
      "module.boatfly.mode", "module.boatfly.mode.desc", "module.boatfly.mode.grim", "module.boatfly.mode.vanilla"
   );
   private final SliderSetting speed = new SliderSetting("module.boatfly.speed", 22.0F, 1.0F, 60.0F, 0.5F, "module.boatfly.speed.desc", "bps");
   private final BooleanSetting vertical = new BooleanSetting("module.boatfly.vertical", "module.boatfly.vertical.desc", false);
   private final SliderSetting verticalSpeed = new SliderSetting(
      "module.boatfly.verticalSpeed", 8.0F, 1.0F, 30.0F, 0.5F, "module.boatfly.verticalSpeed.desc", "bps", this.vertical::isEnabled
   );
   private final BooleanSetting remount = new BooleanSetting("module.boatfly.remount", "module.boatfly.remount.desc", true, () -> this.mode.isSelected(0));
   private final BooleanSetting antiKick = new BooleanSetting("module.boatfly.antiKick", "module.boatfly.antiKick.desc", true);
   private final BooleanSetting debug = new BooleanSetting("module.boatfly.debug", "module.boatfly.debug.desc", false);
   private volatile boolean controllingBoat;
   private final AtomicInteger ignoredCorrectionCount = new AtomicInteger();
   private AbstractBoat lastControlledBoat;
   private int remountTicksRemaining;
   private int flightTicks;
   private boolean restoreAntiKickOffset;
   private int debugTicks;

   private BoatFlyModule() {
   }

   @Override
   public void onEnable() {
      this.resetState();
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.resetState();
      super.onDisable();
   }

   private void resetState() {
      this.controllingBoat = false;
      this.ignoredCorrectionCount.set(0);
      this.lastControlledBoat = null;
      this.remountTicksRemaining = 0;
      this.flightTicks = 0;
      this.restoreAntiKickOffset = false;
      this.debugTicks = 0;
   }

   @EventTarget
   public void onUpdate(UpdateEvent event) {
      if (mc.player != null && mc.level != null) {
         if (mc.player.getVehicle() instanceof AbstractBoat boat && boat.getControllingPassenger() == mc.player) {
            this.lastControlledBoat = boat;
            this.remountTicksRemaining = 0;
         } else {
            this.controllingBoat = false;
            if (this.lastControlledBoat != null) {
               if (mc.options.keyShift.isDown() || !this.lastControlledBoat.isAlive()) {
                  this.lastControlledBoat = null;
               } else if (this.remountTicksRemaining == 0) {
                  this.remountTicksRemaining = 20;
                  if (this.debug.isEnabled()) {
                     ChatLogger.method2021("BoatFly: сервер ссадил с лодки");
                  }
               }
            }

            this.tryRemount();
         }

         if (this.debug.isEnabled() && ++this.debugTicks >= 20) {
            this.debugTicks = 0;
            int ignoredCorrections = this.ignoredCorrectionCount.getAndSet(0);
            if (ignoredCorrections > 0) {
               ChatLogger.method2019("BoatFly: проигнорировано откатов за секунду: " + ignoredCorrections);
            }
         }
      } else {
         this.resetState();
      }
   }

   private void tryRemount() {
      if (this.remountTicksRemaining > 0 && this.lastControlledBoat != null) {
         this.remountTicksRemaining--;
         if (this.mode.isSelected(0)
            && this.remount.isEnabled()
            && this.lastControlledBoat.isAlive()
            && !this.lastControlledBoat.isVehicle()
            && !(mc.player.distanceToSqr(this.lastControlledBoat) > 20.25)) {
            if (this.remountTicksRemaining % 2 == 0) {
               mc.gameMode.interact(mc.player, this.lastControlledBoat, new net.minecraft.world.phys.EntityHitResult(this.lastControlledBoat), InteractionHand.MAIN_HAND);
               org.zenith.utility.game.SwingCompat.swing(mc.player, InteractionHand.MAIN_HAND);
            }
         }

         if (this.remountTicksRemaining == 0) {
            this.lastControlledBoat = null;
         }
      }
   }

   @EventTarget
   public void onPacket(PacketEvent event) {
      if (event.isIncoming() && this.controllingBoat && this.mode.isSelected(0) && event.getPacket() instanceof ClientboundMoveVehiclePacket) {
         event.cancel();
         this.ignoredCorrectionCount.incrementAndGet();
      }
   }

   public Vec3 method1932(AbstractBoat boat, Vec3 originalMovement) {
      if (this.isEnabled() && mc.player != null && boat.getControllingPassenger() == mc.player) {
         this.controllingBoat = true;
         float yaw = mc.player.getYRot();
         boat.setYRot(yaw);
         Input input = mc.player.input.keyPresses;
         double forwardInput = (input.forward() ? 1.0 : 0.0) - (input.backward() ? 1.0 : 0.0);
         double sidewaysInput = (input.left() ? 1.0 : 0.0) - (input.right() ? 1.0 : 0.0);
         double velocityX = 0.0;
         double velocityZ = 0.0;
         double inputLength = Math.sqrt(forwardInput * forwardInput + sidewaysInput * sidewaysInput);
         if (inputLength > 0.0) {
            double speedScale = this.speed.getValue3() / 20.0 / inputLength;
            double yawRadians = Math.toRadians(yaw);
            double sinYaw = Math.sin(yawRadians);
            double cosYaw = Math.cos(yawRadians);
            velocityX = (sidewaysInput * cosYaw - forwardInput * sinYaw) * speedScale;
            velocityZ = (forwardInput * cosYaw + sidewaysInput * sinYaw) * speedScale;
         }

         double velocityY = 0.0;
         if (this.vertical.isEnabled()) {
            velocityY = ((input.jump() ? 1.0 : 0.0) - (input.sprint() ? 1.0 : 0.0)) * this.verticalSpeed.getValue3() / 20.0;
         }

         this.flightTicks++;
         if (this.restoreAntiKickOffset) {
            velocityY += 0.04;
            this.restoreAntiKickOffset = false;
         } else if (this.antiKick.isEnabled() && velocityY >= 0.0 && this.flightTicks % 40 == 0 && !boat.onGround()) {
            velocityY -= 0.04;
            this.restoreAntiKickOffset = true;
         }

         Vec3 deltaMovement = new Vec3(velocityX, velocityY, velocityZ);
         boat.setDeltaMovement(deltaMovement);
         boat.fallDistance = 0.0;
         return deltaMovement;
      } else {
         return originalMovement;
      }
   }
}
