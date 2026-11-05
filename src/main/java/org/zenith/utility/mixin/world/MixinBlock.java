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
   public void injectPlaced(Level var1, BlockPos var2, BlockState var3, LivingEntity var4, ItemStack var5, CallbackInfo var6) {
      if (Minecraft.getInstance().player == var4) {
         BlockPlacedEvent var7 = new BlockPlacedEvent(var2, var3);
         EventManager.call(var7);
      }
   }
}
