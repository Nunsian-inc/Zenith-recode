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
   public void method950(CrosshairTargetEvent var1) {
      if (mc.player != null && mc.level != null) {
         Rotation var2 = Zenith.getInstance().getRotationManager().method7844();
         BlockHitResult var3 = TargetUtils.method3216(mc.player.getEyePosition(1.0F), var2, mc.player.blockInteractionRange(), this::method951);
         if (var3 != null && var3.getType() == Type.BLOCK) {
            mc.hitResult = var3;
         }
      }
   }

   private boolean method951(BlockHitResult var1) {
      if (var1 != null && var1.getType() == Type.BLOCK) {
         BlockState var2 = mc.level.getBlockState(var1.getBlockPos());
         if (var2.getBlock() instanceof EnderChestBlock) {
            return true;
         } else {
            BlockEntity var3 = mc.level.getBlockEntity(var1.getBlockPos());
            return var3 instanceof Container && var2.getMenuProvider(mc.level, var1.getBlockPos()) != null;
         }
      } else {
         return false;
      }
   }
}
