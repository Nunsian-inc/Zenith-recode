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
   public void onUpdate(UpdateEvent event) {
      if (this.flag) {
         mc.player.setXRot(60.0F);
         this.flag = false;
      }
   }

   private Block method4512(BlockPos blockPos) {
      return mc.level.getBlockState(blockPos).getBlock();
   }

   @EventTarget
   public void method4513(PlayerMoveEvent event) {
      BlockPos pos = new BlockPos(mc.player.getBlockX() + 1, mc.player.getBlockY(), mc.player.getBlockZ());
      BlockPos blockPos = new BlockPos(mc.player.getBlockX(), mc.player.getBlockY(), mc.player.getBlockZ() + 1);
      BlockPos pos2 = new BlockPos(mc.player.getBlockX() - 1, mc.player.getBlockY(), mc.player.getBlockZ());
      BlockPos pos3 = new BlockPos(mc.player.getBlockX(), mc.player.getBlockY(), mc.player.getBlockZ() - 1);
      int selected = InventoryUtils.method922(slot -> mc.player.getInventory().getItem(slot).getItem() == Items.SLIME_BLOCK);
      HitResult hitResult = mc.hitResult;
      if (mc.player.horizontalCollision) {
         if (this.method4512(pos) == Blocks.SLIME_BLOCK
            || this.method4512(blockPos) == Blocks.SLIME_BLOCK
            || this.method4512(pos2) == Blocks.SLIME_BLOCK
            || this.method4512(pos3) == Blocks.SLIME_BLOCK) {
            if (!(mc.player.getDeltaMovement().y <= -1.0)) {
               if (hitResult instanceof BlockHitResult blockHitResult) {
                  Direction direction = blockHitResult.getDirection();
                  if (this.method4512(blockHitResult.getBlockPos()) == Blocks.AIR) {
                     return;
                  }

                  BlockHitResult blockHit = new BlockHitResult(blockHitResult.getLocation(), direction, blockHitResult.getBlockPos(), true);
                  if (selected != -1) {
                     mc.player.getInventory().setSelectedSlot(selected);
                  }

                  this.flag = true;
                  if (this.cooldown >= 1) {
                     mc.gameMode.useItemOn(mc.player, InteractionHand.MAIN_HAND, blockHit);
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
