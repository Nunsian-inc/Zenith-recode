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
   private static final int CONST_intValue = 40;
   private static final double CONST_doubleValue = 0.04;
   private static final int CONST_intValue2 = 20;
   private static final double CONST_doubleValue2 = 20.25;
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
   private volatile boolean flag;
   private final AtomicInteger atomicInteger = new AtomicInteger();
   private AbstractBoat abstractBoatEntity;
   private int intValue;
   private int intValue2;
   private boolean flag2;
   private int intValue3;

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
      this.flag = false;
      this.atomicInteger.set(0);
      this.abstractBoatEntity = null;
      this.intValue = 0;
      this.intValue2 = 0;
      this.flag2 = false;
      this.intValue3 = 0;
   }

   @EventTarget
   public void onUpdate(UpdateEvent event) {
      if (mc.player != null && mc.level != null) {
         if (mc.player.getVehicle() instanceof AbstractBoat abstractBoatEntity && abstractBoatEntity.getControllingPassenger() == mc.player) {
            this.abstractBoatEntity = abstractBoatEntity;
            this.intValue = 0;
         } else {
            this.flag = false;
            if (this.abstractBoatEntity != null) {
               if (mc.options.keyShift.isDown() || !this.abstractBoatEntity.isAlive()) {
                  this.abstractBoatEntity = null;
               } else if (this.intValue == 0) {
                  this.intValue = 20;
                  if (this.debug.isEnabled()) {
                     ChatLogger.method2021("BoatFly: сервер ссадил с лодки");
                  }
               }
            }

            this.method1931();
         }

         if (this.debug.isEnabled() && ++this.intValue3 >= 20) {
            this.intValue3 = 0;
            int andSet;
            if ((andSet = this.atomicInteger.getAndSet(0)) > 0) {
               ChatLogger.method2019("BoatFly: проигнорировано откатов за секунду: " + andSet);
            }
         }
      } else {
         this.resetState();
      }
   }

   private void method1931() {
      if (this.intValue > 0 && this.abstractBoatEntity != null) {
         this.intValue--;
         if (this.mode.isSelected(0)
            && this.remount.isEnabled()
            && this.abstractBoatEntity.isAlive()
            && !this.abstractBoatEntity.isVehicle()
            && !(mc.player.distanceToSqr(this.abstractBoatEntity) > 20.25)) {
            if (this.intValue % 2 == 0) {
               mc.gameMode.interact(mc.player, this.abstractBoatEntity, new net.minecraft.world.phys.EntityHitResult(this.abstractBoatEntity), InteractionHand.MAIN_HAND);
               org.zenith.utility.game.SwingCompat.swing(mc.player, InteractionHand.MAIN_HAND);
            }

            if (this.intValue == 0) {
               this.abstractBoatEntity = null;
            }
         } else {
            if (this.intValue == 0) {
               this.abstractBoatEntity = null;
            }
         }
      }
   }

   @EventTarget
   public void onPacket(PacketEvent event) {
      if (event.method7100() && this.flag && this.mode.isSelected(0) && event.getPacket3() instanceof ClientboundMoveVehiclePacket) {
         event.cancel();
         this.atomicInteger.incrementAndGet();
      }
   }

   public Vec3 method1932(AbstractBoat abstractBoat, Vec3 vec) {
      if (this.isEnabled() && mc.player != null && abstractBoat.getControllingPassenger() == mc.player) {
         this.flag = true;
         float yRot = mc.player.getYRot();
         abstractBoat.setYRot(yRot);
         Input keyPresses = mc.player.input.keyPresses;
         double d = (keyPresses.forward() ? 1.0 : 0.0) - (keyPresses.backward() ? 1.0 : 0.0);
         double d2 = (keyPresses.left() ? 1.0 : 0.0) - (keyPresses.right() ? 1.0 : 0.0);
         double x = 0.0;
         double z = 0.0;
         double d3;
         if ((d3 = Math.sqrt(d * d + d2 * d2)) > 0.0) {
            double d4 = this.speed.getValue3() / 20.0 / d3;
            double d5;
            double d6 = Math.sin(d5 = Math.toRadians(yRot));
            double d7 = Math.cos(d5);
            x = (d2 * d7 - d * d6) * d4;
            z = (d * d7 + d2 * d6) * d4;
         }

         double y = 0.0;
         if (this.vertical.isEnabled()) {
            y = ((keyPresses.jump() ? 1.0 : 0.0) - (keyPresses.sprint() ? 1.0 : 0.0)) * this.verticalSpeed.getValue3() / 20.0;
         }

         this.intValue2++;
         if (this.flag2) {
            y += 0.04;
            this.flag2 = false;
         } else if (this.antiKick.isEnabled() && y >= 0.0 && this.intValue2 % 40 == 0 && !abstractBoat.onGround()) {
            y -= 0.04;
            this.flag2 = true;
         }

         Vec3 deltaMovement = new Vec3(x, y, z);
         abstractBoat.setDeltaMovement(deltaMovement);
         abstractBoat.fallDistance = 0.0;
         return deltaMovement;
      } else {
         return vec;
      }
   }
}
