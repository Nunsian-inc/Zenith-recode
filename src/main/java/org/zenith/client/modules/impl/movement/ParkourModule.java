package org.zenith.client.modules.impl.movement;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.Priority;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.base.Module;
import org.zenith.client.events.MoveInputEvent;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.misc.MovementInputUtils;

@ModuleInfo(name = "Parkour", description = "module.parkour.description", category = ModuleCategory.MOVEMENT)
public final class ParkourModule extends Module {
   public static final ParkourModule INSTANCE = new ParkourModule();

   private final SliderSetting minGapDepth = new SliderSetting("module.parkour.minGapDepth", 2.0F, 1.1F, 4.0F, 0.1F,
      "module.parkour.minGapDepth.desc", "b");
   private final BooleanSetting skipSneaking = new BooleanSetting("module.parkour.skipSneaking", "module.parkour.skipSneaking.desc", true);
   private final BooleanSetting pauseScaffold = new BooleanSetting("module.parkour.pauseScaffold", "module.parkour.pauseScaffold.desc", true);

   private ParkourModule() {
   }

   @EventTarget(Priority.LOWEST)
   public void onMoveInput(MoveInputEvent event) {
      if (event.isCancelled() || mc.player == null || mc.level == null || mc.gui.screen() != null
         || !mc.player.isAlive() || !mc.player.onGround() || mc.player.isSpectator()
         || mc.player.isInWater() || mc.player.isInLava() || mc.player.onClimbable()
         || mc.player.isPassenger() || mc.player.getAbilities().flying || mc.player.isFallFlying()
         || this.pauseScaffold.isEnabled() && ScaffoldModule.INSTANCE.isEnabled()) {
         return;
      }
      var input = event.getPlayerInput();
      if (input == null || input.jump()
         || this.skipSneaking.isEnabled() && (input.shift() || mc.player.isShiftKeyDown())) {
         return;
      }

      int forward = (input.forward() ? 1 : 0) - (input.backward() ? 1 : 0);
      int strafe = (input.left() ? 1 : 0) - (input.right() ? 1 : 0);
      Vec3 velocity = mc.player.getDeltaMovement();
      Vec3 direction = new Vec3(velocity.x, 0.0, velocity.z);
      double speed = direction.horizontalDistance();
      if (speed < 1.0E-3) {
         if (forward == 0 && strafe == 0) {
            return;
         }
         float yaw = rotationManager.getCurrentRotation() == null
            ? mc.player.getYRot() : rotationManager.getCurrentRotation().getFloatValue();
         double angle = MovementInputUtils.movementYawRadians(yaw, forward, strafe);
         direction = new Vec3(-Math.sin(angle), 0.0, Math.cos(angle));
      } else {
         direction = direction.scale(1.0 / speed);
      }

      AABB box = mc.player.getBoundingBox();
      if (!this.hasSupport(box, 0.05)) {
         return;
      }
      // Проверяем всю площадь ног по реальному движению, а не только блок под направлением взгляда.
      Vec3 ahead = direction.scale(Math.min(0.6, Math.max(0.15, speed + 0.1)));
      // Опора ниже порога сохраняет обычный спуск по ступеням, плитам и перепадам в один блок.
      if (!this.hasSupport(box.move(ahead), this.minGapDepth.getValue3())) {
         // Меняем только ввод этого тика: keyJump не зажимаем, очищать его при выключении не нужно.
         event.setJump(true);
      }
   }

   private boolean hasSupport(AABB box, double depth) {
      AABB below = new AABB(box.minX + 1.0E-4, box.minY - depth, box.minZ + 1.0E-4,
         box.maxX - 1.0E-4, box.minY, box.maxZ - 1.0E-4);
      return mc.level.getBlockCollisions(mc.player, below).iterator().hasNext();
   }
}
