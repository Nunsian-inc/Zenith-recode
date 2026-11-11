package org.zenith.utility.mixin.world;

import com.darkmagician6.eventapi.EventManager;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.zenith.client.modules.impl.movement.JesusModule;
import org.zenith.client.events.BlockCollisionEvent;

@Mixin({BlockStateBase.class})
public abstract class MixinAbstractBlockState {
   @Shadow
   public abstract Block getBlock();

   @Shadow
   protected abstract BlockState asState();

   @Inject(
      method = {"getCollisionShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;"},
      at = {@At("RETURN")},
      cancellable = true
   )
   public void onCollisionShape(BlockGetter level, BlockPos blockPos, CollisionContext context,
      CallbackInfoReturnable<VoxelShape> callbackInfo) {
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft == null || minecraft.player == null || minecraft.level == null || level != minecraft.level || !minecraft.isSameThread()
         || !(context instanceof EntityCollisionContext entityContext) || entityContext.getEntity() != minecraft.player) {
         return;
      }
      VoxelShape shape = callbackInfo.getReturnValue();
      VoxelShape replacement = JesusModule.INSTANCE.collisionShape(level, blockPos, this.asState(), context, shape);
      if (replacement != shape) callbackInfo.setReturnValue(replacement);
   }

   @Inject(
      method = {"entityInside(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/InsideBlockEffectApplier;Z)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void onEntityCollision(Level level, BlockPos blockPos, Entity entity, InsideBlockEffectApplier blockEffectApplier, boolean flag, CallbackInfo callbackInfo) {
      if (entity == Minecraft.getInstance().player) {
         BlockCollisionEvent event = new BlockCollisionEvent(this.getBlock(), blockPos);
         EventManager.call(event);
         if (event.isCancelled()) {
            callbackInfo.cancel();
         }
      }
   }
}
