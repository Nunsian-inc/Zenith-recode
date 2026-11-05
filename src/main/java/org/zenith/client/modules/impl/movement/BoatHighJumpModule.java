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
   private boolean flag = false;

   private BoatHighJumpModule() {
   }

   @EventTarget
   public void onUpdate(UpdateEvent event) {
      if (mc.player != null && mc.level != null) {
         if (mc.player.getVehicle() == null) {
            if (mc.player.onGround()) {
               if (mc.options.keyJump.isDown() && !this.flag && this.method2736() != null) {
                  mc.player.setDeltaMovement(mc.player.getDeltaMovement().x, 0.82, mc.player.getDeltaMovement().z);
                  this.flag = true;
               }
            } else {
               this.flag = false;
            }
         }
      }
   }

   private Entity method2736() {
      AABB inflate = mc.player.getBoundingBox().inflate(1.0);

      for (Entity entity : mc.level.entitiesForRendering()) {
         if (entity instanceof Boat boat && !boat.hasPassenger(mc.player) && boat.getBoundingBox().intersects(inflate)) {
            return boat;
         }
      }

      return null;
   }

   @Override
   public void onDisable() {
      this.flag = false;
      super.onDisable();
   }
}
