package org.zenith.client.modules.impl.player;

import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.zenith.ChatLogger;
import org.zenith.MinecraftAccess;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.base.Module;
import org.zenith.client.events.UpdateEvent;

@ModuleInfo(
   name = "ClanUpgrade",
   category = ModuleCategory.PLAYER,
   description = "module.clanupgrade.description"
)
public final class ClanUpgradeModule extends Module implements MinecraftAccess {
   public static final ClanUpgradeModule INSTANCE = new ClanUpgradeModule();
   private static final float CONST_floatValue = 90.0F;
   private static final float CONST_floatValue2 = 0.0F;
   private float floatValue = 0.0F;
   private float floatValue2 = 0.0F;
   private boolean flag = false;
   private boolean flag2 = false;

   @EventTarget
   public void onUpdate(UpdateEvent event) {
      if (mc.player != null && mc.level != null && mc.gameMode != null) {
         ItemStack itemInHand = mc.player.getItemInHand(InteractionHand.MAIN_HAND);
         if (itemInHand.getItem() != Items.REDSTONE) {
            if (!this.flag2) {
               this.flag2 = true;
               ChatLogger.method2022("Возьмите редстоун в руку!");
            }
         } else {
            this.flag2 = false;
            BlockPos blockPosition = mc.player.blockPosition();
            BlockState blockState = mc.level.getBlockState(blockPosition);
            if (!this.flag) {
               this.floatValue = mc.player.getXRot();
               this.floatValue2 = mc.player.getYRot();
               this.flag = true;
            }

            mc.player.setXRot(90.0F);
            mc.player.setYRot(0.0F);
            if (blockState.isAir()) {
               this.method3947(blockPosition);
            } else if (blockState.getBlock() == Blocks.REDSTONE_WIRE) {
               this.method3948(blockPosition);
            }
         }
      }
   }

   private void method3947(BlockPos blockPosition) {
      BlockPos below = blockPosition.below();
      BlockState blockState = mc.level.getBlockState(below);
      if (blockState.isSolid()) {
         Vec3 vec = new Vec3(below.getX() + 0.5, below.getY() + 1.0, below.getZ() + 0.5);
         BlockHitResult blockHitResult = new BlockHitResult(vec, Direction.UP, below, false);
         mc.gameMode.useItemOn(mc.player, InteractionHand.MAIN_HAND, blockHitResult);
         org.zenith.utility.game.SwingCompat.swing(mc.player, InteractionHand.MAIN_HAND);
      }
   }

   private void method3948(BlockPos blockPosition) {
      mc.gameMode.startDestroyBlock(blockPosition, Direction.UP);
      org.zenith.utility.game.SwingCompat.swing(mc.player, InteractionHand.MAIN_HAND);
   }

   @Override
   public void onDisable() {
      super.onDisable();
      if (this.flag && mc.player != null) {
         mc.player.setXRot(this.floatValue);
         mc.player.setYRot(this.floatValue2);
         this.flag = false;
      }
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.flag = false;
      this.flag2 = false;
      if (mc.player != null) {
         this.floatValue = mc.player.getXRot();
         this.floatValue2 = mc.player.getYRot();
      }
   }
}
