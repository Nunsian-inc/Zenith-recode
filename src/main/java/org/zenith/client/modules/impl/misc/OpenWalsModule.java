package org.zenith.client.modules.impl.misc;

import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.world.Container;
import net.minecraft.world.level.block.EnderChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Rotation;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.client.events.CrosshairTargetEvent;
import org.zenith.utility.game.TargetUtils;

@ModuleInfo(
   name = "OpenWals",
   category = ModuleCategory.MISC,
   description = "module.openwals.description"
)
public final class OpenWalsModule extends Module {
   public static final OpenWalsModule INSTANCE = new OpenWalsModule();

   private OpenWalsModule() {
   }

   @EventTarget
   public void method950(CrosshairTargetEvent event) {
      if (mc.player != null && mc.level != null) {
         Rotation rotation = Zenith.getInstance().getRotationManager().getEffectiveRotation();
         BlockHitResult hitResult = TargetUtils.method3216(mc.player.getEyePosition(1.0F), rotation, mc.player.blockInteractionRange(), this::method951);
         if (hitResult != null && hitResult.getType() == Type.BLOCK) {
            mc.hitResult = hitResult;
         }
      }
   }

   private boolean method951(BlockHitResult blockHitResult) {
      if (blockHitResult != null && blockHitResult.getType() == Type.BLOCK) {
         BlockState blockState = mc.level.getBlockState(blockHitResult.getBlockPos());
         if (blockState.getBlock() instanceof EnderChestBlock) {
            return true;
         } else {
            BlockEntity blockEntity = mc.level.getBlockEntity(blockHitResult.getBlockPos());
            return blockEntity instanceof Container && blockState.getMenuProvider(mc.level, blockHitResult.getBlockPos()) != null;
         }
      } else {
         return false;
      }
   }
}
