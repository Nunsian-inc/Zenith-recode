package org.zenith.utility.mixin.entity;

import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.zenith.client.modules.impl.movement.BoatFlyModule;

@Mixin({AbstractBoat.class})
public abstract class MixinAbstractBoatEntity {
   @ModifyArg(
      method = {"tick()V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/vehicle/boat/AbstractBoat;move(Lnet/minecraft/world/entity/MoverType;Lnet/minecraft/world/phys/Vec3;)V"
      ),
      index = 1
   )
   private Vec3 zenith$boatFlyVelocity(Vec3 vec) {
      return BoatFlyModule.INSTANCE.method1932((AbstractBoat)(Object)this, vec);
   }
}
