package org.zenith.utility.mixin.world;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.zenith.Rotation;
import org.zenith.Zenith;

@Mixin({Item.class})
public class MixinItem {
   @ModifyExpressionValue(
      method = {"getPlayerPOVHitResult(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/ClipContext$Fluid;)Lnet/minecraft/world/phys/BlockHitResult;"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/player/Player;calculateViewVector(FF)Lnet/minecraft/world/phys/Vec3;"
      )}
   )
   private static Vec3 hookFixRotation(Vec3 vec, Level level, Player player, Fluid fluid) {
      Rotation currentRotation = Zenith.getInstance().getRotationManager().getCurrentRotation();
      return player == Minecraft.getInstance().player && currentRotation != null ? currentRotation.toDirection() : vec;
   }
}
