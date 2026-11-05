package org.zenith.client.events;

import lombok.Generated;
import net.minecraft.world.entity.player.Input;
import org.zenith.CancellableEvent;

public class MoveInputEvent extends CancellableEvent {
   private Input playerInput;

   public void method3038(boolean var1) {
      this.playerInput = new Input(
         var1,
         this.playerInput.backward(),
         this.playerInput.left(),
         this.playerInput.right(),
         this.playerInput.jump(),
         this.playerInput.shift(),
         this.playerInput.sprint()
      );
   }

   public void method3039(boolean var1) {
      this.playerInput = new Input(
         this.playerInput.forward(),
         this.playerInput.backward(),
         this.playerInput.left(),
         this.playerInput.right(),
         this.playerInput.jump(),
         this.playerInput.shift(),
         var1
      );
   }

   public void method3040(boolean var1) {
      this.playerInput = new Input(
         this.playerInput.forward(),
         this.playerInput.backward(),
         this.playerInput.left(),
         this.playerInput.right(),
         this.playerInput.jump(),
         var1,
         this.playerInput.sprint()
      );
   }

   public void method3041(boolean var1) {
      this.playerInput = new Input(
         this.playerInput.forward(),
         this.playerInput.backward(),
         this.playerInput.left(),
         this.playerInput.right(),
         var1,
         this.playerInput.shift(),
         this.playerInput.sprint()
      );
   }

   public void method3042(float var1, float var2) {
      boolean[] var3 = method3043(var1);
      boolean[] var4 = method3043(var2);
      this.playerInput = new Input(var3[0], var3[1], var4[0], var4[1], this.playerInput.jump(), this.playerInput.shift(), this.playerInput.sprint());
   }

   private static boolean[] method3043(float var0) {
      if (var0 == 1.0F) {
         return new boolean[]{true, false};
      } else {
         return var0 == -1.0F ? new boolean[]{false, true} : new boolean[]{false, false};
      }
   }

   public void method3044() {
      this.playerInput = new Input(false, false, false, false, false, false, false);
   }

   public void method3045(boolean var1) {
      this.playerInput = new Input(
         this.playerInput.forward(),
         this.playerInput.backward(),
         var1,
         this.playerInput.right(),
         this.playerInput.jump(),
         this.playerInput.shift(),
         this.playerInput.sprint()
      );
   }

   public void method3046(boolean var1) {
      this.playerInput = new Input(
         this.playerInput.forward(),
         this.playerInput.backward(),
         this.playerInput.left(),
         var1,
         this.playerInput.jump(),
         this.playerInput.shift(),
         this.playerInput.sprint()
      );
   }

   public void method3047(boolean var1) {
      this.playerInput = new Input(
         this.playerInput.forward(),
         var1,
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
   public void setPlayerInput(Input var1) {
      this.playerInput = var1;
   }

   @Generated
   public MoveInputEvent(Input var1) {
      this.playerInput = var1;
   }
}
