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
   public void onUpdate(UpdateEvent var1) {
      if (mc.player != null && mc.level != null) {
         if (mc.player.getVehicle() == null) {
            if (mc.player.onGround()) {
               if (mc.options.keyJump.isDown() && !this.flag && mc.player.isSprinting() && this.method1825() != null) {
                  float var2 = mc.player.getYRot();
                  double var3 = mc.player.getDeltaMovement().x + -Math.sin(Math.toRadians(var2)) * 0.9F;
                  double var5 = mc.player.getDeltaMovement().z + Math.cos(Math.toRadians(var2)) * 0.9F;
                  mc.player.setDeltaMovement(var3, mc.player.getDeltaMovement().y, var5);
                  this.flag = true;
               }
            } else {
               this.flag = false;
            }
         }
      }
   }

   private Entity method1825() {
      AABB var1 = mc.player.getBoundingBox().inflate(1.0);

      for (Entity var3 : mc.level.entitiesForRendering()) {
         if (var3 instanceof Boat var4 && !var4.hasPassenger(mc.player) && var4.getBoundingBox().intersects(var1)) {
            return var4;
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
