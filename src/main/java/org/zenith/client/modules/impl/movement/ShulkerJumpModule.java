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
   public int intValue = 0;
   public boolean flag = false;

   @EventTarget
   public void method5129(UpdateEvent var1) {
      if (mc.player != null && mc.level != null) {
         if (mc.player.containerMenu instanceof ShulkerBoxMenu && !this.flag) {
            mc.options.keyJump.setDown(true);
            mc.player.closeContainer();
            mc.player.push(0.0, 2.4, 0.0);
            this.flag = true;
            this.intValue = 0;
         }

         if (this.flag) {
            if (this.intValue >= 5) {
               mc.player.closeContainer();
               mc.options.keyJump.setDown(false);
               this.flag = false;
               this.intValue = 0;
            } else {
               this.intValue++;
            }
         }
      }
   }
}
