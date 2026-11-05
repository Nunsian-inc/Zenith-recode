package org.zenith.utility.mixin.input;

import com.darkmagician6.eventapi.EventManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.ClientInput;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.phys.Vec2;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.client.events.MoveInputEvent;
import org.zenith.client.modules.impl.movement.WaterRiseModule;
import org.zenith.utility.misc.ViaCompat;

@Mixin({KeyboardInput.class})
public abstract class MixinKeyboardInput extends ClientInput {
   @Inject(
      method = {"tick()V"},
      at = {@At("TAIL")}
   )
   private void tickHook(CallbackInfo callbackInfo) {
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft.player != null && minecraft.player.input == this) {
         MoveInputEvent event = new MoveInputEvent(this.keyPresses);
         EventManager.call(event);
         Input playerInput = event.getPlayerInput();
         if (playerInput != null) {
            this.keyPresses = new Input(playerInput.forward(), playerInput.backward(), playerInput.left(), playerInput.right(), playerInput.jump(), playerInput.shift(), playerInput.sprint());
            this.moveVector = ViaCompat.method4851(
               new Vec2(zenith$multiplier(playerInput.left(), playerInput.right()), zenith$multiplier(playerInput.forward(), playerInput.backward()))
            );
            WaterRiseModule waterRiseModule = WaterRiseModule.INSTANCE;
            if (waterRiseModule.method1743()) {
               if (playerInput.forward()) {
                  waterRiseModule.method1742();
               } else if (!playerInput.backward()) {
                  this.moveVector = new Vec2(this.moveVector.x, waterRiseModule.method1744());
               }
            }
         }
      }
   }

   @Unique
   private static float zenith$multiplier(boolean left, boolean right) {
      if (left == right) {
         return 0.0F;
      } else {
         return left ? 1.0F : -1.0F;
      }
   }
}
