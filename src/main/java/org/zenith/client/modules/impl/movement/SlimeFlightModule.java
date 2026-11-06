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
   public boolean adjustPitchNextTick = false;

   @EventTarget
   public void onUpdate(UpdateEvent event) {
      if (this.adjustPitchNextTick) {
         mc.player.setXRot(60.0F);
         this.adjustPitchNextTick = false;
      }
   }

   private Block getBlock(BlockPos southPos) {
      return mc.level.getBlockState(southPos).getBlock();
   }

   @EventTarget
   public void onPlayerMove(PlayerMoveEvent event) {
      BlockPos eastPos = new BlockPos(mc.player.getBlockX() + 1, mc.player.getBlockY(), mc.player.getBlockZ());
      BlockPos southPos = new BlockPos(mc.player.getBlockX(), mc.player.getBlockY(), mc.player.getBlockZ() + 1);
      BlockPos westPos = new BlockPos(mc.player.getBlockX() - 1, mc.player.getBlockY(), mc.player.getBlockZ());
      BlockPos northPos = new BlockPos(mc.player.getBlockX(), mc.player.getBlockY(), mc.player.getBlockZ() - 1);
      int slimeHotbarSlot = InventoryUtils.method922(slot -> mc.player.getInventory().getItem(slot).getItem() == Items.SLIME_BLOCK);
      HitResult hitResult = mc.hitResult;
      if (mc.player.horizontalCollision) {
         if (this.getBlock(eastPos) == Blocks.SLIME_BLOCK
            || this.getBlock(southPos) == Blocks.SLIME_BLOCK
            || this.getBlock(westPos) == Blocks.SLIME_BLOCK
            || this.getBlock(northPos) == Blocks.SLIME_BLOCK) {
            if (!(mc.player.getDeltaMovement().y <= -1.0)) {
               if (hitResult instanceof BlockHitResult blockHitResult) {
                  Direction direction = blockHitResult.getDirection();
                  if (this.getBlock(blockHitResult.getBlockPos()) == Blocks.AIR) {
                     return;
                  }

                  BlockHitResult blockHit = new BlockHitResult(blockHitResult.getLocation(), direction, blockHitResult.getBlockPos(), true);
                  if (slimeHotbarSlot != -1) {
                     mc.player.getInventory().setSelectedSlot(slimeHotbarSlot);
                  }

                  this.adjustPitchNextTick = true;
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
