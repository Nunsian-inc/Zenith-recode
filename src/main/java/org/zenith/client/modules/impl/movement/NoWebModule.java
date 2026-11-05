package org.zenith.client.modules.impl.movement;

import com.darkmagician6.eventapi.EventTarget;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket.Action;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.base.Module;
import org.zenith.client.events.BlockCollisionEvent;
import org.zenith.client.settings.ModeSetting;
import org.zenith.utility.misc.MovementInputUtils;
import org.zenith.utility.misc.ViaCompat;
import org.zenith.utility.network.UseItemUtils;

@ModuleInfo(
   name = "NoWeb",
   category = ModuleCategory.MOVEMENT,
   description = "module.noweb.description"
)
public final class NoWebModule extends Module {
   public static final NoWebModule INSTANCE = new NoWebModule();
   private final ModeSetting mode = new ModeSetting("module.noweb.mode", "module.noweb.mode.desc");
   private final ModeSetting.Entry entry = new ModeSetting.Entry(this.mode, "module.noweb.mode.default");
   private final ModeSetting.Entry entry2 = new ModeSetting.Entry(this.mode, "module.noweb.mode.rw116");
   private final ModeSetting.Entry entry3 = new ModeSetting.Entry(this.mode, "module.noweb.mode.rw121");

   private NoWebModule() {
   }

   @EventTarget
   public void method266(BlockCollisionEvent var1) {
      if (var1.getBlock() == Blocks.COBWEB) {
         if (this.entry3.isSelected()) {
            mc.gameMode
               .startPrediction(mc.level, var1x -> new ServerboundPlayerActionPacket(Action.START_DESTROY_BLOCK, var1.getBlockPos(), Direction.UP, var1x));
            var1.setCancelled(true);
         } else if (this.entry2.isSelected()) {
            double var5 = 0.0;
            if (UseItemUtils.method6520(KeyMappingHelper.getBoundKeyOf(mc.options.keyJump))) {
               var5 = 0.8;
            } else if (UseItemUtils.method6520(KeyMappingHelper.getBoundKeyOf(mc.options.keyShift))) {
               var5 = -0.8;
            }

            if (MovementInputUtils.method7418()) {
               double[] var4 = MovementInputUtils.method7421(0.22F);
               mc.player.setDeltaMovement(new Vec3(var4[0], var5, var4[1]));
            } else {
               mc.player.setDeltaMovement(new Vec3(0.0, var5, 0.0));
            }
         } else {
            if (UseItemUtils.method6520(KeyMappingHelper.getBoundKeyOf(mc.options.keyJump))) {
               mc.player.setDeltaMovement(new Vec3(0.0, 1.0, 0.0));
            } else if (UseItemUtils.method6520(KeyMappingHelper.getBoundKeyOf(mc.options.keyShift))) {
               mc.player.setDeltaMovement(new Vec3(0.0, -3.0, 0.0));
            }

            if (ViaCompat.method4846(mc.player.input) != 0.0F || ViaCompat.method4847(mc.player.input) != 0.0F) {
               double[] var2 = MovementInputUtils.method7421(0.62F);
               mc.player.setDeltaMovement(new Vec3(var2[0], mc.player.getDeltaMovement().y, var2[1]));
            }
         }
      }
   }
}
