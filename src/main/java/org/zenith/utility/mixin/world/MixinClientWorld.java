package org.zenith.utility.mixin.world;

import com.darkmagician6.eventapi.EventManager;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.client.events.EntityAddEvent;
import org.zenith.utility.game.SnowSurfaceTracker;

@Mixin({ClientLevel.class})
public class MixinClientWorld {
   @Inject(
      method = {"addEntity(Lnet/minecraft/world/entity/Entity;)V"},
      at = {@At("RETURN")}
   )
   public void injectAddEntity(Entity entity, CallbackInfo callbackInfo) {
      EventManager.call(new EntityAddEvent(entity));
   }

   @Inject(
      method = {"sendBlockUpdated(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;I)V"},
      at = {@At("RETURN")}
   )
   public void zenith$snowBlockChanged(BlockPos pos, BlockState blockState, BlockState blockState2, int n, CallbackInfo callbackInfo) {
      SnowSurfaceTracker.onBlockChanged((ClientLevel)(Object)this, pos, blockState, blockState2);
   }
}
