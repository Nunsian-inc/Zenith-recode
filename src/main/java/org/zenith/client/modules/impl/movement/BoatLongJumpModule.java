package org.zenith.client.modules.impl.movement;

import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.boat.Boat;
import net.minecraft.world.phys.AABB;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.base.Module;
import org.zenith.client.events.UpdateEvent;

@ModuleInfo(
   name = "BoatLongJump",
   category = ModuleCategory.MOVEMENT,
   description = "module.boatlongjump.description"
)
public final class BoatLongJumpModule extends Module {
   public static final BoatLongJumpModule INSTANCE = new BoatLongJumpModule();
   private boolean jumpTriggered = false;

   private BoatLongJumpModule() {
   }

   @EventTarget
   public void onUpdate(UpdateEvent event) {
      if (mc.player != null && mc.level != null && mc.player.getVehicle() == null) {
         if (mc.player.onGround()) {
            if (mc.options.keyJump.isDown() && !this.jumpTriggered && mc.player.isSprinting() && this.findNearbyBoat() != null) {
               float yaw = mc.player.getYRot();
               double velocityX = mc.player.getDeltaMovement().x + -Math.sin(Math.toRadians(yaw)) * 0.9F;
               double velocityZ = mc.player.getDeltaMovement().z + Math.cos(Math.toRadians(yaw)) * 0.9F;
               mc.player.setDeltaMovement(velocityX, mc.player.getDeltaMovement().y, velocityZ);
               this.jumpTriggered = true;
            }
         } else {
            this.jumpTriggered = false;
         }
      }
   }

   private Entity findNearbyBoat() {
      AABB searchBounds = mc.player.getBoundingBox().inflate(1.0);

      for (Entity entity : mc.level.entitiesForRendering()) {
         if (entity instanceof Boat boat && !boat.hasPassenger(mc.player) && boat.getBoundingBox().intersects(searchBounds)) {
            return boat;
         }
      }

      return null;
   }

   @Override
   public void onDisable() {
      this.jumpTriggered = false;
      super.onDisable();
   }
}
