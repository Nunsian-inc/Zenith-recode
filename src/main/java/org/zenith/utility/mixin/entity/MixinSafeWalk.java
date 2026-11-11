package org.zenith.utility.mixin.entity;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.zenith.client.modules.impl.movement.SafeWalkModule;

@Mixin(Player.class)
public abstract class MixinSafeWalk {
   @ModifyExpressionValue(
      method = "maybeBackOffFromEdge(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/entity/MoverType;)Lnet/minecraft/world/phys/Vec3;",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isStayingOnGroundSurface()Z"),
      require = 1
   )
   private boolean zenith$safeWalk(boolean vanilla) {
      // Меняем только условие обрезки у края: обычный Shift, поза и скорость остаются ванильными.
      return vanilla || SafeWalkModule.INSTANCE.shouldClipEdge((Player)(Object)this);
   }
}
