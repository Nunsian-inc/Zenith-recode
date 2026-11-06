package org.zenith.client.modules.impl.movement;

import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.client.player.KeyboardInput;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Rotation;
import org.zenith.base.Module;
import org.zenith.client.events.PreRotationEvent;
import org.zenith.client.events.MoveInputEvent;
import org.zenith.utility.misc.MovementInputUtils;
import org.zenith.utility.misc.RotationRequest;

@ModuleInfo(
   name = "Strafe",
   description = "module.strafe.description",
   category = ModuleCategory.MOVEMENT
)
public final class StrafeModule extends Module {
   public static final StrafeModule INSTANCE = new StrafeModule();

   private StrafeModule() {
   }

   @EventTarget
   public void onStrafeRotation(PreRotationEvent event) {
      float forwardImpulse = KeyboardInput.calculateImpulse(mc.options.keyUp.isDown(), mc.options.keyDown.isDown());
      float strafeImpulse = KeyboardInput.calculateImpulse(mc.options.keyLeft.isDown(), mc.options.keyRight.isDown());
      if (forwardImpulse != 0.0F || strafeImpulse != 0.0F) {
         float playerYaw = mc.player.getYRot();
         float movementYawOffset = (float)Math.toDegrees(Math.atan2(-strafeImpulse, forwardImpulse));
         float movementYaw = playerYaw + movementYawOffset;
         Rotation rotation = rotationManager.getEffectiveRotation().add(rotationManager.getEffectiveRotation().wrappedDeltaTo(new Rotation(movementYaw, mc.player.getXRot())));
         rotationManager.requestRotation(
            new RotationRequest(
               rotation, () -> rotationManager.getBcCameraTestnerv().method1851(bcCameraTestnerv.getRotationConfig(), rotation), bcCameraTestnerv.getRotationConfig()
            ),
            -1,
            this
         );
      }
   }

   @EventTarget
   public void onMoveInput(MoveInputEvent event) {
      if (MovementInputUtils.method7418() && rotationManager.getModule() == this) {
         event.method3047(false);
         event.method3045(false);
         event.method3046(false);
         event.method3038(
            mc.options.keyUp.isDown() || mc.options.keyDown.isDown() || mc.options.keyRight.isDown() || mc.options.keyLeft.isDown()
         );
      }
   }
}
