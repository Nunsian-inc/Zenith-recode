package org.zenith.client.modules.impl.movement;

import com.darkmagician6.eventapi.EventTarget;
import java.util.List;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.base.Module;
import org.zenith.client.events.BlockCollisionEvent;
import org.zenith.client.events.EntityPushEvent;
import org.zenith.client.settings.MultiSelectSetting;

@ModuleInfo(
   name = "NoPush",
   description = "module.nopush.description",
   category = ModuleCategory.MOVEMENT
)
public final class NoPushModule extends Module {
   private final MultiSelectSetting ignoreSetting = MultiSelectSetting.method5224(
      "module.noPush.ignoreSetting",
      "module.noPush.ignoreSetting.desc",
      List.of(
         "module.noPush.ignoreSetting.water",
         "module.noPush.ignoreSetting.blocks",
         "module.noPush.ignoreSetting.entities",
         "module.noPush.ignoreSetting.snow",
         "module.noPush.ignoreSetting.berries"
      )
   );
   public static final NoPushModule INSTANCE = new NoPushModule();

   private NoPushModule() {
   }

   @EventTarget
   public void onEntityPush(EntityPushEvent event) {
      switch (event.getType()) {
         case COLLISION:
            event.setCancelled(this.ignoreSetting.method5227(2));
            break;
         case WATER:
            event.setCancelled(this.ignoreSetting.method5227(0));
            break;
         case BLOCK:
            event.setCancelled(this.ignoreSetting.method5227(1));
      }
   }

   @EventTarget
   public void onBlockCollision(BlockCollisionEvent event) {
      Block block = event.getBlock();
      if (block.equals(Blocks.POWDER_SNOW)) {
         event.setCancelled(this.ignoreSetting.method5227(3));
      } else if (block.equals(Blocks.SWEET_BERRY_BUSH)) {
         event.setCancelled(this.ignoreSetting.method5227(4));
      }
   }
}
