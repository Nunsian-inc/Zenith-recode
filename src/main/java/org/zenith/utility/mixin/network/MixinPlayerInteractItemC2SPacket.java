package org.zenith.utility.mixin.network;

import net.minecraft.network.protocol.game.ServerboundUseItemPacket;
import net.minecraft.world.InteractionHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.Rotation;
import org.zenith.Zenith;

@Mixin({ServerboundUseItemPacket.class})
public class MixinPlayerInteractItemC2SPacket {
   @Shadow
   public float yRot;
   @Shadow
   public float xRot;

   @Inject(
      method = {"<init>(Lnet/minecraft/world/InteractionHand;IFF)V"},
      at = {@At("RETURN")}
   )
   private void modifyRotation(InteractionHand interactionHand, int n, float f, float f2, CallbackInfo callbackInfo) {
      if (Zenith.getInstance().getRotationManager().getCurrentRotation() != null) {
         Rotation currentRotation = Zenith.getInstance().getRotationManager().getCurrentRotation();
         this.yRot = currentRotation.getFloatValue();
         this.xRot = currentRotation.getFloatValue2();
      }
   }
}
