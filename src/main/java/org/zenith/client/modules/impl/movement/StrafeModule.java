package org.zenith.client.modules.impl.movement;

import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.client.player.KeyboardInput;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Rotation;
import org.zenith.base.Module;
import org.zenith.client.events.Event5;
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
   public void method1527(Event5 event) {
      float impulse = KeyboardInput.calculateImpulse(mc.options.keyUp.isDown(), mc.options.keyDown.isDown());
      float f = KeyboardInput.calculateImpulse(mc.options.keyLeft.isDown(), mc.options.keyRight.isDown());
      if (impulse != 0.0F || f != 0.0F) {
         float yRot = mc.player.getYRot();
         float f2 = (float)Math.toDegrees(Math.atan2(-f, impulse));
         float yRot2 = yRot + f2;
         Rotation rotation = rotationManager.method7844().method3141(rotationManager.method7844().method3131(new Rotation(yRot2, mc.player.getXRot())));
         rotationManager.method7849(
            new RotationRequest(
               rotation, () -> rotationManager.getBcCameraTestnerv().method1851(bcCameraTestnerv.getRotationConfig(), rotation), bcCameraTestnerv.getRotationConfig()
            ),
            -1,
            this
         );
      }
   }

   @EventTarget
   public void method1528(MoveInputEvent event) {
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
