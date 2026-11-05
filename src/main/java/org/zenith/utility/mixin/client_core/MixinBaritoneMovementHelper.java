package org.zenith.utility.mixin.client_core;

import baritone.pathing.movement.MovementHelper;
import baritone.pathing.precompute.Ternary;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.zenith.Zenith;
import org.zenith.base.Module;

@Mixin(
   value = {MovementHelper.class},
   remap = false
)
public interface MixinBaritoneMovementHelper {
   @Inject(
      method = {"a(Lnet/minecraft/world/level/block/state/BlockState;)Lbaritone/pathing/precompute/Ternary;"},
      at = {@At("HEAD")},
      cancellable = true,
      require = 0
   )
   private static void zenith$walkThroughWeb(BlockState blockState, CallbackInfoReturnable<Ternary> callbackInfoReturnable) {
      if (blockState.getBlock() == Blocks.COBWEB && zenith$isAutoZamokEnabled()) {
         callbackInfoReturnable.setReturnValue(Ternary.a);
      }
   }

   @Inject(
      method = {"b(Lnet/minecraft/world/level/block/state/BlockState;)Z"},
      at = {@At("HEAD")},
      cancellable = true,
      require = 0
   )
   private static void zenith$walkIntoWeb(BlockState blockState, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
      if (blockState.getBlock() == Blocks.COBWEB && zenith$isAutoZamokEnabled()) {
         callbackInfoReturnable.setReturnValue(false);
      }
   }

   @Unique
   private static boolean zenith$isAutoZamokEnabled() {
      Module moduleValue = Zenith.getInstance().getModuleManager().method2484("AutoZamok");
      return moduleValue != null && moduleValue.isEnabled();
   }
}
