package org.zenith.client.modules.impl.movement;

import com.darkmagician6.eventapi.EventTarget;
import lombok.Generated;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.zenith.KeyEvent;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.StopWatch;
import org.zenith.base.Module;
import org.zenith.client.events.PlayerMoveEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.network.InventoryUtils;
import org.zenith.utility.network.UseItemUtils;

@ModuleInfo(
   name = "ElytraMotion",
   description = "module.elytramotion.description",
   category = ModuleCategory.MOVEMENT
)
public final class ElytraMotionModule extends Module {
   public static final ElytraMotionModule INSTANCE = new ElytraMotionModule();
   private final ModeSetting mode = new ModeSetting(
      "module.elytraMotion.mode", "module.elytraMotion.mode.desc", "module.elytraMotion.mode.distance", "module.elytraMotion.mode.smart"
   );
   private final SliderSetting distance = new SliderSetting(
      "module.elytraMotion.distance", 0.1F, 0.1F, 4.0F, 1.0F, "module.elytraMotion.distance.desc", "b", () -> this.mode.isSelected(0), null
   );
   private boolean movementStopped = false;
   private final StopWatch fireworkTimer = new StopWatch();

   private ElytraMotionModule() {
   }

   @Override
   public void onEnable() {
      this.movementStopped = false;
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.movementStopped = false;
      super.onDisable();
   }

   @EventTarget
   public void onKey(KeyEvent event) {
   }

   @EventTarget
   public void onUpdate(UpdateEvent event) {
      if (!mc.player.isFallFlying()) {
         this.movementStopped = false;
      } else {
         if (this.isWithinTargetRange()) {
            if (UseItemUtils.method6517() != null) {
               this.movementStopped = true;
            } else if (this.fireworkTimer.hasElapsed(300L) && !this.movementStopped) {
               InventoryUtils.useFireworkInFlight();
               this.fireworkTimer.reset();
            }
         } else {
            this.movementStopped = false;
         }
      }
   }

   public boolean isWithinTargetRange() {
      AABB box = ElytraTargetModule.INSTANCE.getBox();
      if (box == null) {
         return false;
      } else {
         return this.mode.isSelected(0)
            ? mc.player.distanceToSqr(box.getCenter()) <= this.distance.getValue3() * this.distance.getValue3()
            : mc.player.getBoundingBox().intersects(ElytraTargetModule.INSTANCE.getBox());
      }
   }

   @EventTarget
   public void onPlayerMove(PlayerMoveEvent event) {
      if (this.movementStopped) {
         event.setVec3d(Vec3.ZERO);
      }
   }

   @EventTarget
   public void onPacket(PacketEvent event) {
   }

   @Generated
   public boolean isMovementStopped() {
      return this.movementStopped;
   }
}
