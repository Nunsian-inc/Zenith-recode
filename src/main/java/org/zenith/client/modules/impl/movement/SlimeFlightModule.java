package org.zenith.client.modules.impl.movement;

import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.base.Module;
import org.zenith.client.events.PlayerMoveEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.utility.network.InventoryUtils;

@ModuleInfo(
   name = "SlimeFlight",
   category = ModuleCategory.MOVEMENT,
   description = "module.slimeflight.description"
)
public final class SlimeFlightModule extends Module {
   public static final SlimeFlightModule INSTANCE = new SlimeFlightModule();
   public int cooldown;
   public boolean flag = false;

   @EventTarget
   public void onUpdate(UpdateEvent var1) {
      if (this.flag) {
         mc.player.setXRot(60.0F);
         this.flag = false;
      }
   }

   private Block method4512(BlockPos var1) {
      return mc.level.getBlockState(var1).getBlock();
   }

   @EventTarget
   public void method4513(PlayerMoveEvent var1) {
      BlockPos var2 = new BlockPos(mc.player.getBlockX() + 1, mc.player.getBlockY(), mc.player.getBlockZ());
      BlockPos var3 = new BlockPos(mc.player.getBlockX(), mc.player.getBlockY(), mc.player.getBlockZ() + 1);
      BlockPos var4 = new BlockPos(mc.player.getBlockX() - 1, mc.player.getBlockY(), mc.player.getBlockZ());
      BlockPos var5 = new BlockPos(mc.player.getBlockX(), mc.player.getBlockY(), mc.player.getBlockZ() - 1);
      int var6 = InventoryUtils.method922(var0 -> mc.player.getInventory().getItem(var0).getItem() == Items.SLIME_BLOCK);
      HitResult var7 = mc.hitResult;
      if (mc.player.horizontalCollision) {
         if (this.method4512(var2) == Blocks.SLIME_BLOCK
            || this.method4512(var3) == Blocks.SLIME_BLOCK
            || this.method4512(var4) == Blocks.SLIME_BLOCK
            || this.method4512(var5) == Blocks.SLIME_BLOCK) {
            if (!(mc.player.getDeltaMovement().y <= -1.0)) {
               if (var7 instanceof BlockHitResult var8) {
                  Direction var9 = var8.getDirection();
                  if (this.method4512(var8.getBlockPos()) == Blocks.AIR) {
                     return;
                  }

                  BlockHitResult var10 = new BlockHitResult(var8.getLocation(), var9, var8.getBlockPos(), true);
                  if (var6 != -1) {
                     mc.player.getInventory().setSelectedSlot(var6);
                  }

                  this.flag = true;
                  if (this.cooldown >= 1) {
                     mc.gameMode.useItemOn(mc.player, InteractionHand.MAIN_HAND, var10);
                     mc.player.setDeltaMovement(mc.player.getDeltaMovement().x, 0.62, mc.player.getDeltaMovement().z);
                     org.zenith.utility.game.SwingCompat.swing(mc.player, InteractionHand.MAIN_HAND);
                     this.cooldown = 0;
                  } else {
                     this.cooldown++;
                  }
               }
            }
         }
      }
   }
}
