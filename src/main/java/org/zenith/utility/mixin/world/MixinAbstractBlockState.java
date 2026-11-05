package org.zenith.utility.mixin.world;

import com.darkmagician6.eventapi.EventManager;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.client.events.BlockCollisionEvent;

@Mixin({BlockStateBase.class})
public abstract class MixinAbstractBlockState {
   @Shadow
   public abstract Block getBlock();

   @Inject(
      method = {"entityInside(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/InsideBlockEffectApplier;Z)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void onEntityCollision(Level var1, BlockPos var2, Entity var3, InsideBlockEffectApplier var4, boolean var5, CallbackInfo var6) {
      if (var3 == Minecraft.getInstance().player) {
         BlockCollisionEvent var7 = new BlockCollisionEvent(this.getBlock(), var2);
         EventManager.call(var7);
         if (var7.isCancelled()) {
            var6.cancel();
         }
      }
   }
}
