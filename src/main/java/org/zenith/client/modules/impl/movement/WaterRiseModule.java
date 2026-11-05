package org.zenith.client.modules.impl.movement;

import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.world.effect.MobEffects;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Rotation;
import org.zenith.base.Module;
import org.zenith.client.events.Event5;
import org.zenith.client.events.MoveInputEvent;
import org.zenith.utility.misc.RotationRequest;

@ModuleInfo(
   name = "WaterRise",
   category = ModuleCategory.MOVEMENT,
   description = "module.waterrise.description"
)
public final class WaterRiseModule extends Module {
   public static final WaterRiseModule INSTANCE = new WaterRiseModule();
   public static final float CONST_floatValue = 1.0E-4F;
   private int intValue = -1073741824;

   private WaterRiseModule() {
   }

   private boolean active() {
      if (mc.player == null || mc.level == null || mc.gui.screen() != null) {
         return false;
      } else {
         return mc.player.isInWater() && !mc.player.isPassenger() && !mc.player.isFallFlying() && !mc.player.getAbilities().flying
            ? mc.options.keyJump.isDown() || this.method1741()
            : false;
      }
   }

   private boolean method1741() {
      return mc.options.keyShift.isDown() && !mc.options.keyJump.isDown();
   }

   @EventTarget
   public void onMoveInput(MoveInputEvent event) {
      if (this.active()) {
         if (event.getPlayerInput().forward()) {
            event.method3039(true);
         }

         if (event.getPlayerInput().shift()) {
            event.method3040(false);
         }
      }
   }

   public void method1742() {
      if (mc.player != null && !mc.player.isSprinting() && this.canStartSprinting()) {
         mc.player.setSprinting(true);
      }
   }

   public boolean method1743() {
      return this.isEnabled() && this.active();
   }

   public float method1744() {
      if (mc.player == null || mc.player.isSprinting() || !this.canStartSprinting() || mc.player.tickCount - this.intValue < 10 && mc.player.tickCount >= this.intValue) {
         return 1.0E-4F;
      } else {
         this.intValue = mc.player.tickCount;
         mc.player.setSprinting(true);
         return 1.0F;
      }
   }

   private boolean canStartSprinting() {
      return mc.player.isUnderWater()
         && (mc.player.getFoodData().getFoodLevel() > 6 || mc.player.getAbilities().mayfly)
         && !mc.player.hasEffect(MobEffects.BLINDNESS)
         && !mc.player.isUsingItem();
   }

   @EventTarget
   public void method1745(Event5 event) {
      if (this.active()) {
         float xRot = this.method1741() ? -90.0F : 90.0F;
         Rotation rotation = new Rotation(mc.player.getYRot(), xRot);
         rotationManager.method7849(new RotationRequest(rotation, () -> rotation, bcCameraTestnerv.getRotationConfig()), 0, this);
      }
   }
}
