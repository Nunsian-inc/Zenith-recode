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
   name = "BoatHighJump",
   category = ModuleCategory.MOVEMENT,
   description = "module.boathighjump.description"
)
public final class BoatHighJumpModule extends Module {
   public static final BoatHighJumpModule INSTANCE = new BoatHighJumpModule();
   private boolean jumpTriggered = false;

   private BoatHighJumpModule() {
   }

   @EventTarget
   public void onUpdate(UpdateEvent event) {
      if (mc.player != null && mc.level != null && mc.player.getVehicle() == null) {
         if (mc.player.onGround()) {
            if (mc.options.keyJump.isDown() && !this.jumpTriggered && this.findNearbyBoat() != null) {
               mc.player.setDeltaMovement(mc.player.getDeltaMovement().x, 0.82, mc.player.getDeltaMovement().z);
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
