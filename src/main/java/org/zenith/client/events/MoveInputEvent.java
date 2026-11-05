package org.zenith.client.events;

import lombok.Generated;
import net.minecraft.world.entity.player.Input;
import org.zenith.CancellableEvent;

public class MoveInputEvent extends CancellableEvent {
   private Input playerInput;

   public void method3038(boolean forward) {
      this.playerInput = new Input(
         forward,
         this.playerInput.backward(),
         this.playerInput.left(),
         this.playerInput.right(),
         this.playerInput.jump(),
         this.playerInput.shift(),
         this.playerInput.sprint()
      );
   }

   public void method3039(boolean sprint) {
      this.playerInput = new Input(
         this.playerInput.forward(),
         this.playerInput.backward(),
         this.playerInput.left(),
         this.playerInput.right(),
         this.playerInput.jump(),
         this.playerInput.shift(),
         sprint
      );
   }

   public void method3040(boolean shift) {
      this.playerInput = new Input(
         this.playerInput.forward(),
         this.playerInput.backward(),
         this.playerInput.left(),
         this.playerInput.right(),
         this.playerInput.jump(),
         shift,
         this.playerInput.sprint()
      );
   }

   public void method3041(boolean jump) {
      this.playerInput = new Input(
         this.playerInput.forward(),
         this.playerInput.backward(),
         this.playerInput.left(),
         this.playerInput.right(),
         jump,
         this.playerInput.shift(),
         this.playerInput.sprint()
      );
   }

   public void method3042(float f, float f2) {
      boolean[] flags = method3043(f);
      boolean[] flags2 = method3043(f2);
      this.playerInput = new Input(flags[0], flags[1], flags2[0], flags2[1], this.playerInput.jump(), this.playerInput.shift(), this.playerInput.sprint());
   }

   private static boolean[] method3043(float f) {
      if (f == 1.0F) {
         return new boolean[]{true, false};
      } else {
         return f == -1.0F ? new boolean[]{false, true} : new boolean[]{false, false};
      }
   }

   public void method3044() {
      this.playerInput = new Input(false, false, false, false, false, false, false);
   }

   public void method3045(boolean left) {
      this.playerInput = new Input(
         this.playerInput.forward(),
         this.playerInput.backward(),
         left,
         this.playerInput.right(),
         this.playerInput.jump(),
         this.playerInput.shift(),
         this.playerInput.sprint()
      );
   }

   public void method3046(boolean right) {
      this.playerInput = new Input(
         this.playerInput.forward(),
         this.playerInput.backward(),
         this.playerInput.left(),
         right,
         this.playerInput.jump(),
         this.playerInput.shift(),
         this.playerInput.sprint()
      );
   }

   public void method3047(boolean backward) {
      this.playerInput = new Input(
         this.playerInput.forward(),
         backward,
         this.playerInput.left(),
         this.playerInput.right(),
         this.playerInput.jump(),
         this.playerInput.shift(),
         this.playerInput.sprint()
      );
   }

   @Generated
   public Input getPlayerInput() {
      return this.playerInput;
   }

   @Generated
   public void setPlayerInput(Input playerInput) {
      this.playerInput = playerInput;
   }

   @Generated
   public MoveInputEvent(Input playerInput) {
      this.playerInput = playerInput;
   }
}
