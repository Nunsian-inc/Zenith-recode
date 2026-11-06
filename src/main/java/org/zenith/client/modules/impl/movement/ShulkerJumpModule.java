package org.zenith.client.modules.impl.movement;

import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.world.inventory.ShulkerBoxMenu;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.base.Module;
import org.zenith.client.events.UpdateEvent;

@ModuleInfo(
   name = "ShulkerJump",
   category = ModuleCategory.MOVEMENT,
   description = "module.shulkerjump.description"
)
public final class ShulkerJumpModule extends Module {
   public static final ShulkerJumpModule INSTANCE = new ShulkerJumpModule();
   public int jumpTicks = 0;
   public boolean jumpActive = false;

   @EventTarget
   public void onUpdate(UpdateEvent event) {
      if (mc.player != null && mc.level != null) {
         if (mc.player.containerMenu instanceof ShulkerBoxMenu && !this.jumpActive) {
            mc.options.keyJump.setDown(true);
            mc.player.closeContainer();
            mc.player.push(0.0, 2.4, 0.0);
            this.jumpActive = true;
            this.jumpTicks = 0;
         }

         if (this.jumpActive) {
            if (this.jumpTicks >= 5) {
               mc.player.closeContainer();
               mc.options.keyJump.setDown(false);
               this.jumpActive = false;
               this.jumpTicks = 0;
            } else {
               this.jumpTicks++;
            }
         }
      }
   }
}
