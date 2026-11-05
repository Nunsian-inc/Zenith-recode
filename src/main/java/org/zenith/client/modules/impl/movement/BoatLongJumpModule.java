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
   private boolean flag = false;

   private BoatLongJumpModule() {
   }

   @EventTarget
   public void onUpdate(UpdateEvent event) {
      if (mc.player != null && mc.level != null) {
         if (mc.player.getVehicle() == null) {
            if (mc.player.onGround()) {
               if (mc.options.keyJump.isDown() && !this.flag && mc.player.isSprinting() && this.method1825() != null) {
                  float yRot = mc.player.getYRot();
                  double xd = mc.player.getDeltaMovement().x + -Math.sin(Math.toRadians(yRot)) * 0.9F;
                  double zd = mc.player.getDeltaMovement().z + Math.cos(Math.toRadians(yRot)) * 0.9F;
                  mc.player.setDeltaMovement(xd, mc.player.getDeltaMovement().y, zd);
                  this.flag = true;
               }
            } else {
               this.flag = false;
            }
         }
      }
   }

   private Entity method1825() {
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
