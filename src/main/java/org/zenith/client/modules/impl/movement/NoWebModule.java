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
   private final ModeSetting.Entry defaultMode = new ModeSetting.Entry(this.mode, "module.noweb.mode.default");
   private final ModeSetting.Entry rw116Mode = new ModeSetting.Entry(this.mode, "module.noweb.mode.rw116");
   private final ModeSetting.Entry rw121Mode = new ModeSetting.Entry(this.mode, "module.noweb.mode.rw121");

   private NoWebModule() {
   }

   @EventTarget
   public void onBlockCollision(BlockCollisionEvent event) {
      if (event.getBlock() == Blocks.COBWEB) {
         if (this.rw121Mode.isSelected()) {
            mc.gameMode
               .startPrediction(mc.level, sequence -> new ServerboundPlayerActionPacket(Action.START_DESTROY_BLOCK, event.getBlockPos(), Direction.UP, sequence));
            event.setCancelled(true);
         } else if (this.rw116Mode.isSelected()) {
            double verticalVelocity = 0.0;
            if (UseItemUtils.method6520(KeyMappingHelper.getBoundKeyOf(mc.options.keyJump))) {
               verticalVelocity = 0.8;
            } else if (UseItemUtils.method6520(KeyMappingHelper.getBoundKeyOf(mc.options.keyShift))) {
               verticalVelocity = -0.8;
            }

            if (MovementInputUtils.hasMovementInput()) {
               double[] horizontalVelocity = MovementInputUtils.horizontalVelocityForPlayer(0.22F);
               mc.player.setDeltaMovement(new Vec3(horizontalVelocity[0], verticalVelocity, horizontalVelocity[1]));
            } else {
               mc.player.setDeltaMovement(new Vec3(0.0, verticalVelocity, 0.0));
            }
         } else {
            if (UseItemUtils.method6520(KeyMappingHelper.getBoundKeyOf(mc.options.keyJump))) {
               mc.player.setDeltaMovement(new Vec3(0.0, 1.0, 0.0));
            } else if (UseItemUtils.method6520(KeyMappingHelper.getBoundKeyOf(mc.options.keyShift))) {
               mc.player.setDeltaMovement(new Vec3(0.0, -3.0, 0.0));
            }

            if (ViaCompat.method4846(mc.player.input) != 0.0F || ViaCompat.method4847(mc.player.input) != 0.0F) {
               double[] horizontalVelocity = MovementInputUtils.horizontalVelocityForPlayer(0.62F);
               mc.player.setDeltaMovement(new Vec3(horizontalVelocity[0], mc.player.getDeltaMovement().y, horizontalVelocity[1]));
            }
         }
      }
   }
}
