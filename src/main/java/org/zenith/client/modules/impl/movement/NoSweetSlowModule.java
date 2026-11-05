package org.zenith.client.modules.impl.movement;

import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.base.Module;
import org.zenith.client.events.BlockCollisionEvent;
import org.zenith.client.settings.SliderSetting;

@ModuleInfo(
   name = "NoSweetSlow",
   category = ModuleCategory.MOVEMENT,
   description = "module.nosweetslow.description"
)
public final class NoSweetSlowModule extends Module {
   public static final NoSweetSlowModule INSTANCE = new NoSweetSlowModule();
   private final SliderSetting speed = new SliderSetting("module.noSweetSlow.speed", 1.5F, 0.8F, 1.8F, 0.05F, "module.noSweetSlow.speed.desc", "x");

   private NoSweetSlowModule() {
   }

   @EventTarget
   public void method106(BlockCollisionEvent event) {
      if (event.getBlock() == Blocks.SWEET_BERRY_BUSH) {
         event.setCancelled(true);
         mc.player.makeStuckInBlock(mc.level.getBlockState(event.getBlockPos()), new Vec3(this.speed.getValue3(), 0.75, this.speed.getValue3()));
      }
   }
}
