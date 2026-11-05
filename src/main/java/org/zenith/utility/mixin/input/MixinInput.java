package org.zenith.utility.mixin.input;

import net.minecraft.client.player.ClientInput;
import net.minecraft.world.phys.Vec2;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.zenith.utility.misc.ViaCompat;

@Mixin({ClientInput.class})
public abstract class MixinInput implements ViaCompat.Handler {
   @Shadow
   public Vec2 moveVector;

   @Override
   public void zenith$setMovementVector(Vec2 var1) {
      this.moveVector = var1;
   }
}
