package org.zenith.utility.mixin.world;

import com.darkmagician6.eventapi.EventManager;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.client.events.BlockPlacedEvent;

@Mixin({Block.class})
public class MixinBlock {
   @Inject(
      method = {"setPlacedBy(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;)V"},
      at = {@At("HEAD")}
   )
   public void injectPlaced(Level level, BlockPos blockPos, BlockState blockState, LivingEntity livingEntity, ItemStack stack, CallbackInfo callbackInfo) {
      if (Minecraft.getInstance().player == livingEntity) {
         BlockPlacedEvent event = new BlockPlacedEvent(blockPos, blockState);
         EventManager.call(event);
      }
   }
}
